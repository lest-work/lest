<!-- 部门选择下拉框 -->
<template>
  <el-tree-select
    clearable
    check-strictly
    default-expand-all
    :data="data"
    node-key="deptId"
    :props="{ label: 'deptName' }"
    v-model="model"
    :placeholder="placeholder"
    class="ele-fluid"
    :popper-options="{ strategy: 'fixed' }"
  />
</template>

<script setup>
  import { ref } from 'vue';
  import { EleMessage, toTree } from 'ele-admin-plus';
  import { listDepts } from '@/api/system/dept';

  defineProps({
    /** 提示信息 */
    placeholder: {
      type: String,
      default: '请选择归属部门'
    }
  });

  /** 选中的部门 */
  const model = defineModel({ type: [Number, String] });

  /** 部门数据 */
  const data = ref([]);

  /** 获取部门数据 */
  listDepts()
    .then((list) => {
      data.value = toTree({
        data: list,
        idField: 'deptId',
        parentIdField: 'parentId'
      });
    })
    .catch((e) => {
      EleMessage.error({ message: e.message, plain: true });
    });
</script>
