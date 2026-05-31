# LEST Platform 数据库设计文档

## 1. 数据库规范

### 1.1 命名规范

| 对象 | 命名规则 | 示例 |
|------|---------|------|
| 数据库 | `{service}_db` | `task_db`, `auth_db` |
| 表名 | `{module}_{entity}` | `task`, `task_comment` |
| 字段名 | `snake_case` | `user_id`, `created_at` |
| 索引名 | `idx_{table}_{column}` | `idx_task_assignee_id` |
| 唯一索引 | `uk_{table}_{column}` | `uk_user_username` |
| 外键约束 | `fk_{table}_{ref_table}` | `fk_task_assignee_id` |

### 1.2 字段类型规范

| 数据类型 | 说明 | 使用场景 |
|---------|------|---------|
| BIGINT | 8 字节整数 | 主键、关联 ID |
| INT | 4 字节整数 | 状态码、数量 |
| VARCHAR(n) | 变长字符串 | 短文本、名称 |
| TEXT | 长文本 | 描述、内容 |
| JSON | JSON 数据 | 配置、扩展属性 |
| DATETIME | 日期时间 | 时间戳 |
| DECIMAL(p,s) | 精确数值 | 金额、评分 |
| TINYINT | 1 字节整数 | 布尔状态 |

### 1.3 通用字段

所有表必须包含以下字段：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

可选通用字段：

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| deleted | TINYINT | DEFAULT 0 | 软删除标记 |
| created_by | BIGINT | | 创建人 ID |
| updated_by | BIGINT | | 更新人 ID |

### 1.4 存储引擎

- 默认存储引擎：`InnoDB`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

---

## 2. 数据库 Schema 规划

```
MySQL 8.4 实例
│
├── auth_db           # 认证服务
│   ├── sys_user
│   ├── sys_role
│   ├── sys_menu
│   ├── sys_org
│   ├── sys_user_role
│   ├── sys_role_menu
│   └── auth_token
│
├── system_db         # 系统管理服务
│   ├── sys_config
│   ├── sys_dict
│   ├── sys_dict_data
│   ├── sys_log
│   └── sys_backup
│
├── project_db        # 项目管理服务
│   ├── project
│   ├── project_member
│   └── iteration
│
├── task_db          # 任务管理服务
│   ├── task
│   ├── task_comment
│   ├── task_worklog
│   ├── task_commit
│   ├── task_merge_request
│   ├── task_pipeline
│   └── task_coding_stats
│
├── code_db          # 代码管理服务
│   ├── code_repository
│   ├── code_commit
│   └── code_merge_request
│
├── ci_db            # CI/CD 服务
│   ├── ci_provider
│   ├── ci_pipeline
│   └── ci_build
│
├── meeting_db       # 会议管理服务
│   ├── meet_meeting
│   ├── meet_participant
│   ├── meet_minutes
│   └── meet_action_item
│
├── notification_db  # 通知服务
│   ├── notification
│   ├── notification_channel
│   ├── notification_template
│   ├── notification_setting
│   └── notification_log
│
├── release_db       # 发布管理服务
│   ├── release_version
│   ├── release_approval
│   ├── release_change
│   └── release_deployment
│
├── plugin_db        # 插件系统服务
│   ├── plugin_registry
│   ├── plugin_extension
│   ├── plugin_version
│   └── plugin_permission
│
├── ai_db            # AI 服务
│   ├── ai_model_config
│   ├── ai_prompt_template
│   ├── ai_conversation
│   ├── ai_code_review
│   ├── ai_meeting_summary
│   ├── ai_perf_insight
│   ├── ai_estimate_accuracy
│   ├── ai_project_risk
│   └── ai_usage_log
│
├── wakapi_db       # WakaTime 集成服务
│   ├── wakapi_heartbeat
│   ├── wakapi_daily_summary
│   ├── wakapi_task_link
│   └── wakapi_machine
│
├── performance_db   # 团队绩效服务
│   ├── perf_work_log
│   ├── perf_metric_config
│   ├── perf_metric
│   ├── perf_goal
│   ├── perf_report
│   └── perf_metric_data
│
├── open_db          # 开放平台服务
│   ├── open_api_token
│   ├── open_oauth_application
│   ├── open_oauth_token
│   ├── open_webhook
│   ├── open_webhook_delivery
│   └── open_api_log
│
└── file_db          # 文件服务
    └── file_record
```

## 3. 认证服务 (auth_db)

### 3.1 sys_user 用户表

```sql
CREATE TABLE sys_user (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '用户ID',
    username        VARCHAR(64)     NOT NULL                  COMMENT '用户名',
    password       VARCHAR(256)    NOT NULL                  COMMENT '密码（加密）',
    nickname        VARCHAR(64)     NOT NULL                  COMMENT '昵称',
    email          VARCHAR(128)    NOT NULL                  COMMENT '邮箱',
    phone          VARCHAR(32)     DEFAULT NULL               COMMENT '手机号',
    avatar         VARCHAR(512)    DEFAULT NULL               COMMENT '头像URL',
    org_id         BIGINT          DEFAULT NULL               COMMENT '机构ID',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    login_attempts  INT             NOT NULL DEFAULT 0        COMMENT '连续登录失败次数',
    locked_until   DATETIME        DEFAULT NULL               COMMENT '账户锁定截止时间',
    last_login_at  DATETIME        DEFAULT NULL               COMMENT '最后登录时间',
    last_login_ip  VARCHAR(45)     DEFAULT NULL               COMMENT '最后登录IP',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除：0-未删除，1-已删除',
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    KEY idx_org_id (org_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 3.2 sys_role 角色表

```sql
CREATE TABLE sys_role (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '角色ID',
    role_name       VARCHAR(64)     NOT NULL                  COMMENT '角色名称',
    role_code       VARCHAR(64)     NOT NULL                  COMMENT '角色编码',
    description     VARCHAR(256)     DEFAULT NULL               COMMENT '角色描述',
    data_scope      INT             NOT NULL DEFAULT 1         COMMENT '数据范围：1-全部，2-本机构，3-本部门，4-仅本人',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    sort            INT             NOT NULL DEFAULT 0         COMMENT '排序号',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_role_code (role_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
```

### 3.3 sys_user_role 用户角色关联表

```sql
CREATE TABLE sys_user_role (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    role_id         BIGINT          NOT NULL                  COMMENT '角色ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
```

### 3.4 sys_menu 菜单表

```sql
CREATE TABLE sys_menu (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '菜单ID',
    parent_id       BIGINT          NOT NULL DEFAULT 0          COMMENT '父菜单ID',
    menu_name       VARCHAR(64)     NOT NULL                  COMMENT '菜单名称',
    menu_type       TINYINT         NOT NULL                  COMMENT '菜单类型：1-目录，2-菜单，3-按钮',
    path            VARCHAR(256)    DEFAULT NULL               COMMENT '路由路径',
    component       VARCHAR(256)    DEFAULT NULL               COMMENT '组件路径',
    redirect        VARCHAR(256)    DEFAULT NULL               COMMENT '重定向地址',
    icon            VARCHAR(64)     DEFAULT NULL               COMMENT '菜单图标',
    query           VARCHAR(256)    DEFAULT NULL               COMMENT '路由参数',
    is_cache        TINYINT         NOT NULL DEFAULT 0         COMMENT '是否缓存：0-否，1-是',
    is_visible      TINYINT         NOT NULL DEFAULT 1         COMMENT '是否显示：0-否，1-是',
    permission      VARCHAR(128)    DEFAULT NULL               COMMENT '权限标识',
    sort            INT             NOT NULL DEFAULT 0         COMMENT '显示顺序',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    KEY idx_parent_id (parent_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';
```

### 3.5 sys_role_menu 角色菜单关联表

```sql
CREATE TABLE sys_role_menu (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    role_id         BIGINT          NOT NULL                  COMMENT '角色ID',
    menu_id         BIGINT          NOT NULL                  COMMENT '菜单ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';
```

### 3.6 sys_org 机构表

```sql
CREATE TABLE sys_org (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '机构ID',
    parent_id       BIGINT          NOT NULL DEFAULT 0          COMMENT '父机构ID',
    org_name        VARCHAR(128)    NOT NULL                  COMMENT '机构名称',
    org_code       VARCHAR(64)     NOT NULL                  COMMENT '机构编码',
    org_type       TINYINT         NOT NULL                  COMMENT '机构类型：1-公司，2-部门',
    leader          BIGINT          DEFAULT NULL               COMMENT '负责人用户ID',
    phone          VARCHAR(32)     DEFAULT NULL               COMMENT '联系电话',
    email          VARCHAR(128)    DEFAULT NULL               COMMENT '邮箱',
    sort            INT             NOT NULL DEFAULT 0         COMMENT '排序号',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_org_code (org_code),
    KEY idx_parent_id (parent_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机构表';
```

### 3.7 auth_token 认证令牌表

```sql
CREATE TABLE auth_token (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    token          VARCHAR(512)    NOT NULL                  COMMENT '访问令牌',
    refresh_token  VARCHAR(512)    DEFAULT NULL               COMMENT '刷新令牌',
    token_type     VARCHAR(16)     NOT NULL DEFAULT 'Bearer'  COMMENT '令牌类型',
    expires_at     DATETIME        NOT NULL                  COMMENT '过期时间',
    refresh_expires_at DATETIME    DEFAULT NULL               COMMENT '刷新令牌过期时间',
    device          VARCHAR(64)     DEFAULT NULL               COMMENT '设备信息',
    ip_address      VARCHAR(45)     DEFAULT NULL               COMMENT 'IP地址',
    revoked         TINYINT         NOT NULL DEFAULT 0         COMMENT '是否撤销：0-否，1-是',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_token (token(255)),
    KEY idx_user_id (user_id),
    KEY idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='认证令牌表';
```

---

## 4. 系统管理服务 (system_db)

### 4.1 sys_config 系统配置表

```sql
CREATE TABLE sys_config (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '配置ID',
    config_key     VARCHAR(128)    NOT NULL                  COMMENT '配置键',
    config_value   TEXT            DEFAULT NULL               COMMENT '配置值',
    config_type    VARCHAR(32)     NOT NULL                  COMMENT '配置类型：string、number、boolean、json',
    config_group   VARCHAR(64)     DEFAULT NULL               COMMENT '配置分组',
    description     VARCHAR(256)    DEFAULT NULL               COMMENT '配置描述',
    is_system      TINYINT         NOT NULL DEFAULT 0         COMMENT '是否系统配置：0-否，1-是',
    sort            INT             NOT NULL DEFAULT 0         COMMENT '排序号',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key),
    KEY idx_config_group (config_group),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
```

### 4.2 sys_dict 字典表

```sql
CREATE TABLE sys_dict (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '字典ID',
    dict_name       VARCHAR(128)    NOT NULL                  COMMENT '字典名称',
    dict_code       VARCHAR(64)     NOT NULL                  COMMENT '字典编码',
    description     VARCHAR(256)    DEFAULT NULL               COMMENT '字典描述',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_dict_code (dict_code),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';
```

### 4.3 sys_dict_data 字典数据表

```sql
CREATE TABLE sys_dict_data (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '字典数据ID',
    dict_id         BIGINT          NOT NULL                  COMMENT '字典ID',
    dict_label      VARCHAR(128)    NOT NULL                  COMMENT '字典标签',
    dict_value      VARCHAR(256)    NOT NULL                  COMMENT '字典键值',
    dict_type       VARCHAR(32)     DEFAULT NULL               COMMENT '字典类型（冗余）',
    css_class       VARCHAR(64)     DEFAULT NULL               COMMENT '样式属性',
    list_class       VARCHAR(32)     DEFAULT NULL               COMMENT '回显样式',
    sort            INT             NOT NULL DEFAULT 0         COMMENT '显示顺序',
    is_default      TINYINT         NOT NULL DEFAULT 0         COMMENT '是否默认：0-否，1-是',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    KEY idx_dict_id (dict_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';
```

### 4.4 sys_log 操作日志表

```sql
CREATE TABLE sys_log (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '日志ID',
    user_id         BIGINT          DEFAULT NULL               COMMENT '用户ID',
    username        VARCHAR(64)     DEFAULT NULL               COMMENT '用户名',
    operation       VARCHAR(64)     NOT NULL                  COMMENT '操作类型',
    request_method  VARCHAR(8)      NOT NULL                  COMMENT '请求方法',
    request_url     VARCHAR(512)    NOT NULL                  COMMENT '请求地址',
    request_params  TEXT            DEFAULT NULL               COMMENT '请求参数',
    request_body   TEXT            DEFAULT NULL               COMMENT '请求体',
    response_status INT             DEFAULT NULL               COMMENT '响应状态',
    response_body   TEXT            DEFAULT NULL               COMMENT '响应内容',
    ip_address      VARCHAR(45)     DEFAULT NULL               COMMENT 'IP地址',
    user_agent      VARCHAR(512)    DEFAULT NULL               COMMENT '用户代理',
    execution_time  INT             DEFAULT NULL               COMMENT '执行时间（毫秒）',
    error_message   TEXT            DEFAULT NULL               COMMENT '错误信息',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_user_id (user_id),
    KEY idx_operation (operation),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
```

---

## 5. 项目管理服务 (project_db)

### 5.1 project 项目表

```sql
CREATE TABLE project (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '项目ID',
    project_name    VARCHAR(128)    NOT NULL                  COMMENT '项目名称',
    project_code    VARCHAR(64)     NOT NULL                  COMMENT '项目编码',
    description     TEXT            DEFAULT NULL               COMMENT '项目描述',
    project_type    VARCHAR(32)     NOT NULL                  COMMENT '项目类型：scrum/kanban/custom',
    visibility      TINYINT         NOT NULL DEFAULT 1         COMMENT '可见性：1-私有，2-项目内',
    owner_id       BIGINT          NOT NULL                  COMMENT '负责人ID',
    icon            VARCHAR(512)    DEFAULT NULL               COMMENT '项目图标',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：1-活跃，2-已归档',
    settings       JSON            DEFAULT NULL               COMMENT '项目设置',
    start_date      DATE            DEFAULT NULL               COMMENT '开始日期',
    end_date        DATE            DEFAULT NULL               COMMENT '结束日期',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_project_code (project_code),
    KEY idx_owner_id (owner_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 外键约束（建议）
ALTER TABLE project ADD CONSTRAINT fk_project_owner FOREIGN KEY (owner_id) REFERENCES sys_user(id);
```

### 5.2 project_member 项目成员表

```sql
CREATE TABLE project_member (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    project_id      BIGINT          NOT NULL                  COMMENT '项目ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    role_type       VARCHAR(32)     NOT NULL                  COMMENT '角色类型：owner/lead/member/viewer',
    join_at         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_project_user (project_id, user_id),
    KEY idx_user_id (user_id),
    KEY idx_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

ALTER TABLE project_member ADD CONSTRAINT fk_project_member_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE project_member ADD CONSTRAINT fk_project_member_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 5.3 iteration 迭代表

```sql
CREATE TABLE iteration (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '迭代ID',
    project_id      BIGINT          NOT NULL                  COMMENT '项目ID',
    iteration_name  VARCHAR(128)    NOT NULL                  COMMENT '迭代名称',
    iteration_code  VARCHAR(64)     NOT NULL                  COMMENT '迭代编码',
    description     TEXT            DEFAULT NULL               COMMENT '迭代描述',
    status         VARCHAR(16)     NOT NULL DEFAULT 'planned' COMMENT '状态：planned/in_progress/completed',
    start_date      DATE            NOT NULL                  COMMENT '开始日期',
    end_date        DATE            NOT NULL                  COMMENT '结束日期',
    goal            TEXT            DEFAULT NULL               COMMENT '迭代目标',
    capacity        INT             DEFAULT NULL               COMMENT '团队容量（人天）',
    velocity        DECIMAL(5,2)    DEFAULT NULL               COMMENT '团队速率（点/人天）',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_project_iteration (project_id, iteration_code),
    KEY idx_project_id (project_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='迭代表';

ALTER TABLE iteration ADD CONSTRAINT fk_iteration_project FOREIGN KEY (project_id) REFERENCES project(id);
```

---

## 6. 任务管理服务 (task_db)

### 6.1 task 任务表

```sql
CREATE TABLE task (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '任务ID',
    project_id      BIGINT          NOT NULL                  COMMENT '项目ID',
    iteration_id    BIGINT          DEFAULT NULL               COMMENT '迭代ID',
    parent_id       BIGINT          DEFAULT NULL               COMMENT '父任务ID',
    task_no         VARCHAR(32)     NOT NULL                  COMMENT '任务编号',
    title           VARCHAR(256)    NOT NULL                  COMMENT '任务标题',
    description     TEXT            DEFAULT NULL               COMMENT '任务描述',
    task_type       VARCHAR(16)     NOT NULL                  COMMENT '任务类型：task/bug/story/epic',
    status         VARCHAR(16)     NOT NULL                  COMMENT '状态：todo/in_progress/review/completed',
    priority       VARCHAR(16)     NOT NULL                  COMMENT '优先级：low/medium/high/urgent',
    assignee_id     BIGINT          DEFAULT NULL               COMMENT '经办人ID',
    creator_id      BIGINT          NOT NULL                  COMMENT '创建人ID',
    reporter_id     BIGINT          DEFAULT NULL               COMMENT '报告人ID',
    story_points    INT             DEFAULT NULL               COMMENT '故事点数',
    estimated_hours DECIMAL(10,2)   DEFAULT NULL               COMMENT '预估工时（小时）',
    actual_hours   DECIMAL(10,2)   DEFAULT NULL               COMMENT '实际工时（小时）',
    started_at      DATETIME        DEFAULT NULL               COMMENT '开始时间',
    due_date        DATE            DEFAULT NULL               COMMENT '截止日期',
    completed_at    DATETIME        DEFAULT NULL               COMMENT '完成时间',
    order_index     INT             NOT NULL DEFAULT 0         COMMENT '排序索引',
    version         INT             NOT NULL DEFAULT 1         COMMENT '乐观锁版本号',
    external_id     VARCHAR(128)    DEFAULT NULL               COMMENT '外部系统ID（如Jira同步）',
    custom_fields   JSON            DEFAULT NULL               COMMENT '自定义字段',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_task_no (task_no),
    KEY idx_project_id (project_id),
    KEY idx_iteration_id (iteration_id),
    KEY idx_parent_id (parent_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_creator_id (creator_id),
    KEY idx_status (status),
    KEY idx_priority (priority),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

ALTER TABLE task ADD CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE task ADD CONSTRAINT fk_task_iteration FOREIGN KEY (iteration_id) REFERENCES iteration(id);
ALTER TABLE task ADD CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) REFERENCES sys_user(id);
ALTER TABLE task ADD CONSTRAINT fk_task_creator FOREIGN KEY (creator_id) REFERENCES sys_user(id);
```

### 6.2 task_comment 任务评论表

```sql
CREATE TABLE task_comment (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '评论ID',
    task_id         BIGINT          NOT NULL                  COMMENT '任务ID',
    parent_id       BIGINT          DEFAULT NULL               COMMENT '父评论ID（回复）',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    content         TEXT            NOT NULL                  COMMENT '评论内容',
    content_type    VARCHAR(16)     NOT NULL DEFAULT 'text'    COMMENT '内容类型：text/markdown',
    attachments     JSON            DEFAULT NULL               COMMENT '附件列表',
    like_count      INT             NOT NULL DEFAULT 0         COMMENT '点赞数',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    KEY idx_task_id (task_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务评论表';

ALTER TABLE task_comment ADD CONSTRAINT fk_comment_task FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE task_comment ADD CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 6.3 task_worklog 任务工时表

```sql
CREATE TABLE task_worklog (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '工时ID',
    task_id         BIGINT          NOT NULL                  COMMENT '任务ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    hours           DECIMAL(10,2)   NOT NULL                  COMMENT '工时（小时）',
    description     VARCHAR(256)    DEFAULT NULL               COMMENT '工作描述',
    work_date       DATE            NOT NULL                  COMMENT '工作日期',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    KEY idx_task_id (task_id),
    KEY idx_user_id (user_id),
    KEY idx_work_date (work_date),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务工时表';

ALTER TABLE task_worklog ADD CONSTRAINT fk_worklog_task FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE task_worklog ADD CONSTRAINT fk_worklog_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 6.4 task_commit 任务-提交关联表

```sql
CREATE TABLE task_commit (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    task_id         BIGINT          NOT NULL                  COMMENT '任务ID',
    repository_id   BIGINT          NOT NULL                  COMMENT '仓库ID',
    commit_sha      VARCHAR(64)     NOT NULL                  COMMENT '提交SHA',
    commit_message  VARCHAR(512)    DEFAULT NULL               COMMENT '提交信息',
    commit_url      VARCHAR(512)    DEFAULT NULL               COMMENT '提交URL',
    author_id       BIGINT          DEFAULT NULL               COMMENT '作者ID',
    author_username VARCHAR(64)     DEFAULT NULL               COMMENT '作者用户名',
    committed_at    DATETIME        DEFAULT NULL               COMMENT '提交时间',
    link_type       VARCHAR(16)     NOT NULL DEFAULT 'manual'  COMMENT '关联类型：manual/auto',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_commit_task (repository_id, commit_sha, task_id),
    KEY idx_task_id (task_id),
    KEY idx_commit_sha (commit_sha),
    KEY idx_author_id (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务提交关联表';

ALTER TABLE task_commit ADD CONSTRAINT fk_task_commit_task FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE task_commit ADD CONSTRAINT fk_task_commit_repository FOREIGN KEY (repository_id) REFERENCES code_repository(id);
```

### 6.5 task_merge_request 任务-MR关联表

```sql
CREATE TABLE task_merge_request (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    task_id         BIGINT          NOT NULL                  COMMENT '任务ID',
    mr_id           BIGINT          NOT NULL                  COMMENT 'MR ID',
    link_type       VARCHAR(16)     NOT NULL DEFAULT 'manual'  COMMENT '关联类型：manual/auto',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_task_mr (task_id, mr_id),
    KEY idx_mr_id (mr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务MR关联表';

ALTER TABLE task_merge_request ADD CONSTRAINT fk_task_mr_task FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE task_merge_request ADD CONSTRAINT fk_task_mr_mr FOREIGN KEY (mr_id) REFERENCES code_merge_request(id);
```

### 6.6 task_pipeline 任务-流水线关联表

```sql
CREATE TABLE task_pipeline (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    task_id         BIGINT          NOT NULL                  COMMENT '任务ID',
    pipeline_id     BIGINT          NOT NULL                  COMMENT '流水线ID',
    build_id       BIGINT          DEFAULT NULL               COMMENT '构建ID',
    link_type       VARCHAR(16)     NOT NULL DEFAULT 'auto'   COMMENT '关联类型：auto/manual',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_task_pipeline (task_id, pipeline_id),
    KEY idx_pipeline_id (pipeline_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务流水线关联表';

ALTER TABLE task_pipeline ADD CONSTRAINT fk_task_pipeline_task FOREIGN KEY (task_id) REFERENCES task(id);
```

---

## 7. 代码管理服务 (code_db)

### 7.1 code_repository 仓库表

```sql
CREATE TABLE code_repository (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '仓库ID',
    project_id      BIGINT          NOT NULL                  COMMENT '项目ID',
    provider        VARCHAR(32)     NOT NULL                  COMMENT '提供商：github/gitlab/gitee',
    owner           VARCHAR(128)    NOT NULL                  COMMENT '仓库所有者',
    repo            VARCHAR(128)    NOT NULL                  COMMENT '仓库名称',
    full_name       VARCHAR(256)    NOT NULL                  COMMENT '完整路径',
    description     TEXT            DEFAULT NULL               COMMENT '仓库描述',
    default_branch  VARCHAR(64)     NOT NULL DEFAULT 'main'    COMMENT '默认分支',
    language        VARCHAR(64)     DEFAULT NULL               COMMENT '主要语言',
    stars           INT             NOT NULL DEFAULT 0         COMMENT 'Star数',
    forks           INT             NOT NULL DEFAULT 0         COMMENT 'Fork数',
    webhook_id     VARCHAR(128)    DEFAULT NULL               COMMENT 'Webhooks ID',
    access_token   VARCHAR(512)    DEFAULT NULL               COMMENT '访问令牌（加密）',
    refresh_token  VARCHAR(512)    DEFAULT NULL               COMMENT '刷新令牌（加密）',
    token_expires_at DATETIME      DEFAULT NULL               COMMENT '令牌过期时间',
    status         TINYINT         NOT NULL DEFAULT 1         COMMENT '状态：0-禁用，1-启用',
    last_synced_at  DATETIME        DEFAULT NULL               COMMENT '最后同步时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_provider_repo (provider, owner, repo),
    KEY idx_project_id (project_id),
    KEY idx_status (status),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码仓库表';

ALTER TABLE code_repository ADD CONSTRAINT fk_repository_project FOREIGN KEY (project_id) REFERENCES project(id);
```

### 7.2 code_commit 代码提交表

```sql
CREATE TABLE code_commit (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '提交ID',
    repository_id   BIGINT          NOT NULL                  COMMENT '仓库ID',
    commit_sha      VARCHAR(64)     NOT NULL                  COMMENT '提交SHA',
    message         TEXT            NOT NULL                  COMMENT '提交信息',
    author_name     VARCHAR(128)    NOT NULL                  COMMENT '作者名称',
    author_email    VARCHAR(128)    NOT NULL                  COMMENT '作者邮箱',
    author_id       BIGINT          DEFAULT NULL               COMMENT '作者用户ID',
    committed_at    DATETIME        NOT NULL                  COMMENT '提交时间',
    additions       INT             NOT NULL DEFAULT 0         COMMENT '新增行数',
    deletions       INT             NOT NULL DEFAULT 0         COMMENT '删除行数',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_commit_sha (repository_id, commit_sha),
    KEY idx_author_id (author_id),
    KEY idx_committed_at (committed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码提交表';

ALTER TABLE code_commit ADD CONSTRAINT fk_commit_repository FOREIGN KEY (repository_id) REFERENCES code_repository(id);
ALTER TABLE code_commit ADD CONSTRAINT fk_commit_author FOREIGN KEY (author_id) REFERENCES sys_user(id);
```

### 7.3 code_merge_request MR/PR 表

```sql
CREATE TABLE code_merge_request (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT 'MR ID',
    repository_id   BIGINT          NOT NULL                  COMMENT '仓库ID',
    mr_number       INT             NOT NULL                  COMMENT 'MR 编号',
    title           VARCHAR(512)    NOT NULL                  COMMENT 'MR 标题',
    description     TEXT            DEFAULT NULL               COMMENT 'MR 描述',
    source_branch   VARCHAR(128)    NOT NULL                  COMMENT '源分支',
    target_branch   VARCHAR(128)    NOT NULL                  COMMENT '目标分支',
    author_id       BIGINT          NOT NULL                  COMMENT '作者ID',
    author_username VARCHAR(64)     NOT NULL                  COMMENT '作者用户名',
    status         VARCHAR(16)     NOT NULL                  COMMENT '状态：open/merged/closed',
    draft          TINYINT         NOT NULL DEFAULT 0         COMMENT '是否草稿',
    review_status   VARCHAR(32)     DEFAULT NULL               COMMENT '评审状态：pending/approved/changes_requested',
    additions       INT             NOT NULL DEFAULT 0         COMMENT '新增行数',
    deletions       INT             NOT NULL DEFAULT 0         COMMENT '删除行数',
    changed_files   INT             NOT NULL DEFAULT 0         COMMENT '变更文件数',
    merged_at      DATETIME        DEFAULT NULL               COMMENT '合并时间',
    closed_at      DATETIME        DEFAULT NULL               COMMENT '关闭时间',
    web_url        VARCHAR(512)    DEFAULT NULL               COMMENT 'Web URL',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_repo_mr (repository_id, mr_number),
    KEY idx_author_id (author_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='代码MR表';

ALTER TABLE code_merge_request ADD CONSTRAINT fk_mr_repository FOREIGN KEY (repository_id) REFERENCES code_repository(id);
ALTER TABLE code_merge_request ADD CONSTRAINT fk_mr_author FOREIGN KEY (author_id) REFERENCES sys_user(id);
```

---

## 8. CI/CD 服务 (ci_db)

### 8.1 ci_provider CI提供商表

```sql
CREATE TABLE ci_provider (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '提供商ID',
    provider_name   VARCHAR(64)     NOT NULL                  COMMENT '提供商名称',
    provider_type   VARCHAR(32)     NOT NULL                  COMMENT '提供商类型：jenkins/gitlab_ci/github_actions',
    api_url         VARCHAR(256)    DEFAULT NULL               COMMENT 'API 地址',
    web_url         VARCHAR(256)    DEFAULT NULL               COMMENT 'Web 地址',
    access_token   VARCHAR(512)    DEFAULT NULL               COMMENT '访问令牌（加密）',
    webhook_secret VARCHAR(256)    DEFAULT NULL               COMMENT 'Webhook 密钥',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    description     VARCHAR(256)    DEFAULT NULL               COMMENT '描述',
    settings       JSON            DEFAULT NULL               COMMENT '额外设置',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    KEY idx_type (provider_type),
    KEY idx_enabled (enabled),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CI提供商表';
```

### 8.2 ci_pipeline 流水线表

```sql
CREATE TABLE ci_pipeline (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '流水线ID',
    provider_id     BIGINT          NOT NULL                  COMMENT '提供商ID',
    project_id      BIGINT          DEFAULT NULL               COMMENT '关联项目ID',
    pipeline_name   VARCHAR(128)    NOT NULL                  COMMENT '流水线名称',
    external_id     VARCHAR(128)   NOT NULL                  COMMENT '外部系统ID',
    external_url    VARCHAR(512)   DEFAULT NULL               COMMENT '外部URL',
    status         VARCHAR(32)     NOT NULL                  COMMENT '状态：pending/running/success/failed/canceled',
    branch          VARCHAR(128)    DEFAULT NULL               COMMENT '触发分支',
    commit_sha      VARCHAR(64)     DEFAULT NULL               COMMENT '触发提交',
    triggered_by     VARCHAR(64)     DEFAULT NULL               COMMENT '触发者',
    started_at      DATETIME        DEFAULT NULL               COMMENT '开始时间',
    finished_at     DATETIME        DEFAULT NULL               COMMENT '结束时间',
    duration        INT             DEFAULT NULL               COMMENT '持续时间（秒）',
    stages          JSON            DEFAULT NULL               COMMENT '阶段配置',
    variables       JSON            DEFAULT NULL               COMMENT '环境变量',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_external_id (provider_id, external_id),
    KEY idx_provider_id (provider_id),
    KEY idx_project_id (project_id),
    KEY idx_status (status),
    KEY idx_branch (branch)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CI流水线表';

ALTER TABLE ci_pipeline ADD CONSTRAINT fk_pipeline_provider FOREIGN KEY (provider_id) REFERENCES ci_provider(id);
```

### 8.3 ci_build 构建记录表

```sql
CREATE TABLE ci_build (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '构建ID',
    pipeline_id     BIGINT          NOT NULL                  COMMENT '流水线ID',
    build_number    INT             NOT NULL                  COMMENT '构建编号',
    stage           VARCHAR(64)     DEFAULT NULL               COMMENT '当前阶段',
    status         VARCHAR(32)     NOT NULL                  COMMENT '状态：pending/running/success/failed/canceled',
    job_name        VARCHAR(128)    DEFAULT NULL               COMMENT '作业名称',
    runner          VARCHAR(128)    DEFAULT NULL               COMMENT '运行器',
    started_at      DATETIME        DEFAULT NULL               COMMENT '开始时间',
    finished_at     DATETIME        DEFAULT NULL               COMMENT '结束时间',
    duration        INT             DEFAULT NULL               COMMENT '持续时间（秒）',
    artifacts       JSON            DEFAULT NULL               COMMENT '产物列表',
    logs            TEXT            DEFAULT NULL               COMMENT '构建日志',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_pipeline_build (pipeline_id, build_number),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CI构建记录表';

ALTER TABLE ci_build ADD CONSTRAINT fk_build_pipeline FOREIGN KEY (pipeline_id) REFERENCES ci_pipeline(id);
```

---

## 9. 会议管理服务 (meeting_db)

### 9.1 meet_meeting 会议表

```sql
CREATE TABLE meet_meeting (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '会议ID',
    project_id      BIGINT          DEFAULT NULL               COMMENT '项目ID',
    iteration_id    BIGINT          DEFAULT NULL               COMMENT '迭代ID',
    meeting_title   VARCHAR(256)    NOT NULL                  COMMENT '会议标题',
    meeting_type    VARCHAR(32)     NOT NULL                  COMMENT '会议类型：daily/weekly/sprint_planning/sprint_retrospective/sprint_review/one_on_one/brainstorm',
    status         VARCHAR(16)     NOT NULL DEFAULT 'scheduled' COMMENT '状态：scheduled/in_progress/completed/canceled',
    start_time      DATETIME        NOT NULL                  COMMENT '开始时间',
    end_time        DATETIME        NOT NULL                  COMMENT '结束时间',
    duration        INT             NOT NULL DEFAULT 0         COMMENT '持续时间（分钟）',
    location        VARCHAR(256)    DEFAULT NULL               COMMENT '会议地点',
    online_url      VARCHAR(512)    DEFAULT NULL               COMMENT '在线会议链接',
    organizer_id    BIGINT          NOT NULL                  COMMENT '组织者ID',
    agenda          TEXT            DEFAULT NULL               COMMENT '会议议程',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    KEY idx_project_id (project_id),
    KEY idx_iteration_id (iteration_id),
    KEY idx_organizer_id (organizer_id),
    KEY idx_status (status),
    KEY idx_start_time (start_time),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议表';

ALTER TABLE meet_meeting ADD CONSTRAINT fk_meeting_organizer FOREIGN KEY (organizer_id) REFERENCES sys_user(id);
```

### 9.2 meet_participant 会议参与者表

```sql
CREATE TABLE meet_participant (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    meeting_id      BIGINT          NOT NULL                  COMMENT '会议ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    role_type       VARCHAR(16)     NOT NULL DEFAULT 'member'  COMMENT '角色类型：organizer/participant',
    response_status VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '响应状态：pending/accepted/declined/tentative',
    attended        TINYINT         DEFAULT NULL               COMMENT '是否出席：0-否，1-是',
    report_content  JSON            DEFAULT NULL               COMMENT '站会汇报内容',
    joined_at       DATETIME        DEFAULT NULL               COMMENT '加入时间',
    left_at         DATETIME        DEFAULT NULL               COMMENT '离开时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_meeting_user (meeting_id, user_id),
    KEY idx_user_id (user_id),
    KEY idx_response_status (response_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议参与者表';

ALTER TABLE meet_participant ADD CONSTRAINT fk_participant_meeting FOREIGN KEY (meeting_id) REFERENCES meet_meeting(id);
ALTER TABLE meet_participant ADD CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 9.3 meet_minutes 会议纪要表

```sql
CREATE TABLE meet_minutes (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '纪要ID',
    meeting_id      BIGINT          NOT NULL                  COMMENT '会议ID',
    content         TEXT            DEFAULT NULL               COMMENT '纪要内容',
    decisions       JSON            DEFAULT NULL               COMMENT '会议决议',
    key_points      JSON            DEFAULT NULL               COMMENT '关键讨论点',
    recorder_id     BIGINT          DEFAULT NULL               COMMENT '记录人ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_meeting (meeting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议纪要表';

ALTER TABLE meet_minutes ADD CONSTRAINT fk_minutes_meeting FOREIGN KEY (meeting_id) REFERENCES meet_meeting(id);
ALTER TABLE meet_minutes ADD CONSTRAINT fk_minutes_recorder FOREIGN KEY (recorder_id) REFERENCES sys_user(id);
```

### 9.4 meet_action_item 会议行动项表

```sql
CREATE TABLE meet_action_item (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '行动项ID',
    meeting_id      BIGINT          NOT NULL                  COMMENT '会议ID',
    task_id         BIGINT          DEFAULT NULL               COMMENT '关联任务ID',
    assignee_id     BIGINT          DEFAULT NULL               COMMENT '负责人ID',
    assignee_name   VARCHAR(64)     DEFAULT NULL               COMMENT '负责人姓名',
    title           VARCHAR(256)    NOT NULL                  COMMENT '行动项标题',
    description     TEXT            DEFAULT NULL               COMMENT '行动项描述',
    due_date        DATE            DEFAULT NULL               COMMENT '截止日期',
    status         VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending/in_progress/completed/canceled',
    priority       VARCHAR(16)     DEFAULT 'medium'          COMMENT '优先级：low/medium/high',
    completed_at    DATETIME        DEFAULT NULL               COMMENT '完成时间',
    created_by      BIGINT          NOT NULL                  COMMENT '创建人ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_meeting_id (meeting_id),
    KEY idx_task_id (task_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_status (status),
    KEY idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议行动项表';

ALTER TABLE meet_action_item ADD CONSTRAINT fk_action_meeting FOREIGN KEY (meeting_id) REFERENCES meet_meeting(id);
ALTER TABLE meet_action_item ADD CONSTRAINT fk_action_task FOREIGN KEY (task_id) REFERENCES task(id);
ALTER TABLE meet_action_item ADD CONSTRAINT fk_action_assignee FOREIGN KEY (assignee_id) REFERENCES sys_user(id);
```

---

## 10. 通知服务 (notification_db)

### 10.1 notification 通知表

```sql
CREATE TABLE notification (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '通知ID',
    user_id         BIGINT          NOT NULL                  COMMENT '接收用户ID',
    title           VARCHAR(256)    NOT NULL                  COMMENT '通知标题',
    content         TEXT            DEFAULT NULL               COMMENT '通知内容',
    type           VARCHAR(32)     NOT NULL                  COMMENT '通知类型',
    category       VARCHAR(32)     DEFAULT NULL               COMMENT '分类：task/code/ci/meeting/system',
    event_type      VARCHAR(64)     DEFAULT NULL               COMMENT '事件类型',
    source_type     VARCHAR(32)     DEFAULT NULL               COMMENT '来源类型',
    source_id       BIGINT          DEFAULT NULL               COMMENT '来源ID',
    priority       TINYINT         NOT NULL DEFAULT 1         COMMENT '优先级：1-低，2-中，3-高',
    is_read        TINYINT         NOT NULL DEFAULT 0         COMMENT '是否已读：0-否，1-是',
    read_at         DATETIME        DEFAULT NULL               COMMENT '阅读时间',
    action_url     VARCHAR(512)    DEFAULT NULL               COMMENT '操作链接',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_user_id (user_id),
    KEY idx_type (type),
    KEY idx_category (category),
    KEY idx_is_read (is_read),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

ALTER TABLE notification ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 10.2 notification_setting 通知设置表

```sql
CREATE TABLE notification_setting (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '设置ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    event_type      VARCHAR(64)     NOT NULL                  COMMENT '事件类型',
    channel         VARCHAR(32)     NOT NULL                  COMMENT '通知渠道：in_app/email/dingtalk/wechat/feishu',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用：0-禁用，1-启用',
    quiet_hours_start TIME          DEFAULT NULL               COMMENT '免打扰开始时间',
    quiet_hours_end   TIME          DEFAULT NULL               COMMENT '免打扰结束时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_user_event_channel (user_id, event_type, channel),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知设置表';
```

### 10.3 notification_channel 通知渠道配置表

```sql
CREATE TABLE notification_channel (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '渠道ID',
    channel_code    VARCHAR(32)     NOT NULL                  COMMENT '渠道编码：email/dingtalk/wechat/feishu/sms',
    channel_name   VARCHAR(64)     NOT NULL                  COMMENT '渠道名称',
    description     VARCHAR(256)    DEFAULT NULL               COMMENT '渠道描述',
    config         JSON            DEFAULT NULL               COMMENT '渠道配置（JSON格式，如钉钉的webhook地址、密钥等）',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用：0-禁用，1-启用',
    is_global      TINYINT         NOT NULL DEFAULT 0         COMMENT '是否全局配置：0-否，1-是',
    creator_id     BIGINT          DEFAULT NULL               COMMENT '创建人ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_channel_code (channel_code),
    KEY idx_enabled (enabled),
    KEY idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知渠道配置表';
```

### 10.4 notification_template 通知模板表

```sql
CREATE TABLE notification_template (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '模板ID',
    template_code   VARCHAR(64)     NOT NULL                  COMMENT '模板编码',
    template_name   VARCHAR(128)    NOT NULL                  COMMENT '模板名称',
    event_type      VARCHAR(64)     NOT NULL                  COMMENT '关联事件类型',
    channel         VARCHAR(32)     NOT NULL                  COMMENT '通知渠道：in_app/email/dingtalk/wechat/feishu',
    title_template  VARCHAR(256)    NOT NULL                  COMMENT '标题模板（支持变量替换，如 {userName}）',
    content_template TEXT           NOT NULL                  COMMENT '内容模板（支持 Markdown）',
    variables       JSON            DEFAULT NULL               COMMENT '变量定义列表',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    is_default     TINYINT         NOT NULL DEFAULT 0         COMMENT '是否默认模板',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_event_channel (event_type, channel),
    KEY idx_channel (channel),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';
```

### 10.5 notification_log 通知发送日志表

```sql
CREATE TABLE notification_log (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '日志ID',
    notification_id BIGINT          DEFAULT NULL               COMMENT '通知ID',
    channel         VARCHAR(32)     NOT NULL                  COMMENT '通知渠道',
    recipient       VARCHAR(256)    NOT NULL                  COMMENT '接收者（邮箱/手机号/UserID）',
    title           VARCHAR(256)    NOT NULL                  COMMENT '发送标题',
    content         TEXT            NOT NULL                  COMMENT '发送内容',
    status          VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending/success/failed',
    error_message   VARCHAR(512)    DEFAULT NULL               COMMENT '错误信息',
    retry_count     INT             NOT NULL DEFAULT 0         COMMENT '重试次数',
    sent_at         DATETIME        DEFAULT NULL               COMMENT '发送时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_notification_id (notification_id),
    KEY idx_channel (channel),
    KEY idx_status (status),
    KEY idx_recipient (recipient(64)),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送日志表';
```

---

## 11. 发布管理服务 (release_db)

### 11.1 release_version 发布版本表

```sql
CREATE TABLE release_version (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '版本ID',
    project_id      BIGINT          NOT NULL                  COMMENT '项目ID',
    version         VARCHAR(64)     NOT NULL                  COMMENT '版本号',
    release_name    VARCHAR(128)    NOT NULL                  COMMENT '版本名称',
    description     TEXT            DEFAULT NULL               COMMENT '版本描述',
    release_type    VARCHAR(16)     NOT NULL                  COMMENT '发布类型：major/minor/patch/hotfix',
    status         VARCHAR(16)     NOT NULL DEFAULT 'draft'  COMMENT '状态：draft/pending/approved/deploying/deployed/rolled_back/failed',
    environment     VARCHAR(32)     NOT NULL                  COMMENT '环境：dev/test/staging/production',
    planned_date    DATE            DEFAULT NULL               COMMENT '计划发布日期',
    released_by     BIGINT          DEFAULT NULL               COMMENT '发布人ID',
    released_at     DATETIME        DEFAULT NULL               COMMENT '发布时间',
    artifact_url    VARCHAR(512)    DEFAULT NULL               COMMENT '产物地址',
    changelog       TEXT            DEFAULT NULL               COMMENT '变更日志',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted        TINYINT         NOT NULL DEFAULT 0         COMMENT '软删除',
    UNIQUE KEY uk_project_version (project_id, version),
    KEY idx_project_id (project_id),
    KEY idx_status (status),
    KEY idx_environment (environment),
    KEY idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布版本表';

ALTER TABLE release_version ADD CONSTRAINT fk_release_project FOREIGN KEY (project_id) REFERENCES project(id);
ALTER TABLE release_version ADD CONSTRAINT fk_release_released_by FOREIGN KEY (released_by) REFERENCES sys_user(id);
```

### 11.2 release_approval 发布审批表

```sql
CREATE TABLE release_approval (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '审批ID',
    version_id      BIGINT          NOT NULL                  COMMENT '版本ID',
    approver_id     BIGINT          NOT NULL                  COMMENT '审批人ID',
    approval_order  INT             NOT NULL DEFAULT 1         COMMENT '审批顺序',
    status         VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending/approved/rejected',
    comment         TEXT            DEFAULT NULL               COMMENT '审批意见',
    decided_at      DATETIME        DEFAULT NULL               COMMENT '审批时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_version_id (version_id),
    KEY idx_approver_id (approver_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布审批表';

ALTER TABLE release_approval ADD CONSTRAINT fk_approval_version FOREIGN KEY (version_id) REFERENCES release_version(id);
ALTER TABLE release_approval ADD CONSTRAINT fk_approval_approver FOREIGN KEY (approver_id) REFERENCES sys_user(id);
```

### 11.3 release_change 发布变更表

```sql
CREATE TABLE release_change (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '变更ID',
    version_id      BIGINT          NOT NULL                  COMMENT '版本ID',
    task_id         BIGINT          DEFAULT NULL               COMMENT '关联任务ID',
    change_type     VARCHAR(16)     NOT NULL                  COMMENT '变更类型：task/mr/commit',
    change_id       BIGINT          NOT NULL                  COMMENT '变更实体ID',
    change_summary  VARCHAR(256)    DEFAULT NULL               COMMENT '变更摘要',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_version_id (version_id),
    KEY idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布变更表';

ALTER TABLE release_change ADD CONSTRAINT fk_change_version FOREIGN KEY (version_id) REFERENCES release_version(id);
ALTER TABLE release_change ADD CONSTRAINT fk_change_task FOREIGN KEY (task_id) REFERENCES task(id);
```

### 11.4 release_deployment 发布部署记录表

```sql
CREATE TABLE release_deployment (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '部署ID',
    version_id      BIGINT          NOT NULL                  COMMENT '版本ID',
    pipeline_id     BIGINT          DEFAULT NULL               COMMENT '关联CI流水线ID',
    environment     VARCHAR(32)     NOT NULL                  COMMENT '部署环境：dev/test/staging/production',
    status         VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending/running/success/failed/rollback',
    deployer_id    BIGINT          DEFAULT NULL               COMMENT '部署人ID',
    started_at      DATETIME        DEFAULT NULL               COMMENT '开始时间',
    finished_at     DATETIME        DEFAULT NULL               COMMENT '结束时间',
    duration        INT             DEFAULT NULL               COMMENT '持续时间（秒）',
    deploy_logs    TEXT            DEFAULT NULL               COMMENT '部署日志',
    artifact_url    VARCHAR(512)    DEFAULT NULL               COMMENT '产物地址',
    rollback_to_version VARCHAR(64)  DEFAULT NULL               COMMENT '回滚目标版本',
    rollback_reason VARCHAR(256)    DEFAULT NULL               COMMENT '回滚原因',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_version_id (version_id),
    KEY idx_environment (environment),
    KEY idx_status (status),
    KEY idx_deployer_id (deployer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发布部署记录表';

ALTER TABLE release_deployment ADD CONSTRAINT fk_deployment_version FOREIGN KEY (version_id) REFERENCES release_version(id);
ALTER TABLE release_deployment ADD CONSTRAINT fk_deployment_deployer FOREIGN KEY (deployer_id) REFERENCES sys_user(id);
```

---

## 12. 插件系统服务 (plugin_db)

### 12.1 plugin_registry 插件注册表

```sql
CREATE TABLE plugin_registry (
    id              VARCHAR(64)      PRIMARY KEY                  COMMENT '插件ID',
    name            VARCHAR(128)    NOT NULL                  COMMENT '插件名称',
    description     VARCHAR(512)    DEFAULT NULL               COMMENT '插件描述',
    version         VARCHAR(32)     NOT NULL                  COMMENT '当前版本',
    author          VARCHAR(128)    DEFAULT NULL               COMMENT '作者',
    author_email    VARCHAR(128)    DEFAULT NULL               COMMENT '作者邮箱',
    homepage        VARCHAR(256)    DEFAULT NULL               COMMENT '插件主页',
    license         VARCHAR(32)     DEFAULT NULL               COMMENT '许可证',
    icon_url        VARCHAR(256)    DEFAULT NULL               COMMENT '图标URL',
    source          VARCHAR(16)     NOT NULL                  COMMENT '来源：market/file/url',
    status         VARCHAR(16)     NOT NULL DEFAULT 'uninstalled' COMMENT '状态：uninstalled/installed/enabled/disabled',
    installed_at    DATETIME        DEFAULT NULL               COMMENT '安装时间',
    enabled_at      DATETIME        DEFAULT NULL               COMMENT '启用时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_status (status),
    KEY idx_author (author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='插件注册表';
```

### 12.2 plugin_extension 插件扩展点注册表

```sql
CREATE TABLE plugin_extension (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键ID',
    plugin_id       VARCHAR(64)     NOT NULL                  COMMENT '插件ID',
    extension_type  VARCHAR(32)     NOT NULL                  COMMENT '扩展点类型',
    extension_id    VARCHAR(64)     NOT NULL                  COMMENT '扩展点ID',
    location        VARCHAR(128)    DEFAULT NULL               COMMENT '渲染位置',
    label           VARCHAR(128)    DEFAULT NULL               COMMENT '显示标签',
    order_index     INT             NOT NULL DEFAULT 0         COMMENT '渲染顺序',
    config          JSON            DEFAULT NULL               COMMENT '扩展点配置',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_plugin_extension (plugin_id, extension_type, extension_id),
    KEY idx_plugin_id (plugin_id),
    KEY idx_extension_type (extension_type),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='插件扩展点表';
```

### 12.3 plugin_version 插件版本表

```sql
CREATE TABLE plugin_version (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '版本ID',
    plugin_id       VARCHAR(64)     NOT NULL                  COMMENT '插件ID',
    version         VARCHAR(32)     NOT NULL                  COMMENT '版本号',
    changelog       TEXT            DEFAULT NULL               COMMENT '版本变更说明',
    package_url     VARCHAR(512)    NOT NULL                  COMMENT '插件包下载地址',
    package_hash    VARCHAR(64)     DEFAULT NULL               COMMENT '包Hash值（SHA256）',
    min_platform_version VARCHAR(32)  DEFAULT NULL               COMMENT '最低平台版本要求',
    max_platform_version VARCHAR(32)  DEFAULT NULL               COMMENT '最高平台版本要求',
    dependencies    JSON            DEFAULT NULL               COMMENT '依赖插件列表',
    released_at     DATETIME        NOT NULL                  COMMENT '发布时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_plugin_version (plugin_id, version),
    KEY idx_plugin_id (plugin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='插件版本表';
```

### 12.4 plugin_permission 插件权限声明表

```sql
CREATE TABLE plugin_permission (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '权限ID',
    plugin_id       VARCHAR(64)     NOT NULL                  COMMENT '插件ID',
    permission_code VARCHAR(64)     NOT NULL                  COMMENT '权限编码',
    permission_name VARCHAR(128)    NOT NULL                  COMMENT '权限名称',
    description     VARCHAR(256)    DEFAULT NULL               COMMENT '权限描述',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_plugin_permission (plugin_id, permission_code),
    KEY idx_plugin_id (plugin_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='插件权限声明表';
```

---

## 13. AI 服务 (ai_db)

### 13.1 ai_model_config AI模型配置表

```sql
CREATE TABLE ai_model_config (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '配置ID',
    provider        VARCHAR(32)     NOT NULL                  COMMENT '模型提供商：openai/anthropic/qwen/kimi/local',
    model_id        VARCHAR(64)     NOT NULL                  COMMENT '模型ID',
    model_name      VARCHAR(128)    NOT NULL                  COMMENT '模型名称',
    api_base        VARCHAR(256)    DEFAULT NULL               COMMENT 'API地址',
    api_key         VARCHAR(256)    DEFAULT NULL               COMMENT 'API Key（加密存储）',
    embedding_model VARCHAR(64)     DEFAULT NULL               COMMENT 'Embedding模型',
    config         JSON            DEFAULT NULL               COMMENT '额外配置（temperature/max_tokens等）',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    is_default     TINYINT         NOT NULL DEFAULT 0         COMMENT '是否默认模型',
    priority       INT             NOT NULL DEFAULT 0         COMMENT '优先级（数字越小越优先）',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_provider_model (provider, model_id),
    KEY idx_enabled (enabled),
    KEY idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI模型配置表';
```

### 13.2 ai_prompt_template AI提示词模板表

```sql
CREATE TABLE ai_prompt_template (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '模板ID',
    template_code   VARCHAR(64)     NOT NULL                  COMMENT '模板编码',
    template_name   VARCHAR(128)    NOT NULL                  COMMENT '模板名称',
    scene           VARCHAR(32)     NOT NULL                  COMMENT '使用场景：code_review/meeting_summary/task_analysis/chat',
    system_prompt   TEXT            DEFAULT NULL               COMMENT '系统提示词',
    user_prompt     TEXT            DEFAULT NULL               COMMENT '用户提示词模板',
    variables       JSON            DEFAULT NULL               COMMENT '变量定义',
    model_config_id BIGINT          DEFAULT NULL               COMMENT '关联模型配置ID',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_template_code (template_code),
    KEY idx_scene (scene),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI提示词模板表';
```

### 13.3 ai_conversation AI会话表

```sql
CREATE TABLE ai_conversation (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '会话ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    session_id      VARCHAR(64)     NOT NULL                  COMMENT '会话标识（用于多轮对话）',
    scene           VARCHAR(32)     NOT NULL                  COMMENT '使用场景',
    model_config_id BIGINT          DEFAULT NULL               COMMENT '使用的模型配置ID',
    messages        JSON            NOT NULL                  COMMENT '消息历史',
    total_tokens    INT             NOT NULL DEFAULT 0         COMMENT '总Token数',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_session_id (session_id),
    KEY idx_scene (scene),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会话表';
```

### 13.4 ai_code_review AI代码评审记录表

```sql
CREATE TABLE ai_code_review (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '评审ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    commit_id       BIGINT          DEFAULT NULL               COMMENT '提交ID',
    mr_id           BIGINT          DEFAULT NULL               COMMENT 'MR ID',
    model_config_id BIGINT          DEFAULT NULL               COMMENT '使用的模型配置ID',
    language        VARCHAR(16)     DEFAULT NULL               COMMENT '代码语言',
    code_snippet    TEXT            DEFAULT NULL               COMMENT '代码片段',
    prompt          TEXT            NOT NULL                  COMMENT '输入提示词',
    review_result   TEXT            NOT NULL                  COMMENT '评审结果',
    issues          JSON            DEFAULT NULL               COMMENT '问题列表',
    total_tokens    INT             NOT NULL DEFAULT 0         COMMENT '消耗Token数',
    duration_ms     INT             DEFAULT NULL               COMMENT '耗时（毫秒）',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_user_id (user_id),
    KEY idx_commit_id (commit_id),
    KEY idx_mr_id (mr_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI代码评审记录表';
```

### 13.5 ai_meeting_summary AI会议纪要表

```sql
CREATE TABLE ai_meeting_summary (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '纪要ID',
    meeting_id      BIGINT          NOT NULL                  COMMENT '会议ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    model_config_id BIGINT          DEFAULT NULL               COMMENT '使用的模型配置ID',
    transcript      TEXT            DEFAULT NULL               COMMENT '会议转录文本',
    summary         TEXT            NOT NULL                  COMMENT 'AI生成的会议纪要',
    action_items    JSON            DEFAULT NULL               COMMENT '行动项列表',
    decisions       JSON            DEFAULT NULL               COMMENT '决策列表',
    total_tokens    INT             NOT NULL DEFAULT 0         COMMENT '消耗Token数',
    duration_ms     INT             DEFAULT NULL               COMMENT '耗时（毫秒）',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_meeting_id (meeting_id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会议纪要表';
```

### 13.6 ai_perf_insight AI绩效洞察记录表

```sql
CREATE TABLE ai_perf_insight (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '洞察ID',
    insight_type    VARCHAR(32)     NOT NULL                  COMMENT '洞察类型：coding_activity/estimation_accuracy/personal_development/team_report/project_risk',
    target_type     VARCHAR(16)     NOT NULL                  COMMENT '目标类型：user / project / team',
    target_id       BIGINT          NOT NULL                  COMMENT '目标ID',
    period_start    DATE            NOT NULL                  COMMENT '统计周期开始',
    period_end     DATE            NOT NULL                  COMMENT '统计周期结束',
    period_type     VARCHAR(16)     NOT NULL                  COMMENT '周期类型：daily / weekly / monthly',
    data            JSON            DEFAULT NULL               COMMENT '洞察原始数据',
    insights        JSON            DEFAULT NULL               COMMENT 'AI生成的洞察文字',
    suggestions     JSON            DEFAULT NULL               COMMENT '改进建议',
    health_score    INT             DEFAULT NULL               COMMENT '健康度评分（0-100）',
    generated_by    VARCHAR(64)     DEFAULT NULL               COMMENT '生成模型',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_type_target_period (insight_type, target_type, target_id, period_start),
    KEY idx_target (target_type, target_id),
    KEY idx_period (period_start, period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI绩效洞察记录表';
```

### 13.7 ai_estimate_accuracy AI工时预估准确度记录表

```sql
CREATE TABLE ai_estimate_accuracy (
    id                  BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '记录ID',
    task_id             BIGINT          NOT NULL                  COMMENT '任务ID',
    user_id             BIGINT          NOT NULL                  COMMENT '用户ID',
    project_id          BIGINT          NOT NULL                  COMMENT '项目ID',
    task_type           VARCHAR(16)     NOT NULL                  COMMENT '任务类型：story/task/bug/improvement',
    estimated_hours     DECIMAL(5,1)   NOT NULL                  COMMENT '预估工时（小时）',
    actual_hours        DECIMAL(5,1)   DEFAULT NULL               COMMENT '实际工时（小时，来自WakaTime）',
    deviation_percent   DECIMAL(6,2)   DEFAULT NULL               COMMENT '偏差百分比（正数=低估，负数=高估）',
    deviation_hours     DECIMAL(5,1)   DEFAULT NULL               COMMENT '偏差小时数',
    completed_at        DATETIME        DEFAULT NULL               COMMENT '任务完成时间',
    accuracy_data       JSON            DEFAULT NULL               COMMENT '准确度分析详情（偏差模式、归因分析等）',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_user_project (user_id, project_id),
    KEY idx_task_id (task_id),
    KEY idx_completed_at (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI工时预估准确度记录表';
```

### 13.8 ai_project_risk AI项目风险预警记录表

```sql
CREATE TABLE ai_project_risk (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '预警ID',
    risk_id         VARCHAR(32)     NOT NULL                  COMMENT '风险唯一标识（如 R001）',
    project_id      BIGINT          NOT NULL                  COMMENT '项目ID',
    risk_type       VARCHAR(32)     NOT NULL                  COMMENT '风险类型：schedule_risk/member_fatigue/workload_imbalance/declining_activity',
    severity        VARCHAR(16)     NOT NULL                  COMMENT '严重程度：low / medium / high / critical',
    title           VARCHAR(256)    NOT NULL                  COMMENT '风险标题',
    description     TEXT            DEFAULT NULL               COMMENT '风险描述',
    evidence        JSON            DEFAULT NULL               COMMENT '证据数据（用于生成报告的多维度数据）',
    suggestions     JSON            DEFAULT NULL               COMMENT '建议措施',
    status          VARCHAR(16)     NOT NULL DEFAULT 'active' COMMENT '状态：active / acknowledged / resolved / ignored',
    affected_users  JSON            DEFAULT NULL               COMMENT '受影响的用户ID列表',
    notified_to     JSON            DEFAULT NULL               COMMENT '已通知对象列表',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    resolved_at     DATETIME        DEFAULT NULL               COMMENT '解决时间',
    updated_at      DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_project_status (project_id, status),
    KEY idx_severity (severity),
    KEY idx_risk_type (risk_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI项目风险预警记录表';
```

---

## 14. WakaTime 集成服务 (wakapi_db)

### 14.1 wakapi_heartbeat 心跳记录表

```sql
CREATE TABLE wakapi_heartbeat (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '心跳ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    entity          VARCHAR(512)    NOT NULL                  COMMENT '文件路径/URL',
    entity_type     VARCHAR(16)     NOT NULL DEFAULT 'file'  COMMENT '实体类型：file/url/domain',
    category        VARCHAR(32)     NOT NULL DEFAULT 'coding' COMMENT '分类：coding/browsing',
    project_id      BIGINT          DEFAULT NULL               COMMENT '关联项目ID（映射 project_name）',
    repository_id   BIGINT          DEFAULT NULL               COMMENT '关联仓库ID',
    project_name    VARCHAR(128)    DEFAULT NULL               COMMENT '项目名（原始值）',
    branch          VARCHAR(256)    DEFAULT NULL               COMMENT 'Git 分支',
    language        VARCHAR(64)     DEFAULT NULL               COMMENT '编程语言',
    is_write        TINYINT         NOT NULL DEFAULT 0         COMMENT '是否写操作',
    editor          VARCHAR(64)     DEFAULT NULL               COMMENT '编辑器名',
    os              VARCHAR(64)     DEFAULT NULL               COMMENT '操作系统',
    machine_name    VARCHAR(128)    DEFAULT NULL               COMMENT '机器名',
    machine_id      VARCHAR(64)     DEFAULT NULL               COMMENT '机器唯一ID',
    heartbeat_time  DATETIME        NOT NULL                  COMMENT '心跳时间',
    lines_total     INT             DEFAULT NULL               COMMENT '文件总行数',
    lineno          INT             DEFAULT NULL               COMMENT '当前行号',
    cursorpos       INT             DEFAULT NULL               COMMENT '光标位置',
    user_agent      VARCHAR(256)    DEFAULT NULL               COMMENT 'User-Agent',
    heartbeat_hash  VARCHAR(64)     DEFAULT NULL               COMMENT '防重哈希（MD5(entity+time)）',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_heartbeat_hash (heartbeat_hash),
    KEY idx_user_time (user_id, heartbeat_time),
    KEY idx_project_name (project_name),
    KEY idx_language (language),
    KEY idx_machine_id (machine_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心跳记录表';

ALTER TABLE wakapi_heartbeat ADD CONSTRAINT fk_heartbeat_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 14.2 wakapi_daily_summary 每日编码汇总表

```sql
CREATE TABLE wakapi_daily_summary (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '汇总ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    summary_date    DATE            NOT NULL                  COMMENT '汇总日期',
    total_seconds   INT             NOT NULL DEFAULT 0         COMMENT '总编码秒数',
    total_hours     DECIMAL(6,2)   NOT NULL DEFAULT 0         COMMENT '总编码小时数',
    active_minutes  INT             NOT NULL DEFAULT 0         COMMENT '活跃分钟数（>0的分钟）',
    projects_json   JSON            DEFAULT NULL               COMMENT '各项目时间分布',
    languages_json  JSON            DEFAULT NULL               COMMENT '各语言时间分布',
    editors_json    JSON            DEFAULT NULL               COMMENT '各编辑器时间分布',
    machines_json   JSON            DEFAULT NULL               COMMENT '各机器时间分布',
    categories_json JSON            DEFAULT NULL               COMMENT '各分类时间分布',
    daily_average   INT             NOT NULL DEFAULT 0         COMMENT '日均编码秒数',
    calculated_at   DATETIME        DEFAULT NULL               COMMENT '计算时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_user_date (user_id, summary_date),
    KEY idx_user_id (user_id),
    KEY idx_summary_date (summary_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日编码汇总表';
```

### 14.3 wakapi_task_link 心跳-任务关联表

```sql
CREATE TABLE wakapi_task_link (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '关联ID',
    heartbeat_id    BIGINT          NOT NULL                  COMMENT '心跳记录ID',
    task_id         BIGINT          NOT NULL                  COMMENT '关联任务ID',
    link_type       VARCHAR(16)     NOT NULL DEFAULT 'auto'  COMMENT '关联类型：auto/manual',
    match_rule       VARCHAR(32)     DEFAULT NULL               COMMENT '匹配规则：file_path/project/commit',
    seconds          INT             NOT NULL DEFAULT 0         COMMENT '该心跳记录的编码秒数',
    linked_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_heartbeat_task (heartbeat_id, task_id),
    KEY idx_task_id (task_id),
    KEY idx_link_type (link_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心跳-任务关联表';

ALTER TABLE wakapi_task_link ADD CONSTRAINT fk_task_link_heartbeat FOREIGN KEY (heartbeat_id) REFERENCES wakapi_heartbeat(id);
ALTER TABLE wakapi_task_link ADD CONSTRAINT fk_task_link_task FOREIGN KEY (task_id) REFERENCES task(id);
```

### 14.4 wakapi_machine 机器注册表

```sql
CREATE TABLE wakapi_machine (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '机器ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    machine_name    VARCHAR(128)    NOT NULL                  COMMENT '机器名',
    machine_id       VARCHAR(64)     NOT NULL                  COMMENT '机器唯一ID（WakaTime生成）',
    os              VARCHAR(64)     DEFAULT NULL               COMMENT '操作系统',
    last_heartbeat_at DATETIME      DEFAULT NULL               COMMENT '最后心跳时间',
    is_active       TINYINT         NOT NULL DEFAULT 1         COMMENT '是否激活',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_user_machine (user_id, machine_id),
    KEY idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机器注册表';

ALTER TABLE wakapi_machine ADD CONSTRAINT fk_machine_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

---

## 15. 团队绩效服务 (performance_db)

### 15.1 perf_work_log 工作日志表

```sql
CREATE TABLE perf_work_log (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '日志ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    task_id         BIGINT          DEFAULT NULL               COMMENT '关联任务ID',
    date            DATE            NOT NULL                  COMMENT '工作日期',
    hours           DECIMAL(4,1)   NOT NULL                  COMMENT '工时（小时）',
    description     VARCHAR(512)    DEFAULT NULL               COMMENT '工作描述',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_user_task_date (user_id, task_id, date),
    KEY idx_user_id (user_id),
    KEY idx_task_id (task_id),
    KEY idx_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作日志表';
```

### 15.2 perf_metric_config 绩效指标配置表

```sql
CREATE TABLE perf_metric_config (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '配置ID',
    metric_code     VARCHAR(32)     NOT NULL                  COMMENT '指标编码',
    metric_name     VARCHAR(64)     NOT NULL                  COMMENT '指标名称',
    metric_type     VARCHAR(16)     NOT NULL                  COMMENT '指标类型：worklog/code/mr/review/meeting',
    unit            VARCHAR(16)     DEFAULT NULL               COMMENT '单位：hour/count/score/percentage',
    weight          DECIMAL(3,2)   NOT NULL DEFAULT 1.00     COMMENT '权重',
    formula         VARCHAR(256)    DEFAULT NULL               COMMENT '计算公式',
    target_value    DECIMAL(10,2)  DEFAULT NULL               COMMENT '目标值',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_metric_type (metric_type),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效指标配置表';
```

### 15.3 perf_metric 绩效指标定义表

```sql
CREATE TABLE perf_metric (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '指标ID',
    metric_code      VARCHAR(64)     NOT NULL                  COMMENT '指标编码',
    metric_name     VARCHAR(128)    NOT NULL                  COMMENT '指标名称',
    metric_type     VARCHAR(16)     NOT NULL                  COMMENT '指标类型：worklog/code/mr/review/custom',
    description     VARCHAR(512)    DEFAULT NULL               COMMENT '指标描述',
    data_source     VARCHAR(32)     DEFAULT NULL               COMMENT '数据来源表',
    formula         VARCHAR(256)    DEFAULT NULL               COMMENT '计算公式',
    unit           VARCHAR(16)     DEFAULT NULL               COMMENT '单位：hour/count/score/percentage',
    weight         DECIMAL(5,2)    NOT NULL DEFAULT 1.00     COMMENT '权重（用于综合评分）',
    enabled        TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_metric_code (metric_code),
    KEY idx_metric_type (metric_type),
    KEY idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效指标定义表';
```

### 15.4 perf_goal 绩效目标表

```sql
CREATE TABLE perf_goal (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '目标ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    project_id      BIGINT          DEFAULT NULL               COMMENT '项目ID',
    metric_id       BIGINT          NOT NULL                  COMMENT '指标ID',
    cycle_type     VARCHAR(16)     NOT NULL                  COMMENT '周期类型：weekly/monthly/quarterly',
    cycle_value     VARCHAR(16)     NOT NULL                  COMMENT '周期值（如 2026-05）',
    target_value   DECIMAL(15,2)   NOT NULL                  COMMENT '目标值',
    actual_value   DECIMAL(15,2)   DEFAULT NULL               COMMENT '实际值',
    status         VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending/in_progress/achieved/failed',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_user_metric_cycle (user_id, metric_id, cycle_type, cycle_value),
    KEY idx_user_id (user_id),
    KEY idx_project_id (project_id),
    KEY idx_metric_id (metric_id),
    KEY idx_cycle (cycle_type, cycle_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效目标表';

ALTER TABLE perf_goal ADD CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
ALTER TABLE perf_goal ADD CONSTRAINT fk_goal_metric FOREIGN KEY (metric_id) REFERENCES perf_metric(id);
```

### 15.5 perf_report 绩效报告表

```sql
CREATE TABLE perf_report (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '报告ID',
    user_id         BIGINT          NOT NULL                  COMMENT '被考核用户ID',
    reviewer_id     BIGINT          NOT NULL                  COMMENT '考核人ID',
    project_id      BIGINT          DEFAULT NULL               COMMENT '项目ID',
    cycle_type     VARCHAR(16)     NOT NULL                  COMMENT '考核周期类型',
    cycle_value     VARCHAR(16)     NOT NULL                  COMMENT '考核周期值',
    total_score    DECIMAL(5,2)   DEFAULT NULL               COMMENT '综合得分',
    summary         TEXT            DEFAULT NULL               COMMENT '评语',
    attachment_urls JSON            DEFAULT NULL               COMMENT '附件URL列表',
    status         VARCHAR(16)     NOT NULL DEFAULT 'draft'  COMMENT '状态：draft/submitted/acknowledged',
    submitted_at    DATETIME        DEFAULT NULL               COMMENT '提交时间',
    acknowledged_at  DATETIME        DEFAULT NULL               COMMENT '确认时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_reviewer_id (reviewer_id),
    KEY idx_project_id (project_id),
    KEY idx_cycle (cycle_type, cycle_value),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效报告表';

ALTER TABLE perf_report ADD CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
ALTER TABLE perf_report ADD CONSTRAINT fk_report_reviewer FOREIGN KEY (reviewer_id) REFERENCES sys_user(id);
```

### 15.6 perf_metric_data 绩效指标数据快照表

```sql
CREATE TABLE perf_metric_data (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '数据ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    project_id      BIGINT          DEFAULT NULL               COMMENT '项目ID',
    metric_id       BIGINT          NOT NULL                  COMMENT '指标ID',
    metric_code     VARCHAR(64)     NOT NULL                  COMMENT '指标编码（冗余存储）',
    value          DECIMAL(15,2)   NOT NULL                  COMMENT '指标值',
    record_date     DATE            NOT NULL                  COMMENT '记录日期',
    data_source     VARCHAR(32)     DEFAULT NULL               COMMENT '数据来源',
    source_id       BIGINT          DEFAULT NULL               COMMENT '来源记录ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_metric_date (user_id, metric_id, record_date),
    KEY idx_user_id (user_id),
    KEY idx_project_id (project_id),
    KEY idx_metric_id (metric_id),
    KEY idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='绩效指标数据快照表';
```

---

## 16. 文件服务 (file_db)

### 16.1 file_record 文件记录表

```sql
CREATE TABLE file_record (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '文件ID',
    file_name       VARCHAR(256)    NOT NULL                  COMMENT '文件原始名称',
    file_path       VARCHAR(512)    NOT NULL                  COMMENT '文件存储路径（相对路径）',
    file_url        VARCHAR(512)    DEFAULT NULL               COMMENT '文件访问URL',
    storage_type   VARCHAR(16)     NOT NULL DEFAULT 'local'  COMMENT '存储类型：local/minio/aliyun_oss',
    file_size       BIGINT          NOT NULL                  COMMENT '文件大小（字节）',
    mime_type       VARCHAR(128)   DEFAULT NULL               COMMENT 'MIME类型',
    file_hash       VARCHAR(64)     DEFAULT NULL               COMMENT '文件Hash（SHA256）',
    bucket_name     VARCHAR(64)     DEFAULT NULL               COMMENT '存储桶名称',
    uploader_id    BIGINT          NOT NULL                  COMMENT '上传人ID',
    project_id      BIGINT          DEFAULT NULL               COMMENT '关联项目ID',
    biz_type       VARCHAR(32)     DEFAULT NULL               COMMENT '业务类型：task_attachment/avatar/commit',
    biz_id         BIGINT          DEFAULT NULL               COMMENT '业务ID',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_uploader_id (uploader_id),
    KEY idx_project_id (project_id),
    KEY idx_biz (biz_type, biz_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件记录表';

ALTER TABLE file_record ADD CONSTRAINT fk_file_uploader FOREIGN KEY (uploader_id) REFERENCES sys_user(id);
```

---

## 17. 开放平台服务 (open_db)

### 17.1 open_api_token API令牌表

```sql
CREATE TABLE open_api_token (
    id              VARCHAR(32)      PRIMARY KEY                  COMMENT '令牌ID',
    user_id         BIGINT          NOT NULL                  COMMENT '用户ID',
    name            VARCHAR(128)    NOT NULL                  COMMENT '令牌名称',
    token           VARCHAR(64)     NOT NULL                  COMMENT '令牌值（hash存储）',
    scopes          JSON            DEFAULT NULL               COMMENT '权限范围',
    expires_at      DATETIME        DEFAULT NULL               COMMENT '过期时间',
    last_used_at    DATETIME        DEFAULT NULL               COMMENT '最后使用时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_token (token(64)),
    KEY idx_user_id (user_id),
    KEY idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API令牌表';

ALTER TABLE open_api_token ADD CONSTRAINT fk_api_token_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 17.2 open_oauth_application OAuth应用表

```sql
CREATE TABLE open_oauth_application (
    id              VARCHAR(32)      PRIMARY KEY                  COMMENT '应用ID',
    user_id         BIGINT          NOT NULL                  COMMENT '创建者用户ID',
    name            VARCHAR(128)    NOT NULL                  COMMENT '应用名称',
    description     VARCHAR(512)    DEFAULT NULL               COMMENT '应用描述',
    client_id       VARCHAR(64)     NOT NULL                  COMMENT 'Client ID',
    client_secret   VARCHAR(256)    NOT NULL                  COMMENT 'Client Secret（加密存储）',
    redirect_uris   JSON            DEFAULT NULL               COMMENT '允许的回调地址列表',
    scopes          JSON            DEFAULT NULL               COMMENT '默认权限范围',
    homepage        VARCHAR(256)    DEFAULT NULL               COMMENT '应用主页',
    logo_url        VARCHAR(256)    DEFAULT NULL               COMMENT 'Logo URL',
    status          VARCHAR(16)     NOT NULL DEFAULT 'active'  COMMENT '状态：active/disabled',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    UNIQUE KEY uk_client_id (client_id),
    KEY idx_user_id (user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth应用表';

ALTER TABLE open_oauth_application ADD CONSTRAINT fk_oauth_app_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 17.3 open_oauth_token OAuth访问令牌表

```sql
CREATE TABLE open_oauth_token (
    id              VARCHAR(32)      PRIMARY KEY                  COMMENT 'Token ID',
    application_id  VARCHAR(32)     NOT NULL                  COMMENT '应用ID',
    user_id         BIGINT          NOT NULL                  COMMENT '授权用户ID',
    access_token    VARCHAR(256)    NOT NULL                  COMMENT 'Access Token（hash存储）',
    refresh_token   VARCHAR(256)    DEFAULT NULL               COMMENT 'Refresh Token（hash存储）',
    scopes          JSON            DEFAULT NULL               COMMENT '权限范围',
    expires_at      DATETIME        NOT NULL                  COMMENT 'Access Token过期时间',
    refresh_expires_at DATETIME     DEFAULT NULL               COMMENT 'Refresh Token过期时间',
    revoked         TINYINT         NOT NULL DEFAULT 0         COMMENT '是否已撤销',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_access_token (access_token(64)),
    UNIQUE KEY uk_refresh_token (refresh_token(64)),
    KEY idx_application_id (application_id),
    KEY idx_user_id (user_id),
    KEY idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth访问令牌表';

ALTER TABLE open_oauth_token ADD CONSTRAINT fk_oauth_token_app FOREIGN KEY (application_id) REFERENCES open_oauth_application(id);
ALTER TABLE open_oauth_token ADD CONSTRAINT fk_oauth_token_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
```

### 17.4 open_webhook Webhook表

```sql
CREATE TABLE open_webhook (
    id              VARCHAR(32)      PRIMARY KEY                  COMMENT 'Webhook ID',
    user_id         BIGINT          NOT NULL                  COMMENT '创建者ID',
    application_id  VARCHAR(32)     DEFAULT NULL               COMMENT 'OAuth应用ID（可选）',
    name            VARCHAR(128)    NOT NULL                  COMMENT 'Webhook名称',
    url             VARCHAR(512)    NOT NULL                  COMMENT '回调URL',
    secret          VARCHAR(128)    NOT NULL                  COMMENT '签名密钥',
    events          JSON            NOT NULL                  COMMENT '订阅的事件类型',
    headers         JSON            DEFAULT NULL               COMMENT '自定义请求头',
    ssl_verify      TINYINT         NOT NULL DEFAULT 1         COMMENT '是否验证SSL',
    active          TINYINT         NOT NULL DEFAULT 1         COMMENT '是否启用',
    retry_policy    JSON            DEFAULT NULL               COMMENT '重试策略',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME        ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_application_id (application_id),
    KEY idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Webhook表';
```

### 17.5 open_webhook_delivery Webhook投递记录表

```sql
CREATE TABLE open_webhook_delivery (
    id              VARCHAR(32)      PRIMARY KEY                  COMMENT '投递记录ID',
    webhook_id      VARCHAR(32)     NOT NULL                  COMMENT 'Webhook ID',
    event_id        VARCHAR(64)     NOT NULL                  COMMENT '事件ID',
    event_type      VARCHAR(64)     NOT NULL                  COMMENT '事件类型',
    payload         JSON            NOT NULL                  COMMENT '投递内容',
    status          VARCHAR(16)     NOT NULL                  COMMENT '状态：pending/success/failed',
    status_code     INT             DEFAULT NULL               COMMENT 'HTTP响应码',
    response_body   TEXT            DEFAULT NULL               COMMENT '响应内容',
    response_time   INT             DEFAULT NULL               COMMENT '响应时间（毫秒）',
    attempt         INT             NOT NULL DEFAULT 1         COMMENT '尝试次数',
    error_message   VARCHAR(512)    DEFAULT NULL               COMMENT '错误信息',
    next_retry_at   DATETIME        DEFAULT NULL               COMMENT '下次重试时间',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    completed_at     DATETIME        DEFAULT NULL               COMMENT '完成时间',
    KEY idx_webhook_id (webhook_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Webhook投递记录表';
```

### 17.6 open_api_log API调用日志表

```sql
CREATE TABLE open_api_log (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '日志ID',
    application_id  VARCHAR(32)     DEFAULT NULL               COMMENT 'OAuth应用ID',
    token_id        VARCHAR(32)     DEFAULT NULL               COMMENT 'API令牌ID',
    user_id         BIGINT          DEFAULT NULL               COMMENT '关联用户ID',
    method          VARCHAR(8)      NOT NULL                  COMMENT 'HTTP方法',
    path           VARCHAR(256)    NOT NULL                  COMMENT '请求路径',
    query_params    VARCHAR(512)    DEFAULT NULL               COMMENT '查询参数',
    request_body    TEXT            DEFAULT NULL               COMMENT '请求体',
    response_status INT             DEFAULT NULL               COMMENT '响应状态码',
    response_time   INT             DEFAULT NULL               COMMENT '响应时间（毫秒）',
    ip_address     VARCHAR(45)     DEFAULT NULL               COMMENT '请求IP',
    user_agent     VARCHAR(256)    DEFAULT NULL               COMMENT 'User-Agent',
    error_code     VARCHAR(32)     DEFAULT NULL               COMMENT '错误码',
    error_message  VARCHAR(512)    DEFAULT NULL               COMMENT '错误信息',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_application_id (application_id),
    KEY idx_token_id (token_id),
    KEY idx_user_id (user_id),
    KEY idx_path (path),
    KEY idx_response_status (response_status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API调用日志表';
```


## 18. 索引规范总结

### 19.1 必建索引

| 表类型 | 必须索引 |
|--------|---------|
| 主从表 | 外键字段（FK） |
| 业务表 | `status`, `deleted` |
| 关联表 | 联合唯一索引 |
| 审计表 | `created_at` |

### 18.2 命名规范

```sql
-- 普通索引
KEY idx_{table}_{column} (column)

-- 唯一索引
UNIQUE KEY uk_{table}_{column} (column)

-- 外键索引（隐含）
KEY idx_{table}_{fk_column} ({fk_column})
```

### 18.3 联合索引规范

```sql
-- 联合索引字段顺序：等值查询字段 > 范围查询字段 > 排序字段
KEY idx_task_project_status (project_id, status)
KEY idx_notification_user_read (user_id, is_read)
KEY idx_project_member_user (project_id, user_id)
```
