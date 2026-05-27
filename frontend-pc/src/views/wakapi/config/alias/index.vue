<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">别名管理</span>
          <el-button type="primary" @click="openEdit()">
            <el-icon><Plus /></el-icon>
            新建
          </el-button>
        </div>
      </template>

      <ele-table :loading="loading" :data="data" :columns="columns" row-key="id" stripe>
        <template #type="{ row }">
          <el-tag :type="typeTagType(row.type)" size="small">
            {{ typeLabel(row.type) }}
          </el-tag>
        </template>
        <template #action="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定要删除此别名吗?" @confirm="remove(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </ele-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pager.page"
          v-model:page-size="pager.limit"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="query"
          @current-change="query"
        />
      </div>
    </ele-card>

    <el-dialog
      v-model="editVisible"
      :title="editRow?.id ? '编辑别名' : '新建别名'"
      width="440px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="editForm.type" style="width: 100%">
            <el-option :value="1" label="项目 (Project)" />
            <el-option :value="2" label="语言 (Language)" />
            <el-option :value="3" label="编辑器 (Editor)" />
            <el-option :value="4" label="操作系统 (OS)" />
            <el-option :value="5" label="机器 (Machine)" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配键" prop="key">
          <el-input v-model="editForm.key" placeholder="原始名称，如 vue-tsc" clearable />
        </el-form-item>
        <el-form-item label="替换为" prop="value">
          <el-input v-model="editForm.value" placeholder="映射后的名称，如 Vue.js" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, reactive } from 'vue';
  import { Plus } from '@element-plus/icons-vue';
  import { EleMessage } from 'ele-admin-plus';
  import type { FormInstance, FormRules } from 'element-plus';
  import {
    pageAliases,
    addAlias,
    updateAlias,
    removeAlias,
    type WakapiAlias
  } from '@/api/wakapi';

  defineOptions({ name: 'WakapiAliasConfig' });

  const loading = ref(false);
  const saving = ref(false);
  const data = ref<WakapiAlias[]>([]);
  const total = ref(0);
  const pager = reactive({ page: 1, limit: 20 });

  const editVisible = ref(false);
  const editRow = ref<WakapiAlias | null>(null);
  const editForm = reactive({ type: 1, key: '', value: '' });
  const formRef = ref<FormInstance>();

  const rules: FormRules = {
    type: [{ required: true, message: '请选择类型', trigger: 'change' }],
    key: [{ required: true, message: '请输入匹配键', trigger: 'blur' }],
    value: [{ required: true, message: '请输入替换值', trigger: 'blur' }]
  };

  const columns = [
    { columnKey: 'type', label: '类型', width: 110, slot: true },
    { columnKey: 'key', label: '匹配键', minWidth: 150, prop: 'key' },
    { columnKey: 'value', label: '替换为', minWidth: 150, prop: 'value' },
    { columnKey: 'createTime', label: '创建时间', width: 170, prop: 'createTime' },
    { columnKey: 'action', label: '操作', width: 120, slot: true, align: 'center' }
  ];

  const typeLabel = (type: number): string => {
    const map: Record<number, string> = { 1: '项目', 2: '语言', 3: '编辑器', 4: '操作系统', 5: '机器' };
    return map[type] ?? '未知';
  };

  const typeTagType = (type: number): string => {
    const map: Record<number, string> = { 1: '', 2: 'success', 3: 'warning', 4: 'info', 5: 'danger' };
    return map[type] ?? 'info';
  };

  const query = async () => {
    loading.value = true;
    try {
      const res = await pageAliases({ page: pager.page, limit: pager.limit });
      data.value = res?.list ?? [];
      total.value = res?.count ?? 0;
    } catch {
      data.value = [];
      total.value = 0;
    } finally {
      loading.value = false;
    }
  };

  const openEdit = (row?: WakapiAlias) => {
    editRow.value = row ?? null;
    if (row) {
      editForm.type = row.type;
      editForm.key = row.key;
      editForm.value = row.value;
    } else {
      editForm.type = 1;
      editForm.key = '';
      editForm.value = '';
    }
    editVisible.value = true;
    formRef.value?.clearValidate();
  };

  const save = async () => {
    const valid = await formRef.value?.validate().catch(() => false);
    if (!valid) return;
    saving.value = true;
    try {
      const payload = { ...editForm };
      if (editRow.value?.id) {
        await updateAlias({ ...payload, id: editRow.value.id });
        EleMessage.success('修改成功');
      } else {
        await addAlias(payload);
        EleMessage.success('添加成功');
      }
      editVisible.value = false;
      query();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '保存失败');
    } finally {
      saving.value = false;
    }
  };

  const remove = async (row: WakapiAlias) => {
    if (!row.id) return;
    try {
      await removeAlias(row.id);
      EleMessage.success('删除成功');
      query();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '删除失败');
    }
  };

  query();
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .title { font-size: 15px; font-weight: 500; }
  }
  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
</style>
