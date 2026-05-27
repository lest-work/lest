<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">排行榜</span>
          <div class="toolbar-right">
            <el-select v-model="range" size="small" style="width: 130px" @change="fetchData">
              <el-option value="today" label="今天" />
              <el-option value="this_week" label="本周" />
              <el-option value="this_month" label="本月" />
              <el-option value="this_year" label="本年" />
            </el-select>
          </div>
        </div>
      </template>

      <ele-table
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="{ show: true, total: total, modelValue: pager, onUpdate: fetchData }"
        row-key="user_id"
        stripe
      >
        <template #rank="{ row, $index }">
          <span class="rank-badge" :class="rankClass($index)">{{ row.rank ?? $index + 1 }}</span>
        </template>

        <template #user="{ row }">
          <div class="user-cell">
            <el-avatar :size="32" :src="row.avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <div class="user-info">
              <span class="username">{{ row.username || row.name }}</span>
              <span class="display-name" v-if="row.displayName">{{ row.displayName }}</span>
            </div>
          </div>
        </template>

        <template #totalTime="{ row }">
          <span class="time-value">{{ row.text || row.digital }}</span>
        </template>

        <template #percent="{ row }">
          <div class="progress-cell">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: row.percent + '%' }" />
            </div>
            <span class="progress-text">{{ row.percent?.toFixed(1) }}%</span>
          </div>
        </template>
      </ele-table>
    </ele-card>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted, reactive } from 'vue';
  import { UserFilled } from '@element-plus/icons-vue';
  import { getWakapiLeaders, type WakapiLeaderData } from '@/api/wakapi';

  defineOptions({ name: 'WakapiLeaderboard' });

  const loading = ref(false);
  const range = ref('this_week');
  const data = ref<WakapiLeaderData[]>([]);
  const total = computed(() => data.value.length);
  const pager = reactive({ page: 1, limit: 50 });

  const columns = [
    { columnKey: 'rank', label: '排名', width: 80, slot: true, align: 'center' },
    { columnKey: 'user', label: '用户', minWidth: 180, slot: true },
    { columnKey: 'totalTime', label: '编码时长', width: 140, slot: true, align: 'center' },
    { columnKey: 'percent', label: '占比', minWidth: 200, slot: true }
  ];

  const rankClass = (idx: number): string => {
    if (idx === 0) return 'gold';
    if (idx === 1) return 'silver';
    if (idx === 2) return 'bronze';
    return '';
  };

  const fetchData = async () => {
    loading.value = true;
    try {
      const result = await getWakapiLeaders({ range: range.value, limit: 200 });
      const leaders = (result ?? []).map((l, idx) => ({
        ...l,
        rank: idx + 1,
        percent: 0
      }));
      if (leaders.length > 0) {
        const max = leaders[0].total_seconds;
        leaders.forEach(l => {
          l.percent = max > 0 ? (l.total_seconds / max) * 100 : 0;
        });
      }
      data.value = leaders;
    } catch {
      data.value = [];
    } finally {
      loading.value = false;
    }
  };

  onMounted(() => {
    fetchData();
  });
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    .title { font-size: 15px; font-weight: 500; }
    .toolbar-right { display: flex; gap: 8px; align-items: center; }
  }

  .rank-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    font-size: 13px;
    font-weight: 600;
    background: var(--el-fill-color-light);
    color: var(--el-text-color-regular);

    &.gold {
      background: linear-gradient(135deg, #fdf6ec, #f3d29e);
      color: #b8860b;
    }
    &.silver {
      background: linear-gradient(135deg, #f5f5f5, #d0d0d0);
      color: #696969;
    }
    &.bronze {
      background: linear-gradient(135deg, #fdf6ec, #d4a574);
      color: #a0522d;
    }
  }

  .user-cell {
    display: flex;
    align-items: center;
    gap: 10px;
    .user-info {
      display: flex;
      flex-direction: column;
      .username {
        font-size: 13px;
        font-weight: 500;
        color: var(--el-text-color-primary);
      }
      .display-name {
        font-size: 11px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .time-value {
    font-size: 14px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .progress-cell {
    display: flex;
    align-items: center;
    gap: 8px;
    .progress-bar {
      flex: 1;
      height: 8px;
      background: var(--el-fill-color-light);
      border-radius: 4px;
      overflow: hidden;
      .progress-fill {
        height: 100%;
        background: var(--el-color-primary);
        border-radius: 4px;
        transition: width 0.3s;
      }
    }
    .progress-text {
      width: 48px;
      text-align: right;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      flex-shrink: 0;
    }
  }
</style>
