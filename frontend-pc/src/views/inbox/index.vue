<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import EmptyState from '@/components/common/EmptyState.vue';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';
import { notificationApi, type Notification } from '@/api/notification';

const loading = ref(false);
const notifications = ref<Notification[]>([]);
const total = ref(0);
const activeTab = ref<'all' | 'unread'>('all');

const unreadCount = computed(() => notifications.value.filter(n => !n.isRead && !n.read).length);

const visibleList = computed(() =>
  activeTab.value === 'unread'
    ? notifications.value.filter(n => !n.isRead && !n.read)
    : notifications.value
);

async function loadNotifications() {
  loading.value = true;
  try {
    const res = await notificationApi.list({ pageNum: 1, pageSize: 50 });
    if (res.code === 200) {
      notifications.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch {
    // fallback: keep empty
  } finally {
    loading.value = false;
  }
}

async function markAsRead(item: Notification) {
  const id = item.notificationId || item.id;
  if (!id || item.isRead || item.read) return;
  try {
    await notificationApi.markAsRead(id);
    item.isRead = true;
  } catch {
    // silently fail
  }
}

async function markAllRead() {
  try {
    await notificationApi.markAllAsRead();
    notifications.value.forEach(n => { n.isRead = true; });
  } catch {
    // silently fail
  }
}

function filterTab(tab: 'all' | 'unread') { activeTab.value = tab; }

function getTypeIcon(type: string) {
  const map: Record<string, string> = {
    task_assigned: '👤',
    task_commented: '💬',
    task_status_changed: '✅',
    task_created: '🆕',
    mention: '@',
    task_due_soon: '⏰',
  };
  return map[type] || '🔔';
}

const TYPE_COLORS: Record<string, string> = {
  task_assigned: 'var(--status-in-progress)',
  task_commented: 'var(--type-epic)',
  task_status_changed: 'var(--status-completed)',
  task_created: 'var(--color-primary)',
  mention: 'var(--priority-p1)',
  task_due_soon: 'var(--color-danger)',
};
function getTypeColor(type: string) { return TYPE_COLORS[type] || 'var(--text-muted)'; }

function relativeTime(isoString: string) {
  if (!isoString) return '';
  const diff = Date.now() - new Date(isoString).getTime();
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes} 分钟前`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `${hours} 小时前`;
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days} 天前`;
  return `${Math.floor(days / 7)} 周前`;
}

onMounted(() => { loadNotifications(); });
</script>

<template>
  <div class="inbox-view">
    <!-- 头部 -->
    <div class="inbox-header">
      <div class="header-left">
        <h1 class="page-title">收件箱</h1>
        <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }} 未读</span>
      </div>
      <button
        v-if="unreadCount > 0"
        class="mark-all-btn"
        @click="markAllRead"
      >
        全部标为已读
      </button>
    </div>

    <!-- Tab 过滤 -->
    <div class="inbox-tabs">
      <button class="tab-btn" :class="{ active: activeTab === 'all' }" @click="filterTab('all')">
        全部
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'unread' }" @click="filterTab('unread')">
        未读
        <span v-if="unreadCount > 0" class="tab-badge">{{ unreadCount }}</span>
      </button>
    </div>

    <!-- Loading -->
    <LoadingSpinner v-if="loading" />

    <!-- 通知列表 -->
    <div v-else-if="visibleList.length" class="inbox-list">
      <div
        v-for="item in visibleList"
        :key="item.notificationId || item.id"
        class="notif-item"
        :class="{ unread: !item.isRead && !item.read }"
        @click="markAsRead(item)"
      >
        <div class="notif-avatar" :style="{ background: getTypeColor(item.type) + '20', color: getTypeColor(item.type) }">
          {{ getTypeIcon(item.type) }}
        </div>
        <div class="notif-body">
          <div class="notif-top">
            <span class="notif-title">{{ item.title }}</span>
            <span class="notif-time">{{ relativeTime(item.createTime || item.createdAt || '') }}</span>
          </div>
          <div class="notif-content">{{ item.content }}</div>
          <div v-if="item.taskNo" class="notif-ref">
            <span class="ref-project">{{ item.projectName }}</span>
            <span class="ref-sep">·</span>
            <span class="ref-taskno">{{ item.taskNo }}</span>
          </div>
        </div>
        <div v-if="!item.isRead && !item.read" class="unread-dot" />
      </div>
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-else
      :title="activeTab === 'unread' ? '暂无未读通知' : '暂无通知'"
      description="所有消息都处理完毕了"
      icon="inbox"
    />
  </div>
</template>

<style scoped lang="scss">
.inbox-view {
  max-width: 720px;
  margin: 0 auto;
  padding: var(--space-6) var(--space-5);
  font-family: var(--font-primary);
}

.inbox-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-5);

  .header-left {
    display: flex;
    align-items: center;
    gap: var(--space-3);
  }
}

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.unread-badge {
  font-size: var(--text-xs);
  background: rgba(239, 68, 68, 0.1);
  color: var(--color-danger);
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-weight: var(--font-medium);
}

.mark-all-btn {
  border: none;
  background: transparent;
  color: var(--text-link);
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  cursor: pointer;
  padding: var(--space-1) var(--space-2);
  border-radius: var(--radius-sm);
  transition: opacity var(--transition-fast);

  &:hover { opacity: 0.7; }
  &:focus-visible { outline: 2px solid var(--color-primary); outline-offset: 2px; }
}

.inbox-tabs {
  display: flex;
  gap: var(--space-1);
  margin-bottom: var(--space-4);
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-1);
  width: fit-content;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-4);
  border: none;
  background: transparent;
  color: var(--text-secondary);
  font-size: var(--text-sm);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-family: var(--font-primary);
  transition: background var(--transition-fast), color var(--transition-fast);

  &:hover { color: var(--text-primary); }
  &.active {
    background: var(--bg-hover);
    color: var(--text-primary);
    font-weight: var(--font-semibold);
  }
  &:focus-visible { outline: 2px solid var(--color-primary); outline-offset: 2px; }
}

.tab-badge {
  background: var(--color-danger);
  color: #fff;
  font-size: 10px;
  padding: 0 5px;
  border-radius: var(--radius-full);
  font-weight: var(--font-bold);
  min-width: 16px;
  text-align: center;
  line-height: 16px;
}

.inbox-list {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.notif-item {
  display: flex;
  align-items: flex-start;
  gap: var(--space-3);
  padding: var(--space-4);
  cursor: pointer;
  transition: background var(--transition-fast);
  border-bottom: 1px solid var(--border-light);
  position: relative;

  &:last-child { border-bottom: none; }
  &:hover { background: var(--bg-hover); }
  &.unread { background: var(--bg-hover); border-left: 3px solid var(--color-primary); }
}

.notif-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.notif-body { flex: 1; min-width: 0; }

.notif-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-1);
}

.notif-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
}

.notif-time {
  font-size: var(--text-xs);
  color: var(--text-muted);
  flex-shrink: 0;
  margin-left: var(--space-2);
}

.notif-content {
  font-size: var(--text-sm);
  color: var(--text-secondary);
  line-height: var(--leading-normal);
  margin-bottom: var(--space-1);
}

.notif-ref {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-xs);

  .ref-project { color: var(--text-muted); }
  .ref-sep { color: var(--border-color); }
  .ref-taskno { color: var(--color-primary); font-family: var(--font-mono); font-weight: var(--font-medium); }
}

.unread-dot {
  position: absolute;
  right: var(--space-4);
  top: 50%;
  transform: translateY(-50%);
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--color-primary);
}
</style>
