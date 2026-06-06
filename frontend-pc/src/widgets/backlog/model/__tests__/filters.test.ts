import { describe, expect, it } from "vitest";

import { backlogStatusOptions, backlogTypeOptions, backlogPriorityOptions } from "../filters";

describe("backlog filter options", () => {
  it("should have all status options", () => {
    const statuses = backlogStatusOptions.map((o) => o.id);
    expect(statuses).toContain("todo");
    expect(statuses).toContain("in-progress");
    expect(statuses).toContain("review");
    expect(statuses).toContain("done");
    expect(statuses).toContain("closed");
  });

  it("should have all type options", () => {
    const types = backlogTypeOptions.map((o) => o.id);
    expect(types).toContain("task");
    expect(types).toContain("bug");
    expect(types).toContain("story");
    expect(types).toContain("subtask");
  });

  it("should have all priority options", () => {
    const priorities = backlogPriorityOptions.map((o) => o.id);
    expect(priorities).toContain("highest");
    expect(priorities).toContain("high");
    expect(priorities).toContain("medium");
    expect(priorities).toContain("low");
    expect(priorities).toContain("lowest");
  });
});
