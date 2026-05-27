<!-- 路由出口 -->
<template>
  <router-view v-slot="{ route, Component }">
    <transition :name="transitionName" mode="out-in" appear>
      <keep-alive v-if="pageKeepAlive" :include="keepAliveInclude" :max="10">
        <component :key="route.path" :is="Component" />
      </keep-alive>
      <component v-else :key="route.path" :is="Component" />
    </transition>
  </router-view>
</template>

<script lang="ts" setup>
  import { storeToRefs } from 'pinia';
  import { useThemeStore } from '@/store/modules/theme';

  defineOptions({ name: 'RouterLayout' });

  const themeStore = useThemeStore();
  const { keepAliveInclude, transitionName, pageKeepAlive } =
    storeToRefs(themeStore);
</script>
