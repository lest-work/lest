<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { releaseApi, type ReleasePlan } from '@/api/release';
import StatusChip from '@/components/common/StatusChip.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

const route = useRoute();
const projectId = computed(() => Number(route.params.id));

const plans = ref<ReleasePlan[]>([]);
const loading = ref(false);
const saving = ref(false);
const showForm = ref(false);
const editPlan = ref<Partial<ReleasePlan>>({});

const STATUS_CONFIG: Record<string, { label: string; color: string }> = {
  '0': { label: '草稿',   color: 'var(--status-todo)' },
  '1': { label: '已发布', color: 'var(--status-completed)' },
  '2': { label: '已归档', color: 'var(--text-muted)' },
  '3': { label: '构建中', color: 'var(--status-in-progress)' },
};

onMounted(async () => { await fetchPlans(); });

async function fetchPlans() {
  loading.value = true;
  try { const r = await releaseApi.plan.list({ projectId: projectId.value }); plans.value = r.data?.records || []; }
  catch { plans.value = []; }
  finally { loading.value = false; }
}

function openCreateForm() {
  editPlan.value = { name: '', version: '', description: '', plannedDate: '', projectId: projectId.value };
  showForm.value = true;
}

function openEditForm(plan: ReleasePlan) {
  editPlan.value = { ...plan };
  showForm.value = true;
}

async function savePlan() {
  if (!editPlan.value.name) return;
  saving.value = true;
  try {
    if (editPlan.value.releasePlanId) await releaseApi.plan.update(editPlan.value);
    else await releaseApi.plan.create({ ...editPlan.value, projectId: projectId.value });
    showForm.value = false;
    await fetchPlans();
  } finally { saving.value = false; }
}

async function publishPlan(id: number) { await releaseApi.plan.publish(id); await fetchPlans(); }
async function archivePlan(id: number) { await releaseApi.plan.archive(id); await fetchPlans(); }
async function restorePlan(id: number) { await releaseApi.plan.restore(id); await fetchPlans(); }
async function startBuild(id: number) { await releaseApi.plan.startBuild(id); await fetchPlans(); }
async function deletePlan(id: number) { await releaseApi.plan.delete(id); await fetchPlans(); }

function getStatusInfo(status?: string) {
  return STATUS_CONFIG[status || '0'] || STATUS_CONFIG['0'];
}
</script>

<template>
  <div class="release-page">
    <div class="page-header">
      <h2 class="page-title">发布计划</h2>
      <button class="create-btn" @click="openCreateForm">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建发布计划
      </button>
    </div>

    <!-- Loading -->
    <LoadingSpinner v-if="loading" />

    <!-- 空状态 -->
    <EmptyState
      v-else-if="plans.length === 0"
      title="暂无发布计划"
      description="创建你的第一个发布计划"
      cta="新建发布计划"
      icon="release"
      @cta="openCreateForm"
    />

    <!-- 计划列表 -->
    <div v-else class="plan-list">
      <div v-for="plan in plans" :key="plan.releasePlanId" class="plan-card">
        <div class="plan-header">
          <div class="plan-title-row">
            <span class="plan-name">{{ plan.name }}</span>
            <StatusChip
              :label="getStatusInfo(plan.status).label"
              :color="getStatusInfo(plan.status).color"
              :status="plan.status || '0'"
              size="sm"
            />
          </div>
          <div class="plan-meta">
            <span class="meta-version">v{{ plan.version }}</span>
            <span v-if="plan.plannedDate" class="meta-date">计划日期: {{ plan.plannedDate }}</span>
          </div>
        </div>

        <div v-if="plan.description" class="plan-body">
          <p class="plan-desc">{{ plan.description }}</p>
        </div>

        <div class="plan-actions">
          <button class="action-link" @click="openEditForm(plan)">编辑</button>
          <button v-if="plan.status === '0'" class="action-link primary" @click="publishPlan(plan.releasePlanId!)">发布</button>
          <button v-if="plan.status === '1'" class="action-link primary" @click="startBuild(plan.releasePlanId!)">构建</button>
          <button v-if="plan.status === '1'" class="action-link" @click="archivePlan(plan.releasePlanId!)">归档</button>
          <button v-if="plan.status === '2'" class="action-link" @click="restorePlan(plan.releasePlanId!)">恢复</button>
          <button class="action-link danger" @click="deletePlan(plan.releasePlanId!)">删除</button>
        </div>
      </div>
    </div>

    <!-- 表单弹窗 -->
    <Teleport to="body">
      <div v-if="showForm" class="form-overlay" @click.self="showForm = false">
        <div class="form-panel">
          <div class="form-header">
            <h3 class="form-title">{{ editPlan.releasePlanId ? '编辑发布计划' : '新建发布计划' }}</h3>
            <button class="form-close" @click="showForm = false">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          <div class="form-body">
            <div class="form-row">
              <label class="form-label">名称 *</label>
              <input v-model="editPlan.name" class="form-input" placeholder="如：v1.0.0" />
            </div>
            <div class="form-row">
              <label class="form-label">版本号</label>
              <input v-model="editPlan.version" class="form-input" placeholder="如：1.0.0" />
            </div>
            <div class="form-row">
              <label class="form-label">描述</label>
              <textarea v-model="editPlan.description" class="form-textarea" placeholder="版本说明..." rows="3"/>
            </div>
            <div class="form-row">
              <label class="form-label">计划日期</label>
              <input v-model="editPlan.plannedDate" type="date" class="form-input" />
            </div>
          </div>
          <div class="form-footer">
            <button class="form-btn secondary" @click="showForm = false">取消</button>
            <button class="form-btn primary" :disabled="saving || !editPlan.name" @click="savePlan">保存</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped lang="scss">
.release-page {
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

.create-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  height: 36px;
  padding: 0 var(--space-4);
  border: none;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast);
  &:hover { background: var(--color-primary-hover); }
}

.plan-list { display: flex; flex-direction: column; gap: var(--space-3); }

.plan-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
  transition: border-color var(--transition-fast);

  &:hover { border-color: var(--border-focus); }
}

.plan-header { margin-bottom: var(--space-2); }

.plan-title-row {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin-bottom: var(--space-1);
}

.plan-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.plan-meta {
  display: flex;
  gap: var(--space-4);
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.meta-version { font-family: var(--font-mono); }

.plan-body { margin-bottom: var(--space-2); }

.plan-desc {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  margin: 0;
  line-height: var(--leading-normal);
}

.plan-actions {
  display: flex;
  gap: var(--space-1);
  flex-wrap: wrap;
  padding-top: var(--space-3);
  border-top: 1px solid var(--border-light);
}

.action-link {
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: color var(--transition-fast), background var(--transition-fast);

  &:hover { color: var(--text-primary); background: var(--bg-hover); }
  &.primary { color: var(--color-primary); &:hover { background: var(--color-primary-light); } }
  &.danger { color: var(--color-danger); &:hover { background: rgba(239,68,68,0.08); } }
}

/* Form Overlay */
.form-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(2px);
}

.form-panel {
  width: 480px;
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-modal);
  overflow: hidden;
}

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-color);
}

.form-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.form-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: background var(--transition-fast), color var(--transition-fast);
  &:hover { background: var(--bg-hover); color: var(--text-primary); }
}

.form-body {
  padding: var(--space-5);
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: var(--space-1);
}

.form-label {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.form-input {
  height: 36px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 0 var(--space-3);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  color: var(--text-primary);
  background: var(--bg-primary);
  outline: none;
  transition: border-color var(--transition-fast);
  &:focus { border-color: var(--color-primary); }
}

.form-textarea {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: var(--space-3);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  color: var(--text-primary);
  background: var(--bg-primary);
  resize: vertical;
  outline: none;
  line-height: var(--leading-normal);
  &:focus { border-color: var(--color-primary); }
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-2);
  padding: var(--space-4) var(--space-5);
  border-top: 1px solid var(--border-color);
}

.form-btn {
  height: 36px;
  padding: 0 var(--space-4);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast);

  &.secondary {
    border: 1px solid var(--border-color);
    background: var(--bg-primary);
    color: var(--text-secondary);
    &:hover { border-color: var(--text-muted); color: var(--text-primary); }
  }

  &.primary {
    border: none;
    background: var(--color-primary);
    color: #fff;
    &:hover:not(:disabled) { background: var(--color-primary-hover); }
    &:disabled { opacity: 0.5; cursor: not-allowed; }
  }
}
</style>
