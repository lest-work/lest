export const PAGINATION = {
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],
} as const;

export const DATE_FORMAT = "YYYY-MM-DD" as const;
export const DATE_TIME_FORMAT = "YYYY-MM-DD HH:mm" as const;

export const API = {
  DEFAULT_TIMEOUT: 30000,
  RETRY_COUNT: 3,
} as const;

export const REGEX = {
  EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  URL: /^https?:\/\/.+/,
} as const;
