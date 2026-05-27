/* ==================== Stats / Summaries Response Models ==================== */

/** Stats API 响应 - 单一时间范围的统计数据 */
export interface WakapiStatsResponse {
  data: WakapiStatsData;
}

export interface WakapiStatsData {
  username: string;
  user_id: string;
  timezone: string;
  start: string;
  end: string;
  total_seconds: number;
  daily_average: number;
  status: string;
  projects?: WakapiSummariesEntry[];
  languages?: WakapiSummariesEntry[];
  editors?: WakapiSummariesEntry[];
  machines?: WakapiSummariesEntry[];
  operating_systems?: WakapiSummariesEntry[];
  categories?: WakapiSummariesEntry[];
  branches?: WakapiSummariesEntry[];
}

export interface WakapiSummariesEntry {
  name: string;
  total_seconds: number;
  percent: number;
  digital: string;
  text: string;
  hours: number;
  minutes: number;
  seconds: number;
}

/** Summaries API 响应 - 每日汇总列表 */
export interface WakapiSummariesResponse {
  start: string;
  end: string;
  daily_average: WakapiDailyAverage;
  cumulative_total: WakapiCumulativeTotal;
  data: WakapiSummariesData[];
}

export interface WakapiDailyAverage {
  seconds: number;
  text: string;
  days_including_holidays: number;
  days_minus_holidays: number;
  holidays: number;
}

export interface WakapiCumulativeTotal {
  seconds: number;
  text: string;
  digital: string;
  decimal: string;
}

export interface WakapiSummariesData {
  grand_total: WakapiGrandTotal;
  range: { start: string; end: string; date: string; text: string };
  projects?: WakapiSummariesEntry[];
  languages?: WakapiSummariesEntry[];
  editors?: WakapiSummariesEntry[];
  operating_systems?: WakapiSummariesEntry[];
  machines?: WakapiSummariesEntry[];
  categories?: WakapiSummariesEntry[];
  branches?: WakapiSummariesEntry[];
}

export interface WakapiGrandTotal {
  total_seconds: number;
  hours: number;
  minutes: number;
  digital: string;
  text: string;
}

/** All Time API 响应 - 累计总时长 */
export interface WakapiAllTimeResponse {
  data: WakapiAllTimeData;
}

export interface WakapiAllTimeData {
  total_seconds: number;
  text: string;
  is_up_to_date: boolean;
  range: {
    start: string;
    end: string;
    date: string;
    text: string;
    timezone: string;
  };
}

/** Projects API 响应 - 项目列表 */
export interface WakapiProjectResponse {
  projects: WakapiProject[];
}

export interface WakapiProject {
  id: string;
  name: string;
  last_heartbeat_at: string;
  created_at: string;
  color?: string;
  description?: string;
  url?: string;
  title?: string;
  slug?: string;
}

/** Heartbeats API 响应 - 心跳记录 */
export interface WakapiHeartbeatsResponse {
  start: string;
  end: string;
  timezone: string;
  data: WakapiHeartbeatEntry[];
}

export interface WakapiHeartbeatEntry {
  id: string;
  entity: string;
  type: string;
  category: string;
  project: string;
  language: string;
  branch: string;
  is_write: boolean;
  time: number;
  lines?: number;
  lineno?: number;
  cursorpos?: number;
}

/** Shield Badge API 响应 */
export interface WakapiBadgeResponse {
  schemaVersion: number;
  label: string;
  message: string;
  color: string;
}

/** Leaders API 响应 - 排行榜 */
export interface WakapiLeadersResponse {
  data: WakapiLeaderData[];
}

export interface WakapiLeaderData {
  rank: number;
  user_id: string;
  username: string;
  total_seconds: number;
  digital: string;
  text: string;
  name: string;
  avatar?: string;
  is_current_user?: boolean;
}

/* ==================== Config Models (Alias / Label / LanguageMapping) ==================== */

/** 别名 */
export interface WakapiAlias {
  id?: number;
  type: number;         // 1=project, 2=language, 3=editor, 4=os, 5=machine
  key: string;
  value: string;
  userId?: string;
  createTime?: string;
}

export interface WakapiAliasParam {
  type?: number;
  key?: string;
  value?: string;
  userId?: string;
}

/** 项目标签 */
export interface WakapiLabel {
  id?: number;
  label: string;
  projectKey: string;
  color?: string;
  userId?: string;
  createTime?: string;
}

export interface WakapiLabelParam {
  label?: string;
  projectKey?: string;
  userId?: string;
}

/** 语言映射 */
export interface WakapiLanguageMapping {
  id?: number;
  extension: string;
  language: string;
  userId?: string;
  createTime?: string;
}

export interface WakapiLanguageMappingParam {
  extension?: string;
  language?: string;
  userId?: string;
}

/* ==================== Admin Models ==================== */

/** WakAPI 用户 */
export interface WakapiUser {
  userId?: string;
  username?: string;
  displayName?: string;
  avatar?: string;
  heartbeatCount?: number;
  lastHeartbeat?: string;
  apiKey?: string;
  enabled?: boolean;
  createTime?: string;
}

export interface WakapiUserParam {
  keyword?: string;
  enabled?: boolean;
  minHeartbeats?: number;
}

/** 定时任务 */
export interface ScheduledTask {
  name: string;
  displayName: string;
  cron: string;
  lastRun?: string;
  nextRun?: string;
  enabled: boolean;
  status: 'idle' | 'running' | 'disabled';
}

export interface ScheduledTaskLog {
  id: number;
  taskName: string;
  startTime: string;
  endTime?: string;
  duration?: number;
  status: 'success' | 'failed' | 'running';
  message?: string;
  userId?: string;
}

/* ==================== Summary Types ==================== */

/** Summary type 常量 */
export const SUMMARY_TYPE = {
  PROJECT: 1,
  LANGUAGE: 2,
  EDITOR: 3,
  OPERATING_SYSTEM: 4,
  MACHINE: 5,
  CATEGORY: 6,
  LABEL: 7,
  BRANCH: 8,
  ENTITY: 9
} as const;

export type SUMMARY_TYPE = (typeof SUMMARY_TYPE)[keyof typeof SUMMARY_TYPE];

/** Summary type 名称映射 */
export const SUMMARY_TYPE_NAME: Record<number, string> = {
  [SUMMARY_TYPE.PROJECT]: '项目',
  [SUMMARY_TYPE.LANGUAGE]: '语言',
  [SUMMARY_TYPE.EDITOR]: '编辑器',
  [SUMMARY_TYPE.OPERATING_SYSTEM]: '操作系统',
  [SUMMARY_TYPE.MACHINE]: '机器',
  [SUMMARY_TYPE.CATEGORY]: '分类',
  [SUMMARY_TYPE.LABEL]: '标签',
  [SUMMARY_TYPE.BRANCH]: '分支',
  [SUMMARY_TYPE.ENTITY]: '文件'
};

/** 时间范围选项 */
export interface TimeRangeOption {
  label: string;
  value: string;
  start: string;
  end: string;
}

export const TIME_RANGE_OPTIONS: TimeRangeOption[] = [
  { label: '今天', value: 'today', start: '', end: '' },
  { label: '昨天', value: 'yesterday', start: '', end: '' },
  { label: '最近 7 天', value: 'last_7_days', start: '', end: '' },
  { label: '最近 14 天', value: 'last_14_days', start: '', end: '' },
  { label: '最近 30 天', value: 'last_30_days', start: '', end: '' },
  { label: '本周', value: 'this_week', start: '', end: '' },
  { label: '本月', value: 'this_month', start: '', end: '' },
  { label: '本年', value: 'this_year', start: '', end: '' }
];

/* ==================== Activity Overview Models ==================== */

export interface ActivityOverview {
  dau: number;
  wau: number;
  mau: number;
  totalSeconds: number;
  activeRate: number;
  range: string;
}

export interface ActivityTrendPoint {
  date: string;
  activeUsers: number;
  totalSeconds: number;
}

export interface ActivityRankItem {
  userId: string;
  username: string;
  displayName?: string;
  avatar?: string;
  totalSeconds: number;
  activeDays: number;
  percent: number;
  rank: number;
}

/* ==================== Team Models ==================== */

export interface Team {
  id?: number;
  name: string;
  description?: string;
  visibility: 'public' | 'private';
  inviteCode?: string;
  memberCount?: number;
  totalSeconds?: number;
  leaderId?: string;
  leaderName?: string;
  createTime?: string;
  updateTime?: string;
}

export interface TeamMember {
  userId: string;
  username: string;
  displayName?: string;
  avatar?: string;
  role: 'owner' | 'admin' | 'member';
  joinedAt?: string;
  totalSeconds?: number;
  lastActiveAt?: string;
}

export interface TeamLeaderboardItem {
  rank: number;
  userId: string;
  username: string;
  displayName?: string;
  avatar?: string;
  totalSeconds: number;
  digital: string;
  text: string;
  percent: number;
}

export interface TeamRankItem {
  teamId: number;
  teamName: string;
  totalSeconds: number;
  memberCount: number;
  dailyAvgSeconds: number;
  percent: number;
  rank: number;
}

/* ==================== Alert Models ==================== */

export interface AlertRule {
  id?: number;
  name: string;
  type: AlertRuleType;
  condition: AlertCondition;
  threshold: number;
  target?: string;
  enabled: boolean;
  notifyChannels: string[];
  description?: string;
  createTime?: string;
  updateTime?: string;
  lastTriggered?: string;
  triggerCount?: number;
}

export type AlertRuleType =
  | 'silence'
  | 'coding_time_low'
  | 'coding_time_high'
  | 'inactive'
  | 'project_change';

export interface AlertCondition {
  operator: 'lt' | 'gt' | 'eq' | 'ne' | 'lte' | 'gte';
  unit: 'hours' | 'days' | 'count';
  value: number;
}

export interface AlertHistory {
  id?: number;
  ruleId: number;
  ruleName: string;
  userId: string;
  username: string;
  status: AlertStatus;
  triggeredAt: string;
  resolvedAt?: string;
  resolvedBy?: string;
  message: string;
  details?: string;
}

export type AlertStatus = 'triggered' | 'acknowledged' | 'resolved' | 'ignored';
