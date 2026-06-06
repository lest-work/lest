import { BarChart3, Eye, LineChart, PieChart, Save, Table2, X } from "lucide-react";
import { useEffect, useState } from "react";

import type { ReportConfigResponse } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button, Drawer, EmptyState, Input, Select } from "@/shared/ui";

interface ReportConfigPanelProps {
  projectKey: string;
  onClose: () => void;
  onSaved: (message: string) => void;
}

const chartIcons: Record<ReportConfigResponse["report"]["chartType"], typeof BarChart3> = {
  bar: BarChart3,
  line: LineChart,
  donut: PieChart,
  table: Table2,
  number: Eye,
};

function toggleItem(items: string[], item: string) {
  return items.includes(item) ? items.filter((value) => value !== item) : [...items, item];
}

export function ReportConfigPanel({ projectKey, onClose, onSaved }: ReportConfigPanelProps) {
  const [data, setData] = useState<ReportConfigResponse>();
  const [saving, setSaving] = useState(false);
  const [nameError, setNameError] = useState("");

  useEffect(() => {
    void projectWorkspaceService.getReportConfig({ projectKey }).then(setData).catch(err => console.error("load report config:", err));
  }, [projectKey]);

  if (!data) {
    return (
      <Drawer
        open
        closable={false}
        width="min(100vw, 1120px)"
        zIndex={50}
        className="lest-frameless-drawer"
        styles={{ body: { height: "100%" }, mask: { background: "rgba(9, 30, 66, 0.25)" } }}
        onClose={onClose}
      >
        <section className="h-full w-full border-l border-[#DFE1E6] bg-white p-6">
          <div className="h-8 w-52 rounded bg-[#F1F2F4]" />
          <div className="mt-6 grid gap-4 md:grid-cols-[260px_1fr_260px]">
            <div className="h-[520px] rounded bg-[#F7F8F9]" />
            <div className="h-[520px] rounded bg-[#F7F8F9]" />
            <div className="h-[520px] rounded bg-[#F7F8F9]" />
          </div>
        </section>
      </Drawer>
    );
  }

  async function handleSave(publish: boolean) {
    if (!data) {
      return;
    }
    if (!data.report.name.trim()) {
      setNameError("报表名称不能为空");
      return;
    }
    setNameError("");
    setSaving(true);
    const response = await projectWorkspaceService.saveReportConfig({ projectKey, report: data.report, publish }).catch(err => { console.error("saveReportConfig:", err); return null; });
    if (!response) return;
    setSaving(false);
    onSaved(response.message);
    onClose();
  }

  return (
    <Drawer
      open
      closable={false}
      width="min(100vw, 1180px)"
      zIndex={50}
      className="lest-frameless-drawer"
      styles={{ body: { height: "100%" }, mask: { background: "rgba(9, 30, 66, 0.25)" } }}
      onClose={onClose}
    >
      <section className="flex h-full w-full flex-col border-l border-[#DFE1E6] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <header className="flex items-center justify-between border-b border-[#EBECF0] px-6 py-4">
          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2">
              <Input
                className={cn("h-9 max-w-[280px] rounded-[4px] border-[#DFE1E6] text-[15px] font-bold", nameError && "border-[#E34935] bg-[#FFF4F2]")}
                value={data.report.name}
                onChange={(event) => setData((current) => current ? { ...current, report: { ...current.report, name: event.target.value } } : current)}
              />
              <span className="rounded-[4px] bg-[#E9F2FF] px-2 py-1 text-[11px] font-bold text-[#0C66E4]">自定义报表</span>
            </div>
            {nameError ? <div className="mt-1 text-[12px] font-medium text-[#AE2E24]">{nameError}</div> : null}
          </div>
          <div className="flex items-center gap-2">
            <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[13px]">预览</Button>
            <Button variant="outline" loading={saving} className="h-8 rounded-[4px] bg-white text-[13px]" onClick={() => void handleSave(false)}>
              <Save className="h-4 w-4" />
              保存
            </Button>
            <Button loading={saving} className="h-8 rounded-[4px] bg-[#0C66E4] text-[13px] text-white" onClick={() => void handleSave(true)}>保存并发布</Button>
            <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[4px]" onClick={onClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
        </header>

        <div className="workspace-scrollbar grid flex-1 gap-4 overflow-y-auto bg-[#F4F5F7] p-5 xl:grid-cols-[260px_1fr_280px]">
          <aside className="space-y-4">
            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
              <h3 className="mb-3 text-[13px] font-bold text-[#172B4D]">数据源</h3>
              <div className="space-y-2">
                {data.dataSources.map((source) => (
                  <Button
                    key={source.id}
                    disabled={source.disabled}
                    className={cn("w-full rounded-[4px] border px-3 py-2 text-left text-[13px] font-semibold", data.report.dataSource === source.id ? "border-[#0C66E4] bg-[#E9F2FF] text-[#0C66E4]" : "border-[#DFE1E6] bg-white text-[#44546F] hover:bg-[#F4F5F7]", source.disabled && "cursor-not-allowed opacity-50")}
                    onClick={() => setData((current) => current ? { ...current, report: { ...current.report, dataSource: source.id } } : current)}
                  >
                    {source.label}
                    {source.helper ? <div className="mt-1 text-[11px] font-medium text-[#626F86]">{source.helper}</div> : null}
                  </Button>
                ))}
              </div>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
              <h3 className="mb-3 text-[13px] font-bold text-[#172B4D]">维度</h3>
              <div className="space-y-2">
                {data.dimensions.map((dimension) => {
                  const active = data.report.dimensions.includes(dimension.id);
                  return (
                    <Button
                      key={dimension.id}
                      className={cn("flex w-full items-center justify-between rounded-[4px] px-3 py-2 text-[13px] font-semibold", active ? "bg-[#E9F2FF] text-[#0C66E4]" : "hover:bg-[#F4F5F7] text-[#44546F]")}
                      onClick={() => setData((current) => current ? { ...current, report: { ...current.report, dimensions: toggleItem(current.report.dimensions, dimension.id) } } : current)}
                    >
                      {dimension.label}
                      <span className={cn("h-4 w-4 rounded border", active ? "border-[#0C66E4] bg-[#0C66E4]" : "border-[#C1C7D0]")} />
                    </Button>
                  );
                })}
              </div>
            </section>
          </aside>

          <main className="space-y-4">
            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
              <h3 className="mb-3 text-[13px] font-bold text-[#172B4D]">图表类型</h3>
              <div className="grid gap-3 sm:grid-cols-5">
                {data.chartTypes.map((chart) => {
                  const chartType = chart.id as ReportConfigResponse["report"]["chartType"];
                  const Icon = chartIcons[chartType];
                  const active = data.report.chartType === chart.id;
                  return (
                    <Button
                      key={chart.id}
                      className={cn("rounded-[6px] border px-3 py-4 text-center", active ? "border-[#0C66E4] bg-[#E9F2FF] text-[#0C66E4]" : "border-[#DFE1E6] bg-white text-[#44546F] hover:bg-[#F4F5F7]")}
                      onClick={() => setData((current) => current ? { ...current, report: { ...current.report, chartType } } : current)}
                    >
                      <Icon className="mx-auto h-5 w-5" />
                      <div className="mt-2 text-[12px] font-bold">{chart.label}</div>
                    </Button>
                  );
                })}
              </div>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
              <div className="mb-4 flex items-center justify-between">
                <h3 className="text-[13px] font-bold text-[#172B4D]">预览效果</h3>
                <span className="text-[12px] text-[#626F86]">基于当前配置实时预览</span>
              </div>
              {data.preview.hasData ? (
                <div className="grid gap-4 lg:grid-cols-[1.5fr_0.9fr]">
                  <div className="h-[240px] rounded-[6px] border border-[#EBECF0] bg-[#FAFBFC] p-5">
                    <div className="flex h-full items-end gap-5 border-b border-l border-[#DFE1E6] px-5 pb-8">
                      {data.preview.trend.map((point) => (
                        <div key={point.label} className="relative flex flex-1 items-end justify-center gap-2">
                          <span className="w-4 rounded-t-[3px] bg-[#0C66E4]" style={{ height: `${Math.max(point.created * 3, 8)}px` }} />
                          <span className="w-4 rounded-t-[3px] bg-[#22A06B]" style={{ height: `${Math.max(point.resolved * 3, 8)}px` }} />
                          <span className="absolute -bottom-6 text-[11px] font-medium text-[#626F86]">{point.label}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                  <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-1">
                    {data.preview.metrics.map((metric) => (
                      <div key={metric.id} className="rounded-[6px] border border-[#EBECF0] bg-[#FAFBFC] p-4">
                        <div className="text-[12px] font-bold text-[#626F86]">{metric.label}</div>
                        <div className="mt-2 text-[24px] font-bold text-[#172B4D]">{metric.value}</div>
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <EmptyState compact title="暂无预览数据" description="当前配置无法生成预览，请调整数据源、维度或筛选条件。" actionLabel="调整条件" />
              )}
            </section>
          </main>

          <aside className="space-y-4">
            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
              <h3 className="mb-3 text-[13px] font-bold text-[#172B4D]">指标</h3>
              <div className="space-y-2">
                {data.metrics.map((metric) => {
                  const active = data.report.metrics.includes(metric.id);
                  return (
                    <Button
                      key={metric.id}
                      className={cn("flex w-full items-center justify-between rounded-[4px] px-3 py-2 text-[13px] font-semibold", active ? "bg-[#E9F2FF] text-[#0C66E4]" : "hover:bg-[#F4F5F7] text-[#44546F]")}
                      onClick={() => setData((current) => current ? { ...current, report: { ...current.report, metrics: toggleItem(current.report.metrics, metric.id) } } : current)}
                    >
                      {metric.label}
                      <span className={cn("h-4 w-4 rounded border", active ? "border-[#0C66E4] bg-[#0C66E4]" : "border-[#C1C7D0]")} />
                    </Button>
                  );
                })}
              </div>
            </section>

            <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
              <h3 className="mb-3 text-[13px] font-bold text-[#172B4D]">发布范围</h3>
              <Select
                className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px] font-medium text-[#172B4D]"
                value={data.report.visibility}
                onChange={(value) => setData((current) => current ? { ...current, report: { ...current.report, visibility: value as ReportConfigResponse["report"]["visibility"] } } : current)}
              >
                <option value="private">仅自己可见</option>
                <option value="team">团队可见</option>
                <option value="public">公开报表</option>
              </Select>
            </section>
          </aside>
        </div>
      </section>
    </Drawer>
  );
}
