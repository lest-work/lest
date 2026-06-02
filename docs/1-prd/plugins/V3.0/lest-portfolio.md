# lest-portfolio — Portfolio 多项目治理插件

> **插件 ID**: `lest-portfolio`
>
> **版本**: V3.0
>
> **Jira 映射**: Jira Portfolio / Advanced Roadmaps（商业功能）
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST Portfolio 为项目经理和高层管理者提供跨项目的全局视角，将分散在各个 LEST 项目中的史诗（Epic）、迭代、任务数据统一聚合，提供 Portfolio 级路线图、进度追踪、风险预警和资源负载分析，帮助管理层做出数据驱动的决策。

### 1.2 与竞品对标

| 功能 | Jira Portfolio | LEST Portfolio | 说明 |
|------|--------------|--------------|------|
| 跨项目汇总 | ✅ | ✅ | 所有项目 Epic/Story 汇总 |
| Roadmap 路线图 | ✅ | ✅ | 甘特图形式展示 |
| 资源负载分析 | ✅ | ✅ | 团队成员工作负载 |
| 进度追踪 | ✅ | ✅ | 自动聚合子项目进度 |
| 风险识别 | ✅ | ✅ | AI 风险识别 |
| 依赖管理 | ✅ | ✅ | 跨项目依赖 |
| Scenario 规划 | ✅ | ❌ | 多方案对比（规划中） |

---

## 2. 功能范围

### 2.1 核心功能

| 功能 | 说明 | 优先级 |
|------|------|--------|
| Portfolio 创建与管理 | 创建 Portfolio，选择参与的项目 | P0 |
| 跨项目 Epic 汇总 | 聚合所有项目的 Epic，显示进度和状态 | P0 |
| Portfolio 路线图 | 甘特图形式展示所有 Epic 的时间线 | P0 |
| 跨项目依赖视图 | 显示 Portfolio 内的 Epic 间依赖关系 | P0 |
| Portfolio 级风险识别 | 自动检测延期风险/资源冲突 | P0 |
| 资源负载分析 | 按团队成员展示工作负载热力图 | P1 |
| Portfolio 报表 | 吞吐量/完成率/周期时间趋势 | P1 |
| Scenario 规划 | 多版本路线图对比（乐观/保守） | P2 |
| Portfolio 甘特图导出 | PDF/PNG 导出 | P2 |

---

## 3. 数据库设计

### 3.1 表结构

```sql
-- Portfolio 视图
CREATE TABLE pl_portfolio (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_key   VARCHAR(64) NOT NULL UNIQUE,
    name            VARCHAR(128) NOT NULL,
    description     VARCHAR(512),
    owner_id        BIGINT NOT NULL,
    status          VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'DRAFT/ACTIVE/ARCHIVED',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Portfolio 参与项目
CREATE TABLE pl_portfolio_project (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id    BIGINT NOT NULL,
    project_id      BIGINT NOT NULL,
    sort            INT DEFAULT 0,
    FOREIGN KEY (portfolio_id) REFERENCES pl_portfolio(id),
    FOREIGN KEY (project_id) REFERENCES project(id),
    UNIQUE (portfolio_id, project_id)
);

-- Portfolio Epic 视图（聚合缓存）
CREATE TABLE pl_portfolio_epic (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id    BIGINT NOT NULL,
    epic_id         BIGINT NOT NULL COMMENT '关联的 Epic issue_id',
    epic_key        VARCHAR(32) NOT NULL,
    epic_title      VARCHAR(256) NOT NULL,
    project_id      BIGINT NOT NULL,
    project_name    VARCHAR(128),
    start_date      DATE,
    end_date        DATE,
    progress        DECIMAL(5,2) DEFAULT 0 COMMENT '完成百分比',
    story_points_total INT DEFAULT 0,
    story_points_done INT DEFAULT 0,
    issue_count_total  INT DEFAULT 0,
    issue_count_done  INT DEFAULT 0,
    risk_level      VARCHAR(16) DEFAULT 'LOW' COMMENT 'LOW/MEDIUM/HIGH/CRITICAL',
    risk_factors    JSON COMMENT '风险因素列表',
    last_updated    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES pl_portfolio(id)
);

-- Portfolio 级依赖（跨 Epic）
CREATE TABLE pl_portfolio_dependency (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id    BIGINT NOT NULL,
    source_epic_id BIGINT NOT NULL COMMENT '依赖方 Epic',
    target_epic_id BIGINT NOT NULL COMMENT '被依赖方 Epic',
    dependency_type VARCHAR(16) NOT NULL COMMENT 'BLOCKS/FINISHES_START/STARTS_FINISH',
    description     VARCHAR(256),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES pl_portfolio(id)
);

-- Portfolio 资源负载
CREATE TABLE pl_portfolio_capacity (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id    BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    week_date       DATE NOT NULL COMMENT '周起始日期',
    total_hours     DECIMAL(5,2) DEFAULT 0 COMMENT '总可用工时',
    allocated_hours DECIMAL(5,2) DEFAULT 0 COMMENT '已分配工时',
    FOREIGN KEY (portfolio_id) REFERENCES pl_portfolio(id),
    UNIQUE (portfolio_id, user_id, week_date)
);

-- Portfolio 快照（历史版本）
CREATE TABLE pl_portfolio_snapshot (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id    BIGINT NOT NULL,
    snapshot_date   DATE NOT NULL,
    snapshot_data   JSON NOT NULL COMMENT 'Portfolio 完整状态快照',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (portfolio_id) REFERENCES pl_portfolio(id)
);
```

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-PORT-001 | 作为项目经理，我希望创建 Portfolio 并选择参与的项目 | Portfolio 创建后自动聚合所有项目数据 |
| US-PORT-002 | 作为项目经理，我希望在 Portfolio 详情页查看所有 Epic 的汇总进度 | 进度自动从子项目中聚合计算 |
| US-PORT-003 | 作为项目经理，我希望查看 Portfolio 级路线图（甘特图），展示所有 Epic 的时间线 | 甘特图支持拖拽调整日期 |
| US-PORT-004 | 作为项目经理，我希望查看 Portfolio 内的跨项目 Epic 依赖关系 | 依赖关系以连线形式在路线图上展示 |
| US-PORT-005 | 作为项目经理，我希望系统自动识别 Portfolio 级风险（Epic 延期/资源冲突） | 高风险 Epic 在路线图上高亮显示 |
| US-PORT-006 | 作为项目经理，我希望查看团队成员的资源负载热力图 | 热力图以周为单位，颜色深浅表示负载程度 |
| US-PORT-007 | 作为项目经理，我希望创建 Portfolio 快照，记录某一时间点的 Portfolio 状态 | 快照可与当前状态对比 |
| US-PORT-008 | 作为项目经理，我希望导出 Portfolio 路线图为 PDF | PDF 包含甘特图 + Epic 列表 |
| US-PORT-009 | 作为系统，我希望在 Epic 完成/延期时自动更新 Portfolio 缓存 | 缓存更新延迟 < 5 分钟 |
| US-PORT-010 | 作为项目经理，我希望在路线图上添加 Portfolio 里程碑 | 里程碑以菱形标记显示 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/portfolio` | GET/POST | Portfolio 列表/创建 |
| `/portfolio/{id}` | GET/PUT/DELETE | Portfolio 详情/更新/删除 |
| `/portfolio/{id}/projects` | GET/POST/DELETE | Portfolio 参与项目管理 |
| `/portfolio/{id}/epics` | GET | Portfolio Epic 汇总（自动聚合） |
| `/portfolio/{id}/epics/{epicId}` | PUT | 更新 Epic 视图数据 |
| `/portfolio/{id}/roadmap` | GET | Portfolio 路线图数据（甘特图格式） |
| `/portfolio/{id}/dependencies` | GET/POST/DELETE | Portfolio 级依赖 |
| `/portfolio/{id}/capacity` | GET/PUT | 资源负载数据 |
| `/portfolio/{id}/risks` | GET | Portfolio 级风险列表 |
| `/portfolio/{id}/snapshots` | GET/POST | Portfolio 快照列表/创建 |
| `/portfolio/{id}/export/pdf` | GET | 导出为 PDF |

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
| Portfolio | 11400-11499 | 插件 Portfolio 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2027年中 | 初始版本 | - |
