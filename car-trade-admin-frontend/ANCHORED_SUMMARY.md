# goodcar-admin (Vue 3 + Element Plus) — Anchored Summary

## Context
- The previous React/JSX admin frontend has been **completely rewritten** to **Vue 3 + TypeScript + Element Plus**
- Project name: `goodcar-admin` (in package.json)
- Build: `vue-tsc -b && vite build`
- Icons: `@element-plus/icons-vue` | Charts: `echarts` + `vue-echarts` | CSS: Tailwind v4 + SCSS
- Auto-imports via `unplugin-auto-import` + `unplugin-vue-components`

## Architecture

```
src/
├── main.ts                          # Vue 3 app entry
├── router/index.ts                  # 12 lazy-loaded routes under AdminLayout + 1 public login
├── config/nav.ts                    # NavItem[] — 11 nav entries (path, label, icon, badge)
├── style.css                        # Tailwind v4 + CSS vars (light/dark) + Element Plus overrides
├── styles/page.css                  # Shared .page / .table-panel / .filter-bar / .panel styles
├── layouts/
│   ├── AdminLayout.vue              # App shell: AppHeader + SidebarNav + MobileNav + RouterView
│   └── hooks/useAdminLayout.ts      # Thin theme wrapper
├── components/
│   ├── AppHeader.vue                # Brand logo, user email, restore btn, theme toggle, avatar dropdown
│   ├── SidebarNav.vue               # 240px sidebar, nav links with active detection, el-badge, info footer
│   ├── MobileNav.vue                # Horizontal scrollable tab bar (≤749px)
│   ├── PageHeader.vue               # Title/subtitle + actions slot
│   ├── StatCard.vue                 # KPI card (accent bar, icon, trend arrow)
│   ├── StatusBadge.vue              # Wraps el-tag with semantic status→type mapping
│   └── hooks/useNav.ts             # Shared nav logic for SidebarNav + MobileNav
├── composables/
│   ├── useAuth.ts                   # Token/user state, localStorage persist, hardcoded mock login
│   └── useTheme.ts                  # Light/dark toggle (data-theme attr), localStorage persist
└── views/
    ├── login/index.vue              # Split layout (animated showcase + el-form), 647 lines
    ├── dashboard/index.vue          # KPI cards, echarts trend/pie, coupon list, approval queue
    ├── users/index.vue              # el-table expand rows, inline edit, image upload, 802 lines
    ├── dealers/index.vue            # el-table multi-column, filter bar, create dialog, 1424 lines
    ├── dealer-audit/index.vue       # Native HTML table, split-pane, approve/reject, 712 lines
    ├── vehicles/index.vue           # el-table rich cells (tags/channel/province), detail drawer, 1040 lines
    ├── purchase/index.vue           # el-table brand/trim/publisher/price, 377 lines
    ├── transactions/index.vue       # Custom grid layout (not el-table), date range, 607 lines
    ├── deposit/index.vue            # Summary stat cards + el-table + manual entry dialog, 700 lines
    ├── export-config/index.vue      # el-table constraint tags, create/edit dialog, 528 lines
    ├── models/index.vue             # Cascading brand→series→variant selects, excel upload, 1029 lines
    └── resources/index.vue          # el-tabs (banner/popup/rules/privacy/contract), banner cards, 622 lines
```

## Key Observations
- **Monolith views**: 377–1424 lines each (~700 avg), all hardcoded mock data, no API integration
- **8/12 views** use `el-table`; transactions + dealer-audit use custom HTML grids
- **No shared types** — each view defines its own interfaces inline
- **No Vuex/Pinia** — all state is local to views or composable singletons
- **Mock auth** — hardcoded credentials (`yuan2026@5d.com` / `5d2026`) in `useAuth.ts`
- **No API layer** — no `api/` directory; all data is local mock arrays
- **Login page** is unique — split layout with CSS animations, not wrapped in AdminLayout
- **Pattern**: `<PageHeader>` + `<el-card class="panel">` + `<el-table>` is the dominant view pattern
- **Styling**: Tailwind utilities + SCSS scoped `.page` / `.filter-bar` / `.table-panel` classes; Element Plus variables overridden in `style.css`
- **dealer-audit** is architecturally inconsistent — uses native `<table>` + `<select>` + `<input>` instead of Element Plus equivalents
