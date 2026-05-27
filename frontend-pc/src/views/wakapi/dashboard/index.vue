<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">编码仪表盘</span>
          <div class="toolbar-right">
            <el-select v-model="range" size="small" style="width: 130px" @change="fetchAll">
              <el-option value="today" label="今天" />
              <el-option value="last_7_days" label="近 7 天" />
              <el-option value="last_30_days" label="近 30 天" />
              <el-option value="this_week" label="本周" />
              <el-option value="this_month" label="本月" />
              <el-option value="this_year" label="本年" />
            </el-select>
          </div>
        </div>
      </template>

      <!-- 统计概览卡片 -->
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :xs="12" :sm="8" :md="4">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff20; color: #409eff">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview?.dau ?? '-' }}</div>
              <div class="stat-label">DAU</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="4">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a20; color: #67c23a">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview?.wau ?? '-' }}</div>
              <div class="stat-label">WAU</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="4">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c20; color: #e6a23c">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview?.mau ?? '-' }}</div>
              <div class="stat-label">MAU</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="4">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c20; color: #f56c6c">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatRate(overview?.activeRate) }}</div>
              <div class="stat-label">活跃率</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="4">
          <div class="stat-card">
            <div class="stat-icon" style="background: #90939920; color: #909399">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatDuration(overview?.totalSeconds) }}</div>
              <div class="stat-label">总编码时长</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="8" :md="4">
          <div class="stat-card">
            <div class="stat-icon" style="background: #a855f720; color: #a855f7">
              <el-icon><Medal /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ activeUserCount }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 图表行 1: 趋势 + 语言分布 -->
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :xs="24" :lg="14">
          <div class="chart-card">
            <div class="chart-title">近 7 天活跃趋势</div>
            <v-chart
              :option="trendOption"
              :autoresize="true"
              style="height: 280px"
            />
          </div>
        </el-col>
        <el-col :xs="24" :lg="10">
          <div class="chart-card">
            <div class="chart-title">语言分布</div>
            <v-chart
              :option="languageOption"
              :autoresize="true"
              style="height: 280px"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 图表行 2: 用户排行榜 + 团队排行榜 -->
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :xs="24" :lg="12">
          <div class="chart-card">
            <div class="chart-title">TOP 10 活跃用户</div>
            <v-chart
              :option="userLeaderOption"
              :autoresize="true"
              style="height: 320px"
            />
          </div>
        </el-col>
        <el-col :xs="24" :lg="12">
          <div class="chart-card">
            <div class="chart-title">团队编码时长排名</div>
            <v-chart
              :option="teamRankOption"
              :autoresize="true"
              style="height: 320px"
            />
          </div>
        </el-col>
      </el-row>

      <!-- 年度热力图 -->
      <div class="chart-card" style="margin-bottom: 16px">
        <div class="chart-title">编码热力图</div>
        <div class="heatmap-wrapper" ref="heatmapRef">
          <div class="heatmap-months">
            <span v-for="m in heatmapMonths" :key="m.label" :style="{ width: m.width + 'px' }">
              {{ m.label }}
            </span>
          </div>
          <div class="heatmap-grid">
            <div v-for="(week, wi) in heatmapWeeks" :key="wi" class="heatmap-week">
              <div
                v-for="(day, di) in week"
                :key="di"
                class="heatmap-day"
                :style="{ background: getHeatmapColor(day.value) }"
                :title="day.date + ': ' + day.value + ' 人活跃'"
              />
            </div>
          </div>
          <div class="heatmap-legend">
            <span>少</span>
            <div v-for="i in 5" :key="i" class="heatmap-legend-cell" :style="{ background: getHeatmapColor((i - 1) * 0.25) }" />
            <span>多</span>
          </div>
        </div>
      </div>
    </ele-card>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted, shallowRef } from 'vue';
  import VChart from 'vue-echarts';
  import { use } from 'echarts/core';
  import { CanvasRenderer } from 'echarts/renderers';
  import {
    LineChart,
    PieChart,
    BarChart
  } from 'echarts/charts';
  import {
    GridComponent,
    TooltipComponent,
    LegendComponent,
    TitleComponent
  } from 'echarts/components';
  import { User, UserFilled, Clock, TrendCharts, Medal } from '@element-plus/icons-vue';
  import {
    getActivityOverview,
    getActivityTrends,
    getActivityRanking,
    getWakapiSummaries,
    getWakapiLeaders,
    getAllTeamLeaderboard,
    formatDuration
  } from '@/api/wakapi';
  import { getRangeDates } from '@/api/wakapi/stats';
  import type { ActivityOverview, ActivityTrendPoint, ActivityRankItem, WakapiSummariesEntry, WakapiLeaderData, TeamRankItem } from '@/api/wakapi/model';

  use([CanvasRenderer, LineChart, PieChart, BarChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent]);

  defineOptions({ name: 'WakapiDashboard' });

  const loading = ref(false);
  const range = ref('last_7_days');

  const overview = ref<ActivityOverview | null>(null);
  const trends = ref<ActivityTrendPoint[]>([]);
  const ranking = ref<ActivityRankItem[]>([]);
  const summariesData = ref<{ data: any[] } | null>(null);
  const leaders = ref<WakapiLeaderData[]>([]);
  const teamRanks = ref<TeamRankItem[]>([]);

  const activeUserCount = computed(() => ranking.value.filter(r => r.totalSeconds > 0).length);

  const formatRate = (val?: number): string => {
    if (val == null) return '-';
    return (val * 100).toFixed(1) + '%';
  };

  /* ---- 7天趋势图 ---- */
  const trendOption = computed(() => {
    const dates = trends.value.map(t => t.date);
    const users = trends.value.map(t => t.activeUsers);
    const seconds = trends.value.map(t => Math.round(t.totalSeconds / 3600 * 10) / 10);

    return {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' }
      },
      legend: { data: ['活跃用户', '编码时长(h)'], bottom: 0 },
      grid: { left: '3%', right: '4%', top: '8%', bottom: '18%', containLabel: true },
      xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 11 } },
      yAxis: [
        { type: 'value', name: '用户', axisLabel: { fontSize: 11 } },
        { type: 'value', name: '小时', axisLabel: { fontSize: 11 } }
      ],
      series: [
        { name: '活跃用户', type: 'line', smooth: true, data: users, itemStyle: { color: '#409eff' } },
        { name: '编码时长(h)', type: 'line', smooth: true, yAxisIndex: 1, data: seconds, itemStyle: { color: '#67c23a' } }
      ]
    };
  });

  /* ---- 语言分布饼图 ---- */
  const languageOption = computed(() => {
    const items = topLanguages.value.slice(0, 8);
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

  /* ---- 用户排行榜柱状图 ---- */
  const userLeaderOption = computed(() => {
    const items = topUsers.value.slice(0, 10);
    const names = items.map(i => i.username || i.name || i.userId);
    const seconds = items.map(i => Math.round(i.total_seconds / 3600 * 10) / 10);
    return {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: (p: any) => `${p[0].name}<br/>${p[0].value}h` },
      grid: { left: '3%', right: '8%', top: '3%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value', name: '小时', axisLabel: { fontSize: 11 } },
      yAxis: { type: 'category', data: names.reverse(), axisLabel: { fontSize: 11, width: 80, overflow: 'truncate' } },
      series: [{
        type: 'bar',
        data: seconds.reverse(),
        itemStyle: { color: (p: any) => {
          const idx = p.dataIndex;
          if (idx === items.length - 1) return '#c0a04a';
          if (idx === items.length - 2) return '#8a8a8a';
          if (idx === items.length - 3) return '#b87333';
          return '#409eff';
        }},
        barMaxWidth: 24,
        label: { show: true, position: 'right', formatter: '{c}h', fontSize: 11 }
      }]
    };
  });

  /* ---- 团队排名柱状图 ---- */
  const teamRankOption = computed(() => {
    const items = teamRanks.value.slice(0, 10);
    const names = items.map(i => i.teamName);
    const seconds = items.map(i => Math.round(i.totalSeconds / 3600 * 10) / 10);
    return {
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: (p: any) => `${p[0].name}<br/>${p[0].value}h` },
      grid: { left: '3%', right: '8%', top: '3%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value', name: '小时', axisLabel: { fontSize: 11 } },
      yAxis: { type: 'category', data: names.reverse(), axisLabel: { fontSize: 11, width: 80, overflow: 'truncate' } },
      series: [{
        type: 'bar',
        data: seconds.reverse(),
        itemStyle: { color: '#67c23a' },
        barMaxWidth: 24,
        label: { show: true, position: 'right', formatter: '{c}h', fontSize: 11 }
      }]
    };
  });

  /* ---- 热力图 ---- */
  const heatmapWeeks = computed(() => {
    const weeks: { date: string; value: number }[][] = [];
    const end = new Date();
    const start = new Date(end);
    start.setFullYear(start.getFullYear() - 1);
    start.setDate(start.getDate() - start.getDay() + 1);

    const dayMap: Record<string, number> = {};
    trends.value.forEach(t => {
      dayMap[t.date] = t.activeUsers;
    });

    const cur = new Date(start);
    let week: { date: string; value: number }[] = [];
    while (cur <= end) {
      const d = new Date(cur);
      const dateStr = d.toISOString().slice(0, 10);
      week.push({ date: dateStr, value: dayMap[dateStr] ?? 0 });
      if (week.length === 7) {
        weeks.push(week);
        week = [];
      }
      cur.setDate(cur.getDate() + 1);
    }
    if (week.length > 0) weeks.push(week);
    return weeks;
  });

  const heatmapMonths = computed(() => {
    const months: { label: string; width: number }[] = [];
    const weeks = heatmapWeeks.value;
    if (!weeks.length) return months;
    let lastMonth = -1;
    let count = 0;
    weeks.forEach((week, i) => {
      const m = new Date(week[0].date).getMonth();
      if (m !== lastMonth) {
        if (lastMonth !== -1) months.push({ label: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'][lastMonth], width: count * 13 });
        lastMonth = m;
        count = 1;
      } else {
        count++;
      }
    });
    if (lastMonth !== -1) months.push({ label: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'][lastMonth], width: count * 13 });
    return months;
  });

  const getHeatmapColor = (value: number): string => {
    if (value <= 0) return 'var(--el-fill-color-light)';
    const ratio = Math.min(value / 20, 1);
    if (ratio < 0.25) return '#9be9a8';
    if (ratio < 0.5) return '#40c463';
    if (ratio < 0.75) return '#30a14e';
    return '#216e39';
  };

  /* ---- 辅助数据 ---- */
  const topLanguages = computed((): WakapiSummariesEntry[] => {
    if (!summariesData.value?.data?.length) return [];
    const aggregated: Record<string, number> = {};
    summariesData.value.data.forEach((d: any) => {
      (d.languages ?? []).forEach((item: WakapiSummariesEntry) => {
        aggregated[item.name] = (aggregated[item.name] ?? 0) + item.total_seconds;
      });
    });
    return Object.entries(aggregated)
      .map(([name, total_seconds]) => ({ name, total_seconds, percent: 0 }))
      .sort((a, b) => b.total_seconds - a.total_seconds);
  });

  const topUsers = computed(() => leaders.value);

  /* ---- 数据获取 ---- */
  const fetchAll = async () => {
    loading.value = true;
    try {
      const [ov, tr, rk, sm, ld, tr2] = await Promise.allSettled([
        getActivityOverview({ range: range.value }),
        getActivityTrends({ days: 365 }),
        getActivityRanking({ range: range.value, limit: 20 }),
        getWakapiSummaries({ range: range.value }),
        getWakapiLeaders({ range: range.value, limit: 20 }),
        getAllTeamLeaderboard({ range: range.value, limit: 20 })
      ]);
      if (ov.status === 'fulfilled') overview.value = ov.value;
      if (tr.status === 'fulfilled') trends.value = tr.value ?? [];
      if (rk.status === 'fulfilled') ranking.value = rk.value ?? [];
      if (sm.status === 'fulfilled') summariesData.value = sm.value;
      if (ld.status === 'fulfilled') leaders.value = ld.value ?? [];
      if (tr2.status === 'fulfilled') teamRanks.value = tr2.value?.list ?? [];
    } catch (e) {
      overview.value = null;
      trends.value = [];
      ranking.value = [];
      summariesData.value = null;
      leaders.value = [];
      teamRanks.value = [];
    } finally {
      loading.value = false;
    }
  };

  onMounted(() => {
    fetchAll();
  });
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    .title {
      font-size: 15px;
      font-weight: 500;
    }
    .toolbar-right {
      display: flex;
      gap: 8px;
      align-items: center;
    }
  }

  .stat-card {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 14px 16px;
    background: var(--el-fill-color-light);
    border-radius: 8px;
    margin-bottom: 8px;
    .stat-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      .el-icon { font-size: 20px; }
    }
    .stat-info {
      .stat-value {
        font-size: 20px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        line-height: 1.2;
      }
      .stat-label {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
        margin-top: 2px;
      }
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

  .heatmap-wrapper {
    overflow-x: auto;
    padding-bottom: 8px;
    .heatmap-months {
      display: flex;
      margin-left: 30px;
      margin-bottom: 4px;
      span {
        font-size: 10px;
        color: var(--el-text-color-secondary);
      }
    }
    .heatmap-grid {
      display: flex;
      gap: 3px;
      .heatmap-week {
        display: flex;
        flex-direction: column;
        gap: 3px;
        .heatmap-day {
          width: 11px;
          height: 11px;
          border-radius: 2px;
          cursor: pointer;
        }
      }
    }
    .heatmap-legend {
      display: flex;
      align-items: center;
      gap: 3px;
      margin-top: 8px;
      justify-content: flex-end;
      font-size: 10px;
      color: var(--el-text-color-secondary);
      .heatmap-legend-cell {
        width: 11px;
        height: 11px;
        border-radius: 2px;
      }
    }
  }
</style>
