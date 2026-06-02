# AI 服务 PRD
> **📌 Jira 映射**：Atlassian Intelligence（AI-Native 智能引擎，Atlassian AI）
>
> **功能定位**：LEST 的 AI-Native 智能引擎，为所有业务模块提供 AI 能力支撑，对应 Jira 的 Atlassian Intelligence。


## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V2.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-25 |
| 最后更新 | 2026-05-25 |

> **核心设计理念**：LEST Platform 是一个 **AI-Native 敏捷管理平台**，AI 不是独立功能模块，而是一个**贯穿所有业务模块的中央智能引擎**。各业务模块通过 AI 服务提供的标准 API 和事件机制调用 AI 能力，实现智能洞察、自动化分析和数据驱动决策。AI 服务保持独立部署，但以服务调用的方式深度融入业务。

---

## 1. 概述

### 1.1 功能背景

LEST Platform 定位为开源的 AI-Native 敏捷管理平台。平台已接入 WakaTime 编码追踪、任务管理、代码管理、CI/CD 等多个业务模块，积累了丰富的团队协作数据。AI 服务作为中央智能引擎，为这些数据赋予智能分析能力，帮助团队实现数据驱动的管理和决策。

### 1.2 设计理念

**AI 不是一个功能，而是一种能力。**

- 各业务模块（任务、绩效、WakaTime、会议等）通过 API 调用 AI 服务，而非各模块自己实现 AI 逻辑
- AI 服务提供标准化的智能分析能力，业务模块按需消费
- AI 的调用由事件驱动（任务完成时触发 AI 分析、每日定时生成洞察报告等）
- 保持 AI 服务的独立性，便于升级模型、切换 Provider 而不影响业务代码

### 1.3 目标

- **绩效模块深度 AI 化**：将 WakaTime 编码数据 + 任务完成数据 + 代码提交数据整合，为绩效评估提供客观数据支撑和 AI 洞察
- **AI 绩效洞察服务**：自动分析编码活跃度、工时预估准确度、工作曲线，为管理者提供团队状态报告
- **多场景 AI 助手**：代码审查、任务分析、会议总结等场景统一由 AI 引擎提供能力
- **统一的 AI 网关**：多模型接入、路由、降级、成本控制

### 1.4 与其他模块的关系

```
┌──────────────────────────────────────────────────────────────────────────┐
│                     LEST Platform 业务模块                               │
│                                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐ │
│  │ 任务管理  │  │ 绩效管理  │  │ WakaTime │  │ 代码管理  │  │ 会议管理 │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └───┬────┘ │
│       │              │              │              │              │      │
└───────┼──────────────┼──────────────┼──────────────┼──────────────┼──────┘
        │              │              │              │              │
        │              │              │              │              │
        ▼              ▼              ▼              ▼              ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                         AI 服务（中央智能引擎）                            │
│                           lest-ai                                       │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        AI 引擎核心                                 │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │   │
│  │  │ AI 网关   │  │ 模型管理   │  │ 提示词管理 │  │ 调用计费   │ │   │
│  │  │ Gateway   │  │ Models    │  │ Prompts   │  │ Billing    │ │   │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘ │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        AI 业务能力层                               │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐ │   │
│  │  │ 绩效洞察   │  │ 代码审查   │  │ 任务分析   │  │ 会议总结   │ │   │
│  │  │ Perf Insight│  │ Code Review│ │ Task AI   │  │ Meeting Sum│ │   │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘ │   │
│  │        │                 │                 │                 │        │   │
│  │        ▼                 ▼                 ▼                 ▼        │   │
│  │  ┌─────────────────────────────────────────────────────────┐        │   │
│  │  │              AI 数据整合层（Performance Analytics）        │        │   │
│  │  │  WakaTime 数据 + 任务数据 + 代码提交 + 会议数据 + 绩效数据  │        │   │
│  │  └─────────────────────────────────────────────────────────┘        │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
                          ┌──────────────────┐
                          │  通知服务         │
                          │  推送 AI 洞察报告 │
                          └──────────────────┘
```

---

## 2. 用户故事

### 2.1 AI 绩效洞察（核心场景）

#### US-001: AI 编码活跃度分析
**作为** 开发者，**我希望** 系统自动分析我的编码活跃度趋势，**以便** 了解自己的工作状态。

验收标准：
- [ ] AI 自动分析 WakaTime 每日编码时长数据
- [ ] 生成编码活跃度趋势图和解读文字
- [ ] 识别工作曲线特征（早起型/夜猫型/间歇型）
- [ ] 对比团队平均水平，给出相对评价
- [ ] 编码时长波动超过阈值时自动预警

#### US-002: AI 工时预估准确度评估
**作为** 项目经理，**我希望** 系统自动评估团队成员的工时预估准确度，**以便** 改进项目排期。

验收标准：
- [ ] AI 自动对比任务预估工时 vs 实际工时（来自 WakaTime）
- [ ] 计算个人和团队的平均偏差率
- [ ] 识别预估偏差的模式（如：Bug 类任务常被低估）
- [ ] 基于历史数据给出新任务工时 AI 估算建议
- [ ] 工时偏差超过阈值时通知项目经理

#### US-003: AI 个人发展建议
**作为** 团队成员，**我希望** 获得基于个人数据的 AI 发展建议，**以便** 提升工作效率。

验收标准：
- [ ] AI 分析个人的工作曲线，识别高效工作时段
- [ ] 基于编码语言分布，建议技能提升方向
- [ ] 对比同岗位平均水平，给出差距分析
- [ ] 提供可执行的个人改进建议

#### US-004: AI 团队状态报告
**作为** 管理者，**我希望** 定期收到 AI 生成的团队状态报告，**以便** 及时了解团队健康度。

验收标准：
- [ ] AI 每日/每周自动生成团队状态报告
- [ ] 报告包含：编码活跃度分布、任务完成趋势、风险预警、改进建议
- [ ] 支持推送到邮件/钉钉/飞书
- [ ] 支持管理层定制报告模板

#### US-005: AI 项目风险预警
**作为** 项目经理，**我希望** AI 自动预警项目风险，**以便** 及时调整计划。

验收标准：
- [ ] AI 分析任务延期率、编码时长变化、代码提交频率等多维度数据
- [ ] 识别团队工作负荷不均、成员疲劳、进度落后等风险信号
- [ ] AI 自动生成风险报告并发送给项目经理

### 2.2 AI 代码审查

#### US-006: MR 创建时自动触发 AI 审查
**作为** 开发人员，**我希望** 提交 MR 后自动进行 AI 代码审查，**以便** 获得即时的代码质量反馈。

验收标准：
- [ ] MR 创建时自动触发审查（通过 Webhook 或事件总线）
- [ ] AI 审查完成后发送通知给 MR 作者
- [ ] 审查结果包含问题列表、严重程度、修改建议
- [ ] 支持在 MR 页面展示审查结果

#### US-007: AI 审查结果推送
**作为** 开发者，**我希望** 在代码审查结果出来后收到通知，**以便** 及时处理问题。

验收标准：
- [ ] AI 审查完成后通过站内信/邮件通知 MR 作者
- [ ] 通知内容包含审查摘要和高严重性问题列表
- [ ] 支持点击跳转到具体文件和行号

### 2.3 AI 任务助手

#### US-008: 任务创建时 AI 分析
**作为** 开发人员，**我希望** 创建任务时获得 AI 分析，**以便** 更好地理解和实现任务。

验收标准：
- [ ] 输入任务描述后，AI 自动分析任务复杂度
- [ ] AI 基于历史任务数据给出工时估算建议
- [ ] AI 识别任务的关键要点和潜在风险
- [ ] AI 建议可能相关的其他任务

#### US-009: AI 任务描述优化
**作为** 产品经理，**我希望** AI 优化任务描述，**以便** 提高开发人员的理解效率。

验收标准：
- [ ] AI 自动检查任务描述的完整性和清晰度
- [ ] AI 提供描述优化建议
- [ ] AI 检查验收标准是否可量化

### 2.4 AI 会议助手

#### US-010: AI 自动生成会议总结
**作为** 记录人，**我希望** AI 自动生成会议总结，**以便** 减少记录工作量。

验收标准：
- [ ] 会议结束后，AI 自动生成会议纪要
- [ ] 包含：关键讨论点、会议决议、行动项
- [ ] AI 自动为行动项推荐负责人
- [ ] 支持一键将行动项创建为任务

#### US-011: AI 会议待办追踪
**作为** 参与者，**我希望** 查看 AI 提取的会议待办，**以便** 跟踪执行情况。

验收标准：
- [ ] 显示 AI 提取的行动项列表
- [ ] 显示每个行动项的执行状态
- [ ] 行动项关联到具体任务后，显示任务状态

### 2.5 AI 网关管理

#### US-012: 配置 AI 模型
**作为** 平台管理员，**我希望** 配置 AI 模型连接，**以便** 平台能够调用 AI 能力。

验收标准：
- [ ] 支持配置通义千问 API
- [ ] 支持配置 Kimi API
- [ ] 支持配置 OpenAI GPT API
- [ ] 支持配置 Ollama 本地模型
- [ ] 支持设置模型优先级和默认模型
- [ ] 支持测试连接

#### US-013: 查看 AI 使用统计
**作为** 管理员，**我希望** 查看 AI 使用统计，**以便** 控制 AI 成本。

验收标准：
- [ ] 显示 AI 调用次数统计
- [ ] 显示 Token 消耗统计
- [ ] 显示成本估算
- [ ] 显示各模型使用占比
- [ ] 支持按用户/团队统计

### 2.6 AI Provider 扩展

#### US-014: 接入自定义 AI Provider
**作为** 开发者，**我希望** 通过插件接入自定义 AI Provider，**以便** 使用公司自研模型。

验收标准：
- [ ] 通过插件系统注册自定义 AI Provider
- [ ] 自定义 Provider 与内置 Provider 行为一致
- [ ] 支持配置 API Key 和 Base URL

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 调用方 | 优先级 |
|------|--------|---------|--------|--------|
| 1 | AI 绩效洞察 API | 为绩效模块提供 AI 分析能力 | 绩效服务 | P0 |
| 2 | 编码活跃度分析 | 分析 WakaTime 数据，生成活跃度报告 | 绩效服务 | P0 |
| 3 | 工时预估评估 | 对比预估与实际，给出准确度评估 | 绩效服务 | P0 |
| 4 | 个人发展建议 | 基于个人数据给出改进建议 | 绩效服务 | P0 |
| 5 | 团队状态报告 | 自动生成团队健康度报告 | 绩效服务 | P0 |
| 6 | 项目风险预警 | 多维度数据分析，识别风险 | 绩效服务 | P0 |
| 7 | AI 代码审查 | MR 自动审查，结果推送 | 代码服务 | P0 |
| 8 | 任务分析 | 任务创建时的 AI 分析 | 任务服务 | P1 |
| 9 | 会议总结 | 自动生成会议纪要和行动项 | 会议服务 | P1 |
| 10 | AI 网关 | 统一 AI 调用接口，多模型路由 | 内部 | P0 |
| 11 | AI 模型配置 | 管理 AI 模型连接配置 | 系统设置 | P0 |
| 12 | AI 使用统计 | 调用量、Token、成本统计 | 系统设置 | P1 |
| 13 | AI Provider 扩展 | 通过插件接入自定义模型 | 插件系统 | P1 |

### 3.2 AI 绩效洞察数据流

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        AI 绩效洞察数据流                                 │
└─────────────────────────────────────────────────────────────────────────┘

[ WakaTime 服务 ]
      │
      │ 每日心跳数据
      ▼
[ AI 绩效数据聚合 ]
      │
      ├──► 编码时长（每日/每周/每月）
      ├──► 工作曲线（按小时分布）
      ├──► 项目编码时间分布
      ├──► 语言使用分布
      │
      ▼
[ AI 绩效洞察引擎 ]
      │
      ├──► 活跃度趋势分析
      │     输入：每日编码时长序列（过去30天）
      │     输出：趋势描述（上升/下降/稳定）+ 波动幅度 + 同比环比数据
      │
      ├──► 工作曲线分析
      │     输入：按小时编码时长分布
      │     输出：工作时段类型（早起型/夜猫型）+ 高效时段 + 碎片化指数
      │
      ├──► 工时预估评估
      │     输入：task.estimated_hours vs wakapi_task_link.seconds
      │     输出：个人偏差率 + 团队偏差率 + 偏差模式分析
      │
      ├──► 综合发展建议
      │     输入：上述所有分析 + 任务完成数据 + 代码提交数据
      │     输出：多维度改进建议（按优先级排序）
      │
      └──► 团队状态报告
            输入：所有成员上述数据
            输出：团队健康度评分 + 风险预警 + 改进建议
                  │
                  ▼
            [ 通知服务 ]
                  │
                  ├──► 站内信推送（给个人/管理者）
                  ├──► 邮件推送（可选）
                  └──► 钉钉/飞书推送（可选）
```

### 3.3 AI 网关设计

#### 3.3.1 AI 网关架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           AI 网关（AI Gateway）                          │
│                                                                         │
│   请求入口                                                              │
│      │                                                                 │
│      ▼                                                                 │
│   ┌──────────┐     ┌──────────────┐     ┌──────────────────────────┐   │
│   │ 请求验证  │────►│ 模型路由    │────►│ 模型调用（多 Provider）  │   │
│   │ Auth     │     │ Router      │     │                          │   │
│   └──────────┘     └──────────────┘     │  ┌────────┐ ┌────────┐  │   │
│                                          │  │ 通义千问│ │  Kimi  │  │   │
│                                          │  └────────┘ └────────┘  │   │
│                                          │  ┌────────┐ ┌────────┐  │   │
│                                          │  │GPT-4   │ │Ollama │  │   │
│                                          │  └────────┘ └────────┘  │   │
│                                          └──────────────────────────┘   │
│                                              │                          │
│      ┌───────────────────────────────────────┼───────────────────────┐   │
│      │                                       ▼                       │   │
│      │   ┌────────────┐  ┌────────────┐  ┌────────────┐            │   │
│      │   │ 降级处理   │  │ 重试机制   │  │ 计费记录   │            │   │
│      │   │ Fallback  │  │ Retry     │  │ Billing   │            │   │
│      │   └────────────┘  └────────────┘  └────────────┘            │   │
│      │                                                               │   │
│      │   ┌────────────┐  ┌────────────┐                           │   │
│      │   │ 响应转换   │  │ 错误处理   │                           │   │
│      │   │ Transform │  │ Error     │                           │   │
│      │   └────────────┘  └────────────┘                           │   │
│      │                                                               │   │
│      └───────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 3.3.2 模型路由策略

```java
public class AIModelRouter {

    // 路由策略优先级
    // 1. 按场景路由：不同业务场景使用不同模型
    // 2. 按负载路由：模型调用量均衡分配
    // 3. 降级策略：主模型不可用时自动切换到备用模型

    public String route(AIScene scene, List<ModelConfig> availableModels) {
        // 按场景选择模型
        switch (scene) {
            case CODE_REVIEW:
                return selectModelByCapability(availableModels, "code_review");
            case PERFORMANCE_INSIGHT:
                return selectModelByCapability(availableModels, "analysis");
            case MEETING_SUMMARY:
                return selectModelByCapability(availableModels, "summarize");
            default:
                return availableModels.stream()
                    .filter(ModelConfig::isDefault)
                    .findFirst()
                    .map(ModelConfig::getModelId)
                    .orElse("qwen-turbo");
        }
    }
}
```

### 3.4 AI 绩效洞察 API 设计

#### 3.4.1 编码活跃度分析

```json
// POST /ai/insight/coding-activity
// 业务调用方：绩效服务
// 触发时机：每日定时 + 用户手动触发

请求:
{
  "userId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-31",
  "includeTeamAvg": true
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "period": {
      "startDate": "2026-05-01",
      "endDate": "2026-05-31",
      "days": 31
    },
    "summary": {
      "totalHours": 156.5,
      "dailyAverage": 5.05,
      "activeDays": 28,
      "teamAvgHours": 140.2,
      "teamAvgDaily": 4.52,
      "vsTeamAvg": 11.6  // 高出团队平均 11.6%
    },
    "trend": {
      "direction": "stable",  // rising / falling / stable
      "weeklyChange": 3.2,
      "monthlyChange": -5.1
    },
    "workCurve": {
      "type": "morning_bird",  // morning_bird / night_owl / balanced
      "peakHour": 10,
      "peakHours": [9, 10, 11, 14, 15],
      "fragmentationIndex": 0.23  // 越低越连续
    },
    "insights": [
      {
        "type": "positive",
        "title": "编码时长稳定",
        "description": "本周编码时长较上周增长 3.2%，继续保持良好状态。"
      },
      {
        "type": "warning",
        "title": "深夜编码较多",
        "description": "22:00-24:00 时段编码占比 18%，建议关注作息健康。"
      }
    ],
    "generatedAt": "2026-05-25 08:00:00"
  }
}
```

#### 3.4.2 工时预估准确度评估

```json
// POST /ai/insight/estimation-accuracy
// 业务调用方：绩效服务
// 触发时机：任务完成时 + 每日汇总

请求:
{
  "userId": 1,
  "projectId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-31"
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "summary": {
      "totalTasks": 12,
      "avgDeviation": -18.5,  // 负数表示低估百分比
      "avgDeviationHours": -1.5,
      "mostAccurateType": "task",
      "leastAccurateType": "bug"
    },
    "byTaskType": [
      {
        "taskType": "story",
        "count": 5,
        "avgEstimated": 6.0,
        "avgActual": 7.2,
        "avgDeviation": 20.0,
        "accuracy": "fair"  // excellent (>90%) / good (>80%) / fair (>70%) / poor
      },
      {
        "taskType": "bug",
        "count": 4,
        "avgEstimated": 2.0,
        "avgActual": 4.5,
        "avgDeviation": 125.0,
        "accuracy": "poor"
      }
    ],
    "patterns": [
      {
        "type": "bug_underestimation",
        "description": "Bug 类任务平均低估 125%，建议预估时增加风险缓冲。",
        "suggestion": "Bug 类任务建议在原预估基础上 ×1.8 系数。"
      }
    ],
    "suggestions": [
      "优化类任务预估相对准确，继续保持。",
      "Bug 类任务建议增加 50%-100% 的缓冲时间。",
      "复杂任务拆分为子任务后预估更准确。"
    ]
  }
}
```

#### 3.4.3 个人发展建议

```json
// POST /ai/insight/personal-development
// 业务调用方：绩效服务
// 触发时机：用户查看绩效时 + 每周自动推送

请求:
{
  "userId": 1,
  "period": "monthly"  // weekly / monthly / quarterly
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "period": "monthly",
    "overallScore": 85,
    "dimensions": [
      {
        "dimension": "coding_activity",
        "score": 90,
        "description": "编码活跃度高于团队平均水平",
        "vsTeamAvg": 11.6
      },
      {
        "dimension": "task_completion",
        "score": 78,
        "description": "任务完成率良好，但延期率略高",
        "vsTeamAvg": -3.2
      },
      {
        "dimension": "code_quality",
        "score": 88,
        "description": "AI 代码审查问题率低于平均水平",
        "vsTeamAvg": 5.0
      }
    ],
    "suggestions": [
      {
        "priority": "high",
        "category": "work_style",
        "title": "优化工作时段",
        "description": "你的高效工作时段集中在上午，建议将复杂任务安排在此时段。"
      },
      {
        "priority": "medium",
        "category": "skill",
        "title": "加强测试覆盖",
        "description": "建议为新功能编写单元测试，参考团队测试覆盖率目标 80%。"
      }
    ],
    "learningResources": [
      {
        "title": "如何准确评估任务工时",
        "url": "https://example.com/estimation-guide",
        "reason": "基于你的工时预估偏差模式推荐"
      }
    ],
    "generatedAt": "2026-05-25 08:00:00"
  }
}
```

#### 3.4.4 团队状态报告

```json
// POST /ai/insight/team-report
// 业务调用方：绩效服务
// 触发时机：每日定时生成 + 管理请求
// 调用方：绩效服务

请求:
{
  "projectId": 1,
  "period": "weekly",  // daily / weekly / sprint
  "receivers": ["project_manager", "tech_lead"],
  "channels": ["in_app", "email"]
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "projectId": 1,
    "period": {
      "startDate": "2026-05-18",
      "endDate": "2026-05-25",
      "type": "weekly"
    },
    "healthScore": 82,
    "summary": {
      "totalCommits": 234,
      "totalTasksCompleted": 45,
      "totalCodingHours": 892,
      "avgDailyCodingHours": 127.4
    },
    "activityTrend": {
      "direction": "stable",
      "change": 2.3
    },
    "topPerformers": [
      {
        "rank": 1,
        "userId": 1,
        "username": "张三",
        "score": 95,
        "highlights": ["任务完成率 100%", "编码活跃度高于平均 20%"]
      }
    ],
    "riskSignals": [
      {
        "severity": "medium",
        "type": "workload_imbalance",
        "title": "工作负荷不均",
        "description": "张三编码时长是赵六的 2.3 倍，建议关注任务分配均衡性。",
        "affectedUsers": [1, 6],
        "suggestion": "建议项目经理重新评估任务分配。"
      },
      {
        "severity": "low",
        "type": "declining_activity",
        "title": "活跃度下降",
        "description": "李四本周编码时长较上周下降 35%。",
        "affectedUsers": [2],
        "suggestion": "建议私下了解情况，排除项目或个人因素。"
      }
    ],
    "recommendations": [
      {
        "priority": 1,
        "title": "优化迭代任务分配",
        "description": "当前迭代任务分布不均，建议在下次迭代计划时均衡分配。"
      }
    ],
    "generatedAt": "2026-05-25 08:00:00"
  }
}
```

#### 3.4.5 项目风险预警

```json
// POST /ai/insight/project-risk
// 业务调用方：绩效服务
// 触发时机：每日定时 + 关键指标变化时

请求:
{
  "projectId": 1,
  "lookbackDays": 14
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "projectId": 1,
    "overallRisk": "medium",  // low / medium / high / critical
    "riskScore": 65,
    "risks": [
      {
        "riskId": "R001",
        "severity": "high",
        "type": "schedule_risk",
        "title": "迭代进度滞后",
        "description": "当前迭代完成率 42%，按此进度，到周五仅能完成 65% 的任务。",
        "evidence": {
          "plannedCompletionRate": 100,
          "currentCompletionRate": 42,
          "remainingDays": 3,
          "tasksRemaining": 12,
          "avgTasksPerDay": 2.1
        },
        "impact": "可能导致里程碑延期 2 天。",
        "suggestions": [
          "识别阻塞任务，优先解决。",
          "考虑缩减部分低优先级任务。",
          "增加资源或延长迭代时间。"
        ],
        "notifyTo": ["project_manager"]
      },
      {
        "riskId": "R002",
        "severity": "medium",
        "type": "member_fatigue",
        "title": "成员疲劳信号",
        "description": "张三连续两周编码时长超过团队平均 50%，且周末仍有编码记录。",
        "affectedUsers": [1],
        "suggestions": [
          "关注成员工作负荷，适时调整任务分配。",
          "鼓励适当休息，避免 burnout。"
        ]
      }
    ],
    "generatedAt": "2026-05-25 08:00:00"
  }
}
```

### 3.5 AI 代码审查 API 设计

#### 3.5.1 触发代码审查

```json
// POST /ai/code-review
// 业务调用方：代码服务（MR 创建时通过内部事件触发）
// 触发时机：MR 创建 Webhook → 代码服务 → 调用 AI 服务

请求:
{
  "mrId": 45,
  "repositoryId": 1,
  "diff": "diff --git a/src/main/java/com/lest/auth/UserDao.java...",
  "files": [
    {
      "path": "src/main/java/com/lest/auth/UserDao.java",
      "changeType": "modified",
      "additions": 50,
      "deletions": 10
    }
  ]
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "reviewId": "review-xxx",
    "mrId": 45,
    "status": "completed",
    "score": 7.5,
    "summary": "代码整体质量良好，存在 2 个需要注意的问题。",
    "issues": [
      {
        "severity": "high",
        "type": "security",
        "file": "src/main/java/com/lest/auth/UserDao.java",
        "line": 45,
        "description": "SQL 注入风险：直接使用用户输入拼接 SQL 查询。",
        "suggestion": "使用预编译语句（PreparedStatement）。"
      },
      {
        "severity": "medium",
        "type": "performance",
        "file": "src/main/java/com/lest/task/TaskService.java",
        "line": 120,
        "description": "N+1 查询问题：循环中执行数据库查询。",
        "suggestion": "使用批量查询或 JOIN FETCH。"
      }
    ],
    "stats": {
      "filesReviewed": 5,
      "linesAdded": 234,
      "linesDeleted": 56,
      "issuesFound": 3,
      "highSeverity": 1,
      "mediumSeverity": 2,
      "lowSeverity": 0
    },
    "generatedAt": "2026-05-25 10:00:00"
  }
}
```

### 3.6 AI 任务分析 API 设计

#### 3.6.1 任务分析

```json
// POST /ai/task/analyze
// 业务调用方：任务服务（任务创建/编辑时调用）

请求:
{
  "taskId": 123,
  "title": "实现用户登录功能",
  "description": "实现用户的登录功能，包括用户名密码登录、图形验证码、Token 签发。",
  "taskType": "task",
  "projectId": 1,
  "assignerId": 1
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": 123,
    "analysis": {
      "complexity": "medium",  // low / medium / high
      "estimatedHours": 6.5,
      "estimatedHoursBasis": "基于历史相似任务分析，建议范围 5-8 小时。",
      "keyPoints": [
        "需要对接现有 JWT Token 方案",
        "图形验证码需考虑 Redis 存储",
        "登录失败锁定机制需设计"
      ],
      "risks": [
        {
          "type": "integration",
          "description": "涉及多个子系统（认证、Token），需提前确认接口规范。"
        }
      ],
      "similarTasks": [
        {
          "taskId": 45,
          "title": "实现用户注册功能",
          "estimatedHours": 5,
          "actualHours": 6,
          "similarity": 0.85
        }
      ],
      "descriptionQuality": {
        "score": 85,
        "issues": [],
        "suggestions": [
          "建议补充验收标准，明确登录成功和失败的判断条件。"
        ]
      }
    },
    "generatedAt": "2026-05-25 10:00:00"
  }
}
```

### 3.7 AI 会议总结 API 设计

#### 3.7.1 生成会议总结

```json
// POST /ai/meeting/summary
// 业务调用方：会议服务（会议结束时调用）

请求:
{
  "meetingId": 1,
  "title": "Sprint 计划会",
  "transcript": "会议原始记录文本...",
  "participants": ["张三", "李四", "王五", "赵六", "钱七", "孙八"],
  "duration": 90
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "meetingId": 1,
    "summary": {
      "type": "Sprint 计划会",
      "participants": 6,
      "duration": 90
    },
    "keyPoints": [
      "确认了用户认证模块包含登录、注册、密码找回三个功能。",
      "讨论决定采用 JWT Token 方案。",
      "确认使用阿里云短信服务进行验证码发送。"
    ],
    "decisions": [
      {
        "content": "决定采用 JWT Token 作为认证方案。",
        "decidedBy": "张三",
        "confirmedAt": "10:15"
      },
      {
        "content": "决定使用阿里云短信服务。",
        "decidedBy": "李四",
        "confirmedAt": "10:25"
      }
    ],
    "actionItems": [
      {
        "content": "联系阿里云获取短信 API 文档",
        "assignee": "张三",
        "dueDate": "2026-05-26",
        "priority": "high",
        "confidence": 0.9
      },
      {
        "content": "设计 JWT Token 接口规范",
        "assignee": "李四",
        "dueDate": "2026-05-26",
        "priority": "high",
        "confidence": 0.95
      }
    ],
    "sprintPlan": {
      "goal": "完成用户认证模块开发",
      "taskCount": 12,
      "estimatedHours": 48,
      "tasks": [
        {
          "title": "实现用户登录功能",
          "estimatedHours": 8,
          "priority": "high"
        }
      ]
    },
    "generatedAt": "2026-05-25 10:30:00"
  }
}
```

### 3.8 AI Provider 扩展点设计

#### 3.8.1 TypeScript SDK 接口

```typescript
// AI Provider 插件接口
export interface AIAIProviderExtension extends PluginExtension {
    readonly type: 'ai-provider';

    metadata: {
        id: string;
        name: string;
        description: string;
        supportedModels: string[];
        capabilities: string[];  // chat / code_review / analysis / summarize
    };

    authConfig: {
        type: 'api-key' | 'oauth' | 'basic' | 'custom';
        fields: AuthField[];
    };

    modelConfig: {
        defaultModel?: string;
        models: ModelDefinition[];
    };

    chat(request: AIChatRequest): Promise<AIChatResponse>;
    chatStream?(request: AIChatRequest): AsyncIterable<AIStreamChunk>;
}
```

#### 3.8.2 Java SDK 接口

```java
// Java SDK - AI Provider 插件接口
public interface AIProviderPlugin extends Extension {
    AIProviderMetadata getMetadata();
    AuthConfig getAuthConfig();
    List<ModelDefinition> getModels();
    AIChatResponse chat(AIChatRequest request);
    default Flux<AIStreamChunk> chatStream(AIChatRequest request) {
        throw new UnsupportedOperationException();
    }
    default boolean testConnection(AuthConfig config) { return true; }
}
```

### 3.9 页面原型

#### 3.9.1 AI 绩效洞察页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ AI 绩效洞察                                      [刷新]  [导出]  [订阅报告]  │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  本周编码活跃度                                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 周一 ████████                                                        │  │
│  │ 周二 ████████████                                                    │  │
│  │ 周三 ██████████████                                                    │  │
│  │ 周四 ██████████                                                      │  │
│  │ 周五 ██████████████████████                                          │  │
│  │ 周六 ████                                                            │  │
│  │ 周日 ██                                                              │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│  本周总时长: 32.5h  日均: 4.6h  团队平均: 28.1h  高于平均 15.6%           │
│                                                                            │
├────────────────────────────────────────────────────────────────────────────┤
│  工作曲线分析                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 类型: 早起型 · 高效时段: 9:00-12:00 · 碎片化指数: 0.18 (良好)      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
├────────────────────────────────────────────────────────────────────────────┤
│  AI 洞察                                                                  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 🟢 编码时长稳定                                                      │  │
│  │    本周编码时长较上周增长 3.2%，继续保持良好状态。                   │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 🟡 深夜编码较多                                                      │  │
│  │    22:00-24:00 时段编码占比 18%，建议关注作息健康。                  │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
├────────────────────────────────────────────────────────────────────────────┤
│  工时预估准确度                                                            │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 本月完成任务: 12 个  平均偏差: -18.5% (低估)                        │  │
│  │ 预估最准确类型: 需求    预估偏差最大类型: Bug                        │  │
│  │ Bug 类任务建议增加 50%-100% 的缓冲时间。                              │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.9.2 团队健康度报告页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 团队健康度报告 · 2026年5月第三周                    [发送到邮件]  [推送到钉钉]  │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  团队健康度评分: 82/100  🟢 良好                                          │
│                                                                            │
├────────────────────────────────────────────────────────────────────────────┤
│  概览                                                                    │
│  ┌─────────────┬─────────────┬─────────────┬─────────────┐               │
│  │ 总提交      │ 完成任务    │ 编码时长    │ 团队日均   │               │
│  │   234      │    45      │   892h     │   127h    │               │
│  │  ↑ 12%    │  ↑ 8%     │  ↓ 3%     │  ↓ 2%    │               │
│  └─────────────┴─────────────┴─────────────┴─────────────┘               │
│                                                                            │
├────────────────────────────────────────────────────────────────────────────┤
│  风险预警 ⚠️                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 🔴 中等优先级: 工作负荷不均                                         │  │
│  │ 张三编码时长是赵六的 2.3 倍，建议关注任务分配均衡性。                │  │
│  │ 建议: 建议项目经理重新评估任务分配。                                │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 🟡 低优先级: 活跃度下降                                             │  │
│  │ 李四本周编码时长较上周下降 35%。                                    │  │
│  │ 建议: 建议私下了解情况，排除项目或个人因素。                        │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
├────────────────────────────────────────────────────────────────────────────┤
│  改进建议                                                                  │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │ 1. 优化迭代任务分配：当前迭代任务分布不均，建议下次迭代均衡分配。    │  │
│  │ 2. 关注成员健康：建议定期了解成员工作状态，避免疲劳累积。           │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 接口设计

### 4.1 AI 网关接口（内部调用）

#### 4.1.1 统一聊天接口

```
POST /ai/chat
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "scene": "code_review",  // code_review / task_analysis / meeting_summary / performance_insight
  "model": "qwen-turbo",
  "messages": [
    { "role": "system", "content": "你是一位资深代码审查专家..." },
    { "role": "user", "content": "请审查以下代码..." }
  ],
  "temperature": 0.7,
  "maxTokens": 2000,
  "metadata": {
    "requestId": "req-xxx",
    "caller": "lest-code",  // 调用方服务名
    "userId": 1
  }
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "chat-xxx",
    "model": "qwen-turbo",
    "usage": {
      "promptTokens": 100,
      "completionTokens": 200,
      "totalTokens": 300
    },
    "choices": [
      {
        "message": {
          "role": "assistant",
          "content": "审查结果..."
        }
      }
    ],
    "latency": 1234
  }
}
```

### 4.2 业务能力接口

#### 4.2.1 编码活跃度分析

```
POST /ai/insight/coding-activity
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "userId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-31",
  "includeTeamAvg": true
}

响应: 见 3.4.1
```

#### 4.2.2 工时预估准确度评估

```
POST /ai/insight/estimation-accuracy
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "userId": 1,
  "projectId": 1,
  "startDate": "2026-05-01",
  "endDate": "2026-05-31"
}

响应: 见 3.4.2
```

#### 4.2.3 个人发展建议

```
POST /ai/insight/personal-development
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "userId": 1,
  "period": "monthly"
}

响应: 见 3.4.3
```

#### 4.2.4 团队状态报告

```
POST /ai/insight/team-report
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "projectId": 1,
  "period": "weekly",
  "receivers": ["project_manager"],
  "channels": ["in_app", "email"]
}

响应: 见 3.4.4
```

#### 4.2.5 项目风险预警

```
POST /ai/insight/project-risk
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "projectId": 1,
  "lookbackDays": 14
}

响应: 见 3.4.5
```

#### 4.2.6 代码审查

```
POST /ai/code-review
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "mrId": 45,
  "repositoryId": 1,
  "diff": "...",
  "files": [...]
}

响应: 见 3.5.1
```

#### 4.2.7 任务分析

```
POST /ai/task/analyze
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "taskId": 123,
  "title": "实现用户登录功能",
  "description": "...",
  "taskType": "task",
  "projectId": 1
}

响应: 见 3.6.1
```

#### 4.2.8 会议总结

```
POST /ai/meeting/summary
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "meetingId": 1,
  "title": "Sprint 计划会",
  "transcript": "...",
  "participants": ["张三", "李四"],
  "duration": 90
}

响应: 见 3.7.1
```

### 4.3 管理接口（系统设置模块调用）

#### 4.3.1 获取 AI 模型列表

```
GET /ai/model
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "通义千问",
      "modelType": "qwen",
      "defaultModel": "qwen-turbo",
      "enabled": true,
      "priority": 1
    }
  ]
}
```

#### 4.3.2 配置 AI 模型

```
POST /ai/model
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "name": "通义千问",
  "modelType": "qwen",
  "apiUrl": "https://dashscope.aliyuncs.com",
  "apiKey": "sk-xxx",
  "defaultModel": "qwen-turbo",
  "enabled": true,
  "priority": 1
}

响应:
{
  "code": 200,
  "data": { "id": 1 }
}
```

#### 4.3.3 AI 使用统计

```
GET /ai/usage-stats?startDate=2026-05-01&endDate=2026-05-31
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "data": {
    "totalCalls": 1234,
    "totalTokens": 567890,
    "costEstimate": 56.78,
    "byModel": {
      "qwen": { "calls": 800, "tokens": 300000, "cost": 30.0 },
      "kimi": { "calls": 300, "tokens": 150000, "cost": 15.0 }
    },
    "byScene": {
      "code_review": { "calls": 200, "tokens": 200000 },
      "performance_insight": { "calls": 500, "tokens": 250000 }
    }
  }
}
```

### 4.4 内部事件接口

#### 4.4.1 任务完成事件触发 AI 分析

```
// 任务服务 → 通知服务 → AI 服务
// 事件: TASK_COMPLETED

POST /ai/event/task-completed
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "event": "TASK_COMPLETED",
  "taskId": 123,
  "userId": 1,
  "projectId": 1,
  "taskType": "task",
  "estimatedHours": 8.0,
  "actualHours": 0,  // 待 WakaTime 汇总后更新
  "completedAt": "2026-05-25 14:00:00"
}

说明: 触发 AI 工时预估评估分析，结果存入 ai_estimate_accuracy 表
```

#### 4.4.2 每日定时事件触发 AI 洞察

```
// 调度服务 → AI 服务
// 事件: DAILY_INSIGHT_GENERATION

POST /ai/event/daily-insight
X-Internal-Token: {内部调用Token}
Content-Type: application/json

请求:
{
  "event": "DAILY_INSIGHT_GENERATION",
  "date": "2026-05-25",
  "scope": "all"  // all / project / user
}

说明: 触发编码活跃度分析、个人发展建议生成
结果通过通知服务推送给用户
```

---

## 5. 数据库设计

### 5.1 表结构

#### ai_model_config AI 模型配置表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(64) | NOT NULL | 模型名称 |
| model_type | VARCHAR(32) | NOT NULL | 模型类型 |
| api_url | VARCHAR(256) | | API 地址 |
| api_key | VARCHAR(512) | | 加密存储的 API Key |
| default_model | VARCHAR(64) | | 默认模型 |
| capabilities | JSON | | 支持的能力列表 |
| enabled | TINYINT | DEFAULT 1 | 启用状态 |
| priority | INT | DEFAULT 100 | 优先级 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### ai_prompt_template AI 提示词模板表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| scene | VARCHAR(32) | NOT NULL | 场景代码 |
| name | VARCHAR(128) | NOT NULL | 模板名称 |
| description | VARCHAR(256) | | 描述 |
| template | TEXT | NOT NULL | 提示词模板 |
| variables | JSON | | 变量定义 |
| model_type | VARCHAR(32) | | 适用模型类型 |
| enabled | TINYINT | DEFAULT 1 | 启用状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| uk_scene | | UNIQUE | scene 唯一索引 |

#### ai_conversation AI 对话记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| caller | VARCHAR(32) | NOT NULL | 调用方服务 |
| scene | VARCHAR(32) | | 场景 |
| model_type | VARCHAR(32) | | 模型类型 |
| model | VARCHAR(64) | | 具体模型 |
| messages | JSON | | 对话历史 |
| request_tokens | INT | | 请求 Token 数 |
| response_tokens | INT | | 响应 Token 数 |
| cost | DECIMAL(10,4) | | 估算成本 |
| duration_ms | INT | | 耗时（毫秒） |
| status | VARCHAR(16) | | 状态 |
| error_message | VARCHAR(512) | | 错误信息 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| idx_caller | | | 调用方索引 |
| idx_scene | | | 场景索引 |

#### ai_code_review AI 代码审查记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| mr_id | BIGINT | NOT NULL | MR ID |
| repository_id | BIGINT | NOT NULL | 仓库 ID |
| score | DECIMAL(3,1) | | 综合评分 |
| summary | TEXT | | 总体评价 |
| issues | JSON | | 问题列表 |
| stats | JSON | | 统计信息 |
| status | VARCHAR(16) | | 状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_mr_id | | | MR ID 索引 |

#### ai_meeting_summary AI 会议总结记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| meeting_id | BIGINT | NOT NULL | 会议 ID |
| summary | JSON | | 会议总结 |
| key_points | JSON | | 关键讨论点 |
| decisions | JSON | | 会议决议 |
| action_items | JSON | | 行动项 |
| sprint_plan | JSON | | 迭代计划 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_meeting_id | | UNIQUE | 会议 ID 唯一索引 |

#### ai_perf_insight AI 绩效洞察记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| insight_type | VARCHAR(32) | NOT NULL | 洞察类型 |
| target_type | VARCHAR(16) | NOT NULL | 目标类型：user / project / team |
| target_id | BIGINT | NOT NULL | 目标 ID |
| period_start | DATE | NOT NULL | 统计周期开始 |
| period_end | DATE | NOT NULL | 统计周期结束 |
| period_type | VARCHAR(16) | NOT NULL | 周期类型：daily / weekly / monthly |
| data | JSON | | 洞察数据 |
| insights | JSON | | AI 生成的洞察文字 |
| suggestions | JSON | | 改进建议 |
| generated_by | VARCHAR(32) | | 生成模型 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| uk_type_target_period | | UNIQUE | (insight_type, target_type, target_id, period_start) |
| idx_target | | | 目标索引 |

#### ai_estimate_accuracy AI 工时预估准确度记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| task_id | BIGINT | NOT NULL | 任务 ID |
| user_id | BIGINT | NOT NULL | 用户 ID |
| project_id | BIGINT | NOT NULL | 项目 ID |
| task_type | VARCHAR(16) | NOT NULL | 任务类型 |
| estimated_hours | DECIMAL(5,1) | NOT NULL | 预估工时 |
| actual_hours | DECIMAL(5,1) | | 实际工时（来自 WakaTime） |
| deviation_percent | DECIMAL(6,2) | | 偏差百分比 |
| deviation_hours | DECIMAL(5,1) | | 偏差小时数 |
| completed_at | DATETIME | | 任务完成时间 |
| accuracy_data | JSON | | 准确度分析详情 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_user_project | | | (user_id, project_id) 索引 |
| idx_task_id | | | 任务 ID 索引 |

#### ai_project_risk AI 项目风险预警记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| risk_id | VARCHAR(32) | NOT NULL | 风险 ID |
| project_id | BIGINT | NOT NULL | 项目 ID |
| risk_type | VARCHAR(32) | NOT NULL | 风险类型 |
| severity | VARCHAR(16) | NOT NULL | 严重程度 |
| title | VARCHAR(256) | NOT NULL | 风险标题 |
| description | TEXT | | 风险描述 |
| evidence | JSON | | 证据数据 |
| suggestions | JSON | | 建议措施 |
| status | VARCHAR(16) | DEFAULT 'active' | 状态 |
| notified_to | JSON | | 已通知对象 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| resolved_at | DATETIME | | 解决时间 |
| **索引** | | | |
| idx_project_status | | | (project_id, status) 索引 |

---

## 6. 跨模块调用关系

### 6.1 AI 服务被调用的场景

| 调用方 | 触发时机 | AI 能力 | 返回结果处理 |
|--------|---------|---------|-------------|
| **绩效服务** | 用户查看绩效页 | 编码活跃度分析 | 展示在绩效看板 |
| **绩效服务** | 每日定时 | 个人发展建议 | 推送通知 |
| **绩效服务** | 每周定时 | 团队状态报告 | 推送给项目经理 |
| **绩效服务** | 每日定时 | 项目风险预警 | 推送给项目经理 |
| **代码服务** | MR 创建 Webhook | 代码审查 | 展示在 MR 页面 + 通知 |
| **任务服务** | 任务创建/编辑 | 任务分析 | 展示在任务详情 |
| **会议服务** | 会议结束 | 会议总结 | 展示在会议纪要 |

### 6.2 事件驱动架构

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           事件驱动调用架构                                │
└──────────────────────────────────────────────────────────────────────────┘

各业务服务
    │
    │ 1. 发送事件到 Kafka
    │
    ▼
通知服务（Kafka Consumer）
    │
    │ 2. 根据事件类型路由
    │
    ├──► 发送通知
    │
    └──► 3. 需要 AI 处理？
            │
            ├── Yes ──► AI 服务（HTTP 同步调用）
            │                │
            │                │ 4. AI 分析结果
            │                │
            │                ▼
            │         通知服务
            │                │
            │                │ 5. 推送通知
            │                ▼
            │         用户
            │
            └──► No ──► 发送通知
```

### 6.3 Kafka 事件定义

| Topic | 事件类型 | 触发方 | 消费方 | AI 处理 |
|-------|---------|--------|--------|---------|
| lest.notification.event | TASK_COMPLETED | 任务服务 | 通知服务 | 工时预估评估 |
| lest.notification.event | MR_CREATED | 代码服务 | 通知服务 | 代码审查 |
| lest.notification.event | MEETING_ENDED | 会议服务 | 通知服务 | 会议总结 |
| lest.notification.event | DAILY_REPORT | 调度服务 | 通知服务 | 编码活跃度、个人发展建议 |
| lest.notification.event | WEEKLY_REPORT | 调度服务 | 通知服务 | 团队状态报告 |
| lest.notification.event | PROJECT_RISK_CHECK | 调度服务 | 通知服务 | 项目风险预警 |

---

## 7. 验收标准

| 用例 | 验收标准 |
|------|---------|
| UC-001 | 绩效服务可调用编码活跃度分析 API，返回包含趋势、工作曲线、洞察的数据 |
| UC-002 | 绩效服务可调用工时预估评估 API，正确计算预估偏差率和模式分析 |
| UC-003 | 绩效服务可调用个人发展建议 API，返回多维度评分和改进建议 |
| UC-004 | 绩效服务可调用团队状态报告 API，生成包含风险预警的综合报告 |
| UC-005 | 代码服务可调用 AI 代码审查 API，MR 创建时自动触发审查 |
| UC-006 | AI 审查结果正确显示问题列表、严重程度和修改建议 |
| UC-007 | 任务服务可调用任务分析 API，给出复杂度评估和工时估算建议 |
| UC-008 | 会议服务可调用会议总结 API，生成关键讨论点、决议和行动项 |
| UC-009 | 可配置多个 AI 模型，设置优先级和默认模型 |
| UC-010 | AI 使用统计正确记录调用量、Token 消耗和成本估算 |
| UC-011 | AI 服务不可用时，业务功能降级不影响主流程 |
| UC-012 | 通过插件系统可接入自定义 AI Provider |

---

## 8. 错误码

### 8.1 AI 模块错误码 (16000-16999)

| 错误码 | 枚举常量 | HTTP 状态码 | 说明 |
|---------|----------|-------------|------|
| 16000 | `MODEL_NOT_FOUND` | 404 | AI 模型不存在 |
| 16001 | `MODEL_CONFIG_INVALID` | 400 | 模型配置无效 |
| 16002 | `MODEL_API_ERROR` | 502 | 模型 API 调用失败 |
| 16003 | `MODEL_RATE_LIMIT` | 429 | 模型调用频率限制 |
| 16004 | `MODEL_QUOTA_EXCEEDED` | 429 | 模型配额超限 |
| 16005 | `REVIEW_NOT_FOUND` | 404 | 审查记录不存在 |
| 16006 | `SUMMARY_NOT_FOUND` | 404 | 总结记录不存在 |
| 16007 | `AI_SERVICE_UNAVAILABLE` | 503 | AI 服务不可用 |
| 16008 | `ALL_MODELS_FAILED` | 503 | 所有模型均调用失败 |
| 16009 | `AI_REQUEST_TIMEOUT` | 504 | AI 请求超时 |
| 16010 | `AI_RESULT_PARSE_ERROR` | 500 | AI 结果解析失败 |
| 16011 | `INSIGHT_NOT_READY` | 503 | 洞察数据尚未生成 |
| 16012 | `INSUFFICIENT_DATA` | 400 | 数据不足，无法生成洞察 |

---

## 9. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
| V1.1 | 2026-05-25 | 新增 AI Provider 扩展点、AI Workflow 扩展点 | - |
| V2.0 | 2026-05-25 | 全面重构：AI 从独立模块改为中央智能引擎；新增 AI 绩效洞察体系（编码活跃度、工时预估、个人发展、团队报告、风险预警）；定义完整的跨模块调用关系和事件驱动架构；纳管绩效数据表设计 | - |
