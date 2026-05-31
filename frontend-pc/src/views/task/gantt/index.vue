<template>
  <ele-page>
    <!-- 头部过滤栏 -->
    <ele-card style="margin-bottom: 16px">
      <div style="display: flex; align-items: center; gap: 16px; flex-wrap: wrap">
        <el-select
          v-model="projectId"
          filterable
          clearable
          placeholder="选择项目"
          style="width: 200px"
          @change="handleProjectChange"
        >
          <el-option v-for="p in projectOptions" :key="p.projectId" :label="p.name" :value="p.projectId" />
        </el-select>
        <el-select
          v-model="iterationId"
          clearable
          placeholder="选择迭代（可选）"
          style="width: 160px"
          @change="loadGantt"
        >
          <el-option v-for="it in iterationOptions" :key="it.iterationId" :label="it.name" :value="it.iterationId" />
        </el-select>
        <el-button @click="loadGantt">刷新</el-button>
        <el-button @click="$router.push('/task/index')">列表视图</el-button>
        <el-button @click="$router.push('/task/board')">看板视图</el-button>
        <el-button type="primary" :icon="PlusOutlined" @click="openAddDialog">新建任务</el-button>
      </div>
    </ele-card>

    <!-- 甘特图 -->
    <ele-card v-loading="loading">
      <template #header>
        <span>任务甘特图</span>
        <span v-if="projectName" style="margin-left: 8px; font-size: 13px; color: #999; font-weight: normal">
          — {{ projectName }}
        </span>
      </template>
      <div v-if="!projectId" style="text-align: center; padding: 80px 0; color: #999">
        请先选择一个项目
      </div>
      <v-chart
        v-else
        ref="ganttRef"
        :option="ganttOption"
        :autoresize="true"
        style="height: 600px"
      />
    </ele-card>

    <!-- 新建任务弹窗 -->
    <el-dialog v-model="newTaskVisible" title="新建任务" width="500px" destroy-on-close>
      <el-form ref="newTaskFormRef" :model="newTaskForm" :rules="newTaskRules" label-width="90px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="newTaskForm.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="所属迭代">
          <el-select v-model="newTaskForm.iterationId" clearable placeholder="选择迭代（可选）" style="width: 100%">
            <el-option v-for="it in iterationOptions" :key="it.iterationId" :label="it.name" :value="it.iterationId" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="newTaskForm.taskType" style="width: 100%">
            <el-option label="用户故事" value="story" />
            <el-option label="开发任务" value="task" />
            <el-option label="Bug" value="bug" />
            <el-option label="改进" value="improvement" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="newTaskForm.priority" style="width: 100%">
            <el-option label="P0 - 紧急" value="p0" />
            <el-option label="P1 - 高" value="p1" />
            <el-option label="P2 - 中" value="p2" />
            <el-option label="P3 - 低" value="p3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="newTaskForm.startTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="newTaskForm.dueDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预估工时">
          <el-input-number v-model="newTaskForm.estimatedHours" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="newTaskForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="newTaskVisible = false">取消</el-button>
        <el-button type="primary" :loading="newTaskLoading" @click="handleCreateTask">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import { EleMessage } from 'ele-admin-plus';
  import { use } from 'echarts/core';
  import { CanvasRenderer } from 'echarts/renderers';
  import { BarChart } from 'echarts/charts';
  import {
    GridComponent,
    TooltipComponent,
    TitleComponent,
    LegendComponent
  } from 'echarts/components';
  import VChart from 'vue-echarts';
  import { PlusOutlined } from '@/components/icons';
  import { getBoard, addTask } from '@/api/task';
  import { pageProjects, listIterations } from '@/api/project';
  import { useEcharts } from '@/utils/use-echarts';

  use([CanvasRenderer, BarChart, GridComponent, TooltipComponent, TitleComponent, LegendComponent]);

  const route = useRoute();
  const ganttRef = ref(null);
  useEcharts([ganttRef]);

  const TYPE_COLOR = {
    story: '#409eff',
    task: '#67c23a',
    bug: '#f56c6c',
    improvement: '#e6a23c'
  };

  const STATUS_COLOR = {
    todo: '#909399',
    in_progress: '#409eff',
    completed: '#67c23a'
  };

  const TYPE_LABEL = { story: '故事', task: '任务', bug: 'Bug', improvement: '改进' };

  const loading = ref(false);
  const projectId = ref(undefined);
  const iterationId = ref(undefined);
  const projectOptions = ref([]);
  const iterationOptions = ref([]);
  const projectName = ref('');
  const ganttOption = reactive({});

  const newTaskVisible = ref(false);
  const newTaskLoading = ref(false);
  const newTaskFormRef = ref(null);
  const newTaskForm = reactive({
    projectId: undefined,
    iterationId: undefined,
    title: '',
    taskType: 'task',
    priority: 'p2',
    startTime: undefined,
    dueDate: undefined,
    estimatedHours: undefined,
    description: ''
  });
  const newTaskRules = {
    title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }]
  };

  function loadProjectOptions() {
    pageProjects({ pageSize: 200 }).then((res) => {
      projectOptions.value = res.rows ?? [];
      if (projectId.value) {
        const p = projectOptions.value.find((x) => x.projectId === projectId.value);
        if (p) projectName.value = p.name;
      }
    }).catch(() => {});
  }

  function handleProjectChange(val) {
    iterationId.value = undefined;
    iterationOptions.value = [];
    projectName.value = '';
    if (val) {
      const p = projectOptions.value.find((x) => x.projectId === val);
      if (p) projectName.value = p.name;
      listIterations(val, { pageSize: 100 }).then((res) => {
        iterationOptions.value = res.rows ?? [];
      }).catch(() => {});
    }
    loadGantt();
  }

  function buildGanttOption(columns) {
    const today = new Date();
    const todayStr = today.toISOString().slice(0, 10);
    const allTasks = [];
    columns.forEach((col) => {
      col.tasks.forEach((t) => {
        if (t.startTime || t.dueDate) {
          allTasks.push(t);
        }
      });
    });

    if (allTasks.length === 0) {
      Object.assign(ganttOption, {
        title: { text: '暂无任务甘特数据', left: 'center', top: 'center', textStyle: { color: '#ccc', fontSize: 14 } },
        xAxis: { show: false },
        yAxis: { show: false }
      });
      return;
    }

    const sortedTasks = [...allTasks].sort((a, b) => {
      if (a.startTime !== b.startTime) {
        return (a.startTime || todayStr) > (b.startTime || todayStr) ? 1 : -1;
      }
      return (a.dueDate || todayStr) > (b.dueDate || todayStr) ? 1 : -1;
    });

    const minDate = sortedTasks.reduce((acc, t) => {
      const d = t.startTime || todayStr;
      return d < acc ? d : acc;
    }, todayStr);
    const maxDate = sortedTasks.reduce((acc, t) => {
      const d = t.dueDate || todayStr;
      return d > acc ? d : acc;
    }, todayStr);

    const minD = new Date(minDate);
    const maxD = new Date(maxDate);
    const diffDays = Math.max(7, Math.ceil((maxD - minD) / 86400000) + 2);
    const viewStart = new Date(minD);
    viewStart.setDate(viewStart.getDate() - 1);
    const viewEnd = new Date(viewStart);
    viewEnd.setDate(viewEnd.getDate() + diffDays + 1);

    const dateFormat = (d) => d.toISOString().slice(0, 10);

    const categories = sortedTasks.map((t) => {
      const type = TYPE_LABEL[t.taskType] || t.taskType || '';
      return `${type} ${t.title}`.slice(0, 30);
    });

    const barData = sortedTasks.map((t) => {
      const start = t.startTime ? new Date(t.startTime) : new Date(t.dueDate || todayStr);
      const end = t.dueDate ? new Date(t.dueDate) : new Date(t.startTime || todayStr);
      let duration = Math.max(1, Math.ceil((end - start) / 86400000));
      if (!t.startTime && t.dueDate) {
        duration = Math.max(1, Math.ceil((new Date(t.dueDate) - today) / 86400000));
      }
      return {
        value: [categories.indexOf(`${TYPE_LABEL[t.taskType] || t.taskType || ''} ${t.title}`.slice(0, 30)), duration],
        itemStyle: {
          color: STATUS_COLOR[t.status] || '#409eff',
          borderRadius: [4, 4, 4, 4]
        }
      };
    });

    const startDates = sortedTasks.map((t) => {
      if (t.startTime) return t.startTime;
      return dateFormat(today);
    });

    const taskColors = sortedTasks.map((t) => STATUS_COLOR[t.status] || '#409eff');

    Object.assign(ganttOption, {
      tooltip: {
        formatter: (params) => {
          const idx = params.value[0];
          const t = sortedTasks[idx];
          if (!t) return '';
          const start = t.startTime || '未设置';
          const end = t.dueDate || '未设置';
          return `<b>${t.title}</b><br/>` +
            `类型：${TYPE_LABEL[t.taskType] || t.taskType || '-'} &nbsp; ` +
            `状态：${t.status || '-'}<br/>` +
            `开始：${start} &nbsp; 截止：${end}`;
        }
      },
      title: {
        text: `${projectName.value || ''} 任务甘特图`,
        left: 16,
        textStyle: { fontSize: 14, fontWeight: '600' }
      },
      grid: {
        left: 16,
        right: 24,
        top: 60,
        bottom: 48
      },
      xAxis: {
        type: 'time',
        min: viewStart.getTime(),
        max: viewEnd.getTime(),
        axisLabel: {
          formatter: (val) => {
            const d = new Date(val);
            return `${d.getMonth() + 1}/${d.getDate()}`;
          }
        },
        splitLine: { show: false }
      },
      yAxis: {
        type: 'category',
        data: categories,
        inverse: true,
        axisLabel: { fontSize: 12 },
        splitLine: { show: false }
      },
      series: [
        {
          type: 'custom',
          renderItem: (params, api) => {
            const categoryIndex = api.value(0);
            const start = api.coord([new Date(startDates[categoryIndex]).getTime(), 0]);
            const barWidth = api.size([api.value(1) * 86400000, 0])[0];
            const y = start[1] - 10;
            const barHeight = 20;
            const color = taskColors[categoryIndex];
            const task = sortedTasks[categoryIndex];
            return {
              type: 'group',
              children: [
                {
                  type: 'rect',
                  shape: { x: start[0], y, width: barWidth, height: barHeight, r: [4, 4, 4, 4] },
                  style: { fill: color, opacity: 0.85 }
                }
              ]
            };
          },
          encode: { x: 0, y: 0 },
          data: sortedTasks.map((t, i) => ({ value: [i, 1], task: t }))
        }
      ]
    });
  }

  function loadGantt() {
    if (!projectId.value) return;
    loading.value = true;
    getBoard(projectId.value, iterationId.value || undefined)
      .then((columns) => {
        buildGanttOption(Array.isArray(columns) ? columns : []);
      })
      .catch(() => {
        buildGanttOption([]);
      })
      .finally(() => {
        loading.value = false;
      });
  }

  function openAddDialog() {
    Object.assign(newTaskForm, {
      projectId: projectId.value,
      iterationId: iterationId.value || undefined,
      title: '',
      taskType: 'task',
      priority: 'p2',
      startTime: undefined,
      dueDate: undefined,
      estimatedHours: undefined,
      description: ''
    });
    newTaskVisible.value = true;
  }

  function handleCreateTask() {
    newTaskFormRef.value?.validate().then(() => {
      newTaskLoading.value = true;
      addTask(newTaskForm)
        .then(() => {
          EleMessage.success({ message: '新建成功', plain: true });
          newTaskVisible.value = false;
          loadGantt();
        })
        .catch((e) => {
          EleMessage.error({ message: e.message, plain: true });
        })
        .finally(() => {
          newTaskLoading.value = false;
        });
    });
  }

  onMounted(() => {
    loadProjectOptions();
    const qid = route.query.projectId;
    if (qid) {
      projectId.value = Number(qid);
      handleProjectChange(projectId.value);
    }
  });
</script>
