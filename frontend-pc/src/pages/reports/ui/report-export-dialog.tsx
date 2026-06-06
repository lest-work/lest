import { FileDown, FileSpreadsheet, FileText, Image, Presentation, X } from "lucide-react";
import { useState } from "react";

import type { ExportReportRequest, ReportFilterState, ReportsRequest } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { Button, Checkbox, Input, Modal, Radio } from "@/shared/ui";

interface ReportExportDialogProps {
  reportType: ReportsRequest["reportType"];
  filters: ReportFilterState;
  onClose: () => void;
  onExported: (message: string) => void;
}

const formatOptions: Array<{ id: ExportReportRequest["format"]; label: string; helper: string; icon: typeof FileSpreadsheet }> = [
  { id: "xlsx", label: "Excel", helper: "保留表格格式", icon: FileSpreadsheet },
  { id: "csv", label: "CSV", helper: "纯文本数据", icon: FileText },
  { id: "pdf", label: "PDF", helper: "适合打印分享", icon: FileDown },
  { id: "png", label: "图片", helper: "导出图表快照", icon: Image },
  { id: "pptx", label: "PPT", helper: "用于汇报材料", icon: Presentation },
];

export function ReportExportDialog({ reportType, filters, onClose, onExported }: ReportExportDialogProps) {
  const [fileName, setFileName] = useState("缺陷趋势分析_20260606");
  const [format, setFormat] = useState<ExportReportRequest["format"]>("xlsx");
  const [scope, setScope] = useState<ExportReportRequest["scope"]>("all_results");
  const [includeCharts, setIncludeCharts] = useState(true);
  const [includeDetails, setIncludeDetails] = useState(true);
  const [includeFilterDescription, setIncludeFilterDescription] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleExport() {
    setError("");
    setLoading(true);
    const response = await projectWorkspaceService.exportReport({
      projectKey: filters.projectKey,
      reportType,
      fileName,
      format,
      scope,
      dateRange: scope === "custom_range" ? filters.dateRange : undefined,
      includeCharts,
      includeDetails,
      includeFilterDescription,
      filters,
    }).catch(err => { console.error("exportReport:", err); return null; });
    setLoading(false);
    if (!response) return;
    if (response.status === "failed") {
      setError(response.message);
      return;
    }
    onExported(response.message);
    onClose();
  }

  return (
    <Modal
      open
      closable={false}
      footer={null}
      width={620}
      zIndex={50}
      className="lest-frameless-modal"
      styles={{ mask: { background: "rgba(9, 30, 66, 0.3)" } }}
      onCancel={onClose}
    >
      <section className="w-full rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <header className="flex items-center justify-between border-b border-[#EBECF0] px-6 py-4">
          <h2 className="text-[16px] font-bold text-[#172B4D]">导出报表</h2>
          <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[4px]" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </header>
        <div className="space-y-5 px-6 py-5">
          <label className="block space-y-1.5 text-[12px] font-bold text-[#44546F]">
            文件名称
            <Input className="h-9 rounded-[4px] border-[#DFE1E6] text-[13px]" value={fileName} maxLength={100} onChange={(event) => setFileName(event.target.value)} />
          </label>
          {error ? <div className="rounded-[4px] border border-[#FFD5CC] bg-[#FFF4F2] px-3 py-2 text-[12px] font-medium text-[#AE2E24]">{error}</div> : null}

          <div>
            <div className="mb-2 text-[12px] font-bold text-[#44546F]">导出格式</div>
            <div className="grid gap-3 sm:grid-cols-5">
              {formatOptions.map((item) => {
                const Icon = item.icon;
                const active = format === item.id;
                return (
                  <Button
                    key={item.id}
                    className={`rounded-[6px] border p-3 text-center ${active ? "border-[#0C66E4] bg-[#E9F2FF] text-[#0C66E4]" : "border-[#DFE1E6] bg-white text-[#44546F] hover:bg-[#F4F5F7]"}`}
                    onClick={() => setFormat(item.id)}
                  >
                    <Icon className="mx-auto h-5 w-5" />
                    <div className="mt-2 text-[12px] font-bold">{item.label}</div>
                    <div className="mt-1 text-[10px] text-[#626F86]">{item.helper}</div>
                  </Button>
                );
              })}
            </div>
          </div>

          <div className="grid gap-3 sm:grid-cols-3">
            {[
              ["current_view", "当前页数据"],
              ["all_results", "所有数据"],
              ["custom_range", "自定义范围"],
            ].map(([id, label]) => (
              <label key={id} className="flex items-center gap-2 rounded-[4px] border border-[#DFE1E6] px-3 py-2 text-[13px] font-medium text-[#172B4D]">
                <Radio checked={scope === id} onChange={() => setScope(id as ExportReportRequest["scope"])} />
                {label}
              </label>
            ))}
          </div>

          <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
            <div className="mb-3 text-[12px] font-bold text-[#44546F]">高级选项</div>
            <div className="grid gap-2 text-[13px] font-medium text-[#44546F]">
              <label className="flex items-center gap-2"><Checkbox checked={includeCharts} onChange={(event) => setIncludeCharts(event.target.checked)} />包含图表截图</label>
              <label className="flex items-center gap-2"><Checkbox checked={includeDetails} onChange={(event) => setIncludeDetails(event.target.checked)} />包含明细数据</label>
              <label className="flex items-center gap-2"><Checkbox checked={includeFilterDescription} onChange={(event) => setIncludeFilterDescription(event.target.checked)} />包含筛选条件说明</label>
            </div>
          </div>
        </div>
        <footer className="flex items-center justify-end gap-3 border-t border-[#EBECF0] bg-[#FAFBFC] px-6 py-4">
          <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[13px]" onClick={onClose}>取消</Button>
          <Button loading={loading} className="h-8 rounded-[4px] bg-[#0C66E4] text-[13px] text-white" onClick={handleExport}>导出</Button>
        </footer>
      </section>
    </Modal>
  );
}
