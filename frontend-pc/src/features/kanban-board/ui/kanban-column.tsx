import { useDroppable } from "@dnd-kit/core";
import { MoreHorizontal, Plus } from "lucide-react";

import { type IssueStatus } from "@/entities/issue";
import type { BoardColumn as BoardColumnData, BoardIssueCard } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui";
import { boardDropMessage, canDropBoardIssue } from "../model/rules";
import { KanbanIssueCard } from "./kanban-issue-card";

const statusDot: Record<IssueStatus, string> = {
  todo: "#6B7280",
  "in-progress": "#0C66E4",
  review: "#FF9F1A",
  done: "#22A06B",
  closed: "#9CA3AF",
};

export interface KanbanColumnProps {
  column: BoardColumnData;
  columns: BoardColumnData[];
  activeIssue?: BoardIssueCard;
  overStatus?: IssueStatus;
  onOpenIssue: (issue: BoardIssueCard) => void;
  onMoveIssue: (issue: BoardIssueCard, status: IssueStatus) => void;
  onOpenSettings: (columnId: IssueStatus) => void;
}

export function KanbanColumn({ column, columns, activeIssue, overStatus, onOpenIssue, onMoveIssue, onOpenSettings }: KanbanColumnProps) {
  const { setNodeRef, isOver } = useDroppable({
    id: `column:${column.id}`,
    data: {
      type: "column",
      status: column.id,
    },
  });
  const isActiveDrop = overStatus === column.id || isOver;
  const canDrop = activeIssue ? canDropBoardIssue(activeIssue, column.id) : true;
  const quickTargets = columns.filter((target) => target.id !== column.id).map((target) => ({ id: target.id, title: target.title }));
  const wipSeverity = column.wipLimit && column.count > column.wipLimit ? "exceeded" : column.wipLimit && column.count === column.wipLimit ? "warning" : "normal";

  return (
    <section
      ref={setNodeRef}
      className={cn(
        "min-h-[500px] rounded-[8px] border border-[#DFE1E6] bg-[#FAFBFC] p-3 transition",
        isActiveDrop && canDrop && "border-[#22A06B] bg-[#F3FCF7] ring-2 ring-[#22C55E]/20",
        isActiveDrop && !canDrop && "border-[#FF8F73] bg-[#FFF4F2] ring-2 ring-[#E34935]/15",
      )}
    >
      <div className="mb-3 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: statusDot[column.id] }} />
          <h2 className="text-[14px] font-bold text-[#172B4D]">{column.title}</h2>
          <span className="text-[12px] text-[#626F86]">{column.count}</span>
        </div>
        <Button variant="outline" size="icon" className="h-7 w-7 rounded-[4px] bg-white" onClick={() => onOpenSettings(column.id)}>
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </div>
      {column.wipLimit ? (
        <div
          className={cn(
            "mb-3 flex items-center justify-between rounded-[4px] border px-2 py-1 text-[11px] font-semibold",
            wipSeverity === "exceeded" && "border-[#FF8F73] bg-[#FFEBE6] text-[#DE350B]",
            wipSeverity === "warning" && "border-[#FFE2BD] bg-[#FFF7ED] text-[#974F0C]",
            wipSeverity === "normal" && "border-[#CCE0FF] bg-[#E9F2FF] text-[#0C66E4]",
          )}
        >
          <span>WIP {column.count}/{column.wipLimit}</span>
          {wipSeverity === "exceeded" ? <span>超限 {column.count - column.wipLimit}</span> : wipSeverity === "warning" ? <span>即将超限</span> : null}
        </div>
      ) : null}

      {activeIssue && isActiveDrop ? (
        <div className={cn("mb-3 rounded-[4px] border px-2 py-2 text-[12px] font-medium", canDrop ? "border-[#ABF5D1] bg-[#E3FCEF] text-[#216E4E]" : "border-[#FFD5CC] bg-[#FFF4F2] text-[#AE2E24]")}>
          {boardDropMessage(activeIssue, column.id)}
        </div>
      ) : null}

      <div className="space-y-2">
        {column.issues.map((issue) => (
          <KanbanIssueCard key={issue.id} issue={issue} quickTargets={quickTargets} onOpen={onOpenIssue} onQuickMove={onMoveIssue} />
        ))}
        <Button className="flex w-full items-center gap-2 rounded-[4px] px-3 py-2 text-left text-[13px] font-medium text-[#44546F] hover:bg-[#EBECF0]">
          <Plus className="h-4 w-4" />
          添加问题
        </Button>
      </div>
    </section>
  );
}
