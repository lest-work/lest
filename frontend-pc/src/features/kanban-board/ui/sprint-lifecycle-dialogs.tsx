import { AlertTriangle, CheckCircle2, Info, XCircle } from "lucide-react";
import { useEffect, useState } from "react";

import type { BoardSprint, CloseSprintRequest, SprintCheckItem, SprintClosePreviewResponse, SprintStartPreviewResponse } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button, Checkbox, Input, Radio, Textarea } from "@/shared/ui";
import { BoardDialogFrame } from "./board-dialog-frame";

function checkTone(status: SprintCheckItem["status"]) {
  if (status === "passed") return { className: "text-[#22A06B]", icon: CheckCircle2 };
  if (status === "warning") return { className: "text-[#FF9F1A]", icon: AlertTriangle };
  if (status === "failed") return { className: "text-[#DE350B]", icon: XCircle };
  return { className: "text-[#0C66E4]", icon: Info };
}

function CheckList({ checks }: { checks: SprintCheckItem[] }) {
  return (
    <div className="space-y-2">
      {checks.map((check) => {
        const tone = checkTone(check.status);
        const Icon = tone.icon;
        return (
          <div key={check.id} className="flex items-start gap-3 rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2">
            <Icon className={cn("mt-0.5 h-4 w-4 shrink-0", tone.className)} />
            <div>
              <div className="text-[13px] font-bold text-[#172B4D]">{check.label}</div>
              <div className="mt-0.5 text-[12px] text-[#626F86]">{check.description}</div>
            </div>
          </div>
        );
      })}
    </div>
  );
}

function SprintInfo({ sprint }: { sprint: BoardSprint }) {
  return (
    <div className="rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
      <div className="mb-3 text-[13px] font-bold text-[#172B4D]">Sprint 信息</div>
      <dl className="grid grid-cols-2 gap-x-4 gap-y-2 text-[12px]">
        <dt className="text-[#626F86]">Sprint 名称</dt><dd className="font-bold text-[#172B4D]">{sprint.name}</dd>
        <dt className="text-[#626F86]">开始日期</dt><dd className="font-bold text-[#172B4D]">{sprint.startDate}</dd>
        <dt className="text-[#626F86]">结束日期</dt><dd className="font-bold text-[#172B4D]">{sprint.endDate}</dd>
        <dt className="text-[#626F86]">持续时间</dt><dd className="font-bold text-[#172B4D]">{sprint.durationDays} 天</dd>
      </dl>
    </div>
  );
}

export function SprintStartDialog({ projectKey, sprintId, onClose, onStarted }: { projectKey: string; sprintId: string; onClose: () => void; onStarted: (message: string) => void }) {
  const [preview, setPreview] = useState<SprintStartPreviewResponse>();
  const [saving, setSaving] = useState(false);
  const [confirmWarnings, setConfirmWarnings] = useState(false);

  useEffect(() => {
    void projectWorkspaceService.getSprintStartPreview({ projectKey, sprintId }).then(setPreview);
  }, [projectKey, sprintId]);

  async function handleStart() {
    setSaving(true);
    try {
      const response = await projectWorkspaceService.startSprint({ projectKey, sprintId, confirmWarnings });
      if (response.result === "success") {
        onStarted(response.message);
      } else if (response.result === "warning") {
        setConfirmWarnings(true);
      }
    } finally {
      setSaving(false);
    }
  }

  const hasWarnings = preview?.riskLevel === "warning";

  return (
    <BoardDialogFrame
      title="启动 Sprint"
      description="确认并检查以下信息，启动后 Sprint 状态将变更为“进行中”。"
      widthClassName="max-w-[680px]"
      onClose={onClose}
      footer={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white" onClick={onClose}>取消</Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" loading={saving} disabled={!preview?.canStart} onClick={() => void handleStart()}>
            {hasWarnings && !confirmWarnings ? "仍要启动" : "启动 Sprint"}
          </Button>
        </>
      }
    >
      {preview ? (
        <div className="grid gap-4 md:grid-cols-2">
          <SprintInfo sprint={preview.sprint} />
          <div>
            <div className="mb-3 text-[13px] font-bold text-[#172B4D]">启动检查项</div>
            <CheckList checks={preview.checks} />
          </div>
          {hasWarnings ? (
            <label className="md:col-span-2 flex items-center gap-3 rounded-[4px] border border-[#FFE2BD] bg-[#FFF7ED] px-4 py-3 text-[13px] text-[#974F0C]">
              <Checkbox className="h-4 w-4" checked={confirmWarnings} onChange={(event) => setConfirmWarnings(event.target.checked)} />
              我已确认风险项，仍然启动 Sprint
            </label>
          ) : null}
        </div>
      ) : <div className="text-[13px] text-[#626F86]">正在加载启动检查...</div>}
    </BoardDialogFrame>
  );
}

export function SprintCloseDialog({ projectKey, sprintId, onClose, onClosed }: { projectKey: string; sprintId: string; onClose: () => void; onClosed: (message: string) => void }) {
  const [preview, setPreview] = useState<SprintClosePreviewResponse>();
  const [step, setStep] = useState(1);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState<Pick<CloseSprintRequest, "incompleteStrategy" | "createNextSprint" | "nextSprintName" | "nextSprintStartDate" | "retrospective" | "archiveNote">>({
    incompleteStrategy: "move_to_next_sprint",
    createNextSprint: true,
    nextSprintName: "Sprint 14",
    nextSprintStartDate: "2024-06-17",
    retrospective: "",
    archiveNote: "",
  });

  useEffect(() => {
    void projectWorkspaceService.getSprintClosePreview({ projectKey, sprintId }).then(setPreview);
  }, [projectKey, sprintId]);

  async function handleCloseSprint() {
    setSaving(true);
    try {
      const response = await projectWorkspaceService.closeSprint({ projectKey, sprintId, ...form });
      if (response.result === "success") {
        onClosed(response.message);
      }
    } finally {
      setSaving(false);
    }
  }

  return (
    <BoardDialogFrame
      title="结束 Sprint"
      description="总结当前迭代，处理未完成工作，并可选择自动创建下一 Sprint。"
      widthClassName="max-w-[720px]"
      onClose={onClose}
      footer={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white" onClick={step === 1 ? onClose : () => setStep((current) => current - 1)}>
            {step === 1 ? "取消" : "上一步"}
          </Button>
          {step < 4 ? (
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" disabled={!preview?.canClose} onClick={() => setStep((current) => current + 1)}>下一步</Button>
          ) : (
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" loading={saving} disabled={!preview?.canClose} onClick={() => void handleCloseSprint()}>结束 Sprint</Button>
          )}
        </>
      }
    >
      {preview ? (
        <div className="space-y-5">
          <div className="grid grid-cols-4 gap-2">
            {["结束确认", "未完成处理", "总结与归档", "下一步设置"].map((label, index) => (
              <div key={label} className={cn("rounded-[4px] border px-3 py-2 text-[12px] font-bold", step >= index + 1 ? "border-[#CCE0FF] bg-[#F0F6FF] text-[#0C66E4]" : "border-[#DFE1E6] text-[#626F86]")}>{index + 1}. {label}</div>
            ))}
          </div>

          {step === 1 ? (
            <div className="grid gap-4 md:grid-cols-2">
              <SprintInfo sprint={preview.sprint} />
              <div className="rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
                <div className="mb-3 text-[13px] font-bold text-[#172B4D]">数据概览</div>
                {[
                  ["总任务数", preview.summary.totalIssues],
                  ["已完成", preview.summary.completedIssues],
                  ["进行中", preview.summary.inProgressIssues],
                  ["未开始", preview.summary.todoIssues],
                ].map(([label, value]) => (
                  <div key={label} className="flex justify-between border-b border-[#EBECF0] py-1.5 text-[12px] last:border-b-0"><span className="text-[#626F86]">{label}</span><b className="text-[#172B4D]">{value}</b></div>
                ))}
              </div>
              <div className="md:col-span-2"><CheckList checks={preview.checks} /></div>
            </div>
          ) : null}

          {step === 2 ? (
            <div className="space-y-3">
              {[
                { id: "move_to_next_sprint", label: "移动到下一个 Sprint" },
                { id: "move_to_backlog", label: "移动到 Backlog" },
                { id: "mark_done", label: "标记为已完成" },
                { id: "keep_in_sprint", label: "保留在当前 Sprint（不推荐）" },
              ].map((option) => (
                <label key={option.id} className="flex items-center gap-3 rounded-[4px] border border-[#DFE1E6] px-4 py-3 text-[13px] font-bold text-[#172B4D]">
                  <Radio className="h-4 w-4" checked={form.incompleteStrategy === option.id} onChange={() => setForm((prev) => ({ ...prev, incompleteStrategy: option.id as CloseSprintRequest["incompleteStrategy"] }))} />
                  {option.label}
                </label>
              ))}
            </div>
          ) : null}

          {step === 3 ? (
            <div className="grid gap-4">
              <label><div className="mb-2 text-[13px] font-bold text-[#172B4D]">迭代总结（选填）</div><Textarea className="min-h-[96px] w-full resize-none rounded-[4px] border-[#DFE1E6] px-3 py-2 text-[13px]" value={form.retrospective} onChange={(event) => setForm((prev) => ({ ...prev, retrospective: event.target.value }))} /></label>
              <label><div className="mb-2 text-[13px] font-bold text-[#172B4D]">归档备注（选填）</div><Textarea className="min-h-[96px] w-full resize-none rounded-[4px] border-[#DFE1E6] px-3 py-2 text-[13px]" value={form.archiveNote} onChange={(event) => setForm((prev) => ({ ...prev, archiveNote: event.target.value }))} /></label>
            </div>
          ) : null}

          {step === 4 ? (
            <div className="space-y-4">
              <label className="flex items-center gap-3 rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-4 py-3">
                <Checkbox className="h-4 w-4" checked={form.createNextSprint} onChange={(event) => setForm((prev) => ({ ...prev, createNextSprint: event.target.checked }))} />
                <span className="text-[13px] font-bold text-[#172B4D]">自动创建下一个 Sprint</span>
              </label>
              <div className="grid gap-4 md:grid-cols-2">
                <label><div className="mb-2 text-[13px] font-bold text-[#172B4D]">Sprint 名称</div><Input className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.nextSprintName} disabled={!form.createNextSprint} onChange={(event) => setForm((prev) => ({ ...prev, nextSprintName: event.target.value }))} /></label>
                <label><div className="mb-2 text-[13px] font-bold text-[#172B4D]">预计开始日期</div><Input type="date" className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.nextSprintStartDate} disabled={!form.createNextSprint} onChange={(event) => setForm((prev) => ({ ...prev, nextSprintStartDate: event.target.value }))} /></label>
              </div>
            </div>
          ) : null}
        </div>
      ) : <div className="text-[13px] text-[#626F86]">正在加载结束检查...</div>}
    </BoardDialogFrame>
  );
}
