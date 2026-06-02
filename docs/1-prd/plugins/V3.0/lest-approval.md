# lest-approval — 审批流引擎插件

> **插件 ID**: `lest-approval`
>
> **版本**: V3.0
>
> **Jira 映射**: Jira Workflow + Approvals / Jira Service Management Approvals（商业功能）
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST 审批流插件为团队提供灵活的自定义审批流程引擎。与 LEST 任务管理和工作流引擎深度集成——审批节点可以嵌入任务工作流（如"发布审批 → 待发布 → 已发布"），审批结果自动触发后续动作。

### 1.2 与竞品对标

| 功能 | Jira Approvals | LEST Approval | 说明 |
|------|---------------|--------------|------|
| 审批流定义 | ✅ | ✅ | 可视化流程设计器 |
| 审批节点类型 | ✅ | ✅ | 单人/会签/或签/条件 |
| 条件审批 | ✅ | ✅ | 基于字段值动态路由 |
| 代理/加签 | ✅ | ✅ | 审批转交 |
| 与工作流集成 | ✅ | ✅ | 审批节点嵌入工作流 |
| IM 通知 | ✅ | ✅ | 钉钉/企微/飞书 |
| 审批历史 | ✅ | ✅ | 完整审批记录 |

---

## 2. 功能范围

### 2.1 核心功能

| 功能 | 说明 | 优先级 |
|------|------|--------|
| 审批流定义 | 可视化流程设计器，创建审批节点和连线 | P0 |
| 审批节点类型 | 单人审批、会签（需全部通过）、或签（任一人通过）、条件分支 | P0 |
| 审批执行 | 提交审批 → 审批 → 通过/驳回/转交/加签 | P0 |
| 审批历史 | 完整审批记录（时间/审批人/意见/结果） | P0 |
| 与工作流集成 | 审批节点作为工作流的步骤，审批通过后自动流转 | P0 |
| 消息通知 | 站内/邮件/IM（钉钉/企微/飞书）通知 | P0 |
| 代理审批 | 审批人委托他人代为审批 | P1 |
| 加签审批 | 审批过程中追加审批人 | P1 |
| 驳回重提 | 驳回后重新修改提交 | P1 |
| 审批催办 | 超时未审批自动催办 | P2 |

---

## 3. 数据库设计

### 3.1 表结构

```sql
-- 审批工作流定义
CREATE TABLE pl_approval_workflow (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_key    VARCHAR(64) NOT NULL UNIQUE COMMENT '工作流标识',
    name            VARCHAR(128) NOT NULL COMMENT '工作流名称',
    description     VARCHAR(512),
    version         INT DEFAULT 1 COMMENT '版本号，支持发布历史',
    is_published    BOOLEAN DEFAULT FALSE COMMENT '是否已发布',
    is_current      BOOLEAN DEFAULT TRUE COMMENT '是否为当前版本',
    trigger_type    VARCHAR(32) NOT NULL COMMENT 'MANUAL/ISSUE_STATUS/WEBHOOK',
    trigger_config  JSON COMMENT '触发条件配置',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 审批节点
CREATE TABLE pl_approval_node (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_id     BIGINT NOT NULL,
    node_key        VARCHAR(64) NOT NULL COMMENT '节点标识',
    node_name       VARCHAR(128) NOT NULL COMMENT '节点名称',
    node_type       VARCHAR(32) NOT NULL COMMENT 'START/APPROVER/CONDITION/ACTION/END',
    position_x      INT DEFAULT 0 COMMENT '设计器中的 X 坐标',
    position_y      INT DEFAULT 0 COMMENT '设计器中的 Y 坐标',
    approver_type   VARCHAR(32) COMMENT 'FIXED/DYNAMIC/SELF/SUPERVISOR',
    approver_config JSON COMMENT '审批人配置：user_id 列表或 SPEL 表达式',
    multi_type      VARCHAR(16) DEFAULT 'ANY' COMMENT '会签/或签：ALL/ANY',
    timeout_hours   INT DEFAULT 0 COMMENT '超时小时数，0 表示不超时',
    timeout_action  VARCHAR(16) DEFAULT 'SKIP' COMMENT '超时动作：SKIP/AUTO_PASS/AUTO_REJECT',
    sort            INT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES pl_approval_workflow(id)
);

-- 审批节点连线
CREATE TABLE pl_approval_edge (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_id     BIGINT NOT NULL,
    source_node_id  BIGINT NOT NULL,
    target_node_id  BIGINT NOT NULL,
    edge_type       VARCHAR(16) DEFAULT 'PASS' COMMENT 'PASS/REJECT/_TIMEOUT',
    condition_expr  VARCHAR(512) COMMENT '条件表达式，满足时走此路径',
    sort            INT DEFAULT 0,
    FOREIGN KEY (workflow_id) REFERENCES pl_approval_workflow(id),
    FOREIGN KEY (source_node_id) REFERENCES pl_approval_node(id),
    FOREIGN KEY (target_node_id) REFERENCES pl_approval_node(id)
);

-- 审批条件节点配置
CREATE TABLE pl_approval_condition (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    edge_id         BIGINT NOT NULL,
    field_key       VARCHAR(64) NOT NULL COMMENT '字段标识',
    operator        VARCHAR(16) NOT NULL COMMENT 'EQ/NEQ/GT/LT/GTE/LTE/CONTAINS/IN',
    value           VARCHAR(256) NOT NULL COMMENT '比较值',
    logic_group     INT DEFAULT 1 COMMENT '逻辑分组，组内 OR，组间 AND',
    FOREIGN KEY (edge_id) REFERENCES pl_approval_edge(id)
);

-- 审批记录（实例）
CREATE TABLE pl_approval_instance (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_id     BIGINT NOT NULL,
    instance_key    VARCHAR(64) NOT NULL UNIQUE COMMENT '实例标识',
    biz_type        VARCHAR(32) NOT NULL COMMENT '业务类型：ISSUE/RELEASE/DOCUMENT',
    biz_id          BIGINT NOT NULL COMMENT '业务 ID（如 issue_id）',
    status          VARCHAR(16) NOT NULL COMMENT 'PENDING/APPROVED/REJECTED/CANCELLED',
    submitter_id    BIGINT NOT NULL COMMENT '提交人',
    current_node_id BIGINT COMMENT '当前节点',
    started_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    finished_at     DATETIME,
    FOREIGN KEY (workflow_id) REFERENCES pl_approval_workflow(id),
    FOREIGN KEY (current_node_id) REFERENCES pl_approval_node(id)
);

-- 审批任务（节点实例）
CREATE TABLE pl_approval_task (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    instance_id     BIGINT NOT NULL,
    node_id         BIGINT NOT NULL,
    task_key        VARCHAR(64) NOT NULL UNIQUE COMMENT '任务标识',
    assignee_id      BIGINT NOT NULL COMMENT '当前审批人',
    status          VARCHAR(16) NOT NULL COMMENT 'PENDING/APPROVED/REJECTED/DELEGATED/ADDED',
    decision        VARCHAR(16) COMMENT 'PASS/REJECT/DELEGATE/ADD',
    comment         TEXT COMMENT '审批意见',
    delegated_to    BIGINT COMMENT '转交给',
    started_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    finished_at     DATETIME,
    FOREIGN KEY (instance_id) REFERENCES pl_approval_instance(id),
    FOREIGN KEY (node_id) REFERENCES pl_approval_node(id),
    FOREIGN KEY (assignee_id) REFERENCES sys_user(user_id)
);

-- 审批历史
CREATE TABLE pl_approval_history (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id         BIGINT NOT NULL,
    action          VARCHAR(32) NOT NULL COMMENT 'SUBMIT/APPROVE/REJECT/DELEGATE/ADD/REMIND/CANCEL',
    operator_id     BIGINT NOT NULL,
    comment         TEXT,
    metadata        JSON COMMENT '扩展数据，如转交原因',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES pl_approval_task(id)
);
```

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-APPR-001 | 作为系统管理员，我希望在审批流设计器中创建审批节点并设置审批人类型 | 节点创建后可在设计器中拖动定位 |
| US-APPR-002 | 作为系统管理员，我希望设置会签/或签节点，所有审批人通过或任一人通过 | 不同模式下通过逻辑正确 |
| US-APPR-003 | 作为系统管理员，我希望为条件分支设置字段条件表达式 | 条件满足时自动路由到对应分支 |
| US-APPR-004 | 作为系统管理员，我希望将审批流与 LEST 工作流集成，审批通过后自动触发状态流转 | 审批通过后任务状态自动变更 |
| US-APPR-005 | 作为员工，我希望在任务详情页提交审批申请 | 提交后审批人收到通知 |
| US-APPR-006 | 作为审批人，我希望在审批中心查看待审批任务列表 | 列表支持按业务类型/提交时间/紧急程度筛选 |
| US-APPR-007 | 作为审批人，我希望审批通过/驳回，并填写审批意见 | 审批后实例自动流转到下一节点 |
| US-APPR-008 | 作为审批人，我希望转交审批给他人，并填写转交原因 | 转交后原审批人收到通知 |
| US-APPR-009 | 作为审批人，我希望追加审批人到当前节点（加签） | 加签后原节点所有审批人审批后才流转 |
| US-APPR-010 | 作为员工，我希望在审批详情页查看完整审批历史 | 历史记录包含每一步的时间、审批人、意见 |
| US-APPR-011 | 作为系统，我希望在审批超时后自动催办（站内 + IM） | 催办消息包含审批链接 |
| US-APPR-012 | 作为系统管理员，我希望查看审批流的使用统计（使用次数/平均审批时长/通过率） | 统计以图表形式展示 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/approval/workflows` | GET/POST | 审批流列表/创建 |
| `/approval/workflows/{id}` | GET/PUT/DELETE | 审批流详情/更新/删除 |
| `/approval/workflows/{id}/publish` | POST | 发布审批流 |
| `/approval/workflows/{id}/nodes` | GET/POST | 节点列表/创建 |
| `/approval/workflows/{id}/edges` | GET/POST | 连线列表/创建 |
| `/approval/instances` | GET | 审批实例列表（提交人视角） |
| `/approval/instances/{id}` | GET | 审批实例详情 |
| `/approval/instances` | POST | 提交审批申请 |
| `/approval/instances/{id}/cancel` | POST | 取消审批 |
| `/approval/tasks` | GET | 待审批任务列表（审批人视角） |
| `/approval/tasks/{id}` | GET | 审批任务详情 |
| `/approval/tasks/{id}/approve` | POST | 审批通过 |
| `/approval/tasks/{id}/reject` | POST | 审批驳回 |
| `/approval/tasks/{id}/delegate` | POST | 转交审批 |
| `/approval/tasks/{id}/add` | POST | 加签审批 |
| `/approval/history/{instanceId}` | GET | 审批历史 |
| `/approval/stats` | GET | 审批流使用统计 |

---


## 6. 前端设计

### 6.1 页面结构

> **说明**：前端设计细节在开发阶段细化。此处预留章节结构。

### 6.2 关键组件

| 组件 | 说明 |
|------|------|
| — | — |

### 6.3 状态管理

| 状态 | 说明 |
|------|------|
| — | — |

---


## 7. 与其他模块的集成

### 7.1 集成点

| 集成模块 | 集成方式 | 说明 |
|---------|---------|------|
| 任务管理 | LEST Core API | 与任务生命周期联动 |

### 7.2 事件订阅

| 事件 | 处理逻辑 |
|------|---------|
| — | — |

---

## 8. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 审批 | 11500-11599 | 插件 审批 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2027年中 | 初始版本 | - |
