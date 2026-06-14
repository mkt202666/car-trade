<template>

  <header class="app-header">
    <div class="header-brand">
      <div class="brand-logo">5D</div>
      <div>
        <h1 class="brand-title">5D Auto Operation Desk</h1>
        <p class="brand-sub">车商互操作系统运作管理后台 v3.5</p>
      </div>
    </div>

    <div class="header-actions">
      <el-tag class="login-badge" effect="dark" size="small" round>
        <el-icon><Lock /></el-icon>
        <span>运维登录: {{ userEmail }}</span>
      </el-tag>

      <el-button type="warning" size="small" title="点击恢复预置演示数据">还原预设</el-button>

      <el-button
        size="small"
        class="btn-theme"
        :title="theme === 'light' ? '切换至深色主题' : '切换至浅色主题'"
        @click="toggleTheme"
      >
        <el-icon>
          <Moon v-if="theme === 'light'" />
          <Sunny v-else />
        </el-icon>
        <span class="btn-theme-label">{{ theme === 'light' ? '深色' : '浅色' }}</span>
      </el-button>

      <el-dropdown class="user-dropdown" trigger="click" @command="handleUserCommand">
        <div class="user-block">
          <el-avatar class="user-avatar" :size="32">{{ userInitials }}</el-avatar>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled class="user-dropdown-info">
              <span class="user-dropdown-name">{{ userName }}</span>
              <span class="user-dropdown-role">{{ userRole }}</span>
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon class="user-dropdown-icon"><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>

</template>

<script setup lang="ts">

import { Lock, Moon, Sunny, SwitchButton } from '@element-plus/icons-vue'
import { useAppHeader } from './hooks/useAppHeader'

const { theme, toggleTheme, userName, userRole, userInitials, userEmail, handleUserCommand } = useAppHeader()

</script>

<style lang="scss" scoped>

.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  flex-shrink: 0;
  z-index: 50;
  color: #fff;
  background: linear-gradient(to right, #0f172a, #2e1065, #0f172a);
  border-bottom: 1px solid var(--color-primary-border);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.header-brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: var(--color-primary-dark);
  border: 1px solid rgba(196, 181, 253, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 18px;
  letter-spacing: 0.05em;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-title {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
  letter-spacing: -0.025em;
  line-height: 1.2;
}

.brand-sub {
  margin: 0;
  font-size: 10px;
  color: var(--color-primary-muted);
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-weight: 600;
  transform: scale(0.95);
  transform-origin: left;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-badge {
  display: none;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 10.5px;

  .el-icon {
    margin-right: 4px;
  }
}

.btn-theme {
  --el-button-bg-color: rgba(255, 255, 255, 0.08);
  --el-button-border-color: rgba(255, 255, 255, 0.12);
  --el-button-text-color: #e0e7ff;
  --el-button-hover-bg-color: rgba(255, 255, 255, 0.15);
  --el-button-hover-text-color: #e0e7ff;
}

.btn-theme-label {
  display: none;
  margin-left: 4px;
}

.user-dropdown {
  padding-left: 16px;
  border-left: 1px solid rgba(255, 255, 255, 0.15);
}

.user-block {
  display: flex;
  align-items: center;
  cursor: pointer;
  outline: none;
}

.user-avatar {
  --el-avatar-bg-color: #e0e7ff;
  --el-avatar-text-color: #1e1b4b;
  font-size: 12px;
  font-weight: 900;
  border: 1px solid var(--color-primary-light);
  transition: box-shadow 0.2s;

  .user-block:hover & {
    box-shadow: 0 0 0 2px rgba(196, 181, 253, 0.45);
  }
}

:deep(.user-dropdown-info) {
  cursor: default;
  opacity: 1 !important;
  color: inherit !important;

  &.is-disabled {
    color: inherit;
  }
}

.user-dropdown-name {
  display: block;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.3;
  color: var(--el-text-color-primary);
}

.user-dropdown-role {
  display: block;
  margin-top: 2px;
  font-size: 11px;
  line-height: 1.3;
  color: var(--el-text-color-secondary);
}

.user-dropdown-icon {
  margin-right: 6px;
  vertical-align: -2px;
}

@media (min-width: 768px) {
  .login-badge {
    display: inline-flex;
  }

  .btn-theme-label {
    display: inline;
  }
}

</style>
