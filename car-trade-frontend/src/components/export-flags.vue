<template>
  <view class="export-flags">
    <text v-for="(country, i) in countries" :key="i" class="flag">{{ getFlag(country) }}</text>
  </view>
</template>

<script>
const FLAG_MAP = {
  '俄罗斯': '🇷🇺', '俄': '🇷🇺', 'Russia': '🇷🇺',
  '哈萨克斯坦': '🇰🇿', '哈萨克': '🇰🇿',
  '乌兹别克斯坦': '🇺🇿', '乌兹别克': '🇺🇿',
  '吉尔吉斯斯坦': '🇰🇬', '吉尔吉斯': '🇰🇬',
  '塔吉克斯坦': '🇹🇯', '塔吉克': '🇹🇯',
  '阿塞拜疆': '🇦🇿',
  '蒙古': '🇲🇳', 'Mongolia': '🇲🇳',
  '白俄罗斯': '🇧🇾', '白俄': '🇧🇾',
  '越南': '🇻🇳', 'Vietnam': '🇻🇳',
  '老挝': '🇱🇦',
  '柬埔寨': '🇰🇭',
  '缅甸': '🇲🇲',
  '泰国': '🇹🇭', 'Thailand': '🇹🇭',
  '马来西亚': '🇲🇾', 'Malaysia': '🇲🇾',
  '菲律宾': '🇵🇭',
  '印度尼西亚': '🇮🇩', '印尼': '🇮🇩',
  '非洲': '🌍',
  '中东': '🌍'
}
export default {
  props: {
    countries: { type: Array, required: true }
  },
  methods: {
    getFlag(name) {
      return FLAG_MAP[name] || '🌍'
    }
  }
}
</script>

<style scoped>
.export-flags {
  display: inline-flex;
  align-items: center;
  gap: 4rpx;
}
.flag {
  font-size: 24rpx;
}
</style>
