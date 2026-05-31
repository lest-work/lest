<!-- 项目进度 -->
<template>
  <ele-card :header="title" :body-style="{ padding: '10px', height: '370px' }">
    <template #extra>
      <more-icon @command="handleCommand" />
    </template>
    <ele-pro-table
      :height="352"
      row-key="id"
      :columns="columns"
      :datasource="projectList"
      :show-overflow-tooltip="true"
      highlight-current-row
      :pagination="false"
      :toolbar="false"
      :bottom-line="false"
      size="large"
      class="project-table"
    >
      <template #projectName="{ row }">
        <el-link type="primary" underline="never">
          {{ row.projectName }}
        </el-link>
      </template>
      <template #status="{ row }">
        <ele-text v-if="row.status === 0" type="success">进行中</ele-text>
        <ele-text v-else-if="row.status === 1" type="danger">已延期</ele-text>
        <ele-text v-else-if="row.status === 2" type="warning">
          未开始
        </ele-text>
        <ele-text v-else-if="row.status === 3" type="info">已结束</ele-text>
      </template>
      <template #progress="{ row }">
        <el-progress :percentage="row.progress" />
      </template>
    </ele-pro-table>
  </ele-card>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import MoreIcon from './more-icon.vue';
  import { pageProjects } from '@/api/project';

  defineProps({
    title: String
  });

  const emit = defineEmits(['command']);

  /** 表格列配置 */
  const columns = ref([
    {
      type: 'index',
      columnKey: 'index',
      width: 50,
      align: 'center' /* ,
      fixed: 'left' */
    },
    {
      prop: 'projectName',
      label: '项目名称',
      slot: 'projectName',
      minWidth: 110
    },
    {
      prop: 'startDate',
      label: '开始时间',
      align: 'center',
      minWidth: 110
    },
    {
      prop: 'endDate',
      label: '结束时间',
      align: 'center',
      minWidth: 110
    },
    {
      prop: 'status',
      label: '状态',
      slot: 'status',
      align: 'center',
      width: 90
    },
    {
      prop: 'progress',
      label: '进度',
      width: 180,
      align: 'center',
      slot: 'progress',
      showOverflowTooltip: false
    }
  ]);

  /** 项目进度数据 */
  const projectList = ref([]);

  /**
   * 项目 status 映射：
   *   后端 1=活跃→前端 0(进行中)，2=已归档→前端 3(已结束)
   *   通过日期计算是否已延期：endDate < today → 1(已延期)
   *   startDate > today → 2(未开始)
   */
  const mapProjectStatus = (p) => {
    if (p.status === 2) return 3;
    const today = new Date();
    if (p.startDate && new Date(p.startDate) > today) return 2;
    if (p.endDate && new Date(p.endDate) < today) return 1;
    return 0;
  };

  /** 查询项目进度 */
  const queryProjectList = async () => {
    try {
      const res = await pageProjects({ pageNum: 1, pageSize: 10 });
      projectList.value = (res.rows ?? []).map((p) => ({
        id: p.projectId,
        projectName: p.name,
        status: mapProjectStatus(p),
        startDate: p.startDate,
        endDate: p.endDate,
        progress: p.progress ?? 0
      }));
    } catch {
      projectList.value = [];
    }
  };

  const handleCommand = (command) => {
    if (command === 'refresh') queryProjectList();
    emit('command', command);
  };

  onMounted(queryProjectList);
</script>

<style lang="scss" scoped>
  .project-table :deep(.el-progress__text) {
    font-size: 12px !important;
  }
</style>
