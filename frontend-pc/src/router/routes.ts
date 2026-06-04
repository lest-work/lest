import type { RouteRecordRaw } from 'vue-router';

/**
 * 静态路由配置
 * 不再依赖后端菜单驱动，前端直接定义所有路由
 */
export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页仪表盘' },
      },
      {
        path: 'inbox',
        name: 'Inbox',
        component: () => import('@/views/inbox/index.vue'),
        meta: { title: '收件箱' },
      },
      {
        path: 'my-tasks',
        name: 'MyTasks',
        component: () => import('@/views/task/TaskList.vue'),
        meta: { title: '我的任务' },
      },
      {
        path: 'projects',
        name: 'ProjectList',
        component: () => import('@/views/project/ProjectList.vue'),
        meta: { title: '项目列表' },
      },
      {
        path: 'project/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/project/ProjectDetail.vue'),
        redirect: (to) => `/project/${to.params.id}/tasks`,
        children: [
          {
            path: 'tasks',
            name: 'TaskList',
            component: () => import('@/views/task/TaskList.vue'),
          },
          {
            path: 'board',
            name: 'Kanban',
            component: () => import('@/views/task/Kanban.vue'),
          },
          {
            path: 'gantt',
            name: 'Gantt',
            component: () => import('@/views/task/Gantt.vue'),
          },
          {
            path: 'iteration/:iterationId',
            name: 'IterationDetail',
            component: () => import('@/views/project/IterationDetail.vue'),
          },
          {
            path: 'milestone/:milestoneId',
            name: 'MilestoneDetail',
            component: () => import('@/views/project/MilestoneDetail.vue'),
          },
          {
            path: 'release',
            name: 'ReleasePlan',
            component: () => import('@/views/project/ReleasePlan.vue'),
          },
        ],
      },
      {
        path: 'settings',
        name: 'Settings',
        redirect: '/settings/profile',
        children: [
          {
            path: 'profile',
            name: 'Profile',
            component: () => import('@/views/settings/Profile.vue'),
            meta: { title: '个人设置' },
          },
          {
            path: 'openapi',
            name: 'OpenApiSettings',
            component: () => import('@/views/settings/OpenApiSettings.vue'),
            meta: { title: '开放平台' },
          },
        ],
      },
      {
        path: 'meetings',
        name: 'MeetingList',
        component: () => import('@/views/meeting/MeetingList.vue'),
        meta: { title: '会议' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
        component: () => import('@/views/exception/404/index.vue'),
  },
];

/**
 * 白名单路由（无需登录即可访问）
 */
export const WHITE_LIST = ['/login'];
