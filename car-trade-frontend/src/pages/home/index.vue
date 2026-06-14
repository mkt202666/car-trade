<template>
  <view class="page">
    <!-- 顶部滚动广告位（带悬浮 Logo） -->
    <view class="ad-section">
      <swiper class="ad-swiper" indicator-dots indicator-color="rgba(255,255,255,0.5)" indicator-active-color="#ffffff" circular autoplay :interval="3500" duration="600">
        <swiper-item v-for="(ad, idx) in adList" :key="idx">
          <view class="ad-slide" :style="{ background: ad.bg }">
            <view class="ad-text">
              <text class="ad-title">{{ ad.title }}</text>
              <text class="ad-sub">{{ ad.subtitle }}</text>
            </view>
            <text class="ad-tag" v-if="ad.tag">{{ ad.tag }}</text>
          </view>
        </swiper-item>
      </swiper>

      <!-- 状态栏占位 -->
      <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>

      <!-- 悬浮顶部：Logo + 语言切换 -->
      <view class="ad-header">
        <view class="ad-logo-area">
          <text class="ad-logo">5D好车</text>
          <text class="ad-subtitle">AI赋能二手车拓展商机</text>
        </view>
        <view class="ad-lang">
          <text class="lang-text">中文</text>
          <u-icon name="arrow-down" size="20" color="#ffffff"></u-icon>
        </view>
      </view>

      <!-- 渐变遮罩（平滑过渡到下方白色） -->
      <view class="ad-bottom-mask"></view>
    </view>

    <!-- 搜索栏（白底 + 城市选择器 + 搜索框 + 搜索按钮 + 筛选） -->
    <view class="search-wrap">
      <view class="city-picker" @click="selectCity">
        <text class="city-text">{{ currentCity }}</text>
        <text class="city-arrow">▾</text>
      </view>
      <view class="search-field">
        <u-icon name="search" size="28" color="#9ca3af"></u-icon>
        <input class="search-input" placeholder="请输入搜索内容" placeholder-style="color:#9ca3af" v-model="searchKeyword" confirm-type="search" @confirm="doSearch" />
      </view>
      <view class="search-btn" @click="doSearch">
        <text>搜索</text>
      </view>
      <view class="filter-icon" @click="openFilter">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="4" y1="21" x2="4" y2="14"/><line x1="4" y1="10" x2="4" y2="3"/><line x1="12" y1="21" x2="12" y2="12"/><line x1="12" y1="8" x2="12" y2="3"/><line x1="20" y1="21" x2="20" y2="16"/><line x1="20" y1="12" x2="20" y2="3"/><line x1="1" y1="14" x2="7" y2="14"/><line x1="9" y1="8" x2="15" y2="8"/><line x1="17" y1="16" x2="23" y2="16"/></svg>
        <view class="filter-dot" v-if="hasActiveFilter"></view>
      </view>
    </view>

    <!-- 一级筛选 Tab（原型：胶囊按钮风格） -->
    <view class="tabs-wrap">
      <view class="tabs-inner">
        <view
          class="tab-pill"
          v-for="(tab, index) in filterTabs"
          :key="tab.key"
          :class="{ active: currentTab === index }"
          @click="switchTab(index)"
        >
          <text class="tab-label">{{ tab.label }}</text>
        </view>
      </view>
    </view>

    <!-- 出口二级区域筛选（点击"出口"Tab 时显示）：中东/俄罗斯/非洲/远东/东南亚/南美 -->
    <view class="region-wrap" v-if="currentTab === 3">
      <view class="region-inner">
        <view
          class="region-pill"
          v-for="(r, i) in exportRegions"
          :key="r.code"
          :class="{ active: currentRegion === r.code }"
          @click="filterByRegion(r.code)"
        >
          <text class="region-label">{{ r.name }}</text>
        </view>
      </view>
    </view>

    <!-- 车源列表：单列卡片（左图右信息） -->
    <view class="car-list">
      <view class="car-card" v-for="item in carList" :key="item.id" @click="toDetail(item.id)">
        <!-- 左侧图 -->
        <view class="card-image-wrap">
          <image :src="item.coverImage || defaultImage" mode="aspectFill" class="car-image"></image>
          <view class="image-tags">
            <view class="tag-energy pure" v-if="item.energyType === '纯电'">纯电</view>
            <view class="tag-energy hybrid" v-else-if="item.energyType === '混动'">混动</view>
            <view class="tag-energy fuel" v-else>燃油</view>
          </view>
        </view>

        <!-- 右侧信息 -->
        <view class="card-info">
          <view class="car-title">{{ item.brandName }} {{ item.seriesName }} {{ item.modelName || '' }}</view>
          <view class="car-sub-row">
            <text class="car-sub">{{ item.year }}年</text>
            <text class="dot">·</text>
            <text class="car-sub">{{ formatMileage(item.mileage) }}公里</text>
          </view>

          <!-- 标签行（保证金 + 出口） -->
          <view class="meta-row">
            <view class="deposit-badge" v-if="item.hasDeposit">保证金</view>
            <view class="export-country-badge" v-for="(code, i) in (item.exportCountries || []).slice(0, 2)" :key="i">{{ flagMap[code] || code }}</view>
          </view>

          <!-- 位置/时间 -->
          <view class="location-row">
            <text>{{ item.city || '全国' }}</text>
            <text class="dot">·</text>
            <text>{{ formatTime(item.createdAt) }}</text>
          </view>

          <!-- 价格（右下红色大字） -->
          <view class="price-row">
            <text class="price-value">{{ formatPrice(item.price) }}<text class="price-unit">万</text></text>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="carList.length > 0">
      <text>{{ hasMore ? '上拉加载更多' : '已加载全部车源' }}</text>
    </view>

    <!-- 空状态 -->
    <view class="empty" v-if="carList.length === 0 && !loading">
      <u-icon name="shopping-cart" size="80" color="#e5e7eb"></u-icon>
      <text class="empty-text">暂无车源</text>
    </view>

    <!-- 右侧悬浮"+ 开始"按钮 -->
    <view class="fab-single" @click="openMenu">
      <view class="fab-single-circle">
        <text class="fab-single-plus">+</text>
      </view>
      <text class="fab-single-label">开始</text>
    </view>

    <!-- 菜单弹窗 -->
    <view class="menu-popup-mask" v-if="showMenu" @click="closeMenu"></view>
    <view class="menu-popup" v-if="showMenu" @click.stop>
      <view class="menu-item" @click="gotoPublish">
        <text class="menu-text">发布车源</text>
      </view>
      <view class="menu-item" @click="gotoPurchaseDemand">
        <text class="menu-text">发布求购</text>
      </view>
      <view class="menu-item" @click="toAI">
        <text class="menu-icon">✈</text>
        <text class="menu-text highlight">AI助理</text>
      </view>
      <view class="menu-item" @click="toCustomerService">
        <text class="menu-text">客服帮助</text>
      </view>
      <view class="menu-close-btn" @click="closeMenu">
        <text class="menu-close-icon">×</text>
      </view>
    </view>

    <!-- 底部 TabBar 占位 -->
    <view class="tabbar-placeholder"></view>

    <!-- 筛选弹窗 -->
    <u-popup :show="showFilter" mode="bottom" round="24" :closeable="true" @close="showFilter = false">
      <view class="filter-popup-wrapper">
      <view class="filter-popup">
        <!-- 筛选标题 -->
        <view class="filter-header">
          <text class="filter-title">高级筛选</text>
        </view>

        <!-- 可滚动筛选内容区 -->
        <view class="filter-scroll">

        <!-- 品牌/车系选择 -->
        <view class="filter-section">
          <view class="section-title">品牌/车系</view>
          <view class="select-box" @click="selectBrand">
            <text class="select-value" :class="{ placeholder: !filterData.brandName }">{{ filterData.brandName || '请选择品牌' }}</text>
            <u-icon name="arrow-right" size="20" color="#94a3b8"></u-icon>
          </view>
        </view>

        <!-- 价格区间 -->
        <view class="filter-section">
          <view class="section-title">
            <text>价格区间（万）</text>
            <text class="range-value">{{ filterData.minPrice || 0 }} - {{ filterData.maxPrice || '不限' }}</text>
          </view>
          <u-slider
            v-model="priceRange"
            :min="0"
            :max="100"
            :step="5"
            :active-color="'#0369A1'"
            :inactive-color="'#E2E8F0'"
            :show-active-value="false"
          ></u-slider>
          <view class="quick-price">
            <view class="quick-price-item" :class="{ active: filterData.priceRange === '0-5' }" @tap.stop="setQuickPrice('0-5')">5万以下</view>
            <view class="quick-price-item" :class="{ active: filterData.priceRange === '5-10' }" @tap.stop="setQuickPrice('5-10')">5-10万</view>
            <view class="quick-price-item" :class="{ active: filterData.priceRange === '10-15' }" @tap.stop="setQuickPrice('10-15')">10-15万</view>
            <view class="quick-price-item" :class="{ active: filterData.priceRange === '15-20' }" @tap.stop="setQuickPrice('15-20')">15-20万</view>
            <view class="quick-price-item" :class="{ active: filterData.priceRange === '20-30' }" @tap.stop="setQuickPrice('20-30')">20-30万</view>
            <view class="quick-price-item" :class="{ active: filterData.priceRange === '30-100' }" @tap.stop="setQuickPrice('30-100')">30万以上</view>
          </view>
        </view>

        <!-- 车龄要求 -->
        <view class="filter-section">
          <view class="section-title">
            <text>车龄要求（年）</text>
            <text class="range-value">{{ filterData.minAge || 0 }} - {{ filterData.maxAge || '不限' }}</text>
          </view>
          <u-slider
            v-model="ageRange"
            :min="0"
            :max="20"
            :step="1"
            :active-color="'#0369A1'"
            :inactive-color="'#E2E8F0'"
            :show-active-value="false"
          ></u-slider>
          <view class="quick-age">
            <view class="quick-age-item" :class="{ active: filterData.ageRange === '0-1' }" @tap.stop="setQuickAge('0-1')">1年以内</view>
            <view class="quick-age-item" :class="{ active: filterData.ageRange === '1-3' }" @tap.stop="setQuickAge('1-3')">1-3年</view>
            <view class="quick-age-item" :class="{ active: filterData.ageRange === '3-5' }" @tap.stop="setQuickAge('3-5')">3-5年</view>
            <view class="quick-age-item" :class="{ active: filterData.ageRange === '5-8' }" @tap.stop="setQuickAge('5-8')">5-8年</view>
            <view class="quick-age-item" :class="{ active: filterData.ageRange === '8-20' }" @tap.stop="setQuickAge('8-20')">8年以上</view>
          </view>
        </view>

        <!-- 表显里程 -->
        <view class="filter-section">
          <view class="section-title">
            <text>表显里程（万公里）</text>
            <text class="range-value">{{ filterData.minMileage || 0 }} - {{ filterData.maxMileage || '不限' }}</text>
          </view>
          <u-slider
            v-model="mileageRange"
            :min="0"
            :max="50"
            :step="1"
            :active-color="'#0369A1'"
            :inactive-color="'#E2E8F0'"
            :show-active-value="false"
          ></u-slider>
          <view class="quick-mileage">
            <view class="quick-mileage-item" :class="{ active: filterData.mileageRange === '0-1' }" @tap.stop="setQuickMileage('0-1')">1万以内</view>
            <view class="quick-mileage-item" :class="{ active: filterData.mileageRange === '1-3' }" @tap.stop="setQuickMileage('1-3')">1-3万</view>
            <view class="quick-mileage-item" :class="{ active: filterData.mileageRange === '3-5' }" @tap.stop="setQuickMileage('3-5')">3-5万</view>
            <view class="quick-mileage-item" :class="{ active: filterData.mileageRange === '5-10' }" @tap.stop="setQuickMileage('5-10')">5-10万</view>
            <view class="quick-mileage-item" :class="{ active: filterData.mileageRange === '10-50' }" @tap.stop="setQuickMileage('10-50')">10万以上</view>
          </view>
        </view>

        <!-- 变速箱 -->
        <view class="filter-section">
          <view class="section-title">变速箱</view>
          <view class="transmission-list">
            <view
              class="transmission-item"
              v-for="trans in transmissionList"
              :key="trans.value"
              :class="{ active: filterData.transmission === trans.value }"
              @click="selectTransmission(trans.value)"
            >
              {{ trans.label }}
            </view>
          </view>
        </view>

        <!-- 能源类型 -->
        <view class="filter-section">
          <view class="section-title">能源类型</view>
          <view class="transmission-list">
            <view
              class="transmission-item"
              v-for="energy in energyTypeList"
              :key="energy.value"
              :class="{ active: filterData.energyType === energy.value }"
              @click="selectEnergyType(energy.value)"
            >
              {{ energy.label }}
            </view>
          </view>
        </view>

        </view>

        <!-- 底部操作按钮 -->
        <view class="filter-footer">
          <view class="btn-reset" @click="resetFilter">重置</view>
          <view class="btn-confirm" @click="applyFilter">确认筛选</view>
        </view>
      </view>
      </view>
    </u-popup>

    <!-- 自定义底部导航栏 -->
    <custom-tab-bar />
  </view>
</template>

<script>
import { getCarList } from '@/api/car'
import { getHotCities } from '@/api/city'
import { getBrands } from '@/api/brand'
import CustomTabBar from '@/custom-tab-bar/index.vue'

export default {
  components: {
    CustomTabBar
  },
  data() {
    return {
      statusBarHeight: 20,
      searchKeyword: '',
      currentTab: 0,
      carList: [],
      page: 1,
      size: 10,
      total: 0,
      loading: false,
      hasMore: true,
      defaultImage: '/static/tab/home.png',
      // 筛选弹窗
      showFilter: false,
      // 菜单弹窗
      showMenu: false,
      // 筛选数据
      filterData: {
        brandName: '',
        brandId: '',
        minPrice: null,
        maxPrice: null,
        minAge: null,
        maxAge: null,
        minMileage: null,
        maxMileage: null,
        transmission: '',
        energyType: '',
        priceRange: '',
        ageRange: '',
        mileageRange: ''
      },
      // 滑块值
      priceRange: [0, 100],
      ageRange: [0, 20],
      mileageRange: [0, 50],
      // 变速箱选项
      transmissionList: [
        { value: 'auto', label: '自动' },
        { value: 'manual', label: '手动' }
      ],
      // 能源类型选项
      energyTypeList: [
        { value: '汽油', label: '汽油' },
        { value: '柴油', label: '柴油' },
        { value: '纯电', label: '纯电' },
        { value: '混动', label: '混动' }
      ],
      // 品牌列表（从后端动态获取）
      brandList: [],
      // 顶部滚动广告位数据
      adList: [
        { title: '超级跑车专场', subtitle: '畅享极速体验，限时特惠', tag: 'HOT', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
        { title: 'AI智能车况评估', subtitle: '精准评估 · 快速成交', tag: 'AI', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
        { title: '厂家认证车源', subtitle: '品质保证 · 放心购买', tag: '精选', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
        { title: '限时降价专场', subtitle: '今日特卖 · 最高立减3万', tag: '限时', bg: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
      ],
      filterTabs: [
        { key: 'latest', label: '最新' },
        { key: 'ne', label: '新能源' },
        { key: 'deposit', label: '保证金' },
        { key: 'export', label: '出口' }
      ],
      // 出口二级区域筛选（原型显示：中东/俄罗斯/非洲/远东/东南亚/南美）
      exportRegions: [
        { code: 'ME', name: '中东' },
        { code: 'RU', name: '俄罗斯' },
        { code: 'AF', name: '非洲' },
        { code: 'FE', name: '远东' },
        { code: 'SEA', name: '东南亚' },
        { code: 'SA', name: '南美' }
      ],
      currentCity: '全国',
      cityList: [],  // 从后端获取的城市列表
      currentRegion: 'ME',
      exportCountries: [
        { code: 'RU', name: '俄罗斯', flag: '🇷🇺', count: 128 },
        { code: 'KZ', name: '哈萨克斯坦', flag: '🇰🇿', count: 86 },
        { code: 'AE', name: '阿联酋', flag: '🇦🇪', count: 72 },
        { code: 'AU', name: '澳大利亚', flag: '🇦🇺', count: 54 },
        { code: 'SA', name: '南非', flag: '🇿🇦', count: 41 },
        { code: 'SE', name: '东南亚', flag: '🌏', count: 98 }
      ],
      flagMap: {
        RU: '🇷🇺', KZ: '🇰🇿', AE: '🇦🇪', AU: '🇦🇺', SA: '🇿🇦', SE: '🌏', CN: '🇨🇳', JP: '🇯🇵', US: '🇺🇸', EU: '🇪🇺'
      }
    }
  },
  watch: {
    // 监听价格滑块变化
    priceRange: {
      handler(val) {
        this.filterData.minPrice = val[0] === 0 ? null : val[0]
        this.filterData.maxPrice = val[1] === 100 ? null : val[1]
        this.filterData.priceRange = ''
      },
      deep: true
    },
    // 监听车龄滑块变化
    ageRange: {
      handler(val) {
        this.filterData.minAge = val[0] === 0 ? null : val[0]
        this.filterData.maxAge = val[1] === 20 ? null : val[1]
        this.filterData.ageRange = ''
      },
      deep: true
    },
    // 监听里程滑块变化
    mileageRange: {
      handler(val) {
        this.filterData.minMileage = val[0] === 0 ? null : val[0]
        this.filterData.maxMileage = val[1] === 50 ? null : val[1]
        this.filterData.mileageRange = ''
      },
      deep: true
    }
  },
  computed: {
    // 判断是否有激活的筛选条件
    hasActiveFilter() {
      const { brandName, minPrice, maxPrice, minAge, maxAge, minMileage, maxMileage, transmission, energyType } = this.filterData
      return !!(brandName || minPrice !== null || maxPrice !== null || minAge !== null || maxAge !== null || minMileage !== null || maxMileage !== null || transmission || energyType)
    }
  },
  onLoad() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
    this.fetchCityList()  // 加载城市列表
    this.fetchBrandList() // 加载品牌列表
    this.fetchCarList()
  },
  onPullDownRefresh() {
    this.page = 1
    this.hasMore = true
    this.fetchCarList(true).finally(() => uni.stopPullDownRefresh())
  },
  onReachBottom() {
    if (this.hasMore) {
      this.page++
      this.fetchCarList()
    }
  },
  onShow() {
    // 进入页面时，根据当前页更新自定义 tabBar 的选中状态
    if (typeof this.$mp === 'undefined') {
      // 触发子组件内部 activeIndex 同步
    }
  },
  methods: {
    async fetchCarList(reset) {
      if (this.loading) return
      this.loading = true
      try {
        const params = { page: this.page, size: this.size }
        if (this.searchKeyword) params.keyword = this.searchKeyword
        if (this.currentTab === 1) params.energyType = '纯电'
        if (this.currentTab === 2) params.deposit = true
        if (this.currentTab === 3 && this.currentRegion) params.region = this.currentRegion
        // 添加城市筛选
        if (this.currentCity && this.currentCity !== '全国') params.region = this.currentCity
        // 添加筛选参数
        if (this.filterData.brandId) params.brandId = this.filterData.brandId
        if (this.filterData.minPrice !== null) params.minPrice = this.filterData.minPrice
        if (this.filterData.maxPrice !== null) params.maxPrice = this.filterData.maxPrice
        if (this.filterData.minAge !== null) params.minAge = this.filterData.minAge
        if (this.filterData.maxAge !== null) params.maxAge = this.filterData.maxAge
        if (this.filterData.minMileage !== null) params.minMileage = this.filterData.minMileage
        if (this.filterData.maxMileage !== null) params.maxMileage = this.filterData.maxMileage
        if (this.filterData.transmission) params.transmission = this.filterData.transmission
        if (this.filterData.energyType) params.energyType = this.filterData.energyType
        const res = await getCarList(params)
        const data = res && res.data ? res.data : {}
        if (reset || this.page === 1) {
          this.carList = data.list || []
        } else {
          this.carList = this.carList.concat(data.list || [])
        }
        this.total = data.total || 0
        this.hasMore = this.carList.length < this.total
      } catch (e) {
        console.error('load cars fail', e)
        uni.$u.toast('加载车源失败,请下拉刷新重试')
      } finally {
        this.loading = false
      }
    },
    switchTab(index) {
      if (this.currentTab === index) return
      this.currentTab = index
      this.page = 1
      this.hasMore = true
      if (index === 3) {
        // 出口Tab，默认使用当前区域
        this.currentRegion = 'ME'
      }
      this.fetchCarList(true)
    },
    filterByRegion(code) {
      this.currentRegion = code
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    doSearch() {
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    selectCity() {
      if (this.cityList.length === 0) {
        uni.showToast({ title: '城市数据加载中', icon: 'loading' })
        return
      }
      const cities = this.cityList.map(c => c.name)
      uni.showActionSheet({
        itemList: cities,
        success: (res) => {
          const selectedCity = this.cityList[res.tapIndex]
          this.currentCity = selectedCity.name
          this.page = 1
          this.hasMore = true
          this.fetchCarList(true)
        }
      })
    },
    async fetchCityList() {
      try {
        const res = await getHotCities()
        this.cityList = res.data || []
        if (this.cityList.length === 0) {
          // 降级使用本地静态数据
          this.cityList = [
            { code: 'ALL', name: '全国', hot: true },
            { code: 'BEIJING', name: '北京', hot: true },
            { code: 'SHANGHAI', name: '上海', hot: true },
            { code: 'GUANGZHOU', name: '广州', hot: true },
            { code: 'SHENZHEN', name: '深圳', hot: true },
            { code: 'HANGZHOU', name: '杭州', hot: true },
            { code: 'CHENGDU', name: '成都', hot: true },
            { code: 'WUHAN', name: '武汉', hot: true },
            { code: 'XIAN', name: '西安', hot: true },
            { code: 'CHONGQING', name: '重庆', hot: true }
          ]
        }
      } catch (e) {
        console.error('load cities fail', e)
        uni.$u.toast('加载城市列表失败,使用默认城市')
        // 降级使用本地静态数据
        this.cityList = [
          { code: 'ALL', name: '全国', hot: true },
          { code: 'BEIJING', name: '北京', hot: true },
          { code: 'SHANGHAI', name: '上海', hot: true },
          { code: 'GUANGZHOU', name: '广州', hot: true },
          { code: 'SHENZHEN', name: '深圳', hot: true },
          { code: 'HANGZHOU', name: '杭州', hot: true },
          { code: 'CHENGDU', name: '成都', hot: true },
          { code: 'WUHAN', name: '武汉', hot: true },
          { code: 'XIAN', name: '西安', hot: true },
          { code: 'CHONGQING', name: '重庆', hot: true }
        ]
      }
    },
    // 加载品牌列表
    async fetchBrandList() {
      try {
        const res = await getBrands()
        const brands = res.data || []
        // 转换为前端需要的格式
        this.brandList = brands.map(brand => ({
          id: brand.id,
          name: brand.name,
          series: [] // 车系需要点击品牌后异步加载
        }))
      } catch (e) {
        console.error('load brands fail', e)
        uni.$u.toast('加载品牌列表失败')
        // 异常时使用空数组，用户将无法看到品牌选项
        this.brandList = []
      }
    },
    filterByCountry(code) {
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    // 打开筛选弹窗
    openFilter() {
      // 同步滑块初始值
      if (this.filterData.priceRange) {
        const [min, max] = this.filterData.priceRange.split('-').map(Number)
        this.priceRange = [min || 0, max || 100]
      } else {
        this.priceRange = [
          this.filterData.minPrice !== null ? this.filterData.minPrice : 0,
          this.filterData.maxPrice !== null ? this.filterData.maxPrice : 100
        ]
      }
      
      if (this.filterData.ageRange) {
        const [min, max] = this.filterData.ageRange.split('-').map(Number)
        this.ageRange = [min || 0, max || 20]
      } else {
        this.ageRange = [
          this.filterData.minAge !== null ? this.filterData.minAge : 0,
          this.filterData.maxAge !== null ? this.filterData.maxAge : 20
        ]
      }
      
      if (this.filterData.mileageRange) {
        const [min, max] = this.filterData.mileageRange.split('-').map(Number)
        this.mileageRange = [min || 0, max || 50]
      } else {
        this.mileageRange = [
          this.filterData.minMileage !== null ? this.filterData.minMileage : 0,
          this.filterData.maxMileage !== null ? this.filterData.maxMileage : 50
        ]
      }
      
      this.showFilter = true
    },
    // 选择品牌
    selectBrand() {
      uni.showActionSheet({
        itemList: this.brandList.map(b => b.name),
        success: (res) => {
          const brand = this.brandList[res.tapIndex]
          this.filterData.brandName = brand.name
          this.filterData.brandId = brand.id
        }
      })
    },
    // 快捷价格选择
    setQuickPrice(range) {
      this.filterData.priceRange = range
      const [min, max] = range.split('-').map(Number)
      this.filterData.minPrice = min === 0 ? null : min
      this.filterData.maxPrice = max === 100 ? null : max
      // 使用 nextTick 确保更新不被 watch 覆盖
      this.$nextTick(() => {
        this.priceRange = [min || 0, max || 100]
      })
    },
    // 快捷车龄选择
    setQuickAge(range) {
      this.filterData.ageRange = range
      const [min, max] = range.split('-').map(Number)
      this.filterData.minAge = min === 0 ? null : min
      this.filterData.maxAge = max === 20 ? null : max
      this.$nextTick(() => {
        this.ageRange = [min || 0, max || 20]
      })
    },
    // 快捷里程选择
    setQuickMileage(range) {
      this.filterData.mileageRange = range
      const [min, max] = range.split('-').map(Number)
      this.filterData.minMileage = min === 0 ? null : min
      this.filterData.maxMileage = max === 50 ? null : max
      this.$nextTick(() => {
        this.mileageRange = [min || 0, max || 50]
      })
    },
    // 选择变速箱
    selectTransmission(value) {
      this.filterData.transmission = value
    },
    // 选择能源类型
    selectEnergyType(value) {
      this.filterData.energyType = value
    },
    // 重置筛选
    resetFilter() {
      this.filterData = {
        brandName: '',
        brandId: '',
        minPrice: null,
        maxPrice: null,
        minAge: null,
        maxAge: null,
        minMileage: null,
        maxMileage: null,
        transmission: '',
        energyType: '',
        priceRange: '',
        ageRange: '',
        mileageRange: ''
      }
      this.priceRange = [0, 100]
      this.ageRange = [0, 20]
      this.mileageRange = [0, 50]
    },
    // 应用筛选
    applyFilter() {
      this.showFilter = false
      this.page = 1
      this.hasMore = true
      this.fetchCarList(true)
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/car-detail/index?id=' + id })
    },
    openMenu() {
      this.showMenu = true
    },
    closeMenu() {
      this.showMenu = false
    },
    gotoPublish() {
      this.showMenu = false
      uni.navigateTo({ url: '/pages/publish/index' })
    },
    gotoPurchaseDemand() {
      this.showMenu = false
      uni.navigateTo({ url: '/pages/purchase-demand/index' })
    },
    toAI() {
      this.showMenu = false
      uni.navigateTo({ url: '/pages/ai/index' })
    },
    toCustomerService() {
      this.showMenu = false
      uni.navigateTo({ url: '/pages/customer-service/index' })
    },
    formatPrice(price) {
      if (!price) return '面议'
      const wan = price / 10000
      return wan >= 10 ? wan.toFixed(0) : wan.toFixed(1)
    },
    formatMileage(mileage) {
      if (!mileage) return '0'
      if (mileage >= 10000) return (mileage / 10000).toFixed(1) + '万'
      return mileage.toString()
    },
    formatTime(ts) {
      if (!ts) return ''
      const now = Date.now()
      const d = new Date(ts).getTime()
      const diff = (now - d) / 1000
      if (diff < 60) return '刚刚'
      if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
      if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
      if (diff < 86400 * 7) return Math.floor(diff / 86400) + '天前'
      const dt = new Date(ts)
      return (dt.getMonth() + 1) + '-' + dt.getDate()
    }
  }
}
</script>

<style lang="scss" scoped>
/* =========================================================
   5D好车首页 - Liquid Glass | Marketplace Premium
   设计语言: Dimensional Layering | Smooth Motion | Navy Gold
   颜色系统: 深海军蓝 #0F172A + 琥珀金 #F59E0B + 专业蓝 #0369A1 + 软背景 #F8FAFC
   ========================================================= */

/* ============ 全局容器 ============ */
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%);
  animation: fadeIn 400ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

/* ============ 顶部 Hero 区域 ============ */
.ad-section {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #0F172A 100%);
}

.ad-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.15) 0%, transparent 70%);
  pointer-events: none;
}

.ad-swiper {
  width: 100%;
  height: 340rpx;
}

.ad-slide {
  width: 100%;
  height: 340rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 64rpx 32rpx 0;
  position: relative;
  box-sizing: border-box;
}

.ad-text {
  display: flex;
  flex-direction: column;
  max-width: 75%;
}

.ad-title {
  font-size: 42rpx;
  font-weight: 800;
  color: #FCD34D;
  letter-spacing: 0.5rpx;
  line-height: 1.15;
}

.ad-sub {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.75);
  margin-top: 12rpx;
  letter-spacing: 0.5rpx;
}

.ad-tag {
  position: absolute;
  top: 64rpx;
  right: 32rpx;
  font-size: 22rpx;
  color: #FCD34D;
  background: rgba(245, 158, 11, 0.12);
  padding: 10rpx 24rpx;
  border-radius: 20rpx;
  font-weight: 700;
  letter-spacing: 0.5rpx;
  border: 1rpx solid rgba(245, 158, 11, 0.25);
  backdrop-filter: blur(10rpx);
}

.status-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 5;
  width: 100%;
}

.ad-header {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 4;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16rpx 32rpx 24rpx;
  padding-top: calc(var(--status-bar-height, 44px) + 16rpx);
}

.ad-logo-area {
  display: flex;
  flex-direction: column;
}

.ad-logo {
  font-size: 34rpx;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 1rpx;
  line-height: 1.15;
}

.ad-subtitle {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 8rpx;
  letter-spacing: 0.5rpx;
}

.ad-lang {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: rgba(255, 255, 255, 0.08);
  padding: 12rpx 22rpx;
  border-radius: 24rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  border: 1rpx solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12rpx);

  &:active {
    transform: scale(0.96);
    background: rgba(255, 255, 255, 0.18);
  }
}

.ad-lang text {
  font-size: 24rpx;
  color: #ffffff;
  font-weight: 600;
}

.ad-bottom-mask {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 100rpx;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0) 0%, rgba(248, 250, 252, 1) 100%);
  z-index: 2;
  pointer-events: none;
}

/* ============ 搜索栏 Search Bar - Liquid Glass ============ */
.search-wrap {
  display: flex;
  align-items: center;
  gap: 14rpx;
  padding: 20rpx 32rpx 24rpx;
  margin-top: -44rpx;
  position: relative;
  z-index: 10;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 100ms both;
}

/* 城市选择器 - Liquid Glass 效果 */
.city-picker {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: #FFFFFF;
  padding: 20rpx 24rpx;
  border-radius: 20rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 4rpx 20rpx rgba(15, 23, 42, 0.06), 0 1rpx 2rpx rgba(15, 23, 42, 0.04);
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: scale(0.96);
    border-color: #F59E0B;
    box-shadow: 0 4rpx 20rpx rgba(245, 158, 11, 0.15);
  }
}

.city-text {
  font-size: 26rpx;
  color: #0F172A;
  font-weight: 700;
}

.city-arrow {
  font-size: 18rpx;
  color: #64748B;
  margin-top: 2rpx;
}

/* 搜索框 - Liquid Glass */
.search-field {
  flex: 1;
  display: flex;
  align-items: center;
  background: #FFFFFF;
  padding: 16rpx 24rpx;
  border-radius: 20rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.9);
  box-shadow: 0 4rpx 20rpx rgba(15, 23, 42, 0.06), inset 0 1rpx 2rpx rgba(255, 255, 255, 0.9);
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:focus-within {
    border-color: #0369A1;
    background: #ffffff;
    box-shadow: 0 0 0 6rpx rgba(3, 105, 161, 0.1), 0 4rpx 20rpx rgba(3, 105, 161, 0.08);
  }
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #0F172A;
  margin-left: 12rpx;
  font-weight: 500;
}

/* 搜索按钮 - 琥珀金渐变 */
.search-btn {
  flex-shrink: 0;
  background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
  padding: 20rpx 32rpx;
  border-radius: 20rpx;
  box-shadow: 0 6rpx 20rpx rgba(245, 158, 11, 0.35);
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  text {
    color: #0F172A;
    font-size: 28rpx;
    font-weight: 800;
    letter-spacing: 0.5rpx;
  }

  &:active {
    transform: scale(0.96);
    box-shadow: 0 2rpx 8rpx rgba(245, 158, 11, 0.35);
  }
}

/* 筛选图标按钮 */
.filter-icon {
  flex-shrink: 0;
  width: 72rpx;
  height: 72rpx;
  background: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid rgba(226, 232, 240, 0.9);
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;
  color: #334155;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.05);

  &:active {
    transform: scale(0.92);
    border-color: #0369A1;
    box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.2);
  }
}

.filter-dot {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  width: 18rpx;
  height: 18rpx;
  background: #DC2626;
  border-radius: 50%;
  border: 3rpx solid #FFFFFF;
  box-shadow: 0 2rpx 8rpx rgba(220, 38, 38, 0.35);
}

/* ============ 筛选 Tab - 胶囊样式升级 ============ */
.tabs-wrap {
  background: #ffffff;
  padding: 24rpx 32rpx 20rpx;
  margin-top: 4rpx;
  border-bottom: 1rpx solid rgba(241, 245, 249, 0.8);
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) 200ms both;
}

.tabs-inner {
  display: flex;
  gap: 14rpx;
  align-items: center;
}

.tab-pill {
  flex-shrink: 0;
  padding: 14rpx 32rpx;
  background: #F8FAFC;
  border: 1rpx solid #E2E8F0;
  border-radius: 40rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &.active {
    background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
    border-color: #0F172A;
    box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.25);

    .tab-label {
      color: #ffffff;
      font-weight: 700;
    }
  }

  &:active:not(.active) {
    background: #F1F5F9;
    transform: scale(0.96);
  }
}

.tab-label {
  font-size: 26rpx;
  color: #475569;
  font-weight: 600;
}

/* ============ 出口二级区域筛选 - 优化 ============ */
.region-wrap {
  background: #ffffff;
  padding: 8rpx 32rpx 28rpx;
  border-bottom: 1rpx solid rgba(241, 245, 249, 0.8);
}

.region-inner {
  display: flex;
  gap: 12rpx;
  align-items: center;
  flex-wrap: wrap;
}

.region-pill {
  flex-shrink: 0;
  padding: 12rpx 24rpx;
  background: #F8FAFC;
  border: 1rpx solid #E2E8F0;
  border-radius: 32rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &.active {
    background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
    border-color: #0369A1;
    box-shadow: 0 4rpx 16rpx rgba(3, 105, 161, 0.25);

    .region-label {
      color: #ffffff;
      font-weight: 700;
    }
  }

  &:active:not(.active) {
    background: #F1F5F9;
    transform: scale(0.96);
  }
}

.region-label {
  font-size: 24rpx;
  color: #475569;
  font-weight: 600;
}

/* ============ 车源卡片列表 - Liquid Glass Cards ============ */
.car-list {
  padding: 24rpx 32rpx 0;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

/* 卡片主容器 - Dimensional Layering */
.car-card {
  display: flex;
  flex-direction: row;
  background: #ffffff;
  border-radius: 28rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 24rpx rgba(15, 23, 42, 0.08), 0 1rpx 4rpx rgba(15, 23, 42, 0.04);
  padding: 20rpx;
  gap: 20rpx;
  border: 1rpx solid rgba(226, 232, 240, 0.7);
  cursor: pointer;
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;

  &:active {
    transform: translateY(-2rpx) scale(0.985);
    box-shadow: 0 8rpx 32rpx rgba(15, 23, 42, 0.12);
    border-color: rgba(148, 163, 184, 0.5);
  }
}

/* 图片容器 */
.card-image-wrap {
  position: relative;
  width: 240rpx;
  height: 240rpx;
  flex-shrink: 0;
  border-radius: 20rpx;
  overflow: hidden;
  background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
  box-shadow: inset 0 2rpx 8rpx rgba(15, 23, 42, 0.08);
}

.car-image {
  width: 100%;
  height: 100%;
}

.image-tags {
  position: absolute;
  top: 12rpx;
  left: 12rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.tag-energy {
  font-size: 20rpx;
  padding: 6rpx 14rpx;
  border-radius: 10rpx;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.3rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);

  &.pure {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  }
  &.hybrid {
    background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  }
  &.fuel {
    background: linear-gradient(135deg, #475569 0%, #334155 100%);
  }
}

/* 右侧信息 */
.card-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.car-title {
  font-size: 30rpx;
  font-weight: 800;
  color: #0F172A;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  letter-spacing: -0.3rpx;
}

.car-sub-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 10rpx;
}

.car-sub {
  font-size: 24rpx;
  color: #64748B;
  font-weight: 500;
}

.dot {
  color: #CBD5E1;
  font-size: 16rpx;
}

/* 标签行 - 升级徽章 */
.meta-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 12rpx;
  flex-wrap: wrap;
}

.deposit-badge {
  display: inline-flex;
  align-items: center;
  gap: 4rpx;
  background: linear-gradient(135deg, #F0F9FF 0%, #E0F2FE 100%);
  color: #0369A1;
  font-size: 20rpx;
  font-weight: 700;
  padding: 6rpx 14rpx;
  border-radius: 10rpx;
  border: 1rpx solid rgba(3, 105, 161, 0.2);
}

.export-country-badge {
  display: inline-flex;
  align-items: center;
  gap: 4rpx;
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  color: #92400E;
  font-size: 20rpx;
  font-weight: 700;
  padding: 6rpx 14rpx;
  border-radius: 10rpx;
  border: 1rpx solid rgba(245, 158, 11, 0.25);
}

/* 位置/时间行 */
.location-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-top: 14rpx;
  flex-wrap: wrap;
  font-size: 24rpx;
  color: #94A3B8;
}

/* 价格行 - 突出显示 */
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 16rpx;
}

.price-value {
  font-size: 40rpx;
  font-weight: 900;
  color: #DC2626;
  letter-spacing: -0.5rpx;
  line-height: 1.1;
}

.price-unit {
  font-size: 24rpx;
  font-weight: 700;
  color: #DC2626;
  margin-left: 4rpx;
}

.export-flags {
  display: flex;
  gap: 10rpx;
}

.mini-flag {
  font-size: 28rpx;
  line-height: 1;
}

/* ============ 加载更多 & 空状态 ============ */
.load-more {
  text-align: center;
  padding: 48rpx;
  font-size: 24rpx;
  color: #64748B;
  letter-spacing: 0.5rpx;
  font-weight: 500;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 180rpx 0;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

.empty-text {
  margin-top: 28rpx;
  font-size: 28rpx;
  color: #64748B;
  font-weight: 600;
}

/* ============ 右侧悬浮 FAB 按钮 - Liquid Gold ============ */
.fab-single {
  position: fixed;
  right: 32rpx;
  bottom: 220rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  z-index: 100;
  cursor: pointer;
  animation: float 3.5s ease-in-out infinite;

  &:active .fab-single-circle {
    transform: translateY(0) scale(0.92);
    box-shadow: 0 4rpx 16rpx rgba(245, 158, 11, 0.5);
  }
}

.fab-single-circle {
  width: 130rpx;
  height: 130rpx;
  border-radius: 65rpx;
  background: linear-gradient(135deg, #FDE68A 0%, #FBBF24 50%, #F59E0B 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 40rpx rgba(245, 158, 11, 0.5), 0 4rpx 12rpx rgba(245, 158, 11, 0.3);
  border: 4rpx solid rgba(255, 255, 255, 0.8);
  transition: all 400ms cubic-bezier(0.25, 0.1, 0.25, 1);
}

.fab-single-plus {
  font-size: 56rpx;
  color: #0F172A;
  font-weight: 900;
  line-height: 1;
  letter-spacing: -1rpx;
}

.fab-single-label {
  font-size: 24rpx;
  color: #0F172A;
  font-weight: 800;
  letter-spacing: 1rpx;
  background: rgba(255, 255, 255, 0.9);
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.1);
}

/* ============ 菜单弹窗 - Glass Effect ============ */
.menu-popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(12px);
  z-index: 200;
  animation: fadeIn 300ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

.menu-popup {
  position: fixed;
  right: 32rpx;
  bottom: 380rpx;
  width: 380rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 32rpx;
  box-shadow: 0 24rpx 64rpx rgba(15, 23, 42, 0.25), 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
  z-index: 201;
  padding: 16rpx 0;
  animation: scaleIn 400ms cubic-bezier(0.34, 1.2, 0.64, 1) both;
  border: 1rpx solid rgba(226, 232, 240, 0.8);
  backdrop-filter: blur(20px);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 36rpx 32rpx;
  border-bottom: 1rpx solid rgba(241, 245, 249, 0.9);
  cursor: pointer;
  transition: all 200ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:last-of-type {
    border-bottom: none;
  }

  &:active {
    background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
    transform: scale(0.985);
  }
}

.menu-icon {
  font-size: 32rpx;
  color: #F59E0B;
  margin-right: 12rpx;
}

.menu-text {
  font-size: 30rpx;
  color: #1E293B;
  font-weight: 700;

  &.highlight {
    color: #F59E0B;
    font-weight: 800;
  }
}

.menu-close-btn {
  position: absolute;
  bottom: -140rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 88rpx;
  height: 88rpx;
  border-radius: 44rpx;
  background: linear-gradient(135deg, #1E293B 0%, #0F172A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 32rpx rgba(15, 23, 42, 0.45);
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  border: 2rpx solid rgba(255, 255, 255, 0.1);

  &:active {
    transform: translateX(-50%) scale(0.92);
  }
}

.menu-close-icon {
  font-size: 52rpx;
  color: #FFFFFF;
  font-weight: 300;
  line-height: 1;
}

/* ============ TabBar 占位 ============ */
.tabbar-placeholder {
  height: 200rpx;
}

/* ============ 响应式适配 ============ */
@media (min-width: 376px) {
  .car-title {
    font-size: 32rpx;
  }
  .price-value {
    font-size: 38rpx;
  }
  .card-image-wrap {
    width: 260rpx;
    height: 260rpx;
  }
  .ad-swiper,
  .ad-slide {
    height: 360rpx;
  }
}

@media (max-width: 320px) {
  .card-image-wrap {
    width: 200rpx;
    height: 200rpx;
  }
  .car-title {
    font-size: 28rpx;
  }
  .price-value {
    font-size: 32rpx;
  }
  .ad-logo {
    font-size: 32rpx;
  }
  .search-field {
    padding: 14rpx 20rpx;
  }
  .search-btn {
    padding: 14rpx 24rpx;
  }
  .filter-icon {
    width: 60rpx;
    height: 60rpx;
  }
}

/* ============ 筛选弹窗样式 - Premium Filter ============ */
.filter-popup-wrapper {
}

.u-popup {
  z-index: 1100 !important;
}

.filter-popup {
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 200rpx);
  background: #ffffff;
  border-radius: 40rpx 40rpx 0 0;
  animation: fadeInUp 500ms cubic-bezier(0.25, 0.1, 0.25, 1) both;
  overflow: hidden;
  box-shadow: 0 -8rpx 48rpx rgba(15, 23, 42, 0.15);
}

.filter-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0 32rpx 24rpx;
  -webkit-overflow-scrolling: touch;
  touch-action: pan-y;
}

.filter-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx 32rpx 32rpx;
  border-bottom: 1rpx solid rgba(226, 232, 240, 0.8);
  flex-shrink: 0;
  position: relative;
}

.filter-header::after {
  content: '';
  position: absolute;
  top: 12rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 6rpx;
  background: #E2E8F0;
  border-radius: 6rpx;
}

.filter-title {
  font-size: 36rpx;
  font-weight: 800;
  color: #0F172A;
  letter-spacing: 0.5rpx;
}

.filter-section {
  margin-bottom: 48rpx;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 28rpx;
  font-weight: 700;
  color: #0F172A;
  margin-bottom: 24rpx;
  letter-spacing: 0.3rpx;
}

.range-value {
  font-size: 24rpx;
  color: #0369A1;
  font-weight: 700;
}

/* 选择框 - Liquid Glass */
.select-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  border: 1rpx solid rgba(226, 232, 240, 0.9);
  border-radius: 24rpx;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.03);

  &:active {
    background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
    transform: scale(0.99);
    box-shadow: 0 4rpx 16rpx rgba(15, 23, 42, 0.08);
    border-color: #CBD5E1;
  }
}

.select-value {
  font-size: 28rpx;
  color: #0F172A;
  font-weight: 600;
}

.select-value.placeholder {
  color: #94A3B8;
  font-weight: 500;
}

/* 快捷选项 - 胶囊网格 */
.quick-price,
.quick-age,
.quick-mileage {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 24rpx;
}

.quick-price-item,
.quick-age-item,
.quick-mileage-item {
  flex: 0 0 calc((100% - 48rpx) / 3);
  padding: 24rpx 16rpx;
  background: #F8FAFC;
  border: 1rpx solid #E2E8F0;
  border-radius: 20rpx;
  text-align: center;
  font-size: 26rpx;
  color: #475569;
  font-weight: 600;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.03);

  &:active {
    background: #F1F5F9;
    transform: scale(0.97);
  }
}

/* 激活状态 - 深海军蓝 */
.quick-price-item.active,
.quick-age-item.active,
.quick-mileage-item.active {
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
  border-color: #0F172A;
  color: #ffffff;
  font-weight: 700;
  box-shadow: 0 6rpx 20rpx rgba(15, 23, 42, 0.25);
}

/* 变速箱/能源类型选项 */
.transmission-list {
  display: flex;
  gap: 16rpx;
}

.transmission-item {
  flex: 1;
  padding: 28rpx 16rpx;
  background: #F8FAFC;
  border: 1rpx solid #E2E8F0;
  border-radius: 20rpx;
  text-align: center;
  font-size: 28rpx;
  color: #475569;
  font-weight: 600;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 2rpx 8rpx rgba(15, 23, 42, 0.03);

  &:active {
    transform: scale(0.97);
  }
}

/* 激活状态 - 专业蓝色 */
.transmission-item.active {
  background: linear-gradient(135deg, #0369A1 0%, #0284C7 100%);
  border-color: #0369A1;
  color: #ffffff;
  font-weight: 700;
  box-shadow: 0 6rpx 20rpx rgba(3, 105, 161, 0.25);
}

/* 底部操作栏 */
.filter-footer {
  display: flex;
  gap: 24rpx;
  padding: 24rpx 32rpx calc(24rpx + env(safe-area-inset-bottom));
  border-top: 1rpx solid rgba(226, 232, 240, 0.8);
  flex-shrink: 0;
  background: #ffffff;
}

.btn-reset {
  flex: 1;
  padding: 32rpx 20rpx;
  background: #F8FAFC;
  border: 1rpx solid #E2E8F0;
  border-radius: 48rpx;
  text-align: center;
  font-size: 28rpx;
  color: #475569;
  font-weight: 700;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);

  &:active {
    transform: scale(0.97);
    background: #F1F5F9;
  }
}

/* 确认按钮 - 琥珀金渐变 */
.btn-confirm {
  flex: 2;
  padding: 32rpx 20rpx;
  background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
  border-radius: 48rpx;
  text-align: center;
  font-size: 28rpx;
  color: #0F172A;
  font-weight: 800;
  cursor: pointer;
  transition: all 300ms cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 8rpx 28rpx rgba(245, 158, 11, 0.4);

  &:active {
    transform: scale(0.97);
    box-shadow: 0 4rpx 16rpx rgba(245, 158, 11, 0.35);
  }
}

/* ============ 动画定义 ============ */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.92);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10rpx);
  }
}

/* ============ 卡片依次入场动画延迟 ============ */
.car-card:nth-child(1) { animation-delay: 100ms; }
.car-card:nth-child(2) { animation-delay: 200ms; }
.car-card:nth-child(3) { animation-delay: 300ms; }
.car-card:nth-child(4) { animation-delay: 400ms; }
.car-card:nth-child(5) { animation-delay: 500ms; }
</style>
