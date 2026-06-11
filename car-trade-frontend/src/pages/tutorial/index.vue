<template>
  <view class="page">
    <u-navbar title="使用教程" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="content">
      <!-- 教程分组列表 -->
      <view class="tutorial-group" v-for="(group, gIdx) in tutorialList" :key="gIdx">
        <view class="group-header" @click="toggleGroup(group)">
          <view class="group-icon" :style="{ background: group.bg }">
            <u-icon :name="group.icon" size="36" color="#fff"></u-icon>
          </view>
          <view class="group-info">
            <text class="group-title">{{ group.title }}</text>
            <text class="group-desc">{{ group.desc }}</text>
          </view>
          <u-icon :name="group.expanded ? 'arrow-up' : 'arrow-down'" size="28" color="#94A3B8"></u-icon>
        </view>

        <!-- 展开内容 -->
        <view class="group-content" v-if="group.expanded">
          <view class="article-item" v-for="(article, aIdx) in group.articles" :key="aIdx" @click="showArticle(article)">
            <view class="article-dot"></view>
            <text class="article-title">{{ article.title }}</text>
            <u-icon name="arrow-right" size="24" color="#CBD5E1"></u-icon>
          </view>
        </view>
      </view>

      <!-- 底部提示 -->
      <view class="footer-tip">
        <text>如有疑问请联系在线客服</text>
      </view>
    </view>

    <!-- 文章详情弹窗 -->
    <view class="article-modal" v-if="showModal" @click="showModal = false">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ currentArticle.title }}</text>
          <view class="modal-close" @click="showModal = false">
            <u-icon name="close" size="32" color="#64748B"></u-icon>
          </view>
        </view>
        <scroll-view scroll-y class="modal-body">
          <rich-text :nodes="currentArticle.content"></rich-text>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      showModal: false,
      currentArticle: {},
      tutorialList: [
        {
          title: '找车使用协议',
          desc: '平台服务条款与使用条件',
          icon: 'file-text',
          bg: 'linear-gradient(135deg, #0369A1, #0284C7)',
          expanded: false,
          articles: [
            {
              title: '服务协议概述',
              content: '<p><strong>5D好车</strong>（以下简称"本平台"）是一个专注于B2B二手车交易的在线平台。通过注册和使用本平台，您同意遵守以下服务条款。</p><p><strong>一、服务内容</strong></p><p>本平台为二手车商提供车源发布、搜索、交易、合同签署、金融服务等一站式服务。所有服务均基于中华人民共和国法律法规。</p><p><strong>二、用户资格</strong></p><p>1. 注册用户须为年满18周岁的自然人或合法注册的企业<br>2. 车商用户须提交真实有效的营业执照<br>3. 禁止同一车商多次注册账号</p><p><strong>三、交易规范</strong></p><p>1. 所有交易须通过平台保障合同流程<br>2. 定金支付须通过平台担保<br>3. 禁止场外微信转账等绕开平台的行为</p>'
            }
          ]
        },
        {
          title: '找车使用规范',
          desc: '搜索、筛选、联系卖家的操作指南',
          icon: 'search',
          bg: 'linear-gradient(135deg, #10B981, #059669)',
          expanded: false,
          articles: [
            {
              title: '如何搜索车源',
              content: '<p><strong>一、关键词搜索</strong></p><p>在首页搜索框输入品牌、车系或车型名称，即可快速找到相关车源。</p><p><strong>二、筛选条件</strong></p><p>使用顶部筛选标签可以按以下条件筛选：<br>• 最新发布<br>• 新能源（纯电/混动）<br>• 保证金车源<br>• 出口车源</p><p><strong>三、联系卖家</strong></p><p>找到心仪车源后，点击"聊天"按钮即可与卖家在线沟通，或点击"付定"直接下单锁定车源。</p>'
            }
          ]
        },
        {
          title: '找车发车规范',
          desc: '车源发布标准与审核要求',
          icon: 'car',
          bg: 'linear-gradient(135deg, #F59E0B, #D97706)',
          expanded: false,
          articles: [
            {
              title: '车源发布标准',
              content: '<p><strong>一、发布前准备</strong></p><p>1. 完成车商资质认证<br>2. 确保保证金账户余额充足<br>3. 准备车辆真实照片和相关信息</p><p><strong>二、信息填写要求</strong></p><p>1. 品牌/车系/车型须准确无误<br>2. 上牌日期、里程数须真实<br>3. 车况信息须如实填写<br>4. 价格设置须合理</p><p><strong>三、图片要求</strong></p><p>1. 必须上传车辆实拍外观照片<br>2. 支持多张图片上传，可拖拽排序<br>3. 禁止使用网络图片或套用其他车辆图片</p><p><strong>四、审核流程</strong></p><p>提交后平台将在24小时内完成审核，审核通过后车源正式上架。</p>'
            }
          ]
        },
        {
          title: '车源发布规范',
          desc: '发布车源的详细操作流程',
          icon: 'edit-pen',
          bg: 'linear-gradient(135deg, #7C3AED, #6D28D9)',
          expanded: false,
          articles: [
            {
              title: '发布流程详解',
              content: '<p><strong>第一步：基本信息</strong></p><p>填写品牌、车系、车型、上牌日期、里程、颜色等基本信息。</p><p><strong>第二步：车况信息</strong></p><p>选择整体车况（非事故/事故）、漆面状态、结构件状态、发动机状态、变速箱状态等。</p><p><strong>第三步：图片上传</strong></p><p>上传车辆外观、内饰、细节照片，支持最多20张图片。</p><p><strong>第四步：价格设置</strong></p><p>设置售价和保证金金额。可选择一口价或拍卖模式。</p><p><strong>第五步：发布</strong></p><p>确认信息无误后点击发布，等待平台审核。</p>'
            }
          ]
        },
        {
          title: '找车隐私政策',
          desc: '个人信息保护与数据安全',
          icon: 'lock',
          bg: 'linear-gradient(135deg, #EF4444, #DC2626)',
          expanded: false,
          articles: [
            {
              title: '隐私政策',
              content: '<p><strong>一、信息收集</strong></p><p>我们收集以下信息用于提供服务：<br>• 注册信息（手机号、昵称）<br>• 车商信息（营业执照、法人信息）<br>• 交易信息（订单、合同）<br>• 设备信息（用于安全验证）</p><p><strong>二、信息使用</strong></p><p>收集的信息仅用于：<br>• 提供交易平台服务<br>• 身份认证与安全验证<br>• 客户服务与问题处理<br>• 法律法规要求</p><p><strong>三、信息保护</strong></p><p>1. 所有数据加密存储<br>2. 双向端到端加密通信<br>3. 独立部署于安全隔离内网<br>4. 定期安全审计与漏洞扫描</p><p><strong>四、信息共享</strong></p><p>未经用户同意，我们不会向第三方共享个人信息，法律法规要求除外。</p>'
            }
          ]
        },
        {
          title: '交易规范',
          desc: '在线交易流程与保障机制',
          icon: 'order',
          bg: 'linear-gradient(135deg, #06B6D4, #0891B2)',
          expanded: false,
          articles: [
            {
              title: '交易流程说明',
              content: '<p><strong>一、定金保障交易流程</strong></p><p>1. 买家通过"找车"相中车源，点击"聊天"联系车商<br>2. 核实最新价格、提车方式、落户费用<br>3. 在平台一键付定金锁定车源<br>4. 卖家补充合同内容<br>5. 双方现场交接过户<br>6. 确认交易完成</p><p><strong>二、保证金规范</strong></p><p>1. 保证金用于违约和虚假交易赔付<br>2. 保证金已扣除后，买家有权无责全额退还定金<br>3. 未返还前保证金暂不可提现</p><p><strong>三、争议处理</strong></p><p>1. 买卖双方可发起争议<br>2. 平台官方客服介入裁决仲裁<br>3. 享受全程争议追溯</p><p><strong>四、合同签署</strong></p><p>1. 使用全国标准的工商二手车交易买卖通用模板<br>2. 电子签名合同，具备完整法律保障<br>3. 双方确认无误后通过</p>'
            }
          ]
        }
      ]
    }
  },
  methods: {
    toggleGroup(group) {
      group.expanded = !group.expanded
    },
    showArticle(article) {
      this.currentArticle = article
      this.showModal = true
    }
  }
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F8FAFC;
}
.content {
  padding: 20rpx 24rpx;
}
.tutorial-group {
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}
.group-header {
  display: flex;
  align-items: center;
  padding: 28rpx 24rpx;
  cursor: pointer;
  &:active { background: #F8FAFC; }
}
.group-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
  flex-shrink: 0;
}
.group-info {
  flex: 1;
}
.group-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #0F172A;
  display: block;
}
.group-desc {
  font-size: 24rpx;
  color: #94A3B8;
  margin-top: 4rpx;
  display: block;
}
.group-content {
  padding: 0 24rpx 16rpx;
  border-top: 1rpx solid #F1F5F9;
}
.article-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  cursor: pointer;
  border-bottom: 1rpx solid #F8FAFC;
  &:last-child { border-bottom: none; }
  &:active { background: #F8FAFC; }
}
.article-dot {
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background: #0369A1;
  margin-right: 16rpx;
  flex-shrink: 0;
}
.article-title {
  flex: 1;
  font-size: 28rpx;
  color: #334155;
}
.footer-tip {
  text-align: center;
  padding: 40rpx;
  text {
    font-size: 24rpx;
    color: #94A3B8;
  }
}
/* 文章弹窗 */
.article-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-content {
  width: 85%;
  max-height: 80vh;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid #F1F5F9;
}
.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #0F172A;
  flex: 1;
}
.modal-close {
  padding: 8rpx;
}
.modal-body {
  padding: 24rpx;
  max-height: 60vh;
}
</style>
