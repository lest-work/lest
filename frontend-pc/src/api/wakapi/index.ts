/**
 * WakAPI 模块 API 统一导出
 */

// Stats API
export {
  getWakapiStats,
  getWakapiSummaries,
  getWakapiAllTime,
  getWakapiProjects,
  getWakapiHeartbeats,
  getWakapiBadge,
  getWakapiLeaders,
  formatDuration,
  formatDigital,
  secondsToDays,
  getRangeDates,
  type WakapiStatsResponse,
  type WakapiSummariesResponse,
  type WakapiAllTimeResponse,
  type WakapiProjectResponse,
  type WakapiHeartbeatsResponse,
  type WakapiBadgeResponse,
  type WakapiLeadersResponse
} from './stats';

export {
  // Alias
  pageAliases,
  listAliases,
  getAlias,
  addAlias,
  updateAlias,
  removeAlias,
  type WakapiAlias,
  type WakapiAliasParam,
  // Label
  pageLabels,
  listLabels,
  getLabel,
  addLabel,
  updateLabel,
  removeLabel,
  type WakapiLabel,
  type WakapiLabelParam,
  // LanguageMapping
  pageLanguageMappings,
  listLanguageMappings,
  getLanguageMapping,
  addLanguageMapping,
  updateLanguageMapping,
  removeLanguageMapping,
  type WakapiLanguageMapping,
  type WakapiLanguageMappingParam
} from './config';

export {
  // Admin
  pageWakapiUsers,
  listWakapiUsers,
  getWakapiUser,
  updateWakapiUser,
  getWakapiUserStats,
  getWakapiUserKey,
  regenerateWakapiKey,
  type WakapiUser,
  type WakapiUserParam,
  // Scheduled
  getScheduledTasks,
  triggerScheduledTask,
  getScheduledTaskLogs,
  type ScheduledTask,
  type ScheduledTaskLog
} from './admin';

// Teams API
export {
  getTeams,
  getTeam,
  createTeam,
  updateTeam,
  deleteTeam,
  getTeamMembers,
  addTeamMember,
  removeTeamMember,
  updateTeamMemberRole,
  joinTeam,
  leaveTeam,
  getTeamLeaderboard,
  getAllTeamLeaderboard,
  type Team,
  type TeamMember,
  type TeamLeaderboardItem,
  type TeamRankItem
} from './teams';

// Alerts API
export {
  pageAlertRules,
  listAlertRules,
  getAlertRule,
  createAlertRule,
  updateAlertRule,
  deleteAlertRule,
  toggleAlertRule,
  pageAlertHistory,
  getAlertHistory,
  acknowledgeAlert,
  resolveAlert,
  ignoreAlert,
  type AlertRule,
  type AlertHistory,
  type AlertRuleType,
  type AlertStatus
} from './alerts';

// Activity API
export {
  getActivityOverview,
  getActivityRanking,
  getActivityTrends,
  type ActivityOverview,
  type ActivityTrendPoint,
  type ActivityRankItem
} from './activity';
