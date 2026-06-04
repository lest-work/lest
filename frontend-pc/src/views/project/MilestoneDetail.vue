<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { milestoneApi } from '@/api/milestone';
import type { Milestone } from '@/api/milestone';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

const route = useRoute();
const router = useRouter();

const milestoneId = computed(() => Number(route.params.milestoneId));
const projectId = computed(() => Number(route.params.id));

const milestone = ref<Milestone | null>(null);
const loading = ref(false);

onMounted(async () => {
  loading.value = true;
  try { const r = await milestoneApi.getById(milestoneId.value); milestone.value = r.data; }
  catch { milestone.value = null; }
  finally { loading.value = false; }
});

function goBack() { router.push(`/project/${projectId.value}/tasks`); }
</script>

<template>
  <div class="milestone-detail">
    <div class="page-header">
      <div class="header-left">
        <button class="back-btn" @click="goBack">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"/>
          </svg>
          返回
        </button>
        <h2 class="page-title">{{ milestone?.name || '里程碑详情' }}</h2>
      </div>
    </div>

    <LoadingSpinner v-if="loading" />

    <div v-else-if="milestone" class="content">
      <div class="info-card">
        <div class="info-row">
          <span class="info-key">描述</span>
          <span class="info-val">{{ milestone.description || '暂无描述' }}</span>
        </div>
        <div class="info-row">
          <span class="info-key">目标日期</span>
          <span class="info-val">{{ milestone.targetDate || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-key">完成时间</span>
          <span class="info-val">{{ milestone.completedAt || '-' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.milestone-detail {
  padding: var(--space-6) var(--space-5);
  font-family: var(--font-primary);
}
.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5);
}
.header-left { display: flex; align-items: center; gap: var(--space-3); }
.back-btn {
  display: flex; align-items: center; gap: var(--space-1);
  border: none; background: transparent; color: var(--text-secondary);
  font-size: var(--text-sm); font-family: var(--font-primary); cursor: pointer;
  padding: var(--space-1) var(--space-2); border-radius: var(--radius-sm);
  transition: color var(--transition-fast), background var(--transition-fast);
  &:hover { color: var(--text-primary); background: var(--bg-hover); }
}
.page-title { font-size: var(--text-xl); font-weight: var(--font-semibold); color: var(--text-primary); margin: 0; }
.content { display: flex; flex-direction: column; gap: var(--space-4); }
.info-card { background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: var(--radius-lg); overflow: hidden; }
.info-row {
  display: flex; align-items: flex-start;
  padding: var(--space-3) var(--space-4); border-bottom: 1px solid var(--border-light);
  font-size: var(--text-sm);
  &:last-child { border-bottom: none; }
}
.info-key { width: 80px; color: var(--text-muted); font-weight: var(--font-medium); flex-shrink: 0; }
.info-val { color: var(--text-primary); }
</style>
