import issuesJson from "./mock-data/issues.json";
import activitiesJson from "./mock-data/activities.json";
import type { IssueService } from "./service";
import type {
  CancelIssueAttachmentPayload,
  CommentQuickAction,
  CreateIssuePayload,
  CreateIssueCommentPayload,
  CreateIssueCommentResponse,
  CreateIssueResponse,
  DeleteIssueAttachmentPayload,
  DeleteIssueCommentPayload,
  DeleteIssueCommentResponse,
  DeleteIssuePayload,
  DeleteIssueResponse,
  EmojiCategory,
  GetBacklogIssuesParams,
  GetBacklogIssuesResponse,
  GetIssueAttachmentsParams,
  GetIssueAttachmentsResponse,
  GetIssueCommentsParams,
  GetIssueCommentsResponse,
  GetIssueDetailParams,
  GetIssueDetailResponse,
  IssueAttachment,
  IssueComment,
  IssueDetailExtra,
  IssueRelation,
  IssueRelationType,
  LinkIssuesPayload,
  LinkIssuesResponse,
  MutateIssueAttachmentResponse,
  RetryIssueAttachmentPayload,
  SearchLinkableIssuesParams,
  SearchLinkableIssuesResponse,
  UpdateIssueCommentPayload,
  UpdateIssueCommentResponse,
  UpdateIssuePayload,
  UpdateIssueResponse,
  UpdateIssueRankPayload,
  UpdateIssueRankResponse,
  UploadIssueAttachmentPayload,
  UploadIssueAttachmentResponse,
} from "./contracts";
import { filterBacklogIssues, reorderBacklogIssues } from "./mock-data/helpers";
import type { MockActivitiesJson, MockIssue, MockIssuesJson } from "./mock-data/types";
import type { Issue, User } from "@/entities/issue/model/types";

const issueDataset = structuredClone(issuesJson as MockIssuesJson);
const activitiesDataset = activitiesJson as MockActivitiesJson;
const relationTypeLabels: Record<IssueRelationType, string> = {
  blocks: "阻塞",
  blocked_by: "被阻塞",
  duplicates: "重复",
  relates_to: "相关",
  subtask: "子任务",
  parent: "父任务",
};

const issueRelations = new Map<string, IssueRelation[]>([
  [
    "issue-1",
    [
      {
        id: "rel-1",
        type: "blocked_by",
        issue: {
          id: "issue-6",
          key: "LST-128",
          title: "修复页面刷新后丢失筛选状态",
          status: "todo",
          priority: "highest",
          type: "bug",
          assignee: issueDataset.items.find((issue) => issue.id === "issue-6")?.assignee,
        },
      },
    ],
  ],
]);

const emojiCategories: EmojiCategory[] = [
  { id: "popular", label: "常用", emojis: ["👍", "🎉", "🚀", "✅", "🙏", "🔥", "💡", "👀"] },
  { id: "smile", label: "Smile", emojis: ["😀", "😄", "😊", "😉", "😍", "🤔", "😅", "😎"] },
  { id: "work", label: "协作", emojis: ["📌", "📎", "🧩", "🛠", "📣", "🧪", "📦", "📝"] },
];

const commentQuickActions: CommentQuickAction[] = [
  { id: "format-bold", label: "加粗", description: "突出关键结论", shortcut: "Ctrl+B", template: "**重点：** " },
  { id: "insert-todo", label: "待办", description: "插入待办项", shortcut: "[]", template: "- [ ] 待补充处理人\n" },
  { id: "insert-code", label: "代码块", description: "插入代码片段", shortcut: "```", template: "```\n// 粘贴日志或代码\n```\n" },
  { id: "template-risk", label: "风险模板", description: "记录风险和下一步", template: "风险：\n影响：\n下一步：\n" },
];

const attachmentPolicy = {
  maxFileSizeMb: 100,
  maxFilesPerRequest: 20,
  totalLimitMb: 1024,
  acceptedExtensions: [".png", ".jpg", ".jpeg", ".pdf", ".doc", ".docx", ".zip", ".mp4", ".sketch", ".xlsx"],
};

function findIssue(issueId: string) {
  return issueDataset.items.find((issue) => issue.id === issueId);
}

function formatFileSize(bytes: number) {
  if (bytes < 1024 * 1024) {
    return `${Math.max(1, Math.round(bytes / 1024))} KB`;
  }
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
}

const commentsByIssueId = new Map<string, IssueComment[]>();
const attachmentsByIssueId = new Map<string, IssueAttachment[]>();

function issueAttachments(issue: Issue): IssueAttachment[] {
  if (!attachmentsByIssueId.has(issue.id)) {
    attachmentsByIssueId.set(issue.id, [
      { id: "att-1", name: "error-log.png", size: "120 KB", uploadedAt: "2026-06-06 14:35", uploadedBy: issue.reporter.name, status: "uploaded", mimeType: "image/png" },
      { id: "att-2", name: "console-error.log", size: "12 KB", uploadedAt: "2026-06-06 14:36", uploadedBy: issue.reporter.name, status: "uploaded", mimeType: "text/plain" },
      { id: "att-3", name: "复现步骤.docx", size: "48 KB", uploadedAt: "2026-06-06 14:37", uploadedBy: issue.reporter.name, status: "uploading", progress: 68, mimeType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
      { id: "att-4", name: "视频演示.mp4", size: "35.6 MB", uploadedAt: "2026-06-06 14:38", uploadedBy: issue.reporter.name, status: "failed", progress: 100, mimeType: "video/mp4", failureReason: "网络错误" },
    ]);
  }
  return attachmentsByIssueId.get(issue.id) ?? [];
}

function issueComments(issue: Issue): IssueComment[] {
  if (!commentsByIssueId.has(issue.id)) {
    const assignee = issue.assignee ?? issue.reporter;
    commentsByIssueId.set(issue.id, [
      {
        id: `comment-${issue.id}-1`,
        issueId: issue.id,
        author: assignee,
        body: "这里是一个示例评论内容，支持 Markdown 格式，也可以添加 @ 成员、表情与快捷操作。",
        createdAt: "2 小时前",
        updatedAt: "1 小时前",
        edited: true,
        canEdit: true,
        canDelete: true,
        repliesCount: 1,
        reactions: [{ emoji: "👍", count: 2, reactedByMe: true }],
      },
      {
        id: `comment-${issue.id}-2`,
        issueId: issue.id,
        author: issue.reporter,
        body: "补充说明：这里需要注意边界条件的处理。",
        createdAt: "1 小时前",
        edited: false,
        canEdit: false,
        canDelete: true,
        repliesCount: 0,
        reactions: [{ emoji: "👀", count: 1 }],
      },
    ]);
  }
  return commentsByIssueId.get(issue.id) ?? [];
}

function wait(ms = 180) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function uniqueUsers() {
  const users = new Map<string, User>();
  issueDataset.items.forEach((issue) => {
    if (issue.assignee) {
      users.set(issue.assignee.id, issue.assignee);
    }
    users.set(issue.reporter.id, issue.reporter);
  });
  return users;
}

function buildIssueExtra(issue: Issue): IssueDetailExtra {
  return {
    watchers: issue.key === "LST-101" ? 5 : 3,
    views: issue.key === "LST-101" ? 32 : 18,
    environment: "测试环境 (Test)",
    affectedVersion: "v2.1.0",
    fixedVersion: "v2.1.1",
    sprintName: issue.sprintId ? "Sprint 12 (5.13 - 5.26)" : "未规划",
    remainingHours: Math.max((issue.estimate ?? 0) - 2.5, 0),
    actualHours: 2.5,
    severity: issue.type === "bug" ? "critical" : "major",
    attachments: issueAttachments(issue),
    relations: issueRelations.get(issue.id) ?? [],
  };
}

function toStoredIssue(issue: Issue): MockIssue {
  return {
    ...issue,
    updatedTimestamp: new Date().toISOString(),
  };
}

function createKey() {
  const max = issueDataset.items.reduce((value, issue) => {
    const numeric = Number(issue.key.split("-")[1] ?? 0);
    return Number.isFinite(numeric) ? Math.max(value, numeric) : value;
  }, 100);
  return `LST-${max + 1}`;
}

function applyBacklogFilters(items: MockIssuesJson["items"], params?: GetBacklogIssuesParams) {
  const keyword = params?.keyword?.trim().toLowerCase();
  const onlyBacklog = params?.onlyBacklog ?? true;
  const updatedAfter = params?.updatedAfter ? new Date(params.updatedAfter).getTime() : undefined;
  const createdOn = params?.createdOn;
  const baseList = onlyBacklog ? filterBacklogIssues(items) : [...items].sort((first, second) => first.rank - second.rank);

  return baseList.filter((issue) => {
    if (params?.projectKey && issue.projectKey !== params.projectKey) {
      return false;
    }

    if (keyword) {
      const haystack = `${issue.key} ${issue.title} ${issue.description} ${issue.labels.join(" ")}`.toLowerCase();
      if (!haystack.includes(keyword)) {
        return false;
      }
    }

    if (params?.assigneeId && issue.assignee?.id !== params.assigneeId) {
      return false;
    }

    if (params?.statuses?.length && !params.statuses.includes(issue.status)) {
      return false;
    }

    if (params?.priorities?.length && !params.priorities.includes(issue.priority)) {
      return false;
    }

    if (params?.types?.length && !params.types.includes(issue.type)) {
      return false;
    }

    if (params?.labels?.length && !params.labels.every((label) => issue.labels.includes(label))) {
      return false;
    }

    if (updatedAfter && new Date((issue as MockIssue).updatedTimestamp).getTime() < updatedAfter) {
      return false;
    }

    if (createdOn && !(issue.createdAt ?? (issue as MockIssue).updatedTimestamp ?? "").startsWith(createdOn)) {
      return false;
    }

    if (typeof params?.blocked === "boolean" && Boolean(issue.blocked) !== params.blocked) {
      return false;
    }

    if (params?.onlyBacklog === true && issue.sprintId) {
      return false;
    }

    return true;
  });
}

export const mockIssueService: IssueService = {
  async getBacklogIssues(params): Promise<GetBacklogIssuesResponse> {
    await wait();

    const filtered = applyBacklogFilters(issueDataset.items, params);
    const page = params?.page ?? 1;
    const pageSize = params?.pageSize ?? filtered.length;
    const startIndex = (page - 1) * pageSize;

    return {
      items: filtered.slice(startIndex, startIndex + pageSize),
      total: filtered.length,
    };
  },

  async getIssueDetail({ issueId }: GetIssueDetailParams): Promise<GetIssueDetailResponse> {
    await wait(120);
    const issue = findIssue(issueId);

    if (!issue) {
      throw new Error(`Issue ${issueId} not found`);
    }

    return {
      issue,
      activities: activitiesDataset.items,
      extra: buildIssueExtra(issue),
    };
  },

  async getIssueComments(params: GetIssueCommentsParams): Promise<GetIssueCommentsResponse> {
    await wait(120);
    const issue = findIssue(params.issueId);

    if (!issue) {
      throw new Error(`Issue ${params.issueId} not found`);
    }

    const items = issueComments(issue);
    const page = params.page ?? 1;
    const pageSize = params.pageSize ?? items.length;
    const startIndex = (page - 1) * pageSize;

    return {
      items: items.slice(startIndex, startIndex + pageSize),
      total: items.length,
      emojiCategories,
      quickActions: commentQuickActions,
      permissions: {
        canComment: true,
        canAttach: true,
        canUseQuickActions: true,
      },
    };
  },

  async createIssueComment(payload: CreateIssueCommentPayload): Promise<CreateIssueCommentResponse> {
    await wait(260);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    const author = issue.assignee ?? issue.reporter;
    const comment: IssueComment = {
      id: `comment-${Date.now()}`,
      issueId: payload.issueId,
      author,
      body: `${payload.body}${payload.emoji ? ` ${payload.emoji}` : ""}`,
      createdAt: "刚刚",
      updatedAt: "刚刚",
      edited: false,
      canEdit: true,
      canDelete: true,
      repliesCount: 0,
      reactions: [],
    };

    commentsByIssueId.set(payload.issueId, [comment, ...issueComments(issue)]);

    return {
      comment,
      createdAt: "2026-06-06 17:28:10",
      message: "评论已发布",
    };
  },

  async updateIssueComment(payload: UpdateIssueCommentPayload): Promise<UpdateIssueCommentResponse> {
    await wait(300);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    let updatedComment: IssueComment | undefined;
    const nextItems = issueComments(issue).map((comment) => {
      if (comment.id !== payload.commentId) {
        return comment;
      }
      updatedComment = {
        ...comment,
        body: payload.body,
        updatedAt: "刚刚",
        edited: true,
      };
      return updatedComment;
    });

    if (!updatedComment) {
      throw new Error(`Comment ${payload.commentId} not found`);
    }

    commentsByIssueId.set(payload.issueId, nextItems);

    return {
      comment: updatedComment,
      updatedAt: "2026-06-06 17:29:00",
      message: "编辑成功，评论已更新",
    };
  },

  async deleteIssueComment(payload: DeleteIssueCommentPayload): Promise<DeleteIssueCommentResponse> {
    await wait(260);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    if (!payload.confirmed) {
      throw new Error("Comment delete confirmation required");
    }

    commentsByIssueId.set(
      payload.issueId,
      issueComments(issue).filter((comment) => comment.id !== payload.commentId),
    );

    return {
      commentId: payload.commentId,
      deletedAt: "2026-06-06 17:29:22",
      message: payload.deleteReplies ? "评论及全部回复已删除" : "评论已删除",
    };
  },

  async getIssueAttachments(params: GetIssueAttachmentsParams): Promise<GetIssueAttachmentsResponse> {
    await wait(100);
    const issue = findIssue(params.issueId);

    if (!issue) {
      throw new Error(`Issue ${params.issueId} not found`);
    }

    return {
      items: issueAttachments(issue),
      policy: attachmentPolicy,
      actions: ["preview", "download", "retry", "cancel", "delete"],
    };
  },

  async uploadIssueAttachments(payload: UploadIssueAttachmentPayload): Promise<UploadIssueAttachmentResponse> {
    await wait(360);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    const nextAttachments = payload.files.map<IssueAttachment>((file, index) => ({
      id: `att-${Date.now()}-${index}`,
      name: file.name,
      size: formatFileSize(file.size),
      uploadedAt: "刚刚",
      uploadedBy: issue.reporter.name,
      status: index === 0 ? "uploading" : "uploaded",
      progress: index === 0 ? 60 : 100,
      mimeType: file.mimeType,
    }));

    attachmentsByIssueId.set(payload.issueId, [...nextAttachments, ...issueAttachments(issue)]);

    return {
      attachments: nextAttachments,
      message: `正在上传 ${nextAttachments.length} 个附件`,
    };
  },

  async retryIssueAttachment(payload: RetryIssueAttachmentPayload): Promise<MutateIssueAttachmentResponse> {
    await wait(240);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    let updatedAttachment: IssueAttachment | undefined;
    const nextItems = issueAttachments(issue).map((attachment) => {
      if (attachment.id !== payload.attachmentId) {
        return attachment;
      }
      updatedAttachment = { ...attachment, status: "uploading", progress: 50, failureReason: undefined };
      return updatedAttachment;
    });

    if (!updatedAttachment) {
      throw new Error(`Attachment ${payload.attachmentId} not found`);
    }

    attachmentsByIssueId.set(payload.issueId, nextItems);

    return {
      attachment: updatedAttachment,
      message: "已重新开始上传",
    };
  },

  async cancelIssueAttachment(payload: CancelIssueAttachmentPayload): Promise<MutateIssueAttachmentResponse> {
    await wait(180);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    let updatedAttachment: IssueAttachment | undefined;
    const nextItems = issueAttachments(issue).map((attachment) => {
      if (attachment.id !== payload.attachmentId) {
        return attachment;
      }
      updatedAttachment = { ...attachment, status: "canceled", progress: undefined };
      return updatedAttachment;
    });

    if (!updatedAttachment) {
      throw new Error(`Attachment ${payload.attachmentId} not found`);
    }

    attachmentsByIssueId.set(payload.issueId, nextItems);

    return {
      attachment: updatedAttachment,
      message: "已取消上传",
    };
  },

  async deleteIssueAttachment(payload: DeleteIssueAttachmentPayload): Promise<MutateIssueAttachmentResponse> {
    await wait(220);
    const issue = findIssue(payload.issueId);

    if (!issue) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    const target = issueAttachments(issue).find((attachment) => attachment.id === payload.attachmentId);

    if (!target) {
      throw new Error(`Attachment ${payload.attachmentId} not found`);
    }

    attachmentsByIssueId.set(
      payload.issueId,
      issueAttachments(issue).filter((attachment) => attachment.id !== payload.attachmentId),
    );

    return {
      attachment: target,
      message: "附件已删除",
    };
  },

  async createIssue(payload: CreateIssuePayload): Promise<CreateIssueResponse> {
    await wait(520);
    const users = uniqueUsers();
    const reporter = users.get(payload.reporterId) ?? issueDataset.items[0].reporter;
    const assignee = payload.assigneeId ? users.get(payload.assigneeId) : undefined;
    const issue: MockIssue = {
      id: `issue-${Date.now()}`,
      key: createKey(),
      title: payload.title,
      projectKey: payload.projectKey,
      type: payload.type,
      priority: payload.priority,
      status: payload.status,
      assignee,
      reporter,
      labels: payload.labels,
      estimate: payload.estimate ?? 0,
      dueDate: payload.dueDate,
      createdAt: "2026-06-06",
      updatedAt: "刚刚",
      updatedTimestamp: new Date().toISOString(),
      description: payload.description,
      sprintId: payload.sprintId,
      rank: issueDataset.items.length + 1,
    };

    issueDataset.items = [issue, ...issueDataset.items];
    issueDataset.total = issueDataset.items.length;

    return {
      issue,
      createdAt: "2026-06-06 17:20:01",
      message: `问题 ${issue.key} 已创建`,
    };
  },

  async updateIssue({ issueId, changes, saveAsDraft }: UpdateIssuePayload): Promise<UpdateIssueResponse> {
    await wait(360);
    const users = uniqueUsers();
    const target = issueDataset.items.find((issue) => issue.id === issueId);

    if (!target) {
      throw new Error(`Issue ${issueId} not found`);
    }

    const updated: MockIssue = toStoredIssue({
      ...target,
      ...changes,
      assignee: changes.assigneeId ? users.get(changes.assigneeId) : target.assignee,
      updatedAt: "刚刚",
    });

    issueDataset.items = issueDataset.items.map((issue) => (issue.id === issueId ? updated : issue));

    return {
      issue: updated,
      updatedAt: "2026-06-06 17:22:18",
      message: saveAsDraft ? `已保存 ${updated.key} 草稿` : `${updated.key} 已更新`,
    };
  },

  async deleteIssue(payload: DeleteIssuePayload): Promise<DeleteIssueResponse> {
    await wait(420);
    const target = issueDataset.items.find((issue) => issue.id === payload.issueId);

    if (!target) {
      throw new Error(`Issue ${payload.issueId} not found`);
    }

    if (!payload.confirmed) {
      throw new Error("Delete confirmation required");
    }

    issueDataset.items = issueDataset.items.filter((issue) => issue.id !== payload.issueId);
    issueDataset.total = issueDataset.items.length;
    issueRelations.delete(payload.issueId);

    return {
      issueId: payload.issueId,
      deletedKey: target.key,
      message: `${target.key} 已删除`,
    };
  },

  async searchLinkableIssues(params: SearchLinkableIssuesParams): Promise<SearchLinkableIssuesResponse> {
    await wait(180);
    const keyword = params.keyword?.trim().toLowerCase();
    const candidates = issueDataset.items.filter((issue) => {
      if (issue.id === params.issueId) {
        return false;
      }
      if (!keyword) {
        return true;
      }
      return `${issue.key} ${issue.title} ${issue.labels.join(" ")}`.toLowerCase().includes(keyword);
    });
    const page = params.page ?? 1;
    const pageSize = params.pageSize ?? 5;
    const startIndex = (page - 1) * pageSize;

    return {
      items: candidates.slice(startIndex, startIndex + pageSize),
      total: candidates.length,
      relationTypes: (Object.entries(relationTypeLabels) as [IssueRelationType, string][]).map(([id, label], index) => ({
        id,
        label,
        count: id === params.relationType ? Math.max(1, index) : index % 3,
      })),
    };
  },

  async linkIssues(payload: LinkIssuesPayload): Promise<LinkIssuesResponse> {
    await wait(360);
    const relations = payload.targetIssueIds
      .map((targetIssueId) => issueDataset.items.find((issue) => issue.id === targetIssueId))
      .filter((issue): issue is MockIssue => Boolean(issue))
      .map<IssueRelation>((issue) => ({
        id: `rel-${payload.issueId}-${issue.id}`,
        type: payload.relationType,
        issue: {
          id: issue.id,
          key: issue.key,
          title: issue.title,
          status: issue.status,
          priority: issue.priority,
          type: issue.type,
          assignee: issue.assignee,
        },
      }));

    issueRelations.set(payload.issueId, [...(issueRelations.get(payload.issueId) ?? []), ...relations]);

    return {
      relations,
      message: `已关联 ${relations.length} 个问题`,
    };
  },

  async updateIssueRank({ issueId, overIssueId }: UpdateIssueRankPayload): Promise<UpdateIssueRankResponse> {
    await wait(220);
    const reordered = reorderBacklogIssues(issueDataset.items, issueId, overIssueId);

    if (!reordered || !reordered.movedIssue) {
      throw new Error("Unable to reorder backlog issues");
    }

    issueDataset.items = reordered.items;

    return {
      items: filterBacklogIssues(issueDataset.items),
      movedIssueId: reordered.movedIssue.id,
      movedIssueKey: reordered.movedIssue.key,
      targetPosition: reordered.targetPosition,
      notice: `已将 ${reordered.movedIssue.key} 移动到第 ${reordered.targetPosition} 位`,
    };
  },
};
