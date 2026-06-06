import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { dashboardService } from "./index";
import type {
  AddIssueCommentRequest,
  CreateIssueRequest,
  DashboardOverviewRequest,
  UpdateIssueStatusRequest,
} from "./contracts";

// ─── Query keys ─────────────────────────────────────────────────

export const dashboardQueryKeys = {
  all: ["dashboard"] as const,
  overview: (params: DashboardOverviewRequest) => ["dashboard", "overview", params] as const,
  filterOptions: (projectKey: string) => ["dashboard", "filterOptions", projectKey] as const,
  issueDetail: (issueId: string) => ["dashboard", "issueDetail", issueId] as const,
  search: (keyword: string) => ["dashboard", "search", keyword] as const,
  notifications: () => ["dashboard", "notifications"] as const,
};

// ─── Queries ─────────────────────────────────────────────────────

export function useDashboardOverviewQuery(params: DashboardOverviewRequest) {
  return useQuery({
    queryKey: dashboardQueryKeys.overview(params),
    queryFn: () => dashboardService.getOverview(params),
  });
}

export function useDashboardFilterOptionsQuery(projectKey: string) {
  return useQuery({
    queryKey: dashboardQueryKeys.filterOptions(projectKey),
    queryFn: () => dashboardService.getFilterOptions(projectKey),
    staleTime: 5 * 60 * 1000,
  });
}

export function useDashboardIssueDetailQuery(issueId: string | undefined) {
  return useQuery({
    queryKey: dashboardQueryKeys.issueDetail(issueId ?? ""),
    queryFn: () => dashboardService.getIssueDetail({ issueId: issueId! }),
    enabled: !!issueId,
  });
}

// ─── Mutations ───────────────────────────────────────────────────

export function useCreateIssueMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateIssueRequest) => dashboardService.createIssue(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dashboardQueryKeys.all });
    },
  });
}

export function useUpdateIssueStatusMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UpdateIssueStatusRequest) => dashboardService.updateIssueStatus(payload),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: dashboardQueryKeys.all });
      queryClient.invalidateQueries({ queryKey: dashboardQueryKeys.issueDetail(variables.issueId) });
    },
  });
}

export function useAddIssueCommentMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: AddIssueCommentRequest) => dashboardService.addIssueComment(payload),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: dashboardQueryKeys.issueDetail(variables.issueId) });
      queryClient.invalidateQueries({ queryKey: dashboardQueryKeys.all });
    },
  });
}
