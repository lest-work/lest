import { defineStore } from 'pinia';
import { ref } from 'vue';
import { boardApi, type BoardColumn, type BoardTask, type MoveCardPayload } from '@/api/board';

export const useBoardStore = defineStore('board', () => {
  const columns = ref<BoardColumn[]>([]);
  const projectId = ref<number | null>(null);
  const loading = ref(false);

  async function fetchBoard(pid: number, iterationId?: number) {
    loading.value = true;
    projectId.value = pid;
    try {
      const res = await boardApi.getBoard(pid, iterationId);
      columns.value = res.data;
    } finally {
      loading.value = false;
    }
  }

  function getTasksByColumn(columnKey: string): BoardTask[] {
    const col = columns.value.find((c) => c.status === columnKey);
    if (!col) return [];
    return [...col.tasks].sort((a, b) => (a.position || 0) - (b.position || 0));
  }

  async function moveCard(taskId: number, targetColumn: string, targetPosition: number) {
    const oldColumn = columns.value.find((c) =>
      c.tasks.some((t) => t.id === taskId)
    );
    const oldTask = oldColumn?.tasks.find((t) => t.id === taskId);
    if (!oldTask) return;

    const payload: MoveCardPayload = {
      targetColumn,
      targetPosition,
    };
    try {
      await boardApi.moveCard(taskId, payload);
      // Update local state: remove from old column, add to new column
      if (oldColumn) {
        oldColumn.tasks = oldColumn.tasks.filter((t) => t.id !== taskId);
        oldColumn.taskCount = oldColumn.tasks.length;
      }
      const newCol = columns.value.find((c) => c.status === targetColumn);
      if (newCol) {
        oldTask.status = targetColumn;
        oldTask.position = targetPosition;
        newCol.tasks.push(oldTask);
        newCol.taskCount = newCol.tasks.length;
      }
    } catch {
      // Rollback is implicit — we didn't modify columns.value on failure
    }
  }

  return { columns, projectId, loading, fetchBoard, getTasksByColumn, moveCard };
});
