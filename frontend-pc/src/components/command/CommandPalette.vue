<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useProjectStore } from '@/stores/project';
import { taskApi, type Task } from '@/api/task';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
}>();

const router = useRouter();
const route = useRoute();
const projectStore = useProjectStore();

const searchInput = ref<any>(null);
const query = ref('');
const selectedIndex = ref(0);
const searchResults = ref<Task[]>([]);
const isSearching = ref(false);
let searchTimeout: ReturnType<typeof setTimeout> | null = null;

interface QuickAction {
  id: string;
  label: string;
  icon: string;
  action: () => void;
}

const quickActions: QuickAction[] = [
  {
    id: 'new-task',
    label: '新建任务',
    icon: 'plus',
    action: () => {
      const projectId = route.params.id;
      if (projectId) {
        router.push(`/project/${projectId}/tasks`);
      } else {
        router.push('/my-tasks');
      }
      close();
    },
  },
  {
    id: 'new-project',
    label: '新建项目',
    icon: 'folder-plus',
    action: () => {
      router.push('/projects');
      close();
    },
  },
  {
    id: 'project-list',
    label: '打开项目列表',
    icon: 'folder',
    action: () => {
      router.push('/projects');
      close();
    },
  },
  {
    id: 'kanban',
    label: '打开看板',
    icon: 'layout',
    action: () => {
      const currentProjectId = route.params.id;
      if (currentProjectId) {
        router.push(`/project/${currentProjectId}/board`);
      } else if (projectStore.projects.length > 0) {
        router.push(`/project/${projectStore.projects[0].id}/board`);
      }
      close();
    },
  },
  {
    id: 'my-tasks',
    label: '我的任务',
    icon: 'check-square',
    action: () => {
      router.push('/my-tasks');
      close();
    },
  },
  {
    id: 'dashboard',
    label: '仪表盘',
    icon: 'layout-dashboard',
    action: () => {
      router.push('/dashboard');
      close();
    },
  },
];

interface SearchResultItem {
  type: 'action' | 'task';
  action?: QuickAction;
  task?: Task;
}

const filteredResults = computed<SearchResultItem[]>(() => {
  if (!query.value.trim()) {
    return quickActions.map((action) => ({ type: 'action', action }));
  }

  const q = query.value.toLowerCase();
  const results: SearchResultItem[] = [];

  const matchedActions = quickActions.filter((action) =>
    action.label.toLowerCase().includes(q)
  );
  matchedActions.forEach((action) => results.push({ type: 'action', action }));

  if (!isSearching.value) {
    searchResults.value.forEach((task) => {
      results.push({ type: 'task', task });
    });
  }

  return results;
});

function getFlatIndex(item: SearchResultItem) {
  return filteredResults.value.indexOf(item);
}

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      query.value = '';
      selectedIndex.value = 0;
      searchResults.value = [];
      nextTick(() => {
        searchInput.value?.focus();
      });
    }
  }
);

watch(query, (newQuery) => {
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }

  if (!newQuery.trim()) {
    searchResults.value = [];
    isSearching.value = false;
    return;
  }

  isSearching.value = true;
  searchTimeout = setTimeout(async () => {
    try {
      const res = await taskApi.list({
        keyword: newQuery,
        size: 10,
      });
      if (res.code === 200) {
        searchResults.value = res.data.records;
      }
    } catch (error) {
      console.error('Search failed:', error);
    } finally {
      isSearching.value = false;
    }
  }, 300);
});

function close() {
  emit('update:visible', false);
}

function handleKeydown(e: KeyboardEvent) {
  const total = filteredResults.value.length;

  switch (e.key) {
    case 'ArrowDown':
      e.preventDefault();
      selectedIndex.value = (selectedIndex.value + 1) % total;
      break;
    case 'ArrowUp':
      e.preventDefault();
      selectedIndex.value = (selectedIndex.value - 1 + total) % total;
      break;
    case 'Enter':
      e.preventDefault();
      selectItem(selectedIndex.value);
      break;
    case 'Escape':
      e.preventDefault();
      close();
      break;
  }
}

function selectItem(index: number) {
  const item = filteredResults.value[index];
  if (!item) return;

  if (item.type === 'action' && item.action) {
    item.action.action();
  } else if (item.type === 'task' && item.task) {
    router.push(`/project/${item.task.projectId}/tasks`);
    close();
  }
}

function handleBackdropClick(e: MouseEvent) {
  if (e.target === e.currentTarget) {
    close();
  }
}

function getTaskTypeLabel(type: string): string {
  const map: Record<string, string> = {
    epic: '史诗',
    story: '故事',
    task: '任务',
    bug: '缺陷',
  };
  return map[type] || type;
}

function getTaskTypeColor(type: string): string {
  const map: Record<string, string> = {
    epic: '#8B5CF6',
    story: '#3B82F6',
    task: '#10B981',
    bug: '#EF4444',
  };
  return map[type] || '#6B7280';
}

function getStatusLabel(status: string): string {
  const map: Record<string, string> = {
    todo: '待处理',
    in_progress: '进行中',
    completed: '已完成',
  };
  return map[status] || status;
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown);
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }
});
</script>

<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="visible" class="command-overlay" @click="handleBackdropClick">
        <div class="command-modal">
          <div class="command-header">
            <el-input
              ref="searchInput"
              v-model="query"
              placeholder="搜索任务或输入命令..."
              :prefix-icon="SearchIcon"
              clearable
              autofocus
              @keydown.stop
            />
          </div>

          <div class="command-body">
            <div v-if="filteredResults.length === 0" class="command-empty">
              <span v-if="isSearching">搜索中...</span>
              <span v-else>无匹配结果</span>
            </div>

            <template v-else>
              <div v-if="!query.trim()" class="command-section">
                <div class="section-title">快捷操作</div>
                <div
                  v-for="(item, index) in filteredResults"
                  :key="item.action?.id"
                  class="command-item"
                  :class="{ selected: selectedIndex === index }"
                  @click="selectItem(index)"
                  @mouseenter="selectedIndex = index"
                >
                  <span class="item-icon">
                    <svg
                      v-if="item.action?.icon === 'plus'"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <line x1="12" y1="5" x2="12" y2="19" />
                      <line x1="5" y1="12" x2="19" y2="12" />
                    </svg>
                    <svg
                      v-else-if="item.action?.icon === 'folder-plus'"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2z" />
                      <line x1="12" y1="11" x2="12" y2="17" />
                      <line x1="9" y1="14" x2="15" y2="14" />
                    </svg>
                    <svg
                      v-else-if="item.action?.icon === 'folder'"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <path d="M22 19a2 2 0 01-2 2H4a2 2 0 01-2-2V5a2 2 0 012-2h5l2 3h9a2 2 0 012 2z" />
                    </svg>
                    <svg
                      v-else-if="item.action?.icon === 'layout'"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                      <line x1="3" y1="9" x2="21" y2="9" />
                      <line x1="9" y1="21" x2="9" y2="9" />
                    </svg>
                    <svg
                      v-else-if="item.action?.icon === 'check-square'"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <polyline points="9 11 12 14 22 4" />
                      <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11" />
                    </svg>
                    <svg
                      v-else-if="item.action?.icon === 'layout-dashboard'"
                      width="16"
                      height="16"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                    >
                      <rect x="3" y="3" width="7" height="9" />
                      <rect x="14" y="3" width="7" height="5" />
                      <rect x="14" y="12" width="7" height="9" />
                      <rect x="3" y="16" width="7" height="5" />
                    </svg>
                  </span>
                  <span class="item-label">{{ item.action?.label }}</span>
                  <span class="item-shortcut">
                    <kbd>↵</kbd> 选择
                  </span>
                </div>
              </div>

              <template v-else>
                <div v-if="filteredResults.some((r) => r.type === 'action')" class="command-section">
                  <div class="section-title">操作</div>
                  <div
                    v-for="item in filteredResults.filter((r) => r.type === 'action')"
                    :key="item.action?.id"
                    class="command-item"
                    :class="{ selected: selectedIndex === getFlatIndex(item) }"
                    @click="selectItem(getFlatIndex(item))"
                    @mouseenter="selectedIndex = getFlatIndex(item)"
                  >
                    <span class="item-icon">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10" />
                        <line x1="12" y1="8" x2="12" y2="16" />
                        <line x1="8" y1="12" x2="16" y2="12" />
                      </svg>
                    </span>
                    <span class="item-label">{{ item.action?.label }}</span>
                  </div>
                </div>

                <div v-if="searchResults.length > 0" class="command-section">
                  <div class="section-title">任务</div>
                  <div
                    v-for="item in filteredResults.filter((r) => r.type === 'task')"
                    :key="item.task?.id"
                    class="command-item"
                    :class="{ selected: selectedIndex === getFlatIndex(item) }"
                    @click="selectItem(getFlatIndex(item))"
                    @mouseenter="selectedIndex = getFlatIndex(item)"
                  >
                    <span class="task-type-badge" :style="{ background: getTaskTypeColor(item.task?.type || 'task') }">
                      {{ getTaskTypeLabel(item.task?.type || 'task') }}
                    </span>
                    <span class="task-title">{{ item.task?.title }}</span>
                    <span class="task-status">{{ getStatusLabel(item.task?.status || 'todo') }}</span>
                  </div>
                </div>
              </template>
            </template>
          </div>

          <div class="command-footer">
            <span class="footer-hint">
              <kbd>↑</kbd><kbd>↓</kbd> 导航
              <kbd>↵</kbd> 选择
              <kbd>Esc</kbd> 关闭
            </span>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script lang="ts">
import { h } from 'vue';

const SearchIcon = {
  render() {
    return h('svg', {
      width: 16,
      height: 16,
      viewBox: '0 0 24 24',
      fill: 'none',
      stroke: 'currentColor',
      'stroke-width': 2,
    }, [
      h('circle', { cx: 11, cy: 11, r: 8 }),
      h('line', { x1: 21, y1: 21, x2: 16.65, y2: 16.65 }),
    ]);
  },
};
export default {};
</script>

<style scoped lang="scss">
.command-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 15vh;
  z-index: 9999;
}

.command-modal {
  width: 100%;
  max-width: 600px;
  background: var(--cmd-bg, #1a1d21);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-modal);
  overflow: hidden;
}

.command-header {
  padding: 12px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);

  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: var(--radius-md);
    box-shadow: none;

    &:hover,
    &:focus-within {
      border-color: rgba(99, 102, 241, 0.5);
    }
  }

  :deep(.el-input__inner) {
    color: #e5e7eb;
    font-size: var(--text-sm);

    &::placeholder {
      color: #6b7280;
    }
  }

  :deep(.el-input__prefix) {
    color: #6b7280;
  }
}

.command-body {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;

  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.1); border-radius: 3px; }
}

.command-empty {
  padding: 24px;
  text-align: center;
  color: #6b7280;
  font-size: var(--text-sm);
}

.command-section {
  margin-bottom: 8px;
  &:last-child { margin-bottom: 0; }
}

.section-title {
  padding: 8px 12px 4px;
  font-size: 11px;
  font-weight: var(--font-semibold);
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.command-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);

  &:hover,
  &.selected {
    background: rgba(99, 102, 241, 0.15);
  }

  &.selected .item-shortcut { opacity: 1; }
}

.item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: var(--radius-sm);
  color: #9ca3af;
  flex-shrink: 0;
}

.item-label {
  flex: 1;
  color: #e5e7eb;
  font-size: var(--text-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-shortcut {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #6b7280;
  font-size: var(--text-xs);
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.task-type-badge {
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  font-size: 11px;
  font-weight: var(--font-medium);
  color: #fff;
  flex-shrink: 0;
}

.task-title {
  flex: 1;
  color: #e5e7eb;
  font-size: var(--text-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-status {
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: var(--radius-sm);
  font-size: 11px;
  color: #9ca3af;
  flex-shrink: 0;
}

.command-footer {
  padding: 10px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  background: rgba(0, 0, 0, 0.2);
}

.footer-hint {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: var(--text-xs);
  color: #6b7280;
}

kbd {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 5px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-sm);
  font-family: inherit;
  font-size: 11px;
  color: #9ca3af;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
