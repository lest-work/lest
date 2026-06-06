export type WorkspacePageKey =
  | "dashboard"
  | "roadmap"
  | "backlog"
  | "kanban"
  | "reports"
  | "issues"
  | "settings"
  | "components";

export type WorkspaceNavigationIcon =
  | "activity"
  | "box"
  | "clipboard"
  | "code"
  | "compass"
  | "file"
  | "folder"
  | "git"
  | "layout"
  | "pie"
  | "settings";

export interface WorkspaceNavigationItem {
  label: string;
  icon: WorkspaceNavigationIcon;
  pageKey?: WorkspacePageKey;
}

export interface WorkspaceNavigationSection {
  title: string;
  items: WorkspaceNavigationItem[];
}

export type WorkspaceTopNavigationKey = "workbench" | "projects" | "filters" | "dashboards" | "team" | "apps";

export interface WorkspaceTopNavigationItem {
  key: WorkspaceTopNavigationKey;
  label: string;
  pageKeys: WorkspacePageKey[];
  hasMenu?: boolean;
  defaultPageKey?: WorkspacePageKey;
}

export type WorkspaceShellActionKey = "help" | "settings" | "user";

export interface WorkspaceShellMenuGroup {
  key: string;
  title?: string;
  items: string[];
}
