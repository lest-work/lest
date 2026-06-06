import {
  CalendarDays,
  Check,
  ChevronDown,
  Download,
  Edit3,
  Eye,
  MoreHorizontal,
  Settings,
  Share2,
  Star,
  Trash2,
  X,
} from "lucide-react";

import type { DashboardFilterOptionsResponse, DashboardIssueFilterId, DateRangePreset } from "@/api/dashboard";
import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui";
import { FilterButton } from "./dashboard-components";
import type { OverlayId } from "./dashboard-constants";

export function ProjectHeader({
  dateLabel,
  issueFilterLabel,
  filters,
  datePreset,
  issueFilterId,
  overlay,
  onOverlayChange,
  onDatePresetChange,
  onIssueFilterChange,
}: {
  dateLabel: string;
  issueFilterLabel: string;
  filters?: DashboardFilterOptionsResponse;
  datePreset: DateRangePreset;
  issueFilterId: DashboardIssueFilterId;
  overlay: OverlayId;
  onOverlayChange: (id: OverlayId) => void;
  onDatePresetChange: (id: DateRangePreset) => void;
  onIssueFilterChange: (id: DashboardIssueFilterId) => void;
}) {
  return (
    <div className="relative mb-4 flex items-start justify-between gap-6">
      <div>
        <div className="flex items-center gap-2">
          <h1 className="text-[26px] font-semibold tracking-[-0.01em] text-[#172B4D]">项目仪表盘</h1>
          <Button className="flex h-8 w-8 items-center justify-center rounded-[4px] text-[#7A869A] hover:bg-[#F4F5F7] hover:text-[#FFAB00]" aria-label="收藏">
            <Star className="h-5 w-5" />
          </Button>
        </div>
        <div className="mt-5 flex flex-wrap items-center gap-4">
          <div className="relative">
            <FilterButton icon={<CalendarDays className="h-4 w-4" />} label={dateLabel} onClick={() => onOverlayChange(overlay === "date" ? null : "date")} />
            {overlay === "date" && filters ? (
              <DateRangeDropdown options={filters.dateRanges} value={datePreset} onChange={onDatePresetChange} onClose={() => onOverlayChange(null)} />
            ) : null}
          </div>
          <div className="relative">
            <FilterButton label={issueFilterLabel} onClick={() => onOverlayChange(overlay === "issueFilter" ? null : "issueFilter")} />
            {overlay === "issueFilter" && filters ? (
              <IssueFilterDropdown options={filters.issueFilters} value={issueFilterId} onChange={onIssueFilterChange} onClose={() => onOverlayChange(null)} />
            ) : null}
          </div>
        </div>
      </div>
      <div className="mt-1 flex items-center gap-3">
        <Button variant="outline" className="h-[38px] rounded-[4px] border-[#DFE1E6] bg-white px-4 text-[13px] font-semibold text-[#172B4D] hover:bg-[#F4F5F7]">
          <Share2 className="h-4 w-4" />
          分享
        </Button>
        <Button variant="outline" className="h-[38px] rounded-[4px] border-[#DFE1E6] bg-white px-4 text-[13px] font-semibold text-[#172B4D] hover:bg-[#F4F5F7]">
          <Edit3 className="h-4 w-4" />
          编辑
        </Button>
        <div className="relative">
          <Button
            variant="outline"
            size="icon"
            className="h-[38px] w-[44px] rounded-[4px] border-[#DFE1E6] bg-white text-[#172B4D] hover:bg-[#F4F5F7]"
            onClick={() => onOverlayChange(overlay === "pageMore" ? null : "pageMore")}
          >
            <MoreHorizontal className="h-5 w-5" />
          </Button>
          {overlay === "pageMore" ? <CardMoreMenu className="right-0 top-[44px]" dangerLabel="删除仪表盘" /> : null}
        </div>
      </div>
    </div>
  );
}

function CardMoreMenu({ className, dangerLabel = "从仪表盘移除" }: { className?: string; dangerLabel?: string }) {
  return (
    <div className={cn("absolute z-30 w-[184px] rounded-[4px] border border-[#DFE1E6] bg-white py-2 shadow-[0_8px_24px_rgba(9,30,66,0.18)]", className)}>
      {[
        { label: "查看详情", icon: Eye },
        { label: "导出数据", icon: Download },
        { label: "配置图表", icon: Settings },
      ].map((item) => {
        const Icon = item.icon;
        return (
          <Button key={item.label} className="flex w-full items-center gap-2 px-3 py-2 text-left text-[13px] text-[#172B4D] hover:bg-[#F4F5F7]">
            <Icon className="h-4 w-4 text-[#44546F]" />
            {item.label}
          </Button>
        );
      })}
      <div className="my-1 border-t border-[#EBECF0]" />
      <Button className="flex w-full items-center gap-2 px-3 py-2 text-left text-[13px] text-[#DE350B] hover:bg-[#FFEBE6]">
        <Trash2 className="h-4 w-4" />
        {dangerLabel}
      </Button>
    </div>
  );
}

function DateRangeDropdown({
  options,
  value,
  onChange,
  onClose,
}: {
  options: NonNullable<DashboardFilterOptionsResponse["dateRanges"]>;
  value: DateRangePreset;
  onChange: (id: DateRangePreset) => void;
  onClose: () => void;
}) {
  return (
    <div className="absolute left-0 top-[44px] z-30 flex w-[520px] rounded-[4px] border border-[#DFE1E6] bg-white shadow-[0_8px_24px_rgba(9,30,66,0.18)]">
      <div className="w-[188px] border-r border-[#EBECF0] py-2">
        {options.map((option) => (
          <Button
            key={option.id}
            className={cn("flex w-full items-center justify-between px-4 py-2 text-left text-[13px] hover:bg-[#F4F5F7]", value === option.id ? "font-semibold text-[#0C66E4]" : "text-[#172B4D]")}
            onClick={() => onChange(option.id)}
          >
            {option.label}
            {value === option.id ? <Check className="h-4 w-4" /> : null}
          </Button>
        ))}
      </div>
      <div className="flex-1 p-4">
        <div className="mb-3 text-[13px] font-bold text-[#172B4D]">自定义日期范围</div>
        <div className="grid grid-cols-2 gap-3">
          {["2026 年 6 月", "2026 年 7 月"].map((month) => (
            <div key={month} className="rounded-[4px] border border-[#DFE1E6] p-3">
              <div className="mb-2 text-center text-[12px] font-semibold text-[#44546F]">{month}</div>
              <div className="grid grid-cols-7 gap-1 text-center text-[11px] text-[#6B778C]">
                {"一二三四五六日".split("").map((day) => (
                  <span key={day}>{day}</span>
                ))}
                {Array.from({ length: 28 }).map((_, index) => (
                  <span key={index} className={cn("rounded-[3px] py-1", index > 8 && index < 18 ? "bg-[#E9F2FF] text-[#0C66E4]" : "hover:bg-[#F4F5F7]")}>
                    {index + 1}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
        <div className="mt-4 flex justify-end gap-2">
          <Button variant="outline" className="h-8 rounded-[3px] px-3 text-[12px]" onClick={onClose}>
            取消
          </Button>
          <Button className="h-8 rounded-[3px] bg-[#0C66E4] px-3 text-[12px]" onClick={onClose}>
            确定
          </Button>
        </div>
      </div>
    </div>
  );
}

function IssueFilterDropdown({
  options,
  value,
  onChange,
  onClose,
}: {
  options: NonNullable<DashboardFilterOptionsResponse["issueFilters"]>;
  value: DashboardIssueFilterId;
  onChange: (id: DashboardIssueFilterId) => void;
  onClose: () => void;
}) {
  return (
    <div className="absolute left-0 top-[44px] z-30 w-[260px] rounded-[4px] border border-[#DFE1E6] bg-white py-2 shadow-[0_8px_24px_rgba(9,30,66,0.18)]">
      {options.map((option) => (
        <Button
          key={option.id}
          className={cn("flex w-full items-center justify-between px-4 py-2 text-left text-[13px] hover:bg-[#F4F5F7]", value === option.id ? "font-semibold text-[#0C66E4]" : "text-[#172B4D]")}
          onClick={() => {
            onChange(option.id);
            onClose();
          }}
        >
          <span>{option.label}</span>
          <span className="text-[12px] text-[#6B778C]">{option.count}</span>
        </Button>
      ))}
      <div className="my-1 border-t border-[#EBECF0]" />
      <Button className="w-full px-4 py-2 text-left text-[13px] font-medium text-[#0C66E4] hover:bg-[#F4F5F7]">管理筛选器</Button>
    </div>
  );
}
