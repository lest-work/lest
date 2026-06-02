# lest-okr — OKR 目标管理插件

> **插件 ID**: `lest-okr`
>
> **版本**: V3.0
>
> **Jira 映射**: Jira Align / Perodicity Goals（商业功能）
>
> **许可**: 🔒 商业版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST OKR 插件为企业提供科学的目标管理能力，帮助团队将战略目标逐级分解为可执行的关键结果，实现"上下对齐、左右协同"。OKR 与 LEST 任务管理深度集成——每个关键结果（KR）直接关联到具体的 Epic/Story/Task，形成从战略到执行的全链路闭环。

### 1.2 与竞品对标

| 功能 | Jira Align | LEST OKR | 说明 |
|------|-----------|---------|------|
| OKR 制定 | ✅ | ✅ | 目标/关键结果/周期 |
| 纵向对齐 | ✅ | ✅ | 父级 OKR 对齐 |
| 横向协同 | ✅ | ✅ | 跨团队 OKR 可见 |
| 评分体系 | ✅ | ✅ | 0.0~1.0 评分 |
| 进度追踪 | ✅ | ✅ | 进度条 + KR 更新 |
| 与任务集成 | ✅ | ✅ | KR → Epic/Story/Task |
| AI 辅助评分 | ❌ | ✅ | AI 辅助评分建议 |
| 仪表盘 | ✅ | ✅ | 个人 + 团队 + 公司级 |

---

## 2. 功能范围

### 2.1 核心功能

| 功能 | 说明 | 优先级 |
|------|------|--------|
| OKR 周期管理 | 季度/半年/年度周期创建与管理 | P0 |
| 目标制定 | 目标（O）创建、编辑、归档 | P0 |
| 关键结果制定 | 关键结果（KR）创建、编辑，每个 O 最多 5 个 KR | P0 |
| 纵向对齐 | 子目标对齐到父目标，形成对齐树 | P0 |
| 进度更新 | 每次更新记录进度 + 信心度变化 | P0 |
| KR → 任务关联 | KR 关联到 LEST Epic/Story/Task，自动计算进度 | P0 |
| 评分 | 自评 + 上级评，最终评分 0.0~1.0 | P0 |
| OKR 仪表盘 | 个人视图 + 团队视图 + 公司视图 | P0 |
| 对齐树视图 | 可视化展示组织级 OKR 对齐关系 | P1 |
| AI 辅助评分 | 基于 KR 完成情况 + 任务数据，AI 生成评分建议 | P1 |
| OKR 模板 | 公司预设 OKR 模板（战略主题/职能目标） | P2 |
| OKR 导入/导出 | CSV 批量导入，PDF/Excel 导出 | P2 |

### 2.2 非功能性需求

- 性能：周期内 OKR 列表查询 < 200ms
- 隔离：OKR 数据通过 `pl_okr_` 表前缀隔离
- 权限：OKR 查看受项目权限控制，对齐树只显示有权限的目标

---

## 3. 数据库设计

### 3.1 表结构

```sql
-- OKR 周期
CREATE TABLE pl_okr_period (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    period_key      VARCHAR(64) NOT NULL UNIQUE COMMENT '季度标识，如 2026Q1',
    period_type     VARCHAR(16) NOT NULL COMMENT 'QUARTER/HALF_YEAR/YEAR',
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    scoring_start   DATE NOT NULL COMMENT '开始评分日期',
    scoring_end     DATE NOT NULL COMMENT '评分截止日期',
    status         VARCHAR(16) NOT NULL COMMENT 'DRAFT/ACTIVE/CLOSED',
    created_by     BIGINT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 目标
CREATE TABLE pl_okr_objective (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    period_id       BIGINT NOT NULL,
    parent_id       BIGINT DEFAULT NULL COMMENT '父目标 ID，纵向对齐',
    owner_id        BIGINT NOT NULL COMMENT '目标负责人',
    title           VARCHAR(256) NOT NULL COMMENT '目标内容',
    description     TEXT COMMENT '目标详细描述',
    color           VARCHAR(16) DEFAULT '#3B82F6' COMMENT '目标颜色标签',
    status          VARCHAR(16) NOT NULL COMMENT 'DRAFT/PUBLISHED/ACHIEVED/CANCELLED',
    final_score     DECIMAL(3,2) DEFAULT NULL COMMENT '最终评分 0.00~1.00',
    project_id      BIGINT DEFAULT NULL COMMENT '关联项目，可为空（公司级 OKR）',
    sort            INT DEFAULT 0,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (period_id) REFERENCES pl_okr_period(id),
    FOREIGN KEY (parent_id) REFERENCES pl_okr_objective(id),
    FOREIGN KEY (owner_id) REFERENCES sys_user(user_id)
);

-- 关键结果
CREATE TABLE pl_okr_key_result (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    objective_id    BIGINT NOT NULL,
    title           VARCHAR(256) NOT NULL COMMENT 'KR 描述',
    kr_type         VARCHAR(16) NOT NULL COMMENT 'BINARY/PERCENTAGE/NUMBER/CURRENCY',
    target_value    DECIMAL(20,4) NOT NULL COMMENT '目标值',
    current_value   DECIMAL(20,4) DEFAULT 0 COMMENT '当前值',
    unit            VARCHAR(32) DEFAULT '' COMMENT '单位，如 %、个、元',
    weight          DECIMAL(3,2) DEFAULT 1.00 COMMENT '权重，多个 KR 权重和应为 1',
    status          VARCHAR(16) NOT NULL DEFAULT 'ON_TRACK' COMMENT 'ON_TRACK/AT_RISK/NO_PROGRESS/ACHIEVED',
    due_date        DATE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (objective_id) REFERENCES pl_okr_objective(id)
);

-- KR 进度更新记录
CREATE TABLE pl_okr_progress (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    key_result_id   BIGINT NOT NULL,
    value          DECIMAL(20,4) NOT NULL COMMENT '更新后的值',
    confidence      VARCHAR(16) DEFAULT 'SAME' COMMENT 'HIGHER/SAME/LOWER 信心度变化',
    note            VARCHAR(512) COMMENT '更新说明',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (key_result_id) REFERENCES pl_okr_key_result(id)
);

-- KR → 任务关联
CREATE TABLE pl_okr_task_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    key_result_id   BIGINT NOT NULL,
    issue_id        BIGINT NOT NULL COMMENT 'LEST 任务 ID',
    contribution    DECIMAL(3,2) DEFAULT 1.00 COMMENT '贡献度 0.00~1.00',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (key_result_id) REFERENCES pl_okr_key_result(id),
    FOREIGN KEY (issue_id) REFERENCES issue(id)
);

-- OKR 评分
CREATE TABLE pl_okr_score (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    objective_id    BIGINT NOT NULL,
    scorer_id       BIGINT NOT NULL COMMENT '评分人（自评或上级）',
    score_type      VARCHAR(16) NOT NULL COMMENT 'SELF/COMPANY',
    score           DECIMAL(3,2) NOT NULL COMMENT '评分 0.00~1.00',
    comment         TEXT COMMENT '评分说明',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (objective_id) REFERENCES pl_okr_objective(id),
    FOREIGN KEY (scorer_id) REFERENCES sys_user(user_id)
);
```

---

## 4. 用户故事

### 4.1 OKR 周期管理

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-OKR-001 | 作为系统管理员，我希望创建 OKR 周期（如 2026Q3），设置开始/截止日期和评分窗口 | 周期创建后，用户可在周期内创建 OKR |
| US-OKR-002 | 作为系统管理员，我希望管理 OKR 周期状态（DRAFT→ACTIVE→CLOSED） | 不同状态下功能可用性符合预期 |
| US-OKR-003 | 作为系统管理员，我希望设置周期级 OKR 对齐规则（是否强制对齐、是否公开评分） | 规则在周期内全局生效 |

### 4.2 目标制定

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-OKR-010 | 作为员工，我希望创建自己的目标（O） | 目标创建后自动归属当前周期 |
| US-OKR-011 | 作为员工，我希望将自己的目标对齐到上级/同事的目标 | 对齐后可在对齐树中查看 |
| US-OKR-012 | 作为员工，我希望为每个目标设置关键结果（KR），每个目标最多 5 个 KR | KR 超出 5 个时提示 |
| US-OKR-013 | 作为员工，我希望将 KR 与 LEST 任务关联，系统自动汇总任务完成度作为 KR 进度 | 任务完成时 KR 进度自动更新 |
| US-OKR-014 | 作为员工，我希望在目标详情页查看对齐树（上级/平级/下级目标） | 对齐树展示所有层级 |
| US-OKR-015 | 作为员工，我希望归档/取消不再追踪的目标 | 归档后不影响历史评分数据 |

### 4.3 进度追踪

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-OKR-020 | 作为员工，我希望更新 KR 进度（手动填值或通过任务自动同步） | 进度更新后自动计算目标完成百分比 |
| US-OKR-021 | 作为员工，我希望每次更新 KR 时同时更新信心度（HIGHER/SAME/LOWER） | 信心度变化在进度历史中可见 |
| US-OKR-022 | 作为系统，我希望自动检测 KR 风险（进度落后计划 20% 以上）并提醒 | 风险 KR 在仪表盘高亮显示 |

### 4.4 评分

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-OKR-030 | 作为员工，我希望在评分窗口期内提交自评（0.0~1.0） | 自评提交后在目标详情页显示 |
| US-OKR-031 | 作为上级，我希望为下属的所有目标评分 | 上级评分与自评分开显示 |
| US-OKR-032 | 作为系统，我希望在评分截止后自动计算最终评分（自评 40% + 上级评分 60%） | 最终评分在目标详情页显示 |
| US-OKR-033 | 作为员工，我希望查看 AI 辅助评分建议（基于任务完成数据和进度历史） | AI 评分建议以对话形式展示，可选择采纳 |

---

## 5. API 设计

### 5.1 核心接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/okr/periods` | GET | 周期列表 |
| `/okr/periods` | POST | 创建周期（仅管理员） |
| `/okr/periods/{id}` | PUT | 更新周期 |
| `/okr/objectives` | GET | 目标列表（支持 period_id/owner_id/parent_id 筛选） |
| `/okr/objectives` | POST | 创建目标 |
| `/okr/objectives/{id}` | PUT | 更新目标 |
| `/okr/objectives/{id}` | DELETE | 删除目标（仅 DRAFT 状态） |
| `/okr/objectives/{id}/align` | GET | 获取对齐树（上级→当前→下级） |
| `/okr/objectives/{id}/score` | POST | 提交评分 |
| `/okr/key-results` | POST | 创建关键结果 |
| `/okr/key-results/{id}` | PUT | 更新关键结果 |
| `/okr/key-results/{id}/progress` | POST | 更新进度 |
| `/okr/key-results/{id}/tasks` | POST | 关联任务 |
| `/okr/key-results/{id}/tasks/{issueId}` | DELETE | 取消关联 |
| `/okr/dashboard/personal` | GET | 个人 OKR 仪表盘 |
| `/okr/dashboard/team` | GET | 团队 OKR 仪表盘 |
| `/okr/dashboard/company` | GET | 公司 OKR 仪表盘 |
| `/okr/ai/score-suggest` | POST | AI 评分建议 |

---

## 6. 前端设计

### 6.1 页面结构

```
OKR 模块
├── OKR 周期设置（仅管理员）
│   └── 周期列表 → 创建/编辑周期
├── 我的 OKR
│   ├── OKR 列表（当前周期）
│   │   ├── 目标卡片（进度条 + KR 列表）
│   │   └── 新建目标弹窗
│   ├── 对齐树视图
│   ├── 历史 OKR
│   └── 评分中心（评分窗口期）
└── OKR 仪表盘
    ├── 个人 OKR
    ├── 团队 OKR
    └── 公司 OKR
```

### 6.2 关键组件

| 组件 | 说明 |
|------|------|
| `OkrObjectiveCard` | 目标卡片（标题 + 进度条 + KR 列表 + 负责人头像） |
| `OkrAlignTree` | 对齐树（树形结构，支持展开/折叠） |
| `OkrKrForm` | KR 编辑表单（类型选择 + 目标值 + 单位） |
| `OkrProgressTimeline` | 进度时间线（每个 KR 的进度历史） |
| `OkrScorePanel` | 评分面板（自评 + 上级评 + AI 建议） |
| `OkrDashboard` | OKR 仪表盘（ECharts 仪表盘 + 环形图） |

---

## 7. 与其他模块的集成

| 集成点 | 方式 | 说明 |
|--------|------|------|
| 任务管理 | LEST API | KR → 任务关联，自动同步任务状态 |
| 项目管理 | 字段关联 | OKR 可关联到具体项目 |
| AI 服务 | lest-ai API | AI 评分建议 |
| 通知框架 | 通知事件 | OKR 进展通知、对齐通知、评分通知 |

---

## 8. 数据隔离

- 所有表使用 `pl_okr_` 前缀
- 插件元数据存储在 `plugin` 表
- 插件配置存储在 `pl_okr_config` 表

---

## 9. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| OKR | 11300-11399 | 插件 OKR 模块 |

## 10. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2027年中 | 初始版本 | - |
