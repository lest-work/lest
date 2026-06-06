import { CalendarDays, CheckCircle2, Link2, MoreHorizontal, Paperclip, Pencil, Star, Trash2, X } from "lucide-react";
import type { ReactNode } from "react";

import { issuePriorityLabel, issueStatusLabel, type IssueStatus } from "@/entities/issue";
import type { BoardIssueDetailResponse } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Drawer, Select } from "@/shared/ui";

const statusOrder: IssueStatus[] = ["todo", "in-progress", "review", "done", "closed"];
const statusClassName: Record<IssueStatus, string> = {
  todo: "bg-[#F1F2F4] text-[#44546F]",
  "in-progress": "bg-[#DEEBFF] text-[#0747A6]",
  review: "bg-[#FFF0B3] text-[#A54800]",
  done: "bg-[#E3FCEF] text-[#006644]",
  closed: "bg-[#FFEBE6] text-[#AE2E24]",
};

export interface BoardIssueDetailDrawerProps {
  detail: BoardIssueDetailResponse;
  saving?: boolean;
  onClose: () => void;
  onMoveStatus: (status: IssueStatus) => Promise<void>;
}

export function BoardIssueDetailDrawer({ detail, saving, onClose, onMoveStatus }: BoardIssueDetailDrawerProps) {
  const { card, fields } = detail;
  const doneCount = detail.subtasks.filter((item) => item.done).length;

  return (
    <Drawer
      open
      closable={false}
      width="min(100vw, 520px)"
      zIndex={50}
      className="lest-frameless-drawer"
      styles={{ body: { height: "100%" }, mask: { background: "rgba(9, 30, 66, 0.2)" } }}
      onClose={onClose}
    >
      <aside className="workspace-scrollbar h-full w-full overflow-y-auto border-l border-[#DFE1E6] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.24)]">
        <header className="sticky top-0 z-10 border-b border-[#EBECF0] bg-white px-6 py-5">
          <div className="mb-3 flex items-center justify-between">
            <span className="text-[12px] font-bold text-[#0C66E4]">{card.key}</span>
            <div className="flex items-center gap-1 text-[#44546F]">
              <IconButton title="编辑" icon={<Pencil className="h-4 w-4" />} />
              <IconButton title="关注" icon={<Star className="h-4 w-4" />} />
              <IconButton title="关联" icon={<Link2 className="h-4 w-4" />} />
              <IconButton title="更多" icon={<MoreHorizontal className="h-4 w-4" />} />
              <IconButton title="关闭" icon={<X className="h-4 w-4" />} onClick={onClose} />
            </div>
          </div>
          <h2 className="text-[20px] font-bold leading-7 text-[#172B4D]">{card.title}</h2>
          <div className="mt-4 grid grid-cols-4 gap-3 border-y border-[#EBECF0] py-3 text-[12px]">
            <Field label="状态">
              <Select className={cn("h-8 w-full rounded-[4px] border-[#DFE1E6] text-[12px] font-semibold", statusClassName[card.status])} value={card.status} disabled={saving} onChange={(value) => void onMoveStatus(value as IssueStatus)}>
                {statusOrder.map((status) => (
                  <option key={status} value={status}>
                    {issueStatusLabel[status as IssueStatus]}
                  </option>
                ))}
              </Select>
            </Field>
            <Field label="优先级">
              <span className="font-semibold text-[#B76E00]">{issuePriorityLabel[card.priority as IssuePriority]}</span>
            </Field>
            <Field label="负责人">
              {card.assignee ? (
                <span className="inline-flex items-center gap-1.5">
                  <Avatar name={card.assignee.name} imageSrc={card.assignee.avatar} className="h-5 w-5 text-[10px]" />
                  {card.assignee.name}
                </span>
              ) : (
                "未分配"
              )}
            </Field>
            <Field label="截止日期">
              <span className="inline-flex items-center gap-1">
                <CalendarDays className="h-3.5 w-3.5 text-[#626F86]" />
                {fields.dueDate}
              </span>
            </Field>
          </div>
        </header>

        <main className="space-y-4 px-6 py-5">
          <section>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">项目</div>
            <div className="grid grid-cols-2 gap-3 text-[13px]">
              <DetailLine label="产品项目" value={fields.projectName} />
              <DetailLine label="迭代" value={fields.sprintName} />
              <DetailLine label="报告人" value={fields.reporter} />
              <DetailLine label="关注" value={`${fields.watchers} 人`} />
            </div>
          </section>

          <section>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">描述</div>
            <p className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-3 text-[13px] leading-6 text-[#172B4D]">{detail.description}</p>
          </section>

          <section>
            <div className="mb-2 flex items-center justify-between">
              <div className="text-[13px] font-bold text-[#172B4D]">子任务 {doneCount}/{detail.subtasks.length}</div>
              <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]">
                添加子任务
              </Button>
            </div>
            <div className="space-y-2 rounded-[6px] border border-[#DFE1E6] p-2">
              {detail.subtasks.map((subtask) => (
                <div key={subtask.id} className="flex items-center justify-between gap-3 rounded-[4px] bg-[#FAFBFC] px-3 py-2 text-[12px]">
                  <span className="flex min-w-0 items-center gap-2">
                    <CheckCircle2 className={cn("h-4 w-4", subtask.done ? "text-[#22A06B]" : "text-[#A5ADBA]")} />
                    <span className="font-semibold text-[#0C66E4]">{subtask.key}</span>
                    <span className="truncate text-[#172B4D]">{subtask.title}</span>
                  </span>
                  <span className={cn("rounded-[3px] px-2 py-0.5 font-semibold", subtask.done ? "bg-[#E3FCEF] text-[#006644]" : "bg-[#F1F2F4] text-[#44546F]")}>{subtask.done ? "完成" : "待办"}</span>
                </div>
              ))}
            </div>
          </section>

          <section>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">标签</div>
            <div className="flex flex-wrap gap-2">
              {card.labels.map((label) => (
                <span key={label} className="rounded-[4px] bg-[#F1F2F4] px-2 py-1 text-[12px] font-semibold text-[#44546F]">
                  {label}
                </span>
              ))}
            </div>
          </section>

          <section>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">附件</div>
            <div className="flex items-center justify-between rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-2 text-[13px]">
              <span className="inline-flex items-center gap-2 text-[#172B4D]">
                <Paperclip className="h-4 w-4 text-[#0C66E4]" />
                {fields.attachments} 个附件
              </span>
              <Button className="text-[12px] font-semibold text-[#0C66E4]">查看</Button>
            </div>
          </section>

          <section>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">活动区</div>
            <div className="space-y-3">
              {detail.activity.map((item, index) => (
                <div key={item.id} className="relative pl-5 text-[12px]">
                  <span className="absolute left-0 top-1.5 h-2.5 w-2.5 rounded-full bg-[#0C66E4]" />
                  {index < detail.activity.length - 1 ? <span className="absolute bottom-[-14px] left-[5px] top-4 w-px bg-[#DFE1E6]" /> : null}
                  <div className="text-[#626F86]">{item.time}</div>
                  <div className="mt-1 text-[#172B4D]">
                    <span className="font-semibold">{item.actor}</span> {item.content}
                  </div>
                </div>
              ))}
            </div>
          </section>
        </main>

        <footer className="sticky bottom-0 flex items-center justify-between border-t border-[#EBECF0] bg-white px-6 py-4">
          <div className="flex gap-2">
            <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]">
              评论
            </Button>
            <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]">
              关联问题
            </Button>
          </div>
          <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px] text-[#E34935]">
            <Trash2 className="h-4 w-4" />
            删除
          </Button>
        </footer>
      </aside>
    </Drawer>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="min-w-0">
      <div className="mb-1 text-[#626F86]">{label}</div>
      <div className="truncate font-medium text-[#172B4D]">{children}</div>
    </div>
  );
}

function DetailLine({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-2">
      <div className="text-[12px] text-[#626F86]">{label}</div>
      <div className="mt-1 truncate text-[13px] font-semibold text-[#172B4D]">{value}</div>
    </div>
  );
}

function IconButton({ icon, title, onClick }: { icon: ReactNode; title: string; onClick?: () => void }) {
  return (
    <Button title={title} className="inline-flex h-8 w-8 items-center justify-center rounded-[4px] hover:bg-[#F4F5F7]" onClick={onClick}>
      {icon}
    </Button>
  );
}
