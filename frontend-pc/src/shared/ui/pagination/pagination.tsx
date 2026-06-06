import { ChevronLeft, ChevronRight, MoreHorizontal } from "lucide-react";

import { cn } from "@/shared/lib/utils";
import { Button } from "../button";

export interface PaginationProps {
  page: number;
  pageSize: number;
  total: number;
  onPageChange?: (page: number) => void;
}

export function Pagination({ page, pageSize, total, onPageChange }: PaginationProps) {
  const totalPages = Math.max(1, Math.ceil(total / pageSize));
  const start = total === 0 ? 0 : (page - 1) * pageSize + 1;
  const end = Math.min(page * pageSize, total);

  const pages = totalPages <= 5
    ? Array.from({ length: totalPages }, (_, index) => index + 1)
    : [1, Math.max(2, page - 1), page, Math.min(totalPages - 1, page + 1), totalPages]
        .filter((value, index, array) => array.indexOf(value) === index)
        .sort((first, second) => first - second);

  return (
    <div className="flex flex-col gap-3 border-t border-[#DFE1E6] bg-white px-4 py-3 text-xs text-[#626F86] md:flex-row md:items-center md:justify-between">
      <span>
        显示 {start} - {end} 项，共 {total} 项
      </span>

      <div className="flex items-center gap-2">
        <Button
          className="inline-flex h-8 items-center gap-1 rounded-[3px] border border-[#DFE1E6] px-2.5 text-[#44546F] transition hover:bg-[#F7F8F9] disabled:cursor-not-allowed disabled:opacity-50"
          disabled={page <= 1}
          onClick={() => onPageChange?.(page - 1)}
        >
          <ChevronLeft className="h-3.5 w-3.5" />
          上一页
        </Button>

        <div className="flex items-center gap-1">
          {pages.map((pageNumber, index) => {
            const previous = pages[index - 1];
            const showGap = previous && pageNumber - previous > 1;

            return (
              <div key={pageNumber} className="flex items-center gap-1">
                {showGap ? (
                  <span className="inline-flex h-8 w-8 items-center justify-center text-[#97A0AF]">
                    <MoreHorizontal className="h-3.5 w-3.5" />
                  </span>
                ) : null}
                <Button
                  className={cn(
                    "inline-flex h-8 min-w-8 items-center justify-center rounded-[3px] border px-2 text-[12px] font-medium transition",
                    page === pageNumber
                      ? "border-[#0C66E4] bg-[#0C66E4] text-white"
                      : "border-[#DFE1E6] bg-white text-[#44546F] hover:bg-[#F7F8F9]",
                  )}
                  onClick={() => onPageChange?.(pageNumber)}
                >
                  {pageNumber}
                </Button>
              </div>
            );
          })}
        </div>

        <Button
          className="inline-flex h-8 items-center gap-1 rounded-[3px] border border-[#DFE1E6] px-2.5 text-[#44546F] transition hover:bg-[#F7F8F9] disabled:cursor-not-allowed disabled:opacity-50"
          disabled={page >= totalPages}
          onClick={() => onPageChange?.(page + 1)}
        >
          下一页
          <ChevronRight className="h-3.5 w-3.5" />
        </Button>

        <div className="inline-flex h-8 items-center rounded-[3px] border border-[#DFE1E6] bg-[#F7F8F9] px-2.5 text-[#44546F]">
          {pageSize} 条/页
        </div>
      </div>
    </div>
  );
}
