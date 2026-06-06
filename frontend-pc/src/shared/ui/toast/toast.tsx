import { AlertCircle, CheckCircle2, Info, Loader2, TriangleAlert, X } from "lucide-react";
import { useEffect } from "react";

import { cn } from "@/shared/lib/utils";
import { Button } from "../button";

export type ToastTone = "success" | "error" | "warning" | "info" | "loading" | "default";
export type ToastPlacement = "top-center" | "top-right" | "center" | "bottom-left" | "bottom-right";

export interface ToastMessage {
  id: string;
  title: string;
  description?: string;
  tone?: ToastTone;
  actionLabel?: string;
}

export interface ToastViewportProps {
  messages: ToastMessage[];
  placement?: ToastPlacement;
  onClose: (id: string) => void;
  onAction?: (id: string) => void;
}

const toneClassName: Record<ToastTone, string> = {
  success: "border-[#ABF5D1] bg-[#F3FCF7] text-[#164B35]",
  error: "border-[#FFD5CC] bg-[#FFF4F2] text-[#AE2E24]",
  warning: "border-[#FFE2BD] bg-[#FFF7ED] text-[#974F0C]",
  info: "border-[#CCE0FF] bg-[#F0F6FF] text-[#0C66E4]",
  loading: "border-[#CCE0FF] bg-white text-[#172B4D]",
  default: "border-[#DFE1E6] bg-white text-[#172B4D]",
};

const placementClassName: Record<ToastPlacement, string> = {
  "top-center": "left-1/2 top-5 -translate-x-1/2",
  "top-right": "right-6 top-20",
  center: "left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2",
  "bottom-left": "bottom-6 left-6",
  "bottom-right": "bottom-6 right-6",
};

function ToastIcon({ tone = "default" }: { tone?: ToastTone }) {
  if (tone === "success") {
    return <CheckCircle2 className="h-4 w-4 text-[#22A06B]" />;
  }
  if (tone === "error") {
    return <AlertCircle className="h-4 w-4 text-[#E34935]" />;
  }
  if (tone === "warning") {
    return <TriangleAlert className="h-4 w-4 text-[#F59E0B]" />;
  }
  if (tone === "loading") {
    return <Loader2 className="h-4 w-4 animate-spin text-[#0C66E4]" />;
  }
  return <Info className="h-4 w-4 text-[#0C66E4]" />;
}

export function ToastViewport({ messages, placement = "top-right", onClose, onAction }: ToastViewportProps) {
  useEffect(() => {
    const timers = messages
      .filter((message) => message.tone !== "loading")
      .map((message) => window.setTimeout(() => onClose(message.id), 3200));

    return () => timers.forEach((timer) => window.clearTimeout(timer));
  }, [messages, onClose]);

  if (!messages.length) {
    return null;
  }

  return (
    <div className={cn("fixed z-[80] flex w-[360px] max-w-[calc(100vw-32px)] flex-col gap-2", placementClassName[placement])}>
      {messages.map((message) => (
        <div
          key={message.id}
          className={cn(
            "flex items-start gap-3 rounded-[6px] border px-4 py-3 shadow-[0_8px_24px_rgba(9,30,66,0.18)]",
            toneClassName[message.tone ?? "default"],
          )}
        >
          <ToastIcon tone={message.tone} />
          <div className="min-w-0 flex-1">
            <div className="text-[13px] font-semibold">{message.title}</div>
            {message.description ? <div className="mt-1 text-[12px] leading-5 text-[#44546F]">{message.description}</div> : null}
            {message.actionLabel ? (
              <Button className="mt-2 text-[12px] font-semibold text-[#0C66E4] hover:underline" onClick={() => onAction?.(message.id)}>
                {message.actionLabel}
              </Button>
            ) : null}
          </div>
          <Button className="rounded-[3px] p-0.5 text-[#626F86] hover:bg-white/60 hover:text-[#172B4D]" onClick={() => onClose(message.id)}>
            <X className="h-3.5 w-3.5" />
          </Button>
        </div>
      ))}
    </div>
  );
}
