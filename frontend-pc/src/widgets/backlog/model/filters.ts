import {
  issuePriorityLabel,
  issueStatusLabel,
  issueTypeLabel,
  type IssuePriority,
  type IssueStatus,
  type IssueType,
} from "@/entities/issue";
import type { GetBacklogIssuesParams } from "@/api/issue";

export type BacklogFilterKey = "projectKey" | "types" | "statuses" | "priorities" | "assigneeId" | "labels";

export interface BacklogFiltersState {
  projectKey: string;
  types: IssueType[];
  statuses: IssueStatus[];
  priorities: IssuePriority[];
  assigneeId?: string;
  labels: string[];
}

export const backlogProjectOptions = [{ id: "lest-platform", name: "ALP 平台项目" }] as const;

export const backlogTypeOptions = (Object.entries(issueTypeLabel) as [IssueType, string][]).map(([id, label]) => ({
  id,
  label,
}));

export const backlogStatusOptions = (Object.entries(issueStatusLabel) as [IssueStatus, string][]).map(([id, label]) => ({
  id,
  label,
}));

export const backlogPriorityOptions = (Object.entries(issuePriorityLabel) as [IssuePriority, string][]).map(([id, label]) => ({
  id,
  label,
}));

const avatar = (name: string) => `https://api.dicebear.com/9.x/lorelei/svg?seed=${encodeURIComponent(name)}`;

export const backlogAssigneeOptions = [
  { id: "u1", name: "张晓明", role: "开发", avatar: avatar("张晓明") },
  { id: "u2", name: "李华", role: "开发", avatar: avatar("李华") },
  { id: "u3", name: "王芳", role: "测试", avatar: avatar("王芳") },
  { id: "u4", name: "刘强", role: "开发", avatar: avatar("刘强") },
  { id: "u5", name: "陈静", role: "产品", avatar: avatar("陈静") },
  { id: "u6", name: "周杰", role: "测试", avatar: avatar("周杰") },
];

export const backlogLabelOptions = [
  { id: "登录模块", label: "登录模块" },
  { id: "数据报表", label: "数据报表" },
  { id: "权限管理", label: "权限管理" },
  { id: "性能优化", label: "性能优化" },
  { id: "移动端", label: "移动端" },
];

export const defaultBacklogFilters: BacklogFiltersState = {
  projectKey: backlogProjectOptions[0]!.id,
  types: [],
  statuses: [],
  priorities: [],
  assigneeId: undefined,
  labels: [],
};

export type QuickFilterKey = "all" | "mine" | "unplanned" | "today" | "recent" | "blocked";

export const quickFilterOptions: { key: QuickFilterKey; label: string; description: string }[] = [
  { key: "all", label: "全部", description: "展示所有符合条件的问题" },
  { key: "mine", label: "我负责的", description: "负责人为当前账号" },
  { key: "unplanned", label: "我创建的", description: "报告人为当前账号" },
  { key: "today", label: "今天创建", description: "今天创建的问题" },
  { key: "recent", label: "即将到期", description: "截止日期临近的问题" },
  { key: "blocked", label: "已阻塞", description: "当前被阻塞的问题" },
];

export const currentUser = {
  id: "u1",
  name: "张晓明",
};

function normalizeArrayValue<T>(values?: T[]) {
  return values && values.length ? values : undefined;
}

export function buildBacklogQueryParams(
  filters: BacklogFiltersState,
  quickFilter: QuickFilterKey,
  keyword: string,
  page: number,
  pageSize: number,
): GetBacklogIssuesParams {
  const params: GetBacklogIssuesParams = {
    projectKey: filters.projectKey,
    keyword: keyword.trim() ? keyword.trim() : undefined,
    assigneeId: filters.assigneeId,
    statuses: normalizeArrayValue(filters.statuses),
    priorities: normalizeArrayValue(filters.priorities),
    types: normalizeArrayValue(filters.types),
    labels: normalizeArrayValue(filters.labels),
    onlyBacklog: true,
    page,
    pageSize,
  };

  switch (quickFilter) {
    case "mine":
      params.assigneeId = currentUser.id;
      break;
    case "today":
      params.createdOn = "2026-06-06";
      break;
    case "recent":
      params.updatedAfter = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString();
      break;
    case "blocked":
      params.blocked = true;
      break;
    default:
      break;
  }

  return params;
}
