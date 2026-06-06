import type { Issue, IssueStatus } from "./model/types";
export type { Issue, IssuePriority, IssueStatus, IssueType, User } from "./model/types";
export { issuePriorityLabel, issueStatusLabel, issueTypeLabel, statusClassName, priorityIconClassName } from "./model/constants";
export { getBacklogIssues, sortIssuesByRank } from "./model/selectors";
export { BlockedBadge, StatusPill, IssueCard, IssueTypeBadge, PriorityBadge, StatusBadge, PriorityInline } from "./ui";
export type { IssueCardProps } from "./ui";

export interface KanbanColumnData {
  id: IssueStatus;
  title: string;
  description: string;
  wipLimit?: number;
  issues: Issue[];
}
