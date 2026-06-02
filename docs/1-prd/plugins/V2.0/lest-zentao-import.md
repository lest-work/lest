# 禅道导入（lest-zentao-import）PRD — V2.0

> **插件 ID**: `lest-zentao-import`
>
> **版本**: V2.0
>
> **Jira 映射**: Jira Importers（第三方迁移工具）
>
> **许可**: 🆓 开源基础版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

支持从禅道（Zentao）一键迁移项目数据到 LEST，包括产品/项目/需求/任务/Bug/用户/评论。

---

## 2. 兼容版本

| 禅道版本 | 兼容状态 | 说明 |
|---------|---------|------|
| Zentao 18.x | ✅ 兼容 | 当前测试版本 |
| Zentao 16.x ~ 17.x | ✅ 兼容 | OpenAPI 兼容 |
| Zentao 15.x | ⚠️ 可能兼容 | API 差异需测试 |
| ZenTaoIPM（社区版） | ⚠️ 部分兼容 | 需验证 OpenAPI 可用性 |

---

## 3. 数据库设计

```sql
-- 禅道连接配置
CREATE TABLE pl_zentao_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_name     VARCHAR(128) NOT NULL,
    zentao_url      VARCHAR(256) NOT NULL,
    auth_type       VARCHAR(16) NOT NULL COMMENT 'PASSWORD/TOKEN',
    username        VARCHAR(128),
    password        VARCHAR(256) COMMENT '加密存储（仅密码模式）',
    api_token       VARCHAR(256) COMMENT 'API Token（Token 模式）',
    is_enabled      BOOLEAN DEFAULT TRUE,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 禅道产品/项目映射
CREATE TABLE pl_zentao_product_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    zentao_product_id BIGINT NOT NULL,
    zentao_product_name VARCHAR(256),
    lest_project_id BIGINT,
    status          VARCHAR(16) DEFAULT 'PENDING' COMMENT 'PENDING/MAPPED/IMPORTED',
    FOREIGN KEY (config_id) REFERENCES pl_zentao_config(id),
    FOREIGN KEY (lest_project_id) REFERENCES project(id)
);

-- 字段映射规则
CREATE TABLE pl_zentao_field_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    zentao_field    VARCHAR(128) NOT NULL COMMENT '禅道字段名',
    lest_field      VARCHAR(128) NOT NULL COMMENT 'LEST 字段标识',
    transform_type  VARCHAR(16) DEFAULT 'DIRECT' COMMENT 'DIRECT/MAPPING/FORMULA',
    transform_config JSON COMMENT '映射表或公式',
    FOREIGN KEY (config_id) REFERENCES pl_zentao_config(id)
);

-- 状态映射
CREATE TABLE pl_zentao_status_map (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    zentao_status   VARCHAR(64) NOT NULL,
    zentao_type     VARCHAR(32) NOT NULL COMMENT 'bug/story/task',
    lest_status_id  BIGINT NOT NULL,
    FOREIGN KEY (config_id) REFERENCES pl_zentao_config(id),
    FOREIGN KEY (lest_status_id) REFERENCES workflow_status(id)
);

-- 导入任务记录
CREATE TABLE pl_zentao_import_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_id       BIGINT NOT NULL,
    product_map_id  BIGINT NOT NULL,
    status          VARCHAR(16) NOT NULL COMMENT 'PENDING/RUNNING/SUCCESS/FAILED/PARTIAL',
    total_items     INT DEFAULT 0,
    imported_items  INT DEFAULT 0,
    failed_items    INT DEFAULT 0,
    started_at      DATETIME,
    finished_at     DATETIME,
    error_detail    TEXT,
    FOREIGN KEY (config_id) REFERENCES pl_zentao_config(id),
    FOREIGN KEY (product_map_id) REFERENCES pl_zentao_product_map(id)
);

-- 导入失败的 Item
CREATE TABLE pl_zentao_import_error (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    log_id          BIGINT NOT NULL,
    zentao_type     VARCHAR(32) NOT NULL,
    zentao_id       BIGINT NOT NULL,
    error_message   VARCHAR(512) NOT NULL,
    retry_count     INT DEFAULT 0,
    FOREIGN KEY (log_id) REFERENCES pl_zentao_import_log(id)
);
```

---

## 4. 导入字段映射

### 4.1 禅道 → LEST 字段映射

| 禅道（产品） | 禅道（项目） | LEST |
|------------|------------|------|
| 产品名称 | 项目名称 | Project Name |
| 产品代号 | 项目代号 | Project Key |
| 产品描述 | 项目描述 | Project Description |
| 产品负责人 | 项目负责人 | Project Lead |
| — | 团队成员 | Project Members |
| — | 燃尽图数据 | Sprints |
| 需求 (story) | — | Epic / Story |
| Bug | — | Bug |
| — | 任务 (task) | Task |
| — | 生命周期 (bug) | Bug |
| 产品用户 | — | LEST User |
| — | 文档 | 暂不支持（V2.0） |

### 4.2 状态映射（默认）

| 禅道 Bug 状态 | LEST Status |
|-------------|-------------|
| 待确认 (wait) | Open |
| 激活 (active) | In Progress |
| 已解决 (resolved) | Done |
| 关闭 (closed) | Closed |
| 延期 (postponed) | Won't Do |

---

## 5. 导入向导流程

```
步骤 1: 连接禅道
  └── 输入 URL / 认证信息 → 测试连接

步骤 2: 选择产品/项目
  └── 列出禅道产品列表 → 选择要导入的产品

步骤 3: 配置映射
  └── 字段映射 → 状态映射 → 用户映射

步骤 4: 预览
  └── 预览数据统计（需求数/Bug 数/任务数/用户数）

步骤 5: 执行导入
  └── 实时显示导入进度
  └── 完成后显示统计报告

步骤 6: 验证
  └── 生成导入报告（成功数/失败数）
  └── 跳转到新创建的项目
```

---

## 6. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-IMP-001 | 作为系统管理员，我希望配置禅道连接（URL + 认证信息）并测试连接 | 连接测试通过后显示禅道产品列表 |
| US-IMP-002 | 作为系统管理员，我希望选择要导入的禅道产品/项目 | 选择后显示数据统计预览 |
| US-IMP-003 | 作为系统管理员，我希望配置字段映射规则（禅道字段 → LEST 字段） | 支持直接映射和自定义映射 |
| US-IMP-004 | 作为系统管理员，我希望配置状态映射（禅道 Bug/需求状态 → LEST 状态） | 状态映射在导入时自动应用 |
| US-IMP-005 | 作为系统管理员，我希望配置禅道用户 → LEST 用户的映射 | 用户映射确保评论/负责人正确归属 |
| US-IMP-006 | 作为系统管理员，我希望在导入前预览数据（显示即将导入多少条记录） | 预览显示各类型数据的统计数量 |
| US-IMP-007 | 作为系统管理员，我希望执行导入，实时查看进度 | 进度条显示已完成/总数 |
| US-IMP-008 | 作为系统管理员，我希望查看导入日志，了解导入失败的原因 | 日志显示错误信息和重试入口 |
| US-IMP-009 | 作为系统管理员，我希望在导入失败时重试失败项 | 重试后只导入失败项，不重复已成功项 |
| US-IMP-010 | 作为系统管理员，我希望导入完成后跳转到新创建的项目 | 跳转后项目包含所有导入的数据 |

---

## 7. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/zentao/config` | GET/POST | 连接配置列表/创建 |
| `/zentao/config/{id}/test` | POST | 测试连接 |
| `/zentao/products` | GET | 获取禅道产品列表 |
| `/zentao/config/{id}` | PUT/DELETE | 更新/删除配置 |
| `/zentao/field-maps` | GET/POST/DELETE | 字段映射规则 |
| `/zentao/status-maps` | GET/POST/DELETE | 状态映射规则 |
| `/zentao/user-maps` | GET/POST/DELETE | 用户映射规则 |
| `/zentao/import/preview` | POST | 导入预览 |
| `/zentao/import` | POST | 执行导入 |
| `/zentao/import/logs` | GET | 导入日志列表 |
| `/zentao/import/logs/{id}` | GET | 导入日志详情 |
| `/zentao/import/errors/{logId}` | GET | 失败项列表 |
| `/zentao/import/retry/{logId}` | POST | 重试失败项 |

---

## 8. 错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| 禅道导入 | 10900-10999 | 插件 禅道导入 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
