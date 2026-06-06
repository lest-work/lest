import type { IssuePriority, IssueStatus, IssueType } from "./types";

export const issueStatusLabel: Record<string, string> = {
  todo: "未开始",
  "in-progress": "进行中",
  review: "已评审",
  done: "已完成",
  closed: "已关闭",
};

export const issuePriorityLabel: Record<string, string> = {
  highest: "最高",
  high: "高",
  medium: "中",
  low: "低",
  lowest: "最低",
};

export const statusClassName: Record<IssueStatus, string> = {
  todo: "bg-[#F4F5F7] text-[#42526E]",
  "in-progress": "bg-[#DEEBFF] text-[#0747A6]",
  review: "bg-[#FFF0B3] text-[#974F0C]",
  done: "bg-[#E3FCEF] text-[#006644]",
  closed: "bg-[#E3FCEF] text-[#006644]",
};

export const priorityIconClassName: Record<IssuePriority, string> = {
  highest: "text-[#FF3B30]",
  high: "text-[#FF3B30]",
  medium: "text-[#0C66E4]",
  low: "text-[#1F9D83]",
  lowest: "text-[#6B778C]",
};

export const issueTypeLabel: Record<string, string> = {
  task: "任务",
  bug: "缺陷",
  story: "需求",
  subtask: "子任务",
  epic: "史诗",
};
