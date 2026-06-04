<script setup lang="ts">
export interface StatusChipProps {
  status: string;
  label: string;
  color: string;
  size?: 'sm' | 'md';
}

withDefaults(defineProps<StatusChipProps>(), {
  size: 'md',
});
</script>

<template>
  <span class="status-chip" :class="[`chip-${size}`]" :style="{ background: color + '20', color }">
    <span class="status-dot" :style="{ background: color }"/>
    {{ label }}
  </span>
</template>

<style scoped lang="scss">
.status-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-weight: var(--font-medium);
  border-radius: var(--radius-full);
  white-space: nowrap;

  &.chip-md {
    font-size: var(--text-xs);
    padding: 3px 8px;
  }

  &.chip-sm {
    font-size: 11px;
    padding: 2px 6px;
  }
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}
</style>
