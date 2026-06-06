<template>
  <view>
    <u-navbar title="车源发布" :border-bottom="false" :placeholder="true"></u-navbar>
    <view class="page-content">
      <view class="form-section">
        <u-field label="车辆标题" :required="true">
          <u-input v-model="form.title" placeholder="请输入车辆标题" :maxlength="50" input-align="right"></u-input>
        </u-field>
        <u-field label="品牌">
          <u-input v-model="form.brand" placeholder="请输入品牌" input-align="right" @click="showBrandPicker"></u-input>
          <u-icon slot="right" name="arrow-right" size="28" color="#999"></u-icon>
        </u-field>
        <u-field label="车系">
          <u-input v-model="form.series" placeholder="请输入车系" input-align="right"></u-input>
        </u-field>
        <u-field label="上牌年份">
          <u-input v-model="form.year" placeholder="如: 2023" input-align="right" type="number"></u-input>
        </u-field>
        <u-field label="行驶里程(万公里)">
          <u-input v-model="form.mileage" placeholder="请输入里程" input-align="right" type="digit"></u-input>
        </u-field>
        <u-field label="售价(万元)">
          <u-input v-model="form.price" placeholder="请输入售价" input-align="right" type="digit"></u-input>
        </u-field>
        <u-field label="排量">
          <u-input v-model="form.emission" placeholder="如: 2.0" input-align="right" type="digit"></u-input>
        </u-field>
        <u-field label="变速箱">
          <u-input v-model="form.gearbox" placeholder="自动/手动" input-align="right"></u-input>
        </u-field>
        <u-field label="颜色">
          <u-input v-model="form.color" placeholder="车辆颜色" input-align="right"></u-input>
        </u-field>
        <u-field label="所在城市">
          <u-input v-model="form.city" placeholder="城市" input-align="right"></u-input>
        </u-field>
        <u-field label="车辆描述">
          <u-input v-model="form.description" placeholder="描述车辆状况、车况等" type="textarea" :height="160" :maxlength="500"></u-input>
        </u-field>
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
        </view>
      </view>
      <view class="submit-bar">
        <u-button type="primary" size="default" :loading="submitting" @click="handleSubmit" shape="circle">{{ submitting ? '发布中...' : '发布车源' }}</u-button>
      </view>
    </view>
  </view>
</template>

<script>
import { createCar } from '@/api/car'
import { isValidPrice, isValidMileage } from '@/utils/validate'

export default {
  data() {
    return {
      form: {
        title: '', brand: '', series: '', year: '', mileage: '', price: '',
        emission: '', gearbox: '', color: '', city: '', description: ''
      },
      imageList: [],
      submitting: false
    }
  },
  methods: {
    chooseImage() {
      uni.chooseImage({
        count: 9 - this.imageList.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          this.imageList = this.imageList.concat(res.tempFilePaths)
        }
      })
    },
    removeImage(index) {
      this.imageList.splice(index, 1)
    },
    showBrandPicker() {
      uni.showActionSheet({
        itemList: ['宝马', '奔驰', '奥迪', '保时捷', '丰田', '本田', '大众', '特斯拉', '其他'],
        success: (res) => {
          this.form.brand = ['宝马', '奔驰', '奥迪', '保时捷', '丰田', '本田', '大众', '特斯拉', '其他'][res.tapIndex]
        }
      })
    },
    async handleSubmit() {
      if (!this.form.title) { uni.$u.toast('请输入车辆标题'); return }
      if (!this.form.price || !isValidPrice(this.form.price)) { uni.$u.toast('请输入有效售价'); return }
      if (!isValidMileage(this.form.mileage)) { uni.$u.toast('请输入有效里程'); return }
      this.submitting = true
      try {
        const params = { ...this.form, price: parseFloat(this.form.price) * 10000, mileage: parseFloat(this.form.mileage) * 10000 }
        await createCar(params)
        uni.$u.toast('发布成功')
        setTimeout(() => { uni.navigateBack() }, 800)
      } catch (e) {}
      this.submitting = false
    }
  }
}
</script>

<style lang="scss" scoped>
.page-content { min-height: 100vh; background: #f5f5f5; padding-bottom: 120rpx; }
.form-section { background: #fff; padding: 0 30rpx; }
.upload-section { padding: 30rpx 0; border-top: 1rpx solid #eee; }
.upload-label { font-size: 28rpx; color: #333; display: block; margin-bottom: 20rpx; }
.upload-list { display: flex; flex-wrap: wrap; gap: 16rpx; }
.upload-item { width: 180rpx; height: 180rpx; position: relative; }
.upload-image { width: 100%; height: 100%; border-radius: 12rpx; }
.upload-remove { position: absolute; top: -12rpx; right: -12rpx; }
.upload-btn {
  border: 2rpx dashed #ddd; border-radius: 12rpx;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
}
.upload-text { font-size: 22rpx; color: #ccc; margin-top: 8rpx; }
.submit-bar { padding: 30rpx; }
</style>
