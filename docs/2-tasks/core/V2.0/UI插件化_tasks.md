# UI 扩展点契约任务单

> **PRD**：[UI 扩展点契约设计](../../../1-prd/core/V2.0/UI插件化架构设计.md)
>
> **版本归属**：V2.0 Core
>
> **开发口径**：V2.0 只定义前端扩展点 ID、上下文、布局边界和 Core 页面占位，不实现插件运行时、动态加载、SDK、市场或官方插件。插件平台实现见 [V4.0 插件平台任务单](../../plugins/V4.0/plugin-platform_tasks.md)。

## 任务分解

| 任务 ID | 说明 | 优先级 | 状态 |
|---------|------|--------|------|
| UIP-CONTRACT-001 | 定义 Task 详情页扩展点 ID、上下文和布局边界 | P1 | 待启动 |
| UIP-CONTRACT-002 | 定义 Project 详情页扩展点 ID、上下文和布局边界 | P1 | 待启动 |
| UIP-CONTRACT-003 | 定义 Board / Dashboard / Command Palette 扩展点 ID | P2 | 待启动 |
| UIP-CONTRACT-004 | 将扩展点清单同步到 `docs/1-prd/core/EXTENSION-POINTS.md` | P1 | 待启动 |
| UIP-CONTRACT-005 | Core 页面预留非破坏性插槽，不接入动态插件加载 | P2 | 待启动 |
| UIP-CONTRACT-006 | 定义扩展点上下文脱敏规则，避免插件读取无关 Issue / Project 数据 | P1 | 待启动 |
| UIP-CONTRACT-007 | 输出 V4 插件平台需要消费的前端扩展点契约清单 | P2 | 待启动 |

## V2.0 不做

| 不做项 | 后续归属 |
|--------|----------|
| PluginRegistry / ExtensionResolver / ComponentRegistry | V4.0 插件平台 |
| 插件动态加载、异步组件渲染、热更新 | V4.0 插件平台 |
| `@lest/plugin-sdk`、CLI 模板、插件 API Client | V4.0 插件平台 |
| 任务详情 / 项目详情的真实插件 Tab 注入 | V4.0 官方插件或第三方插件 |
| 插件市场、远程仓库、付费分发 | V4.0 之后再评估 |

## 验收标准

1. V2.0 扩展点 ID、上下文、布局边界已写入 PRD 和扩展点总览。
2. Core 页面占位不改变 V2.0 的任务、项目、看板主链路。
3. 所有运行时、SDK、动态加载任务都只在 V4.0 插件平台任务中执行。
