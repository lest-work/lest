<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { taskApi, type Task, type TaskStatus, type TaskComment, type TaskWorklog, type TaskDependency, type TaskCommit } from '@/api/task';
import { usePluginStore } from '@/stores/plugin';

const props = defineProps<{
  visible: boolean;
  taskId: number | null;
}>();

const emit = defineEmits<{
  (e: 'update:visible', val: boolean): void;
  (e: 'updated', task: Task): void;
}>();

const task = ref<Task | null>(null);
const loading = ref(false);
const saving = ref(false);

const draftKey = computed(() => props.taskId ? `task-draft-${props.taskId}` : '');
const draftSaved = ref(false);
const editingTitle = ref(false);
const editingDescription = ref(false);
let saveTimer: ReturnType<typeof setTimeout> | null = null;

interface DraftData { title: string; description: string | undefined; }

function loadDraft() {
  if (!draftKey.value) return;
  const raw = localStorage.getItem(draftKey.value);
  if (raw) {
    try {
      const draft: DraftData = JSON.parse(raw);
      if (task.value) {
        task.value = { ...task.value, title: draft.title, description: draft.description };
      }
      draftSaved.value = true;
    } catch { /* ignore */ }
  } else {
    draftSaved.value = false;
  }
}

function saveDraft() {
  if (!draftKey.value || !task.value) return;
  draftSaved.value = true;
  const draft: DraftData = { title: task.value.title ?? '', description: task.value.description };
  localStorage.setItem(draftKey.value, JSON.stringify(draft));
}

function scheduleSaveDraft() {
  if (saveTimer) clearTimeout(saveTimer);
  saveTimer = setTimeout(() => { saveDraft(); }, 500);
}

function clearDraft() {
  if (draftKey.value) localStorage.removeItem(draftKey.value);
  draftSaved.value = false;
}

function startEditTitle() { editingTitle.value = true; }
function confirmTitle() {
  editingTitle.value = false;
  scheduleSaveDraft();
}
function startEditDescription() { editingDescription.value = true; }
function confirmDescription() {
  editingDescription.value = false;
  scheduleSaveDraft();
}

const statusOptions = [
  { value: 'todo', label: '待办', color: 'var(--status-todo)' },
  { value: 'in_progress', label: '进行中', color: 'var(--status-in-progress)' },
  { value: 'completed', label: '已完成', color: 'var(--status-completed)' },
];

const priorityOptions = [
  { value: 'highest', label: '最高', color: 'var(--priority-p0)' },
  { value: 'high', label: '高', color: 'var(--priority-p1)' },
  { value: 'medium', label: '中', color: 'var(--priority-p2)' },
  { value: 'low', label: '低', color: 'var(--priority-p3)' },
  { value: 'lowest', label: '最低', color: 'var(--priority-none)' },
];

const typeOptions = [
  { value: 'epic', label: 'Epic', color: 'var(--type-epic)' },
  { value: 'story', label: 'Story', color: 'var(--type-story)' },
  { value: 'task', label: 'Task', color: 'var(--type-task)' },
  { value: 'bug', label: 'Bug', color: 'var(--type-bug)' },
];

const typeEmoji: Record<string, string> = {
  epic: '🎯', story: '📄', task: '☑', bug: '🐛',
};

function getStatusStyle(status: string) {
  return statusOptions.find(s => s.value === status) || statusOptions[0];
}
function getPriorityStyle(priority: string) {
  return priorityOptions.find(p => p.value === priority) || priorityOptions[2];
}
function getTypeStyle(type: string) {
  return typeOptions.find(t => t.value === type) || typeOptions[2];
}

// 
const subtasks = ref<Task[]>([]);
const subtasksLoading = ref(false);
const showSubtaskForm = ref(false);
const subtaskForm = ref({ title: '', taskType: 'task' as string });
const completedCount = computed(() => subtasks.value.filter(s => s.status === 'completed').length);

const pluginStore = usePluginStore();
const pluginDetailTabs = computed(() =>
  pluginStore.getExtensions('task.detail.tab')
);

const comments = ref<TaskComment[]>([]);
const commentsLoading = ref(false);
const newComment = ref('');

interface FieldChange {
  id: number;
  type: 'field';
  field: string;
  fieldLabel: string;
  fromValue: string;
  toValue: string;
  userName: string;
  createTime: string;
}

interface CommitEntry {
  id: number;
  type: 'commit';
  userName: string;
  createTime: string;
  message: string;
  sha: string;
  url?: string;
}

type ActivityItem =
  | (TaskComment & { type: 'comment' })
  | CommitEntry
  | FieldChange;

// Track initial task values for field-change detection
let _initialTask: Task | null = null;
// Track field changes triggered by user actions
const fieldChanges = ref<FieldChange[]>([]);

const activityFeed = computed<ActivityItem[]>(() => {
  const items: ActivityItem[] = [];

  for (const c of comments.value) {
    items.push({ ...c, type: 'comment' });
  }

  for (const c of commits.value) {
    items.push({
      id: c.id || 0,
      type: 'commit',
      userName: c.author || 'Unknown',
      createTime: c.date || c.createTime || '',
      message: c.commitMessage || '',
      sha: c.commitHash?.substring(0, 7) || '',
      url: c.url,
    });
  }

  for (const f of fieldChanges.value) {
    items.push(f);
  }

  return items.sort((a, b) => {
    const ta = (a as any).createTime || (a as any).createdAt || '';
    const tb = (b as any).createTime || (b as any).createdAt || '';
    return ta.localeCompare(tb);
  });
});

const worklogs = ref<TaskWorklog[]>([]);
const worklogsLoading = ref(false);
const showWorklogForm = ref(false);
const worklogForm = ref({ hours: 0, workDate: '', description: '' });

const dependencies = ref<TaskDependency[]>([]);
const depsLoading = ref(false);
const showDepForm = ref(false);
const depForm = ref({ dependencyTaskId: 0, type: 'blocker' });

const commits = ref<TaskCommit[]>([]);
const mrs = ref<TaskCommit[]>([]);
const commitsLoading = ref(false);

const watchedByMe = ref(false);

watch(() => props.taskId, async (id) => {
  if (!id) { task.value = null; _initialTask = null; return; }
  loading.value = true;
  fieldChanges.value = [];
  try {
    const res = await taskApi.getById(id);
    task.value = res.data;
    _initialTask = JSON.parse(JSON.stringify(res.data));
    editingTitle.value = false;
    editingDescription.value = false;
    loadDraft();
    await Promise.all([
      loadSubtasks(id), loadComments(id), loadWorklogs(id),
      loadDependencies(id), loadCommits(id),
    ]);
    try {
      const wr = await taskApi.getWatched();
      watchedByMe.value = (wr.data || []).includes(id);
    } catch { watchedByMe.value = false; }
  } catch {
    task.value = null;
    _initialTask = null;
  } finally {
    loading.value = false;
  }
}, { immediate: true });

async function loadSubtasks(id: number) {
  subtasksLoading.value = true;
  try { const r = await taskApi.subtaskList(id); subtasks.value = r.data || []; }
  catch { subtasks.value = []; }
  finally { subtasksLoading.value = false; }
}
async function loadComments(id: number) {
  commentsLoading.value = true;
  try { const r = await taskApi.commentList(id); comments.value = r.data || []; }
  catch { comments.value = []; }
  finally { commentsLoading.value = false; }
}
async function loadWorklogs(id: number) {
  worklogsLoading.value = true;
  try { const r = await taskApi.worklogList(id); worklogs.value = r.data?.records || []; }
  catch { worklogs.value = []; }
  finally { worklogsLoading.value = false; }
}
async function loadDependencies(id: number) {
  depsLoading.value = true;
  try { const r = await taskApi.dependencyList(id); dependencies.value = r.data || []; }
  catch { dependencies.value = []; }
  finally { depsLoading.value = false; }
}
async function loadCommits(id: number) {
  commitsLoading.value = true;
  try {
    const [cr, mr] = await Promise.all([taskApi.commitList(id), taskApi.mergeRequestList(id)]);
    commits.value = cr.data || [];
    mrs.value = mr.data || [];
  } catch { commits.value = []; mrs.value = []; }
  finally { commitsLoading.value = false; }
}

function close() { emit('update:visible', false); }

async function updateStatus(status: TaskStatus) {
  if (!task.value) return;
  const prev = task.value.status;
  saving.value = true;
  try {
    await taskApi.updateStatus(task.value.id!, status);
    task.value = { ...task.value, status };
    if (prev !== status) {
      const statusLabels: Record<string, string> = { todo: '待办', in_progress: '进行中', completed: '已完成' };
      fieldChanges.value.push({
        id: Date.now(),
        type: 'field',
        field: 'status',
        fieldLabel: '状态',
        fromValue: statusLabels[prev] || prev,
        toValue: statusLabels[status] || status,
        userName: '我',
        createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
      });
    }
    clearDraft();
    emit('updated', task.value);
  } catch (e: any) {
    ElMessage.error(e.message || '状态更新失败');
  } finally { saving.value = false; }
}

function formatCommentContent(raw: string): string {
  if (!raw) return '';
  let s = raw
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
  s = s.replace(/(^|\s)@([A-Za-z0-9_.+\-@]+)/g, '$1<span class="mention">@$2</span>');
  s = s.replace(/\n/g, '<br/>');
  return s;
}

async function addSubtask() {
  if (!task.value || !subtaskForm.value.title.trim()) return;
  saving.value = true;
  try {
    await taskApi.createSubtask(task.value.id!, { title: subtaskForm.value.title, taskType: subtaskForm.value.taskType as any, projectId: task.value.projectId });
    subtaskForm.value = { title: '', taskType: 'task' };
    showSubtaskForm.value = false;
    await loadSubtasks(task.value.id!);
  } catch (e: any) {
    ElMessage.error(e.message || '子任务创建失败');
  } finally { saving.value = false; }
}

const KEY_COMMENT = 'comment-textarea';
const editingCommentId = ref<number | null>(null);
const editingCommentContent = ref('');

async function addComment() {
  if (!task.value || !newComment.value.trim()) return;
  saving.value = true;
  try {
    await taskApi.addComment(task.value.id!, newComment.value);
    newComment.value = '';
    await loadComments(task.value.id!);
  } catch (e: any) {
    ElMessage.error(e.message || '评论发送失败');
  } finally { saving.value = false; }
}

function startEditComment(comment: TaskComment) {
  const id = (comment as any).id || comment.taskCommentId;
  editingCommentId.value = id ?? null;
  editingCommentContent.value = comment.content || '';
}

async function saveCommentEdit() {
  if (!task.value || !editingCommentId.value || !editingCommentContent.value.trim()) return;
  saving.value = true;
  try {
    await taskApi.updateComment(task.value.id!, editingCommentId.value, editingCommentContent.value);
    editingCommentId.value = null;
    editingCommentContent.value = '';
    await loadComments(task.value.id!);
  } catch (e: any) {
    ElMessage.error(e.message || '评论更新失败');
  } finally { saving.value = false; }
}

function cancelEditComment() {
  editingCommentId.value = null;
  editingCommentContent.value = '';
}

async function deleteComment(id: number) {
  if (!task.value) return;
  saving.value = true;
  try {
    await taskApi.removeComment(task.value.id!, id);
    await loadComments(task.value.id!);
  } catch (e: any) {
    ElMessage.error(e.message || '删除评论失败');
  } finally { saving.value = false; }
}

async function addWorklog() {
  if (!task.value || !worklogForm.value.hours || !worklogForm.value.workDate) return;
  saving.value = true;
  try {
    await taskApi.addWorklog(task.value.id!, worklogForm.value);
    worklogForm.value = { hours: 0, workDate: '', description: '' };
    showWorklogForm.value = false;
    await loadWorklogs(task.value.id!);
  } finally { saving.value = false; }
}

async function addDep() {
  if (!task.value || !depForm.value.dependencyTaskId) return;
  saving.value = true;
  try {
    await taskApi.addDependency(task.value.id!, depForm.value.dependencyTaskId, depForm.value.type);
    depForm.value = { dependencyTaskId: 0, type: 'blocker' };
    showDepForm.value = false;
    await loadDependencies(task.value.id!);
  } finally { saving.value = false; }
}

async function removeDep(depTaskId: number) {
  if (!task.value) return;
  try {
    await taskApi.removeDependency(task.value.id!, depTaskId);
    await loadDependencies(task.value.id!);
  } catch { /* ignore */ }
}

async function toggleWatch() {
  if (!task.value) return;
  saving.value = true;
  try {
    if (watchedByMe.value) {
      await taskApi.unwatch(task.value.id!);
      watchedByMe.value = false;
    } else {
      await taskApi.watch(task.value.id!);
      watchedByMe.value = true;
    }
  } finally { saving.value = false; }
}
</script>

<template>
  <!--
    TaskDetailDrawer — Linear/Jira 规范设计
    - 全宽面板 (80% viewport width)
    - 不使用 Tab — 所有区域在同一页面展开
    - 左侧: 工作区 (描述/子任务/活动)  右侧: 属性面板 (Notion 风格)
  -->
  <Teleport to="body">
    <div v-if="visible" class="task-detail-overlay" @click.self="close">
      <div class="task-detail-panel">

        <!-- ===== Panel Header ===== -->
        <div class="detail-header">
          <button class="close-btn" @click="close" title="关闭">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>

          <!-- Header: type + taskNo + actions -->
          <div class="header-meta" v-if="task">
            <span
              class="type-chip"
              :style="{ color: getTypeStyle(task.taskType || task.type || 'task').color }"
            >
              {{ typeEmoji[task.taskType || task.type || 'task'] || '☑' }}
              {{ getTypeStyle(task.taskType || task.type || 'task').label }}
            </span>
            <span class="task-no">{{ task.taskNo }}</span>
          </div>

          <!-- Header actions: watch + more -->
          <div class="header-actions" v-if="task">
            <button
              class="action-icon-btn"
              :class="{ active: watchedByMe }"
              @click="toggleWatch"
              :title="watchedByMe ? '取消关注' : '关注此任务'"
            >
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 01-3.46 0" v-if="!watchedByMe"/>
                <path d="M9.5 9.5L18 1m0 0l-3.5 3.5M18 1l-8.5 8.5" v-else stroke-width="2.5"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- ===== Loading ===== -->
        <div v-if="loading" class="detail-loading">
          <div class="loading-pulse" />
        </div>

        <!-- ===== Content: Two-column layout ===== -->
        <div v-else-if="task" class="detail-body">

          <!-- === Left Column: Work Area === -->
          <div class="work-area">

            <!-- Title -->
            <div class="task-title-area">
              <div v-if="!editingTitle" class="task-title-display" @click="startEditTitle">
                <h1 class="task-title">{{ task.title || '未填写标题' }}</h1>
                <span v-if="draftSaved" class="unsaved-dot" title="有未保存的更改" />
              </div>
              <input
                v-else
                v-model="task.title"
                class="task-title-input"
                placeholder="请输入标题"
                @blur="confirmTitle"
                @keydown.enter.prevent="confirmTitle"
                @input="scheduleSaveDraft"
                autofocus
              />
            </div>

            <!-- Description -->
            <div class="desc-section">
              <div class="section-label-row">
                <span class="section-label">描述</span>
              </div>
              <div v-if="!editingDescription" class="desc-display" @click="startEditDescription">
                <p v-if="task.description" class="desc-text">{{ task.description }}</p>
                <p v-else class="desc-placeholder">添加描述...</p>
              </div>
              <textarea
                v-else
                v-model="task.description"
                class="desc-input"
                placeholder="描述此任务的背景、目标和复现步骤..."
                rows="5"
                @blur="confirmDescription"
                @input="scheduleSaveDraft"
              />
            </div>

            <!-- Subtasks -->
            <div class="subtasks-section">
              <div class="section-label-row">
                <span class="section-label">
                  子任务
                  <span v-if="subtasks.length > 0" class="subtask-progress">
                    {{ completedCount }}/{{ subtasks.length }}
                  </span>
                </span>
                <button class="inline-add-btn" @click="showSubtaskForm = !showSubtaskForm">
                  <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                  添加子任务
                </button>
              </div>

              <!-- Progress bar -->
              <div v-if="subtasks.length > 0" class="subtask-progress-bar">
                <div
                  class="subtask-progress-fill"
                  :style="{ width: `${(completedCount / subtasks.length) * 100}%` }"
                />
              </div>

              <!-- New subtask form -->
              <div v-if="showSubtaskForm" class="subtask-form">
                <input
                  v-model="subtaskForm.title"
                  class="subtask-form-input"
                  placeholder="子任务标题"
                  @keydown.enter.prevent="addSubtask"
                />
                <select v-model="subtaskForm.taskType" class="subtask-form-select">
                  <option value="task">Task</option>
                  <option value="bug">Bug</option>
                </select>
                <button class="subtask-form-confirm" @click="addSubtask" :disabled="saving || !subtaskForm.title.trim()">添加</button>
                <button class="subtask-form-cancel" @click="showSubtaskForm = false">取消</button>
              </div>

              <!-- Subtask list -->
              <div v-if="subtasksLoading" class="section-loading"><div class="loading-dots"><span/><span/><span/></div></div>
              <div v-else-if="subtasks.length === 0" class="section-empty">暂无子任务</div>
              <div v-else class="subtask-list">
                <div v-for="sub in subtasks" :key="sub.id" class="subtask-row">
                  <span class="subtask-check" :class="{ done: sub.status === 'completed' }">
                    <svg v-if="sub.status === 'completed'" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                      <polyline points="20 6 9 17 4 12"/>
                    </svg>
                  </span>
                  <span class="subtask-title" :class="{ done: sub.status === 'completed' }">{{ sub.title }}</span>
                  <span
                    class="subtask-type"
                    :style="{ color: getTypeStyle(sub.taskType || sub.type || 'task').color }"
                  >{{ typeEmoji[sub.taskType || sub.type || 'task'] }}</span>
                </div>
              </div>
            </div>

            <!-- Activity Feed (comments + commits + field changes) -->
            <div class="activity-section">
              <div class="section-label-row">
                <span class="section-label">活动</span>
                <span class="activity-count">{{ activityFeed.length }} 条</span>
              </div>

              <!-- Comment input -->
              <div class="comment-box">
                <textarea
                  :id="KEY_COMMENT"
                  v-model="newComment"
                  class="comment-input"
                  placeholder="添加评论或回复... (Ctrl+Enter 发送)"
                  rows="2"
                  @keydown.ctrl.enter.prevent="addComment"
                />
                <div class="comment-actions">
                  <button
                    class="comment-submit"
                    :disabled="saving || !newComment.trim()"
                    @click="addComment"
                  >发送</button>
                </div>
              </div>

              <!-- Unified Activity list -->
              <div v-if="commentsLoading" class="section-loading"><div class="loading-dots"><span/><span/><span/></div></div>
              <div v-else-if="activityFeed.length === 0" class="section-empty">暂无活动记录</div>
              <div v-else class="activity-list">
                <div
                  v-for="item in activityFeed"
                  :key="`${item.type}-${(item as any).id || (item as any).taskCommentId || (item as any).createTime || Math.random()}`"
                  class="activity-item"
                >
                  <!-- Field change -->
                  <template v-if="item.type === 'field'">
                    <div class="activity-avatar field-change-icon">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
                        <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
                      </svg>
                    </div>
                    <div class="activity-content">
                      <div class="activity-meta">
                        <span class="activity-user">{{ (item as any).userName }}</span>
                        <span class="activity-action">更新了</span>
                        <span class="activity-field">{{ (item as FieldChange).fieldLabel }}</span>
                        <span class="activity-time">{{ (item as any).createTime }}</span>
                      </div>
                      <div class="field-change-body">
                        <span class="field-old">{{ (item as FieldChange).fromValue }}</span>
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="field-arrow">
                          <line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>
                        </svg>
                        <span class="field-new">{{ (item as FieldChange).toValue }}</span>
                      </div>
                    </div>
                  </template>

                  <!-- Commit -->
                  <template v-else-if="item.type === 'commit'">
                    <div class="activity-avatar commit-icon">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="4"/><line x1="1.05" y1="12" x2="7" y2="12"/>
                        <line x1="17.01" y1="12" x2="22.96" y2="12"/>
                      </svg>
                    </div>
                    <div class="activity-content">
                      <div class="activity-meta">
                        <span class="activity-user">{{ (item as CommitEntry).userName }}</span>
                        <span class="activity-action">提交了代码</span>
                        <span class="activity-time">{{ (item as CommitEntry).createTime }}</span>
                      </div>
                      <div class="commit-body">
                        <span class="commit-sha">{{ (item as CommitEntry).sha }}</span>
                        <span class="commit-msg">{{ (item as CommitEntry).message }}</span>
                      </div>
                    </div>
                  </template>

                  <!-- Comment -->
                  <template v-else>
                    <div class="activity-avatar">
                      <div class="comment-avatar">{{ (item as any).userName?.charAt(0) || 'U' }}</div>
                    </div>
                    <div class="activity-content">
                      <div class="activity-meta">
                        <span class="activity-user">{{ (item as any).userName || '未知用户' }}</span>
                        <span class="activity-time">{{ (item as any).createTime }}</span>
                      </div>
                      <div class="activity-body">
                        <div class="comment-bubble">{{ (item as any).content }}</div>
                        <button class="activity-delete" @click="deleteComment((item as any).id || (item as any).taskCommentId)">删除</button>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>

          </div>

          <!-- === Right Column: Property Panel === -->
          <div class="property-panel">

            <!-- Status (most prominent) -->
            <div class="prop-section">
              <div class="prop-row prop-status-row">
                <span class="prop-key">状态</span>
                <div class="prop-val">
                  <span class="status-chip" :style="{ background: getStatusStyle(task.status).color + '20', color: getStatusStyle(task.status).color }">
                    <span class="status-dot" :style="{ background: getStatusStyle(task.status).color }"/>
                    {{ getStatusStyle(task.status).label }}
                  </span>
                  <select class="prop-select" :value="task.status" @change="(e) => updateStatus((e.target as HTMLSelectElement).value as TaskStatus)">
                    <option v-for="s in statusOptions" :key="s.value" :value="s.value">{{ s.label }}</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Priority -->
            <div class="prop-section">
              <div class="prop-row">
                <span class="prop-key">优先级</span>
                <div class="prop-val">
                  <span class="priority-indicator" :style="{ background: getPriorityStyle(task.priority).color }"/>
                  {{ getPriorityStyle(task.priority).label }}
                </div>
              </div>
            </div>

            <!-- Assignee -->
            <div class="prop-section">
              <div class="prop-row">
                <span class="prop-key">指派给</span>
                <div class="prop-val prop-assignee">
                  <div class="prop-avatar">{{ task.assigneeName?.charAt(0) || '?' }}</div>
                  <span>{{ task.assigneeName || '未分配' }}</span>
                </div>
              </div>
            </div>

            <!-- Reporter -->
            <div class="prop-section">
              <div class="prop-row">
                <span class="prop-key">报告人</span>
                <div class="prop-val">
                  <div class="prop-avatar">{{ task.reporterName?.charAt(0) || '-' }}</div>
                  <span>{{ task.reporterName || '-' }}</span>
                </div>
              </div>
            </div>

            <div class="prop-divider"/>

            <!-- Iteration -->
            <div v-if="task.iterationName" class="prop-section">
              <div class="prop-row">
                <span class="prop-key">迭代</span>
                <div class="prop-val">
                  <span class="iteration-badge">{{ task.iterationName }}</span>
                </div>
              </div>
            </div>

            <!-- Due date -->
            <div v-if="task.dueDate" class="prop-section">
              <div class="prop-row">
                <span class="prop-key">截止日期</span>
                <div class="prop-val">{{ task.dueDate }}</div>
              </div>
            </div>

            <!-- Story points -->
            <div v-if="task.storyPoints" class="prop-section">
              <div class="prop-row">
                <span class="prop-key">故事点</span>
                <div class="prop-val">{{ task.storyPoints }} SP</div>
              </div>
            </div>

            <div class="prop-divider"/>

            <!-- Time tracking -->
            <div class="prop-section">
              <div class="prop-row">
                <span class="prop-key">预估工时</span>
                <div class="prop-val">
                  <span v-if="task.estimateHours">{{ task.estimateHours }}h</span>
                  <span v-else class="prop-empty">-</span>
                </div>
              </div>
            </div>
            <div class="prop-section">
              <div class="prop-row">
                <span class="prop-key">已记录</span>
                <div class="prop-val">
                  <span v-if="task.actualHours">{{ task.actualHours }}h</span>
                  <span v-else class="prop-empty">-</span>
                  <button v-if="worklogs.length > 0" class="prop-link" @click="showWorklogForm = !showWorklogForm">查看</button>
                </div>
              </div>
            </div>

            <!-- Worklog form -->
            <div v-if="showWorklogForm" class="worklog-form">
              <div class="worklog-form-row">
                <input
                  v-model.number="worklogForm.hours"
                  type="number"
                  class="worklog-input"
                  placeholder="小时"
                  min="0.5"
                  step="0.5"
                />
                <input
                  v-model="worklogForm.workDate"
                  type="date"
                  class="worklog-input worklog-date"
                />
                <button class="worklog-confirm" @click="addWorklog" :disabled="saving">记录</button>
              </div>
            </div>

            <div class="prop-divider"/>

            <!-- Labels -->
            <div v-if="task.labelNames?.length" class="prop-section">
              <div class="prop-row prop-row-top">
                <span class="prop-key">标签</span>
                <div class="prop-val prop-labels">
                  <span v-for="label in task.labelNames" :key="label" class="label-chip">{{ label }}</span>
                </div>
              </div>
            </div>

            <!-- Dependencies -->
            <div class="prop-section">
              <div class="prop-row prop-row-top">
                <span class="prop-key">前置任务</span>
                <div class="prop-val prop-deps">
                  <template v-if="dependencies.length > 0">
                    <div v-for="d in dependencies" :key="d.dependencyTaskId" class="dep-row">
                      <span
                        class="dep-status-dot"
                        :style="{ background: d.dependencyTaskStatus === 'completed' ? 'var(--status-completed)' : d.dependencyTaskStatus === 'in_progress' ? 'var(--status-in-progress)' : 'var(--status-todo)' }"
                      />
                      <span class="dep-title">#{{ d.dependencyTaskId }}</span>
                      <button class="dep-remove" @click="removeDep(d.dependencyTaskId)">
                        <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                        </svg>
                      </button>
                    </div>
                  </template>
                  <button class="prop-link" @click="showDepForm = !showDepForm">+ 添加依赖</button>
                </div>
              </div>
            </div>

            <!-- Dep form -->
            <div v-if="showDepForm" class="dep-form">
              <input
                v-model.number="depForm.dependencyTaskId"
                type="number"
                class="dep-form-input"
                placeholder="任务 ID"
                min="1"
              />
              <button class="dep-form-confirm" @click="addDep" :disabled="saving || !depForm.dependencyTaskId">添加</button>
            </div>

            <div class="prop-divider"/>

            <!-- Metadata -->
            <div class="prop-section prop-meta">
              <div class="prop-row">
                <span class="prop-key">创建</span>
                <div class="prop-val prop-muted">{{ task.createTime || '-' }}</div>
              </div>
              <div class="prop-row">
                <span class="prop-key">更新</span>
                <div class="prop-val prop-muted">{{ task.updateTime || '-' }}</div>
              </div>
            </div>

          </div>
        </div>

        <!-- Plugin Extension Slots -->
        <div v-if="pluginDetailTabs.length > 0" class="plugin-tabs-section">
          <div class="plugin-tabs-header">
            <span class="plugin-tabs-label">插件</span>
          </div>
          <div v-for="tab in pluginDetailTabs" :key="tab.id" class="plugin-tab-panel">
            <div class="plugin-tab-title">{{ tab.label }}</div>
            <!-- 插件可注入任意内容，component 由插件系统动态加载 -->
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<style scoped lang="scss">
.task-detail-overlay {
  position: fixed;
  inset: 0;
  background: transparent;
  z-index: 1000;
  display: flex;
  align-items: stretch;
  justify-content: flex-end;
}

.task-detail-panel {
  width: 100%;
  height: 100vh;
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-modal);
  animation: slideInRight 0.2s ease;
  overflow: hidden;
}

@keyframes slideInRight {
  from { transform: translateX(40px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.detail-header {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-4) var(--space-6);
  border-bottom: 1px solid var(--border-color);
  flex-shrink: 0;
  background: var(--bg-primary);
}

.close-btn {
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

  &:hover {
    background: var(--bg-hover);
    color: var(--text-primary);
  }
}

.header-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex: 1;

  .type-chip {
    font-size: var(--text-sm);
    font-weight: var(--font-semibold);
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .task-no {
    font-size: var(--text-sm);
    color: var(--text-muted);
    font-family: var(--font-mono);
  }
}

.header-actions {
  display: flex;
  gap: var(--space-1);
}

.action-icon-btn {
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

  &:hover {
    background: var(--bg-hover);
    color: var(--text-secondary);
  }

  &.active {
    color: var(--color-primary);
    background: var(--color-primary-light);
  }
}

.detail-loading {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-pulse {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-primary);
  animation: pulse 1.2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.3; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1); }
}

.section-loading {
  display: flex;
  justify-content: center;
  padding: var(--space-6);
}

.loading-dots {
  display: flex;
  gap: 4px;
  span {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: var(--text-muted);
    animation: dotPulse 1.2s ease-in-out infinite;
    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}

@keyframes dotPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

.detail-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* Left: Work Area */
.work-area {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
  border-right: 1px solid var(--border-color);
}

/* Right: Property Panel */
.property-panel {
  width: 280px;
  min-width: 280px;
  overflow-y: auto;
  padding: var(--space-5);
  background: var(--bg-secondary);
  display: flex;
  flex-direction: column;
  gap: 0;
}


/* Task Title */
.task-title-area { }

.task-title-display {
  position: relative;
  cursor: text;
  display: inline-block;
  width: 100%;

  &:hover .task-title { color: var(--color-primary); }
}

.task-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  line-height: var(--leading-tight);
  margin: 0;
  padding: var(--space-1) 0;
  transition: color var(--transition-fast);
}

.unsaved-dot {
  position: absolute;
  top: var(--space-2);
  right: -16px;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--color-primary);
  animation: dotPulse 2s ease-in-out infinite;
}

.task-title-input {
  width: 100%;
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  line-height: var(--leading-tight);
  border: none;
  border-bottom: 2px solid var(--color-primary);
  background: transparent;
  padding: var(--space-1) 0;
  outline: none;
  font-family: var(--font-primary);
}

/* Description */
.desc-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.section-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-label {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.subtask-progress {
  background: var(--bg-tertiary);
  color: var(--text-muted);
  font-size: 11px;
  padding: 1px 6px;
  border-radius: var(--radius-full);
  font-weight: var(--font-normal);
  text-transform: none;
  letter-spacing: 0;
}

.inline-add-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: transparent;
  color: var(--text-muted);
  font-size: var(--text-xs);
  cursor: pointer;
  padding: 4px 6px;
  border-radius: var(--radius-sm);
  font-family: var(--font-primary);
  transition: background var(--transition-fast), color var(--transition-fast);

  &:hover {
    background: var(--bg-hover);
    color: var(--color-primary);
  }
}

.desc-display {
  cursor: text;
  padding: var(--space-3);
  border-radius: var(--radius-md);
  border: 1px solid transparent;
  transition: border-color var(--transition-fast);

  &:hover { border-color: var(--border-color); }
}

.desc-text {
  font-size: var(--text-base);
  color: var(--text-primary);
  line-height: var(--leading-relaxed);
  white-space: pre-wrap;
  margin: 0;
}

.desc-placeholder {
  font-size: var(--text-base);
  color: var(--text-muted);
  font-style: italic;
  margin: 0;
}

.desc-input {
  width: 100%;
  font-size: var(--text-base);
  color: var(--text-primary);
  line-height: var(--leading-relaxed);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  padding: var(--space-3);
  resize: vertical;
  outline: none;
  font-family: var(--font-primary);
  transition: border-color var(--transition-fast);

  &:focus { border-color: var(--color-primary); }
}

/* Subtasks */
.subtasks-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.subtask-progress-bar {
  height: 3px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.subtask-progress-fill {
  height: 100%;
  background: var(--status-completed);
  border-radius: var(--radius-full);
  transition: width 0.3s ease;
}

.subtask-form {
  display: flex;
  gap: var(--space-2);
  align-items: center;
  padding: var(--space-2);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
}

.subtask-form-input {
  flex: 1;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: var(--space-2) var(--space-3);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  outline: none;
  background: var(--bg-primary);
  color: var(--text-primary);
  &:focus { border-color: var(--color-primary); }
}

.subtask-form-select {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  outline: none;
  background: var(--bg-primary);
  color: var(--text-secondary);
  cursor: pointer;
}

.subtask-form-confirm {
  padding: var(--space-1) var(--space-3);
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.subtask-form-cancel {
  padding: var(--space-1) var(--space-3);
  background: transparent;
  color: var(--text-muted);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  &:hover { color: var(--text-secondary); }
}

.section-empty {
  text-align: center;
  color: var(--text-muted);
  font-size: var(--text-sm);
  padding: var(--space-6) 0;
}

.subtask-list {
  display: flex;
  flex-direction: column;
}

.subtask-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast);

  &:hover { background: var(--bg-hover); }
}

.subtask-check {
  width: 18px;
  height: 18px;
  border-radius: var(--radius-sm);
  border: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: var(--status-completed);

  &.done {
    background: var(--status-completed);
    border-color: var(--status-completed);
    color: #fff;
  }
}

.subtask-title {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-primary);
  &.done { text-decoration: line-through; color: var(--text-muted); }
}

.subtask-type { font-size: var(--text-sm); }

/* Activity */
.activity-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.activity-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-weight: var(--font-normal);
  text-transform: none;
}

.comment-box {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.comment-input {
  width: 100%;
  font-size: var(--text-sm);
  color: var(--text-primary);
  line-height: var(--leading-normal);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  background: var(--bg-primary);
  padding: var(--space-3);
  resize: none;
  outline: none;
  font-family: var(--font-primary);
  transition: border-color var(--transition-fast);

  &:focus { border-color: var(--color-primary); }
  &::placeholder { color: var(--text-muted); }
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.comment-submit {
  padding: var(--space-2) var(--space-4);
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast);

  &:hover { background: var(--color-primary-hover); }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-4);
}

.activity-item {
  display: flex;
  gap: var(--space-3);
}

.activity-avatar { flex-shrink: 0; }

.activity-avatar.field-change-icon,
.activity-avatar.commit-icon {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
  color: var(--text-muted);
}

.activity-avatar.field-change-icon { background: rgba(99,102,241,0.1); color: var(--color-primary); }
.activity-avatar.commit-icon { background: rgba(34,197,94,0.1); color: var(--status-completed); }

.comment-avatar {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-md);
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-size: 12px;
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-meta {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
  margin-bottom: var(--space-1);
}

.activity-user {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}

.activity-time {
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.activity-action {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin: 0 2px;
}

.activity-field {
  font-size: var(--text-xs);
  color: var(--color-primary);
  font-weight: var(--font-medium);
}

.activity-body {
  display: flex;
  align-items: flex-start;
  gap: var(--space-2);
}

.comment-bubble {
  flex: 1;
  font-size: var(--text-sm);
  color: var(--text-primary);
  line-height: var(--leading-normal);
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  padding: var(--space-2) var(--space-3);
}

.field-change-body {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  margin-top: var(--space-1);
}

.field-old {
  color: var(--text-muted);
  text-decoration: line-through;
  background: var(--bg-tertiary);
  padding: 1px 6px;
  border-radius: var(--radius-xs);
}

.field-arrow { color: var(--text-muted); flex-shrink: 0; }

.field-new {
  color: var(--text-primary);
  font-weight: var(--font-medium);
  background: var(--color-primary-light);
  color: var(--color-primary);
  padding: 1px 6px;
  border-radius: var(--radius-xs);
}

.commit-body {
  display: flex;
  align-items: baseline;
  gap: var(--space-2);
  font-size: var(--text-sm);
  margin-top: var(--space-1);
}

.commit-sha {
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--status-completed);
  background: rgba(34,197,94,0.1);
  padding: 1px 4px;
  border-radius: var(--radius-xs);
}

.commit-msg {
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-delete {
  border: none;
  background: transparent;
  color: var(--text-muted);
  font-size: var(--text-xs);
  cursor: pointer;
  opacity: 0;
  padding: var(--space-1);
  border-radius: var(--radius-sm);
  font-family: var(--font-primary);
  transition: opacity var(--transition-fast), color var(--transition-fast);

  .activity-item:hover & { opacity: 1; }
  &:hover { color: var(--color-danger); }
}

.prop-section { }

.prop-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-2) 0;
  min-height: 36px;
  gap: var(--space-2);

  &.prop-row-top { align-items: flex-start; }
}

.prop-key {
  font-size: var(--text-xs);
  color: var(--text-muted);
  font-weight: var(--font-medium);
  text-transform: uppercase;
  letter-spacing: 0.4px;
  flex-shrink: 0;
  min-width: 56px;
}

.prop-val {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--text-sm);
  color: var(--text-primary);
  flex: 1;
  flex-wrap: wrap;
}

.prop-muted { color: var(--text-muted); }

.prop-empty { color: var(--text-muted); font-size: var(--text-xs); }

.prop-divider {
  height: 1px;
  background: var(--border-color);
  margin: var(--space-2) 0;
}

.prop-assignee {
  gap: var(--space-2);
  .prop-avatar { background: var(--color-primary-light); color: var(--color-primary); }
}

.prop-avatar {
  width: 20px;
  height: 20px;
  border-radius: var(--radius-sm);
  font-size: 10px;
  font-weight: var(--font-bold);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

.prop-link {
  border: none;
  background: transparent;
  color: var(--text-link);
  font-size: var(--text-xs);
  cursor: pointer;
  font-family: var(--font-primary);
  padding: 2px 4px;
  border-radius: var(--radius-sm);
  transition: opacity var(--transition-fast);

  &:hover { opacity: 0.7; }
}

.prop-select {
  display: none;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: 2px var(--space-2);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  outline: none;
  background: var(--bg-primary);
  color: var(--text-primary);
  cursor: pointer;
  margin-left: auto;
}

/* Status chip */
.prop-status-row {
  .prop-val { flex-wrap: nowrap; }
  &:hover .prop-select { display: block; }
}

.status-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  padding: 3px 8px;
  border-radius: var(--radius-full);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.priority-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.iteration-badge {
  font-size: var(--text-xs);
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
}

.prop-labels { gap: var(--space-1); flex-wrap: wrap; }

.label-chip {
  font-size: 11px;
  padding: 2px 6px;
  background: var(--color-primary-light);
  color: var(--color-primary);
  border-radius: var(--radius-sm);
  font-weight: var(--font-medium);
}

.prop-deps { flex-direction: column; align-items: flex-start; gap: 4px; }

.dep-row {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  width: 100%;
}

.dep-status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex-shrink: 0;
}

.dep-title {
  flex: 1;
  font-size: var(--text-xs);
  color: var(--text-secondary);
  font-family: var(--font-mono);
}

.dep-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border: none;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  border-radius: var(--radius-sm);
  opacity: 0;
  transition: opacity var(--transition-fast), color var(--transition-fast);

  .dep-row:hover & { opacity: 1; }
  &:hover { color: var(--color-danger); }
}

.dep-form {
  display: flex;
  gap: var(--space-2);
  align-items: center;
  margin-top: var(--space-1);
}

.dep-form-input {
  flex: 1;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  outline: none;
  background: var(--bg-primary);
  color: var(--text-primary);
  &:focus { border-color: var(--color-primary); }
}

.dep-form-confirm {
  padding: var(--space-1) var(--space-3);
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.worklog-form { margin-top: var(--space-2); }

.worklog-form-row {
  display: flex;
  gap: var(--space-2);
  align-items: center;
}

.worklog-input {
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: var(--space-1) var(--space-2);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  outline: none;
  background: var(--bg-primary);
  color: var(--text-primary);
  width: 70px;
  &:focus { border-color: var(--color-primary); }
  &.worklog-date { width: 120px; }
}

.worklog-confirm {
  padding: var(--space-1) var(--space-3);
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-family: var(--font-primary);
  cursor: pointer;
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.prop-meta {
  .prop-row { padding: var(--space-1) 0; min-height: 28px; }
}

/* Plugin Extension Slots */
.plugin-tabs-section {
  padding: 0 var(--space-4);
  border-top: 1px solid var(--border-light);
  margin-top: var(--space-3);
}

.plugin-tabs-header {
  padding: var(--space-3) 0 var(--space-2);
  .plugin-tabs-label {
    font-size: var(--text-xs);
    font-weight: var(--font-semibold);
    color: var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
}

.plugin-tab-panel {
  padding: var(--space-2) 0;
  border-bottom: 1px solid var(--border-light);
  &:last-child { border-bottom: none; }
}

.plugin-tab-title {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
}
</style>
