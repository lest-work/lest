import type { BoardIssueCard } from "@/api/project";
import type { IssueStatus } from "@/entities/issue";

export function canDropBoardIssue(issue: BoardIssueCard, targetStatus: IssueStatus) {
  if (issue.status === "closed") {
    return false;
  }
  if (targetStatus === "closed" && issue.status !== "done") {
    return false;
  }
  return true;
}

export function boardDropMessage(issue: BoardIssueCard, targetStatus: IssueStatus) {
  if (issue.status === "closed") {
    return "只读卡片不能拖拽";
  }
  if (targetStatus === "closed" && issue.status !== "done") {
    return "需要先完成后才能关闭";
  }
  return `可以放置在目标列`;
}
