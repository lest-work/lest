type LogLevel = "debug" | "info" | "warn" | "error";

const LOG_LEVELS: Record<LogLevel, number> = {
  debug: 0, info: 1, warn: 2, error: 3,
};

const currentLevel: LogLevel = import.meta.env.DEV ? "debug" : "warn";

function shouldLog(level: LogLevel): boolean {
  return LOG_LEVELS[level] >= LOG_LEVELS[currentLevel];
}

export function createAppLogger(namespace: string) {
  const prefix = `[${namespace}]`;
  return {
    debug: (...args: unknown[]) => shouldLog("debug") && console.debug(prefix, ...args),
    info: (...args: unknown[]) => shouldLog("info") && console.info(prefix, ...args),
    warn: (...args: unknown[]) => shouldLog("warn") && console.warn(prefix, ...args),
    error: (...args: unknown[]) => shouldLog("error") && console.error(prefix, ...args),
  };
}
