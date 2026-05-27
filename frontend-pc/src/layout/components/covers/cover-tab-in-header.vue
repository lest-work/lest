<template>
  <div :style="{ height: '100%', display: 'flex' }">
    <div class="setting-layout-cover-bg-dark" :style="{ width: '28px' }"></div>

    <div
      class="setting-layout-cover-bg-light"
      :style="{
        flex: 1,
        height: '14px',
        display: 'flex',
        alignItems: 'center',
        padding: '0 4px'
      }"
    >
      <IconSkeleton size="xs" :style="{ width: '10px' }" />
      <IconSkeleton size="xs" :style="{ width: '10px', marginLeft: '4px' }" />
      <IconSkeleton size="xs" :style="{ width: '10px', marginLeft: '4px' }" />
      <IconSkeleton
        :style="{ width: '8px', height: '8px', marginLeft: 'auto' }"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
  import IconSkeleton from './icon-skeleton.vue';
</script>
