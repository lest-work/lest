# 前端 & 后端目录清理报告

> **清理日期**: 2026-06-03
> **目的**: 删除旧 RuoYi/ele-admin-plus 遗留文件，确保代码库干净

---

## 一、前端清理结果（`frontend-pc`）

### 1.1 已删除的目录/文件

| 删除项 | 文件数 | 说明 |
|-------|--------|------|
| `src/views/system/` | 38 | OA 用户/角色/部门/菜单/字典/通知管理 |
| `src/views/monitor/` | 17 | 系统监控（在线用户/操作日志/定时任务/缓存/服务状态）|
| `src/views/index/` | 9 | 旧仪表盘页 |
| `src/views/release/` | 2 | 旧发布管理页 |
| `src/views/profile/` | 3 | 旧个人中心页 |
| `src/layout/`（含 components/）| 20+ | 旧 ele-admin-plus 布局系统 |
| `src/components/icons/` | 1 | ele-admin-plus 图标重导出 |
| `src/components/MonacoEditor/` | 4 | 富文本编辑器 |
| `src/components/TinymceEditor/` | 2 | TinyMCE 编辑器 |
| `src/components/RegionsSelect/` | 2 | 地区选择器 |
| `src/utils/request.ts` | 1 | 旧 axios 封装 |
| `src/utils/token-util.ts` | 1 | 旧 token 工具 |
| `src/utils/page-title-util.ts` | 1 | 旧页面标题 |
| `src/utils/use-body-resize.ts` | 1 | 依赖旧 theme store |
| `src/utils/use-dict-data.ts` | 1 | 依赖旧 store + API |
| `src/utils/use-echarts.ts` | 1 | 依赖旧 theme store + ele-admin-plus |
| `src/utils/use-form-data.ts` | 1 | 表单数据 hook |
| `src/utils/use-mobile.ts` | 1 | 移动端检测 |
| `src/utils/use-page-tab.ts` | 1 | 依赖旧 theme store |
| `src/utils/use-permission.ts` | 1 | 依赖旧 store/modules/user |
| `src/utils/common.ts` | 1 | 旧通用工具 |
| `src/config/`（整个目录）| 2 | 旧全局配置 |
| `src/global-import.ts` | 1 | ele-admin-plus 入口 |
| `src/as-needed.ts` | 1 | 旧按需引入 |
| `vite.config.js` | 1 | 旧 vite 配置（含 EleAdminResolver）|
| `public/tinymce/` | 1+ | TinyMCE 语言包和皮肤 |
| `public/json/` | 1 | 地区数据 JSON |
| `src/styles/transition.scss` | 1 | 含 `.ele-*` 死类 |
| `src/api/` 子目录 | 30+ | `api/login/`、`api/system/`、`api/monitor/`、`api/dashboard/`、`api/profile/`、`api/layout/` |

### 1.2 已修改的文件

| 文件 | 修改内容 |
|------|---------|
| `src/main.ts` | 移除 `import '@/styles/index.scss'`（文件已删除）|
| `src/router/routes.ts` | 修正 NotFound 路由引用 `exception/404/index.vue` |
| `src/views/exception/403/index.vue` | 替换 `<ele-page>`/`<ele-text>` 为标准 Element Plus 组件 |
| `src/views/exception/404/index.vue` | 同上 |
| `src/views/exception/404.vue` | 已删除（合并到 index.vue）|
| `src/views/exception/403/components/icon-svg.vue` | 已删除 |
| `src/views/exception/404/components/icon-svg.vue` | 已删除 |

### 1.3 最终目录结构

```
frontend-pc/src/
├── App.vue                      ✅
├── main.ts                     ✅
├── api/                        ✅
│   ├── auth.ts                 ✅
│   ├── axios.ts                ✅
│   ├── board.ts                ✅
│   ├── project.ts              ✅
│   └── task.ts                 ✅
├── assets/                     ✅
├── components/
│   └── layout/
│       └── AppLayout.vue       ✅
├── router/
│   ├── index.ts               ✅
│   └── routes.ts               ✅
├── stores/
│   ├── board.ts               ✅
│   ├── project.ts             ✅
│   └── user.ts                ✅
├── styles/
│   └── variables.scss          ✅
└── views/
    ├── dashboard/              ✅
    ├── exception/
    │   ├── 403/index.vue     ✅
    │   └── 404/index.vue     ✅
    ├── inbox/                ✅
    ├── login/                 ✅
    ├── project/               ✅
    ├── settings/             ✅
    └── task/                 ✅
```

### 1.4 待补充的 Store

| 文件 | 状态 | 说明 |
|------|------|------|
| `src/stores/task.ts` | ❌ 缺失 | TaskList.vue 和 Kanban.vue 依赖，必须创建 |

---

## 二、后端清理结果（`backend`）

### 2.1 已删除的文件

| 模块 | 删除项 | 说明 |
|------|--------|------|
| `lest-system` | `SysDeptController.java` | OA 部门管理 Controller |
| `lest-system` | `SysPostController.java` | OA 岗位管理 Controller |
| `lest-system` | `ISysDeptService.java` | 部门服务接口 |
| `lest-system` | `ISysPostService.java` | 岗位服务接口 |
| `lest-system` | `SysDeptServiceImpl.java` | 部门服务实现 |
| `lest-system` | `SysPostServiceImpl.java` | 岗位服务实现 |
| `lest-system` | `SysDeptMapper.java` | 部门 Mapper |
| `lest-system` | `SysPostMapper.java` | 岗位 Mapper |
| `lest-system` | `SysDeptMapper.xml` | 部门 Mapper XML |
| `lest-system` | `SysPostMapper.xml` | 岗位 Mapper XML |
| `lest-system` | `SysDept.java`（domain）| domain 类在 lest-api-system 中，已保留 |
| `lest-system` | `SysPost.java`（domain）| 内部 domain 类，已删除 |

### 2.2 已修复的编译错误

| 文件 | 修复内容 |
|------|---------|
| `SysUserController.java` | 移除 `ISysDeptService`、`ISysPostService` 字段和调用；移除 `deptTree` 端点；移除 `postIds`/`posts` 响应字段 |
| `SysRoleController.java` | 移除 `ISysDeptService` 字段和调用；移除 `deptTree/{roleId}` 端点 |
| `SysUserServiceImpl.java` | 移除 `SysPostMapper`、`ISysDeptService` 字段；`selectUserPostGroup()` 返回空字符串；移除导入时 dept 数据范围校验 |

### 2.3 当前 lest-system 模块 Controller

| Controller | 用途 | V0.1 状态 |
|-----------|------|---------|
| `SysUserController.java` | 用户管理 | ✅ 保留（修复后）|
| `SysRoleController.java` | 角色管理 | ✅ 保留（修复后）|
| `SysMenuController.java` | 菜单管理 | ✅ 保留（V0.2 清理）|
| `SysNoticeController.java` | 系统通知 | ✅ 保留 |
| `SysProfileController.java` | 个人信息 | ✅ 保留 |
| `SysConfigController.java` | 系统配置 | ✅ 保留 |
| `SysDictTypeController.java` | 字典类型 | ✅ 保留 |
| `SysDictDataController.java` | 字典数据 | ✅ 保留 |
| `SysOperlogController.java` | 操作日志 | ✅ 保留（V0.2 清理）|
| `SysLogininforController.java` | 登录日志 | ✅ 保留（V0.2 清理）|
| `SysUserOnlineController.java` | 在线用户 | ✅ 保留（V0.2 清理）|
| `SysDashboardController.java` | 仪表盘 | ✅ 保留 |

---

## 三、清理后仍需处理的事项

### 3.1 遗留但不阻塞（V0.2 处理）

| 事项 | 说明 |
|------|------|
| `SysMenuController/RoleController` 等删除 | V0.2 才删除 |
| `SysOperlogController` 等日志 Controller | V0.2 才删除 |
| `sys_menu`、`sys_role`、`sys_user_role` 表 | V0.2 才处理 |
| `lest-visual/lest-monitor` 模块 | 独立监控应用，暂不动 |
| `lest-common-datascope` `@DataScope` 链路 | V0.2 才删除（硬编码依赖 `sys_dept` 表）|

### 3.2 仍存在的跨模块引用需注意

| 引用 | 位置 | 说明 |
|------|------|------|
| `SysDept`（`lest-api-system`）| `SysUser` 中嵌套 DTO | 保留 — 用于 API 边界传输 |
| `DataScopeAspect` | `lest-common-datascope` | 保留 — 但 `DATA_SCOPE_DEPT_AND_CHILD` 硬编码 `sys_dept` SQL |

---

## 五、第二轮清理（V1.1 — 2026-06-03）

### 5.1 已删除的 Maven 模块

| 模块 | 删除原因 |
|------|---------|
| `lest-modules/lest-open` | 只有 1 个 Application 类，空壳 |
| `lest-modules/lest-performance` | 只有 1 个 Application 类，空壳 |
| `lest-modules/lest-wakapi` | 只有 1 个 Application 类，空壳 |
| `lest-modules/lest-gateway` | 与顶层 `lest-gateway/` 重复，无 Java 文件 |
| `lest-visual`（含 `lest-monitor`） | 只有 2 个类，OA 风格监控系统 |

### 5.2 已删除的 OA 风格控制器

| 类型 | 删除的类 |
|------|---------|
| Controller | `SysConfigController`、`SysDictTypeController`、`SysDictDataController`、`SysMenuController`、`SysNoticeController`、`SysLogininforController`、`SysOperlogController`、`SysRoleController`、`SysUserOnlineController` |
| Service | `ISysConfigService`、`ISysDictTypeService`、`ISysDictDataService`、`ISysMenuService`、`ISysNoticeService`、`ISysNoticeReadService`、`ISysLogininforService`、`ISysOperLogService`、`ISysPermissionService`、`ISysRoleService`、`ISysUserOnlineService` 及所有 `*Impl` 实现 |
| Mapper | `SysConfigMapper`、`SysDictTypeMapper`、`SysDictDataMapper`、`SysMenuMapper`、`SysNoticeMapper`、`SysNoticeReadMapper`、`SysLogininforMapper`、`SysOperLogMapper`、`SysRoleMapper`、`SysRoleDeptMapper`、`SysRoleMenuMapper`、`SysUserPostMapper`、`SysUserRoleMapper` |
| Mapper XML | 对应的 13 个 XML 文件 |
| Domain | `SysConfig`、`SysMenu`、`SysNotice`、`SysNoticeRead`、`SysRoleDept`、`SysRoleMenu`、`SysUserOnline`、`SysUserPost`、`SysUserRole`、`SysRole`、`MetaVo`、`RouterVo`、`TreeSelect` |

### 5.3 已清理的 lest-api-system 文件

| 文件 | 删除原因 |
|------|---------|
| `domain/SysOperLog.java`、`SysDictData.java`、`SysDictType.java`、`SysDept.java`、`SysFile.java` | OA 风格，不属于敏捷平台 |
| `RemoteLogService.java`、`RemoteFileService.java` | 对应已删除的 OA 服务 |
| `RemoteFileFallbackFactory.java`、`RemoteLogFallbackFactory.java`、`RemoteUserFallbackFactory.java` | 引用已删除的类 |

### 5.4 已重写的文件

| 文件 | 修改 |
|------|------|
| `SysUserServiceImpl.java` | 完全重写，移除所有对 `SysDept/SysRole/SysUserRole/SysUserPost/ISysConfigService` 的引用 |
| `ISysUserService.java` | 移除 `insertUserAuth()` 方法 |
| `SysUserMapper.xml` | 移除所有 `LEFT JOIN sys_dept/sys_role/sys_user_role`；简化 resultMap |
| `RemoteUserService.java` | 移除 `fallbackFactory` 引用 |
| `pom.xml`（backend） | 移除 `<module>lest-visual</module>` |
| `pom.xml`（lest-modules） | 移除 `lest-open`、`lest-performance`、`lest-wakapi`、`lest-gateway`（重复） |

### 5.5 已删除的构建产物

| 目录 | 说明 |
|------|------|
| `jar/`、`logs/` | Maven 产物 + 运行时日志 |
| `lest-modules/*/bin/`、`build/`、`logs/`、`target/` | Gradle + Maven 构建残留 |
| `docker/lest-modules/`、`docker/lest-auth/jar/`、`docker/lest-gateway/jar/` | Docker 产物 |
| `docker/lest/` 整个目录 | 重复的 Docker 构建产物 |

### 5.6 最终后端模块结构

```
backend/
├── pom.xml                         ✅ Maven 父 POM
├── lest-common/                     ✅ 通用模块
│   ├── lest-common-core/           ✅
│   ├── lest-common-datasource/     ✅
│   ├── lest-common-datascope/      ✅
│   ├── lest-common-log/            ✅
│   ├── lest-common-redis/          ✅
│   ├── lest-common-security/       ✅
│   ├── lest-common-seata/          ✅
│   ├── lest-common-sensitive/      ✅
│   └── lest-common-swagger/        ✅
├── lest-api/                       ✅
│   └── lest-api-system/            ✅
├── lest-auth/                      ✅ 认证服务
├── lest-gateway/                   ✅ 网关
├── lest-modules/                   ✅ 业务模块
│   ├── lest-system/               ✅（精简后：仅 User/Profile）
│   ├── lest-project/              ✅
│   ├── lest-task/                 ✅
│   ├── lest-release/              ✅
│   ├── lest-file/                 ✅
│   ├── lest-ai/                   ✅
│   ├── lest-job/                  ✅
│   ├── lest-meeting/             ✅
│   ├── lest-notification/         ✅
│   └── lest-plugin/               ✅
└── docker/                         ✅ Docker 配置
    ├── docker-compose.yml
    ├── bin/
    ├── mysql/
    ├── minio/
    └── nacos/
```

## 六、第三轮清理（V1.2 — 2026-06-03）

### 6.1 再删除的空壳模块

| 模块 | 删除原因 |
|------|---------|
| `lest-modules/lest-ai` | 只有 1 个 Application 类 |
| `lest-modules/lest-notification` | 只有 1 个 Application 类 |
| `lest-modules/lest-meeting` | 只有 1 个 Application 类 |
| `lest-modules/lest-plugin` | 只有 1 个 Application 类 |

### 6.2 删除的 Common 模块

| 模块 | 删除原因 |
|------|---------|
| `lest-common/lest-common-seata` | 分布式事务，V0.1 不需要 |
| `lest-common/lest-common-sensitive` | 敏感词过滤，V0.1 不需要 |

### 6.3 修复的跨模块引用

| 文件 | 问题 | 修复方式 |
|------|------|---------|
| `SysRecordLogService.java` | 引用已删除的 `RemoteLogService` | 重写为日志输出 |
| `AsyncLogService.java` | 引用已删除的 `RemoteLogService`/`SysOperLog` | 重写为日志输出 |
| `LogAspect.java` | 引用已删除的 `SysOperLog` | 重写为结构化日志输出 |
| `lest-api-system/SysLogininfor.java` | 残留 OA 登录日志 domain | 已在上轮删除 `RemoteLogService` 时保留，本轮确认无用后保留（无引用方） |

### 6.4 清理的 Gateway 组件

| 类别 | 删除的文件 |
|------|---------|
| Captcha | `CaptchaConfig.java`、`KaptchaTextCreator.java`、`SimpleLineNoise.java`、`ValidateCodeFilter.java`、`ValidateCodeHandler.java`、`ValidateCodeService.java`、`ValidateCodeServiceImpl.java`、`CaptchaProperties.java` |
| Security | `BlackListUrlFilter.java`、`XssFilter.java`、`XssProperties.java`、`SpringDocConfig.java` |
| Fallback | `SentinelFallbackHandler.java` |
| 配置类 | `GatewayConfig.java`（重写为空）、`RouterFunctionConfiguration.java`（重写为 /health 端点） |
| application.yml | 移除过时的 `captcha`、`Sentinel` 配置块；简化 `ignore.whites` |

### 6.5 更新的 POM

| 文件 | 修改 |
|------|------|
| `lest-modules/pom.xml` | 移除 `lest-ai`、`lest-notification`、`lest-meeting`、`lest-plugin` |
| `lest-common/pom.xml` | 移除 `lest-common-seata`、`lest-common-sensitive` |

### 6.6 最终后端模块结构

```
backend/
├── pom.xml                         ✅ Maven 父 POM
├── lest-common/                     ✅ 通用模块（精简为 7 个）
│   ├── lest-common-core/           ✅
│   ├── lest-common-datasource/     ✅
│   ├── lest-common-datascope/      ✅（V0.2 清理）
│   ├── lest-common-log/            ✅（精简后直接日志输出）
│   ├── lest-common-redis/          ✅
│   ├── lest-common-security/       ✅
│   └── lest-common-swagger/        ✅
├── lest-api/                       ✅
│   └── lest-api-system/            ✅（仅保留 User/Role/LoginUser）
├── lest-auth/                      ✅ 认证服务（精简后）
├── lest-gateway/                   ✅ 网关（精简后，移除 Captcha/XSS/Sentinel）
├── lest-modules/                   ✅ 业务模块（精简为 6 个）
│   ├── lest-system/              ✅（仅 User + Profile）
│   ├── lest-project/             ✅
│   ├── lest-task/                ✅
│   ├── lest-release/             ✅
│   ├── lest-file/                ✅
│   └── lest-job/                 ✅（仅基础定时任务）
└── docker/                         ✅ Docker 配置
    ├── docker-compose.yml
    ├── bin/
    ├── mysql/
    ├── minio/
    └── nacos/
```

## 七、版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-03 | 初始清理报告（前端清理 + 首批后端删除） |
| V1.1 | 2026-06-03 | 后端大清理：删除空壳模块（4个）、OA 控制器（9个）、OA 服务/Mapper（20+）、OA Domain（13个）、OA API（5个）；清理所有构建产物目录；精简 lest-system 为仅 User+Profile |
| V1.2 | 2026-06-03 | 再删 4 个空壳模块（ai/notification/meeting/plugin）；删除 2 个无用 common（seata/sensitive）；清理 Gateway 过时组件（Captcha/XSS/Sentinel/Swagger 共 14 个文件）；修复跨模块引用（RemoteLogService/SysOperLog）；重写 LogAspect/AsyncLogService/SysRecordLogService 为结构化日志；精简 application.yml |
