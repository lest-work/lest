# 插件系统错误码总览

> **规划口径**：插件从 V4.0 开始正式开发。错误码只按 V4 插件体系分配，不再保留 V1/V2/V3 插件错误码段；V5 企业商业 Core 使用 Core 错误码体系。

## 错误码分配

| 范围 | 模块 | 说明 |
|------|------|------|
| 5000-5199 | plugin-runtime | 插件安装、启用、禁用、卸载、版本兼容 |
| 5200-5299 | plugin-sdk | SDK、Manifest、扩展点注册、上下文校验 |
| 5300-5399 | plugin-permission | 权限声明、权限校验、越权审计 |
| 5400-5499 | plugin-event | 事件订阅、事件投递、失败重试 |
| 5500-5599 | im-plugins | 飞书、钉钉、企业微信通知插件 |
| 5600-5699 | devops-plugins | 代码仓库、CI/CD 插件 |
| 5700-5799 | collaboration-plugins | WakaTime、会议、报表、Jira Sync |
| 5800-5899 | ai-plugins | AI Provider、AI 助手、AI 任务拆解 |

## 公共错误码

| 错误码 | 枚举常量 | HTTP | 说明 |
|--------|----------|------|------|
| 5000 | PLUGIN_NOT_FOUND | 404 | 插件不存在 |
| 5001 | PLUGIN_ALREADY_INSTALLED | 409 | 插件已安装 |
| 5002 | PLUGIN_DEPENDENCY_UNSATISFIED | 409 | 依赖插件未安装或版本不匹配 |
| 5003 | PLUGIN_VERSION_CONFLICT | 409 | 插件版本冲突 |
| 5004 | PLUGIN_PLATFORM_INCOMPATIBLE | 409 | 插件与平台版本不兼容 |
| 5005 | PLUGIN_PERMISSION_DENIED | 403 | 插件权限不足 |
| 5006 | PLUGIN_INSTALL_FAILED | 500 | 插件安装失败 |
| 5007 | PLUGIN_UNINSTALL_FAILED | 500 | 插件卸载失败 |
| 5008 | PLUGIN_ENABLE_FAILED | 500 | 插件启用失败 |
| 5009 | PLUGIN_DISABLE_FAILED | 500 | 插件禁用失败 |
| 5010 | PLUGIN_UPGRADE_FAILED | 500 | 插件升级失败 |
| 5011 | PLUGIN_MIGRATION_FAILED | 500 | 数据迁移失败 |
| 5012 | PLUGIN_CONFIG_INVALID | 400 | 插件配置无效 |
| 5013 | PLUGIN_SIGNATURE_INVALID | 403 | 插件签名无效 |
| 5014 | PLUGIN_HEALTH_CHECK_FAILED | 503 | 插件健康检查失败 |
| 5015 | PLUGIN_EVENT_DELIVERY_FAILED | 500 | 插件事件投递失败 |

## 维护规则

1. 新增插件必须先在本文件登记错误码段。
2. 插件错误码必须绑定到 V4 插件能力，不得回填 V1/V2/V3。
3. 每个插件 PRD/TASK 只引用自己的错误码段，不跨段复用。
