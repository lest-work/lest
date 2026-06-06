import { Download, Filter, Plus, RefreshCw, Share2, SlidersHorizontal } from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";

import type {
  ReportDrilldownRequest,
  ReportFilterOptionsResponse,
  ReportFilterState,
  ReportsRequest,
  ReportsResponse,
} from "@/api/project";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button, EmptyState, ToastViewport, type ToastMessage } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

import { ReportConfigPanel } from "./report-config-panel";
import { ReportDrilldownDrawer } from "./report-drilldown-drawer";
import { ReportExportDialog } from "./report-export-dialog";
import { ReportFilterPanel } from "./report-filter-panel";

const projectKey = "alpha-platform";
const reportTabs: Array<{ id: ReportsRequest["reportType"]; label: string }> = [
  { id: "overview", label: "概览" },
  { id: "efficiency", label: "效率" },
  { id: "quality", label: "质量" },
  { id: "cycle", label: "周期" },
];

function mergeFilters(base: ReportFilterState, patch: Partial<ReportFilterState>): ReportFilterState {
  return {
    ...base,
    ...patch,
    dateRange: patch.dateRange ?? base.dateRange,
    customFields: {
      ...base.customFields,
      ...(patch.customFields ?? {}),
    },
  };
}

function filterSummary(filters?: ReportFilterState) {
  if (!filters) {
    return ["最近 30 天", "全部负责人", "全部状态"];
  }
  return [
    `${filters.dateRange?.from} 至 ${filters.dateRange?.to}`,
    filters.assigneeIds.length ? `${filters.assigneeIds.length} 位负责人` : "全部负责人",
    filters.statuses.length ? `${filters.statuses.length} 个状态` : "全部状态",
  ];
}

export function ReportsPage({ onNavigate }: { onNavigate: (page: WorkspacePageKey) => void }) {
  const [data, setData] = useState<ReportsResponse>();
  const [filterOptions, setFilterOptions] = useState<ReportFilterOptionsResponse>();
  const [activeTab, setActiveTab] = useState<ReportsRequest["reportType"]>("overview");
  const [filters, setFilters] = useState<ReportFilterState>();
  const [filterOpen, setFilterOpen] = useState(false);
  const [exportOpen, setExportOpen] = useState(false);
  const [configOpen, setConfigOpen] = useState(false);
  const [drilldownRequest, setDrilldownRequest] = useState<ReportDrilldownRequest>();
  const [loading, setLoading] = useState(false);
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const loadReport = useCallback(async (nextFilters?: ReportFilterState, nextTab = activeTab) => {
    setLoading(true);
    try {
      const response = await projectWorkspaceService.getReports({
        projectKey,
        reportType: nextTab,
        datePreset: "last_30_days",
        compareWithPrevious: true,
        filters: nextFilters ?? filters as ReportFilterState,
      });
      setData(response);
      setFilters(response.activeFilters);
    } finally {
      setLoading(false);
    }
  }, [activeTab]);

  useEffect(() => {
    void Promise.all([
      projectWorkspaceService.getReportFilterOptions({ projectKey, includeSavedFilters: true }).then(setFilterOptions),
      loadReport(),
    ]);
  }, [loadReport]);

  const maxTrend = Math.max(...(data?.trend.flatMap((item) => [item.created, item.resolved]) ?? [1]));
  const summaries = useMemo(() => filterSummary(filters), [filters]);

  function pushToast(message: Omit<ToastMessage, "id">) {
    setToasts((items) => [{ ...message, id: `${Date.now()}-${items.length}` }, ...items].slice(0, 4));
  }

  async function handleTabChange(tab: ReportsRequest["reportType"]) {
    setActiveTab(tab);
    await loadReport(filters, tab);
  }

  async function handleApplyFilters(nextFilters: ReportFilterState) {
    await loadReport(nextFilters);
    pushToast({ title: "筛选已应用", description: "报表数据已按最新条件刷新。", tone: "success" });
  }

  async function handleQuickFilter(filterId: string) {
    const quick = data?.quickFilters?.find((item) => item.id === filterId) ?? filterOptions?.quickFilters?.find((item) => item.id === filterId);
    if (!quick || !filters) {
      return;
    }
    const nextFilters = mergeFilters(filters, quick.filters ?? {});
    await handleApplyFilters(nextFilters);
  }

  function openTrendDrilldown(label: string) {
    if (!filters) {
      return;
    }
    setDrilldownRequest({
      projectKey,
      chartId: "trend",
      dimension: "date",
      dimensionValue: label,
      filters,
      page: 1,
      pageSize: 10,
      sortBy: "createdAt",
      sortOrder: "desc",
    });
  }

  function openDistributionDrilldown(issueType: string) {
    if (!filters) {
      return;
    }
    setDrilldownRequest({
      projectKey,
      chartId: "distribution",
      dimension: "issueType",
      dimensionValue: issueType,
      filters,
      page: 1,
      pageSize: 10,
      sortBy: "priority",
      sortOrder: "desc",
    });
  }

  return (
    <ProjectWorkspaceShell
      activePage="reports"
      title="报表"
      subtitle="分析项目问题趋势、团队效率、交付质量和周期数据。"
      onNavigate={onNavigate}
      actions={
        <>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" onClick={() => setConfigOpen(true)}>
            <Plus className="h-4 w-4" />
            新建报表
          </Button>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white">
            <Share2 className="h-4 w-4" />
            分享
          </Button>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white" onClick={() => setExportOpen(true)} disabled={!filters}>
            <Download className="h-4 w-4" />
            导出
          </Button>
        </>
      }
    >
      <ToastViewport messages={toasts} onClose={(id) => setToasts((items) => items.filter((item) => item.id !== id))} />

      <div className="relative mb-4 flex flex-wrap items-center gap-3">
        <div className="flex overflow-hidden rounded-[4px] border border-[#DFE1E6] bg-white">
          {reportTabs.map((tab) => (
            <Button
              key={tab.id}
              className={cn("h-9 px-4 text-[13px]", activeTab === tab.id ? "bg-[#0C66E4] font-semibold text-white" : "text-[#44546F] hover:bg-[#F4F5F7]")}
              onClick={() => void handleTabChange(tab.id)}
            >
              {tab.label}
            </Button>
          ))}
        </div>
        <Button className="inline-flex h-9 items-center gap-2 rounded-[4px] border border-[#0C66E4] bg-[#E9F2FF] px-4 text-[13px] font-bold text-[#0C66E4]" onClick={() => setFilterOpen((value) => !value)}>
          <Filter className="h-4 w-4" />
          筛选条件
        </Button>
        {summaries.map((item) => (
          <Button key={item} className="h-9 rounded-[4px] border border-[#DFE1E6] bg-white px-4 text-[13px] font-medium text-[#172B4D] hover:bg-[#F4F5F7]">
            {item}
          </Button>
        ))}
        <Button variant="outline" size="icon" className="h-9 w-9 rounded-[4px] bg-white" loading={loading} onClick={() => void loadReport(filters)}>
          <RefreshCw className="h-4 w-4" />
        </Button>
        {filterOpen && filterOptions && filters ? (
          <ReportFilterPanel value={filters} options={filterOptions} onApply={handleApplyFilters} onClose={() => setFilterOpen(false)} />
        ) : null}
      </div>

      <div className="mb-4 grid gap-3 xl:grid-cols-[1fr_360px]">
        <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-[14px] font-bold text-[#172B4D]">快捷筛选</h2>
            <Button className="text-[12px] font-semibold text-[#0C66E4]" onClick={() => setFilterOpen(true)}>管理方案</Button>
          </div>
          <div className="flex flex-wrap gap-2">
            {(data?.quickFilters ?? filterOptions?.quickFilters ?? []).map((quick) => (
              <Button key={quick.id} className="rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-2 text-left hover:border-[#0C66E4] hover:bg-[#E9F2FF]" onClick={() => void handleQuickFilter(quick.id)}>
                <div className="text-[12px] font-bold text-[#172B4D]">{quick.label}</div>
                <div className="mt-0.5 text-[11px] text-[#626F86]">{quick.description}</div>
              </Button>
            ))}
          </div>
        </section>
        <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-[14px] font-bold text-[#172B4D]">报表配置</h2>
            <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[12px]" onClick={() => setConfigOpen(true)}>
              <SlidersHorizontal className="h-4 w-4" />
              自定义
            </Button>
          </div>
          <div className="grid grid-cols-2 gap-2 text-[12px] text-[#626F86]">
            <div className="rounded-[4px] bg-[#FAFBFC] p-3">
              <div className="font-bold text-[#172B4D]">数据源</div>
              <div className="mt-1">Jira 项目问题</div>
            </div>
            <div className="rounded-[4px] bg-[#FAFBFC] p-3">
              <div className="font-bold text-[#172B4D]">维度</div>
              <div className="mt-1">状态、类型</div>
            </div>
          </div>
        </section>
      </div>

      <div className="grid gap-4 xl:grid-cols-4">
        {data?.metrics.map((metric) => (
          <Button
            key={metric.id}
            className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 text-left shadow-[0_1px_2px_rgba(9,30,66,0.08)] hover:border-[#0C66E4] hover:shadow-[0_4px_12px_rgba(9,30,66,0.12)]"
            onClick={() => filters && setDrilldownRequest({ projectKey, chartId: "metric", dimension: "metric", dimensionValue: metric.label, metricId: metric.id, filters, page: 1, pageSize: 10 })}
          >
            <div className="text-[13px] font-semibold text-[#626F86]">{metric.label}</div>
            <div className="mt-3 text-[32px] font-semibold text-[#172B4D]">{metric.value}</div>
            <div className={cn("mt-2 text-[13px] font-semibold", metric.trend === "up" ? "text-[#22A06B]" : "text-[#0C66E4]")}>{metric.change} 较上周期</div>
          </Button>
        ))}
      </div>

      {data && !data.hasData ? (
        <div className="mt-4 rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
          <EmptyState
            title={data.emptyState?.title ?? "暂无数据"}
            description={data.emptyState?.description}
            actionLabel={data.emptyState?.actionLabel ?? "调整筛选"}
            secondaryActionLabel="恢复默认"
            onAction={() => setFilterOpen(true)}
            onSecondaryAction={() => filterOptions?.savedFilters[0] && void handleApplyFilters(filterOptions.savedFilters[0].filters)}
          />
        </div>
      ) : (
        <div className="mt-4 grid gap-4 xl:grid-cols-[1.4fr_0.8fr]">
          <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
            <div className="mb-5 flex items-center justify-between">
              <h2 className="text-[15px] font-bold text-[#172B4D]">问题趋势</h2>
              <div className="flex items-center gap-4 text-[12px] text-[#626F86]">
                <span className="inline-flex items-center gap-1"><i className="h-2.5 w-2.5 rounded-full bg-[#0C66E4]" />新建</span>
                <span className="inline-flex items-center gap-1"><i className="h-2.5 w-2.5 rounded-full bg-[#22A06B]" />解决</span>
              </div>
            </div>
            <div className="flex h-[280px] items-end gap-6 border-l border-b border-[#DFE1E6] px-6 pb-8">
              {data?.trend.map((point) => (
                <Button key={point.label} className="relative flex flex-1 items-end justify-center gap-2 rounded-t-[4px] hover:bg-[#F4F5F7]" onClick={() => openTrendDrilldown(point.label)}>
                  <span className="w-5 rounded-t-[3px] bg-[#0C66E4]" style={{ height: `${Math.max((point.created / maxTrend) * 220, 8)}px` }} />
                  <span className="w-5 rounded-t-[3px] bg-[#22A06B]" style={{ height: `${Math.max((point.resolved / maxTrend) * 220, 8)}px` }} />
                  <span className="absolute -bottom-6 text-[12px] font-medium text-[#626F86]">{point.label}</span>
                </Button>
              ))}
            </div>
          </section>

          <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
            <h2 className="mb-5 text-[15px] font-bold text-[#172B4D]">类型分布</h2>
            <div className="space-y-4">
              {data?.distributions.map((item) => (
                <Button key={item.id} className="block w-full rounded-[4px] p-1 text-left hover:bg-[#F4F5F7]" onClick={() => openDistributionDrilldown(item.id)}>
                  <div className="mb-1 flex items-center justify-between text-[13px]">
                    <span className="font-semibold text-[#172B4D]">{item.label}</span>
                    <span className="text-[#626F86]">{item.value}</span>
                  </div>
                  <div className="h-2 rounded-full bg-[#EBECF0]">
                    <div className="h-full rounded-full" style={{ width: `${Math.min(item.value, 100)}%`, backgroundColor: item.color }} />
                  </div>
                </Button>
              ))}
            </div>
          </section>
        </div>
      )}

      <ReportDrilldownDrawer request={drilldownRequest} onClose={() => setDrilldownRequest(undefined)} />
      {exportOpen && filters ? (
        <ReportExportDialog reportType={activeTab} filters={filters} onClose={() => setExportOpen(false)} onExported={(message) => pushToast({ title: "导出已开始", description: message, tone: "success" })} />
      ) : null}
      {configOpen ? (
        <ReportConfigPanel projectKey={projectKey} onClose={() => setConfigOpen(false)} onSaved={(message) => pushToast({ title: "保存成功", description: message, tone: "success" })} />
      ) : null}
    </ProjectWorkspaceShell>
  );
}
