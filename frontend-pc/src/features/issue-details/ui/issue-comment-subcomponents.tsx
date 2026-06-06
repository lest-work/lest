import type { ReactNode } from "react";
import { useState } from "react";
import { AtSign, Bold, Code2, Heading1, Italic, Link2, List, MoreHorizontal, Paperclip, Quote, Smile, Trash2, X } from "lucide-react";

import type { CommentQuickAction, DeleteIssueCommentPayload, EmojiCategory, IssueComment } from "@/api/issue";
import type { PanelMode } from "./issue-comment-section";
import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui";

export function CommentToolbar({
  panelMode,
  canAttach,
  canUseQuickActions,
  onPanelModeChange,
  onInsert,
  onAttach,
}: {
  panelMode?: PanelMode;
  canAttach: boolean;
  canUseQuickActions: boolean;
  onPanelModeChange: (mode: PanelMode) => void;
  onInsert: (text: string) => void;
  onAttach: () => void;
}) {
  return (
    <div className="flex flex-wrap items-center gap-1 text-[#626F86]">
      <MiniTool icon={<Bold className="h-3.5 w-3.5" />} label="加粗" onClick={() => onInsert("**加粗文本**")} />
      <MiniTool icon={<Italic className="h-3.5 w-3.5" />} label="斜体" onClick={() => onInsert("_斜体文本_")} />
      <MiniTool icon={<Heading1 className="h-3.5 w-3.5" />} label="标题" onClick={() => onInsert("### 小标题\n")} />
      <MiniTool icon={<Code2 className="h-3.5 w-3.5" />} label="代码" onClick={() => onInsert("`code`")} />
      <MiniTool icon={<Quote className="h-3.5 w-3.5" />} label="引用" onClick={() => onInsert("> 引用内容\n")} />
      <MiniTool icon={<List className="h-3.5 w-3.5" />} label="列表" onClick={() => onInsert("- 列表项\n")} />
      <MiniTool icon={<Link2 className="h-3.5 w-3.5" />} label="链接" onClick={() => onInsert("[链接标题](https://docs.example.com)")} />
      <MiniTool icon={<AtSign className="h-3.5 w-3.5" />} label="提及成员" onClick={() => onInsert("@张三")} />
      <MiniTool active={panelMode === "emoji"} icon={<Smile className="h-3.5 w-3.5" />} label="表情" onClick={() => onPanelModeChange("emoji")} />
      <MiniTool disabled={!canAttach} icon={<Paperclip className="h-3.5 w-3.5" />} label="附件" onClick={onAttach} />
      <MiniTool disabled={!canUseQuickActions} active={panelMode === "actions"} icon={<MoreHorizontal className="h-3.5 w-3.5" />} label="快捷操作" onClick={() => onPanelModeChange("actions")} />
    </div>
  );
}

export function MiniTool({ icon, label, active, disabled, onClick }: { icon: ReactNode; label: string; active?: boolean; disabled?: boolean; onClick?: () => void }) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      disabled={disabled}
      className={cn("inline-flex h-7 w-7 items-center justify-center rounded-[4px] transition hover:bg-[#F1F2F4] hover:text-[#172B4D] disabled:cursor-not-allowed disabled:opacity-40", active && "bg-[#E9F2FF] text-[#0C66E4]")}
      onClick={onClick}
    >
      {icon}
    </button>
  );
}

export function CommentAssistPanel({
  mode,
  emojiCategories,
  activeCategoryId,
  emojis,
  emojiKeyword,
  quickActions,
  onCategoryChange,
  onEmojiKeywordChange,
  onInsert,
  onClose,
}: {
  mode: PanelMode;
  emojiCategories: EmojiCategory[];
  activeCategoryId: string;
  emojis: string[];
  emojiKeyword: string;
  quickActions: CommentQuickAction[];
  onCategoryChange: (id: string) => void;
  onEmojiKeywordChange: (value: string) => void;
  onInsert: (text: string, quickActionId?: string) => void;
  onClose: () => void;
}) {
  return (
    <div className="absolute left-3 top-[calc(100%-4px)] z-30 w-[320px] rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_12px_32px_rgba(9,30,66,0.18)]">
      <div className="flex h-10 items-center justify-between border-b border-[#EBECF0] px-3">
        <div className="flex gap-4 text-[12px] font-semibold">
          <span className={mode === "emoji" ? "text-[#0C66E4]" : "text-[#626F86]"}>表情</span>
          <span className={mode === "actions" ? "text-[#0C66E4]" : "text-[#626F86]"}>快捷操作</span>
        </div>
        <button className="rounded-[3px] p-1 text-[#626F86] hover:bg-[#F4F5F7]" onClick={onClose}>
          <X className="h-3.5 w-3.5" />
        </button>
      </div>
      {mode === "emoji" ? (
        <div className="p-3">
          <input
            className="mb-3 h-8 w-full rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 text-[12px] outline-none focus:border-[#0C66E4] focus:ring-1 focus:ring-[#0C66E4]"
            placeholder="搜索表情"
            value={emojiKeyword}
            onChange={(event) => onEmojiKeywordChange(event.target.value)}
          />
          <div className="grid grid-cols-[72px_minmax(0,1fr)] gap-3">
            <div className="space-y-1">
              {emojiCategories.map((category) => (
                <button
                  key={category.id}
                  className={cn("h-7 w-full rounded-[3px] px-2 text-left text-[12px]", activeCategoryId === category.id ? "bg-[#E9F2FF] font-semibold text-[#0C66E4]" : "text-[#44546F] hover:bg-[#F4F5F7]")}
                  onClick={() => onCategoryChange(category.id)}
                >
                  {category.label}
                </button>
              ))}
            </div>
            <div className="grid grid-cols-6 gap-1">
              {emojis.map((emoji) => (
                <button key={emoji} className="h-8 rounded-[4px] text-[18px] transition hover:bg-[#F4F5F7]" onClick={() => onInsert(emoji)}>
                  {emoji}
                </button>
              ))}
            </div>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-2 p-3">
          {quickActions.map((action) => (
            <button
              key={action.id}
              disabled={action.disabled}
              className="rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-2 text-left text-[12px] text-[#172B4D] hover:border-[#CCE0FF] hover:bg-[#F0F6FF] disabled:opacity-40"
              onClick={() => onInsert(action.template, action.id)}
            >
              <div className="font-semibold">{action.label}</div>
              <div className="mt-1 text-[#626F86]">{action.shortcut ?? action.description}</div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export function CommentDeleteDialog({
  comment,
  onClose,
  onConfirm,
}: {
  comment: IssueComment;
  onClose: () => void;
  onConfirm: (payload: DeleteIssueCommentPayload) => Promise<void>;
}) {
  const [deleteReplies, setDeleteReplies] = useState(comment.repliesCount > 0);
  const [deleting, setDeleting] = useState(false);

  async function submit() {
    setDeleting(true);
    try {
      await onConfirm({
        issueId: comment.issueId,
        commentId: comment.id,
        confirmed: true,
        deleteReplies,
      });
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-[80] flex items-center justify-center bg-[#091E42]/36 px-4">
      <div className="w-full max-w-[520px] rounded-[8px] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <header className="flex items-center justify-between border-b border-[#EBECF0] px-5 py-4">
          <h3 className="text-[17px] font-bold text-[#172B4D]">删除此评论？</h3>
          <button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#F4F5F7]" onClick={onClose}>
            <X className="h-4 w-4" />
          </button>
        </header>
        <div className="space-y-4 px-5 py-4 text-[13px] text-[#172B4D]">
          <p>删除后，评论内容将无法恢复。</p>
          <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-3 text-[#44546F] line-clamp-3">{comment.body}</div>
          {comment.repliesCount ? (
            <label className="flex items-center gap-2 rounded-[4px] border border-[#DFE1E6] px-3 py-2">
              <input type="checkbox" checked={deleteReplies} onChange={(event) => setDeleteReplies(event.target.checked)} />
              同时删除 {comment.repliesCount} 条回复
            </label>
          ) : null}
        </div>
        <footer className="flex justify-end gap-3 border-t border-[#EBECF0] px-5 py-4">
          <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4" onClick={onClose}>
            取消
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#E34935] px-4 text-white hover:bg-[#C9372C]" loading={deleting} onClick={() => void submit()}>
            <Trash2 className="h-4 w-4" />
            删除
          </Button>
        </footer>
      </div>
    </div>
  );
}
