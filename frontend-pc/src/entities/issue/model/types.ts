export type IssueType = "task" | "bug" | "story" | "subtask" | "epic";

export type IssuePriority = "highest" | "high" | "medium" | "low" | "lowest";

export type IssueStatus = "todo" | "in-progress" | "review" | "done" | "closed";

export interface User {
  id: string;
  name: string;
  role: string;
  avatarColor: string;
  avatarUrl?: string;
}

export interface Issue {
  id: string;
  key: string;
  title: string;
  projectKey: string;
  type: IssueType;
  priority: IssuePriority;
  status: IssueStatus;
  assignee?: User;
  reporter: User;
  labels: string[];
  estimate: number;
  dueDate?: string;
  createdAt?: string;
  updatedAt: string;
  description: string;
  sprintId?: string;
  rank: number;
  blocked?: boolean;
}
