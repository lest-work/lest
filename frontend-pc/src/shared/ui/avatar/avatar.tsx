import * as React from "react";

import { cn, getInitials } from "@/shared/lib/utils";

export interface AvatarProps {
  name: string;
  color?: string;
  imageSrc?: string;
  className?: string;
}

export function Avatar({ name, color = "bg-slate-600", imageSrc, className }: AvatarProps) {
  return (
    <span
      className={cn(
        "inline-flex h-8 w-8 shrink-0 items-center justify-center overflow-hidden rounded-full bg-slate-200 text-xs font-semibold text-white",
        !imageSrc && color,
        className,
      )}
      title={name}
    >
      {imageSrc ? (
        <img src={imageSrc} alt={name} className="h-full w-full object-cover" />
      ) : (
        <span>{getInitials(name)}</span>
      )}
    </span>
  );
}
