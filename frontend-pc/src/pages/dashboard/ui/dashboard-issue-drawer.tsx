import {
  Check,
  ChevronDown,
  Copy,
  Edit3,
  Link2,
  Paperclip,
  Plus,
  X,
} from "lucide-react";
import { useState, type ReactNode } from "react";

import type { DashboardActivity, DashboardIssue, IssueComment } from "@/api/dashboard";
import { issueStatusLabel, issueTypeLabel, type IssueStatus } from "@/entities/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Drawer, Textarea } from "@/shared/ui";
import { STATUS_ORDER } from "./dashboard-constants";
import { statusClassName, PriorityInline } from "@/entities/issue";

export function DashboardIssueDrawer({
  detail,
  onClose,
  onStatusChange,
  onAddComment,
}: {
  detail: { issue: DashboardIssue; comments: IssueComment[]; activities: DashboardActivity[] };
  onClose: () => void;
  onStatusChange: (issueId: string, status: IssueStatus) => Promise<void>;
  onAddComment: (issueId: string, content: string) => Promise<void>;
}) {
  const [statusOpen, setStatusOpen] = useState(false);
  const [comment, setComment] = useState("");
  const [submitting, setSubmitting] = useState(false);

  async function submitComment() {
    if (!comment.trim()) {
      return;
    }
    setSubmitting(true);
    await onAddComment(detail.issue.id, comment.trim());
    setComment("");
    setSubmitting(false);
  }

  return (
    <Drawer
      open
      closable={false}
      width="min(100vw, 820px)"
      zIndex={40}
      className="lest-frameless-drawer"
      styles={{ body: { height: "100%" }, mask: { background: "rgba(9, 30, 66, 0.2)" } }}
      onClose={onClose}
    >
      <aside className="workspace-scrollbar h-full w-full overflow-y-auto border-l border-[#DFE1E6] bg-[#F7F8F9] shadow-[0_12px_32px_rgba(9,30,66,0.24)]">
        <header className="sticky top-0 z-10 border-b border-[#DFE1E6] bg-white/95 px-6 py-4 backdrop-blur">
          <div className="flex items-start justify-between gap-4">
            <div className="min-w-0 flex-1">
              <div className="mb-2 flex items-center gap-2 text-[12px] font-semibold text-[#0C66E4]">
                <span>{detail.issue.key}</span>
                <Button className="inline-flex items-center gap-1 rounded-[3px] px-1.5 py-0.5 text-[#44546F] transition hover:bg-[#F1F2F4] hover:text-[#172B4D]">
                  <Copy className="h-3.5 w-3.5" />
                  复制链接
                </Button>
              </div>
              <h2 className="text-[24px] font-semibold leading-8 text-[#172B4D]">{detail.issue.title}</h2>
              <div className="mt-3 flex flex-wrap items-center gap-2">
                <span className="rounded-[4px] bg-[#F1F2F4] px-2 py-1 text-[12px] font-semibold text-[#44546F]">{issueTypeLabel[detail.issue.type]}</span>
                <div className="relative">
                  <Button
                    className={cn("inline-flex items-center gap-1 rounded-[4px] px-2 py-1 text-[12px] font-semibold", statusClassName[detail.issue.status])}
                    onClick={() => setStatusOpen((value) => !value)}
                  >
                    {issueStatusLabel[detail.issue.status]}
                    <ChevronDown className="h-3.5 w-3.5" />
                  </Button>
                  {statusOpen ? (
                    <div className="absolute left-0 top-[30px] z-20 w-[150px] rounded-[4px] border border-[#DFE1E6] bg-white py-1 shadow-[0_8px_24px_rgba(9,30,66,0.18)]">
                      {STATUS_ORDER.map((status) => (
                        <Button
                          key={status}
                          className="flex w-full items-center justify-between px-3 py-2 text-left text-[13px] text-[#172B4D] hover:bg-[#F4F5F7]"
                          onClick={async () => {
                            await onStatusChange(detail.issue.id, status);
                            setStatusOpen(false);
                          }}
                        >
                          {issueStatusLabel[status]}
                          {detail.issue.status === status ? <Check className="h-4 w-4 text-[#0C66E4]" /> : null}
                        </Button>
                      ))}
                    </div>
                  ) : null}
                </div>
                <PriorityInline priority={detail.issue.priority} />
              </div>
            </div>
            <div className="flex items-center gap-1">
              <Button variant="ghost" className="h-8 rounded-[3px] px-2 text-[#44546F] hover:bg-[#F1F2F4]">
                <Edit3 className="h-4 w-4" />
                编辑
              </Button>
              <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[3px] text-[#44546F] hover:bg-[#F1F2F4]" onClick={onClose}>
                <X className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </header>

        <div className="grid gap-6 px-6 py-5 xl:grid-cols-[minmax(0,1fr)_280px]">
          <div className="space-y-4">
            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_1px_rgba(9,30,66,0.08)]">
              <div className="mb-3 text-[13px] font-semibold text-[#172B4D]">描述</div>
              <p className="text-[14px] leading-7 text-[#172B4D]">{detail.issue.description}</p>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_1px_rgba(9,30,66,0.08)]">
              <div className="mb-3 flex items-center justify-between">
                <div className="text-[14px] font-semibold text-[#172B4D]">评论</div>
                <Button className="text-[12px] font-medium text-[#0C66E4] hover:underline">显示活动</Button>
              </div>
              <div className="rounded-[6px] border border-[#DFE1E6] bg-white p-3">
                <div className="flex gap-3">
                  <Avatar name="张晓明" imageSrc={detail.issue.reporter.avatar} className="h-8 w-8" />
                  <div className="min-w-0 flex-1">
                    <Textarea
                      className="min-h-[72px] w-full rounded-[4px] border-[#DFE1E6] px-3 py-2 text-[13px]"
                      placeholder="添加评论，可使用 @ 提及成员"
                      value={comment}
                      onChange={(event) => setComment(event.target.value)}
                    />
                    <div className="mt-3 flex items-center justify-between">
                      <div className="flex items-center gap-2 text-[#626F86]">
                        <Button className="rounded-[3px] p-1 transition hover:bg-[#F1F2F4] hover:text-[#172B4D]">
                          <Paperclip className="h-4 w-4" />
                        </Button>
                        <Button className="rounded-[3px] p-1 transition hover:bg-[#F1F2F4] hover:text-[#172B4D]">
                          <Link2 className="h-4 w-4" />
                        </Button>
                      </div>
                      <Button className="h-8 rounded-[3px] bg-[#0C66E4] px-3 text-xs font-semibold text-white hover:bg-[#0055CC]" loading={submitting} onClick={submitComment}>
                        评论
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
              <div className="mt-4 space-y-4">
                {detail.comments.map((item) => (
                  <div key={item.id} className="flex gap-3 rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-3">
                    <Avatar name={item.author.name} imageSrc={item.author.avatar} className="h-8 w-8" />
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2 text-[13px]">
                        <span className="font-semibold text-[#172B4D]">{item.author.name}</span>
                        <span className="text-[#626F86]">{item.createdAtText}</span>
                      </div>
                      <p className="mt-1 text-[13px] leading-6 text-[#44546F]">{item.content}</p>
                    </div>
                  </div>
                ))}
              </div>
            </section>
          </div>

          <aside className="space-y-4">
            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_1px_rgba(9,30,66,0.08)]">
              <h3 className="text-[13px] font-semibold text-[#172B4D]">详细信息</h3>
              <div className="mt-4 space-y-4 text-[13px]">
                <DetailLine label="负责人">
                  <div className="flex items-center gap-2">
                    {detail.issue.assignee ? <Avatar name={detail.issue.assignee.name} imageSrc={detail.issue.assignee.avatar} className="h-7 w-7" /> : null}
                    <span>{detail.issue.assignee?.name ?? "未分配"}</span>
                  </div>
                </DetailLine>
                <DetailLine label="报告人">
                  <div className="flex items-center gap-2">
                    <Avatar name={detail.issue.reporter.name} imageSrc={detail.issue.reporter.avatar} className="h-7 w-7" />
                    <span>{detail.issue.reporter.name}</span>
                  </div>
                </DetailLine>
                <DetailLine label="故事点">{detail.issue.estimate ?? 0}</DetailLine>
                <DetailLine label="标签">
                  <div className="flex flex-wrap gap-2">
                    {detail.issue.labels.map((label) => (
                      <span key={label} className="rounded-[12px] bg-[#F1F2F4] px-2.5 py-1 text-[12px] text-[#44546F]">
                        {label}
                      </span>
                    ))}
                  </div>
                </DetailLine>
                <DetailLine label="截止日期">{detail.issue.dueDate ?? "未设置"}</DetailLine>
                <DetailLine label="附件">
                  <Button className="inline-flex items-center gap-1 text-[#0C66E4] hover:underline">
                    <Paperclip className="h-3.5 w-3.5" />
                    {detail.issue.attachmentsCount ?? 0} 个附件
                  </Button>
                </DetailLine>
                <DetailLine label="关联问题">
                  <Button className="inline-flex items-center gap-1 text-[#0C66E4] hover:underline">
                    <Plus className="h-3.5 w-3.5" />
                    添加关联
                  </Button>
                </DetailLine>
              </div>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_1px_rgba(9,30,66,0.08)]">
              <h3 className="text-[13px] font-semibold text-[#172B4D]">活动</h3>
              <div className="mt-4 space-y-3">
                {detail.activities.map((item) => (
                  <div key={item.id} className="relative pl-5">
                    <span className="absolute left-0 top-2 h-2 w-2 rounded-full bg-[#0C66E4]" />
                    <div className="text-[12px] text-[#626F86]">{item.createdAtText}</div>
                    <div className="mt-1 text-[13px] leading-5 text-[#172B4D]">
                      {item.actor.name} {item.verb} {item.issueKey} {item.targetText}
                    </div>
                  </div>
                ))}
              </div>
            </section>
          </aside>
        </div>
      </aside>
    </Drawer>
  );
}

function DetailLine({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
      <span className="text-[#626F86]">{label}</span>
      <div className="min-w-0 text-[#172B4D]">{children}</div>
    </div>
  );
}
