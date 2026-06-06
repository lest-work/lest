import {
  Bell,
  Check,
  ChevronDown,
  Copy,
  Download,
  Edit3,
  Eye,
  Link2,
  MoreHorizontal,
  Printer,
  Star,
  Trash2,
  X,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { useState } from "react";

import {
  issuePriorityLabel,
  issueStatusLabel,
  issueTypeLabel,
  type Issue,
  type IssueStatus,
} from "@/entities/issue";
import type {
  CancelIssueAttachmentPayload,
  CreateIssueCommentPayload,
  DeleteIssueAttachmentPayload,
  DeleteIssueCommentPayload,
  GetIssueDetailResponse,
  GetIssueAttachmentsResponse,
  GetIssueCommentsResponse,
  IssueRelation,
  RetryIssueAttachmentPayload,
  UpdateIssueCommentPayload,
  UploadIssueAttachmentPayload,
} from "@/api/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button } from "@/shared/ui";
import { IssueAttachmentPanel } from "./issue-attachment-panel";
import { IssueCommentSection } from "./issue-comment-section";

export interface FullIssueDetailDrawerProps {
  detail: GetIssueDetailResponse;
  comments: GetIssueCommentsResponse;
  attachments: GetIssueAttachmentsResponse;
  onClose: () => void;
  onEdit: (issue: Issue) => void;
  onDelete: (issue: Issue) => void;
  onLinkIssue: (issue: Issue) => void;
  onCreateComment: (payload: CreateIssueCommentPayload) => Promise<void>;
  onUpdateComment: (payload: UpdateIssueCommentPayload) => Promise<void>;
  onDeleteComment: (payload: DeleteIssueCommentPayload) => Promise<void>;
  onUploadAttachments: (payload: UploadIssueAttachmentPayload) => Promise<void>;
  onRetryAttachment: (payload: RetryIssueAttachmentPayload) => Promise<void>;
  onCancelAttachment: (payload: CancelIssueAttachmentPayload) => Promise<void>;
  onDeleteAttachment: (payload: DeleteIssueAttachmentPayload) => Promise<void>;
}

const statusOrder: IssueStatus[] = ["todo", "in-progress", "review", "done", "closed"];
const statusClassName: Record<IssueStatus, string> = {
  todo: "bg-[#E9F2FF] text-[#0C66E4]",
  "in-progress": "bg-[#FFF7ED] text-[#B76E00]",
  review: "bg-[#F3F0FF] text-[#5E4DB2]",
  done: "bg-[#E3FCEF] text-[#006644]",
  closed: "bg-[#F1F2F4] text-[#44546F]",
};

export function FullIssueDetailDrawer({
  detail,
  comments,
  attachments,
  onClose,
  onEdit,
  onDelete,
  onLinkIssue,
  onCreateComment,
  onUpdateComment,
  onDeleteComment,
  onUploadAttachments,
  onRetryAttachment,
  onCancelAttachment,
  onDeleteAttachment,
}: FullIssueDetailDrawerProps) {
  const [moreOpen, setMoreOpen] = useState(false);
  const [editingInline, setEditingInline] = useState(false);
  const { issue, extra } = detail;

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-[#091E42]/20">
      <aside className="workspace-scrollbar h-full w-full max-w-[1200px] overflow-y-auto border-l border-[#DFE1E6] bg-[#F7F8F9] shadow-[0_12px_32px_rgba(9,30,66,0.24)]">
        <header className="sticky top-0 z-20 border-b border-[#DFE1E6] bg-white/95 px-6 py-4 backdrop-blur">
          <div className="mb-3 flex items-center gap-2 text-[12px] font-semibold text-[#44546F]">
            <span>问题列表</span>
            <span>/</span>
            <span>DEMO 项目</span>
            <span>/</span>
            <span className="text-[#0C66E4]">{issue.key}</span>
          </div>
          <div className="flex items-start justify-between gap-4">
            <div className="min-w-0 flex-1">
              <h2 className="text-[24px] font-bold leading-8 text-[#172B4D]">
                {issue.key} {issue.title}
              </h2>
              <div className="mt-3 flex flex-wrap items-center gap-2">
                <span className="rounded-[4px] bg-[#FFECE8] px-2 py-1 text-[12px] font-semibold text-[#AE2E24]">{issueTypeLabel[issue.type]}</span>
                <span className={cn("rounded-[4px] px-2 py-1 text-[12px] font-semibold", statusClassName[issue.status])}>{issueStatusLabel[issue.status]}</span>
                <span className="rounded-[4px] bg-[#FFF7ED] px-2 py-1 text-[12px] font-semibold text-[#B76E00]">{issuePriorityLabel[issue.priority]}</span>
                <span className="rounded-[4px] bg-[#F1F2F4] px-2 py-1 text-[12px] text-[#44546F]">{extra.watchers} 人关注</span>
                <span className="rounded-[4px] bg-[#F1F2F4] px-2 py-1 text-[12px] text-[#44546F]">{extra.views} 次浏览</span>
              </div>
            </div>
            <div className="relative flex items-center gap-2">
              <Button className="h-8 rounded-[4px] bg-[#0C66E4] px-3 text-[13px] text-white" onClick={() => setEditingInline(true)}>
                <Edit3 className="h-4 w-4" />
                编辑
              </Button>
              <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[13px]" onClick={() => setMoreOpen((value) => !value)}>
                <MoreHorizontal className="h-4 w-4" />
                更多
                <ChevronDown className="h-3.5 w-3.5" />
              </Button>
              <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[4px]" aria-label="关闭" onClick={onClose}>
                <X className="h-4 w-4" />
              </Button>
              {moreOpen ? (
                <IssueMoreMenu
                  onEdit={() => onEdit(issue)}
                  onLink={() => onLinkIssue(issue)}
                  onDelete={() => onDelete(issue)}
                />
              ) : null}
            </div>
          </div>
        </header>

        <div className="grid gap-4 px-6 py-5 xl:grid-cols-[minmax(0,1fr)_300px_320px]">
          <main className="space-y-4">
            {editingInline ? (
              <section className="rounded-[8px] border border-[#CCE0FF] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
                <div className="mb-3 flex items-center justify-between">
                  <h3 className="text-[15px] font-bold text-[#172B4D]">字段编辑状态</h3>
                  <div className="flex gap-2">
                    <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]" onClick={() => setEditingInline(false)}>
                      取消
                    </Button>
                    <Button className="h-8 rounded-[4px] bg-[#0C66E4] px-3 text-[12px] text-white" onClick={() => setEditingInline(false)}>
                      <Check className="h-3.5 w-3.5" />
                      保存
                    </Button>
                  </div>
                </div>
                <input className="h-10 w-full rounded-[4px] border border-[#0C66E4] px-3 text-[16px] font-semibold text-[#172B4D] ring-1 ring-[#0C66E4]" value={issue.title} onChange={() => undefined} />
                <textarea className="mt-3 min-h-[120px] w-full rounded-[4px] border border-[#DFE1E6] px-3 py-2 text-[13px] leading-6" value={issue.description} onChange={() => undefined} />
                <div className="mt-2 text-[12px] text-[#B76E00]">有变更未保存，请保存或取消。</div>
              </section>
            ) : null}

            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h3 className="mb-4 text-[15px] font-bold text-[#172B4D]">基本信息</h3>
              <div className="grid gap-x-6 gap-y-4 text-[13px] md:grid-cols-3">
                <InfoLine label="问题类型" value={issueTypeLabel[issue.type]} />
                <InfoLine label="优先级" value={issuePriorityLabel[issue.priority]} />
                <InfoLine label="状态" value={issueStatusLabel[issue.status]} />
                <InfoLine label="影响版本" value={extra.affectedVersion} />
                <InfoLine label="修复版本" value={extra.fixedVersion} />
                <InfoLine label="所属迭代" value={extra.sprintName} />
                <InfoLine label="负责人" value={issue.assignee?.name ?? "未分配"} avatar={issue.assignee?.avatarUrl} />
                <InfoLine label="报告人" value={issue.reporter.name} avatar={issue.reporter.avatarUrl} />
                <InfoLine label="截止日期" value={issue.dueDate ?? "未设置"} />
                <InfoLine label="关注人数" value={`${extra.watchers} 人关注`} />
                <InfoLine label="浏览次数" value={`${extra.views} 次`} />
                <InfoLine label="创建时间" value={issue.createdAt ?? "2026-06-06"} />
              </div>
            </section>

            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h3 className="mb-3 text-[15px] font-bold text-[#172B4D]">描述</h3>
              <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-4 text-[13px] leading-7 text-[#172B4D] whitespace-pre-line">{issue.description}</div>
            </section>

            <IssueAttachmentPanel
              issueId={issue.id}
              attachments={attachments.items}
              policy={attachments.policy}
              onUpload={onUploadAttachments}
              onRetry={onRetryAttachment}
              onCancel={onCancelAttachment}
              onDelete={onDeleteAttachment}
            />

            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <div className="mb-3 flex items-center justify-between">
                <h3 className="text-[15px] font-bold text-[#172B4D]">关联问题（{extra.relations.length}）</h3>
                <button className="text-[12px] font-semibold text-[#0C66E4]" onClick={() => onLinkIssue(issue)}>
                  添加关联
                </button>
              </div>
              <div className="space-y-2">
                {extra.relations.map((relation) => (
                  <RelationRow key={relation.id} relation={relation} />
                ))}
              </div>
            </section>

            <IssueCommentSection
              issueId={issue.id}
              comments={comments.items}
              emojiCategories={comments.emojiCategories}
              quickActions={comments.quickActions}
              canComment={comments.permissions.canComment}
              canAttach={comments.permissions.canAttach}
              canUseQuickActions={comments.permissions.canUseQuickActions}
              onCreateComment={onCreateComment}
              onUpdateComment={onUpdateComment}
              onDeleteComment={onDeleteComment}
              onAttachFromComment={async () => {
                await onUploadAttachments({
                  issueId: issue.id,
                  source: "comment",
                  files: [{ name: "评论截图.png", size: 1.2 * 1024 * 1024, mimeType: "image/png" }],
                });
              }}
            />
          </main>

          <aside className="space-y-4">
            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h3 className="text-[15px] font-bold text-[#172B4D]">更多信息</h3>
              <div className="mt-4 space-y-3 text-[13px]">
                <InfoLine label="预估工时" value={`${issue.estimate} 小时`} />
                <InfoLine label="实际工时" value={`${extra.actualHours} 小时`} />
                <InfoLine label="剩余工时" value={`${extra.remainingHours} 小时`} />
                <InfoLine label="严重程度" value={extra.severity === "critical" ? "严重" : "一般"} />
                <InfoLine label="复现概率" value="100%" />
                <InfoLine label="环境" value={extra.environment} />
              </div>
            </section>
            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h3 className="text-[15px] font-bold text-[#172B4D]">状态流转</h3>
              <div className="mt-4 flex flex-wrap items-center gap-2">
                {statusOrder.map((status, index) => (
                  <div key={status} className="flex items-center gap-2">
                    <span className={cn("rounded-[4px] px-2 py-1 text-[12px] font-semibold", statusClassName[status])}>{issueStatusLabel[status]}</span>
                    {index < statusOrder.length - 1 ? <span className="text-[#8C9BAB]">→</span> : null}
                  </div>
                ))}
              </div>
            </section>
          </aside>

          <aside className="space-y-4">
            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h3 className="text-[15px] font-bold text-[#172B4D]">时间线</h3>
              <div className="mt-4 space-y-4">
                {detail.activities.map((item, index) => (
                  <div key={item.id} className="relative pl-6">
                    <span className="absolute left-0 top-1.5 h-2.5 w-2.5 rounded-full bg-[#0C66E4]" />
                    {index < detail.activities.length - 1 ? <span className="absolute bottom-[-18px] left-[5px] top-4 w-px bg-[#DFE1E6]" /> : null}
                    <div className="text-[12px] text-[#626F86]">{item.time}</div>
                    <div className="mt-1 rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-3 text-[13px] leading-5 text-[#172B4D]">
                      <span className="font-semibold">{item.user}</span> {item.content}
                    </div>
                  </div>
                ))}
              </div>
            </section>
          </aside>
        </div>
      </aside>
    </div>
  );
}

function IssueMoreMenu({ onEdit, onLink, onDelete }: { onEdit: () => void; onLink: () => void; onDelete: () => void }) {
  const groups: Array<Array<{ label: string; icon: LucideIcon; danger?: boolean; action?: () => void }>> = [
    [
      { label: "转换为", icon: Edit3 },
      { label: "移动到项目", icon: Eye },
      { label: "移动到迭代", icon: Eye },
      { label: "克隆问题", icon: Copy },
      { label: "链接问题", icon: Link2, action: onLink },
    ],
    [
      { label: "导出", icon: Download },
      { label: "打印", icon: Printer },
      { label: "复制链接", icon: Copy },
      { label: "监控", icon: Bell },
      { label: "关注", icon: Star },
    ],
    [{ label: "删除", icon: Trash2, danger: true, action: onDelete }],
  ];

  return (
    <div className="absolute right-8 top-10 z-30 w-[220px] rounded-[6px] border border-[#DFE1E6] bg-white py-2 shadow-[0_12px_32px_rgba(9,30,66,0.22)]">
      {groups.map((items, groupIndex) => (
        <div key={groupIndex} className={cn(groupIndex > 0 && "border-t border-[#EBECF0] pt-2")}>
          {items.map((item) => {
            const Icon = item.icon;
            return (
              <button
                key={item.label}
                className={cn("flex w-full items-center gap-2 px-4 py-2 text-left text-[13px] hover:bg-[#F4F5F7]", item.danger ? "text-[#E34935]" : "text-[#172B4D]")}
                onClick={() => {
                  item.action?.();
                  if (item.label === "转换为") {
                    onEdit();
                  }
                }}
              >
                <Icon className="h-4 w-4" />
                {item.label}
              </button>
            );
          })}
        </div>
      ))}
    </div>
  );
}

function InfoLine({ label, value, avatar }: { label: string; value: string; avatar?: string }) {
  return (
    <div className="grid grid-cols-[86px_minmax(0,1fr)] items-center gap-3">
      <span className="text-[#626F86]">{label}</span>
      <span className="flex min-w-0 items-center gap-2 font-medium text-[#172B4D]">
        {avatar ? <Avatar name={value} imageSrc={avatar} className="h-6 w-6" /> : null}
        <span className="truncate">{value}</span>
      </span>
    </div>
  );
}

function RelationRow({ relation }: { relation: IssueRelation }) {
  return (
    <div className="flex items-center justify-between gap-3 rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-2 text-[13px]">
      <div className="min-w-0">
        <div className="flex items-center gap-2">
          <Link2 className="h-3.5 w-3.5 text-[#0C66E4]" />
          <span className="font-semibold text-[#0C66E4]">{relation.issue.key}</span>
          <span className="text-[#626F86]">{relation.type}</span>
        </div>
        <div className="mt-1 truncate text-[#172B4D]">{relation.issue.title}</div>
      </div>
      <span className={cn("shrink-0 rounded-[4px] px-2 py-1 text-[12px] font-semibold", statusClassName[relation.issue.status])}>{issueStatusLabel[relation.issue.status]}</span>
    </div>
  );
}
