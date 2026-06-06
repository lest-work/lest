import type { ReactNode } from "react";

export type MenuVariant = "vertical" | "horizontal" | "dropdown";
export type MenuSize = "sm" | "md" | "lg";

export interface MenuItemData {
  key: string;
  label: string;
  icon?: ReactNode;
  disabled?: boolean;
  hasSubMenu?: boolean;
  suffix?: ReactNode;
  className?: string;
  active?: boolean;
  href?: string;
  onClick?: () => void;
  onFocus?: () => void;
  onMouseEnter?: () => void;
}

export interface MenuGroupData {
  title?: string;
  items: MenuItemData[];
}
