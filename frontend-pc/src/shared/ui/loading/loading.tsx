import { Loader2 } from "lucide-react";

import { cn } from "@/shared/lib/utils";

export interface LoadingProps {
  label?: string;
  className?: string;
}

export function Loading({ label = "加载中", className }: LoadingProps) {
  return (
    <div className={cn("inline-flex items-center gap-2 text-[13px] font-medium text-[#44546F]", className)}>
      <Loader2 className="h-4 w-4 animate-spin text-[#0C66E4]" />
      <span>{label}</span>
    </div>
  );
}
