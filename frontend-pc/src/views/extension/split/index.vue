<template>
  <ele-page
    hide-footer
    :flex-table="type === 'table'"
    :multi-card="type !== 'table'"
    :style="type !== 'table' ? void 0 : { minHeight: '420px' }"
  >
    <demo-basic v-if="type !== 'table'" @change="handleChange" />
    <demo-table v-else @change="handleChange" />
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, watch, unref } from 'vue';
  import { useRouter } from 'vue-router';
  import DemoBasic from './components/demo-basic.vue';
  import DemoTable from './components/demo-table.vue';

  defineOptions({ name: 'ExtensionSplit' });

  const { currentRoute, push } = useRouter();

  const type = ref<string>();

  const handleChange = (value?: string) => {
    type.value = value;
    if (value) {
      push(`/extension/split?type=${value}`);
      return;
    }
    push('/extension/split');
  };

  watch(
    currentRoute,
    (route) => {
      const { path, query } = unref(route);
      if (path === '/extension/split') {
        if (query.type) {
          if (typeof query.type === 'string') {
            type.value = query.type;
            return;
          } else if (query.type.length && query.type[0]) {
            type.value = query.type[0];
            return;
          }
        }
        type.value = '';
      }
    },
    { immediate: true }
  );
</script>
