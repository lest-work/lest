-- ============================================================
-- LEST Platform - 数据库初始化脚本
-- 数据库架构：所有微服务共用 lest_platform 单一数据库
-- 执行方式：mysql --default-character-set=utf8mb4 -u root -p lest_platform < 01_lest_platform_init.sql
-- ============================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection = utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lest_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lest_platform;

-- ============================================================
-- 删除所有现有表（按外键依赖顺序）
-- ============================================================
DROP TABLE IF EXISTS sys_job_log;
DROP TABLE IF EXISTS sys_job;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS task_change_history;
DROP TABLE IF EXISTS task_watcher;
DROP TABLE IF EXISTS task_label;
DROP TABLE IF EXISTS task_dependency;
DROP TABLE IF EXISTS task_commit;
DROP TABLE IF EXISTS task_comment;
DROP TABLE IF EXISTS task_worklog;
DROP TABLE IF EXISTS label;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS milestone_iteration;
DROP TABLE IF EXISTS milestone;
DROP TABLE IF EXISTS iteration;
DROP TABLE IF EXISTS project_member;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS sys_user;

-- ============================================================
-- 用户表 sys_user（Lest 平台核心）
-- ============================================================
CREATE TABLE sys_user (
    user_id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    nick_name       VARCHAR(30)  NOT NULL COMMENT '用户昵称',
    user_type       VARCHAR(2)   DEFAULT '00' COMMENT '用户类型（00系统用户）',
    email           VARCHAR(50)  DEFAULT '' COMMENT '用户邮箱',
    phonenumber     VARCHAR(11)  DEFAULT '' COMMENT '手机号码',
    sex             CHAR(1)      DEFAULT '0' COMMENT '用户性别（0未知 1男 2女）',
    avatar          VARCHAR(100) DEFAULT '' COMMENT '头像地址',
    password        VARCHAR(100) DEFAULT '' COMMENT '密码（BCrypt加密）',
    status          CHAR(1)      DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    is_platform_admin TINYINT(1) NOT NULL DEFAULT 0 COMMENT '平台管理员标识：0-否 1-是',
    del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    login_ip        VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
    login_date      DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    pwd_update_date DATETIME     DEFAULT NULL COMMENT '密码更新时间',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ============================================================
-- 定时任务调度表 sys_job（Quartz 持久化）
-- ============================================================
CREATE TABLE sys_job (
    job_id            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    job_name          VARCHAR(64) NOT NULL COMMENT '任务名称',
    job_group         VARCHAR(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    invoke_target     VARCHAR(500) NOT NULL COMMENT '调用目标字符串',
    cron_expression   VARCHAR(255) DEFAULT '' COMMENT 'cron执行表达式',
    misfire_policy    VARCHAR(20)  DEFAULT '3' COMMENT '计划策略（0默认 1立即触发 2触发一次 3不触发）',
    concurrent        CHAR(1)     DEFAULT '1' COMMENT '是否并发（0允许 1禁止）',
    status            CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1暂停）',
    create_by         VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time       DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by         VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time       DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务调度表';

-- ============================================================
-- 定时任务调度日志表 sys_job_log
-- ============================================================
CREATE TABLE sys_job_log (
    job_log_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    job_name       VARCHAR(64) DEFAULT '' COMMENT '任务名称',
    job_group      VARCHAR(64) DEFAULT '' COMMENT '任务组名',
    invoke_target  VARCHAR(500) DEFAULT '' COMMENT '调用目标字符串',
    job_message    VARCHAR(500) DEFAULT '' COMMENT '日志信息',
    status         CHAR(1)     DEFAULT '0' COMMENT '执行状态（0成功 1失败）',
    exception_info VARCHAR(2000) DEFAULT '' COMMENT '异常信息',
    start_time     DATETIME    DEFAULT NULL COMMENT '开始时间',
    end_time       DATETIME    DEFAULT NULL COMMENT '结束时间',
    create_time    DATETIME    DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (job_log_id),
    KEY idx_job_name (job_name),
    KEY idx_job_group (job_group),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务调度日志表';

-- ============================================================
-- Quartz 标准表（11张，兼容分布式调度）
-- ============================================================
CREATE TABLE QRTZ_JOB_DETAILS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    job_name             VARCHAR(200)  NOT NULL COMMENT '任务名称',
    job_group            VARCHAR(200)  NOT NULL COMMENT '任务组名',
    description          VARCHAR(250)  DEFAULT NULL COMMENT '相关介绍',
    job_class_name       VARCHAR(250)  NOT NULL COMMENT '执行任务类名称',
    is_durable           VARCHAR(1)    NOT NULL COMMENT '是否持久化',
    is_nonconcurrent     VARCHAR(1)    NOT NULL COMMENT '是否并发',
    is_update_data       VARCHAR(1)    NOT NULL COMMENT '是否更新数据',
    requests_recovery    VARCHAR(1)    NOT NULL COMMENT '是否接受恢复执行',
    job_data             BLOB          DEFAULT NULL COMMENT '存放持久化job对象',
    PRIMARY KEY (sched_name, job_name, job_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务详细信息表';

CREATE TABLE QRTZ_TRIGGERS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    trigger_name         VARCHAR(200)  NOT NULL COMMENT '触发器名称',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT '触发器所属组',
    job_name             VARCHAR(200)  NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
    job_group            VARCHAR(200)  NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
    description          VARCHAR(250)  DEFAULT NULL COMMENT '相关介绍',
    next_fire_time       BIGINT(13)    DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
    prev_fire_time       BIGINT(13)    DEFAULT NULL COMMENT '下一次触发时间（毫秒）',
    priority             INTEGER       DEFAULT NULL COMMENT '优先级',
    trigger_state        VARCHAR(16)   NOT NULL COMMENT '触发器状态',
    trigger_type         VARCHAR(8)    NOT NULL COMMENT '触发器类型',
    start_time           BIGINT(13)    NOT NULL COMMENT '开始时间',
    end_time             BIGINT(13)    DEFAULT NULL COMMENT '结束时间',
    calendar_name        VARCHAR(200)  DEFAULT NULL COMMENT '日程表名称',
    misfire_instr        SMALLINT(2)   DEFAULT NULL COMMENT '补偿执行策略',
    job_data             BLOB          DEFAULT NULL COMMENT '存放持久化Trigger对象',
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    KEY idx_qrtz_t_j (sched_name, job_name, job_group),
    KEY idx_qrtz_t_jg (sched_name, job_group),
    KEY idx_qrtz_t_tg (sched_name, trigger_name, trigger_group),
    KEY idx_qrtz_t_s (sched_name, trigger_state),
    KEY idx_qrtz_t_nft (sched_name, next_fire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='触发器详细信息表';

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    trigger_name         VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    repeat_count         BIGINT(7)     NOT NULL COMMENT '重复次数统计',
    repeat_interval      BIGINT(12)    NOT NULL COMMENT '重复间隔时间',
    times_triggered      BIGINT(10)    NOT NULL COMMENT '已触发次数',
    PRIMARY KEY (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简单触发器信息表';

CREATE TABLE QRTZ_CRON_TRIGGERS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    trigger_name         VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    cron_expression      VARCHAR(200)  NOT NULL COMMENT 'cron表达式',
    time_zone_id         VARCHAR(80)   DEFAULT NULL COMMENT '时区',
    PRIMARY KEY (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Cron触发器表';

CREATE TABLE QRTZ_BLOB_TRIGGERS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    trigger_name         VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    blob_data            BLOB          DEFAULT NULL COMMENT '存放持久化Trigger对象',
    PRIMARY KEY (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Blob类型触发器表';

CREATE TABLE QRTZ_CALENDARS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    calendar_name        VARCHAR(200)  NOT NULL COMMENT '日历名称',
    calendar             BLOB          NOT NULL COMMENT '存放持久化calendar对象',
    PRIMARY KEY (sched_name, calendar_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日历信息表';

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    PRIMARY KEY (sched_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='暂停触发器表';

CREATE TABLE QRTZ_FIRED_TRIGGERS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    entry_id             VARCHAR(95)   NOT NULL COMMENT '调度器实例ID',
    trigger_name         VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    instance_name        VARCHAR(200)  NOT NULL COMMENT '调度器实例名',
    fired_time           BIGINT(13)    NOT NULL COMMENT '触发时间',
    sched_time           BIGINT(13)    NOT NULL COMMENT '定时器制定的时间',
    priority             INTEGER       NOT NULL COMMENT '优先级',
    state                VARCHAR(16)   NOT NULL COMMENT '状态',
    job_name             VARCHAR(200)  DEFAULT NULL COMMENT '任务名称',
    job_group            VARCHAR(200)  DEFAULT NULL COMMENT '任务组名',
    is_nonconcurrent     VARCHAR(1)    DEFAULT NULL COMMENT '是否并发',
    requests_recovery    VARCHAR(1)    DEFAULT NULL COMMENT '是否接受恢复执行',
    PRIMARY KEY (sched_name, entry_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='已触发触发器表';

CREATE TABLE QRTZ_SCHEDULER_STATE (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    instance_name        VARCHAR(200)  NOT NULL COMMENT '实例名称',
    last_checkin_time    BIGINT(13)    NOT NULL COMMENT '上次检查时间',
    checkin_interval     BIGINT(13)    NOT NULL COMMENT '检查间隔时间',
    PRIMARY KEY (sched_name, instance_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='调度器状态表';

CREATE TABLE QRTZ_LOCKS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    lock_name           VARCHAR(40)   NOT NULL COMMENT '悲观锁名称',
    PRIMARY KEY (sched_name, lock_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='悲观锁信息表';

CREATE TABLE QRTZ_SIMPROP_TRIGGERS (
    sched_name           VARCHAR(120)  NOT NULL COMMENT '调度名称',
    trigger_name         VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
    trigger_group        VARCHAR(200)  NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
    str_prop_1           VARCHAR(512)  DEFAULT NULL COMMENT 'String类型第1参数',
    str_prop_2           VARCHAR(512)  DEFAULT NULL COMMENT 'String类型第2参数',
    str_prop_3           VARCHAR(512)  DEFAULT NULL COMMENT 'String类型第3参数',
    int_prop_1           INT           DEFAULT NULL COMMENT 'int类型第1参数',
    int_prop_2           INT           DEFAULT NULL COMMENT 'int类型第2参数',
    long_prop_1          BIGINT        DEFAULT NULL COMMENT 'long类型第1参数',
    long_prop_2          BIGINT        DEFAULT NULL COMMENT 'long类型第2参数',
    dec_prop_1           NUMERIC(13,4) DEFAULT NULL COMMENT 'decimal类型第1参数',
    dec_prop_2           NUMERIC(13,4) DEFAULT NULL COMMENT 'decimal类型第2参数',
    bool_prop_1          VARCHAR(1)    DEFAULT NULL COMMENT 'Boolean类型第1参数',
    bool_prop_2          VARCHAR(1)    DEFAULT NULL COMMENT 'Boolean类型第2参数',
    PRIMARY KEY (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='同步机制行锁表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员用户（密码：123456，BCrypt hash）
INSERT INTO sys_user (user_id, nick_name, user_type, email, phonenumber, sex, avatar, password, status, is_platform_admin, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, remark) VALUES
(1, '管理员', '00', 'admin@lest.com', '15888888888', '1', '', '$2a$10$VQ/en0QCD03MXql2I/WLrO4Y3jSotPd7AhRVIqI.o9rojJ3HlUIbW', '0', 1, '0', '127.0.0.1', NOW(), NOW(), NOW(), 'admin', NOW(), '平台管理员');

-- 测试用户
INSERT INTO sys_user (user_id, nick_name, user_type, email, phonenumber, sex, avatar, password, status, is_platform_admin, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, remark) VALUES
(2, '测试用户', '00', 'user@lest.com', '15666666666', '1', '', '$2a$10$VQ/en0QCD03MXql2I/WLrO4Y3jSotPd7AhRVIqI.o9rojJ3HlUIbW', '0', 0, '0', '127.0.0.1', NOW(), NOW(), NOW(), 'admin', NOW(), '测试用户');

-- ============================================================
-- 项目管理模块 Project Module
-- ============================================================

CREATE TABLE `project` (
  `project_id`    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_key`   VARCHAR(32)           DEFAULT NULL   COMMENT '项目 Key，形如 P1、P2，用于 Issue Key 前缀',
  `name`          VARCHAR(100) NOT NULL                COMMENT '项目名称',
  `description`   TEXT                                  COMMENT '项目描述',
  `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-活跃 2-已归档',
  `template`      VARCHAR(20)  NOT NULL DEFAULT 'agile' COMMENT '模板：agile/waterfall/kanban',
  `owner_id`      BIGINT                                COMMENT '负责人用户ID',
  `start_date`     DATE                                  COMMENT '开始日期',
  `end_date`       DATE                                  COMMENT '结束日期',
  `deleted`       TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-删除',
  `create_by`     VARCHAR(64)          DEFAULT ''     COMMENT '创建者',
  `create_time`   DATETIME                             COMMENT '创建时间',
  `update_by`     VARCHAR(64)          DEFAULT ''     COMMENT '更新者',
  `update_time`   DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uk_project_key` (`project_key`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='项目表';

CREATE TABLE `project_member` (
  `project_id` BIGINT      NOT NULL                    COMMENT '项目ID',
  `user_id`    BIGINT      NOT NULL                    COMMENT '用户ID',
  `role`       VARCHAR(20) NOT NULL DEFAULT 'member'   COMMENT '角色：admin/member/viewer',
  `joined_at`  DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`project_id`, `user_id`)
) ENGINE=InnoDB COMMENT='项目成员表';

CREATE TABLE `project_invite` (
  `invite_id`  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '邀请ID',
  `project_id` BIGINT       NOT NULL                COMMENT '项目ID',
  `email`      VARCHAR(255) NOT NULL                COMMENT '受邀邮箱',
  `role`       VARCHAR(20)  NOT NULL                COMMENT '邀请的项目角色',
  `token`      VARCHAR(64)  NOT NULL                COMMENT '唯一邀请令牌',
  `expires_at` DATETIME     NOT NULL                COMMENT '过期时间',
  `used`       TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否已使用：0-未使用 1-已使用',
  `used_by`    BIGINT                           DEFAULT NULL COMMENT '实际接受邀请的用户ID',
  `used_at`    DATETIME                         DEFAULT NULL COMMENT '接受邀请时间',
  PRIMARY KEY (`invite_id`),
  UNIQUE KEY `uk_project_invite_token` (`token`),
  KEY `idx_project_invite_project` (`project_id`),
  KEY `idx_project_invite_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='项目邀请表（邮箱邀请链接，一次性使用）';

CREATE TABLE `iteration` (
  `iteration_id` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '迭代ID',
  `project_id`   BIGINT       NOT NULL                COMMENT '所属项目ID',
  `name`         VARCHAR(100) NOT NULL                COMMENT '迭代名称',
  `goal`         TEXT                                  COMMENT '迭代目标',
  `status`       INT          NOT NULL DEFAULT 1      COMMENT '状态：1-计划中 2-进行中 3-已完成',
  `start_date`   DATE                                COMMENT '开始日期',
  `end_date`     DATE                                COMMENT '结束日期',
  `completed_at` DATETIME                             COMMENT '完成时间',
  `deleted`      TINYINT     NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-删除',
  `create_by`    VARCHAR(64)          DEFAULT ''    COMMENT '创建者',
  `create_time`  DATETIME                            COMMENT '创建时间',
  `update_by`    VARCHAR(64)          DEFAULT ''    COMMENT '更新者',
  `update_time`  DATETIME                            COMMENT '更新时间',
  PRIMARY KEY (`iteration_id`),
  KEY `idx_iteration_project` (`project_id`),
  KEY `idx_iteration_status`  (`status`),
  KEY `idx_iteration_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='迭代表';

CREATE TABLE `milestone` (
  `milestone_id` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '里程碑ID',
  `project_id`   BIGINT       NOT NULL                COMMENT '所属项目ID',
  `name`         VARCHAR(100) NOT NULL                COMMENT '里程碑名称',
  `description`  TEXT                                  COMMENT '描述',
  `target_date`  DATE                                  COMMENT '目标日期',
  `create_by`    VARCHAR(64)          DEFAULT ''     COMMENT '创建者',
  `create_time`  DATETIME                             COMMENT '创建时间',
  `update_by`    VARCHAR(64)          DEFAULT ''     COMMENT '更新者',
  `update_time`  DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (`milestone_id`),
  KEY `idx_milestone_project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='里程碑表';

CREATE TABLE `milestone_iteration` (
  `milestone_id` BIGINT NOT NULL COMMENT '里程碑ID',
  `iteration_id`  BIGINT NOT NULL COMMENT '迭代ID',
  PRIMARY KEY (`milestone_id`, `iteration_id`)
) ENGINE=InnoDB COMMENT='里程碑-迭代关联表';

-- ============================================================
-- 任务管理模块 Task Module
-- ============================================================

CREATE TABLE `task` (
  `task_id`          BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '任务ID',
  `title`           VARCHAR(200)  NOT NULL                 COMMENT '任务标题',
  `task_no`         VARCHAR(32)                           COMMENT '任务编号（PROJ-TASK-001 格式）',
  `description`     TEXT                                   COMMENT '任务描述',
  `project_id`       BIGINT        NOT NULL                 COMMENT '所属项目ID',
  `iteration_id`     BIGINT                                COMMENT '所属迭代ID',
  `parent_id`       BIGINT                                COMMENT '父任务ID（子任务）',
  `task_type`       VARCHAR(20)   NOT NULL DEFAULT 'task'  COMMENT '类型：epic/story/task/bug',
  `priority`        VARCHAR(10)   NOT NULL DEFAULT 'p2'    COMMENT '优先级：p0/p1/p2/p3',
  `status`          VARCHAR(20)   NOT NULL DEFAULT 'todo'   COMMENT '状态：todo/in_progress/completed',
  `assignee_id`    BIGINT                                COMMENT '负责人用户ID',
  `reporter_id`   BIGINT                                COMMENT '报告人用户ID',
  `start_time`      DATETIME                               COMMENT '开始时间',
  `completed_at`   DATETIME                               COMMENT '完成时间',
  `estimated_hours` DECIMAL(6,2)                          COMMENT '预估工时（小时）',
  `actual_hours`   DECIMAL(6,2)                          COMMENT '实际工时（小时）',
  `remaining_hours` DECIMAL(6,2)                          COMMENT '剩余工时（小时）',
  `due_date`        DATE                                   COMMENT '截止日期',
  `sort`            INT           DEFAULT 0                 COMMENT '排序权重',
  `deleted`         TINYINT       NOT NULL DEFAULT 0       COMMENT '逻辑删除：0-正常 1-删除',
  `is_subtask`     TINYINT       NOT NULL DEFAULT 0       COMMENT '是否为子任务：0-否 1-是',
  `root_id`         BIGINT                                COMMENT '根任务ID',
  `deleted_by`      BIGINT                                COMMENT '删除人用户ID',
  `deleted_at`      DATETIME                               COMMENT '删除时间',
  `deleted_reason`  VARCHAR(500)                          COMMENT '删除原因',
  `story_points`    DECIMAL(6,2)                          COMMENT '故事点',
  `create_by`       VARCHAR(64)            DEFAULT ''      COMMENT '创建者',
  `create_time`     DATETIME                              COMMENT '创建时间',
  `update_by`       VARCHAR(64)            DEFAULT ''      COMMENT '更新者',
  `update_time`     DATETIME                              COMMENT '更新时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_task_project`  (`project_id`),
  KEY `idx_task_iteration`(`iteration_id`),
  KEY `idx_task_assignee` (`assignee_id`),
  KEY `idx_task_status`   (`status`),
  KEY `idx_task_deleted`  (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务表';

CREATE TABLE `label` (
  `label_id`     BIGINT      NOT NULL AUTO_INCREMENT    COMMENT '标签ID',
  `project_id`   BIGINT      NOT NULL                  COMMENT '所属项目ID',
  `name`        VARCHAR(50) NOT NULL                  COMMENT '标签名称',
  `color`       VARCHAR(20) NOT NULL DEFAULT '#409EFF' COMMENT '标签颜色',
  `create_by`   VARCHAR(64)          DEFAULT ''       COMMENT '创建者',
  `create_time`  DATETIME                              COMMENT '创建时间',
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='标签表';

CREATE TABLE `task_label` (
  `task_id`  BIGINT NOT NULL COMMENT '任务ID',
  `label_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`task_id`, `label_id`)
) ENGINE=InnoDB COMMENT='任务-标签关联表';

CREATE TABLE `task_watcher` (
  `task_id`     BIGINT   NOT NULL                         COMMENT '任务ID',
  `user_id`     BIGINT   NOT NULL                         COMMENT '用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP         COMMENT '关注时间',
  PRIMARY KEY (`task_id`, `user_id`)
) ENGINE=InnoDB COMMENT='任务关注人表';

CREATE TABLE `task_worklog` (
  `task_worklog_id` BIGINT       NOT NULL AUTO_INCREMENT COMMENT '工时记录ID',
  `task_id`        BIGINT       NOT NULL                COMMENT '任务ID',
  `user_id`        BIGINT       NOT NULL                COMMENT '记录人用户ID',
  `hours`          DECIMAL(6,2) NOT NULL                COMMENT '工时（小时）',
  `work_date`      DATE         NOT NULL                COMMENT '工作日期',
  `description`   VARCHAR(500)                         COMMENT '工作内容描述',
  `create_by`     VARCHAR(64)           DEFAULT ''    COMMENT '创建者',
  `create_time`    DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (`task_worklog_id`),
  KEY `idx_worklog_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务工时记录表';

CREATE TABLE `task_comment` (
  `task_comment_id` BIGINT   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `task_id`        BIGINT   NOT NULL                COMMENT '任务ID',
  `user_id`        BIGINT   NOT NULL                COMMENT '评论人用户ID',
  `content`        TEXT     NOT NULL                COMMENT '评论内容',
  `parent_id`       BIGINT                          COMMENT '父评论ID（回复）',
  `create_by`      VARCHAR(64)      DEFAULT ''   COMMENT '创建者',
  `create_time`     DATETIME                        COMMENT '创建时间',
  `update_time`     DATETIME                        COMMENT '更新时间',
  `deleted`         TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-删除',
  PRIMARY KEY (`task_comment_id`),
  KEY `idx_comment_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务评论表';

CREATE TABLE `task_commit` (
  `task_commit_id`  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '提交记录ID',
  `task_id`        BIGINT       NOT NULL                COMMENT '任务ID',
  `repo_id`        VARCHAR(200)                         COMMENT '仓库ID/名称',
  `commit_hash`     VARCHAR(64)  NOT NULL                COMMENT '提交哈希',
  `commit_message`  VARCHAR(500)                         COMMENT '提交信息',
  `author`         VARCHAR(100)                         COMMENT '提交者',
  `commit_time`     DATETIME                             COMMENT '提交时间',
  `create_by`      VARCHAR(64)           DEFAULT ''    COMMENT '创建者',
  `create_time`     DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (`task_commit_id`),
  KEY `idx_commit_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务关联代码提交表';

CREATE TABLE `task_dependency` (
  `task_dependency_id`  BIGINT      NOT NULL AUTO_INCREMENT COMMENT '依赖关系ID',
  `task_id`            BIGINT      NOT NULL                COMMENT '任务ID',
  `dependency_task_id`  BIGINT      NOT NULL                COMMENT '依赖的任务ID',
  `type`               VARCHAR(20) NOT NULL DEFAULT 'blocks' COMMENT '依赖类型：blocks/blocked_by',
  PRIMARY KEY (`task_dependency_id`),
  UNIQUE KEY `uk_task_dep` (`task_id`, `dependency_task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务依赖关系表';

CREATE TABLE `task_change_history` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `user_id` BIGINT NOT NULL COMMENT '操作用户ID',
    `user_name` VARCHAR(64) DEFAULT NULL COMMENT '操作用户名称',
    `field_name` VARCHAR(64) NOT NULL COMMENT '变更字段名',
    `old_value` TEXT DEFAULT NULL COMMENT '旧值',
    `new_value` TEXT DEFAULT NULL COMMENT '新值',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_task_id` (`task_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务变更历史记录表';

-- ============================================================
-- 示例数据（V10-STAB-005）
-- ============================================================

-- 示例项目
INSERT INTO project (project_id, project_key, name, description, status, template, owner_id, start_date, end_date, deleted, create_by, create_time, update_by, update_time) VALUES
(1, 'P1', 'LEST 平台', '敏捷项目管理平台 — 示例项目', 1, 'agile', 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 0, 'admin', NOW(), 'admin', NOW());

-- 项目成员（admin = ADMIN, user = MEMBER）
INSERT INTO project_member (project_id, user_id, role, joined_at) VALUES
(1, 1, 'admin', NOW()),
(1, 2, 'member', NOW());

-- 示例迭代（Sprint 1）
INSERT INTO iteration (iteration_id, project_id, name, goal, status, start_date, end_date, deleted, create_by, create_time, update_by, update_time) VALUES
(1, 1, 'Sprint 1', '完成核心登录与项目管理功能', 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 0, 'admin', NOW(), 'admin', NOW());

-- 示例任务
INSERT INTO task (task_id, title, task_no, description, project_id, iteration_id, task_type, priority, status, assignee_id, reporter_id, story_points, deleted, create_by, create_time, update_by, update_time) VALUES
(1, '用户登录与注册', 'LEST-1', '实现用户名/密码登录、JWT 令牌颁发', 1, 1, 'story', 'p1', 'completed', 1, 1, 3, 0, 'admin', NOW(), 'admin', NOW()),
(2, '项目 CRUD 接口', 'LEST-2', '实现项目的创建、查询、编辑、归档、删除接口', 1, 1, 'task', 'p1', 'in_progress', 1, 1, 5, 0, 'admin', NOW(), 'admin', NOW()),
(3, '看板拖拽功能', 'LEST-3', '支持任务在不同状态列间拖拽并更新状态', 1, 1, 'task', 'p2', 'todo', 2, 1, 3, 0, 'admin', NOW(), 'admin', NOW()),
(4, '修复：成员邀请后无法登录', 'LEST-4', '新成员接受邀请后首次登录报 401', 1, 1, 'bug', 'p0', 'in_progress', 2, 1, 1, 0, 'admin', NOW(), 'admin', NOW()),
(5, '迭代燃尽图', 'LEST-5', '在迭代详情页显示剩余任务趋势燃尽图', 1, NULL, 'story', 'p3', 'todo', NULL, 1, 5, 0, 'admin', NOW(), 'admin', NOW());

-- 示例标签
INSERT INTO label (label_id, project_id, name, color, create_by, create_time) VALUES
(1, 1, 'backend', '#409EFF', 'admin', NOW()),
(2, 1, 'frontend', '#67C23A', 'admin', NOW()),
(3, 1, 'urgent',   '#F56C6C', 'admin', NOW());

