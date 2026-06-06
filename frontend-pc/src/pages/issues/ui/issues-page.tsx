import { Download, MoreHorizontal, Plus, Search, SlidersHorizontal } from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";

import {
  issuePriorityLabel,
  issueStatusLabel,
  issueTypeLabel,
  StatusPill,
  type Issue,
  type User,
} from "@/entities/issue";
import {
  issueService,
  type CancelIssueAttachmentPayload,
  type CreateIssuePayload,
  type CreateIssueCommentPayload,
  type DeleteIssueAttachmentPayload,
  type DeleteIssueCommentPayload,
  type GetIssueAttachmentsResponse,
  type GetIssueCommentsResponse,
  type GetIssueDetailResponse,
  type IssueRelationType,
  type RetryIssueAttachmentPayload,
  type SearchLinkableIssuesResponse,
  type UpdateIssueCommentPayload,
  type UpdateIssuePayload,
  type UploadIssueAttachmentPayload,
} from "@/api/issue";

import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import {
  FullIssueDetailDrawer,
  IssueCreateDialog,
  IssueDeleteConfirmDialog,
  IssueEditDrawer,
  LinkIssueDialog,
} from "@/features/issue-details";
import { cn } from "@/shared/lib/utils";
import { Avatar, Badge, Button, Input, Pagination, ToastViewport, type ToastMessage } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

const pageSize = 8;

export function IssuesPage({ onNavigate }: { onNavigate: (page: WorkspacePageKey) => void }) {
  const [issues, setIssues] = useState<Issue[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);
  const [detail, setDetail] = useState<GetIssueDetailResponse>();
  const [commentPanel, setCommentPanel] = useState<GetIssueCommentsResponse>();
  const [attachmentPanel, setAttachmentPanel] = useState<GetIssueAttachmentsResponse>();
  const [editingIssue, setEditingIssue] = useState<Issue>();
  const [deletingIssue, setDeletingIssue] = useState<Issue>();
  const [linkingIssue, setLinkingIssue] = useState<Issue>();
  const [toasts, setToasts] = useState<ToastMessage[]>([
    { id: "demo-success", title: "创建成功", description: "问题 DEMO-129 已创建", tone: "success", actionLabel: "撤销" },
    { id: "demo-info", title: "已取消关联", description: "已取消与 DEMO-128 的关联", tone: "info" },
  ]);

  const loadIssues = useCallback(async () => {
    setLoading(true);
    try {
      const response = await issueService.getBacklogIssues({
        projectKey: "lest-platform",
        keyword,
        onlyBacklog: false,
        page,
        pageSize,
      });
      setIssues(response.items);
      setTotal(response.total);
    } finally {
      setLoading(false);
    }
  }, [keyword, page]);

  useEffect(() => {
    void loadIssues();
  }, [loadIssues]);

  const users = useMemo(() => {
    const userMap = new Map<string, User>();
    issues.forEach((issue) => {
      if (issue.assignee) {
        userMap.set(issue.assignee.id, issue.assignee);
      }
      userMap.set(issue.reporter.id, issue.reporter);
    });
    return Array.from(userMap.values());
  }, [issues]);

  function pushToast(message: Omit<ToastMessage, "id">) {
    setToasts((items) => [{ ...message, id: `${Date.now()}-${items.length}` }, ...items].slice(0, 5));
  }

  async function refreshDetail(issueId: string) {
    const [response, comments, attachments] = await Promise.all([
      issueService.getIssueDetail({ issueId }),
      issueService.getIssueComments({ issueId, page: 1, pageSize: 20 }),
      issueService.getIssueAttachments({ issueId }),
    ]);
    setDetail(response);
    setCommentPanel(comments);
    setAttachmentPanel(attachments);
  }

  async function refreshComments(issueId: string) {
    const comments = await issueService.getIssueComments({ issueId, page: 1, pageSize: 20 });
    setCommentPanel(comments);
  }

  async function refreshAttachments(issueId: string) {
    const [response, attachments] = await Promise.all([
      issueService.getIssueDetail({ issueId }),
      issueService.getIssueAttachments({ issueId }),
    ]);
    setDetail(response);
    setAttachmentPanel(attachments);
  }

  async function openDetail(issueId: string) {
    await refreshDetail(issueId);
  }

  async function createIssue(payload: CreateIssuePayload) {
    const response = await issueService.createIssue(payload);
    await loadIssues();
    pushToast({ title: "创建成功", description: response.message, tone: "success", actionLabel: "查看详情" });
    return response;
  }

  async function updateIssue(payload: UpdateIssuePayload) {
    const response = await issueService.updateIssue(payload);
    setEditingIssue(undefined);
    pushToast({ title: payload.saveAsDraft ? "草稿已保存" : "更新成功", description: response.message, tone: "success" });
    await loadIssues();
    if (detail?.issue.id === payload.issueId) {
      await openDetail(payload.issueId);
    }
  }

  async function createComment(payload: CreateIssueCommentPayload) {
    const response = await issueService.createIssueComment(payload);
    pushToast({ title: "评论成功", description: response.message, tone: "success" });
    await refreshComments(payload.issueId);
  }

  async function updateComment(payload: UpdateIssueCommentPayload) {
    const response = await issueService.updateIssueComment(payload);
    pushToast({ title: "编辑成功", description: response.message, tone: "success" });
    await refreshComments(payload.issueId);
  }

  async function deleteComment(payload: DeleteIssueCommentPayload) {
    const response = await issueService.deleteIssueComment(payload);
    pushToast({ title: "删除成功", description: response.message, tone: "success" });
    await refreshComments(payload.issueId);
  }

  async function uploadAttachments(payload: UploadIssueAttachmentPayload) {
    const response = await issueService.uploadIssueAttachments(payload);
    pushToast({ title: "上传已开始", description: response.message, tone: "info" });
    await refreshAttachments(payload.issueId);
  }

  async function retryAttachment(payload: RetryIssueAttachmentPayload) {
    const response = await issueService.retryIssueAttachment(payload);
    pushToast({ title: "正在重试", description: response.message, tone: "info" });
    await refreshAttachments(payload.issueId);
  }

  async function cancelAttachment(payload: CancelIssueAttachmentPayload) {
    const response = await issueService.cancelIssueAttachment(payload);
    pushToast({ title: "已取消上传", description: response.message, tone: "info" });
    await refreshAttachments(payload.issueId);
  }

  async function deleteAttachment(payload: DeleteIssueAttachmentPayload) {
    const response = await issueService.deleteIssueAttachment(payload);
    pushToast({ title: "附件已删除", description: response.message, tone: "success" });
    await refreshAttachments(payload.issueId);
  }

  const loadLinkable = useCallback(
    async ({ keyword: nextKeyword, relationType, page: nextPage }: { keyword: string; relationType: IssueRelationType; page: number }): Promise<SearchLinkableIssuesResponse> => {
      if (!linkingIssue) {
        return { items: [], total: 0, relationTypes: [] };
      }
      return issueService.searchLinkableIssues({ issueId: linkingIssue.id, keyword: nextKeyword, relationType, page: nextPage, pageSize: 5 });
    },
    [linkingIssue],
  );

  return (
    <ProjectWorkspaceShell
      activePage="issues"
      title="问题管理"
      subtitle="覆盖新建、编辑、详情、更多操作、删除确认、关联问题选择和反馈提示。"
      onNavigate={onNavigate}
      actions={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white">
            <Download className="h-4 w-4" />
            导出
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" onClick={() => setCreateOpen(true)}>
            <Plus className="h-4 w-4" />
            新建问题
          </Button>
        </>
      }
    >
      <ToastViewport messages={toasts} onClose={(id) => setToasts((items) => items.filter((item) => item.id !== id))} onAction={() => detail?.issue && void openDetail(detail.issue.id)} />

      <div className="mb-4 grid gap-4 xl:grid-cols-4">
        {[
          ["总问题数", total, "当前项目全部问题", "blue"],
          ["进行中", issues.filter((issue) => issue.status === "in-progress").length, "需要继续推进", "orange"],
          ["高优先级", issues.filter((issue) => issue.priority === "high" || issue.priority === "highest").length, "需要重点关注", "red"],
          ["已完成", issues.filter((issue) => issue.status === "done" || issue.status === "closed").length, "本周期完成", "green"],
        ].map(([label, value, helper, tone]) => (
          <section key={label} className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
            <div className="text-[13px] font-semibold text-[#626F86]">{label}</div>
            <div className="mt-3 flex items-end justify-between">
              <span className="text-[28px] font-bold text-[#172B4D]">{value}</span>
              <span className={cn("h-2.5 w-2.5 rounded-full", tone === "blue" && "bg-[#0C66E4]", tone === "orange" && "bg-[#FF9F1A]", tone === "red" && "bg-[#E34935]", tone === "green" && "bg-[#22A06B]")} />
            </div>
            <div className="mt-2 text-[12px] text-[#626F86]">{helper}</div>
          </section>
        ))}
      </div>

      <section className="rounded-[8px] border border-[#DFE1E6] bg-white shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
        <div className="flex flex-wrap items-center gap-3 border-b border-[#EBECF0] px-4 py-4">
          <div className="relative w-[320px]">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-[#6B778C]" />
            <Input
              className="h-9 rounded-[4px] border-[#DFE1E6] pl-9 text-[13px]"
              placeholder="搜索问题标题或编号"
              value={keyword}
              onChange={(event) => {
                setKeyword(event.target.value);
                setPage(1);
              }}
            />
          </div>
          {["所有类型", "所有状态", "所有优先级", "全部负责人"].map((item) => (
            <button key={item} className="h-9 rounded-[4px] border border-[#DFE1E6] bg-white px-4 text-[13px] font-medium text-[#172B4D] hover:bg-[#F4F5F7]">
              {item}
            </button>
          ))}
          <Button variant="outline" className="ml-auto h-9 rounded-[4px] bg-white">
            <SlidersHorizontal className="h-4 w-4" />
            列设置
          </Button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[980px] table-fixed text-left text-[13px]">
            <thead className="bg-[#FAFBFC] text-[12px] font-bold text-[#626F86]">
              <tr>
                <th className="w-10 px-4 py-3"><input type="checkbox" /></th>
                <th className="w-[110px] px-3 py-3">编号</th>
                <th className="px-3 py-3">标题</th>
                <th className="w-[110px] px-3 py-3">类型</th>
                <th className="w-[110px] px-3 py-3">状态</th>
                <th className="w-[110px] px-3 py-3">优先级</th>
                <th className="w-[130px] px-3 py-3">负责人</th>
                <th className="w-[110px] px-3 py-3">操作</th>
              </tr>
            </thead>
            <tbody>
              {issues.map((issue) => (
                <tr key={issue.id} className="border-t border-[#EBECF0] hover:bg-[#F7F8F9]">
                  <td className="px-4 py-3"><input type="checkbox" /></td>
                  <td className="px-3 py-3 font-bold text-[#0C66E4]">{issue.key}</td>
                  <td className="truncate px-3 py-3 font-semibold text-[#172B4D]">
                    <button className="truncate hover:text-[#0C66E4]" onClick={() => void openDetail(issue.id)}>{issue.title}</button>
                  </td>
                  <td className="px-3 py-3"><Badge className="rounded-[4px] bg-[#F1F2F4] px-2 py-1 text-[12px] text-[#44546F]">{issueTypeLabel[issue.type]}</Badge></td>
                  <td className="px-3 py-3"><StatusPill status={issue.status} /></td>
                  <td className="px-3 py-3">{issuePriorityLabel[issue.priority]}</td>
                  <td className="px-3 py-3">
                    {issue.assignee ? (
                      <div className="flex items-center gap-2">
                        <Avatar name={issue.assignee.name} imageSrc={issue.assignee.avatarUrl} className="h-7 w-7" />
                        <span className="truncate">{issue.assignee.name}</span>
                      </div>
                    ) : (
                      "未分配"
                    )}
                  </td>
                  <td className="px-3 py-3">
                    <div className="flex items-center gap-1">
                      <button className="rounded-[4px] p-1.5 text-[#44546F] hover:bg-[#F4F5F7]" onClick={() => setEditingIssue(issue)}>编辑</button>
                      <button className="rounded-[4px] p-1.5 text-[#44546F] hover:bg-[#F4F5F7]" onClick={() => void openDetail(issue.id)}>
                        <MoreHorizontal className="h-4 w-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {loading ? (
                <tr>
                  <td className="px-4 py-8 text-center text-[#626F86]" colSpan={8}>正在加载问题...</td>
                </tr>
              ) : null}
            </tbody>
          </table>
        </div>
        <Pagination page={page} pageSize={pageSize} total={total} onPageChange={setPage} />
      </section>

      {createOpen ? (
        <IssueCreateDialog
          users={users}
          onClose={() => setCreateOpen(false)}
          onSubmit={createIssue}
          onViewIssue={(issue) => {
            setCreateOpen(false);
            void openDetail(issue.id);
          }}
        />
      ) : null}
      {detail && commentPanel && attachmentPanel ? (
        <FullIssueDetailDrawer
          detail={detail}
          comments={commentPanel}
          attachments={attachmentPanel}
          onClose={() => setDetail(undefined)}
          onEdit={setEditingIssue}
          onDelete={setDeletingIssue}
          onLinkIssue={setLinkingIssue}
          onCreateComment={createComment}
          onUpdateComment={updateComment}
          onDeleteComment={deleteComment}
          onUploadAttachments={uploadAttachments}
          onRetryAttachment={retryAttachment}
          onCancelAttachment={cancelAttachment}
          onDeleteAttachment={deleteAttachment}
        />
      ) : null}
      {editingIssue ? <IssueEditDrawer issue={editingIssue} users={users} onClose={() => setEditingIssue(undefined)} onSubmit={updateIssue} /> : null}
      {deletingIssue ? (
        <IssueDeleteConfirmDialog
          issue={deletingIssue}
          onClose={() => setDeletingIssue(undefined)}
          onConfirm={async (payload) => {
            const response = await issueService.deleteIssue(payload);
            setDeletingIssue(undefined);
            setDetail(undefined);
            setCommentPanel(undefined);
            setAttachmentPanel(undefined);
            pushToast({ title: "删除成功", description: response.message, tone: "success" });
            await loadIssues();
          }}
        />
      ) : null}
      {linkingIssue ? (
        <LinkIssueDialog
          issue={linkingIssue}
          loadCandidates={loadLinkable}
          onClose={() => setLinkingIssue(undefined)}
          onConfirm={async (payload) => {
            const response = await issueService.linkIssues(payload);
            setLinkingIssue(undefined);
            pushToast({ title: "关联成功", description: response.message, tone: "success" });
            if (detail?.issue.id === payload.issueId) {
              await openDetail(payload.issueId);
            }
          }}
        />
      ) : null}
    </ProjectWorkspaceShell>
  );
}

