-- ----------------------------
-- lest-auth 数据库初始化脚本
-- ----------------------------

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lest_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lest_platform;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(32) NOT NULL COMMENT '用户名',
    `password` varchar(128) NOT NULL COMMENT '密码',
    `nickname` varchar(64) DEFAULT NULL COMMENT '用户昵称',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `sex` tinyint DEFAULT '0' COMMENT '性别: 0=未知, 1=男, 2=女',
    `status` tinyint DEFAULT '1' COMMENT '状态: 1=正常, 0=禁用',
    `org_id` bigint DEFAULT NULL COMMENT '机构ID',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code` varchar(32) NOT NULL COMMENT '角色编码',
    `role_name` varchar(64) NOT NULL COMMENT '角色名称',
    `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
    `status` tinyint DEFAULT '1' COMMENT '状态: 1=正常, 0=禁用',
    `sort` int DEFAULT '0' COMMENT '排序号',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';

-- ----------------------------
-- 3. 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
    `menu_name` varchar(64) NOT NULL COMMENT '菜单名称',
    `menu_type` tinyint NOT NULL COMMENT '菜单类型: 1=目录, 2=菜单, 3=按钮',
    `path` varchar(128) DEFAULT NULL COMMENT '路由路径',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `permission` varchar(128) DEFAULT NULL COMMENT '权限标识',
    `icon` varchar(64) DEFAULT NULL COMMENT '菜单图标',
    `sort` int DEFAULT '0' COMMENT '排序号',
    `visible` tinyint DEFAULT '1' COMMENT '是否可见: 1=可见, 0=隐藏',
    `status` tinyint DEFAULT '1' COMMENT '状态: 1=正常, 0=禁用',
    `keep_alive` tinyint DEFAULT '0' COMMENT '是否缓存: 1=缓存, 0=不缓存',
    `always_show` tinyint DEFAULT '0' COMMENT '是否总是显示: 1=是, 0=否',
    `redirect` varchar(128) DEFAULT NULL COMMENT '重定向地址',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_menu_type` (`menu_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';

-- ----------------------------
-- 4. 机构表
-- ----------------------------
DROP TABLE IF EXISTS `sys_organization`;
CREATE TABLE `sys_organization` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '机构ID',
    `parent_id` bigint DEFAULT '0' COMMENT '父机构ID',
    `org_name` varchar(64) NOT NULL COMMENT '机构名称',
    `org_code` varchar(32) NOT NULL COMMENT '机构编码',
    `sort` int DEFAULT '0' COMMENT '排序号',
    `status` tinyint DEFAULT '1' COMMENT '状态: 1=正常, 0=禁用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_code` (`org_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统机构表';

-- ----------------------------
-- 5. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `dict_code` varchar(32) NOT NULL COMMENT '字典编码',
    `dict_name` varchar(64) NOT NULL COMMENT '字典名称',
    `description` varchar(255) DEFAULT NULL COMMENT '字典描述',
    `status` tinyint DEFAULT '1' COMMENT '状态: 1=正常, 0=禁用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典类型表';

-- ----------------------------
-- 6. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dictionary_data`;
CREATE TABLE `sys_dictionary_data` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    `dict_id` bigint NOT NULL COMMENT '字典类型ID',
    `data_key` varchar(64) NOT NULL COMMENT '数据键',
    `data_value` varchar(255) NOT NULL COMMENT '数据值',
    `label` varchar(64) DEFAULT NULL COMMENT '显示标签',
    `sort` int DEFAULT '0' COMMENT '排序号',
    `status` tinyint DEFAULT '1' COMMENT '状态: 1=正常, 0=禁用',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_dict_id` (`dict_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典数据表';

-- ----------------------------
-- 7. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 8. 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- 9. 验证码表
-- ----------------------------
DROP TABLE IF EXISTS `auth_captcha`;
CREATE TABLE `auth_captcha` (
    `uuid` varchar(64) NOT NULL COMMENT 'UUID',
    `code` varchar(8) NOT NULL COMMENT '验证码',
    `expire_time` datetime NOT NULL COMMENT '过期时间',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 插入超级管理员角色
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `description`, `status`, `sort`) VALUES
(1, 'SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 1, 0);

-- 插入普通用户角色
INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `description`, `status`, `sort`) VALUES
(2, 'USER', '普通用户', '系统普通用户', 1, 1);

-- 插入超级管理员用户（密码: lest123456，使用BCrypt加密）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'admin@lest.com', 1);

-- 关联超级管理员用户和角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 插入默认机构
INSERT INTO `sys_organization` (`id`, `parent_id`, `org_name`, `org_code`, `sort`, `status`) VALUES
(1, 0, 'Lest科技', 'LEST', 0, 1);

-- 插入示例菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `icon`, `sort`, `status`) VALUES
(1, 0, '系统管理', 1, '/system', 'Layout', 'Setting', 0, 1),
(2, 1, '用户管理', 2, '/system/user', 'system/user/index', 'User', 0, 1),
(3, 1, '角色管理', 2, '/system/role', 'system/role/index', 'Role', 1, 1),
(4, 1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'Menu', 2, 1),
(5, 1, '机构管理', 2, '/system/org', 'system/org/index', 'Office', 3, 1),
(6, 0, '工作台', 1, '/dashboard', 'Layout', 'HomeFilled', 1, 1),
(7, 6, '工作台首页', 2, '/dashboard/index', 'dashboard/index', '', 0, 1);

-- 关联超级管理员角色和菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
SELECT 1, `id` FROM `sys_menu`;

-- 插入字典数据
INSERT INTO `sys_dictionary` (`id`, `dict_code`, `dict_name`, `description`, `status`) VALUES
(1, 'sex', '性别', '用户性别字典', 1),
(2, 'status', '状态', '通用状态字典', 1);

INSERT INTO `sys_dictionary_data` (`dict_id`, `data_key`, `data_value`, `label`, `sort`, `status`) VALUES
(1, '0', 'unknown', '未知', 0, 1),
(1, '1', 'male', '男', 1, 1),
(1, '2', 'female', '女', 2, 1),
(2, '0', 'disabled', '禁用', 0, 1),
(2, '1', 'enabled', '正常', 1, 1);
