import { useQuery } from "@tanstack/react-query";

import { issueService, type GetBacklogIssuesParams } from "@/api/issue";
import { issueQueryKeys } from "@/api/issue/keys";

export function useBacklogIssuesQuery(params?: GetBacklogIssuesParams) {
  return useQuery({
    queryKey: issueQueryKeys.backlog(params),
    queryFn: () => issueService.getBacklogIssues(params),
  });
}
