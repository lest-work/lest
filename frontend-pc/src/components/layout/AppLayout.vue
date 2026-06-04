<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { useProjectStore } from '@/stores/project';
import { useThemeStore } from '@/stores/theme';
import { notificationWsService } from '@/services/notificationWs';
import { notificationApi } from '@/api/notification';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const projectStore = useProjectStore();
const themeStore = useThemeStore();

const emit = defineEmits<{
  (e: 'openCommandPalette'): void;
}>();

const collapsed = ref(false);
const userMenuVisible = ref(false);

// 侧边栏当前展开的项目（已弃用，项目子导航移除）
// const expandedProjectId = ref<number | null>(null);

// 当前激活的导航项
const activeNav = computed(() => {
  const path = route.path;
  if (path.startsWith('/inbox')) return '/inbox';
  if (path.startsWith('/my-tasks')) return '/my-tasks';
  if (path.startsWith('/dashboard')) return '/dashboard';
  if (path.startsWith('/projects')) return '/projects';
  if (path.startsWith('/settings')) return '/settings';
  if (path.startsWith('/project/')) return `/project/${route.params.id}`;
  return '';
});

// 收件箱未读数（支持实时更新）
const inboxCount = ref(0);

async function refreshInboxCount() {
  try {
    const res = await notificationApi.unreadCount();
    if (res.code === 200) {
      inboxCount.value = res.data || 0;
    }
  } catch {
    // ignore
  }
}

onMounted(async () => {
  await projectStore.fetchProjects({ status: 'active' });
  await refreshInboxCount();

  // 连接 WebSocket 实时通知（仅在已登录时）
  const token = userStore.token || localStorage.getItem('token');
  const userId = userStore.userInfo?.id;
  if (token && userId) {
    notificationWsService.connect(String(userId), token);
    notificationWsService.onNotification(() => {
      inboxCount.value += 1;
    });
  }
});

onUnmounted(() => {
  notificationWsService.disconnect();
});

function toggleSidebar() {
  collapsed.value = !collapsed.value;
}

function navigateTo(path: string) {
  router.push(path);
  userMenuVisible.value = false;
}

// function toggleProject(projectId: number) {
//   if (expandedProjectId.value === projectId) {
//     expandedProjectId.value = null;
//   } else {
//     expandedProjectId.value = projectId;
//   }
// }

function navigateToProject(projectId: number) {
  router.push(`/project/${projectId}/board`);
}

function getCurrentProjectName() {
  const id = route.params.id;
  if (!id) return '';
  const project = projectStore.projects.find(p => p.id === Number(id));
  return project?.name || '';
}

function getProjectColor(project: { color?: string }) {
  return project.color || 'var(--color-primary)';
}

async function handleLogout() {
  userMenuVisible.value = false;
  await userStore.logout();
  router.push('/login');
}
</script>

<template>
  <div class="lest-shell">
    <!-- ========== 侧边栏 — Linear 深色工作台风格 ========== -->
    <aside class="lest-sidebar" :class="{ collapsed }">
      <!-- Logo -->
      <div class="sidebar-logo" @click="navigateTo('/dashboard')" title="LEST">
        <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
          <rect width="28" height="28" rx="6" fill="var(--color-primary)"/>
          <path d="M8 20V8l6 6 6-6v12" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span v-if="!collapsed" class="logo-text">LEST</span>
      </div>

      <!-- 全局导航 -->
      <nav class="sidebar-nav">
        <button
          class="nav-item"
          :class="{ active: activeNav === '/inbox' }"
          @click="navigateTo('/inbox')"
          :title="collapsed ? '收件箱' : ''"
        >
          <span class="nav-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="22 12 16 12 14 15 10 15 8 12 2 12"/>
              <path d="M5.45 5.11L2 12v6a2 2 0 002 2h16a2 2 0 002-2v-6l-3.45-6.89A2 2 0 0016.76 4H7.24a2 2 0 00-1.79 1.11z"/>
            </svg>
          </span>
          <span v-if="!collapsed" class="nav-label">收件箱</span>
          <span v-if="!collapsed && inboxCount > 0" class="nav-badge">{{ inboxCount }}</span>
        </button>

        <button
          class="nav-item"
          :class="{ active: activeNav === '/my-tasks' }"
          @click="navigateTo('/my-tasks')"
          :title="collapsed ? '我的任务' : ''"
        >
          <span class="nav-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 11l3 3L22 4"/>
              <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
            </svg>
          </span>
          <span v-if="!collapsed" class="nav-label">我的任务</span>
        </button>

        <button
          class="nav-item"
          :class="{ active: activeNav === '/dashboard' }"
          @click="navigateTo('/dashboard')"
          :title="collapsed ? '仪表盘' : ''"
        >
          <span class="nav-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="7" height="7"/>
              <rect x="14" y="3" width="7" height="7"/>
              <rect x="14" y="14" width="7" height="7"/>
              <rect x="3" y="14" width="7" height="7"/>
            </svg>
          </span>
          <span v-if="!collapsed" class="nav-label">仪表盘</span>
        </button>

        <button
          class="nav-item"
          :class="{ active: activeNav === '/meetings' }"
          @click="navigateTo('/meetings')"
          :title="collapsed ? '会议' : ''"
        >
          <span class="nav-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 00-3-3.87"/>
              <path d="M16 3.13a4 4 0 010 7.75"/>
            </svg>
          </span>
          <span v-if="!collapsed" class="nav-label">会议</span>
        </button>

      </nav>

      <div class="sidebar-divider" />

      <!-- 项目列表 — Jira 风格：仅图标，无文字标签 -->
      <div v-if="!collapsed" class="sidebar-projects-header">
        <button class="icon-btn" @click="navigateTo('/projects')" title="所有项目">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
        </button>
      </div>

      <div class="sidebar-projects" :class="{ compact: collapsed }">
        <!-- 收起模式：只显示项目首字母 -->
        <template v-if="collapsed">
          <button
            v-for="project in projectStore.projects"
            :key="project.id"
            class="project-item-mini"
            :class="{ active: activeNav === `/project/${project.id}` }"
            @click="navigateToProject(project.id ?? 0)"
            :title="project.name"
          >
            <span class="project-icon" :style="{ background: getProjectColor(project) + '33', color: getProjectColor(project) }">
              {{ project.name.charAt(0).toUpperCase() }}
            </span>
          </button>
        </template>

        <!-- 展开模式：可展开的层级项目 -->
        <template v-else>
          <div
            v-for="project in projectStore.projects"
            :key="project.id"
            class="project-group"
          >
            <button
              class="project-item"
              :class="{ active: activeNav === `/project/${project.id}` }"
              @click="navigateToProject(project.id ?? 0)"
            >
              <span class="project-icon" :style="{ background: getProjectColor(project) + '33', color: getProjectColor(project) }">
                {{ project.name.charAt(0).toUpperCase() }}
              </span>
            </button>
          </div>
        </template>
      </div>

      <div class="sidebar-spacer" />

      <!-- 底部用户区 — 头像下拉菜单（规范要求） -->
      <div class="sidebar-footer">
        <el-dropdown trigger="click" v-model:visible="userMenuVisible" placement="top-start" :hide-timeout="150">
          <button class="user-avatar-btn" :title="collapsed ? userStore.userInfo?.nickname : ''">
            <el-avatar :size="collapsed ? 28 : 32" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
            </el-avatar>
            <div v-if="!collapsed" class="user-info">
              <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
              <span class="user-role">{{ userStore.userInfo?.roles?.[0] || '成员' }}</span>
            </div>
            <svg v-if="!collapsed" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="chevron">
              <polyline points="6 9 12 15 18 9"/>
            </svg>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateTo('/settings/profile')">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item divided @click="themeStore.toggle()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px">
                  <circle cx="12" cy="12" r="5"/>
                  <line x1="12" y1="1" x2="12" y2="3"/>
                  <line x1="12" y1="21" x2="12" y2="23"/>
                  <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
                  <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
                  <line x1="1" y1="12" x2="3" y2="12"/>
                  <line x1="21" y1="12" x2="23" y2="12"/>
                  <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
                  <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
                </svg>
                {{ themeStore.isDark ? '浅色模式' : '深色模式' }}
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:8px">
                  <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
                  <polyline points="16 17 21 12 16 7"/>
                  <line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <button class="collapse-btn" @click="toggleSidebar" :title="collapsed ? '展开侧边栏' : '收起侧边栏'">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline v-if="collapsed" points="9 18 15 12 9 6"/>
            <polyline v-else points="15 18 9 12 15 6"/>
          </svg>
        </button>
      </div>
    </aside>

    <!-- ========== 主内容区 ========== -->
    <div class="lest-content">
      <!-- TopBar — 面包屑 + 搜索 + 通知 + 头像 -->
      <header class="lest-header">
        <div class="header-breadcrumb">
          <!-- 规范要求：面包屑只在项目内显示项目名称，工作区页面（收件箱/仪表盘等）TopBar 不显示页面标题 -->
          <span v-if="activeNav.startsWith('/project/') && getCurrentProjectName()" class="breadcrumb-project">
            {{ getCurrentProjectName() }}
          </span>
        </div>

        <!-- 搜索入口 — ⌘K（Jira 风格：仅图标） -->
        <button class="header-search-btn" @click="$emit('openCommandPalette')" title="搜索 ⌘K">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
        </button>

        <div class="header-actions">
          <!-- 通知 -->
          <el-badge :value="inboxCount" :max="9" :hidden="inboxCount === 0">
            <button class="icon-action-btn" @click="navigateTo('/inbox')" title="收件箱">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 01-3.46 0"/>
              </svg>
            </button>
          </el-badge>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="lest-main">
        <router-view v-slot="{ Component }">
          <transition name="lest-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped lang="scss">
.lest-shell {
  display: flex;
  height: 100vh;
  width: 100vw;
  background: var(--bg-primary);
  font-family: var(--font-primary);
  overflow: hidden;
}

.lest-sidebar {
  width: var(--sidebar-width);
  min-width: var(--sidebar-width);
  background: var(--sidebar-bg);
  color: var(--sidebar-text);
  display: flex;
  flex-direction: column;
  transition: width var(--transition-normal), min-width var(--transition-normal);
  overflow: hidden;
  position: relative;
  z-index: 100;

  &.collapsed {
    width: var(--sidebar-collapsed);
    min-width: var(--sidebar-collapsed);
  }
}

/* Logo */
.sidebar-logo {
  height: var(--header-height);
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  cursor: pointer;
  flex-shrink: 0;

  .logo-text {
    font-size: var(--text-md);
    font-weight: var(--font-bold);
    color: #fff;
    letter-spacing: 3px;
  }
}

/* 全局导航 */
.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 1px;
  padding: var(--space-2) var(--space-2);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-md);
  border: none;
  background: transparent;
  color: var(--sidebar-text);
  cursor: pointer;
  width: 100%;
  text-align: left;
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  transition: background var(--transition-fast), color var(--transition-fast);
  white-space: nowrap;

  .nav-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    width: 18px;
    height: 18px;
  }

  .nav-label {
    flex: 1;
    font-weight: var(--font-medium);
  }

  .nav-badge {
    background: var(--color-danger);
    color: #fff;
    font-size: 10px;
    font-weight: var(--font-bold);
    padding: 1px 5px;
    border-radius: var(--radius-full);
    min-width: 16px;
    text-align: center;
    line-height: 14px;
  }

  &:hover {
    background: var(--sidebar-hover-bg);
    color: #E5E7EB;
  }

  &.active {
    background: var(--sidebar-active-bg);
    color: var(--sidebar-text-active);
  }

  &:focus-visible {
    outline: 2px solid var(--color-primary);
    outline-offset: -2px;
  }
}

.sidebar-divider {
  height: 1px;
  background: var(--sidebar-divider);
  margin: var(--space-1) var(--space-3);
  flex-shrink: 0;
}

/* 项目列表 */
.sidebar-projects-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: var(--space-1) var(--space-2);
  flex-shrink: 0;
}

.icon-btn {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: #6B7280;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: background var(--transition-fast), color var(--transition-fast);

  &:hover {
    background: var(--sidebar-hover-bg);
    color: var(--sidebar-text);
  }
}

/* 项目列表 */
.sidebar-projects {
  display: flex;
  flex-direction: column;
  padding: var(--space-1) var(--space-2);
  overflow-y: auto;
  flex: 1;

  &.compact {
    padding: var(--space-1) var(--space-1);
    align-items: center;
  }
}

.project-group {
  display: flex;
  flex-direction: column;
}

.project-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 6px var(--space-2);
  border-radius: var(--radius-md);
  border: none;
  background: transparent;
  color: var(--sidebar-text);
  cursor: pointer;
  width: 100%;
  text-align: left;
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  transition: background var(--transition-fast), color var(--transition-fast);

  .project-icon {
    width: 22px;
    height: 22px;
    border-radius: 5px;
    font-size: 11px;
    font-weight: var(--font-bold);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  &:hover {
    background: var(--sidebar-hover-bg);
  }

  &.active {
    background: var(--sidebar-active-bg);
  }
}

.project-item-mini {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);

  .project-icon {
    width: 26px;
    height: 26px;
    border-radius: 6px;
    font-size: 12px;
    font-weight: var(--font-bold);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &:hover {
    background: var(--sidebar-hover-bg);
  }

  &.active .project-icon {
    box-shadow: 0 0 0 2px var(--sidebar-text);
  }
}

/* 占位 */
.sidebar-spacer { flex: 1; min-height: var(--space-2); }

/* 底部用户区 */
.sidebar-footer {
  padding: var(--space-2);
  border-top: 1px solid rgba(255,255,255,0.04);
  display: flex;
  align-items: center;
  gap: var(--space-1);
  flex-shrink: 0;
}

.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-1) var(--space-2);
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: var(--radius-md);
  flex: 1;
  min-width: 0;
  transition: background var(--transition-fast);

  &:hover { background: var(--sidebar-hover-bg); }

  .user-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;

    .user-name {
      font-size: var(--text-sm);
      color: #D1D5DB;
      font-weight: var(--font-medium);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .user-role {
      font-size: 11px;
      color: #6B7280;
    }
  }

  .chevron {
    color: #6B7280;
    flex-shrink: 0;
  }
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: #6B7280;
  cursor: pointer;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
  transition: background var(--transition-fast), color var(--transition-fast);

  &:hover {
    background: var(--sidebar-hover-bg);
    color: var(--sidebar-text);
  }
}

.lest-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--bg-secondary);
}

/* TopBar */
.lest-header {
  height: var(--header-height);
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  padding: 0 var(--space-5);
  gap: var(--space-4);
  flex-shrink: 0;
}

.header-breadcrumb {
  .breadcrumb-project {
    font-size: var(--text-base);
    font-weight: var(--font-semibold);
    color: var(--text-primary);
  }

  .breadcrumb-page {
    font-size: var(--text-base);
    font-weight: var(--font-semibold);
    color: var(--text-primary);
  }
}

/* 搜索按钮 — ⌘K 入口 */
.header-search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  color: var(--text-secondary);
  cursor: pointer;
  transition: background var(--transition-fast), border-color var(--transition-fast), color var(--transition-fast);

  &:hover {
    background: var(--bg-hover);
    border-color: var(--border-color);
    color: var(--text-primary);
  }
}

/* 右侧操作 */
.header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  margin-left: auto;
}

.icon-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: background var(--transition-fast), color var(--transition-fast);

  &:hover {
    background: var(--bg-hover);
    color: var(--text-primary);
  }
}

/* 页面内容 */
.lest-main {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

/* 页面切换动画 */
.lest-fade-enter-active,
.lest-fade-leave-active {
  transition: opacity var(--transition-normal);
}
.lest-fade-enter-from,
.lest-fade-leave-to {
  opacity: 0;
}
</style>
