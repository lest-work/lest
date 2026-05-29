-- ----------------------------
-- lest-auth 数据库初始化脚本
-- 与 sql/01_lest_platform_init.sql 保持 schema 同步
-- ----------------------------

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lest_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lest_platform;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`      VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `email`         VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `sex`           TINYINT      NOT NULL DEFAULT 0 COMMENT '性别：0=未知, 1=男, 2=女',
    `org_id`        BIGINT       DEFAULT NULL COMMENT '所属机构ID',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
    `last_login_at`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code`   VARCHAR(64) NOT NULL COMMENT '角色编码',
    `role_name`   VARCHAR(64) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
    `is_super`    TINYINT     NOT NULL DEFAULT 0 COMMENT '是否超管：1=是，0=否',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '排序号',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- ----------------------------
-- 3. 菜单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级',
    `menu_name`   VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `menu_type`   TINYINT     NOT NULL DEFAULT 1 COMMENT '类型：1=目录, 2=菜单, 3=按钮',
    `path`        VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
    `component`   VARCHAR(255) DEFAULT NULL COMMENT '组件路径（含前导/）',
    `permission`  VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    `icon`        VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '排序号',
    `visible`     TINYINT     NOT NULL DEFAULT 1 COMMENT '是否显示：1=是, 0=否',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
    `keep_alive`  TINYINT     NOT NULL DEFAULT 0 COMMENT '是否缓存：1=是, 0=否',
    `always_show` TINYINT     NOT NULL DEFAULT 0 COMMENT '是否总是显示',
    `redirect`    VARCHAR(255) DEFAULT NULL COMMENT '重定向路径',
    `meta`        TEXT        DEFAULT NULL COMMENT '路由元数据（JSON字符串）',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- ----------------------------
-- 4. 机构表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_organization` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '机构ID',
    `parent_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '父机构ID',
    `org_name`    VARCHAR(64) NOT NULL COMMENT '机构名称',
    `org_code`    VARCHAR(64) NOT NULL COMMENT '机构编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    UNIQUE KEY `uk_org_code` (`org_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统机构表';

-- ----------------------------
-- 5. 字典类型表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dictionary` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `dict_code`   VARCHAR(64) NOT NULL COMMENT '字典编码',
    `dict_name`   VARCHAR(64) NOT NULL COMMENT '字典名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典类型表';

-- ----------------------------
-- 6. 字典数据表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dictionary_data` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    `dict_id`    BIGINT      NOT NULL COMMENT '字典ID',
    `data_key`   VARCHAR(64) NOT NULL COMMENT '数据键',
    `data_value` VARCHAR(255) DEFAULT NULL COMMENT '数据值',
    `label`      VARCHAR(64) NOT NULL COMMENT '显示标签',
    `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`     TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1=正常, 0=禁用',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_dict_id` (`dict_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典数据表';

-- ----------------------------
-- 7. 用户角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 8. 角色菜单关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- 9. 验证码表（过期时间由Redis TTL控制，不存储在数据库）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `auth_captcha` (
    `uuid` VARCHAR(64)  NOT NULL COMMENT '验证码UUID',
    `code` VARCHAR(16)  NOT NULL COMMENT '验证码内容',
    PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 角色
INSERT IGNORE INTO `sys_role` (`id`, `role_code`, `role_name`, `description`, `status`, `is_super`, `sort`) VALUES
(1, 'SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 1, 1, 0),
(2, 'USER',        '普通用户',   '系统普通用户',                 1, 0, 1);

-- 用户（admin 密码: 123456，BCrypt加密）
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'admin@lest.com', 1);

-- 用户角色
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 机构
INSERT IGNORE INTO `sys_organization` (`id`, `parent_id`, `org_name`, `org_code`, `sort`, `status`) VALUES
(1, 0, 'Lest科技', 'LEST', 0, 1);

-- 菜单（component 字段含前导 /，用于前端动态路由解析）
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`, `visible`) VALUES
(1,  0, '工作台',    1, '/dashboard',           'Layout',                          'HomeFilled',    0, 1, 1),
(2,  0, '系统管理',  1, '/system',              'Layout',                          'Setting',       1, 1, 1),
(3,  0, '日志管理',  1, '/log',                 'Layout',                          'Document',      2, 1, 1),
(10, 1, '工作台首页',2, '/dashboard/index',     '/dashboard/index',                '',              0, 1, 1),
(20, 2, '用户管理',  2, '/system/user',         '/system/user/index',              'User',          0, 1, 1),
(21, 2, '角色管理',  2, '/system/role',         '/system/role/index',              'UserFilled',    1, 1, 1),
(22, 2, '菜单管理',  2, '/system/menu',         '/system/menu/index',              'Menu',          2, 1, 1),
(23, 2, '机构管理',  2, '/system/organization', '/system/organization/index',      'OfficeBuilding',3, 1, 1),
(24, 2, '字典管理',  2, '/system/dictionary',   '/system/dictionary/index',        'Collection',    4, 1, 1),
(25, 2, '在线用户',  2, '/system/online-user',  '/system/online-user/index',       'Monitor',       5, 1, 1),
(30, 3, '操作日志',  2, '/log/operation-record','/system/operation-record/index',  'List',          0, 1, 1),
(31, 3, '登录日志',  2, '/log/login-record',    '/system/login-record/index',      'Tickets',       1, 1, 1);

-- 角色菜单
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) SELECT 1, `id` FROM `sys_menu`;

-- 字典
INSERT IGNORE INTO `sys_dictionary` (`id`, `dict_code`, `dict_name`, `description`, `status`) VALUES
(1, 'sex',            '性别',     '用户性别字典', 1),
(2, 'status',         '状态',     '通用状态字典', 1),
(3, 'operation_type', '操作类型', '操作日志类型', 1),
(4, 'login_type',     '登录类型', '登录日志类型', 1);

-- 字典数据
INSERT IGNORE INTO `sys_dictionary_data` (`dict_id`, `data_key`, `data_value`, `label`, `sort`, `status`) VALUES
(1, '0', '0', '未知',      0, 1),
(1, '1', '1', '男',        1, 1),
(1, '2', '2', '女',        2, 1),
(2, '0', '0', '禁用',      0, 1),
(2, '1', '1', '正常',      1, 1),
(3, '0', '0', '登录',      0, 1),
(3, '1', '1', '登出',      1, 1),
(3, '2', '2', '新增',      2, 1),
(3, '3', '3', '修改',      3, 1),
(3, '4', '4', '删除',      4, 1),
(3, '5', '5', '查询',      5, 1),
(4, '0', '0', '登录成功',  0, 1),
(4, '1', '1', '登录失败',  1, 1),
(4, '2', '2', '退出登录',  2, 1),
(4, '3', '3', '刷新Token', 3, 1);

-- ----------------------------
-- 增量迁移（已有数据库执行以下语句）
-- ----------------------------
-- ALTER TABLE `sys_user` ADD COLUMN `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间' AFTER `status`;
-- ALTER TABLE `sys_menu` ADD COLUMN `meta` TEXT DEFAULT NULL COMMENT '路由元数据（JSON字符串）' AFTER `redirect`;
-- ALTER TABLE `sys_organization` ADD COLUMN `description` VARCHAR(255) DEFAULT NULL COMMENT '描述' AFTER `org_code`;
