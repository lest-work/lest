import { Checkbox as AntdCheckbox } from "antd";
import type { CheckboxProps as AntdCheckboxProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type CheckboxProps = AntdCheckboxProps;

export function Checkbox({ className, ...props }: CheckboxProps) {
  return <AntdCheckbox className={cn("text-[13px] text-[#172B4D]", className)} {...props} />;
}
