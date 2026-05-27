<template>
  <ele-card :loading="loading">
    <template #header>
      <div class="header">
        <span class="title">今日心跳</span>
        <el-radio-group v-model="date" size="small" style="margin-left: auto">
          <el-radio-button value="today">今天</el-radio-button>
          <el-radio-button value="yesterday">昨天</el-radio-button>
          <el-radio-button value="7days">近7天</el-radio-button>
        </el-radio-group>
      </div>
    </template>

    <div v-if="mode === 'single'">
      <!-- 单日模式：时间线列表 -->
      <div v-if="heartbeats.length" class="timeline">
        <div
          v-for="(hb, idx) in heartbeats"
          :key="hb.id"
          class="timeline-item"
        >
          <div class="timeline-dot" :style="{ background: categoryColor(hb.category) }"></div>
          <div class="timeline-content">
            <div class="hb-header">
              <span class="hb-time">{{ formatTime(hb.time) }}</span>
              <el-tag :type="categoryType(hb.category)" size="small">
                {{ categoryLabel(hb.category) }}
              </el-tag>
              <span v-if="hb.project" class="hb-project">
                <el-icon><FolderOutlined /></el-icon>
                {{ hb.project }}
              </span>
            </div>
            <div class="hb-entity">
              <el-icon><FileOutlined /></el-icon>
              {{ hb.entity }}
            </div>
            <div v-if="hb.language" class="hb-meta">
              <span v-if="hb.editor">
                <el-icon><CodeOutlined /></el-icon>
                {{ hb.editor }}
              </span>
              <span v-if="hb.language">
                <el-icon><GlobalOutlined /></el-icon>
                {{ hb.language }}
              </span>
              <span v-if="hb.machine">
                <el-icon><DesktopOutlined /></el-icon>
                {{ hb.machine }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="今日暂无心跳记录" />
    </div>

    <div v-else>
      <!-- 多日模式：汇总卡片 -->
      <div class="multi-day">
        <div
          v-for="day in dailySummaries"
          :key="day.date"
          class="day-card"
        >
          <div class="day-header">
            <span class="day-date">{{ formatDayDate(day.date) }}</span>
            <span class="day-total">{{ day.total }}</span>
          </div>
          <div class="day-bars">
            <div
              v-for="item in day.topProjects.slice(0, 3)"
              :key="item.name"
              class="day-bar-row"
            >
              <span class="day-bar-name">{{ item.name }}</span>
              <div class="day-bar-wrapper">
                <div
                  class="day-bar"
                  :style="{ width: item.percent + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </ele-card>
</template>

<script lang="ts" setup>
  import { ref, computed, watch } from 'vue';
  import {
    FolderOutlined,
    FileOutlined,
    CodeOutlined,
    GlobalOutlined,
    DesktopOutlined
  } from '@/components/icons';
  import {
    getWakapiHeartbeats,
    getWakapiSummaries,
    formatDuration
  } from '@/api/wakapi';
  import type {
    WakapiHeartbeatEntry,
    WakapiSummariesData
  } from '@/api/wakapi/model';

  defineOptions({ name: 'WakapiHeartbeatStream' });

  const loading = ref(false);
  const date = ref('today');
  const heartbeats = ref<WakapiHeartbeatEntry[]>([]);
  const summaries = ref<WakapiSummariesData[]>([]);

  const mode = computed(() => date.value === '7days' ? 'multi' : 'single');

  const dailySummaries = computed(() => {
    return summaries.value.slice(0, 7).map(s => {
      const totalSeconds = s.grand_total?.total_seconds ?? 0;
      const projects = s.projects ?? [];
      const maxSeconds = projects[0]?.total_seconds ?? 1;

      return {
        date: s.range?.date ?? '',
        total: formatDuration(totalSeconds),
        topProjects: projects.slice(0, 3).map(p => ({
          name: p.name,
          percent: Math.round((p.total_seconds / maxSeconds) * 100)
        }))
      };
    });
  });

  const categoryColor = (cat?: string): string => {
    const map: Record<string, string> = {
      coding: '#67c23a',
      browsing: '#409eff',
      building: '#e6a23c',
      debugging: '#f56c6c',
      designing: '#909399',
      learning: '#9c27b0',
      writing: '#ff9800'
    };
    return map[cat ?? ''] ?? '#909399';
  };

  const categoryType = (cat?: string): string => {
    const map: Record<string, string> = {
      coding: 'success',
      browsing: '',
      building: 'warning',
      debugging: 'danger'
    };
    return map[cat ?? ''] ?? 'info';
  };

  const categoryLabel = (cat?: string): string => {
    const map: Record<string, string> = {
      coding: '编码',
      browsing: '浏览',
      building: '构建',
      debugging: '调试',
      designing: '设计',
      learning: '学习',
      writing: '写作'
    };
    return map[cat ?? ''] ?? cat ?? '其他';
  };

  const formatTime = (time: number): string => {
    const d = new Date(time * 1000);
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;
  };

  const formatDayDate = (dateStr: string): string => {
    if (!dateStr) return '';
    const d = new Date(dateStr + 'T00:00:00');
    const today = new Date();
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);

    if (d.toDateString() === today.toDateString()) return '今天';
    if (d.toDateString() === yesterday.toDateString()) return '昨天';
    return `${d.getMonth() + 1}/${d.getDate()} ${['日','一','二','三','四','五','六'][d.getDay()]}`;
  };

  const fetchSingleDay = async (d: string) => {
    loading.value = true;
    try {
      const res = await getWakapiHeartbeats({ date: d });
      heartbeats.value = res.data ?? [];
    } catch {
      heartbeats.value = [];
    } finally {
      loading.value = false;
    }
  };

  const fetchMultiDay = async () => {
    loading.value = true;
    try {
      const res = await getWakapiSummaries({ range: 'last_7_days' });
      summaries.value = res.data ?? [];
    } catch {
      summaries.value = [];
    } finally {
      loading.value = false;
    }
  };

  const fetchData = async () => {
    if (mode.value === 'single') {
      const d = new Date();
      if (date.value === 'yesterday') {
        d.setDate(d.getDate() - 1);
      }
      const dateStr = d.toISOString().slice(0, 10);
      await fetchSingleDay(dateStr);
    } else {
      await fetchMultiDay();
    }
  };

  watch(date, fetchData, { immediate: true });
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
  }

  /* 时间线模式 */
  .timeline {
    display: flex;
    flex-direction: column;
    gap: 0;

    .timeline-item {
      display: flex;
      gap: 12px;
      padding: 10px 0;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 4px;
        top: 28px;
        bottom: -10px;
        width: 1px;
        background: var(--el-border-color-lighter);
      }

      &:last-child::before {
        display: none;
      }
    }

    .timeline-dot {
      width: 9px;
      height: 9px;
      border-radius: 50%;
      flex-shrink: 0;
      margin-top: 6px;
      position: relative;
      z-index: 1;
    }

    .timeline-content {
      flex: 1;
      min-width: 0;

      .hb-header {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;

        .hb-time {
          font-size: 12px;
          color: var(--el-text-color-placeholder);
          font-family: monospace;
        }

        .hb-project {
          display: flex;
          align-items: center;
          gap: 3px;
          font-size: 12px;
          color: var(--el-text-color-regular);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 200px;

          .el-icon {
            flex-shrink: 0;
          }
        }
      }

      .hb-entity {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: var(--el-text-color-primary);
        margin-top: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        .el-icon {
          flex-shrink: 0;
          color: var(--el-text-color-placeholder);
        }
      }

      .hb-meta {
        display: flex;
        gap: 12px;
        margin-top: 3px;
        flex-wrap: wrap;

        span {
          display: flex;
          align-items: center;
          gap: 3px;
          font-size: 12px;
          color: var(--el-text-color-placeholder);

          .el-icon {
            font-size: 12px;
          }
        }
      }
    }
  }

  /* 多日汇总模式 */
  .multi-day {
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 400px;
    overflow-y: auto;
  }

  .day-card {
    padding: 8px 12px;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 6px;
    background: var(--el-fill-color-blank);

    .day-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .day-date {
        font-size: 13px;
        font-weight: 500;
        color: var(--el-text-color-regular);
      }

      .day-total {
        font-size: 13px;
        font-weight: 600;
        color: var(--el-color-primary);
      }
    }

    .day-bars {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .day-bar-row {
        display: flex;
        align-items: center;
        gap: 8px;

        .day-bar-name {
          width: 100px;
          font-size: 12px;
          color: var(--el-text-color-secondary);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          flex-shrink: 0;
        }

        .day-bar-wrapper {
          flex: 1;
          height: 4px;
          background: var(--el-fill-color-light);
          border-radius: 2px;
          overflow: hidden;
        }

        .day-bar {
          height: 100%;
          background: var(--el-color-primary);
          border-radius: 2px;
        }
      }
    }
  }
</style>
