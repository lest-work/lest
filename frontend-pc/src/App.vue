<template>
  <el-config-provider :locale="zh_CN">
    <ele-config-provider
      :locale="eleZh_CN"
      :table="tableConfig"
      :map-key="MAP_KEY"
      :license="LICENSE_CODE"
    >
      <ele-app>
        <router-view />
      </ele-app>
    </ele-config-provider>
  </el-config-provider>
</template>

<script setup>
  import { MAP_KEY, LICENSE_CODE } from '@/config/setting';
  import { useGlobalConfig } from '@/config/use-global-config';
  import { useThemeStore } from '@/store/modules/theme';
  import zh_CN from 'element-plus/es/locale/lang/zh-cn';
  import eleZh_CN from 'ele-admin-plus/es/lang/zh_CN';
  import dayjs from 'dayjs';
  import 'dayjs/locale/zh-cn';

  dayjs.locale('zh-cn');

  /** 组件全局配置 */
  const { tableConfig } = useGlobalConfig();

  /** 恢复主题 */
  const themeStore = useThemeStore();
  themeStore.recoverTheme();
</script>
