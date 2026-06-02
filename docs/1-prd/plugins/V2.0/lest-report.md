# 报表中心（lest-report）PRD — V2.0

> **插件 ID**: `lest-report`
>
> **版本**: V2.0
>
> **Jira 映射**: Jira Reports / Portfolio for Jira
>
> **许可**: 🆓 开源基础版 / 🔒 商业增强版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST 报表中心为团队提供丰富的数据可视化能力，超越基础看板统计，提供可导出的报表、报表订阅（邮件/IM）、自定义报表配置，以及跨项目/跨团队的绩效分析。

### 1.2 与 Jira 对标

| 功能 | Jira Reports | LEST Report | 说明 |
|------|------------|------------|------|
| Sprint Burndown | ✅ | ✅ | 与 Core V3.0 对齐 |
| Cumulative Flow Diagram | ✅ | ✅ | 与 Core V3.0 对齐 |
| Velocity Chart | ✅ | ✅ | 与 Core V3.0 对齐 |
| Control Chart | ✅ | ✅ | 与 Core V3.0 对齐 |
| Epic Report | ✅ | ✅ | 史诗进度报告 |
| 自定义报表 | ✅ | ✅ | 自定义 SQL/可视化 |
| 报表订阅 | ✅ | ✅ | 定时发送邮件/IM |
| PDF/Excel 导出 | ✅ | ✅ | 报表导出 |
| 跨项目报表 | ✅ | ✅ | 多项目汇总统计 |
| 团队容量报表 | ✅ | ✅ | 迭代容量分析 |

---

## 2. 数据库设计

```sql
-- 报表定义
CREATE TABLE pl_report (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_key     VARCHAR(64) NOT NULL UNIQUE,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(512),
    report_type     VARCHAR(32) NOT NULL COMMENT 'BURNDOWN/CFD/VELOCITY/CONTROL/EPIC/CUSTOM/CAPACITY',
    config          JSON NOT NULL COMMENT '报表配置参数',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 报表订阅
CREATE TABLE pl_report_subscription (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_id       BIGINT NOT NULL,
    subscribe_type  VARCHAR(16) NOT NULL COMMENT 'DAILY/WEEKLY/MONTHLY',
    recipients      JSON NOT NULL COMMENT 'user_id 列表或邮件地址',
    channel         VARCHAR(16) NOT NULL DEFAULT 'EMAIL' COMMENT 'EMAIL/IM/PUSH',
    last_sent_at    DATETIME,
    next_send_at    DATETIME,
    FOREIGN KEY (report_id) REFERENCES pl_report(id)
);

-- 报表导出记录
CREATE TABLE pl_report_export (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_id       BIGINT NOT NULL,
    export_type     VARCHAR(16) NOT NULL COMMENT 'PDF/EXCEL/CSV',
    file_url        VARCHAR(512),
    exported_by     BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (report_id) REFERENCES pl_report(id)
);
```

---

## 3. 报表类型详细设计

### 3.1 Sprint Burndown Chart（燃尽图）

**配置参数**：
```json
{
  "projectId": 1,
  "boardId": 1,
  "sprintId": 5,
  "scope": "STORY_POINTS",
  "idealLine": true
}
```

### 3.2 Cumulative Flow Diagram（CFD 累积流图）

**配置参数**：
```json
{
  "projectId": 1,
  "boardId": 1,
  "sprintId": 5,
  "groupBy": "STATUS",
  "dateRange": "LAST_5_SPRINTS"
}
```

### 3.3 Velocity Chart（速度图）

**配置参数**：
```json
{
  "projectId": 1,
  "boardId": 1,
  "sprintRange": "LAST_6_SPRINTS",
  "metric": "STORY_POINTS",
  "showAverage": true
}
```

### 3.4 Epic Progress Report（史诗进度报告）

展示所有 Epic 的完成进度，包括 Story 点数、完成率、剩余时间。

### 3.5 Team Capacity Report（团队容量报告）

| 维度 | 说明 |
|------|------|
| 迭代容量 | 计划点数 vs 实际完成点数 |
| 成员负载 | 每人分配的任务数/点数 |
| 空闲时间 | 未分配任务数 |
| 趋势分析 | 连续 5 个迭代的容量变化 |

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-RPT-001 | 作为员工，我希望在报表中心查看所有可用报表列表 | 列表按类型分类，支持搜索 |
| US-RPT-002 | 作为项目经理，我希望查看 Sprint 燃尽图，了解迭代进度 | 图表显示理想线和实际线 |
| US-RPT-003 | 作为项目经理，我希望查看 CFD 累积流图，了解工作流吞吐量 | 图中每个泳道代表一个状态 |
| US-RPT-004 | 作为项目经理，我希望查看 Velocity 图表，了解团队速度趋势 | 图表显示历史迭代速度和平均值 |
| US-RPT-005 | 作为项目经理，我希望导出报表为 PDF/Excel | 导出文件包含图表 + 数据表格 |
| US-RPT-006 | 作为项目经理，我希望配置报表订阅，每天/每周自动收到报表邮件 | 订阅配置后按时发送 |
| US-RPT-007 | 作为团队负责人，我希望查看团队容量报告，了解成员工作负载 | 报告展示每人分配/完成的任务数 |
| US-RPT-008 | 作为项目经理，我希望创建自定义报表，选择指标和维度 | 自定义报表保存后可重复使用 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/report` | GET/POST | 报表列表/创建 |
| `/report/{id}` | GET/PUT/DELETE | 报表详情/更新/删除 |
| `/report/{id}/data` | GET | 报表数据（图表数据） |
| `/report/{id}/export/{type}` | GET | 导出报表（PDF/EXCEL/CSV） |
| `/report/{id}/subscribe` | POST/DELETE | 配置/取消订阅 |
| `/report/templates` | GET | 报表模板列表 |
| `/report/dashboard` | GET | 报表仪表盘（常用报表快捷入口） |

## 6. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 报表 | 11100-11199 | 插件 报表 模块 |

## 7. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
