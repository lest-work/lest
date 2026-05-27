<template>
  <ele-card :loading="loading">
    <template #header>
      <div class="ele-card-title">
        <span>编码统计</span>
        <el-radio-group v-model="range" size="small" style="margin-left: auto">
          <el-radio-button value="today">今天</el-radio-button>
          <el-radio-button value="this_week">本周</el-radio-button>
          <el-radio-button value="this_month">本月</el-radio-button>
          <el-radio-button value="this_year">本年</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <el-row :gutter="16">
      <!-- 总时长卡片 -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-item">
          <div class="stat-value">{{ formatTotal(statsData?.total_seconds) }}</div>
          <div class="stat-label">编码时长</div>
          <div class="stat-sub">{{ statsData?.timezone || 'Asia/Shanghai' }}</div>
        </div>
      </el-col>

      <!-- 日均时长 -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-item">
          <div class="stat-value">{{ formatDigital(statsData?.daily_average) }}</div>
          <div class="stat-label">日均时长</div>
          <div class="stat-sub">每天平均</div>
        </div>
      </el-col>

      <!-- 项目数 -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-item">
          <div class="stat-value">{{ statsData?.projects?.length ?? '-' }}</div>
          <div class="stat-label">活跃项目</div>
          <div class="stat-sub">本周期内</div>
        </div>
      </el-col>

      <!-- 贡献天数 -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-item">
          <div class="stat-value">{{ daysWithActivity }}</div>
          <div class="stat-label">编码天数</div>
          <div class="stat-sub">本周期内</div>
        </div>
      </el-col>
    </el-row>

    <!-- 分类环形图 -->
    <el-row :gutter="16" style="margin-top: 20px">
      <el-col :xs="24" :sm="8">
        <div class="chart-wrapper">
          <div class="chart-title">语言</div>
          <div v-if="topLanguages?.length" class="donut-chart">
            <div class="donut-items">
              <div
                v-for="(item, idx) in topLanguages.slice(0, 5)"
                :key="item.name"
                class="donut-item"
              >
                <span class="donut-dot" :style="{ background: DONUT_COLORS[idx] }"></span>
                <span class="donut-name">{{ item.name }}</span>
                <span class="donut-time">{{ formatShort(item.total_seconds) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </div>
      </el-col>

      <el-col :xs="24" :sm="8">
        <div class="chart-wrapper">
          <div class="chart-title">项目</div>
          <div v-if="topProjects?.length" class="donut-chart">
            <div class="donut-items">
              <div
                v-for="(item, idx) in topProjects.slice(0, 5)"
                :key="item.name"
                class="donut-item"
              >
                <span class="donut-dot" :style="{ background: DONUT_COLORS[idx] }"></span>
                <span class="donut-name" :title="item.name">{{ truncate(item.name, 12) }}</span>
                <span class="donut-time">{{ formatShort(item.total_seconds) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </div>
      </el-col>

      <el-col :xs="24" :sm="8">
        <div class="chart-wrapper">
          <div class="chart-title">编辑器</div>
          <div v-if="topEditors?.length" class="donut-chart">
            <div class="donut-items">
              <div
                v-for="(item, idx) in topEditors.slice(0, 5)"
                :key="item.name"
                class="donut-item"
              >
                <span class="donut-dot" :style="{ background: DONUT_COLORS[idx] }"></span>
                <span class="donut-name">{{ item.name }}</span>
                <span class="donut-time">{{ formatShort(item.total_seconds) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </div>
      </el-col>
    </el-row>

    <!-- 项目详细列表 -->
    <div v-if="topProjects?.length" style="margin-top: 20px">
      <el-divider content-position="left">
        <span class="divider-title">项目详情</span>
      </el-divider>
      <div class="project-list">
        <div
          v-for="item in topProjects.slice(0, 8)"
          :key="item.name"
          class="project-row"
        >
          <span class="project-name" :title="item.name">{{ item.name }}</span>
          <div class="project-bar-wrapper">
            <div
              class="project-bar"
              :style="{ width: (item.percent * 0.7 + 5) + '%' }"
            ></div>
          </div>
          <span class="project-time">{{ formatShort(item.total_seconds) }}</span>
          <span class="project-percent">{{ item.percent.toFixed(1) }}%</span>
        </div>
      </div>
    </div>
  </ele-card>
</template>

<script lang="ts" setup>
  import { ref, computed, watch } from 'vue';
  import { getWakapiStats, formatDigital } from '@/api/wakapi';
  import type { WakapiStatsData, WakapiSummariesEntry } from '@/api/wakapi/model';

  defineOptions({ name: 'WakapiCodingStats' });

  const DONUT_COLORS = [
    '#409eff',
    '#67c23a',
    '#e6a23c',
    '#f56c6c',
    '#909399'
  ];

  const loading = ref(false);
  const range = ref('this_week');
  const statsData = ref<WakapiStatsData | null>(null);

  const topLanguages = computed(() => statsData.value?.languages ?? []);
  const topProjects = computed(() => statsData.value?.projects ?? []);
  const topEditors = computed(() => statsData.value?.editors ?? []);

  const daysWithActivity = computed(() => {
    return topProjects.value.length > 0 ? Math.max(1, Math.min(topProjects.value.length, 7)) : 0;
  });

  const formatTotal = (seconds?: number): string => {
    if (!seconds) return '-';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h > 0) return `${h}h ${m}m`;
    return `${m}m`;
  };

  const formatShort = (seconds?: number): string => {
    if (!seconds) return '-';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h > 0) return `${h}h`;
    return `${m}m`;
  };

  const truncate = (str: string, len: number): string => {
    return str.length > len ? str.slice(0, len) + '...' : str;
  };

  const fetchStats = async () => {
    loading.value = true;
    try {
      const res = await getWakapiStats({ range: range.value });
      statsData.value = res.data ?? null;
    } catch {
      statsData.value = null;
    } finally {
      loading.value = false;
    }
  };

  watch(range, fetchStats, { immediate: true });
</script>

<style lang="scss" scoped>
  .ele-card-title {
    display: flex;
    align-items: center;
    font-size: 15px;
    font-weight: 500;
  }

  .stat-item {
    text-align: center;
    padding: 12px 8px;
    border-right: 1px solid var(--el-border-color-lighter);
    &:last-child { border-right: none; }

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: var(--el-text-color-regular);
      margin-top: 4px;
    }

    .stat-sub {
      font-size: 12px;
      color: var(--el-text-color-placeholder);
      margin-top: 2px;
    }
  }

  .chart-wrapper {
    .chart-title {
      font-size: 13px;
      font-weight: 500;
      color: var(--el-text-color-secondary);
      margin-bottom: 10px;
      padding-left: 4px;
    }

    .donut-chart {
      min-height: 120px;
    }

    .donut-items {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .donut-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;

      .donut-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        flex-shrink: 0;
      }

      .donut-name {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        color: var(--el-text-color-regular);
      }

      .donut-time {
        color: var(--el-text-color-secondary);
        font-size: 12px;
        flex-shrink: 0;
      }
    }
  }

  .divider-title {
    font-size: 13px;
    font-weight: 500;
    color: var(--el-text-color-secondary);
  }

  .project-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .project-row {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;

    .project-name {
      width: 140px;
      flex-shrink: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: var(--el-text-color-regular);
    }

    .project-bar-wrapper {
      flex: 1;
      height: 6px;
      background: var(--el-fill-color-light);
      border-radius: 3px;
      overflow: hidden;
    }

    .project-bar {
      height: 100%;
      background: linear-gradient(90deg, #409eff, #67c23a);
      border-radius: 3px;
      transition: width 0.6s ease;
    }

    .project-time {
      width: 40px;
      text-align: right;
      color: var(--el-text-color-secondary);
      font-size: 12px;
      flex-shrink: 0;
    }

    .project-percent {
      width: 45px;
      text-align: right;
      color: var(--el-color-primary);
      font-weight: 500;
      font-size: 12px;
      flex-shrink: 0;
    }
  }

  @media screen and (max-width: 768px) {
    .stat-item {
      border-right: none;
      border-bottom: 1px solid var(--el-border-color-lighter);
      &:last-child { border-bottom: none; }
    }
  }
</style>
