import type { LucideIcon } from "lucide-react";
import { SearchX } from "lucide-react";
import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui/button";

export interface EmptyStateProps {
  title: string;
  description?: string;
  actionLabel?: string;
  secondaryActionLabel?: string;
  icon?: LucideIcon;
  compact?: boolean;
  className?: string;
  children?: ReactNode;
  onAction?: () => void;
  onSecondaryAction?: () => void;
}

export function EmptyState({
  title,
  description,
  actionLabel,
  secondaryActionLabel,
  icon: Icon = SearchX,
  compact,
  className,
  children,
  onAction,
  onSecondaryAction,
}: EmptyStateProps) {
  return (
    <div className={cn("flex flex-col items-center justify-center rounded-[6px] border border-dashed border-[#C1C7D0] bg-[#FAFBFC] px-6 text-center", compact ? "py-8" : "py-14", className)}>
      <div className="flex h-16 w-16 items-center justify-center rounded-full bg-[#E9F2FF] text-[#0C66E4] shadow-[inset_0_0_0_1px_rgba(12,102,228,0.12)]">
        <Icon className="h-7 w-7" />
      </div>
      <h3 className="mt-4 text-[15px] font-bold text-[#172B4D]">{title}</h3>
      {description ? <p className="mt-2 max-w-[420px] text-[13px] leading-6 text-[#626F86]">{description}</p> : null}
      {children}
      {actionLabel || secondaryActionLabel ? (
        <div className="mt-5 flex flex-wrap items-center justify-center gap-3">
          {secondaryActionLabel ? (
            <Button variant="outline" className="h-8 rounded-[4px] border-[#DFE1E6] bg-white text-[13px]" onClick={onSecondaryAction}>
              {secondaryActionLabel}
            </Button>
          ) : null}
          {actionLabel ? (
            <Button className="h-8 rounded-[4px] bg-[#0C66E4] text-[13px] text-white" onClick={onAction}>
              {actionLabel}
            </Button>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
