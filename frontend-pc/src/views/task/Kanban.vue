<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import draggable from 'vuedraggable';
import { useBoardStore } from '@/stores/board';
import TaskDetailDrawer from '@/components/task/TaskDetailDrawer.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import TypeIcon from '@/components/common/TypeIcon.vue';
import PriorityDot from '@/components/common/PriorityDot.vue';
import AssigneeAvatar from '@/components/common/AssigneeAvatar.vue';
import type { BoardTask } from '@/api/board';

const route = useRoute();
const router = useRouter();
const boardStore = useBoardStore();

const projectId = computed(() => Number(route.params.id));

// TaskDetailDrawer state
const drawerVisible = ref(false);
const drawerTaskId = ref<number | null>(null);

const COLUMN_NAMES: Record<string, string> = {
  todo: '待办',
  in_progress: '进行中',
  completed: '已完成',
};

const COLUMN_COLORS: Record<string, string> = {
  todo: 'var(--status-todo)',
  in_progress: 'var(--status-in-progress)',
  completed: 'var(--status-completed)',
};

onMounted(() => {
  boardStore.fetchBoard(projectId.value);
});

async function onDragEnd(columnKey: string, evt: any) {
  const { item, newIndex } = evt;
  if (!item || newIndex == null) return;
  const taskId = Number(item.dataset.taskId);
  await boardStore.moveCard(taskId, columnKey, newIndex);
}

// Open task in inline drawer (not page navigation)
function openTask(taskId: number) {
  drawerTaskId.value = taskId;
  drawerVisible.value = true;
}

function switchView(view: string) {
  router.push(`/project/${projectId.value}/${view}`);
}

const taskCountForColumn = computed(() => {
  return (columnKey: string) => {
    const col = boardStore.columns.find((c) => c.status === columnKey);
    return col?.taskCount ?? 0;
  };
});
</script>

<template>
  <div class="kanban-view">
    <!-- 加载态 -->
    <LoadingSpinner v-if="boardStore.loading" />

    <!-- 看板内容 -->
    <div v-else class="kanban-board">
      <div
        v-for="col in boardStore.columns"
        :key="col.status"
        class="kanban-column"
      >
        <!-- 列头 -->
        <div class="column-header">
          <div class="column-header-left">
            <span
              class="column-status-dot"
              :style="{ background: COLUMN_COLORS[col.status] || 'var(--status-todo)' }"
            />
            <span class="column-name">{{ COLUMN_NAMES[col.status] || col.status }}</span>
          </div>
          <span class="column-count">{{ taskCountForColumn(col.status) }}</span>
        </div>

        <!-- 任务卡片列表 -->
        <draggable
          :list="boardStore.getTasksByColumn(col.status)"
          :group="{ name: 'tasks', pull: true, put: true }"
          item-key="id"
          class="column-tasks"
          ghost-class="ghost-card"
          @end="(evt: any) => onDragEnd(col.status, evt)"
        >
          <template #item="{ element: task }">
            <div
              class="kanban-card"
              :data-task-id="task.id"
              @click="openTask(task.id)"
            >
              <!-- Epic 色条（如果设置了 epic 颜色） -->
              <div
                v-if="task.epicColor"
                class="card-epic-bar"
                :style="{ background: task.epicColor }"
              />

              <!-- 卡片主体 -->
              <div class="card-body">
                <!-- 第一行: 类型图标 + taskNo -->
                <div class="card-top-row">
                  <TypeIcon :type="task.type || 'task'" :size="13" />
                  <span class="card-task-no">{{ task.taskNo }}</span>
                </div>

                <!-- 标题 -->
                <p class="card-title">{{ task.title }}</p>

                <!-- 标签 -->
                <div v-if="task.labelNames?.length" class="card-labels">
                  <span
                    v-for="label in (task.labelNames || []).slice(0, 2)"
                    :key="label"
                    class="card-label"
                  >{{ label }}</span>
                  <span v-if="(task.labelNames || []).length > 2" class="card-label-more">
                    +{{ (task.labelNames || []).length - 2 }}
                  </span>
                </div>

                <!-- 底部: 优先级 + 负责人 + 截止日期（Jira 风格） -->
                <div class="card-footer">
                  <!-- 优先级点 -->
                  <PriorityDot :priority="task.priority" :size="7" />

                  <div class="card-footer-right">
                    <!-- 截止日期 -->
                    <span v-if="task.dueDate" class="card-due-date" :class="{ overdue: false }">
                      <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                        <line x1="16" y1="2" x2="16" y2="6"/>
                        <line x1="8" y1="2" x2="8" y2="6"/>
                        <line x1="3" y1="10" x2="21" y2="10"/>
                      </svg>
                      {{ task.dueDate?.slice(5) }}
                    </span>

                    <!-- 负责人 -->
                    <AssigneeAvatar
                      v-if="task.assigneeName"
                      :name="task.assigneeName"
                      :src="task.assigneeAvatar"
                      :size="22"
                    />
                  </div>
                </div>
              </div>
            </div>
          </template>
        </draggable>
      </div>
    </div>

    <!-- TaskDetailDrawer — 内联打开 -->
    <TaskDetailDrawer
      v-model:visible="drawerVisible"
      :task-id="drawerTaskId"
    />
  </div>
</template>

<style scoped lang="scss">
.kanban-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0;
}

.kanban-board {
  display: flex;
  gap: var(--space-4);
  overflow-x: auto;
  flex: 1;
  padding: var(--space-4) var(--space-5);
  align-items: flex-start;
  padding-bottom: var(--space-8);
}

.kanban-column {
  flex: 0 0 272px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 200px);
}

.column-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-3) var(--space-3) var(--space-2);
  gap: var(--space-2);
}

.column-header-left {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.column-status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.column-name {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.column-header-right {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.column-tasks {
  flex: 1;
  overflow-y: auto;
  padding: 0 var(--space-2) var(--space-2);
  display: flex;
  flex-direction: column;
  gap: 0;
  min-height: 48px;
}

/* Ghost card */
.ghost-card {
  opacity: 0.4;
  border: 1px dashed var(--color-primary) !important;
  background: var(--color-primary-light) !important;
}

.kanban-card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-md);
  margin-bottom: var(--space-2);
  cursor: pointer;
  transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
  overflow: hidden;

  &:hover {
    border-color: var(--border-color);
    box-shadow: var(--shadow-card);
  }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }
}

.card-epic-bar {
  height: 3px;
  width: 100%;
}

.card-body {
  padding: var(--space-3);
}

.card-top-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-bottom: var(--space-1);
}

.card-task-no {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-family: var(--font-mono);
}

.card-title {
  font-size: var(--text-sm);
  color: var(--text-primary);
  line-height: var(--leading-normal);
  margin: 0 0 var(--space-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: var(--font-medium);
}

.card-labels {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
  margin-bottom: var(--space-2);
}

.card-label {
  font-size: 10px;
  padding: 1px 5px;
  background: var(--color-primary-light);
  color: var(--color-primary);
  border-radius: var(--radius-xs);
  font-weight: var(--font-medium);
}

.card-label-more {
  font-size: 10px;
  color: var(--text-muted);
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-2);
  margin-top: var(--space-2);
}

.card-footer-right {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  margin-left: auto;
}

.card-due-date {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  color: var(--text-muted);
}
</style>
