<template>

  <el-tag :type="tagType" :effect="tagEffect" size="small" round>{{ label }}</el-tag>

</template>

<script setup lang="ts">

import { computed } from 'vue'

const props = defineProps<{
  status: 'success' | 'warning' | 'danger' | 'info' | 'neutral' | 'primary'
  label: string
}>()

const tagType = computed(() => {
  if (props.status === 'neutral') return 'info'
  if (props.status === 'primary') return 'primary'
  return props.status
})

const tagEffect = computed(() => (props.status === 'neutral' ? 'plain' : 'light'))

</script>
