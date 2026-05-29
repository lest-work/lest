-- FileRecord table (admin system file)
CREATE TABLE IF NOT EXISTS `sys_file` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `name`          VARCHAR(255) NOT NULL COMMENT '文件名',
    `path`          VARCHAR(512) NOT NULL COMMENT 'MinIO存储路径',
    `length`        BIGINT       NOT NULL DEFAULT 0 COMMENT '文件大小(字节)',
    `content_type` VARCHAR(128) DEFAULT NULL COMMENT 'MIME类型',
    `create_user_id` BIGINT      DEFAULT NULL COMMENT '上传人ID',
    `create_username` VARCHAR(64) DEFAULT NULL COMMENT '上传人账号',
    `create_nickname` VARCHAR(64) DEFAULT NULL COMMENT '上传人名称',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_create_user_id` (`create_user_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- UserFile table (personal file manager)
CREATE TABLE IF NOT EXISTS `sys_user_file` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `user_id`      BIGINT       NOT NULL COMMENT '所属用户ID',
    `name`          VARCHAR(255) NOT NULL COMMENT '文件/文件夹名称',
    `is_directory`  TINYINT     NOT NULL DEFAULT 0 COMMENT '是否文件夹: 0=否, 1=是',
    `parent_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '上级ID, 0=根目录',
    `path`          VARCHAR(512) DEFAULT NULL COMMENT '关联sys_file的path(文件时), 文件夹时为空',
    `length`        BIGINT       DEFAULT NULL COMMENT '文件大小(字节)',
    `content_type` VARCHAR(128) DEFAULT NULL COMMENT 'MIME类型',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_is_directory` (`is_directory`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户文件表';
