import type {
  BoardColumnSettingsRequest,
  BoardColumnSettingsResponse,
  BoardIssueCard,
  BoardIssueDetailRequest,
  BoardIssueDetailResponse,
  BoardRequest,
  BoardResponse,
  BoardSprint,
  BoardSprintsRequest,
  BoardSprintsResponse,
  CloseSprintRequest,
  CloseSprintResponse,
  ComponentFeedbackStates,
  ComponentGalleryRequest,
  ComponentGalleryResponse,
  DangerActionSpec,
  CreateSprintRequest,
  CreateSprintResponse,
  MoveBoardIssueRequest,
  MoveBoardIssueResponse,
  ProjectSettingsRequest,
  ProjectSettingsResponse,
  ExportReportRequest,
  ExportReportResponse,
  ReportConfigRequest,
  ReportConfigResponse,
  ReportDrilldownRequest,
  ReportDrilldownResponse,
  ReportFilterOptionsRequest,
  ReportFilterOptionsResponse,
  ReportFilterState,
  SaveReportConfigRequest,
  SaveReportConfigResponse,
  ReportsRequest,
  ReportsResponse,
  RoadmapRequest,
  RoadmapResponse,
  SprintCheckItem,
  SprintClosePreviewRequest,
  SprintClosePreviewResponse,
  SprintStartPreviewRequest,
  SprintStartPreviewResponse,
  StartSprintRequest,
  StartSprintResponse,
  UpdateBoardColumnSettingsRequest,
  UpdateBoardColumnSettingsResponse,
  WorkspaceNotification,
  WorkspaceNotificationsRequest,
  WorkspaceNotificationsResponse,
  WorkspaceRequest,
  WorkspaceSearchRequest,
  WorkspaceSearchResponse,
  WorkspaceShellRequest,
  WorkspaceShellResponse,
  WorkspaceUser,
} from "./contracts";
import type { ProjectWorkspaceService } from "./service";
const avatar = (name: string) => `https://api.dicebear.com/9.x/lorelei/svg?seed=${encodeURIComponent(name)}`;

const currentWorkspaceUser: WorkspaceUser = {
  id: "u-current",
  name: "张晓明",
  role: "项目负责人",
  email: "zhangxiaoming@example.com",
  avatar: avatar("张晓明"),
};

const workspaceUsers: WorkspaceUser[] = [
  currentWorkspaceUser,
  { id: "u-2", name: "李华", role: "前端工程师", email: "lihua@example.com", avatar: avatar("李华") },
  { id: "u-3", name: "王芳", role: "后端工程师", email: "wangfang@example.com", avatar: avatar("王芳") },
  { id: "u-4", name: "陈静", role: "测试工程师", email: "chenjing@example.com", avatar: avatar("陈静") },
  { id: "u-5", name: "刘强", role: "DevOps 工程师", email: "liuqiang@example.com", avatar: avatar("刘强") },
];

let boardIssues: BoardIssueCard[] = [
  { id: "alp-128", key: "ALP-128", title: "修复页面刷新后丢失状态", type: "bug", priority: "high", status: "todo", assignee: { name: "张晓明", avatar: avatar("张晓明") }, labels: ["缺陷"], estimate: 2 },
  { id: "alp-162", key: "ALP-162", title: "添加接口单元测试", type: "task", priority: "medium", status: "todo", assignee: { name: "张晓明", avatar: avatar("张晓明") }, labels: ["任务"], estimate: 3 },
  { id: "alp-135", key: "ALP-135", title: "用户批量导入功能", type: "task", priority: "highest", status: "in-progress", assignee: { name: "李华", avatar: avatar("李华") }, labels: ["任务"], estimate: 5 },
  { id: "alp-150", key: "ALP-150", title: "移动端适配优化", type: "task", priority: "high", status: "in-progress", assignee: { name: "王芳", avatar: avatar("王芳") }, labels: ["任务"], estimate: 8 },
  { id: "alp-171", key: "ALP-171", title: "支付风控规则联调", type: "story", priority: "high", status: "in-progress", assignee: { name: "刘强", avatar: avatar("刘强") }, labels: ["风控"], estimate: 5 },
  { id: "alp-174", key: "ALP-174", title: "审计日志字段补齐", type: "task", priority: "medium", status: "in-progress", assignee: { name: "陈静", avatar: avatar("陈静") }, labels: ["审计"], estimate: 3 },
  { id: "alp-177", key: "ALP-177", title: "通知模板变量预览", type: "task", priority: "medium", status: "in-progress", assignee: { name: "李华", avatar: avatar("李华") }, labels: ["通知"], estimate: 2 },
  { id: "alp-142", key: "ALP-142", title: "权限系统重构设计", type: "task", priority: "highest", status: "review", assignee: { name: "陈静", avatar: avatar("陈静") }, labels: ["权限"], estimate: 8 },
  { id: "alp-123", key: "ALP-123", title: "添加用户登录认证功能", type: "task", priority: "highest", status: "done", assignee: { name: "张晓明", avatar: avatar("张晓明") }, labels: ["认证"], estimate: 8 },
  { id: "alp-120", key: "ALP-120", title: "修复导出文件乱码", type: "bug", priority: "high", status: "closed", assignee: { name: "刘强", avatar: avatar("刘强") }, labels: ["缺陷"], estimate: 2 },
];

let workspaceNotifications: WorkspaceNotification[] = [
  {
    id: "notice-1",
    type: "comment",
    title: "李华评论了 ALP-124",
    description: "导航栏错位在 1440 宽度下仍可复现。",
    createdAtText: "4 小时前",
    unread: true,
    actor: workspaceUsers[1],
  },
  {
    id: "notice-2",
    type: "assignment",
    title: "你被指派到 ALP-123",
    description: "添加用户登录认证功能需要今天完成联调。",
    createdAtText: "今天 10:30",
    unread: true,
    actor: workspaceUsers[0],
  },
  {
    id: "notice-3",
    type: "status",
    title: "ALP-125 进入评审",
    description: "王芳将仪表盘性能优化提交评审。",
    createdAtText: "昨天",
    unread: true,
    actor: workspaceUsers[2],
  },
  {
    id: "notice-4",
    type: "system",
    title: "本周报表已生成",
    description: "项目健康度、问题趋势和负责人分布已经更新。",
    createdAtText: "2 天前",
    unread: false,
  },
];

let boardColumnSettings: Record<BoardIssueCard["status"], BoardColumnSettingsResponse["column"]> = {
  todo: { id: "todo", name: "待处理", statusKey: "todo", color: "#6B7280", wipEnabled: true, wipLimit: 8, description: "等待团队认领或排期的任务", editable: true, systemColumn: false },
  "in-progress": { id: "in-progress", name: "进行中", statusKey: "in-progress", color: "#0C66E4", wipEnabled: true, wipLimit: 5, description: "正在开发、设计或联调中的任务", editable: true, systemColumn: false },
  review: { id: "review", name: "评审中", statusKey: "review", color: "#FF9F1A", wipEnabled: true, wipLimit: 4, description: "等待代码评审、产品验收或测试确认", editable: true, systemColumn: false },
  done: { id: "done", name: "已完成", statusKey: "done", color: "#22A06B", wipEnabled: false, description: "已完成但尚未归档的问题", editable: false, systemColumn: true },
  closed: { id: "closed", name: "已关闭", statusKey: "closed", color: "#9CA3AF", wipEnabled: false, description: "已关闭归档的问题", editable: false, systemColumn: true },
};

let sprints: BoardSprint[] = [
  {
    id: "sp-13",
    name: "Sprint 13",
    projectKey: "alpha-platform",
    goal: "完成用户登录与权限相关联调，并收敛高优先级缺陷。",
    status: "planning",
    startDate: "2024-06-03",
    endDate: "2024-06-16",
    durationDays: 14,
    capacity: 120,
    wipLimit: 5,
    maxParallelIssues: 20,
    issueCount: 32,
    completedIssueCount: 24,
    remainingIssueCount: 8,
    members: [
      { id: "u-1", name: "张三", role: "Scrum Master", avatar: avatar("张三") },
      { id: "u-2", name: "李华", role: "开发", avatar: avatar("李华") },
      { id: "u-3", name: "王芳", role: "测试", avatar: avatar("王芳") },
    ],
    tags: ["登录与认证", "质量周"],
  },
  {
    id: "sp-12",
    name: "Sprint 12",
    projectKey: "alpha-platform",
    goal: "完成核心看板和 Backlog 的基础体验。",
    status: "completed",
    startDate: "2024-05-20",
    endDate: "2024-06-02",
    durationDays: 14,
    capacity: 100,
    wipLimit: 5,
    maxParallelIssues: 18,
    issueCount: 28,
    completedIssueCount: 28,
    remainingIssueCount: 0,
    members: [{ id: "u-1", name: "张三", role: "Scrum Master", avatar: avatar("张三") }],
    tags: ["看板"],
  },
];

function wait(delay = 140) {
  return new Promise((resolve) => setTimeout(resolve, delay));
}

function boardColumns(): BoardResponse["columns"] {
  const defs = Object.values(boardColumnSettings);

  return defs.map((column) => {
    const issues = boardIssues.filter((issue) => issue.status === column.id);
    return {
      id: column.id,
      title: column.name,
      color: column.color,
      wipLimit: column.wipEnabled ? column.wipLimit : undefined,
      description: column.description,
      editable: column.editable,
      count: issues.length,
      issues,
    };
  });
}

function sprintDurationDays(startDate: string, endDate: string) {
  const start = new Date(`${startDate}T00:00:00`);
  const end = new Date(`${endDate}T00:00:00`);
  const diff = Math.round((end.getTime() - start.getTime()) / 86400000);
  return Math.max(diff + 1, 1);
}

function findSprint(sprintId: string) {
  const sprint = sprints.find((item) => item.id === sprintId);
  if (!sprint) {
    throw new Error(`Sprint ${sprintId} not found`);
  }
  return sprint;
}

function startChecks(sprint: BoardSprint): SprintCheckItem[] {
  const activeExists = sprints.some((item) => item.status === "active" && item.id !== sprint.id);
  return [
    { id: "date", label: "开始日期在今天或未来", status: "passed", description: `${sprint.startDate} 可以作为迭代开始日期` },
    { id: "active", label: "迭代未被其他迭代阻塞", status: activeExists ? "warning" : "passed", description: activeExists ? "已有进行中的 Sprint，建议确认后启动" : "当前没有其他进行中的 Sprint" },
    { id: "members", label: "团队成员已分配", status: sprint.members.length >= 3 ? "passed" : "warning", description: `已分配 ${sprint.members.length}/10 名成员` },
    { id: "unfinished", label: "未完成的问题任务", status: sprint.remainingIssueCount > 10 ? "failed" : sprint.remainingIssueCount > 0 ? "warning" : "passed", description: `${sprint.remainingIssueCount} 项未完成问题将进入 Sprint` },
    { id: "wip", label: "WIP 限制已生效", status: sprint.wipLimit > 0 ? "info" : "failed", description: sprint.wipLimit > 0 ? `${sprint.wipLimit} 张卡片/列` : "需要设置 WIP 限制" },
  ];
}

function closeChecks(sprint: BoardSprint): SprintCheckItem[] {
  return [
    { id: "archive", label: "所有任务可归档", status: "passed", description: "已完成任务将进入只读归档状态" },
    { id: "unfinished", label: "存在未完成任务", status: sprint.remainingIssueCount > 0 ? "warning" : "passed", description: `${sprint.remainingIssueCount} 项未完成任务需要处理` },
    { id: "blocked", label: "阻塞任务检查", status: sprint.remainingIssueCount > 6 ? "failed" : "passed", description: sprint.remainingIssueCount > 6 ? "存在过多未完成工作，建议先拆分" : "阻塞项数量在可处理范围内" },
    { id: "wip", label: "WIP 限制已满足", status: "passed", description: "结束后不会导致目标队列超限" },
  ];
}

function riskFromChecks(checks: SprintCheckItem[]) {
  if (checks.some((item) => item.status === "failed")) {
    return "blocked" as const;
  }
  if (checks.some((item) => item.status === "warning")) {
    return "warning" as const;
  }
  return "none" as const;
}

const defaultReportFilters: ReportFilterState = {
  projectKey: "alpha-platform",
  sprintId: "sp-13",
  dateRange: { from: "2026-05-13", to: "2026-06-06" },
  statuses: ["todo", "in-progress", "review", "done"],
  priorities: ["highest", "high", "medium"],
  assigneeIds: ["u-1", "u-2"],
  labels: ["登录模块", "权限管理"],
  customFields: {
    source: "jira",
    severity: "all",
  },
};

const emptyReportFilters: ReportFilterState = {
  ...defaultReportFilters,
  statuses: ["closed"],
  labels: ["__empty__"],
};

const reportSavedFilters: ReportFilterOptionsResponse["savedFilters"] = [
  {
    id: "filter-default",
    name: "默认方案",
    description: "本项目最近 30 天核心交付指标",
    filters: defaultReportFilters,
    isDefault: true,
    updatedAt: "2026-06-06 09:20",
  },
  {
    id: "filter-quality",
    name: "缺陷质量复盘",
    description: "聚焦高优先级缺陷与修复效率",
    filters: {
      ...defaultReportFilters,
      priorities: ["highest", "high"],
      labels: ["缺陷", "质量周"],
    },
    updatedAt: "2026-06-05 18:10",
  },
  {
    id: "filter-empty",
    name: "本周无缺陷",
    description: "用于核对空数据状态与引导操作",
    filters: emptyReportFilters,
    updatedAt: "2026-06-04 14:35",
  },
];

const reportQuickFilters = [
  { id: "my-open", label: "我负责的", description: "当前用户负责且未关闭的问题", filters: { assigneeIds: ["u-1"], statuses: ["todo", "in-progress", "review"] } },
  { id: "high-priority", label: "高优先级", description: "最高和高优先级问题", filters: { priorities: ["highest", "high"] } },
  { id: "empty-bugs", label: "本周无缺陷", description: "查看无数据时的报表提示", filters: emptyReportFilters },
  { id: "recent-done", label: "最近完成", description: "近期已完成与已关闭事项", filters: { statuses: ["done", "closed"] } },
] satisfies ReportFilterOptionsResponse["quickFilters"];

const componentFeedbackStates: ComponentFeedbackStates = {
  loading: [
    { id: "issue-table", title: "问题列表骨架", scope: "table", shape: "table", rowCount: 6, description: "用于列表、筛选结果和分页切换，保持列宽与真实表格一致。" },
    { id: "activity-list", title: "活动列表骨架", scope: "panel", shape: "list", rowCount: 5, description: "用于评论、动态和通知面板，头像与内容块同步占位。" },
    { id: "kanban-cards", title: "看板卡片骨架", scope: "panel", shape: "cards", rowCount: 4, description: "用于看板列加载，保留标题、标签、负责人和估算位置。" },
    { id: "issue-detail", title: "问题详情骨架", scope: "drawer", shape: "detail", rowCount: 4, description: "用于抽屉详情，字段区、描述区、附件区分别占位。" },
    { id: "report-chart", title: "报表图表骨架", scope: "panel", shape: "chart", rowCount: 7, description: "用于柱状图、折线图和指标卡片，避免图表加载时跳动。" },
    { id: "issue-form", title: "表单页面骨架", scope: "form", shape: "form", rowCount: 5, description: "用于弹窗和表单页，保留标签、输入框和操作按钮位置。" },
  ],
  empty: [
    { id: "filtered-issues", title: "暂无匹配问题", description: "当前筛选条件没有匹配的问题，可以清空筛选或新建问题。", scope: "table", actionLabel: "清空筛选", secondaryActionLabel: "新建问题" },
    { id: "no-sprint", title: "暂无 Sprint", description: "当前项目还没有创建 Sprint，创建后即可在看板中规划迭代。", scope: "page", actionLabel: "创建 Sprint" },
    { id: "no-report", title: "暂无报表数据", description: "所选时间范围没有可统计的数据，调整日期或查看全部项目。", scope: "panel", actionLabel: "调整筛选" },
  ],
  errors: [
    { id: "network-failed", title: "网络连接失败", description: "请检查网络连接后重试，错误信息已记录便于排查。", scope: "page", severity: "high", code: "NETWORK_UNREACHABLE", traceId: "trc-20260606-001", actionLabel: "重新加载", secondaryActionLabel: "查看帮助" },
    { id: "fetch-timeout", title: "数据加载超时", description: "服务响应时间过长，可以重试或缩小筛选范围。", scope: "panel", severity: "medium", code: "REQUEST_TIMEOUT", traceId: "trc-20260606-002", actionLabel: "重试" },
    { id: "form-invalid", title: "表单校验失败", description: "请检查高亮字段后再次提交。", scope: "form", severity: "low", code: "VALIDATION_FAILED", traceId: "trc-20260606-003", actionLabel: "定位错误" },
    { id: "export-failed", title: "报表导出失败", description: "当前报表数据生成失败，请稍后再试。", scope: "toast", severity: "medium", code: "EXPORT_FAILED", traceId: "trc-20260606-004", actionLabel: "重试" },
  ],
  permissions: [
    { id: "project-denied", title: "无权访问此页面", description: "你当前没有查看该项目的权限，请联系项目管理员开通访问。", scope: "page", permissionKey: "project:view", roleSuggestion: "项目成员或项目管理员", actionLabel: "申请权限", secondaryActionLabel: "返回首页" },
    { id: "report-denied", title: "部分内容受限", description: "敏感报表字段仅对项目管理员可见。", scope: "panel", permissionKey: "report:read_sensitive", roleSuggestion: "项目管理员", actionLabel: "了解详情" },
    { id: "delete-denied", title: "无权删除问题", description: "删除问题属于高风险操作，需要项目管理员角色。", scope: "drawer", permissionKey: "issue:delete", roleSuggestion: "项目管理员", actionLabel: "申请权限" },
  ],
};

const dangerActions: DangerActionSpec[] = [
  {
    id: "delete-issue",
    title: "删除需求",
    description: "确定要删除需求「登录优化」吗？删除后无法恢复。",
    actionLabel: "删除",
    cancelLabel: "取消",
    severity: "delete",
    confirmationMode: "default",
    affectedSummary: [{ label: "影响范围", value: "1 个需求" }, { label: "恢复方式", value: "无法恢复" }],
  },
  {
    id: "bulk-delete",
    title: "批量删除",
    description: "将删除选中的 12 条问题，关联评论和附件会一并移除。",
    actionLabel: "删除",
    cancelLabel: "取消",
    severity: "bulk-delete",
    confirmationMode: "checkbox",
    affectedSummary: [{ label: "问题", value: "12 条" }, { label: "评论", value: "36 条" }, { label: "附件", value: "9 个" }],
  },
  {
    id: "permanent-delete",
    title: "永久删除需求",
    description: "该操作不可逆，请输入需求编号确认。",
    actionLabel: "永久删除",
    cancelLabel: "取消",
    severity: "delete",
    confirmationMode: "type-to-confirm",
    confirmationText: "ALP-128",
    affectedSummary: [{ label: "需求编号", value: "ALP-128" }, { label: "审计记录", value: "保留 180 天" }],
  },
  {
    id: "close-sprint",
    title: "关闭 Sprint",
    description: "关闭后成员将无法继续更新 Sprint 内问题，未完成项会移动到 Backlog。",
    actionLabel: "关闭",
    cancelLabel: "取消",
    severity: "close",
    confirmationMode: "countdown",
    affectedSummary: [{ label: "Sprint", value: "Sprint 13" }, { label: "未完成", value: "8 项" }],
  },
  {
    id: "move-issue",
    title: "移动成员",
    description: "成员移动后将继承目标项目的权限策略。",
    actionLabel: "移动",
    cancelLabel: "取消",
    severity: "move",
    confirmationMode: "default",
    affectedSummary: [{ label: "成员", value: "李华" }, { label: "目标项目", value: "支付中台" }],
  },
  {
    id: "archive-project",
    title: "归档项目",
    description: "项目归档后默认只读，仍可由管理员恢复。",
    actionLabel: "归档",
    cancelLabel: "取消",
    severity: "archive",
    confirmationMode: "checkbox",
    affectedSummary: [{ label: "问题", value: "178 条" }, { label: "Sprint", value: "13 个" }, { label: "报表", value: "8 个" }],
  },
];

function isEmptyReport(filters?: ReportFilterState) {
  return Boolean(filters?.labels.includes("__empty__"));
}

function reportFilters(params?: { filters?: ReportFilterState }) {
  return params?.filters ?? defaultReportFilters;
}

function reportPreview(filters: ReportFilterState): ReportsResponse {
  const empty = isEmptyReport(filters);
  return {
    metrics: empty
      ? [
          { id: "created", label: "新建问题", value: "0", change: "0%", trend: "down" },
          { id: "resolved", label: "解决问题", value: "0", change: "0%", trend: "down" },
          { id: "cycle", label: "平均周期", value: "--", change: "0%", trend: "down" },
          { id: "quality", label: "缺陷率", value: "--", change: "0%", trend: "down" },
        ]
      : [
          { id: "created", label: "新建问题", value: "145", change: "+18%", trend: "up" },
          { id: "resolved", label: "解决问题", value: "123", change: "+20%", trend: "up" },
          { id: "cycle", label: "平均周期", value: "2.4 天", change: "-8%", trend: "down" },
          { id: "quality", label: "缺陷率", value: "6.8%", change: "-3%", trend: "down" },
        ],
    trend: empty
      ? []
      : [
          { label: "5-13", created: 18, resolved: 12 },
          { label: "5-14", created: 28, resolved: 18 },
          { label: "5-15", created: 35, resolved: 22 },
          { label: "5-16", created: 30, resolved: 26 },
          { label: "5-17", created: 42, resolved: 35 },
          { label: "5-18", created: 20, resolved: 28 },
          { label: "5-19", created: 16, resolved: 24 },
        ],
    distributions: empty
      ? []
      : [
          { id: "task", label: "任务", value: 82, color: "#22A06B" },
          { id: "bug", label: "缺陷", value: 38, color: "#FF5630" },
          { id: "story", label: "需求", value: 44, color: "#FFAB00" },
          { id: "subtask", label: "子任务", value: 21, color: "#0C66E4" },
        ],
    activeFilters: filters,
    savedFilters: reportSavedFilters,
    quickFilters: reportQuickFilters,
    hasData: !empty,
    emptyState: empty
      ? {
          title: "暂无匹配的报表数据",
          description: "当前筛选条件下没有可统计的问题。可以调整筛选范围，或创建新问题后再回来查看趋势。",
          actionLabel: "调整筛选",
          reason: "filtered_empty",
        }
      : undefined,
  };
}

function drilldownRows(params: ReportDrilldownRequest): ReportDrilldownResponse["rows"] {
  const source = boardIssues.filter((issue) => {
    if (params.filters.statuses.length && !params.filters.statuses.includes(issue.status)) {
      return false;
    }
    if (params.filters.priorities.length && !params.filters.priorities.includes(issue.priority)) {
      return false;
    }
    if (params.dimension === "issueType" && issue.type !== params.dimensionValue) {
      return false;
    }
    return true;
  });

  return source.map((issue, index) => ({
    id: issue.id,
    key: issue.key,
    title: issue.title,
    type: issue.type,
    status: issue.status,
    priority: issue.priority,
    assignee: issue.assignee?.name ?? "未分配",
    projectName: "敏捷研发平台",
    createdAt: `2026-05-${String(16 + index).padStart(2, "0")} 09:${String(20 + index).padStart(2, "0")}`,
  }));
}


export const mockProjectWorkspaceService: ProjectWorkspaceService = {
  async getWorkspaceShell(params): Promise<WorkspaceShellResponse> {
    await wait(80);
    const notifications = params.includeNotifications === false ? [] : workspaceNotifications.slice(0, params.notificationPageSize ?? 8);

    return {
      project: {
        key: params.projectKey,
        name: "敏捷研发平台",
        category: "软件项目",
      },
      user: currentWorkspaceUser,
      workspace: {
        id: "workspace-alpha",
        name: "Alpha 产品研发",
      },
      unreadNotificationCount: workspaceNotifications.filter((item) => item.unread).length,
      theme: "light",
      locale: "zh-CN",
      notifications,
    };
  },

  async searchWorkspace(params): Promise<WorkspaceSearchResponse> {
    await wait(120);
    const keyword = params.keyword.trim().toLowerCase();
    const limit = params.limit ?? 8;
    const issueResults = boardIssues
      .filter((issue) => !keyword || `${issue.key} ${issue.title} ${issue.labels.join(" ")}`.toLowerCase().includes(keyword))
      .map((issue) => ({
        id: issue.id,
        type: "issue" as const,
        title: `${issue.key} ${issue.title}`,
        description: issue.labels.length ? issue.labels.join(" / ") : "项目问题",
        targetPageKey: "issues",
        avatar: issue.assignee?.avatar,
        meta: issue.status,
      }));
    const memberResults = workspaceUsers
      .filter((user) => !keyword || `${user.name} ${user.role} ${user.email}`.toLowerCase().includes(keyword))
      .map((user) => ({
        id: user.id,
        type: "member" as const,
        title: user.name,
        description: user.role,
        avatar: user.avatar,
        meta: user.email,
      }));
    const pageResults = [
      { id: "page-dashboard", type: "page" as const, title: "项目仪表盘", description: "查看项目指标与动态", targetPageKey: "dashboard" },
      { id: "page-roadmap", type: "page" as const, title: "路线图", description: "查看迭代和里程碑", targetPageKey: "roadmap" },
      { id: "page-backlog", type: "page" as const, title: "待办事项", description: "规划 Backlog 和 Sprint", targetPageKey: "backlog" },
      { id: "page-kanban", type: "page" as const, title: "看板", description: "跟踪 Sprint 状态流转", targetPageKey: "kanban" },
      { id: "page-reports", type: "page" as const, title: "报告", description: "查看报表与导出数据", targetPageKey: "reports" },
    ].filter((page) => !keyword || `${page.title} ${page.description}`.toLowerCase().includes(keyword));

    return {
      recentKeywords: ["登录认证", "仪表盘", "导航栏"],
      results: [
        ...issueResults,
        { id: "project-alpha", type: "project" as const, title: "敏捷研发平台", description: "软件项目", targetPageKey: "dashboard", meta: params.projectKey },
        ...pageResults,
        ...memberResults,
      ].slice(0, limit),
    };
  },

  async getWorkspaceNotifications(params): Promise<WorkspaceNotificationsResponse> {
    await wait(100);
    const source = params.unreadOnly ? workspaceNotifications.filter((item) => item.unread) : workspaceNotifications;
    const page = params.page ?? 1;
    const pageSize = params.pageSize ?? source.length;
    const startIndex = (page - 1) * pageSize;

    return {
      items: source.slice(startIndex, startIndex + pageSize),
      total: source.length,
      page,
      pageSize,
      unreadCount: workspaceNotifications.filter((item) => item.unread).length,
    };
  },

  async markAllWorkspaceNotificationsRead(params): Promise<{ unreadCount: number }> {
    void params;
    await wait(100);
    workspaceNotifications = workspaceNotifications.map((item) => ({ ...item, unread: false }));
    return { unreadCount: 0 };
  },

  async getRoadmap(params): Promise<RoadmapResponse> {
    void params;
    await wait();

    return {
      today: "2026-05-20",
      milestones: [
        { id: "m1", name: "V1.5 版本发布", date: "2026-06-30", color: "#0C66E4" },
        { id: "m2", name: "用户认证 2.0", date: "2026-08-15", color: "#22A06B" },
        { id: "m3", name: "多组织支持上线", date: "2026-11-30", color: "#6554C0" },
      ],
      iterations: [
        { id: "sp12", projectName: "敏捷研发平台", name: "SP12 用户认证功能优化", status: "active", startDate: "2026-04-10", endDate: "2026-06-20", progress: 70, owner: "张晓明", color: "#0C66E4", labels: ["认证", "安全"] },
        { id: "sp13", projectName: "敏捷研发平台", name: "SP13 通知中心重构", status: "planning", startDate: "2026-06-21", endDate: "2026-08-20", progress: 0, owner: "李华", color: "#A5ADBA", labels: ["通知"] },
        { id: "mapp3", projectName: "移动端 App", name: "迭代3 移动端性能优化", status: "active", startDate: "2026-04-15", endDate: "2026-06-30", progress: 65, owner: "王芳", color: "#22A06B", labels: ["移动端"] },
        { id: "data2", projectName: "数据平台", name: "迭代2 数据集成", status: "active", startDate: "2026-04-08", endDate: "2026-06-15", progress: 40, owner: "陈静", color: "#FFAB00", labels: ["数据"] },
        { id: "infra1", projectName: "基础设施", name: "迭代1 基础设施升级", status: "done", startDate: "2026-02-20", endDate: "2026-04-30", progress: 100, owner: "刘强", color: "#A5ADBA", labels: ["基础设施"] },
      ],
    };
  },

  async getBoard(params: BoardRequest): Promise<BoardResponse> {
    await wait();
    const keyword = params.keyword?.trim().toLowerCase();
    const source = keyword ? boardIssues.filter((issue) => `${issue.key} ${issue.title}`.toLowerCase().includes(keyword)) : boardIssues;
    const original = boardIssues;
    boardIssues = source;
    const columns = boardColumns();
    boardIssues = original;

    return {
      columns,
      metrics: [
        { id: "total", label: "总问题数", value: 178, helper: "较昨日 ↑ 12", color: "#0C66E4" },
        { id: "todo", label: "待处理", value: 56, helper: "31%", color: "#6B7280" },
        { id: "doing", label: "进行中", value: 34, helper: "19%", color: "#0C66E4" },
        { id: "review", label: "评审中", value: 18, helper: "10%", color: "#FF9F1A" },
        { id: "done", label: "已完成", value: 42, helper: "24%", color: "#22A06B" },
        { id: "closed", label: "已关闭", value: 28, helper: "16%", color: "#9CA3AF" },
      ],
    };
  },

  async getBoardIssueDetail(params: BoardIssueDetailRequest): Promise<BoardIssueDetailResponse> {
    await wait(120);
    const issue = boardIssues.find((item) => item.id === params.issueId);

    if (!issue) {
      throw new Error(`Board issue ${params.issueId} not found`);
    }

    return {
      card: issue,
      fields: {
        projectName: "敏捷研发平台",
        sprintName: issue.status === "todo" ? "Backlog" : "Sprint 12",
        reporter: "张三",
        dueDate: "2026-05-28",
        watchers: issue.estimate ? issue.estimate + 2 : 3,
        comments: issue.estimate ?? 2,
        attachments: issue.status === "done" ? 1 : 2,
      },
      description: "优化全局导航的信息层级，提升使用密集页面时的定位效率。这里保留原型里的详情面板结构：字段区、子任务、标签、附件与活动区。",
      subtasks: [
        { id: `${issue.id}-sub-1`, key: `${issue.key}-1`, title: "登录页样式优化", done: issue.status === "done" || issue.status === "closed" },
        { id: `${issue.id}-sub-2`, key: `${issue.key}-2`, title: "接口字段联调", done: issue.status === "done" },
        { id: `${issue.id}-sub-3`, key: `${issue.key}-3`, title: "交互验收", done: false },
      ],
      activity: [
        { id: "act-1", actor: "张三", content: `创建了 ${issue.key}`, time: "05-20 10:30" },
        { id: "act-2", actor: issue.assignee?.name ?? "系统", content: "更新了状态和负责人", time: "05-20 11:15" },
        { id: "act-3", actor: "王五", content: "添加了附件", time: "05-20 14:20" },
      ],
    };
  },

  async moveBoardIssue(payload: MoveBoardIssueRequest): Promise<MoveBoardIssueResponse> {
    await wait(220);
    const target = boardIssues.find((issue) => issue.id === payload.issueId);

    if (!target) {
      throw new Error(`Board issue ${payload.issueId} not found`);
    }

    if (target.status === "closed") {
      return {
        issueId: payload.issueId,
        targetStatus: target.status,
        result: "error",
        message: "只读卡片不能拖拽",
      };
    }

    if (payload.targetStatus === "closed" && target.status !== "done") {
      return {
        issueId: payload.issueId,
        targetStatus: target.status,
        result: "error",
        message: "需要先完成后才能关闭",
      };
    }

    const moved = { ...target, status: payload.targetStatus };
    const remaining = boardIssues.filter((issue) => issue.id !== payload.issueId);
    const beforeTarget = remaining.filter((issue) => issue.status !== payload.targetStatus);
    const targetColumn = remaining.filter((issue) => issue.status === payload.targetStatus);
    targetColumn.splice(Math.max(payload.targetIndex, 0), 0, moved);
    boardIssues = [...beforeTarget, ...targetColumn];
    const column = boardColumns().find((item) => item.id === payload.targetStatus);
    const wipWarning = column?.wipLimit && column.count > column.wipLimit
      ? {
          columnId: column.id,
          columnTitle: column.title,
          currentCount: column.count,
          wipLimit: column.wipLimit,
          exceededBy: column.count - column.wipLimit,
          severity: column.count - column.wipLimit > 1 ? "exceeded" as const : "near-limit" as const,
          message: `"${column.title}" 列已达到 WIP 限制 (${column.count}/${column.wipLimit})`,
          suggestions: ["优先移出阻塞的卡片", "拆分卡片到更小的任务", "调整 WIP 限制需要项目管理员权限"],
        }
      : undefined;

    return {
      issueId: payload.issueId,
      targetStatus: payload.targetStatus,
      result: wipWarning ? "warning" : payload.sourceStatus === payload.targetStatus ? "warning" : "success",
      message: wipWarning?.message ?? (payload.sourceStatus === payload.targetStatus ? "已在当前列中调整位置" : "问题状态已更新"),
      wipWarning,
    };
  },

  async getBoardColumnSettings(params: BoardColumnSettingsRequest): Promise<BoardColumnSettingsResponse> {
    await wait(100);
    const column = boardColumnSettings[params.columnId];

    return {
      column,
      colorOptions: ["#DE350B", "#FF5630", "#FFAB00", "#36B37E", "#00B8D9", "#0052CC", "#6554C0", "#FF8B00", "#C1C7D0"],
      statusOptions: [
        { id: "todo", label: "待处理（起始状态）", kind: "start" },
        { id: "in-progress", label: "进行中（中间状态）", kind: "middle" },
        { id: "review", label: "评审中（中间状态）", kind: "middle" },
        { id: "done", label: "已完成（结束状态）", kind: "end" },
        { id: "closed", label: "已关闭（结束状态）", kind: "end" },
      ],
      automationRules: [
        { id: "rule-1", name: "进入本列时自动分配负责人", enabled: true, trigger: "状态进入" },
        { id: "rule-2", name: "离开本列时同步通知关注人", enabled: true, trigger: "状态离开" },
      ],
    };
  },

  async updateBoardColumnSettings(payload: UpdateBoardColumnSettingsRequest): Promise<UpdateBoardColumnSettingsResponse> {
    await wait(180);
    const previous = boardColumnSettings[payload.columnId];
    const next = {
      ...previous,
      name: payload.name.trim(),
      color: payload.color,
      statusKey: payload.statusKey,
      wipEnabled: payload.wipEnabled,
      wipLimit: payload.wipEnabled ? payload.wipLimit : undefined,
      description: payload.description,
    };
    boardColumnSettings = { ...boardColumnSettings, [payload.columnId]: next };

    return {
      column: next,
      result: "success",
      message: `列"${next.name}"已保存`,
    };
  },

  async getBoardSprints(params: BoardSprintsRequest): Promise<BoardSprintsResponse> {
    await wait(100);
    const scoped = sprints.filter((sprint) => sprint.projectKey === params.projectKey && (params.includeCompleted || sprint.status !== "completed"));

    return {
      sprints: scoped,
      activeSprintId: scoped.find((sprint) => sprint.status === "active")?.id,
    };
  },

  async createSprint(payload: CreateSprintRequest): Promise<CreateSprintResponse> {
    await wait(220);
    const sprint: BoardSprint = {
      id: `sp-${Date.now()}`,
      name: payload.name.trim(),
      projectKey: payload.targetProjectKey,
      goal: payload.goal?.trim() || "本次迭代目标待补充",
      status: payload.autoStart ? "active" : "planning",
      startDate: payload.startDate,
      endDate: payload.endDate,
      durationDays: sprintDurationDays(payload.startDate, payload.endDate),
      capacity: payload.capacity,
      wipLimit: payload.wipLimit,
      maxParallelIssues: payload.maxParallelIssues,
      issueCount: 0,
      completedIssueCount: 0,
      remainingIssueCount: 0,
      members: payload.memberIds.map((id, index) => ({ id, name: ["张三", "李华", "王芳", "陈静"][index] ?? `成员${index + 1}`, role: index === 0 ? "Scrum Master" : "成员", avatar: avatar(id) })),
      tags: payload.tags,
    };
    if (payload.autoStart) {
      sprints = sprints.map((item) => item.status === "active" ? { ...item, status: "completed" } : item);
    }
    sprints = [sprint, ...sprints];

    return {
      sprint,
      result: "success",
      message: `${sprint.name} 已创建${payload.autoStart ? "并启动" : ""}`,
    };
  },

  async getSprintStartPreview(params: SprintStartPreviewRequest): Promise<SprintStartPreviewResponse> {
    await wait(120);
    const sprint = findSprint(params.sprintId);
    const checks = startChecks(sprint);
    const riskLevel = riskFromChecks(checks);

    return {
      sprint,
      canStart: riskLevel !== "blocked",
      riskLevel,
      checks,
    };
  },

  async startSprint(payload: StartSprintRequest): Promise<StartSprintResponse> {
    await wait(220);
    const sprint = findSprint(payload.sprintId);
    const checks = startChecks(sprint);
    const riskLevel = riskFromChecks(checks);

    if (riskLevel === "blocked" || (riskLevel === "warning" && !payload.confirmWarnings)) {
      return {
        sprint,
        result: riskLevel === "blocked" ? "error" : "warning",
        message: riskLevel === "blocked" ? "启动检查未通过，请处理阻塞项后重试" : "存在风险项，请确认后启动",
        checks,
      };
    }

    const updated = { ...sprint, status: "active" as const };
    sprints = sprints.map((item) => {
      if (item.id === payload.sprintId) {
        return updated;
      }
      return item.status === "active" ? { ...item, status: "completed" as const } : item;
    });

    return {
      sprint: updated,
      result: "success",
      message: `${updated.name} 已启动成功`,
      checks,
    };
  },

  async getSprintClosePreview(params: SprintClosePreviewRequest): Promise<SprintClosePreviewResponse> {
    await wait(120);
    const sprint = findSprint(params.sprintId);
    const checks = closeChecks(sprint);
    const riskLevel = riskFromChecks(checks);

    return {
      sprint,
      canClose: riskLevel !== "blocked",
      riskLevel,
      summary: {
        totalIssues: sprint.issueCount,
        completedIssues: sprint.completedIssueCount,
        inProgressIssues: Math.max(sprint.remainingIssueCount - 2, 0),
        todoIssues: Math.min(sprint.remainingIssueCount, 2),
        blockedIssues: sprint.remainingIssueCount > 6 ? 1 : 0,
      },
      checks,
    };
  },

  async closeSprint(payload: CloseSprintRequest): Promise<CloseSprintResponse> {
    await wait(240);
    const sprint = findSprint(payload.sprintId);
    const checks = closeChecks(sprint);
    if (riskFromChecks(checks) === "blocked") {
      return {
        sprint,
        result: "error",
        message: "存在阻塞项，暂不能结束 Sprint",
      };
    }

    const closed = { ...sprint, status: "completed" as const, completedIssueCount: sprint.issueCount, remainingIssueCount: 0 };
    let nextSprint: BoardSprint | undefined;

    if (payload.createNextSprint) {
      nextSprint = {
        ...sprint,
        id: `sp-${Date.now()}`,
        name: payload.nextSprintName?.trim() || "Sprint 14",
        status: "planning",
        startDate: payload.nextSprintStartDate || sprint.endDate,
        endDate: payload.nextSprintStartDate || sprint.endDate,
        durationDays: 14,
        issueCount: payload.incompleteStrategy === "move_to_next_sprint" ? sprint.remainingIssueCount : 0,
        completedIssueCount: 0,
        remainingIssueCount: payload.incompleteStrategy === "move_to_next_sprint" ? sprint.remainingIssueCount : 0,
      };
    }

    sprints = sprints.map((item) => item.id === payload.sprintId ? closed : item);
    if (nextSprint) {
      sprints = [nextSprint, ...sprints];
    }

    return {
      sprint: closed,
      nextSprint,
      result: "success",
      message: `${closed.name} 已结束${nextSprint ? `，未完成项已进入 ${nextSprint.name}` : ""}`,
    };
  },

  async getReportFilterOptions(params: ReportFilterOptionsRequest): Promise<ReportFilterOptionsResponse> {
    await wait(90);

    return {
      projects: [
        { id: "alpha-platform", label: "敏捷研发平台" },
        { id: "mobile-app", label: "移动端 App" },
        { id: "data-platform", label: "数据平台", disabled: true, helper: "无报表权限" },
      ],
      sprints: [
        { id: "sp-13", label: "Sprint 13", helper: "2026-05-20 至 2026-06-06" },
        { id: "sp-12", label: "Sprint 12", helper: "已完成" },
        { id: "all", label: "全部迭代" },
      ],
      statuses: [
        { id: "todo", label: "未开始" },
        { id: "in-progress", label: "进行中" },
        { id: "review", label: "已评审" },
        { id: "done", label: "已完成" },
        { id: "closed", label: "已关闭" },
      ],
      priorities: [
        { id: "highest", label: "最高" },
        { id: "high", label: "高" },
        { id: "medium", label: "中" },
        { id: "low", label: "低" },
        { id: "lowest", label: "最低" },
      ],
      assignees: [
        { id: "u-1", label: "张晓明", avatar: avatar("张晓明") },
        { id: "u-2", label: "李华", avatar: avatar("李华") },
        { id: "u-3", label: "王芳", avatar: avatar("王芳") },
        { id: "u-4", label: "陈静", avatar: avatar("陈静") },
      ],
      labels: [
        { id: "登录模块", label: "登录模块" },
        { id: "权限管理", label: "权限管理" },
        { id: "缺陷", label: "缺陷" },
        { id: "质量周", label: "质量周" },
        { id: "__empty__", label: "无数据演示" },
      ],
      customFields: [
        { id: "source", label: "来源", options: [{ id: "jira", label: "Jira 同步" }, { id: "manual", label: "手动录入" }] },
        { id: "severity", label: "严重等级", options: [{ id: "all", label: "全部" }, { id: "critical", label: "严重" }, { id: "major", label: "主要" }] },
      ],
      savedFilters: params.includeSavedFilters === false ? [] : reportSavedFilters,
      quickFilters: reportQuickFilters,
    };
  },

  async getReports(params: ReportsRequest): Promise<ReportsResponse> {
    await wait();
    return reportPreview(reportFilters(params));
  },

  async getReportDrilldown(params: ReportDrilldownRequest): Promise<ReportDrilldownResponse> {
    await wait(160);
    const rows = drilldownRows(params);
    const start = (params.page - 1) * params.pageSize;
    const scopedRows = rows.slice(start, start + params.pageSize);

    return {
      title: params.dimension === "issueType" ? `${params.dimensionValue} 类型明细` : `${params.dimensionValue} 下钻明细`,
      breadcrumb: ["报表", "问题趋势", params.dimensionValue],
      summary: [
        { id: "created", label: "新建", value: String(rows.length + 12), tone: "red" },
        { id: "running", label: "进行中", value: String(rows.filter((row) => row.status === "in-progress").length), tone: "blue" },
        { id: "resolved", label: "已解决", value: String(rows.filter((row) => row.status === "done").length), tone: "green" },
        { id: "closed", label: "已关闭", value: String(rows.filter((row) => row.status === "closed").length), tone: "neutral" },
      ],
      rows: scopedRows,
      total: rows.length,
      page: params.page,
      pageSize: params.pageSize,
      filters: params.filters,
    };
  },

  async exportReport(payload: ExportReportRequest): Promise<ExportReportResponse> {
    await wait(300);

    if (!payload.fileName.trim()) {
      return {
        jobId: "export-invalid-name",
        status: "failed",
        message: "文件名称不能为空",
        fileName: payload.fileName,
        estimatedSeconds: 0,
      };
    }

    return {
      jobId: `export-${Date.now()}`,
      status: "queued",
      message: `${payload.fileName}.${payload.format} 已加入导出队列`,
      fileName: `${payload.fileName}.${payload.format}`,
      estimatedSeconds: payload.scope === "all_results" ? 18 : 8,
    };
  },

  async getReportConfig(params: ReportConfigRequest): Promise<ReportConfigResponse> {
    await wait(120);
    const filters = reportFilters();

    return {
      report: {
        id: params.reportId,
        name: params.reportId ? "缺陷质量分析" : "未命名报表",
        visibility: "team",
        dataSource: "jira-issues",
        chartType: "bar",
        dimensions: ["status", "issueType"],
        metrics: ["created", "resolved"],
        filters,
      },
      dataSources: [
        { id: "jira-issues", label: "Jira 项目问题" },
        { id: "jira-sprints", label: "Jira Sprint 数据" },
        { id: "custom-sql", label: "自定义数据集", disabled: true, helper: "需要管理员权限" },
      ],
      dimensions: [
        { id: "status", label: "状态" },
        { id: "issueType", label: "问题类型" },
        { id: "priority", label: "优先级" },
        { id: "assignee", label: "负责人" },
      ],
      metrics: [
        { id: "created", label: "新建数" },
        { id: "resolved", label: "已解决数" },
        { id: "resolutionRate", label: "完成率" },
        { id: "avgCycleDays", label: "平均处理时长" },
      ],
      chartTypes: [
        { id: "bar", label: "柱状图" },
        { id: "line", label: "折线图" },
        { id: "donut", label: "环形图" },
        { id: "table", label: "表格" },
        { id: "number", label: "数字卡片" },
      ],
      preview: reportPreview(filters),
    };
  },

  async saveReportConfig(payload: SaveReportConfigRequest): Promise<SaveReportConfigResponse> {
    await wait(220);

    return {
      reportId: payload.report.id ?? `report-${Date.now()}`,
      message: payload.publish ? "报表已保存并发布" : "报表草稿已保存",
      savedAt: "2026-06-06 16:30",
    };
  },

  async getProjectSettings(params: ProjectSettingsRequest): Promise<ProjectSettingsResponse> {
    void params;
    await wait();

    return {
      project: {
        key: "ALP",
        name: "敏捷研发平台",
        category: "软件项目",
        lead: "张晓明",
        visibility: "team",
      },
      workflow: [
        { id: "todo", label: "待处理", color: "#6B7280", transitions: ["in-progress", "closed"] },
        { id: "in-progress", label: "进行中", color: "#0C66E4", transitions: ["review", "todo"] },
        { id: "review", label: "评审中", color: "#FF9F1A", transitions: ["done", "in-progress"] },
        { id: "done", label: "已完成", color: "#22A06B", transitions: ["closed"] },
        { id: "closed", label: "已关闭", color: "#9CA3AF", transitions: [] },
      ],
      roles: [
        { id: "admin", name: "项目管理员", members: 3, permissions: ["管理项目", "管理成员", "删除问题"] },
        { id: "developer", name: "开发成员", members: 18, permissions: ["创建问题", "更新状态", "提交评论"] },
        { id: "viewer", name: "访客", members: 6, permissions: ["查看项目", "查看报表"] },
      ],
      automations: [
        { id: "auto-1", name: "缺陷创建后通知负责人", enabled: true, trigger: "问题创建" },
        { id: "auto-2", name: "完成后自动同步报告", enabled: true, trigger: "状态变更" },
        { id: "auto-3", name: "逾期问题每日提醒", enabled: false, trigger: "定时任务" },
      ],
    };
  },

  async getComponentGallery(params: ComponentGalleryRequest): Promise<ComponentGalleryResponse> {
    await wait(80);

    return {
      tableRows: boardIssues.slice(0, 5),
      badgeSamples: [
        { id: "status-todo", label: "待处理", kind: "status", tone: "neutral" },
        { id: "status-doing", label: "进行中", kind: "status", tone: "blue" },
        { id: "status-review", label: "评审中", kind: "status", tone: "orange" },
        { id: "priority-high", label: "高", kind: "priority", tone: "red" },
        { id: "type-bug", label: "缺陷", kind: "type", tone: "red" },
        { id: "tag-api", label: "接口", kind: "tag", tone: "green" },
      ],
      feedbackStates: componentFeedbackStates,
      dangerActions,
      apiSpec: params.includeApiSpec
        ? {
            endpoint: "/api/project-workspace/component-gallery",
            method: "GET",
            requestParams: params,
            responseShape: "ComponentGalleryResponse",
            mockLatencyMs: 80,
          }
        : undefined,
    };
  },
};
