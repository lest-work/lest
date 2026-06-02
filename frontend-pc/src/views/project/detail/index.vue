<template>
  <ele-page>
    <!-- 面包屑+头部 / Breadcrumb + header -->
    <ele-card style="margin-bottom: 16px">
      <div style="display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 12px">
        <div style="display: flex; align-items: center; gap: 12px">
          <el-button :icon="ArrowLeftOutlined" circle @click="$router.back()" />
          <div>
            <div style="font-size: 18px; font-weight: 600">{{ project.name || '项目详情' }}</div>
            <div style="font-size: 13px; color: #999; margin-top: 2px">
              <el-tag :type="project.status === 1 ? 'success' : 'info'" size="small">
                {{ project.status === 1 ? '活跃' : '已归档' }}
              </el-tag>
              <span style="margin-left: 8px">{{ TEMPLATE_LABEL[project.template] || '' }}</span>
              <span v-if="project.ownerName" style="margin-left: 8px">负责人：{{ project.ownerName }}</span>
            </div>
          </div>
        </div>
        <div style="display: flex; gap: 8px">
          <el-button :icon="KanbanIcon" @click="goBoard">任务看板</el-button>
          <el-button
            v-permission="['task:task:add']"
            type="primary"
            :icon="PlusOutlined"
            @click="openTaskDialog"
          >
            新建任务
          </el-button>
        </div>
      </div>
    </ele-card>

    <!-- Tab 页签 / Tab panels -->
    <ele-card>
      <el-tabs v-model="activeTab">
        <!-- 概况 -->
        <el-tab-pane label="概况" name="overview">
          <div style="padding: 8px 0; max-width: 640px">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="项目名称">{{ project.name }}</el-descriptions-item>
              <el-descriptions-item label="模板">{{ TEMPLATE_LABEL[project.template] || '-' }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="project.status === 1 ? 'success' : 'info'" size="small">
                  {{ project.status === 1 ? '活跃' : '已归档' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="负责人">{{ project.ownerName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ project.startDate || '-' }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ project.endDate || '-' }}</el-descriptions-item>
              <el-descriptions-item label="项目描述" :span="2">{{ project.description || '暂无描述' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-tab-pane>

        <!-- 成员 -->
        <el-tab-pane label="成员" name="member">
          <div style="margin-bottom: 12px">
            <el-button
              v-permission="['project:project:edit']"
              type="primary"
              :icon="PlusOutlined"
              size="small"
              @click="openMemberDialog"
            >
              添加成员
            </el-button>
          </div>
          <el-table :data="members" stripe style="width: 100%">
            <el-table-column prop="userName" label="用户名" min-width="120" />
            <el-table-column prop="nickName" label="姓名" min-width="120" />
            <el-table-column prop="role" label="角色" min-width="100">
              <template #default="{ row }">
                <el-tag :type="row.role === 'admin' ? 'danger' : row.role === 'developer' ? 'primary' : 'info'" size="small">
                  {{ ROLE_LABEL[row.role] || row.role }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="joinedAt" label="加入时间" min-width="160" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-link
                  v-permission="['project:project:edit']"
                  type="danger"
                  underline="never"
                  @click="handleRemoveMember(row)"
                >
                  移除
                </el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 迭代 -->
        <el-tab-pane label="迭代" name="iteration">
          <div style="margin-bottom: 12px">
            <el-button
              v-permission="['project:project:edit']"
              type="primary"
              :icon="PlusOutlined"
              size="small"
              @click="openIterationDialog(null)"
            >
              新建迭代
            </el-button>
          </div>
          <el-table :data="iterations" stripe>
            <el-table-column prop="name" label="迭代名称" min-width="160" />
            <el-table-column prop="goal" label="目标" min-width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="ITER_STATUS_TYPE[row.status]" size="small">
                  {{ ITER_STATUS_LABEL[row.status] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始" width="110" />
            <el-table-column prop="endDate" label="截止" width="110" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-link type="primary" underline="never" @click="openIterationDialog(row)">编辑</el-link>
                <el-divider direction="vertical" />
                <el-link type="danger" underline="never" @click="handleDeleteIteration(row)">删除</el-link>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 任务 -->
        <el-tab-pane label="任务" name="task">
          <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap; margin-bottom: 12px">
            <el-select v-model="taskQuery.status" clearable placeholder="状态" style="width: 110px" @change="fetchTasks">
              <el-option label="待办" value="todo" />
              <el-option label="进行中" value="in_progress" />
              <el-option label="已完成" value="completed" />
            </el-select>
            <el-select v-model="taskQuery.priority" clearable placeholder="优先级" style="width: 100px" @change="fetchTasks">
              <el-option label="P0" value="p0" />
              <el-option label="P1" value="p1" />
              <el-option label="P2" value="p2" />
              <el-option label="P3" value="p3" />
            </el-select>
            <el-select v-model="taskQuery.iterationId" clearable placeholder="迭代" style="width: 140px" @change="fetchTasks">
              <el-option v-for="it in iterations" :key="it.iterationId" :label="it.name" :value="it.iterationId" />
            </el-select>
          </div>

          <!-- 任务卡片网格 -->
          <ele-loading :loading="taskLoading" style="min-height: 200px">
            <div v-if="taskList.length === 0 && !taskLoading" style="text-align: center; padding: 60px 0; color: #999">
              暂无任务
            </div>
            <el-row :gutter="16">
              <el-col
                v-for="item in taskList"
                :key="item.taskId"
                :xs="24"
                :sm="12"
                :md="8"
                :lg="6"
                style="margin-bottom: 16px"
              >
                <el-card
                  shadow="hover"
                  style="cursor: pointer; min-height: 140px; display: flex; flex-direction: column"
                  @click="openTaskEdit(item)"
                >
                  <div style="display: flex; justify-content: space-between; align-items: flex-start">
                    <div style="flex: 1; overflow: hidden">
                      <div style="font-size: 15px; font-weight: 600; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                        {{ item.title }}
                      </div>
                      <div style="display: flex; flex-wrap: wrap; gap: 4px">
                        <el-tag :type="TASK_TYPE_TAG[item.taskType]" size="small">
                          {{ TASK_TYPE_LABEL[item.taskType] || item.taskType }}
                        </el-tag>
                        <el-tag :type="TASK_PRIORITY_TAG[item.priority]" size="small">
                          {{ item.priority?.toUpperCase() }}
                        </el-tag>
                        <el-tag :type="TASK_STATUS_TAG[item.status]" size="small">
                          {{ TASK_STATUS_LABEL[item.status] || item.status }}
                        </el-tag>
                      </div>
                    </div>
                    <el-dropdown @click.stop>
                      <el-icon style="cursor: pointer; padding: 4px; flex-shrink: 0"><IconElMore /></el-icon>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item
                            v-permission="['task:task:remove']"
                            style="color: #f56c6c"
                            @click.stop="handleDeleteTask(item)"
                          >
                            删除
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                  <div
                    style="
                      flex: 1;
                      margin-top: 10px;
                      font-size: 13px;
                      color: #666;
                      overflow: hidden;
                      display: -webkit-box;
                      -webkit-line-clamp: 2;
                      -webkit-box-orient: vertical;
                    "
                  >
                    {{ item.description || '暂无描述' }}
                  </div>
                  <div style="margin-top: 10px; font-size: 12px; color: #999; display: flex; gap: 12px; flex-wrap: wrap">
                    <span v-if="item.assigneeName">负责人：{{ item.assigneeName }}</span>
                    <span v-if="item.dueDate">截止：{{ item.dueDate }}</span>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 分页 -->
            <div v-if="taskTotal > 0" style="display: flex; justify-content: flex-end; margin-top: 8px">
              <el-pagination
                v-model:current-page="taskQuery.pageNum"
                v-model:page-size="taskQuery.pageSize"
                :total="taskTotal"
                :page-sizes="[12, 24, 48]"
                layout="total, sizes, prev, pager, next"
                @change="fetchTasks"
              />
            </div>
          </ele-loading>
        </el-tab-pane>

        <!-- 里程碑 -->
        <el-tab-pane label="里程碑" name="milestone">
          <div style="margin-bottom: 12px">
            <el-button
              v-permission="['project:project:edit']"
              type="primary"
              :icon="PlusOutlined"
              size="small"
              @click="openMilestoneDialog"
            >
              新建里程碑
            </el-button>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="m in milestones"
              :key="m.milestoneId"
              :timestamp="m.targetDate"
              placement="top"
            >
              <el-card>
                <div style="display: flex; justify-content: space-between">
                  <div>
                    <div style="font-weight: 600">{{ m.name }}</div>
                    <div style="color: #666; font-size: 13px; margin-top: 4px">{{ m.description }}</div>
                  </div>
                  <el-link
                    v-permission="['project:project:remove']"
                    type="danger"
                    underline="never"
                    @click="handleDeleteMilestone(m)"
                  >
                    删除
                  </el-link>
                </div>
              </el-card>
            </el-timeline-item>
            <el-timeline-item v-if="milestones.length === 0">
              <span style="color: #999">暂无里程碑</span>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
    </ele-card>

    <!-- 新建迭代弹窗 / Iteration dialog -->
    <el-dialog
      v-model="iterationDialogVisible"
      :title="iterForm.iterationId ? '编辑迭代' : '新建迭代'"
      width="480px"
      destroy-on-close
    >
      <el-form ref="iterFormRef" :model="iterForm" :rules="iterRules" label-width="80px">
        <el-form-item label="迭代名称" prop="name">
          <el-input v-model="iterForm.name" placeholder="如：Sprint 1" />
        </el-form-item>
        <el-form-item label="迭代目标">
          <el-input v-model="iterForm.goal" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="iterForm.startDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="iterForm.endDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="iterationDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="handleSaveIteration">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新建任务弹窗 / Task dialog -->
    <el-dialog v-model="taskDialogVisible" :title="taskForm.taskId ? '编辑任务' : '新建任务'" width="520px" destroy-on-close>
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="90px">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="taskForm.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="所属迭代">
          <el-select v-model="taskForm.iterationId" clearable placeholder="选择迭代（可选）" style="width: 100%">
            <el-option v-for="it in iterations" :key="it.iterationId" :label="it.name" :value="it.iterationId" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="taskForm.taskType" style="width: 100%">
            <el-option label="用户故事" value="story" />
            <el-option label="开发任务" value="task" />
            <el-option label="Bug" value="bug" />
            <el-option label="改进" value="improvement" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="taskForm.priority" style="width: 100%">
            <el-option label="P0 - 紧急" value="p0" />
            <el-option label="P1 - 高" value="p1" />
            <el-option label="P2 - 中" value="p2" />
            <el-option label="P3 - 低" value="p3" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="taskForm.dueDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="预估工时(h)">
          <el-input-number v-model="taskForm.estimatedHours" :min="0" :step="0.5" :precision="1" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="taskForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="taskSaveLoading" @click="handleSaveTask">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新建里程碑弹窗 / Milestone dialog -->
    <el-dialog v-model="milestoneDialogVisible" title="新建里程碑" width="440px" destroy-on-close>
      <el-form ref="milestoneFormRef" :model="milestoneForm" :rules="msRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="milestoneForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="milestoneForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="目标日期">
          <el-date-picker v-model="milestoneForm.targetDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="milestoneDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saveLoading" @click="handleSaveMilestone">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted, watch } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import { ElMessageBox } from 'element-plus';
  import { EleMessage } from 'ele-admin-plus';
  import { ArrowLeftOutlined, PlusOutlined, AppstoreOutlined as KanbanIcon, IconElMore } from '@/components/icons';
  import {
    getProject,
    getProjectMembers,
    removeProjectMember,
    listIterations,
    addIteration,
    updateIteration,
    removeIteration,
    listMilestones,
    addMilestone,
    removeMilestone
  } from '@/api/project';
  import { pageTasks, addTask, updateTask, removeTask } from '@/api/task';

  const route = useRoute();
  const router = useRouter();

  const TEMPLATE_LABEL = { agile: '敏捷开发', kanban: '看板', waterfall: '瀑布流' };
  const ROLE_LABEL = { admin: '管理员', developer: '开发者', observer: '观察者' };
  const ITER_STATUS_LABEL = { 1: '计划中', 2: '进行中', 3: '已完成' };
  const ITER_STATUS_TYPE = { 1: 'info', 2: 'warning', 3: 'success' };
  const TASK_TYPE_LABEL = { story: '故事', task: '任务', bug: 'Bug', improvement: '改进' };
  const TASK_TYPE_TAG = { story: 'primary', task: '', bug: 'danger', improvement: 'warning' };
  const TASK_PRIORITY_TAG = { p0: 'danger', p1: 'warning', p2: 'primary', p3: 'info' };
  const TASK_STATUS_LABEL = { todo: '待办', in_progress: '进行中', completed: '已完成' };
  const TASK_STATUS_TAG = { todo: 'info', in_progress: 'warning', completed: 'success' };

  const activeTab = ref('overview');
  const saveLoading = ref(false);

  const project = ref({});
  const members = ref([]);
  const iterations = ref([]);
  const milestones = ref([]);

  const iterationDialogVisible = ref(false);
  const milestoneDialogVisible = ref(false);
  const taskDialogVisible = ref(false);
  const iterFormRef = ref(null);
  const milestoneFormRef = ref(null);
  const taskFormRef = ref(null);

  const taskLoading = ref(false);
  const taskSaveLoading = ref(false);
  const taskList = ref([]);
  const taskTotal = ref(0);

  const taskQuery = reactive({
    pageNum: 1,
    pageSize: 20,
    projectId: undefined,
    status: undefined,
    priority: undefined,
    iterationId: undefined
  });

  const taskForm = reactive({
    projectId: undefined,
    iterationId: undefined,
    title: '',
    taskType: 'task',
    priority: 'p2',
    status: 'todo',
    dueDate: undefined,
    estimatedHours: undefined,
    description: ''
  });

  const taskRules = {
    title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }]
  };

  const iterForm = reactive({
    iterationId: undefined,
    projectId: undefined,
    name: '',
    goal: '',
    startDate: undefined,
    endDate: undefined
  });

  const milestoneForm = reactive({
    projectId: undefined,
    name: '',
    description: '',
    targetDate: undefined
  });

  const iterRules = { name: [{ required: true, message: '请输入迭代名称', trigger: 'blur' }] };
  const msRules = { name: [{ required: true, message: '请输入里程碑名称', trigger: 'blur' }] };

  function getProjectId() {
    return Number(route.params.id);
  }

  async function loadAll() {
    const id = getProjectId();
    getProject(id).then((data) => { project.value = data; }).catch(() => {});
    getProjectMembers(id).then((data) => { members.value = data; }).catch(() => {});
    listIterations(id, { pageSize: 100 }).then((res) => { iterations.value = res.rows ?? []; }).catch(() => {});
    listMilestones(id).then((data) => { milestones.value = data; }).catch(() => {});
  }

  function openIterationDialog(item) {
    Object.assign(iterForm, {
      iterationId: item ? item.iterationId : undefined,
      projectId: getProjectId(),
      name: item ? item.name : '',
      goal: item ? item.goal : '',
      startDate: item ? item.startDate : undefined,
      endDate: item ? item.endDate : undefined
    });
    iterationDialogVisible.value = true;
  }

  function handleSaveIteration() {
    iterFormRef.value?.validate().then(() => {
      saveLoading.value = true;
      const projectId = getProjectId();
      const fn = iterForm.iterationId ? updateIteration(iterForm) : addIteration(projectId, iterForm);
      fn.then(() => {
        EleMessage.success({ message: '保存成功', plain: true });
        iterationDialogVisible.value = false;
        listIterations(projectId, { pageSize: 100 }).then((res) => {
          iterations.value = res.rows ?? [];
        });
      }).catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      }).finally(() => {
        saveLoading.value = false;
      });
    });
  }

  function handleDeleteIteration(row) {
    ElMessageBox.confirm(`确认删除迭代「${row.name}」？`, '提示', { type: 'warning', draggable: true })
      .then(() => {
        removeIteration(row.iterationId).then(() => {
          EleMessage.success({ message: '删除成功', plain: true });
          iterations.value = iterations.value.filter((i) => i.iterationId !== row.iterationId);
        });
      })
      .catch(() => {});
  }

  function openMilestoneDialog() {
    Object.assign(milestoneForm, { projectId: getProjectId(), name: '', description: '', targetDate: undefined });
    milestoneDialogVisible.value = true;
  }

  function handleSaveMilestone() {
    milestoneFormRef.value?.validate().then(() => {
      saveLoading.value = true;
      addMilestone(getProjectId(), milestoneForm).then(() => {
        EleMessage.success({ message: '保存成功', plain: true });
        milestoneDialogVisible.value = false;
        listMilestones(getProjectId()).then((data) => { milestones.value = data; });
      }).catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      }).finally(() => {
        saveLoading.value = false;
      });
    });
  }

  function handleDeleteMilestone(m) {
    ElMessageBox.confirm(`确认删除里程碑「${m.name}」？`, '提示', { type: 'warning', draggable: true })
      .then(() => {
        removeMilestone(m.milestoneId).then(() => {
          EleMessage.success({ message: '删除成功', plain: true });
          milestones.value = milestones.value.filter((x) => x.milestoneId !== m.milestoneId);
        });
      })
      .catch(() => {});
  }

  function openMemberDialog() {
    EleMessage.warning({ message: '成员管理功能建设中', plain: true });
  }

  function handleRemoveMember(row) {
    ElMessageBox.confirm(`确认移除成员「${row.nickName || row.userName}」？`, '提示', {
      type: 'warning',
      draggable: true
    }).then(() => {
      removeProjectMember(getProjectId(), row.userId).then(() => {
        EleMessage.success({ message: '移除成功', plain: true });
        members.value = members.value.filter((m) => m.userId !== row.userId);
      });
    }).catch(() => {});
  }

  async function fetchTasks() {
    taskLoading.value = true;
    try {
      taskQuery.projectId = getProjectId();
      const res = await pageTasks(taskQuery);
      taskList.value = res.rows ?? [];
      taskTotal.value = res.total ?? 0;
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      taskLoading.value = false;
    }
  }

  function openTaskDialog() {
    Object.assign(taskForm, {
      projectId: getProjectId(),
      iterationId: undefined,
      title: '',
      taskType: 'task',
      priority: 'p2',
      status: 'todo',
      dueDate: undefined,
      estimatedHours: undefined,
      description: ''
    });
    taskDialogVisible.value = true;
  }

  function openTaskEdit(item) {
    Object.assign(taskForm, {
      taskId: item.taskId,
      projectId: getProjectId(),
      iterationId: item.iterationId,
      title: item.title,
      taskType: item.taskType || 'task',
      priority: item.priority || 'p2',
      status: item.status || 'todo',
      dueDate: item.dueDate,
      estimatedHours: item.estimatedHours,
      description: item.description
    });
    taskDialogVisible.value = true;
  }

  function handleSaveTask() {
    taskFormRef.value?.validate().then(() => {
      taskSaveLoading.value = true;
      const fn = taskForm.taskId ? updateTask(taskForm) : addTask(taskForm);
      fn
        .then(() => {
          EleMessage.success({ message: taskForm.taskId ? '修改成功' : '新建成功', plain: true });
          taskDialogVisible.value = false;
          fetchTasks();
        })
        .catch((e) => {
          EleMessage.error({ message: e.message, plain: true });
        })
        .finally(() => {
          taskSaveLoading.value = false;
        });
    });
  }

  function handleDeleteTask(row) {
    ElMessageBox.confirm(`确认删除任务「${row.title}」？`, '提示', { type: 'warning', draggable: true })
      .then(() => {
        removeTask(row.taskId)
          .then(() => {
            EleMessage.success({ message: '删除成功', plain: true });
            fetchTasks();
          })
          .catch((e) => {
            EleMessage.error({ message: e.message, plain: true });
          });
      })
      .catch(() => {});
  }

  function goBoard() {
    router.push({ path: '/task/board', query: { projectId: getProjectId() } });
  }

  watch(activeTab, (val) => {
    if (val === 'task') fetchTasks();
  });

  onMounted(loadAll);
</script>
