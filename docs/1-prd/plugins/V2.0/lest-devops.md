# DevOps 集成（lest-devops）PRD — V2.0

> **插件 ID**: `lest-devops`
>
> **版本**: V2.0
>
> **Jira 映射**: Jira DevOps Integrations
>
> **许可**: 🆓 开源基础版 / 🔒 商业增强版
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

将 SonarQube、Harbor、Prometheus 等 DevOps 工具与 LEST 任务关联，形成从需求到交付的可追踪链路。

---

## 2. 数据库设计

```sql
-- DevOps 工具连接配置
CREATE TABLE pl_devops_connection (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider_type   VARCHAR(32) NOT NULL COMMENT 'SONARQUBE/HARBOR/PROMETHEUS/JENKINS',
    name            VARCHAR(128) NOT NULL,
    base_url        VARCHAR(256) NOT NULL,
    auth_type       VARCHAR(16) NOT NULL COMMENT 'TOKEN/BASIC/API_KEY',
    auth_config     JSON NOT NULL COMMENT '加密存储认证信息',
    is_enabled      BOOLEAN DEFAULT TRUE,
    created_by      BIGINT NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- SonarQube 质量门禁关联
CREATE TABLE pl_sonarqube_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    project_key     VARCHAR(128) NOT NULL COMMENT 'SonarQube 项目 Key',
    lest_project_id BIGINT NOT NULL,
    quality_gate    VARCHAR(64) COMMENT '质量门禁名称',
    FOREIGN KEY (connection_id) REFERENCES pl_devops_connection(id),
    FOREIGN KEY (lest_project_id) REFERENCES project(id)
);

-- 制品关联
CREATE TABLE pl_artifact_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    artifact_name   VARCHAR(256) NOT NULL,
    version         VARCHAR(64) NOT NULL,
    digest          VARCHAR(128) COMMENT '镜像 SHA256',
    build_url       VARCHAR(512) COMMENT 'CI 构建链接',
    release_id      BIGINT COMMENT '关联的发布记录',
    FOREIGN KEY (connection_id) REFERENCES pl_devops_connection(id)
);

-- 指标关联
CREATE TABLE pl_metric_link (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id   BIGINT NOT NULL,
    metric_name     VARCHAR(128) NOT NULL COMMENT '如：cpu_usage/memory_usage/error_rate',
    threshold_warn  DECIMAL(10,4) COMMENT '警告阈值',
    threshold_error DECIMAL(10,4) COMMENT '错误阈值',
    lest_release_id BIGINT COMMENT '关联发布',
    FOREIGN KEY (connection_id) REFERENCES pl_devops_connection(id)
);
```

---

## 3. 集成详情

### 3.1 SonarQube 集成

| 功能 | 说明 |
|------|------|
| 代码质量关联 | 每个 Commit/PR 自动触发 SonarQube 扫描，结果关联到任务 |
| 质量门禁 | Build 失败时阻止发布，消息回写到 LEST |
| 趋势图 | 在任务详情页显示代码质量趋势（Bug/Smell/Vulnerability 数） |

### 3.2 Harbor 集成

| 功能 | 说明 |
|------|------|
| 制品元数据 | 记录镜像名/版本/Digest/构建时间 |
| 发布关联 | 制品自动关联到 Release，记录变更内容 |
| 安全扫描 | 关联 Trivy 扫描结果，高危漏洞告警 |

### 3.3 Prometheus 集成

| 功能 | 说明 |
|------|------|
| 指标关联 | 发布后监控关键指标（错误率/响应时间） |
| 告警规则 | 配置阈值，超阈值自动在 LEST 创建 Issue |
| 趋势看板 | DevOps 仪表盘展示指标趋势 |

---

## 4. 用户故事

| ID | 用户故事 | 验收标准 |
|----|---------|---------|
| US-OPS-001 | 作为 DevOps 工程师，我希望在 LEST 中配置 SonarQube 连接（URL + Token）并测试 | 连接测试通过后保存 |
| US-OPS-002 | 作为开发者，我希望在任务详情页查看该任务关联代码的 SonarQube 质量报告 | 质量报告以卡片形式展示 |
| US-OPS-003 | 作为 DevOps 工程师，我希望配置 Harbor 制品与发布的关联 | 制品列表在发布详情页展示 |
| US-OPS-004 | 作为运维人员，我希望在 LEST 中查看 Prometheus 指标，并在异常时自动创建 Issue | 告警触发后在 LEST 中生成任务并通知 |
| US-OPS-005 | 作为开发者，我希望在 DevOps 仪表盘看到完整的 CI/CD 链路（Build → Scan → Deploy → Monitor） | 链路图展示各环节状态 |

---

## 5. API 设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `/devops/connections` | GET/POST | 连接列表/创建 |
| `/devops/connections/{id}` | GET/PUT/DELETE | 连接详情/更新/删除 |
| `/devops/connections/{id}/test` | POST | 测试连接 |
| `/devops/sonarqube/projects` | GET | 获取 SonarQube 项目列表 |
| `/devops/sonarqube/measures` | GET | 获取质量指标 |
| `/devops/harbor/artifacts` | GET | 获取制品列表 |
| `/devops/prometheus/metrics` | GET | 获取指标数据 |
| `/devops/dashboard/{projectId}` | GET | DevOps 仪表盘数据 |

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
| DevOps | 10600-10699 | 插件 DevOps 模块 |

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026年底 | 初始版本 | - |
