import type {
  CreateIssuePayload,
  CreateIssueResponse,
  CancelIssueAttachmentPayload,
  DeleteIssuePayload,
  DeleteIssueAttachmentPayload,
  DeleteIssueCommentPayload,
  DeleteIssueCommentResponse,
  DeleteIssueResponse,
  GetBacklogIssuesParams,
  GetBacklogIssuesResponse,
  GetIssueAttachmentsParams,
  GetIssueAttachmentsResponse,
  GetIssueCommentsParams,
  GetIssueCommentsResponse,
  GetIssueDetailParams,
  GetIssueDetailResponse,
  LinkIssuesPayload,
  LinkIssuesResponse,
  MutateIssueAttachmentResponse,
  RetryIssueAttachmentPayload,
  SearchLinkableIssuesParams,
  SearchLinkableIssuesResponse,
  CreateIssueCommentPayload,
  CreateIssueCommentResponse,
  UpdateIssueCommentPayload,
  UpdateIssueCommentResponse,
  UpdateIssuePayload,
  UpdateIssueResponse,
  UpdateIssueRankPayload,
  UpdateIssueRankResponse,
  UploadIssueAttachmentPayload,
  UploadIssueAttachmentResponse,
} from "./contracts";

export interface IssueService {
  getBacklogIssues(params?: GetBacklogIssuesParams): Promise<GetBacklogIssuesResponse>;
  getIssueDetail(params: GetIssueDetailParams): Promise<GetIssueDetailResponse>;
  getIssueComments(params: GetIssueCommentsParams): Promise<GetIssueCommentsResponse>;
  createIssueComment(payload: CreateIssueCommentPayload): Promise<CreateIssueCommentResponse>;
  updateIssueComment(payload: UpdateIssueCommentPayload): Promise<UpdateIssueCommentResponse>;
  deleteIssueComment(payload: DeleteIssueCommentPayload): Promise<DeleteIssueCommentResponse>;
  getIssueAttachments(params: GetIssueAttachmentsParams): Promise<GetIssueAttachmentsResponse>;
  uploadIssueAttachments(payload: UploadIssueAttachmentPayload): Promise<UploadIssueAttachmentResponse>;
  retryIssueAttachment(payload: RetryIssueAttachmentPayload): Promise<MutateIssueAttachmentResponse>;
  cancelIssueAttachment(payload: CancelIssueAttachmentPayload): Promise<MutateIssueAttachmentResponse>;
  deleteIssueAttachment(payload: DeleteIssueAttachmentPayload): Promise<MutateIssueAttachmentResponse>;
  createIssue(payload: CreateIssuePayload): Promise<CreateIssueResponse>;
  updateIssue(payload: UpdateIssuePayload): Promise<UpdateIssueResponse>;
  deleteIssue(payload: DeleteIssuePayload): Promise<DeleteIssueResponse>;
  searchLinkableIssues(params: SearchLinkableIssuesParams): Promise<SearchLinkableIssuesResponse>;
  linkIssues(payload: LinkIssuesPayload): Promise<LinkIssuesResponse>;
  updateIssueRank(payload: UpdateIssueRankPayload): Promise<UpdateIssueRankResponse>;
}
