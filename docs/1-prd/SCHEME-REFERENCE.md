# Scheme 参考手册 — Jira 方案完整对照

> 本文档是 LEST Platform V2.0 定制化体系的核心设计参考，涵盖所有 Scheme 的完整定义。每个 Scheme 对应 Jira 的一个配置概念。

---

## 1. Scheme 体系总览

LEST Platform 的 Scheme 体系完整对标 Jira Data Center，共分为 6 大类：

```
┌─────────────────────────────────────────────────────────────┐
│                        Project                                │
│                   (项目级配置)                                │
│                                                             │
│  ├── Issue Type Scheme ───→ 允许创建哪些 Issue Type         │
│  ├── Workflow Scheme ─────→ 每种类型用哪个工作流              │
│  ├── Screen Scheme ──────→ 每种类型用哪个屏幕方案            │
│  ├── Field Config Scheme ─→ 每种类型的字段行为               │
│  ├── Priority Scheme ─────→ 优先级集合                      │
│  └── Board Scheme ────────→ 看板配置                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Issue Type Scheme（任务类型方案）

### 2.1 概念定义

Issue Type Scheme 定义了一个项目允许创建哪些类型的 Issue。每个项目引用一个 Issue Type Scheme。

**Jira 对应**：`Issue Type Scheme`

### 2.2 表结构

```sql
-- issue_type_scheme
CREATE TABLE issue_type_scheme (
  scheme_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  name            VARCHAR(128) NOT NULL,
  description     TEXT,
  is_default      TINYINT DEFAULT 0,    -- 1=默认方案
  is_active       TINYINT DEFAULT 1,
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- issue_type_scheme_issue_type（方案包含的类型）
CREATE TABLE issue_type_scheme_issue_type (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  scheme_id        BIGINT NOT NULL,
  issue_type_id    BIGINT NOT NULL,
  sub_task_type_id BIGINT,              -- 该类型的子任务类型
  sort             INT DEFAULT 0,
  FOREIGN KEY (scheme_id) REFERENCES issue_type(scheme_id)
);

-- issue_type（类型定义）
CREATE TABLE issue_type (
  issue_type_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name            VARCHAR(64) NOT NULL,  -- Bug / 用户故事 / 故障单
  type_key        VARCHAR(32) NOT NULL,  -- bug / story / incident
  description     TEXT,
  icon            VARCHAR(64),
  color           VARCHAR(16),
  is_system       TINYINT DEFAULT 0,     -- 1=内置不可删除
  sort            INT DEFAULT 0,
  created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 2.3 官方内置 Issue Type

| type_key | 名称 | 说明 | 用途 |
|----------|------|------|------|
| `epic` | 史诗 | 大型需求容器 | 管理大型需求 |
| `story` | 用户故事 | 独立可交付的功能 | 敏捷开发 |
| `task` | 任务 | 需要完成的工作 | 通用工作项 |
| `bug` | Bug | 缺陷报告 | 缺陷追踪 |
| `tech_debt` | 技术债务 | 代码重构或优化项 | 技术改进 |
| `subtask` | 子任务 | 父任务的子工作项 | 分解复杂任务 |

---

## 3. Workflow Scheme（工作流方案）

### 3.1 概念定义

Workflow Scheme 将 Issue Type 映射到具体的 Workflow Definition。每个项目可以有不同的 Workflow Scheme，同一个 Workflow 可以被多个类型共用。

**Jira 对应**：`Workflow Scheme`

### 3.2 表结构

```sql
-- workflow_definition（工作流定义）
CREATE TABLE workflow_definition (
  workflow_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name          VARCHAR(128) NOT NULL,
  description   TEXT,
  is_active     TINYINT DEFAULT 1,
  is_default    TINYINT DEFAULT 0,
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- workflow_status（工作流中的状态）
CREATE TABLE workflow_status (
  workflow_status_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  workflow_id        BIGINT NOT NULL,
  status_key        VARCHAR(32) NOT NULL,  -- todo / in_review / done
  status_name       VARCHAR(64) NOT NULL,  -- 待办 / 审核中 / 已完成
  status_category   VARCHAR(16),            -- todo / in_progress / done
  color             VARCHAR(16),
  icon              VARCHAR(32),
  is_initial        TINYINT DEFAULT 0,     -- 1=初始状态
  sort              INT DEFAULT 0,
  FOREIGN KEY (workflow_id) REFERENCES workflow_definition(workflow_id)
);

-- workflow_transition（流转路径）
CREATE TABLE workflow_transition (
  transition_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  workflow_id     BIGINT NOT NULL,
  from_status_id  BIGINT NOT NULL,
  to_status_id    BIGINT NOT NULL,
  name            VARCHAR(64),             -- 开始 / 提交审核 / 关闭
  description      VARCHAR(255),
  sort             INT DEFAULT 0,
  FOREIGN KEY (workflow_id) REFERENCES workflow_definition(workflow_id)
);

-- workflow_condition（流转条件：是否允许触发）
CREATE TABLE workflow_condition (
  condition_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
  transition_id BIGINT NOT NULL,
  field_key    VARCHAR(64),
  operator     VARCHAR(16),                -- eq / in / is_current_user / is_not_empty
  value        VARCHAR(512),
  logic        VARCHAR(8) DEFAULT 'AND',  -- AND / OR
  FOREIGN KEY (transition_id) REFERENCES workflow_transition(transition_id)
);

-- workflow_validator（流转校验：数据是否合法）
CREATE TABLE workflow_validator (
  validator_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  transition_id   BIGINT NOT NULL,
  field_key       VARCHAR(64),
  operator        VARCHAR(16),
  value           VARCHAR(512),
  error_message   VARCHAR(255),
  FOREIGN KEY (transition_id) REFERENCES workflow_transition(transition_id)
);

-- workflow_post_function（流转后置动作）
CREATE TABLE workflow_post_function (
  function_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  transition_id    BIGINT NOT NULL,
  function_type    VARCHAR(32),            -- SET_FIELD / SEND_NOTIFICATION / CREATE_SUBTASK
  function_config  JSON,
  execute_order    INT DEFAULT 0,
  FOREIGN KEY (transition_id) REFERENCES workflow_transition(transition_id)
);

-- workflow_scheme（工作流方案）
CREATE TABLE workflow_scheme (
  scheme_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name       VARCHAR(128) NOT NULL,
  description TEXT,
  is_default TINYINT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- workflow_scheme_issue_type（方案→类型→工作流映射）
CREATE TABLE workflow_scheme_issue_type (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  scheme_id    BIGINT NOT NULL,
  issue_type_id BIGINT NOT NULL,
  workflow_id   BIGINT,                      -- 覆盖 Issue Type 的默认工作流
  FOREIGN KEY (scheme_id) REFERENCES workflow_scheme(scheme_id),
  FOREIGN KEY (workflow_id) REFERENCES workflow_definition(workflow_id)
);
```

### 3.3 操作符枚举

| operator | 含义 | 示例 |
|----------|------|------|
| `eq` | 等于 | `assignee_id = 5` |
| `neq` | 不等于 | `status != 'done'` |
| `gt` / `gte` / `lt` / `lte` | 数值比较 | `story_points > 5` |
| `in` | 在列表中 | `priority in ['p0', 'p1']` |
| `not_in` | 不在列表中 | `type not_in ['bug']` |
| `contains` | 包含 | `labels contains 'reviewed'` |
| `is_empty` | 为空 | `due_date is empty` |
| `is_not_empty` | 不为空 | `assignee_id is not empty` |
| `is_current_user` | 是当前用户 | `assignee_id is current_user` |
| `changed` | 值已变更 | `status changed` |

### 3.4 后置动作类型

| function_type | 说明 | 配置示例 |
|--------------|------|---------|
| `SET_FIELD` | 设置字段值 | `{"field": "status", "value": "in_progress"}` |
| `CLEAR_FIELD` | 清空字段 | `{"field": "due_date"}` |
| `COPY_FIELD` | 复制字段值 | `{"from": "title", "to": "summary"}` |
| `SEND_NOTIFICATION` | 发送通知 | `{"template": "assigned", "to": "assignee"}` |
| `CREATE_SUBTASK` | 创建子任务 | `{"taskType": "task", "titleTemplate": "Review: ${title}"}` |
| `ADD_LABEL` | 添加标签 | `{"labels": ["reviewed"]}` |
| `FIRE_WEBHOOK` | 触发 Webhook | `{"url": "https://...", "event": "status.changed"}` |
| `RUN_AUTOMATION` | 触发自动化规则 | `{"ruleId": 5}` |

---

## 4. Screen / Screen Scheme（屏幕 + 屏幕方案）

### 4.1 概念定义

| 层级 | Jira 名称 | 说明 |
|------|----------|------|
| Screen | Screen | 定义哪些字段在哪个 Tab 上、布局顺序 |
| Screen Scheme | Screen Scheme | 定义 Create / Edit / View / Transition 各操作对应哪个 Screen |
| Issue Type Screen Scheme | Issue Type Screen Scheme | 定义每种 Issue Type 用哪个 Screen Scheme |

### 4.2 表结构

```sql
-- screen（屏幕配置）
CREATE TABLE screen (
  screen_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(128) NOT NULL,    -- Bug 创建页 / 全局详情页
  description TEXT,
  screen_type VARCHAR(16),               -- create / edit / view / transition
  layout      JSON,                      -- Tab + 字段分组布局
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- screen_field（Screen 中的字段）
CREATE TABLE screen_field (
  screen_field_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  screen_id       BIGINT NOT NULL,
  field_key      VARCHAR(64) NOT NULL,
  field_type     VARCHAR(32),           -- system / custom
  is_required    TINYINT DEFAULT 0,
  is_readonly    TINYINT DEFAULT 0,
  is_hidden      TINYINT DEFAULT 0,
  show_in_quick_edit TINYINT DEFAULT 0,
  sort           INT DEFAULT 0,
  FOREIGN KEY (screen_id) REFERENCES screen(screen_id)
);

-- screen_scheme（屏幕方案）
CREATE TABLE screen_scheme (
  scheme_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(128) NOT NULL,
  description TEXT,
  is_default  TINYINT DEFAULT 0,
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- screen_scheme_detail（操作→Screen 映射）
CREATE TABLE screen_scheme_detail (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  scheme_id    BIGINT NOT NULL,
  operation    VARCHAR(16),              -- create / edit / view / subtask_create / transition
  screen_id    BIGINT NOT NULL,
  FOREIGN KEY (scheme_id) REFERENCES screen_scheme(scheme_id),
  FOREIGN KEY (screen_id) REFERENCES screen(screen_id)
);

-- issue_type_screen_scheme（类型→屏幕方案映射）
CREATE TABLE issue_type_screen_scheme (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  issue_type_id    BIGINT NOT NULL,
  screen_scheme_id BIGINT NOT NULL,
  FOREIGN KEY (issue_type_id) REFERENCES issue_type(issue_type_id),
  FOREIGN KEY (screen_scheme_id) REFERENCES screen_scheme(scheme_id)
);
```

### 4.3 Screen Layout JSON 结构

```json
{
  "tabs": [
    {
      "tabName": "基本信息",
      "fields": [
        { "fieldKey": "title", "label": "标题", "row": 1, "col": 1 },
        { "fieldKey": "description", "label": "描述", "row": 2, "col": 1 },
        { "fieldKey": "priority", "label": "优先级", "row": 3, "col": 1 },
        { "fieldKey": "severity", "label": "严重程度", "row": 3, "col": 2 }
      ]
    },
    {
      "tabName": "人员",
      "fields": [
        { "fieldKey": "assignee_id", "label": "负责人" },
        { "fieldKey": "reporter_id", "label": "报告人" }
      ]
    }
  ]
}
```

---

## 5. Field Configuration Scheme（字段配置方案）

### 5.1 概念定义

Field Configuration Scheme 定义每个字段在每个 Issue Type 中的行为（隐藏/必填/只读/标签）。

**Jira 对应**：`Field Configuration Scheme`

### 5.2 表结构

```sql
-- custom_field_definition（自定义字段定义）
CREATE TABLE custom_field_definition (
  field_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  field_key        VARCHAR(64) NOT NULL,   -- severity / team / story_points
  field_name       VARCHAR(128) NOT NULL,   -- 严重程度 / 团队 / 故事点
  field_type       VARCHAR(32) NOT NULL,   -- 25 种字段类型
  description      TEXT,
  placeholder      VARCHAR(128),
  default_value    TEXT,
  validation_rule  JSON,                   -- 校验规则
  searcher_key     VARCHAR(64),           -- 搜索器类型
  is_global        TINYINT DEFAULT 1,     -- 1=全局字段
  project_id       BIGINT,                 -- NULL=全局，否则属于某项目
  is_active        TINYINT DEFAULT 1,
  is_system_locked TINYINT DEFAULT 0,     -- 1=系统字段不可删除
  created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- custom_field_option（可选项，select/radio/multiselect 等）
CREATE TABLE custom_field_option (
  option_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  field_id         BIGINT NOT NULL,
  parent_option_id BIGINT,                 -- 级联选择
  option_value     VARCHAR(128) NOT NULL,
  option_label     VARCHAR(128) NOT NULL,
  color            VARCHAR(16),
  sort             INT DEFAULT 0,
  is_disabled      TINYINT DEFAULT 0,
  FOREIGN KEY (field_id) REFERENCES custom_field_definition(field_id)
);

-- custom_field_context（字段上下文，字段的作用范围）
CREATE TABLE custom_field_context (
  context_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  field_id     BIGINT NOT NULL,
  context_type VARCHAR(16),               -- global / project / template
  project_id   BIGINT,
  is_default   TINYINT DEFAULT 0,
  FOREIGN KEY (field_id) REFERENCES custom_field_definition(field_id)
);

-- custom_field_value（EAV 存储）
CREATE TABLE custom_field_value (
  value_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  issue_id      BIGINT NOT NULL,
  field_id      BIGINT NOT NULL,
  text_value    TEXT,
  number_value  DECIMAL(20,5),
  date_value    DATETIME,
  date_value2  DATETIME,                 -- 日期范围结束
  option_value  VARCHAR(128),            -- 单选项 ID
  created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_issue_field (issue_id, field_id)
);

-- field_config_scheme（字段配置方案）
CREATE TABLE field_config_scheme (
  scheme_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(128) NOT NULL,
  description TEXT,
  is_default  TINYINT DEFAULT 0,
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- field_config（每个字段在方案中的行为）
CREATE TABLE field_config (
  config_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  scheme_id      BIGINT NOT NULL,
  field_id       BIGINT NOT NULL,
  is_hidden      TINYINT DEFAULT 0,
  is_required    TINYINT DEFAULT 0,
  is_readonly    TINYINT DEFAULT 0,
  default_value  TEXT,
  label          VARCHAR(128),           -- 可覆写字段标签
  description    TEXT,
  show_in_list  TINYINT DEFAULT 1,
  show_in_board  TINYINT DEFAULT 1,
  show_in_gantt  TINYINT DEFAULT 0,
  FOREIGN KEY (scheme_id) REFERENCES field_config_scheme(scheme_id),
  FOREIGN KEY (field_id) REFERENCES custom_field_definition(field_id)
);

-- field_config_scheme_issue_type（方案→类型映射）
CREATE TABLE field_config_scheme_issue_type (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  scheme_id     BIGINT NOT NULL,
  issue_type_id BIGINT NOT NULL,
  FOREIGN KEY (scheme_id) REFERENCES field_config_scheme(scheme_id),
  FOREIGN KEY (issue_type_id) REFERENCES issue_type(issue_type_id)
);
```

### 5.3 字段类型枚举（25 种）

| field_type | 说明 | 存储列 | 搜索器 |
|-----------|------|--------|--------|
| `textfield` | 单行文本 | `text_value` | textsearcher |
| `textarea` | 多行文本 | `text_value` | textsearcher |
| `richtext` | 富文本 | `text_value` | - |
| `number` | 整数 | `number_value` | exactnumber |
| `decimal` | 小数 | `number_value` | exactnumber |
| `currency` | 货币 | `number_value` | - |
| `datepicker` | 日期 | `date_value` | datepicker |
| `datetimepicker` | 日期时间 | `date_value` | datepicker |
| `datetimerange` | 日期范围 | `date_value` + `date_value2` | - |
| `select` | 单选下拉 | `option_value` | multiselectsearcher |
| `multiselect` | 多选 | `text_value`（逗号分隔） | multiselectsearcher |
| `radiobuttons` | 单选按钮组 | `option_value` | - |
| `checkboxes` | 多选按钮组 | `text_value` | - |
| `userpicker` | 用户选择 | `text_value`（用户 ID） | userpicker |
| `usergrouppicker` | 用户组选择 | `text_value` | - |
| `projectpicker` | 项目选择 | `text_value` | projectpicker |
| `versionpicker` | 版本选择 | `text_value` | versionsearcher |
| `issuelink` | 任务链接 | `text_value` | - |
| `labels` | 标签 | `text_value` | labelssearcher |
| `url` | URL | `text_value` | urlsearcher |
| `email` | 邮箱 | `text_value` | textsearcher |
| `phone` | 电话 | `text_value` | - |
| `cascadingselect` | 级联选择 | `text_value`（JSON） | - |
| `matrix` | 矩阵字段 | `text_value`（JSON） | - |
| `formula` | 计算字段 | （只读） | - |

---

## 6. Board Scheme（看板配置）

### 6.1 概念定义

Board Scheme 定义项目的看板视图，包含列配置、泳道、WIP 限制。

**Jira 对应**：`Kanban Board` / `Scrum Board`

### 6.2 表结构

```sql
-- board（看板配置）
CREATE TABLE board (
  board_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id       BIGINT NOT NULL,
  board_name       VARCHAR(128) NOT NULL,
  board_type       VARCHAR(16) NOT NULL,  -- kanban / scrum
  filter_jql       TEXT,                  -- JQL 过滤条件（Scrum 看板）
  group_by_field   VARCHAR(64),           -- 分组字段（默认 status）
  default_swimlane VARCHAR(64),
  show_subtasks    TINYINT DEFAULT 1,
  created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- board_column（看板列配置）
CREATE TABLE board_column (
  column_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  board_id    BIGINT NOT NULL,
  column_name VARCHAR(64) NOT NULL,
  status_key  VARCHAR(32),                -- 对应的状态 key（逗号分隔，多对一）
  wip_limit   INT,                       -- WIP 限制（Kanban）
  color       VARCHAR(16),
  sort        INT DEFAULT 0,
  is_collapsed TINYINT DEFAULT 0,
  FOREIGN KEY (board_id) REFERENCES board(board_id)
);

-- board_swimlane（泳道配置）
CREATE TABLE board_swimlane (
  swimlane_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
  board_id     BIGINT NOT NULL,
  field_key    VARCHAR(64),
  is_default   TINYINT DEFAULT 0,
  sort         INT DEFAULT 0,
  FOREIGN KEY (board_id) REFERENCES board(board_id)
);
```

---

## 7. Priority Scheme（优先级方案）

### 7.1 概念定义

Priority Scheme 定义项目使用的优先级集合和默认优先级。

**Jira 对应**：`Priority Scheme`

### 7.2 表结构

```sql
-- priority（优先级）
CREATE TABLE priority (
  priority_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(64) NOT NULL,     -- 紧急 / 高 / 中 / 低
  priority_key VARCHAR(16) NOT NULL,     -- p0 / p1 / p2 / p3
  color        VARCHAR(16),
  icon         VARCHAR(32),
  sort         INT DEFAULT 0,
  is_active    TINYINT DEFAULT 1
);

-- priority_scheme（优先级方案）
CREATE TABLE priority_scheme (
  scheme_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(128) NOT NULL,
  description TEXT,
  is_default  TINYINT DEFAULT 0,
  created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- priority_scheme_priority（方案包含的优先级）
CREATE TABLE priority_scheme_priority (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  scheme_id   BIGINT NOT NULL,
  priority_id BIGINT NOT NULL,
  is_default  TINYINT DEFAULT 0,         -- 1=默认优先级
  FOREIGN KEY (scheme_id) REFERENCES priority_scheme(scheme_id),
  FOREIGN KEY (priority_id) REFERENCES priority(priority_id)
);

-- project.project_id → priority_scheme.scheme_id（项目引用优先级方案）
```

---

## 8. Project Template（项目模板）

### 8.1 概念定义

Project Template 从模板创建新项目时，完整复制一套 Scheme 配置。包含 Issue Type Scheme、Workflow Scheme、Screen Scheme、Field Config Scheme、自定义字段、看板配置。

**Jira 对应**：`Jira Project Templates`

### 8.2 表结构

```sql
-- project_template（模板定义）
CREATE TABLE project_template (
  template_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(128) NOT NULL,   -- 敏捷开发 / Bug 追踪 / 空白项目
  description  TEXT,
  category     VARCHAR(32),              -- agile / kanban / bug_tracker / blank
  icon         VARCHAR(64),
  is_active    TINYINT DEFAULT 1,
  usage_count  INT DEFAULT 0,
  is_official  TINYINT DEFAULT 0,       -- 1=官方模板
  created_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- project_template_content（模板内容，完整 Scheme 配置 JSON）
CREATE TABLE project_template_content (
  content_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  template_id   BIGINT NOT NULL,
  content_type  VARCHAR(32),             -- issue_type_scheme / workflow_scheme / screen_scheme
                                          -- / field_config_scheme / custom_field / board
  content_data  JSON NOT NULL,          -- 完整配置内容
  FOREIGN KEY (template_id) REFERENCES project_template(template_id)
);
```

### 8.3 官方模板

| 模板 | 分类 | 包含配置 |
|------|------|---------|
| 敏捷开发 | agile | Epic + Story + Task + Bug，Scrum 工作流，5 列看板 |
| Bug 追踪 | bug_tracker | Bug + Task，Bug 状态工作流（新建/已分配/修复/验证/关闭）|
| 看板管理 | kanban | Story + Task + Bug，Kanban 工作流，WIP 限制看板 |
| 空白项目 | blank | 仅默认 Issue Type，无预设工作流 |

---

## 9. 配置优先级

```
项目自定义 > 项目模板 > 全局默认
```

| 来源 | 优先级 | 说明 |
|------|-------|------|
| 项目手动配置 | 最高 | 在项目设置中手动修改的配置 |
| 项目模板 | 中 | 从模板创建时继承的配置 |
| 全局默认 | 最低 | 系统预设的默认值 |

---

## 10. Scheme 引用链路（完整）

```
project
  │
  ├── issue_type_scheme_id ───→ issue_type_scheme
  │                               └── 包含 issue_type_ids[]
  │                                     │
  │                                     └── 每个 issue_type 有：
  │                                           - default_workflow_id
  │                                           - default_screen_id
  │                                           - default_field_config_id
  │
  ├── workflow_scheme_id ───→ workflow_scheme
  │                            └── 包含 (issue_type_id → workflow_id) 映射
  │
  ├── screen_scheme_id ───→ issue_type_screen_scheme
  │                            └── 包含 (issue_type_id → screen_scheme_id) 映射
  │                                  └── screen_scheme → screen_scheme_detail
  │                                        └── operation → screen_id
  │
  ├── field_config_scheme_id ───→ field_config_scheme
  │                                  └── 包含 (issue_type_id → field_config_id) 映射
  │                                        └── field_config → custom_field_definition
  │
  ├── priority_scheme_id ───→ priority_scheme
  │                             └── 包含 priority_ids[]
  │
  └── board_id ───→ board
                      └── board_columns[]
```

---

## 11. 版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| V1.0 | 2026-06-01 | 初始版本：Issue Type / Workflow / Screen / Field Configuration / Board / Priority / Project Template 完整 Scheme 设计 |
