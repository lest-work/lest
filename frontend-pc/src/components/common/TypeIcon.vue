<script setup lang="ts">
import { computed } from 'vue';

export interface TypeIconProps {
  type: 'epic' | 'story' | 'task' | 'bug' | string;
  size?: number;
}

const TYPE_EMOJI: Record<string, string> = {
  epic: '🎯',
  story: '📄',
  task: '☑',
  bug: '🐛',
};

const TYPE_COLOR: Record<string, string> = {
  epic: 'var(--type-epic)',
  story: 'var(--type-story)',
  task: 'var(--type-task)',
  bug: 'var(--type-bug)',
};

const props = withDefaults(defineProps<TypeIconProps>(), {
  size: 13,
});

const emoji = computed(() => TYPE_EMOJI[props.type] || '☑');
const color = computed(() => TYPE_COLOR[props.type] || 'var(--type-task)');
</script>

<template>
  <span class="type-icon" :style="{ color: color, fontSize: size + 'px', lineHeight: 1 }">
    {{ emoji }}
  </span>
</template>

<style scoped lang="scss">
.type-icon {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}
</style>
