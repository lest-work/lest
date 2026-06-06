import { X } from "lucide-react";
import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";
import { Button, Modal } from "@/shared/ui";

export interface BoardDialogFrameProps {
  title: string;
  description?: string;
  widthClassName?: string;
  children: ReactNode;
  footer?: ReactNode;
  onClose: () => void;
}

export function BoardDialogFrame({ title, description, widthClassName, children, footer, onClose }: BoardDialogFrameProps) {
  return (
    <Modal
      open
      closable={false}
      footer={null}
      zIndex={50}
      width="min(calc(100vw - 32px), 720px)"
      className="lest-frameless-modal"
      styles={{ mask: { background: "rgba(9, 30, 66, 0.3)" } }}
      onCancel={onClose}
    >
      <section className={cn("mx-auto max-h-[calc(100vh-48px)] w-full overflow-hidden rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]", widthClassName ?? "max-w-[640px]")}>
        <header className="flex items-start justify-between gap-4 border-b border-[#EBECF0] px-6 py-4">
          <div>
            <h2 className="text-[16px] font-bold text-[#172B4D]">{title}</h2>
            {description ? <p className="mt-1 text-[13px] leading-5 text-[#626F86]">{description}</p> : null}
          </div>
          <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[4px] text-[#44546F]" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </header>
        <div className="workspace-scrollbar max-h-[calc(100vh-180px)] overflow-y-auto px-6 py-5">{children}</div>
        {footer ? <footer className="flex items-center justify-end gap-3 border-t border-[#EBECF0] bg-[#FAFBFC] px-6 py-4">{footer}</footer> : null}
      </section>
    </Modal>
  );
}
