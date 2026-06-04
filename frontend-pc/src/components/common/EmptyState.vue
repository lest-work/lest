<script setup lang="ts">
export interface EmptyStateProps {
  title: string;
  description?: string;
  cta?: string;
  icon?: 'task' | 'project' | 'release' | 'iteration' | 'milestone' | 'gantt' | 'inbox' | 'search';
}

defineEmits<{
  (e: 'cta'): void;
}>();

const props = withDefaults(defineProps<EmptyStateProps>(), {
  icon: 'task',
});
</script>

<template>
  <div class="empty-state">
    <div class="empty-illustration">
      <!-- Task -->
      <svg v-if="icon === 'task'" width="56" height="56" viewBox="0 0 56 56" fill="none">
        <rect x="8" y="16" width="40" height="28" rx="6" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4"/>
        <path d="M18 30h20M18 38h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <!-- Project -->
      <svg v-else-if="icon === 'project'" width="56" height="56" viewBox="0 0 56 56" fill="none">
        <rect x="10" y="20" width="36" height="28" rx="6" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4"/>
        <path d="M28 20V10M20 10L28 6L36 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
      <!-- Release -->
      <svg v-else-if="icon === 'release'" width="56" height="56" viewBox="0 0 56 56" fill="none">
        <rect x="8" y="16" width="40" height="28" rx="6" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4"/>
        <path d="M20 30h16M20 38h10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <!-- Inbox -->
      <svg v-else-if="icon === 'inbox'" width="56" height="56" viewBox="0 0 56 56" fill="none">
        <rect x="6" y="14" width="44" height="32" rx="6" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4"/>
        <polyline points="22 24 26 28 34 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        <path d="M18 38h20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <!-- Search -->
      <svg v-else-if="icon === 'search'" width="56" height="56" viewBox="0 0 56 56" fill="none">
        <circle cx="24" cy="24" r="14" stroke="currentColor" stroke-width="2"/>
        <line x1="34" y1="34" x2="46" y2="46" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
      <!-- Default -->
      <svg v-else width="56" height="56" viewBox="0 0 56 56" fill="none">
        <rect x="8" y="16" width="40" height="28" rx="6" stroke="currentColor" stroke-width="2" stroke-dasharray="4 4"/>
        <path d="M18 30h20M18 38h14" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>
    </div>
    <p class="empty-title">{{ title }}</p>
    <p v-if="description" class="empty-desc">{{ description }}</p>
    <button v-if="cta" class="empty-cta" @click="$emit('cta')">{{ cta }}</button>
  </div>
</template>

<style scoped lang="scss">
.empty-state {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-12) var(--space-6);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);

  .empty-title {
    font-size: var(--text-md);
    font-weight: var(--font-semibold);
    color: var(--text-primary);
    margin: 0;
  }

  .empty-desc {
    font-size: var(--text-sm);
    color: var(--text-muted);
    margin: 0;
  }

  .empty-cta {
    margin-top: var(--space-2);
    padding: var(--space-2) var(--space-5);
    border: none;
    border-radius: var(--radius-md);
    background: var(--color-primary);
    color: #fff;
    font-size: var(--text-sm);
    font-family: var(--font-primary);
    cursor: pointer;
    transition: background var(--transition-fast);

    &:hover { background: var(--color-primary-hover); }
  }
}

.empty-illustration {
  opacity: 0.3;
  color: var(--border-color);
}
</style>
