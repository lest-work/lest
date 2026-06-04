<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useProjectStore } from '@/stores/project';
import { projectApi } from '@/api/project';

const router = useRouter();
const projectStore = useProjectStore();
const filter = ref<'active' | 'archived'>('active');

const dialogVisible = ref(false);
const creating = ref(false);
const createFormRef = ref();
const createForm = ref({ name: '', description: '', template: 'agile' });
const createRules = {
  name: [
    { required: true, message: '项目名称不能为空', trigger: 'blur' },
    { max: 100, message: '不能超过100个字符', trigger: 'blur' },
  ],
};

onMounted(() => {
  projectStore.fetchProjects({ status: filter.value });
});

function switchFilter(val: 'active' | 'archived') {
  filter.value = val;
  projectStore.fetchProjects({ status: val });
}

function goToProject(id: number) {
  router.push(`/project/${id}/tasks`);
}

async function handleCreate() {
  await createFormRef.value?.validate();
  creating.value = true;
  try {
    await projectApi.create({ ...createForm.value });
    ElMessage.success('项目创建成功');
    dialogVisible.value = false;
    createForm.value = { name: '', description: '', template: 'agile' };
    projectStore.fetchProjects({ status: filter.value });
  } finally {
    creating.value = false;
  }
}

function openCreate() {
  createForm.value = { name: '', description: '', template: 'agile' };
  dialogVisible.value = true;
}
</script>

<template>
  <div class="project-list-view">
    <div class="page-header">
      <h2 class="page-title">我的项目</h2>
      <el-button type="primary" @click="openCreate">
        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" style="margin-right:5px">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建项目
      </el-button>
    </div>

    <div class="filter-tabs">
      <el-button
        :type="filter === 'active' ? 'primary' : 'default'"
        round
        size="small"
        @click="switchFilter('active')"
      >
        进行中
        <el-badge v-if="filter === 'active' && projectStore.projects.length" :value="projectStore.projects.length" class="tab-badge" />
      </el-button>
      <el-button
        :type="filter === 'archived' ? 'primary' : 'default'"
        round
        size="small"
        @click="switchFilter('archived')"
      >
        已归档
      </el-button>
    </div>

    <!-- Loading skeleton -->
    <div v-if="projectStore.loading" class="project-grid">
      <el-skeleton v-for="i in 6" :key="i" animated class="skeleton-card">
        <template #template>
          <el-skeleton-item variant="rect" style="height: 36px; width: 36px; border-radius: 8px; margin-bottom: 12px" />
          <el-skeleton-item variant="h3" style="width: 60%; margin-bottom: 8px" />
          <el-skeleton-item variant="text" style="width: 40%; margin-bottom: 8px" />
          <el-skeleton-item variant="text" style="width: 85%" />
        </template>
      </el-skeleton>
    </div>

    <!-- 空状态 -->
    <el-empty v-else-if="projectStore.projects.length === 0" description="暂无项目，创建你的第一个敏捷项目">
      <el-button type="primary" @click="openCreate">新建项目</el-button>
    </el-empty>

    <!-- 项目网格 -->
    <div v-else class="project-grid">
      <div
        v-for="project in projectStore.projects"
        :key="project.id"
        class="project-card"
        @click="goToProject(project.id ?? 0)"
      >
        <div class="project-card-top">
          <span
            class="project-icon"
            :style="{ background: (project.color || 'var(--color-primary)') + '22', color: project.color || 'var(--color-primary)' }"
          >
            {{ project.name?.charAt(0).toUpperCase() || 'P' }}
          </span>
          <el-tag size="small" type="info" effect="plain">{{ project.template || 'agile' }}</el-tag>
        </div>
        <div class="project-name">{{ project.name }}</div>
        <div class="project-key">{{ project.projectKey || ('P' + (project.projectId || project.id)) }}</div>
        <div class="project-desc">{{ project.description || '暂无描述' }}</div>
        <div class="project-footer">
          <span class="project-meta-item">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            {{ project.memberCount || 0 }}
          </span>
          <span class="project-meta-item">
            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 11l3 3L22 4"/>
              <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
            </svg>
            {{ project.taskCount || 0 }}
          </span>
        </div>
      </div>
    </div>

    <!-- 新建项目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新建项目"
      width="480px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-position="top"
        @submit.prevent="handleCreate"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input
            v-model="createForm.name"
            placeholder="如：LEST 平台开发"
            maxlength="100"
            show-word-limit
            autofocus
            @keydown.enter="handleCreate"
          />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="简单描述项目目标（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="项目模板">
          <el-radio-group v-model="createForm.template">
            <el-radio-button value="agile">Scrum</el-radio-button>
            <el-radio-button value="kanban">看板</el-radio-button>
            <el-radio-button value="waterfall">瀑布</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreate">创建项目</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.project-list-view {
  padding: var(--space-6) var(--space-5);
  font-family: var(--font-primary);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-5);
}

.tab-badge {
  margin-left: 4px;
}

.skeleton-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--space-4);
}

.project-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  cursor: pointer;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);

  &:hover {
    border-color: var(--color-primary);
    box-shadow: var(--shadow-card);
  }
}

.project-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-3);
}

.project-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  font-size: var(--text-md);
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-1);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.project-key {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-family: var(--font-mono);
  margin-bottom: var(--space-2);
}

.project-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: var(--space-3);
}

.project-footer {
  display: flex;
  gap: var(--space-4);
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-light);
}

.project-meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
  color: var(--text-muted);
}
</style>
