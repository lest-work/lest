<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">统计详情</span>
          <div class="toolbar-right">
            <el-select
              v-model="selectedUserId"
              placeholder="选择用户"
              size="small"
              clearable
              filterable
              style="width: 160px"
              @change="fetchData"
            >
              <el-option
                v-for="u in userList"
                :key="u.userId"
                :value="u.userId"
                :label="u.username || u.displayName"
              />
            </el-select>
            <el-select v-model="range" size="small" style="width: 130px" @change="fetchData">
              <el-option value="today" label="今天" />
              <el-option value="this_week" label="本周" />
              <el-option value="this_month" label="本月" />
              <el-option value="this_year" label="本年" />
            </el-select>
          </div>
        </div>
      </template>

      <!-- Tab 切换 -->
      <el-tabs v-model="activeTab" @tab-change="fetchData">
        <el-tab-pane label="项目" name="project" />
        <el-tab-pane label="语言" name="language" />
        <el-tab-pane label="编辑器" name="editor" />
        <el-tab-pane label="操作系统" name="os" />
        <el-tab-pane label="机器" name="machine" />
      </el-tabs>

      <!-- 时间汇总卡片 -->
      <el-row :gutter="16" style="margin: 16px 0">
        <el-col :xs="12" :sm="8">
          <div class="stat-card">
            <div class="stat-label">总计时长</div>
            <div class="stat-value">{{ formatTotal(statsData?.total_seconds) }}</div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8">
          <div class="stat-card">
            <div class="stat-label">日均时长</div>
            <div class="stat-value">{{ formatTotal(statsData?.daily_average) }}</div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8">
          <div class="stat-card">
            <div class="stat-label">最长单日</div>
            <div class="stat-value">{{ longestDay }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 每日趋势 -->
      <div class="chart-card" style="margin-bottom: 16px">
        <div class="chart-title">每日编码时长趋势</div>
        <v-chart :option="trendOption" :autoresize="true" style="height: 260px" />
      </div>

      <!-- 分布图 -->
      <el-row :gutter="16">
        <el-col :xs="24" :lg="12">
          <div class="chart-card">
            <div class="chart-title">{{ tabLabel }}分布柱状图</div>
            <v-chart :option="barOption" :autoresize="true" style="height: 320px" />
          </div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="chart-card">
            <div class="chart-title">{{ tabLabel }}分布饼图</div>
            <v-chart :option="pieOption" :autoresize="true" style="height: 320px" />
          </div>
        </el-col>
      </el-row>

      <!-- 明细表格 -->
      <el-table :data="tableData" size="small" style="margin-top: 16px" stripe>
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="digital" label="时长" width="100" align="center" />
        <el-table-column prop="percent" label="占比" width="120" align="center">
          <template #default="{ row }">
            <el-progress :percentage="Number(row.percent.toFixed(1))" :stroke-width="6" />
          </template>
        </el-table-column>
        <el-table-column prop="text" label="可读时长" width="100" align="center" />
      </el-table>
    </ele-card>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue';
  import VChart from 'vue-echarts';
  import { use } from 'echarts/core';
  import { CanvasRenderer } from 'echarts/renderers';
  import { LineChart, BarChart, PieChart } from 'echarts/charts';
  import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components';
  import {
    getWakapiStats,
    getWakapiSummaries,
    listWakapiUsers,
    type WakapiSummariesData,
    type WakapiSummariesEntry
  } from '@/api/wakapi';

  use([CanvasRenderer, LineChart, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent]);

  defineOptions({ name: 'WakapiStats' });

  const loading = ref(false);
  const range = ref('this_month');
  const selectedUserId = ref<string>('');
  const userList = ref<any[]>([]);
  const activeTab = ref('project');
  const statsData = ref<any>(null);
  const summariesData = ref<WakapiSummariesData[]>([]);

  const tabLabelMap: Record<string, string> = {
    project: '项目',
    language: '语言',
    editor: '编辑器',
    os: '操作系统',
    machine: '机器'
  };

  const tabLabel = computed(() => tabLabelMap[activeTab.value] ?? activeTab.value);

  const tabFieldMap: Record<string, string> = {
    project: 'projects',
    language: 'languages',
    editor: 'editors',
    os: 'operating_systems',
    machine: 'machines'
  };

  const tableData = computed((): WakapiSummariesEntry[] => {
    if (!statsData.value) return [];
    const key = tabFieldMap[activeTab.value];
    return (statsData.value[key] ?? []) as WakapiSummariesEntry[];
  });

  const longestDay = computed(() => {
    if (!summariesData.value.length) return '-';
    const max = summariesData.value.reduce((best, d) => {
      const t = d.grand_total?.total_seconds ?? 0;
      return t > best ? t : best;
    }, 0);
    return formatTotal(max);
  });

  const formatTotal = (seconds?: number): string => {
    if (!seconds) return '-';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h > 0) return `${h}h ${m}m`;
    return `${m}m`;
  };

  /* 趋势图 */
  const trendOption = computed(() => {
    const data = summariesData.value;
    return {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', top: '8%', bottom: '15%', containLabel: true },
      xAxis: {
        type: 'category',
        data: data.map(d => d.range.date),
        axisLabel: { fontSize: 11, rotate: 30 }
      },
      yAxis: { type: 'value', name: '小时', axisLabel: { fontSize: 11 } },
      series: [{
        name: '编码时长',
        type: 'bar',
        data: data.map(d => Math.round((d.grand_total?.total_seconds ?? 0) / 3600 * 10) / 10),
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
      }]
    };
  });

  /* 柱状图 */
  const barOption = computed(() => {
    const items = tableData.value.slice(0, 15);
    return {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '4%', top: '3%', bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: items.map(i => i.name), axisLabel: { fontSize: 10, rotate: 30 } },
      yAxis: { type: 'value', name: '小时', axisLabel: { fontSize: 11 } },
      series: [{
        type: 'bar',
        data: items.map(i => Math.round(i.total_seconds / 3600 * 10) / 10),
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
      }]
    };
  });

  /* 饼图 */
  const pieOption = computed(() => {
    const items = tableData.value.slice(0, 8);
    const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#a855f7', '#00bcd4', '#ff9800'];
    return {
      tooltip: { trigger: 'item', formatter: '{b}: {c}h ({d}%)' },
      legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { fontSize: 11 }, itemWidth: 10, itemHeight: 10 },
      series: [{
        type: 'pie',
        radius: ['35%', '65%'],
        center: ['35%', '50%'],
        label: { show: false },
        data: items.map((item, i) => ({
          name: item.name,
          value: Math.round(item.total_seconds / 3600 * 10) / 10,
          itemStyle: { color: colors[i % colors.length] }
        }))
      }]
    };
  });

  const fetchData = async () => {
    loading.value = true;
    try {
      const params: Record<string, string> = { range: range.value };
      if (selectedUserId.value) {
        params['userId'] = selectedUserId.value;
      }
      const [stats, summaries] = await Promise.all([
        getWakapiStats(params).catch(() => null),
        getWakapiSummaries(params).catch(() => null)
      ]);
      statsData.value = stats;
      summariesData.value = summaries?.data ?? [];
    } catch {
      statsData.value = null;
      summariesData.value = [];
    } finally {
      loading.value = false;
    }
  };

  const loadUsers = async () => {
    try {
      const users = await listWakapiUsers();
      userList.value = users ?? [];
    } catch {
      userList.value = [];
    }
  };

  onMounted(() => {
    loadUsers();
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

  .stat-card {
    padding: 16px;
    background: var(--el-fill-color-light);
    border-radius: 8px;
    text-align: center;
    margin-bottom: 8px;
    .stat-label {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 6px;
    }
    .stat-value {
      font-size: 22px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .chart-card {
    background: var(--el-fill-color-blank);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    padding: 16px;
    .chart-title {
      font-size: 14px;
      font-weight: 500;
      color: var(--el-text-color-primary);
      margin-bottom: 12px;
    }
  }
</style>
