# lest-ai-report — AI 报表插件

> **插件 ID**: `lest-ai-report`
>
> **版本**: V4.0.5
>
> **Jira 映射**: Jira Reports（AI 增强）
>
> **许可**: 开源（MIT）
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST AI 报表通过 AI 分析团队的行为数据，自动生成个性化的效能分析报告，为团队提供客观的绩效洞察和改进行动建议。任务、看板、Sprint 数据来自 Core；工时、会议、代码数据来自 V4.0 插件，未安装对应插件时自动降级为不可用维度。

### 1.2 与竞品对标

|| 功能 | Jira Reports | LEST AI 报表 | 说明 |
||------|------------|--------------|------|
| 基础统计 | ✅ | ✅ | 完成率/速度/工时 |
| 趋势图表 | ✅ | ✅ | 周/月趋势 |
| AI 分析解读 | ❌ | ✅ | AI 自动解读数据含义 |
| 个性化建议 | ❌ | ✅ | 基于数据生成改进行动建议 |
| 自动化周报 | ❌ | ✅ | AI 自动生成结构化周报 |
| 多数据源 | ❌ | ✅ | 任务+工时+会议+代码 |

---

## 2. 功能范围

### 2.1 核心功能（P0）

|| 功能 | 说明 |
||------|------|
| 个人效能报告 | 周报/月报自动生成，聚合任务/工时/代码/会议数据 |
| 团队效能分析 | 团队协作效能热力图、速度趋势、交付质量 |
| AI 总结建议 | 基于数据生成改进行动建议 |
| 导出与分享 | PDF/Excel 导出 |

### 2.2 增强功能（P1）

|| 功能 | 说明 |
||------|------|
| 自定义报表模板 | 用户自定义报表维度和指标 |
| 效能基准对比 | 与行业基准或历史均值对比 |
| 异常检测 | AI 自动发现异常数据点并标注 |

### 2.3 进阶功能（P2）

|| 功能 | 说明 |
||------|------|
| 趋势预测 | 基于历史数据预测下期效能趋势 |
| 个性化推荐 | 基于用户行为推荐最适合的报表类型 |

---

## 3. 数据来源

|| 数据源 | 内容 | 更新频率 |
|---------|------|---------|
| Core 任务数据 | 完成的任务、状态变更、分配记录 | 实时 |
| Core 看板数据 | Sprint 速度、燃尽图 | 实时 |
| WakaTime 插件数据 | 编码时长、Issue 关联 | 每日 |
| 会议插件数据 | 会议参与、行动项 | 每日 |
| DevOps 插件数据 | 提交数、PR/MR 数、构建状态 | 每日 |

---

## 4. 报表类型

### 4.1 个人效能报告

|| 指标 | 计算方式 |
||------|---------|
| 本周完成任务数 | COUNT(完成的任务) |
| 本周工时 | SUM(工时记录) |
| 代码提交数 | COUNT(Git 提交) |
| 会议参与数 | COUNT(参与的会议) |
| 任务完成率 | 完成数 / 计划数 |
| 平均任务时长 | AVG(任务完成时间 - 创建时间) |

### 4.2 团队效能分析

|| 指标 | 说明 |
||------|------|
| Sprint 速度 | 完成 Story Point / Sprint |
| 燃尽图 | Sprint 内工作量消耗趋势 |
| 交付质量 | Bug 数量 / 完成任务数 |
| 团队协作热力图 | 按周/天展示团队活跃度 |
| 阻塞分析 | 阻塞任务平均解决时长 |

### 4.3 AI 总结建议示例

```
## 本周效能总结

### 亮点
- ✅ 本周完成任务 12 个，超额完成 20%
- ✅ 代码提交频率稳定，平均每天 3.2 次
- ✅ Sprint 速度较上周提升 15%

### 关注点
- ⚠️ 平均任务完成时长 3.2 天，略高于团队均值 2.8 天
- ⚠️ 周三会议时间较长（平均 68 分钟），建议精简议程

### 下周建议
1. 优先处理 P0 高优先级任务
2. 减少会议频次，将会议控制在 45 分钟以内
3. 关注延期超过 3 天的任务
```

---

## 5. 数据库设计

```sql
CREATE TABLE ai_report (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    report_type     VARCHAR(32) NOT NULL COMMENT 'PERSONAL_WEEKLY/PERSONAL_MONTHLY/TEAM',
    period_start    DATE NOT NULL,
    period_end      DATE NOT NULL,
    content         JSON NOT NULL COMMENT '报表内容结构',
    summary         TEXT COMMENT 'AI 生成的总结',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_report_subscription (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    report_type     VARCHAR(32) NOT NULL,
    schedule        VARCHAR(32) NOT NULL COMMENT 'WEEKLY/MONTHLY',
    channels        JSON NOT NULL COMMENT '邮件/IM/Webhook',
    enabled         BOOLEAN DEFAULT TRUE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 6. API 设计

|| 端点 | 方法 | 说明 |
||------|------|------|
| `/ai/report/generate` | POST | 生成 AI 报表 |
| `/ai/report/list` | GET | 获取报表列表 |
| `/ai/report/{id}` | GET | 获取报表详情 |
| `/ai/report/subscription` | POST | 创建报表订阅 |
| `/ai/report/export` | GET | 导出报表（PDF/Excel） |
