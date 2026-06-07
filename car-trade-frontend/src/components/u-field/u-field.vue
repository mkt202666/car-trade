<template>
  <u-form-item :label="label" :required="required" :border-bottom="borderBottom" :left-icon="leftIcon">
    <slot />
  </u-form-item>
</template>
<script>
export default {
  name: 'u-field',
  props: {
    label: { type: String, default: '' },
    required: { type: Boolean, default: false },
    borderBottom: { type: Boolean, default: true },
    leftIcon: { type: String, default: '' }
  }
}
</script>
