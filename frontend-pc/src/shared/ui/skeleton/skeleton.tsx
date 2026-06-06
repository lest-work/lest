import * as React from "react";

import { cn } from "@/shared/lib/utils";

export interface SkeletonProps extends React.HTMLAttributes<HTMLDivElement> {}

export function Skeleton({ className, ...props }: SkeletonProps) {
  return <div className={cn("animate-pulse rounded-[4px] bg-[#EBECF0]", className)} {...props} />;
}

export interface SkeletonBlockProps {
  shape: "table" | "list" | "cards" | "detail" | "chart" | "form";
  rows?: number;
  className?: string;
}

export function SkeletonBlock({ shape, rows = 4, className }: SkeletonBlockProps) {
  if (shape === "table") {
    return (
      <div className={cn("overflow-hidden rounded-[6px] border border-[#DFE1E6] bg-white", className)}>
        <div className="grid grid-cols-[40px_90px_1fr_92px_92px] gap-3 border-b border-[#EBECF0] bg-[#FAFBFC] px-4 py-3">
          {Array.from({ length: 5 }).map((_, index) => (
            <Skeleton key={index} className="h-3" />
          ))}
        </div>
        <div className="divide-y divide-[#EBECF0]">
          {Array.from({ length: rows }).map((_, index) => (
            <div key={index} className="grid grid-cols-[40px_90px_1fr_92px_92px] gap-3 px-4 py-3">
              <Skeleton className="h-4 w-4 rounded-full" />
              <Skeleton className="h-3" />
              <Skeleton className="h-3" />
              <Skeleton className="h-3" />
              <Skeleton className="h-3" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (shape === "list") {
    return (
      <div className={cn("space-y-3 rounded-[6px] border border-[#DFE1E6] bg-white p-4", className)}>
        {Array.from({ length: rows }).map((_, index) => (
          <div key={index} className="flex gap-3">
            <Skeleton className="h-8 w-8 shrink-0 rounded-full" />
            <div className="min-w-0 flex-1 space-y-2">
              <Skeleton className="h-3 w-2/3" />
              <Skeleton className="h-3 w-full" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (shape === "cards") {
    return (
      <div className={cn("grid gap-3 md:grid-cols-2", className)}>
        {Array.from({ length: rows }).map((_, index) => (
          <div key={index} className="rounded-[6px] border border-[#DFE1E6] bg-white p-3">
            <Skeleton className="h-3 w-4/5" />
            <Skeleton className="mt-3 h-3 w-1/2" />
            <div className="mt-4 flex items-center justify-between">
              <Skeleton className="h-5 w-16" />
              <Skeleton className="h-7 w-7 rounded-full" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (shape === "detail") {
    return (
      <div className={cn("rounded-[6px] border border-[#DFE1E6] bg-white p-4", className)}>
        <Skeleton className="h-4 w-2/3" />
        <Skeleton className="mt-4 h-24 w-full" />
        <div className="mt-4 grid gap-3 md:grid-cols-3">
          {Array.from({ length: 3 }).map((_, index) => (
            <Skeleton key={index} className="h-8" />
          ))}
        </div>
      </div>
    );
  }

  if (shape === "chart") {
    return (
      <div className={cn("rounded-[6px] border border-[#DFE1E6] bg-white p-4", className)}>
        <Skeleton className="h-3 w-32" />
        <div className="mt-5 flex h-32 items-end gap-3">
          {Array.from({ length: rows }).map((_, index) => (
            <Skeleton key={index} className="w-full" style={{ height: `${28 + ((index * 17) % 72)}%` }} />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className={cn("rounded-[6px] border border-[#DFE1E6] bg-white p-4", className)}>
      {Array.from({ length: rows }).map((_, index) => (
        <div key={index} className="mb-3 last:mb-0">
          <Skeleton className="mb-2 h-3 w-24" />
          <Skeleton className="h-9 w-full" />
        </div>
      ))}
      <div className="mt-5 flex justify-end gap-2">
        <Skeleton className="h-8 w-16" />
        <Skeleton className="h-8 w-20" />
      </div>
    </div>
  );
}
