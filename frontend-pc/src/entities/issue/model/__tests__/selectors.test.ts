import { describe, expect, it } from "vitest";

// Test infrastructure for issue selectors
// These are pure functions - easiest to test when vitest is available

import { sortIssuesByRank } from "../selectors";

describe("sortIssuesByRank", () => {
  it("should sort by rank ascending", () => {
    const issues = [
      { id: "2", rank: 2, title: "Second" } as any,
      { id: "1", rank: 1, title: "First" } as any,
      { id: "3", rank: 3, title: "Third" } as any,
    ];
    const sorted = sortIssuesByRank(issues);
    expect(sorted[0].id).toBe("1");
    expect(sorted[1].id).toBe("2");
    expect(sorted[2].id).toBe("3");
  });

  it("should handle empty array", () => {
    expect(sortIssuesByRank([])).toEqual([]);
  });
});
