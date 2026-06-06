export const issueQueryKeys = {
  all: ["issues"] as const,
  backlogRoot: ["issues", "backlog"] as const,
  backlog: (params?: object) => ["issues", "backlog", params ?? {}] as const,
  detail: (issueId?: string) => ["issues", "detail", issueId] as const,
};
