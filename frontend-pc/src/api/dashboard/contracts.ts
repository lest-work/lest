import type { IssuePriority, IssueStatus, IssueType } from "@/entities/issue/model/types";

export type DateRangePreset =
  | "today"
  | "yesterday"
  | "last_7_days"
  | "last_30_days"
  | "last_90_days"
  | "this_week"
  | "last_week"
  | "this_month"
  | "last_month"
  | "this_quarter"
  | "this_year"
  | "last_year"
  | "custom";

export type DashboardIssueFilterId =
  | "all"
  | "unresolved"
  | "assigned_to_me"
  | "created_by_me"
  | "high_priority"
  | "done";

export interface ApiPageRequest {
  page?: number;
  pageSize?: number;
}

export interface ApiPageResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface DashboardOverviewRequest {
  projectKey: string;
  datePreset: DateRangePreset;
  issueFilterId: DashboardIssueFilterId;
  startDate?: string;
  endDate?: string;
}

export interface DashboardMetric {
  id: string;
  label: string;
  value: string;
  change: number;
  trend: "up" | "down";
  compareText: string;
  chartType: "line" | "bar";
  chartValues: number[];
  color: string;
}

export interface DistributionDatum {
  id: string;
  label: string;
  value: number;
  percentage: number;
  color: string;
}

export interface AssigneeWorkload {
  id: string;
  name: string;
  avatar?: string;
  value: number;
  color: string;
}

export interface DashboardUser {
  id: string;
  name: string;
  role: string;
  email: string;
  avatar?: string;
}

export interface DashboardIssue {
  id: string;
  key: string;
  title: string;
  projectKey: string;
  type: IssueType;
  status: IssueStatus;
  priority: IssuePriority;
  assignee?: DashboardUser;
  reporter: DashboardUser;
  labels: string[];
  description: string;
  dueDate?: string;
  updatedAt: string;
  createdAt: string;
  estimate?: number;
  attachmentsCount?: number;
  linkedIssuesCount?: number;
}

export interface DashboardActivity {
  id: string;
  actor: DashboardUser;
  verb: string;
  issueKey?: string;
  issueId?: string;
  targetText?: string;
  createdAtText: string;
}

export interface DashboardOverviewResponse {
  metrics: DashboardMetric[];
  statusDistribution: DistributionDatum[];
  priorityDistribution: DistributionDatum[];
  assigneeDistribution: AssigneeWorkload[];
  recentIssues: DashboardIssue[];
  activities: DashboardActivity[];
}

export interface DashboardSearchRequest {
  keyword: string;
  projectKey?: string;
  limit?: number;
}

export interface DashboardSearchResult {
  id: string;
  type: "issue" | "project" | "member";
  title: string;
  description?: string;
  avatar?: string;
  meta?: string;
}

export interface DashboardSearchResponse {
  recentKeywords: string[];
  recentIssues: DashboardSearchResult[];
  results: DashboardSearchResult[];
}

export interface DashboardNotification {
  id: string;
  type: "comment" | "assignment" | "status" | "attachment" | "system";
  title: string;
  description: string;
  createdAtText: string;
  unread: boolean;
  actor?: DashboardUser;
}

export interface DashboardNotificationsRequest extends ApiPageRequest {
  unreadOnly?: boolean;
}

export interface DashboardNotificationsResponse extends ApiPageResponse<DashboardNotification> {
  unreadCount: number;
}

export interface DashboardUserMenuResponse {
  user: DashboardUser;
  workspace: {
    id: string;
    name: string;
  };
  theme: "light" | "dark" | "system";
  locale: "zh-CN" | "en-US";
}

export interface DateRangeOption {
  id: DateRangePreset;
  label: string;
  description?: string;
}

export interface IssueFilterOption {
  id: DashboardIssueFilterId;
  label: string;
  description?: string;
  count?: number;
}

export interface DashboardFilterOptionsResponse {
  dateRanges: DateRangeOption[];
  issueFilters: IssueFilterOption[];
}

export interface CreateIssueRequest {
  projectKey: string;
  type: IssueType;
  title: string;
  description?: string;
  assigneeId?: string;
  priority: IssuePriority;
  dueDate?: string;
  attachmentIds?: string[];
}

export interface CreateIssueResponse {
  issue: DashboardIssue;
  message: string;
}

export interface UpdateIssueStatusRequest {
  issueId: string;
  status: IssueStatus;
}

export interface UpdateIssueStatusResponse {
  issue: DashboardIssue;
  activity: DashboardActivity;
}

export interface AddIssueCommentRequest {
  issueId: string;
  content: string;
  mentionedUserIds?: string[];
}

export interface IssueComment {
  id: string;
  author: DashboardUser;
  content: string;
  createdAtText: string;
}

export interface AddIssueCommentResponse {
  comment: IssueComment;
  activity: DashboardActivity;
}

export interface GetDashboardIssueDetailRequest {
  issueId: string;
}

export interface GetDashboardIssueDetailResponse {
  issue: DashboardIssue;
  comments: IssueComment[];
  activities: DashboardActivity[];
}
