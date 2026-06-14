/** 资源管理相关类型定义 */

/** 广告位条目（Banner / 弹窗），供卡片列表与编辑弹窗共用 */
export interface AdItem {
  /** 唯一标识，新建时以时间戳生成 */
  id: string
  /** 活动或弹窗标题，展示在卡片主体区域 */
  title: string
  /** 点击后跳转的前端路由或外链路径 */
  link: string
  /** 展示图片 URL，支持远程地址或本地上传预览 */
  image: string
  /** 是否在前端生效展示 */
  active: boolean
}

/** 协议类文本 Tab 的 name 键，与 el-tabs 的 name 属性一一对应 */
export type TextTabKey = 'trade-rules' | 'user-agreement' | 'privacy' | 'contract'
