# lest-ai-assistant — AI 助手插件

> **插件 ID**: `lest-ai-assistant`
>
> **版本**: V4.0
>
> **Jira 映射**: Atlassian Intelligence（AI 助手）
>
> **许可**: 开源（MIT）
>
> **依赖**: [AI Provider 插件](./lest-ai-provider.md)
>
> **最后更新**: 2026-06-02

---

## 1. 产品概述

### 1.1 目标与愿景

LEST AI 助手是平台的全局 AI 入口，通过浮动窗口和嵌入式建议卡片，在用户需要帮助时主动提供 AI 能力。AI 助手理解当前页面上下文，记住对话历史，并能够直接执行操作指令——让 AI 从"问答工具"进化为"智能工作伙伴"。

### 1.2 与竞品对标

|| 功能 | Jira Intelligence | LEST AI 助手 | 说明 |
||------|------------------|--------------|------|
| 全局浮动助手 | ❌ | ✅ | 任意页面可呼出 |
| 上下文感知 | ✅ | ✅ | 自动识别当前页面 |
| 操作执行 | 部分 | ✅ | AI 指令可直接执行 |
| 嵌入式建议 | ❌ | ✅ | 各页面内嵌 AI 建议卡片 |
| 对话历史 | ✅ | ✅ | 多轮上下文 |
| 快捷指令 | ❌ | ✅ | `/` 快捷命令 |

---

## 2. 功能范围

### 2.1 核心功能（P0）

|| 功能 | 说明 |
||------|------|
| 全局浮动助手 | 任意页面右下角呼出，支持 `Cmd+K` 快捷键 |
| 上下文感知 | 自动注入当前页面信息（任务ID/项目名/用户等） |
| 操作执行 | AI 指令 → 平台 API 执行（需用户确认） |
| 嵌入式建议卡片 | 任务详情/看板/会议/仪表盘等页面内嵌 AI 建议 |
| 多轮对话 | 支持追问和上下文延续 |
| 快捷指令 | `/task` 创建任务、`/sprint` 查看迭代、`/report` 生成报表 |

### 2.2 增强功能（P1）

|| 功能 | 说明 |
||------|------|
| 对话历史 | 历史对话可搜索、可继续 |
| AI 意图识别 | 自动识别用户意图并推荐下一步 |
| 敏感操作确认 | 涉及数据变更的操作需二次确认 |
| 深色模式适配 | 建议卡片和浮窗支持深色模式 |

### 2.3 进阶功能（P2）

|| 功能 | 说明 |
||------|------|
| 快捷指令扩展 | 插件可注册自定义指令 |
| 语音输入 | 支持语音转文字（Web Speech API） |
| 快捷回复模板 | 常用回复保存为模板 |

---

## 3. 数据模型

```typescript
interface AIConversation {
  id: string;
  userId: string;
  sessionId: string;        // 对话 session，同一 session 共享上下文
  messages: AIMessage[];
  context: AIContext;       // 当前页面上下文
  createdAt: Date;
  updatedAt: Date;
}

interface AIMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  actions?: AIAction[];     // AI 建议的操作
  createdAt: Date;
}

interface AIContext {
  page: 'task' | 'board' | 'sprint' | 'meeting' | 'dashboard' | 'project';
  taskId?: string;
  projectId?: string;
  boardId?: string;
  sprintId?: string;
  userId?: string;
}

interface AIAction {
  id: string;
  type: 'create_task' | 'update_status' | 'assign' | 'comment' | 'navigate';
  description: string;       // 用户可见的操作描述
  payload: object;          // 操作参数
  confirmRequired: boolean;
}
```

---

## 4. API 设计

|| 端点 | 方法 | 说明 |
||------|------|------|
| `/ai/assistant/chat` | POST | 发送消息，返回 AI 响应和建议操作 |
| `/ai/assistant/conversations` | GET | 获取历史会话列表 |
| `/ai/assistant/conversations/{id}` | GET | 获取会话详情 |
| `/ai/assistant/execute` | POST | 执行 AI 建议的操作 |
| `/ai/assistant/context` | GET | 获取当前页面上下文 |
| `/ai/assistant/shortcuts` | GET | 获取可用快捷指令列表 |

---

## 5. 交互设计

### 5.1 全局浮动助手

```
呼出方式:
1. 快捷键 Cmd+K（Mac）/ Ctrl+K（Windows）
2. 页面右下角 AI 图标点击
3. 任务详情页工具栏按钮

界面结构:
┌─────────────────────────────────────┐
│ 💬 LEST AI 助手          [历史] [×] │
├─────────────────────────────────────┤
│                                     │
│  [AI 回复内容，支持 Markdown 格式]  │
│                                     │
│  ┌─────────────────────────────┐    │
│  │ 💡 建议操作              │    │
│  │ [ ] 将此任务状态改为进行中│    │
│  │ [ ] 指派给 张三         │    │
│  │ [ ] 添加备注说明进度     │    │
│  │              [全部执行 ✓] │    │
│  └─────────────────────────────┘    │
│                                     │
├─────────────────────────────────────┤
│ [请输入问题或指令...]           [→] │
└─────────────────────────────────────┘
```

### 5.2 快捷指令

|| 指令 | 说明 | 示例 |
||------|------|------|
| `/task <描述>` | 创建任务 | `/task 修复登录页面样式问题` |
| `/sprint` | 查看当前迭代 | `/sprint` |
| `/report` | 生成报表 | `/report 本周工作总结` |
| `/search <关键词>` | 搜索任务 | `/search 高优先级未完成` |
| `/assign <人> <任务>` | 分配任务 | `/assign @张三 LEST-123` |
| `/help` | 显示帮助 | `/help` |

### 5.3 嵌入式建议卡片

|| 页面 | 建议内容 |
||------|---------|
| 任务详情页 | 估算工时建议、优先级建议、子任务拆解建议、代码审查建议 |
| 看板页 | 列均衡建议、优先级排序建议、WIP 限制建议、依赖冲突提示 |
| 会议页 | 议程建议、时长建议、行动项建议、会议纪要生成 |
| 仪表盘 | 数据解读、异常提醒、优化建议 |
| Sprint 规划页 | 工作量均衡建议、依赖冲突提示 |

---

## 6. 非功能性需求

|| 要求 | 说明 |
||------|------|
| 响应时间 | P50 < 2s，P95 < 5s（不含流式输出） |
| 流式输出 | 支持 SSE 流式输出，逐步显示回复 |
| 上下文长度 | 单会话最多 20 轮对话 |
| 离线处理 | AI 建议卡片在网络断开时显示缓存建议 |
| 安全 | 操作执行需用户明确确认；敏感数据不在日志中记录 |
