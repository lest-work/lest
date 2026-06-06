import type { LucideIcon } from "lucide-react";
import { AlertTriangle, FileSearch, LockKeyhole, RefreshCcw } from "lucide-react";
import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui/button";

export type ResultStateTone = "empty" | "error" | "permission";
export type ResultStateSize = "sm" | "md" | "lg";

export interface ResultStateProps {
  tone: ResultStateTone;
  title: string;
  description?: string;
  actionLabel?: string;
  secondaryActionLabel?: string;
  icon?: LucideIcon;
  size?: ResultStateSize;
  meta?: ReactNode;
  className?: string;
  onAction?: () => void;
  onSecondaryAction?: () => void;
}

const toneConfig: Record<ResultStateTone, { icon: LucideIcon; iconClassName: string; shellClassName: string }> = {
  empty: {
    icon: FileSearch,
    iconClassName: "bg-[#E9F2FF] text-[#0C66E4]",
    shellClassName: "border-dashed border-[#C1C7D0] bg-[#FAFBFC]",
  },
  error: {
    icon: AlertTriangle,
    iconClassName: "bg-[#FFECEB] text-[#E34935]",
    shellClassName: "border-[#FFD5CC] bg-white",
  },
  permission: {
    icon: LockKeyhole,
    iconClassName: "bg-[#F4F5F7] text-[#172B4D]",
    shellClassName: "border-[#DFE1E6] bg-white",
  },
};

const sizeClassName: Record<ResultStateSize, string> = {
  sm: "min-h-[168px] px-5 py-6",
  md: "min-h-[240px] px-6 py-10",
  lg: "min-h-[360px] px-8 py-16",
};

export function ResultState({
  tone,
  title,
  description,
  actionLabel,
  secondaryActionLabel,
  icon,
  size = "md",
  meta,
  className,
  onAction,
  onSecondaryAction,
}: ResultStateProps) {
  const config = toneConfig[tone];
  const Icon = icon ?? config.icon;

  return (
    <div className={cn("flex flex-col items-center justify-center rounded-[8px] border text-center", config.shellClassName, sizeClassName[size], className)}>
      <div className={cn("flex h-16 w-16 items-center justify-center rounded-full shadow-[inset_0_0_0_1px_rgba(9,30,66,0.08)]", config.iconClassName)}>
        <Icon className="h-7 w-7" />
      </div>
      <h3 className="mt-4 text-[16px] font-bold text-[#172B4D]">{title}</h3>
      {description ? <p className="mt-2 max-w-[460px] text-[13px] leading-6 text-[#626F86]">{description}</p> : null}
      {meta ? <div className="mt-3">{meta}</div> : null}
      {actionLabel || secondaryActionLabel ? (
        <div className="mt-5 flex flex-wrap items-center justify-center gap-3">
          {actionLabel ? (
            <Button className="h-8 rounded-[4px] bg-[#0C66E4] px-4 text-[13px] font-semibold text-white hover:bg-[#0052CC]" onClick={onAction}>
              {tone === "error" ? <RefreshCcw className="h-3.5 w-3.5" /> : null}
              {actionLabel}
            </Button>
          ) : null}
          {secondaryActionLabel ? (
            <Button variant="outline" className="h-8 rounded-[4px] border-[#DFE1E6] bg-white px-4 text-[13px] font-semibold" onClick={onSecondaryAction}>
              {secondaryActionLabel}
            </Button>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
