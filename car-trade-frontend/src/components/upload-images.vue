<template>
  <u-upload
    :action="uploadAction"
    :max-count="max"
    :file-list="fileList"
    @on-success="onSuccess"
    @on-remove="onRemove"
    @on-error="onError"
  ></u-upload>
</template>

<script>
export default {
  props: {
    max: { type: Number, default: 9 },
    images: { type: Array, default: () => [] }
  },
  emits: ['update:images'],
  data() {
    return {
      fileList: this.images.map(url => ({ url })),
      uploadAction: `${uni.$u.http.config.baseURL}/upload`
    }
  },
  watch: {
    images(val) {
      this.fileList = val.map(url => ({ url }))
    }
  },
  methods: {
    onSuccess(data, index, lists, name) {
      const urls = lists.filter(f => f.url).map(f => f.url)
      this.$emit('update:images', urls)
    },
    onRemove(index, lists) {
      const urls = lists.filter(f => f.url).map(f => f.url)
      this.$emit('update:images', urls)
    },
    onError(error) {
      this.$u.toast('上传失败，请重试')
    }
  }
}
</script>
