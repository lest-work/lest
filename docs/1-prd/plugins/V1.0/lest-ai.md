# AI 服务 PRD
> **📌 Jira 映射**：Atlassian Intelligence（AI-Native 智能引擎）
>
> **功能定位**：AI 网关、代码审查、任务助手、会议总结、绩效洞察。
>
> **📌 注意**：V1.0 为 AI 服务基础版，V2.0（`../core/V2.0/AI服务.md`）为 AI-Native 重构版，中央智能引擎架构。


>
> **功能定位**：AI 网关、代码审查、任务助手、会议总结、绩效洞察


## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-25 |
| 最后更新 | 2026-05-25 |

> **重要说明**：AI 服务模块深度依赖 **插件系统** 和 **开放平台** 模块。AI Provider 以插件形式接入，AI 能力通过开放平台 API 对外暴露。开发此模块前，请先完成 `插件系统.md` 和 `开放平台.md` 的设计。

---

## 1. 概述

### 1.1 功能背景

AI 服务是 LEST Platform 实现智能化团队管理的核心模块。作为开源的 AI-Native 敏捷管理平台，我们深度集成 AI 能力，为团队提供智能辅助决策、自动化工作流程和个性化体验。

### 1.2 功能目标

- 提供统一的 AI 网关，支持多模型接入（通义千问、Kimi、OpenAI GPT 等）
- 提供 AI 代码审查，辅助开发者提升代码质量
- 提供 AI 任务助手，自动分析任务描述、建议实现方案
- 提供 AI 会议助手，自动生成会议纪要和行动项
- 提供 AI 绩效分析，辅助管理者评估团队效能
- 支持 AI 工作流编排，实现复杂任务自动化
- 支持本地部署模型（Llama 等）
- 提供 AI 使用分析和成本控制

### 1.3 目标用户

- **开发人员**：使用 AI 代码审查、AI 任务助手
- **项目经理**：使用 AI 绩效分析、AI 会议助手
- **Scrum Master**：使用 AI 团队分析
- **平台管理员**：管理 AI 模型配置和成本控制

---

## 2. 用户故事

### 2.1 AI 网关

#### US-001: 配置 AI 模型
**作为** 平台管理员，**我希望** 配置 AI 模型连接，**以便** 平台能够调用 AI 能力。

验收标准：
- [ ] 支持配置通义千问 API
- [ ] 支持配置 Kimi API
- [ ] 支持配置 OpenAI GPT API
- [ ] 支持配置本地部署模型（Ollama）
- [ ] 支持设置模型优先级
- [ ] 支持设置默认模型
- [ ] 支持测试连接

#### US-002: AI 调用路由
**作为** 开发者，**我希望** 通过统一接口调用 AI，**以便** 无关心里层实现细节。

验收标准：
- [ ] 提供统一的 AI 调用接口
- [ ] 支持模型自动路由
- [ ] 支持请求重试和降级
- [ ] 支持调用日志记录
- [ ] 支持调用量统计

### 2.2 AI 代码审查

#### US-003: 自动代码审查
**作为** 开发人员，**我希望** 提交 MR 后自动进行 AI 代码审查，**以便** 获得代码质量反馈。

验收标准：
- [ ] MR 创建时自动触发审查
- [ ] 分析代码规范遵循情况
- [ ] 识别潜在 Bug 和安全问题
- [ ] 提供代码优化建议
- [ ] 评估代码复杂度
- [ ] 生成审查报告
- [ ] 支持在 MR 页面展示审查结果

#### US-004: 审查历史和统计
**作为** 管理者，**我希望** 查看 AI 审查统计，**以便** 了解代码质量趋势。

验收标准：
- [ ] 显示审查次数统计
- [ ] 显示问题发现率
- [ ] 显示建议采纳率
- [ ] 支持按仓库/成员筛选

### 2.3 AI 任务助手

#### US-005: 任务分析
**作为** 开发人员，**我希望** 创建任务时获得 AI 分析，**以便** 更好地理解和实现任务。

验收标准：
- [ ] 分析任务描述的完整性和清晰度
- [ ] 识别任务的关键要点
- [ ] 建议任务的技术方案
- [ ] 预估任务复杂度
- [ ] 建议关联的任务或依赖

#### US-006: 任务实现辅助
**作为** 开发人员，**我希望** 在实现任务时获得 AI 辅助，**以便** 提升开发效率。

验收标准：
- [ ] 根据任务描述生成代码框架
- [ ] 提供实现代码示例
- [ ] 解释代码逻辑
- [ ] 回答技术问题

### 2.4 AI 会议助手

#### US-007: 会议总结生成
**作为** 记录人，**我希望** AI 自动生成会议总结，**以便** 减少记录工作量。

验收标准：
- [ ] 识别会议关键讨论点
- [ ] 生成会议纪要草稿
- [ ] 提取会议决议
- [ ] 生成行动项建议
- [ ] 支持编辑和确认

#### US-008: 会议待办追踪
**作为** 参与者，**我希望** 查看 AI 提取的会议待办，**以便** 跟踪执行情况。

验收标准：
- [ ] 显示 AI 提取的行动项
- [ ] 支持一键创建任务
- [ ] 支持设置截止日期
- [ ] 支持 @提及责任人

### 2.5 AI 绩效分析

#### US-009: 绩效趋势分析
**作为** 管理者，**我希望** 获得 AI 绩效分析，**以便** 了解团队状态。

验收标准：
- [ ] 分析团队工作趋势
- [ ] 识别潜在风险（如工作负荷不均）
- [ ] 预测项目交付风险
- [ ] 提供改进建议

#### US-010: 个人发展建议
**作为** 团队成员，**我希望** 获得 AI 个人发展建议，**以便** 提升个人能力。

验收标准：
- [ ] 分析工作表现数据
- [ ] 识别技能短板
- [ ] 提供学习建议
- [ ] 推荐适合的任务类型

### 2.6 AI 工作流

#### US-011: 配置 AI 工作流
**作为** 平台管理员，**我希望** 配置 AI 工作流，**以便** 实现复杂任务的自动化。

验收标准：
- [ ] 支持可视化工作流编排
- [ ] 支持多步骤任务编排
- [ ] 支持条件分支
- [ ] 支持循环执行
- [ ] 支持工作流执行日志

#### US-012: 预置工作流
**作为** 用户，**我希望** 使用预置的 AI 工作流，**以便** 快速获得 AI 辅助。

验收标准：
- [ ] 提供代码审查工作流
- [ ] 提供任务分析工作流
- [ ] 提供会议总结工作流
- [ ] 支持自定义工作流

### 2.7 AI 使用分析

#### US-013: 查看 AI 使用统计
**作为** 管理员，**我希望** 查看 AI 使用统计，**以便** 控制 AI 成本。

验收标准：
- [ ] 显示 AI 调用次数统计
- [ ] 显示 Token 消耗统计
- [ ] 显示成本估算
- [ ] 显示各模型使用占比
- [ ] 支持按用户/团队统计
- [ ] 支持设置用量告警

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 优先级 |
|------|--------|---------|--------|
| 1 | AI 网关 | 多模型接入、路由、降级 | P0 |
| 2 | AI 模型配置 | 配置各模型 API | P0 |
| 3 | AI 代码审查 | 自动 MR 审查 | P0 |
| 4 | AI 任务分析 | 任务描述分析 | P1 |
| 5 | AI 会议总结 | 自动生成会议纪要 | P1 |
| 6 | AI 绩效分析 | 绩效趋势分析 | P2 |
| 7 | AI 工作流 | 可视化工作流编排 | P2 |
| 8 | AI 使用统计 | 用量统计和成本控制 | P1 |
| 9 | AI 本地模型 | 支持 Ollama 本地部署 | P2 |
| 10 | AI 提示词管理 | 维护各类提示词模板 | P1 |
| 11 | AI Provider 扩展点 | 允许通过插件接入自定义 AI Provider | P0 |
| 12 | AI Workflow 扩展点 | 允许通过插件扩展 AI 工作流节点 | P1 |

### 3.2 AI 模型配置

```java
public enum AIModelType {
    QWEN("通义千问", "qwen-turbo", "https://dashscope.aliyuncs.com"),
    KIMI("Kimi", "moonshot-v1-8k", "https://api.moonshot.cn"),
    GPT4("GPT-4", "gpt-4", "https://api.openai.com"),
    GPT35("GPT-3.5", "gpt-3.5-turbo", "https://api.openai.com"),
    OLLAMA("Ollama", "llama2", "http://localhost:11434");

    private final String name;
    private final String defaultModel;
    private final String baseUrl;
}

public interface AIModel {
    String getType();
    String getDefaultModel();
    AIResponse chat(List<ChatMessage> messages);
    AIResponse chat(String prompt);
}

public class ChatMessage {
    private String role;      // system / user / assistant
    private String content;
}
```

### 3.3 AI 提示词模板

```java
public class PromptTemplate {
    private String code;
    private String name;
    private String template;
    private AIModelType modelType;
    private Integer maxTokens;
    private Double temperature;
}

// 示例：代码审查提示词
public static final String CODE_REVIEW_PROMPT = """
    你是一位资深的代码审查专家。请审查以下代码变更：

    ## 变更文件
    {files}

    ## Diff 内容
    {diff}

    ## 审查要求
    1. 检查代码规范遵循情况
    2. 识别潜在的 Bug 和安全问题
    3. 提供代码优化建议
    4. 评估代码复杂度

    请以 JSON 格式返回审查结果：
    {
      "issues": [
        {
          "file": "文件路径",
          "line": 行号,
          "severity": "high/medium/low",
          "type": "bug/security/style/performance",
          "description": "问题描述",
          "suggestion": "修改建议"
        }
      ],
      "summary": "总体评价",
      "score": 1-10
    }
    """;
```

### 3.4 页面原型

#### 3.4.1 AI 代码审查结果页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ AI 代码审查 · MR #45                                                    │
├────────────────────────────────────────────────────────────────────────────┤
│ 综合评分: 8.5/10                                                          │
│ 问题数: 3 高 · 5 中 · 8 低                                               │
├────────────────────────────────────────────────────────────────────────────┤
│ 高严重性问题 (3)                                                          │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 🔴 安全: SQL 注入风险                                                   ││
│ │ 文件: src/main/java/com/lest/auth/UserDao.java                         ││
│ │ 行号: 45                                                               ││
│ │ 问题: 直接使用用户输入拼接 SQL 查询，存在 SQL 注入风险                   ││
│ │ 建议: 使用预编译语句 (PreparedStatement) 或 ORM 框架                   ││
│ │ ┌────────────────────────────────────────────────────────────────────┐││
│ │ │ // 修复前                                                           │││
│ │ │ String sql = "SELECT * FROM users WHERE name = '" + name + "'";   │││
│ │ │                                                                     │││
│ │ │ // 修复后                                                           │││
│ │ │ String sql = "SELECT * FROM users WHERE name = ?";                 │││
│ │ │ preparedStatement.setString(1, name);                             │││
│ │ └────────────────────────────────────────────────────────────────────┘││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 中等问题 (5)                                                               │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 🟡 性能: N+1 查询问题                                                   ││
│ │ 文件: src/main/java/com/lest/task/TaskService.java                    ││
│ │ 问题: 循环中执行数据库查询，建议使用批量查询                            ││
│ │ 建议: 使用 MyBatis 批量查询或 JPA JOIN FETCH                         ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 低严重性问题 (8)                                                           │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ ⚪ 风格: 命名不规范                                                     ││
│ │ 文件: src/main/java/com/lest/auth/LoginController.java                 ││
│ │ 问题: 变量名 a, b, c 不够语义化                                        ││
│ │ 建议: 使用有意义的变量名如 userId, password                            ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 总体评价                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 代码整体质量良好，主要问题集中在安全方面。请重点关注 SQL 注入问题，      ││
│ │ 其他问题可在后续迭代中逐步优化。                                        ││
│ └──────────────────────────────────────────────────────────────────────────┘│
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.4.2 AI 会议总结页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ AI 会议总结 · Sprint 计划会                                             │
├────────────────────────────────────────────────────────────────────────────┤
│ 生成时间: 2026-05-25 10:30:00                        [重新生成] [导出]    │
├────────────────────────────────────────────────────────────────────────────┤
│ 会议概要                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 会议类型: Sprint 计划会                                                  ││
│ │ 参与人数: 6 人                                                          ││
│ │ 会议时长: 90 分钟                                                        ││
│ │ 迭代目标: 完成用户认证模块开发                                           ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 关键讨论点                                                                  │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 1. 确认了用户认证模块包含登录、注册、密码找回三个功能                   ││
│ │ 2. 讨论决定采用 JWT Token 方案                                         ││
│ │ 3. 确认使用阿里云短信服务进行验证码发送                                 ││
│ │ 4. 前端采用 Vue 3 + Element Plus 实现                                  ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 会议决议                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ ✓ 决定采用 JWT Token 作为认证方案                                       ││
│ │ ✓ 决定使用阿里云短信服务                                                ││
│ │ ✓ 确定认证模块的 API 接口规范                                           ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 行动项 (AI 自动提取)                                                      │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ [创建任务] 张三 联系阿里云获取短信 API 文档            截止: 2026-05-26 ││
│ │ [创建任务] 李四 设计 JWT Token 接口规范              截止: 2026-05-26 ││
│ │ [创建任务] 王五 调研 Vue 3 认证组件库               截止: 2026-05-27 ││
│ └──────────────────────────────────────────────────────────────────────────┘│
├────────────────────────────────────────────────────────────────────────────┤
│ 迭代任务规划                                                                │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ Sprint 目标: 完成用户认证模块开发                                       ││
│ │ 规划任务数: 12 个                                                       ││
│ │ 预估工时: 48 人天                                                       ││
│ │                                                                          ││
│ │ 高优先级:                                                               ││
│ │ • 实现用户登录功能 (8h)                                                  ││
│ │ • 实现用户注册功能 (8h)                                                  ││
│ │ • 实现密码找回功能 (6h)                                                  ││
│ │                                                                          ││
│ │ 中优先级:                                                               ││
│ │ • 实现 Token 刷新机制 (4h)                                              ││
│ │ • 实现第三方登录 (8h)                                                    ││
│ └──────────────────────────────────────────────────────────────────────────┘│
└────────────────────────────────────────────────────────────────────────────┘
```

### 3.5 AI Provider 扩展点设计

作为 AI-Native 平台，AI 服务模块必须支持通过插件系统接入自定义 AI Provider。任何人都可以开发插件来添加新的 AI 模型支持。

#### 3.5.1 AI Provider 插件接口

```typescript
// TypeScript SDK - AI Provider 插件接口
import { PluginExtension, AIProvider, AIChatRequest, AIChatResponse } from '@lest/plugin-sdk';

// AI Provider 插件必须实现此接口
export interface AIAIProviderExtension extends PluginExtension {
    // 插件类型标识
    readonly type: 'ai-provider';

    // Provider 元数据
    metadata: {
        id: string;                    // Provider 唯一标识
        name: string;                   // Provider 名称，如 "通义千问"、"Kimi"
        description: string;            // 描述
        logo?: string;                  // Logo URL
        website?: string;               // 官网
        supportedModels: string[];      // 支持的模型列表
        capabilities: string[];         // 能力：chat, embedding, image 等
    };

    // 认证配置
    authConfig: {
        type: 'api-key' | 'oauth' | 'basic' | 'custom';
        fields: AuthField[];            // 需要的认证字段
    };

    // 模型配置
    modelConfig: {
        defaultModel?: string;          // 默认模型
        models: ModelDefinition[];      // 模型列表
    };

    // 聊天接口实现
    chat(request: AIChatRequest): Promise<AIChatResponse>;

    // 流式聊天接口（可选）
    chatStream?(request: AIChatRequest): AsyncIterable<AIStreamChunk>;
}

// 认证字段定义
interface AuthField {
    key: string;           // 字段标识
    label: string;          // 显示名称
    type: 'text' | 'password' | 'url';  // 字段类型
    required: boolean;
    placeholder?: string;
    helpText?: string;
}

// 模型定义
interface ModelDefinition {
    id: string;             // 模型 ID，如 "qwen-turbo"
    name: string;            // 模型名称
    description?: string;    // 模型描述
    maxTokens?: number;     // 最大 Token 数
    supportsStreaming?: boolean;  // 是否支持流式输出
    pricing?: {             // 价格信息
        input: number;      // 输入价格（每 1M Tokens）
        output: number;      // 输出价格
        currency: string;
    };
}
```

#### 3.5.2 Java SDK - AI Provider 插件接口

```java
// Java SDK - AI Provider 插件接口
package com.lest.plugin.sdk.ai;

// AI Provider 插件必须实现的接口
public interface AIProviderPlugin extends Extension {

    // 获取 Provider 元数据
    AIProviderMetadata getMetadata();

    // 获取认证配置
    AuthConfig getAuthConfig();

    // 获取模型列表
    List<ModelDefinition> getModels();

    // 聊天接口
    AIChatResponse chat(AIChatRequest request);

    // 流式聊天（可选）
    default Flux<AIStreamChunk> chatStream(AIChatRequest request) {
        throw new UnsupportedOperationException("流式聊天未实现");
    }

    // 测试连接
    default boolean testConnection(AuthConfig config) {
        return true;
    }
}

// Provider 元数据
public record AIProviderMetadata(
    String id,               // Provider 唯一标识
    String name,             // Provider 名称
    String description,      // 描述
    String logo,             // Logo URL
    String website,          // 官网
    List<String> supportedModels,   // 支持的模型
    List<String> capabilities       // 能力列表
) {}

// 聊天请求
public record AIChatRequest(
    String model,             // 模型 ID
    List<ChatMessage> messages,  // 消息列表
    Double temperature,       // 温度参数
    Integer maxTokens,        // 最大 Token 数
    Map<String, Object> extraParams  // 额外参数
) {}

// 聊天响应
public record AIChatResponse(
    String id,                // 响应 ID
    String model,             // 实际使用的模型
    List<ChatMessage> choices, // 回复消息
    AIUsage usage,            // Token 使用量
    String finishReason       // 结束原因
) {}
```

#### 3.5.3 AI Provider 插件示例

**插件元数据 (plugin.json)**：
```json
{
  "id": "my-ai-provider",
  "name": "我的自定义 AI",
  "version": "1.0.0",
  "description": "接入我司自研的 AI 模型",
  "type": "ai-provider",
  "extensionPoints": [
    {
      "type": "AI_PROVIDER",
      "id": "my-custom-ai",
      "config": {
        "authType": "api-key",
        "models": [
          { "id": "my-gpt-4", "name": "MyGPT-4" },
          { "id": "my-gpt-3.5", "name": "MyGPT-3.5" }
        ]
      }
    }
  ]
}
```

**TypeScript 实现示例**：
```typescript
// src/index.ts
import { registerPlugin } from '@lest/plugin-sdk';
import type { AIAIProviderExtension, AIChatRequest, AIChatResponse } from '@lest/plugin-sdk';

class MyAIProvider implements AIAIProviderExtension {
    readonly type = 'ai-provider';

    metadata = {
        id: 'my-custom-ai',
        name: '我的自定义 AI',
        description: '接入我司自研 AI 模型',
        supportedModels: ['my-gpt-4', 'my-gpt-3.5'],
        capabilities: ['chat']
    };

    authConfig = {
        type: 'api-key',
        fields: [
            { key: 'apiKey', label: 'API Key', type: 'password', required: true },
            { key: 'baseUrl', label: 'API 地址', type: 'url', required: true }
        ]
    };

    modelConfig = {
        defaultModel: 'my-gpt-4',
        models: [
            { id: 'my-gpt-4', name: 'MyGPT-4', maxTokens: 8192 },
            { id: 'my-gpt-3.5', name: 'MyGPT-3.5', maxTokens: 4096 }
        ]
    };

    async chat(request: AIChatRequest): Promise<AIChatResponse> {
        const { apiKey, baseUrl } = this.getCredentials();

        const response = await fetch(`${baseUrl}/v1/chat/completions`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${apiKey}`
            },
            body: JSON.stringify({
                model: request.model,
                messages: request.messages,
                temperature: request.temperature,
                max_tokens: request.maxTokens
            })
        });

        return response.json();
    }
}

registerPlugin(new MyAIProvider());
```

### 3.6 AI Workflow 扩展点设计

AI 工作流支持通过插件扩展工作流节点，实现自定义 AI 能力。

#### 3.6.1 Workflow 节点类型

| 节点类型 | 说明 | 内置 |
|---------|------|------|
| `llm` | LLM 调用节点 | ✅ |
| `condition` | 条件分支节点 | ✅ |
| `code` | 代码执行节点 | ✅ |
| `webhook` | Webhook 调用节点 | ✅ |
| `custom` | 自定义节点（插件提供） | ❌ |

#### 3.6.2 自定义 Workflow 节点接口

```typescript
// 自定义 Workflow 节点接口
export interface AIWorkflowNodeExtension extends PluginExtension {
    readonly type: 'ai-workflow-node';

    metadata: {
        id: string;           // 节点类型标识
        name: string;         // 节点名称
        description: string;  // 描述
        icon?: string;        // 图标
        category: string;     // 分类
    };

    // 输入参数定义
    inputSchema: JSONSchema;

    // 输出参数定义
    outputSchema: JSONSchema;

    // 执行逻辑
    execute(context: WorkflowExecutionContext, inputs: any): Promise<any>;
}
```

---

## 4. 接口设计

### 4.1 AI 网关接口

#### 4.1.1 通用聊天接口
```
POST /ai/chat
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "model": "qwen",  // 可选，默认使用配置
  "messages": [
    { "role": "system", "content": "你是一个代码审查专家" },
    { "role": "user", "content": "请审查这段代码..." }
  ],
  "temperature": 0.7,
  "maxTokens": 2000
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
    ]
  }
}
```

#### 4.1.2 代码审查接口
```
POST /ai/code-review
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "mrId": 45,
  "repositoryId": 1
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "reviewId": "review-xxx",
    "mrId": 45,
    "score": 8.5,
    "summary": "代码整体质量良好，主要问题集中在安全方面",
    "issues": [
      {
        "file": "src/main/java/com/lest/auth/UserDao.java",
        "line": 45,
        "severity": "high",
        "type": "security",
        "description": "SQL 注入风险",
        "suggestion": "使用预编译语句"
      }
    ],
    "createdAt": "2026-05-25 10:00:00"
  }
}
```

#### 4.1.3 会议总结接口
```
POST /ai/meeting-summary
Authorization: Bearer {accessToken}
Content-Type: application/json

请求:
{
  "meetingId": 1,
  "minutes": "会议原始记录..."
}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "summaryId": "summary-xxx",
    "meetingId": 1,
    "overview": {
      "type": "Sprint 计划会",
      "participants": 6,
      "duration": 90
    },
    "keyPoints": [
      "确认了用户认证模块包含登录、注册、密码找回三个功能"
    ],
    "decisions": [
      { "content": "决定采用 JWT Token 作为认证方案" }
    ],
    "actionItems": [
      {
        "content": "联系阿里云获取短信 API 文档",
        "assignee": "张三",
        "dueDate": "2026-05-26"
      }
    ],
    "taskSuggestions": [
      {
        "title": "联系阿里云获取短信 API 文档",
        "estimatedHours": 2,
        "priority": "high"
      }
    ],
    "createdAt": "2026-05-25 10:30:00"
  }
}
```

#### 4.1.4 AI 使用统计接口
```
GET /ai/usage-stats?startDate=2026-05-01&endDate=2026-05-31
Authorization: Bearer {accessToken}

响应:
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCalls": 1234,
    "totalTokens": 567890,
    "costEstimate": 56.78,
    "byModel": {
      "qwen": { "calls": 800, "tokens": 300000, "cost": 30.0 },
      "kimi": { "calls": 300, "tokens": 150000, "cost": 15.0 },
      "gpt4": { "calls": 134, "tokens": 117890, "cost": 11.78 }
    },
    "byUser": [
      { "userId": 1, "username": "张三", "calls": 200, "tokens": 50000 }
    ],
    "dailyTrend": [
      { "date": "2026-05-01", "calls": 50, "tokens": 20000 }
    ]
  }
}
```

---

## 5. 数据库设计

### 5.1 表结构

#### ai_model_config AI 模型配置表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| model_type | VARCHAR(32) | NOT NULL | 模型类型 |
| api_key | VARCHAR(512) | | 加密存储的 API Key |
| api_url | VARCHAR(256) | | API 地址 |
| default_model | VARCHAR(64) | | 默认模型 |
| enabled | TINYINT | DEFAULT 1 | 启用状态 |
| priority | INT | DEFAULT 100 | 优先级 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

#### ai_prompt_template AI 提示词模板表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| code | VARCHAR(64) | NOT NULL | 模板代码 |
| name | VARCHAR(128) | NOT NULL | 模板名称 |
| description | VARCHAR(256) | | 描述 |
| template | TEXT | NOT NULL | 提示词模板 |
| model_type | VARCHAR(32) | | 适用模型类型 |
| variables | JSON | | 变量定义 |
| enabled | TINYINT | DEFAULT 1 | 启用状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| uk_code | | UNIQUE | 模板代码唯一索引 |

#### ai_conversation AI 对话记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_id | BIGINT | NOT NULL | 用户 ID |
| scene | VARCHAR(32) | | 场景：code_review / meeting / task |
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
| idx_user_id | | | 用户 ID 索引 |
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
| overview | JSON | | 会议概要 |
| key_points | JSON | | 关键讨论点 |
| decisions | JSON | | 会议决议 |
| action_items | JSON | | 行动项 |
| task_suggestions | JSON | | 任务建议 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| **索引** | | | |
| idx_meeting_id | | UNIQUE | 会议 ID 唯一索引 |

---

## 6. 验收标准

| 用例 | 验收标准 |
|------|---------|
| UC-001 | 可配置多个 AI 模型 |
| UC-002 | AI 网关正确路由请求 |
| UC-003 | MR 创建后自动触发代码审查 |
| UC-004 | 代码审查结果正确显示问题和评分 |
| UC-005 | 会议结束后可生成总结 |
| UC-006 | AI 会议总结包含关键点、决议和行动项 |
| UC-007 | AI 使用统计正确记录调用量 |
| UC-008 | 可设置用量告警 |

---

## 7. 错误码

### 7.1 AI 模块错误码 (16000-16999)

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

---

## 8. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
| V1.1 | 2026-05-25 | 新增 AI Provider 扩展点设计（允许通过插件接入自定义 AI Provider）、新增 AI Workflow 扩展点设计（允许通过插件扩展工作流节点）、新增功能清单备注说明依赖插件系统和开放平台模块 | - |
