# LEST Platform — 文档一致性追踪

> **维护日期**：2026-06-04  
> **当前口径**：Core-first。V1.0/V2.0/V3.0 交付 Jira Core，V4.0 启动插件生态与 AI，V5.0 进入企业商业版。  
> **主路线图**：[ROADMAP-CORE-FIRST.zh-CN.md](./ROADMAP-CORE-FIRST.zh-CN.md)

## 单一事实源

| 事项 | 主文档 | 说明 |
|------|--------|------|
| 版本边界 | [ROADMAP-CORE-FIRST.zh-CN.md](./ROADMAP-CORE-FIRST.zh-CN.md) | 0.1 ~ V5.0 的产品路线和商业边界 |
| 版本总览 | [MILESTONES.zh-CN.md](./MILESTONES.zh-CN.md) | 大版本发布时间、定位和路线原则 |
| 小版本排期 | [MILESTONES/](./MILESTONES/) | V1.0 ~ V5.0 的小版本拆分 |
| PRD 索引 | [1-prd/README.md](./1-prd/README.md) | Core PRD、V4/V5 插件 PRD 入口 |
| Task 索引 | [2-tasks/README.md](./2-tasks/README.md) | Core 任务单、V4/V5 插件 Task 入口 |
| Scheme 参考 | [1-prd/SCHEME-REFERENCE.md](./1-prd/SCHEME-REFERENCE.md) | Jira Scheme 体系参考 |
| 扩展点契约 | [1-prd/core/EXTENSION-POINTS.md](./1-prd/core/EXTENSION-POINTS.md) | V1/V2 预留契约，V4 完整运行时依据 |

## 版本一致性状态

| 版本 | PRD | Task | 小版本规划 | 当前判断 |
|------|-----|------|------------|----------|
| 0.1 | [architecture/VERSION-PLAN.md](./architecture/VERSION-PLAN.md) | 同左 | 同左 | 预发布执行记录 |
| 0.2 | [architecture/VERSION-PLAN.md](./architecture/VERSION-PLAN.md) | 同左 | 同左 | 预发布执行记录 |
| 0.3 | [architecture/VERSION-PLAN.md](./architecture/VERSION-PLAN.md) | 同左 | 同左 | 预发布执行记录 |
| V1.0 | [1-prd/core/V1.0/](./1-prd/core/V1.0/) | [2-tasks/core/V1.0/](./2-tasks/core/V1.0/) | [V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md) | Core-only，一致 |
| V2.0 | [1-prd/core/V2.0/](./1-prd/core/V2.0/) | [2-tasks/core/V2.0/](./2-tasks/core/V2.0/) | [V2.0-小版本规划.md](./MILESTONES/V2.0-小版本规划.md) | Scheme 主线，一致 |
| V3.0 | [1-prd/core/V3.0/](./1-prd/core/V3.0/) | [2-tasks/core/V3.0/](./2-tasks/core/V3.0/) | [V3.0-小版本规划.md](./MILESTONES/V3.0-小版本规划.md) | Jira Core 收口，一致 |
| V4.0 | [1-prd/core/V4.0/](./1-prd/core/V4.0/) + [1-prd/plugins/V4.0/](./1-prd/plugins/V4.0/) | [2-tasks/core/V4.0/](./2-tasks/core/V4.0/) + [2-tasks/plugins/V4.0/](./2-tasks/plugins/V4.0/) | [V4.0-小版本规划.md](./MILESTONES/V4.0-小版本规划.md) | 扩展生态与 AI，骨架一致 |
| V5.0 | [1-prd/core/V5.0/](./1-prd/core/V5.0/) | [2-tasks/core/V5.0/](./2-tasks/core/V5.0/) | [V5.0-小版本规划.md](./MILESTONES/V5.0-小版本规划.md) | 企业商业版，Core 一致 |

## 已完成清理

| 日期 | 清理项 | 结果 |
|------|--------|------|
| 2026-06-04 | 删除 `1-prd` 根目录历史评审/差距报告 | 避免过程稿被误读为正式 PRD |
| 2026-06-04 | 移除 V3 Core 下的 IM 深度集成任务单 | V3.0 不再出现 IM Core 交付暗示 |
| 2026-06-04 | 将 IM 深度集成改为 V4/V5 插件任务 | 归入 `2-tasks/plugins/V4.0/` |
| 2026-06-04 | 修正 V4/V5 命名和日期 | V4.0 = 扩展生态与 AI，2027-12-10；V5.0 = 企业商业版，2028-07-04 |
| 2026-06-04 | 清理 README/TODO 的历史数字和旧目录口径 | 插件入口不再按 V1/V2/V3 设置 |

## 待持续维护

| 优先级 | 事项 | 建议 |
|--------|------|------|
| P1 | V4.0 插件池正式排期 | 启动 V4 前，按 V4 小版本重新评估、编号和估算 |
| P1 | V4.0 AI 模块 PRD 深化 | 当前为骨架，进入开发前补充数据权限、成本控制、模型降级和验收标准 |
| P1 | V5.0 企业能力 PRD 深化 | 当前为骨架，进入开发前补充租户模型、身份协议、审计留存和 SLA 指标 |
| P2 | 技术参考与真实实现对齐 | `reference/` 与 `architecture/` 需要随代码实现持续校准 |
| P2 | 插件任务去重 | V4 排期时合并重复的报表、AI、发布、IM、DevOps 任务拆分 |

## 维护规则

1. 改路线先改主路线图和主里程碑，再改 PRD、Task、小版本规划。
2. V1.0/V2.0/V3.0 文档不得出现“官方插件随版本交付”的暗示。
3. 插件文档必须放入 V4 插件生态或 V5 企业商业版，不在 V1/V2/V3 建插件入口。
4. 删除历史过程稿后，要同步删除 README/TODO/索引中的引用。
5. 每次批量调整后运行 Markdown 链接检查。
