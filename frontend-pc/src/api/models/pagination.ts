export interface ApiPageRequest {
  pageNum: number;
  pageSize: number;
}

export interface ApiSortRequest {
  field: string;
  order: "asc" | "desc";
}

export interface ApiPageResponse<TItem> {
  pageNum: number;
  pageSize: number;
  total: number;
  list: TItem[];
}

export interface ApiRequestMeta {
  requestId?: string;
  traceId?: string;
}
