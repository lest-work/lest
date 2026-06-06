import type { DashboardIssue } from "@/api/dashboard";
import { issueStatusLabel, statusClassName, PriorityInline } from "@/entities/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, DataTable } from "@/shared/ui";
import { Panel } from "./dashboard-components";
import { type OverlayId } from "./dashboard-constants";


export function RecentIssuesPanel({
  issues,
  overlay,
  onOverlayChange,
  onOpenIssue,
}: {
  issues: DashboardIssue[];
  overlay: OverlayId;
  onOverlayChange: (id: OverlayId) => void;
  onOpenIssue: (issueId: string) => void;
}) {
  return (
    <Panel id="recent-issues" title="最近问题" className="min-h-[282px]" overlay={overlay} onOverlayChange={onOverlayChange}>
      <div className="px-5 pb-4">
        <DataTable<DashboardIssue>
          className="lest-compact-table"
          columns={[
            { title: "编号", dataIndex: "key", width: 80, render: (key) => <span className="font-medium text-[#0C66E4]">{key}</span> },
            { title: "标题", dataIndex: "title", render: (title) => <span className="block truncate pr-4">{title}</span> },
            {
              title: "负责人",
              dataIndex: "assignee",
              width: 120,
              render: (_, issue) => (
                <div className="flex items-center gap-2">
                  {issue.assignee ? <Avatar name={issue.assignee.name} imageSrc={issue.assignee.avatar} className="h-6 w-6" /> : null}
                  <span className="truncate">{issue.assignee?.name ?? "未分配"}</span>
                </div>
              ),
            },
            {
              title: "状态",
              dataIndex: "status",
              width: 92,
              render: (status: DashboardIssue["status"]) => (
                <span className={cn("inline-flex rounded-[3px] px-1.5 py-0.5 text-[12px] font-semibold", statusClassName[status])}>
                  {issueStatusLabel[status]}
                </span>
              ),
            },
            { title: "优先级", dataIndex: "priority", width: 88, render: (priority: DashboardIssue["priority"]) => <PriorityInline priority={priority} /> },
            { title: "更新时间", dataIndex: "updatedAt", width: 90, align: "right", render: (updatedAt) => <span className="text-[#44546F]">{updatedAt}</span> },
          ]}
          dataSource={issues}
          pagination={false}
          rowKey="id"
          onRow={(issue) => ({
            className: "h-[34px] cursor-pointer border-b border-transparent text-[13px] text-[#172B4D] hover:bg-[#F7F8F9]",
            onClick: () => onOpenIssue(issue.id),
          })}
          size="small"
        />
        <Button className="mt-3 text-[13px] font-medium text-[#0C66E4] hover:underline">查看所有问题</Button>
      </div>
    </Panel>
  );
}
