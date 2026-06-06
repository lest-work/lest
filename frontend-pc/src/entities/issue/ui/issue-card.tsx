import { CalendarDays, GripVertical, MoreHorizontal } from "lucide-react";

import { type Issue } from "@/entities/issue";
import { BlockedBadge, IssueTypeBadge, PriorityBadge, StatusBadge } from "@/entities/issue/ui/issue-badges";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Card } from "@/shared/ui";

export interface IssueCardProps {
  issue: Issue;
  compact?: boolean;
  dragHandleProps?: React.HTMLAttributes<HTMLButtonElement>;
  isDragging?: boolean;
  onOpen?: (issue: Issue) => void;
}

export function IssueCard({ issue, compact, dragHandleProps, isDragging, onOpen }: IssueCardProps) {
  return (
    <Card
      className={cn(
        "group cursor-pointer border-slate-200 bg-white p-4 transition-all hover:border-blue-200 hover:shadow-workspace",
        isDragging && "scale-[1.01] border-blue-300 opacity-80 shadow-workspace",
      )}
      onClick={() => onOpen?.(issue)}
    >
      <div className="flex items-start gap-3">
        {dragHandleProps ? (
          <button
            className="mt-0.5 rounded p-1 text-slate-400 opacity-60 transition hover:bg-slate-100 hover:text-slate-700 group-hover:opacity-100"
            aria-label={`拖拽 ${issue.key}`}
            onClick={(event) => event.stopPropagation()}
            {...dragHandleProps}
          >
            <GripVertical className="h-4 w-4" />
          </button>
        ) : null}
        <div className="min-w-0 flex-1">
          <div className="mb-2 flex items-center gap-2">
            <span className="font-mono text-xs font-semibold text-blue-600">{issue.key}</span>
            <IssueTypeBadge type={issue.type} />
            {issue.blocked ? <BlockedBadge /> : null}
          </div>
          <h3 className="line-clamp-2 text-sm font-semibold leading-6 text-slate-900">{issue.title}</h3>
          {!compact ? <p className="mt-2 line-clamp-2 text-xs leading-5 text-slate-500">{issue.description}</p> : null}
          <div className="mt-3 flex flex-wrap items-center gap-2">
            <StatusBadge status={issue.status} />
            <PriorityBadge priority={issue.priority} />
            {issue.labels.map((label) => (
              <span key={label} className="rounded bg-slate-100 px-2 py-1 text-xs text-slate-600">
                {label}
              </span>
            ))}
          </div>
        </div>
        <Button variant="ghost" size="icon" className="h-8 w-8 shrink-0 text-slate-400">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </div>
      <div className="mt-4 flex items-center justify-between border-t pt-3 text-xs text-slate-500">
        <div className="flex items-center gap-2">
          {issue.assignee ? <Avatar name={issue.assignee.name} color={issue.assignee.avatarColor} /> : null}
          <span>{issue.assignee?.name ?? "未分配"}</span>
        </div>
        <div className="flex items-center gap-3">
          {issue.dueDate ? (
            <span className="inline-flex items-center gap-1">
              <CalendarDays className="h-3.5 w-3.5" />
              {issue.dueDate}
            </span>
          ) : null}
          <span>{issue.estimate} 点</span>
        </div>
      </div>
    </Card>
  );
}
