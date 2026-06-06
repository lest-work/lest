import {
  AtSign,
  Bold,
  Check,
  Code2,
  Heading1,
  Italic,
  Link2,
  List,
  MessageSquareText,
  MoreHorizontal,
  Paperclip,
  Pencil,
  Quote,
  Send,
  Smile,
  Trash2,
  X,
} from "lucide-react";
import type { ReactNode } from "react";
import { useMemo, useState } from "react";

import type {
  CommentQuickAction,
  CreateIssueCommentPayload,
  DeleteIssueCommentPayload,
  EmojiCategory,
  IssueComment,
  UpdateIssueCommentPayload,
} from "@/api/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button } from "@/shared/ui";
import { CommentToolbar, MiniTool, CommentAssistPanel, CommentDeleteDialog } from "./issue-comment-subcomponents";

export interface IssueCommentSectionProps {
  issueId: string;
  comments: IssueComment[];
  emojiCategories: EmojiCategory[];
  quickActions: CommentQuickAction[];
  canComment: boolean;
  canAttach: boolean;
  canUseQuickActions: boolean;
  onCreateComment: (payload: CreateIssueCommentPayload) => Promise<void>;
  onUpdateComment: (payload: UpdateIssueCommentPayload) => Promise<void>;
  onDeleteComment: (payload: DeleteIssueCommentPayload) => Promise<void>;
  onAttachFromComment: () => Promise<void>;
}

export type PanelMode = "comment" | "quick-action" | "emoji" | "assist" | "actions";

export function IssueCommentSection({
  issueId,
  comments,
  emojiCategories,
  quickActions,
  canComment,
  canAttach,
  canUseQuickActions,
  onCreateComment,
  onUpdateComment,
  onDeleteComment,
  onAttachFromComment,
}: IssueCommentSectionProps) {
  const [body, setBody] = useState("");
  const [panelMode, setPanelMode] = useState<PanelMode>();
  const [emojiKeyword, setEmojiKeyword] = useState("");
  const [activeCategoryId, setActiveCategoryId] = useState(emojiCategories[0]?.id ?? "popular");
  const [submitting, setSubmitting] = useState(false);
  const [editingCommentId, setEditingCommentId] = useState<string>();
  const [editingBody, setEditingBody] = useState("");
  const [savingCommentId, setSavingCommentId] = useState<string>();
  const [commentMenuId, setCommentMenuId] = useState<string>();
  const [deleteTarget, setDeleteTarget] = useState<IssueComment>();

  const activeCategory = emojiCategories.find((category) => category.id === activeCategoryId) ?? emojiCategories[0];
  const filteredEmojis = useMemo(() => {
    if (!emojiKeyword.trim()) {
      return activeCategory?.emojis ?? [];
    }
    return emojiCategories.flatMap((category) => category.emojis).filter((emoji) => emoji.includes(emojiKeyword.trim()));
  }, [activeCategory?.emojis, emojiCategories, emojiKeyword]);

  function insertText(value: string) {
    setBody((current) => `${current}${current && !current.endsWith("\n") ? " " : ""}${value}`);
  }

  async function submitComment() {
    if (!body.trim() || submitting || !canComment) {
      return;
    }
    setSubmitting(true);
    try {
      await onCreateComment({ issueId, body: body.trim(), mentionedUserIds: body.includes("@") ? ["user-mentioned"] : [] });
      setBody("");
      setPanelMode(undefined);
    } finally {
      setSubmitting(false);
    }
  }

  async function saveComment(comment: IssueComment) {
    if (!editingBody.trim()) {
      return;
    }
    setSavingCommentId(comment.id);
    try {
      await onUpdateComment({
        issueId,
        commentId: comment.id,
        body: editingBody.trim(),
        expectedUpdatedAt: comment.updatedAt,
      });
      setEditingCommentId(undefined);
      setEditingBody("");
    } finally {
      setSavingCommentId(undefined);
    }
  }

  return (
    <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
      <div className="mb-3 flex items-center justify-between">
        <h3 className="flex items-center gap-2 text-[15px] font-bold text-[#172B4D]">
          <MessageSquareText className="h-4 w-4 text-[#0C66E4]" />
          评论（{comments.length}）
        </h3>
        <div className="flex rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] p-0.5 text-[12px] font-semibold">
          {["评论", "历史记录", "工作日志"].map((tab, index) => (
            <button key={tab} className={cn("h-7 rounded-[3px] px-3", index === 0 ? "bg-white text-[#0C66E4] shadow-sm" : "text-[#626F86] hover:text-[#172B4D]")}>
              {tab}
            </button>
          ))}
        </div>
      </div>

      <div className="relative rounded-[6px] border border-[#DFE1E6] bg-white p-3">
        <textarea
          className="min-h-[92px] w-full resize-none rounded-[4px] border border-transparent bg-white px-2 py-2 text-[13px] leading-6 text-[#172B4D] outline-none placeholder:text-[#97A0AF] focus:border-[#0C66E4] focus:ring-1 focus:ring-[#0C66E4]"
          placeholder="添加评论，可使用 @ 提及成员，支持 Markdown"
          value={body}
          disabled={!canComment}
          onChange={(event) => setBody(event.target.value)}
        />
        <div className="mt-2 flex flex-wrap items-center justify-between gap-3 border-t border-[#EBECF0] pt-2">
          <CommentToolbar
            panelMode={panelMode}
            canAttach={canAttach}
            canUseQuickActions={canUseQuickActions}
            onPanelModeChange={(mode) => setPanelMode((current) => (current === mode ? undefined : mode))}
            onInsert={insertText}
            onAttach={() => void onAttachFromComment()}
          />
          <Button className="h-8 rounded-[4px] bg-[#0C66E4] px-3 text-[12px] text-white" disabled={!body.trim() || !canComment} loading={submitting} onClick={() => void submitComment()}>
            <Send className="h-3.5 w-3.5" />
            评论
          </Button>
        </div>

        {panelMode ? (
          <CommentAssistPanel
            mode={panelMode}
            emojiCategories={emojiCategories}
            activeCategoryId={activeCategoryId}
            emojis={filteredEmojis}
            emojiKeyword={emojiKeyword}
            quickActions={quickActions}
            onCategoryChange={setActiveCategoryId}
            onEmojiKeywordChange={setEmojiKeyword}
            onInsert={(text) => {
              insertText(text);
            }}
            onClose={() => setPanelMode(undefined)}
          />
        ) : null}
      </div>

      <div className="mt-4 space-y-3">
        {comments.map((comment) => (
          <article key={comment.id} className={cn("relative rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-3", editingCommentId === comment.id && "border-[#CCE0FF] bg-white")}>
            <div className="flex gap-3">
              <Avatar name={comment.author.name} imageSrc={comment.author.avatarUrl} className="h-8 w-8" />
              <div className="min-w-0 flex-1">
                <div className="flex items-start justify-between gap-3">
                  <div className="min-w-0">
                    <div className="flex flex-wrap items-center gap-2 text-[13px]">
                      <span className="font-semibold text-[#172B4D]">{comment.author.name}</span>
                      <span className="text-[#626F86]">{comment.createdAt}</span>
                      {comment.edited ? <span className="text-[#626F86]">已编辑</span> : null}
                    </div>
                  </div>
                  <button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#EBECF0] hover:text-[#172B4D]" onClick={() => setCommentMenuId((current) => (current === comment.id ? undefined : comment.id))}>
                    <MoreHorizontal className="h-4 w-4" />
                  </button>
                  {commentMenuId === comment.id ? (
                    <div className="absolute right-3 top-10 z-20 w-[160px] overflow-hidden rounded-[6px] border border-[#DFE1E6] bg-white py-1 shadow-[0_12px_28px_rgba(9,30,66,0.18)]">
                      <button
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-[13px] text-[#172B4D] hover:bg-[#F4F5F7]"
                        disabled={!comment.canEdit}
                        onClick={() => {
                          setEditingCommentId(comment.id);
                          setEditingBody(comment.body);
                          setCommentMenuId(undefined);
                        }}
                      >
                        <Pencil className="h-4 w-4" />
                        编辑
                      </button>
                      <button
                        className="flex w-full items-center gap-2 px-3 py-2 text-left text-[13px] text-[#E34935] hover:bg-[#FFF4F2]"
                        disabled={!comment.canDelete}
                        onClick={() => {
                          setDeleteTarget(comment);
                          setCommentMenuId(undefined);
                        }}
                      >
                        <Trash2 className="h-4 w-4" />
                        删除
                      </button>
                    </div>
                  ) : null}
                </div>

                {editingCommentId === comment.id ? (
                  <div className="mt-2">
                    <textarea
                      className="min-h-[104px] w-full resize-none rounded-[4px] border border-[#0C66E4] bg-white px-3 py-2 text-[13px] leading-6 text-[#172B4D] outline-none ring-1 ring-[#0C66E4]"
                      value={editingBody}
                      onChange={(event) => setEditingBody(event.target.value)}
                    />
                    <div className="mt-2 flex items-center justify-between">
                      <div className="flex items-center gap-1 text-[#626F86]">
                        <MiniTool icon={<Bold className="h-3.5 w-3.5" />} label="加粗" />
                        <MiniTool icon={<Italic className="h-3.5 w-3.5" />} label="斜体" />
                        <MiniTool icon={<Link2 className="h-3.5 w-3.5" />} label="链接" />
                        <MiniTool icon={<Smile className="h-3.5 w-3.5" />} label="表情" />
                      </div>
                      <div className="flex gap-2">
                        <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]" onClick={() => setEditingCommentId(undefined)}>
                          取消
                        </Button>
                        <Button className="h-8 rounded-[4px] bg-[#0C66E4] px-3 text-[12px] text-white" loading={savingCommentId === comment.id} onClick={() => void saveComment(comment)}>
                          <Check className="h-3.5 w-3.5" />
                          保存
                        </Button>
                      </div>
                    </div>
                  </div>
                ) : (
                  <>
                    <p className="mt-2 whitespace-pre-line text-[13px] leading-6 text-[#172B4D]">{comment.body}</p>
                    <div className="mt-3 flex flex-wrap items-center gap-3 text-[12px] text-[#626F86]">
                      {comment.reactions.map((reaction) => (
                        <button key={reaction.emoji} className={cn("rounded-[12px] border px-2 py-0.5", reaction.reactedByMe ? "border-[#CCE0FF] bg-[#E9F2FF] text-[#0C66E4]" : "border-[#DFE1E6] bg-white")}>
                          {reaction.emoji} {reaction.count}
                        </button>
                      ))}
                      <button className="hover:text-[#0C66E4]">回复</button>
                      {comment.repliesCount ? <span>{comment.repliesCount} 条回复</span> : null}
                    </div>
                  </>
                )}
              </div>
            </div>
          </article>
        ))}
      </div>

      {deleteTarget ? (
        <CommentDeleteDialog
          comment={deleteTarget}
          onClose={() => setDeleteTarget(undefined)}
          onConfirm={async (payload) => {
            await onDeleteComment(payload);
            setDeleteTarget(undefined);
          }}
        />
      ) : null}
    </section>
  );
}

