import { UserRound } from "lucide-react";

import type { AssigneeWorkload, DistributionDatum } from "@/api/dashboard";
import { Avatar } from "@/shared/ui";
import { cn } from "@/shared/lib/utils";
import { Panel } from "./dashboard-components";
import type { OverlayId } from "./dashboard-constants";

export function StatusDistributionPanel({
  data,
  overlay,
  onOverlayChange,
}: {
  data: DistributionDatum[];
  overlay: OverlayId;
  onOverlayChange: (id: OverlayId) => void;
}) {
  const total = data.reduce((sum, item) => sum + item.value, 0);
  const conic = data
    .reduce<{ current: number; parts: string[] }>(
      (acc, item) => {
        const next = acc.current + item.percentage;
        acc.parts.push(`${item.color} ${acc.current}% ${next}%`);
        return { current: next, parts: acc.parts };
      },
      { current: 0, parts: [] },
    )
    .parts.join(", ");

  return (
    <Panel id="status-distribution" title="问题状态分布" className="h-[264px]" overlay={overlay} onOverlayChange={onOverlayChange}>
      <div className="flex h-[218px] items-center gap-8 px-6 pb-5">
        <div className="relative flex h-[168px] w-[168px] shrink-0 items-center justify-center rounded-full" style={{ background: `conic-gradient(${conic})` }}>
          <div className="flex h-[92px] w-[92px] flex-col items-center justify-center rounded-full bg-white">
            <div className="text-[28px] font-semibold leading-none text-[#172B4D]">{total}</div>
            <div className="mt-2 text-[13px] text-[#44546F]">总数</div>
          </div>
        </div>
        <div className="min-w-[170px] flex-1 space-y-4">
          {data.map((item) => (
            <div key={item.id} className="grid grid-cols-[18px_1fr_auto] items-center gap-3 text-[14px] text-[#172B4D]">
              <span className="h-[13px] w-[13px] rounded-full" style={{ backgroundColor: item.color }} />
              <span>{item.label}</span>
              <span className="tabular-nums text-[#172B4D]">
                {item.value} ({item.percentage}%)
              </span>
            </div>
          ))}
        </div>
      </div>
    </Panel>
  );
}

export function PriorityDistributionPanel({
  data,
  overlay,
  onOverlayChange,
}: {
  data: DistributionDatum[];
  overlay: OverlayId;
  onOverlayChange: (id: OverlayId) => void;
}) {
  const max = Math.max(...data.map((item) => item.value));

  return (
    <Panel id="priority-distribution" title="问题优先级分布" className="h-[264px]" overlay={overlay} onOverlayChange={onOverlayChange}>
      <div className="px-5 pb-4">
        <div className="relative grid h-[184px] grid-cols-[36px_1fr]">
          <div className="flex flex-col justify-between pb-6 pt-1 text-[13px] text-[#6B778C]">
            {[100, 80, 60, 40, 20, 0].map((item) => (
              <span key={item}>{item}</span>
            ))}
          </div>
          <div className="relative flex items-end justify-around border-l border-[#DFE1E6] pb-6">
            <div className="pointer-events-none absolute inset-0 flex flex-col justify-between pb-6">
              {[0, 1, 2, 3, 4, 5].map((item) => (
                <span key={item} className="h-px w-full bg-[#EBECF0]" />
              ))}
            </div>
            {data.map((item) => (
              <div key={item.id} className="relative z-10 flex w-[44px] flex-col items-center justify-end">
                <span className="w-[20px] rounded-t-[3px]" style={{ height: `${Math.max((item.value / max) * 132, 12)}px`, backgroundColor: item.color }} />
              </div>
            ))}
            <div className="absolute bottom-0 left-0 right-0 grid grid-cols-5 text-center text-[13px] font-medium text-[#44546F]">
              {data.map((item) => (
                <span key={item.id}>{item.label}</span>
              ))}
            </div>
          </div>
        </div>
      </div>
    </Panel>
  );
}

export function AssigneeDistributionPanel({
  data,
  overlay,
  onOverlayChange,
}: {
  data: AssigneeWorkload[];
  overlay: OverlayId;
  onOverlayChange: (id: OverlayId) => void;
}) {
  const max = Math.max(...data.map((item) => item.value));

  return (
    <Panel id="assignee-distribution" title="问题负责人分布" className="h-[264px]" overlay={overlay} onOverlayChange={onOverlayChange}>
      <div className="space-y-3 px-5 pb-5">
        {data.map((item) => (
          <div key={item.id} className="grid grid-cols-[28px_74px_1fr_28px] items-center gap-3">
            {item.avatar ? (
              <Avatar name={item.name} imageSrc={item.avatar} className="h-6 w-6" />
            ) : (
              <span className="flex h-6 w-6 items-center justify-center rounded-full border border-[#DFE1E6] bg-[#F4F5F7] text-[#44546F]">
                <UserRound className="h-4 w-4" />
              </span>
            )}
            <span className="truncate text-[14px] font-medium text-[#172B4D]">{item.name}</span>
            <span className="h-[6px] overflow-hidden rounded-full bg-[#EBECF0]">
              <span className="block h-full rounded-full" style={{ width: `${Math.max((item.value / max) * 100, 8)}%`, backgroundColor: item.color }} />
            </span>
            <span className="text-right text-[14px] font-medium tabular-nums text-[#172B4D]">{item.value}</span>
          </div>
        ))}
      </div>
    </Panel>
  );
}
