import { AlertCircle, CheckCircle2, Info, TriangleAlert } from "lucide-react";
import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export type AlertTone = "success" | "error" | "warning" | "info";

export interface AlertProps {
  title: string;
  description?: ReactNode;
  tone?: AlertTone;
  className?: string;
}

const toneClassName: Record<AlertTone, string> = {
  success: "border-[#ABF5D1] bg-[#F3FCF7] text-[#164B35]",
  error: "border-[#FFD5CC] bg-[#FFF4F2] text-[#AE2E24]",
  warning: "border-[#FFE2BD] bg-[#FFF7ED] text-[#974F0C]",
  info: "border-[#CCE0FF] bg-[#F0F6FF] text-[#0C66E4]",
};

function AlertIcon({ tone }: { tone: AlertTone }) {
  if (tone === "success") {
    return <CheckCircle2 className="h-4 w-4" />;
  }
  if (tone === "error") {
    return <AlertCircle className="h-4 w-4" />;
  }
  if (tone === "warning") {
    return <TriangleAlert className="h-4 w-4" />;
  }
  return <Info className="h-4 w-4" />;
}

export function Alert({ title, description, tone = "info", className }: AlertProps) {
  return (
    <div className={cn("flex gap-3 rounded-[6px] border px-4 py-3", toneClassName[tone], className)}>
      <AlertIcon tone={tone} />
      <div className="min-w-0">
        <div className="text-[13px] font-semibold">{title}</div>
        {description ? <div className="mt-1 text-[12px] leading-5 text-[#44546F]">{description}</div> : null}
      </div>
    </div>
  );
}
