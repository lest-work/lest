import type {
  AddIssueCommentRequest,
  AddIssueCommentResponse,
  CreateIssueRequest,
  CreateIssueResponse,
  DashboardActivity,
  DashboardFilterOptionsResponse,
  DashboardIssue,
  DashboardNotificationsRequest,
  DashboardNotificationsResponse,
  DashboardOverviewRequest,
  DashboardOverviewResponse,
  DashboardSearchRequest,
  DashboardSearchResponse,
  DashboardUser,
  DashboardUserMenuResponse,
  DateRangePreset,
  GetDashboardIssueDetailRequest,
  GetDashboardIssueDetailResponse,
  IssueComment,
  UpdateIssueStatusRequest,
  UpdateIssueStatusResponse,
} from "./contracts";
import type { DashboardService } from "./service";

const statusLabel = {
  todo: "未开始",
  "in-progress": "进行中",
  review: "已评审",
  done: "已完成",
  closed: "已关闭",
} as const;


const currentUser: DashboardUser = {
  id: "u-current",
  name: "张晓明",
  role: "项目负责人",
  email: "zhangxiaoming@example.com",
  avatar: "https://api.dicebear.com/9.x/lorelei/svg?seed=Zhang",
};

const users: DashboardUser[] = [
  currentUser,
  {
    id: "u2",
    name: "李华",
    role: "前端工程师",
    email: "lihua@example.com",
    avatar: "https://api.dicebear.com/9.x/lorelei/svg?seed=Li",
  },
  {
    id: "u3",
    name: "王芳",
    role: "后端工程师",
    email: "wangfang@example.com",
    avatar: "https://api.dicebear.com/9.x/lorelei/svg?seed=Wang",
  },
  {
    id: "u4",
    name: "陈静",
    role: "测试工程师",
    email: "chenjing@example.com",
    avatar: "https://api.dicebear.com/9.x/lorelei/svg?seed=Chen",
  },
  {
    id: "u5",
    name: "刘强",
    role: "DevOps 工程师",
    email: "liuqiang@example.com",
    avatar: "https://api.dicebear.com/9.x/lorelei/svg?seed=Liu",
  },
];

let issues: DashboardIssue[] = [
  {
    id: "issue-alp-123",
    key: "ALP-123",
    title: "添加用户登录认证功能",
    projectKey: "alpha-platform",
    type: "story",
    status: "in-progress",
    priority: "high",
    assignee: users[0],
    reporter: users[1],
    labels: ["登录模块", "安全"],
    description: "为 Web 端和管理端补齐统一登录、Token 刷新、异常退出和登录日志能力。",
    dueDate: "2026-06-18",
    createdAt: "2026-06-01",
    updatedAt: "2 小时前",
    estimate: 8,
    attachmentsCount: 2,
    linkedIssuesCount: 3,
  },
  {
    id: "issue-alp-124",
    key: "ALP-124",
    title: "修复导航栏显示异常",
    projectKey: "alpha-platform",
    type: "bug",
    status: "todo",
    priority: "medium",
    assignee: users[1],
    reporter: users[0],
    labels: ["导航", "UI"],
    description: "处理顶部导航在宽屏下搜索框挤压菜单、用户菜单错位的问题。",
    dueDate: "2026-06-12",
    createdAt: "2026-06-02",
    updatedAt: "4 小时前",
    estimate: 3,
    attachmentsCount: 1,
    linkedIssuesCount: 1,
  },
  {
    id: "issue-alp-125",
    key: "ALP-125",
    title: "优化仪表盘性能",
    projectKey: "alpha-platform",
    type: "task",
    status: "review",
    priority: "high",
    assignee: users[2],
    reporter: users[0],
    labels: ["仪表盘", "性能优化"],
    description: "优化首页图表数据聚合和活动流查询，减少初屏等待时间。",
    dueDate: "2026-06-20",
    createdAt: "2026-06-03",
    updatedAt: "1 天前",
    estimate: 5,
    attachmentsCount: 0,
    linkedIssuesCount: 2,
  },
  {
    id: "issue-alp-126",
    key: "ALP-126",
    title: "更新依赖版本",
    projectKey: "alpha-platform",
    type: "task",
    status: "todo",
    priority: "low",
    assignee: users[3],
    reporter: users[2],
    labels: ["工程化"],
    description: "升级基础依赖并回归构建、Lint 和主要页面渲染。",
    dueDate: "2026-06-22",
    createdAt: "2026-06-04",
    updatedAt: "1 天前",
    estimate: 2,
    attachmentsCount: 0,
    linkedIssuesCount: 0,
  },
  {
    id: "issue-alp-127",
    key: "ALP-127",
    title: "添加接口单元测试",
    projectKey: "alpha-platform",
    type: "task",
    status: "done",
    priority: "medium",
    assignee: users[4],
    reporter: users[0],
    labels: ["测试", "接口"],
    description: "为项目、问题、评论和通知接口补齐基础单元测试。",
    dueDate: "2026-06-24",
    createdAt: "2026-06-05",
    updatedAt: "2 天前",
    estimate: 4,
    attachmentsCount: 2,
    linkedIssuesCount: 1,
  },
];

let commentsByIssueId: Record<string, IssueComment[]> = {
  "issue-alp-123": [
    {
      id: "comment-1",
      author: users[1],
      content: "认证流程已经接入测试环境，待联调短信验证码异常重试。",
      createdAtText: "36 分钟前",
    },
    {
      id: "comment-2",
      author: users[0],
      content: "请同步补充登录失败和锁定策略的验收标准。",
      createdAtText: "12 分钟前",
    },
  ],
};

let activities: DashboardActivity[] = [
  {
    id: "activity-1",
    actor: users[0],
    verb: "将问题",
    issueId: "issue-alp-123",
    issueKey: "ALP-123",
    targetText: "的状态从“待处理”更新为“进行中”",
    createdAtText: "2 小时前",
  },
  {
    id: "activity-2",
    actor: users[1],
    verb: "评论了问题",
    issueId: "issue-alp-124",
    issueKey: "ALP-124",
    createdAtText: "4 小时前",
  },
  {
    id: "activity-3",
    actor: users[2],
    verb: "解决了问题",
    issueId: "issue-alp-118",
    issueKey: "ALP-118",
    createdAtText: "昨天",
  },
  {
    id: "activity-4",
    actor: users[3],
    verb: "创建了问题",
    issueId: "issue-alp-127",
    issueKey: "ALP-127",
    createdAtText: "昨天",
  },
  {
    id: "activity-5",
    actor: users[4],
    verb: "更新了问题",
    issueId: "issue-alp-125",
    issueKey: "ALP-125",
    targetText: "的优先级为“高”",
    createdAtText: "2 天前",
  },
];

let notifications = [
  {
    id: "notice-1",
    type: "comment" as const,
    title: "李华评论了 ALP-124",
    description: "导航栏错位在 1440 宽度下仍可复现。",
    createdAtText: "4 小时前",
    unread: true,
    actor: users[1],
  },
  {
    id: "notice-2",
    type: "assignment" as const,
    title: "你被指派到 ALP-123",
    description: "添加用户登录认证功能需要今天完成联调。",
    createdAtText: "今天 10:30",
    unread: true,
    actor: users[0],
  },
  {
    id: "notice-3",
    type: "status" as const,
    title: "ALP-125 进入评审",
    description: "王芳将仪表盘性能优化提交评审。",
    createdAtText: "昨天",
    unread: true,
    actor: users[2],
  },
  {
    id: "notice-4",
    type: "system" as const,
    title: "本周报表已生成",
    description: "项目健康度、问题趋势和负责人分布已经更新。",
    createdAtText: "2 天前",
    unread: false,
  },
];

const dateRanges = [
  { id: "today", label: "今天" },
  { id: "yesterday", label: "昨天" },
  { id: "last_7_days", label: "最近 7 天" },
  { id: "last_30_days", label: "最近 30 天", description: "默认范围" },
  { id: "last_90_days", label: "最近 90 天" },
  { id: "this_week", label: "本周" },
  { id: "last_week", label: "上周" },
  { id: "this_month", label: "本月" },
  { id: "last_month", label: "上月" },
  { id: "this_quarter", label: "本季度" },
  { id: "this_year", label: "今年" },
  { id: "last_year", label: "去年" },
  { id: "custom", label: "自定义日期范围" },
] satisfies Array<{ id: DateRangePreset; label: string; description?: string }>;

function wait(delay = 160) {
  return new Promise((resolve) => setTimeout(resolve, delay));
}

function getOverviewMetrics() {
  return [
    {
      id: "created-issues",
      label: "新建问题数",
      value: "145",
      change: 18,
      trend: "up" as const,
      compareText: "较前 30 天",
      chartType: "line" as const,
      color: "#0C66E4",
      chartValues: [24, 31, 34, 30, 26, 27, 34, 30, 39, 36, 42, 34, 35, 31, 32, 43, 38, 44, 41],
    },
    {
      id: "resolved-issues",
      label: "解决问题数",
      value: "123",
      change: 20,
      trend: "up" as const,
      compareText: "较前 30 天",
      chartType: "line" as const,
      color: "#0C9D8A",
      chartValues: [20, 29, 33, 24, 22, 24, 32, 30, 36, 34, 40, 35, 41, 33, 36, 34, 39, 33, 37],
    },
    {
      id: "unresolved-issues",
      label: "未解决问题数",
      value: "231",
      change: 5,
      trend: "down" as const,
      compareText: "较前 30 天",
      chartType: "line" as const,
      color: "#0C66E4",
      chartValues: [26, 30, 31, 34, 30, 35, 33, 38, 36, 43, 40, 46, 42, 47, 42, 42, 39, 37, 42],
    },
    {
      id: "avg-resolution-time",
      label: "平均解决时间",
      value: "2.4 天",
      change: 8,
      trend: "down" as const,
      compareText: "较前 30 天",
      chartType: "bar" as const,
      color: "#0C66E4",
      chartValues: [48, 64, 18, 42, 12, 36, 58, 8, 24, 14, 54, 22, 30, 70, 10, 6, 12],
    },
  ];
}

function applyIssueFilter(params: DashboardOverviewRequest) {
  const filtered = issues.filter((issue) => {
    if (issue.projectKey !== params.projectKey) {
      return false;
    }

    switch (params.issueFilterId) {
      case "unresolved":
        return issue.status !== "done" && issue.status !== "closed";
      case "assigned_to_me":
        return issue.assignee?.id === currentUser.id;
      case "created_by_me":
        return issue.reporter.id === currentUser.id;
      case "high_priority":
        return issue.priority === "highest" || issue.priority === "high";
      case "done":
        return issue.status === "done" || issue.status === "closed";
      default:
        return true;
    }
  });

  return filtered;
}

function createStatusDistribution() {
  return [
    { id: "todo", label: "待处理", value: 85, percentage: 37, color: "#0C66E4" },
    { id: "in-progress", label: "进行中", value: 63, percentage: 27, color: "#1F9D83" },
    { id: "review", label: "已评审", value: 36, percentage: 16, color: "#FFAB00" },
    { id: "done", label: "已完成", value: 47, percentage: 20, color: "#19A884" },
  ];
}

function createPriorityDistribution() {
  return [
    { id: "highest", label: "最高", value: 76, percentage: 30, color: "#FF3B30" },
    { id: "high", label: "高", value: 50, percentage: 20, color: "#FFAB00" },
    { id: "medium", label: "中", value: 71, percentage: 28, color: "#0C66E4" },
    { id: "low", label: "低", value: 35, percentage: 14, color: "#1F9D83" },
    { id: "lowest", label: "最低", value: 18, percentage: 8, color: "#C1C7D0" },
  ];
}

function createAssigneeDistribution() {
  return [
    { id: users[0].id, name: users[0].name, avatar: users[0].avatar, value: 57, color: "#0C66E4" },
    { id: users[1].id, name: users[1].name, avatar: users[1].avatar, value: 45, color: "#0C66E4" },
    { id: users[2].id, name: users[2].name, avatar: users[2].avatar, value: 38, color: "#0C66E4" },
    { id: users[3].id, name: users[3].name, avatar: users[3].avatar, value: 29, color: "#0C66E4" },
    { id: users[4].id, name: users[4].name, avatar: users[4].avatar, value: 22, color: "#0C66E4" },
    { id: "unassigned", name: "未分配", value: 40, color: "#A5ADBA" },
  ];
}

function createActivity(actor: DashboardUser, verb: string, issue: DashboardIssue, targetText?: string): DashboardActivity {
  return {
    id: `activity-${Date.now()}`,
    actor,
    verb,
    issueId: issue.id,
    issueKey: issue.key,
    targetText,
    createdAtText: "刚刚",
  };
}

export const mockDashboardService: DashboardService = {
  async getOverview(params): Promise<DashboardOverviewResponse> {
    await wait();
    const filteredIssues = applyIssueFilter(params);

    return {
      metrics: getOverviewMetrics(),
      statusDistribution: createStatusDistribution(),
      priorityDistribution: createPriorityDistribution(),
      assigneeDistribution: createAssigneeDistribution(),
      recentIssues: filteredIssues.slice(0, 5),
      activities: activities.slice(0, 5),
    };
  },

  async getFilterOptions(): Promise<DashboardFilterOptionsResponse> {
    await wait(80);

    return {
      dateRanges,
      issueFilters: [
        { id: "all", label: "所有问题", count: 231 },
        { id: "unresolved", label: "未解决问题", count: 128 },
        { id: "assigned_to_me", label: "我负责的问题", count: 57 },
        { id: "created_by_me", label: "我创建的问题", count: 24 },
        { id: "high_priority", label: "高优先级问题", count: 76 },
        { id: "done", label: "已完成问题", count: 47 },
      ],
    };
  },

  async search(params): Promise<DashboardSearchResponse> {
    await wait(120);
    const keyword = params.keyword.trim().toLowerCase();
    const limit = params.limit ?? 8;
    const issueResults = issues
      .filter((issue) => !keyword || `${issue.key} ${issue.title} ${issue.labels.join(" ")}`.toLowerCase().includes(keyword))
      .map((issue) => ({
        id: issue.id,
        type: "issue" as const,
        title: `${issue.key} ${issue.title}`,
        description: issue.description,
        avatar: issue.assignee?.avatar,
        meta: issue.status,
      }));
    const memberResults = users
      .filter((user) => !keyword || `${user.name} ${user.role} ${user.email}`.toLowerCase().includes(keyword))
      .map((user) => ({
        id: user.id,
        type: "member" as const,
        title: user.name,
        description: user.role,
        avatar: user.avatar,
        meta: user.email,
      }));

    return {
      recentKeywords: ["登录认证", "仪表盘", "导航栏"],
      recentIssues: issueResults.slice(0, 3),
      results: [
        ...issueResults,
        { id: "project-alpha", type: "project" as const, title: "敏捷研发平台", description: "软件项目", meta: "alpha-platform" },
        ...memberResults,
      ].slice(0, limit),
    };
  },

  async getNotifications(params): Promise<DashboardNotificationsResponse> {
    await wait(100);
    const source = params?.unreadOnly ? notifications.filter((item) => item.unread) : notifications;
    const page = params?.page ?? 1;
    const pageSize = params?.pageSize ?? source.length;
    const startIndex = (page - 1) * pageSize;

    return {
      items: source.slice(startIndex, startIndex + pageSize),
      total: source.length,
      page,
      pageSize,
      unreadCount: notifications.filter((item) => item.unread).length,
    };
  },

  async getUserMenu(): Promise<DashboardUserMenuResponse> {
    await wait(80);

    return {
      user: currentUser,
      workspace: {
        id: "workspace-alpha",
        name: "Alpha 产品研发",
      },
      theme: "light",
      locale: "zh-CN",
    };
  },

  async createIssue(payload): Promise<CreateIssueResponse> {
    await wait(360);
    const assignee = users.find((user) => user.id === payload.assigneeId);
    const nextNumber = 128 + issues.length;
    const issue: DashboardIssue = {
      id: `issue-alp-${nextNumber}`,
      key: `ALP-${nextNumber}`,
      title: payload.title,
      projectKey: payload.projectKey,
      type: payload.type,
      status: "todo",
      priority: payload.priority,
      assignee,
      reporter: currentUser,
      labels: [],
      description: payload.description ?? "暂无描述",
      dueDate: payload.dueDate,
      createdAt: "2026-06-06",
      updatedAt: "刚刚",
      estimate: 0,
      attachmentsCount: payload.attachmentIds?.length ?? 0,
      linkedIssuesCount: 0,
    };
    issues = [issue, ...issues];
    activities = [createActivity(currentUser, "创建了问题", issue), ...activities];

    return {
      issue,
      message: `已创建问题 ${issue.key}`,
    };
  },

  async getIssueDetail({ issueId }): Promise<GetDashboardIssueDetailResponse> {
    await wait(120);
    const issue = issues.find((item) => item.id === issueId);

    if (!issue) {
      throw new Error(`Issue ${issueId} not found`);
    }

    return {
      issue,
      comments: commentsByIssueId[issueId] ?? [],
      activities: activities.filter((item) => item.issueId === issueId).slice(0, 6),
    };
  },

  async updateIssueStatus({ issueId, status }: UpdateIssueStatusRequest): Promise<UpdateIssueStatusResponse> {
    await wait(180);
    const issue = issues.find((item) => item.id === issueId);

    if (!issue) {
      throw new Error(`Issue ${issueId} not found`);
    }

    issue.status = status;
    issue.updatedAt = "刚刚";
    const activity = createActivity(currentUser, "更新了问题", issue, `的状态为“${statusLabel[status]}”`);
    activities = [activity, ...activities];

    return { issue, activity };
  },

  async addIssueComment({ issueId, content }: AddIssueCommentRequest): Promise<AddIssueCommentResponse> {
    await wait(180);
    const issue = issues.find((item) => item.id === issueId);

    if (!issue) {
      throw new Error(`Issue ${issueId} not found`);
    }

    const comment = {
      id: `comment-${Date.now()}`,
      author: currentUser,
      content,
      createdAtText: "刚刚",
    };
    commentsByIssueId = {
      ...commentsByIssueId,
      [issueId]: [comment, ...(commentsByIssueId[issueId] ?? [])],
    };
    const activity = createActivity(currentUser, "评论了问题", issue);
    activities = [activity, ...activities];

    return { comment, activity };
  },

  async markAllNotificationsRead(): Promise<{ unreadCount: number }> {
    await wait(100);
    notifications = notifications.map((item) => ({ ...item, unread: false }));

    return { unreadCount: 0 };
  },
};
