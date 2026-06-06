export interface ApiErrorBody {
  code: string;
  message: string;
  traceId: string;
  details?: Record<string, unknown>;
}

export type ApiResult<TData> =
  | {
      success: true;
      code: "0";
      message: string;
      data: TData;
      traceId: string;
    }
  | {
      success: false;
      code: string;
      message: string;
      error: ApiErrorBody;
      traceId: string;
    };
