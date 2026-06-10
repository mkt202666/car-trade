<template>
  <view class="page">
    <u-navbar title="交易规范" :border-bottom="false" :placeholder="true">
      <template v-slot:left>
        <view class="nav-back" @click="goBack">
          <u-icon name="arrow-left" size="32" color="#333"></u-icon>
        </view>
      </template>
    </u-navbar>

    <view class="page-content">
      <!-- 规范概述 -->
      <view class="section">
        <view class="section-title">规范概述</view>
        <view class="section-content">
          <text class="content-text">本规范旨在保障二手车交易双方的合法权益，维护平台交易秩序，促进公平、公正、透明的交易环境。所有用户在平台进行交易时，均应遵守本规范。</text>
        </view>
      </view>

      <!-- 车源发布规范 -->
      <view class="section">
        <view class="section-title">车源发布规范</view>
        <view class="rules-list">
          <view class="rule-item" v-for="(rule, index) in publishRules" :key="index">
            <view class="rule-index">{{ index + 1 }}</view>
            <view class="rule-content">
              <text class="rule-title">{{ rule.title }}</text>
              <text class="rule-desc">{{ rule.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 交易流程规范 -->
      <view class="section">
        <view class="section-title">交易流程规范</view>
        <view class="rules-list">
          <view class="rule-item" v-for="(rule, index) in tradeRules" :key="index">
            <view class="rule-index">{{ index + 1 }}</view>
            <view class="rule-content">
              <text class="rule-title">{{ rule.title }}</text>
              <text class="rule-desc">{{ rule.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 保证金规范 -->
      <view class="section">
        <view class="section-title">保证金规范</view>
        <view class="rules-list">
          <view class="rule-item" v-for="(rule, index) in depositRules" :key="index">
            <view class="rule-index">{{ index + 1 }}</view>
            <view class="rule-content">
              <text class="rule-title">{{ rule.title }}</text>
              <text class="rule-desc">{{ rule.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 争议处理规范 -->
      <view class="section">
        <view class="section-title">争议处理规范</view>
        <view class="rules-list">
          <view class="rule-item" v-for="(rule, index) in disputeRules" :key="index">
            <view class="rule-index">{{ index + 1 }}</view>
            <view class="rule-content">
              <text class="rule-title">{{ rule.title }}</text>
              <text class="rule-desc">{{ rule.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 违规处罚 -->
      <view class="section">
        <view class="section-title">违规处罚</view>
        <view class="penalty-list">
          <view class="penalty-item" v-for="(item, index) in penalties" :key="index">
            <view class="penalty-level" :class="'level-' + item.level">
              <text>{{ item.levelName }}</text>
            </view>
            <view class="penalty-content">
              <text class="penalty-title">{{ item.title }}</text>
              <text class="penalty-desc">{{ item.desc }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 联系客服 -->
      <view class="section">
        <view class="section-title">如有疑问</view>
        <view class="contact-info">
          <text class="contact-text">如您对交易规范有任何疑问，或在交易过程中遇到问题，请联系平台客服：</text>
          <view class="contact-btn" @click="toCustomerService">
            <u-icon name="headphones" size="28" color="#0369A1"></u-icon>
            <text>联系客服</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      publishRules: [
        {
          title: '车源信息真实',
          desc: '发布的车源信息必须真实、准确，包括车辆品牌、型号、年份、里程、价格等关键信息。'
        },
        {
          title: '图片清晰真实',
          desc: '上传的车辆图片必须清晰、真实，不得使用虚假图片或过度美化。'
        },
        {
          title: '车况描述准确',
          desc: '车辆描述应客观、准确，包括车况、配置、维修记录等重要信息。'
        },
        {
          title: '价格合理',
          desc: '车辆定价应合理，不得恶意抬价或低价引流。'
        },
        {
          title: '及时更新',
          desc: '车源信息发生变化时，应及时更新或下架。'
        }
      ],
      tradeRules: [
        {
          title: '确认订单',
          desc: '买家下单后，卖家应在24小时内确认订单，超时系统将自动取消。'
        },
        {
          title: '支付保证金',
          desc: '订单确认后，买家需支付保证金，保证金将冻结至交易完成。'
        },
        {
          title: '签订合同',
          desc: '双方应签订电子合同，明确交易条款和双方权利义务。'
        },
        {
          title: '交易完成',
          desc: '双方确认交易完成后，保证金将解冻并退还。'
        }
      ],
      depositRules: [
        {
          title: '保证金用途',
          desc: '保证金用于保障交易安全，防止恶意下单和违约行为。'
        },
        {
          title: '保证金金额',
          desc: '保证金金额由卖家设定，通常为车价的5%-10%。'
        },
        {
          title: '保证金退还',
          desc: '交易完成或取消后，保证金将在1-3个工作日内退还至原支付账户。'
        },
        {
          title: '保证金扣除',
          desc: '如发生违约行为，平台有权扣除保证金作为违约金。'
        }
      ],
      disputeRules: [
        {
          title: '争议发起',
          desc: '如交易过程中出现问题，任何一方均可发起争议。'
        },
        {
          title: '举证责任',
          desc: '发起争议方需提供相关证据，包括聊天记录、检测报告等。'
        },
        {
          title: '平台介入',
          desc: '平台将在24小时内介入争议处理，并根据证据做出裁决。'
        },
        {
          title: '裁决执行',
          desc: '平台裁决为最终结果，双方应积极配合执行。'
        }
      ],
      penalties: [
        {
          level: 'warning',
          levelName: '警告',
          title: '轻微违规',
          desc: '首次发布虚假信息、轻微违规等，给予警告并要求整改。'
        },
        {
          level: 'limit',
          levelName: '限制',
          title: '一般违规',
          desc: '多次违规、恶意投诉等，限制部分功能使用7-30天。'
        },
        {
          level: 'suspend',
          levelName: '暂停',
          title: '严重违规',
          desc: '严重虚假信息、欺诈行为等，暂停账号30-90天。'
        },
        {
          level: 'ban',
          levelName: '封禁',
          title: '特别严重违规',
          desc: '恶意欺诈、严重违法行为等，永久封禁账号。'
        }
      ]
    }
  },
  methods: {
    toCustomerService() {
      uni.navigateTo({
        url: '/pages/customer-service/index'
      })
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss" scoped>
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
  padding: 20rpx;
}

.section {
  background: #fff;
  border-radius: $border-radius;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: $shadow;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid $border-color;
}

.section-content {
  margin-bottom: 20rpx;
}

.content-text {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
}

/* 规范列表 */
.rules-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.rule-item {
  display: flex;
  gap: 16rpx;
  padding: 16rpx;
  background: $bg-color;
  border-radius: 12rpx;
}

.rule-index {
  width: 48rpx;
  height: 48rpx;
  background: $cta-color;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
}

.rule-content {
  flex: 1;
}

.rule-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.rule-desc {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.5;
}

/* 处罚列表 */
.penalty-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.penalty-item {
  display: flex;
  gap: 16rpx;
  padding: 16rpx;
  background: $bg-color;
  border-radius: 12rpx;
}

.penalty-level {
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  font-weight: 600;
  flex-shrink: 0;

  &.level-warning {
    background: #fff3cd;
    color: #856404;
  }

  &.level-limit {
    background: #ffeeba;
    color: #856404;
  }

  &.level-suspend {
    background: #f5c6cb;
    color: #721c24;
  }

  &.level-ban {
    background: #f8d7da;
    color: #721c24;
  }
}

.penalty-content {
  flex: 1;
}

.penalty-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-bottom: 8rpx;
}

.penalty-desc {
  font-size: 24rpx;
  color: $text-secondary;
  line-height: 1.5;
}

/* 联系客服 */
.contact-info {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.contact-text {
  font-size: 28rpx;
  color: $text-secondary;
  line-height: 1.6;
}

.contact-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 20rpx;
  background: rgba(3, 105, 161, 0.1);
  border-radius: 12rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    background: rgba(3, 105, 161, 0.2);
  }

  text {
    font-size: 28rpx;
    color: $cta-color;
    font-weight: 600;
  }
}
</style>
