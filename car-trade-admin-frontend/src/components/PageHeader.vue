<template>

  <div class="flex justify-between items-end mb-6 gap-4">
    <div>
      <h2 class="text-[18px] font-bold text-gray-900 tracking-tight">{{ title }}</h2>
      <p v-if="subtitle" class="text-[12px] text-gray-500 mt-1">{{ subtitle }}</p>
    </div>
    <div v-if="$slots.actions" class="flex gap-2.5 shrink-0">
      <slot name="actions" />
    </div>
  </div>

</template>

<script setup lang="ts">

defineProps<{
  title: string
  subtitle?: string
}>()

</script>
