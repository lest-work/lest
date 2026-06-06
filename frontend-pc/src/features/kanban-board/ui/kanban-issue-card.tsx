import { useDraggable } from "@dnd-kit/core";
import { CSS } from "@dnd-kit/utilities";
import { Clock3, GripVertical, Lock, MessageCircle, MoreHorizontal, Paperclip } from "lucide-react";
import type { CSSProperties, ReactNode } from "react";

import { issuePriorityLabel, issueStatusLabel } from "@/entities/issue";
import type { BoardIssueCard } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button } from "@/shared/ui";

export interface KanbanIssueCardProps {
  issue: BoardIssueCard;
  quickTargets: Array<{ id: BoardIssueCard["status"]; title: string }>;
  onOpen: (issue: BoardIssueCard) => void;
  onQuickMove: (issue: BoardIssueCard, status: BoardIssueCard["status"]) => void;
}

export function KanbanIssueCard({ issue, quickTargets, onOpen, onQuickMove }: KanbanIssueCardProps) {
  const { attributes, listeners, setActivatorNodeRef, setNodeRef, transform, isDragging } = useDraggable({
    id: `issue:${issue.id}`,
    disabled: issue.status === "closed",
    data: {
      type: "issue",
      issue,
      status: issue.status,
    },
  });

  const style = {
    transform: CSS.Translate.toString(transform),
  };

  const dragHandle = (
    <Button
      ref={setActivatorNodeRef}
      className={cn("rounded-[4px] p-1 text-[#626F86] transition hover:bg-[#F1F2F4] hover:text-[#172B4D]", issue.status === "closed" && "cursor-not-allowed opacity-40")}
      aria-label={`拖拽 ${issue.key}`}
      onClick={(event) => event.stopPropagation()}
      {...listeners}
      {...attributes}
    >
      {issue.status === "closed" ? <Lock className="h-4 w-4" /> : <GripVertical className="h-4 w-4" />}
    </Button>
  );

  return (
    <KanbanIssueCardShell
      refCallback={setNodeRef}
      style={style}
      issue={issue}
      dragHandle={dragHandle}
      quickTargets={quickTargets}
      isDragging={isDragging}
      onOpen={() => onOpen(issue)}
      onQuickMove={(status) => onQuickMove(issue, status)}
    />
  );
}

export function KanbanIssueCardPreview({ issue }: { issue: BoardIssueCard }) {
  return <KanbanIssueCardShell issue={issue} isOverlay quickTargets={[]} />;
}

function KanbanIssueCardShell({
  issue,
  refCallback,
  style,
  dragHandle,
  quickTargets,
  isDragging,
  isOverlay,
  onOpen,
  onQuickMove,
}: {
  issue: BoardIssueCard;
  refCallback?: (node: HTMLElement | null) => void;
  style?: CSSProperties;
  dragHandle?: ReactNode;
  quickTargets: Array<{ id: BoardIssueCard["status"]; title: string }>;
  isDragging?: boolean;
  isOverlay?: boolean;
  onOpen?: () => void;
  onQuickMove?: (status: BoardIssueCard["status"]) => void;
}) {
  return (
    <article
      ref={refCallback}
      style={style}
      className={cn(
        "rounded-[6px] border border-[#DFE1E6] bg-white p-3 shadow-[0_1px_2px_rgba(9,30,66,0.08)] transition",
        "hover:border-[#A6C5E8] hover:shadow-[0_6px_18px_rgba(9,30,66,0.12)]",
        isDragging && "opacity-35",
        isOverlay && "w-[236px] rotate-1 border-[#0C66E4] shadow-[0_16px_36px_rgba(9,30,66,0.22)]",
      )}
      onClick={onOpen}
    >
      <div className="mb-2 flex items-center justify-between gap-2">
        <div className="flex min-w-0 items-center gap-1.5">
          {dragHandle}
          <span className="truncate text-[12px] font-semibold text-[#0C66E4]">{issue.key}</span>
        </div>
        <Button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#F1F2F4]" onClick={(event) => event.stopPropagation()}>
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </div>

      <h3 className="min-h-[40px] text-[13px] font-semibold leading-5 text-[#172B4D]">{issue.title}</h3>

      <div className="mt-3 flex items-center justify-between gap-2">
        <div className="flex items-center gap-1.5 text-[11px]">
          <span className="rounded-[3px] bg-[#E3FCEF] px-1.5 py-0.5 font-semibold text-[#006644]">{issue.type === "bug" ? "缺陷" : issue.type === "story" ? "需求" : "任务"}</span>
          <span className="rounded-[3px] bg-[#FFEBE6] px-1.5 py-0.5 font-semibold text-[#DE350B]">{issuePriorityLabel[issue.priority as IssuePriority]}</span>
        </div>
        {issue.assignee ? <Avatar name={issue.assignee.name} imageSrc={issue.assignee.avatar} className="h-6 w-6" /> : null}
      </div>

      <div className="mt-3 flex items-center justify-between text-[11px] text-[#626F86]">
        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1">
            <MessageCircle className="h-3.5 w-3.5" />
            {issue.estimate ?? 2}
          </span>
          <span className="inline-flex items-center gap-1">
            <Paperclip className="h-3.5 w-3.5" />
            {issue.labels.length}
          </span>
        </div>
        <span className="inline-flex items-center gap-1">
          <Clock3 className="h-3.5 w-3.5" />
          {issue.estimate ?? 1}h
        </span>
      </div>

      {quickTargets.length ? (
        <div className="mt-3 flex flex-wrap gap-1">
          {quickTargets.slice(0, 2).map((target) => (
            <Button
              key={target.id}
              className="rounded-[3px] bg-[#F4F5F7] px-2 py-1 text-[11px] font-medium text-[#44546F] hover:bg-[#DEEBFF] hover:text-[#0C66E4]"
              onClick={(event) => {
                event.stopPropagation();
                onQuickMove?.(target.id);
              }}
            >
              移至{issueStatusLabel[target.id as IssueStatus]}
            </Button>
          ))}
        </div>
      ) : null}
    </article>
  );
}
