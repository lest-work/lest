# 系统设置开发任务列表

## 模块上下文

- **微服务模块**：`lest-system`（端口: 8001，与认证系统共用）
- **主目录**：`backend/lest-modules/lest-system`
- **包路径**：`com.lest.system`
- **数据库 Schema**：`lest_system`
- **前端 PC 目录**：`frontend-pc/src/views/system/`


---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-DDL-101** | DDL | `V1.0.5__init_sys_config_table.sql` | 创建 `sys_config` 表：`id`(BIGINT PK)、`config_key`(VARCHAR 128, UNIQUE)、`config_value`(TEXT)、`config_name`(VARCHAR 128)、`config_type`(VARCHAR 32, string/number/boolean/json)、`config_group`(VARCHAR 32, security/notification/storage/integration)、`description`(VARCHAR 256)、`editable`(TINYINT DEFAULT 1)、`created_at`、`updated_at`。建立唯一索引 `uk_config_key`。初始化内置配置项（密码策略/Token 有效期/会话超时等）。 | P0 | 待启动 | 未测试 |
| **TSK-SYS-SET-DDL-102** | DDL | `V1.0.6__init_audit_backup_tables.sql` | 创建 `sys_audit_log` 表：`id`(BIGINT PK)、`user_id`(BIGINT)、`username`(VARCHAR 64)、`operation_type`(VARCHAR 32)、`operation_name`(VARCHAR 64)、`target_type`(VARCHAR 32)、`target_id`(VARCHAR 64)、`target_name`(VARCHAR 256)、`detail`(TEXT)、`ip`(VARCHAR 64)、`user_agent`(VARCHAR 512)、`created_at`，建立索引 `idx_user_id`、`idx_operation_type`、`idx_created_at`。创建 `sys_backup_record` 表：`id`(BIGINT PK)、`filename`(VARCHAR 256)、`file_path`(VARCHAR 512)、`file_size`(BIGINT)、`status`(VARCHAR 16, running/completed/failed)、`backup_type`(VARCHAR 16, manual/scheduled)、`error_message`(VARCHAR 512)、`created_by`(BIGINT)、`created_at`、`finished_at`。 | P0 | 待启动 | 未测试 |
| **TSK-SYS-SET-DDL-103** | DDL | `V1.0.7__init_announcement_table.sql` | 创建 `sys_announcement` 表：`id`(BIGINT PK)、`title`(VARCHAR 256)、`content`(TEXT)、`type`(VARCHAR 16 DEFAULT 'normal', normal/important/urgent)、`level`(VARCHAR 16 DEFAULT 'global', global/project)、`project_id`(BIGINT)、`status`(VARCHAR 16 DEFAULT 'draft', draft/published/expired)、`publish_at`(DATETIME)、`expire_at`(DATETIME)、`created_by`(BIGINT NOT NULL)、`created_at`、`updated_at`。建立索引 `idx_status`、`idx_publish_at`。 | P1 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 系统参数接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-API-101** | `SysConfigController` | `GET /system/config` | Header: Bearer Token（需 admin 权限）；Query: `group`(可选, security/notification/storage/integration) | `{ data: { [group]: [{ configKey, configValue, configName, configType, description, editable }] } }` | 按 group 分组返回配置项；`editable=0` 的配置项只读展示；敏感配置（如密码类）脱敏返回。 | P0 | 待启动 | 未测试 |
| **TSK-SYS-SET-API-102** | `SysConfigController` | `PUT /system/config` | Body: `{ params: { [configKey]: value } }` | `{ code: 200, data: null }` | 1. 校验 configKey 存在且 editable=1；2. 按 configType 校验值格式（number/boolean/json）；3. 批量更新 `sys_config`；4. 清除相关配置缓存（Redis）；5. 记录操作日志。 | P0 | 待启动 | 未测试 |

### 2.2 审计日志接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-API-201** | `AuditLogController` | `GET /system/audit-log/page` | Query: `userId`(可选)、`operationType`(可选)、`startDate`、`endDate`、`page`、`size` | `{ records: [{ id, userId, username, operationType, operationName, targetType, targetId, targetName, detail, ip, userAgent, createdAt }], total, page, size }` | 分页查询 `sys_audit_log`；按 `created_at` 倒序；日志不可删除，无 DELETE 接口。 | P0 | 待启动 | 未测试 |

### 2.3 数据备份接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-API-301** | `BackupController` | `POST /system/backup` | Header: Bearer Token（需 admin 权限） | `{ backupId, status: "running" }` | 1. 检查是否有进行中的备份，有则抛 `BACKUP_IN_PROGRESS(9554)`；2. 插入 `sys_backup_record`（status=running）；3. 异步执行 mysqldump；4. 完成后更新 status 和 file_size。 | P0 | 待启动 | 未测试 |
| **TSK-SYS-SET-API-302** | `BackupController` | `GET /system/backup/page` | Query: `page`、`size` | `{ records: [{ id, filename, fileSize, status, backupType, createdBy, createdAt, finishedAt }], total, page, size }` | 分页查询备份记录；按 `created_at` 倒序。 | P0 | 待启动 | 未测试 |
| **TSK-SYS-SET-API-303** | `BackupController` | `POST /system/restore` | Body: `{ backupId }` | `{ restoreId, status: "running" }` | 1. 检查是否有进行中的恢复，有则抛 `RESTORE_IN_PROGRESS(9555)`；2. 恢复前自动创建当前数据快照；3. 异步执行数据恢复；4. 恢复完成后重启相关服务缓存。 | P0 | 待启动 | 未测试 |

### 2.4 在线用户接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-API-401** | `OnlineUserController` | `GET /system/online-user/page` | Query: `page`、`size` | `{ records: [{ sessionId, userId, username, nickname, loginTime, lastActiveTime, ip, device }], total, page, size }` | 从 Redis 中查询所有在线 Session（Key 前缀 `session:{userId}:`）；聚合用户信息；按 `loginTime` 倒序。 | P0 | 待启动 | 未测试 |
| **TSK-SYS-SET-API-402** | `OnlineUserController` | `POST /system/online-user/kickout` | Body: `{ sessionIds: string[] }` | `{ code: 200, data: null }` | sessionIds 为空数组时踢出所有在线用户；从 Redis 删除对应 Session；删除对应 Refresh Token；记录操作日志。 | P0 | 待启动 | 未测试 |

### 2.5 系统公告接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-API-501** | `AnnouncementController` | `GET /system/announcement/page` | Query: `status`(可选)、`type`(可选)、`page`、`size` | `{ records: [{ id, title, type, level, status, publishAt, expireAt, createdBy, createdAt }], total, page, size }` | 管理端查询所有公告；按 `created_at` 倒序。 | P1 | 待启动 | 未测试 |
| **TSK-SYS-SET-API-502** | `AnnouncementController` | `POST /system/announcement` | Body: `{ title, content, type, level, projectId?, publishAt?, expireAt? }` | `{ id, title, status }` | 插入 `sys_announcement`；若 publishAt 为空则立即发布（status=published）；若有 publishAt 则定时发布（status=draft，定时任务处理）。 | P1 | 待启动 | 未测试 |
| **TSK-SYS-SET-API-503** | `AnnouncementController` | `GET /system/announcement/active` | Header: Bearer Token（普通用户可访问） | `{ data: [{ id, title, content, type, publishAt }] }` | 查询当前有效公告（status=published，publishAt <= now，expireAt > now 或为空）；用于前端首页展示公告横幅。 | P1 | 待启动 | 未测试 |

### 2.6 系统状态接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-API-601** | `SystemStatusController` | `GET /system/status` | Header: Bearer Token（需 admin 权限） | `{ system: { version, startTime, uptime }, database: { status, type, connections }, redis: { status, usedMemory }, kafka: { status, brokers[] }, disk: { total, used, usagePercent }, cpu: { usagePercent }, memory: { total, used, usagePercent } }` | 通过 Spring Actuator 获取 JVM 指标；通过 JDBC 查询数据库连接数；通过 Redis INFO 命令获取内存；通过 Kafka AdminClient 获取 broker 状态；通过 Java File API 获取磁盘信息。 | P0 | 待启动 | 未测试 |

---

## 3. 前端 PC 端任务

### 3.1 系统参数配置页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-FE-101** | `@/views/system/config.vue` | `/system/config` | 分组 Tab（安全设置/通知设置/存储设置/集成设置）；参数表格：参数名称、参数值（根据 configType 渲染不同控件：文本框/数字输入/开关/JSON 编辑器）、说明；顶部：保存按钮、重置按钮 | 1. 页面加载调用 `GET /system/config` 回显所有配置；2. 保存按钮调用 `PUT /system/config`（传入所有修改过的参数）；3. editable=0 的参数禁用编辑。 | P0 | 待启动 | 未测试 |

### 3.2 审计日志页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-FE-201** | `@/views/system/audit-log.vue` | `/system/audit-log` | 顶部：用户名输入框、操作类型下拉、时间范围选择器、搜索按钮、重置按钮、导出按钮；列表：操作时间、用户名、操作类型、操作对象、IP 地址、操作列（详情按钮）；底部：分页组件 | 1. 页面加载调用 `GET /system/audit-log/page`；2. 详情按钮打开弹窗展示完整 detail 字段（JSON 格式化）；3. 导出按钮触发 Excel 导出（调用带 export=true 参数的接口）。 | P0 | 待启动 | 未测试 |

### 3.3 在线用户管理页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-FE-301** | `@/views/system/online-user.vue` | `/system/online-user` | 顶部：在线人数统计、强制全部下线按钮；列表：用户头像、用户名、昵称、登录时间、最后活跃时间、IP 地址、设备信息、操作列（强制下线按钮）；底部：分页组件 | 1. 页面加载调用 `GET /system/online-user/page`；2. 每30秒自动刷新；3. 强制下线按钮弹出确认框后调用 `POST /system/online-user/kickout`（传入 sessionId）；4. 强制全部下线按钮弹出确认框后调用 `POST /system/online-user/kickout`（传空数组）。 | P0 | 待启动 | 未测试 |

### 3.4 系统状态监控页

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-SET-FE-401** | `@/views/system/status.vue` | `/system/status` | 系统信息卡片（版本/运行时长）；组件状态卡片列表（数据库/Redis/Kafka，绿色=正常/红色=异常）；资源使用仪表盘（CPU/内存/磁盘，ECharts 仪表盘图）；刷新按钮 | 1. 页面加载调用 `GET /system/status`；2. 每60秒自动刷新；3. 组件状态异常时显示红色告警标识；4. ECharts 仪表盘展示 CPU/内存/磁盘使用率。 | P0 | 待启动 | 未测试 |