import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export interface DropdownMenuProps {
  className?: string;
  title?: string;
  children: ReactNode;
}

/**
 * A styled dropdown/overlay menu panel (card-style container with shadow).
 * Use inside this panel: MenuItem components, or any custom content.
 */
export function DropdownMenu({ className, title, children }: DropdownMenuProps) {
  return (
    <div
      className={cn(
        "absolute z-40 min-w-[200px] rounded-[4px] border border-[#DFE1E6] bg-white py-2 shadow-[0_8px_24px_rgba(9,30,66,0.18)]",
        className,
      )}
    >
      {title ? (
        <div className="border-b border-[#EBECF0] px-4 pb-2 text-[12px] font-bold text-[#172B4D]">
          {title}
        </div>
      ) : null}
      {children}
    </div>
  );
}

export interface DropdownMenuItemProps {
  label: string;
  active?: boolean;
  disabled?: boolean;
  suffix?: ReactNode;
  onClick?: () => void;
}

export function DropdownMenuItem({
  label,
  active = false,
  disabled = false,
  suffix,
  onClick,
}: DropdownMenuItemProps) {
  return (
    <button
      className={cn(
        "flex w-full items-center justify-between px-4 py-2 text-left text-[13px] transition-colors",
        disabled && "cursor-not-allowed opacity-50",
        active
          ? "bg-[#E9F2FF]/50 font-semibold text-[#0C66E4]"
          : "text-[#172B4D] hover:bg-[#F4F5F7]",
      )}
      disabled={disabled}
      onClick={onClick}
    >
      <span>{label}</span>
      {suffix ? <span className="shrink-0 text-[12px] text-[#6B778C]">{suffix}</span> : null}
      {active ? <span className="font-bold text-[#0C66E4]">✓</span> : null}
    </button>
  );
}

export function DropdownMenuDivider() {
  return <div className="my-1 border-t border-[#EBECF0]" />;
}
