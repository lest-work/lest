import { AlertTriangle, Check, Link2, Search, Trash2, X } from "lucide-react";
import { useEffect, useState } from "react";

import {
  issuePriorityLabel,
  issueStatusLabel,
  issueTypeLabel,
  type Issue,
  type IssuePriority,
  type IssueStatus,
  type User,
} from "@/entities/issue";
import type {
  DeleteIssuePayload,
  IssueRelationType,
  LinkIssuesPayload,
  SearchLinkableIssuesResponse,
  UpdateIssuePayload,
} from "@/api/issue";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Input, Pagination } from "@/shared/ui";

const relationLabel: Record<IssueRelationType, string> = {
  blocks: "阻塞",
  blocked_by: "被阻塞",
  duplicates: "重复",
  relates_to: "相关",
  subtask: "子任务",
  parent: "父任务",
};

export function IssueDeleteConfirmDialog({
  issue,
  onClose,
  onConfirm,
}: {
  issue: Issue;
  onClose: () => void;
  onConfirm: (payload: DeleteIssuePayload) => Promise<void>;
}) {
  const [confirmed, setConfirmed] = useState(false);
  const [includeSubtasks, setIncludeSubtasks] = useState(true);
  const [unlinkRelatedIssues, setUnlinkRelatedIssues] = useState(true);
  const [deleting, setDeleting] = useState(false);

  async function submit() {
    if (!confirmed) {
      return;
    }
    setDeleting(true);
    try {
      await onConfirm({ issueId: issue.id, confirmed, includeSubtasks, unlinkRelatedIssues });
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-[70] flex items-center justify-center bg-[#091E42]/36 px-4">
      <div className="w-full max-w-[520px] rounded-[8px] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <header className="flex items-start gap-3 border-b border-[#EBECF0] px-6 py-5">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-[#FFECE8] text-[#E34935]">
            <AlertTriangle className="h-5 w-5" />
          </div>
          <div className="min-w-0 flex-1">
            <h2 className="text-[18px] font-bold text-[#172B4D]">删除问题确认</h2>
            <p className="mt-1 text-[13px] leading-6 text-[#626F86]">删除后问题、评论、附件和关联信息将从当前项目视图中移除。</p>
          </div>
          <button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#F4F5F7]" onClick={onClose}>
            <X className="h-5 w-5" />
          </button>
        </header>
        <div className="space-y-4 px-6 py-5">
          <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
            <div className="text-[12px] font-semibold text-[#626F86]">被删除问题</div>
            <div className="mt-2 flex items-center justify-between gap-4">
              <div className="min-w-0">
                <div className="text-[13px] font-bold text-[#0C66E4]">{issue.key}</div>
                <div className="truncate text-[14px] font-semibold text-[#172B4D]">{issue.title}</div>
              </div>
              <span className="rounded-[4px] bg-[#FFECE8] px-2 py-1 text-[12px] font-semibold text-[#AE2E24]">{issueTypeLabel[issue.type]}</span>
            </div>
          </div>
          <label className="flex items-start gap-3 rounded-[4px] border border-[#DFE1E6] px-3 py-3 text-[13px] text-[#172B4D]">
            <input className="mt-0.5" type="checkbox" checked={includeSubtasks} onChange={(event) => setIncludeSubtasks(event.target.checked)} />
            同时删除该问题下的子任务
          </label>
          <label className="flex items-start gap-3 rounded-[4px] border border-[#DFE1E6] px-3 py-3 text-[13px] text-[#172B4D]">
            <input className="mt-0.5" type="checkbox" checked={unlinkRelatedIssues} onChange={(event) => setUnlinkRelatedIssues(event.target.checked)} />
            删除前取消与其他问题的关联关系
          </label>
          <label className="flex items-start gap-3 rounded-[4px] border border-[#FFD5CC] bg-[#FFF4F2] px-3 py-3 text-[13px] text-[#AE2E24]">
            <input className="mt-0.5" type="checkbox" checked={confirmed} onChange={(event) => setConfirmed(event.target.checked)} />
            我确认要删除该问题，此操作不可撤销
          </label>
        </div>
        <footer className="flex justify-end gap-3 border-t border-[#EBECF0] px-6 py-4">
          <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4" onClick={onClose}>
            取消
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#E34935] px-4 text-white hover:bg-[#C9372C]" disabled={!confirmed} loading={deleting} onClick={submit}>
            <Trash2 className="h-4 w-4" />
            删除问题
          </Button>
        </footer>
      </div>
    </div>
  );
}

export function LinkIssueDialog({
  issue,
  loadCandidates,
  onClose,
  onConfirm,
}: {
  issue: Issue;
  loadCandidates: (params: { keyword: string; relationType: IssueRelationType; page: number }) => Promise<SearchLinkableIssuesResponse>;
  onClose: () => void;
  onConfirm: (payload: LinkIssuesPayload) => Promise<void>;
}) {
  const [keyword, setKeyword] = useState("");
  const [relationType, setRelationType] = useState<IssueRelationType>("blocked_by");
  const [page, setPage] = useState(1);
  const [data, setData] = useState<SearchLinkableIssuesResponse>();
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    void loadCandidates({ keyword, relationType, page }).then(setData);
  }, [keyword, loadCandidates, page, relationType]);

  async function submit() {
    setSubmitting(true);
    try {
      await onConfirm({ issueId: issue.id, relationType, targetIssueIds: selectedIds });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="fixed inset-0 z-[70] flex items-center justify-center bg-[#091E42]/36 px-4">
      <div className="w-full max-w-[760px] overflow-hidden rounded-[8px] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <header className="flex h-16 items-center justify-between border-b border-[#EBECF0] px-6">
          <h2 className="text-[18px] font-bold text-[#172B4D]">关联问题</h2>
          <button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#F4F5F7]" onClick={onClose}>
            <X className="h-5 w-5" />
          </button>
        </header>
        <div className="border-b border-[#EBECF0] px-6 py-4">
          <div className="flex gap-3">
            <div className="relative min-w-0 flex-1">
              <Search className="absolute left-3 top-2.5 h-4 w-4 text-[#6B778C]" />
              <Input className="h-9 rounded-[4px] border-[#DFE1E6] pl-9 text-[13px]" value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder="搜索问题标题或编号" />
            </div>
            <select className="h-9 rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]" value={relationType} onChange={(event) => setRelationType(event.target.value as IssueRelationType)}>
              {Object.entries(relationLabel).map(([id, label]) => (
                <option key={id} value={id}>
                  {label}
                </option>
              ))}
            </select>
            <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4">
              筛选
            </Button>
          </div>
        </div>
        <div className="grid min-h-[360px] grid-cols-[160px_minmax(0,1fr)]">
          <aside className="border-r border-[#EBECF0] bg-[#FAFBFC] p-3">
            {data?.relationTypes.map((item) => (
              <button
                key={item.id}
                className={cn("mb-1 flex h-9 w-full items-center justify-between rounded-[4px] px-3 text-left text-[13px]", relationType === item.id ? "bg-[#E9F2FF] font-semibold text-[#0C66E4]" : "text-[#44546F] hover:bg-[#F4F5F7]")}
                onClick={() => setRelationType(item.id)}
              >
                <span>{item.label}</span>
                <span>({item.count})</span>
              </button>
            ))}
          </aside>
          <div className="min-w-0 p-4">
            <div className="mb-3 text-[13px] font-semibold text-[#172B4D]">已选择 {selectedIds.length} 个</div>
            <div className="overflow-hidden rounded-[6px] border border-[#DFE1E6]">
              <table className="w-full table-fixed text-left text-[13px]">
                <thead className="bg-[#FAFBFC] text-[12px] text-[#626F86]">
                  <tr>
                    <th className="w-10 px-3 py-3" />
                    <th className="w-[92px] px-3 py-3">编号</th>
                    <th className="px-3 py-3">标题</th>
                    <th className="w-[92px] px-3 py-3">状态</th>
                    <th className="w-[86px] px-3 py-3">优先级</th>
                    <th className="w-[96px] px-3 py-3">负责人</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.items.map((item) => (
                    <tr key={item.id} className="border-t border-[#EBECF0] hover:bg-[#F7F8F9]">
                      <td className="px-3 py-3">
                        <input
                          type="checkbox"
                          checked={selectedIds.includes(item.id)}
                          onChange={(event) => setSelectedIds(event.target.checked ? [...selectedIds, item.id] : selectedIds.filter((id) => id !== item.id))}
                        />
                      </td>
                      <td className="px-3 py-3 font-semibold text-[#0C66E4]">{item.key}</td>
                      <td className="truncate px-3 py-3 font-medium text-[#172B4D]">{item.title}</td>
                      <td className="px-3 py-3">{issueStatusLabel[item.status]}</td>
                      <td className="px-3 py-3">{issuePriorityLabel[item.priority]}</td>
                      <td className="px-3 py-3">
                        {item.assignee ? (
                          <div className="flex items-center gap-1.5">
                            <Avatar name={item.assignee.name} imageSrc={item.assignee.avatarUrl} className="h-6 w-6" />
                            <span className="truncate">{item.assignee.name}</span>
                          </div>
                        ) : (
                          "未分配"
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <div className="flex items-center justify-between border-t border-[#EBECF0] px-4 py-3">
                <span className="text-[13px] text-[#626F86]">共 {data?.total ?? 0} 条</span>
                <Pagination page={page} pageSize={5} total={data?.total ?? 0} onPageChange={setPage} />
              </div>
            </div>
          </div>
        </div>
        <footer className="flex justify-end gap-3 border-t border-[#EBECF0] px-6 py-4">
          <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4" onClick={onClose}>
            取消
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] px-4 text-white" disabled={!selectedIds.length} loading={submitting} onClick={submit}>
            <Link2 className="h-4 w-4" />
            确定 ({selectedIds.length})
          </Button>
        </footer>
      </div>
    </div>
  );
}

export function IssueEditDrawer({
  issue,
  users,
  onClose,
  onSubmit,
}: {
  issue: Issue;
  users: User[];
  onClose: () => void;
  onSubmit: (payload: UpdateIssuePayload) => Promise<void>;
}) {
  const [title, setTitle] = useState(issue.title);
  const [description, setDescription] = useState(issue.description);
  const [priority, setPriority] = useState<IssuePriority>(issue.priority);
  const [status, setStatus] = useState<IssueStatus>(issue.status);
  const [assigneeId, setAssigneeId] = useState(issue.assignee?.id ?? "");
  const [estimate, setEstimate] = useState(String(issue.estimate ?? 0));
  const [saving, setSaving] = useState(false);
  const dirty = title !== issue.title || description !== issue.description || priority !== issue.priority || status !== issue.status || assigneeId !== (issue.assignee?.id ?? "") || estimate !== String(issue.estimate ?? 0);
  const titleError = title.trim().length < 5;

  async function submit(saveAsDraft = false) {
    if (titleError) {
      return;
    }
    setSaving(true);
    try {
      await onSubmit({
        issueId: issue.id,
        saveAsDraft,
        changes: {
          title,
          description,
          priority,
          status,
          assigneeId,
          estimate: Number(estimate),
        },
      });
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-[60] flex justify-end bg-[#091E42]/20">
      <aside className="workspace-scrollbar h-full w-full max-w-[760px] overflow-y-auto border-l border-[#DFE1E6] bg-[#F7F8F9] shadow-[0_12px_32px_rgba(9,30,66,0.24)]">
        <header className="sticky top-0 z-10 flex items-center justify-between border-b border-[#DFE1E6] bg-white px-6 py-4">
          <div>
            <div className="text-[12px] font-semibold text-[#0C66E4]">{issue.key}</div>
            <h2 className="mt-1 text-[18px] font-bold text-[#172B4D]">编辑问题</h2>
          </div>
          <button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#F4F5F7]" onClick={onClose}>
            <X className="h-5 w-5" />
          </button>
        </header>
        <div className="space-y-4 px-6 py-5">
          {dirty ? <div className="rounded-[4px] border border-[#FFE2BD] bg-[#FFF7ED] px-3 py-2 text-[13px] text-[#974F0C]">有变更未保存，离开前请保存或取消。</div> : null}
          <EditField label="标题" required error={titleError ? "标题不能少于 5 个字符" : ""}>
            <Input className={cn("h-9 rounded-[4px] border-[#DFE1E6]", titleError && "border-[#E34935]")} value={title} onChange={(event) => setTitle(event.target.value)} />
          </EditField>
          <EditField label="描述">
            <textarea className="min-h-[160px] w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2 text-[13px] leading-6 outline-none focus:border-[#0C66E4] focus:ring-1 focus:ring-[#0C66E4]" value={description} onChange={(event) => setDescription(event.target.value)} />
          </EditField>
          <div className="grid gap-4 md:grid-cols-2">
            <EditField label="优先级" required>
              <select className="h-9 w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]" value={priority} onChange={(event) => setPriority(event.target.value as IssuePriority)}>
                {(Object.keys(issuePriorityLabel) as IssuePriority[]).map((item) => (
                  <option key={item} value={item}>
                    {issuePriorityLabel[item]}
                  </option>
                ))}
              </select>
            </EditField>
            <EditField label="状态" required>
              <select className="h-9 w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]" value={status} onChange={(event) => setStatus(event.target.value as IssueStatus)}>
                {(Object.keys(issueStatusLabel) as IssueStatus[]).map((item) => (
                  <option key={item} value={item}>
                    {issueStatusLabel[item]}
                  </option>
                ))}
              </select>
            </EditField>
            <EditField label="负责人" required>
              <select className="h-9 w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]" value={assigneeId} onChange={(event) => setAssigneeId(event.target.value)}>
                {users.map((user) => (
                  <option key={user.id} value={user.id}>
                    {user.name}
                  </option>
                ))}
              </select>
            </EditField>
            <EditField label="预估工时">
              <Input className="h-9 rounded-[4px] border-[#DFE1E6]" value={estimate} onChange={(event) => setEstimate(event.target.value)} />
            </EditField>
          </div>
        </div>
        <footer className="sticky bottom-0 flex justify-end gap-3 border-t border-[#EBECF0] bg-white px-6 py-4">
          <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4" onClick={onClose}>
            取消
          </Button>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white px-4" onClick={() => void submit(true)}>
            保存草稿
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] px-4 text-white" disabled={!dirty || titleError} loading={saving} onClick={() => void submit(false)}>
            <Check className="h-4 w-4" />
            更新问题
          </Button>
        </footer>
      </aside>
    </div>
  );
}

function EditField({ label, required, error, children }: { label: string; required?: boolean; error?: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <div className="mb-1.5 text-[13px] font-semibold text-[#172B4D]">
        {label}
        {required ? <span className="ml-0.5 text-[#E34935]">*</span> : null}
      </div>
      {children}
      {error ? <div className="mt-1 text-[12px] text-[#E34935]">{error}</div> : null}
    </label>
  );
}
