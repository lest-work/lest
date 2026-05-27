<template>
  <ele-card :loading="loading">
    <template #header>
      <div class="header">
        <span class="title">编码活动热力图</span>
        <span class="subtitle">{{ year }} 年度</span>
      </div>
    </template>

    <!-- 月份标签 -->
    <div class="months">
      <span
        v-for="(month, idx) in months"
        :key="idx"
        class="month-label"
        :style="{ left: getMonthLeft(idx) + 'px' }"
      >{{ month }}</span>
    </div>

    <!-- 热力图网格 -->
    <div class="heatmap-wrapper" ref="wrapperRef">
      <div class="week-labels">
        <span>Mon</span>
        <span>Wed</span>
        <span>Fri</span>
      </div>
      <div class="grid" :style="{ width: gridWidth + 'px' }">
        <div
          v-for="(day, idx) in gridDays"
          :key="idx"
          class="day-cell"
          :style="{
            background: getColor(day.seconds),
            gridColumn: day.col + 1,
            gridRow: day.row
          }"
          :title="getTooltip(day)"
        ></div>
      </div>
    </div>

    <!-- 图例 -->
    <div class="legend">
      <span class="legend-label">少</span>
      <div
        v-for="level in 5"
        :key="level"
        class="legend-cell"
        :style="{ background: getColorForLevel(level) }"
      ></div>
      <span class="legend-label">多</span>
    </div>

    <!-- 年度统计 -->
    <div v-if="yearStats" class="year-summary">
      <div class="year-stat">
        <span class="year-stat-value">{{ yearStats.total }}</span>
        <span class="year-stat-label">总计</span>
      </div>
      <div class="year-stat">
        <span class="year-stat-value">{{ yearStats.days }}</span>
        <span class="year-stat-label">编码天数</span>
      </div>
      <div class="year-stat">
        <span class="year-stat-value">{{ yearStats.dailyAvg }}</span>
        <span class="year-stat-label">日均</span>
      </div>
      <div class="year-stat">
        <span class="year-stat-value">{{ yearStats.longestStreak }}</span>
        <span class="year-stat-label">最长连续</span>
      </div>
    </div>
  </ele-card>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted, nextTick } from 'vue';
  import { getWakapiSummaries } from '@/api/wakapi';

  defineOptions({ name: 'WakapiActivityHeatmap' });

  const CELL_SIZE = 12;
  const CELL_GAP = 3;
  const WEEKS_TO_SHOW = 53;

  const loading = ref(false);
  const wrapperRef = ref<HTMLElement>();
  const gridWidth = ref(0);
  const year = new Date().getFullYear();
  const dayData = ref<Map<string, number>>(new Map());

  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

  const gridDays = computed(() => {
    const days: Array<{ date: string; seconds: number; col: number; row: number }> = [];
    const startOfYear = new Date(year, 0, 1);
    // 调整到该年的第一个周一
    let firstMondayOffset = startOfYear.getDay() === 0 ? 6 : startOfYear.getDay() - 1;
    const startDate = new Date(startOfYear);
    startDate.setDate(startDate.getDate() - firstMondayOffset);

    let week = 0;
    let current = new Date(startDate);

    while (week < WEEKS_TO_SHOW) {
      for (let dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
        const dateStr = formatDate(current);
        const isInYear = current.getFullYear() === year;
        const seconds = isInYear ? (dayData.value.get(dateStr) ?? 0) : 0;

        days.push({
          date: dateStr,
          seconds,
          col: week + 1,
          row: dayOfWeek + 1
        });

        current.setDate(current.getDate() + 1);
      }
      week++;
    }

    return days;
  });

  const yearStats = computed(() => {
    const values = Array.from(dayData.value.values());
    const totalSeconds = values.reduce((a, b) => a + b, 0);
    const activeDays = values.filter(v => v > 0).length;
    const avgSeconds = activeDays > 0 ? Math.round(totalSeconds / activeDays) : 0;

    // 计算最长连续编码天数
    let longestStreak = 0;
    let currentStreak = 0;
    const sortedDates = Array.from(dayData.value.keys()).sort();
    for (let i = 0; i < sortedDates.length; i++) {
      if (dayData.value.get(sortedDates[i])! > 0) {
        currentStreak++;
        longestStreak = Math.max(longestStreak, currentStreak);
      } else {
        currentStreak = 0;
      }
    }

    const formatH = (s: number) => `${Math.floor(s / 3600)}h ${Math.floor((s % 3600) / 60)}m`;
    const formatAvg = (s: number) => `${Math.floor(s / 3600)}h`;

    return {
      total: formatH(totalSeconds),
      days: activeDays,
      dailyAvg: formatAvg(avgSeconds),
      longestStreak: longestStreak + '天'
    };
  });

  const getColor = (seconds: number): string => {
    if (seconds === 0) return 'var(--el-fill-color-light)';
    const maxSeconds = Math.max(...Array.from(dayData.value.values()), 1);
    const ratio = Math.min(seconds / maxSeconds, 1);

    if (ratio < 0.25) return '#9be9a8';
    if (ratio < 0.5) return '#40c463';
    if (ratio < 0.75) return '#30a14e';
    return '#216e39';
  };

  const getColorForLevel = (level: number): string => {
    const colors = [
      'var(--el-fill-color-light)',
      '#9be9a8',
      '#40c463',
      '#30a14e',
      '#216e39'
    ];
    return colors[level - 1];
  };

  const getTooltip = (day: { date: string; seconds: number }): string => {
    if (day.seconds === 0) return `${day.date}: 无活动`;
    const h = Math.floor(day.seconds / 3600);
    const m = Math.floor((day.seconds % 3600) / 60);
    return `${day.date}: ${h > 0 ? h + 'h ' : ''}${m}m`;
  };

  const formatDate = (d: Date): string => {
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  };

  const getMonthLeft = (monthIdx: number): number => {
    const startOfYear = new Date(year, 0, 1);
    const firstDayOfMonth = new Date(year, monthIdx, 1);
    const diffDays = Math.floor((firstDayOfMonth.getTime() - startOfYear.getTime()) / (1000 * 60 * 60 * 24));
    const firstMondayOffset = startOfYear.getDay() === 0 ? 6 : startOfYear.getDay() - 1;
    const weekOfMonth = Math.floor((diffDays + firstMondayOffset) / 7);
    return weekOfMonth * (CELL_SIZE + CELL_GAP) + 30;
  };

  const fetchYearData = async () => {
    loading.value = true;
    try {
      const res = await getWakapiSummaries({
        start: `${year}-01-01`,
        end: `${year}-12-31`
      });

      const newMap = new Map<string, number>();
      if (res.data) {
        for (const day of res.data) {
          const dateStr = day.range?.date;
          const totalSeconds = day.grand_total?.total_seconds ?? 0;
          if (dateStr) {
            newMap.set(dateStr, totalSeconds);
          }
        }
      }
      dayData.value = newMap;
    } catch {
      dayData.value = new Map();
    } finally {
      loading.value = false;
    }
  };

  const updateWidth = () => {
    nextTick(() => {
      if (wrapperRef.value) {
        gridWidth.value = wrapperRef.value.clientWidth - 40;
      }
    });
  };

  onMounted(() => {
    fetchYearData();
    updateWidth();
    window.addEventListener('resize', updateWidth);
  });
</script>

<style lang="scss" scoped>
  .header {
    display: flex;
    align-items: center;
    gap: 12px;

    .title {
      font-size: 15px;
      font-weight: 500;
    }

    .subtitle {
      font-size: 12px;
      color: var(--el-text-color-placeholder);
    }
  }

  .months {
    position: relative;
    height: 20px;
    margin-left: 30px;
    margin-bottom: 4px;

    .month-label {
      position: absolute;
      font-size: 11px;
      color: var(--el-text-color-placeholder);
    }
  }

  .heatmap-wrapper {
    display: flex;
    overflow-x: auto;
    padding: 4px 0 8px;

    .week-labels {
      display: flex;
      flex-direction: column;
      justify-content: space-around;
      width: 28px;
      flex-shrink: 0;
      font-size: 10px;
      color: var(--el-text-color-placeholder);
      padding-top: 2px;
    }

    .grid {
      display: grid;
      grid-template-rows: repeat(7, v-bind('CELL_SIZE + "px"'));
      grid-auto-flow: column;
      gap: v-bind('CELL_GAP + "px"');
      grid-auto-columns: v-bind('CELL_SIZE + "px"');
    }

    .day-cell {
      border-radius: 2px;
      cursor: pointer;
      transition: opacity 0.2s, transform 0.1s;

      &:hover {
        opacity: 0.8;
        transform: scale(1.2);
      }
    }
  }

  .legend {
    display: flex;
    align-items: center;
    gap: 4px;
    justify-content: flex-end;
    margin-top: 8px;

    .legend-label {
      font-size: 11px;
      color: var(--el-text-color-placeholder);
      margin: 0 4px;
    }

    .legend-cell {
      width: 12px;
      height: 12px;
      border-radius: 2px;
    }
  }

  .year-summary {
    display: flex;
    gap: 24px;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid var(--el-border-color-lighter);
    justify-content: center;

    .year-stat {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;

      .year-stat-value {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .year-stat-label {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }
    }
  }
</style>
