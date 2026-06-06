import type { ApiResult } from "./models/response";

export interface MockApiOptions {
  message?: string;
  traceId?: string;
}

let mockCounter = 0;

export function createMockTraceId(): string {
  mockCounter += 1;
  return `mock-trace-${Date.now()}-${mockCounter}`;
}

export async function withMockLatency<T>(fn: () => T, minMs = 80, maxMs = 320): Promise<T> {
  const delay = minMs + Math.random() * (maxMs - minMs);
  await new Promise((resolve) => setTimeout(resolve, delay));
  return fn();
}

export function createMockApiSuccess<T>(data: T, options?: MockApiOptions): ApiResult<T> {
  return {
    success: true,
    code: "0",
    message: options?.message ?? "success",
    data,
    traceId: options?.traceId ?? createMockTraceId(),
  };
}
