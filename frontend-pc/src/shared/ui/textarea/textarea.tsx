import { Input } from "antd";
import type { TextAreaProps as AntdTextAreaProps } from "antd/es/input";
import type { TextAreaRef } from "antd/es/input/TextArea";
import { forwardRef } from "react";

import { cn } from "@/shared/lib/utils";

export type TextareaProps = AntdTextAreaProps;

export const Textarea = forwardRef<TextAreaRef, TextareaProps>(({ className, ...props }, ref) => (
  <Input.TextArea
    ref={ref}
    className={cn(
      "min-h-[88px] w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2 text-sm shadow-sm placeholder:text-[#6B778C] focus:border-[#0C66E4] focus:ring-1 focus:ring-[#0C66E4]",
      className,
    )}
    {...props}
  />
));

Textarea.displayName = "Textarea";
