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

export interface ProjectWorkspaceService {
  getWorkspaceShell(params: WorkspaceShellRequest): Promise<WorkspaceShellResponse>;
  searchWorkspace(params: WorkspaceSearchRequest): Promise<WorkspaceSearchResponse>;
  getWorkspaceNotifications(params: WorkspaceNotificationsRequest): Promise<WorkspaceNotificationsResponse>;
  markAllWorkspaceNotificationsRead(params: WorkspaceRequest): Promise<{ unreadCount: number }>;
  getRoadmap(params: RoadmapRequest): Promise<RoadmapResponse>;
  getBoard(params: BoardRequest): Promise<BoardResponse>;
  getBoardIssueDetail(params: BoardIssueDetailRequest): Promise<BoardIssueDetailResponse>;
  moveBoardIssue(payload: MoveBoardIssueRequest): Promise<MoveBoardIssueResponse>;
  getBoardColumnSettings(params: BoardColumnSettingsRequest): Promise<BoardColumnSettingsResponse>;
  updateBoardColumnSettings(payload: UpdateBoardColumnSettingsRequest): Promise<UpdateBoardColumnSettingsResponse>;
  getBoardSprints(params: BoardSprintsRequest): Promise<BoardSprintsResponse>;
  createSprint(payload: CreateSprintRequest): Promise<CreateSprintResponse>;
  getSprintStartPreview(params: SprintStartPreviewRequest): Promise<SprintStartPreviewResponse>;
  startSprint(payload: StartSprintRequest): Promise<StartSprintResponse>;
  getSprintClosePreview(params: SprintClosePreviewRequest): Promise<SprintClosePreviewResponse>;
  closeSprint(payload: CloseSprintRequest): Promise<CloseSprintResponse>;
  getReportFilterOptions(params: ReportFilterOptionsRequest): Promise<ReportFilterOptionsResponse>;
  getReports(params: ReportsRequest): Promise<ReportsResponse>;
  getReportDrilldown(params: ReportDrilldownRequest): Promise<ReportDrilldownResponse>;
  exportReport(payload: ExportReportRequest): Promise<ExportReportResponse>;
  getReportConfig(params: ReportConfigRequest): Promise<ReportConfigResponse>;
  saveReportConfig(payload: SaveReportConfigRequest): Promise<SaveReportConfigResponse>;
  getProjectSettings(params: ProjectSettingsRequest): Promise<ProjectSettingsResponse>;
  getComponentGallery(params: ComponentGalleryRequest): Promise<ComponentGalleryResponse>;
}

