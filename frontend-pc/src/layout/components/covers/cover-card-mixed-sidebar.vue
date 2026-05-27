<template>
  <div :style="{ height: '100%', display: 'flex', flexDirection: 'column' }">
    <div
      class="setting-layout-cover-bg-light"
      :style="{
        height: '14px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        paddingRight: '4px'
      }"
    >
      <IconSkeleton :style="{ width: '8px', height: '8px' }" />
    </div>
    <div :style="{ flex: 1, display: 'flex', padding: '6px' }">
      <div
        class="setting-layout-cover-bg-light"
        :style="{ width: '32px', borderRadius: '4px' }"
      >
        <div
          class="setting-layout-cover-border-lighter"
          :style="{
            width: '12px',
            height: '100%',
            borderRightStyle: 'solid',
            borderRightWidth: '1px'
          }"
        ></div>
      </div>
      <div
        class="setting-layout-cover-bg-light"
        :style="{ flex: 1, marginLeft: '6px', borderRadius: '4px' }"
      ></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import IconSkeleton from './icon-skeleton.vue';
</script>
