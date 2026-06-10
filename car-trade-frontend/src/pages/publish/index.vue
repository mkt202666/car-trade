<template>
  <view>
    <u-navbar title="车源发布" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <view class="form-section">
        <!-- 车架号输入 -->
        <u-field label="车架号">
          <u-input v-model="form.vin" placeholder="扫描行驶证或车身铭牌，自动识别" input-align="right"></u-input>
          <template v-slot:right>
            <view class="scan-btn" @click="scanVin">
              <u-icon name="scan" size="36" color="#2979ff"></u-icon>
              <text class="scan-text">扫描</text>
            </view>
          </template>
        </u-field>

        <u-field label="车辆标题" :required="true">
          <u-input v-model="form.title" placeholder="请输入车辆标题" :maxlength="50" input-align="right"></u-input>
        </u-field>

        <!-- 品牌选择 -->
        <u-field label="品牌" :required="true">
          <u-input v-model="selectedBrandName" placeholder="请选择品牌" input-align="right" disabled @click="showBrandPicker = true"></u-input>
          <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
        </u-field>

        <!-- 车系选择 -->
        <u-field label="车系" :required="true">
          <u-input v-model="selectedSeriesName" placeholder="请先选择品牌" input-align="right" disabled @click="showSeriesPicker = true" :disabled="!form.brandId"></u-input>
          <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
        </u-field>

        <!-- 车型选择 -->
        <u-field label="车型">
          <u-input v-model="selectedModelName" placeholder="请先选择车系" input-align="right" disabled @click="showModelPicker = true" :disabled="!form.seriesId"></u-input>
          <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
        </u-field>

        <u-field label="上牌年份" :required="true">
          <u-input v-model="form.year" placeholder="如: 2023" input-align="right" type="number" :disabled="!form.seriesId"></u-input>
        </u-field>

        <u-field label="行驶里程(万公里)" :required="true">
          <u-input v-model="form.mileage" placeholder="请输入里程" input-align="right" type="digit" :disabled="!form.seriesId"></u-input>
        </u-field>

        <!-- 报价方式选择 -->
        <view class="pricing-section">
          <text class="section-title">报价方式</text>
          <view class="pricing-options">
            <view
              class="pricing-option"
              :class="{ active: form.pricingType === 'FIXED' }"
              @click="form.pricingType = 'FIXED'"
            >
              <text>一口价</text>
            </view>
            <view
              class="pricing-option"
              :class="{ active: form.pricingType === 'AUCTION' }"
              @click="form.pricingType = 'AUCTION'"
            >
              <text>拍卖</text>
            </view>
          </view>
        </view>

        <!-- 一口价输入 -->
        <view v-if="form.pricingType === 'FIXED'">
          <u-field label="售价(万元)" :required="true">
            <u-input v-model="form.price" placeholder="请输入价格" input-align="right" type="digit"></u-input>
            <template v-slot:right>
              <text class="unit">元</text>
            </template>
          </u-field>
        </view>

        <!-- 拍卖价格输入 -->
        <view v-if="form.pricingType === 'AUCTION'">
          <u-field label="起拍价" :required="true">
            <u-input v-model="form.startingPrice" placeholder="请输入" input-align="right" type="digit"></u-input>
            <template v-slot:right>
              <text class="unit">元</text>
            </template>
          </u-field>
          <u-field label="封顶价">
            <u-input v-model="form.ceilingPrice" placeholder="请输入" input-align="right" type="digit"></u-input>
            <template v-slot:right>
              <text class="unit">元</text>
            </template>
          </u-field>
          <u-field label="加价幅度">
            <u-input v-model="form.bidIncrement" placeholder="请输入" input-align="right" type="digit"></u-input>
            <template v-slot:right>
              <text class="unit">元</text>
            </template>
          </u-field>
        </view>

        <u-field label="能源类型">
          <u-input v-model="form.energyType" placeholder="纯电/燃油/混动" input-align="right" @click="showEnergyPicker = true">
            <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
          </u-input>
        </u-field>

        <u-field label="变速箱">
          <u-input v-model="form.gearbox" placeholder="自动/手动" input-align="right" :disabled="!form.seriesId"></u-input>
        </u-field>

        <u-field label="颜色">
          <u-input v-model="form.color" placeholder="车辆颜色" input-align="right" @click="showColorPicker = true">
            <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
          </u-input>
        </u-field>

        <u-field label="所在城市" :required="true">
          <u-input v-model="form.cityName" placeholder="请选择城市" input-align="right" disabled @click="showCityPicker = true">
            <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
          </u-input>
        </u-field>

        <u-field label="钥匙数量">
          <u-input v-model="form.keyCount" placeholder="如: 2" input-align="right" type="number" :disabled="!form.seriesId"></u-input>
        </u-field>

        <view class="switch-field">
          <text class="switch-label">是否有抵押</text>
          <switch :checked="form.isMortgaged" @change="form.isMortgaged = $event.detail.value" color="#2979ff" />
        </view>

        <view class="switch-field">
          <text class="switch-label">是否有继承</text>
          <switch :checked="form.isInherited" @change="form.isInherited = $event.detail.value" color="#2979ff" />
        </view>

        <!-- 检测报告上传 -->
        <view class="upload-section">
          <text class="upload-label">检测报告</text>
          <view class="report-type-selector">
            <view
              class="report-type"
              :class="{ active: form.inspectionReportType === 'LINK' }"
              @click="form.inspectionReportType = 'LINK'"
            >
              <text>链接</text>
            </view>
            <view
              class="report-type"
              :class="{ active: form.inspectionReportType === 'FILE' }"
              @click="form.inspectionReportType = 'FILE'"
            >
              <text>文件 (PDF)</text>
            </view>
          </view>
          <view v-if="form.inspectionReportType === 'LINK'" class="report-input">
            <u-input v-model="form.inspectionReportUrl" placeholder="请输入报告链接"></u-input>
          </view>
          <view v-if="form.inspectionReportType === 'FILE'" class="report-upload">
            <u-button type="primary" size="mini" @click="uploadReport">上传报告</u-button>
          </view>
          <text class="upload-tip">上传报告会有检测标签，可获得优先推荐</text>
        </view>

        <!-- 证件材料上传 -->
        <view class="upload-section">
          <text class="upload-label">证件材料</text>
          <text class="upload-subtitle">(仅门店成员可见)</text>
          <view class="material-list">
            <view class="material-item" @click="uploadMaterial('driving_license')">
              <u-icon name="file-text" size="40" color="#2979ff"></u-icon>
              <text>行驶证</text>
            </view>
            <view class="material-item" @click="uploadMaterial('property_certificate')">
              <u-icon name="file-text" size="40" color="#2979ff"></u-icon>
              <text>产权证</text>
            </view>
            <view class="material-item" @click="uploadMaterial('vehicle_nameplate')">
              <u-icon name="file-text" size="40" color="#2979ff"></u-icon>
              <text>车身铭牌</text>
            </view>
          </view>
          <text class="upload-tip">行驶证、产权证、车辆铭牌等至少一张，非店铺所有车源，必须取得车主授权同意才可能发布，以免引起投诉影响发车权限。</text>
        </view>

        <u-field label="车辆描述">
          <u-input v-model="form.description" placeholder="请详细描述车况状态、配置等重要信息，将有效提升您的成交率，更快获得AI智能匹配。" type="textarea" :height="160" :maxlength="500"></u-input>
        </u-field>

        <!-- 发布选项开关 -->
        <view class="switch-field">
          <view class="switch-info">
            <text class="switch-label">支持锁车洽谈</text>
            <text class="switch-desc">买家支付保证金后锁定车辆</text>
          </view>
          <switch :checked="form.supportLockNegotiation" @change="form.supportLockNegotiation = $event.detail.value" color="#2979ff" />
        </view>

        <view class="switch-field">
          <view class="switch-info">
            <text class="switch-label">AI自动推广</text>
            <text class="switch-desc">自动分发到多个平台</text>
          </view>
          <switch :checked="form.aiAutoPromote" @change="form.aiAutoPromote = $event.detail.value" color="#2979ff" />
        </view>

        <view class="upload-section">
          <text class="upload-label">上传图片</text>
          <view class="upload-list">
            <view class="upload-item" v-for="(img, index) in imageList" :key="index">
              <image :src="img" mode="aspectFill" class="upload-image"></image>
              <view class="upload-remove" @click="removeImage(index)">
                <u-icon name="close-circle-fill" size="40" color="#f56c6c"></u-icon>
              </view>
            </view>
            <view class="upload-item upload-btn" @click="chooseImage" v-if="imageList.length < 9">
              <u-icon name="camera" size="48" color="#ccc"></u-icon>
              <text class="upload-text">上传</text>
            </view>
          </view>
          <text class="upload-tip">最多上传9张图片，建议尺寸 750x560</text>
        </view>
      </view>

      <view class="submit-bar">
        <view class="submit-buttons">
          <u-button type="default" size="default" @click="saveDraft" shape="circle">保存草稿</u-button>
          <u-button type="primary" size="default" :loading="submitting" @click="handleSubmit" shape="circle">
            {{ submitting ? '发布中...' : '发布' }}
          </u-button>
        </view>
        <view class="ai-fill-btn" @click="aiFill">
          <u-icon name="magic" size="24" color="#2979ff"></u-icon>
          <text>AI帮我填</text>
        </view>
      </view>
    </view>

    <!-- 品牌选择器 -->
    <u-picker
      mode="selector"
      v-model="showBrandPicker"
      :default-value="brandPickerIndex"
      :range="brandList"
      range-key="name"
      @confirm="onBrandConfirm"
    ></u-picker>

    <!-- 车系选择器 -->
    <u-picker
      mode="selector"
      v-model="showSeriesPicker"
      :default-value="seriesPickerIndex"
      :range="seriesList"
      range-key="name"
      @confirm="onSeriesConfirm"
    ></u-picker>

    <!-- 车型选择器 -->
    <u-picker
      mode="selector"
      v-model="showModelPicker"
      :default-value="modelPickerIndex"
      :range="modelList"
      range-key="name"
      @confirm="onModelConfirm"
    ></u-picker>

    <!-- 能源类型选择器 -->
    <u-picker
      mode="selector"
      v-model="showEnergyPicker"
      :default-value="energyPickerIndex"
      :range="energyTypes"
      @confirm="onEnergyConfirm"
    ></u-picker>

    <!-- 城市选择器 -->
    <u-picker
      mode="selector"
      v-model="showCityPicker"
      :default-value="cityPickerIndex"
      :range="cityList"
      range-key="name"
      @confirm="onCityConfirm"
    ></u-picker>

    <!-- 颜色选择器 -->
    <u-picker
      mode="selector"
      v-model="showColorPicker"
      :default-value="colorPickerIndex"
      :range="colorList"
      @confirm="onColorConfirm"
    ></u-picker>

    <!-- 发布确认弹窗 -->
    <u-modal v-model="showPublishConfirm" title="发布车源" :show-cancel-button="true" @confirm="confirmPublish">
      <view class="confirm-content">
        <text>发布车源，请确认符合</text>
        <text class="link">《车源发布规范》</text>
      </view>
    </u-modal>

    <!-- 保证金选择弹窗 -->
    <u-modal v-model="showDepositChoice" title="使用保证金" :show-cancel-button="true" @confirm="confirmWithDeposit" @cancel="confirmWithoutDeposit">
      <view class="deposit-content">
        <text>交易怕风险，使用定金保障，车源获得更多曝光，更快成交。未使用的保证金会自动退回账户余额。</text>
        <view class="deposit-option">
          <text>交易冻结3000</text>
        </view>
      </view>
    </u-modal>

    <!-- 发布成功弹窗 -->
    <u-modal v-model="showPublishSuccess" title="发布成功!" :show-cancel-button="false" @confirm="goBack">
      <view class="success-content">
        <text>您的车源符合</text>
        <text class="highlight">{{ exportCountriesText }}</text>
        <text>的出口推荐条件，已被同步至出口板块。</text>
      </view>
    </u-modal>

    <!-- AI语音录入弹窗 -->
    <u-popup v-model="showVoiceInput" mode="bottom" :round="20">
      <view class="voice-input-content">
        <view class="voice-header">
          <text class="voice-title">AI 语音录入</text>
          <text class="voice-desc">请说出车源信息，我会帮您自动录入。您也可拍照上传，我会识别车源信息。</text>
        </view>
        <view class="voice-wave">
          <view class="wave-bar" v-for="i in 20" :key="i" :style="{ height: Math.random() * 50 + 10 + 'px' }"></view>
        </view>
        <text class="voice-status">我在听...</text>
        <view class="voice-buttons">
          <view class="voice-btn" @click="takePhoto">
            <u-icon name="camera" size="40" color="#2979ff"></u-icon>
            <text>拍照</text>
          </view>
          <view class="voice-btn active" @click="toggleVoice">
            <u-icon name="mic" size="40" color="#fff"></u-icon>
            <text>语音</text>
          </view>
          <view class="voice-btn" @click="chooseFromAlbum">
            <u-icon name="image" size="40" color="#2979ff"></u-icon>
            <text>相册</text>
          </view>
        </view>
        <view class="voice-documents">
          <text class="doc-label">行驶证</text>
          <text class="doc-label">产权证</text>
          <text class="doc-label">车身铭牌</text>
        </view>
      </view>
    </u-popup>
  </view>
</template>

<script>
import { createCar } from '@/api/car'
import { requireAuth } from '@/utils/auth'
import { isValidPrice, isValidMileage } from '@/utils/validate'

// 品牌数据
const brandData = [
  { id: 1, name: '宝马', firstLetter: 'B' },
  { id: 2, name: '奔驰', firstLetter: 'B' },
  { id: 3, name: '奥迪', firstLetter: 'A' },
  { id: 4, name: '保时捷', firstLetter: 'B' },
  { id: 5, name: '丰田', firstLetter: 'T' },
  { id: 6, name: '本田', firstLetter: 'B' },
  { id: 7, name: '大众', firstLetter: 'D' },
  { id: 8, name: '特斯拉', firstLetter: 'T' },
  { id: 9, name: '比亚迪', firstLetter: 'B' },
  { id: 10, name: '蔚来', firstLetter: 'W' },
  { id: 11, name: '小鹏', firstLetter: 'X' },
  { id: 12, name: '理想', firstLetter: 'L' },
  { id: 13, name: '小米', firstLetter: 'X' }
]

// 车系数据
const seriesData = [
  // 宝马
  { id: 1, brandId: 1, name: '3系' },
  { id: 2, brandId: 1, name: '5系' },
  { id: 3, brandId: 1, name: '7系' },
  { id: 4, brandId: 1, name: 'X3' },
  { id: 5, brandId: 1, name: 'X5' },
  { id: 6, brandId: 1, name: 'X7' },
  { id: 7, brandId: 1, name: 'M4' },
  // 奔驰
  { id: 8, brandId: 2, name: 'C级' },
  { id: 9, brandId: 2, name: 'E级' },
  { id: 10, brandId: 2, name: 'S级' },
  { id: 11, brandId: 2, name: 'GLC' },
  { id: 12, brandId: 2, name: 'GLE' },
  { id: 13, brandId: 2, name: 'EQE' },
  // 奥迪
  { id: 14, brandId: 3, name: 'A4L' },
  { id: 15, brandId: 3, name: 'A6L' },
  { id: 16, brandId: 3, name: 'Q5L' },
  { id: 17, brandId: 3, name: 'RS7' },
  // 保时捷
  { id: 18, brandId: 4, name: '911' },
  { id: 19, brandId: 4, name: 'Cayenne' },
  { id: 20, brandId: 4, name: 'Panamera' },
  // 丰田
  { id: 21, brandId: 5, name: '卡罗拉' },
  { id: 22, brandId: 5, name: '凯美瑞' },
  { id: 23, brandId: 5, name: 'RAV4' },
  { id: 24, brandId: 5, name: '埃尔法' },
  // 本田
  { id: 25, brandId: 6, name: '思域' },
  { id: 26, brandId: 6, name: 'CR-V' },
  // 大众
  { id: 27, brandId: 7, name: 'Polo' },
  { id: 28, brandId: 7, name: '朗逸' },
  { id: 29, brandId: 7, name: '帕萨特' },
  { id: 30, brandId: 7, name: '途观' },
  // 特斯拉
  { id: 31, brandId: 8, name: 'Model 3' },
  { id: 32, brandId: 8, name: 'Model Y' },
  { id: 33, brandId: 8, name: 'Model S' },
  { id: 34, brandId: 8, name: 'Model X' },
  // 比亚迪
  { id: 35, brandId: 9, name: '汉' },
  { id: 36, brandId: 9, name: '唐' },
  { id: 37, brandId: 9, name: '宋' },
  { id: 38, brandId: 9, name: '秦' },
  { id: 39, brandId: 9, name: '海豹' },
  // 蔚来
  { id: 40, brandId: 10, name: 'ET5' },
  { id: 41, brandId: 10, name: 'ET7' },
  { id: 42, brandId: 10, name: 'ES6' },
  { id: 43, brandId: 10, name: 'ES8' },
  // 小鹏
  { id: 44, brandId: 11, name: 'P7' },
  { id: 45, brandId: 11, name: 'P5' },
  { id: 46, brandId: 11, name: 'G9' },
  // 理想
  { id: 47, brandId: 12, name: 'L7' },
  { id: 48, brandId: 12, name: 'L8' },
  { id: 49, brandId: 12, name: 'L9' },
  // 小米
  { id: 50, brandId: 13, name: 'SU7' }
]

// 车型数据
const modelData = [
  // 宝马3系
  { id: 1, seriesId: 1, name: '2023款 320i M运动套装' },
  { id: 2, seriesId: 1, name: '2023款 325i M运动套装' },
  { id: 3, seriesId: 1, name: '2024款 330i M运动曜夜套装' },
  // 宝马5系
  { id: 4, seriesId: 2, name: '2023款 525Li M运动套装' },
  { id: 5, seriesId: 2, name: '2024款 530Li 领先型 M运动套装' },
  // 宝马X5
  { id: 6, seriesId: 5, name: '2023款 xDrive30Li 尊享型 M运动套装' },
  { id: 7, seriesId: 5, name: '2024款 xDrive40Li M运动套装' },
  // 奔驰C级
  { id: 8, seriesId: 8, name: '2023款 C 200 L 运动版' },
  { id: 9, seriesId: 8, name: '2024款 C 260 L 运动版' },
  // 奔驰E级
  { id: 10, seriesId: 9, name: '2023款 E 260 L 运动型' },
  { id: 11, seriesId: 9, name: '2024款 E 300 L 豪华型' },
  // 奥迪A6L
  { id: 12, seriesId: 15, name: '2023款 45 TFSI 臻选动感型' },
  { id: 13, seriesId: 15, name: '2024款 45 TFSI 臻选致雅型' },
  // 大众Polo
  { id: 14, seriesId: 27, name: '2023款 1.5L 自动全景乐享版' },
  { id: 15, seriesId: 27, name: '2024款 1.5L 自动潮酷乐享版' },
  // 特斯拉
  { id: 16, seriesId: 31, name: '2023款 后驱版' },
  { id: 17, seriesId: 31, name: '2024款 高性能版' },
  { id: 18, seriesId: 32, name: '2023款 长续航版' },
  { id: 19, seriesId: 32, name: '2024款 Performance高性能版' },
  // 小米SU7
  { id: 20, seriesId: 50, name: '2024款 后驱长续航智驾版' },
  { id: 21, seriesId: 50, name: '2024款 四驱超长续航高阶版' }
]

// 城市数据
const cityData = [
  { code: '110000', name: '北京' },
  { code: '120000', name: '天津' },
  { code: '310000', name: '上海' },
  { code: '320000', name: '南京' },
  { code: '320100', name: '南京' },
  { code: '320500', name: '苏州' },
  { code: '330100', name: '杭州' },
  { code: '330200', name: '宁波' },
  { code: '330400', name: '温州' },
  { code: '440100', name: '广州' },
  { code: '440300', name: '深圳' },
  { code: '440600', name: '佛山' },
  { code: '441900', name: '东莞' },
  { code: '500000', name: '重庆' },
  { code: '510100', name: '成都' },
  { code: '610100', name: '西安' },
  { code: '370100', name: '济南' },
  { code: '370200', name: '青岛' },
  { code: '370300', name: '淄博' },
  { code: '210200', name: '大连' },
  { code: '210100', name: '沈阳' },
  { code: '350100', name: '福州' },
  { code: '350200', name: '厦门' }
]

// 能源类型
const energyTypes = ['纯电', '燃油', '混动', '增程式']

export default {
  data() {
    return {
      form: {
        title: '',
        vin: '',  // 车架号
        brandId: null,
        seriesId: null,
        modelId: null,
        year: '',
        mileage: '',
        price: '',
        pricingType: 'FIXED',  // FIXED:一口价, AUCTION:拍卖
        startingPrice: '',  // 起拍价
        ceilingPrice: '',  // 封顶价
        bidIncrement: '',  // 加价幅度
        energyType: '',
        gearbox: '',
        color: '',
        cityCode: '',
        cityName: '',
        keyCount: '',
        isMortgaged: false,
        isInherited: false,
        description: '',
        inspectionReportType: 'LINK',  // LINK:链接, FILE:文件
        inspectionReportUrl: '',  // 检测报告链接
        certificateMaterials: '',  // 证件材料JSON
        supportLockNegotiation: false,  // 支持锁车洽谈
        aiAutoPromote: false,  // AI自动推广
        isDraft: false  // 是否草稿
      },
      imageList: [],
      submitting: false,

      // 选择器相关
      showBrandPicker: false,
      showSeriesPicker: false,
      showModelPicker: false,
      showEnergyPicker: false,
      showCityPicker: false,
      showColorPicker: false,

      brandPickerIndex: 0,
      seriesPickerIndex: 0,
      modelPickerIndex: 0,
      energyPickerIndex: 0,
      cityPickerIndex: 0,
      colorPickerIndex: 0,

      selectedBrandName: '',
      selectedSeriesName: '',
      selectedModelName: '',

      // 弹窗相关
      showPublishConfirm: false,
      showDepositChoice: false,
      showPublishSuccess: false,
      showVoiceInput: false,

      // 颜色列表
      colorList: ['黑色', '白色', '银色', '灰色', '红色', '蓝色', '棕色', '香槟色', '黄色', '绿色', '橙色', '紫色', '粉色', '青色', '其他'],

      // 静态数据
      brandList: brandData,
      seriesList: [],
      modelList: [],
      cityList: cityData,
      energyTypes: energyTypes
    }
  },
  
  onLoad() {
    // 验证登录状态
    if (!requireAuth()) {
      return
    }
  },
  
  methods: {
    // 选择品牌
    onBrandConfirm(e) {
      const index = e[0]
      const brand = this.brandList[index]
      this.form.brandId = brand.id
      this.selectedBrandName = brand.name
      this.brandPickerIndex = index
      
      // 重置车系和车型
      this.form.seriesId = null
      this.form.modelId = null
      this.selectedSeriesName = ''
      this.selectedModelName = ''
      this.seriesPickerIndex = 0
      this.modelPickerIndex = 0
      
      // 获取该品牌的车系
      this.seriesList = seriesData.filter(s => s.brandId === brand.id)
      
      uni.$u.toast('已选择：' + brand.name)
    },
    
    // 选择车系
    onSeriesConfirm(e) {
      const index = e[0]
      const series = this.seriesList[index]
      this.form.seriesId = series.id
      this.selectedSeriesName = series.name
      this.seriesPickerIndex = index
      
      // 重置车型
      this.form.modelId = null
      this.selectedModelName = ''
      this.modelPickerIndex = 0
      
      // 获取该车系的车型
      this.modelList = modelData.filter(m => m.seriesId === series.id)
      
      uni.$u.toast('已选择：' + series.name)
    },
    
    // 选择车型
    onModelConfirm(e) {
      const index = e[0]
      const model = this.modelList[index]
      this.form.modelId = model.id
      this.selectedModelName = model.name
      this.modelPickerIndex = index
      
      // 自动填充标题
      if (!this.form.title) {
        this.form.title = `${this.selectedBrandName} ${this.selectedSeriesName} ${model.name}`
      }
      
      uni.$u.toast('已选择：' + model.name)
    },
    
    // 选择能源类型
    onEnergyConfirm(e) {
      const index = e[0]
      this.form.energyType = this.energyTypes[index]
      this.energyPickerIndex = index
    },
    
    // 选择城市
    onCityConfirm(e) {
      const index = e[0]
      const city = this.cityList[index]
      this.form.cityCode = city.code
      this.form.cityName = city.name
      this.cityPickerIndex = index
      uni.$u.toast('已选择：' + city.name)
    },
    
    chooseImage() {
      uni.chooseImage({
        count: 9 - this.imageList.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.imageList = this.imageList.concat(res.tempFilePaths)
        },
        fail: (err) => {
          if (err.errMsg !== 'chooseImage:fail cancel') {
            uni.$u.toast('选择图片失败')
          }
        }
      })
    },

    removeImage(index) {
      this.imageList.splice(index, 1)
    },

    // 选择颜色
    onColorConfirm(e) {
      const index = e[0]
      this.form.color = this.colorList[index]
      this.colorPickerIndex = index
    },

    // 扫描车架号
    scanVin() {
      uni.showActionSheet({
        itemList: ['扫描行驶证', '扫描车身铭牌', '手动输入'],
        success: (res) => {
          if (res.tapIndex === 0) {
            this.scanDocument('driving_license')
          } else if (res.tapIndex === 1) {
            this.scanDocument('vehicle_nameplate')
          } else {
            // 手动输入，聚焦到输入框
            this.$refs.vinInput && this.$refs.vinInput.focus()
          }
        }
      })
    },

    // 扫描证件
    scanDocument(type) {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['camera'],
        success: (res) => {
          // 调用OCR识别接口
          this.$u.api.post('/ai/ocr', {
            type: type,
            imageUrl: res.tempFilePaths[0]
          }).then(response => {
            if (response.data && response.data.vin) {
              this.form.vin = response.data.vin
              uni.$u.toast('识别成功')
            } else {
              uni.$u.toast('识别失败，请手动输入')
            }
          }).catch(() => {
            uni.$u.toast('识别失败，请手动输入')
          })
        }
      })
    },

    // 上传检测报告
    uploadReport() {
      uni.chooseFile({
        count: 1,
        type: 'file',
        extension: ['.pdf'],
        success: (res) => {
          // 上传文件
          this.$u.api.post('/upload/file', {
            file: res.tempFilePaths[0]
          }).then(response => {
            if (response.data && response.data.url) {
              this.form.inspectionReportUrl = response.data.url
              uni.$u.toast('上传成功')
            }
          }).catch(() => {
            uni.$u.toast('上传失败')
          })
        }
      })
    },

    // 上传证件材料
    uploadMaterial(type) {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          // 上传图片
          this.$u.api.post('/upload/image', {
            file: res.tempFilePaths[0]
          }).then(response => {
            if (response.data && response.data.url) {
              // 解析现有的证件材料
              let materials = {}
              try {
                materials = JSON.parse(this.form.certificateMaterials || '{}')
              } catch (e) {
                materials = {}
              }
              materials[type] = response.data.url
              this.form.certificateMaterials = JSON.stringify(materials)
              uni.$u.toast('上传成功')
            }
          }).catch(() => {
            uni.$u.toast('上传失败')
          })
        }
      })
    },

    // 保存草稿
    saveDraft() {
      this.form.isDraft = true
      this.submitForm()
    },

    // AI帮我填
    aiFill() {
      this.showVoiceInput = true
    },

    // 拍照识别
    takePhoto() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['camera'],
        success: (res) => {
          // 调用AI识别接口
          this.$u.api.post('/ai/recognize', {
            imageUrl: res.tempFilePaths[0]
          }).then(response => {
            if (response.data) {
              // 填充识别到的信息
              const data = response.data
              if (data.title) this.form.title = data.title
              if (data.brand) this.form.brandId = data.brandId
              if (data.series) this.form.seriesId = data.seriesId
              if (data.model) this.form.modelId = data.modelId
              if (data.year) this.form.year = data.year
              if (data.mileage) this.form.mileage = data.mileage
              if (data.price) this.form.price = data.price
              if (data.color) this.form.color = data.color
              if (data.description) this.form.description = data.description
              this.showVoiceInput = false
              uni.$u.toast('识别成功')
            }
          }).catch(() => {
            uni.$u.toast('识别失败')
          })
        }
      })
    },

    // 语音录入
    toggleVoice() {
      // 实现语音录入功能
      uni.$u.toast('语音录入功能开发中')
    },

    // 从相册选择
    chooseFromAlbum() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album'],
        success: (res) => {
          // 调用AI识别接口
          this.$u.api.post('/ai/recognize', {
            imageUrl: res.tempFilePaths[0]
          }).then(response => {
            if (response.data) {
              // 填充识别到的信息
              const data = response.data
              if (data.title) this.form.title = data.title
              if (data.brand) this.form.brandId = data.brandId
              if (data.series) this.form.seriesId = data.seriesId
              if (data.model) this.form.modelId = data.modelId
              if (data.year) this.form.year = data.year
              if (data.mileage) this.form.mileage = data.mileage
              if (data.price) this.form.price = data.price
              if (data.color) this.form.color = data.color
              if (data.description) this.form.description = data.description
              this.showVoiceInput = false
              uni.$u.toast('识别成功')
            }
          }).catch(() => {
            uni.$u.toast('识别失败')
          })
        }
      })
    },

    // 提交表单
    handleSubmit() {
      // 验证表单
      if (!this.validateForm()) {
        return
      }
      // 显示发布确认弹窗
      this.showPublishConfirm = true
    },

    // 确认发布
    confirmPublish() {
      this.showPublishConfirm = false
      // 显示保证金选择弹窗
      this.showDepositChoice = true
    },

    // 使用保证金确认
    confirmWithDeposit() {
      this.showDepositChoice = false
      this.form.deposit = 3000
      this.submitForm()
    },

    // 不使用保证金确认
    confirmWithoutDeposit() {
      this.showDepositChoice = false
      this.form.deposit = 0
      this.submitForm()
    },

    // 提交表单到后端
    submitForm() {
      this.submitting = true
      createCar(this.form).then(res => {
        this.submitting = false
        if (res.code === 200) {
          // 显示发布成功弹窗
          this.showPublishSuccess = true
        } else {
          uni.$u.toast(res.message || '发布失败')
        }
      }).catch(err => {
        this.submitting = false
        uni.$u.toast('发布失败')
      })
    },

    // 验证表单
    validateForm() {
      if (!this.form.brandId) {
        uni.$u.toast('请选择品牌')
        return false
      }
      if (!this.form.seriesId) {
        uni.$u.toast('请选择车系')
        return false
      }
      if (!this.form.year) {
        uni.$u.toast('请输入上牌年份')
        return false
      }
      if (!this.form.mileage) {
        uni.$u.toast('请输入行驶里程')
        return false
      }
      if (this.form.pricingType === 'FIXED' && !this.form.price) {
        uni.$u.toast('请输入售价')
        return false
      }
      if (this.form.pricingType === 'AUCTION' && !this.form.startingPrice) {
        uni.$u.toast('请输入起拍价')
        return false
      }
      if (!this.form.cityCode) {
        uni.$u.toast('请选择城市')
        return false
      }
      return true
    },

    // 返回上一页
    goBack() {
      uni.navigateBack()
    },

    // 计算出口国家文本
    exportCountriesText() {
      // 根据车源信息计算符合的出口国家
      return '俄罗斯、澳大利亚'
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

.page-content {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 120rpx;
}

.form-section {
  background: #fff;
  padding: 0 30rpx;
  border-radius: $border-radius;
  margin: 20rpx;
  box-shadow: $shadow;
}

.switch-field {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 0;
  border-bottom: 1rpx solid $border-color;
  transition: $transition;

  &:active {
    background: rgba(0, 0, 0, 0.02);
  }
}

.switch-label {
  font-size: 28rpx;
  color: $text-color;
}

.upload-section {
  padding: 30rpx 0;
  border-top: 1rpx solid $border-color;
}

.upload-label {
  font-size: 28rpx;
  color: $text-color;
  font-weight: 500;
  display: block;
  margin-bottom: 20rpx;
}

.upload-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.upload-item {
  width: 180rpx;
  height: 180rpx;
  position: relative;
  border-radius: $border-radius;
  overflow: hidden;
}

.upload-image {
  width: 100%;
  height: 100%;
  border-radius: $border-radius;
}

.upload-remove {
  position: absolute;
  top: -12rpx;
  right: -12rpx;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.9);
  }
}

.upload-btn {
  border: 2rpx dashed $border-color;
  border-radius: $border-radius;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: $transition;
  background: $bg-color;

  &:hover {
    border-color: $cta-color;
    background: rgba(3, 105, 161, 0.05);
  }

  &:active {
    transform: scale(0.98);
  }
}

.upload-text {
  font-size: 22rpx;
  color: $text-secondary;
  margin-top: 8rpx;
}

.upload-tip {
  font-size: 22rpx;
  color: $text-secondary;
  margin-top: 16rpx;
  display: block;
}

.upload-subtitle {
  font-size: 22rpx;
  color: $text-secondary;
  margin-left: 10rpx;
}

.submit-bar {
  padding: 30rpx;
  background: #fff;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  box-shadow: 0 -4rpx 16rpx rgba(15, 23, 42, 0.08);
  border-radius: $border-radius $border-radius 0 0;
}

.submit-buttons {
  display: flex;
  gap: 20rpx;
}

.ai-fill-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20rpx;
  padding: 16rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: $border-radius;
  cursor: pointer;
  transition: $transition;

  &:active {
    transform: scale(0.98);
  }

  text {
    color: #fff;
    font-size: 28rpx;
    margin-left: 10rpx;
  }
}

.scan-btn {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 10rpx;
  border-radius: 8rpx;
  background: rgba(41, 121, 255, 0.1);
  transition: $transition;

  &:active {
    background: rgba(41, 121, 255, 0.2);
  }
}

.scan-text {
  font-size: 22rpx;
  color: #2979ff;
  margin-left: 6rpx;
}

.pricing-section {
  padding: 30rpx 0;
  border-bottom: 1rpx solid $border-color;
}

.section-title {
  font-size: 28rpx;
  color: $text-color;
  font-weight: 500;
  display: block;
  margin-bottom: 20rpx;
}

.pricing-options {
  display: flex;
  gap: 20rpx;
}

.pricing-option {
  flex: 1;
  padding: 20rpx;
  text-align: center;
  background: $bg-color;
  border: 2rpx solid $border-color;
  border-radius: $border-radius;
  cursor: pointer;
  transition: $transition;

  &.active {
    background: rgba(41, 121, 255, 0.1);
    border-color: #2979ff;
    color: #2979ff;
  }

  &:active {
    transform: scale(0.98);
  }
}

.unit {
  font-size: 24rpx;
  color: $text-secondary;
}

.report-type-selector {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.report-type {
  padding: 16rpx 30rpx;
  background: $bg-color;
  border: 2rpx solid $border-color;
  border-radius: 8rpx;
  cursor: pointer;
  transition: $transition;

  &.active {
    background: rgba(41, 121, 255, 0.1);
    border-color: #2979ff;
    color: #2979ff;
  }
}

.report-input {
  margin-bottom: 16rpx;
}

.report-upload {
  margin-bottom: 16rpx;
}

.material-list {
  display: flex;
  gap: 30rpx;
  margin-bottom: 20rpx;
}

.material-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
  background: $bg-color;
  border-radius: $border-radius;
  cursor: pointer;
  transition: $transition;

  &:active {
    background: rgba(41, 121, 255, 0.1);
  }

  text {
    font-size: 24rpx;
    color: $text-secondary;
    margin-top: 10rpx;
  }
}

.confirm-content {
  padding: 30rpx;
  text-align: center;

  .link {
    color: #2979ff;
    text-decoration: underline;
  }
}

.deposit-content {
  padding: 30rpx;

  .deposit-option {
    margin-top: 20rpx;
    padding: 20rpx;
    background: rgba(41, 121, 255, 0.1);
    border-radius: 8rpx;
    text-align: center;
    font-weight: 500;
  }
}

.success-content {
  padding: 30rpx;
  text-align: center;

  .highlight {
    color: #2979ff;
    font-weight: 500;
  }
}

.voice-input-content {
  padding: 40rpx;
}

.voice-header {
  text-align: center;
  margin-bottom: 40rpx;

  .voice-title {
    font-size: 36rpx;
    font-weight: 600;
    color: $text-color;
    display: block;
    margin-bottom: 16rpx;
  }

  .voice-desc {
    font-size: 26rpx;
    color: $text-secondary;
    line-height: 1.5;
  }
}

.voice-wave {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100rpx;
  margin-bottom: 30rpx;

  .wave-bar {
    width: 6rpx;
    background: linear-gradient(180deg, #2979ff 0%, #667eea 100%);
    margin: 0 4rpx;
    border-radius: 3rpx;
    animation: wave 1s ease-in-out infinite;
  }
}

@keyframes wave {
  0%, 100% {
    transform: scaleY(0.5);
  }
  50% {
    transform: scaleY(1);
  }
}

.voice-status {
  text-align: center;
  font-size: 28rpx;
  color: $text-secondary;
  margin-bottom: 40rpx;
}

.voice-buttons {
  display: flex;
  justify-content: center;
  gap: 60rpx;
  margin-bottom: 40rpx;
}

.voice-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx;
  background: $bg-color;
  border-radius: 50%;
  cursor: pointer;
  transition: $transition;

  &.active {
    background: linear-gradient(135deg, #2979ff 0%, #667eea 100%);
    box-shadow: 0 8rpx 20rpx rgba(41, 121, 255, 0.3);
  }

  &:active {
    transform: scale(0.95);
  }

  text {
    font-size: 22rpx;
    color: $text-secondary;
    margin-top: 10rpx;
  }
}

.voice-documents {
  display: flex;
  justify-content: center;
  gap: 40rpx;

  .doc-label {
    font-size: 24rpx;
    color: #2979ff;
    padding: 10rpx 20rpx;
    background: rgba(41, 121, 255, 0.1);
    border-radius: 6rpx;
  }
}

.switch-info {
  display: flex;
  flex-direction: column;

  .switch-desc {
    font-size: 22rpx;
    color: $text-secondary;
    margin-top: 6rpx;
  }
}
</style>
