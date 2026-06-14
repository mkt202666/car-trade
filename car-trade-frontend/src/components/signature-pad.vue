<template>
  <view class="signature-pad-container">
    <view class="signature-header">
      <text class="signature-title">{{ title || '请手写签名' }}</text>
      <view class="signature-actions" v-if="showActions">
        <text class="action-btn clear-btn" @click="clear">清空</text>
        <text class="action-btn confirm-btn" @click="confirm">确认</text>
      </view>
    </view>
    
    <canvas 
      class="signature-canvas" 
      :id="canvasId" 
      :canvas-id="canvasId"
      @touchstart="handleTouchStart"
      @touchmove="handleTouchMove"
      @touchend="handleTouchEnd"
      disable-scroll="true"
    ></canvas>
    
    <view class="signature-hint" v-if="hint">
      <text>{{ hint }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SignaturePad',
  props: {
    canvasId: {
      type: String,
      default: 'signatureCanvas'
    },
    title: {
      type: String,
      default: '请手写签名'
    },
    hint: {
      type: String,
      default: '请在上方区域手写您的签名'
    },
    showActions: {
      type: Boolean,
      default: true
    },
    lineWidth: {
      type: Number,
      default: 3
    },
    lineColor: {
      type: String,
      default: '#000000'
    },
    bgColor: {
      type: String,
      default: '#ffffff'
    }
  },
  data() {
    return {
      ctx: null,
      isDrawing: false,
      lastX: 0,
      lastY: 0,
      hasDrawn: false
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCanvas()
    })
  },
  methods: {
    initCanvas() {
      // uni-app Canvas上下文初始化
      this.ctx = uni.createCanvasContext(this.canvasId, this)
      
      // 设置画布样式
      this.ctx.setStrokeStyle(this.lineColor)
      this.ctx.setLineWidth(this.lineWidth)
      this.ctx.setLineCap('round')
      this.ctx.setLineJoin('round')
    },
    
    handleTouchStart(e) {
      this.isDrawing = true
      const touch = e.touches[0]
      this.lastX = touch.x
      this.lastY = touch.y
      this.hasDrawn = true
      
      // 绘制起点
      this.ctx.beginPath()
      this.ctx.moveTo(touch.x, touch.y)
    },
    
    handleTouchMove(e) {
      if (!this.isDrawing) return
      
      const touch = e.touches[0]
      this.ctx.lineTo(touch.x, touch.y)
      this.ctx.stroke()
      this.ctx.draw(true) // true表示保留之前的绘制
      
      this.lastX = touch.x
      this.lastY = touch.y
    },
    
    handleTouchEnd() {
      this.isDrawing = false
      this.ctx.closePath()
    },
    
    // 清空画布
    clear() {
      this.ctx.clearRect(0, 0, 9999, 9999)
      this.ctx.draw()
      this.hasDrawn = false
      this.$emit('clear')
    },
    
    // 确认签名并导出图片
    confirm() {
      if (!this.hasDrawn) {
        uni.$u.toast('请先手写签名')
        return
      }
      
      // 导出为临时文件
      uni.canvasToTempFilePath({
        canvasId: this.canvasId,
        fileType: 'png',
        quality: 1,
        success: (res) => {
          this.$emit('confirm', res.tempFilePath)
        },
        fail: (err) => {
          console.error('导出签名失败:', err)
          uni.$u.toast('导出签名失败')
        }
      }, this)
    },
    
    // 获取签名的base64数据(用于直接上传)
    getBase64() {
      return new Promise((resolve, reject) => {
        uni.canvasToTempFilePath({
          canvasId: this.canvasId,
          fileType: 'png',
          quality: 1,
          success: (res) => {
            // 读取文件并转换为base64
            uni.getFileSystemManager().readFile({
              filePath: res.tempFilePath,
              encoding: 'base64',
              success: (fileRes) => {
                resolve(fileRes.data)
              },
              fail: reject
            })
          },
          fail: reject
        }, this)
      })
    }
  }
}
</script>

<style scoped>
.signature-pad-container {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin: 16px 0;
}

.signature-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.signature-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.signature-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 4px;
}

.clear-btn {
  color: #999;
  background: #f5f5f5;
}

.confirm-btn {
  color: #fff;
  background: #4f46e5;
}

.signature-canvas {
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  background: #fafafa;
}

.signature-hint {
  margin-top: 8px;
  text-align: center;
}

.signature-hint text {
  font-size: 12px;
  color: #999;
}
</style>
