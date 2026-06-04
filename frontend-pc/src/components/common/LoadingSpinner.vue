<script setup lang="ts">
export interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
}

withDefaults(defineProps<LoadingSpinnerProps>(), {
  size: 'md',
});

const SIZE_MAP = { sm: 20, md: 28, lg: 36 };
</script>

<template>
  <div class="loading-center">
    <div class="loading-pulse" :style="{ width: SIZE_MAP[size] + 'px', height: SIZE_MAP[size] + 'px' }"/>
  </div>
</template>

<style scoped lang="scss">
.loading-center {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--space-16);
}

.loading-pulse {
  border-radius: 50%;
  background: var(--color-primary);
  animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1); }
}
</style>
