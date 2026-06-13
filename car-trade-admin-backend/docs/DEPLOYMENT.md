# 5D好车运营端 — 部署文档

> 版本：v1.0 | 日期：2026-06-13 | Phase 3 Day 18

---

## 一、环境要求

| 组件 | 版本要求 | 说明 |
|--------|-----------|------|
| JDK | 21+ | Spring Boot 3.5 最低要求 |
| Maven | 3.9+ | 编译构建 |
| PostgreSQL | 14+ | 共享移动端数据库 `new_car_trade` |
| Redis | 6.2+ | 缓存 + 限流 + Pub/Sub |
| Node.js | 18+ | 前端构建 |
| npm / npx | 自带 | Vite 构建工具 |

---

## 二、数据库初始化

### 2.1 共享表（与移动端共用，无需新建）

- `users` — 用户/车行信息
- `car_sources` — 车源
- `orders` — 订单
- `disputes` — 争议
- `deposit_accounts` — 保证金账户
- `deposit_records` — 保证金流水
- `order_logs` — 订单日志
- `shop_members` — 车行成员
- `coupons` — 优惠券
- `purchase_requests` — 求购

### 2.2 运营端独有表（需执行建表 SQL）

执行文件：`car-trade-admin-backend/src/main/resources/db/schema.sql`

核心表：
- `admin_users` — 管理员账号
- `admin_notifications` — 通知记录
- `audit_logs` — 操作审计日志
- `banners` — Banner 管理
- `configs` — 系统配置
- `export_templates` — 导出模板
- `car_brands` / `car_series` / `car_models` — 车型库

### 2.3 性能索引（推荐生产环境执行）

执行文件：`car-trade-admin-backend/src/main/resources/db/performance_indexes.sql`

```bash
psql -d new_car_trade -f car-trade-admin-backend/src/main/resources/db/performance_indexes.sql
```

> ⚠️ 索引创建会锁表，建议在业务低峰期执行，或加 `CONCURRENTLY`（PostgreSQL 支持）

---

## 三、配置说明

### 3.1 后端配置（环境变量）

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SERVER_PORT` | 服务端口 | 8081 |
| `SPRING_PROFILES_ACTIVE` | 激活配置 | local |
| `DB_URL` | PostgreSQL URL | — |
| `DB_USERNAME` | 数据库用户名 | — |
| `DB_PASSWORD` | 数据库密码 | — |
| `REDIS_HOST` | Redis 主机 | — |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `REDIS_PASSWORD` | Redis 密码 | — |
| `REDIS_DATABASE` | Redis 库号 | 0 |
| `JWT_SECRET` | JWT 签名密钥（≥32字符） | — |
| `JWT_EXPIRATION` | AccessToken 有效期（秒） | 7200 |
| `JWT_REFRESH_EXPIRATION` | RefreshToken 有效期（秒） | 604800 |
| `CORS_ALLOWED_ORIGINS` | 前端允许来源 | `http://localhost:5174` |

### 3.2 环境配置

```
# 本地开发
spring.profiles.active=local
# → 使用 application-local.properties

# 生产环境
spring.profiles.active=prod
# → 所有敏感配置通过环境变量注入
```

本地开发配置样例 `application-local.properties`：

```properties
# 数据库（本地）
spring.datasource.url=jdbc:postgresql://localhost:5432/new_car_trade
spring.datasource.username=postgres
spring.datasource.password=password

# Redis（本地）
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=

# JWT（本地明文，生产环境用环境变量）
jwt.secret=5d-car-trade-admin-default-secret-key-please-change-in-prod-env-2026

# 演示模式（跳过登录）
app.demo-mode=true
```

---

## 四、编译与启动

### 4.1 后端编译

```bash
cd car-trade-admin-backend
JAVA_HOME="C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot"
mvn -B clean compile
mvn -B package -DskipTests
```

### 4.2 后端启动

```bash
# 方式一：Maven 直接启动（开发推荐）
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 方式二：Java 直接启动（生产推荐）
java -jar target/car-trade-admin-backend-1.0.0.jar \
  --spring.profiles.active=prod \
  --JWT_SECRET=your-32char-secret-key-here
```

### 4.3 前端编译

```bash
cd car-trade-admin-frontend
npm install
npx vite build          # 输出到 dist/
```

### 4.4 前端开发模式

```bash
cd car-trade-admin-frontend
npx vite                 # 默认 http://localhost:5174
```

> 开发模式下 Vite 代理 `/api` 请求到 `http://localhost:8081`

---

## 五、生产部署建议

### 5.1 后端部署

```bash
# 使用 systemd 管理（Linux）
sudo tee /etc/systemd/system/car-trade-admin.service > /dev/null <<EOF
[Unit]
Description=5D Car Trade Admin Backend
After=network.target postgresql.service redis.service

[Service]
Type=simple
User=app
WorkingDirectory=/opt/car-trade/car-trade-admin-backend
ExecStart=/usr/lib/jvm/java-21/bin/java \
  -Xms512m -Xmx1024m \
  -jar target/car-trade-admin-backend-1.0.0.jar \
  --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable car-trade-admin
sudo systemctl start car-trade-admin
```

### 5.2 前端部署

将 `dist/` 目录部署到任意静态文件服务器（Nginx 推荐）：

```nginx
server {
    listen 80;
    server_name admin.5dcar.com;
    root /opt/car-trade/car-trade-admin-frontend/dist;
    index index.html;

    # SPA 路由兜底
    location / {
        try_files $uri $uri/ /index.html;
    }

    # GZIP 压缩
    gzip on;
    gzip_types application/json application/javascript text/css;

    # 静态资源长期缓存
    location /assets/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

### 5.3 Redis 配置建议（生产）

```conf
# redis.conf
maxmemory 512mb
maxmemory-policy allkeys-lru
tcp-keepalive 60
```

---

## 六、API 端点总览

基础路径：`/api/v1/admin`

### 认证（`/auth`）

| 方法 | 路径 | 说明 | 限流 |
|------|------|------|------|
| POST | `/auth/login` | 管理员登录 | 10次/5分钟 |
| POST | `/auth/logout` | 登出（Token 黑名单） | 5次/分钟 |
| POST | `/auth/refresh` | 刷新 Token | — |
| GET | `/auth/me` | 获取当前用户信息 | — |
| PUT | `/auth/password` | 修改密码 | — |

### 用户管理（`/users`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/users` | 用户列表（分页） |
| GET | `/users/{id}` | 用户详情 |
| PUT | `/users/{id}/status` | 更新用户状态 |
| GET | `/users/stats` | 用户统计 |

### 车行管理（`/shops`、`/shop-reviews`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/shops` | 车行列表 |
| GET | `/shops/{id}` | 车行详情 |
| PUT | `/shops/{id}/status` | 更新车行状态 |
| GET | `/shop-reviews` | 审核列表 |
| POST | `/shop-reviews/{id}/approve` | 通过审核 |
| POST | `/shop-reviews/{id}/reject` | 驳回审核 |
| POST | `/shop-reviews/batch-approve` | 批量通过 |
| GET | `/shop-reviews/pending-count` | 待审数量 |

### 车源管理（`/cars`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/cars` | 车源列表 |
| GET | `/cars/export` | 导出 Excel |
| GET | `/cars/{id}` | 车源详情 |
| PUT | `/cars/{id}/status` | 更新状态 |
| POST | `/cars/{id}/violation` | 标记违规 |

### 交易管理（`/orders`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/orders` | 订单列表 |
| GET | `/orders/export` | 导出 Excel |
| GET | `/orders/{id}` | 订单详情 |
| GET | `/orders/{id}/logs` | 订单操作日志 |

### 争议处理（`/disputes`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/disputes` | 争议列表 |
| GET | `/disputes/export` | 导出 Excel |
| POST | `/disputes/{id}/resolve` | 处理争议 |
| GET | `/disputes/pending-count` | 待处理数量 |

### 保证金管理（`/deposits`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/deposits/accounts` | 账户列表 |
| GET | `/deposits/records` | 流水记录 |
| GET | `/deposits/records/export` | 导出流水 |
| POST | `/deposits/adjust` | 手动入账 |
| GET | `/deposits/summary` | 汇总统计 |

### 通知管理（`/notifications`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/notifications/announcement` | 发布系统公告 |
| POST | `/notifications/send` | 发送私信 |
| GET | `/notifications` | 通知列表 |
| GET | `/notifications/stats` | 通知统计 |

### 仪表盘（`/dashboard`）

| 方法 | 路径 | 说明 | 缓存 |
|------|------|------|------|
| GET | `/dashboard/kpi` | KPI 指标 | 5分钟 |
| GET | `/dashboard/trend` | 交易趋势 | 10分钟 |
| GET | `/dashboard/car-distribution` | 车辆分布 | 15分钟 |
| GET | `/dashboard/coupon-stats` | 券统计 | 15分钟 |
| GET | `/dashboard/approval-queue` | 待审批队列 | 2分钟 |
| GET | `/dashboard/warnings` | 风险预警 | 2分钟 |

### 车型库管理（`/car-library`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/car-library/brands` | 品牌列表 |
| POST | `/car-library/brands` | 新增品牌 |
| PUT | `/car-library/brands/{id}` | 更新品牌 |
| DELETE | `/car-library/brands/{id}` | 删除品牌 |
| GET | `/car-library/series` | 车系列表 |
| POST | `/car-library/series` | 新增车系 |
| PUT | `/car-library/series/{id}` | 更新车系 |
| DELETE | `/car-library/series/{id}` | 删除车系 |
| GET | `/car-library/models` | 车型列表 |
| POST | `/car-library/models` | 新增车型 |
| PUT | `/car-library/models/{id}` | 更新车型 |
| DELETE | `/car-library/models/{id}` | 删除车型 |
| GET | `/car-library/export-template` | 下载导入模板 |
| POST | `/car-library/import` | 批量导入 |

### 资源管理（`/resources`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/resources/banners` | Banner 列表 |
| POST | `/resources/banners` | 新增 Banner |
| PUT | `/resources/banners/{id}` | 更新 Banner |
| DELETE | `/resources/banners/{id}` | 删除 Banner |
| PUT | `/resources/banners/{id}/sort` | 排序 |
| PUT | `/resources/banners/{id}/status` | 状态切换 |
| GET | `/resources/config` | 获取配置 |
| PUT | `/resources/config` | 更新配置 |

### 健康检查

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/health` | 健康检查（无鉴权） |

---

## 七、缓存策略

| 缓存区域 | TTL | 说明 |
|-----------|-----|------|
| `dashboard:kpi` | 5分钟 | KPI 指标 |
| `dashboard:trend` | 10分钟 | 交易趋势 |
| `dashboard:distribution` | 15分钟 | 车辆分布 |
| `dashboard:coupon` | 15分钟 | 券统计 |
| `dashboard:approval` | 2分钟 | 待审批队列 |
| `dashboard:warnings` | 2分钟 | 风险预警 |
| `config` | 30分钟 | 系统配置 |

缓存清除：
- 定时任务每天凌晨 2:00 自动清除所有 Dashboard 缓存
- `DashboardService.evictAllCache()` 可手动清除

---

## 八、限流策略

基于 Redis ZSet 滑动窗口实现：

| 接口 | 窗口 | 最大请求数 | 维度 |
|------|------|-----------|------|
| `/auth/login` | 5分钟 | 10次 | IP |
| `/auth/logout` | 1分钟 | 5次 | 用户 |

响应头：
- `X-RateLimit-Limit` — 窗口内最大请求数
- `X-RateLimit-Remaining` — 剩余请求数
- `X-RateLimit-Window` — 窗口时间（秒）
- `Retry-After` — 限流时返回，单位秒

超限返回 HTTP 429：
```json
{ "code": 429, "message": "请求过于频繁，请稍后再试" }
```

---

## 九、监控检查清单

- [ ] 健康检查端点 `/api/v1/admin/health` 返回 `{"status":"UP"}`
- [ ] Redis 连接正常（检查 health 端点）
- [ ] 数据库连接正常（检查 health 端点）
- [ ] Swagger 文档可访问 `http://localhost:8081/swagger-ui.html`
- [ ] 限流生效（连续请求登录接口 11 次应返回 429）
- [ ] Token 黑名单生效（登出后旧 Token 无法使用）
- [ ] 缓存生效（Dashboard 二次访问响应明显加快）

---

## 十、常见问题

### Q1: 启动时报 `JWT secret must be >= 32 characters`
**A**: 设置环境变量 `JWT_SECRET`，长度至少 32 字符。

### Q2: 前端登录后 401
**A**: 检查后端 CORS 配置，确认前端地址在 `cors.allowed-origins` 中。

### Q3: 导出 Excel 中文乱码
**A**: 已通过 `URLEncoder.encode(fileName, "UTF-8")` 处理，确保浏览器支持 UTF-8。

### Q4: Dashboard 数据未更新
**A**: Dashboard 有缓存，等待 TTL 过期或手动调用清除缓存接口。

---

*文档生成时间：2026-06-13 | 项目：5D好车运营端 Phase 3 Day 18*
