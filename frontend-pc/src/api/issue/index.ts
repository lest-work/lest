import { mockIssueService } from "./mock";
export type { IssueService } from "./service";
export { mockIssueService } from "./mock";
export { issueQueryKeys } from "./keys";

/** Default service implementation (currently backed by mock) */
export const issueService = mockIssueService;

export type {
  AttachmentUploadPolicy,
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
  GetIssueAttachmentsParams,
  GetIssueAttachmentsResponse,
  GetIssueCommentsParams,
  GetIssueCommentsResponse,
  GetBacklogIssuesParams,
  GetBacklogIssuesResponse,
  GetIssueDetailParams,
  GetIssueDetailResponse,
  IssueActivity,
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
