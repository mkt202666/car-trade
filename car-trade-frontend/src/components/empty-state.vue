<template>
  <u-empty :text="text" :icon="icon" mode="list"></u-empty>
</template>

<script>
export default {
  props: {
    text: { type: String, default: '暂无数据' },
    icon: { type: String, default: 'list' }
  }
}
</script>
