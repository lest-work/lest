import { CalendarDays, Check, Circle, Users } from "lucide-react";
import { useMemo, useState } from "react";

import type { CreateSprintRequest } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button, Checkbox, Input, Textarea } from "@/shared/ui";
import { BoardDialogFrame } from "./board-dialog-frame";

export interface SprintCreateDialogProps {
  projectKey: string;
  onClose: () => void;
  onCreated: (message: string) => void;
}

const teamMembers = [
  { id: "u-1", name: "张三", role: "Scrum Master" },
  { id: "u-2", name: "李华", role: "前端开发" },
  { id: "u-3", name: "王芳", role: "测试工程师" },
  { id: "u-4", name: "陈静", role: "后端开发" },
];

const tags = ["登录与认证", "质量周", "Q2", "接口联调", "灰度发布"];

export function SprintCreateDialog({ projectKey, onClose, onCreated }: SprintCreateDialogProps) {
  const [step, setStep] = useState(1);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState<CreateSprintRequest>({
    projectKey,
    targetProjectKey: projectKey,
    name: "Sprint 13",
    startDate: "2024-06-03",
    endDate: "2024-06-16",
    goal: "完成用户登录与权限相关联调，并收敛高优先级缺陷。",
    type: "regular",
    capacity: 120,
    wipLimit: 5,
    maxParallelIssues: 20,
    memberIds: ["u-1", "u-2", "u-3"],
    tags: ["登录与认证", "质量周"],
    autoStart: false,
  });

  const errors = useMemo(() => {
    const list: string[] = [];
    if (!form.name.trim()) list.push("请输入 Sprint 名称");
    if (!form.startDate) list.push("请选择开始日期");
    if (!form.endDate) list.push("请选择结束日期");
    if (form.startDate && form.endDate && form.startDate > form.endDate) list.push("结束日期不能早于开始日期");
    if (form.capacity <= 0) list.push("团队容量必须大于 0");
    return list;
  }, [form]);

  async function handleSubmit() {
    setSaving(true);
    try {
      const response = await projectWorkspaceService.createSprint(form);
      onCreated(response.message);
    } finally {
      setSaving(false);
    }
  }

  function toggleMember(memberId: string) {
    setForm((prev) => ({
      ...prev,
      memberIds: prev.memberIds.includes(memberId) ? prev.memberIds.filter((id) => id !== memberId) : [...prev.memberIds, memberId],
    }));
  }

  function toggleTag(tag: string) {
    setForm((prev) => ({
      ...prev,
      tags: prev.tags.includes(tag) ? prev.tags.filter((item) => item !== tag) : [...prev.tags, tag],
    }));
  }

  return (
    <BoardDialogFrame
      title="创建 Sprint"
      description="创建新的迭代，设置时间范围、容量、WIP 与团队规则。"
      widthClassName="max-w-[680px]"
      onClose={onClose}
      footer={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white" onClick={step === 1 ? onClose : () => setStep((current) => current - 1)}>
            {step === 1 ? "取消" : "上一步"}
          </Button>
          {step < 3 ? (
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" disabled={step === 1 && errors.length > 0} onClick={() => setStep((current) => current + 1)}>
              下一步
            </Button>
          ) : (
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" loading={saving} disabled={errors.length > 0} onClick={() => void handleSubmit()}>
              创建 Sprint
            </Button>
          )}
        </>
      }
    >
      <div className="mb-6 grid grid-cols-3 gap-3">
        {[
          { id: 1, label: "基础信息" },
          { id: 2, label: "容量与规则" },
          { id: 3, label: "高级设置" },
        ].map((item) => (
          <div key={item.id} className={cn("flex items-center gap-2 rounded-[4px] border px-3 py-2 text-[13px] font-bold", step >= item.id ? "border-[#CCE0FF] bg-[#F0F6FF] text-[#0C66E4]" : "border-[#DFE1E6] bg-white text-[#626F86]")}>
            <span className="flex h-5 w-5 items-center justify-center rounded-full bg-current text-[11px] text-white">{step > item.id ? <Check className="h-3 w-3" /> : item.id}</span>
            {item.label}
          </div>
        ))}
      </div>

      {step === 1 ? (
        <div className="grid gap-4">
          <label>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">Sprint 名称 <span className="text-[#DE350B]">*</span></div>
            <Input className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.name} maxLength={50} onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))} />
          </label>
          <div className="grid gap-4 md:grid-cols-2">
            <label>
              <div className="mb-2 flex items-center gap-2 text-[13px] font-bold text-[#172B4D]"><CalendarDays className="h-4 w-4" />开始日期</div>
              <Input type="date" className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.startDate} onChange={(event) => setForm((prev) => ({ ...prev, startDate: event.target.value }))} />
            </label>
            <label>
              <div className="mb-2 flex items-center gap-2 text-[13px] font-bold text-[#172B4D]"><CalendarDays className="h-4 w-4" />结束日期</div>
              <Input type="date" className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.endDate} onChange={(event) => setForm((prev) => ({ ...prev, endDate: event.target.value }))} />
            </label>
          </div>
          <label>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">迭代目标</div>
            <Textarea className="min-h-[88px] w-full resize-none rounded-[4px] border-[#DFE1E6] px-3 py-2 text-[13px]" value={form.goal} maxLength={200} onChange={(event) => setForm((prev) => ({ ...prev, goal: event.target.value }))} />
          </label>
          {errors.length ? <div className="rounded-[4px] border border-[#FFD5CC] bg-[#FFF4F2] px-3 py-2 text-[12px] font-medium text-[#AE2E24]">{errors[0]}</div> : null}
        </div>
      ) : null}

      {step === 2 ? (
        <div className="grid gap-4 md:grid-cols-2">
          <label>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">团队容量点数</div>
            <Input type="number" className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.capacity} onChange={(event) => setForm((prev) => ({ ...prev, capacity: Number(event.target.value) }))} />
          </label>
          <label>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">WIP 限制</div>
            <Input type="number" className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.wipLimit} onChange={(event) => setForm((prev) => ({ ...prev, wipLimit: Number(event.target.value) }))} />
          </label>
          <label>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">最大并行任务数</div>
            <Input type="number" className="h-9 rounded-[4px] border-[#DFE1E6]" value={form.maxParallelIssues} onChange={(event) => setForm((prev) => ({ ...prev, maxParallelIssues: Number(event.target.value) }))} />
          </label>
          <label className="flex items-center gap-3 rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-4 py-3">
            <Checkbox className="h-4 w-4" checked={form.autoStart} onChange={(event) => setForm((prev) => ({ ...prev, autoStart: event.target.checked }))} />
            <span className="text-[13px] font-bold text-[#172B4D]">创建后自动启动 Sprint</span>
          </label>
        </div>
      ) : null}

      {step === 3 ? (
        <div className="space-y-5">
          <div>
            <div className="mb-3 flex items-center gap-2 text-[13px] font-bold text-[#172B4D]"><Users className="h-4 w-4" />参与成员</div>
            <div className="grid gap-2 md:grid-cols-2">
              {teamMembers.map((member) => {
                const active = form.memberIds.includes(member.id);
                return (
                  <Button key={member.id} className={cn("flex items-center justify-between rounded-[4px] border px-3 py-2 text-left", active ? "border-[#0C66E4] bg-[#F0F6FF]" : "border-[#DFE1E6] bg-white hover:bg-[#F4F5F7]")} onClick={() => toggleMember(member.id)}>
                    <span><span className="block text-[13px] font-bold text-[#172B4D]">{member.name}</span><span className="text-[12px] text-[#626F86]">{member.role}</span></span>
                    {active ? <Check className="h-4 w-4 text-[#0C66E4]" /> : <Circle className="h-4 w-4 text-[#A5ADBA]" />}
                  </Button>
                );
              })}
            </div>
          </div>
          <div>
            <div className="mb-3 text-[13px] font-bold text-[#172B4D]">迭代标签</div>
            <div className="flex flex-wrap gap-2">
              {tags.map((tag) => (
                <Button key={tag} className={cn("rounded-[4px] border px-3 py-1.5 text-[12px] font-bold", form.tags.includes(tag) ? "border-[#B3DF72] bg-[#E3FCEF] text-[#006644]" : "border-[#DFE1E6] bg-white text-[#44546F] hover:bg-[#F4F5F7]")} onClick={() => toggleTag(tag)}>
                  {tag}
                </Button>
              ))}
            </div>
          </div>
        </div>
      ) : null}
    </BoardDialogFrame>
  );
}
