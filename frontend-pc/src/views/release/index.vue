<template>
  <ele-page>
    <!-- 搜索栏 -->
    <ele-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="发布名称">
          <el-input
            v-model="query.name"
            clearable
            placeholder="搜索发布名称"
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option v-for="(label, key) in STATUS_LABEL" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="openAddDialog">新建发布</el-button>
        </el-form-item>
      </el-form>
    </ele-card>

    <!-- 发布列表 -->
    <ele-card>
      <el-table v-loading="loading" :data="list" stripe row-key="releasePlanId" style="width: 100%">
        <el-table-column prop="name" label="发布名称" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" underline="never" @click="goDetail(row)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="STATUS_TAG[row.status]" size="small">{{ STATUS_LABEL[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="releaseType" label="发布类型" width="100">
          <template #default="{ row }">
            {{ RELEASE_TYPE_LABEL[row.releaseType] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="releaseDate" label="计划日期" width="120" />
        <el-table-column prop="gitTag" label="Git Tag" width="140" show-overflow-tooltip />
        <el-table-column prop="gitBranch" label="分支" width="120" show-overflow-tooltip />
        <el-table-column prop="artifactCount" label="产物数" width="80" align="center" />
        <el-table-column prop="issueCount" label="关联问题" width="90" align="center" />
        <el-table-column prop="buildNumber" label="构建号" width="80" align="center" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-link type="primary" underline="never" @click="goDetail(row)">详情</el-link>
            <el-divider direction="vertical" />
            <el-link
              v-if="row.status === 0 || row.status === 1"
              type="success"
              underline="never"
              @click="handlePublish(row)"
            >
              发布
            </el-link>
            <el-link
              v-if="row.status === 0"
              type="primary"
              underline="never"
              @click="openEditDialog(row)"
            >
              编辑
            </el-link>
            <el-divider v-if="row.status === 0" direction="vertical" />
            <el-link
              v-if="row.status === 0 || row.status === 3"
              type="warning"
              underline="never"
              @click="handleArchive(row)"
            >
              归档
            </el-link>
            <el-link
              v-if="row.status === 4"
              type="primary"
              underline="never"
              @click="handleRestore(row)"
            >
              恢复
            </el-link>
            <el-divider direction="vertical" />
            <el-link type="danger" underline="never" @click="handleDelete(row)">删除</el-link>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="display: flex; justify-content: flex-end; margin-top: 12px">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @change="fetchList"
        />
      </div>
    </ele-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="formData.releasePlanId ? '编辑发布' : '新建发布'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="发布名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入发布名称" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="formData.projectId" filterable placeholder="请选择项目" style="width: 100%">
            <el-option
              v-for="p in projectOptions"
              :key="p.projectId"
              :label="p.name"
              :value="p.projectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发布类型">
          <el-select v-model="formData.releaseType" style="width: 100%">
            <el-option
              v-for="(label, key) in RELEASE_TYPE_LABEL"
              :key="key"
              :label="label"
              :value="Number(key)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划日期">
          <el-date-picker
            v-model="formData.releaseDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="Git Tag">
          <el-input v-model="formData.gitTag" placeholder="如 v0.2.0" />
        </el-form-item>
        <el-form-item label="Git 分支">
          <el-input v-model="formData.gitBranch" placeholder="如 main" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="发布说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessageBox } from 'element-plus';
  import { EleMessage } from 'ele-admin-plus';
  import {
    pageReleasePlans,
    addReleasePlan,
    updateReleasePlan,
    removeReleasePlan,
    publishReleasePlan,
    archiveReleasePlan,
    restoreReleasePlan,
    STATUS_LABEL,
    STATUS_TAG,
    RELEASE_TYPE_LABEL
  } from '@/api/release';
  import { pageProjects } from '@/api/project';

  const router = useRouter();
  const loading = ref(false);
  const submitLoading = ref(false);
  const list = ref([]);
  const total = ref(0);
  const dialogVisible = ref(false);
  const formRef = ref(null);
  const projectOptions = ref([]);

  const query = reactive({
    pageNum: 1,
    pageSize: 20,
    name: undefined,
    status: undefined
  });

  const formData = reactive({
    releasePlanId: undefined,
    projectId: undefined,
    name: '',
    description: '',
    releaseDate: undefined,
    releaseType: 1,
    gitTag: '',
    gitBranch: '',
    isDraft: 0
  });

  const rules = {
    name: [{ required: true, message: '请输入发布名称', trigger: 'blur' }],
    projectId: [{ required: true, message: '请选择所属项目', trigger: 'change' }]
  };

  async function fetchList() {
    loading.value = true;
    try {
      const res = await pageReleasePlans(query);
      list.value = res.rows;
      total.value = res.total;
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      loading.value = false;
    }
  }

  async function loadProjects() {
    try {
      const res = await pageProjects({ pageNum: 1, pageSize: 100, status: 1 });
      projectOptions.value = res.rows;
    } catch (e) {
      // ignore
    }
  }

  function handleSearch() {
    query.pageNum = 1;
    fetchList();
  }

  function handleReset() {
    query.name = undefined;
    query.status = undefined;
    query.pageNum = 1;
    fetchList();
  }

  function openAddDialog() {
    Object.assign(formData, {
      releasePlanId: undefined,
      projectId: undefined,
      name: '',
      description: '',
      releaseDate: undefined,
      releaseType: 1,
      gitTag: '',
      gitBranch: '',
      isDraft: 0
    });
    dialogVisible.value = true;
  }

  function openEditDialog(row) {
    Object.assign(formData, {
      releasePlanId: row.releasePlanId,
      projectId: row.projectId,
      name: row.name,
      description: row.description,
      releaseDate: row.releaseDate,
      releaseType: row.releaseType,
      gitTag: row.gitTag,
      gitBranch: row.gitBranch
    });
    dialogVisible.value = true;
  }

  function handleSubmit() {
    formRef.value?.validate(async (valid) => {
      if (!valid) return;
      submitLoading.value = true;
      try {
        if (formData.releasePlanId) {
          await updateReleasePlan(formData);
          EleMessage.success({ message: '修改成功', plain: true });
        } else {
          await addReleasePlan(formData);
          EleMessage.success({ message: '创建成功', plain: true });
        }
        dialogVisible.value = false;
        fetchList();
      } catch (e) {
        EleMessage.error({ message: e.message, plain: true });
      } finally {
        submitLoading.value = false;
      }
    });
  }

  function goDetail(row) {
    router.push(`/release/detail/${row.releasePlanId}`);
  }

  function handlePublish(row) {
    ElMessageBox.confirm(`确认发布「${row.name}」？`, '确认发布').then(async () => {
      try {
        await publishReleasePlan(row.releasePlanId);
        EleMessage.success({ message: '发布成功', plain: true });
        fetchList();
      } catch (e) {
        EleMessage.error({ message: e.message, plain: true });
      }
    }).catch(() => {});
  }

  function handleArchive(row) {
    ElMessageBox.confirm(`确认归档「${row.name}」？`, '确认归档').then(async () => {
      try {
        await archiveReleasePlan(row.releasePlanId);
        EleMessage.success({ message: '归档成功', plain: true });
        fetchList();
      } catch (e) {
        EleMessage.error({ message: e.message, plain: true });
      }
    }).catch(() => {});
  }

  function handleRestore(row) {
    ElMessageBox.confirm(`确认恢复「${row.name}」？`, '确认恢复').then(async () => {
      try {
        await restoreReleasePlan(row.releasePlanId);
        EleMessage.success({ message: '恢复成功', plain: true });
        fetchList();
      } catch (e) {
        EleMessage.error({ message: e.message, plain: true });
      }
    }).catch(() => {});
  }

  function handleDelete(row) {
    ElMessageBox.confirm(`确认删除发布「${row.name}」？此操作不可恢复。`, '确认删除').then(async () => {
      try {
        await removeReleasePlan(row.releasePlanId);
        EleMessage.success({ message: '删除成功', plain: true });
        fetchList();
      } catch (e) {
        EleMessage.error({ message: e.message, plain: true });
      }
    }).catch(() => {});
  }

  onMounted(() => {
    fetchList();
    loadProjects();
  });
</script>
