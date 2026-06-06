import { create } from "zustand";

interface BacklogUiState {
  selectedIssueId?: string;
  dragNotice?: string;
  selectIssue: (issueId?: string) => void;
  setDragNotice: (notice?: string) => void;
}

export const useProjectStore = create<BacklogUiState>((set) => ({
  selectedIssueId: undefined,
  dragNotice: undefined,
  selectIssue: (issueId) => set({ selectedIssueId: issueId }),
  setDragNotice: (notice) => set({ dragNotice: notice }),
}));
