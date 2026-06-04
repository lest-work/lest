import { defineStore } from 'pinia';
import { ref } from 'vue';
import { taskApi, type Task, type TaskFilter, type TaskStatus } from '@/api/task';

function normalizeTask(raw: any): Task {
  return {
    id: raw.taskId ?? raw.id,
    taskId: raw.taskId,
    taskNo: raw.taskNo,
    projectId: raw.projectId,
    title: raw.title,
    description: raw.description,
    type: raw.taskType || raw.type || 'task',
    taskType: raw.taskType || raw.type,
    status: raw.status,
    priority: raw.priority,
    assigneeId: raw.assigneeId,
    assigneeName: raw.assigneeName,
    reporterId: raw.reporterId,
    reporterName: raw.reporterName,
    iterationId: raw.iterationId,
    iterationName: raw.iterationName,
    parentId: raw.parentId,
    storyPoints: raw.storyPoints,
    estimateHours: raw.estimatedHours ?? raw.estimateHours,
    actualHours: raw.actualHours,
    remainingHours: raw.remainingHours,
    startDate: raw.startDate || raw.startTime,
    dueDate: raw.dueDate,
    completedAt: raw.completedAt,
    labels: raw.labelNames || raw.labels,
    labelNames: raw.labelNames,
    labelIds: raw.labelIds,
    watchers: raw.watchers,
    childCount: raw.childCount,
    hasBlockers: raw.hasBlockers,
    depth: raw.depth,
    sort: raw.sort,
    createdAt: raw.createTime || raw.createdAt,
    createTime: raw.createTime,
    updatedAt: raw.updateTime || raw.updatedAt,
    updateTime: raw.updateTime,
  };
}

export const useTaskStore = defineStore('task', () => {
  const tasks = ref<Task[]>([]);
  const loading = ref(false);
  const pagination = ref({ page: 1, size: 20, total: 0 });

  async function fetchTasks(filter: Partial<TaskFilter> = {}) {
    loading.value = true;
    try {
      const res = await taskApi.list({
        page: pagination.value.page,
        size: pagination.value.size,
        ...filter,
      });
      tasks.value = (res.data.records || []).map(normalizeTask);
      pagination.value.total = res.data.total || 0;
    } finally {
      loading.value = false;
    }
  }

  async function createTask(data: Partial<Task>) {
    const res = await taskApi.create(data);
    tasks.value.unshift(normalizeTask(res.data));
    return res.data;
  }

  async function updateTask(id: number, data: Partial<Task>) {
    const res = await taskApi.update(id, data);
    const idx = tasks.value.findIndex((t) => t.id === id);
    if (idx !== -1) tasks.value[idx] = normalizeTask(res.data);
    return res.data;
  }

  async function deleteTask(id: number) {
    await taskApi.delete(id);
    tasks.value = tasks.value.filter((t) => t.id !== id);
  }

  async function updateStatus(id: number, status: TaskStatus) {
    await taskApi.updateStatus(id, status);
    const task = tasks.value.find((t) => t.id === id);
    if (task) task.status = status;
  }

  async function assignTask(id: number, assigneeId: number) {
    await taskApi.assign(id, assigneeId);
    const task = tasks.value.find((t) => t.id === id);
    if (task) task.assigneeId = assigneeId;
  }

  return { tasks, loading, pagination, fetchTasks, createTask, updateTask, deleteTask, updateStatus, assignTask };
});
