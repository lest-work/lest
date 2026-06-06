import { AlertTriangle, CheckSquare2, Clock3, ShieldAlert, X } from "lucide-react";
import { useMemo, useState } from "react";

import { cn } from "@/shared/lib/utils";
import { Button } from "@/shared/ui/button";
import { Checkbox } from "@/shared/ui/checkbox";
import { Input } from "@/shared/ui/input";

export type DangerConfirmationMode = "default" | "checkbox" | "type-text" | "type-to-confirm" | "countdown";
export type DangerSeverity = "delete" | "close" | "move" | "archive" | "bulk-delete";

export interface DangerDialogProps {
  title: string;
  description: string;
  actionLabel: string;
  cancelLabel: string;
  severity: DangerSeverity;
  confirmationMode: DangerConfirmationMode;
  confirmationText?: string;
  affectedSummary?: Array<{ label: string; value: string }>;
  defaultOpen?: boolean;
  className?: string;
}

const severityClassName: Record<DangerSeverity, string> = {
  delete: "text-[#E34935] bg-[#FFECEB]",
  "bulk-delete": "text-[#E34935] bg-[#FFECEB]",
  close: "text-[#FF8B00] bg-[#FFF4E5]",
  move: "text-[#0C66E4] bg-[#E9F2FF]",
  archive: "text-[#44546F] bg-[#F4F5F7]",
};

export function DangerDialog({
  title,
  description,
  actionLabel,
  cancelLabel,
  severity,
  confirmationMode,
  confirmationText,
  affectedSummary = [],
  defaultOpen = true,
  className,
}: DangerDialogProps) {
  const [checked, setChecked] = useState(false);
  const [typedText, setTypedText] = useState("");
  const [countdownReady, setCountdownReady] = useState(false);

  const confirmationMessage = useMemo(() => {
    if (confirmationMode === "checkbox") {
      return "我已知晓该操作会影响关联数据";
    }
    if (confirmationMode === "type-to-confirm") {
      return `请输入 ${confirmationText ?? "确认文本"} 后继续`;
    }
    if (confirmationMode === "countdown") {
      return countdownReady ? "已完成二次确认" : "5 秒后允许执行";
    }
    return "确认后将立即执行该操作";
  }, [confirmationMode, confirmationText, countdownReady]);

  const canConfirm =
    confirmationMode === "checkbox"
      ? checked
      : confirmationMode === "type-to-confirm"
        ? typedText === confirmationText
        : confirmationMode === "countdown"
          ? countdownReady
          : true;

  if (!defaultOpen) {
    return null;
  }

  return (
    <div className={cn("w-full rounded-[8px] border border-[#DFE1E6] bg-white p-4 shadow-[0_8px_24px_rgba(9,30,66,0.14)]", className)}>
      <div className="flex items-start gap-3">
        <div className={cn("flex h-9 w-9 shrink-0 items-center justify-center rounded-full", severityClassName[severity])}>
          {confirmationMode === "checkbox" ? <CheckSquare2 className="h-5 w-5" /> : confirmationMode === "countdown" ? <Clock3 className="h-5 w-5" /> : <ShieldAlert className="h-5 w-5" />}
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-3">
            <h3 className="text-[15px] font-bold text-[#172B4D]">{title}</h3>
            <Button className="rounded-[4px] p-1 text-[#626F86] hover:bg-[#F4F5F7]" aria-label="关闭">
              <X className="h-4 w-4" />
            </Button>
          </div>
          <p className="mt-2 text-[13px] leading-6 text-[#44546F]">{description}</p>
        </div>
      </div>

      {affectedSummary.length ? (
        <div className="mt-4 grid gap-2 rounded-[6px] bg-[#FAFBFC] p-3 text-[12px] text-[#44546F] sm:grid-cols-2">
          {affectedSummary.map((item) => (
            <div key={item.label} className="flex items-center justify-between gap-3">
              <span>{item.label}</span>
              <span className="font-semibold text-[#172B4D]">{item.value}</span>
            </div>
          ))}
        </div>
      ) : null}

      <div className="mt-4 rounded-[6px] border border-[#DFE1E6] bg-white p-3">
        <div className="flex items-center gap-2 text-[12px] font-semibold text-[#44546F]">
          <AlertTriangle className="h-4 w-4 text-[#FF8B00]" />
          {confirmationMessage}
        </div>
        {confirmationMode === "checkbox" ? (
          <label className="mt-3 flex items-center gap-2 text-[12px] text-[#44546F]">
            <Checkbox checked={checked} onChange={(event) => setChecked(event.target.checked)} />
            确认继续执行
          </label>
        ) : null}
        {confirmationMode === "type-to-confirm" ? (
          <Input className="mt-3 h-8 rounded-[4px] border-[#DFE1E6] text-[12px]" value={typedText} placeholder={confirmationText} onChange={(event) => setTypedText(event.target.value)} />
        ) : null}
        {confirmationMode === "countdown" ? (
          <Button className="mt-3 text-[12px] font-semibold text-[#0C66E4]" onClick={() => setCountdownReady(true)}>
            跳过演示倒计时
          </Button>
        ) : null}
      </div>

      <div className="mt-5 flex justify-end gap-3">
        <Button variant="outline" className="h-8 rounded-[4px] border-[#DFE1E6] bg-white px-4 text-[13px] font-semibold">
          {cancelLabel}
        </Button>
        <Button className="h-8 rounded-[4px] bg-[#E34935] px-4 text-[13px] font-semibold text-white hover:bg-[#C9372C]" disabled={!canConfirm}>
          {actionLabel}
        </Button>
      </div>
    </div>
  );
}
