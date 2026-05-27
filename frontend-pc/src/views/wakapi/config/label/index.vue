<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">标签配置</span>
          <el-button type="primary" @click="openEdit()">
            <el-icon><Plus /></el-icon>
            新建
          </el-button>
        </div>
      </template>

      <ele-table :loading="loading" :data="data" :columns="columns" row-key="id" stripe>
        <template #color="{ row }">
          <span class="color-dot" :style="{ background: row.color || '#409eff' }"></span>
          {{ row.color || '#409eff' }}
        </template>
        <template #action="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定要删除此标签吗?" @confirm="remove(row)">
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
      :title="editRow?.id ? '编辑项目标签' : '新建项目标签'"
      width="440px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="90px">
        <el-form-item label="标签名称" prop="label">
          <el-input v-model="editForm.label" placeholder="如: 前端项目, 后端项目" clearable />
        </el-form-item>
        <el-form-item label="匹配项目" prop="projectKey">
          <el-select
            v-model="editForm.projectKey"
            placeholder="选择或搜索项目"
            filterable
            allow-create
            style="width: 100%"
          >
            <el-option
              v-for="p in projectList"
              :key="p.name"
              :value="p.name"
              :label="p.name"
            />
          </el-select>
          <div class="form-tip">输入项目名称，可手动输入不在列表中的项目</div>
        </el-form-item>
        <el-form-item label="标签颜色" prop="color">
          <el-color-picker v-model="editForm.color" />
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
  import { ref, reactive, onMounted } from 'vue';
  import { Plus } from '@element-plus/icons-vue';
  import { EleMessage } from 'ele-admin-plus';
  import type { FormInstance, FormRules } from 'element-plus';
  import {
    pageLabels,
    addLabel,
    updateLabel,
    removeLabel,
    getWakapiProjects,
    type WakapiLabel,
    type WakapiProject
  } from '@/api/wakapi';

  defineOptions({ name: 'WakapiProjectLabelConfig' });

  const loading = ref(false);
  const saving = ref(false);
  const data = ref<WakapiLabel[]>([]);
  const total = ref(0);
  const pager = reactive({ page: 1, limit: 20 });
  const projectList = ref<WakapiProject[]>([]);

  const editVisible = ref(false);
  const editRow = ref<WakapiLabel | null>(null);
  const editForm = reactive({ label: '', projectKey: '', color: '#409eff' });
  const formRef = ref<FormInstance>();

  const rules: FormRules = {
    label: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
    projectKey: [{ required: true, message: '请输入或选择项目', trigger: 'change' }]
  };

  const columns = [
    { columnKey: 'label', label: '标签名称', minWidth: 120, prop: 'label' },
    { columnKey: 'projectKey', label: '匹配项目', minWidth: 150, prop: 'projectKey' },
    { columnKey: 'color', label: '颜色', width: 130, slot: true },
    { columnKey: 'createTime', label: '创建时间', width: 170, prop: 'createTime' },
    { columnKey: 'action', label: '操作', width: 120, slot: true, align: 'center' }
  ];

  const loadProjects = async () => {
    try {
      const projects = await getWakapiProjects();
      projectList.value = projects ?? [];
    } catch {
      projectList.value = [];
    }
  };

  const query = async () => {
    loading.value = true;
    try {
      const res = await pageLabels({ page: pager.page, limit: pager.limit });
      data.value = res?.list ?? [];
      total.value = res?.count ?? 0;
    } catch {
      data.value = [];
      total.value = 0;
    } finally {
      loading.value = false;
    }
  };

  const openEdit = (row?: WakapiLabel) => {
    editRow.value = row ?? null;
    editForm.label = row?.label ?? '';
    editForm.projectKey = row?.projectKey ?? '';
    editForm.color = row?.color ?? '#409eff';
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
        await updateLabel({ ...payload, id: editRow.value.id });
        EleMessage.success('修改成功');
      } else {
        await addLabel(payload);
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

  const remove = async (row: WakapiLabel) => {
    if (!row.id) return;
    try {
      await removeLabel(row.id);
      EleMessage.success('删除成功');
      query();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '删除失败');
    }
  };

  onMounted(() => {
    loadProjects();
    query();
  });
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    .title { font-size: 15px; font-weight: 500; }
  }
  .color-dot {
    display: inline-block;
    width: 14px;
    height: 14px;
    border-radius: 50%;
    vertical-align: middle;
    margin-right: 4px;
  }
  .form-tip {
    font-size: 12px;
    color: var(--el-text-color-placeholder);
    margin-top: 4px;
  }
  .pagination-wrap {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
</style>
