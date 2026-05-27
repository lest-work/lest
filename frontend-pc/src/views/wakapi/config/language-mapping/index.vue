<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">语言映射</span>
          <el-button type="primary" @click="openEdit()">
            <el-icon><Plus /></el-icon>
            新建
          </el-button>
        </div>
      </template>

      <ele-table :loading="loading" :data="data" :columns="columns" row-key="id" stripe>
        <template #extension="{ row }">
          <code class="ext-code">.{{ row.extension }}</code>
        </template>
        <template #action="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定要删除此映射吗?" @confirm="remove(row)">
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
      :title="editRow?.id ? '编辑语言映射' : '新建语言映射'"
      width="440px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="editForm" :rules="rules" label-width="90px">
        <el-form-item label="文件扩展名" prop="extension">
          <el-input v-model="editForm.extension" placeholder="如: tsx, vue, jsx" clearable />
          <div class="form-tip">输入扩展名，不需要带点</div>
        </el-form-item>
        <el-form-item label="映射语言" prop="language">
          <el-input v-model="editForm.language" placeholder="如: TypeScript, Vue" clearable />
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
    pageLanguageMappings,
    addLanguageMapping,
    updateLanguageMapping,
    removeLanguageMapping,
    type WakapiLanguageMapping
  } from '@/api/wakapi';

  defineOptions({ name: 'WakapiLanguageMappingConfig' });

  const loading = ref(false);
  const saving = ref(false);
  const data = ref<WakapiLanguageMapping[]>([]);
  const total = ref(0);
  const pager = reactive({ page: 1, limit: 20 });

  const editVisible = ref(false);
  const editRow = ref<WakapiLanguageMapping | null>(null);
  const editForm = reactive({ extension: '', language: '' });
  const formRef = ref<FormInstance>();

  const rules: FormRules = {
    extension: [{ required: true, message: '请输入扩展名', trigger: 'blur' }],
    language: [{ required: true, message: '请输入语言名称', trigger: 'blur' }]
  };

  const columns = [
    { columnKey: 'extension', label: '扩展名', width: 120, slot: true },
    { columnKey: 'language', label: '映射语言', minWidth: 150, prop: 'language' },
    { columnKey: 'createTime', label: '创建时间', width: 170, prop: 'createTime' },
    { columnKey: 'action', label: '操作', width: 120, slot: true, align: 'center' }
  ];

  const query = async () => {
    loading.value = true;
    try {
      const res = await pageLanguageMappings({ page: pager.page, limit: pager.limit });
      data.value = res?.list ?? [];
      total.value = res?.count ?? 0;
    } catch {
      data.value = [];
      total.value = 0;
    } finally {
      loading.value = false;
    }
  };

  const openEdit = (row?: WakapiLanguageMapping) => {
    editRow.value = row ?? null;
    editForm.extension = row?.extension ?? '';
    editForm.language = row?.language ?? '';
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
        await updateLanguageMapping({ ...payload, id: editRow.value.id });
        EleMessage.success('修改成功');
      } else {
        await addLanguageMapping(payload);
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

  const remove = async (row: WakapiLanguageMapping) => {
    if (!row.id) return;
    try {
      await removeLanguageMapping(row.id);
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
  .ext-code {
    padding: 2px 6px;
    background: var(--el-fill-color-light);
    border-radius: 4px;
    font-size: 12px;
    font-family: 'SF Mono', Consolas, monospace;
    color: var(--el-color-primary);
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
