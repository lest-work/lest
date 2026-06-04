<script setup lang="ts">
export interface AssigneeAvatarProps {
  name?: string;
  src?: string;
  size?: number;
}

const props = withDefaults(defineProps<AssigneeAvatarProps>(), {
  size: 22,
});
</script>

<template>
  <div
    class="assignee-avatar"
    :style="{ width: size + 'px', height: size + 'px', fontSize: Math.floor(size * 0.45) + 'px' }"
  >
    <img v-if="src" :src="src" :alt="name" />
    <span v-else>{{ name?.charAt(0)?.toUpperCase() || '?' }}</span>
  </div>
</template>

<style scoped lang="scss">
.assignee-avatar {
  border-radius: var(--radius-sm);
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
</style>
