export const appConfig = {
  /** API base URL - override via VITE_API_BASE_URL env */
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? "/api",
  /** Application title */
  title: import.meta.env.VITE_APP_TITLE ?? "敏捷研发平台",
  /** Environment */
  environment: import.meta.env.MODE as "development" | "production" | "test",
  /** Whether the app is running in development mode */
  isDev: import.meta.env.DEV,
  /** Whether the app is running in production mode */
  isProd: import.meta.env.PROD,
} as const;

export { lestAntdTheme } from "./theme";
