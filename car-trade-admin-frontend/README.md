# goodCar-Admin（5D Auto 管理后台）

> **面向 AI / 开发者**：本文档描述项目定位、目录约定、数据流与扩展方式。修改代码前请先阅读「架构约定」与「业务模块索引」。

## 项目概述

| 项        | 说明                                                                             |
| --------- | -------------------------------------------------------------------------------- |
| 名称      | `goodcar-admin`                                                                  |
| 用途      | 5D 二手车业务线的 **B 端管理后台**（用户、车行、车源、交易、保证金、出口配置等） |
| 运行形态  | 纯前端 SPA，当前 **业务数据以本地 mock 为主**；HTTP 封装已就绪，尚未接入真实后端 |
| 语言 / UI | 界面中文；Element Plus 组件库 + 中文 locale                                      |

## 技术栈

- **框架**：Vue 3（`<script setup>` + Composition API）+ TypeScript
- **构建**：Vite 8
- **路由**：vue-router 4（History 模式）
- **UI**：Element Plus（按需自动导入）+ Tailwind CSS 4
- **图表**：ECharts 6 + vue-echarts（仪表盘）
- **HTTP**：axios（封装于 `src/utils/request/`）
- **样式**：全局 CSS 变量主题（`src/style.css`）+ 页面级 SCSS + `src/styles/page.css`

## 快速开始

```bash
# 安装依赖
npm install

# 开发（默认 http://localhost:5173，host: true 可局域网访问）
npm run dev

# 类型检查 + 生产构建
npm run build

# 预览构建产物
npm run preview
```

### 环境变量

文件：`.env.development`

| 变量                | 默认值  | 说明             |
| ------------------- | ------- | ---------------- |
| `VITE_API_BASE_URL` | `/api`  | axios `baseURL`  |
| `VITE_API_TIMEOUT`  | `30000` | 请求超时（毫秒） |

> 当前各页面 **未调用** `get/post` 等 API 方法；对接后端时在各模块 `hooks/use*.ts` 中替换 mock 逻辑即可。

### 演示登录

路由守卫依赖 `localStorage` 中的 `token`。演示账号（硬编码于 `src/composables/useAuth.ts`）：

- 邮箱：`yuan2026@5d.com`
- 密码：`5d2026`

## 目录结构

```
goodCar-Admin/
├── index.html                 # 入口 HTML，title: 5D Auto 管理后台
├── vite.config.ts             # Vite + Tailwind + Element Plus 自动导入
├── .env.development           # 开发环境变量
└── src/
    ├── main.ts                # createApp + router + 全局样式
    ├── App.vue                # ElConfigProvider（zh-cn）+ RouterView
    ├── style.css              # Tailwind + 亮/暗主题 CSS 变量
    ├── styles/page.css        # 通用页面布局类（.page, .panel, .toolbar 等）
    ├── auto-imports.d.ts      # 自动生成，勿手改
    ├── components.d.ts        # 自动生成，勿手改
    │
    ├── router/index.ts        # 路由表 + 登录守卫
    ├── config/nav.ts          # 侧边栏导航项（需与路由 path 保持一致）
    │
    ├── composables/           # 全局 composable
    │   ├── useAuth.ts         # 登录 / token / 用户信息（localStorage）
    │   └── useTheme.ts        # light | dark，存 localStorage，设 data-theme
    │
    ├── layouts/
    │   ├── AdminLayout.vue    # 后台壳：Header + Sidebar + MobileNav + RouterView
    │   └── hooks/useAdminLayout.ts
    │
    ├── components/            # 跨页面共享组件
    │   ├── AppHeader.vue
    │   ├── SidebarNav.vue
    │   ├── MobileNav.vue
    │   ├── PageHeader.vue     # 页面标题区
    │   ├── StatCard.vue
    │   ├── StatusBadge.vue
    │   └── hooks/             # 组件级 composable（useNav, useAppHeader）
    │
    ├── utils/request/         # axios 封装（已就绪，待业务接入）
    │   ├── index.ts           # get/post/put/del，Bearer token，code===0 解包
    │   └── types.ts           # ApiResponse, PageResult, RequestConfig
    │
    └── views/                 # 按业务模块划分，见下方「业务模块索引」
        └── <module>/
            ├── index.vue              # 页面模板 + 样式，逻辑尽量放 hooks
            ├── hooks/
            │   ├── use<Module>.ts     # 主 composable：状态 + 交互
            │   ├── constants.ts       # mock 种子数据 SEED_*、表单规则、选项
            │   ├── types.ts           # 模块 TypeScript 类型
            │   └── *Utils.ts          # 纯函数工具（格式化、校验、映射）
            └── components/            # 仅本模块使用的子组件（可选）
```

## 架构约定（修改代码时请遵守）

### 1. 页面模块分层

每个 `views/<module>/` 遵循同一模式：

1. **`index.vue`**：模板、Element Plus 组件、scoped SCSS；从 `hooks/use*.ts` 解构状态与方法。
2. **`hooks/use*.ts`**：响应式状态（`ref`/`reactive`）、计算属性、事件处理；必要时 re-export `types`/`constants`/`utils`。
3. **`hooks/constants.ts`**：以 `SEED_*` 命名的 mock 初始数据、表单 `rules`、下拉选项。
4. **`hooks/types.ts`**：模块实体与表单类型。
5. **`*Utils.ts`**：无副作用纯函数，便于单测与复用。

**不要**在 `index.vue` 的 `<script setup>` 中写大段业务逻辑；**不要**把 mock 数据散落在 vue 文件里。

### 2. 路由与导航

- 路由定义：`src/router/index.ts`
- 侧边栏：`src/config/nav.ts` 的 `navItems`
- 新增页面时 **两处都要改**，且 `path` 必须一致（如 `/users`）
- 受保护路由挂在 `AdminLayout` 的 `children` 下；公开路由设 `meta: { public: true }`

### 3. 认证流

```
未登录 → 访问任意非 public 路由 → 重定向 /login?redirect=...
已登录 → 访问 /login → 重定向 /dashboard
token 键名：localStorage['token']（与 request 拦截器共用 AUTH_TOKEN_KEY）
用户信息：localStorage['goodcar-admin-user']（JSON）
```

路由守卫使用 `hasAuthToken()`（`useAuth.ts`），与 `useAuth().login()` 写入的 key 一致。

### 4. 主题

- `useTheme()` 切换 `light` / `dark`
- 通过 `document.documentElement.setAttribute('data-theme', value)` 驱动
- 颜色令牌定义在 `src/style.css` 的 `:root` / `[data-theme='dark']`
- 布局组件使用 `admin-layout--${theme}`、`sidebar--${theme}` 等 BEM 修饰类
- ECharts 图表在 composable 内根据 `theme.value === 'dark'` 构建 option

### 5. UI 与样式

- Element Plus 组件 **无需手动 import**（`unplugin-vue-components` + `ElementPlusResolver`）
- 通用页面结构：外层 `.page` → `PageHeader` → `el-card.panel` → `.toolbar` + 表格/表单
- 共享页面样式类见 `src/styles/page.css`
- 图标：`@element-plus/icons-vue`，在 script 中按需 import

### 6. API 响应约定（对接后端时）

`src/utils/request/types.ts`：

```ts
interface ApiResponse<T> {
  code: number // 0 表示成功
  message: string
  data: T
}
```

成功时拦截器直接返回 `data`；失败弹出 `ElMessage.error`（`config.silent` 可关闭）。

### 7. TypeScript 注意

- `tsconfig.app.json` 关闭了 `noUnusedLocals`：composable 变量可能只在 template 使用，vue-tsc 无法识别
- 不要手动编辑 `auto-imports.d.ts`、`components.d.ts`

## 业务模块索引

| 路由 path        | 路由 name     | 页面标题     | 目录                   | 职责摘要                                    |
| ---------------- | ------------- | ------------ | ---------------------- | ------------------------------------------- |
| `/login`         | login         | 登录         | `views/login/`         | 演示登录表单                                |
| `/dashboard`     | dashboard     | 分析仪表盘   | `views/dashboard/`     | 统计卡片、ECharts 趋势/渠道图、待办列表     |
| `/users`         | users         | 用户管理     | `views/users/`         | 用户列表、档案编辑、手动建档、保证金调配    |
| `/dealers`       | dealers       | 车行管理     | `views/dealers/`       | 车行 CRUD、调保                             |
| `/dealer-audit`  | dealer-audit  | 车行注册审核 | `views/dealer-audit/`  | 注册审核队列                                |
| `/vehicles`      | vehicles      | 5D 车源管理  | `views/vehicles/`      | 车源 listing                                |
| `/purchase`      | purchase      | 求购管理     | `views/purchase/`      | 求购单                                      |
| `/transactions`  | transactions  | 交易管理     | `views/transactions/`  | 交易订单（含 `TransactionOrderDrawer.vue`） |
| `/deposit`       | deposit       | 保证金现金流 | `views/deposit/`       | 保证金账户与流水                            |
| `/export-config` | export-config | 出口配置     | `views/export-config/` | 出口区域与约束规则                          |
| `/models`        | models        | 车型库       | `views/models/`        | 车型数据（mock 扩展至 303 条测分页）        |
| `/resources`     | resources     | 资源管理     | `views/resources/`     | Banner、协议文案等运营资源                  |

默认入口：`/` → redirect `/dashboard`。

## 数据现状

| 层级                   | 状态                                           |
| ---------------------- | ---------------------------------------------- |
| 列表 / 表单 / 弹窗交互 | ✅ 已实现（前端 mock）                         |
| 持久化                 | ❌ 刷新后 mock 重置（除 login token、theme）   |
| HTTP API               | ⚠️ 封装完成，业务 hooks 仍用 `SEED_*` 内存数据 |

对接真实 API 的典型步骤：

1. 在 `src/api/`（可新建）或模块 hooks 内定义接口函数，调用 `get/post` from `src/utils/request`
2. 将 `use*.ts` 中 `ref([...SEED_*])` 改为 `onMounted` 拉取 + 提交后刷新
3. 保留 `types.ts` 与后端 DTO 对齐；`constants.ts` 可仅保留表单 rules 与静态选项

## 常见任务指引（给 AI）

### 新增一个管理页面

1. 创建 `src/views/<name>/index.vue` + `hooks/use*.ts`（及 types/constants）
2. 在 `src/router/index.ts` 的 `AdminLayout.children` 注册路由
3. 在 `src/config/nav.ts` 添加 `navItems` 条目
4. 复用 `PageHeader`、`StatusBadge`、`src/styles/page.css` 中的工具类

### 修改某一业务模块

- 先读 `views/<module>/hooks/use*.ts` 与 `constants.ts`
- 类型变更同步 `types.ts`
- 格式化 / 状态映射放 `*Utils.ts`，避免堆在 composable 末尾

### 修改全局布局 / 导航

- 顶栏：`components/AppHeader.vue` + `hooks/useAppHeader.ts`
- 侧栏：`components/SidebarNav.vue` + `config/nav.ts`
- 移动端导航：`components/MobileNav.vue`

### 修改登录 / 权限

- 仅演示逻辑：`composables/useAuth.ts` 的 `login()` 硬编码校验
- 路由守卫：`router/index.ts` 的 `beforeEach`

## 构建产物

- 输出目录：`dist/`
- `npm run build` 会先执行 `vue-tsc -b` 再 `vite build`

## 关键文件速查

| 需求          | 文件                          |
| ------------- | ----------------------------- |
| 改路由 / 守卫 | `src/router/index.ts`         |
| 改菜单        | `src/config/nav.ts`           |
| 改登录逻辑    | `src/composables/useAuth.ts`  |
| 改主题色      | `src/style.css`               |
| 改 axios      | `src/utils/request/index.ts`  |
| 改后台布局    | `src/layouts/AdminLayout.vue` |
| Vite / 插件   | `vite.config.ts`              |
