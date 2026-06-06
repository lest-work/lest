import type { IssueStatus } from "@/entities/issue";

export interface WorkflowNodeData {
  id: IssueStatus;
  title: string;
  description: string;
  color: string;
  transitions: IssueStatus[];
}
