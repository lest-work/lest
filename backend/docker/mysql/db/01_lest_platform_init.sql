-- ============================================================
-- LEST Platform - 全量初始化脚本
-- 数据库架构决策：所有微服务共用 lest_platform 单一数据库
-- 所有 sys_* 表严格遵循 RuoYi 框架标准 DDL
-- 执行方式：docker-entrypoint-initdb.d 自动执行（首次启动）
--           或手动：mysql --default-character-set=utf8mb4 -u root -p lest_platform < 01_lest_platform_init.sql
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
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_user_post;
DROP TABLE IF EXISTS sys_role_dept;
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

DROP TABLE IF EXISTS sys_notice_read;
DROP TABLE IF EXISTS sys_notice;
DROP TABLE IF EXISTS sys_oper_log;
DROP TABLE IF EXISTS sys_logininfor;
DROP TABLE IF EXISTS sys_config;
DROP TABLE IF EXISTS sys_dict_data;
DROP TABLE IF EXISTS sys_dict_type;
DROP TABLE IF EXISTS sys_post;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_dept;

-- ============================================================
-- 核心表：sys_user（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_user (
    user_id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id         BIGINT       DEFAULT NULL COMMENT '部门ID',
    user_name       VARCHAR(30)  NOT NULL COMMENT '用户名称',
    nick_name       VARCHAR(30)  NOT NULL COMMENT '用户昵称',
    user_type       VARCHAR(2)   DEFAULT '00' COMMENT '用户类型（00系统用户）',
    email           VARCHAR(50)  DEFAULT '' COMMENT '用户邮箱',
    phonenumber     VARCHAR(11)  DEFAULT '' COMMENT '手机号码',
    sex             CHAR(1)      DEFAULT '0' COMMENT '用户性别（0未知 1男 2女）',
    avatar          VARCHAR(100) DEFAULT '' COMMENT '头像地址',
    password        VARCHAR(100) DEFAULT '' COMMENT '密码（BCrypt加密）',
    status          CHAR(1)      DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    login_ip        VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
    login_date      DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    pwd_update_date DATETIME     DEFAULT NULL COMMENT '密码更新时间',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (user_id),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ============================================================
-- 核心表：sys_role（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_role (
    role_id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name         VARCHAR(30) NOT NULL COMMENT '角色名称',
    role_key          VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    role_sort         INT         NOT NULL COMMENT '显示顺序',
    data_scope        CHAR(1)     DEFAULT '1' COMMENT '数据范围（1全部 2本部门及以下 3本部门 4仅本人 5自定义）',
    menu_check_strictly TINYINT  DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    dept_check_strictly TINYINT  DEFAULT 1 COMMENT '部门树选择项是否关联显示',
    status            CHAR(1)     NOT NULL COMMENT '角色状态（0正常 1停用）',
    del_flag          CHAR(1)     DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by         VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time       DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by         VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time       DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';

-- ============================================================
-- 核心表：sys_menu（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_menu (
    menu_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    menu_name   VARCHAR(50) NOT NULL COMMENT '菜单名称',
    parent_id   BIGINT      DEFAULT 0 COMMENT '父菜单ID',
    order_num   INT         DEFAULT 0 COMMENT '显示顺序',
    path        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    component   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    query       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    route_name  VARCHAR(50) DEFAULT NULL COMMENT '路由名称',
    is_frame    INT         DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    is_cache    INT         DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
    menu_type   CHAR(1)     DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    visible     CHAR(1)     DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
    status      CHAR(1)     DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    perms       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    icon        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (menu_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- ============================================================
-- 核心表：sys_dept（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_dept (
    dept_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '部门id',
    parent_id   BIGINT      DEFAULT 0 COMMENT '父部门id',
    ancestors   VARCHAR(50) DEFAULT '' COMMENT '祖级列表',
    dept_name   VARCHAR(30) DEFAULT '' COMMENT '部门名称',
    order_num   INT         DEFAULT 0 COMMENT '显示顺序',
    leader      VARCHAR(20) DEFAULT NULL COMMENT '负责人',
    phone       VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
    email       VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    status      CHAR(1)     DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    del_flag    CHAR(1)     DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (dept_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ============================================================
-- 关联表：sys_role_dept（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和部门关联表';

-- ============================================================
-- 关联表：sys_user_role（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- ============================================================
-- 关联表：sys_role_menu（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

-- ============================================================
-- 关联表：sys_user_post（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_user_post (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (user_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户与岗位关联表';

-- ============================================================
-- 核心表：sys_post（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_post (
    post_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    post_code   VARCHAR(64) NOT NULL COMMENT '岗位编码',
    post_name   VARCHAR(50) NOT NULL COMMENT '岗位名称',
    post_sort   INT         NOT NULL COMMENT '显示顺序',
    status      CHAR(1)     NOT NULL COMMENT '状态（0正常 1停用）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位信息表';

-- ============================================================
-- 核心表：sys_dict_type（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_dict_type (
    dict_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    dict_name   VARCHAR(100) DEFAULT '' COMMENT '字典名称',
    dict_type   VARCHAR(100) DEFAULT '' COMMENT '字典类型（唯一）',
    status      CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (dict_id),
    UNIQUE KEY dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ============================================================
-- 核心表：sys_dict_data（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_dict_data (
    dict_code  BIGINT      NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    dict_sort  INT         DEFAULT 0 COMMENT '字典排序',
    dict_label VARCHAR(100) DEFAULT '' COMMENT '字典标签',
    dict_value VARCHAR(100) DEFAULT '' COMMENT '字典键值',
    dict_type  VARCHAR(100) DEFAULT '' COMMENT '字典类型',
    css_class  VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
    list_class VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
    is_default CHAR(1)     DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    status     CHAR(1)     DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by  VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME   DEFAULT NULL COMMENT '创建时间',
    update_by  VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME   DEFAULT NULL COMMENT '更新时间',
    remark     VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- ============================================================
-- 核心表：sys_config（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_config (
    config_id   INT         NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    config_name VARCHAR(100) DEFAULT '' COMMENT '参数名称',
    config_key  VARCHAR(100) DEFAULT '' COMMENT '参数键名',
    config_value VARCHAR(500) DEFAULT '' COMMENT '参数键值',
    config_type CHAR(1)     DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    create_by   VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by   VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置表';

-- ============================================================
-- 核心表：sys_logininfor（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_logininfor (
    info_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    user_name   VARCHAR(50) DEFAULT '' COMMENT '用户名称',
    ipaddr      VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
    status      CHAR(1)     DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    msg         VARCHAR(255) DEFAULT '' COMMENT '提示消息',
    access_time DATETIME    DEFAULT NULL COMMENT '访问时间',
    PRIMARY KEY (info_id),
    KEY idx_ipaddr (ipaddr),
    KEY idx_user_name (user_name),
    KEY idx_access_time (access_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='sys_logininfor表';

-- ============================================================
-- 核心表：sys_oper_log（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_oper_log (
    oper_id        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    title          VARCHAR(50) DEFAULT '' COMMENT 'sys_menu模块标题',
    business_type  INT         DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    method         VARCHAR(200) DEFAULT '' COMMENT '请求方法',
    request_method VARCHAR(10) DEFAULT '' COMMENT '请求方式',
    operator_type  INT         DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name      VARCHAR(50) DEFAULT '' COMMENT '操作人员',
    dept_name      VARCHAR(50) DEFAULT '' COMMENT '部门名称',
    oper_url       VARCHAR(255) DEFAULT '' COMMENT '请求地址',
    oper_ip        VARCHAR(128) DEFAULT '' COMMENT '主机地址',
    oper_location  VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    oper_param     VARCHAR(2000) DEFAULT '' COMMENT '请求参数',
    json_result    VARCHAR(2000) DEFAULT '' COMMENT '返回参数',
    status         INT         DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    error_msg      VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
    oper_time      DATETIME    DEFAULT NULL COMMENT '操作时间',
    cost_time      BIGINT      DEFAULT 0 COMMENT '消耗时间（毫秒）',
    PRIMARY KEY (oper_id),
    KEY idx_oper_time (oper_time),
    KEY idx_oper_ip (oper_ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='sys_oper_log表';

-- ============================================================
-- 核心表：sys_notice（RuoYi 标准）
-- ============================================================
CREATE TABLE sys_notice (
    notice_id      INT         NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    notice_title   VARCHAR(50) NOT NULL COMMENT '公告标题',
    notice_type    CHAR(1)     NOT NULL COMMENT '公告类型（1通知 2公告）',
    notice_content LONGTEXT    COMMENT '公告内容',
    status         CHAR(1)     DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
    create_by      VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time    DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by      VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time    DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

-- ============================================================
-- 核心表：sys_notice_read（公告已读记录）
-- ============================================================
CREATE TABLE sys_notice_read (
    read_id   BIGINT   NOT NULL AUTO_INCREMENT COMMENT '已读主键',
    notice_id INT      NOT NULL COMMENT '公告id',
    user_id   BIGINT   NOT NULL COMMENT '用户id',
    read_time DATETIME NOT NULL COMMENT '阅读时间',
    PRIMARY KEY (read_id),
    UNIQUE KEY uk_user_notice (user_id, notice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告已读记录表';

-- ============================================================
-- 核心表：sys_job（RuoYi 标准，兼容 Quartz 调度）
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
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务调度表';

-- ============================================================
-- 核心表：sys_job_log（RuoYi 标准）
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
    job_data             BLOB          DEFAULT NULL COMMENT '存放持久化job对象',
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
    lock_name            VARCHAR(40)   NOT NULL COMMENT '悲观锁名称',
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
-- 初始化数据（Bootstrap Seed Data）
-- ============================================================

-- 岗位初始化
INSERT INTO sys_post (post_id, post_code, post_name, post_sort, status, create_by, create_time, remark) VALUES
(1, 'CEO',   '首席执行官',   1, '0', 'admin', NOW(), '公司最高管理者'),
(2, 'CTO',   '首席技术官',   2, '0', 'admin', NOW(), '技术负责人'),
(3, 'dev',   '后端开发',     3, '0', 'admin', NOW(), '后端开发工程师'),
(4, 'tester','测试工程师',   4, '0', 'admin', NOW(), '测试工程师');

-- 角色初始化
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark) VALUES
(1, '超管',   'admin',  1, '1', 1, 1, '0', '0', 'admin', NOW(), '超级管理员，拥有所有权限'),
(2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', NOW(), '普通角色'),
(3, '访客',   'guest',   3, '5', 1, 1, '0', '0', 'admin', NOW(), '访客角色，仅有查询权限');

-- 用户初始化（admin密码：123456，BCrypt hash from verified DB）
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, pwd_update_date, create_by, create_time, remark) VALUES
(1, 100, 'admin', '管理员', '00', 'admin@lest.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), NOW(), 'admin', NOW(), '管理员'),
(2, 103, 'lest',  '测试用户', '00', 'user@lest.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', NOW(), NOW(), 'admin', NOW(), '测试用户');

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- 用户岗位关联
INSERT INTO sys_user_post (user_id, post_id) VALUES
(1, 1),
(2, 4);

-- 部门初始化（RuoYi 标准）
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, remark) VALUES
(100,  0,   '0',           'Lest科技',   0, '管理员', '15888888888', 'admin@lest.com', '0', '0', 'admin', NOW(), '总公司'),
(101, 100, '0,100',        '深圳总公司', 1, '管理员', '15888888888', 'admin@lest.com', '0', '0', 'admin', NOW(), '深圳运营中心'),
(102, 100, '0,100',        '长沙分公司', 2, '管理员', '15888888888', 'admin@lest.com', '0', '0', 'admin', NOW(), '长沙分部'),
(103, 101, '0,100,101',    '研发部',     1, '管理员', '15888888888', 'admin@lest.com', '0', '0', 'admin', NOW(), '研发部门'),
(104, 101, '0,100,101',    '市场部',     2, '管理员', '15888888888', 'admin@lest.com', '0', '0', 'admin', NOW(), '市场部门'),
(105, 101, '0,100,101',    '测试部',     3, '管理员', '15888888888', 'admin@lest.com', '0', '0', 'admin', NOW(), '测试部门');

-- 角色部门关联（超管可见全部）
INSERT INTO sys_role_dept (role_id, dept_id) VALUES
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105);

-- 字典类型初始化
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark) VALUES
(1,  '用户性别', 'sys_user_sex',       '0', 'admin', NOW(), '用户性别列表'),
(2,  '菜单状态', 'sys_show_hide',      '0', 'admin', NOW(), '菜单状态列表'),
(3,  '系统开关', 'sys_normal_disable', '0', 'admin', NOW(), '系统开关列表'),
(4,  '任务状态', 'sys_job_status',     '0', 'admin', NOW(), '任务状态列表'),
(5,  '任务分组', 'sys_job_group',      '0', 'admin', NOW(), '任务分组列表'),
(6,  '系统是否', 'sys_yes_no',         '0', 'admin', NOW(), '系统是否列表'),
(7,  '通知类型', 'sys_notice_type',    '0', 'admin', NOW(), '通知类型列表'),
(8,  '通知状态', 'sys_notice_status',  '0', 'admin', NOW(), '通知状态列表'),
(9,  '操作类型', 'sys_oper_type',      '0', 'admin', NOW(), '操作类型列表'),
(10, '系统状态', 'sys_common_status',  '0', 'admin', NOW(), '登录状态列表');

-- 字典数据初始化
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
(1,  '男',       '0',       'sys_user_sex',        '', '',        'Y', '0', 'admin', NOW(), '性别男'),
(2,  '女',       '1',       'sys_user_sex',        '', '',        'N', '0', 'admin', NOW(), '性别女'),
(3,  '未知',     '2',       'sys_user_sex',        '', '',        'N', '0', 'admin', NOW(), '性别未知'),
(1,  '显示',     '0',       'sys_show_hide',       '', 'primary', 'Y', '0', 'admin', NOW(), '显示菜单'),
(2,  '隐藏',     '1',       'sys_show_hide',       '', 'danger',  'N', '0', 'admin', NOW(), '隐藏菜单'),
(1,  '正常',     '0',       'sys_normal_disable',  '', 'primary', 'Y', '0', 'admin', NOW(), '正常状态'),
(2,  '停用',     '1',       'sys_normal_disable',  '', 'danger',  'N', '0', 'admin', NOW(), '停用状态'),
(1,  '正常',     '0',       'sys_job_status',      '', 'primary', 'Y', '0', 'admin', NOW(), '正常状态'),
(2,  '暂停',     '1',       'sys_job_status',      '', 'danger',  'N', '0', 'admin', NOW(), '停用状态'),
(1,  '默认',     'DEFAULT', 'sys_job_group',       '', '',        'Y', '0', 'admin', NOW(), '默认分组'),
(2,  '系统',     'SYSTEM',  'sys_job_group',       '', '',        'N', '0', 'admin', NOW(), '系统分组'),
(1,  '是',       'Y',       'sys_yes_no',          '', 'primary', 'Y', '0', 'admin', NOW(), '系统默认是'),
(2,  '否',       'N',       'sys_yes_no',          '', 'danger',  'N', '0', 'admin', NOW(), '系统默认否'),
(1,  '通知',     '1',       'sys_notice_type',     '', 'warning', 'Y', '0', 'admin', NOW(), '通知'),
(2,  '公告',     '2',       'sys_notice_type',     '', 'success', 'N', '0', 'admin', NOW(), '公告'),
(1,  '正常',     '0',       'sys_notice_status',   '', 'primary', 'Y', '0', 'admin', NOW(), '正常状态'),
(2,  '关闭',     '1',       'sys_notice_status',   '', 'danger',  'N', '0', 'admin', NOW(), '关闭状态'),
(0,  '其他',     '0',       'sys_oper_type',       '', 'info',    'N', '0', 'admin', NOW(), '其他操作'),
(1,  '新增',     '1',       'sys_oper_type',       '', 'info',    'N', '0', 'admin', NOW(), '新增操作'),
(2,  '修改',     '2',       'sys_oper_type',       '', 'info',    'N', '0', 'admin', NOW(), '修改操作'),
(3,  '删除',     '3',       'sys_oper_type',       '', 'danger',  'N', '0', 'admin', NOW(), '删除操作'),
(4,  '授权',     '4',       'sys_oper_type',       '', 'primary', 'N', '0', 'admin', NOW(), '授权操作'),
(5,  '导出',     '5',       'sys_oper_type',       '', 'warning', 'N', '0', 'admin', NOW(), '导出操作'),
(6,  '导入',     '6',       'sys_oper_type',       '', 'warning', 'N', '0', 'admin', NOW(), '导入操作'),
(7,  '强退',     '7',       'sys_oper_type',       '', 'danger',  'N', '0', 'admin', NOW(), '强退操作'),
(8,  '生成代码', '8',       'sys_oper_type',       '', 'warning', 'N', '0', 'admin', NOW(), '生成操作'),
(9,  '清空数据', '9',       'sys_oper_type',       '', 'danger',  'N', '0', 'admin', NOW(), '清空操作'),
(1,  '成功',     '0',       'sys_common_status',   '', 'primary', 'N', '0', 'admin', NOW(), '正常状态'),
(2,  '失败',     '1',       'sys_common_status',   '', 'danger',  'N', '0', 'admin', NOW(), '停用状态');

-- 参数配置初始化
INSERT INTO sys_config (config_id, config_name, config_key, config_value, config_type, create_by, create_time, remark) VALUES
(1, '主框架页-默认皮肤样式名称',     'sys.index.skinName',               'skin-blue',  'Y', 'admin', NOW(), '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),
(2, '用户管理-账号初始密码',         'sys.user.initPassword',            '123456',     'Y', 'admin', NOW(), '初始化密码 123456'),
(3, '主框架页-侧边栏主题',           'sys.index.sideTheme',              'theme-dark', 'Y', 'admin', NOW(), '深色主题theme-dark，浅色主题theme-light'),
(4, '账号自助-是否开启用户注册功能', 'sys.account.registerUser',         'false',      'Y', 'admin', NOW(), '是否开启注册用户功能（true开启，false关闭）'),
(5, '用户登录-黑名单列表',           'sys.login.blackIPList',            '',           'Y', 'admin', NOW(), '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),
(6, '用户管理-初始密码修改策略',     'sys.account.initPasswordModify',   '1',          'Y', 'admin', NOW(), '0：关闭，1：提醒用户修改初始密码'),
(7, '用户管理-账号密码更新周期',     'sys.account.passwordValidateDays', '0',          'Y', 'admin', NOW(), '密码更新周期（0不限制，正整数为天数）'),
(8, '用户管理-密码字符范围',         'sys.account.chrtype',              '0',          'Y', 'admin', NOW(), '0任意，1数字，2字母，3字母和数字，4字母数字和特殊字符');

-- 通知公告初始化
INSERT INTO sys_notice (notice_id, notice_title, notice_type, notice_content, status, create_by, create_time, update_by, update_time, remark) VALUES
(1, '温馨提醒：2018-07-01 若依新版本发布啦', '2', '新版本内容', '0', 'admin', '2026-03-24 16:36:03', '', NULL, '管理员'),
(2, '维护通知：2018-07-01 若依系统凌晨维护', '1', '维护内容',   '0', 'admin', '2026-03-24 16:36:04', '', NULL, '管理员'),
(3, '若依开源框架介绍', '1', '<p><span style="color: rgb(230, 0, 0);">项目介绍</span></p><p><font color="#333333">RuoYi开源项目是为企业用户定制的后台脚手架框架，为企业打造的一站式解决方案，降低企业开发成本，提升开发效率。主要包括用户管理、角色管理、部门管理、菜单管理、参数管理、字典管理、</font><span style="color: rgb(51, 51, 51);">岗位管理</span><span style="color: rgb(51, 51, 51);">、定时任务</span><span style="color: rgb(51, 51, 51);">、</span><span style="color: rgb(51, 51, 51);">服务监控、登录日志、操作日志、代码生成等功能。</span></p>', '0', 'admin', '2026-03-24 16:36:04', '', NULL, '管理员');

-- 菜单初始化（RuoYi 标准：M目录 C菜单 F按钮）
INSERT INTO sys_menu (menu_id, parent_id, menu_name, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
-- 一级目录
(1,   0, '工作台',    1, '/dashboard', NULL, '', '', 1, 0, 'M', '0', '0', '', 'IconElHomeFilled', 'admin', NOW(), ''),
(2,   0, '系统管理',  2, '/system',    NULL, '', '', 1, 0, 'M', '0', '0', '', 'IconElSetting',    'admin', NOW(), ''),
(3,   0, '系统监控',  3, '/monitor',   NULL, '', '', 1, 0, 'M', '0', '0', '', 'IconElMonitor',    'admin', NOW(), ''),
-- 工作台首页
(100, 1, '工作台首页', 1, 'index', '/index', '', '', 1, 0, 'C', '0', '0', '', '', 'admin', NOW(), ''),
-- 系统管理子菜单（component 均带前导 /）
(200, 2, '用户管理',  1, 'user',    '/system/user/index',   '', '', 1, 0, 'C', '0', '0', 'system:user:list',   'IconElUser',          'admin', NOW(), ''),
(201, 2, '角色管理',  2, 'role',    '/system/role/index',   '', '', 1, 0, 'C', '0', '0', 'system:role:list',   'IconElUserFilled',    'admin', NOW(), ''),
(202, 2, '菜单管理',  3, 'menu',    '/system/menu/index',   '', '', 1, 0, 'C', '0', '0', 'system:menu:list',   'IconElMenu',          'admin', NOW(), ''),
(203, 2, '部门管理',  4, 'dept',    '/system/dept/index',   '', '', 1, 0, 'C', '0', '0', 'system:dept:list',   'IconElOfficeBuilding','admin', NOW(), ''),
(204, 2, '岗位管理',  5, 'post',    '/system/post/index',   '', '', 1, 0, 'C', '0', '0', 'system:post:list',   'IconElBriefcase',     'admin', NOW(), ''),
(205, 2, '字典管理',  6, 'dict',    '/system/dict/index',   '', '', 1, 0, 'C', '0', '0', 'system:dict:list',   'IconElCollection',    'admin', NOW(), ''),
(206, 2, '参数设置',  7, 'config',  '/system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'IconElEdit',          'admin', NOW(), ''),
(207, 2, '通知公告',  8, 'notice',  '/system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'IconElMessage',       'admin', NOW(), ''),
(208, 2, '日志管理',  9, 'log',     NULL,                   '', '', 1, 0, 'M', '0', '0', '',                   'IconElList',          'admin', NOW(), ''),
-- 日志管理子菜单（parent=208，隶属系统管理下的子目录）
(300, 208, '操作日志', 1, 'operlog',    '/monitor/operlog/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'IconElDocumentCopy', 'admin', NOW(), ''),
(301, 208, '登录日志', 2, 'logininfor', '/monitor/logininfor/index',  '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'IconElKey',          'admin', NOW(), ''),
-- 系统监控子菜单（parent=3）
(302, 3, '在线用户', 1, 'online',    '/monitor/online/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'IconElConnection', 'admin', NOW(), ''),
(303, 3, '定时任务', 2, 'job',       '/monitor/job/index',         '', '', 1, 0, 'C', '0', '0', 'monitor:job:list',    'IconElClock',      'admin', NOW(), ''),
(304, 3, '服务监控', 3, 'server',    '/monitor/server/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'IconElPlatform',   'admin', NOW(), ''),
(305, 3, '缓存监控', 4, 'cache',     '/monitor/cache/index',       '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',  'IconElCpu',        'admin', NOW(), ''),
(306, 3, '缓存列表', 5, 'cacheList', '/monitor/cache/list',        '', '', 1, 0, 'C', '0', '0', 'monitor:cache:query', 'IconElCoin',       'admin', NOW(), ''),
-- 用户管理按钮
(1000, 200, '用户查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:query',   '#', 'admin', NOW(), ''),
(1001, 200, '用户新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:add',     '#', 'admin', NOW(), ''),
(1002, 200, '用户修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:edit',    '#', 'admin', NOW(), ''),
(1003, 200, '用户删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:remove',  '#', 'admin', NOW(), ''),
(1004, 200, '用户导出',     5, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:export',  '#', 'admin', NOW(), ''),
(1005, 200, '用户导入',     6, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:import',  '#', 'admin', NOW(), ''),
(1006, 200, '重置密码',     7, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:user:resetPwd','#', 'admin', NOW(), ''),
-- 角色管理按钮
(1007, 201, '角色查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:role:query',   '#', 'admin', NOW(), ''),
(1008, 201, '角色新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:role:add',     '#', 'admin', NOW(), ''),
(1009, 201, '角色修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:role:edit',    '#', 'admin', NOW(), ''),
(1010, 201, '角色删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:role:remove',  '#', 'admin', NOW(), ''),
(1011, 201, '角色导出',     5, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:role:export',  '#', 'admin', NOW(), ''),
(1012, 201, '分配权限',     6, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:role:edit',    '#', 'admin', NOW(), ''),
-- 菜单管理按钮
(1013, 202, '菜单查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:menu:query',   '#', 'admin', NOW(), ''),
(1014, 202, '菜单新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:menu:add',     '#', 'admin', NOW(), ''),
(1015, 202, '菜单修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:menu:edit',    '#', 'admin', NOW(), ''),
(1016, 202, '菜单删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:menu:remove',  '#', 'admin', NOW(), ''),
-- 部门管理按钮
(1017, 203, '部门查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dept:query',   '#', 'admin', NOW(), ''),
(1018, 203, '部门新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dept:add',     '#', 'admin', NOW(), ''),
(1019, 203, '部门修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dept:edit',    '#', 'admin', NOW(), ''),
(1020, 203, '部门删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dept:remove',  '#', 'admin', NOW(), ''),
-- 岗位管理按钮
(1021, 204, '岗位查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:post:query',   '#', 'admin', NOW(), ''),
(1022, 204, '岗位新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:post:add',     '#', 'admin', NOW(), ''),
(1023, 204, '岗位修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:post:edit',    '#', 'admin', NOW(), ''),
(1024, 204, '岗位删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:post:remove',  '#', 'admin', NOW(), ''),
(1025, 204, '岗位导出',     5, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:post:export',  '#', 'admin', NOW(), ''),
-- 字典管理按钮
(1026, 205, '字典查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dict:query',   '#', 'admin', NOW(), ''),
(1027, 205, '字典新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dict:add',     '#', 'admin', NOW(), ''),
(1028, 205, '字典修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dict:edit',    '#', 'admin', NOW(), ''),
(1029, 205, '字典删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dict:remove',  '#', 'admin', NOW(), ''),
(1030, 205, '字典导出',     5, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:dict:export',  '#', 'admin', NOW(), ''),
-- 参数设置按钮
(1031, 206, '参数查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', NOW(), ''),
(1032, 206, '参数新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:config:add',   '#', 'admin', NOW(), ''),
(1033, 206, '参数修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:config:edit',  '#', 'admin', NOW(), ''),
(1034, 206, '参数删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:config:remove','#', 'admin', NOW(), ''),
(1035, 206, '参数导出',     5, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:config:export','#', 'admin', NOW(), ''),
-- 通知公告按钮重新编号（因上方岗/字/参按钮各加1条）
(1036, 207, '公告查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', NOW(), ''),
(1037, 207, '公告新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:notice:add',   '#', 'admin', NOW(), ''),
(1038, 207, '公告修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:notice:edit',  '#', 'admin', NOW(), ''),
(1039, 207, '公告删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'system:notice:remove','#', 'admin', NOW(), ''),
-- 操作日志按钮
(1040, 300, '操作查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:operlog:query',  '#', 'admin', NOW(), ''),
(1041, 300, '操作删除',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', NOW(), ''),
(1042, 300, '日志导出',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', NOW(), ''),
-- 登录日志按钮
(1043, 301, '登录查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:logininfor:query',  '#', 'admin', NOW(), ''),
(1044, 301, '登录删除',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', NOW(), ''),
(1045, 301, '日志导出',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', NOW(), ''),
-- 在线用户按钮
(1046, 302, '在线查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:online:query',       '#', 'admin', NOW(), ''),
(1047, 302, '批量强退',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', NOW(), ''),
(1048, 302, '单条强退',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', NOW(), ''),
-- 定时任务按钮
(1049, 303, '任务查询',     1, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:query',        '#', 'admin', NOW(), ''),
(1050, 303, '任务新增',     2, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:add',          '#', 'admin', NOW(), ''),
(1051, 303, '任务修改',     3, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:edit',         '#', 'admin', NOW(), ''),
(1052, 303, '任务删除',     4, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:remove',       '#', 'admin', NOW(), ''),
(1053, 303, '状态修改',     5, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', NOW(), ''),
(1054, 303, '任务导出',     6, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:export',       '#', 'admin', NOW(), ''),
(1055, 303, '立即执行',     7, '#', '', '', '',  1, 0, 'F', '0', '0', 'monitor:job:run',          '#', 'admin', NOW(), '');

-- 角色菜单关联（超管拥有所有菜单）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu;

-- ============================================================
-- 项目管理模块 Project Module
-- ============================================================

DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `project_id`    bigint       NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `name`          varchar(100) NOT NULL                COMMENT '项目名称',
  `description`   text                                 COMMENT '项目描述',
  `status`        tinyint      NOT NULL DEFAULT 1    COMMENT '状态：1-活跃 2-已归档',
  `template`      varchar(20)  NOT NULL DEFAULT 'agile' COMMENT '模板：agile/waterfall/kanban',
  `owner_id`      bigint                               COMMENT '负责人用户ID',
  `start_date`    date                                 COMMENT '开始日期',
  `end_date`      date                                 COMMENT '结束日期',
  `deleted`       tinyint      NOT NULL DEFAULT 0    COMMENT '逻辑删除：0-正常 1-删除',
  `create_by`     varchar(64)          DEFAULT ''   COMMENT '创建者',
  `create_time`   datetime                             COMMENT '创建时间',
  `update_by`     varchar(64)          DEFAULT ''   COMMENT '更新者',
  `update_time`   datetime                             COMMENT '更新时间',
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='项目表';

DROP TABLE IF EXISTS `project_member`;
CREATE TABLE `project_member` (
  `project_id` bigint      NOT NULL                    COMMENT '项目ID',
  `user_id`    bigint      NOT NULL                    COMMENT '用户ID',
  `role`       varchar(20) NOT NULL DEFAULT 'developer' COMMENT '角色：admin/developer/observer',
  `joined_at`  datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`project_id`, `user_id`)
) ENGINE=InnoDB COMMENT='项目成员表';

DROP TABLE IF EXISTS `iteration`;
CREATE TABLE `iteration` (
  `iteration_id` bigint       NOT NULL AUTO_INCREMENT COMMENT '迭代ID',
  `project_id`   bigint       NOT NULL                COMMENT '所属项目ID',
  `name`         varchar(100) NOT NULL               COMMENT '迭代名称',
  `goal`         text                                COMMENT '迭代目标',
  `status`       tinyint     NOT NULL DEFAULT 1     COMMENT '状态：1-计划中 2-进行中 3-已完成',
  `start_date`   date                                COMMENT '开始日期',
  `end_date`     date                                COMMENT '结束日期',
  `completed_at` datetime                            COMMENT '完成时间',
  `create_by`    varchar(64)          DEFAULT ''    COMMENT '创建者',
  `create_time`  datetime                            COMMENT '创建时间',
  `update_by`    varchar(64)          DEFAULT ''    COMMENT '更新者',
  `update_time`  datetime                            COMMENT '更新时间',
  PRIMARY KEY (`iteration_id`),
  KEY `idx_iteration_project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='迭代表';

DROP TABLE IF EXISTS `milestone`;
CREATE TABLE `milestone` (
  `milestone_id` bigint       NOT NULL AUTO_INCREMENT COMMENT '里程碑ID',
  `project_id`   bigint       NOT NULL                COMMENT '所属项目ID',
  `name`         varchar(100) NOT NULL                COMMENT '里程碑名称',
  `description`  text                                 COMMENT '描述',
  `target_date`  date                                 COMMENT '目标日期',
  `create_by`    varchar(64)          DEFAULT ''     COMMENT '创建者',
  `create_time`  datetime                             COMMENT '创建时间',
  `update_by`    varchar(64)          DEFAULT ''     COMMENT '更新者',
  `update_time`  datetime                             COMMENT '更新时间',
  PRIMARY KEY (`milestone_id`),
  KEY `idx_milestone_project` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='里程碑表';

DROP TABLE IF EXISTS `milestone_iteration`;
CREATE TABLE `milestone_iteration` (
  `milestone_id` bigint NOT NULL COMMENT '里程碑ID',
  `iteration_id`  bigint NOT NULL COMMENT '迭代ID',
  PRIMARY KEY (`milestone_id`, `iteration_id`)
) ENGINE=InnoDB COMMENT='里程碑-迭代关联表';

-- ============================================================
-- 任务管理模块 Task Module
-- ============================================================

DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `task_id`          bigint        NOT NULL AUTO_INCREMENT  COMMENT '任务ID',
  `title`            varchar(200)  NOT NULL                 COMMENT '任务标题',
  `description`       text                                   COMMENT '任务描述',
  `project_id`       bigint        NOT NULL                 COMMENT '所属项目ID',
  `iteration_id`     bigint                                COMMENT '所属迭代ID',
  `parent_id`       bigint                                COMMENT '父任务ID（子任务）',
  `task_type`        varchar(20)   NOT NULL DEFAULT 'task' COMMENT '类型：story/task/bug/improvement',
  `priority`         varchar(10)   NOT NULL DEFAULT 'p2'   COMMENT '优先级：p0/p1/p2/p3',
  `status`           varchar(20)   NOT NULL DEFAULT 'todo'  COMMENT '状态：todo/in_progress/completed',
  `assignee_id`      bigint                                COMMENT '负责人用户ID',
  `start_time`        datetime                               COMMENT '开始时间',
  `completed_at`      datetime                               COMMENT '完成时间',
  `estimated_hours`   decimal(6,2)                          COMMENT '预估工时（小时）',
  `actual_hours`     decimal(6,2)                          COMMENT '实际工时（小时）',
  `due_date`         date                                   COMMENT '截止日期',
  `sort`             int                    DEFAULT 0      COMMENT '排序权重',
  `deleted`          tinyint       NOT NULL DEFAULT 0      COMMENT '逻辑删除：0-正常 1-删除',
  `create_by`        varchar(64)            DEFAULT ''     COMMENT '创建者',
  `create_time`       datetime                              COMMENT '创建时间',
  `update_by`        varchar(64)            DEFAULT ''     COMMENT '更新者',
  `update_time`       datetime                              COMMENT '更新时间',
  PRIMARY KEY (`task_id`),
  KEY `idx_task_project`  (`project_id`),
  KEY `idx_task_assignee` (`assignee_id`),
  KEY `idx_task_status`   (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务表';

DROP TABLE IF EXISTS `label`;
CREATE TABLE `label` (
  `label_id`     bigint      NOT NULL AUTO_INCREMENT    COMMENT '标签ID',
  `project_id`   bigint      NOT NULL                  COMMENT '所属项目ID',
  `name`        varchar(50) NOT NULL                  COMMENT '标签名称',
  `color`       varchar(20) NOT NULL DEFAULT '#409EFF' COMMENT '标签颜色',
  `create_by`   varchar(64)          DEFAULT ''       COMMENT '创建者',
  `create_time`  datetime                              COMMENT '创建时间',
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='标签表';

DROP TABLE IF EXISTS `task_label`;
CREATE TABLE `task_label` (
  `task_id`  bigint NOT NULL COMMENT '任务ID',
  `label_id` bigint NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`task_id`, `label_id`)
) ENGINE=InnoDB COMMENT='任务-标签关联表';

DROP TABLE IF EXISTS `task_watcher`;
CREATE TABLE `task_watcher` (
  `task_id`     bigint   NOT NULL                         COMMENT '任务ID',
  `user_id`     bigint   NOT NULL                         COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP         COMMENT '关注时间',
  PRIMARY KEY (`task_id`, `user_id`)
) ENGINE=InnoDB COMMENT='任务关注人表';

DROP TABLE IF EXISTS `task_worklog`;
CREATE TABLE `task_worklog` (
  `task_worklog_id` bigint       NOT NULL AUTO_INCREMENT COMMENT '工时记录ID',
  `task_id`        bigint       NOT NULL                COMMENT '任务ID',
  `user_id`        bigint       NOT NULL                COMMENT '记录人用户ID',
  `hours`          decimal(6,2) NOT NULL                COMMENT '工时（小时）',
  `work_date`      date         NOT NULL                COMMENT '工作日期',
  `description`   varchar(500)                         COMMENT '工作内容描述',
  `create_by`     varchar(64)           DEFAULT ''    COMMENT '创建者',
  `create_time`    datetime                             COMMENT '创建时间',
  PRIMARY KEY (`task_worklog_id`),
  KEY `idx_worklog_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务工时记录表';

DROP TABLE IF EXISTS `task_comment`;
CREATE TABLE `task_comment` (
  `task_comment_id` bigint   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `task_id`        bigint   NOT NULL                COMMENT '任务ID',
  `user_id`        bigint   NOT NULL                COMMENT '评论人用户ID',
  `content`        text     NOT NULL                COMMENT '评论内容',
  `parent_id`       bigint                          COMMENT '父评论ID（回复）',
  `create_by`      varchar(64)      DEFAULT ''   COMMENT '创建者',
  `create_time`     datetime                        COMMENT '创建时间',
  `update_time`     datetime                        COMMENT '更新时间',
  PRIMARY KEY (`task_comment_id`),
  KEY `idx_comment_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务评论表';

DROP TABLE IF EXISTS `task_commit`;
CREATE TABLE `task_commit` (
  `task_commit_id`  bigint       NOT NULL AUTO_INCREMENT COMMENT '提交记录ID',
  `task_id`        bigint       NOT NULL                COMMENT '任务ID',
  `repo_id`        varchar(200)                         COMMENT '仓库ID/名称',
  `commit_hash`    varchar(64)  NOT NULL                COMMENT '提交哈希',
  `commit_message`  varchar(500)                         COMMENT '提交信息',
  `author`         varchar(100)                         COMMENT '提交者',
  `commit_time`     datetime                             COMMENT '提交时间',
  `create_by`      varchar(64)           DEFAULT ''    COMMENT '创建者',
  `create_time`     datetime                             COMMENT '创建时间',
  PRIMARY KEY (`task_commit_id`),
  KEY `idx_commit_task` (`task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务关联代码提交表';

DROP TABLE IF EXISTS `task_dependency`;
CREATE TABLE `task_dependency` (
  `task_dependency_id` bigint      NOT NULL AUTO_INCREMENT COMMENT '依赖关系ID',
  `task_id`            bigint      NOT NULL                COMMENT '任务ID',
  `dependency_task_id` bigint      NOT NULL                COMMENT '依赖的任务ID',
  `type`              varchar(20) NOT NULL DEFAULT 'blocks' COMMENT '依赖类型：blocks/blocked_by',
  PRIMARY KEY (`task_dependency_id`),
  UNIQUE KEY `uk_task_dep` (`task_id`, `dependency_task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='任务依赖关系表';

-- ============================================================
-- 项目管理 & 任务管理菜单 sys_menu
-- ============================================================
INSERT INTO sys_menu (menu_id, parent_id, menu_name, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
-- 一级目录：项目管理
(4,   0, '项目管理', 4, '/project', NULL, '', '', 1, 0, 'M', '0', '0', '', 'IconElFolderOpened', 'admin', NOW(), ''),
-- 一级目录：任务管理
(5,   0, '任务管理', 5, '/task',    NULL, '', '', 1, 0, 'M', '0', '0', '', 'IconElTickets',      'admin', NOW(), ''),
-- 项目管理子菜单
(400, 4, '项目列表',  1, 'index',      '/project/index',  '', '', 1, 0, 'C', '0', '0', 'project:list',   'IconElGrid',     'admin', NOW(), ''),
(401, 4, '项目详情',  2, 'detail/:id', '/project/detail', '', '', 1, 0, 'C', '1', '0', 'project:query',  '',               'admin', NOW(), ''),
-- 任务管理子菜单
(500, 5, '任务列表',  1, 'index', '/task/index', '', '', 1, 0, 'C', '0', '0', 'task:list',  'IconElList',   'admin', NOW(), ''),
(501, 5, '任务看板',  2, 'board', '/task/board', '', '', 1, 0, 'C', '0', '0', 'task:board', 'IconElPicture','admin', NOW(), ''),
-- 项目管理按钮
(2000, 400, '项目查询', 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'project:query',  '#', 'admin', NOW(), ''),
(2001, 400, '项目新增', 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'project:add',    '#', 'admin', NOW(), ''),
(2002, 400, '项目修改', 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'project:edit',   '#', 'admin', NOW(), ''),
(2003, 400, '项目删除', 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'project:remove', '#', 'admin', NOW(), ''),
-- 任务管理按钮
(2010, 500, '任务查询', 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'task:query',  '#', 'admin', NOW(), ''),
(2011, 500, '任务新增', 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'task:add',    '#', 'admin', NOW(), ''),
(2012, 500, '任务修改', 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'task:edit',   '#', 'admin', NOW(), ''),
(2013, 500, '任务删除', 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'task:remove', '#', 'admin', NOW(), '');

-- 超管角色关联新菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1,4),(1,5),(1,400),(1,401),(1,500),(1,501),
(1,2000),(1,2001),(1,2002),(1,2003),(1,2010),(1,2011),(1,2012),(1,2013);

-- ============================================================
-- 发布管理模块 Release Module
-- ============================================================

DROP TABLE IF EXISTS `release_issue`;
DROP TABLE IF EXISTS `release_artifact`;
DROP TABLE IF EXISTS `release_plan`;

CREATE TABLE `release_plan` (
  `release_plan_id`    BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '发布计划ID',
  `project_id`         BIGINT       NOT NULL                COMMENT '所属项目ID',
  `name`               VARCHAR(128) NOT NULL DEFAULT ''      COMMENT '发布名称',
  `description`        VARCHAR(500) DEFAULT NULL             COMMENT '发布描述',
  `release_date`       DATETIME     DEFAULT NULL             COMMENT '计划发布日期',
  `status`             INT          DEFAULT 0 COMMENT '状态：0=草稿 1=已发布 2=构建中 3=已发布 4=已归档',
  `build_number`       INT          DEFAULT 0                COMMENT '构建号',
  `git_tag`            VARCHAR(128) DEFAULT NULL             COMMENT 'Git标签',
  `git_branch`         VARCHAR(128) DEFAULT NULL             COMMENT 'Git分支',
  `changelog`          TEXT                                   COMMENT '变更日志',
  `created_by`         BIGINT     DEFAULT NULL               COMMENT '创建者',
  `created_at`         DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by`         BIGINT     DEFAULT NULL               COMMENT '更新者',
  `updated_at`         DATETIME    DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `desc`               VARCHAR(500) DEFAULT NULL             COMMENT '描述',
  `release_type`       INT          DEFAULT 0 COMMENT '类型：0=标准 1=热修复 2=特性 3=测试版 4=预发布',
  `is_draft`           TINYINT(1)  DEFAULT 1                COMMENT '是否草稿',
  `is_stable`          TINYINT(1)  DEFAULT 1                COMMENT '是否稳定版',
  `download_url`       VARCHAR(500) DEFAULT NULL             COMMENT '下载地址',
  `release_notes`      TEXT                                   COMMENT '发布说明',
  `deleted`            TINYINT(1)  DEFAULT 0                 COMMENT '逻辑删除',
  INDEX `idx_release_project` (`project_id`),
  INDEX `idx_release_status`  (`status`),
  INDEX `idx_release_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='发布计划表';

CREATE TABLE `release_artifact` (
  `release_artifact_id` BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '发布产物ID',
  `release_plan_id`     BIGINT       NOT NULL                COMMENT '所属发布计划ID',
  `artifact_name`       VARCHAR(128) NOT NULL DEFAULT ''      COMMENT '产物名称',
  `artifact_type`       VARCHAR(32)  DEFAULT NULL             COMMENT '产物类型',
  `file_name`           VARCHAR(256) DEFAULT NULL             COMMENT '文件名称',
  `file_path`           VARCHAR(500) DEFAULT NULL             COMMENT '文件路径',
  `file_size`           BIGINT       DEFAULT NULL             COMMENT '文件大小',
  `file_hash`           VARCHAR(64)  DEFAULT NULL             COMMENT '文件哈希',
  `download_url`        VARCHAR(500) DEFAULT NULL             COMMENT '下载地址',
  `download_count`      INT          DEFAULT 0                COMMENT '下载次数',
  `uploaded_by`         BIGINT       DEFAULT NULL             COMMENT '上传人',
  `uploaded_at`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `metadata`            TEXT                                   COMMENT '元数据',
  `deleted`             TINYINT(1)  DEFAULT 0                 COMMENT '逻辑删除',
  INDEX `idx_artifact_release` (`release_plan_id`),
  INDEX `idx_artifact_deleted` (`deleted`)
) ENGINE=InnoDB COMMENT='发布产物表';

CREATE TABLE `release_issue` (
  `release_issue_id`  BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '发布关联问题ID',
  `release_plan_id`   BIGINT       NOT NULL                COMMENT '所属发布计划ID',
  `issue_id`           BIGINT       DEFAULT NULL             COMMENT '外部问题ID',
  `task_id`            BIGINT       DEFAULT NULL             COMMENT '关联任务ID',
  `category`           INT          DEFAULT NULL COMMENT '类别：0=bug 1=新特性 2=优化 3=任务',
  `notes`              VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  `added_by`           BIGINT       DEFAULT NULL             COMMENT '添加人',
  `added_at`           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `deleted`            TINYINT(1)  DEFAULT 0                 COMMENT '逻辑删除',
  INDEX `idx_issue_release` (`release_plan_id`),
  INDEX `idx_issue_task`     (`task_id`),
  INDEX `idx_issue_deleted`  (`deleted`)
) ENGINE=InnoDB COMMENT='发布关联问题表';
