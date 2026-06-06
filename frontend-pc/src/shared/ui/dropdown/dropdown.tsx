import { Dropdown as AntdDropdown } from "antd";
import type { DropdownProps as AntdDropdownProps, MenuProps } from "antd";

export type DropdownProps = AntdDropdownProps;
export type DropdownMenuItem = Required<MenuProps>["items"][number];

export function Dropdown(props: DropdownProps) {
  return <AntdDropdown {...props} />;
}
