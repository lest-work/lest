import type { IssueStatus } from "@/entities/issue";
export const DASHBOARD_PROJECT_KEY = "alpha-platform";

export const STATUS_ORDER: IssueStatus[] = ["todo", "in-progress", "review", "done", "closed"];

export const ISSUE_TYPES = ["task", "bug", "story", "subtask"] as const;

export const ISSUE_PRIORITIES = ["highest", "high", "medium", "low", "lowest"] as const;

export type OverlayId = "search" | "notifications" | "user" | "date" | "issueFilter" | "pageMore" | string | null;
