-- lest-platform system_db 初始化脚本
-- 用于系统管理服务（系统参数、日志、备份等）

-- 创建数据库
CREATE DATABASE IF NOT EXISTS system_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE system_db;

-- ----------------------------
-- 1. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    config_key      VARCHAR(128)    NOT NULL COMMENT '配置键',
    config_value    TEXT            DEFAULT NULL COMMENT '配置值',
    config_type     VARCHAR(32)     NOT NULL DEFAULT 'string' COMMENT '配置类型：string/number/boolean/json',
    config_group    VARCHAR(64)     DEFAULT NULL COMMENT '配置分组',
    description     VARCHAR(256)    DEFAULT NULL COMMENT '配置描述',
    is_system       TINYINT         NOT NULL DEFAULT 0 COMMENT '是否系统配置：0-否，1-是',
    sort            INT             NOT NULL DEFAULT 0 COMMENT '排序号',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key),
    KEY idx_config_group (config_group),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- 2. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    dict_name       VARCHAR(128)    NOT NULL COMMENT '字典名称',
    dict_code       VARCHAR(64)     NOT NULL COMMENT '字典编码',
    description     VARCHAR(256)    DEFAULT NULL COMMENT '字典描述',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '软删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_code (dict_code),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ----------------------------
-- 3. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    dict_id         BIGINT          NOT NULL COMMENT '字典ID',
    dict_label      VARCHAR(128)    NOT NULL COMMENT '字典标签',
    dict_value      VARCHAR(256)    NOT NULL COMMENT '字典键值',
    dict_type       VARCHAR(32)     DEFAULT NULL COMMENT '字典类型（冗余）',
    css_class       VARCHAR(64)     DEFAULT NULL COMMENT '样式属性',
    list_class      VARCHAR(32)     DEFAULT NULL COMMENT '回显样式',
    sort            INT             NOT NULL DEFAULT 0 COMMENT '显示顺序',
    is_default      TINYINT         NOT NULL DEFAULT 0 COMMENT '是否默认：0-否，1-是',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '软删除',
    PRIMARY KEY (id),
    KEY idx_dict_id (dict_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- ----------------------------
-- 4. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id         BIGINT          DEFAULT NULL COMMENT '用户ID',
    username        VARCHAR(64)     DEFAULT NULL COMMENT '用户名',
    nickname        VARCHAR(64)     DEFAULT NULL COMMENT '用户昵称',
    login_type      TINYINT         NOT NULL DEFAULT 0 COMMENT '登录类型：0-登录成功，1-登录失败，2-退出登录，3-刷新Token',
    ip_address      VARCHAR(45)     DEFAULT NULL COMMENT 'IP地址',
    user_agent      VARCHAR(512)    DEFAULT NULL COMMENT '用户代理',
    os              VARCHAR(64)     DEFAULT NULL COMMENT '操作系统',
    browser         VARCHAR(64)     DEFAULT NULL COMMENT '浏览器',
    device          VARCHAR(64)     DEFAULT NULL COMMENT '设备',
    status          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    msg             VARCHAR(255)    DEFAULT NULL COMMENT '消息',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_username (username),
    KEY idx_login_type (login_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ----------------------------
-- 5. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id         BIGINT          DEFAULT NULL COMMENT '用户ID',
    username        VARCHAR(64)     DEFAULT NULL COMMENT '用户名',
    nickname        VARCHAR(64)     DEFAULT NULL COMMENT '用户昵称',
    module          VARCHAR(64)     DEFAULT NULL COMMENT '操作模块',
    description     VARCHAR(255)     DEFAULT NULL COMMENT '操作功能描述',
    operation       VARCHAR(64)     DEFAULT NULL COMMENT '操作类型',
    request_method  VARCHAR(8)      DEFAULT NULL COMMENT '请求方法',
    request_url     VARCHAR(512)    DEFAULT NULL COMMENT '请求地址',
    request_params  TEXT            DEFAULT NULL COMMENT '请求参数',
    request_body    TEXT            DEFAULT NULL COMMENT '请求体',
    response_status INT             DEFAULT NULL COMMENT '响应状态',
    response_body   TEXT            DEFAULT NULL COMMENT '响应内容',
    ip_address      VARCHAR(45)     DEFAULT NULL COMMENT 'IP地址',
    user_agent      VARCHAR(512)    DEFAULT NULL COMMENT '用户代理',
    os              VARCHAR(64)     DEFAULT NULL COMMENT '操作系统',
    browser         VARCHAR(64)     DEFAULT NULL COMMENT '浏览器',
    device          VARCHAR(64)     DEFAULT NULL COMMENT '设备',
    execution_time  INT             DEFAULT NULL COMMENT '执行时间（毫秒）',
    error_message   TEXT            DEFAULT NULL COMMENT '错误信息',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_operation (operation),
    KEY idx_module (module),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ----------------------------
-- 6. 数据备份表
-- ----------------------------
DROP TABLE IF EXISTS sys_backup;
CREATE TABLE sys_backup (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '备份ID',
    backup_name     VARCHAR(128)    NOT NULL COMMENT '备份名称',
    backup_type     VARCHAR(16)     NOT NULL DEFAULT 'manual' COMMENT '备份类型：manual/auto',
    backup_path     VARCHAR(512)    DEFAULT NULL COMMENT '备份文件路径',
    file_size       BIGINT          DEFAULT NULL COMMENT '文件大小（字节）',
    status          VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending/running/success/failed',
    description     VARCHAR(256)    DEFAULT NULL COMMENT '备份描述',
    created_by      BIGINT          DEFAULT NULL COMMENT '创建人ID',
    completed_at    DATETIME        DEFAULT NULL COMMENT '完成时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '软删除',
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_backup_type (backup_type),
    KEY idx_created_by (created_by),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据备份表';

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 系统参数初始化
INSERT INTO sys_config (config_key, config_value, config_type, config_group, description, is_system, sort, status) VALUES
-- 安全配置
('password.minLength', '8', 'number', 'security', '密码最小长度', 1, 1, 1),
('password.maxLength', '32', 'number', 'security', '密码最大长度', 1, 2, 1),
('password.requireLetters', 'true', 'boolean', 'security', '密码必须包含字母', 1, 3, 1),
('password.requireNumbers', 'true', 'boolean', 'security', '密码必须包含数字', 1, 4, 1),
('password.expireDays', '90', 'number', 'security', '密码过期天数', 1, 5, 1),
('login.maxRetryCount', '5', 'number', 'security', '登录最大重试次数', 1, 6, 1),
('login.lockMinutes', '30', 'number', 'security', '账户锁定分钟数', 1, 7, 1),
('jwt.accessTokenExpireMinutes', '15', 'number', 'security', 'Access Token 有效期（分钟）', 1, 8, 1),
('jwt.refreshTokenExpireDays', '7', 'number', 'security', 'Refresh Token 有效期（天）', 1, 9, 1),
-- 通知配置
('notification.email.enabled', 'true', 'boolean', 'notification', '邮件通知开关', 0, 10, 1),
('notification.sms.enabled', 'false', 'boolean', 'notification', '短信通知开关', 0, 11, 1),
('notification.webhook.enabled', 'true', 'boolean', 'notification', 'Webhook 通知开关', 0, 12, 1),
-- 系统配置
('system.siteName', 'LEST Platform', 'string', 'system', '系统名称', 1, 20, 1),
('system.siteDescription', '敏捷管理平台', 'string', 'system', '系统描述', 0, 21, 1),
('system.copyright', '© 2026 Lest科技', 'string', 'system', '版权信息', 0, 22, 1),
('system.defaultAvatar', '/assets/default-avatar.png', 'string', 'system', '默认头像', 0, 23, 1),
('system.siteLogo', '/assets/logo.png', 'string', 'system', '系统Logo', 1, 24, 1),
('system.siteFavicon', '/favicon.ico', 'string', 'system', '浏览器标签页图标', 0, 25, 1),
('system.primaryColor', '#409eff', 'string', 'system', '主题主色调', 0, 26, 1),
('system.enableRegister', 'false', 'boolean', 'system', '是否开放注册', 0, 27, 1),
('system.maintenanceMode', 'false', 'boolean', 'system', '维护模式', 1, 28, 1),
-- 站点信息
('site.title', 'LEST Platform', 'string', 'site', '站点标题', 1, 40, 1),
('site.keywords', 'LEST,敏捷管理,RBAC', 'string', 'site', 'SEO关键字', 0, 41, 1),
('site.description', '基于Spring Cloud+VUE3的企业级敏捷管理平台', 'string', 'site', 'SEO描述', 0, 42, 1),
('site.icp', '京ICP备XXXXXXXX号', 'string', 'site', 'ICP备案号', 0, 43, 1),
('site.police', '京公网安备XXXXXXXX号', 'string', 'site', '公安网安备案号', 0, 44, 1),
('site.contactEmail', 'contact@lest.com', 'string', 'site', '联系邮箱', 0, 45, 1),
('site.contactPhone', '400-888-8888', 'string', 'site', '联系电话', 0, 46, 1),
('site.address', '北京市海淀区中关村大街1号', 'string', 'site', '公司地址', 0, 47, 1),
-- 文件存储配置
('storage.type', 'minio', 'string', 'storage', '存储类型：local/minio/oss/s3', 0, 30, 1),
('storage.maxFileSize', '104857600', 'number', 'storage', '最大文件大小（字节）100MB', 0, 31, 1),
('storage.allowedExtensions', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar', 'string', 'storage', '允许的文件扩展名', 0, 32, 1);

-- 字典初始化
INSERT INTO sys_dict (dict_name, dict_code, description, status) VALUES
('性别', 'sex', '用户性别', 1),
('状态', 'status', '通用状态', 1),
('操作类型', 'operation_type', '操作日志类型', 1),
('登录类型', 'login_type', '登录日志类型', 1),
('备份状态', 'backup_status', '数据备份状态', 1),
('配置类型', 'config_type', '系统配置值类型', 1);

INSERT INTO sys_dict_data (dict_id, dict_label, dict_value, dict_type, sort, status) VALUES
-- 性别
(1, '未知', 'unknown', 'sex', 0, 1),
(1, '男', 'male', 'sex', 1, 1),
(1, '女', 'female', 'sex', 2, 1),
-- 通用状态
(2, '禁用', '0', 'status', 0, 1),
(2, '正常', '1', 'status', 1, 1),
-- 操作类型
(3, '登录', 'LOGIN', 'operation_type', 0, 1),
(3, '登出', 'LOGOUT', 'operation_type', 1, 1),
(3, '创建', 'CREATE', 'operation_type', 2, 1),
(3, '更新', 'UPDATE', 'operation_type', 3, 1),
(3, '删除', 'DELETE', 'operation_type', 4, 1),
(3, '查询', 'QUERY', 'operation_type', 5, 1),
-- 登录类型
(4, '登录成功', '0', 'login_type', 0, 1),
(4, '登录失败', '1', 'login_type', 1, 1),
(4, '退出登录', '2', 'login_type', 2, 1),
(4, '刷新Token', '3', 'login_type', 3, 1),
-- 备份状态
(5, '等待中', 'pending', 'backup_status', 0, 1),
(5, '运行中', 'running', 'backup_status', 1, 1),
(5, '成功', 'success', 'backup_status', 2, 1),
(5, '失败', 'failed', 'backup_status', 3, 1),
-- 配置类型
(6, '字符串', 'string', 'config_type', 0, 1),
(6, '数字', 'number', 'config_type', 1, 1),
(6, '布尔值', 'boolean', 'config_type', 2, 1),
(6, 'JSON', 'json', 'config_type', 3, 1);
