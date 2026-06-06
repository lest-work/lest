import { DatePicker as AntdDatePicker } from "antd";
import type { DatePickerProps as AntdDatePickerProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type DatePickerProps = AntdDatePickerProps;

export function DatePicker({ className, popupClassName, ...props }: DatePickerProps) {
  return (
    <AntdDatePicker
      className={cn("h-9 rounded-[4px] border-[#DFE1E6] text-sm", className)}
      popupClassName={cn("lest-date-picker-dropdown", popupClassName)}
      {...props}
    />
  );
}
