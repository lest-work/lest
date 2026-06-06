import { appConfig } from "@/shared/config";

// ─── Auth token injection ─────────────────────────────────────────

let _getToken: () => string | null = () => null;

/**
 * Configure the auth token provider used by all API requests.
 * Called once at app bootstrap (e.g. in AppProvider).
 */
export function configureAuth(getToken: () => string | null) {
  _getToken = getToken;
}

// ─── Global error handler ─────────────────────────────────────────

let _onError: (error: ApiError) => void = () => {};

/**
 * Configure the global error handler for API errors.
 * Called once at app bootstrap. Use for 401 → redirect login,
 * or show toast for general errors.
 */
export function configureErrorHandler(handler: (error: ApiError) => void) {
  _onError = handler;
}

// ─── ApiError ─────────────────────────────────────────────────────

export class ApiError extends Error {
  /**
   * @param status  0 for network errors, otherwise HTTP status code
   * @param message Human-readable error message
   * @param body    Raw error body from the server (if any)
   */
  constructor(
    public status: number,
    message: string,
    public body: unknown = null,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

// ─── Request config ───────────────────────────────────────────────

interface RequestConfig extends Omit<RequestInit, "body"> {
  /** URL query parameters (automatically encoded) */
  params?: Record<string, string | number | boolean | undefined>;
  /** JSON-serializable request body */
  body?: unknown;
}

// ─── Internal helpers ─────────────────────────────────────────────

function buildQueryString(params: Record<string, string | number | boolean | undefined>): string {
  const entries = Object.entries(params).filter(([, v]) => v !== undefined);
  if (entries.length === 0) return "";
  const parts = entries.map(
    ([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`,
  );
  return `?${parts.join("&")}`;
}

/** Friendly Chinese messages for common HTTP status codes */
function httpStatusMessage(status: number): string {
  const map: Record<number, string> = {
    400: "请求参数有误",
    401: "登录已过期，请重新登录",
    403: "没有权限执行此操作",
    404: "请求的资源不存在",
    409: "数据冲突，请刷新后重试",
    422: "提交的数据校验失败",
    429: "请求过于频繁，请稍后重试",
    500: "服务器内部错误",
    502: "网关错误",
    503: "服务暂时不可用",
  };
  return map[status] ?? "请求失败";
}

// ─── Core request ─────────────────────────────────────────────────

async function request<TResponse>(path: string, config: RequestConfig = {}): Promise<TResponse> {
  const { params, body, ...init } = config;

  // 1. Build URL ─────────────────────────────────────────
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const url = `${appConfig.apiBaseUrl}${normalizedPath}${buildQueryString(params ?? {})}`;

  // 2. Build headers ──────────────────────────────────────
  const customHeaders: Record<string, string> =
    init.headers instanceof Headers
      ? Object.fromEntries(init.headers.entries())
      : Array.isArray(init.headers)
        ? Object.fromEntries(init.headers)
        : (init.headers as Record<string, string> | undefined) ?? {};

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...customHeaders,
  };
  const token = _getToken();
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  // 3. Execute fetch ──────────────────────────────────────
  let response: Response;
  try {
    response = await fetch(url, {
      ...init,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch (error) {
    // Network-level failure (DNS failure, connection refused, etc.)
    const message =
      error instanceof TypeError
        ? "网络连接失败，请检查网络后重试"
        : error instanceof Error
          ? error.message
          : "请求发送失败";
    const apiError = new ApiError(0, message);
    _onError(apiError);
    throw apiError;
  }

  // 4. Handle HTTP errors ─────────────────────────────────
  if (!response.ok) {
    const errorBody = await response.json().catch(() => null);
    const message =
      (errorBody?.message ?? response.statusText) || httpStatusMessage(response.status);
    const apiError = new ApiError(response.status, message, errorBody);
    _onError(apiError);
    throw apiError;
  }

  // 5. Parse response body ────────────────────────────────
  const json: unknown = await response.json();

  // 6. Auto-unwrap ApiResult<T> format ────────────────────
  // Backend returns { success, code, data, message, traceId }
  if (json && typeof json === "object" && "success" in (json as Record<string, unknown>)) {
    const envelope = json as Record<string, unknown>;
    if (envelope.success === true && "data" in envelope) {
      return envelope.data as TResponse;
    }
    if (envelope.success === false) {
      const message = (envelope.message as string) ?? "请求失败";
      const apiError = new ApiError(response.status, message, json);
      _onError(apiError);
      throw apiError;
    }
  }

  // Fallback: return raw JSON (for mock data or non-wrapped responses)
  return json as TResponse;
}

// ─── Public API helpers ───────────────────────────────────────────

export const api = {
  get<TResponse>(path: string, config?: RequestConfig) {
    return request<TResponse>(path, { ...config, method: "GET" });
  },

  post<TResponse>(path: string, body?: unknown, config?: RequestConfig) {
    return request<TResponse>(path, { ...config, method: "POST", body });
  },

  put<TResponse>(path: string, body?: unknown, config?: RequestConfig) {
    return request<TResponse>(path, { ...config, method: "PUT", body });
  },

  patch<TResponse>(path: string, body?: unknown, config?: RequestConfig) {
    return request<TResponse>(path, { ...config, method: "PATCH", body });
  },

  delete<TResponse>(path: string, config?: RequestConfig) {
    return request<TResponse>(path, { ...config, method: "DELETE" });
  },
};
