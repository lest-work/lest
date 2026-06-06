import { CalendarDays, Link2, MessageSquareMore, Paperclip, PanelRightClose, Pencil, Plus, X } from "lucide-react";

import { type Issue, IssueTypeBadge, PriorityBadge, StatusBadge } from "@/entities/issue";
import { Avatar, Button, Input } from "@/shared/ui";

export interface IssueDetailDrawerProps {
  issue?: Issue;
  onClose: () => void;
}

const activityItems = [
  { id: "a1", user: "刘月", content: "将优先级调整为最高，并补充了排序说明。", time: "刚刚" },
  { id: "a2", user: "陈晓", content: "已同步待办事项列表的拖拽反馈样式。", time: "12 分钟前" },
];

export function IssueDetailDrawer({ issue, onClose }: IssueDetailDrawerProps) {
  if (!issue) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-40 flex justify-end bg-[#091E42]/20">
      <aside className="workspace-scrollbar h-full w-full max-w-[820px] overflow-y-auto border-l border-[#DFE1E6] bg-[#F7F8F9] shadow-[0_12px_32px_rgba(9,30,66,0.24)]">
        <header className="sticky top-0 z-10 border-b border-[#DFE1E6] bg-white/95 px-6 py-4 backdrop-blur">
          <div className="flex items-start justify-between gap-4">
            <div className="min-w-0 flex-1">
              <div className="mb-2 flex items-center gap-2 text-[12px] font-semibold text-[#0C66E4]">
                <span>{issue.key}</span>
                <button className="rounded-[3px] px-1.5 py-0.5 text-[#44546F] transition hover:bg-[#F1F2F4] hover:text-[#172B4D]">
                  复制链接
                </button>
              </div>
              <h2 className="text-[24px] font-semibold leading-8 text-[#172B4D]">{issue.title}</h2>
              <div className="mt-3 flex flex-wrap items-center gap-2">
                <IssueTypeBadge type={issue.type} />
                <StatusBadge status={issue.status} />
                <PriorityBadge priority={issue.priority} />
              </div>
            </div>
            <div className="flex items-center gap-1">
              <Button variant="ghost" className="h-8 rounded-[3px] px-2 text-[#44546F] hover:bg-[#F1F2F4]">
                <Pencil className="h-4 w-4" />
                编辑
              </Button>
              <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[3px] text-[#44546F] hover:bg-[#F1F2F4]">
                <PanelRightClose className="h-4 w-4" />
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
              <p className="text-[14px] leading-7 text-[#172B4D]">{issue.description}</p>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_1px_rgba(9,30,66,0.08)]">
              <div className="mb-3 flex items-center justify-between">
                <div className="flex items-center gap-2 text-[14px] font-semibold text-[#172B4D]">
                  <MessageSquareMore className="h-4 w-4 text-[#0C66E4]" />
                  评论
                </div>
                <button className="text-[12px] font-medium text-[#0C66E4] hover:underline">显示活动</button>
              </div>

              <div className="rounded-[6px] border border-[#DFE1E6] bg-white p-3">
                <div className="flex gap-3">
                  <Avatar name="刘月" imageSrc="https://api.dicebear.com/9.x/lorelei/svg?seed=Lest" className="h-8 w-8" />
                  <div className="min-w-0 flex-1">
                    <Input className="h-10 rounded-[4px] border-[#DFE1E6] text-[13px] shadow-none" placeholder="添加评论..." />
                    <div className="mt-3 flex items-center justify-between">
                      <div className="flex items-center gap-2 text-[#626F86]">
                        <button className="rounded-[3px] p-1 transition hover:bg-[#F1F2F4] hover:text-[#172B4D]">
                          <Paperclip className="h-4 w-4" />
                        </button>
                        <button className="rounded-[3px] p-1 transition hover:bg-[#F1F2F4] hover:text-[#172B4D]">
                          <Link2 className="h-4 w-4" />
                        </button>
                      </div>
                      <Button className="h-8 rounded-[3px] bg-[#0C66E4] px-3 text-xs font-semibold text-white hover:bg-[#0055CC]">
                        评论
                      </Button>
                    </div>
                  </div>
                </div>
              </div>

              <div className="mt-4 space-y-4">
                {activityItems.map((item) => (
                  <div key={item.id} className="flex gap-3 rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-3">
                    <Avatar name={item.user} imageSrc={`https://api.dicebear.com/9.x/lorelei/svg?seed=${encodeURIComponent(item.user)}`} className="h-8 w-8" />
                    <div className="min-w-0 flex-1">
                      <div className="flex items-center gap-2 text-[13px]">
                        <span className="font-semibold text-[#172B4D]">{item.user}</span>
                        <span className="text-[#626F86]">{item.time}</span>
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
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">负责人</span>
                  <div className="flex items-center gap-2 text-[#172B4D]">
                    {issue.assignee ? <Avatar name={issue.assignee.name} imageSrc={issue.assignee.avatarUrl} className="h-7 w-7" /> : null}
                    <span>{issue.assignee?.name ?? "未分配"}</span>
                  </div>
                </div>
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">报告人</span>
                  <div className="flex items-center gap-2 text-[#172B4D]">
                    <Avatar name={issue.reporter.name} imageSrc={issue.reporter.avatarUrl} className="h-7 w-7" />
                    <span>{issue.reporter.name}</span>
                  </div>
                </div>
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">故事点</span>
                  <span className="text-[#172B4D]">{issue.estimate}</span>
                </div>
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">标签</span>
                  <div className="flex flex-wrap gap-2">
                    {issue.labels.map((label) => (
                      <span key={label} className="rounded-[12px] bg-[#F1F2F4] px-2.5 py-1 text-[12px] text-[#44546F]">
                        {label}
                      </span>
                    ))}
                  </div>
                </div>
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">截止日期</span>
                  <span className="inline-flex items-center gap-1 text-[#172B4D]">
                    <CalendarDays className="h-3.5 w-3.5 text-[#626F86]" />
                    {issue.dueDate ?? "未设置"}
                  </span>
                </div>
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">附件</span>
                  <button className="inline-flex items-center gap-1 text-[#0C66E4] hover:underline">
                    <Paperclip className="h-3.5 w-3.5" />
                    2 个附件
                  </button>
                </div>
                <div className="grid grid-cols-[88px_minmax(0,1fr)] items-center gap-3">
                  <span className="text-[#626F86]">关联问题</span>
                  <button className="inline-flex items-center gap-1 text-[#0C66E4] hover:underline">
                    <Plus className="h-3.5 w-3.5" />
                    添加关联
                  </button>
                </div>
              </div>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_1px_rgba(9,30,66,0.08)]">
              <h3 className="text-[13px] font-semibold text-[#172B4D]">活动</h3>
              <div className="mt-4 space-y-3">
                {activityItems.map((item) => (
                  <div key={`${item.id}-timeline`} className="relative pl-5">
                    <span className="absolute left-0 top-2 h-2 w-2 rounded-full bg-[#0C66E4]" />
                    <div className="text-[12px] text-[#626F86]">{item.time}</div>
                    <div className="mt-1 text-[13px] leading-5 text-[#172B4D]">{item.content}</div>
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
