import { useMutation, useQueryClient } from "@tanstack/react-query";

import { issueService, type UpdateIssueRankPayload } from "@/api/issue";
import { issueQueryKeys } from "@/api/issue/keys";

export function useReorderBacklogMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UpdateIssueRankPayload) => issueService.updateIssueRank(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: issueQueryKeys.backlogRoot, exact: false });
    },
  });
}
