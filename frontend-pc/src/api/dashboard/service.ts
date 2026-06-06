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

export interface DashboardService {
  getOverview(params: DashboardOverviewRequest): Promise<DashboardOverviewResponse>;
  getFilterOptions(projectKey: string): Promise<DashboardFilterOptionsResponse>;
  search(params: DashboardSearchRequest): Promise<DashboardSearchResponse>;
  getNotifications(params?: DashboardNotificationsRequest): Promise<DashboardNotificationsResponse>;
  getUserMenu(): Promise<DashboardUserMenuResponse>;
  createIssue(payload: CreateIssueRequest): Promise<CreateIssueResponse>;
  getIssueDetail(params: GetDashboardIssueDetailRequest): Promise<GetDashboardIssueDetailResponse>;
  updateIssueStatus(payload: UpdateIssueStatusRequest): Promise<UpdateIssueStatusResponse>;
  addIssueComment(payload: AddIssueCommentRequest): Promise<AddIssueCommentResponse>;
  markAllNotificationsRead(): Promise<{ unreadCount: number }>;
}
