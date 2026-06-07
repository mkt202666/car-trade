<template>
  <u-cell
    :title="title"
    :value="value"
    :icon="icon"
    :border="borderBottom"
    :icon-style="iconStyleObj"
    :clickable="true"
    :is-link="!!$attrs.onClick"
    @click="$emit('click')"
  >
    <template #value><slot name="right" /></template>
    <slot />
  </u-cell>
</template>
<script>
export default {
  name: 'u-cell-item',
  props: {
    title: { type: [String, Number], default: '' },
    value: { type: [String, Number], default: '' },
    icon: { type: String, default: '' },
    iconColor: { type: String, default: '' },
    borderBottom: { type: Boolean, default: true }
  },
  computed: {
    iconStyleObj() {
      return this.iconColor ? { color: this.iconColor } : undefined
    }
  }
}
</script>
