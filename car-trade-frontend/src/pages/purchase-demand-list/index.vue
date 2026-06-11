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

    <!-- 底部 TabBar 占位 -->
    <view class="tabbar-placeholder"></view>
  </view>
</template>

<script>
import { getPurchaseList, deletePurchase } from '@/api/purchase'

export default {
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
        const data = res.data
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
.page {
  min-height: 100vh;
  background: #F8FAFC;
}
.header {
  background: #fff;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #F1F5F9;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #0F172A;
}
.publish-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: linear-gradient(135deg, #F59E0B, #D97706);
  color: #fff;
  padding: 16rpx 28rpx;
  border-radius: 32rpx;
  font-size: 26rpx;
  font-weight: 600;
  box-shadow: 0 4rpx 12rpx rgba(245, 158, 11, 0.3);
}
.list-wrap {
  height: calc(100vh - 120rpx);
  padding: 20rpx 24rpx;
}
.demand-card {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  border-left: 8rpx solid #F59E0B;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 24rpx 8rpx;
}
.car-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}
.car-name {
  font-size: 32rpx;
  font-weight: 700;
  color: #0F172A;
}
.status-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  background: #FEF3C7;
  color: #D97706;
  font-weight: 600;
}
.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #F59E0B;
}
.publish-time {
  font-size: 22rpx;
  color: #94A3B8;
  padding: 0 24rpx 16rpx;
  display: block;
}
.card-body {
  padding: 0 24rpx 20rpx;
}
.info-section {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16rpx;
}
.section-label {
  font-size: 24rpx;
  color: #64748B;
  flex-shrink: 0;
}
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}
.model-tag {
  font-size: 22rpx;
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  background: #F1F5F9;
  color: #334155;
  border: 1rpx solid #E2E8F0;
}
.info-row {
  display: flex;
  gap: 32rpx;
  margin-bottom: 12rpx;
}
.info-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}
.info-label {
  font-size: 24rpx;
  color: #94A3B8;
}
.info-value {
  font-size: 24rpx;
  color: #334155;
  font-weight: 500;
  &.price {
    color: #F59E0B;
    font-weight: 700;
  }
}
.desc-section {
  margin-top: 16rpx;
  padding: 16rpx;
  background: #F8FAFC;
  border-radius: 12rpx;
}
.desc-text {
  font-size: 24rpx;
  color: #64748B;
  line-height: 1.6;
  font-style: italic;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
  border-top: 1rpx solid #F1F5F9;
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
.action-btn {
  font-size: 24rpx;
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  &.edit {
    color: #0369A1;
    background: #F0F9FF;
    border: 1rpx solid #BAE6FD;
  }
  &.delete {
    color: #DC2626;
    background: #FEF2F2;
    border: 1rpx solid #FECACA;
  }
}
.site-count {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: 22rpx;
  color: #94A3B8;
}
.ai-search-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  font-size: 22rpx;
  color: #F59E0B;
  font-weight: 600;
  padding: 8rpx 16rpx;
  background: #FFFBEB;
  border-radius: 8rpx;
  border: 1rpx solid #FDE68A;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 40rpx;
}
.empty-icon { font-size: 80rpx; margin-bottom: 24rpx; }
.empty-title { font-size: 32rpx; color: #0F172A; font-weight: 600; margin-bottom: 12rpx; }
.empty-desc { font-size: 26rpx; color: #64748B; margin-bottom: 32rpx; }
.btn-publish {
  background: #0369A1;
  color: #fff;
  padding: 16rpx 48rpx;
  border-radius: 40rpx;
  font-size: 28rpx;
}
.loading-more, .no-more {
  text-align: center;
  padding: 24rpx;
  font-size: 24rpx;
  color: #94A3B8;
}
.tabbar-placeholder {
  height: 120rpx;
}
</style>
