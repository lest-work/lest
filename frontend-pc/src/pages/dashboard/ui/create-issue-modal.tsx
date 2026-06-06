import { ChevronDown, Paperclip, X } from "lucide-react";
import { useState, type ReactNode } from "react";

import type { AssigneeWorkload } from "@/api/dashboard";
import { issuePriorityLabel, issueTypeLabel, type IssuePriority, type IssueType } from "@/entities/issue";
import { cn } from "@/shared/lib/utils";
import { Button, Input, Modal, Select, Textarea } from "@/shared/ui";
import { ISSUE_PRIORITIES, ISSUE_TYPES } from "./dashboard-constants";

export function CreateIssueModal({
  assignees,
  onClose,
  onSubmit,
}: {
  assignees: AssigneeWorkload[];
  onClose: () => void;
  onSubmit: (payload: { title: string; description: string; type: IssueType; priority: IssuePriority; assigneeId?: string; dueDate?: string }) => Promise<void>;
}) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [type, setType] = useState<IssueType>("task");
  const [priority, setPriority] = useState<IssuePriority>("medium");
  const [assigneeId, setAssigneeId] = useState<string>();
  const [dueDate, setDueDate] = useState("2026-06-20");
  const [submitting, setSubmitting] = useState(false);
  const hasError = title.trim().length === 0;

  async function submit() {
    if (hasError) {
      return;
    }
    setSubmitting(true);
    await onSubmit({ title, description, type, priority, assigneeId, dueDate });
    setSubmitting(false);
  }

  return (
    <Modal
      open
      centered={false}
      closable={false}
      footer={null}
      width={640}
      zIndex={50}
      className="lest-frameless-modal"
      styles={{ mask: { background: "rgba(9, 30, 66, 0.32)" } }}
      style={{ top: 80, paddingBottom: 24 }}
      onCancel={onClose}
    >
      <div className="w-full rounded-[8px] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <div className="flex items-center justify-between border-b border-[#EBECF0] px-6 py-4">
          <h2 className="text-[18px] font-semibold text-[#172B4D]">新建问题</h2>
          <Button className="rounded-[4px] p-1 text-[#44546F] hover:bg-[#F4F5F7]" onClick={onClose}>
            <X className="h-5 w-5" />
          </Button>
        </div>
        <div className="space-y-4 px-6 py-5">
          <FormRow label="项目">
            <SelectLike value="敏捷研发平台" />
          </FormRow>
          <div className="grid grid-cols-2 gap-4">
            <FormRow label="问题类型">
              <Select className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px]" value={type} onChange={(value) => setType(value as IssueType)}>
                {ISSUE_TYPES.map((item) => (
                  <option key={item} value={item}>
                    {issueTypeLabel[item]}
                  </option>
                ))}
              </Select>
            </FormRow>
            <FormRow label="优先级">
              <Select className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px]" value={priority} onChange={(value) => setPriority(value as IssuePriority)}>
                {ISSUE_PRIORITIES.map((item) => (
                  <option key={item} value={item}>
                    {issuePriorityLabel[item]}
                  </option>
                ))}
              </Select>
            </FormRow>
          </div>
          <FormRow label="标题" required>
            <Input className={cn("h-9 rounded-[4px] border-[#DFE1E6] text-[13px]", hasError && "border-[#DE350B]")} value={title} onChange={(event) => setTitle(event.target.value)} placeholder="请输入问题标题" />
            {hasError ? <div className="mt-1 text-[12px] text-[#DE350B]">标题不能为空</div> : null}
          </FormRow>
          <FormRow label="描述">
            <Textarea
              className="min-h-[92px] w-full rounded-[4px] border-[#DFE1E6] px-3 py-2 text-[13px]"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="补充问题背景、验收标准或复现步骤"
            />
          </FormRow>
          <div className="grid grid-cols-2 gap-4">
            <FormRow label="负责人">
              <Select className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px]" value={assigneeId ?? ""} onChange={(value) => setAssigneeId(value ? String(value) : undefined)}>
                <option value="">未分配</option>
                {assignees.filter((item) => item.id !== "unassigned").map((item) => (
                  <option key={item.id} value={item.id}>
                    {item.name}
                  </option>
                ))}
              </Select>
            </FormRow>
            <FormRow label="截止日期">
              <Input className="h-9 rounded-[4px] border-[#DFE1E6] text-[13px]" type="date" value={dueDate} onChange={(event) => setDueDate(event.target.value)} />
            </FormRow>
          </div>
          <div className="rounded-[4px] border border-dashed border-[#C1C7D0] bg-[#FAFBFC] px-4 py-5 text-center text-[13px] text-[#44546F]">
            <Paperclip className="mx-auto mb-2 h-5 w-5 text-[#6B778C]" />
            拖拽附件到此处，或点击上传
          </div>
        </div>
        <div className="flex justify-end gap-3 border-t border-[#EBECF0] px-6 py-4">
          <Button variant="outline" className="h-9 rounded-[4px] border-[#DFE1E6] bg-white px-4" onClick={onClose}>
            取消
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] px-4 text-white" loading={submitting} onClick={submit}>
            创建
          </Button>
        </div>
      </div>
    </Modal>
  );
}

function FormRow({ label, required, children }: { label: string; required?: boolean; children: ReactNode }) {
  return (
    <label className="block">
      <div className="mb-1.5 text-[13px] font-semibold text-[#172B4D]">
        {label}
        {required ? <span className="ml-0.5 text-[#DE350B]">*</span> : null}
      </div>
      {children}
    </label>
  );
}

function SelectLike({ value }: { value: string }) {
  return (
    <div className="flex h-9 items-center justify-between rounded-[4px] border border-[#DFE1E6] bg-[#F7F8F9] px-3 text-[13px] text-[#172B4D]">
      {value}
      <ChevronDown className="h-4 w-4 text-[#6B778C]" />
    </div>
  );
}
