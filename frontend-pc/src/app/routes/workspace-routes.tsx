import { lazy } from "react";
import type { ComponentType, LazyExoticComponent } from "react";

import type { WorkspacePageKey } from "@/entities/project-workspace/model";

export interface WorkspacePageProps {
  onNavigate: (page: WorkspacePageKey) => void;
}

export type WorkspacePageComponent = LazyExoticComponent<ComponentType<WorkspacePageProps>>;
type WorkspacePageModule = { default: ComponentType<WorkspacePageProps> };

export const defaultWorkspacePage: WorkspacePageKey = "dashboard";

const workspacePageLoaders: Record<WorkspacePageKey, () => Promise<WorkspacePageModule>> = {
  dashboard: () => import("@/pages/dashboard").then((module) => ({ default: module.DashboardPage })),
  roadmap: () => import("@/pages/roadmap").then((module) => ({ default: module.RoadmapPage })),
  backlog: () => import("@/pages/backlog").then((module) => ({ default: module.BacklogPage })),
  kanban: () => import("@/pages/kanban").then((module) => ({ default: module.KanbanPage })),
  reports: () => import("@/pages/reports").then((module) => ({ default: module.ReportsPage })),
  issues: () => import("@/pages/issues").then((module) => ({ default: module.IssuesPage })),
  settings: () => import("@/pages/project-settings").then((module) => ({ default: module.ProjectSettingsPage })),
  components: () => import("@/pages/component-gallery").then((module) => ({ default: module.ComponentGalleryPage })),
};

const preloadedWorkspacePages = new Set<WorkspacePageKey>();

export const workspaceRoutes: Record<WorkspacePageKey, WorkspacePageComponent> = {
  dashboard: lazy(workspacePageLoaders.dashboard),
  roadmap: lazy(workspacePageLoaders.roadmap),
  backlog: lazy(workspacePageLoaders.backlog),
  kanban: lazy(workspacePageLoaders.kanban),
  reports: lazy(workspacePageLoaders.reports),
  issues: lazy(workspacePageLoaders.issues),
  settings: lazy(workspacePageLoaders.settings),
  components: lazy(workspacePageLoaders.components),
};

export function preloadWorkspacePage(pageKey?: WorkspacePageKey) {
  if (!pageKey || preloadedWorkspacePages.has(pageKey)) {
    return;
  }

  preloadedWorkspacePages.add(pageKey);
  void workspacePageLoaders[pageKey]();
}
