import { AlertTriangle, ArrowDown, ArrowUp, Bug, CircleDot, Code2, Flag, Layers, ListTodo } from "lucide-react";
import { cn } from "@/shared/lib/utils";

import { issuePriorityLabel, issueStatusLabel, issueTypeLabel, priorityIconClassName, type IssuePriority, type IssueStatus, type IssueType } from "@/entities/issue";
import { Badge } from "@/shared/ui";

export function StatusBadge({ status }: { status: IssueStatus }) {
  const variant = status === "done" || status === "closed" ? "success" : status === "review" ? "warning" : "default";

  return (
    <Badge variant={variant} className="rounded-[4px] px-2 py-0.5 text-[11px] font-medium">
      <CircleDot className="mr-1 h-3 w-3" />
      {issueStatusLabel[status]}
    </Badge>
  );
}

export function PriorityBadge({ priority }: { priority: IssuePriority }) {
  const isHigh = priority === "highest" || priority === "high";
  const isLow = priority === "lowest" || priority === "low";

  return (
    <Badge variant={isHigh ? "danger" : isLow ? "muted" : "warning"} className="rounded-[4px] px-2 py-0.5 text-[11px] font-medium">
      {isHigh ? <ArrowUp className="mr-1 h-3 w-3" /> : <ArrowDown className="mr-1 h-3 w-3" />}
      {issuePriorityLabel[priority]}
    </Badge>
  );
}

export function IssueTypeBadge({ type }: { type: IssueType }) {
  const Icon = type === "bug" ? Bug : type === "epic" ? Layers : type === "story" ? Flag : ListTodo;

  return (
    <Badge variant={type === "bug" ? "danger" : "secondary"} className="rounded-[4px] px-2 py-0.5 text-[11px] font-medium">
      <Icon className="mr-1 h-3 w-3" />
      {issueTypeLabel[type]}
    </Badge>
  );
}

export function BlockedBadge() {
  return (
    <Badge variant="danger" className="rounded-[4px] px-2 py-0.5 text-[11px] font-medium">
      <AlertTriangle className="mr-1 h-3 w-3" />
      阻塞
    </Badge>
  );
}

/** Simple colored status pill for use in compact tables (vs StatusBadge which uses icons) */
export function StatusPill({ status }: { status: IssueStatus }) {
  const className = {
    todo: "bg-[#E9F2FF] text-[#0C66E4]",
    "in-progress": "bg-[#FFF7ED] text-[#B76E00]",
    review: "bg-[#F3F0FF] text-[#5E4DB2]",
    done: "bg-[#E3FCEF] text-[#006644]",
    closed: "bg-[#F1F2F4] text-[#44546F]",
  }[status];

  return <span className={cn("rounded-[4px] px-2 py-1 text-[12px] font-semibold", className)}>{issueStatusLabel[status]}</span>;
}


export function PriorityInline({ priority }: { priority: IssuePriority }) {
  const isMedium = priority === "medium";
  const isLow = priority === "low" || priority === "lowest";
  const Icon = isLow ? ArrowDown : isMedium ? Code2 : ArrowUp;

  return (
    <span className={cn("inline-flex items-center gap-2 text-[13px]", priorityIconClassName[priority])}>
      <Icon className="h-4 w-4 stroke-[2.6]" />
      <span className="text-[#172B4D]">{issuePriorityLabel[priority]}</span>
    </span>
  );
}
