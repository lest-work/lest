import { mockDashboardService } from "./mock";
export { mockDashboardService } from "./mock";
export type { DashboardService } from "./service";
export type {
  AddIssueCommentRequest,
  AddIssueCommentResponse,
  ApiPageRequest,
  ApiPageResponse,
  AssigneeWorkload,
  CreateIssueRequest,
  CreateIssueResponse,
  DashboardActivity,
  DashboardFilterOptionsResponse,
  DashboardIssue,
  DashboardIssueFilterId,
  DashboardMetric,
  DashboardNotification,
  DashboardNotificationsRequest,
  DashboardNotificationsResponse,
  DashboardOverviewRequest,
  DashboardOverviewResponse,
  DashboardSearchRequest,
  DashboardSearchResponse,
  DashboardUser,
  DashboardUserMenuResponse,
  DateRangeOption,
  DateRangePreset,
  DistributionDatum,
  GetDashboardIssueDetailRequest,
  GetDashboardIssueDetailResponse,
  IssueComment,
  IssueFilterOption,
  UpdateIssueStatusRequest,
  UpdateIssueStatusResponse,
} from "./contracts";

/** Default service implementation (currently mock) */
export const dashboardService = mockDashboardService;
