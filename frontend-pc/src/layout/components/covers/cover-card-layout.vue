<template>
  <div
    :style="{
      height: '100%',
      padding: '6px',
      boxSizing: 'border-box',
      display: 'flex'
    }"
  >
    <div
      class="setting-layout-cover-bg-dark"
      :style="{ width: '10px', borderRadius: '3px' }"
    ></div>
    <div
      class="setting-layout-cover-bg-light"
      :style="{ width: '20px', borderRadius: '3px', margin: '0 4px' }"
    ></div>
    <div :style="{ flex: 1, display: 'flex', flexDirection: 'column' }">
      <div
        class="setting-layout-cover-bg-primary"
        :style="{ height: '12px', borderRadius: '3px 3px 0 0' }"
      ></div>
      <div
        class="setting-layout-cover-bg-light"
        :style="{
          height: '8px',
          borderRadius: '0 0 3px 3px',
          display: 'flex',
          alignItems: 'center',
          padding: '0 4px'
        }"
      >
        <IconSkeleton size="xs" :style="{ width: '10px' }" />
        <IconSkeleton size="xs" :style="{ width: '10px', marginLeft: '4px' }" />
        <IconSkeleton size="xs" :style="{ width: '10px', marginLeft: '4px' }" />
      </div>
      <div
        class="setting-layout-cover-bg-light"
        :style="{ flex: 1, marginTop: '4px', borderRadius: '3px' }"
      ></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import IconSkeleton from './icon-skeleton.vue';
</script>
