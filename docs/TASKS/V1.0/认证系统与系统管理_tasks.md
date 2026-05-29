# 认证系统与系统管理开发任务列表

> **最后更新**：2026-05-29 | **整体状态**：🟢 基础功能已完成

## 模块上下文

- **微服务模块**：`lest-auth`（端口: 8096）/ `lest-system`（端口: 8081）
- **主目录**：`backend/lest-auth` / `backend/lest-modules/lest-system`
- **包路径**：`com.lest.auth` / `com.lest.system`
- **初始化 SQL**：`backend/docker/mysql/db/01_lest_platform_init.sql`
- **前端 PC 目录**：`frontend-pc/src/views/system/`

> **实现说明**：本模块基于 RuoYi Cloud 框架实现（非独立设计），使用 MyBatis（非 Flyway），
> 数据库统一初始化于 `01_lest_platform_init.sql`，响应格式为 `{code, msg, data/rows}`。

---

## 1. 数据库 DDL 任务

| 任务 ID | 表名 | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AUTH-DDL-101** | `sys_user` | 用户表（含 dept_id、user_name、nick_name、email、phonenumber、sex、avatar、password、status、del_flag、login_ip、login_date） | P0 | 🟢 已完成 | `01_lest_platform_init.sql` 含建表+初始 admin 数据 |
| **TSK-AUTH-DDL-102** | `sys_role` / `sys_menu` | 角色表、菜单表（含 menu_type C/M/F、path、component、perms、icon、is_frame） | P0 | 🟢 已完成 | 含完整菜单树初始数据，icon 前缀 `IconEl` |
| **TSK-AUTH-DDL-103** | `sys_user_role` / `sys_role_menu` / `sys_role_dept` / `sys_user_post` | 关联表 | P0 | 🟢 已完成 | |
| **TSK-AUTH-DDL-104** | `sys_dept` / `sys_post` | 部门表、岗位表 | P0 | 🟢 已完成 | |
| **TSK-AUTH-DDL-105** | `sys_dict_type` / `sys_dict_data` | 字典类型/数据表 | P0 | 🟢 已完成 | 含 sys_oper_type 等标准字典 |
| **TSK-AUTH-DDL-106** | `sys_config` | 系统参数配置表 | P0 | 🟢 已完成 | |
| **TSK-AUTH-DDL-107** | `sys_logininfor` / `sys_oper_log` | 登录日志、操作日志表 | P1 | 🟢 已完成 | |
| **TSK-AUTH-DDL-108** | `sys_notice` / `sys_notice_read` | 通知公告表 | P1 | 🟢 已完成 | 含 3 条初始公告 |
| **TSK-AUTH-DDL-109** | `sys_job` / `sys_job_log` / `QRTZ_*` | 定时任务表（11张 Quartz 表） | P2 | 🟢 已完成 | |

---

## 2. 后端 API 任务

### 2.1 认证接口（lest-auth）

| 任务 ID | Endpoint | 说明 | 优先级 | 状态 | 备注 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AUTH-API-101** | `GET /auth/captcha` | 获取图形验证码，返回 UUID + Base64 图片 | P0 | 🟢 已完成 | |
| **TSK-AUTH-API-102** | `POST /auth/login` | 用户名密码+验证码登录，返回 token | P0 | 🟢 已完成 | |
| **TSK-AUTH-API-103** | `DELETE /auth/logout` | 退出登录，清除 Redis Token | P0 | 🟢 已完成 | |
| **TSK-AUTH-API-104** | `POST /auth/refresh` | 刷新 Token | P1 | 🟢 已完成 | |
| **TSK-AUTH-API-105** | `GET /auth/user/info` | 获取当前登录用户信息+权限+菜单 | P0 | 🟢 已完成 | |

### 2.2 系统管理接口（lest-system）

| 任务 ID | Controller | 路径前缀 | 主要功能 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-API-201** | `SysUserController` | `/system/user` | 用户 CRUD、状态变更、重置密码、导入导出、授权角色 | P0 | 🟢 已完成 |
| **TSK-SYS-API-202** | `SysRoleController` | `/system/role` | 角色 CRUD、数据权限、授权用户 | P0 | 🟢 已完成 |
| **TSK-SYS-API-203** | `SysMenuController` | `/system/menu` | 菜单 CRUD、路由树 | P0 | 🟢 已完成 |
| **TSK-SYS-API-204** | `SysDeptController` | `/system/dept` | 部门 CRUD、树形结构 | P0 | 🟢 已完成 |
| **TSK-SYS-API-205** | `SysPostController` | `/system/post` | 岗位 CRUD | P1 | 🟢 已完成 |
| **TSK-SYS-API-206** | `SysDictTypeController` | `/system/dict/type` | 字典类型 CRUD | P1 | 🟢 已完成 |
| **TSK-SYS-API-207** | `SysDictDataController` | `/system/dict/data` | 字典数据 CRUD | P1 | 🟢 已完成 |
| **TSK-SYS-API-208** | `SysConfigController` | `/system/config` | 系统参数 CRUD | P1 | 🟢 已完成 |
| **TSK-SYS-API-209** | `SysNoticeController` | `/system/notice` | 公告 CRUD | P2 | 🟢 已完成 |
| **TSK-SYS-API-210** | `SysOperlogController` | `/monitor/operlog` | 操作日志查询/清空 | P1 | 🟢 已完成 |
| **TSK-SYS-API-211** | `SysLogininforController` | `/monitor/logininfor` | 登录日志查询/清空 | P1 | 🟢 已完成 |
| **TSK-SYS-API-212** | `SysUserOnlineController` | `/monitor/online` | 在线用户查询/强退 | P2 | 🟢 已完成 |
| **TSK-SYS-API-213** | `SysJobController` | `/job` | 定时任务 CRUD/执行/状态 | P2 | 🟢 已完成 |
| **TSK-SYS-API-214** | `SysDashboardController` | `/system/dashboard` | 仪表盘最新动态(`/activities`)、小组成员(`/members`) | P1 | 🟢 已完成 | 新增，Cascade 2026-05-29 |

---

## 3. 前端 PC 端任务

| 任务 ID | 页面/组件路径 | 功能 | 优先级 | 状态 |
| :--- | :--- | :--- | :--- | :--- |
| **TSK-SYS-FE-301** | `views/system/user/` | 用户管理页（列表/新增/编辑/导入/授权角色） | P0 | 🟢 已完成 |
| **TSK-SYS-FE-302** | `views/system/role/` | 角色管理页（含菜单权限分配/数据权限） | P0 | 🟢 已完成 |
| **TSK-SYS-FE-303** | `views/system/menu/` | 菜单管理页（树形） | P0 | 🟢 已完成 |
| **TSK-SYS-FE-304** | `views/system/dept/` | 部门管理页（树形） | P0 | 🟢 已完成 |
| **TSK-SYS-FE-305** | `views/system/post/` | 岗位管理页 | P1 | 🟢 已完成 |
| **TSK-SYS-FE-306** | `views/system/dict/` | 字典管理页 | P1 | 🟢 已完成 |
| **TSK-SYS-FE-307** | `views/system/config/` | 系统参数页 | P1 | 🟢 已完成 |
| **TSK-SYS-FE-308** | `views/system/notice/` | 公告管理页 | P2 | 🟢 已完成 |
| **TSK-SYS-FE-309** | `views/monitor/operlog/` | 操作日志页 | P1 | 🟢 已完成 |
| **TSK-SYS-FE-310** | `views/monitor/logininfor/` | 登录日志页 | P1 | 🟢 已完成 |
| **TSK-SYS-FE-311** | `views/monitor/online/` | 在线用户页 | P2 | 🟢 已完成 |
| **TSK-SYS-FE-312** | `views/monitor/job/` | 定时任务页 | P2 | 🟢 已完成 |
| **TSK-SYS-FE-313** | `views/index/components/activities-card.vue` | 首页最新动态卡片（接入 dashboard API 真实数据） | P1 | 🟢 已完成 | Cascade 2026-05-29 |
| **TSK-SYS-FE-314** | `views/index/components/user-list.vue` | 首页小组成员卡片（接入 dashboard API，在线状态） | P1 | 🟢 已完成 | Cascade 2026-05-29 |

---

## 4. 待完成项（后续需求）

| 任务 ID | 描述 | 优先级 |
| :--- | :--- | :--- |
| **TSK-SYS-TODO-401** | 个人中心页（修改密码、修改头像、查看个人操作记录） | P1 |
| **TSK-SYS-TODO-402** | 多租户支持（tenant_id 隔离） | P2 |
| **TSK-SYS-TODO-403** | 短信/邮件验证码登录 | P2 |
| **TSK-SYS-TODO-404** | OAuth2.0 第三方登录（GitLab / GitHub） | P3 |

---
