import { Select as AntdSelect } from "antd";
import type { SelectProps as AntdSelectProps } from "antd";
import type { BaseOptionType, DefaultOptionType } from "antd/es/select";
import { Children, isValidElement } from "react";
import type { ReactElement, ReactNode } from "react";

import { cn } from "@/shared/lib/utils";

export type SelectProps<ValueType = unknown, OptionType extends BaseOptionType | DefaultOptionType = DefaultOptionType> =
  AntdSelectProps<ValueType, OptionType>;

export function Select<ValueType = unknown, OptionType extends BaseOptionType | DefaultOptionType = DefaultOptionType>({
  className,
  children,
  options,
  popupClassName,
  ...props
}: SelectProps<ValueType, OptionType>) {
  const normalizedOptions = options ?? normalizeOptionChildren(children);

  return (
    <AntdSelect<ValueType, OptionType>
      className={cn("h-9 min-w-[160px] text-sm", className)}
      options={normalizedOptions as OptionType[] | undefined}
      popupClassName={cn("lest-select-dropdown", popupClassName)}
      {...props}
    />
  );
}

function normalizeOptionChildren(children: SelectProps["children"]) {
  if (!children) {
    return undefined;
  }

  return Children.toArray(children)
    .filter(isValidElement)
    .map((child) => {
      const element = child as ReactElement<{ children?: ReactNode; disabled?: boolean; value?: string | number }>;

      return {
        disabled: element.props.disabled,
        label: element.props.children,
        value: element.props.value,
      };
    });
}
