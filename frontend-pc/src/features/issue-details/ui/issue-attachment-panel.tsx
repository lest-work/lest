import {
  AlertCircle,
  CheckCircle2,
  Download,
  Eye,
  FileArchive,
  FileText,
  ImageIcon,
  Loader2,
  Pause,
  RotateCcw,
  Trash2,
  UploadCloud,
  Video,
  X,
} from "lucide-react";
import type { DragEvent, ReactNode } from "react";
import { useRef, useState } from "react";

import type {
  AttachmentUploadPolicy,
  CancelIssueAttachmentPayload,
  DeleteIssueAttachmentPayload,
  IssueAttachment,
  RetryIssueAttachmentPayload,
  UploadIssueAttachmentPayload,
} from "@/api/issue";
import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui";

export interface IssueAttachmentPanelProps {
  issueId: string;
  attachments: IssueAttachment[];
  policy: AttachmentUploadPolicy;
  onUpload: (payload: UploadIssueAttachmentPayload) => Promise<void>;
  onRetry: (payload: RetryIssueAttachmentPayload) => Promise<void>;
  onCancel: (payload: CancelIssueAttachmentPayload) => Promise<void>;
  onDelete: (payload: DeleteIssueAttachmentPayload) => Promise<void>;
}

export function IssueAttachmentPanel({ issueId, attachments, policy, onUpload, onRetry, onCancel, onDelete }: IssueAttachmentPanelProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const [dragging, setDragging] = useState(false);
  const [busyId, setBusyId] = useState<string>();
  const uploaded = attachments.filter((attachment) => attachment.status === "uploaded");
  const activeUploads = attachments.filter((attachment) => attachment.status !== "uploaded");

  async function uploadFiles(files: File[]) {
    if (!files.length) {
      return;
    }
    await onUpload({
      issueId,
      source: dragging ? "dropzone" : "detail",
      files: files.slice(0, policy.maxFilesPerRequest).map((file) => ({
        name: file.name,
        size: file.size,
        mimeType: file.type || "application/octet-stream",
      })),
    });
  }

  async function uploadDemoFile(source: UploadIssueAttachmentPayload["source"]) {
    await onUpload({
      issueId,
      source,
      files: [{ name: "设计稿_final.png", size: 1.8 * 1024 * 1024, mimeType: "image/png" }],
    });
  }

  function handleDrop(event: DragEvent<HTMLDivElement>) {
    event.preventDefault();
    setDragging(false);
    void uploadFiles(Array.from(event.dataTransfer.files));
  }

  async function mutateAttachment(id: string, action: "retry" | "cancel" | "delete") {
    setBusyId(id);
    try {
      if (action === "retry") {
        await onRetry({ issueId, attachmentId: id });
      } else if (action === "cancel") {
        await onCancel({ issueId, attachmentId: id });
      } else {
        await onDelete({ issueId, attachmentId: id });
      }
    } finally {
      setBusyId(undefined);
    }
  }

  return (
    <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
      <div className="mb-3 flex items-center justify-between gap-3">
        <h3 className="text-[15px] font-bold text-[#172B4D]">附件（{attachments.length}）</h3>
        <div className="flex gap-2">
          <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]" onClick={() => inputRef.current?.click()}>
            添加附件
          </Button>
          <Button variant="outline" className="h-8 rounded-[4px] bg-white px-3 text-[12px]" onClick={() => void uploadDemoFile("detail")}>
            模拟上传
          </Button>
        </div>
      </div>

      <input
        ref={inputRef}
        className="hidden"
        type="file"
        multiple
        onChange={(event) => {
          void uploadFiles(Array.from(event.target.files ?? []));
          event.currentTarget.value = "";
        }}
      />

      <div
        className={cn("mb-4 flex min-h-[112px] flex-col items-center justify-center rounded-[8px] border border-dashed px-4 py-5 text-center transition", dragging ? "border-[#0C66E4] bg-[#F0F6FF]" : "border-[#A6C5E8] bg-[#FAFBFC]")}
        onDragEnter={(event) => {
          event.preventDefault();
          setDragging(true);
        }}
        onDragOver={(event) => event.preventDefault()}
        onDragLeave={() => setDragging(false)}
        onDrop={handleDrop}
      >
        <UploadCloud className={cn("h-8 w-8", dragging ? "text-[#0C66E4]" : "text-[#6B778C]")} />
        <div className="mt-2 text-[13px] font-semibold text-[#172B4D]">{dragging ? "释放文件开始上传" : "拖拽文件到此处，或点击上传"}</div>
        <div className="mt-1 text-[12px] text-[#626F86]">
          单个文件不超过 {policy.maxFileSizeMb}MB，单次最多 {policy.maxFilesPerRequest} 个，总大小不超过 {policy.totalLimitMb}MB
        </div>
      </div>

      {activeUploads.length ? (
        <div className="mb-4 rounded-[6px] border border-[#DFE1E6]">
          <div className="border-b border-[#EBECF0] bg-[#FAFBFC] px-3 py-2 text-[12px] font-semibold text-[#172B4D]">上传状态</div>
          {activeUploads.map((attachment) => (
            <AttachmentRow key={attachment.id} attachment={attachment} busy={busyId === attachment.id} onRetry={() => void mutateAttachment(attachment.id, "retry")} onCancel={() => void mutateAttachment(attachment.id, "cancel")} onDelete={() => void mutateAttachment(attachment.id, "delete")} />
          ))}
        </div>
      ) : null}

      <div className="divide-y divide-[#EBECF0] overflow-hidden rounded-[6px] border border-[#DFE1E6]">
        {uploaded.map((attachment) => (
          <AttachmentRow key={attachment.id} attachment={attachment} busy={busyId === attachment.id} onRetry={() => void mutateAttachment(attachment.id, "retry")} onCancel={() => void mutateAttachment(attachment.id, "cancel")} onDelete={() => void mutateAttachment(attachment.id, "delete")} />
        ))}
        {!uploaded.length ? <div className="px-3 py-8 text-center text-[13px] text-[#626F86]">暂无已上传附件</div> : null}
      </div>

      <div className="mt-3 flex flex-wrap gap-1.5 text-[11px] text-[#626F86]">
        {policy.acceptedExtensions.slice(0, 9).map((extension) => (
          <span key={extension} className="rounded-[3px] border border-[#DFE1E6] bg-[#FAFBFC] px-1.5 py-0.5">
            {extension}
          </span>
        ))}
      </div>
    </section>
  );
}

function AttachmentRow({ attachment, busy, onRetry, onCancel, onDelete }: { attachment: IssueAttachment; busy: boolean; onRetry: () => void; onCancel: () => void; onDelete: () => void }) {
  const progress = attachment.progress ?? (attachment.status === "uploaded" ? 100 : 0);

  return (
    <div className="grid grid-cols-[minmax(0,1fr)_88px_132px] items-center gap-3 px-3 py-3 text-[13px]">
      <div className="flex min-w-0 items-center gap-3">
        <div className={cn("flex h-9 w-9 shrink-0 items-center justify-center rounded-[6px]", attachment.status === "failed" ? "bg-[#FFF4F2] text-[#E34935]" : attachment.status === "uploaded" ? "bg-[#E3FCEF] text-[#22A06B]" : "bg-[#E9F2FF] text-[#0C66E4]")}>
          <AttachmentIcon attachment={attachment} />
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex min-w-0 items-center gap-2">
            <span className="truncate font-semibold text-[#172B4D]">{attachment.name}</span>
            <StatusBadge attachment={attachment} />
          </div>
          <div className="mt-1 flex items-center gap-2 text-[12px] text-[#626F86]">
            <span>{attachment.size}</span>
            <span>{attachment.uploadedAt}</span>
            {attachment.failureReason ? <span className="text-[#AE2E24]">失败：{attachment.failureReason}</span> : null}
          </div>
          {attachment.status === "uploading" ? (
            <div className="mt-2 h-1.5 rounded-full bg-[#EBECF0]">
              <div className="h-full rounded-full bg-[#0C66E4]" style={{ width: `${progress}%` }} />
            </div>
          ) : null}
        </div>
      </div>
      <div className="text-right text-[12px] text-[#626F86]">{attachment.status === "uploading" ? `${progress}%` : attachment.uploadedBy}</div>
      <div className="flex justify-end gap-1 text-[#44546F]">
        {attachment.status === "failed" ? <IconButton title="重试" busy={busy} icon={<RotateCcw className="h-4 w-4" />} onClick={onRetry} /> : null}
        {attachment.status === "uploading" ? <IconButton title="暂停/取消" busy={busy} icon={<Pause className="h-4 w-4" />} onClick={onCancel} /> : null}
        {attachment.status === "uploaded" ? (
          <>
            <IconButton title="预览" icon={<Eye className="h-4 w-4" />} />
            <IconButton title="下载" icon={<Download className="h-4 w-4" />} />
          </>
        ) : null}
        <IconButton title="删除" busy={busy} icon={attachment.status === "canceled" ? <X className="h-4 w-4" /> : <Trash2 className="h-4 w-4" />} onClick={onDelete} />
      </div>
    </div>
  );
}

function AttachmentIcon({ attachment }: { attachment: IssueAttachment }) {
  if (attachment.mimeType?.startsWith("image/")) {
    return <ImageIcon className="h-4 w-4" />;
  }
  if (attachment.mimeType?.startsWith("video/")) {
    return <Video className="h-4 w-4" />;
  }
  if (attachment.name.endsWith(".zip")) {
    return <FileArchive className="h-4 w-4" />;
  }
  return <FileText className="h-4 w-4" />;
}

function StatusBadge({ attachment }: { attachment: IssueAttachment }) {
  if (attachment.status === "uploaded") {
    return (
      <span className="inline-flex items-center gap-1 rounded-[3px] bg-[#E3FCEF] px-1.5 py-0.5 text-[11px] font-semibold text-[#006644]">
        <CheckCircle2 className="h-3 w-3" />
        已上传
      </span>
    );
  }
  if (attachment.status === "failed") {
    return (
      <span className="inline-flex items-center gap-1 rounded-[3px] bg-[#FFF4F2] px-1.5 py-0.5 text-[11px] font-semibold text-[#AE2E24]">
        <AlertCircle className="h-3 w-3" />
        上传失败
      </span>
    );
  }
  if (attachment.status === "canceled") {
    return <span className="rounded-[3px] bg-[#F1F2F4] px-1.5 py-0.5 text-[11px] font-semibold text-[#44546F]">已取消</span>;
  }
  return (
    <span className="inline-flex items-center gap-1 rounded-[3px] bg-[#E9F2FF] px-1.5 py-0.5 text-[11px] font-semibold text-[#0C66E4]">
      <Loader2 className="h-3 w-3 animate-spin" />
      上传中
    </span>
  );
}

function IconButton({ icon, title, busy, onClick }: { icon: ReactNode; title: string; busy?: boolean; onClick?: () => void }) {
  return (
    <button className="inline-flex h-7 w-7 items-center justify-center rounded-[4px] hover:bg-[#F4F5F7] disabled:opacity-50" title={title} disabled={busy} onClick={onClick}>
      {busy ? <Loader2 className="h-4 w-4 animate-spin" /> : icon}
    </button>
  );
}
