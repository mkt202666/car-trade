<template>
  <view class="page">
    <u-navbar title="订单详情" :border-bottom="false" :placeholder="true"></u-navbar>

    <view class="page-content">
      <!-- 状态栏 -->
      <view class="status-bar" :class="'status-' + order.status">
        <text class="status-text">{{ order.statusText }}</text>
        <text class="status-desc" v-if="order.statusDesc">· {{ order.statusDesc }}</text>
      </view>

      <!-- 买卖双方信息 -->
      <view class="section">
        <view class="section-title">买卖双方信息</view>
        <view class="party-row">
          <view class="party-info">
            <text class="party-label">买方</text>
            <text class="party-name">{{ order.buyer.name }}({{ order.buyer.city }})</text>
            <text class="party-detail">{{ order.buyer.phone }} | {{ order.buyer.idCard }}</text>
          </view>
        </view>
        <view class="party-row">
          <view class="party-info">
            <text class="party-label">卖方</text>
            <text class="party-name">{{ order.seller.name }}({{ order.seller.city }})</text>
            <text class="party-detail">{{ order.seller.phone }} | {{ order.seller.idCard }}</text>
          </view>
          <view class="call-btn" @click="callSeller">
            <u-icon name="phone" size="32" color="#3c9cff"></u-icon>
            <text>打电话</text>
          </view>
        </view>
      </view>

      <!-- 车辆基本信息 -->
      <view class="section">
        <view class="section-title">车辆基本信息</view>
        <view class="car-basic">
          <text class="car-name">{{ order.carTitle }}</text>
          <text class="car-id">车源ID {{ order.carId }}</text>
        </view>
        <view class="price-row">
          <view class="price-item">
            <text class="price-label">交易总价</text>
            <text class="price-value">{{ order.price / 10000 }}万</text>
          </view>
          <view class="price-item">
            <text class="price-label">定金保障金</text>
            <text class="price-value">{{ order.depositAmount.toLocaleString() }} 元</text>
          </view>
        </view>
      </view>

      <!-- 车辆详情 -->
      <view class="section">
        <view class="section-title">车辆详情</view>
        <view class="detail-grid">
          <view class="detail-item">
            <text class="d-label">车架号</text>
            <text class="d-value">{{ order.vin }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">品牌型号</text>
            <text class="d-value">{{ order.brandModel }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">上牌日期</text>
            <text class="d-value">{{ order.registerDate }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">表显里程</text>
            <text class="d-value">{{ order.mileage }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">车身颜色</text>
            <text class="d-value">{{ order.color }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">年检到期</text>
            <text class="d-value">{{ order.annualInspection }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">强险到期</text>
            <text class="d-value">{{ order.insuranceExpire }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">出厂年月</text>
            <text class="d-value">{{ order.manufactureDate || '--' }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">钥匙数量</text>
            <text class="d-value">{{ order.keyCount }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">使用性质</text>
            <text class="d-value">{{ order.usageType }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">所有人性质</text>
            <text class="d-value">{{ order.ownerType }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">是否抵押车</text>
            <text class="d-value">{{ order.hasMortgage ? '是' : '否' }}</text>
          </view>
          <view class="detail-item">
            <text class="d-label">是否有继承</text>
            <text class="d-value">{{ order.hasInheritance ? '是' : '否' }}</text>
          </view>
        </view>
      </view>

      <!-- 价格确认 -->
      <view class="section">
        <view class="section-title">价格确认</view>
        <view class="price-confirm">
          <view class="pc-item">
            <text class="pc-label">交易总价 (元)</text>
            <text class="pc-value">{{ order.price.toLocaleString() }}</text>
          </view>
          <view class="pc-item">
            <text class="pc-label">保证金 (元)</text>
            <text class="pc-value">{{ order.depositAmount.toLocaleString() }}</text>
          </view>
        </view>
      </view>

      <!-- 车况信息 -->
      <view class="section">
        <view class="section-title">车况信息</view>
        <view class="condition-grid">
          <view class="condition-item">
            <text class="c-label">整体车况</text>
            <text class="c-value">{{ order.condition.overall }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">漆面</text>
            <text class="c-value">{{ order.condition.paint }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">结构件</text>
            <text class="c-value">{{ order.condition.structure }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">发动机</text>
            <text class="c-value">{{ order.condition.engine }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">变速箱</text>
            <text class="c-value">{{ order.condition.gearbox }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">过户次数</text>
            <text class="c-value">{{ order.condition.ownershipTransfer }}</text>
          </view>
          <view class="condition-item">
            <text class="c-label">公里数</text>
            <text class="c-value">{{ order.condition.mileageType }}</text>
          </view>
        </view>
      </view>

      <!-- 车况描述 -->
      <view class="section">
        <view class="section-title">车况描述及其他特别约定 (选填)</view>
        <textarea class="desc-textarea" :value="order.conditionDesc" placeholder="未填写" disabled></textarea>
      </view>

      <!-- 车况异常照片 -->
      <view class="section">
        <view class="section-title">车况异常照片 (选填)</view>
        <view class="upload-hint" v-if="order.conditionPhotos.length === 0">
          <text>可上传车况异常部位图片，支持多张上传</text>
        </view>
        <view class="photo-list" v-if="order.conditionPhotos.length > 0">
          <image v-for="(img, i) in order.conditionPhotos" :key="i" :src="img" mode="aspectFill" class="photo-thumb"></image>
        </view>
      </view>

      <!-- 车辆材料 -->
      <view class="section">
        <view class="section-title">车辆材料 (选填)</view>
        <view class="upload-hint">
          <text>可以上传行驶证、产权证、钥匙、车况图片等材料</text>
        </view>
      </view>

      <!-- 订单信息 -->
      <view class="section">
        <view class="section-title">订单信息</view>
        <view class="order-info-list">
          <view class="oi-item">
            <text class="oi-label">合同编号</text>
            <text class="oi-value">{{ order.contractId }}</text>
          </view>
          <view class="oi-item">
            <text class="oi-label">创建时间</text>
            <text class="oi-value">{{ order.createTime }}</text>
          </view>
          <view class="oi-item">
            <text class="oi-label">买方保障金</text>
            <text class="oi-value">{{ order.buyerDeposit.toLocaleString() }} 元（{{ order.buyerDepositStatus }}）</text>
          </view>
          <view class="oi-item">
            <text class="oi-label">卖方保障金</text>
            <text class="oi-value">{{ order.sellerDeposit.toLocaleString() }} 元（{{ order.sellerDepositStatus }}）</text>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-bar" v-if="showActions">
        <!-- 待确认状态 -->
        <u-button v-if="order.status === 'PENDING'" type="warning" @click="handleCancel" :plain="true">取消订单</u-button>
        <u-button v-if="order.status === 'PENDING'" type="primary" @click="handleConfirm">确认订单</u-button>

        <!-- 已确认状态 -->
        <u-button v-if="order.status === 'CONFIRMED'" type="primary" @click="handlePayDeposit">支付保证金</u-button>

        <!-- 已付保证金状态 -->
        <u-button v-if="order.status === 'DEPOSIT_PAID'" type="primary" @click="showContractSubmit = true">提交合同</u-button>

        <!-- 已提交合同状态 -->
        <u-button v-if="order.status === 'CONTRACT_SUBMITTED'" type="warning" @click="showContractEdit = true">修改合同</u-button>
        <u-button v-if="order.status === 'CONTRACT_SUBMITTED'" type="primary" @click="handleConfirmContract">确认合同</u-button>

        <!-- 已确认合同状态 -->
        <u-button v-if="order.status === 'CONTRACT_CONFIRMED'" type="success" @click="handleComplete">确认交易完成</u-button>

        <!-- 交易中状态 -->
        <u-button v-if="order.status === 'IN_TRANSACTION'" type="warning" @click="showTerminate = true" :plain="true">终止交易</u-button>
        <u-button v-if="order.status === 'IN_TRANSACTION'" type="success" @click="handleComplete">确认交易完成</u-button>

        <!-- 争议中状态 -->
        <u-button v-if="order.status === 'DISPUTE'" type="info" @click="toDisputeDetail">查看争议详情</u-button>

        <!-- 已完成状态 -->
        <u-button v-if="order.status === 'COMPLETED'" type="primary" @click="toContract" :plain="true">查看合同</u-button>
      </view>

      <!-- 取消订单弹窗 -->
      <u-popup v-model="showCancelModal" mode="center" border-radius="16" :mask-closeable="true">
        <view class="cancel-popup">
          <view class="popup-title">取消订单</view>
          <view class="popup-content">
            <text class="popup-label">取消原因</text>
            <u-input v-model="cancelReason" type="textarea" :height="120" :maxlength="200"
              placeholder="请输入取消订单的原因" clearable />
          </view>
          <view class="popup-footer">
            <u-button @click="showCancelModal = false" :plain="true" size="medium">取消</u-button>
            <u-button @click="handleCancelConfirm" type="error" size="medium">确认取消</u-button>
          </view>
        </view>
      </u-popup>

      <!-- 发起纠纷弹窗 -->
      <u-popup v-model="showDisputeModal" mode="center" border-radius="16" :mask-closeable="true">
        <view class="dispute-popup">
          <view class="popup-title">发起纠纷</view>
          <view class="popup-content">
            <view class="form-item">
              <text class="popup-label">纠纷类型</text>
              <u-input v-model="disputeForm.reason" type="text" placeholder="如：车辆信息不符、支付问题等" clearable />
            </view>
            <view class="form-item">
              <text class="popup-label">详细描述</text>
              <u-input v-model="disputeForm.description" type="textarea" :height="160" :maxlength="500"
                placeholder="请详细描述纠纷情况" clearable />
            </view>
          </view>
          <view class="popup-footer">
            <u-button @click="showDisputeModal = false" :plain="true" size="medium">取消</u-button>
            <u-button @click="handleCreateDispute" type="warning" size="medium">提交纠纷</u-button>
          </view>
        </view>
      </u-popup>

      <!-- 提交合同弹窗 -->
      <u-popup v-model="showContractSubmit" mode="center" border-radius="16" :mask-closeable="true">
        <view class="contract-popup">
          <view class="popup-title">提交合同</view>
          <view class="popup-content">
            <view class="form-item">
              <text class="popup-label">合同内容</text>
              <u-input v-model="contractForm.content" type="textarea" :height="200" :maxlength="1000"
                placeholder="请输入合同内容" clearable />
            </view>
            <text class="popup-tip">提交后不能再修改合同</text>
          </view>
          <view class="popup-footer">
            <u-button @click="showContractSubmit = false" :plain="true" size="medium">取消</u-button>
            <u-button @click="handleSubmitContract" type="primary" size="medium">提交</u-button>
          </view>
        </view>
      </u-popup>

      <!-- 修改合同弹窗 -->
      <u-popup v-model="showContractEdit" mode="center" border-radius="16" :mask-closeable="true">
        <view class="contract-popup">
          <view class="popup-title">修改合同</view>
          <view class="popup-content">
            <view class="form-item">
              <text class="popup-label">合同内容</text>
              <u-input v-model="contractForm.content" type="textarea" :height="200" :maxlength="1000"
                placeholder="请输入合同内容" clearable />
            </view>
          </view>
          <view class="popup-footer">
            <u-button @click="showContractEdit = false" :plain="true" size="medium">取消</u-button>
            <u-button @click="handleUpdateContract" type="primary" size="medium">保存</u-button>
          </view>
        </view>
      </u-popup>

      <!-- 终止交易弹窗 -->
      <u-popup v-model="showTerminate" mode="center" border-radius="16" :mask-closeable="true">
        <view class="terminate-popup">
          <view class="popup-title">终止交易</view>
          <view class="popup-content">
            <view class="form-item">
              <text class="popup-label">终止交易原因</text>
              <u-input v-model="terminateForm.reason" type="textarea" :height="160" :maxlength="200"
                placeholder="请输入终止交易的原因" clearable />
            </view>
            <view class="terminate-info">
              <text class="info-text">您今日还可主动终止交易 {{ terminateRemaining }} 次</text>
            </view>
          </view>
          <view class="popup-footer">
            <u-button @click="showTerminate = false" :plain="true" size="medium">取消</u-button>
            <u-button @click="handleTerminate" type="warning" size="medium">终止交易</u-button>
          </view>
        </view>
      </u-popup>

      <!-- 确认交易完成弹窗 -->
      <u-popup v-model="showCompleteConfirm" mode="center" border-radius="16" :mask-closeable="true">
        <view class="complete-popup">
          <view class="popup-title">确认交易完成</view>
          <view class="popup-content">
            <text class="complete-tip">上传规范的凭证将保护您的合法权益，超时未确认将暂时冻结您的在线交易权限</text>
          </view>
          <view class="popup-footer">
            <u-button @click="showCompleteConfirm = false" :plain="true" size="medium">取消</u-button>
            <u-button @click="confirmComplete" type="success" size="medium">确认完成</u-button>
          </view>
        </view>
      </u-popup>
    </view>
  </view>
</template>

<script>
import { getOrderDetail, confirmOrder, cancelOrder, completeOrder, createDispute, submitContract, updateContract, confirmContract, terminateOrder, getTerminateCount } from '@/api/order'
import { formatPrice } from '@/utils/format'
import { requireAuth } from '@/utils/auth'

export default {
  data() {
    return {
      order: {
        id: '',
        orderNo: '',
        carId: '',
        carTitle: '',
        carImage: '',
        price: 0,
        depositAmount: 0,
        buyerDeposit: 0,
        buyerDepositStatus: '',
        sellerDeposit: 0,
        sellerDepositStatus: '',
        status: '',
        statusText: '',
        statusDesc: '',
        buyer: { name: '', city: '', phone: '', idCard: '' },
        seller: { name: '', city: '', phone: '', idCard: '' },
        contractId: '',
        contractContent: '',
        createTime: '',
        vin: '',
        brandModel: '',
        registerDate: '',
        mileage: 0,
        color: '',
        annualInspection: '',
        insuranceExpire: '',
        manufactureDate: '',
        keyCount: 0,
        usageType: '',
        ownerType: '',
        hasMortgage: false,
        hasInheritance: false,
        condition: { overall: '', paint: '', structure: '', engine: '', gearbox: '', ownershipTransfer: '', mileageType: '' },
        conditionDesc: '',
        conditionPhotos: []
      },
      loading: false,
      cancelReason: '',
      showCancelModal: false,
      disputeForm: {
        reason: '',
        description: '',
        evidenceImages: []
      },
      showDisputeModal: false,

      // 合同相关
      contractForm: {
        content: ''
      },
      showContractSubmit: false,
      showContractEdit: false,

      // 终止交易相关
      terminateForm: {
        reason: ''
      },
      showTerminate: false,
      terminateRemaining: 3,

      // 确认完成
      showCompleteConfirm: false
    }
  },
  computed: {
    showActions() {
      // 已取消和已终止的订单不显示操作按钮
      return this.order.status &&
             this.order.status !== 'CANCELLED' &&
             this.order.status !== 'TERMINATED'
    },
    statusTextMap() {
      return {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'DEPOSIT_PAID': '已付保证金',
        'CONTRACT_SUBMITTED': '已提交合同',
        'CONTRACT_CONFIRMED': '已确认合同',
        'IN_TRANSACTION': '交易中',
        'DISPUTE': '争议中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消',
        'TERMINATED': '已终止'
      }
    }
  },
  onLoad(options) {
    if (options.id) {
      this.fetchOrderDetail(options.id)
      this.loadTerminateCount()
    } else {
      uni.$u.toast('参数错误')
      setTimeout(() => uni.navigateBack(), 1500)
    }
  },
  methods: {
    formatPrice,
    async fetchOrderDetail(id) {
      if (!requireAuth()) return
      
      this.loading = true
      try {
        const res = await getOrderDetail(id)
        const data = res.data || res
        
        this.order = {
          id: data.id || '',
          orderNo: data.contractNo || data.id || '',
          carId: data.carId || '',
          carTitle: data.carName || data.carTitle || '',
          carImage: data.carImage || '',
          price: data.totalPrice || 0,
          depositAmount: data.depositAmount || 0,
          buyerDeposit: data.buyerDepositPaid ? (data.depositAmount || 0) : 0,
          buyerDepositStatus: data.buyerDepositPaid ? '已支付' : '未支付',
          sellerDeposit: data.sellerDepositPaid ? (data.depositAmount || 0) : 0,
          sellerDepositStatus: data.sellerDepositPaid ? '已支付' : '未支付',
          status: data.status || '',
          statusText: this.statusTextMap[data.status] || data.status || '',
          statusDesc: data.depositStatus || '',
          buyer: { 
            name: data.buyerName || '', 
            city: data.city || '', 
            phone: data.buyerPhone || '', 
            idCard: data.buyerIdCard || '' 
          },
          seller: { 
            name: data.sellerName || '', 
            city: data.city || '', 
            phone: data.sellerPhone || '', 
            idCard: data.sellerIdCard || '' 
          },
          contractId: data.contractNo || '',
          createTime: data.createdAt || '',
          vin: data.vin || '',
          brandModel: [data.brandName, data.seriesName, data.modelName].filter(Boolean).join(' ') || '',
          registerDate: data.registerDate || '',
          mileage: data.mileage || 0,
          color: data.color || '',
          annualInspection: data.annualInspection || '',
          insuranceExpire: data.insuranceExpire || '',
          manufactureDate: data.manufactureDate || '',
          keyCount: data.keyCount || 0,
          usageType: data.usageType || '',
          ownerType: data.ownerType || '',
          hasMortgage: data.hasMortgage || false,
          hasInheritance: data.hasInheritance || false,
          condition: { 
            overall: data.overallCondition || '', 
            paint: data.paint || '', 
            structure: data.structure || '', 
            engine: data.engine || '', 
            gearbox: data.transmission || '', 
            ownershipTransfer: data.transferCount || '', 
            mileageType: data.mileageType || '' 
          },
          conditionDesc: data.conditionDesc || data.materials || '',
          conditionPhotos: data.conditionPhotos || []
        }
      } catch (e) {
        console.error('加载订单详情失败', e)
        uni.$u.toast('加载订单详情失败，请重试')
      } finally {
        this.loading = false
      }
    },
    callSeller() {
      const phone = this.order.seller.phone
      if (!phone) {
        uni.$u.toast('暂无卖家电话')
        return
      }
      uni.makePhoneCall({
        phoneNumber: phone,
        fail: (err) => {
          console.error('拨打电话失败', err)
          uni.$u.toast('拨打电话失败')
        }
      })
    },
    toContract() {
      if (!this.order.contractId) {
        uni.$u.toast('暂无合同信息')
        return
      }
      uni.navigateTo({ url: '/pages/contract-detail/index?id=' + this.order.contractId })
    },
    async handleConfirm() {
      if (!requireAuth()) return
      
      try {
        await confirmOrder(this.order.id)
        uni.$u.toast('订单已确认')
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('确认订单失败', e)
        uni.$u.toast('操作失败，请重试')
      }
    },
    showCancelDialog() {
      if (!requireAuth()) return
      this.cancelReason = ''
      this.showCancelModal = true
    },
    async handleCancelConfirm() {
      if (!this.cancelReason || !this.cancelReason.trim()) {
        uni.$u.toast('请输入取消原因')
        return
      }
      
      try {
        await cancelOrder(this.order.id, this.cancelReason.trim())
        uni.$u.toast('订单已取消')
        this.showCancelModal = false
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('取消订单失败', e)
        uni.$u.toast('取消失败，请重试')
      }
    },
    handleCancel() {
      this.showCancelDialog()
    },
    async handleComplete() {
      if (!requireAuth()) return
      
      try {
        await completeOrder(this.order.id)
        uni.$u.toast('交易已完成')
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('完成交易失败', e)
        uni.$u.toast('操作失败，请重试')
      }
    },
    showDisputeDialog() {
      if (!requireAuth()) return
      this.disputeForm = {
        reason: '',
        description: '',
        evidenceImages: []
      }
      this.showDisputeModal = true
    },
    handleDispute() {
      this.showDisputeDialog()
    },
    async handleCreateDispute() {
      if (!this.disputeForm.reason || !this.disputeForm.reason.trim()) {
        uni.$u.toast('请选择纠纷类型')
        return
      }
      if (!this.disputeForm.description || !this.disputeForm.description.trim()) {
        uni.$u.toast('请输入纠纷描述')
        return
      }

      try {
        await createDispute(this.order.id, {
          reason: this.disputeForm.reason.trim(),
          description: this.disputeForm.description.trim(),
          evidenceImages: this.disputeForm.evidenceImages
        })
        uni.$u.toast('纠纷已提交')
        this.showDisputeModal = false
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('提交纠纷失败', e)
        uni.$u.toast('提交失败，请重试')
      }
    },

    // 支付保证金
    async handlePayDeposit() {
      if (!requireAuth()) return

      try {
        await payDeposit(this.order.id)
        uni.$u.toast('保证金支付成功')
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('支付保证金失败', e)
        uni.$u.toast('支付失败，请重试')
      }
    },

    // 提交合同
    async handleSubmitContract() {
      if (!this.contractForm.content || !this.contractForm.content.trim()) {
        uni.$u.toast('请输入合同内容')
        return
      }

      try {
        await submitContract(this.order.id, {
          content: this.contractForm.content.trim()
        })
        uni.$u.toast('合同已提交')
        this.showContractSubmit = false
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('提交合同失败', e)
        uni.$u.toast('提交失败，请重试')
      }
    },

    // 修改合同
    async handleUpdateContract() {
      if (!this.contractForm.content || !this.contractForm.content.trim()) {
        uni.$u.toast('请输入合同内容')
        return
      }

      try {
        await updateContract(this.order.id, {
          content: this.contractForm.content.trim()
        })
        uni.$u.toast('合同已更新')
        this.showContractEdit = false
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('更新合同失败', e)
        uni.$u.toast('更新失败，请重试')
      }
    },

    // 确认合同
    async handleConfirmContract() {
      if (!requireAuth()) return

      uni.showModal({
        title: '确认合同',
        content: '是否确认提交？提交后不能再修改合同。',
        success: async (res) => {
          if (res.confirm) {
            try {
              await confirmContract(this.order.id)
              uni.$u.toast('合同已确认')
              this.fetchOrderDetail(this.order.id)
            } catch (e) {
              console.error('确认合同失败', e)
              uni.$u.toast('确认失败，请重试')
            }
          }
        }
      })
    },

    // 终止交易
    async handleTerminate() {
      if (!this.terminateForm.reason || !this.terminateForm.reason.trim()) {
        uni.$u.toast('请输入终止交易原因')
        return
      }

      try {
        await terminateOrder(this.order.id, {
          reason: this.terminateForm.reason.trim()
        })
        uni.$u.toast('交易已终止')
        this.showTerminate = false
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('终止交易失败', e)
        uni.$u.toast('终止失败，请重试')
      }
    },

    // 确认交易完成
    handleComplete() {
      if (!requireAuth()) return
      this.showCompleteConfirm = true
    },

    // 确认完成
    async confirmComplete() {
      try {
        await completeOrder(this.order.id)
        uni.$u.toast('交易已完成')
        this.showCompleteConfirm = false
        this.fetchOrderDetail(this.order.id)
      } catch (e) {
        console.error('完成交易失败', e)
        uni.$u.toast('操作失败，请重试')
      }
    },

    // 查看争议详情
    toDisputeDetail() {
      uni.navigateTo({
        url: `/pages/dispute-detail/index?orderId=${this.order.id}`
      })
    },

    // 加载终止交易次数
    async loadTerminateCount() {
      try {
        const res = await getTerminateCount(this.order.id)
        if (res.data) {
          this.terminateRemaining = res.data.remaining || 0
        }
      } catch (e) {
        console.error('加载终止交易次数失败', e)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
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
  padding-bottom: 140rpx;
}

/* 状态栏 */
.status-bar {
  padding: 30rpx;
  color: #fff;
  font-size: 26rpx;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
.status-text {
  font-size: 30rpx;
  font-weight: 600;
}
.status-desc {
  margin-left: 8rpx;
  opacity: 0.9;
}
.status-PENDING { background: linear-gradient(135deg, #f9ae3d, #f7b731); }
.status-CONFIRMED { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
.status-IN_TRANSACTION { background: linear-gradient(135deg, $cta-color, darken($cta-color, 10%)); }
.status-DISPUTE { background: linear-gradient(135deg, #f56c6c, #e74c3c); }
.status-COMPLETED { background: linear-gradient(135deg, #5ac725, #4ca81b); }
.status-CANCELLED { background: linear-gradient(135deg, #999, #777); }

/* 通用区块 */
.section {
  background: #fff;
  margin: 20rpx;
  padding: 30rpx;
  border-radius: $border-radius;
  box-shadow: $shadow;
}
.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  margin-bottom: 20rpx;
}

/* 买卖双方 */
.party-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10rpx 0;
}
.party-info {
  flex: 1;
}
.party-label {
  font-size: 22rpx;
  color: $text-secondary;
}
.party-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
  margin-top: 4rpx;
}
.party-detail {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-top: 4rpx;
}
.call-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  font-size: 20rpx;
  color: $cta-color;
  cursor: pointer;
  transition: $transition;
  padding: 12rpx 20rpx;
  border-radius: $border-radius;

  &:active {
    background: rgba(3, 105, 161, 0.1);
    transform: scale(0.95);
  }
}

/* 车辆基本信息 */
.car-basic {
  margin-bottom: 20rpx;
}
.car-name {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
  display: block;
}
.car-id {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
  margin-top: 6rpx;
}
.price-row {
  display: flex;
  gap: 40rpx;
}
.price-item {
  flex: 1;
}
.price-label {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
}
.price-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #f56c6c;
  display: block;
  margin-top: 4rpx;
}

/* 车辆详情 */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
.detail-item {
  padding: 12rpx 0;
  border-bottom: 1rpx solid $border-color;
}
.d-label {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
}
.d-value {
  font-size: 26rpx;
  color: $text-color;
  display: block;
  margin-top: 4rpx;
}

/* 价格确认 */
.price-confirm {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.pc-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  border-bottom: 1rpx solid $border-color;
}
.pc-label {
  font-size: 26rpx;
  color: $text-color;
}
.pc-value {
  font-size: 28rpx;
  font-weight: 600;
  color: $text-color;
}

/* 车况信息 */
.condition-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
.condition-item {
  padding: 12rpx 0;
  border-bottom: 1rpx solid $border-color;
}
.c-label {
  font-size: 22rpx;
  color: $text-secondary;
  display: block;
}
.c-value {
  font-size: 26rpx;
  color: $text-color;
  font-weight: 600;
  display: block;
  margin-top: 4rpx;
}

/* 车况描述 */
.desc-textarea {
  width: 100%;
  min-height: 120rpx;
  background: $bg-color;
  border-radius: $border-radius;
  padding: 20rpx;
  font-size: 26rpx;
  color: $text-color;
  border: 1rpx solid $border-color;
}

/* 上传提示 */
.upload-hint {
  padding: 30rpx;
  background: $bg-color;
  border-radius: $border-radius;
  text-align: center;
  font-size: 24rpx;
  color: $text-secondary;
}
.photo-list {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}
.photo-thumb {
  width: 140rpx;
  height: 140rpx;
  border-radius: $border-radius;
  cursor: pointer;
  transition: $transition;

  &:active {
    opacity: 0.8;
    transform: scale(0.98);
  }
}

/* 订单信息 */
.order-info-list {
  display: flex;
  flex-direction: column;
}
.oi-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid $border-color;
}
.oi-label {
  font-size: 26rpx;
  color: $text-color;
}
.oi-value {
  font-size: 24rpx;
  color: $text-secondary;
  max-width: 60%;
  text-align: right;
}

/* 操作按钮 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  display: flex;
  gap: 20rpx;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid $border-color;
  box-shadow: 0 -4rpx 16rpx rgba(15, 23, 42, 0.08);
  z-index: 10;
}
.action-bar u-button {
  flex: 1;
}

/* 弹窗样式 */
.cancel-popup,
.dispute-popup {
  width: 600rpx;
  padding: 40rpx;
}
.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-color;
  text-align: center;
  margin-bottom: 30rpx;
}
.popup-content {
  margin-bottom: 30rpx;
}
.popup-label {
  font-size: 26rpx;
  color: $text-secondary;
  display: block;
  margin-bottom: 12rpx;
}
.form-item {
  margin-bottom: 24rpx;
}
.form-item .popup-label {
  margin-bottom: 12rpx;
}
.popup-footer {
  display: flex;
  gap: 20rpx;
  justify-content: center;
}

/* 合同弹窗样式 */
.contract-popup {
  width: 650rpx;
  padding: 40rpx;
}

.popup-tip {
  font-size: 22rpx;
  color: #f56c6c;
  margin-top: 16rpx;
  display: block;
}

/* 终止交易弹窗样式 */
.terminate-popup {
  width: 600rpx;
  padding: 40rpx;
}

.terminate-info {
  margin-top: 20rpx;
  padding: 16rpx;
  background: #fff3cd;
  border-radius: 8rpx;
}

.info-text {
  font-size: 24rpx;
  color: #856404;
}

/* 确认完成弹窗样式 */
.complete-popup {
  width: 600rpx;
  padding: 40rpx;
}

.complete-tip {
  font-size: 26rpx;
  color: $text-secondary;
  line-height: 1.5;
}

/* 新增状态样式 */
.status-DEPOSIT_PAID { background: linear-gradient(135deg, #17a2b8, #138496); }
.status-CONTRACT_SUBMITTED { background: linear-gradient(135deg, #6f42c1, #5a32a3); }
.status-CONTRACT_CONFIRMED { background: linear-gradient(135deg, #28a745, #1e7e34); }
.status-TERMINATED { background: linear-gradient(135deg, #6c757d, #545b62); }
</style>