import type { Issue } from "@/entities/issue";

export interface MockIssue extends Issue {
  updatedTimestamp: string;
}

export interface MockIssuesJson {
  items: MockIssue[];
  total: number;
}

export interface MockActivitiesJson {
  items: {
    id: string;
    user: string;
    content: string;
    time: string;
  }[];
}
