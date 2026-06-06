import * as React from "react";
import { Button as AntdButton } from "antd";
import type { ButtonProps as AntdButtonProps } from "antd";
import { cva, type VariantProps } from "class-variance-authority";

import { cn } from "@/shared/lib/utils";

const buttonVariants = cva(
  "inline-flex h-auto min-w-0 items-center justify-center gap-2 whitespace-nowrap border-0 bg-transparent p-0 text-inherit shadow-none [box-shadow:none] transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50",
  {
    variants: {
      variant: {
        plain: "",
        default: "bg-primary text-primary-foreground shadow-sm hover:bg-primary/90",
        secondary: "bg-secondary text-secondary-foreground hover:bg-secondary/80",
        outline: "border bg-background hover:bg-accent hover:text-accent-foreground",
        ghost: "hover:bg-accent hover:text-accent-foreground",
        destructive: "bg-destructive text-destructive-foreground shadow-sm hover:bg-destructive/90",
      },
      size: {
        sm: "h-8 px-3 text-xs",
        md: "h-9 px-4",
        lg: "h-10 px-5",
        icon: "h-9 w-9",
      },
    },
    defaultVariants: {
      variant: "plain",
    },
  },
);

export interface ButtonProps
  extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, "color" | "type">,
    VariantProps<typeof buttonVariants> {
  loading?: boolean;
  htmlType?: AntdButtonProps["htmlType"];
  type?: React.ButtonHTMLAttributes<HTMLButtonElement>["type"];
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  { className, variant, size, loading, children, disabled, htmlType, type = "button", ...props },
  ref,
) {
  return (
    <AntdButton
      ref={ref}
      type="text"
      htmlType={htmlType ?? type}
      className={cn(buttonVariants({ variant, size }), className)}
      disabled={disabled || loading}
      loading={loading}
      {...props}
    >
      {children}
    </AntdButton>
  );
});
