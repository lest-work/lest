import { Table as AntdTable } from "antd";
import type { TableProps as AntdTableProps } from "antd";

import { cn } from "@/shared/lib/utils";

export type DataTableProps<RecordType extends object = object> = AntdTableProps<RecordType>;

export function DataTable<RecordType extends object = object>({ className, rowClassName, ...props }: DataTableProps<RecordType>) {
  return (
    <AntdTable<RecordType>
      className={cn("lest-data-table", className)}
      rowClassName={(record, index, indent) =>
        cn(
          "text-[13px] text-[#172B4D]",
          typeof rowClassName === "function" ? rowClassName(record, index, indent) : rowClassName,
        )
      }
      {...props}
    />
  );
}
