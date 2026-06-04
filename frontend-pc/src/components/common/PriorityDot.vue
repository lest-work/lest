<script setup lang="ts">
import { computed } from 'vue';

export interface PriorityDotProps {
  priority: 'highest' | 'high' | 'medium' | 'low' | 'lowest' | string;
  size?: number;
}

const PRIORITY_COLORS: Record<string, string> = {
  highest: 'var(--priority-p0)',
  high: 'var(--priority-p1)',
  medium: 'var(--priority-p2)',
  low: 'var(--priority-p3)',
  lowest: 'var(--priority-none)',
};

const props = withDefaults(defineProps<PriorityDotProps>(), {
  size: 7,
});

const color = computed(() => PRIORITY_COLORS[props.priority] || 'var(--priority-none)');
</script>

<template>
  <span
    class="priority-dot"
    :style="{
      background: color,
      width: size + 'px',
      height: size + 'px',
    }"
  />
</template>

<style scoped lang="scss">
.priority-dot {
  border-radius: 50%;
  flex-shrink: 0;
}
</style>
