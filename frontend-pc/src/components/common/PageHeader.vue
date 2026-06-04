<script setup lang="ts">
export interface PageHeaderProps {
  title: string;
}

const props = defineProps<PageHeaderProps>();

defineEmits<{
  (e: 'back'): void;
}>();
</script>

<template>
  <div class="page-header">
    <div class="header-left">
      <button v-if="$attrs.onBack" class="back-btn" @click="$emit('back')">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
        返回
      </button>
      <h2 class="page-title">{{ props.title }}</h2>
    </div>
    <div class="header-actions">
      <slot name="actions"/>
    </div>
  </div>
</template>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: color var(--transition-fast), background var(--transition-fast);

  &:hover { color: var(--text-primary); background: var(--bg-hover); }
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}
</style>
