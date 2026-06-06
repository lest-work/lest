import { Download, Maximize2, MoreHorizontal, Plus, ZoomIn, ZoomOut } from "lucide-react";
import { useEffect, useState } from "react";

import type { RoadmapResponse } from "@/api/project";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import { projectWorkspaceService } from "@/api/project";
import { Button } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

const months = ["4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"];

function monthOffset(date: string) {
  const month = Number(date.slice(5, 7));
  return Math.max(0, month - 4);
}

export function RoadmapPage({ onNavigate }: { onNavigate: (page: WorkspacePageKey) => void }) {
  const [data, setData] = useState<RoadmapResponse>();
  const [selectedId, setSelectedId] = useState("sp12");

  useEffect(() => {
    void projectWorkspaceService.getRoadmap({ projectKey: "alpha-platform", view: "timeline", from: "2026-04-01", to: "2026-12-31" }).then(setData);
  }, []);

  const selected = data?.iterations.find((item) => item.id === selectedId);

  return (
    <ProjectWorkspaceShell
      activePage="roadmap"
      title="路线图"
      subtitle="按时间线展示项目、迭代、里程碑和交付计划。"
      onNavigate={onNavigate}
      actions={
        <>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] px-4 text-white">
            <Plus className="h-4 w-4" />
            新建计划
          </Button>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white">
            <Download className="h-4 w-4" />
            导出
          </Button>
          <Button variant="outline" size="icon" className="h-9 w-10 rounded-[4px] bg-white">
            <MoreHorizontal className="h-4 w-4" />
          </Button>
        </>
      }
    >
      <div className="mb-4 flex flex-wrap items-center gap-3">
        {["所有项目", "2024 Q2 - 2024 Q4", "标签：全部", "按开始时间"].map((item) => (
          <Button key={item} className="h-9 rounded-[4px] border border-[#DFE1E6] bg-white px-4 text-[13px] font-medium text-[#172B4D] hover:bg-[#F4F5F7]">
            {item}
          </Button>
        ))}
        <div className="ml-auto flex overflow-hidden rounded-[4px] border border-[#DFE1E6] bg-white">
          {["时间线", "季度", "月份", "列表", "看板"].map((item, index) => (
            <Button key={item} className={`h-9 px-4 text-[13px] ${index === 0 ? "bg-[#0C66E4] font-semibold text-white" : "text-[#44546F] hover:bg-[#F4F5F7]"}`}>
              {item}
            </Button>
          ))}
        </div>
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_320px]">
        <section className="overflow-hidden rounded-[8px] border border-[#DFE1E6] bg-white shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
          <div className="grid grid-cols-[210px_1fr] border-b border-[#EBECF0]">
            <div className="px-4 py-4 text-[14px] font-bold text-[#172B4D]">项目与迭代</div>
            <div className="grid grid-cols-9 border-l border-[#EBECF0] text-center text-[12px] font-semibold text-[#44546F]">
              {months.map((month) => (
                <div key={month} className="border-r border-[#EBECF0] py-3 last:border-r-0">
                  {month}
                </div>
              ))}
            </div>
          </div>

          <div className="relative grid grid-cols-[210px_1fr]">
            <div className="space-y-3 border-r border-[#EBECF0] px-4 py-4">
              {data?.iterations.map((item) => (
                <Button
                  key={item.id}
                  className="flex w-full items-center justify-between rounded-[4px] px-2 py-2 text-left hover:bg-[#F4F5F7]"
                  onClick={() => setSelectedId(item.id)}
                >
                  <span className="truncate text-[13px] font-semibold text-[#172B4D]">{item.name.split(" ").slice(0, 2).join(" ")}</span>
                  <span className="text-[12px] text-[#44546F]">{item.progress}%</span>
                </Button>
              ))}
            </div>
            <div className="relative min-h-[430px] bg-[linear-gradient(90deg,#EBECF0_1px,transparent_1px)] bg-[length:11.111%_100%] px-4 py-4">
              <div className="absolute bottom-0 top-0 w-px border-l border-dashed border-[#FF5630]" style={{ left: "24%" }}>
                <span className="absolute -top-3 left-1/2 -translate-x-1/2 rounded-full bg-[#FF5630] px-2 py-0.5 text-[11px] font-bold text-white">5月20日</span>
              </div>
              {data?.milestones.map((item) => (
                <div key={item.id} className="absolute top-6 flex -translate-x-1/2 flex-col items-center gap-1" style={{ left: `${(monthOffset(item.date) / 8) * 100}%` }}>
                  <span className="h-3 w-3 rotate-45" style={{ backgroundColor: item.color }} />
                  <span className="whitespace-nowrap text-[11px] font-semibold text-[#172B4D]">{item.name}</span>
                </div>
              ))}
              <div className="mt-16 space-y-5">
                {data?.iterations.map((item) => {
                  const left = (monthOffset(item.startDate) / 8) * 100;
                  const width = Math.max(((monthOffset(item.endDate) - monthOffset(item.startDate) + 1) / 9) * 100, 18);
                  return (
                    <Button
                      key={item.id}
                      className="relative block h-[34px] rounded-[6px] border px-3 text-left text-[12px] font-semibold shadow-sm"
                      style={{
                        marginLeft: `${left}%`,
                        width: `${width}%`,
                        backgroundColor: `${item.color}18`,
                        borderColor: item.color,
                        color: "#172B4D",
                      }}
                      onClick={() => setSelectedId(item.id)}
                    >
                      <span>{item.name}</span>
                      <span className="absolute bottom-0 left-0 h-1 rounded-full" style={{ width: `${item.progress}%`, backgroundColor: item.color }} />
                      <span className="absolute right-2 top-2 text-[#44546F]">{item.progress}%</span>
                    </Button>
                  );
                })}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-3 border-t border-[#EBECF0] px-4 py-3">
            <div className="h-8 flex-1 rounded-[6px] border border-[#B3D4FF] bg-[#E9F2FF]" />
            <Button variant="outline" size="icon" className="h-8 w-8 rounded-[4px] bg-white">
              <ZoomOut className="h-4 w-4" />
            </Button>
            <span className="text-[12px] font-semibold text-[#44546F]">100%</span>
            <Button variant="outline" size="icon" className="h-8 w-8 rounded-[4px] bg-white">
              <ZoomIn className="h-4 w-4" />
            </Button>
            <Button variant="outline" size="icon" className="h-8 w-8 rounded-[4px] bg-white">
              <Maximize2 className="h-4 w-4" />
            </Button>
          </div>
        </section>

        <aside className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
          <div className="mb-4 flex items-start justify-between">
            <div>
              <h2 className="text-[18px] font-semibold text-[#172B4D]">{selected?.name ?? "SP12 用户认证功能优化"}</h2>
              <span className="mt-2 inline-flex rounded-[3px] bg-[#DEEBFF] px-2 py-1 text-[12px] font-bold text-[#0C66E4]">进行中</span>
            </div>
            <Button className="text-[#44546F]">×</Button>
          </div>
          {selected ? (
            <div className="space-y-4 text-[13px]">
              {[
                ["项目", selected.projectName],
                ["迭代", selected.name.split(" ")[0]],
                ["时间", `${selected.startDate} - ${selected.endDate}`],
                ["负责人", selected.owner],
                ["团队成员", "8 人"],
              ].map(([label, value]) => (
                <div key={label} className="grid grid-cols-[72px_1fr] gap-3">
                  <span className="text-[#626F86]">{label}</span>
                  <span className="font-medium text-[#172B4D]">{value}</span>
                </div>
              ))}
              <div>
                <div className="mb-2 text-[#626F86]">进度</div>
                <div className="h-2 rounded-full bg-[#EBECF0]">
                  <div className="h-full rounded-full bg-[#0C66E4]" style={{ width: `${selected.progress}%` }} />
                </div>
              </div>
              <div className="grid grid-cols-4 gap-2 pt-2">
                {[
                  ["总问题", 58],
                  ["已完成", 41],
                  ["进行中", 17],
                  ["阻塞", 2],
                ].map(([label, value]) => (
                  <div key={label} className="rounded-[6px] border border-[#DFE1E6] p-3 text-center">
                    <div className="text-[12px] text-[#626F86]">{label}</div>
                    <div className="mt-1 text-[20px] font-semibold text-[#172B4D]">{value}</div>
                  </div>
                ))}
              </div>
              <div className="flex flex-wrap gap-2 pt-2">
                <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[12px]">编辑迭代</Button>
                <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[12px]">调整时间</Button>
                <Button className="h-8 rounded-[4px] bg-[#0C66E4] text-[12px] text-white">添加里程碑</Button>
              </div>
            </div>
          ) : null}
        </aside>
      </div>
    </ProjectWorkspaceShell>
  );
}
