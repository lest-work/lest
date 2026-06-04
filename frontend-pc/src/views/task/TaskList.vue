<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useTaskStore } from '@/stores/task';
import { useProjectStore } from '@/stores/project';
import { useUserStore } from '@/stores/user';
import TaskDetailDrawer from '@/components/task/TaskDetailDrawer.vue';
import TypeIcon from '@/components/common/TypeIcon.vue';
import PriorityDot from '@/components/common/PriorityDot.vue';
import AssigneeAvatar from '@/components/common/AssigneeAvatar.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import type { Task, TaskFilter, TaskType, TaskStatus, TaskPriority } from '@/api/task';

const STATUS_CONFIG: Record<string, { label: string; color: string }> = {
  todo:         { label: '待办',   color: 'var(--status-todo)' },
  in_progress:  { label: '进行中', color: 'var(--status-in-progress)' },
  completed:    { label: '已完成', color: 'var(--status-completed)' },
};

const PRIORITY_CONFIG: Record<string, { label: string; color: string }> = {
  highest: { label: '最高', color: 'var(--priority-p0)' },
  high:    { label: '高',   color: 'var(--priority-p1)' },
  medium:  { label: '中',   color: 'var(--priority-p2)' },
  low:     { label: '低',   color: 'var(--priority-p3)' },
  lowest:  { label: '最低', color: 'var(--priority-none)' },
};

const route = useRoute();
const taskStore = useTaskStore();
const projectStore = useProjectStore();
const userStore = useUserStore();

// TaskDetailDrawer state
const drawerVisible = ref(false);
const drawerTaskId = ref<number | null>(null);

// Filter state — single button dropdown
const filterVisible = ref(false);
const filterForm = reactive<Partial<TaskFilter>>({
  projectId: route.params.id ? Number(route.params.id) : undefined,
  type: [],
  status: [],
  assigneeId: undefined,
  priority: [],
  keyword: '',
});

// Detect if we're on the "My Tasks" page (no project context)
const isMyTasks = computed(() => route.path === '/my-tasks' || route.path.startsWith('/my-tasks'));

const myTasksOnly = ref(false);

// Auto-enable "我的任务" filter when on /my-tasks page
if (isMyTasks.value) {
  myTasksOnly.value = true;
}

function toggleMyTasks() {
  myTasksOnly.value = !myTasksOnly.value;
  if (myTasksOnly.value) {
    filterForm.assigneeId = userStore.userInfo?.id;
  } else {
    filterForm.assigneeId = undefined;
  }
  taskStore.pagination.page = 1;
  taskStore.fetchTasks(filterForm as any);
}

onMounted(async () => {
  await projectStore.fetchProjects({ status: 'active' });
  if (isMyTasks.value && !filterForm.assigneeId) {
    filterForm.assigneeId = userStore.userInfo?.id;
  }
  await taskStore.fetchTasks(filterForm as any);
});

function onFilterChange() {
  taskStore.pagination.page = 1;
  taskStore.fetchTasks(filterForm as any);
}

function onKeywordInput() {
  taskStore.pagination.page = 1;
  taskStore.fetchTasks(filterForm as any);
}

async function handlePageChange(page: number) {
  taskStore.pagination.page = page;
  await taskStore.fetchTasks(filterForm as any);
}

function openTask(task: Task) {
  drawerTaskId.value = task.id;
  drawerVisible.value = true;
}

const activeFilterCount = computed(() => {
  let count = 0;
  if (filterForm.type?.length) count++;
  if (filterForm.status?.length) count++;
  if (filterForm.priority?.length) count++;
  if (filterForm.assigneeId) count++;
  if (myTasksOnly.value) count++;
  return count;
});

function clearAllFilters() {
  filterForm.type = [];
  filterForm.status = [];
  filterForm.priority = [];
  filterForm.assigneeId = undefined;
  myTasksOnly.value = false;
  onFilterChange();
}

function getStatusConfig(status: string) {
  return STATUS_CONFIG[status] || { label: status, color: 'var(--status-todo)' };
}
function getPriorityConfig(priority: string) {
  return PRIORITY_CONFIG[priority] || { label: priority, color: 'var(--priority-none)' };
}
</script>

<template>
  <div class="task-list-view">

    <!-- ===== Filter Bar — Linear 风格 ===== -->
    <div class="list-toolbar">
      <!-- 搜索框 -->
      <div class="toolbar-search">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="search-icon">
          <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>
        <input
          v-model="filterForm.keyword"
          class="search-input"
          placeholder="搜索任务..."
          @input="onKeywordInput"
        />
        <button v-if="filterForm.keyword" class="search-clear" @click="filterForm.keyword = ''; onKeywordInput()">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>

      <!-- 我负责的 — 独立 Pill 按钮（规范要求） -->
      <button
        class="my-tasks-pill"
        :class="{ active: myTasksOnly }"
        @click="toggleMyTasks"
      >
        <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 11l3 3L22 4"/>
          <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
        </svg>
        我的任务
      </button>

      <!-- 筛选按钮 -->
      <el-popover
        placement="bottom-start"
        :width="320"
        trigger="click"
        v-model:visible="filterVisible"
      >
        <template #reference>
          <button class="filter-btn" :class="{ active: activeFilterCount > 0 }">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/>
            </svg>
            筛选
            <span v-if="activeFilterCount > 0" class="filter-count">{{ activeFilterCount }}</span>
          </button>
        </template>

        <div class="filter-panel">
          <!-- 类型 -->
          <div class="filter-group">
            <span class="filter-group-label">类型</span>
            <div class="filter-chips">
              <button
                v-for="t in [{ v: 'epic', l: '🎯 Epic' }, { v: 'story', l: '📄 Story' }, { v: 'task', l: '☑ Task' }, { v: 'bug', l: '🐛 Bug' }]"
                :key="t.v"
                class="filter-chip"
                :class="{ selected: filterForm.type?.includes(t.v as TaskType) }"
                @click="() => {
                  if (!filterForm.type) filterForm.type = [];
                  const idx = filterForm.type.indexOf(t.v as TaskType);
                  if (idx >= 0) filterForm.type.splice(idx, 1); else filterForm.type.push(t.v as TaskType);
                  onFilterChange();
                }"
              >{{ t.l }}</button>
            </div>
          </div>

          <!-- 状态 -->
          <div class="filter-group">
            <span class="filter-group-label">状态</span>
            <div class="filter-chips">
              <button
                v-for="s in [{ v: 'todo', l: '⚪ 待办' }, { v: 'in_progress', l: '🔵 进行中' }, { v: 'completed', l: '🟢 已完成' }]"
                :key="s.v"
                class="filter-chip"
                :class="{ selected: filterForm.status?.includes(s.v as TaskStatus) }"
                @click="() => {
                  if (!filterForm.status) filterForm.status = [];
                  const idx = filterForm.status.indexOf(s.v as TaskStatus);
                  if (idx >= 0) filterForm.status.splice(idx, 1); else filterForm.status.push(s.v as TaskStatus);
                  onFilterChange();
                }"
              >{{ s.l }}</button>
            </div>
          </div>

          <!-- 优先级 -->
          <div class="filter-group">
            <span class="filter-group-label">优先级</span>
            <div class="filter-chips">
              <button
                v-for="p in [{ v: 'highest', l: '🔴 最高' }, { v: 'high', l: '🟠 高' }, { v: 'medium', l: '🟡 中' }, { v: 'low', l: '🟢 低' }]"
                :key="p.v"
                class="filter-chip"
                :class="{ selected: filterForm.priority?.includes(p.v as TaskPriority) }"
                @click="() => {
                  if (!filterForm.priority) filterForm.priority = [];
                  const idx = filterForm.priority.indexOf(p.v as TaskPriority);
                  if (idx >= 0) filterForm.priority.splice(idx, 1); else filterForm.priority.push(p.v as TaskPriority);
                  onFilterChange();
                }"
              >{{ p.l }}</button>
            </div>
          </div>

          <!-- 清除 -->
          <div v-if="activeFilterCount > 0" class="filter-clear">
            <button class="clear-btn" @click="clearAllFilters">清除所有筛选</button>
          </div>
        </div>
      </el-popover>

      <!-- 新建任务 -->
      <button class="new-task-btn" @click="openTask(0 as any)">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建任务
      </button>
    </div>

    <!-- 活跃筛选药丸芯片（Jira 风格 — 内联显示可移除的筛选条件） -->
    <div v-if="activeFilterCount > 0" class="active-filters">
      <!-- 类型筛选药丸 -->
      <button
        v-for="t in filterForm.type || []"
        :key="'type-' + t"
        class="filter-pill"
        @click="() => {
          if (!filterForm.type) return;
          const idx = filterForm.type.indexOf(t as TaskType);
          if (idx >= 0) filterForm.type.splice(idx, 1);
          onFilterChange();
        }"
      >
        {{ t }}
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
        </svg>
      </button>

      <!-- 状态筛选药丸 -->
      <button
        v-for="s in filterForm.status || []"
        :key="'status-' + s"
        class="filter-pill"
        @click="() => {
          if (!filterForm.status) return;
          const idx = filterForm.status.indexOf(s as TaskStatus);
          if (idx >= 0) filterForm.status.splice(idx, 1);
          onFilterChange();
        }"
      >
        {{ getStatusConfig(s).label }}
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
        </svg>
      </button>

      <!-- 优先级筛选药丸 -->
      <button
        v-for="p in filterForm.priority || []"
        :key="'priority-' + p"
        class="filter-pill"
        @click="() => {
          if (!filterForm.priority) return;
          const idx = filterForm.priority.indexOf(p as TaskPriority);
          if (idx >= 0) filterForm.priority.splice(idx, 1);
          onFilterChange();
        }"
      >
        {{ p }}
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
        </svg>
      </button>

      <!-- 指派人筛选药丸 -->
      <button
        v-if="filterForm.assigneeId"
        class="filter-pill"
        @click="() => { filterForm.assigneeId = undefined; onFilterChange(); }"
      >
        指派给我
        <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
          <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
        </svg>
      </button>

      <button class="clear-all-filters" @click="clearAllFilters">清除全部</button>
    </div>

    <!-- ===== Task Count ===== -->
    <div class="list-meta">
      <span class="list-count">
        {{ taskStore.pagination.total }} 个任务
        <span v-if="activeFilterCount > 0">（已筛选）</span>
      </span>
    </div>

    <!-- ===== Task List — Linear 风格列表 ===== -->
    <LoadingSpinner v-if="taskStore.loading" />
    <EmptyState
      v-else-if="taskStore.tasks.length === 0"
      title="暂无任务"
      description="开始创建第一个任务吧"
      cta="新建任务"
      icon="task"
      @cta="openTask(0 as any)"
    />
    <div v-else class="task-list">
      <div
        v-for="task in taskStore.tasks"
        :key="task.id"
        class="task-row"
        @click="openTask(task)"
      >
        <!-- 类型图标 -->
        <TypeIcon :type="task.type" :size="14" />

        <!-- Task No -->
        <span class="task-no">{{ task.taskNo }}</span>

        <!-- 标题 -->
        <span class="task-title">{{ task.title }}</span>

        <!-- 状态圆点（Jira 风格） -->
        <span
          class="task-status-dot"
          :style="{ background: getStatusConfig(task.status).color }"
          :title="getStatusConfig(task.status).label"
        />

        <!-- 优先级 -->
        <PriorityDot :priority="task.priority" :size="8" />

        <!-- 负责人 -->
        <AssigneeAvatar
          v-if="task.assigneeName"
          :name="task.assigneeName"
          :src="(task as any).assigneeAvatar"
          :size="24"
        />
        <span v-else class="unassigned">-</span>
      </div>
    </div>

    <!-- ===== Pagination ===== -->
    <div v-if="taskStore.pagination.total > taskStore.pagination.size" class="list-pagination">
      <button
        class="page-btn"
        :disabled="taskStore.pagination.page <= 1"
        @click="handlePageChange(taskStore.pagination.page - 1)"
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
      </button>
      <span class="page-info">{{ taskStore.pagination.page }} / {{ Math.ceil(taskStore.pagination.total / taskStore.pagination.size) }}</span>
      <button
        class="page-btn"
        :disabled="taskStore.pagination.page >= Math.ceil(taskStore.pagination.total / taskStore.pagination.size)"
        @click="handlePageChange(taskStore.pagination.page + 1)"
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="9 18 15 12 9 6"/>
        </svg>
      </button>
    </div>

    <!-- TaskDetailDrawer -->
    <TaskDetailDrawer
      v-model:visible="drawerVisible"
      :task-id="drawerTaskId"
    />
  </div>
</template>

<style scoped lang="scss">
.task-list-view {
  padding: var(--space-5);
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  min-height: 400px;
  display: flex;
  flex-direction: column;
  font-family: var(--font-primary);
}

.list-toolbar {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-5);
  border-bottom: 1px solid var(--border-color);
}

.toolbar-search {
  position: relative;
  display: flex;
  align-items: center;
  flex: 1;
  max-width: 320px;
}

.search-icon {
  position: absolute;
  left: var(--space-3);
  color: var(--text-muted);
  pointer-events: none;
}

.search-input {
  width: 100%;
  height: 36px;
  padding: 0 var(--space-8) 0 var(--space-8);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  color: var(--text-primary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  outline: none;
  transition: border-color var(--transition-fast), background var(--transition-fast);

  &::placeholder { color: var(--text-muted); }

  &:focus {
    border-color: var(--color-primary);
    background: var(--bg-primary);
  }
}

.search-clear {
  position: absolute;
  right: var(--space-2);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: none;
  background: var(--bg-tertiary);
  color: var(--text-muted);
  cursor: pointer;
  border-radius: var(--radius-full);
  transition: background var(--transition-fast);

  &:hover { background: var(--border-color); color: var(--text-primary); }
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  height: 36px;
  padding: 0 var(--space-3);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  transition: border-color var(--transition-fast), color var(--transition-fast);

  &:hover {
    border-color: var(--border-focus);
    color: var(--text-primary);
  }

  &.active {
    border-color: var(--color-primary);
    color: var(--color-primary);
    background: var(--color-primary-light);
  }
}

.filter-count {
  background: var(--color-primary);
  color: #fff;
  font-size: 10px;
  padding: 0 5px;
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  min-width: 16px;
  text-align: center;
  line-height: 16px;
}

.my-tasks-pill {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  height: 36px;
  padding: 0 var(--space-3);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }

  &.active {
    background: var(--color-primary);
    border-color: var(--color-primary);
    color: #fff;
  }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }
}

.new-task-btn {
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
  margin-left: auto;
  transition: background var(--transition-fast);

  &:hover { background: var(--color-primary-hover); }
}

/* Filter Panel */
.filter-panel {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.filter-group-label {
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.4px;
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-1);
}

.filter-chip {
  padding: 4px var(--space-3);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }

  &.selected {
    background: var(--color-primary);
    border-color: var(--color-primary);
    color: #fff;
  }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }
}

.my-tasks-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  width: 100%;
  transition: all var(--transition-fast);

  &:hover { border-color: var(--color-primary); color: var(--color-primary); }
  &.active {
    background: var(--color-primary);
    border-color: var(--color-primary);
    color: #fff;
  }
}

.filter-clear {
  border-top: 1px solid var(--border-color);
  padding-top: var(--space-3);
}

.clear-btn {
  border: none;
  background: transparent;
  color: var(--color-danger);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  &:hover { opacity: 0.7; }
}

.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border-bottom: 1px solid var(--border-light);
}

.filter-pill {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px var(--space-2) 3px var(--space-3);
  background: var(--color-primary-light);
  color: var(--color-primary);
  border: 1px solid var(--color-primary-light);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast), border-color var(--transition-fast);

  &:hover {
    background: var(--color-primary);
    color: #fff;
    border-color: var(--color-primary);
  }
}

.clear-all-filters {
  border: none;
  background: transparent;
  color: var(--text-muted);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  text-decoration: underline;
  padding: 3px var(--space-2);

  &:hover { color: var(--color-danger); }
}

.list-meta {
  padding: var(--space-2) var(--space-5);
  border-bottom: 1px solid var(--border-light);
}

.list-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
}



.task-list {
  display: flex;
  flex-direction: column;
}

.task-row {
  display: grid;
  grid-template-columns: 20px 72px 1fr 16px 20px 28px;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border-bottom: 1px solid var(--border-light);
  cursor: pointer;
  transition: background var(--transition-fast);

  &:hover {
    background: var(--bg-hover);

    .task-arrow { opacity: 1; }
  }

  &:last-child { border-bottom: none; }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: -2px;
  }
}

.task-no {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-family: var(--font-mono);
}

.task-title {
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-weight: var(--font-medium);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-priority {
  display: flex;
  align-items: center;
  justify-content: center;
}

.unassigned {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.task-status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.list-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-3);
  padding: var(--space-4);
  border-top: 1px solid var(--border-color);
}

.page-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover:not(:disabled) {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.page-info {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  font-family: var(--font-mono);
}
</style>
