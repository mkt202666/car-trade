<template>

  <aside class="sidebar" :class="`sidebar--${theme}`">
    <div class="sidebar-inner">
      <span class="nav-section-label">OPERATION MODULES</span>

      <nav class="sidebar-nav">
        <RouterLink
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <div class="nav-item-main">
            <span class="nav-icon" :data-icon="item.icon" />
            <span class="nav-label">{{ item.label }}</span>
          </div>
          <el-badge v-if="item.badge" :value="item.badge" type="primary" />
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div class="footer-title">
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <circle cx="12" cy="12" r="10" />
            <path d="M12 16v-4" />
            <path d="M12 8h.01" />
          </svg>
          <span>5D 业务线管区规章</span>
        </div>
        <p>根据《反电信诈骗五要素》及《代拍法拍二手车监管守则》，本平台将所有核销让利与保证金变动实时日志归档案卷。</p>
      </div>
    </div>
  </aside>

</template>

<script setup lang="ts">

import { navItems } from '../config/nav'
import { useNav } from './hooks/useNav'

const { theme, isActive } = useNav()

</script>

<style lang="scss" scoped>

.sidebar {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

/* ── 浅色主题（默认） ── */
.sidebar--light {
  width: 240px;
  background: #fff;
  border-right: 1px solid #f1f5f9;
  padding: 16px;
}

.sidebar-inner {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.sidebar-inner {
  justify-content: space-between;
}

.nav-section-label {
  display: block;
  padding-left: 8px;
  margin-bottom: 16px;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.1em;
  color: #9ca3af;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
}

.sidebar-nav{
  flex: 1;
  height: 0;
  overflow: auto;
}
.sidebar--light .sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-right: 6px;
}

.sidebar--light .nav-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  padding-left: 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  text-decoration: none;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
  transform: translateX(0);
}

.sidebar--light .nav-item:hover:not(.active) {
  background: rgba(249, 250, 251, 0.7);
  color: #111827;
}

.sidebar--light .nav-item.active {
  background: var(--color-primary-soft);
  color: var(--color-primary-dark);
  border-left-color: var(--color-primary-dark);
  transform: translateX(6px);
  box-shadow:
    0 1px 2px rgba(15, 23, 42, 0.05),
    4px 0 12px -4px var(--color-primary-glow);
}

.sidebar--light .nav-item.active .nav-icon {
  opacity: 1;
  color: var(--color-primary-dark);
}

.sidebar--light .nav-item.active :deep(.el-badge__content) {
  background: var(--color-primary-dark);
}

.nav-item-main {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.sidebar-footer {
  margin-top: 10px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid rgba(241, 245, 249, 0.8);
  background: #f8fafc;
  font-size: 11px;
  color: #94a3b8;
  line-height: 1.5;
}

.footer-title {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
  font-size: 10px;
  font-weight: 600;
  color: #64748b;
}

.sidebar-footer p {
  margin: 0;
}

/* ── 深色主题（结构同浅色，仅配色不同） ── */
.sidebar--dark {
  width: 240px;
  background: #111827;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  padding: 16px;
}

.sidebar--dark .nav-section-label {
  color: #64748b;
}

.sidebar--dark .sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-right: 6px;
}

.sidebar--dark .nav-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  padding-left: 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  text-decoration: none;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
  transform: translateX(0);
}

.sidebar--dark .nav-item:hover:not(.active) {
  background: rgba(255, 255, 255, 0.06);
  color: #e2e8f0;
}

.sidebar--dark .nav-item.active {
  background: var(--color-primary-soft-bg);
  color: var(--color-primary);
  border-left-color: var(--color-primary-dark);
  transform: translateX(6px);
  box-shadow: 4px 0 12px -4px var(--color-primary-glow);
}

.sidebar--dark .nav-item.active :deep(.el-badge__content) {
  background: var(--color-primary-dark);
}

.sidebar--dark .sidebar-footer {
  border-color: rgba(255, 255, 255, 0.08);
  background: #1a2332;
  color: #64748b;
}

.sidebar--dark .footer-title {
  color: #94a3b8;
}

/* ── 导航图标 ── */
.nav-icon {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  opacity: 0.85;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
}

.nav-icon[data-icon='chart'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2394a3b8'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'/%3E%3C/svg%3E");
}

.sidebar--light .nav-item.active .nav-icon[data-icon='chart'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%234c3aed'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'/%3E%3C/svg%3E");
}

.sidebar--dark .nav-item.active .nav-icon[data-icon='chart'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%238b7cf6'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'/%3E%3C/svg%3E");
}

.nav-icon[data-icon='store'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%239ca3af' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M15 21v-5a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v5'/%3E%3Cpath d='M17.774 10.31a1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.451 0 1.12 1.12 0 0 0-1.548 0 2.5 2.5 0 0 1-3.452 0 1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.77-3.248l2.889-4.184A2 2 0 0 1 7 2h10a2 2 0 0 1 1.653.873l2.895 4.192a2.5 2.5 0 0 1-3.774 3.244'/%3E%3Cpath d='M4 10.95V19a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8.05'/%3E%3C/svg%3E");
}

.nav-icon[data-icon='users'],
.nav-icon[data-icon='audit'],
.nav-icon[data-icon='car'],
.nav-icon[data-icon='purchase'],
.nav-icon[data-icon='transaction'],
.nav-icon[data-icon='deposit'],
.nav-icon[data-icon='export'],
.nav-icon[data-icon='model'],
.nav-icon[data-icon='resource'],
.nav-icon[data-icon='dispute'],
.nav-icon[data-icon='notification'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2394a3b8'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M4 6h16M4 12h16M4 18h16'/%3E%3C/svg%3E");
}

.sidebar--light .nav-item.active .nav-icon[data-icon='store'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%234c3aed' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M15 21v-5a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v5'/%3E%3Cpath d='M17.774 10.31a1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.451 0 1.12 1.12 0 0 0-1.548 0 2.5 2.5 0 0 1-3.452 0 1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.77-3.248l2.889-4.184A2 2 0 0 1 7 2h10a2 2 0 0 1 1.653.873l2.895 4.192a2.5 2.5 0 0 1-3.774 3.244'/%3E%3Cpath d='M4 10.95V19a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8.05'/%3E%3C/svg%3E");
}

.sidebar--light .nav-item.active .nav-icon[data-icon='users'],
.sidebar--light .nav-item.active .nav-icon[data-icon='audit'],
.sidebar--light .nav-item.active .nav-icon[data-icon='car'],
.sidebar--light .nav-item.active .nav-icon[data-icon='purchase'],
.sidebar--light .nav-item.active .nav-icon[data-icon='transaction'],
.sidebar--light .nav-item.active .nav-icon[data-icon='deposit'],
.sidebar--light .nav-item.active .nav-icon[data-icon='export'],
.sidebar--light .nav-item.active .nav-icon[data-icon='model'],
.sidebar--light .nav-item.active .nav-icon[data-icon='resource'],
.sidebar--light .nav-item.active .nav-icon[data-icon='dispute'],
.sidebar--light .nav-item.active .nav-icon[data-icon='notification'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%234c3aed'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M4 6h16M4 12h16M4 18h16'/%3E%3C/svg%3E");
}

.sidebar--dark .nav-item.active .nav-icon[data-icon='store'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%238b7cf6' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M15 21v-5a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v5'/%3E%3Cpath d='M17.774 10.31a1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.451 0 1.12 1.12 0 0 0-1.548 0 2.5 2.5 0 0 1-3.452 0 1.12 1.12 0 0 0-1.549 0 2.5 2.5 0 0 1-3.77-3.248l2.889-4.184A2 2 0 0 1 7 2h10a2 2 0 0 1 1.653.873l2.895 4.192a2.5 2.5 0 0 1-3.774 3.244'/%3E%3Cpath d='M4 10.95V19a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8.05'/%3E%3C/svg%3E");
}

.sidebar--dark .nav-item.active .nav-icon[data-icon='users'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='audit'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='car'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='purchase'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='transaction'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='deposit'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='export'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='model'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='resource'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='dispute'],
.sidebar--dark .nav-item.active .nav-icon[data-icon='notification'] {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%238b7cf6'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M4 6h16M4 12h16M4 18h16'/%3E%3C/svg%3E");
}

.nav-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 749px) {
  .sidebar {
    display: none;
  }
}

</style>
