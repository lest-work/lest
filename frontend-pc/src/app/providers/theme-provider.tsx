import { ConfigProvider } from "antd";
import type { CSSProperties, ReactNode } from "react";

import { lestAntdTheme } from "@/shared/config";

export function ThemeProvider({ children }: { children: ReactNode }) {
  return (
    <ConfigProvider theme={lestAntdTheme}>
      <div
        data-lest-theme="default"
        style={
          {
            "--lest-color-primary": lestAntdTheme.token?.colorPrimary,
            "--lest-color-text": lestAntdTheme.token?.colorText,
            "--lest-color-text-secondary": lestAntdTheme.token?.colorTextSecondary,
            "--lest-color-bg-layout": lestAntdTheme.token?.colorBgLayout,
            "--lest-color-border": lestAntdTheme.token?.colorBorder,
          } as CSSProperties
        }
      >
        {children}
      </div>
    </ConfigProvider>
  );
}
