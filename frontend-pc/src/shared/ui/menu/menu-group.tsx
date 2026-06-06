import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export interface MenuGroupProps {
  title?: string;
  className?: string;
  children: ReactNode;
}

export function MenuGroup({ title, className, children }: MenuGroupProps) {
  return (
    <div className={cn(className)}>
      {title ? (
        <div className="mb-2 px-3 text-[12px] font-semibold text-[#626F86]">
          {title}
        </div>
      ) : null}
      <div className="space-y-[2px]">{children}</div>
    </div>
  );
}
