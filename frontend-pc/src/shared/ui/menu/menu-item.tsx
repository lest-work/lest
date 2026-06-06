import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export interface MenuItemProps {
  label: string;
  icon?: ReactNode;
  active?: boolean;
  disabled?: boolean;
  collapsed?: boolean;
  suffix?: ReactNode;
  className?: string;
  onClick?: () => void;
  onFocus?: () => void;
  onMouseEnter?: () => void;
}

export function MenuItem({
  label,
  icon,
  active = false,
  disabled = false,
  collapsed = false,
  suffix,
  className,
  onClick,
  onFocus,
  onMouseEnter,
}: MenuItemProps) {
  return (
    <button
      className={cn(
        "group relative flex w-full items-center rounded-[4px] text-[14px] transition-colors",
        collapsed
          ? "justify-center px-0 py-2"
          : "gap-3 px-3 py-2 text-left",
        disabled && "cursor-not-allowed opacity-50",
        active
          ? "bg-[#E9F2FF] font-medium text-[#0052CC]"
          : "text-[#42526E] hover:bg-[#F4F5F7] hover:text-[#172B4D]",
        className,
      )}
      title={collapsed ? label : undefined}
      disabled={disabled}
      onClick={onClick}
      onFocus={onFocus}
      onMouseEnter={onMouseEnter}
    >
      {active && !collapsed && (
        <span className="absolute bottom-1 left-[-8px] top-1 w-[3px] rounded-r-full bg-[#0C66E4]" />
      )}
      {icon ? <span className="flex h-[18px] w-[18px] shrink-0 items-center justify-center">{icon}</span> : null}
      {!collapsed && (
        <span className="flex-1 truncate text-left">{label}</span>
      )}
      {!collapsed && suffix ? <span className="shrink-0">{suffix}</span> : null}
    </button>
  );
}
