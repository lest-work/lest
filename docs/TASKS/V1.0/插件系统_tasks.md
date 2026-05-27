# 插件系统开发任务列表

## 模块上下文

- **微服务模块**：`lest-plugin`（端口: 8011）
- **主目录**：`backend/lest-modules/lest-plugin`
- **包路径**：`com.lest.plugin`
- **数据库 Schema**：`lest_plugin`（Flyway 目录：`src/main/resources/db/migration/plugin`）
- **前端 PC 目录**：`frontend-pc/src/views/system/plugin/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节与约束规范 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PLUG-DDL-101** | DDL | `V1.0.0__init_plugin_tables.sql` | 创建 `plugin_info` 表：`id`(BIGINT PK)、`plugin_id`(VARCHAR 64, UNIQUE, 插件唯一标识)、`name`(VARCHAR 128)、`version`(VARCHAR 32)、`description`(VARCHAR 512)、`author`(VARCHAR 128)、`jar_path`(VARCHAR 512, Jar 包存储路径)、`status`(VARCHAR 16, installed/enabled/disabled/error)、`config`(JSON, 插件配置项)、`installed_at`(DATETIME)、`updated_at`。建立唯一索引 `uk_plugin_id`、普通索引 `idx_status`。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-DDL-102** | DDL | `V1.0.1__init_plugin_permission_log.sql` | 创建 `plugin_permission` 表：`id`、`plugin_id`(VARCHAR 64)、`permission`(VARCHAR 64, 如 read_task/write_task/send_notification)、`granted`(TINYINT DEFAULT 0)、`granted_at`(DATETIME)，建立唯一索引 `uk_plugin_permission`(plugin_id, permission)。创建 `plugin_call_log` 表：`id`、`plugin_id`(VARCHAR 64)、`method`(VARCHAR 128)、`params`(TEXT)、`result`(TEXT)、`duration_ms`(INT)、`status`(VARCHAR 16)、`created_at`，建立索引 `idx_plugin_id`、`idx_created_at`。 | P1 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 插件生命周期管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PLUG-API-101** | `PluginController` | `GET /plugin/page` | Header: Bearer Token（需 admin 权限）；Query: `name`(模糊)、`status`(可选)、`page`、`size` | `{ records: [{ id, pluginId, name, version, description, author, status, installedAt }], total, page, size }` | 分页查询已安装插件列表；status 枚举：installed/enabled/disabled/error。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-API-102** | `PluginController` | `POST /plugin/install` | Header: Bearer Token（需 admin 权限）；Body: `multipart/form-data`，字段 `file`（.jar）、`config`(JSON 字符串，可选) | `{ pluginId, name, version, status }` | 1. 接收 Jar 包，存储到插件目录（`plugins/{pluginId}/`）；2. 解析 Jar 包中的 `plugin.json` 元数据；3. 使用自定义 `PluginClassLoader` 加载 Jar（双亲委派重写，隔离宿主依赖）；4. 调用插件 `onInstall()` 生命周期方法；5. 写入 `plugin_info` 表，status 设为 installed。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-API-103** | `PluginController` | `PUT /plugin/{pluginId}/enable` | Path: `pluginId` | `{ code: 200, data: null }` | 1. 检查插件 status 为 installed 或 disabled；2. 调用插件 `onEnable()` 生命周期方法；3. 注册插件路由到 `RequestMappingHandlerMapping`（若插件有 `@PluginController`）；4. 更新 status 为 enabled。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-API-104** | `PluginController` | `PUT /plugin/{pluginId}/disable` | Path: `pluginId` | `{ code: 200, data: null }` | 1. 调用插件 `onDisable()` 生命周期方法；2. 从 `RequestMappingHandlerMapping` 注销插件路由；3. 更新 status 为 disabled。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-API-105** | `PluginController` | `DELETE /plugin/{pluginId}` | Path: `pluginId` | `{ code: 200, data: null }` | 1. 先执行 disable 流程；2. 调用插件 `onUninstall()` 生命周期方法；3. 卸载 ClassLoader，删除 Jar 文件；4. 删除 `plugin_info`、`plugin_permission` 记录。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-API-106** | `PluginController` | `GET /plugin/{pluginId}/config` | Path: `pluginId` | `{ config: object, schema: object }` | 返回插件当前配置值和配置 Schema（从 plugin.json 中读取 configSchema）；用于前端动态渲染配置表单。 | P1 | 待启动 | 未测试 |
| **TSK-PLUG-API-107** | `PluginController` | `PUT /plugin/{pluginId}/config` | Path: `pluginId`；Body: `{ config: object }` | `{ code: 200, data: null }` | 更新插件配置；按 configSchema 校验配置项类型和必填；更新 `plugin_info.config` 字段；调用插件 `onConfigUpdate(config)` 方法通知插件配置变更。 | P1 | 待启动 | 未测试 |

### 2.2 插件权限管理接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PLUG-API-201** | `PluginPermissionController` | `GET /plugin/{pluginId}/permission` | Path: `pluginId` | `{ permissions: [{ permission, description, granted, grantedAt }] }` | 查询插件申请的权限列表及授权状态；权限列表从 plugin.json 的 `requiredPermissions` 字段读取。 | P1 | 待启动 | 未测试 |
| **TSK-PLUG-API-202** | `PluginPermissionController` | `PUT /plugin/{pluginId}/permission` | Path: `pluginId`；Body: `{ permissions: [{ permission, granted }] }` | `{ code: 200, data: null }` | 批量更新插件权限授权状态；更新 `plugin_permission` 表；插件调用 `PluginContext` API 时实时校验权限。 | P1 | 待启动 | 未测试 |

### 2.3 插件路由转发接口

| 任务 ID | Controller | 请求方式 & 路径 | 入参 | 响应格式 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PLUG-API-301** | `PluginProxyController` | `ANY /api/v1/plugins/{pluginId}/{path}` | Path: `pluginId`、`path`；其余参数透传 | 由插件自定义 | 1. 校验插件 status 为 enabled；2. 校验请求者有权限调用该插件（可配置插件是否需要登录）；3. 通过反射调用插件 `@PluginController` 中对应路由方法；4. 写入 `plugin_call_log`；5. 返回插件方法的返回值。 | P0 | 待启动 | 未测试 |

### 2.4 PluginContext SDK（宿主 API 桥接）

| 任务 ID | 类路径 | 说明 | 实现要点 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PLUG-SDK-401** | `com.lest.plugin.sdk.PluginContext` | 插件调用宿主服务的统一入口接口 | 定义接口方法：`getTask(taskId)`、`createTask(request)`、`sendNotification(event, data)`、`getCurrentUser()`、`getProjectMembers(projectId)` 等；实现类通过 Feign 调用对应微服务；调用前校验插件是否持有对应权限（查 `plugin_permission` 表），无权限抛 `PluginPermissionDeniedException`。 | P0 | 待启动 | 未测试 |


---

## 3. 前端 PC 端任务

### 3.1 插件市场/管理页面

| 任务 ID | 页面/组件路径 | 路由路径 | 页面元素 | 交互与 API 绑定 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-PLUG-FE-101** | `@/views/system/plugin/index.vue` | `/system/plugin` | 顶部：插件名称搜索框、状态筛选下拉（全部/已启用/已禁用/错误）、安装插件按钮；插件卡片网格：插件图标、名称、版本、描述、作者、状态标签、启用/禁用开关、配置按钮、卸载按钮；底部：分页组件 | 1. 页面加载调用 `GET /plugin/page`；2. 启用/禁用开关调用 `PUT /plugin/{pluginId}/enable` 或 `PUT /plugin/{pluginId}/disable`；3. 安装插件按钮打开 `PluginInstallDialog`；4. 配置按钮打开 `PluginConfigDialog`；5. 卸载按钮弹出确认框后调用 `DELETE /plugin/{pluginId}`；6. 按钮权限控制：`plugin:install`、`plugin:manage`。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-FE-102** | `@/views/system/plugin/components/PluginInstallDialog.vue` | — | 弹窗：文件上传区域（拖拽或点击选择 .jar）、插件预览信息（解析后展示名称/版本/描述/所需权限）、安装按钮、取消按钮 | 1. 选择 Jar 文件后前端解析 Jar 包内的 `plugin.json` 预览插件信息（使用 JSZip 库）；2. 点击安装调用 `POST /plugin/install`（multipart 上传）；3. 安装成功后刷新插件列表。 | P0 | 待启动 | 未测试 |
| **TSK-PLUG-FE-103** | `@/views/system/plugin/components/PluginConfigDialog.vue` | — | 弹窗：插件名称展示、动态配置表单（根据 configSchema 渲染不同类型的表单项：文本/密码/开关/下拉选择）、权限授权列表（每条权限含描述和授权开关）、保存按钮、取消按钮 | 1. 打开时调用 `GET /plugin/{pluginId}/config` 获取配置 Schema 和当前值，调用 `GET /plugin/{pluginId}/permission` 获取权限列表；2. 根据 configSchema 动态渲染表单（type: string/password/boolean/select）；3. 保存时调用 `PUT /plugin/{pluginId}/config` 和 `PUT /plugin/{pluginId}/permission`。 | P1 | 待启动 | 未测试 |