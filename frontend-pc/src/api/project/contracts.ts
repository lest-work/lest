export interface WorkspaceUser {
  id?: string;
  name: string;
  role?: string;
  email?: string;
  avatar?: string;
}

export interface WorkspaceNotification {
  id: string;
  type: "comment" | "assignment" | "status" | "attachment" | "system" | "mention";
  title: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  description?: string;
  createdAtText: string;
  unread: boolean;
  actor?: WorkspaceUser;
}

export interface BoardIssueCard {
  id: string;
  key: string;
  title: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  projectKey?: string;
  type: string;
  status: string;
  priority: string;
  assignee?: WorkspaceUser;
  reporter?: WorkspaceUser;
  labels: string[];
  description?: string;
  dueDate?: string;
  updatedAt?: string;
  createdAt?: string;
  estimate?: number;
  attachmentsCount?: number;
  commentsCount?: number;
  linkedIssuesCount?: number;
}

export interface BoardMetric {
  id: string;
  label: string;
  value: number;
  color: string;
  helper: string;
}

export interface BoardColumn {
  id: string;
  title: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  color: string;
  wipLimit?: number;
  message?: string;
  description?: string;
  editable: boolean;
  count?: number;
  issues?: BoardIssueCard[];
}

export interface BoardSprint {
  id: string;
  name: string;
  projectKey?: string;
  goal: string;
  status: "planning" | "active" | "completed" | "closed";
  startDate?: string;
  endDate?: string;
  durationDays: number;
  capacity: number;
  wipLimit: number;
  maxParallelIssues: number;
  issueCount: number;
  completedIssueCount: number;
  remainingIssueCount: number;
  tags?: string[];
  members?: Array<{ id: string; name: string; role?: string; avatar?: string }>;
}

export interface BoardWipWarning {
  columnId: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  current: number;
  limit: number;
  currentCount?: number;
  wipLimit?: number;
  message?: string;
  exceededBy?: number;
  suggestions?: string[];
}

export interface SprintCheckItem {
  id: string;
  label: string;
  status: "pass" | "warn" | "fail" | "passed" | "warning" | "failed" | "info";
  detail: string;
  description?: string;
}

export interface ComponentFeedbackState {
  id: string;
  label: string;
  permissionKey?: string;
  traceId?: string;
  code?: string;
  scope?: string;
  shape?: string;
  rowCount?: number;
  roleSuggestion?: string;
  title?: string;
  severity?: string;
  description?: string;
  actionLabel?: string;
  secondaryActionLabel?: string;
  tone: "success" | "warning" | "error" | "info" | "neutral";
  actions?: string[];
}

export interface DangerActionSpec {
  id: string;
  title: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  description?: string;
  actionLabel: string;
  cancelLabel: string;
  severity: string;
  confirmationMode: "default" | "checkbox" | "type-text" | "type-to-confirm" | "countdown";
  confirmationText?: string;
  affectedSummary?: Array<{ label: string; value: string }>;
}

// Request types
export interface WorkspaceRequest {
  projectKey?: string;
  chartId?: string;
  sortBy?: string;
  metricId?: string;
}

export interface WorkspaceShellRequest extends WorkspaceRequest {
  includeNotifications?: boolean;
  notificationPageSize?: number;
}
export interface WorkspaceSearchRequest {
  projectKey?: string;
  keyword: string;
  type?: string;
  limit?: number;
}
export interface WorkspaceNotificationsRequest extends WorkspaceRequest {
  page?: number;
  pageSize?: number;
  unreadOnly?: boolean;
}

export interface BoardRequest extends WorkspaceRequest {
  sprintId?: string;
  groupBy?: string;
  keyword?: string;
}
export interface BoardIssueDetailRequest {
  issueId: string;
  projectKey?: string;
}
export interface MoveBoardIssueRequest {
  issueId: string;
  targetStatus: string;
  targetIndex?: number;
  sprintId?: string;
  sourceStatus?: string;
  projectKey?: string;
}
export interface BoardColumnSettingsRequest {
  projectKey?: string;
  columnId: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
}
export interface UpdateBoardColumnSettingsRequest {
  projectKey: string;
  columnId: string;
  name?: string;
  color?: string;
  statusKey?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  wipLimit?: number;
  message?: string;
  description?: string;
}
export interface BoardSprintsRequest extends WorkspaceRequest {
  includeCompleted?: boolean;
}

export interface CreateSprintRequest {
  projectKey?: string;
  name: string;
  type?: string;
  goal?: string;
  startDate?: string;
  endDate?: string;
  autoStart?: boolean;
  targetProjectKey?: string;
  capacity?: number;
  wipLimit?: number;
  message?: string;
  maxParallelIssues?: number;
  memberIds?: string[];
  tags?: string[];
}
export interface StartSprintRequest {
  sprintId: string;
  projectKey?: string;
  confirmWarnings?: boolean;
}
export interface CloseSprintRequest {
  sprintId: string;
  projectKey?: string;
  moveIncompleteIssuesTo?: string;
}
export interface SprintStartPreviewRequest {
  sprintId: string;
  projectKey?: string;
  chartId?: string;
  sortBy?: string;
  metricId?: string;
}
export interface SprintClosePreviewRequest {
  sprintId: string;
  projectKey?: string;
  chartId?: string;
  sortBy?: string;
  metricId?: string;
}

export interface ReportFilterState {
  projectKey?: string;
  sprintId?: string;
  dateRange?: { from?: string; to?: string };
  includeCharts?: boolean;
  includeDetails?: boolean;
  includeFilterDescription?: boolean;
  statuses: string[];
  priorities: string[];
  assigneeIds: string[];
  labels: string[];
  customFields: Record<string, string>;
}

export interface ReportsRequest extends WorkspaceRequest {
  filters: ReportFilterState;
  dimension?: string;
  reportType?: string;
  datePreset?: string;
  compareWithPrevious?: boolean;
}
export interface ReportFilterOptionsRequest extends WorkspaceRequest {
  includeSavedFilters?: boolean;
}
export interface ReportDrilldownRequest {
  dimension: string;
  dimensionValue: string;
  filters: ReportFilterState;
  page: number;
  pageSize: number;
  projectKey?: string;
  chartId?: string;
  sortBy?: string;
  metricId?: string;
  sortOrder?: string;
}
export interface ExportReportRequest {
  projectKey?: string;
  filters: ReportFilterState;
  format: "csv" | "xlsx" | "pdf" | "png" | "pptx";
  fileName: string;
  scope?: string;
  reportType?: string;
  dateRange?: { from?: string; to?: string };
  includeCharts?: boolean;
  includeDetails?: boolean;
  includeFilterDescription?: boolean;
}
export interface ReportConfigRequest {
  reportId?: string;
  projectKey?: string;
  chartId?: string;
  sortBy?: string;
  metricId?: string;
}
export interface SaveReportConfigRequest {
  report: {
    id?: string;
    name: string;
    description?: string;
    dataSource: string;
    chartType: string;
    dimensions: string[];
    metrics: string[];
    filters: ReportFilterState;
  };
  publish?: boolean;
  projectKey?: string;
  chartId?: string;
  sortBy?: string;
  metricId?: string;
};
export interface RoadmapRequest extends WorkspaceRequest {
  view?: string;
  from?: string;
  to?: string;
}
export interface ProjectSettingsRequest extends WorkspaceRequest {
  section?: string;
}
export interface ComponentGalleryRequest extends WorkspaceRequest {
  includeApiSpec?: boolean;
  groups?: string[];
  locale?: string;
}

// Response types
export interface WorkspaceShellResponse {
  project: { key: string; name: string; category: string };
  user: WorkspaceUser;
  workspace: { id: string; name: string };
  unreadNotificationCount: number;
  theme: string;
  locale: string;
  notifications: WorkspaceNotification[];
}

export interface WorkspaceSearchResponse {
  recentKeywords: string[];
  results: Array<{
    id: string;
    type: "issue" | "project" | "member" | "page";
    title: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
    description?: string;
    targetPageKey?: string;
    meta?: string;
    avatar?: string;
  }>;
}

export interface WorkspaceNotificationsResponse {
  items: WorkspaceNotification[];
  total: number;
  page: number;
  pageSize: number;
  unreadCount: number;
}

export interface RoadmapResponse {
  today: string;
  milestones: Array<{ id: string; name: string; date: string; color: string }>;
  iterations: Array<{
    id: string; projectName: string; name: string; status: string;
    startDate: string; endDate: string; progress: number;
    owner: string; color: string; labels: string[];
  }>;
}

export interface BoardResponse {
  columns: Array<BoardColumn & { issues: BoardIssueCard[] }>;
  metrics: BoardMetric[];
}

export interface BoardIssueDetailResponse {
  card: BoardIssueCard;
  fields: {
    projectName: string; sprintName: string; reporter: string;
    dueDate?: string; watchers: number; comments: number; attachments: number;
  };
  description?: string;
  subtasks: Array<{ id: string; key: string; title: string;
  name?: string;
  wipEnabled?: boolean; done: boolean }>;
  statusKey?: string;
  activity: Array<{ id: string; actor: string; content: string; time: string }>;
}

export interface MoveBoardIssueResponse {
  issueId: string;
  targetStatus: string;
  result: string;
  message: string;
  wipWarning?: BoardWipWarning;
}

export interface BoardColumnSettingsResponse {
  column: BoardColumn;
  colorOptions: string[];
  statusOptions: Array<{ id: string; label: string; kind: string }>;
  automationRules: Array<{ id: string; name: string; enabled: boolean; trigger: string }>;
}

export interface UpdateBoardColumnSettingsResponse {
  column: BoardColumn;
  result: string;
  message: string;
}

export interface BoardSprintsResponse {
  sprints: BoardSprint[];
  activeSprintId?: string;
}

export interface CreateSprintResponse {
  sprint: BoardSprint;
  result: string;
  message: string;
}

export interface SprintStartPreviewResponse {
  sprint: BoardSprint;
  canStart: boolean;
  riskLevel: string;
  checks: SprintCheckItem[];
}

export interface StartSprintResponse {
  sprint: BoardSprint;
  result: string;
  message: string;
  checks?: SprintCheckItem[];
}

export interface SprintClosePreviewResponse {
  sprint: BoardSprint;
  canClose: boolean;
  riskLevel: string;
  summary: {
    totalIssues: number; completedIssues: number;
    inProgressIssues: number; todoIssues: number; blockedIssues: number;
  };
  checks: SprintCheckItem[];
}

export interface CloseSprintResponse {
  sprint: BoardSprint;
  result: string;
  message: string;
  checks?: SprintCheckItem[];
}

export interface ReportFilterOptionsResponse {
  projects: Array<{ id: string; label: string; disabled?: boolean; helper?: string }>;
  sprints: Array<{ id: string; label: string; helper?: string }>;
  statuses: Array<{ id: string; label: string }>;
  priorities: Array<{ id: string; label: string }>;
  assignees: Array<{ id: string; label: string; avatar?: string }>;
  labels: Array<{ id: string; label: string }>;
  customFields: Array<{ id: string; label: string; options: Array<{ id: string; label: string }> }>;
  savedFilters: Array<{ id: string; name: string; 
filters: ReportFilterState;
  description?: string;
  createdAt?: string;
}>;
  quickFilters: Array<{ id: string; label: string; 
filters: Partial<ReportFilterState>;
  description?: string;
}>;
}

export interface ReportsResponse {
  chartData: Array<Record<string, string | number>>;
  summary: Array<{ id: string; label: string; value: string; tone: string }>;
  total: number;
  activeFilters?: ReportFilterState;
  hasData?: boolean;
  emptyState?: { icon: string; title: string;
  name?: string;
  wipEnabled?: boolean; description: string; actionLabel?: string };
  statusKey?: string;
  metrics: Array<{ id: string; label: string; value: number; change: number; trend: string; color: string }>;
  trend: Array<{ date: string; label: string; created: number; resolved: number }>;
    distributions: Array<{ id: string; label: string; value: number; color: string }>;
  quickFilters?: Array<{ id: string; label: string; filters?: Partial<ReportFilterState>; description?: string }>;
  reportType?: string;
  dateRange?: { from?: string; to?: string };
  includeCharts?: boolean;
  includeDetails?: boolean;
  includeFilterDescription?: boolean;
};


// ===== Additional response types for barrel exports =====
export interface ComponentFeedbackStates {
  loading: ComponentFeedbackState[];
  empty: ComponentFeedbackState[];
  errors: ComponentFeedbackState[];
  permissions: ComponentFeedbackState[];
}
export interface ComponentGalleryResponse {
  tableRows: BoardIssueCard[];
  badgeSamples: Array<{ id: string; label: string; kind: string; tone: string }>;
  feedbackStates: ComponentFeedbackStates;
  dangerActions: DangerActionSpec[];
  apiSpec?: {
    endpoint: string;
    method: string;
    requestParams: ComponentGalleryRequest;
    responseShape: string;
    mockLatencyMs: number;
  };
}

// ===== Response type aliases for barrel exports =====
export interface ExportReportResponse {
  jobId: string;
  status: string;
  message: string;
  fileName: string;
  estimatedSeconds: number;
}

export interface ProjectSettingsResponse {
  project: { key: string; name: string; category: string; lead: string; visibility: string };
  workflow: Array<{ id: string; label: string; color: string; transitions: string[] }>;
  roles: Array<{ id: string; name: string; members: number; permissions: string[] }>;
  automations: Array<{ id: string; name: string; enabled: boolean; trigger: string }>;
}

export interface ReportConfigResponse {
  report: {
    id?: string; name: string; visibility?: string;
    dataSource: string; chartType: string;
    dimensions: string[]; metrics: string[]; filters: ReportFilterState;
  };
  dataSources: Array<{ id: string; label: string; disabled?: boolean; helper?: string }>;
  dimensions: Array<{ id: string; label: string }>;
  metrics: Array<{ id: string; label: string }>;
  chartTypes: Array<{ id: string; label: string }>;
  preview: ReportsResponse;
}

export interface ReportDrilldownResponse {
  title: string;
  name?: string;
  wipEnabled?: boolean;
  statusKey?: string;
  breadcrumb: string[];
  summary: Array<{ id: string; label: string; value: string; tone: string }>;
  rows: Array<Record<string, string | number>>;
  total: number;
  page: number;
  pageSize: number;
  filters: ReportFilterState;
}

export interface SaveReportConfigResponse {
  reportId: string;
  message: string;
  savedAt: string;
}
export interface ReportSavedFilter { id: string; name: string; filters: ReportFilterState; description?: string; }
