<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, watch, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import * as echarts from 'echarts/core';
import { GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, TitleComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import type { EChartsType } from 'echarts/core';
import { taskApi, type Task } from '@/api/task';

echarts.use([GridComponent, TooltipComponent, LegendComponent, DataZoomComponent, TitleComponent, CanvasRenderer]);

const route = useRoute();
const router = useRouter();
const projectId = computed(() => Number(route.params.id));

const loading = ref(false);
const tasks = ref<Task[]>([]);
const chartRef = ref<HTMLDivElement | null>(null);
let chartInstance: EChartsType | null = null;

const viewMode = computed(() => {
  const path = route.path;
  if (path.endsWith('/board')) return 'board';
  if (path.endsWith('/gantt')) return 'gantt';
  return 'tasks';
});

onMounted(async () => {
  loading.value = true;
  try {
    const res = await taskApi.gantt({ projectId: projectId.value });
    tasks.value = res.data || [];
  } finally {
    loading.value = false;
    await nextTick();
    initChart();
  }
});

onBeforeUnmount(() => {
  chartInstance?.dispose();
  chartInstance = null;
});

watch(tasks, async () => {
  await nextTick();
  renderChart();
});

function switchView(view: string) { router.push(`/project/${projectId.value}/${view}`); }

function initChart() {
  if (!chartRef.value) return;
  chartInstance = echarts.init(chartRef.value, undefined, { renderer: 'canvas' });
  renderChart();
  window.addEventListener('resize', handleResize);
}

function handleResize() {
  chartInstance?.resize();
}

function renderChart() {
  if (!chartInstance || !tasks.value.length) return;

  const ganttData = buildGanttData();

  const option: echarts.EChartsCoreOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter(params: any) {
        const item = params.find((p: any) => p.seriesName !== '');
        if (!item) return '';
        const task = tasks.value[item.dataIndex];
        return `
          <div style="font-family: var(--font-primary); min-width: 180px;">
            <div style="font-weight: 600; margin-bottom: 4px; color: var(--text-primary);">
              ${task?.taskNo || 'TASK'} · ${task?.title || ''}
            </div>
            <div style="font-size: 12px; color: var(--text-secondary); line-height: 1.6;">
              <div><b>状态</b>: ${formatStatus(task?.status || '')}</div>
              <div><b>类型</b>: ${formatType(task?.taskType || task?.type || 'task')}</div>
              ${task?.assigneeName ? `<div><b>负责人</b>: ${task.assigneeName}</div>` : ''}
              ${task?.iterationName ? `<div><b>迭代</b>: ${task.iterationName}</div>` : ''}
            </div>
          </div>
        `;
      }
    },
    legend: {
      bottom: 0,
      data: ['规划时间', '实际时间'],
      textStyle: { fontSize: 12, fontFamily: 'var(--font-primary)' },
    },
    grid: { left: '16', right: '16', top: '8', bottom: '48', containLabel: true },
    xAxis: {
      type: 'time',
      axisLabel: {
        fontSize: 11,
        fontFamily: 'var(--font-primary)',
        color: 'var(--text-muted)',
        formatter(value: number) {
          const d = new Date(value);
          return `${d.getMonth() + 1}/${d.getDate()}`;
        }
      },
      axisLine: { lineStyle: { color: 'var(--border-color)' } },
      splitLine: { lineStyle: { color: 'var(--border-light)' } },
    },
    yAxis: {
      type: 'category',
      data: tasks.value.map(t => t.title || '未知任务'),
      axisLabel: {
        fontSize: 12,
        fontFamily: 'var(--font-primary)',
        color: 'var(--text-primary)',
        overflow: 'truncate',
        width: 180,
      },
      axisLine: { lineStyle: { color: 'var(--border-color)' } },
      axisTick: { show: false },
    },
    series: [
      {
        name: '规划时间',
        type: 'custom',
        renderItem(params, api) {
          const task = tasks.value[params.dataIndex];
          if (!task) return { type: 'group' };
          const start = task.startDate || task.startTime;
          const end = task.endDate || task.dueDate;
          if (!start || !end) return { type: 'group' };

          const startCoord = api.coord([new Date(start).getTime(), params.dataIndex]);
          const endCoord = api.coord([new Date(end).getTime(), params.dataIndex]);

          const barHeight = 20;
          const y = startCoord[1] - barHeight / 2;

          return {
            type: 'group',
            children: [
              {
                type: 'rect',
                shape: {
                  x: startCoord[0],
                  y,
                  width: endCoord[0] - startCoord[0],
                  height: barHeight,
                  r: [4, 4, 4, 4],
                },
                style: {
                  fill: getBarColor(task.taskType || task.type || 'task', 'plan'),
                  opacity: 0.6,
                },
              },
              {
                type: 'rect',
                shape: {
                  x: startCoord[0],
                  y: y + barHeight - 4,
                  width: endCoord[0] - startCoord[0],
                  height: 4,
                  r: [0, 0, 2, 2],
                },
                style: { fill: getBarColor(task.taskType || task.type || 'task', 'fill') },
              },
            ],
          };
        },
        encode: { x: 0, y: 1 },
        data: tasks.value.map((t, i) => ({
          value: [i, t.startDate || t.startTime || new Date().toISOString(), t.endDate || t.dueDate || new Date().toISOString()],
          taskIndex: i,
        })),
        z: 10,
      },
    ],
  };

  chartInstance.setOption(option, true);
}

function buildGanttData() {
  return tasks.value.map(t => ({
    task: t,
    start: t.startDate || t.startTime || null,
    end: t.endDate || t.dueDate || null,
  }));
}

function getBarColor(type: string, mode: 'plan' | 'fill') {
  const colors: Record<string, { plan: string; fill: string }> = {
    epic:      { plan: '#8b5cf6', fill: '#8b5cf6' },
    story:     { plan: '#3b82f6', fill: '#3b82f6' },
    task:      { plan: '#22c55e', fill: '#22c55e' },
    bug:       { plan: '#ef4444', fill: '#ef4444' },
  };
  const c = colors[type] || colors.task;
  return mode === 'plan' ? c.plan + '30' : c.fill;
}

function formatStatus(s: string) {
  const m: Record<string, string> = { todo: '待办', in_progress: '进行中', completed: '已完成' };
  return m[s] || s;
}

function formatType(t: string) {
  const m: Record<string, string> = { epic: '史诗', story: '用户故事', task: '任务', bug: '缺陷' };
  return m[t] || t;
}
</script>

<template>
  <div class="gantt-view">
    <div class="gantt-toolbar">
      <div class="view-switcher">
        <button class="view-btn" :class="{ active: viewMode === 'tasks' }" @click="switchView('tasks')">列表</button>
        <button class="view-btn" :class="{ active: viewMode === 'board' }" @click="switchView('board')">看板</button>
        <button class="view-btn" :class="{ active: viewMode === 'gantt' }" @click="switchView('gantt')">甘特图</button>
      </div>
      <div class="gantt-legend">
        <span class="legend-item"><span class="legend-dot" style="background:#8b5cf6"></span>史诗</span>
        <span class="legend-item"><span class="legend-dot" style="background:#3b82f6"></span>故事</span>
        <span class="legend-item"><span class="legend-dot" style="background:#22c55e"></span>任务</span>
        <span class="legend-item"><span class="legend-dot" style="background:#ef4444"></span>缺陷</span>
      </div>
    </div>

    <LoadingSpinner v-if="loading" />

    <div v-else-if="tasks.length === 0" class="empty-state">
      <div class="empty-illustration">
        <svg width="56" height="56" viewBox="0 0 56 56" fill="none">
          <rect x="8" y="16" width="40" height="28" rx="6" stroke="var(--border-color)" stroke-width="2" stroke-dasharray="4 4"/>
          <path d="M16 30h24M16 38h16" stroke="var(--border-color)" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </div>
      <p class="empty-title">暂无任务数据</p>
      <p class="empty-desc">创建任务后即可在甘特图中查看</p>
      <button class="empty-cta" @click="switchView('tasks')">去创建任务</button>
    </div>

    <div v-else class="gantt-chart-wrapper">
      <div ref="chartRef" class="gantt-chart" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.gantt-view { display: flex; flex-direction: column; height: 100%; font-family: var(--font-primary); }

.gantt-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-4);
  flex-wrap: wrap;
  gap: var(--space-3);
}

.view-switcher {
  display: flex;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-1);
  gap: var(--space-1);
}

.view-btn {
  padding: var(--space-2) var(--space-4);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-family: var(--font-primary);
  transition: background var(--transition-fast), color var(--transition-fast);

  &:hover { color: var(--text-primary); }
  &.active {
    background: var(--bg-primary);
    color: var(--text-primary);
    font-weight: var(--font-semibold);
    box-shadow: var(--shadow-card);
  }
}

.gantt-legend {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  font-size: var(--text-xs);
  color: var(--text-muted);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  flex-shrink: 0;
}

.gantt-chart-wrapper {
  flex: 1;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.gantt-chart { width: 100%; height: 100%; min-height: 400px; }

.empty-state {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-12) var(--space-6);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-3);

  .empty-title { font-size: var(--text-md); font-weight: var(--font-semibold); color: var(--text-primary); margin: 0; }
  .empty-desc { font-size: var(--text-sm); color: var(--text-muted); margin: 0; }
  .empty-cta {
    margin-top: var(--space-2);
    padding: var(--space-2) var(--space-5);
    border: none;
    border-radius: var(--radius-md);
    background: var(--color-primary);
    color: #fff;
    font-size: var(--text-sm);
    font-family: var(--font-primary);
    cursor: pointer;
    &:hover { background: var(--color-primary-hover); }
  }
}
.empty-illustration { opacity: 0.35; }
</style>
