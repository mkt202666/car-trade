<template>

  <header class="mobile-nav" :class="`mobile-nav--${theme}`">
    <nav class="mobile-tabs" aria-label="主导航">
      <RouterLink
        v-for="item in navItems"
        :key="item.path"
        :to="item.path"
        class="tab-item"
        :class="{ active: isActive(item.path) }"
      >
        {{ item.label }}
      </RouterLink>
    </nav>
  </header>

</template>

<script setup lang="ts">

import { navItems } from '../config/nav'
import { useNav } from './hooks/useNav'

const { theme, isActive } = useNav()

</script>

<style lang="scss" scoped>

.mobile-nav {
  display: none;
}

@media (max-width: 749px) {
  .mobile-nav {
    display: block;
    flex-shrink: 0;
    z-index: 100;
  }

  .mobile-tabs {
    display: flex;
    gap: 4px;
    padding: 8px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
  }

  .mobile-tabs::-webkit-scrollbar {
    display: none;
  }

  .tab-item {
    flex-shrink: 0;
    padding: 6px 12px;
    font-size: 11px;
    font-weight: 700;
    text-decoration: none;
    border-radius: 8px;
    transition: all 0.2s;
    white-space: nowrap;
  }

  .mobile-nav--light {
    background: #fff;
    border-bottom: 1px solid #f1f5f9;
  }

  .mobile-nav--light .tab-item {
    color: #6b7280;
    background: #f3f4f6;
  }

  .mobile-nav--light .tab-item.active {
    color: #fff;
    background: var(--color-primary-dark);
  }

  .mobile-nav--dark {
    background: #111827;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  }

  .mobile-nav--dark .tab-item {
    color: #94a3b8;
    background: #1a2332;
  }

  .mobile-nav--dark .tab-item.active {
    color: #fff;
    background: var(--color-primary-dark);
  }
}

</style>
