import { useState } from "react";

import { type DashboardIssueFilterId, type DateRangePreset } from "@/api/dashboard";
import { type IssuePriority, type IssueStatus, type IssueType } from "@/entities/issue";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import { MetricCard } from "./dashboard-components";
import type { ToastMessage } from "@/shared/ui";
import { ToastViewport } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

import { DASHBOARD_PROJECT_KEY, type OverlayId } from "./dashboard-constants";
import {
  useDashboardOverviewQuery,
  useDashboardFilterOptionsQuery,
  useDashboardIssueDetailQuery,
  useCreateIssueMutation,
  useUpdateIssueStatusMutation,
  useAddIssueCommentMutation,
} from "@/api/dashboard/queries";
import { ProjectHeader } from "./project-header";
import { StatusDistributionPanel, PriorityDistributionPanel, AssigneeDistributionPanel } from "./distribution-panels";
import { RecentIssuesPanel } from "./recent-issues-panel";
import { ActivityPanel } from "./activity-panel";
import { CreateIssueModal } from "./create-issue-modal";
import { DashboardIssueDrawer } from "./dashboard-issue-drawer";
import { DashboardSkeleton } from "./dashboard-skeleton";

function DashboardPageContent({ onNavigate }: { onNavigate?: (page: WorkspacePageKey) => void }) {
  const [datePreset, setDatePreset] = useState<DateRangePreset>("last_30_days");
  const [issueFilterId, setIssueFilterId] = useState<DashboardIssueFilterId>("all");
  const [overlay, setOverlay] = useState<OverlayId>(null);
  const [isCreateOpen, setCreateOpen] = useState(false);
  const [selectedIssueId, setSelectedIssueId] = useState<string>();
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const overviewParams = { projectKey: DASHBOARD_PROJECT_KEY, datePreset, issueFilterId };

  const { data: overview, isLoading } = useDashboardOverviewQuery(overviewParams);
  const { data: filters } = useDashboardFilterOptionsQuery(DASHBOARD_PROJECT_KEY);
  const { data: issueDetail } = useDashboardIssueDetailQuery(selectedIssueId);

  const createIssueMutation = useCreateIssueMutation();
  const updateStatusMutation = useUpdateIssueStatusMutation();
  const addCommentMutation = useAddIssueCommentMutation();

  async function handleCreateIssue(payload: {
    title: string;
    description: string;
    type: IssueType;
    priority: IssuePriority;
    assigneeId?: string;
    dueDate?: string;
  }) {
    try {
      const response = await createIssueMutation.mutateAsync({ projectKey: DASHBOARD_PROJECT_KEY, ...payload, attachmentIds: [] });
      const message: ToastMessage = { id: `${Date.now()}`, title: response.message, tone: "success" };
      setToasts((items) => [message, ...items].slice(0, 3));
      setCreateOpen(false);
    } catch {
      const message: ToastMessage = { id: `${Date.now()}`, title: "创建失败，请重试", tone: "error" };
      setToasts((items) => [message, ...items].slice(0, 3));
    }
  }

  async function handleStatusChange(issueId: string, status: IssueStatus) {
    try {
      await updateStatusMutation.mutateAsync({ issueId, status });
    } catch {
      const message: ToastMessage = { id: `${Date.now()}`, title: "状态更新失败，请重试", tone: "error" };
      setToasts((items) => [message, ...items].slice(0, 3));
    }
  }

  async function handleAddComment(issueId: string, content: string) {
    try {
      await addCommentMutation.mutateAsync({ issueId, content });
    } catch {
      const message: ToastMessage = { id: `${Date.now()}`, title: "评论发送失败，请重试", tone: "error" };
      setToasts((items) => [message, ...items].slice(0, 3));
    }
  }

  const dateLabel = filters?.dateRanges.find((item) => item.id === datePreset)?.label ?? "最近 30 天";
  const issueFilterLabel = filters?.issueFilters.find((item) => item.id === issueFilterId)?.label ?? "所有问题";

  return (
    <ProjectWorkspaceShell
      activePage="dashboard"
      title="项目仪表盘"
      onNavigate={onNavigate ?? (() => undefined)}
      showPageHeader={false}
      contentClassName="px-[30px] pb-[30px] pt-[27px]"
      onCreateIssue={() => setCreateOpen(true)}
      onOpenIssue={(issueId) => setSelectedIssueId(issueId)}
    >
      <ProjectHeader
        dateLabel={dateLabel}
        issueFilterLabel={issueFilterLabel}
        filters={filters}
        datePreset={datePreset}
        issueFilterId={issueFilterId}
        overlay={overlay}
        onOverlayChange={setOverlay}
        onDatePresetChange={setDatePreset}
        onIssueFilterChange={setIssueFilterId}
      />

      {isLoading && !overview ? <DashboardSkeleton /> : null}

      {overview ? (
        <>
          <div className="grid grid-cols-1 gap-4 xl:grid-cols-4">
            {overview.metrics.map((item) => (
              <MetricCard key={item.id} item={item} overlay={overlay} onOverlayChange={setOverlay} />
            ))}
          </div>

          <div className="mt-4 grid grid-cols-1 gap-4 xl:grid-cols-[1fr_1fr_1.18fr]">
            <StatusDistributionPanel data={overview.statusDistribution} overlay={overlay} onOverlayChange={setOverlay} />
            <PriorityDistributionPanel data={overview.priorityDistribution} overlay={overlay} onOverlayChange={setOverlay} />
            <AssigneeDistributionPanel data={overview.assigneeDistribution} overlay={overlay} onOverlayChange={setOverlay} />
          </div>

          <div className="mt-4 grid grid-cols-1 gap-4 xl:grid-cols-[1.25fr_0.98fr]">
            <RecentIssuesPanel issues={overview.recentIssues} overlay={overlay} onOverlayChange={setOverlay} onOpenIssue={setSelectedIssueId} />
            <ActivityPanel activities={overview.activities} overlay={overlay} onOverlayChange={setOverlay} />
          </div>
        </>
      ) : null}

      {isCreateOpen ? (
        <CreateIssueModal assignees={overview?.assigneeDistribution ?? []} onClose={() => setCreateOpen(false)} onSubmit={handleCreateIssue} />
      ) : null}

      {issueDetail ? (
        <DashboardIssueDrawer
          detail={issueDetail}
          onClose={() => setSelectedIssueId(undefined)}
          onStatusChange={handleStatusChange}
          onAddComment={handleAddComment}
        />
      ) : null}

      <ToastViewport messages={toasts} onClose={(id) => setToasts((items) => items.filter((item) => item.id !== id))} />
    </ProjectWorkspaceShell>
  );
}

export function DashboardPage({ onNavigate }: { onNavigate?: (page: WorkspacePageKey) => void }) {
  return <DashboardPageContent onNavigate={onNavigate} />;
}
