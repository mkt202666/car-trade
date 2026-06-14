<template>

  <div
    class="stat-card"
    :class="{ featured }"
    :style="accent ? { '--accent': accent } : {}"
  >
    <span v-if="icon" class="stat-icon" :data-icon="icon" />
    <p class="stat-label">{{ label }}</p>
    <h3 class="stat-value">{{ value }}</h3>
    <p v-if="trend" class="stat-trend" :class="trendType">
      <span v-if="trendType === 'up'" class="trend-arrow">↗</span>
      {{ trend }}
    </p>
  </div>

</template>

<script setup lang="ts">

defineProps<{
  label: string
  value: string
  trend?: string
  trendType?: 'up' | 'down' | 'neutral'
  accent?: string
  icon?: 'dollar' | 'car' | 'shield' | 'user-check'
  featured?: boolean
}>()

</script>

<style lang="scss" scoped>

.stat-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 16px 18px;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--accent);
  border-radius: 4px 0 0 4px;
}

.stat-icon {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-size: 18px;
  background-repeat: no-repeat;
  background-position: center;
}

.stat-card.featured .stat-icon {
  background-color: rgba(255, 255, 255, 0.15);
}

.stat-icon[data-icon='dollar'] {
  background-color: rgba(255, 255, 255, 0.15);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ffffff'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z'/%3E%3C/svg%3E");
}

.stat-icon[data-icon='car'] {
  background-color: var(--color-primary-soft-bg);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%234c3aed'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M8 17h.01M16 17h.01M3 11l1.5-4.5A2 2 0 016.4 5h11.2a2 2 0 011.9 1.5L21 11M3 11v6a1 1 0 001 1h1m16-7v6a1 1 0 01-1 1h-1M3 11h18M7 17a2 2 0 104 0 2 2 0 00-4 0zm10 0a2 2 0 104 0 2 2 0 00-4 0z'/%3E%3C/svg%3E");
}

.stat-icon[data-icon='shield'] {
  background-color: rgba(245, 158, 11, 0.12);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23f59e0b'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z'/%3E%3C/svg%3E");
}

.stat-icon[data-icon='user-check'] {
  background-color: rgba(239, 68, 68, 0.12);
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%23ef4444'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z'/%3E%3C/svg%3E");
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0 0 8px;
  padding-right: 40px;
  line-height: 1.4;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px;
  letter-spacing: -0.5px;
}

.stat-trend {
  font-size: 11px;
  margin: 0;
  color: var(--text-muted);
  line-height: 1.4;
}

.stat-trend.up {
  color: #22c55e;
}

.stat-trend.down {
  color: #ef4444;
}

.trend-arrow {
  margin-right: 2px;
}

@media (max-width: 749px) {
  .stat-card.featured {
    background: linear-gradient(135deg, #3730a3 0%, #2e1065 100%);
    border-color: transparent;
    color: #fff;
  }

  .stat-card.featured::before {
    display: none;
  }

  .stat-card.featured .stat-label {
    color: rgba(255, 255, 255, 0.75);
  }

  .stat-card.featured .stat-value {
    color: #fff;
  }

  .stat-card.featured .stat-trend {
    color: rgba(255, 255, 255, 0.85);
  }

  .stat-card.featured .stat-trend.up {
    color: #86efac;
  }
}

@media (min-width: 750px) {
  .stat-card {
    padding: 20px 24px;
  }

  .stat-label {
    font-size: 13px;
    padding-right: 0;
  }

  .stat-value {
    font-size: 28px;
    margin: 0 0 8px;
  }

  .stat-trend {
    font-size: 12px;
  }

  .stat-icon {
    display: none;
  }
}

</style>
