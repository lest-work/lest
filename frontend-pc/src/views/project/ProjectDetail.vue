<script setup lang="ts">
import { onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useProjectStore } from '@/stores/project';

const route = useRoute();
const router = useRouter();
const projectStore = useProjectStore();

const projectId = computed(() => Number(route.params.id));

const currentTab = computed(() => {
  const path = route.path;
  if (path.endsWith('/board')) return 'board';
  if (path.endsWith('/gantt')) return 'gantt';
  if (path.includes('/iteration/')) return 'iteration';
  if (path.includes('/milestone/')) return 'milestone';
  if (path.includes('/release')) return 'release';
  return 'tasks';
});

interface TabItem { key: string; label: string; path: string }

const tabs = computed<TabItem[]>(() => [
  { key: 'tasks', label: '任务列表', path: `/project/${projectId.value}/tasks` },
  { key: 'board', label: '看板', path: `/project/${projectId.value}/board` },
  { key: 'gantt', label: '甘特图', path: `/project/${projectId.value}/gantt` },
  { key: 'release', label: '发布计划', path: `/project/${projectId.value}/release` },
]);

onMounted(async () => {
  await projectStore.fetchProjects({ status: 'active' });
  const project = projectStore.projects.find(p => p.id === projectId.value);
  projectStore.setCurrentProject(project || null);
});

function switchTab(tab: TabItem) {
  router.push(tab.path);
}
</script>

<template>
  <div class="project-detail">
    <!-- 项目导航 Tabs（Jira 风格下划线指示器） -->
    <div class="project-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-btn"
        :class="{ active: currentTab === tab.key }"
        @click="switchTab(tab)"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 子路由视图 -->
    <router-view />
  </div>
</template>

<style scoped lang="scss">
.project-detail {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.project-tabs {
  display: flex;
  gap: 0;
  margin-bottom: var(--space-4);
  border-bottom: 1px solid var(--border-color);
  padding: 0 var(--space-5);
  background: var(--bg-primary);
}

.tab-btn {
  display: flex;
  align-items: center;
  padding: var(--space-3) var(--space-4);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  font-family: var(--font-primary);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: color var(--transition-fast), border-color var(--transition-fast);

  &:hover { color: var(--text-primary); }

  &.active {
    color: var(--color-primary);
    border-bottom-color: var(--color-primary);
    font-weight: var(--font-semibold);
  }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: -2px;
  }
}
</style>
