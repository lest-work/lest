import { arrayMove } from "@dnd-kit/sortable";

import type { Issue } from "@/entities/issue";

export function filterBacklogIssues<T extends Issue>(issues: T[]) {
  return issues.filter((issue) => !issue.sprintId).sort((first, second) => first.rank - second.rank);
}

export function reorderBacklogIssues<T extends Issue>(issues: T[], activeId: string, overId: string) {
  const backlogIssues = filterBacklogIssues(issues);
  const itemIds = backlogIssues.map((issue) => issue.id);
  const activeIndex = itemIds.indexOf(activeId);
  const overIndex = itemIds.indexOf(overId);

  if (activeIndex < 0 || overIndex < 0) {
    return null;
  }

  const reorderedIds = arrayMove(itemIds, activeIndex, overIndex);
  const reorderedIssues = reorderedIds
    .map((id) => backlogIssues.find((issue) => issue.id === id))
    .filter((issue): issue is T => Boolean(issue));

  const rankMap = new Map(reorderedIssues.map((issue, index) => [issue.id, index + 1]));
  const items = issues.map((issue) => {
    const rank = rankMap.get(issue.id);
    return rank ? { ...issue, rank } : issue;
  });

  const movedIssue = items.find((issue) => issue.id === activeId);

  return {
    items,
    movedIssue,
    targetPosition: overIndex + 1,
  };
}
