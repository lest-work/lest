<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">全平台编码汇总</span>
          <div class="toolbar-right">
            <el-select v-model="range" size="small" style="width: 130px" @change="fetchData">
              <el-option value="today" label="今天" />
              <el-option value="this_week" label="本周" />
              <el-option value="this_month" label="本月" />
              <el-option value="this_year" label="本年" />
            </el-select>
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
            <el-button size="small" @click="exportData">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计概览 -->
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff20; color: #409eff">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatTotal(totalSeconds) }}</div>
              <div class="stat-label">总计时长</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a20; color: #67c23a">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ activeUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c20; color: #e6a23c">
              <el-icon><FolderOpened /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ totalProjects }}</div>
              <div class="stat-label">活跃项目</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c20; color: #f56c6c">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ dailyAvg }}</div>
              <div class="stat-label">人均日均</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 排行榜 -->
      <el-tabs v-model="activeTab">
        <el-tab-pane label="项目" name="project">
          <div class="leader-list">
            <div
              v-for="(item, idx) in topProjects"
              :key="item.name"
              class="leader-row"
            >
              <span class="leader-rank" :class="rankClass(idx)">{{ idx + 1 }}</span>
              <div class="leader-info">
                <span class="leader-name">{{ item.name }}</span>
                <div class="leader-bar-wrapper">
                  <div class="leader-bar" :style="{ width: item.percent + '%' }"></div>
                </div>
              </div>
              <span class="leader-time">{{ formatTotal(item.total_seconds) }}</span>
              <span class="leader-percent">{{ item.percent.toFixed(1) }}%</span>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="语言" name="language">
          <div class="leader-list">
            <div
              v-for="(item, idx) in topLanguages"
              :key="item.name"
              class="leader-row"
            >
              <span class="leader-rank" :class="rankClass(idx)">{{ idx + 1 }}</span>
              <div class="leader-info">
                <span class="leader-name">{{ item.name }}</span>
                <div class="leader-bar-wrapper">
                  <div class="leader-bar" :style="{ width: item.percent + '%' }"></div>
                </div>
              </div>
              <span class="leader-time">{{ formatTotal(item.total_seconds) }}</span>
              <span class="leader-percent">{{ item.percent.toFixed(1) }}%</span>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="编辑器" name="editor">
          <div class="leader-list">
            <div
              v-for="(item, idx) in topEditors"
              :key="item.name"
              class="leader-row"
            >
              <span class="leader-rank" :class="rankClass(idx)">{{ idx + 1 }}</span>
              <div class="leader-info">
                <span class="leader-name">{{ item.name }}</span>
                <div class="leader-bar-wrapper">
                  <div class="leader-bar" :style="{ width: item.percent + '%' }"></div>
                </div>
              </div>
              <span class="leader-time">{{ formatTotal(item.total_seconds) }}</span>
              <span class="leader-percent">{{ item.percent.toFixed(1) }}%</span>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="用户" name="user">
          <div class="leader-list">
            <div
              v-for="(item, idx) in userStats"
              :key="item.userId"
              class="leader-row"
            >
              <span class="leader-rank" :class="rankClass(idx)">{{ idx + 1 }}</span>
              <el-avatar :size="28" :src="item.avatar">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <div class="leader-info">
                <span class="leader-name">{{ item.username }}</span>
                <div class="leader-bar-wrapper">
                  <div class="leader-bar" :style="{ width: item.percent + '%' }"></div>
                </div>
              </div>
              <span class="leader-time">{{ formatTotal(item.total_seconds) }}</span>
              <span class="leader-percent">{{ item.percent.toFixed(1) }}%</span>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </ele-card>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, computed, onMounted } from 'vue';
  import { EleMessage } from 'ele-admin-plus';
  import { Download, Clock, User, FolderOpened, TrendCharts, UserFilled } from '@element-plus/icons-vue';
  import {
    getWakapiStats,
    getWakapiSummaries,
    getWakapiLeaders,
    listWakapiUsers,
    formatDuration
  } from '@/api/wakapi';
  import type { WakapiSummariesEntry, WakapiLeaderData, WakapiUser } from '@/api/wakapi/model';

  defineOptions({ name: 'WakapiSummary' });

  const loading = ref(false);
  const range = ref('this_week');
  const selectedUserId = ref<string>('');
  const userList = ref<WakapiUser[]>([]);
  const activeTab = ref('project');

  const summariesData = ref<{ data: any[]; cumulative_total: any; daily_average: any } | null>(null);
  const leadersData = ref<WakapiLeaderData[]>([]);

  const totalSeconds = computed(() => {
    return summariesData.value?.cumulative_total?.seconds ?? 0;
  });

  const activeUsers = computed(() => {
    return userList.value.filter(u => u.heartbeatCount && u.heartbeatCount > 0).length;
  });

  const totalProjects = computed(() => {
    if (!summariesData.value?.data?.length) return 0;
    const allProjects = new Set<string>();
    summariesData.value.data.forEach((d: any) => {
      d.projects?.forEach((p: WakapiSummariesEntry) => allProjects.add(p.name));
    });
    return allProjects.size;
  });

  const dailyAvg = computed(() => {
    const avg = summariesData.value?.daily_average?.seconds ?? 0;
    return formatDuration(avg);
  });

  const topProjects = computed(() => getTopItems('project'));
  const topLanguages = computed(() => getTopItems('language'));
  const topEditors = computed(() => getTopItems('editor'));

  const userStats = computed(() => {
    return leadersData.value.map(l => ({
      userId: l.user_id,
      username: l.username || l.name,
      avatar: l.avatar,
      total_seconds: l.total_seconds,
      percent: 0
    })).sort((a, b) => b.total_seconds - a.total_seconds)
      .map((item, idx, arr) => ({
        ...item,
        percent: arr[0]?.total_seconds > 0 ? (item.total_seconds / arr[0].total_seconds) * 100 : 0
      }));
  });

  const getTopItems = (type: string): WakapiSummariesEntry[] => {
    if (!summariesData.value?.data?.length) return [];

    const aggregated: Record<string, number> = {};
    let maxSeconds = 0;

    summariesData.value.data.forEach((d: any) => {
      const items = d[type + 's'] ?? [];
      items.forEach((item: WakapiSummariesEntry) => {
        aggregated[item.name] = (aggregated[item.name] ?? 0) + item.total_seconds;
        maxSeconds = Math.max(maxSeconds, aggregated[item.name]);
      });
    });

    return Object.entries(aggregated)
      .map(([name, total_seconds]) => ({
        name,
        total_seconds,
        percent: maxSeconds > 0 ? (total_seconds / maxSeconds) * 100 : 0
      }))
      .sort((a, b) => b.total_seconds - a.total_seconds)
      .slice(0, 20);
  };

  const rankClass = (idx: number): string => {
    if (idx === 0) return 'rank-gold';
    if (idx === 1) return 'rank-silver';
    if (idx === 2) return 'rank-bronze';
    return '';
  };

  const formatTotal = (seconds?: number): string => {
    if (!seconds) return '-';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h > 0) return `${h}h ${m}m`;
    return `${m}m`;
  };

  const fetchData = async () => {
    loading.value = true;
    try {
      if (selectedUserId.value) {
        const res = await getWakapiSummaries({ range: range.value });
        summariesData.value = res;
      } else {
        const res = await getWakapiSummaries({ range: range.value });
        summariesData.value = res;
      }
      const leaders = await getWakapiLeaders({ range: range.value, limit: 20 });
      leadersData.value = leaders ?? [];
    } catch (e) {
      summariesData.value = null;
      leadersData.value = [];
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

  const exportData = () => {
    EleMessage.info({ message: '导出功能开发中', plain: true });
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
    gap: 12px;
    padding: 16px;
    background: var(--el-fill-color-light);
    border-radius: 8px;

    .stat-icon {
      width: 44px;
      height: 44px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;

      .el-icon {
        font-size: 22px;
      }
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

  .leader-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
    max-height: 500px;
    overflow-y: auto;
  }

  .leader-row {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 12px;
    border-radius: 6px;
    background: var(--el-fill-color-blank);
    transition: background 0.2s;

    &:hover {
      background: var(--el-fill-color-light);
    }

    .leader-rank {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 600;
      background: var(--el-fill-color-light);
      color: var(--el-text-color-regular);
      flex-shrink: 0;

      &.rank-gold {
        background: #fdf6ec;
        color: #e6a23c;
      }
      &.rank-silver {
        background: #f5f5f5;
        color: #909399;
      }
      &.rank-bronze {
        background: #fdf6ec;
        color: #d48265;
      }
    }

    .leader-info {
      flex: 1;
      min-width: 0;

      .leader-name {
        font-size: 13px;
        color: var(--el-text-color-regular);
        display: block;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-bottom: 4px;
      }

      .leader-bar-wrapper {
        height: 4px;
        background: var(--el-fill-color-light);
        border-radius: 2px;
        overflow: hidden;
      }

      .leader-bar {
        height: 100%;
        background: var(--el-color-primary);
        border-radius: 2px;
      }
    }

    .leader-time {
      width: 60px;
      text-align: right;
      font-size: 13px;
      font-weight: 500;
      color: var(--el-text-color-primary);
      flex-shrink: 0;
    }

    .leader-percent {
      width: 48px;
      text-align: right;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      flex-shrink: 0;
    }
  }
</style>
