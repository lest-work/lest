import { useQuery, useMutation } from "@tanstack/react-query";

import { projectWorkspaceService } from "./index";
import type { BoardIssueDetailRequest, BoardIssueDetailResponse, BoardRequest, BoardResponse, BoardSprintsRequest, BoardSprintsResponse, CloseSprintRequest, CloseSprintResponse, CreateSprintRequest, CreateSprintResponse, MoveBoardIssueRequest, MoveBoardIssueResponse, ReportsRequest, ReportsResponse, RoadmapRequest, RoadmapResponse, SprintClosePreviewRequest, SprintClosePreviewResponse, SprintStartPreviewRequest, SprintStartPreviewResponse, StartSprintRequest, StartSprintResponse, WorkspaceSearchRequest, WorkspaceSearchResponse, WorkspaceShellRequest, WorkspaceShellResponse, WorkspaceNotificationsRequest, WorkspaceNotificationsResponse } from "./contracts";

export const projectQueryKeys = {
  shell: (params: WorkspaceShellRequest) => ["project", "shell", params] as const,
  search: (params: WorkspaceSearchRequest) => ["project", "search", params] as const,
  board: (params: BoardRequest) => ["project", "board", params] as const,
  boardDetail: (params: BoardIssueDetailRequest) => ["project", "board", "detail", params] as const,
  boardSprints: (params: BoardSprintsRequest) => ["project", "board", "sprints", params] as const,
  roadmap: (params: RoadmapRequest) => ["project", "roadmap", params] as const,
  reports: (params: ReportsRequest) => ["project", "reports", params] as const,
  notifications: (params: WorkspaceNotificationsRequest) => ["project", "notifications", params] as const,
};

export function useWorkspaceShellQuery(params: WorkspaceShellRequest) {
  return useQuery({
    queryKey: projectQueryKeys.shell(params),
    queryFn: () => projectWorkspaceService.getWorkspaceShell(params),
  });
}

export function useBoardQuery(params: BoardRequest) {
  return useQuery({
    queryKey: projectQueryKeys.board(params),
    queryFn: () => projectWorkspaceService.getBoard(params),
  });
}

export function useBoardDetailQuery(params: BoardIssueDetailRequest) {
  return useQuery({
    queryKey: projectQueryKeys.boardDetail(params),
    queryFn: () => projectWorkspaceService.getBoardIssueDetail(params),
  });
}

export function useBoardSprintsQuery(params: BoardSprintsRequest) {
  return useQuery({
    queryKey: projectQueryKeys.boardSprints(params),
    queryFn: () => projectWorkspaceService.getBoardSprints(params),
  });
}

export function useRoadmapQuery(params: RoadmapRequest) {
  return useQuery({
    queryKey: projectQueryKeys.roadmap(params),
    queryFn: () => projectWorkspaceService.getRoadmap(params),
  });
}

export function useReportsQuery(params: ReportsRequest) {
  return useQuery({
    queryKey: projectQueryKeys.reports(params),
    queryFn: () => projectWorkspaceService.getReports(params),
  });
}

export function useMoveBoardIssueMutation() {
  return useMutation({
    mutationFn: (payload: MoveBoardIssueRequest) => projectWorkspaceService.moveBoardIssue(payload),
  });
}

export function useCreateSprintMutation() {
  return useMutation({
    mutationFn: (payload: CreateSprintRequest) => projectWorkspaceService.createSprint(payload),
  });
}

export function useStartSprintMutation() {
  return useMutation({
    mutationFn: (payload: StartSprintRequest) => projectWorkspaceService.startSprint(payload),
  });
}

export function useCloseSprintMutation() {
  return useMutation({
    mutationFn: (payload: CloseSprintRequest) => projectWorkspaceService.closeSprint(payload),
  });
}
