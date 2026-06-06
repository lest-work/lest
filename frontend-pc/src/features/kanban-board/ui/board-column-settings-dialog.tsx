import { AlertCircle, Check, Lock, Settings2, Zap } from "lucide-react";
import { useEffect, useState } from "react";

import type { BoardColumnSettingsResponse, UpdateBoardColumnSettingsRequest } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button, Checkbox, Input, Select, Textarea } from "@/shared/ui";
import { BoardDialogFrame } from "./board-dialog-frame";

export interface BoardColumnSettingsDialogProps {
  projectKey: string;
  columnId: UpdateBoardColumnSettingsRequest["columnId"];
  onClose: () => void;
  onSaved: (message: string) => void;
}

type TabKey = "base" | "advanced" | "automation";

export function BoardColumnSettingsDialog({ projectKey, columnId, onClose, onSaved }: BoardColumnSettingsDialogProps) {
  const [data, setData] = useState<BoardColumnSettingsResponse>();
  const [activeTab, setActiveTab] = useState<TabKey>("base");
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState<UpdateBoardColumnSettingsRequest>({
    projectKey,
    columnId,
    name: "",
    color: "#0C66E4",
    wipEnabled: true,
    wipLimit: 5,
    statusKey: columnId,
    description: "",
  });

  useEffect(() => {
    let mounted = true;
    void projectWorkspaceService.getBoardColumnSettings({ projectKey, columnId }).then((response) => {
      if (!mounted) return;
      setData(response);
      setForm({
        projectKey,
        columnId,
        name: response.column.name,
        color: response.column.color,
        wipEnabled: response.column.wipEnabled,
        wipLimit: response.column.wipLimit ?? 5,
        statusKey: response.column.statusKey,
        description: response.column.description ?? "",
      });
    });
    return () => {
      mounted = false;
    };
  }, [columnId, projectKey]);

  async function handleSave() {
    setSaving(true);
    try {
      const response = await projectWorkspaceService.updateBoardColumnSettings(form);
      onSaved(response.message);
    } finally {
      setSaving(false);
    }
  }

  const editable = data?.column.editable ?? true;
  const nameInvalid = !form.name.trim();

  return (
    <BoardDialogFrame
      title="列设置"
      description={editable ? "配置看板列名称、WIP 限制、状态分类与自动化规则。" : "系统列仅支持查看部分设置。"}
      widthClassName="max-w-[720px]"
      onClose={onClose}
      footer={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white" onClick={onClose}>
            取消
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" loading={saving} disabled={!editable || nameInvalid} onClick={() => void handleSave()}>
            保存
          </Button>
        </>
      }
    >
      <div className="mb-5 flex border-b border-[#EBECF0]">
        {[
          { key: "base", label: "基础设置", icon: Settings2 },
          { key: "advanced", label: "高级设置", icon: AlertCircle },
          { key: "automation", label: "自动化规则", icon: Zap },
        ].map((tab) => {
          const Icon = tab.icon;
          return (
            <Button
              key={tab.key}
              className={cn("inline-flex h-10 items-center gap-2 border-b-2 px-4 text-[13px] font-bold", activeTab === tab.key ? "border-[#0C66E4] text-[#0C66E4]" : "border-transparent text-[#626F86] hover:text-[#172B4D]")}
              onClick={() => setActiveTab(tab.key as TabKey)}
            >
              <Icon className="h-4 w-4" />
              {tab.label}
            </Button>
          );
        })}
      </div>

      {!editable ? (
        <div className="mb-4 flex items-start gap-3 rounded-[4px] border border-[#FFE2BD] bg-[#FFF7ED] px-4 py-3 text-[13px] text-[#974F0C]">
          <Lock className="mt-0.5 h-4 w-4 shrink-0" />
          <span>该列已归档或为系统列，无法编辑，只能查看配置。</span>
        </div>
      ) : null}

      {activeTab === "base" ? (
        <div className="space-y-5">
          <label className="block">
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">列名称 <span className="text-[#DE350B]">*</span></div>
            <Input
              className={cn("h-9 rounded-[4px] border-[#DFE1E6]", nameInvalid && "border-[#FF5630] focus-visible:ring-[#FF5630]")}
              value={form.name}
              disabled={!editable}
              maxLength={20}
              onChange={(event) => setForm((prev) => ({ ...prev, name: event.target.value }))}
            />
            <div className="mt-1 text-right text-[11px] text-[#7A869A]">{form.name.length}/20</div>
          </label>

          <div>
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">列颜色</div>
            <div className="flex flex-wrap gap-2">
              {(data?.colorOptions ?? []).map((color) => (
                <Button
                  key={color}
                  className={cn("flex h-6 w-6 items-center justify-center rounded-full border border-white shadow-[0_0_0_1px_rgba(9,30,66,0.16)]", !editable && "cursor-not-allowed opacity-60")}
                  style={{ backgroundColor: color }}
                  disabled={!editable}
                  onClick={() => setForm((prev) => ({ ...prev, color }))}
                >
                  {form.color === color ? <Check className="h-3.5 w-3.5 text-white" /> : null}
                </Button>
              ))}
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-[1fr_160px]">
            <label className="flex items-center gap-3 rounded-[4px] border border-[#DFE1E6] bg-[#FAFBFC] px-4 py-3">
              <Checkbox
                className="h-4 w-4"
                checked={form.wipEnabled}
                disabled={!editable}
                onChange={(event) => setForm((prev) => ({ ...prev, wipEnabled: event.target.checked }))}
              />
              <span>
                <span className="block text-[13px] font-bold text-[#172B4D]">开启 WIP 限制</span>
                <span className="text-[12px] text-[#626F86]">超过后在看板列、拖拽和弹窗中提示</span>
              </span>
            </label>
            <label>
              <div className="mb-2 text-[12px] font-bold text-[#44546F]">卡片上限</div>
              <Input
                type="number"
                min={0}
                className="h-9 rounded-[4px] border-[#DFE1E6]"
                value={form.wipLimit ?? 0}
                disabled={!editable || !form.wipEnabled}
                onChange={(event) => setForm((prev) => ({ ...prev, wipLimit: Number(event.target.value) }))}
              />
            </label>
          </div>
        </div>
      ) : null}

      {activeTab === "advanced" ? (
        <div className="space-y-5">
          <label className="block">
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">列类型</div>
            <Select
              className="h-9 w-full rounded-[4px] border-[#DFE1E6] bg-white text-[13px] text-[#172B4D] disabled:bg-[#F4F5F7]"
              value={form.statusKey}
              disabled={!editable}
              onChange={(value) => setForm((prev) => ({ ...prev, statusKey: value as UpdateBoardColumnSettingsRequest["statusKey"] }))}
            >
              {(data?.statusOptions ?? []).map((status) => (
                <option key={status.id} value={status.id}>{status.label}</option>
              ))}
            </Select>
          </label>
          <label className="block">
            <div className="mb-2 text-[13px] font-bold text-[#172B4D]">描述（可选）</div>
            <Textarea
              className="min-h-[92px] w-full resize-none rounded-[4px] border-[#DFE1E6] bg-white px-3 py-2 text-[13px] leading-5 text-[#172B4D] disabled:bg-[#F4F5F7]"
              value={form.description}
              disabled={!editable}
              maxLength={100}
              onChange={(event) => setForm((prev) => ({ ...prev, description: event.target.value }))}
            />
            <div className="mt-1 text-right text-[11px] text-[#7A869A]">{form.description?.length ?? 0}/100</div>
          </label>
        </div>
      ) : null}

      {activeTab === "automation" ? (
        <div className="space-y-3">
          {(data?.automationRules ?? []).map((rule) => (
            <div key={rule.id} className="flex items-center justify-between rounded-[4px] border border-[#DFE1E6] bg-white px-4 py-3">
              <div>
                <div className="text-[13px] font-bold text-[#172B4D]">{rule.name}</div>
                <div className="mt-1 text-[12px] text-[#626F86]">触发：{rule.trigger}</div>
              </div>
              <span className={cn("rounded-[3px] px-2 py-1 text-[11px] font-bold", rule.enabled ? "bg-[#E3FCEF] text-[#006644]" : "bg-[#F4F5F7] text-[#626F86]")}>{rule.enabled ? "已启用" : "未启用"}</span>
            </div>
          ))}
          <Button className="h-9 rounded-[4px] border border-dashed border-[#A6C5E8] px-4 text-[13px] font-bold text-[#0C66E4] hover:bg-[#F0F6FF]">
            添加规则
          </Button>
        </div>
      ) : null}
    </BoardDialogFrame>
  );
}
