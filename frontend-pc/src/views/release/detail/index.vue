<template>
  <ele-page>
    <!-- 头部 -->
    <ele-card style="margin-bottom: 16px">
      <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 12px">
        <div style="display: flex; align-items: center; gap: 12px">
          <el-button @click="$router.back()">返回</el-button>
          <div>
            <div style="font-size: 18px; font-weight: 600">{{ release.name || '发布详情' }}</div>
            <div style="font-size: 13px; color: #999; margin-top: 2px">
              <el-tag :type="STATUS_TAG[release.status]" size="small">
                {{ STATUS_LABEL[release.status] || '未知' }}
              </el-tag>
              <span v-if="release.releaseType" style="margin-left: 8px">
                {{ RELEASE_TYPE_LABEL[release.releaseType] }}
              </span>
              <span v-if="release.gitTag" style="margin-left: 8px">Tag: {{ release.gitTag }}</span>
            </div>
          </div>
        </div>
        <div style="display: flex; gap: 8px">
          <el-button v-if="release.status === 0 || release.status === 1" type="success" @click="handlePublish">发布</el-button>
          <el-button v-if="release.status === 0 || release.status === 3" type="warning" @click="handleArchive">归档</el-button>
          <el-button v-if="release.status === 4" type="primary" @click="handleRestore">恢复</el-button>
          <el-button v-if="release.status === 0" type="primary" @click="openEditDialog">编辑</el-button>
        </div>
      </div>
    </ele-card>

    <!-- Tab 页签 -->
    <ele-card>
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="info">
          <div style="padding: 8px 0; max-width: 640px">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="发布名称">{{ release.name }}</el-descriptions-item>
              <el-descriptions-item label="发布类型">{{ RELEASE_TYPE_LABEL[release.releaseType] || '-' }}</el-descriptions-item>
              <el-descriptions-item label="计划日期">{{ release.releaseDate || '-' }}</el-descriptions-item>
              <el-descriptions-item label="构建号">{{ release.buildNumber || '-' }}</el-descriptions-item>
              <el-descriptions-item label="Git Tag">{{ release.gitTag || '-' }}</el-descriptions-item>
              <el-descriptions-item label="Git 分支">{{ release.gitBranch || '-' }}</el-descriptions-item>
              <el-descriptions-item label="产物数量">{{ release.artifactCount ?? '-' }}</el-descriptions-item>
              <el-descriptions-item label="关联问题">{{ release.issueCount ?? '-' }}</el-descriptions-item>
            </el-descriptions>
            <div v-if="release.description" style="margin-top: 16px">
              <div style="font-weight: 500; margin-bottom: 6px">描述</div>
              <el-input type="textarea" :model-value="release.description" :rows="3" readonly />
            </div>
            <div v-if="release.releaseNotes" style="margin-top: 16px">
              <div style="font-weight: 500; margin-bottom: 6px">发布说明</div>
              <el-input type="textarea" :model-value="release.releaseNotes" :rows="4" readonly />
            </div>
          </div>
        </el-tab-pane>

        <!-- 发布产物 -->
        <el-tab-pane label="发布产物" name="artifacts">
          <div style="margin-bottom: 12px">
            <el-button type="primary" @click="openArtifactDialog">上传产物</el-button>
          </div>
          <el-table :data="artifacts" stripe row-key="releaseArtifactId" v-loading="artifactLoading">
            <el-table-column prop="artifactName" label="产物名称" min-width="160" />
            <el-table-column prop="artifactType" label="类型" width="100" />
            <el-table-column prop="fileName" label="文件名" min-width="160" show-overflow-tooltip />
            <el-table-column label="大小" width="100">
              <template #default="{ row }">{{ row.fileSize ? formatSize(row.fileSize) : '-' }}</template>
            </el-table-column>
            <el-table-column prop="fileHash" label="文件哈希" width="180" show-overflow-tooltip />
            <el-table-column prop="downloadUrl" label="下载地址" min-width="200" show-overflow-tooltip>
              <template #default="{ row }">
                <el-link v-if="row.downloadUrl" type="primary" :href="row.downloadUrl" target="_blank" underline="never">
                  下载
                </el-link>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="uploadedByName" label="上传人" width="100" />
            <el-table-column prop="uploadedAt" label="上传时间" width="160" />
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-link type="danger" underline="never" @click="handleDeleteArtifact(row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 关联问题 -->
        <el-tab-pane label="关联问题" name="issues">
          <div style="margin-bottom: 12px">
            <el-button type="primary" @click="openIssueDialog">关联问题</el-button>
          </div>
          <el-table :data="issues" stripe row-key="releaseIssueId" v-loading="issueLoading">
            <el-table-column prop="taskId" label="任务ID" width="100" />
            <el-table-column prop="category" label="类别" width="100">
              <template #default="{ row }">{{ ISSUE_CATEGORY_LABEL[row.category] || row.category }}</template>
            </el-table-column>
            <el-table-column prop="notes" label="备注" min-width="200" show-overflow-tooltip />
            <el-table-column prop="addedByName" label="添加人" width="100" />
            <el-table-column prop="addedAt" label="添加时间" width="160" />
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-link type="danger" underline="never" @click="handleDeleteIssue(row)">移除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </ele-card>

    <!-- 编辑发布弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑发布" width="560px" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="发布名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入发布名称" />
        </el-form-item>
        <el-form-item label="发布类型">
          <el-select v-model="editForm.releaseType" style="width: 100%">
            <el-option v-for="(label, key) in RELEASE_TYPE_LABEL" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划日期">
          <el-date-picker v-model="editForm.releaseDate" type="date" placeholder="选择日期" style="width: 100%" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Git Tag">
          <el-input v-model="editForm.gitTag" placeholder="如 v0.2.0" />
        </el-form-item>
        <el-form-item label="Git 分支">
          <el-input v-model="editForm.gitBranch" placeholder="如 main" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 上传产物弹窗 -->
    <el-dialog v-model="artifactDialogVisible" title="上传产物" width="480px" destroy-on-close>
      <el-form ref="artifactFormRef" :model="artifactForm" label-width="100px">
        <el-form-item label="产物名称" prop="artifactName">
          <el-input v-model="artifactForm.artifactName" placeholder="请输入产物名称" />
        </el-form-item>
        <el-form-item label="产物类型">
          <el-select v-model="artifactForm.artifactType" style="width: 100%">
            <el-option label="JAR" value="jar" />
            <el-option label="Docker镜像" value="docker" />
            <el-option label="安装包" value="installer" />
            <el-option label="压缩包" value="archive" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件名">
          <el-input v-model="artifactForm.fileName" placeholder="物理文件名" />
        </el-form-item>
        <el-form-item label="下载地址">
          <el-input v-model="artifactForm.downloadUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="文件哈希">
          <el-input v-model="artifactForm.fileHash" placeholder="SHA256" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="artifactDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleArtifactSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 关联问题弹窗 -->
    <el-dialog v-model="issueDialogVisible" title="关联问题" width="480px" destroy-on-close>
      <el-form ref="issueFormRef" :model="issueForm" label-width="100px">
        <el-form-item label="类别" prop="category">
          <el-select v-model="issueForm.category" style="width: 100%">
            <el-option v-for="(label, key) in ISSUE_CATEGORY_LABEL" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务ID">
          <el-input-number v-model="issueForm.taskId" :min="1" style="width: 100%" placeholder="关联的任务ID" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="issueForm.notes" type="textarea" :rows="3" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="issueDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleIssueSubmit">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import { ElMessageBox } from 'element-plus';
  import { EleMessage } from 'ele-admin-plus';
  import {
    getReleasePlan,
    updateReleasePlan,
    publishReleasePlan,
    archiveReleasePlan,
    restoreReleasePlan,
    listArtifacts,
    addArtifact,
    removeArtifact,
    listIssues,
    addIssue,
    removeIssue,
    STATUS_LABEL,
    STATUS_TAG,
    RELEASE_TYPE_LABEL,
    ISSUE_CATEGORY_LABEL
  } from '@/api/release';

  const route = useRoute();
  const releasePlanId = Number(route.params.id);
  const activeTab = ref('info');
  const loading = ref(false);
  const submitLoading = ref(false);
  const artifactLoading = ref(false);
  const issueLoading = ref(false);
  const release = ref({});
  const artifacts = ref([]);
  const issues = ref([]);

  const editDialogVisible = ref(false);
  const editFormRef = ref(null);
  const editForm = reactive({
    name: '',
    description: '',
    releaseType: 1,
    releaseDate: '',
    gitTag: '',
    gitBranch: ''
  });
  const editRules = {
    name: [{ required: true, message: '请输入发布名称', trigger: 'blur' }]
  };

  const artifactDialogVisible = ref(false);
  const artifactFormRef = ref(null);
  const artifactForm = reactive({
    artifactName: '',
    artifactType: 'jar',
    fileName: '',
    filePath: '',
    fileHash: '',
    downloadUrl: ''
  });

  const issueDialogVisible = ref(false);
  const issueFormRef = ref(null);
  const issueForm = reactive({
    category: 1,
    taskId: undefined,
    notes: ''
  });

  function formatSize(bytes) {
    if (!bytes) return '-';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    if (bytes < 1024 * 1024 * 1024) return (bytes / 1024 / 1024).toFixed(1) + ' MB';
    return (bytes / 1024 / 1024 / 1024).toFixed(1) + ' GB';
  }

  async function fetchRelease() {
    loading.value = true;
    try {
      release.value = await getReleasePlan(releasePlanId);
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      loading.value = false;
    }
  }

  async function fetchArtifacts() {
    artifactLoading.value = true;
    try {
      artifacts.value = await listArtifacts(releasePlanId);
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      artifactLoading.value = false;
    }
  }

  async function fetchIssues() {
    issueLoading.value = true;
    try {
      issues.value = await listIssues(releasePlanId);
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      issueLoading.value = false;
    }
  }

  function openEditDialog() {
    Object.assign(editForm, {
      name: release.value.name,
      description: release.value.description,
      releaseType: release.value.releaseType,
      releaseDate: release.value.releaseDate,
      gitTag: release.value.gitTag,
      gitBranch: release.value.gitBranch
    });
    editDialogVisible.value = true;
  }

  async function handleEditSubmit() {
    const valid = await editFormRef.value?.validate().catch(() => false);
    if (!valid) return;
    submitLoading.value = true;
    try {
      await updateReleasePlan({ releasePlanId, ...editForm });
      EleMessage.success({ message: '修改成功', plain: true });
      editDialogVisible.value = false;
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      submitLoading.value = false;
    }
  }

  async function handlePublish() {
    await ElMessageBox.confirm('确认发布此版本？', '发布确认').catch(() => null);
    if (!confirm) return;
    try {
      await publishReleasePlan(releasePlanId);
      EleMessage.success({ message: '发布成功', plain: true });
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    }
  }

  async function handleArchive() {
    await ElMessageBox.confirm('确认归档此版本？', '归档确认').catch(() => null);
    try {
      await archiveReleasePlan(releasePlanId);
      EleMessage.success({ message: '归档成功', plain: true });
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    }
  }

  async function handleRestore() {
    await ElMessageBox.confirm('确认恢复此版本？', '恢复确认').catch(() => null);
    try {
      await restoreReleasePlan(releasePlanId);
      EleMessage.success({ message: '恢复成功', plain: true });
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    }
  }

  function openArtifactDialog() {
    Object.assign(artifactForm, { artifactName: '', artifactType: 'jar', fileName: '', filePath: '', fileHash: '', downloadUrl: '' });
    artifactDialogVisible.value = true;
  }

  async function handleArtifactSubmit() {
    if (!artifactForm.artifactName) {
      EleMessage.error({ message: '请输入产物名称', plain: true });
      return;
    }
    submitLoading.value = true;
    try {
      await addArtifact({ releasePlanId, ...artifactForm });
      EleMessage.success({ message: '上传成功', plain: true });
      artifactDialogVisible.value = false;
      fetchArtifacts();
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      submitLoading.value = false;
    }
  }

  async function handleDeleteArtifact(row) {
    await ElMessageBox.confirm('确认删除该产物？', '确认删除').catch(() => null);
    try {
      await removeArtifact(row.releaseArtifactId);
      EleMessage.success({ message: '删除成功', plain: true });
      fetchArtifacts();
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    }
  }

  function openIssueDialog() {
    Object.assign(issueForm, { category: 1, taskId: undefined, notes: '' });
    issueDialogVisible.value = true;
  }

  async function handleIssueSubmit() {
    if (!issueForm.taskId) {
      EleMessage.error({ message: '请输入任务ID', plain: true });
      return;
    }
    submitLoading.value = true;
    try {
      await addIssue({ releasePlanId, ...issueForm });
      EleMessage.success({ message: '关联成功', plain: true });
      issueDialogVisible.value = false;
      fetchIssues();
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      submitLoading.value = false;
    }
  }

  async function handleDeleteIssue(row) {
    await ElMessageBox.confirm('确认移除该关联？', '确认移除').catch(() => null);
    try {
      await removeIssue(row.releaseIssueId);
      EleMessage.success({ message: '移除成功', plain: true });
      fetchIssues();
      fetchRelease();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    }
  }

  onMounted(() => {
    fetchRelease();
  });
</script>
