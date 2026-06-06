import type { WorkspaceNavigationSection, WorkspaceShellActionKey, WorkspaceShellMenuGroup, WorkspaceTopNavigationItem } from "./types";

export const workspaceTopNavigationItems: WorkspaceTopNavigationItem[] = [
  { key: "workbench", label: "工作台", pageKeys: [], hasMenu: false },
  { key: "projects", label: "项目", pageKeys: ["roadmap", "backlog", "kanban", "issues", "settings", "components"], hasMenu: true, defaultPageKey: "roadmap" },
  { key: "filters", label: "筛选器", pageKeys: [], hasMenu: true },
  { key: "dashboards", label: "仪表盘", pageKeys: ["dashboard", "reports"], hasMenu: false, defaultPageKey: "dashboard" },
  { key: "team", label: "团队", pageKeys: [], hasMenu: false },
  { key: "apps", label: "应用", pageKeys: [], hasMenu: true },
];

export const workspaceNavigationSections: WorkspaceNavigationSection[] = [
  {
    title: "计划",
    items: [
      { label: "仪表盘", icon: "layout", pageKey: "dashboard" },
      { label: "路线图", icon: "compass", pageKey: "roadmap" },
      { label: "待办事项", icon: "folder", pageKey: "backlog" },
      { label: "看板", icon: "clipboard", pageKey: "kanban" },
      { label: "报告", icon: "pie", pageKey: "reports" },
    ],
  },
  {
    title: "开发",
    items: [
      { label: "代码", icon: "code" },
      { label: "版本发布", icon: "git" },
    ],
  },
  {
    title: "运维",
    items: [
      { label: "部署", icon: "box" },
      { label: "事件", icon: "activity" },
    ],
  },
  {
    title: "项目页面",
    items: [
      { label: "需求文档", icon: "file" },
      { label: "问题管理", icon: "clipboard", pageKey: "issues" },
      { label: "组件规范", icon: "file", pageKey: "components" },
      { label: "项目设置", icon: "settings", pageKey: "settings" },
    ],
  },
];

export const workspaceShellActionMenus: Record<Exclude<WorkspaceShellActionKey, "user">, WorkspaceShellMenuGroup> = {
  help: {
    key: "help",
    title: "帮助中心",
    items: ["打开使用文档", "查看快捷键", "联系项目管理员"],
  },
  settings: {
    key: "settings",
    title: "全局设置",
    items: ["个人偏好", "通知设置", "项目设置"],
  },
};

export const workspaceUserMenuGroups: WorkspaceShellMenuGroup[] = [
  { key: "profile", items: ["个人资料", "账户设置", "通知设置"] },
  { key: "preferences", items: ["主题", "快捷键", "语言"] },
  { key: "workspace", items: ["切换工作区", "帮助中心", "产品更新", "关于我们"] },
];
