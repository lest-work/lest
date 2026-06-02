# 工时管理（lest-worklog）PRD — V1.0

> **插件 ID**: `lest-worklog`
>
> **版本**: V1.0
>
> **Jira 映射**: Jira Worklog / Tempo Timesheets
>
> **许可**: 🆓 开源基础版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标

V1.0 提供基础工时登记能力，用于任务维度的工时记录和人员/项目维度的统计汇总，为绩效分析和报表提供数据基础。

> **说明**：Core V3.0 将补齐 Jira 体系的 Time Tracking + Worklog 能力（作为 Jira 100% 收口项）。本插件在其基础上提供"统计与体验增强"，不改变核心数据模型。

---

## 2. 数据库设计

```sql
-- 工时记录
CREATE TABLE pl_worklog (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    issue_id        BIGINT NOT NULL COMMENT '关联任务',
    user_id         BIGINT NOT NULL COMMENT '登记人',
    time_spent      INT NOT NULL COMMENT '工时（分钟）',
    work_date       DATE NOT NULL COMMENT '工作日期',
    description     VARCHAR(512) COMMENT '工时描述',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issue(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
);

-- 工时配置
CREATE TABLE pl_worklog_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id      BIGINT COMMENT 'NULL 表示全局配置',
    is_required     BOOLEAN DEFAULT FALSE COMMENT '是否强制要求登记工时',
    min_duration    INT DEFAULT 1 COMMENT '最小登记单位（分钟）',
    require_description BOOLEAN DEFAULT FALSE COMMENT '是否强制填写描述',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES project(id)
);
```

---

## 3. 用户故事

### 3.1 工时登记

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-WL-001 | 作为员工，我希望在任务详情页快速登记工时（今日/本周已登记工时一目了然） | 显示当前任务已登记总工时 |
| US-WL-002 | 作为员工，我希望登记工时时选择日期、填写时长和描述 | 工时登记表单字段完整 |
| US-WL-003 | 作为员工，我希望编辑和删除已登记的工时记录 | 编辑后总工时自动更新 |
| US-WL-004 | 作为项目经理，我希望查看某个任务的工时汇总（总工时 + 明细列表） | 明细列表按日期倒序显示 |

### 3.2 工时统计

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-WL-010 | 作为员工，我希望查看自己的工时日历（每日/每周登记时长） | 日历视图高亮有工时登记的日期 |
| US-WL-011 | 作为项目经理，我希望按项目查看团队成员工时汇总 | 汇总表显示每人总工时和任务数 |
| US-WL-012 | 作为项目经理，我希望导出工时数据为 CSV | 导出包含人员/日期/任务/时长/描述 |

---

## 4. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/plugin/lest-worklog` | GET/POST | 工时列表/登记 |
| `/api/plugin/lest-worklog/{id}` | GET/PUT/DELETE | 工时详情/编辑/删除 |
| `/api/plugin/lest-worklog/issue/{issueId}` | GET | 任务工时列表 |
| `/api/plugin/lest-worklog/user/{userId}` | GET | 用户工时列表（支持 dateRange 筛选） |
| `/api/plugin/lest-worklog/project/{projectId}/summary` | GET | 项目工时汇总 |
| `/api/plugin/lest-worklog/export` | GET | 导出工时 CSV |
| `/api/plugin/lest-worklog/config` | GET/PUT | 工时配置 |

---

## 5. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 工时 | 10100-10199 | 插件 工时 模块 |

## 6. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-08 | 初始版本 | - |
