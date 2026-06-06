import { DndContext, DragEndEvent, DragOverEvent, DragOverlay, DragStartEvent, KeyboardSensor, PointerSensor, closestCorners, useSensor, useSensors } from "@dnd-kit/core";
import { AlertTriangle, CalendarDays, Download, Filter, Grid2X2, List, MoreHorizontal, Play, Plus, Save, Settings2, SquareCheckBig } from "lucide-react";
import { useCallback, useEffect, useState } from "react";

import { type IssueStatus } from "@/entities/issue";
import type { BoardIssueCard, BoardIssueDetailResponse, BoardResponse, BoardSprint, BoardWipWarning } from "@/api/project";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import { projectWorkspaceService } from "@/api/project";
import { BoardColumnSettingsDialog, BoardIssueDetailDrawer, canDropBoardIssue, KanbanColumn, KanbanIssueCardPreview, SprintCloseDialog, SprintCreateDialog, SprintStartDialog } from "@/features/kanban-board";
import { cn } from "@/shared/lib/utils";
import { Button, Input } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

type BoardNotice = { message: string; tone: "success" | "warning" | "error" | "info" };

const noticeClassName: Record<BoardNotice["tone"], string> = {
  success: "border-[#ABF5D1] bg-[#E3FCEF] text-[#006644]",
  warning: "border-[#FFE2BD] bg-[#FFF7ED] text-[#974F0C]",
  error: "border-[#FFD5CC] bg-[#FFF4F2] text-[#AE2E24]",
  info: "border-[#CCE0FF] bg-[#F0F6FF] text-[#0C66E4]",
};

export function KanbanPage({ onNavigate }: { onNavigate: (page: WorkspacePageKey) => void }) {
  const [data, setData] = useState<BoardResponse>();
  const [sprints, setSprints] = useState<BoardSprint[]>([]);
  const [keyword, setKeyword] = useState("");
  const [notice, setNotice] = useState<BoardNotice>();
  const [wipWarning, setWipWarning] = useState<BoardWipWarning>();
  const [activeIssue, setActiveIssue] = useState<BoardIssueCard>();
  const [overStatus, setOverStatus] = useState<IssueStatus>();
  const [detail, setDetail] = useState<BoardIssueDetailResponse>();
  const [detailSaving, setDetailSaving] = useState(false);
  const [settingsColumnId, setSettingsColumnId] = useState<IssueStatus>();
  const [showCreateSprint, setShowCreateSprint] = useState(false);
  const [startSprintId, setStartSprintId] = useState<string>();
  const [closeSprintId, setCloseSprintId] = useState<string>();
  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
    useSensor(KeyboardSensor),
  );

  const loadBoard = useCallback(async (nextKeyword = keyword) => {
    const response = await projectWorkspaceService.getBoard({ projectKey: "alpha-platform", groupBy: "status", keyword: nextKeyword });
    setData(response);
  }, [keyword]);

  const loadSprints = useCallback(async () => {
    const response = await projectWorkspaceService.getBoardSprints({ projectKey: "alpha-platform", includeCompleted: true });
    setSprints(response.sprints);
  }, []);

  useEffect(() => {
    void loadBoard("");
    void loadSprints();
  }, [loadBoard, loadSprints]);

  function resolveOverStatus(event: DragOverEvent | DragEndEvent): IssueStatus | undefined {
    const status = event.over?.data.current?.status as IssueStatus | undefined;
    if (status) {
      return status;
    }
    const overId = event.over?.id ? String(event.over.id) : "";
    if (overId.startsWith("column:")) {
      return overId.replace("column:", "") as IssueStatus;
    }
    return undefined;
  }

  async function openBoardDetail(issueId: string) {
    const response = await projectWorkspaceService.getBoardIssueDetail({ projectKey: "alpha-platform", issueId });
    setDetail(response);
  }

  async function moveIssue(issue: BoardIssueCard, status: IssueStatus) {
    const response = await projectWorkspaceService.moveBoardIssue({
      projectKey: "alpha-platform",
      issueId: issue.id,
      sourceStatus: issue.status,
      targetStatus: status,
      targetIndex: 0,
    });
    setNotice({
      message: response.message,
      tone: response.result === "error" ? "error" : response.result === "warning" ? "warning" : response.result === "canceled" ? "info" : "success",
    });
    setWipWarning(response.wipWarning);
    await loadBoard();
    if (detail?.card.id === issue.id) {
      await openBoardDetail(issue.id);
    }
  }

  function handleDragStart(event: DragStartEvent) {
    const issue = event.active.data.current?.issue as BoardIssueCard | undefined;
    setActiveIssue(issue);
  }

  function handleDragOver(event: DragOverEvent) {
    setOverStatus(resolveOverStatus(event));
  }

  async function handleDragEnd(event: DragEndEvent) {
    const issue = event.active.data.current?.issue as BoardIssueCard | undefined;
    const targetStatus = resolveOverStatus(event);
    setActiveIssue(undefined);
    setOverStatus(undefined);

    if (!issue || !targetStatus) {
      setNotice({ message: "已取消移动", tone: "info" });
      return;
    }
    if (!canDropBoardIssue(issue, targetStatus)) {
      setNotice({ message: targetStatus === "closed" ? "需要先完成后才能关闭" : "只读卡片不能拖拽", tone: "error" });
      return;
    }
    await moveIssue(issue, targetStatus);
  }

  const activeSprint = sprints.find((sprint) => sprint.status === "active");
  const planningSprint = sprints.find((sprint) => sprint.status === "planning");

  return (
    <ProjectWorkspaceShell
      activePage="kanban"
      title="看板"
      subtitle="按状态分组管理问题流转，支持快速筛选、视图保存和状态更新。"
      onNavigate={onNavigate}
      actions={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white">
            <Settings2 className="h-4 w-4" />
            看板设置
          </Button>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white">
            <Save className="h-4 w-4" />
            保存视图
          </Button>
          <Button variant="outline" size="icon" className="h-9 w-10 rounded-[4px] bg-white">
            <MoreHorizontal className="h-4 w-4" />
          </Button>
        </>
      }
    >
      <div className="mb-4 rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.06)]">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="min-w-0">
            <div className="flex flex-wrap items-center gap-2">
              <span className="rounded-[3px] bg-[#E9F2FF] px-2 py-1 text-[11px] font-bold text-[#0C66E4]">Sprint 管理</span>
              <h2 className="text-[15px] font-bold text-[#172B4D]">{activeSprint?.name ?? planningSprint?.name ?? "暂无进行中的 Sprint"}</h2>
              <span className={cn("rounded-[3px] px-2 py-1 text-[11px] font-bold", activeSprint ? "bg-[#E3FCEF] text-[#006644]" : planningSprint ? "bg-[#FFF7ED] text-[#974F0C]" : "bg-[#F4F5F7] text-[#626F86]")}>
                {activeSprint ? "进行中" : planningSprint ? "未开始" : "待创建"}
              </span>
            </div>
            <div className="mt-2 flex flex-wrap items-center gap-4 text-[12px] text-[#626F86]">
              <span className="inline-flex items-center gap-1"><CalendarDays className="h-3.5 w-3.5" />{(activeSprint ?? planningSprint)?.startDate ?? "--"} 至 {(activeSprint ?? planningSprint)?.endDate ?? "--"}</span>
              <span>容量 {(activeSprint ?? planningSprint)?.capacity ?? 0}</span>
              <span>WIP {(activeSprint ?? planningSprint)?.wipLimit ?? 0} 张/列</span>
              <span>剩余 {(activeSprint ?? planningSprint)?.remainingIssueCount ?? 0} 项</span>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <Button variant="outline" className="h-9 rounded-[4px] bg-white" onClick={() => setShowCreateSprint(true)}>
              <Plus className="h-4 w-4" />
              创建 Sprint
            </Button>
            <Button variant="outline" className="h-9 rounded-[4px] bg-white" disabled={!planningSprint} onClick={() => planningSprint ? setStartSprintId(planningSprint.id) : undefined}>
              <Play className="h-4 w-4" />
              启动 Sprint
            </Button>
            <Button variant="outline" className="h-9 rounded-[4px] bg-white" disabled={!activeSprint} onClick={() => activeSprint ? setCloseSprintId(activeSprint.id) : undefined}>
              <SquareCheckBig className="h-4 w-4" />
              结束 Sprint
            </Button>
          </div>
        </div>
      </div>

      <div className="mb-4 flex flex-wrap items-center gap-3">
        {["按状态分组", "所有类型", "所有优先级", "所有负责人", "标签"].map((item) => (
          <Button key={item} className="inline-flex h-9 items-center gap-2 rounded-[4px] border border-[#DFE1E6] bg-white px-4 text-[13px] font-medium text-[#172B4D] hover:bg-[#F4F5F7]">
            {item}
          </Button>
        ))}
        <Button className="inline-flex h-9 items-center gap-2 rounded-[4px] border border-[#DFE1E6] bg-white px-4 text-[13px] font-medium text-[#172B4D] hover:bg-[#F4F5F7]">
          更多筛选
          <Filter className="h-4 w-4" />
        </Button>
        <div className="ml-auto flex items-center gap-2">
          <Input
            className="h-9 w-[260px] rounded-[4px] border-[#DFE1E6] text-[13px]"
            placeholder="搜索看板问题"
            value={keyword}
            onChange={(event) => {
              setKeyword(event.target.value);
              void loadBoard(event.target.value);
            }}
          />
          <Button variant="outline" size="icon" className="h-9 w-9 rounded-[4px] bg-white text-[#0C66E4]">
            <Grid2X2 className="h-4 w-4" />
          </Button>
          <Button variant="outline" size="icon" className="h-9 w-9 rounded-[4px] bg-white">
            <List className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {notice ? <div className={cn("mb-3 rounded-[4px] border px-4 py-2 text-[13px] font-medium", noticeClassName[notice.tone])}>{notice.message}</div> : null}
      {wipWarning ? (
        <div className="mb-3 flex flex-wrap items-start justify-between gap-3 rounded-[4px] border border-[#FF8F73] bg-[#FFF4F2] px-4 py-3 text-[13px] text-[#AE2E24]">
          <div className="flex items-start gap-3">
            <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
            <div>
              <div className="font-bold">{wipWarning.message}</div>
              <div className="mt-1 text-[12px]">当前 {wipWarning.currentCount} 张，限制 {wipWarning.wipLimit} 张，超出 {wipWarning.exceededBy} 张。</div>
              <div className="mt-2 flex flex-wrap gap-2">
                {wipWarning.suggestions?.map((suggestion: string) => (
                  <span key={suggestion} className="rounded-[3px] bg-white px-2 py-1 text-[11px] font-bold text-[#974F0C]">{suggestion}</span>
                ))}
              </div>
            </div>
          </div>
          <Button className="text-[12px] font-bold text-[#0C66E4]" onClick={() => setWipWarning(undefined)}>知道了</Button>
        </div>
      ) : null}

      <DndContext sensors={sensors} collisionDetection={closestCorners} onDragStart={handleDragStart} onDragOver={handleDragOver} onDragCancel={() => { setActiveIssue(undefined); setOverStatus(undefined); }} onDragEnd={(event) => void handleDragEnd(event)}>
        <div className="grid gap-4 xl:grid-cols-5">
          {data?.columns.map((column) => (
            <KanbanColumn
              key={column.id}
              column={column}
              columns={data.columns}
              activeIssue={activeIssue}
              overStatus={overStatus}
              onOpenIssue={(issue) => void openBoardDetail(issue.id)}
              onMoveIssue={(issue, status) => void moveIssue(issue, status)}
              onOpenSettings={(columnId) => setSettingsColumnId(columnId)}
            />
          ))}
        </div>
        <DragOverlay>{activeIssue ? <KanbanIssueCardPreview issue={activeIssue} /> : null}</DragOverlay>
      </DndContext>

      <div className="mt-4 grid gap-4 xl:grid-cols-6">
        {data?.metrics.map((metric) => (
          <div key={metric.id} className="rounded-[8px] border border-[#DFE1E6] bg-white p-4">
            <div className="text-[13px] text-[#626F86]">{metric.label}</div>
            <div className="mt-2 text-[26px] font-semibold text-[#172B4D]">{metric.value}</div>
            <div className="mt-2 h-1.5 rounded-full bg-[#EBECF0]">
              <div className="h-full rounded-full" style={{ width: `${Math.min(metric.value, 100)}%`, backgroundColor: metric.color }} />
            </div>
            <div className="mt-2 text-[12px] text-[#626F86]">{metric.helper}</div>
          </div>
        ))}
      </div>

      <div className="mt-5 flex gap-3">
        <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white">
          <Plus className="h-4 w-4" />
          新建问题
        </Button>
        <Button variant="outline" className="h-9 rounded-[4px] bg-white">
          批量操作
        </Button>
        <Button variant="outline" className="h-9 rounded-[4px] bg-white">
          <Download className="h-4 w-4" />
          导出看板
        </Button>
      </div>

      {detail ? (
        <BoardIssueDetailDrawer
          detail={detail}
          saving={detailSaving}
          onClose={() => setDetail(undefined)}
          onMoveStatus={async (status) => {
            setDetailSaving(true);
            try {
              await moveIssue(detail.card, status);
              await openBoardDetail(detail.card.id);
            } finally {
              setDetailSaving(false);
            }
          }}
        />
      ) : null}
      {settingsColumnId ? (
        <BoardColumnSettingsDialog
          projectKey="alpha-platform"
          columnId={settingsColumnId}
          onClose={() => setSettingsColumnId(undefined)}
          onSaved={(message) => {
            setSettingsColumnId(undefined);
            setNotice({ message, tone: "success" });
            void loadBoard();
          }}
        />
      ) : null}
      {showCreateSprint ? (
        <SprintCreateDialog
          projectKey="alpha-platform"
          onClose={() => setShowCreateSprint(false)}
          onCreated={(message) => {
            setShowCreateSprint(false);
            setNotice({ message, tone: "success" });
            void loadSprints();
          }}
        />
      ) : null}
      {startSprintId ? (
        <SprintStartDialog
          projectKey="alpha-platform"
          sprintId={startSprintId}
          onClose={() => setStartSprintId(undefined)}
          onStarted={(message) => {
            setStartSprintId(undefined);
            setNotice({ message, tone: "success" });
            void loadSprints();
          }}
        />
      ) : null}
      {closeSprintId ? (
        <SprintCloseDialog
          projectKey="alpha-platform"
          sprintId={closeSprintId}
          onClose={() => setCloseSprintId(undefined)}
          onClosed={(message) => {
            setCloseSprintId(undefined);
            setNotice({ message, tone: "success" });
            void loadSprints();
          }}
        />
      ) : null}
    </ProjectWorkspaceShell>
  );
}
