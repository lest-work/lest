import { Drawer as AntdDrawer } from "antd";
import type { DrawerProps as AntdDrawerProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type DrawerProps = AntdDrawerProps;

export function Drawer({ className, destroyOnHidden = true, ...props }: DrawerProps) {
  return <AntdDrawer destroyOnHidden={destroyOnHidden} maskClosable={true} className={cn("lest-drawer", className)} {...props} />;
}
