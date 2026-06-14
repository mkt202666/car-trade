<template>

  <ElConfigProvider :locale="zhCn">
    <RouterView />
  </ElConfigProvider>

</template>

<script setup lang="ts">

import zhCn from 'element-plus/es/locale/lang/zh-cn'

</script>
