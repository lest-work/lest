import { Radio as AntdRadio } from "antd";
import type { RadioProps as AntdRadioProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type RadioProps = AntdRadioProps;

export function Radio({ className, ...props }: RadioProps) {
  return <AntdRadio className={cn("text-[13px] text-[#172B4D]", className)} {...props} />;
}
