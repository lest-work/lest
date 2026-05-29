<template>
  <ele-page>
    <!-- 搜索栏 / Search bar -->
    <ele-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="任务标题">
          <el-input
            v-model="query.title"
            clearable
            placeholder="搜索任务标题"
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="所属项目">
          <el-select
            v-model="query.projectId"
            clearable
            filterable
            placeholder="全部项目"
            style="width: 160px"
          >
            <el-option
              v-for="p in projectOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="待办" value="todo" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="query.priority" clearable placeholder="全部" style="width: 100px">
            <el-option label="P0" value="p0" />
            <el-option label="P1" value="p1" />
            <el-option label="P2" value="p2" />
            <el-option label="P3" value="p3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="SearchOutlined" @click="handleSearch">查询</el-button>
          <el-button :icon="ReloadOutlined" @click="handleReset">重置</el-button>
          <el-button
            v-permission="['task:task:add']"
            type="primary"
            :icon="PlusOutlined"
            @click="openAddDialog"
          >
            新建任务
          </el-button>
          <el-button :icon="KanbanIcon" @click="goBoard">看板视图</el-button>
        </el-form-item>
      </el-form>
    </ele-card>

    <!-- 任务表格 / Task table -->
    <ele-card>
      <el-table
        v-loading="loading"
        :data="list"
        stripe
        row-key="id"
        style="width: 100%"
      >
        <el-table-column prop="title" label="任务标题" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" underline="never" @click="openDetail(row)">{{ row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="taskType" label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="TYPE_TAG[row.taskType]" size="small">{{ TYPE_LABEL[row.taskType] || row.taskType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="PRIORITY_TAG[row.priority]" size="small">{{ row.priority?.toUpperCase() }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="STATUS_TAG[row.status]" size="small">{{ STATUS_LABEL[row.status] || row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeName" label="负责人" width="100" />
        <el-table-column prop="dueDate" label="截止日期" width="110" />
        <el-table-column prop="estimatedHours" label="预估(h)" width="90" align="center" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-link type="primary" underline="never" @click="openEditDialog(row)">编辑</el-link>
            <el-divider direction="vertical" />
            <el-link type="primary" underline="never" @click="handleChangeStatus(row)">改状态</el-link>
            <el-divider direction="vertical" />
            <el-link
              v-permission="['task:task:remove']"
              type="danger"
              underline="never"
              @click="handleDelete(row)"
            >
              删除
            </el-link>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="display: flex; justify-content: flex-end; margin-top: 12px">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @change="fetchList"
        />
      </div>
    </ele-card>

    <!-- 新增/编辑弹窗 / Add/Edit dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="formData.id ? '编辑任务' : '新建任务'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入任务标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="formData.projectId" filterable placeholder="请选择项目" style="width: 100%">
            <el-option v-for="p in projectOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属迭代">
          <el-select v-model="formData.iterationId" clearable placeholder="选择迭代（可选）" style="width: 100%" :disabled="!formData.projectId">
            <el-option v-for="it in iterationOptions" :key="it.id" :label="it.name" :value="it.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型" prop="taskType">
          <el-select v-model="formData.taskType" style="width: 100%">
            <el-option label="用户故事" value="story" />
            <el-option label="开发任务" value="task" />
            <el-option label="Bug" value="bug" />
            <el-option label="改进" value="improvement" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="formData.priority" style="width: 100%">
            <el-option label="P0 - 紧急" value="p0" />
            <el-option label="P1 - 高" value="p1" />
            <el-option label="P2 - 中" value="p2" />
            <el-option label="P3 - 低" value="p3" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="formData.dueDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="预估工时(h)">
          <el-input-number v-model="formData.estimatedHours" :min="0" :step="0.5" :precision="1" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 改状态弹窗 / Change status dialog -->
    <el-dialog v-model="statusDialogVisible" title="更新任务状态" width="320px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="新状态">
          <el-select v-model="newStatus" style="width: 100%">
            <el-option label="待办" value="todo" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitStatus">确定</el-button>
      </template>
    </el-dialog>

    <!-- 任务详情侧边栏 / Task detail drawer -->
    <el-drawer
      v-model="drawerVisible"
      :title="currentTask.title"
      size="480px"
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
        <el-descriptions-item label="预估工时">{{ currentTask.estimatedHours ?? '-' }} h</el-descriptions-item>
        <el-descriptions-item label="实际工时">{{ currentTask.actualHours ?? '-' }} h</el-descriptions-item>
        <el-descriptions-item label="描述">{{ currentTask.description || '暂无描述' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessageBox } from 'element-plus';
  import { EleMessage } from 'ele-admin-plus';
  import { SearchOutlined, PlusOutlined, ReloadOutlined, PictureOutlined as KanbanIcon } from '@/components/icons';
  import { pageTasks, addTask, updateTask, removeTask, updateTaskStatus } from '@/api/task';
  import { pageProjects, listIterations } from '@/api/project';

  const router = useRouter();

  const TYPE_LABEL = { story: '故事', task: '任务', bug: 'Bug', improvement: '改进' };
  const TYPE_TAG = { story: 'primary', task: '', bug: 'danger', improvement: 'warning' };
  const PRIORITY_TAG = { p0: 'danger', p1: 'warning', p2: 'primary', p3: 'info' };
  const STATUS_LABEL = { todo: '待办', in_progress: '进行中', completed: '已完成' };
  const STATUS_TAG = { todo: 'info', in_progress: 'warning', completed: 'success' };

  const loading = ref(false);
  const submitLoading = ref(false);
  const list = ref([]);
  const total = ref(0);
  const projectOptions = ref([]);
  const iterationOptions = ref([]);
  const dialogVisible = ref(false);
  const drawerVisible = ref(false);
  const statusDialogVisible = ref(false);
  const currentTask = ref({});
  const newStatus = ref('');
  const formRef = ref(null);

  const query = reactive({
    pageNum: 1,
    pageSize: 20,
    title: undefined,
    projectId: undefined,
    status: undefined,
    priority: undefined
  });

  const formData = reactive({
    id: undefined,
    title: '',
    projectId: undefined,
    iterationId: undefined,
    taskType: 'task',
    priority: 'p2',
    status: 'todo',
    dueDate: undefined,
    estimatedHours: undefined,
    description: ''
  });

  const rules = {
    title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
    projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
    taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }]
  };

  async function fetchList() {
    loading.value = true;
    try {
      const res = await pageTasks(query);
      list.value = res.rows ?? [];
      total.value = res.total ?? 0;
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      loading.value = false;
    }
  }

  function loadProjectOptions() {
    pageProjects({ pageSize: 200 }).then((res) => {
      projectOptions.value = res.rows ?? [];
    }).catch(() => {});
  }

  watch(() => formData.projectId, (val) => {
    iterationOptions.value = [];
    formData.iterationId = undefined;
    if (val) {
      listIterations(val, { pageSize: 100 }).then((res) => {
        iterationOptions.value = res.rows ?? [];
      }).catch(() => {});
    }
  });

  function handleSearch() {
    query.pageNum = 1;
    fetchList();
  }

  function handleReset() {
    query.title = undefined;
    query.projectId = undefined;
    query.status = undefined;
    query.priority = undefined;
    query.pageNum = 1;
    fetchList();
  }

  function openAddDialog() {
    Object.assign(formData, {
      id: undefined,
      title: '',
      projectId: undefined,
      iterationId: undefined,
      taskType: 'task',
      priority: 'p2',
      status: 'todo',
      dueDate: undefined,
      estimatedHours: undefined,
      description: ''
    });
    iterationOptions.value = [];
    dialogVisible.value = true;
  }

  function openEditDialog(item) {
    Object.assign(formData, { ...item });
    dialogVisible.value = true;
  }

  function openDetail(item) {
    currentTask.value = item;
    drawerVisible.value = true;
  }

  function goBoard() {
    router.push('/task/board');
  }

  async function handleSubmit() {
    await formRef.value?.validate();
    submitLoading.value = true;
    try {
      if (formData.id) {
        await updateTask(formData);
        EleMessage.success({ message: '修改成功', plain: true });
      } else {
        await addTask(formData);
        EleMessage.success({ message: '新建成功', plain: true });
      }
      dialogVisible.value = false;
      fetchList();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      submitLoading.value = false;
    }
  }

  function handleChangeStatus(row) {
    currentTask.value = row;
    newStatus.value = row.status;
    statusDialogVisible.value = true;
  }

  function handleSubmitStatus() {
    submitLoading.value = true;
    updateTaskStatus(currentTask.value.id, newStatus.value)
      .then(() => {
        EleMessage.success({ message: '状态已更新', plain: true });
        statusDialogVisible.value = false;
        fetchList();
      })
      .catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      })
      .finally(() => {
        submitLoading.value = false;
      });
  }

  function handleDelete(row) {
    ElMessageBox.confirm(`确认删除任务「${row.title}」？`, '提示', {
      type: 'warning',
      draggable: true
    })
      .then(() => {
        removeTask(row.id)
          .then(() => {
            EleMessage.success({ message: '删除成功', plain: true });
            fetchList();
          })
          .catch((e) => {
            EleMessage.error({ message: e.message, plain: true });
          });
      })
      .catch(() => {});
  }

  onMounted(() => {
    loadProjectOptions();
    fetchList();
  });
</script>
