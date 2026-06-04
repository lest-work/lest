<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { iterationApi, IterationStatus, IterationStatusLabel, type Iteration } from '@/api/iteration';
import StatusChip from '@/components/common/StatusChip.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

const route = useRoute();
const router = useRouter();

const iterationId = computed(() => Number(route.params.iterationId));
const projectId = computed(() => Number(route.params.id));

const iteration = ref<Iteration | null>(null);
const loading = ref(false);
const saving = ref(false);

onMounted(async () => {
  loading.value = true;
  try {
    const r = await iterationApi.getById(iterationId.value);
    iteration.value = r.data;
  } catch { iteration.value = null; }
  finally { loading.value = false; }
});

const STATUS_COLORS: Record<number, string> = {
  [IterationStatus.PLANNING]: 'var(--status-todo)',
  [IterationStatus.ACTIVE]: 'var(--status-in-progress)',
  [IterationStatus.COMPLETED]: 'var(--status-completed)',
};

function statusLabel(status: number) {
  return IterationStatusLabel[status as IterationStatus] || '未知';
}
function statusColor(status: number) {
  return STATUS_COLORS[status] || 'var(--status-todo)';
}

async function startIteration() {
  if (!iteration.value) return;
  saving.value = true;
  try {
    await iterationApi.start(iterationId.value);
    iteration.value = { ...iteration.value, status: IterationStatus.ACTIVE };
  } finally { saving.value = false; }
}

async function completeIteration() {
  if (!iteration.value) return;
  saving.value = true;
  try {
    await iterationApi.complete(iterationId.value);
    iteration.value = { ...iteration.value, status: IterationStatus.COMPLETED };
  } finally { saving.value = false; }
}

function goBack() {
  router.push(`/project/${projectId.value}/tasks`);
}
</script>

<template>
  <div class="iteration-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          返回
        </button>
        <h2 class="page-title">{{ iteration?.name || '迭代详情' }}</h2>
        <StatusChip
          v-if="iteration"
          :label="statusLabel(iteration.status)"
          :color="statusColor(iteration.status)"
          :status="String(iteration.status)"
        />
      </div>
      <div class="header-actions">
        <button
          v-if="iteration?.status === IterationStatus.PLANNING"
          class="action-btn primary"
          :disabled="saving"
          @click="startIteration"
        >
          开始迭代
        </button>
        <button
          v-if="iteration?.status === IterationStatus.ACTIVE"
          class="action-btn success"
          :disabled="saving"
          @click="completeIteration"
        >
          完成迭代
        </button>
      </div>
    </div>

    <!-- Loading -->
    <LoadingSpinner v-if="loading" />

    <!-- 迭代信息 -->
    <div v-else-if="iteration" class="content">
      <div class="info-card">
        <div class="info-row">
          <span class="info-key">迭代目标</span>
          <span class="info-val">{{ iteration.goal || '暂无目标' }}</span>
        </div>
        <div class="info-row">
          <span class="info-key">开始日期</span>
          <span class="info-val">{{ iteration.startDate || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-key">结束日期</span>
          <span class="info-val">{{ iteration.endDate || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-key">完成时间</span>
          <span class="info-val">{{ iteration.completedAt || '-' }}</span>
        </div>
      </div>

      <div class="section-card">
        <h3 class="section-title">任务进度</h3>
        <p class="section-desc">迭代关联的任务将在任务列表中按迭代筛选查看</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.iteration-detail {
  padding: var(--space-6) var(--space-5);
  font-family: var(--font-primary);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-5);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.back-btn {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: color var(--transition-fast), background var(--transition-fast);

  &:hover { color: var(--text-primary); background: var(--bg-hover); }
}

.page-title {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: var(--space-2);
}

.action-btn {
  padding: var(--space-2) var(--space-4);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast);

  &:disabled { opacity: 0.5; cursor: not-allowed; }

  &.primary {
    background: var(--color-primary);
    color: #fff;
    &:hover:not(:disabled) { background: var(--color-primary-hover); }
  }

  &.success {
    background: var(--color-success);
    color: #fff;
    &:hover:not(:disabled) { opacity: 0.85; }
  }
}

.content { display: flex; flex-direction: column; gap: var(--space-4); }

.info-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.info-row {
  display: flex;
  align-items: flex-start;
  padding: var(--space-3) var(--space-4);
  border-bottom: 1px solid var(--border-light);
  font-size: var(--text-sm);
  &:last-child { border-bottom: none; }
}

.info-key {
  width: 80px;
  color: var(--text-muted);
  font-weight: var(--font-medium);
  flex-shrink: 0;
}

.info-val { color: var(--text-primary); }

.section-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-4);
}

.section-title {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-2);
}

.section-desc {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin: 0;
}
</style>
