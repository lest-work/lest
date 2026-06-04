<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useProjectStore } from '@/stores/project';
import { taskApi, type Task } from '@/api/task';
import type { Project } from '@/api/project';
import TaskDetailDrawer from '@/components/task/TaskDetailDrawer.vue';
import TypeIcon from '@/components/common/TypeIcon.vue';
import EmptyState from '@/components/common/EmptyState.vue';

const router = useRouter();
const userStore = useUserStore();
const projectStore = useProjectStore();

const loading = ref(true);
const myTasks = ref<Task[]>([]);
const watchedTasks = ref<Task[]>([]);
const dueSoonTasks = ref<Task[]>([]);
const recentProjects = ref<Project[]>([]);
const stats = ref({ todo: 0, inProgress: 0, done: 0, total: 0 });

// Task drawer state
const drawerVisible = ref(false);
const drawerTaskId = ref<number | null>(null);

function computeDueSoon(tasks: Task[], days: number): Task[] {
  const now = new Date();
  const end = new Date();
  end.setDate(end.getDate() + days);
  return tasks.filter((t) => {
    if (!t.dueDate) return false;
    if (t.status === 'completed') return false;
    const d = new Date(t.dueDate as any);
    return d >= now && d <= end;
  });
}

onMounted(async () => {
  loading.value = true;
  try {
    await projectStore.fetchProjects({ status: 'active' });
    const userId = userStore.userInfo?.id;

    // 我负责的任务
    const myRes = await taskApi.list({ size: 50, assigneeId: userId });
    myTasks.value = myRes.data.records || [];
    stats.value.total = myRes.data.total || myTasks.value.length;
    stats.value.todo = myTasks.value.filter((t: any) => t.status === 'todo').length;
    stats.value.inProgress = myTasks.value.filter((t: any) => t.status === 'in_progress').length;
    stats.value.done = myTasks.value.filter((t: any) => t.status === 'completed').length;

    // 即将到期（未来 7 天内）
    dueSoonTasks.value = computeDueSoon(myTasks.value, 7);

    // 我关注的任务（通过 /task/watched 拿到 ID 再查询详情，最多 10 条）
    try {
      const wr = await taskApi.getWatched();
      const ids = (wr.data || []).slice(0, 10);
      if (ids.length) {
        const detailRes = await Promise.all(
          ids.map((id) => taskApi.getById(id).catch(() => null)),
        );
        const tasks: Task[] = [];
        for (const r of detailRes) {
          if (r && (r as any).data) tasks.push((r as any).data as Task);
        }
        watchedTasks.value = tasks;
      } else {
        watchedTasks.value = [];
      }
    } catch {
      watchedTasks.value = [];
    }

    // 最近项目：按更新时间/创建时间排序，取前 6 个
    const projects = [...projectStore.projects];
    projects.sort((a, b) => {
      const ta = (a.updatedAt || a.createTime || a.createdAt || '') as string;
      const tb = (b.updatedAt || b.createTime || b.createdAt || '') as string;
      return tb.localeCompare(ta);
    });
    recentProjects.value = projects.slice(0, 6);
  } catch {
    // 忽略，保持空状态
  } finally {
    loading.value = false;
  }
});

function openTask(taskId: number) {
  drawerTaskId.value = taskId;
  drawerVisible.value = true;
}

const STATUS_CONFIG: Record<string, { color: string }> = {
  todo:         { color: 'var(--status-todo)' },
  in_progress:  { color: 'var(--status-in-progress)' },
  completed:    { color: 'var(--status-completed)' },
};
function getStatusColor(status: string) {
  return STATUS_CONFIG[status]?.color || 'var(--status-todo)';
}
</script>

<template>
  <div class="dashboard" :class="{ loading }">

    <!-- 统计条（Jira 风格：纯数据，无欢迎语） -->
    <div class="stats-strip">
      <button class="stat-pill" @click="router.push('/my-tasks')">
        <span class="stat-dot" style="background: var(--status-todo)" />
        <span class="stat-label">待处理</span>
        <span class="stat-count">{{ stats.todo }}</span>
      </button>
      <button class="stat-pill" @click="router.push('/my-tasks')">
        <span class="stat-dot" style="background: var(--status-in-progress)" />
        <span class="stat-label">进行中</span>
        <span class="stat-count">{{ stats.inProgress }}</span>
      </button>
      <button class="stat-pill" @click="router.push('/my-tasks')">
        <span class="stat-dot" style="background: var(--status-completed)" />
        <span class="stat-label">已完成</span>
        <span class="stat-count">{{ stats.done }}</span>
      </button>
    </div>

    <div class="dashboard-body">
      <!-- ===== 左侧主列：我的任务 + 即将到期 ===== -->
      <div class="dashboard-main">
        <div class="section">
          <div class="section-header">
            <h2 class="section-title">我的任务</h2>
            <button class="section-link" @click="router.push('/my-tasks')">
              查看全部
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="9 18 15 12 9 6"/>
              </svg>
            </button>
          </div>

          <div v-if="myTasks.length" class="task-list">
            <div
              v-for="task in myTasks"
              :key="task.id"
              class="task-row"
              @click="openTask(task.id)"
            >
              <TypeIcon :type="task.type" :size="15" />
              <span class="task-no">{{ task.taskNo }}</span>
              <span class="task-title">{{ task.title }}</span>
              <span
                class="task-status-dot"
                :style="{ background: getStatusColor(task.status) }"
              />
              <span
                v-if="task.assigneeName"
                class="task-assignee"
              >{{ task.assigneeName }}</span>
            </div>
          </div>

          <EmptyState
            v-else
            title="暂无任务"
            description="开始创建第一个任务吧"
            cta="新建任务"
            icon="task"
            @cta="router.push('/my-tasks')"
          />
        </div>

        <div class="section secondary">
          <div class="section-header">
            <h2 class="section-title">即将到期</h2>
          </div>

          <div v-if="dueSoonTasks.length" class="task-list">
            <div
              v-for="task in dueSoonTasks"
              :key="task.id"
              class="task-row"
              @click="openTask(task.id)"
            >
              <TypeIcon :type="task.type" :size="15" />
              <span class="task-no">{{ task.taskNo }}</span>
              <span class="task-title">{{ task.title }}</span>
              <span
                class="task-status-dot"
                :style="{ background: getStatusColor(task.status) }"
              />
              <span
                v-if="task.assigneeName"
                class="task-assignee"
              >{{ task.assigneeName }}</span>
            </div>
          </div>

          <EmptyState
            v-else
            title="暂无即将到期的任务"
            description="未来 7 天内没有待办任务到期"
            icon="gantt"
          />
        </div>
      </div>

      <!-- ===== 右侧列：我关注 + 最近项目 ===== -->
      <div class="dashboard-side">
        <div class="section">
          <div class="section-header">
            <h2 class="section-title">我关注的任务</h2>
          </div>

          <div v-if="watchedTasks.length" class="task-list">
            <div
              v-for="task in watchedTasks"
              :key="task.id"
              class="task-row"
              @click="openTask(task.id)"
            >
              <TypeIcon :type="task.type" :size="15" />
              <span class="task-no">{{ task.taskNo }}</span>
              <span class="task-title">{{ task.title }}</span>
              <span
                class="task-status-dot"
                :style="{ background: getStatusColor(task.status) }"
              />
              <span
                v-if="task.assigneeName"
                class="task-assignee"
              >{{ task.assigneeName }}</span>
            </div>
          </div>

          <EmptyState
            v-else
            title="暂无关注的任务"
            description="在任务详情中点击“关注”即可在此快速访问"
            icon="inbox"
          />
        </div>

        <div class="section secondary">
          <div class="section-header">
            <h2 class="section-title">最近项目</h2>
          </div>

          <div v-if="recentProjects.length" class="project-list">
            <button
              v-for="p in recentProjects"
              :key="p.id || p.projectId"
              class="project-row"
              @click="router.push(`/project/${p.id || p.projectId}`)"
            >
              <span class="project-avatar">{{ p.name?.charAt(0) || 'P' }}</span>
              <div class="project-meta">
                <div class="project-name">{{ p.name }}</div>
                <div class="project-sub">{{ p.projectKey }} · {{ p.ownerName || '未指定负责人' }}</div>
              </div>
            </button>
          </div>

          <EmptyState
            v-else
            title="暂无项目"
            description="创建一个项目开始你的工作吧"
            icon="project"
            @cta="router.push('/projects')"
          />
        </div>
      </div>
    </div>

  </div>

  <!-- Task Detail Drawer -->
  <TaskDetailDrawer v-model:visible="drawerVisible" :task-id="drawerTaskId" />
</template>

<style scoped lang="scss">
.dashboard {
  max-width: 1100px;
  margin: 0 auto;
  font-family: var(--font-primary);
  padding: var(--space-6) 0;
}

.stats-strip {
  display: flex;
  gap: var(--space-3);
  margin-bottom: var(--space-6);
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-2);
}

.stat-pill {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-4);
  border: none;
  background: transparent;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--transition-fast);
  font-family: var(--font-primary);

  &:hover { background: var(--bg-hover); }
  &:focus-visible { outline: 2px solid var(--color-primary); outline-offset: 2px; }
}

.stat-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  flex: 1;
  text-align: left;
}

.stat-count {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  line-height: 1;
}

.dashboard-body {
  display: grid;
  grid-template-columns: 2fr 1.2fr;
  gap: var(--space-6);
}

.dashboard-main,
.dashboard-side {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.section { margin-bottom: 0; }
.section.secondary { margin-bottom: 0; }

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

.section-title {
  font-size: var(--text-md);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.section-link {
  display: flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: transparent;
  color: var(--text-link);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  transition: opacity var(--transition-fast);

  &:hover { opacity: 0.7; }
}

.task-list {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.task-row {
  display: grid;
  grid-template-columns: 20px 72px 1fr 10px 80px;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  cursor: pointer;
  border-bottom: 1px solid var(--border-light);
  transition: background var(--transition-fast);

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }
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

.task-status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.task-assignee {
  font-size: var(--text-xs);
  color: var(--text-muted);
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.project-list {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-2);
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.project-row {
  display: flex;
  align-items: center;
  width: 100%;
  border: none;
  background: transparent;
  cursor: pointer;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  text-align: left;
  transition: background var(--transition-fast);

  &:hover { background: var(--bg-hover); }
}

.project-avatar {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: var(--color-primary-soft);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  margin-right: var(--space-3);
}

.project-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.project-name {
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-weight: var(--font-medium);
}

.project-sub {
  font-size: var(--text-xs);
  color: var(--text-secondary);
}
</style>
