import { Switch as AntdSwitch } from "antd";
import type { SwitchProps as AntdSwitchProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type SwitchProps = AntdSwitchProps;

export function Switch({ className, ...props }: SwitchProps) {
  return <AntdSwitch className={cn("align-middle", className)} {...props} />;
}
