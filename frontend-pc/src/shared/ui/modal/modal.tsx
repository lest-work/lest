import { Modal as AntdModal } from "antd";
import type { ModalProps as AntdModalProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type ModalProps = AntdModalProps;

export function Modal({ className, centered = true, destroyOnHidden = true, ...props }: ModalProps) {
  return (
    <AntdModal
      centered={centered}
      destroyOnHidden={destroyOnHidden}
      maskClosable={true}
      className={cn("lest-modal", className)}
      {...props}
    />
  );
}
