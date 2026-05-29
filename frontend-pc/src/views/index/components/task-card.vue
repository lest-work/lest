<!-- 我的任务 -->
<template>
  <ele-card
    :header="title"
    :body-style="{ height: '370px', padding: '7px 8px 6px 8px' }"
  >
    <template #extra>
      <more-icon @command="handleCommand" />
    </template>
    <el-scrollbar
      :wrap-style="{ position: 'relative', zIndex: 1 }"
      @scroll="handleTaskTableScroll"
    >
      <ele-table
        size="large"
        :class="['task-table', { 'is-ping-left': isPingLeft }]"
      >
        <thead :style="{ position: 'sticky', top: 0, zIndex: 2 }">
          <tr>
            <th :style="{ width: '38px' }" class="task-table-index"></th>
            <th :style="{ textAlign: 'center', width: '78px' }">优先级</th>
            <th>任务名称</th>
            <th :style="{ textAlign: 'center', width: '80px' }">状态</th>
          </tr>
        </thead>
        <vue-draggable
          tag="tbody"
          item-key="id"
          v-model="taskList"
          handle=".sort-handle"
          :animation="300"
          :set-data="() => void 0"
          :force-fallback="true"
        >
          <template #item="{ element }">
            <tr>
              <td
                :style="{ paddingLeft: 0, paddingRight: 0, width: '38px' }"
                class="task-table-index"
              >
                <ele-text
                  :icon="DragOutlined"
                  :icon-style="{ transform: 'scale(1.15)' }"
                  type="placeholder"
                  class="sort-handle"
                />
              </td>
              <td style="text-align: center; width: 78px">
                <el-tag
                  v-if="element.priority === 1"
                  type="danger"
                  :disable-transitions="true"
                >
                  {{ element.priority }}
                </el-tag>
                <el-tag
                  v-else-if="element.priority === 2"
                  type="warning"
                  :disable-transitions="true"
                >
                  {{ element.priority }}
                </el-tag>
                <el-tag v-else :disable-transitions="true">
                  {{ element.priority }}
                </el-tag>
              </td>
              <td>
                <ele-ellipsis style="line-height: 20px">
                  <el-link type="primary" underline="never">
                    {{ element.taskName }}
                  </el-link>
                </ele-ellipsis>
              </td>
              <td style="text-align: center; width: 80px">
                <ele-text v-if="element.status === 0" type="warning">
                  未开始
                </ele-text>
                <ele-text v-else-if="element.status === 1" type="success">
                  进行中
                </ele-text>
                <ele-text v-else-if="element.status === 2" type="info" deleted>
                  已完成
                </ele-text>
              </td>
            </tr>
          </template>
        </vue-draggable>
      </ele-table>
    </el-scrollbar>
  </ele-card>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import VueDraggable from 'vuedraggable';
  import { DragOutlined } from '@/components/icons';
  import MoreIcon from './more-icon.vue';
  import { pageTasks } from '@/api/task';
  import { useUserStore } from '@/store/modules/user';

  defineProps({
    title: String
  });

  const emit = defineEmits(['command']);

  const userStore = useUserStore();

  /** 优先级映射：p0→1(最高)，p1→2，p2→3，p3→4 */
  const PRIORITY_MAP = { p0: 1, p1: 2, p2: 3, p3: 4 };
  /** 状态映射：todo→0(未开始)，in_progress→1(进行中)，completed→2(已完成) */
  const STATUS_MAP = { todo: 0, in_progress: 1, completed: 2 };

  /** 我的任务数据 */
  const taskList = ref([]);

  /** 查询我的任务（分配给当前用户，未完成的优先显示） */
  const queryTaskList = async () => {
    try {
      const userId = userStore.info?.userId;
      const res = await pageTasks({
        assigneeId: userId,
        pageNum: 1,
        pageSize: 20
      });
      taskList.value = (res.rows ?? []).map((t) => ({
        id: t.id,
        priority: PRIORITY_MAP[t.priority] ?? 3,
        taskName: t.title,
        status: STATUS_MAP[t.status] ?? 0
      }));
    } catch {
      taskList.value = [];
    }
  };

  const handleCommand = (command) => {
    if (command === 'refresh') queryTaskList();
    emit('command', command);
  };

  onMounted(queryTaskList);

  /** 我的任务表格左侧列是否固定状态 */
  const isPingLeft = ref(false);

  /** 我的任务表格滚动事件 */
  const handleTaskTableScroll = ({ scrollLeft }) => {
    isPingLeft.value = scrollLeft > 1;
  };
</script>

<style lang="scss" scoped>
  .task-table {
    table-layout: fixed;
    min-width: 300px;

    .sort-handle {
      cursor: move;
    }

    .el-tag {
      width: 20px;
      height: 20px;
      line-height: 20px;
      border-radius: 50%;
    }

    td,
    th {
      box-sizing: border-box;
    }

    tr.sortable-chosen {
      user-select: none;
    }

    tr.sortable-ghost {
      opacity: 0;
    }

    tr.sortable-fallback {
      opacity: 1 !important;
      display: table !important;
      table-layout: fixed !important;

      td {
        background: var(--el-color-primary-light-8);
      }
    }

    :deep(.task-table-index) {
      text-align: center;
      position: sticky;
      left: 0;
      z-index: 1;
    }

    &.is-ping-left :deep(.task-table-index) {
      backdrop-filter: var(--ele-table-fixed-backdrop-filter);

      &::before {
        content: '';
        width: 10px;
        position: absolute;
        top: 0;
        bottom: -1px;
        right: -10px;
        box-shadow: var(--ele-table-fixed-left-shadow);
        transition: box-shadow 0.2s;
        pointer-events: none;
      }

      &::after {
        display: none;
      }
    }
  }
</style>
