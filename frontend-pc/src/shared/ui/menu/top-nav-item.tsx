import { ChevronDown } from "lucide-react";
import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export interface TopNavItemProps {
  label: string;
  active?: boolean;
  hasSubMenu?: boolean;
  suffix?: ReactNode;
  onClick?: () => void;
  onFocus?: () => void;
  onMouseEnter?: () => void;
}

export function TopNavItem({
  label,
  active = false,
  hasSubMenu = false,
  suffix,
  onClick,
  onFocus,
  onMouseEnter,
}: TopNavItemProps) {
  return (
    <button
      className={cn(
        "relative inline-flex h-[68px] items-center gap-1 text-[14px] font-medium transition-colors",
        active ? "text-[#0C66E4]" : "text-[#172B4D] hover:text-[#0C66E4]",
      )}
      onClick={onClick}
      onFocus={onFocus}
      onMouseEnter={onMouseEnter}
    >
      {label}
      {hasSubMenu ? <ChevronDown className="h-4 w-4" /> : null}
      {suffix}
      {active ? (
        <span className="absolute bottom-0 left-0 right-0 h-[3px] rounded-t-full bg-[#0C66E4]" />
      ) : null}
    </button>
  );
}
