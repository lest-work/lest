import type { ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export interface MenuProps {
  variant?: "vertical" | "horizontal";
  className?: string;
  children: ReactNode;
}

/**
 * Menu container. Use with MenuItem + MenuGroup for sidebar-style navigation,
 * or use standalone with inline custom items for top-navigation style.
 *
 * Examples:
 *   <Menu variant="vertical">
 *     <MenuGroup title="计划">
 *       <MenuItem label="仪表盘" active />
 *     </MenuGroup>
 *   </Menu>
 */
export function Menu({ variant = "vertical", className, children }: MenuProps) {
  if (variant === "horizontal") {
    return (
      <div className={cn("flex items-center gap-3", className)}>
        {children}
      </div>
    );
  }

  return (
    <nav className={cn("flex flex-col", className)}>
      {children}
    </nav>
  );
}
