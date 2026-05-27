<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">定时任务</span>
          <div class="toolbar-right">
            <el-button size="small" @click="fetchTasks">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col
          v-for="task in tasks"
          :key="task.name"
          :xs="24"
          :sm="12"
          :md="8"
        >
          <div class="task-card" :class="statusClass(task.status)">
            <div class="task-header">
              <span class="task-name">{{ task.displayName }}</span>
              <el-tag :type="statusTagType(task.status)" size="small">
                {{ statusLabel(task.status) }}
              </el-tag>
            </div>
            <div class="task-info">
              <div class="task-info-item">
                <el-icon><Clock /></el-icon>
                <span>Cron: {{ task.cron }}</span>
              </div>
              <div v-if="task.lastRun" class="task-info-item">
                <el-icon><Timer /></el-icon>
                <span>上次: {{ formatTime(task.lastRun) }}</span>
              </div>
              <div v-if="task.nextRun" class="task-info-item">
                <el-icon><Calendar /></el-icon>
                <span>下次: {{ formatTime(task.nextRun) }}</span>
              </div>
            </div>
            <div class="task-actions">
              <el-button
                v-if="task.status === 'idle'"
                type="primary"
                size="small"
                :loading="triggering === task.name"
                @click="triggerTask(task.name)"
              >
                立即执行
              </el-button>
              <el-tag v-else-if="task.status === 'running'" type="warning" size="small">
                执行中...
              </el-tag>
              <el-tag v-else type="info" size="small">已禁用</el-tag>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 执行日志 -->
      <el-divider content-position="left">
        <span class="divider-title">执行日志</span>
      </el-divider>

      <ele-table
        :loading="logLoading"
        :data="logs"
        :columns="logColumns"
        :pagination="{ show: true, total: logTotal, modelValue: logPager, onUpdate: fetchLogs }"
        row-key="id"
        size="small"
      />
    </ele-card>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted } from 'vue';
  import { Refresh, Clock, Timer, Calendar } from '@element-plus/icons-vue';
  import { EleMessage } from 'ele-admin-plus';
  import {
    getScheduledTasks,
    triggerScheduledTask,
    getScheduledTaskLogs,
    type ScheduledTask,
    type ScheduledTaskLog
  } from '@/api/wakapi';

  defineOptions({ name: 'WakapiScheduled' });

  const loading = ref(false);
  const logLoading = ref(false);
  const tasks = ref<ScheduledTask[]>([]);
  const logs = ref<ScheduledTaskLog[]>([]);
  const logTotal = ref(0);
  const logPager = reactive({ page: 1, limit: 10 });
  const triggering = ref('');

  const logColumns = [
    { label: '任务', prop: 'taskName', width: 160 },
    {
      label: '状态',
      prop: 'status',
      width: 90,
      slot: true
    },
    { label: '开始时间', prop: 'startTime', width: 170 },
    { label: '结束时间', prop: 'endTime', width: 170 },
    { label: '耗时', prop: 'duration', width: 90, formatter: (row: ScheduledTaskLog) => formatDuration(row.duration) },
    { label: '消息', prop: 'message', minWidth: 120, showOverflowTooltip: true }
  ];

  const statusClass = (status: string): string => {
    return {
      idle: 'task-idle',
      running: 'task-running',
      disabled: 'task-disabled'
    }[status] ?? '';
  };

  const statusLabel = (status: string): string => {
    return {
      idle: '空闲',
      running: '执行中',
      disabled: '已禁用'
    }[status] ?? status;
  };

  const statusTagType = (status: string): string => {
    return {
      idle: 'success',
      running: 'warning',
      disabled: 'info'
    }[status] ?? 'info';
  };

  const formatTime = (iso: string): string => {
    if (!iso) return '-';
    const d = new Date(iso);
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  };

  const formatDuration = (ms?: number): string => {
    if (!ms) return '-';
    if (ms < 1000) return ms + 'ms';
    return (ms / 1000).toFixed(1) + 's';
  };

  const pad = (n: number): string => String(n).padStart(2, '0');

  const fetchTasks = async () => {
    loading.value = true;
    try {
      tasks.value = await getScheduledTasks();
    } catch {
      tasks.value = [];
    } finally {
      loading.value = false;
    }
  };

  const fetchLogs = async () => {
    logLoading.value = true;
    try {
      const res = await getScheduledTaskLogs({ page: logPager.page, limit: logPager.limit });
      logs.value = res?.list ?? [];
      logTotal.value = res?.count ?? 0;
    } catch {
      logs.value = [];
      logTotal.value = 0;
    } finally {
      logLoading.value = false;
    }
  };

  const triggerTask = async (name: string) => {
    triggering.value = name;
    try {
      await triggerScheduledTask(name);
      EleMessage.success('任务已触发');
      fetchTasks();
      fetchLogs();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '触发失败');
    } finally {
      triggering.value = '';
    }
  };

  onMounted(() => {
    fetchTasks();
    fetchLogs();
  });
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      font-size: 15px;
      font-weight: 500;
    }
  }

  .task-card {
    padding: 16px;
    border-radius: 8px;
    border: 1px solid var(--el-border-color-lighter);
    background: var(--el-fill-color-blank);
    margin-bottom: 16px;
    transition: border-color 0.3s, box-shadow 0.3s;

    &:hover {
      border-color: var(--el-border-color);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    }

    &.task-running {
      border-color: var(--el-color-warning);
      background: #fdf6ec;
    }

    &.task-disabled {
      opacity: 0.6;
    }

    .task-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;

      .task-name {
        font-size: 14px;
        font-weight: 500;
        color: var(--el-text-color-primary);
      }
    }

    .task-info {
      display: flex;
      flex-direction: column;
      gap: 6px;
      margin-bottom: 12px;

      .task-info-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: var(--el-text-color-secondary);

        .el-icon {
          font-size: 14px;
          color: var(--el-text-color-placeholder);
        }
      }
    }

    .task-actions {
      display: flex;
      justify-content: flex-end;
    }
  }

  .divider-title {
    font-size: 13px;
    font-weight: 500;
    color: var(--el-text-color-secondary);
  }
</style>
