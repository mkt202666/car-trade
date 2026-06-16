# AGENTS.md

## Project Overview

5D好车 B2B二手车交易平台 — a monorepo with three independent sub-projects:

- **`car-trade-backend/`** — Spring Boot 3.5.14, Java 21, Maven build
- **`car-trade-frontend/`** — uni-app 3.0 (Vue 3.4, Vite 5, Vuex 4, uView Plus 3.8)
- **`car-trade-admin-frontend/`** — Vue 3.5 (Element Plus 2.14, Vite 8, TypeScript 6, Tailwind 4.3)

## Quick Start

### Backend (from `car-trade-backend/`)

```bash
cd car-trade-backend
./mvnw clean install           # build JAR
./mvnw spring-boot:run         # run dev server (port 8080)
java -jar target/car-trade-backend-1.0.0.jar   # or run JAR directly
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

### Frontend (from `car-trade-frontend/`)

```bash
cd car-trade-frontend
npm install
npm run dev:h5          # H5 dev mode (Vite dev server, port 5173)
npm run build:h5        # production H5 build
```

Only `dev:h5` and `build:h5` scripts exist — no WeChat Mini Program scripts are configured. `.npmrc` uses `registry.npmmirror.com` (Chinese mirror).

### Admin Frontend (from `car-trade-admin-frontend/`)

```bash
cd car-trade-admin-frontend
npm install
npm run dev         # dev mode (Vite dev server, port 5174)
npm run build       # production build (vue-tsc -b && vite build)
```

## Backend Architecture

Package root: `com.pancosky.newcartrade`

### Layer hierarchy (top → bottom)

```
Controller → Service → Manager → Mapper
```

| Layer | Convention | Example |
|-------|-----------|---------|
| Controller | `*Controller.java` — REST endpoints, param validation, delegates to Service | `CarController` |
| Service | Interface `I*Service` + impl `*ServiceImpl` — business logic, transactions | `ICarService` / `CarServiceImpl` |
| Manager | `*Manager` — orchestrates multiple Mapper calls, third-party integrations, caching | `CarManager`, `PushManager` |
| Mapper | `*Mapper extends BaseMapper<T>` — DB access, MyBatis-Plus | `CarMapper` |
| Converter | `*Converter` — Entity ↔ DTO ↔ VO mapping (no business logic) | `CarConverter` |
| DTO | `*DTO` — request param objects | `CarCreateDTO` |
| VO | `*VO` — response objects (never return Entity directly) | `CarVO` |

**Strict rule**: Controller must not call Manager directly. Manager must not contain cross-Service business logic.

### Key conventions

- **Soft delete**: `deletedAt` field (TIMESTAMP, NULL = not deleted)
- **Auto-fill**: `createdAt`/`updatedAt` via `MetaObjectHandler` — do NOT set manually
- **Enums**: Implement `IEnum<Integer>` — stored as integers, not strings
- **Unified response**: All endpoints return `ApiResponse<T>`
- **Exceptions**: Throw `BusinessException(message)` — handled by `GlobalExceptionHandler`
- **Logging**: Use Lombok `@Slf4j`, never `System.out.println`
- **Transaction**: `@Transactional(rollbackFor = Exception.class)` on service methods that need it
- **Message push**: Always go through `PushManager`, never use `SimpMessagingTemplate` directly
- **MQ messages**: System/chat messages should go through RocketMQ → Consumer, not direct WebSocket push
- **Owner checks**: Use `OwnerAssert.assertOwner()` / `assertBuyerOrSeller()` for IDOR protection

### Auth system

Four levels via `@RequiresAuth` annotation: `PUBLIC`, `PROTECTED`, `CERTIFIED`, `ADMIN`.

- Method-level annotation overrides class-level
- `WebMvcConfig` excludes specific paths from the auth interceptor
- `AuthenticationInterceptor` reads `@RequiresAuth` + checks token + Redis blacklist
- `TokenBlacklistService` manages JWT blacklisting in Redis (fail-open if Redis is down)
- `SecurityUtils.getCurrentUserId()` reads the current user from request attributes

### Profile & Config

- Profiles: `local`, `test`, `prod` — activated via `SPRING_PROFILES_ACTIVE` env var (defaults to `local`)
- **DB init**: Handled by `StartupRunner` when `car-trade.init-db=true` (NOT `spring.sql.init.mode=always`)
- API prefix: `/api/v1` (configured in `application.properties`)
- AI config: env vars `AI_ENABLED`, `AI_API_KEY`, `AI_BASE_URL`, `AI_MODEL`

**Required environment variables for local dev** (via `.env.local` or export):

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` — PostgreSQL connection
- `REDIS_HOST`, `REDIS_PASSWORD` — Redis connection
- `JWT_SECRET` — JWT signing key (fail-fast if missing, must be ≥ 32 bytes)
- `AI_API_KEY` — Volcengine Ark API key

### Local dev gotchas

- `app.demo-mode=true` in local profile — allows auto-register on login + resets mismatched passwords
- `security.login-lock.enabled=false` in local profile — disables account lockout for debugging
- RocketMQ beans (`MessageConsumer`, `MessageProducer`) use `@ConditionalOnBean(RocketMQTemplate.class)` — won't load if RocketMQ is not configured
- `schema.sql` is NOT a migration script — it contains data updates (UPDATE statements) and test data inserts with `WHERE NOT EXISTS`. `init.sql` is the actual DDL (1083 lines)
- `GlobalExceptionHandler` returns HTTP 200 for `BusinessException` (via `@ResponseStatus(HttpStatus.OK)`) — clients must check `ApiResponse.code`, not HTTP status

## Frontend Architecture

### Page structure (`pages.json`)

34 pages registered. 5-tab custom tab bar: 找车 / 求购 / AI助理 / 消息 / 我的

### API layer (`src/api/`)

- `request.js` — unified HTTP instance with interceptors (JWT injection, 401/403 handling)
- `permissions.js` — public whitelist + merchant-only endpoints + auth level detection
- Module files: `car.js`, `order.js`, `user.js`, `ai.js`, `chat.js`, `contract.js`, etc.
- **Never call `uni.request` directly in pages** — always use `api/*.js` functions

### Component rules

- Pages: `pages/*/index.vue` — handle data fetching, layout, navigation
- Components: `src/components/*.vue` — pure UI, props-in + events-out, no API calls
- Shared UI: `src/custom-tab-bar/` — custom tab bar implementation

### uni-app specifics

- Use `uni.*` APIs (not browser APIs) for cross-platform compatibility
- Multi-platform divergence: use conditional compilation (`<!-- #ifdef MP-WEIXIN -->`)
- Units: `rpx` for responsive sizing
- `easycom` auto-imports: `u-*` components from `uview-ui`

### State management

Vuex 4 in `src/store/` — only for global state (user info, token). Server data stays in page components.

### Frontend gotchas

- Vite dev server proxies `/api` to `http://localhost:8080` (see `vite.config.js`)
- Token storage uses both `uni.setStorageSync` and `localStorage` for H5/mini-program compatibility
- `STORAGE_KEYS` constants in `src/constants/storage.js` — never hardcode storage keys elsewhere
- `permissions.js` public endpoint list must stay in sync with `WebMvcConfig` exclude list on backend
- Auth level check in `request.js` intercepts 401 → attempts refresh token → retry → logout on failure

## Admin Frontend Architecture

### Page structure (`views/`)

12 pages under `AdminLayout`, lazy-loaded via vue-router:

| Route | Module | Lines | Notes |
|-------|--------|-------|-------|
| `/login` | Login | 647 | Split layout, CSS animations, not under AdminLayout |
| `/dashboard` | Dashboard | 437 | ECharts trend/pie charts, KPI stat cards |
| `/users` | Users | 802 | el-table with expand rows + inline edit + image upload |
| `/dealers` | Dealers | 1424 | el-table + filter bar + create dialog |
| `/dealer-audit` | Dealer Audit | 712 | Native HTML table + split-pane approve/reject |
| `/vehicles` | Vehicles | 1040 | el-table rich cells + detail drawer |
| `/purchase` | Purchase | 377 | el-table with brand/trim/publisher/price |
| `/transactions` | Transactions | 607 | Custom HTML grid layout (not el-table) |
| `/deposit` | Deposit | 700 | Stat cards + el-table + manual journal entry |
| `/export-config` | Export Config | 528 | el-table + constraint tags + create/edit dialog |
| `/models` | Models | 1029 | Cascading brand→series→variant selects + Excel upload |
| `/resources` | Resources | 622 | el-tabs (banner/popup/rules/privacy/contract) |

### API layer (`src/utils/request/`)

- `index.ts` — axios wrapper (get/post/put/del) with `code === 0` unwrap
- `types.ts` — `ApiResponse<T>`, `PageResult`, `RequestConfig`
- **NOT yet used by any view** — all views use hardcoded mock data (`SEED_*` constants in module hooks)

### Component rules

- Layout shell: `src/layouts/AdminLayout.vue` — AppHeader + SidebarNav + MobileNav + `<RouterView />`
- Shared components: `src/components/` — `AppHeader.vue`, `SidebarNav.vue`, `MobileNav.vue`, `PageHeader.vue`, `StatCard.vue`, `StatusBadge.vue`
- Page modules: `src/views/<module>/index.vue` + `hooks/use*.ts` + `hooks/types.ts` + `hooks/constants.ts`
- **Never put business logic in `index.vue`** — use hooks composables

### State management

- **No Pinia/Vuex** — all state is local to views or composable singletons
- `useAuth()` — token + user info, persisted to `localStorage`
- `useTheme()` — light/dark toggle via `data-theme` attribute, persisted to `localStorage`

### Admin frontend specifics

- Use Element Plus components (auto-imported) — `el-table`, `el-card`, `el-dialog`, `el-button`, etc.
- Icons from `@element-plus/icons-vue` — import in `<script setup>` explicitly
- ECharts via `vue-echarts` — `VChart` component auto-registered
- Tailwind v4 utility classes + SCSS scoped styles in `.vue` files
- `npm run dev` → port 5174 (proxies `/api` to `http://localhost:8080`)
- `npm run build` → `vue-tsc -b && vite build`

### Admin frontend gotchas

- **All data is mock** — no view calls API; `SEED_*` arrays in `hooks/constants.ts` are the source of truth
- **Login is hardcoded** — credentials `yuan2026@5d.com` / `5d2026` in `useAuth.ts`
- **No shared types** — each view defines its own TypeScript interfaces locally
- **dealer-audit is architecturally inconsistent** — uses raw `<table>` + `<select>` + `<input>` instead of Element Plus components
- **Vite proxy** targets `http://localhost:8080` for API dev; edit in `vite.config.ts`
- **Theme CSS variables** in `src/style.css` — `:root` for light, `[data-theme='dark']` for dark
- **Auto-imports** (`unplugin-auto-import`, `unplugin-vue-components`) — all Element Plus components and Vue APIs auto-registered; don't manually import them

## Infrastructure

- **PostgreSQL 16** — 33 entity tables, DDL in `car-trade-backend/src/main/resources/init.sql`
- **Redis 7** — car source caching, token/blacklist storage, hot data
- **RocketMQ 5.x** — async message processing (order events, auction settlement, notifications)
- **WebSocket (STOMP)** — real-time chat, order status push (`/ws` endpoint with SockJS fallback)
- **JWT** — token auth via `JwtUtil` + `AuthenticationInterceptor`; refresh token rotation via `TokenBlacklistService`

## Testing

- **No backend tests exist** — no test files in `car-trade-backend/src/test/`
- **No frontend linting/formatting** — no ESLint, Prettier, or similar configured
- To verify changes: `mvn compile -q` (backend), `npm run build:h5` (frontend), `npm run build` (admin frontend)

## Documentation

Detailed design docs in `docs/`:
- `01-功能模块.md` through `08-开发规范.md` — comprehensive Chinese-language spec covering modules, pages, API, DB schema, entities, and coding standards

## Git Conventions

- Branches: `feat-*`, `fix-*`, `refactor-*` → merge to `develop`; `release/v*` → merge to `main`
- Commit format: `<type>(<scope>): <description>` (feat/fix/refactor/docs/style/chore)

---

## Gotchas and Non-obvious Patterns

These are hard-earned facts that agents would likely get wrong without explicit guidance.

### Error handling returns HTTP 200

`GlobalExceptionHandler` returns HTTP 200 for **all** `BusinessException` cases. Clients must check `ApiResponse.code` (non-zero = error), not the HTTP status code. This is by design, not a bug.

### Schema is both init and migration

`schema.sql` uses `CREATE TABLE IF NOT EXISTS` + `ALTER TABLE ADD COLUMN IF NOT EXISTS`. It runs once at startup via `StartupRunner` (when `car-trade.init-db=true`). There is no separate migration tool — this file IS the migration.

### Field changes require 6-layer updates

When adding/modifying a DB column, update in this exact order:
1. `init.sql` — DDL (CREATE TABLE / ALTER TABLE)
2. `schema.sql` — migration script (same DDL, idempotent)
3. Entity class — add field with `@TableField`
4. VOs — response objects
5. DTOs — request objects
6. Converters — mapping logic

Missing any layer causes silent runtime failures. Always verify with `mvn compile -q`.

### Auth config maintained in two places

- `WebMvcConfig` — excludes specific paths from the auth interceptor (backend)
- `permissions.js` — maintains a parallel `PUBLIC_ENDPOINTS` list (frontend)

If you add a public endpoint, update BOTH files. They must stay in sync.

### Login logic has 3 paths

`UserServiceImpl.login()`:
1. BCrypt password verification (normal path)
2. Passwordless login for accounts without password field
3. Silent registration when `app.demo-mode=true`

**Account lockout**: 5 failed attempts → 5-minute lock (returns HTTP 200 with `code: 423`). Mitigation: use correct test credentials (`13800138001` / `123456`) or wait 5 minutes.

### Design docs are stale

`04-数据库设计.md` references 28 tables. `init.sql` is the source of truth with 33 tables. Many fields added via ALTER TABLE are missing from the docs. Always check `init.sql`, not the docs, for actual schema.

### notification_settings is raw JSON

The `notification_settings JSONB` column is stored and passed as a raw JSON String (not deserialized to a Map). The `GET/PUT /api/v1/messages/notification-settings` endpoint returns/accepts the raw JSON string. Don't try to deserialize it to a typed object.

### Cities data is idempotent-safe

`data-cities.sql` uses `WHERE NOT EXISTS (SELECT 1 FROM cities WHERE code = 'xxx')` on every INSERT — each row is uniquely identified by its administrative division code (`code` field). Re-running the script is safe; no duplicate entries will be inserted. Hot cities (武汉/广州/深圳/成都/重庆) each appear exactly once.

### SMS endpoint requires auth

`/api/v1/users/sms/send` is NOT in the public exclude list. It returns 401 without auth. If this should be callable pre-login for verification codes, it needs to be added to the WebMvcConfig exclude list.
