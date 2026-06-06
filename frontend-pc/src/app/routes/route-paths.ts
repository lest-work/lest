import type { WorkspacePageKey } from "@/entities/project-workspace/model";

export const pagePaths: Record<WorkspacePageKey, string> = {
  dashboard: "/",
  roadmap: "/roadmap",
  backlog: "/backlog",
  kanban: "/kanban",
  reports: "/reports",
  issues: "/issues",
  settings: "/project-settings",
  components: "/components",
};

export function pathFromPage(page: WorkspacePageKey): string {
  return pagePaths[page];
}

export function pageFromPath(path: string): WorkspacePageKey | undefined {
  const cleanPath = path.replace(/\/$/, "") || "/";
  for (const [page, routePath] of Object.entries(pagePaths)) {
    if (routePath === cleanPath) return page as WorkspacePageKey;
  }
  return undefined;
}
