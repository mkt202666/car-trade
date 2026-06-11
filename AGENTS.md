# AGENTS.md

## Project Overview

5D好车 B2B二手车交易平台 — a monorepo with two independent sub-projects:

- **`car-trade-backend/`** — Spring Boot 3.5.14, Java 21, Maven build
- **`car-trade-frontend/`** — uni-app 3.0 (Vue 3.4, Vite 5, Vuex 4, uView Plus 3.8)

## Quick Start

### Backend (Maven, from `car-trade-backend/`)

```bash
cd car-trade-backend
./mvnw clean install           # build JAR
./mvnw spring-boot:run         # run dev server (port 8080)
java -jar target/car-trade-backend-1.0.0.jar   # or run JAR directly
```

### Frontend (from `car-trade-frontend/`)

```bash
cd car-trade-frontend
npm install
npm run dev:h5          # H5 dev mode (Vite dev server, port 5173)
npm run build:h5        # production H5 build
```

**Note**: `.npmrc` uses `registry.npmmirror.com` (Chinese mirror). Only `dev:h5` and `build:h5` scripts exist — no WeChat Mini Program scripts are configured yet.

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

### Profile & Config

- Profiles: `local`, `test`, `prod` — activated via `SPRING_PROFILES_ACTIVE` env var (defaults to `local`)
- DB init: `spring.sql.init.mode=always` — runs `schema.sql` + `data.sql` on startup
- API prefix: `/api/v1` (configured in `application.properties`)
- AI config: env vars `AI_ENABLED`, `AI_API_KEY`, `AI_BASE_URL`, `AI_MODEL`

## Frontend Architecture

### Page structure (`pages.json`)

24 pages registered. 5-tab custom tab bar: 找车 / 求购 / AI助理 / 消息 / 我的

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

## Infrastructure

- **PostgreSQL 16** — 28 tables, schema in `car-trade-backend/src/main/resources/schema.sql`
- **Redis 7** — car source caching, token storage, hot data
- **RocketMQ 5.x** — async message processing (order events, auction settlement, notifications)
- **WebSocket (STOMP)** — real-time chat, order status push
- **JWT** — token auth via `JwtUtil` + `AuthenticationInterceptor`

## Documentation

Detailed design docs in `docs/`:
- `01-功能模块.md` through `08-开发规范.md` — comprehensive Chinese-language spec covering modules, pages, API, DB schema, entities, and coding standards

## Git Conventions

- Branches: `feat-*`, `fix-*`, `refactor-*` → merge to `develop`; `release/v*` → merge to `main`
- Commit format: `<type>(<scope>): <description>` (feat/fix/refactor/docs/style/chore)
