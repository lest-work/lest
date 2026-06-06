import type { Issue } from "./types";

export function sortIssuesByRank(issues: Issue[]) {
  return [...issues].sort((first, second) => first.rank - second.rank);
}

export function getBacklogIssues(issues: Issue[]) {
  return sortIssuesByRank(issues).filter((issue) => !issue.sprintId);
}
