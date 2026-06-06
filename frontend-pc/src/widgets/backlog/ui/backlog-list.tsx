import { DndContext, DragEndEvent, KeyboardSensor, PointerSensor, closestCenter, useSensor, useSensors } from "@dnd-kit/core";
import { SortableContext, sortableKeyboardCoordinates, useSortable, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useMemo } from "react";
import {
  CheckCircle2,
  GripVertical,
  MoreHorizontal,
  RotateCcw,
  ShieldAlert,
} from "lucide-react";

import { type Issue } from "@/entities/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Checkbox, DataTable } from "@/shared/ui";

const referenceRows: Record<string, { title: string; assignee: string; sprint: string; createdAt: string }> = {
  "ALP-128": { title: "修复页面刷新后丢失状态", assignee: "张晓明", sprint: "Sprint 12", createdAt: "2024-05-20" },
  "ALP-135": { title: "用户批量导入功能", assignee: "李华", sprint: "Sprint 12", createdAt: "2024-05-19" },
  "ALP-142": { title: "权限系统重构设计", assignee: "王芳", sprint: "Sprint 13", createdAt: "2024-05-18" },
  "ALP-150": { title: "移动端适配优化", assignee: "刘强", sprint: "Sprint 12", createdAt: "2024-05-17" },
  "ALP-158": { title: "数据导出支持 Excel", assignee: "陈静", sprint: "Sprint 13", createdAt: "2024-05-16" },
  "ALP-162": { title: "添加接口单元测试", assignee: "周杰", sprint: "Sprint 13", createdAt: "2024-05-15" },
  "ALP-171": { title: "修复报表统计错误", assignee: "张晓明", sprint: "Sprint 11", createdAt: "2024-05-14" },
  "ALP-172": { title: "优化登录性能", assignee: "李华", sprint: "Sprint 12", createdAt: "2024-05-14" },
};

function getIssueDisplayMeta(issue: Issue) {
  const displayKey = issue.key.replace(/^LST-/, "ALP-");
  const reference = referenceRows[displayKey];

  return {
    displayKey,
    title: reference?.title ?? issue.title,
    assigneeName: reference?.assignee ?? issue.assignee?.name,
    createdDate: reference?.createdAt ?? issue.createdAt ?? `2024-05-${String(Math.max(1, 21 - Math.min(issue.rank - 1, 19))).padStart(2, "0")}`,
    sprintName: reference?.sprint ?? (issue.sprintId ? (issue.rank % 3 === 0 ? "Sprint 13" : "Sprint 12") : "待办"),
  };
}

export interface BacklogListProps {
  issues: Issue[];
  notice?: string;
  canDrag?: boolean;
  isReordering?: boolean;
  viewMode?: "list" | "group";
  onReorder: (activeId: string, overId: string) => void;
  onOpenIssue?: (issue: Issue) => void;
  onClearNotice?: () => void;
}

function renderTypeIconAndText(type: string) {
  switch (type) {
    case "bug":
      return (
        <div className="inline-flex items-center gap-1.5">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#DE350B]"><circle cx="12" cy="12" r="10" fill="currentColor" /><path d="M12 8v8M8 12h8" stroke="white" strokeWidth="2" /></svg>
          <span className="text-[13px] text-[#42526E]">缺陷</span>
        </div>
      );
    case "story":
      return (
        <div className="inline-flex items-center gap-1.5">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#FF991F]"><circle cx="12" cy="12" r="10" fill="currentColor" /><path d="M12 7l1 5h5l-4 3 2 5-4-3-4 3 2-5-4-3h5z" fill="white" /></svg>
          <span className="text-[13px] text-[#42526E]">需求</span>
        </div>
      );
    default:
      return (
        <div className="inline-flex items-center gap-1.5">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#36B37E]"><rect width="18" height="18" x="3" y="3" rx="3" fill="currentColor" /><path d="M7 12l3 3 7-7" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" fill="none" /></svg>
          <span className="text-[13px] text-[#42526E]">任务</span>
        </div>
      );
  }
}

function renderPriority(priority: string) {
  switch (priority) {
    case "highest":
      return (
        <span className="inline-flex items-center gap-1 text-[13px] font-bold text-[#DE350B]">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#DE350B]" stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round"><path d="M4 15l8-8 8 8" /><path d="M4 21l8-8 8 8" /></svg>
          最高
        </span>
      );
    case "high":
      return (
        <span className="inline-flex items-center gap-1 text-[13px] font-bold text-[#FF5630]">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#FF5630]" stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round"><path d="M4 17l8-8 8 8" /></svg>
          高
        </span>
      );
    case "low":
      return (
        <span className="inline-flex items-center gap-1 text-[13px] font-bold text-[#36B37E]">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#36B37E]" stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round"><path d="M4 7l8 8 8-8" /></svg>
          低
        </span>
      );
    case "lowest":
      return (
        <span className="inline-flex items-center gap-1 text-[13px] font-bold text-[#4C9AFF]">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#4C9AFF]" stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round"><path d="M4 5l8 8 8-8" /><path d="M4 11l8 8 8-8" /></svg>
          最低
        </span>
      );
    default:
      return (
        <span className="inline-flex items-center gap-1 text-[13px] font-bold text-[#FFAB00]">
          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" className="shrink-0 text-[#FFAB00]" stroke="currentColor" strokeWidth="4" strokeLinecap="round"><path d="M5 12h14" /></svg>
          中
        </span>
      );
  }
}

function renderStatus(status: string) {
  switch (status) {
    case "todo":
      return <span className="inline-flex items-center rounded border border-[#DFE1E6]/30 bg-[#F1F2F4] px-2 py-0.5 text-[12px] font-bold text-[#44546F]">未开始</span>;
    case "in-progress":
      return <span className="inline-flex items-center rounded bg-[#DEEBFF] px-2 py-0.5 text-[12px] font-bold text-[#0747A6]">进行中</span>;
    case "review":
      return <span className="inline-flex items-center rounded bg-[#FFF0B3] px-2 py-0.5 text-[12px] font-bold text-[#A54800]">已评审</span>;
    case "done":
      return <span className="inline-flex items-center rounded bg-[#E3FCEF] px-2 py-0.5 text-[12px] font-bold text-[#006644]">已完成</span>;
    default:
      return <span className="inline-flex items-center rounded bg-[#F4F5F7] px-2 py-0.5 text-[12px] font-bold text-[#42526E]">已关闭</span>;
  }
}

function BacklogDragHandle({ issue, canDrag }: { issue: Issue; canDrag: boolean }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: issue.id,
    disabled: !canDrag || issue.blocked,
  });

  return (
    <span
      ref={setNodeRef}
      style={{ transform: CSS.Transform.toString(transform), transition }}
      className={cn("inline-flex", isDragging && "relative z-10")}
    >
      {canDrag && !issue.blocked ? (
        <Button
          className="-ml-1 rounded-[3px] p-0.5 text-[#97A0AF] opacity-0 transition hover:bg-[#EBECF0] hover:text-[#0052CC] group-hover:opacity-100"
          aria-label={`拖拽 ${getIssueDisplayMeta(issue).displayKey}`}
          onClick={(event) => event.stopPropagation()}
          {...attributes}
          {...listeners}
        >
          <GripVertical className="h-3.5 w-3.5" />
        </Button>
      ) : null}
    </span>
  );
}

export function BacklogList({
  issues,
  notice,
  canDrag = true,
  isReordering,
  viewMode = "list",
  onReorder,
  onOpenIssue,
  onClearNotice,
}: BacklogListProps) {
  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  );
  const backlogIssues = [...issues].sort((first, second) => first.rank - second.rank);
  const itemIds = backlogIssues.map((issue) => issue.id);

  const iterationGroups = useMemo(() => {
    const groups = new Map<string, Issue[]>();
    backlogIssues.forEach((issue) => {
      const { sprintName } = getIssueDisplayMeta(issue);
      const current = groups.get(sprintName) ?? [];
      current.push(issue);
      if (!groups.has(sprintName)) {
        groups.set(sprintName, current);
      }
    });
    return Array.from(groups.entries()).map(([name, list]) => ({ name, list }));
  }, [backlogIssues]);

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    if (!over || active.id === over.id || isReordering) {
      return;
    }
    onReorder(String(active.id), String(over.id));
  }

  const renderTable = (list: Issue[], showHeader = true) => (
    <DataTable<Issue>
      className="min-w-[860px]"
      columns={[
        {
          title: <Checkbox className="h-4 w-4" />,
          dataIndex: "id",
          width: 36,
          render: () => <Checkbox className="h-4 w-4" onClick={(event) => event.stopPropagation()} />,
        },
        {
          title: "Key",
          dataIndex: "key",
          width: 82,
          render: (_, issue) => <span className="font-semibold text-[#0052CC] hover:underline">{getIssueDisplayMeta(issue).displayKey}</span>,
        },
        {
          title: "标题",
          dataIndex: "title",
          render: (_, issue) => {
            const { title, displayKey } = getIssueDisplayMeta(issue);

            return (
              <div className="flex items-center gap-2 text-[13px]">
                <BacklogDragHandle issue={issue} canDrag={canDrag} />
                <div className="line-clamp-1 font-medium text-[#172B4D]">{title}</div>
                {issue.blocked ? <span className="rounded-[3px] bg-[#FFEBE6] px-1.5 text-[11px] font-bold text-[#DE350B]">阻塞</span> : null}
                <span className="sr-only">{displayKey}</span>
              </div>
            );
          },
        },
        { title: "类型", dataIndex: "type", width: 76, render: (type) => renderTypeIconAndText(String(type)) },
        { title: "优先级", dataIndex: "priority", width: 76, render: (priority) => renderPriority(String(priority)) },
        { title: "状态", dataIndex: "status", width: 80, render: (status) => renderStatus(String(status)) },
        {
          title: "负责人",
          dataIndex: "assignee",
          width: 112,
          render: (_, issue) => {
            const { assigneeName } = getIssueDisplayMeta(issue);

            return (
              <div className="flex items-center gap-2">
                {assigneeName ? (
                  <>
                    <Avatar name={assigneeName} imageSrc={issue.assignee?.avatarUrl} className="h-6 w-6 text-[10px]" />
                    <span className="text-[12px] font-medium text-[#42526E]">{assigneeName}</span>
                  </>
                ) : (
                  <span className="text-[12px] text-[#97A0AF]">未分配</span>
                )}
              </div>
            );
          },
        },
        {
          title: "迭代/版本",
          dataIndex: "sprintId",
          width: 94,
          render: (_, issue) => <span className="text-[12px] text-[#6B778C]">{getIssueDisplayMeta(issue).sprintName}</span>,
        },
        {
          title: "创建时间",
          dataIndex: "createdAt",
          width: 100,
          render: (_, issue) => <span className="text-[12px] text-[#6B778C]">{getIssueDisplayMeta(issue).createdDate}</span>,
        },
        {
          title: "操作",
          dataIndex: "id",
          width: 44,
          align: "right",
          render: (_, issue) => (
            <Button
              className="rounded-[3px] p-1 text-[#172B4D] transition hover:bg-[#EBECF0]"
              onClick={(event) => event.stopPropagation()}
              aria-label={`${getIssueDisplayMeta(issue).displayKey} 更多操作`}
            >
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          ),
        },
      ]}
      dataSource={list}
      locale={{ emptyText: <div className="py-12 text-center text-[13px] text-[#6B778C]">暂无对应问题</div> }}
      pagination={false}
      rowKey="id"
      rowClassName={(issue) =>
        cn(
          "group cursor-pointer border-b border-[#EBECF0] bg-white text-[13px] text-[#172B4D] transition hover:bg-[#F4F5F7]",
          issue.blocked && "bg-[#FAFBFC] text-[#97A0AF]",
        )
      }
      onRow={(issue) => ({ onClick: () => onOpenIssue?.(issue) })}
      showHeader={showHeader}
      size="small"
    />
  );

  return (
    <div className="w-full">
      {notice && (
        <div className="mb-4 flex items-center justify-between gap-3 rounded-[4px] border border-[#7EE2B8] bg-[#DCFFF1] px-3 py-2 text-xs text-[#216E4E]">
          <span className="inline-flex items-center gap-2">
            <CheckCircle2 className="h-4 w-4 shrink-0" />
            {notice}
          </span>
          <Button className="inline-flex items-center gap-1 font-medium hover:underline" onClick={onClearNotice}>
            <RotateCcw className="h-3.5 w-3.5" />
            撤销
          </Button>
        </div>
      )}

      {!canDrag && (
        <div className="mb-4 flex items-center gap-2 rounded-[4px] border border-[#F5CD47] bg-[#FFF7D6] px-3 py-2 text-xs text-[#7F5F01]">
          <ShieldAlert className="h-4 w-4 shrink-0" />
          当前角色无权调整待办事项排序。
        </div>
      )}

      <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
        <SortableContext items={itemIds} strategy={verticalListSortingStrategy}>
          <div className="workspace-scrollbar overflow-x-auto">
            {viewMode === "list" ? (
              renderTable(backlogIssues, true)
            ) : (
              <div className="space-y-4">
                {iterationGroups.map((group) => (
                  <div key={group.name} className="rounded-[6px] border border-[#EBECF0] bg-white shadow-sm">
                    <div className="flex items-center justify-between border-b border-[#EBECF0] px-4 py-3">
                      <div className="flex items-center gap-2">
                        <span className="h-2.5 w-2.5 shrink-0 rounded-full bg-[#0C66E4]" />
                        <span className="text-[13px] font-bold text-[#172B4D]">{group.name}</span>
                        <span className="text-[12px] text-[#6B778C]">迭代/版本</span>
                      </div>
                      <span className="rounded-[4px] bg-[#F4F5F7] px-2 py-0.5 text-[12px] font-semibold text-[#42526E]">
                        {group.list.length} 个问题
                      </span>
                    </div>
                    <div className="px-3 py-2">
                      {renderTable(group.list, false)}
                    </div>
                  </div>
                ))}
                {iterationGroups.length === 0 ? (
                  <div className="rounded-[6px] border border-[#EBECF0] bg-white py-12 text-center text-[13px] text-[#6B778C]">
                    暂无对应问题
                  </div>
                ) : null}
                      </div>
            )}
          </div>
        </SortableContext>
      </DndContext>
    </div>
  );
}
