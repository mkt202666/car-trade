
&lt;template&gt;
  &lt;view class="page"&gt;
    &lt;u-navbar title="会员中心" :border-bottom="false" :placeholder="true"&gt;&lt;/u-navbar&gt;

    &lt;view class="page-content"&gt;
      &lt;!-- 我的会员 --&gt;
      &lt;view class="my-member-card"&gt;
        &lt;view class="card-top"&gt;
          &lt;view class="member-info"&gt;
            &lt;text class="member-level"&gt;{{ myMembership ? myMembership.level : '普通' }}会员&lt;/text&gt;
            &lt;text class="member-status" :class="{ active: isActive }"&gt;
              {{ isActive ? '已开通' : '未开通' }}
            &lt;/text&gt;
          &lt;/view&gt;
          &lt;u-icon name="vip-crown" size="60" color="#f9ae3d"&gt;&lt;/u-icon&gt;
        &lt;/view&gt;
        &lt;view class="card-bottom" v-if="myMembership &amp;&amp; isActive"&gt;
          &lt;text class="expire-text"&gt;有效期至: {{ formatTime(myMembership.endAt) }}&lt;/text&gt;
        &lt;/view&gt;
      &lt;/view&gt;

      &lt;!-- 套餐选择 --&gt;
      &lt;view class="plans-section"&gt;
        &lt;view class="section-header"&gt;
          &lt;text class="section-title"&gt;选择套餐&lt;/text&gt;
        &lt;/view&gt;
        &lt;view class="plans-list"&gt;
          &lt;view 
            v-for="plan in plans" 
            :key="plan.id" 
            class="plan-item" 
            :class="{ selected: selectedPlanId === plan.id }"
            @click="selectedPlanId = plan.id"
          &gt;
            &lt;view class="plan-top"&gt;
              &lt;text class="plan-name"&gt;{{ plan.name }}&lt;/text&gt;
              &lt;view class="plan-tag" v-if="plan.sortOrder === 1"&gt;推荐&lt;/view&gt;
            &lt;/view&gt;
            &lt;view class="plan-price"&gt;
              &lt;text class="price-symbol"&gt;¥&lt;/text&gt;
              &lt;text class="price-num"&gt;{{ formatAmount(plan.price) }}&lt;/text&gt;
            &lt;/view&gt;
            &lt;text class="plan-duration"&gt;{{ plan.durationDays }}天&lt;/text&gt;
            &lt;view class="plan-benefits"&gt;
              &lt;text class="benefit-item" v-for="(benefit, idx) in planBenefits" :key="idx"&gt;
                {{ benefit }}
              &lt;/text&gt;
            &lt;/view&gt;
          &lt;/view&gt;
        &lt;/view&gt;

        &lt;!-- 权益说明 --&gt;
        &lt;view class="benefits-section"&gt;
          &lt;view class="section-header"&gt;
            &lt;text class="section-title"&gt;会员权益&lt;/text&gt;
          &lt;/view&gt;
          &lt;view class="benefits-list"&gt;
            &lt;view class="benefit-item" v-for="(item, idx) in benefitsList" :key="idx"&gt;
              &lt;u-icon :name="item.icon" size="40" :color="item.color"&gt;&lt;/u-icon&gt;
              &lt;text class="benefit-text"&gt;{{ item.text }}&lt;/text&gt;
            &lt;/view&gt;
          &lt;/view&gt;
        &lt;/view&gt;

        &lt;!-- 开通按钮 --&gt;
        &lt;view class="bottom-action"&gt;
          &lt;u-button type="primary" shape="circle" :disabled="!selectedPlanId" @click="handleSubscribe"&gt;
            {{ isActive ? '立即续费' : '立即开通' }}
          &lt;/u-button&gt;
        &lt;/view&gt;
      &lt;/view&gt;
    &lt;/view&gt;
  &lt;/view&gt;
&lt;/template&gt;

&lt;script&gt;
import { getPlans, getMyMembership, renewMembership } from '@/api/membership'

export default {
  data() {
    return {
      myMembership: null,
      plans: [],
      selectedPlanId: null,
      benefitsList: [
        { icon: 'thumb-up', color: '#3c9cff', text: '优先匹配车源' },
        { icon: 'gift', color: '#f9ae3d', text: '专属优惠活动' },
        { icon: 'chat-dots', color: '#5ac725', text: '一对一客服服务' },
        { icon: 'eye', color: '#a855f7', text: '无限浏览车源' },
        { icon: 'share', color: '#f56c6c', text: '免费推广车源' }
      ],
      planBenefits: ['专享权益', '专属标识', '优先展示']
    }
  },
  computed: {
    isActive() {
      if (!this.myMembership) return false
      if (this.myMembership.status !== 'ACTIVE') return false
      return new Date(this.myMembership.endAt) &gt; new Date()
    }
  },
  onLoad() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        const [plansRes, myRes] = await Promise.all([getPlans(), getMyMembership()])
        this.plans = plansRes.data
        this.myMembership = myRes.data
        if (this.plans.length &gt; 0) {
          const recommended = this.plans.find(p =&gt; p.sortOrder === 1) || this.plans[0]
          this.selectedPlanId = recommended.id
        }
      } catch (e) {
        console.error(e)
      }
    },
    async handleSubscribe() {
      if (!this.selectedPlanId) return
      try {
        await renewMembership({ planId: this.selectedPlanId })
        uni.$u.toast(this.isActive ? '续费成功' : '开通成功')
        this.fetchData()
      } catch (e) {
        console.error(e)
      }
    },
    formatAmount(val) {
      const n = Number(val) || 0
      return n.toLocaleString()
    },
    formatTime(time) {
      if (!time) return ''
      const d = new Date(time)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    }
  }
}
&lt;/script&gt;

&lt;style lang="scss" scoped&gt;
/* 设计系统变量 */
$primary-color: #0F172A;
$cta-color: #0369A1;
$bg-color: #F8FAFC;
$text-color: #020617;
$text-secondary: #64748B;
$border-color: #E2E8F0;
$border-radius: 16rpx;
$shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
$transition: all 0.2s ease;

.page {
  min-height: 100vh;
  background: $bg-color;
}
.page-content {
  padding: 20rpx 30rpx 120rpx;
}

/* 我的会员卡片 */
.my-member-card {
  background: linear-gradient(135deg, #ffd700, #f9ae3d);
  border-radius: $border-radius;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow;
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.member-info {
  display: flex;
  flex-direction: column;
  gap: 10rpx;
}
.member-level {
  font-size: 36rpx;
  font-weight: 700;
  color: #fff;
}
.member-status {
  font-size: 24rpx;
  color: rgba(255,255,255,0.8);
}
.member-status.active {
  color: #fff;
}
.card-bottom {
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid rgba(255,255,255,0.3);
}
.expire-text {
  font-size: 26rpx;
  color: rgba(255,255,255,0.9);
}

/* 套餐区域 */
.plans-section {
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx;
  box-shadow: $shadow;
}
.section-header {
  margin-bottom: 20rpx;
}
.section-title {
  font-size: 30rpx;
  font-weight: 600;
  color: $text-color;
}
.plans-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  margin-bottom: 40rpx;
}
.plan-item {
  border: 2rpx solid $border-color;
  border-radius: $border-radius;
  padding: 20rpx 16rpx;
  text-align: center;
  position: relative;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.98);
  }
}
.plan-item.selected {
  border-color: $cta-color;
  background: rgba(3, 105, 161, 0.08);
}
.plan-top {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  margin-bottom: 10rpx;
}
.plan-name {
  font-size: 26rpx;
  font-weight: 600;
  color: $text-color;
}
.plan-tag {
  background: #f9ae3d;
  color: #fff;
  font-size: 18rpx;
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
}
.plan-price {
  margin: 10rpx 0;
}
.price-symbol {
  font-size: 24rpx;
  color: #f56c6c;
}
.price-num {
  font-size: 36rpx;
  font-weight: 700;
  color: #f56c6c;
}
.plan-duration {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
}
.plan-benefits {
  margin-top: 10rpx;
}
.benefit-item {
  font-size: 20rpx;
  color: $text-secondary;
  display: block;
  line-height: 1.4;
}

/* 权益列表 */
.benefits-section {
  margin-top: 40rpx;
}
.benefits-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
.benefit-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 20rpx;
  background: $bg-color;
  border-radius: $border-radius;
  transition: $transition;

  &:active {
    background: darken($bg-color, 3%);
  }
}
.benefit-text {
  font-size: 26rpx;
  color: $text-color;
}

/* 底部操作 */
.bottom-action {
  margin-top: 40rpx;
}
&lt;/style&gt;
