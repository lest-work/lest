---
description: LEST Platform 数据库命名、SQL 和初始化规范
---

# 数据库规范

## 1. 命名规范

- 表名：全小写下划线，例如 `sys_user`、`project`、`task`
- 字段名：全小写下划线，例如 `user_id`、`create_time`
- 索引名：`idx_{table}_{column}` 或 `uk_{table}_{column}`（唯一）
- 主键：统一使用 `id` BIGINT AUTO_INCREMENT（系统表用 `xxx_id`）

## 2. 标准字段（继承 BaseEntity）

所有业务表必须包含以下字段：

```sql
create_by    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
create_time  DATETIME     COMMENT '创建时间',
update_by    VARCHAR(64)  DEFAULT '' COMMENT '更新者',
update_time  DATETIME     COMMENT '更新时间',
remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',
```

软删除字段：

```sql
deleted      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标志 0=未删除 1=已删除',
```

## 3. 建表规范

```sql
CREATE TABLE IF NOT EXISTS `xxx` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name`        VARCHAR(100) NOT NULL COMMENT '名称',
  `status`      TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态 1=活跃 2=已归档',
  `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted`     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='xxx表';
```

## 4. sys_menu 规范（RuoYi 标准）

菜单图标使用 Element Plus 图标，前缀 `IconEl`：

- `menu_type`: C=目录，M=菜单，F=按钮
- `perms`：权限字符串格式 `{module}:{resource}:{action}`，必须全局唯一
- `icon`：目录/菜单用 `IconEl{Name}`，按钮用 `#`

```sql
-- 目录（menu_type='C'）
INSERT INTO sys_menu VALUES (2000, '项目管理', 0, 5, 'project', NULL, '', '', 1, 0, 'C', '0', '0', '', 'IconElFolderOpened', 'admin', NOW(), '', NULL, '项目管理目录');

-- 菜单（menu_type='M'）
INSERT INTO sys_menu VALUES (2001, '项目列表', 2000, 1, 'list', 'project/index', '', '', 1, 0, 'M', '0', '0', 'project:project:list', 'IconElList', 'admin', NOW(), '', NULL, '项目列表');

-- 按钮（menu_type='F'，icon='#'）
INSERT INTO sys_menu VALUES (2100, '项目查询', 2001, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'project:project:query', '#', 'admin', NOW(), '', NULL, '');
```

## 5. 初始化 SQL 文件

位置：`backend/docker/mysql/db/01_lest_platform_init.sql`

规范：
- 所有建表语句加 `DROP TABLE IF EXISTS` + `CREATE TABLE IF NOT EXISTS`
- INSERT 数据加 `INSERT INTO ... VALUES` 完整列名列表
- 日期字段使用 `NOW()` 或固定时间戳字符串
- 字典类型（sys_dict_type + sys_dict_data）成对出现
- `sys_oper_type` 标准值：0=其它，1=新增，2=修改，3=删除，4=授权，5=导出，6=导入，7=强退，8=生成代码，9=清空

## 6. 字典值规范

```sql
-- sys_dict_type（字典类型）
INSERT INTO sys_dict_type VALUES (N, '业务类型', 'sys_oper_type', '0', 'admin', NOW(), '', NULL, '系统操作类型');

-- sys_dict_data（字典数据，dict_sort 从小到大连续）
INSERT INTO sys_dict_data VALUES (N, 0, '其它',     '0', 'sys_oper_type', '', 'info',    'N', '0', 'admin', NOW(), '', NULL, '其它操作');
INSERT INTO sys_dict_data VALUES (N, 1, '新增',     '1', 'sys_oper_type', '', 'info',    'N', '0', 'admin', NOW(), '', NULL, '新增操作');
```
