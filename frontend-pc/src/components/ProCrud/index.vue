<!-- 配置式增删改查 -->
<template>
  <EleCrud
    v-bind="$props"
    ref="crudRef"
    :httpRequest="request"
    :proFormComponent="proFormComponent || ProForm"
    @tableSelectionsChange="emitMethods['tableSelectionsChange']"
  >
    <template v-for="name in Object.keys($slots)" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps || {}"></slot>
    </template>
  </EleCrud>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue';
  import { useComponentEvents } from 'ele-admin-plus/es/utils/hook';
  import type { EleCrudInstance } from 'ele-admin-plus/es/ele-app/plus';
  import { crudProps, crudEmits } from 'ele-admin-plus/es/ele-crud/props';
  import ProForm from '@/components/ProForm/index.vue';
  import request from '@/utils/request';

  defineOptions({ name: 'ProCrud' });

  defineProps(crudProps);

  const emit = defineEmits(crudEmits);

  const { emitMethods } = useComponentEvents(crudEmits, emit);

  /** 增删改查组件 */
  const crudRef = ref<EleCrudInstance>(null);

  /** 表格组件 */
  const tableRef = computed(() => crudRef.value?.tableRef);

  /** 获取表格选中数据 */
  const getTableSelections = () => {
    if (!crudRef.value) {
      throw new Error('crudRef is null');
    }
    return crudRef.value.getTableSelections();
  };

  defineExpose({
    crudRef,
    tableRef,
    getTableSelections
  });
</script>
