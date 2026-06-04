<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { meetingApi, type Meeting } from '@/api/meeting';

const loading = ref(false);
const meetings = ref<Meeting[]>([]);
const total = ref(0);
const filter = ref<'all' | 'today' | 'upcoming'>('all');

const visibleList = computed(() => {
  if (filter.value === 'today') {
    const today = new Date().toISOString().split('T')[0];
    return meetings.value.filter(m => m.startTime?.startsWith(today));
  }
  if (filter.value === 'upcoming') {
    const now = new Date().toISOString();
    return meetings.value.filter(m => m.startTime && m.startTime > now);
  }
  return meetings.value;
});

function getTypeIcon(type?: string) {
  const map: Record<string, string> = { standup: '站会', planning: '计划会', review: '评审会', retrospective: '回顾会', other: '会议' };
  return map[type || 'other'] || '会议';
}

function getTypeColor(type?: string) {
  const map: Record<string, string> = { standup: 'var(--status-in-progress)', planning: 'var(--priority-p1)', review: 'var(--color-primary)', retrospective: 'var(--type-epic)' };
  return map[type || 'other'] || 'var(--text-muted)';
}

function relativeTime(iso?: string) {
  if (!iso) return '';
  const diff = Date.now() - new Date(iso).getTime();
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes} 分钟前`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} 小时前`;
  const days = Math.floor(hours / 24);
  return `${days} 天前`;
}

async function loadMeetings() {
  loading.value = true;
  try {
    const res = await meetingApi.list();
    if (res.code === 200) {
      meetings.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch { /* ignore */ }
  finally { loading.value = false; }
}

onMounted(() => { loadMeetings(); });
</script>

<template>
  <div class="meeting-view">
    <div class="page-header">
      <h1 class="page-title">会议</h1>
      <span class="total-count">{{ total }} 场会议</span>
    </div>

    <!-- Filter -->
    <div class="filter-bar">
      <button class="filter-chip" :class="{ active: filter === 'all' }" @click="filter = 'all'">全部</button>
      <button class="filter-chip" :class="{ active: filter === 'today' }" @click="filter = 'today'">今日</button>
      <button class="filter-chip" :class="{ active: filter === 'upcoming' }" @click="filter = 'upcoming'">即将开始</button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-placeholder">加载中...</div>

    <!-- Empty -->
    <div v-else-if="visibleList.length === 0" class="empty-state">
      <div class="empty-icon">📅</div>
      <div class="empty-title">暂无会议</div>
      <div class="empty-desc">暂无会议安排</div>
    </div>

    <!-- List -->
    <div v-else class="meeting-list">
      <div v-for="m in visibleList" :key="m.meetingId" class="meeting-card">
        <div class="meeting-time">
          <div class="meeting-date">{{ m.startTime ? new Date(m.startTime).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }) : '-' }}</div>
          <div class="meeting-hour">{{ m.startTime ? new Date(m.startTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : '' }}</div>
        </div>
        <div class="meeting-divider" />
        <div class="meeting-info">
          <div class="meeting-title">{{ m.title }}</div>
          <div class="meeting-meta">
            <span class="type-badge" :style="{ background: getTypeColor(m.meetingType) + '20', color: getTypeColor(m.meetingType) }">
              {{ getTypeIcon(m.meetingType) }}
            </span>
            <span v-if="m.endTime" class="duration">{{ m.startTime && m.endTime ? Math.round((new Date(m.endTime).getTime() - new Date(m.startTime).getTime()) / 60000) + ' 分钟' : '' }}</span>
          </div>
          <div v-if="m.description" class="meeting-desc">{{ m.description }}</div>
        </div>
        <div class="meeting-status">
          <span class="status-badge" :class="m.status">{{ m.status === 'scheduled' ? '已安排' : m.status === 'ongoing' ? '进行中' : '已结束' }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.meeting-view { padding: var(--space-6) var(--space-5); }
.page-header { display: flex; align-items: center; gap: var(--space-3); margin-bottom: var(--space-5); }
.page-title { font-size: var(--text-3xl); font-weight: var(--font-semibold); color: var(--text-primary); margin: 0; }
.total-count { font-size: var(--text-sm); color: var(--text-muted); }
.filter-bar { display: flex; gap: var(--space-2); margin-bottom: var(--space-5); }
.filter-chip { padding: var(--space-2) var(--space-4); border: 1px solid var(--border-color); border-radius: var(--radius-full); background: transparent; color: var(--text-secondary); cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); transition: all var(--transition-fast); &.active { background: var(--color-primary); color: #fff; border-color: var(--color-primary); } &:hover:not(.active) { border-color: var(--color-primary); color: var(--color-primary); } }
.meeting-list { display: flex; flex-direction: column; gap: var(--space-3); }
.meeting-card { display: flex; align-items: center; gap: var(--space-4); background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: var(--space-4); transition: border-color var(--transition-fast); &:hover { border-color: var(--color-primary); } }
.meeting-time { text-align: center; min-width: 52px; }
.meeting-date { font-size: var(--text-xs); color: var(--text-muted); }
.meeting-hour { font-size: var(--text-sm); font-weight: var(--font-semibold); color: var(--text-primary); }
.meeting-divider { width: 1px; height: 40px; background: var(--border-color); flex-shrink: 0; }
.meeting-info { flex: 1; min-width: 0; }
.meeting-title { font-weight: var(--font-semibold); color: var(--text-primary); margin-bottom: var(--space-1); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.meeting-meta { display: flex; align-items: center; gap: var(--space-2); margin-bottom: var(--space-1); }
.type-badge { font-size: 11px; padding: 1px 6px; border-radius: var(--radius-sm); font-weight: var(--font-medium); }
.duration { font-size: var(--text-xs); color: var(--text-muted); }
.meeting-desc { font-size: var(--text-xs); color: var(--text-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.meeting-status { flex-shrink: 0; }
.status-badge { font-size: 11px; padding: 2px 8px; border-radius: var(--radius-full); font-weight: var(--font-medium); background: var(--bg-hover); color: var(--text-muted); &.scheduled { background: rgba(59,130,246,0.1); color: #3b82f6; } &.ongoing { background: rgba(34,197,94,0.1); color: #22c55e; } }
.loading-placeholder, .empty-state { text-align: center; padding: var(--space-8); }
.empty-icon { font-size: 48px; margin-bottom: var(--space-3); }
.empty-title { font-weight: var(--font-semibold); color: var(--text-primary); margin-bottom: var(--space-1); }
.empty-desc { font-size: var(--text-sm); color: var(--text-muted); }
</style>
