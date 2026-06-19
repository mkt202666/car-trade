import { Connection, Cpu, TrendCharts } from '@element-plus/icons-vue'
import type { Component } from 'vue'

export const DEMO_ACCOUNT = {
  username: 'yuan2026@5d.com',
  password: 'admin123',
} as const

export const AI_TAGLINES = [
  'AI 驱动的车商互操作合规中枢',
  '实时风控 · 智能洞察 · 全域协同',
  '让每一次交易决策都可追溯、可审计',
] as const

export const AI_FEATURES: ReadonlyArray<{
  icon: Component
  title: string
  desc: string
}> = [
  {
    icon: Cpu,
    title: '智能风控引擎',
    desc: '多维规则实时扫描，异常交易毫秒级预警',
  },
  {
    icon: TrendCharts,
    title: '全景数据洞察',
    desc: '业务指标自动对齐，趋势预测辅助决策',
  },
  {
    icon: Connection,
    title: '合规协同网络',
    desc: '跨模块审批流贯通，审计链路全程留痕',
  },
]
