<template>
  <view class="page">
    <!-- 顶部：标题 + 发布按钮 -->
    <view class="header">
      <view class="header-content">
        <text class="header-title">我的求购</text>
        <view class="publish-btn" @click="goPublish">
          <u-icon name="plus" size="28" color="#fff"></u-icon>
          <text>发布求购</text>
        </view>
      </view>
    </view>

    <!-- 求购列表 -->
    <scroll-view scroll-y class="list-wrap" @scrolltolower="loadMore" refresher-enabled @refresherrefresh="onRefresh" :refresher-triggered="refreshing">
      <view class="demand-card" v-for="item in demandList" :key="item.id">
        <view class="card-header">
          <view class="car-info">
            <text class="car-name">{{ item.brandName }} {{ item.seriesName }}</text>
          </view>
          <view class="status-tag" v-if="item.status === 'ACTIVE'">
            <view class="status-dot"></view>
            <text>求购中</text>
          </view>
        </view>
        <text class="publish-time">发布于 {{ formatTime(item.createdAt) }}</text>

        <view class="card-body">
          <!-- 期望车型 -->
          <view class="info-section" v-if="item.modelName">
            <text class="section-label">期望车型：</text>
            <view class="tag-list">
              <view class="model-tag" v-for="(model, idx) in parseModels(item.modelName)" :key="idx">
                <text>{{ model }}</text>
              </view>
            </view>
          </view>

          <!-- 车龄/里程 -->
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">车龄要求:</text>
              <text class="info-value">{{ item.ageRange || '不限' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">里程要求:</text>
              <text class="info-value">{{ item.mileageRange || '不限' }}</text>
            </view>
          </view>

          <!-- 颜色/预算 -->
          <view class="info-row">
            <view class="info-item">
              <text class="info-label">期望车色:</text>
              <text class="info-value">{{ item.color || '不限' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">求购预算:</text>
              <text class="info-value price">{{ formatBudget(item.priceMax) }}</text>
            </view>
          </view>

          <!-- 描述 -->
          <view class="desc-section" v-if="item.description">
            <text class="desc-text">" {{ item.description }} "</text>
          </view>
        </view>

        <view class="card-footer">
          <view class="footer-left">
            <view class="action-btn edit" @click="goEdit(item)">
              <text>编辑</text>
            </view>
            <view class="action-btn delete" @click="handleDelete(item)">
              <text>删除</text>
            </view>
          </view>
          <view class="footer-right">
            <view class="site-count">
              <u-icon name="search" size="28" color="#94A3B8"></u-icon>
              <text>站内 (0)</text>
            </view>
            <view class="ai-search-btn" @click="goAISearch(item)">
              <u-icon name="sparkle" size="28" color="#F59E0B"></u-icon>
              <text>AI搜索</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-state" v-if="demandList.length === 0 && !loading">
        <text class="empty-icon">📋</text>
        <text class="empty-title">暂无求购意向</text>
        <text class="empty-desc">成为第一个发布求购的用户</text>
        <view class="btn-publish" @click="goPublish">发布求购</view>
      </view>

      <!-- 加载状态 -->
      <view class="loading-more" v-if="loading">
        <text>加载中...</text>
      </view>
      <view class="no-more" v-if="!hasMore && demandList.length > 0">
        <text>没有更多了</text>
      </view>
    </scroll-view>

    <!-- 自定义底部导航栏 -->
    <custom-tab-bar />
  </view>
</template>

<script>
import { getPurchaseList, deletePurchase } from '@/api/purchase'
import CustomTabBar from '@/custom-tab-bar/index.vue'

export default {
  components: {
    CustomTabBar
  },
  data() {
    return {
      demandList: [],
      page: 1,
      hasMore: true,
      loading: false,
      refreshing: false
    }
  },
  onShow() {
    this.refresh()
  },
  methods: {
    refresh() {
      this.page = 1
      this.hasMore = true
      this.demandList = []
      this.fetchData()
    },
    async onRefresh() {
      this.refreshing = true
      this.page = 1
      this.hasMore = true
      this.demandList = []
      await this.fetchData()
      this.refreshing = false
    },
    async fetchData() {
      if (this.loading || !this.hasMore) return
      this.loading = true
      try {
        const res = await getPurchaseList({ page: this.page, size: 10 })
        const data = res && res.data ? res.data : {}
        let records = []
        if (data && data.list) {
          records = data.list
        } else if (data && data.records) {
          records = data.records
        } else if (Array.isArray(data)) {
          records = data
        }
        this.demandList = this.page === 1 ? records : [...this.demandList, ...records]
        this.hasMore = records.length >= 10
        this.page++
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      this.fetchData()
    },
    goPublish() {
      uni.navigateTo({ url: '/pages/purchase-demand/index' })
    },
    goEdit(item) {
      uni.navigateTo({ url: `/pages/purchase-demand/index?id=${item.id}` })
    },
    async handleDelete(item) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条求购信息吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await deletePurchase(item.id)
              uni.showToast({ title: '删除成功', icon: 'success' })
              this.refresh()
            } catch (e) {
              uni.showToast({ title: '删除失败', icon: 'none' })
            }
          }
        }
      })
    },
    goAISearch(item) {
      uni.navigateTo({ url: `/pages/ai-search-car/index?brand=${item.brandName}&series=${item.seriesName}` })
    },
    parseModels(modelName) {
      if (!modelName) return []
      return modelName.split(/[,，/]/).map(s => s.trim()).filter(Boolean)
    },
    formatBudget(val) {
      if (!val) return '面议'
      return (Number(val) / 10000).toFixed(2) + '万'
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hour = String(d.getHours()).padStart(2, '0')
      const min = String(d.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${min}`
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车 - 求购大厅 - Liquid Glass Premium
   设计语言: Glassmorphism | Multi-layer Shadow | Subtle Gradient
   颜色系统: #0F172A 深海军蓝 | #F59E0B 琥珀金 | #0369A1 CTA蓝
   ========================================================= */

.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%);
  animation: fadeIn 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
  padding-bottom: 200rpx;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(16rpx); }
  to { opacity: 1; transform: translateY(0); }
}

/* ============ 顶部 Hero Header ============ */
.header {
  position: relative;
  padding: calc(var(--status-bar-height, 44px) + 20rpx) 32rpx 28rpx;
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 60%, #0F172A 100%);
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -40rpx;
    right: -40rpx;
    width: 320rpx;
    height: 320rpx;
    background: radial-gradient(circle, rgba(245, 158, 11, 0.18) 0%, transparent 70%);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    height: 40rpx;
    background: linear-gradient(180deg, rgba(248, 250, 252, 0) 0%, rgba(248, 250, 252, 1) 100%);
    pointer-events: none;
    z-index: 1;
  }
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 40rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 1rpx;
}

.header-subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 8rpx;
  letter-spacing: 0.5rpx;
}

/* 发布按钮 - 琥珀金渐变 */
.publish-btn {
  display: flex;
  align-items: center;
  gap: 10rpx;
  background: linear-gradient(135deg, #FDE68A 0%, #FBBF24 50%, #F59E0B 100%);
  color: #0F172A;
  padding: 18rpx 32rpx;
  border-radius: 40rpx;
  font-size: 26rpx;
  font-weight: 700;
  letter-spacing: 0.5rpx;
  box-shadow: 0 6rpx 24rpx rgba(245, 158, 11, 0.45), inset 0 2rpx 4rpx rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: scale(0.96);
    box-shadow: 0 3rpx 12rpx rgba(245, 158, 11, 0.4);
  }
}

/* ============ 列表区域 ============ */
.list-wrap {
  position: relative;
  z-index: 3;
  padding: 16rpx 24rpx 40rpx;
  margin-top: -24rpx;
}

/* ============ 求购卡片 - Liquid Glass ============ */
.demand-card {
  background: #ffffff;
  border-radius: 28rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.08), 0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  position: relative;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 6rpx;
    background: linear-gradient(180deg, #FDE68A 0%, #F59E0B 50%, #D97706 100%);
    border-radius: 28rpx 0 0 28rpx;
  }

  &:active {
    transform: translateY(-2rpx) scale(0.995);
    box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.12);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 28rpx 12rpx 32rpx;
}

.car-info {
  display: flex;
  align-items: center;
  gap: 14rpx;
}

.car-name {
  font-size: 32rpx;
  font-weight: 800;
  color: #0F172A;
  letter-spacing: 0.3rpx;
}

/* 状态徽章 - Liquid Gold */
.status-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  padding: 8rpx 18rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, rgba(254, 243, 199, 0.9) 0%, rgba(253, 230, 138, 0.6) 100%);
  color: #D97706;
  font-weight: 700;
  letter-spacing: 0.3rpx;
  border: 1rpx solid rgba(245, 158, 11, 0.3);
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #FBBF24 0%, #F59E0B 100%);
  box-shadow: 0 0 8rpx rgba(245, 158, 11, 0.5);
}

.publish-time {
  font-size: 22rpx;
  color: #94A3B8;
  padding: 0 24rpx 16rpx;
  display: block;
}
.card-body {
  padding: 0 32rpx 24rpx;
}

.info-section {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16rpx;
  flex-wrap: wrap;
}

.section-label {
  font-size: 24rpx;
  color: #64748B;
  flex-shrink: 0;
  font-weight: 500;
  margin-right: 12rpx;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.model-tag {
  font-size: 22rpx;
  padding: 8rpx 18rpx;
  border-radius: 16rpx;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  color: #334155;
  border: 1rpx solid rgba(226, 232, 240, 0.9);
  font-weight: 600;
  transition: all 200ms ease;

  &:active {
    background: #E2E8F0;
    transform: scale(0.97);
  }
}

.info-row {
  display: flex;
  gap: 40rpx;
  margin-bottom: 14rpx;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.info-label {
  font-size: 24rpx;
  color: #94A3B8;
  font-weight: 500;
}

.info-value {
  font-size: 26rpx;
  color: #1E293B;
  font-weight: 600;

  &.price {
    background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    font-weight: 800;
    font-size: 28rpx;
    letter-spacing: 0.3rpx;
  }
}

.desc-section {
  margin-top: 18rpx;
  padding: 20rpx 24rpx;
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.95) 0%, rgba(241, 245, 249, 0.6) 100%);
  border-radius: 20rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  position: relative;

  &::before {
    content: '"';
    position: absolute;
    left: 16rpx;
    top: -10rpx;
    font-size: 60rpx;
    color: rgba(245, 158, 11, 0.2);
    font-family: serif;
    font-weight: 900;
  }
}

.desc-text {
  font-size: 24rpx;
  color: #475569;
  line-height: 1.7;
  font-style: italic;
  padding-left: 12rpx;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 32rpx;
  margin: 0 24rpx 24rpx;
  border-top: 1rpx solid rgba(226, 232, 240, 0.8);
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.5) 0%, transparent 100%);
  border-radius: 0 0 24rpx 24rpx;
}

.footer-left {
  display: flex;
  gap: 16rpx;
}
.footer-right {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

/* 操作按钮 - Liquid Glass */
.action-btn {
  font-size: 24rpx;
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);
  letter-spacing: 0.3rpx;

  &.edit {
    color: #0369A1;
    background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%);
    border: 1rpx solid rgba(186, 230, 253, 0.9);
    box-shadow: 0 2rpx 8rpx rgba(3, 105, 161, 0.08);

    &:active {
      transform: scale(0.96);
      box-shadow: 0 2rpx 12rpx rgba(3, 105, 161, 0.2);
    }
  }

  &.delete {
    color: #DC2626;
    background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
    border: 1rpx solid rgba(254, 202, 202, 0.9);
    box-shadow: 0 2rpx 8rpx rgba(220, 38, 38, 0.08);

    &:active {
      transform: scale(0.96);
      box-shadow: 0 2rpx 12rpx rgba(220, 38, 38, 0.2);
    }
  }
}

.site-count {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  color: #64748B;
  font-weight: 500;
}

/* AI搜索按钮 - 金色渐变 */
.ai-search-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #D97706;
  font-weight: 700;
  padding: 12rpx 24rpx;
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  border-radius: 24rpx;
  border: 1rpx solid rgba(245, 158, 11, 0.35);
  box-shadow: 0 2rpx 12rpx rgba(245, 158, 11, 0.2);
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);
  letter-spacing: 0.3rpx;

  &:active {
    transform: scale(0.96);
    box-shadow: 0 4rpx 16rpx rgba(245, 158, 11, 0.3);
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 40rpx 80rpx;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 28rpx;
  filter: drop-shadow(0 4rpx 12rpx rgba(15, 23, 42, 0.1));
}

.empty-title {
  font-size: 32rpx;
  color: #0F172A;
  font-weight: 700;
  margin-bottom: 12rpx;
  letter-spacing: 0.3rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: #64748B;
  margin-bottom: 40rpx;
  text-align: center;
}

.btn-publish {
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  color: #ffffff;
  padding: 20rpx 56rpx;
  border-radius: 48rpx;
  font-size: 28rpx;
  font-weight: 700;
  letter-spacing: 0.5rpx;
  box-shadow: 0 6rpx 24rpx rgba(3, 105, 161, 0.35);
  cursor: pointer;
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: scale(0.96);
    box-shadow: 0 3rpx 12rpx rgba(3, 105, 161, 0.3);
  }
}

.loading-more, .no-more {
  text-align: center;
  padding: 32rpx 24rpx 80rpx;
  font-size: 24rpx;
  color: #94A3B8;
  font-weight: 500;
  letter-spacing: 0.3rpx;
}

.tabbar-placeholder {
  height: 140rpx;
}
</style>
