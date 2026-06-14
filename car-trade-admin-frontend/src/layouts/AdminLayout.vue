<template>

  <div class="admin-layout" :class="`admin-layout--${theme}`">
    <AppHeader />
    <div class="admin-body">
      <SidebarNav />
      <div class="admin-content">
        <MobileNav />
        <main class="admin-main">
          <RouterView />
        </main>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">

import AppHeader from '../components/AppHeader.vue'
import SidebarNav from '../components/SidebarNav.vue'
import MobileNav from '../components/MobileNav.vue'
import { useAdminLayout } from './hooks/useAdminLayout'

const { theme } = useAdminLayout()

</script>

<style lang="scss" scoped>

.admin-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  background: var(--bg-page);
  color: var(--text-primary);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
}

.admin-body {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.admin-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-main {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  color: var(--text-primary);
}

@media (max-width: 749px) {
  .admin-body {
    flex-direction: column;
  }
}

</style>
