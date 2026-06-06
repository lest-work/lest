import { ChevronDown, Save, X } from "lucide-react";
import { useMemo, useState } from "react";

import type { ReportFilterOptionsResponse, ReportFilterState, ReportSavedFilter } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Checkbox, Input, Select } from "@/shared/ui";

interface ReportFilterPanelProps {
  value: ReportFilterState;
  options: ReportFilterOptionsResponse;
  onApply: (filters: ReportFilterState) => void;
  onClose: () => void;
}

const statusLabel: Record<string, string> = {
  todo: "未开始",
  "in-progress": "进行中",
  review: "已评审",
  done: "已完成",
  closed: "已关闭",
};

function toggleItem(items: string[], item: string) {
  return items.includes(item) ? items.filter((value) => value !== item) : [...items, item];
}

export function ReportFilterPanel({ value, options, onApply, onClose }: ReportFilterPanelProps) {
  const [draft, setDraft] = useState<ReportFilterState>(value);
  const [saveName, setSaveName] = useState("");
  const [validationMode, setValidationMode] = useState(false);

  const selectedAssignees = useMemo(
    () => options.assignees.filter((item) => draft.assigneeIds.includes(item.id)),
    [draft.assigneeIds, options.assignees],
  );

  const hasDateError = validationMode && (draft.dateRange?.from ?? "") > (draft.dateRange?.to ?? "");

  function applySavedFilter(filter: ReportSavedFilter) {
    setDraft(filter.filters);
  }

  function handleApply() {
    setValidationMode(true);
    if ((draft.dateRange?.from ?? "") > (draft.dateRange?.to ?? "")) {
      return;
    }
    onApply(draft);
    onClose();
  }

  return (
    <section className="absolute left-0 top-11 z-40 w-[860px] max-w-[calc(100vw-48px)] rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_12px_32px_rgba(9,30,66,0.22)]">
      <header className="flex items-center justify-between border-b border-[#EBECF0] px-5 py-4">
        <div>
          <h2 className="text-[15px] font-bold text-[#172B4D]">基础筛选</h2>
          <p className="mt-1 text-[12px] text-[#626F86]">按项目、迭代、时间范围、成员与自定义字段筛选报表数据。</p>
        </div>
        <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[4px]" onClick={onClose}>
          <X className="h-4 w-4" />
        </Button>
      </header>

      <div className="grid gap-6 px-5 py-5 lg:grid-cols-[1fr_280px]">
        <div className="grid gap-4 md:grid-cols-2">
          <label className="space-y-1.5 text-[12px] font-bold text-[#44546F]">
            项目
            <Select
              className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px] font-medium text-[#172B4D]"
              value={draft.projectKey}
              onChange={(value) => setDraft((current) => ({ ...current, projectKey: String(value) }))}
            >
              {options.projects.map((project) => (
                <option key={project.id} value={project.id} disabled={project.disabled}>{project.label}</option>
              ))}
            </Select>
          </label>
          <label className="space-y-1.5 text-[12px] font-bold text-[#44546F]">
            迭代 / Sprint
            <Select
              className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px] font-medium text-[#172B4D]"
              value={draft.sprintId ?? "all"}
              onChange={(value) => setDraft((current) => ({ ...current, sprintId: String(value) }))}
            >
              {options.sprints.map((sprint) => (
                <option key={sprint.id} value={sprint.id}>{sprint.label}</option>
              ))}
            </Select>
          </label>
          <label className="space-y-1.5 text-[12px] font-bold text-[#44546F]">
            开始日期
            <Input
              type="date"
              className={cn("h-9 rounded-[4px] border-[#DFE1E6] text-[13px]", hasDateError && "border-[#E34935] bg-[#FFF4F2]")}
              value={draft.dateRange?.from ?? ""}
              onChange={(event) => setDraft((current) => ({ ...current, dateRange: { ...current.dateRange, from: event.target.value } }))}
            />
          </label>
          <label className="space-y-1.5 text-[12px] font-bold text-[#44546F]">
            结束日期
            <Input
              type="date"
              className={cn("h-9 rounded-[4px] border-[#DFE1E6] text-[13px]", hasDateError && "border-[#E34935] bg-[#FFF4F2]")}
              value={draft.dateRange?.to ?? ""}
              onChange={(event) => setDraft((current) => ({ ...current, dateRange: { ...current.dateRange, to: event.target.value } }))}
            />
          </label>
          {hasDateError ? <div className="md:col-span-2 rounded-[4px] border border-[#FFD5CC] bg-[#FFF4F2] px-3 py-2 text-[12px] font-medium text-[#AE2E24]">开始日期不能晚于结束日期。</div> : null}

          <div className="md:col-span-2">
            <div className="mb-2 text-[12px] font-bold text-[#44546F]">状态</div>
            <div className="flex flex-wrap gap-2">
              {options.statuses.map((status) => {
                const active = draft.statuses.includes(status.id as ReportFilterState["statuses"][number]);
                return (
                  <Button
                    key={status.id}
                    className={cn("h-8 rounded-[4px] border px-3 text-[12px] font-bold", active ? "border-[#0C66E4] bg-[#E9F2FF] text-[#0C66E4]" : "border-[#DFE1E6] bg-white text-[#44546F]")}
                    onClick={() => setDraft((current) => ({ ...current, statuses: toggleItem(current.statuses, status.id) as ReportFilterState["statuses"] }))}
                  >
                    {statusLabel[status.id] ?? status.label}
                  </Button>
                );
              })}
            </div>
          </div>

          <div className="md:col-span-2">
            <div className="mb-2 text-[12px] font-bold text-[#44546F]">负责人</div>
            <div className="grid gap-2 sm:grid-cols-2">
              {options.assignees.map((assignee) => {
                const active = draft.assigneeIds.includes(assignee.id);
                return (
                  <Button
                    key={assignee.id}
                    className={cn("flex h-9 items-center justify-between rounded-[4px] border px-3 text-left text-[13px]", active ? "border-[#0C66E4] bg-[#E9F2FF]" : "border-[#DFE1E6] bg-white hover:bg-[#F4F5F7]")}
                    onClick={() => setDraft((current) => ({ ...current, assigneeIds: toggleItem(current.assigneeIds, assignee.id) }))}
                  >
                    <span className="flex items-center gap-2 font-medium text-[#172B4D]">
                      <Avatar name={assignee.label} imageSrc={assignee.avatar} className="h-5 w-5 text-[10px]" />
                      {assignee.label}
                    </span>
                    <span className={cn("h-4 w-4 rounded-[3px] border", active ? "border-[#0C66E4] bg-[#0C66E4]" : "border-[#C1C7D0]")} />
                  </Button>
                );
              })}
            </div>
          </div>
        </div>

        <aside className="space-y-4">
          <section className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
            <div className="mb-3 flex items-center justify-between">
              <h3 className="text-[13px] font-bold text-[#172B4D]">筛选方案</h3>
              <ChevronDown className="h-4 w-4 text-[#626F86]" />
            </div>
            <div className="space-y-2">
              {options.savedFilters.map((filter) => (
                <Button key={filter.id} className="w-full rounded-[4px] bg-white px-3 py-2 text-left hover:bg-[#E9F2FF]" onClick={() => applySavedFilter(filter)}>
                  <div className="text-[12px] font-bold text-[#172B4D]">{filter.name}</div>
                  <div className="mt-0.5 truncate text-[11px] text-[#626F86]">{filter.description}</div>
                </Button>
              ))}
            </div>
          </section>
          <section className="rounded-[6px] border border-[#DFE1E6] bg-white p-4">
            <h3 className="text-[13px] font-bold text-[#172B4D]">另存为方案</h3>
            <Input className="mt-3 h-8 rounded-[4px] border-[#DFE1E6] text-[12px]" placeholder="输入方案名称" value={saveName} onChange={(event) => setSaveName(event.target.value)} />
            <label className="mt-3 flex items-center gap-2 text-[12px] font-medium text-[#44546F]">
              <Checkbox />
              设为默认方案
            </label>
          </section>
          <div className="rounded-[6px] border border-[#CCE0FF] bg-[#F0F6FF] p-3 text-[12px] leading-5 text-[#0C66E4]">
            已选择 {selectedAssignees.length} 位负责人、{draft.statuses.length} 个状态、{draft.priorities.length} 个优先级。
          </div>
        </aside>
      </div>

      <footer className="flex items-center justify-between border-t border-[#EBECF0] bg-[#FAFBFC] px-5 py-4">
        <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[13px]" onClick={() => setDraft({ ...value, labels: ["__empty__"], statuses: ["closed"] })}>
          预览空态
        </Button>
        <div className="flex items-center gap-3">
          <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[13px]" onClick={onClose}>取消</Button>
          <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[13px]" onClick={() => setSaveName(saveName || "我的筛选方案")}>
            <Save className="h-4 w-4" />
            另存为方案
          </Button>
          <Button className="h-8 rounded-[4px] bg-[#0C66E4] text-[13px] text-white" onClick={handleApply}>应用</Button>
        </div>
      </footer>
    </section>
  );
}
