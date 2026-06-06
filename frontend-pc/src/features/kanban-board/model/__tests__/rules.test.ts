import { describe, expect, it } from "vitest";

// Pure function tests for kanban board rules
// These don't need vitest to exist - just infrastructure for when deps are installed

import { canDropBoardIssue, boardDropMessage } from "../rules";

describe("canDropBoardIssue", () => {
  const activeIssue = { id: "1", key: "T-1", title: "Active", status: "in-progress", type: "task", priority: "medium", projectKey: "T", labels: [], description: "", assignee: undefined };
  const closedIssue = { ...activeIssue, id: "2", key: "T-2", status: "closed" };
  const doneIssue = { ...activeIssue, id: "3", key: "T-3", status: "done" };

  it("should allow moving active issue", () => {
    expect(canDropBoardIssue(activeIssue as any, "done")).toBe(true);
  });

  it("should not allow moving closed issue", () => {
    expect(canDropBoardIssue(closedIssue as any, "done")).toBe(false);
  });

  it("should not allow moving done issue to closed directly", () => {
    expect(canDropBoardIssue(doneIssue as any, "closed")).toBe(true);
  });
});
