<template>

  <div class="login-page">
    <div class="login-bg">
      <div class="login-grid" aria-hidden="true" />
      <div class="login-orb login-orb--1" aria-hidden="true" />
      <div class="login-orb login-orb--2" aria-hidden="true" />
      <div class="login-orb login-orb--3" aria-hidden="true" />
      <div class="login-scanline" aria-hidden="true" />
    </div>

    <div class="login-shell">
      <section class="login-showcase">
        <div class="showcase-badge">
          <span class="showcase-badge__dot" />
          <span>5D Neural Ops · v3.5</span>
        </div>

        <div class="showcase-brand">
          <div class="showcase-logo">5D</div>
          <div>
            <h1 class="showcase-title">5D Auto Operation Desk</h1>
            <p class="showcase-sub">车商互操作系统 · 智能运维中枢</p>
          </div>
        </div>

        <p class="showcase-tagline">{{ aiTagline }}</p>

        <ul class="showcase-features">
          <li v-for="feature in AI_FEATURES" :key="feature.title" class="feature-item">
            <div class="feature-icon">
              <el-icon><component :is="feature.icon" /></el-icon>
            </div>
            <div>
              <strong class="feature-title">{{ feature.title }}</strong>
              <p class="feature-desc">{{ feature.desc }}</p>
            </div>
          </li>
        </ul>

        <div class="showcase-metrics">
          <div class="metric-card">
            <span class="metric-value">99.97%</span>
            <span class="metric-label">合规扫描可用性</span>
          </div>
          <div class="metric-card">
            <span class="metric-value">&lt;120ms</span>
            <span class="metric-label">风控决策响应</span>
          </div>
          <div class="metric-card">
            <span class="metric-value">24/7</span>
            <span class="metric-label">AI 运维值守</span>
          </div>
        </div>
      </section>

      <section class="login-panel">
        <div class="login-card">
          <div class="login-card__glow" aria-hidden="true" />

          <header class="login-card__header">
            <div class="login-card__logo">5D</div>
            <div>
              <h2 class="login-card__title">运维身份验证</h2>
              <p class="login-card__desc">Secure Access · AI-Enhanced Console</p>
            </div>
          </header>

          <el-form
            ref="formRef"
            class="login-form"
            :model="form"
            :rules="rules"
            size="large"
            @submit.prevent="handleSubmit"
          >
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                autocomplete="username"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="登录密码"
                show-password
                autocomplete="current-password"
                @keyup.enter="handleSubmit"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="login-form__meta">
              <el-checkbox v-model="form.remember">保持登录状态</el-checkbox>
              <el-button link type="primary" class="login-form__demo" @click="fillDemoAccount">
                填入演示账号
              </el-button>
            </div>

            <el-button
              class="login-submit"
              type="primary"
              native-type="submit"
              :loading="loading"
              round
            >
              <el-icon v-if="!loading" class="login-submit__icon"><MagicStick /></el-icon>
              {{ loading ? 'AI 身份核验中…' : '进入智能运维台' }}
            </el-button>
          </el-form>

          <footer class="login-card__footer">
            <el-icon><Cpu /></el-icon>
            <span>演示账号 {{ DEMO_ACCOUNT.username }} · 密码 {{ DEMO_ACCOUNT.password }}</span>
          </footer>
        </div>

        <p class="login-copyright">© 2026 5D Auto · Intelligent Compliance Platform</p>
      </section>
    </div>
  </div>

</template>

<script setup lang="ts">

import { Cpu, Lock, MagicStick, User } from '@element-plus/icons-vue'
import { AI_FEATURES, DEMO_ACCOUNT } from './hooks/constants'
import { useLogin } from './hooks/useLogin'

const { formRef, form, rules, loading, aiTagline, handleSubmit, fillDemoAccount } = useLogin()

</script>

<style lang="scss" scoped>

.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  color: #e0e7ff;
  background: #030712;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
}

.login-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 80% 60% at 20% 20%, rgba(76, 58, 237, 0.35), transparent 55%),
    radial-gradient(ellipse 70% 50% at 80% 80%, rgba(46, 16, 101, 0.55), transparent 50%),
    linear-gradient(145deg, #030712 0%, #0f172a 45%, #1e1b4b 100%);
}

.login-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(139, 124, 246, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(139, 124, 246, 0.08) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: radial-gradient(ellipse 90% 80% at 50% 40%, #000 20%, transparent 75%);
  animation: grid-drift 24s linear infinite;
}

.login-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.55;
  animation: orb-float 12s ease-in-out infinite;

  &--1 {
    width: 420px;
    height: 420px;
    top: -120px;
    left: -80px;
    background: rgba(76, 58, 237, 0.45);
  }

  &--2 {
    width: 320px;
    height: 320px;
    right: 10%;
    bottom: 10%;
    background: rgba(139, 92, 246, 0.35);
    animation-delay: -4s;
  }

  &--3 {
    width: 240px;
    height: 240px;
    left: 42%;
    top: 55%;
    background: rgba(59, 130, 246, 0.25);
    animation-delay: -8s;
  }
}

.login-scanline {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    rgba(139, 124, 246, 0.04) 50%,
    transparent 100%
  );
  background-size: 100% 8px;
  animation: scanline 8s linear infinite;
  pointer-events: none;
}

.login-shell {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(380px, 480px);
  gap: 48px;
  align-items: center;
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 32px;
}

.login-showcase {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.showcase-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #c4b5fd;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(139, 124, 246, 0.35);
  backdrop-filter: blur(8px);
}

.showcase-badge__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 12px rgba(52, 211, 153, 0.8);
  animation: pulse-dot 2s ease-in-out infinite;
}

.showcase-brand {
  display: flex;
  align-items: center;
  gap: 16px;
}

.showcase-logo {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 900;
  letter-spacing: 0.06em;
  color: #e0e7ff;
  background: linear-gradient(135deg, #4c3aed, #7c3aed);
  border: 1px solid rgba(196, 181, 253, 0.45);
  box-shadow:
    0 0 40px rgba(76, 58, 237, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.showcase-title {
  margin: 0;
  font-size: clamp(24px, 3vw, 34px);
  font-weight: 800;
  letter-spacing: -0.03em;
  line-height: 1.15;
  background: linear-gradient(135deg, #fff 0%, #c4b5fd 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.showcase-sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: rgba(199, 210, 254, 0.65);
  font-weight: 500;
}

.showcase-tagline {
  margin: 0;
  max-width: 520px;
  font-size: 16px;
  line-height: 1.7;
  color: rgba(224, 231, 255, 0.82);
  min-height: 54px;
  transition: opacity 0.4s ease;
}

.showcase-features {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  gap: 14px;
  padding: 16px 18px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(139, 124, 246, 0.18);
  backdrop-filter: blur(10px);
  transition: border-color 0.25s, transform 0.25s;

  &:hover {
    border-color: rgba(139, 124, 246, 0.45);
    transform: translateX(4px);
  }
}

.feature-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #c4b5fd;
  background: rgba(76, 58, 237, 0.25);
  border: 1px solid rgba(139, 124, 246, 0.3);
}

.feature-title {
  display: block;
  font-size: 14px;
  font-weight: 700;
  color: #f8fafc;
  margin-bottom: 4px;
}

.feature-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: rgba(199, 210, 254, 0.55);
}

.showcase-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(15, 23, 42, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.metric-value {
  font-size: 18px;
  font-weight: 800;
  color: #a78bfa;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
}

.metric-label {
  font-size: 10px;
  color: rgba(199, 210, 254, 0.45);
  line-height: 1.3;
}

.login-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.login-card {
  position: relative;
  width: 100%;
  padding: 36px 32px 28px;
  border-radius: 24px;
  background: rgba(15, 23, 42, 0.72);
  border: 1px solid rgba(139, 124, 246, 0.28);
  backdrop-filter: blur(20px);
  box-shadow:
    0 24px 80px rgba(0, 0, 0, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
  overflow: hidden;
}

.login-card__glow {
  position: absolute;
  top: -80px;
  right: -80px;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(139, 124, 246, 0.35), transparent 70%);
  pointer-events: none;
}

.login-card__header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.login-card__logo {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 900;
  color: #1e1b4b;
  background: #e0e7ff;
  border: 1px solid rgba(196, 181, 253, 0.5);
}

.login-card__title {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: #f8fafc;
  letter-spacing: -0.02em;
}

.login-card__desc {
  margin: 4px 0 0;
  font-size: 11px;
  color: rgba(199, 210, 254, 0.45);
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  letter-spacing: 0.04em;
}

.login-form {
  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
    box-shadow: 0 0 0 1px rgba(139, 124, 246, 0.2) inset;
    transition: box-shadow 0.2s;

    &:hover {
      box-shadow: 0 0 0 1px rgba(139, 124, 246, 0.4) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px #8b7cf6 inset, 0 0 20px rgba(139, 124, 246, 0.15);
    }
  }

  :deep(.el-input__inner) {
    color: #f1f5f9;

    &::placeholder {
      color: rgba(148, 163, 184, 0.75);
    }

    &:-webkit-autofill,
    &:-webkit-autofill:hover,
    &:-webkit-autofill:focus,
    &:-webkit-autofill:active {
      -webkit-text-fill-color: #f1f5f9 !important;
      caret-color: #f1f5f9;
      box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.05) inset !important;
      transition: background-color 9999s ease-out 0s;
    }
  }

  :deep(.el-input__prefix) {
    color: rgba(167, 139, 250, 0.85);
  }

  :deep(.el-checkbox__label) {
    color: rgba(199, 210, 254, 0.65);
    font-size: 13px;
  }
}

.login-form__meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -4px 0 20px;
}

.login-form__demo {
  font-size: 13px;
  color: #a78bfa !important;
}

.login-submit {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.02em;
  border: none;
  background: linear-gradient(135deg, #4c3aed 0%, #7c3aed 50%, #6366f1 100%);
  box-shadow:
    0 8px 32px rgba(76, 58, 237, 0.45),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover:not(.is-disabled) {
    transform: translateY(-1px);
    box-shadow:
      0 12px 40px rgba(76, 58, 237, 0.55),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }
}

.login-submit__icon {
  margin-right: 6px;
}

.login-card__footer {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 11px;
  line-height: 1.5;
  color: rgba(199, 210, 254, 0.4);
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;

  .el-icon {
    flex-shrink: 0;
    color: rgba(167, 139, 250, 0.6);
  }
}

.login-copyright {
  margin: 0;
  font-size: 11px;
  color: rgba(148, 163, 184, 0.35);
  letter-spacing: 0.04em;
}

@keyframes grid-drift {
  from {
    transform: translateY(0);
  }
  to {
    transform: translateY(48px);
  }
}

@keyframes orb-float {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(24px, -18px) scale(1.06);
  }
}

@keyframes scanline {
  from {
    background-position: 0 0;
  }
  to {
    background-position: 0 100vh;
  }
}

@keyframes pulse-dot {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(0.85);
  }
}

@media (max-width: 960px) {
  .login-shell {
    grid-template-columns: 1fr;
    max-width: 480px;
    padding: 32px 20px;
  }

  .login-showcase {
    display: none;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 28px 20px 22px;
    border-radius: 20px;
  }

  .showcase-metrics {
    grid-template-columns: 1fr;
  }
}

</style>
