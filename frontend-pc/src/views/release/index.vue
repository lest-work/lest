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

    <!-- 发布卡片网格 -->
    <ele-card>
      <ele-loading :loading="loading" style="min-height: 200px">
        <div v-if="list.length === 0 && !loading" style="text-align: center; padding: 60px 0; color: #999">
          暂无发布计划
        </div>
        <el-row :gutter="16">
          <el-col
            v-for="item in list"
            :key="item.releasePlanId"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            style="margin-bottom: 16px"
          >
            <el-card
              shadow="hover"
              style="cursor: pointer; min-height: 160px; display: flex; flex-direction: column"
              @click="goDetail(item)"
            >
              <!-- 头部：名称 + 状态标签 + 下拉菜单 -->
              <div style="display: flex; justify-content: space-between; align-items: flex-start">
                <div style="flex: 1; overflow: hidden">
                  <div style="font-size: 15px; font-weight: 600; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                    {{ item.name }}
                  </div>
                  <div style="display: flex; flex-wrap: wrap; gap: 4px">
                    <el-tag :type="STATUS_TAG[item.status]" size="small">
                      {{ STATUS_LABEL[item.status] || item.status }}
                    </el-tag>
                    <el-tag type="primary" size="small">
                      {{ RELEASE_TYPE_LABEL[item.releaseType] || '-' }}
                    </el-tag>
                  </div>
                </div>
                <el-dropdown @click.stop>
                  <el-icon style="cursor: pointer; padding: 4px; flex-shrink: 0"><IconElMore /></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click.stop="openEditDialog(item)">编辑</el-dropdown-item>
                      <el-dropdown-item
                        v-if="item.status === 0 || item.status === 1"
                        @click.stop="handlePublish(item)"
                      >
                        发布
                      </el-dropdown-item>
                      <el-dropdown-item
                        v-if="item.status === 0 || item.status === 3"
                        @click.stop="handleArchive(item)"
                      >
                        归档
                      </el-dropdown-item>
                      <el-dropdown-item
                        v-if="item.status === 4"
                        @click.stop="handleRestore(item)"
                      >
                        恢复
                      </el-dropdown-item>
                      <el-dropdown-item
                        style="color: #f56c6c"
                        @click.stop="handleDelete(item)"
                      >
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>

              <!-- 描述 -->
              <div
                style="
                  flex: 1;
                  margin-top: 10px;
                  font-size: 13px;
                  color: #666;
                  overflow: hidden;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                "
              >
                {{ item.description || '暂无描述' }}
              </div>

              <!-- 底部信息 -->
              <div style="margin-top: 10px; font-size: 12px; color: #999; display: flex; gap: 12px; flex-wrap: wrap">
                <span v-if="item.releaseDate">计划日期：{{ item.releaseDate }}</span>
                <span v-if="item.gitTag">Tag：{{ item.gitTag }}</span>
                <span v-if="item.artifactCount">产物：{{ item.artifactCount }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div v-if="total > 0" style="display: flex; justify-content: flex-end; margin-top: 8px">
          <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            @change="fetchList"
          />
        </div>
      </ele-loading>
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
    pageSize: 12,
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
