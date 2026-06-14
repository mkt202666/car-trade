<template>

  <div class="stat-card" :class="{ featured }">
    <div v-if="featured" class="stat-glow" aria-hidden="true" />

    <div class="stat-head">
      <span class="stat-label">{{ label }}</span>
      <div class="stat-icon-wrap" :class="iconBg || (featured ? 'glass' : 'indigo')">
        <svg
          v-if="icon === 'dollar'"
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <line x1="12" x2="12" y1="2" y2="22" />
          <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
        </svg>
        <svg
          v-else-if="icon === 'car'"
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2" />
          <circle cx="7" cy="17" r="2" />
          <path d="M9 17h6" />
          <circle cx="17" cy="17" r="2" />
        </svg>
        <svg
          v-else-if="icon === 'shield-alert'"
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z" />
          <path d="M12 8v4" />
          <path d="M12 16h.01" />
        </svg>
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <path d="m16 11 2 2 4-4" />
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
        </svg>
      </div>
    </div>

    <div class="stat-body">
      <h3 class="stat-value">{{ value }}</h3>
      <div
        v-if="trend"
        class="stat-trend"
        :class="{
          'trend-up-arrow': trendVariant === 'up-arrow',
          'trend-split': trendVariant === 'split',
          'trend-pulse': trendVariant === 'pulse',
          'trend-emphasis': trendVariant === 'emphasis',
        }"
      >
        <svg
          v-if="trendVariant === 'up-arrow'"
          xmlns="http://www.w3.org/2000/svg"
          width="14"
          height="14"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <path d="M7 7h10v10" />
          <path d="M7 17 17 7" />
        </svg>
        <svg
          v-else-if="trendVariant === 'pulse'"
          class="pulse-icon"
          xmlns="http://www.w3.org/2000/svg"
          width="14"
          height="14"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <path d="M22 12h-2.48a2 2 0 0 0-1.93 1.46l-2.35 8.36a.25.25 0 0 1-.48 0L9.24 2.18a.25.25 0 0 0-.48 0l-2.35 8.36A2 2 0 0 1 4.49 12H2" />
        </svg>
        <template v-if="trendVariant === 'split' && trendPrefix">
          <span class="trend-prefix">{{ trendPrefix }}</span>
          <span>{{ trend }}</span>
        </template>
        <span v-else>{{ trend }}</span>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
/**
 * 仪表盘统计指标卡片组件。
 * 由 index.vue 的 stats-grid 区域 v-for 渲染，展示单条 KPI 数值、
 * 图标与趋势说明；featured 为 true 时启用渐变高亮样式。
 */
defineProps<{
  /** 指标名称，显示在卡片左上角 */
  label: string
  /** 指标主数值，大号等宽字体展示 */
  value: string
  /** 是否为高亮主指标卡片（渐变背景 + 光晕） */
  featured?: boolean
  /** 右上角图标类型，决定渲染哪组内联 SVG */
  icon: 'dollar' | 'car' | 'shield-alert' | 'user-check'
  /** 图标容器背景色主题；未传时 featured 用 glass，否则 indigo */
  iconBg?: 'glass' | 'indigo' | 'amber' | 'rose'
  /** 趋势说明文案，显示在数值下方 */
  trend?: string
  /** 趋势前缀（如 +1），仅 trendVariant 为 split 时与 trend 分开展示 */
  trendPrefix?: string
  /** 趋势行视觉样式，控制图标与配色类名 */
  trendVariant?: 'up-arrow' | 'split' | 'pulse' | 'emphasis'
}>()

</script>

<style lang="scss" scoped>

.stat-card {
  position: relative;
  overflow: hidden;
  padding: 20px;
  border-radius: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

[data-theme='dark'] .stat-card:not(.featured) {
  box-shadow: none;
}

.stat-card.featured {
  color: #fff;
  border-color: transparent;
  background: linear-gradient(to bottom right, #3730a3, #2e1065, #0f172a);
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.08);
}

.stat-glow {
  position: absolute;
  right: -20%;
  bottom: -20%;
  width: 144px;
  height: 144px;
  border-radius: 50%;
  background: rgba(76, 58, 237, 0.1);
  filter: blur(32px);
  transition: background 0.5s;
  pointer-events: none;
}

.stat-card.featured:hover .stat-glow {
  background: rgba(76, 58, 237, 0.2);
}

.stat-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.stat-label {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: var(--text-muted);
  line-height: 1.4;
}

.stat-card.featured .stat-label {
  color: rgba(199, 210, 254, 0.8);
}

.stat-icon-wrap {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  border-radius: 12px;
}

.stat-icon-wrap.glass {
  color: var(--color-primary-muted);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
}

.stat-icon-wrap.indigo {
  color: var(--color-primary-dark);
  background: var(--color-primary-soft);
}

.stat-icon-wrap.amber {
  color: #d97706;
  background: #fffbeb;
}

.stat-icon-wrap.rose {
  color: #e11d48;
  background: #fff1f2;
}

.stat-body {
  margin-top: 16px;
}

.stat-value {
  margin: 0;
  font-size: 30px;
  font-weight: 700;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
  letter-spacing: -0.025em;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-card.featured .stat-value {
  color: #fff;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.4;
}

.trend-up-arrow {
  color: #34d399;
}

.trend-split {
  color: var(--text-secondary);
}

.trend-prefix {
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, Consolas, monospace;
  font-weight: 500;
  color: #059669;
}

.trend-pulse {
  color: #d97706;
}

.pulse-icon {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

.trend-emphasis {
  color: #e11d48;
  font-weight: 500;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@media (max-width: 749px) {
  .stat-card {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }
}

</style>
