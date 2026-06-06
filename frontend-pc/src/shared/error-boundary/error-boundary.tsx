import { Component, type ErrorInfo, type ReactNode } from "react";

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    console.error("[ErrorBoundary]", error, info.componentStack);
  }

  render(): ReactNode {
    if (this.state.hasError) {
      return this.props.fallback ?? <ErrorFallback error={this.state.error} />;
    }
    return this.props.children;
  }
}

function ErrorFallback({ error }: { error: Error | null }) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#F7F8F9] p-8">
      <div className="w-full max-w-md rounded-[8px] border border-[#DFE1E6] bg-white p-8 text-center shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
        <h1 className="mb-2 text-[20px] font-bold text-[#172B4D]">页面出现异常</h1>
        <p className="mb-6 text-[14px] text-[#626F86]">
          请尝试刷新页面，如果问题持续请联系技术支持。
        </p>
        <pre className="mb-6 max-h-[200px] overflow-auto rounded-[4px] bg-[#F4F5F7] p-4 text-left text-[12px] text-[#DE350B]">
          {error?.message ?? "未知错误"}
        </pre>
        <button
          className="rounded-[4px] bg-[#0C66E4] px-6 py-2 text-[14px] font-semibold text-white hover:bg-[#0052CC]"
          onClick={() => window.location.reload()}
        >
          刷新页面
        </button>
      </div>
    </div>
  );
}
