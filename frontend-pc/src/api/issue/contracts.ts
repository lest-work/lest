import type { Issue, IssuePriority, IssueStatus, IssueType, User } from "@/entities/issue";

export interface GetBacklogIssuesParams {
  projectKey?: string;
  keyword?: string;
  assigneeId?: string;
  statuses?: IssueStatus[];
  priorities?: IssuePriority[];
  types?: IssueType[];
  labels?: string[];
  onlyBacklog?: boolean;
  updatedAfter?: string;
  createdOn?: string;
  blocked?: boolean;
  page?: number;
  pageSize?: number;
}

export interface GetBacklogIssuesResponse {
  items: Issue[];
  total: number;
}

export interface GetIssueDetailParams {
  issueId: string;
}

export interface IssueActivity {
  id: string;
  user: string;
  content: string;
  time: string;
}

export interface IssueCommentReaction {
  emoji: string;
  count: number;
  reactedByMe?: boolean;
}

export interface IssueComment {
  id: string;
  issueId: string;
  author: User;
  body: string;
  createdAt: string;
  updatedAt?: string;
  edited: boolean;
  canEdit: boolean;
  canDelete: boolean;
  repliesCount: number;
  reactions: IssueCommentReaction[];
}

export interface EmojiCategory {
  id: string;
  label: string;
  emojis: string[];
}

export interface CommentQuickAction {
  id: string;
  label: string;
  description: string;
  shortcut?: string;
  template: string;
  disabled?: boolean;
}

export interface IssueAttachment {
  id: string;
  name: string;
  size: string;
  uploadedAt: string;
  uploadedBy: string;
  status: "uploaded" | "uploading" | "failed" | "canceled";
  progress?: number;
  mimeType?: string;
  failureReason?: string;
}

export interface IssueRelation {
  id: string;
  type: IssueRelationType;
  issue: Pick<Issue, "id" | "key" | "title" | "status" | "priority" | "type"> & {
    assignee?: User;
  };
}

export type IssueRelationType = "blocks" | "blocked_by" | "duplicates" | "relates_to" | "subtask" | "parent";

export interface IssueDetailExtra {
  watchers: number;
  views: number;
  environment: string;
  affectedVersion: string;
  fixedVersion: string;
  sprintName: string;
  remainingHours: number;
  actualHours: number;
  severity: "minor" | "major" | "critical";
  attachments: IssueAttachment[];
  relations: IssueRelation[];
}

export interface GetIssueDetailResponse {
  issue: Issue;
  activities: IssueActivity[];
  extra: IssueDetailExtra;
}

export interface GetIssueCommentsParams {
  issueId: string;
  page?: number;
  pageSize?: number;
  includeDeleted?: boolean;
}

export interface GetIssueCommentsResponse {
  items: IssueComment[];
  total: number;
  emojiCategories: EmojiCategory[];
  quickActions: CommentQuickAction[];
  permissions: {
    canComment: boolean;
    canAttach: boolean;
    canUseQuickActions: boolean;
  };
}

export interface CreateIssueCommentPayload {
  issueId: string;
  body: string;
  emoji?: string;
  quickActionId?: string;
  attachmentIds?: string[];
  mentionedUserIds?: string[];
}

export interface CreateIssueCommentResponse {
  comment: IssueComment;
  createdAt: string;
  message: string;
}

export interface UpdateIssueCommentPayload {
  issueId: string;
  commentId: string;
  body: string;
  expectedUpdatedAt?: string;
}

export interface UpdateIssueCommentResponse {
  comment: IssueComment;
  updatedAt: string;
  message: string;
}

export interface DeleteIssueCommentPayload {
  issueId: string;
  commentId: string;
  confirmed: boolean;
  deleteReplies?: boolean;
}

export interface DeleteIssueCommentResponse {
  commentId: string;
  deletedAt: string;
  message: string;
}

export interface AttachmentUploadPolicy {
  maxFileSizeMb: number;
  maxFilesPerRequest: number;
  totalLimitMb: number;
  acceptedExtensions: string[];
}

export interface GetIssueAttachmentsParams {
  issueId: string;
}

export interface GetIssueAttachmentsResponse {
  items: IssueAttachment[];
  policy: AttachmentUploadPolicy;
  actions: Array<"preview" | "download" | "retry" | "cancel" | "delete">;
}

export interface UploadIssueAttachmentPayload {
  issueId: string;
  source: "comment" | "detail" | "dropzone";
  files: Array<{
    name: string;
    size: number;
    mimeType: string;
  }>;
}

export interface UploadIssueAttachmentResponse {
  attachments: IssueAttachment[];
  message: string;
}

export interface RetryIssueAttachmentPayload {
  issueId: string;
  attachmentId: string;
}

export interface CancelIssueAttachmentPayload {
  issueId: string;
  attachmentId: string;
}

export interface DeleteIssueAttachmentPayload {
  issueId: string;
  attachmentId: string;
}

export interface MutateIssueAttachmentResponse {
  attachment: IssueAttachment;
  message: string;
}

export interface CreateIssuePayload {
  projectKey: string;
  type: IssueType;
  title: string;
  description: string;
  priority: IssuePriority;
  status: IssueStatus;
  assigneeId?: string;
  reporterId: string;
  labels: string[];
  sprintId?: string;
  dueDate?: string;
  estimate?: number;
  affectedVersion?: string;
  fixedVersion?: string;
  relatedIssueKeys?: string[];
  watcherIds?: string[];
  createAnother?: boolean;
}

export interface CreateIssueResponse {
  issue: Issue;
  createdAt: string;
  message: string;
}

export interface UpdateIssuePayload {
  issueId: string;
  changes: Partial<Pick<Issue, "title" | "description" | "type" | "priority" | "status" | "labels" | "dueDate" | "estimate">> & {
    assigneeId?: string;
    sprintId?: string;
    affectedVersion?: string;
    fixedVersion?: string;
  };
  saveAsDraft?: boolean;
}

export interface UpdateIssueResponse {
  issue: Issue;
  updatedAt: string;
  message: string;
}

export interface DeleteIssuePayload {
  issueId: string;
  confirmed: boolean;
  includeSubtasks?: boolean;
  unlinkRelatedIssues?: boolean;
}

export interface DeleteIssueResponse {
  issueId: string;
  deletedKey: string;
  message: string;
}

export interface SearchLinkableIssuesParams {
  issueId: string;
  keyword?: string;
  relationType?: IssueRelationType;
  page?: number;
  pageSize?: number;
}

export interface SearchLinkableIssuesResponse {
  items: Issue[];
  total: number;
  relationTypes: Array<{ id: IssueRelationType; label: string; count: number }>;
}

export interface LinkIssuesPayload {
  issueId: string;
  relationType: IssueRelationType;
  targetIssueIds: string[];
}

export interface LinkIssuesResponse {
  relations: IssueRelation[];
  message: string;
}

export interface UpdateIssueRankPayload {
  issueId: string;
  overIssueId: string;
}

export interface UpdateIssueRankResponse {
  items: Issue[];
  movedIssueId: string;
  movedIssueKey: string;
  targetPosition: number;
  notice: string;
}
