<script setup lang="ts">
export interface ConfirmButtonProps {
  text?: string;
  loadingText?: string;
  type?: 'primary' | 'secondary' | 'danger';
  loading?: boolean;
  disabled?: boolean;
}

withDefaults(defineProps<ConfirmButtonProps>(), {
  text: '确定',
  loadingText: '保存中...',
  type: 'primary',
  loading: false,
  disabled: false,
});
</script>

<template>
  <button
    class="confirm-btn"
    :class="[`btn-${type}`]"
    :disabled="disabled || loading"
    @click="$emit('click')"
  >
    {{ loading ? loadingText : text }}
  </button>
</template>

<style scoped lang="scss">
.confirm-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  padding: 0 var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast), opacity var(--transition-fast);
  border: none;

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &.btn-primary {
    background: var(--color-primary);
    color: #fff;
    &:hover:not(:disabled) { background: var(--color-primary-hover); }
  }

  &.btn-secondary {
    background: var(--bg-primary);
    color: var(--text-secondary);
    border: 1px solid var(--border-color);
    &:hover:not(:disabled) { border-color: var(--text-muted); color: var(--text-primary); }
  }

  &.btn-danger {
    background: var(--color-danger);
    color: #fff;
    &:hover:not(:disabled) { opacity: 0.85; }
  }
}
</style>
