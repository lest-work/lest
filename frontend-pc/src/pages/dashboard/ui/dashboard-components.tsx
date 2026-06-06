import { ChevronDown, MoreHorizontal, Plus, X } from "lucide-react";
import type { ReactNode } from "react";

import type { DashboardMetric } from "@/api/dashboard";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button } from "@/shared/ui";

export function FilterButton({ icon, label, onClick }: { icon?: ReactNode; label: string; onClick: () => void }) {
  return (
    <button
      className="flex items-center gap-2 rounded-[4px] px-2 py-1.5 text-[13px] font-medium text-[#172B4D] hover:bg-[#F4F5F7]"
      onClick={onClick}
    >
      {icon}{label}<ChevronDown className="h-4 w-4 text-[#6B778C]" />
    </button>
  );
}

export function Panel({
  id, title, className, overlay, onOverlayChange, children,
}: {
  id: string; title: string; className?: string;
  overlay?: string | null; onOverlayChange?: (id: string | null) => void;
  children: ReactNode;
}) {
  return (
    <section className={cn("rounded-[8px] border border-[#DFE1E6] bg-white shadow-[0_1px_2px_rgba(9,30,66,0.08)]", className)}>
      <div className="flex items-center justify-between border-b border-[#EBECF0] px-4 py-3">
        <h3 className="text-[14px] font-semibold text-[#172B4D]">{title}</h3>
        {onOverlayChange ? (
          <div className="relative">
            <button
              className="flex h-7 w-7 items-center justify-center rounded-[4px] text-[#42526E] hover:bg-[#F4F5F7]"
              onClick={() => onOverlayChange(overlay === id ? null : id)}
            >
              <MoreHorizontal className="h-4 w-4" />
            </button>
          </div>
        ) : null}
      </div>
      <div className="p-4">{children}</div>
    </section>
  );
}

export function MetricCard({ item, overlay, onOverlayChange }: { item: DashboardMetric; overlay: string | null; onOverlayChange: (id: string | null) => void }) {
  return (
    <div className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
      <div className="relative">
        <button
          className="absolute right-0 top-0 flex h-7 w-7 items-center justify-center rounded-[4px] text-[#42526E] hover:bg-[#F4F5F7]"
          onClick={() => onOverlayChange(overlay === `metric-${item.id}` ? null : `metric-${item.id}`)}
        >
          <MoreHorizontal className="h-4 w-4" />
        </button>
      </div>
      <div className="flex items-center justify-between">
        <span className="text-[12px] font-semibold text-[#626F86]">{item.label}</span>
        <span className={`text-[12px] font-medium ${item.change >= 0 ? "text-[#22A06B]" : "text-[#DE350B]"}`}>
          {item.change >= 0 ? "↑" : "↓"} {Math.abs(item.change)}%
        </span>
      </div>
      <div className="mt-2 text-[24px] font-bold text-[#172B4D]">{item.value}</div>
      <div className="mt-1 h-8">{item.chartType === "line" ? <Sparkline values={item.chartValues} color={item.color} /> : <TinyBars values={item.chartValues} color={item.color} />}</div>
      <div className="mt-2 text-[11px] text-[#6B778C]">{item.compareText}</div>
    </div>
  );
}

export function Sparkline({ values, color }: { values: number[]; color: string }) {
  const max = Math.max(...values, 1);
  const height = 32;
  const width = 160;
  const stepX = width / (values.length - 1);

  const points = values.map((v, i) => `${i * stepX},${height - (v / max) * height}`).join(" ");

  return (
    <svg width={width} height={height} className="w-full">
      <polyline fill="none" stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" points={points} />
    </svg>
  );
}

export function TinyBars({ values, color }: { values: number[]; color: string }) {
  const max = Math.max(...values, 1);
  return (
    <div className="flex h-full items-end gap-[2px]">
      {values.map((value, index) => (
        <div
          key={index}
          className="w-full rounded-t-[2px]"
          style={{ height: `${(value / max) * 100}%`, backgroundColor: color, minHeight: "2px" }}
        />
      ))}
    </div>
  );
}
