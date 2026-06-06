import { CheckCircle2, FileText, Loader2, Paperclip, Search, UploadCloud, X } from "lucide-react";
import { useMemo, useState } from "react";

import {
  issuePriorityLabel,
  issueStatusLabel,
  issueTypeLabel,
  type Issue,
  type IssuePriority,
  type IssueStatus,
  type IssueType,
  type User,
} from "@/entities/issue";
import type {
  CreateIssuePayload,
  CreateIssueResponse,
} from "@/api/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Input } from "@/shared/ui";

export interface IssueCreateDialogProps {
  users: User[];
  onClose: () => void;
  onSubmit: (payload: CreateIssuePayload) => Promise<CreateIssueResponse>;
  onViewIssue?: (issue: Issue) => void;
}

const typeOptions: IssueType[] = ["bug", "task", "story", "subtask", "epic"];
const priorityOptions: IssuePriority[] = ["highest", "high", "medium", "low", "lowest"];
const statusOptions: IssueStatus[] = ["todo", "in-progress", "review", "done", "closed"];

export function IssueCreateDialog({ users, onClose, onSubmit, onViewIssue }: IssueCreateDialogProps) {
  const [type, setType] = useState<IssueType | "">("");
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<IssuePriority | "">("");
  const [status, setStatus] = useState<IssueStatus>("todo");
  const [assigneeId, setAssigneeId] = useState("");
  const [labels, setLabels] = useState(["登录问题", "白屏"]);
  const [dueDate, setDueDate] = useState("");
  const [estimate, setEstimate] = useState("8");
  const [createAnother, setCreateAnother] = useState(true);
  const [submitted, setSubmitted] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [created, setCreated] = useState<CreateIssueResponse>();

  const reporter = users[0];
  const selectedAssignee = users.find((user) => user.id === assigneeId);
  const errors = useMemo(
    () => ({
      type: submitted && !type ? "请选择问题类型" : "",
      title: submitted && title.trim().length < 5 ? "标题不能少于 5 个字符" : "",
      description: submitted && description.trim().length < 10 ? "描述不能少于 10 个字符" : "",
      priority: submitted && !priority ? "请选择优先级" : "",
      assigneeId: submitted && !assigneeId ? "请选择负责人" : "",
      dueDate: submitted && dueDate === "2024-05-32" ? "日期格式不正确" : "",
      estimate: submitted && Number(estimate) <= 0 ? "工时必须大于 0" : "",
    }),
    [assigneeId, description, dueDate, estimate, priority, submitted, title, type],
  );
  const hasError = Object.values(errors).some(Boolean);

  async function submit() {
    setSubmitted(true);
    if (hasError || !type || !priority || !reporter) {
      return;
    }

    setSubmitting(true);
    try {
      const response = await onSubmit({
        projectKey: "lest-platform",
        type,
        title,
        description,
        priority,
        status,
        assigneeId,
        reporterId: reporter.id,
        labels,
        sprintId: "sprint-1",
        dueDate,
        estimate: Number(estimate),
        affectedVersion: "v2.1.0",
        fixedVersion: "v2.1.1",
        relatedIssueKeys: ["LST-128"],
        watcherIds: users.slice(1, 3).map((user) => user.id),
        createAnother,
      });
      setCreated(response);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center bg-[#091E42]/32 px-4 pt-[72px]">
      <div className="relative flex max-h-[calc(100vh-96px)] w-full max-w-[960px] overflow-hidden rounded-[8px] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        {submitting ? (
          <div className="absolute inset-0 z-20 flex items-center justify-center bg-white/70 backdrop-blur-[1px]">
            <div className="flex h-[120px] w-[160px] flex-col items-center justify-center rounded-[8px] bg-white shadow-[0_12px_32px_rgba(9,30,66,0.22)]">
              <Loader2 className="h-7 w-7 animate-spin text-[#0C66E4]" />
              <div className="mt-3 text-[13px] font-bold text-[#172B4D]">正在创建问题...</div>
              <div className="mt-1 text-[12px] text-[#626F86]">请勿关闭当前弹窗</div>
            </div>
          </div>
        ) : null}

        <div className="flex min-h-0 min-w-0 flex-1 flex-col">
          <header className="flex h-16 shrink-0 items-center justify-between border-b border-[#EBECF0] px-6">
            <h2 className="text-[18px] font-bold text-[#172B4D]">新建问题</h2>
            <div className="flex items-center gap-2">
              <button className="rounded-[4px] p-1.5 text-[#44546F] hover:bg-[#F4F5F7]" onClick={onClose}>
                <X className="h-5 w-5" />
              </button>
            </div>
          </header>

          <div className="workspace-scrollbar grid min-h-0 flex-1 gap-x-8 gap-y-4 overflow-y-auto px-6 py-5 md:grid-cols-2">
            <Field label="问题类型" required error={errors.type}>
              <select className={fieldClassName(Boolean(errors.type))} value={type} onChange={(event) => setType(event.target.value as IssueType)}>
                <option value="">请选择问题类型</option>
                {typeOptions.map((option) => (
                  <option key={option} value={option}>
                    {issueTypeLabel[option]}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="所属项目" required>
              <div className="flex h-9 items-center gap-2 rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px] text-[#172B4D]">
                <span className="flex h-5 w-5 items-center justify-center rounded-[3px] bg-[#0C66E4] text-[11px] font-bold text-white">项</span>
                示例项目（DEMO）
              </div>
            </Field>
            <Field label="标题" required error={errors.title} className="md:col-span-2">
              <div className="relative">
                <Input className={cn(fieldClassName(Boolean(errors.title)), "pr-16")} value={title} onChange={(event) => setTitle(event.target.value)} placeholder="请输入问题标题（必填）" />
                <span className="absolute right-3 top-2.5 text-[12px] text-[#626F86]">{title.length}/255</span>
              </div>
            </Field>
            <Field label="描述" required error={errors.description} className="md:col-span-2">
              <div className={cn("overflow-hidden rounded-[4px] border bg-white", errors.description ? "border-[#E34935]" : "border-[#DFE1E6]")}>
                <div className="flex h-9 items-center gap-4 border-b border-[#EBECF0] px-3 text-[12px] font-semibold text-[#44546F]">
                  <span>正文</span>
                  {["B", "I", "U", "S", "</>", "•", "1.", "↔"].map((item) => (
                    <button key={item} className="rounded-[3px] px-1 hover:bg-[#F4F5F7]">
                      {item}
                    </button>
                  ))}
                </div>
                <textarea
                  className="min-h-[128px] w-full resize-none px-3 py-3 text-[13px] leading-6 outline-none"
                  value={description}
                  onChange={(event) => setDescription(event.target.value)}
                  placeholder="请输入问题描述，支持 Markdown 格式"
                />
                <div className="border-t border-[#EBECF0] px-3 py-1.5 text-right text-[12px] text-[#626F86]">{description.length}/2000</div>
              </div>
            </Field>
            <Field label="优先级" required error={errors.priority}>
              <select className={fieldClassName(Boolean(errors.priority))} value={priority} onChange={(event) => setPriority(event.target.value as IssuePriority)}>
                <option value="">请选择优先级</option>
                {priorityOptions.map((option) => (
                  <option key={option} value={option}>
                    {issuePriorityLabel[option]}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="状态" required>
              <select className={fieldClassName(false)} value={status} onChange={(event) => setStatus(event.target.value as IssueStatus)}>
                {statusOptions.map((option) => (
                  <option key={option} value={option}>
                    {issueStatusLabel[option]}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="负责人" required error={errors.assigneeId}>
              <select className={fieldClassName(Boolean(errors.assigneeId))} value={assigneeId} onChange={(event) => setAssigneeId(event.target.value)}>
                <option value="">请选择负责人</option>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.name}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="报告人">
              <div className="flex h-9 items-center gap-2 rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]">
                {reporter ? <Avatar name={reporter.name} imageSrc={reporter.avatarUrl} className="h-6 w-6" /> : null}
                {reporter?.name ?? "当前用户"}
              </div>
            </Field>
            <Field label="标签">
              <div className="flex min-h-9 flex-wrap items-center gap-1 rounded-[4px] border border-[#DFE1E6] bg-white px-2 py-1">
                {labels.map((label) => (
                  <button key={label} className="rounded-[3px] bg-[#F1F2F4] px-2 py-1 text-[12px] text-[#44546F]" onClick={() => setLabels(labels.filter((item) => item !== label))}>
                    {label} ×
                  </button>
                ))}
                <button className="text-[12px] font-semibold text-[#0C66E4]" onClick={() => setLabels([...labels, "前端"])}>
                  + 标签
                </button>
              </div>
            </Field>
            <Field label="截止日期" required error={errors.dueDate}>
              <Input className={fieldClassName(Boolean(errors.dueDate))} value={dueDate} onChange={(event) => setDueDate(event.target.value)} placeholder="2026-06-30" />
            </Field>
          </div>

          <footer className="flex h-[72px] shrink-0 items-center justify-between border-t border-[#EBECF0] px-6">
            <label className="flex items-center gap-2 text-[13px] text-[#172B4D]">
              <input type="checkbox" checked={createAnother} onChange={(event) => setCreateAnother(event.target.checked)} />
              创建后继续新建
            </label>
            <div className="flex items-center gap-3">
              <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4" onClick={onClose}>
                取消
              </Button>
              <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4">
                保存草稿
              </Button>
              <Button className="h-9 rounded-[4px] bg-[#0C66E4] px-4 text-white" loading={submitting} onClick={submit}>
                创建问题
              </Button>
            </div>
          </footer>
        </div>

        <aside className="workspace-scrollbar hidden w-[300px] overflow-y-auto border-l border-[#EBECF0] bg-[#FAFBFC] p-5 lg:block">
          {created ? (
            <div className="rounded-[8px] border border-[#ABF5D1] bg-white p-5 text-center">
              <CheckCircle2 className="mx-auto h-14 w-14 text-[#22A06B]" />
              <div className="mt-4 text-[18px] font-bold text-[#164B35]">创建成功！</div>
              <div className="mt-1 text-[13px] text-[#626F86]">问题已创建成功</div>
              <div className="mt-5 rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] px-4 py-3 text-left">
                <div className="text-[12px] text-[#626F86]">问题编号</div>
                <div className="mt-1 flex items-center justify-between">
                  <span className="text-[16px] font-bold text-[#172B4D]">{created.issue.key}</span>
                  <Button variant="outline" className="h-7 rounded-[4px] bg-white px-2 text-[12px]">
                    复制
                  </Button>
                </div>
              </div>
              <div className="mt-4 space-y-2 text-left">
                <button className="flex w-full items-center justify-between rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2 text-[13px] hover:bg-[#F4F5F7]" onClick={() => onViewIssue?.(created.issue)}>
                  查看问题详情 <span>›</span>
                </button>
                <button className="flex w-full items-center justify-between rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2 text-[13px] hover:bg-[#F4F5F7]" onClick={() => setCreated(undefined)}>
                  继续创建问题 <span>›</span>
                </button>
              </div>
            </div>
          ) : (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <h3 className="text-[14px] font-bold text-[#172B4D]">更多信息</h3>
                <span className="text-[12px] text-[#0C66E4]">展开</span>
              </div>
              <SideField label="影响版本" value="v2.1.0" />
              <SideField label="修复版本" value="v2.1.1" />
              <Field label="预估工时" error={errors.estimate}>
                <div className="flex items-center gap-2">
                  <Input className={fieldClassName(Boolean(errors.estimate))} value={estimate} onChange={(event) => setEstimate(event.target.value)} />
                  <span className="text-[13px] text-[#626F86]">小时</span>
                </div>
              </Field>
              <div>
                <div className="mb-1.5 text-[13px] font-semibold text-[#172B4D]">关联问题</div>
                <div className="relative">
                  <Input className="h-9 rounded-[4px] border-[#DFE1E6] pr-8 text-[13px]" value="LST-128 登录接口优化" readOnly />
                  <Search className="absolute right-2.5 top-2.5 h-4 w-4 text-[#6B778C]" />
                </div>
              </div>
              <div>
                <div className="mb-1.5 text-[13px] font-semibold text-[#172B4D]">抄送人</div>
                <div className="flex h-9 items-center gap-1 rounded-[4px] border border-[#DFE1E6] bg-white px-2">
                  {selectedAssignee ? <span className="rounded-[3px] bg-[#F1F2F4] px-2 py-1 text-[12px]">{selectedAssignee.name}</span> : <span className="text-[13px] text-[#8C9BAB]">请选择抄送人</span>}
                </div>
              </div>
              <div className="rounded-[4px] border border-dashed border-[#C1C7D0] bg-white px-4 py-5 text-center text-[12px] text-[#626F86]">
                <UploadCloud className="mx-auto mb-2 h-6 w-6 text-[#0C66E4]" />
                点击或拖拽文件到此处上传
                <div className="mt-1">支持 jpg、png、pdf、zip，单个文件不超过 50MB</div>
              </div>
              <div className="space-y-2">
                {[
                  ["截屏_2024-05-20.png", "320 KB", "success"],
                  ["console-error.log", "12 KB", "success"],
                  ["复现步骤.docx", "68%", "uploading"],
                ].map(([name, meta, state]) => (
                  <div key={name} className="flex items-center gap-2 rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2">
                    <FileText className="h-4 w-4 text-[#0C66E4]" />
                    <div className="min-w-0 flex-1">
                      <div className="truncate text-[12px] text-[#172B4D]">{name}</div>
                      <div className="text-[11px] text-[#626F86]">{meta}</div>
                    </div>
                    {state === "uploading" ? <Loader2 className="h-4 w-4 animate-spin text-[#0C66E4]" /> : <CheckCircle2 className="h-4 w-4 text-[#22A06B]" />}
                  </div>
                ))}
              </div>
              <div className="pt-2 text-[12px] text-[#626F86]">
                <Paperclip className="mr-1 inline h-3.5 w-3.5" />
                自定义字段可在项目设置中扩展
              </div>
            </div>
          )}
        </aside>
      </div>
    </div>
  );
}

function Field({ label, required, error, className, children }: { label: string; required?: boolean; error?: string; className?: string; children: React.ReactNode }) {
  return (
    <label className={cn("block", className)}>
      <div className="mb-1.5 text-[13px] font-semibold text-[#172B4D]">
        {label}
        {required ? <span className="ml-0.5 text-[#E34935]">*</span> : null}
      </div>
      {children}
      {error ? <div className="mt-1 text-[12px] leading-4 text-[#E34935]">{error}</div> : null}
    </label>
  );
}

function SideField({ label, value }: { label: string; value: string }) {
  return (
    <label className="block">
      <div className="mb-1.5 text-[13px] font-semibold text-[#172B4D]">{label}</div>
      <select className={fieldClassName(false)} value={value} onChange={() => undefined}>
        <option>{value}</option>
      </select>
    </label>
  );
}

function fieldClassName(error: boolean) {
  return cn(
    "h-9 w-full rounded-[4px] border bg-white px-3 text-[13px] outline-none transition focus:border-[#0C66E4] focus:ring-1 focus:ring-[#0C66E4]",
    error ? "border-[#E34935] text-[#172B4D] focus:border-[#E34935] focus:ring-[#E34935]" : "border-[#DFE1E6]",
  );
}
