<!-- 最新动态 -->
<template>
  <ele-card :header="title" :body-style="{ padding: '6px 0', height: '370px' }">
    <template #extra>
      <more-icon @command="handleCommand" />
    </template>
    <el-scrollbar :view-style="{ padding: '20px 20px 0 20px' }">
      <el-timeline v-if="activities.length" :reverse="false" class="demo-timeline">
        <el-timeline-item
          v-for="item in activities"
          :key="item.id"
          :timestamp="item.time"
          :type="item.nodeType"
          :hollow="true"
        >
          <span class="activity-name">{{ item.operName }}</span>
          <span class="activity-action">{{ item.actionLabel }}</span>
          <span class="activity-target">{{ item.title }}</span>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else-if="!loading" description="暂无动态" :image-size="60" />
    </el-scrollbar>
  </ele-card>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import MoreIcon from './more-icon.vue';
  import { getDashboardActivities } from '@/api/dashboard';

  defineProps({
    title: String
  });

  const emit = defineEmits(['command']);

  const activities = ref([]);
  const loading = ref(false);

  const BUSINESS_TYPE_LABEL = {
    0: '操作了',
    1: '新增了',
    2: '修改了',
    3: '删除了',
    4: '授权了',
    5: '导出了',
    6: '导入了',
    7: '强退了',
    8: '生成了',
    9: '清空了'
  };

  const NODE_TYPE_MAP = {
    1: 'primary',
    2: 'warning',
    3: 'danger',
    4: 'success',
    5: 'info',
    6: 'info',
    7: 'danger',
    8: 'warning',
    9: 'danger'
  };

  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    const d = new Date(timeStr);
    const now = new Date();
    const isToday =
      d.getFullYear() === now.getFullYear() &&
      d.getMonth() === now.getMonth() &&
      d.getDate() === now.getDate();
    const h = String(d.getHours()).padStart(2, '0');
    const m = String(d.getMinutes()).padStart(2, '0');
    if (isToday) return `${h}:${m}`;
    return `${d.getMonth() + 1}-${d.getDate()} ${h}:${m}`;
  };

  const queryActivities = async () => {
    loading.value = true;
    try {
      const data = await getDashboardActivities(15);
      activities.value = data.map((item) => ({
        id: item.id,
        operName: item.operName || '系统',
        title: item.title || '-',
        actionLabel: BUSINESS_TYPE_LABEL[item.businessType] ?? '操作了',
        nodeType: item.status === 1 ? 'danger' : (NODE_TYPE_MAP[item.businessType] ?? ''),
        time: formatTime(item.operTime)
      }));
    } catch {
      activities.value = [];
    } finally {
      loading.value = false;
    }
  };

  const handleCommand = (command) => {
    if (command === 'refresh') queryActivities();
    emit('command', command);
  };

  onMounted(queryActivities);
</script>

<style lang="scss" scoped>
  /* 时间轴 */
  .demo-timeline {
    padding-left: 0;

    :deep(.el-timeline-item__wrapper) {
      display: flex;

      .el-timeline-item__timestamp {
        order: 0;
        flex-shrink: 0;
        margin: 0 16px 0 0;
        height: 22px;
        line-height: 22px;
        font-size: 14px;
      }

      .el-timeline-item__content {
        order: 1;
        flex: 1;
      }
    }

    :deep(.el-timeline-item__node) {
      top: 3px;
      --el-color-white: var(--el-bg-color);
    }

    :deep(.el-timeline-item__tail) {
      top: 3px;
    }
  }

  .activity-name {
    font-weight: 600;
    margin-right: 4px;
  }

  .activity-action {
    color: var(--el-text-color-secondary);
    margin-right: 4px;
  }

  .activity-target {
    color: var(--el-color-primary);
  }
</style>
