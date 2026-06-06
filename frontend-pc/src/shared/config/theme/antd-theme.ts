import type { ThemeConfig } from "antd";

export interface LestAntdThemeToken {
  colorPrimary: string;
  colorSuccess: string;
  colorWarning: string;
  colorError: string;
  colorInfo: string;
  colorText: string;
  colorTextSecondary: string;
  colorBgLayout: string;
  colorBgContainer: string;
  colorBorder: string;
  borderRadius: number;
  borderRadiusLG: number;
  controlHeight: number;
  controlHeightSM: number;
  controlHeightLG: number;
  fontFamily: string;
}

export interface LestAntdComponentTheme {
  Button: Record<string, string | number>;
  Input: Record<string, string | number>;
  Select: Record<string, string | number>;
  Table: Record<string, string | number>;
  Pagination: Record<string, string | number>;
  Modal: Record<string, string | number>;
  Drawer: Record<string, string | number>;
  DatePicker: Record<string, string | number>;
}

export interface LestAntdThemeConfig extends ThemeConfig {
  token: LestAntdThemeToken;
  components: LestAntdComponentTheme;
}

export const lestAntdTheme: LestAntdThemeConfig = {
  token: {
    colorPrimary: "#0C66E4",
    colorSuccess: "#22A06B",
    colorWarning: "#FFAB00",
    colorError: "#DE350B",
    colorInfo: "#0C66E4",
    colorText: "#172B4D",
    colorTextSecondary: "#626F86",
    colorBgLayout: "#F7F8F9",
    colorBgContainer: "#FFFFFF",
    colorBorder: "#DFE1E6",
    borderRadius: 4,
    borderRadiusLG: 8,
    controlHeight: 36,
    controlHeightSM: 32,
    controlHeightLG: 40,
    fontFamily:
      'Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif',
  },
  components: {
    Button: {
      borderRadius: 4,
      controlHeight: 36,
      fontWeight: 600,
      primaryShadow: "none",
    },
    Input: {
      borderRadius: 4,
      controlHeight: 36,
      activeBorderColor: "#0C66E4",
      hoverBorderColor: "#C1C7D0",
    },
    Select: {
      borderRadius: 4,
      controlHeight: 36,
      optionSelectedBg: "#E9F2FF",
      optionSelectedColor: "#0C66E4",
    },
    Table: {
      borderColor: "#DFE1E6",
      headerBg: "#FAFBFC",
      headerColor: "#6B778C",
      rowHoverBg: "#F7F8F9",
    },
    Pagination: {
      borderRadius: 3,
      itemActiveBg: "#0C66E4",
    },
    Modal: {
      borderRadiusLG: 8,
      titleColor: "#172B4D",
    },
    Drawer: {
      colorBgElevated: "#FFFFFF",
      borderRadiusLG: 0,
    },
    DatePicker: {
      borderRadius: 4,
      controlHeight: 36,
      activeBorderColor: "#0C66E4",
    },
  },
};
