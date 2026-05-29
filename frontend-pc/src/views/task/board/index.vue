<template>
  <ele-page>
    <!-- 头部过滤栏 / Header filter bar -->
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
          <el-option v-for="p in projectOptions" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
        <el-select
          v-model="iterationId"
          clearable
          placeholder="选择迭代（可选）"
          style="width: 160px"
          @change="loadBoard"
        >
          <el-option v-for="it in iterationOptions" :key="it.id" :label="it.name" :value="it.id" />
        </el-select>
        <el-button :icon="ReloadOutlined" @click="loadBoard">刷新</el-button>
        <el-button :icon="ListIcon" @click="$router.push('/task/index')">列表视图</el-button>
        <el-button
          v-if="projectId"
          type="primary"
          :icon="PlusOutlined"
          @click="openNewTaskDialog"
        >
          新建任务
        </el-button>
      </div>
    </ele-card>

    <!-- 看板列 / Kanban columns -->
    <ele-loading :loading="loading" style="min-height: 400px">
      <div v-if="!projectId" style="text-align: center; padding: 80px 0; color: #999">
        请先选择一个项目
      </div>
      <div v-else style="display: flex; gap: 16px; overflow-x: auto; padding-bottom: 8px; align-items: flex-start">
        <div
          v-for="col in columns"
          :key="col.status"
          style="flex: 0 0 300px; min-width: 300px"
        >
          <!-- 列头 / Column header -->
          <div
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding: 10px 12px;
              border-radius: 8px 8px 0 0;
              font-weight: 600;
              font-size: 14px;
            "
            :style="{ background: COL_BG[col.status] }"
          >
            <span>{{ col.title }}</span>
            <el-tag type="info" size="small" round>{{ col.tasks.length }}</el-tag>
          </div>

          <!-- 任务卡片列表 / Task card list -->
          <div
            style="
              min-height: 200px;
              padding: 8px;
              background: #f5f7fa;
              border-radius: 0 0 8px 8px;
            "
          >
            <div
              v-for="task in col.tasks"
              :key="task.id"
              style="
                background: #fff;
                border-radius: 6px;
                padding: 10px 12px;
                margin-bottom: 8px;
                box-shadow: 0 1px 3px rgba(0,0,0,.08);
                cursor: pointer;
              "
              @click="openTaskDetail(task)"
            >
              <!-- 任务类型 + 优先级 -->
              <div style="display: flex; gap: 4px; margin-bottom: 6px">
                <el-tag :type="TYPE_TAG[task.taskType]" size="small">{{ TYPE_LABEL[task.taskType] }}</el-tag>
                <el-tag :type="PRIORITY_TAG[task.priority]" size="small">{{ task.priority?.toUpperCase() }}</el-tag>
              </div>
              <!-- 任务标题 -->
              <div style="font-size: 13px; font-weight: 500; margin-bottom: 6px; line-height: 1.4">
                {{ task.title }}
              </div>
              <!-- 底部：负责人 + 截止日期 -->
              <div style="display: flex; justify-content: space-between; font-size: 12px; color: #999">
                <span>{{ task.assigneeName || '未指派' }}</span>
                <span v-if="task.dueDate">{{ task.dueDate }}</span>
              </div>
              <!-- 操作：更改状态 -->
              <div style="margin-top: 8px; display: flex; gap: 6px">
                <el-button
                  v-if="col.status !== 'in_progress'"
                  size="small"
                  type="primary"
                  plain
                  @click.stop="moveTask(task, 'in_progress')"
                >
                  开始
                </el-button>
                <el-button
                  v-if="col.status !== 'completed'"
                  size="small"
                  type="success"
                  plain
                  @click.stop="moveTask(task, 'completed')"
                >
                  完成
                </el-button>
                <el-button
                  v-if="col.status !== 'todo'"
                  size="small"
                  plain
                  @click.stop="moveTask(task, 'todo')"
                >
                  重开
                </el-button>
              </div>
            </div>

            <!-- 空状态 -->
            <div
              v-if="col.tasks.length === 0"
              style="text-align: center; color: #ccc; padding: 20px 0; font-size: 13px"
            >
              暂无任务
            </div>
          </div>
        </div>
      </div>
    </ele-loading>

    <!-- 新建任务弹窗 / New task dialog -->
    <el-dialog v-model="newTaskVisible" title="新建任务" width="500px" destroy-on-close>
      <el-form ref="newTaskFormRef" :model="newTaskForm" :rules="newTaskRules" label-width="90px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="newTaskForm.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="所属迭代">
          <el-select v-model="newTaskForm.iterationId" clearable placeholder="选择迭代（可选）" style="width: 100%">
            <el-option v-for="it in iterationOptions" :key="it.id" :label="it.name" :value="it.id" />
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
        <el-form-item label="截止日期">
          <el-date-picker v-model="newTaskForm.dueDate" type="date" value-format="YYYY-MM-DD" />
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

    <!-- 任务详情弹窗 / Task detail dialog -->
    <el-drawer
      v-model="drawerVisible"
      :title="currentTask.title"
      size="440px"
      destroy-on-close
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="类型">
          <el-tag :type="TYPE_TAG[currentTask.taskType]" size="small">{{ TYPE_LABEL[currentTask.taskType] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="PRIORITY_TAG[currentTask.priority]" size="small">{{ currentTask.priority?.toUpperCase() }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="STATUS_TAG[currentTask.status]" size="small">{{ STATUS_LABEL[currentTask.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="负责人">{{ currentTask.assigneeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="截止日期">{{ currentTask.dueDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预估(h)">{{ currentTask.estimatedHours ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际(h)">{{ currentTask.actualHours ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentTask.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { useRoute } from 'vue-router';
  import { EleMessage } from 'ele-admin-plus';
  import { ReloadOutlined, UnorderedListOutlined as ListIcon, PlusOutlined } from '@/components/icons';
  import { getBoard, updateTaskStatus, addTask } from '@/api/task';
  import { pageProjects, listIterations } from '@/api/project';

  const route = useRoute();

  const TYPE_LABEL = { story: '故事', task: '任务', bug: 'Bug', improvement: '改进' };
  const TYPE_TAG = { story: 'primary', task: '', bug: 'danger', improvement: 'warning' };
  const PRIORITY_TAG = { p0: 'danger', p1: 'warning', p2: 'primary', p3: 'info' };
  const STATUS_LABEL = { todo: '待办', in_progress: '进行中', completed: '已完成' };
  const STATUS_TAG = { todo: 'info', in_progress: 'warning', completed: 'success' };
  const COL_BG = { todo: '#e8f4fe', in_progress: '#fef6e7', completed: '#edfbee' };

  const loading = ref(false);
  const projectId = ref(undefined);
  const iterationId = ref(undefined);
  const projectOptions = ref([]);
  const iterationOptions = ref([]);
  const drawerVisible = ref(false);
  const currentTask = ref({});

  const newTaskVisible = ref(false);
  const newTaskLoading = ref(false);
  const newTaskFormRef = ref(null);
  const newTaskForm = reactive({
    projectId: undefined,
    iterationId: undefined,
    title: '',
    taskType: 'task',
    priority: 'p2',
    status: 'todo',
    dueDate: undefined,
    description: ''
  });
  const newTaskRules = {
    title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }]
  };

  const DEFAULT_COLUMNS = [
    { status: 'todo', title: '待办', tasks: [] },
    { status: 'in_progress', title: '进行中', tasks: [] },
    { status: 'completed', title: '已完成', tasks: [] }
  ];

  const columns = ref(DEFAULT_COLUMNS.map((c) => ({ ...c, tasks: [] })));

  function loadProjectOptions() {
    pageProjects({ pageSize: 200 }).then((res) => {
      projectOptions.value = res.rows ?? [];
    }).catch(() => {});
  }

  function handleProjectChange(val) {
    iterationId.value = undefined;
    iterationOptions.value = [];
    if (val) {
      listIterations(val, { pageSize: 100 }).then((res) => {
        iterationOptions.value = res.rows ?? [];
      }).catch(() => {});
    }
    loadBoard();
  }

  function loadBoard() {
    if (!projectId.value) {
      columns.value = DEFAULT_COLUMNS.map((c) => ({ ...c, tasks: [] }));
      return;
    }
    loading.value = true;
    getBoard(projectId.value, iterationId.value || undefined)
      .then((data) => {
        if (Array.isArray(data) && data.length > 0) {
          columns.value = data;
        } else {
          columns.value = DEFAULT_COLUMNS.map((c) => ({ ...c, tasks: [] }));
        }
      })
      .catch(() => {
        columns.value = DEFAULT_COLUMNS.map((c) => ({ ...c, tasks: [] }));
      })
      .finally(() => {
        loading.value = false;
      });
  }

  function moveTask(task, targetStatus) {
    updateTaskStatus(task.id, targetStatus)
      .then(() => {
        EleMessage.success({ message: '状态已更新', plain: true });
        loadBoard();
      })
      .catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      });
  }

  function openTaskDetail(task) {
    currentTask.value = task;
    drawerVisible.value = true;
  }

  function openNewTaskDialog() {
    Object.assign(newTaskForm, {
      projectId: projectId.value,
      iterationId: iterationId.value || undefined,
      title: '',
      taskType: 'task',
      priority: 'p2',
      status: 'todo',
      dueDate: undefined,
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
          loadBoard();
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
