<script setup lang="ts">
export interface FilterPillProps {
  active?: boolean;
  filled?: boolean;
  badge?: string | number;
}

withDefaults(defineProps<FilterPillProps>(), {
  active: false,
  filled: false,
});

defineEmits<{
  (e: 'click'): void;
}>();
</script>

<template>
  <button
    class="filter-pill"
    :class="{ active, filled }"
    @click="$emit('click')"
  >
    <slot />
    <span v-if="badge !== undefined && badge !== null && badge !== ''" class="pill-badge">{{ badge }}</span>
  </button>
</template>

<style scoped lang="scss">
.filter-pill {
  display: inline-flex;
  align-items: center;
  gap: var(--space-1);
  height: 32px;
  padding: 0 var(--space-3);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  transition: all var(--transition-fast);
  white-space: nowrap;

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }

  &.active {
    background: var(--color-primary);
    border-color: var(--color-primary);
    color: #fff;
  }

  &.filled {
    background: var(--bg-secondary);
  }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }
}

.pill-badge {
  background: var(--color-primary);
  color: #fff;
  font-size: 10px;
  padding: 0 5px;
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  min-width: 16px;
  text-align: center;
  line-height: 16px;

  .active & {
    background: rgba(255, 255, 255, 0.3);
  }
}
</style>
