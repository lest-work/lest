# LEST Platform 文档中心

## 产品定位

LEST Platform 是面向中国及全球企业的 Jira-like 敏捷管理平台。当前采用 **Core-first** 路线：先把 Jira Core 的项目、任务、看板、工作流、Scheme、搜索、报表和迁移能力做成可真实交付的闭环，再开放插件生态、AI 能力和企业商业能力。

## 文档口径

| 版本 | 发布时间 | 定位 | 交付边界 | 许可 |
|------|----------|------|----------|------|
| 0.1 | 2026-06 | 可运行骨架 | 前端迁移、后端编译、核心服务启动 | 内部预览 |
| 0.2 | 2026-07 | Core 闭环补齐 | 项目/任务/看板/详情/权限/通知跑通 | 内部预览 |
| 0.3 | 2026-08 | 发版候选 | API、Schema、权限、E2E、部署脚本稳定 | 内部预览 |
| V1.0 | 2026-08-28 | Jira Core 基础版 | 项目、任务、看板、Backlog/Sprint、通知、附件、权限、开放 API 基础 | MIT |
| V2.0 | 2026-11-28 | Scheme 可配置版 | Issue Type、Workflow、Custom Field、Screen、Field Config、项目模板 | MIT |
| V3.0 | 2027-03-27 | Jira Core 完整版 | Issue Link、工时、自动化、敏捷报表、批量、JQL、导入导出 | MIT |
| V4.0 | 2027-12-10 | 扩展生态与 AI | 插件运行时、SDK、官方插件池、AI 任务拆解/风险/预测/自然语言查询 | MIT + 商业 |
| V5.0 | 2028-07-04 | 企业商业版 | 多租户、SSO/LDAP、审计合规、精细权限、企业报表、高可用、SLA | 商业 |

> **主路线图**：[ROADMAP-CORE-FIRST.zh-CN.md](./ROADMAP-CORE-FIRST.zh-CN.md)  
> **开发执行入口**：[DEVELOPMENT-EXECUTION-PLAN.zh-CN.md](./DEVELOPMENT-EXECUTION-PLAN.zh-CN.md)  
> **主里程碑**：[MILESTONES.zh-CN.md](./MILESTONES.zh-CN.md)  
> **一致性追踪**：[TODO.md](./TODO.md)

## 路线原则

1. V1.0/V2.0/V3.0 不开发官方插件，只交付 Jira Core 主线能力。
2. V1.0/V2.0 可以预留扩展点契约，但不承诺插件市场、热加载和完整 SDK。
3. V2.0 的主线是 Jira Scheme 体系，不能被插件开发分散。
4. V3.0 的主线是 Jira Core 收口，IM、OKR、Portfolio、审批、LDAP/SSO、多租户不作为 Core 发布阻塞项。
5. V4.0 才启动插件运行时、SDK、官方插件池和 AI 能力。
6. V5.0 明确企业商业边界，承接身份集成、多租户、审计合规、高可用、SLA 和企业报表。

## Jira 概念映射

| Jira 概念 | LEST 对应 | 说明 |
|-----------|-----------|------|
| Project | 项目/模块 | 项目空间、成员、配置、迭代和发布 |
| Issue Type | 任务类型 | Epic / Story / Task / Bug，V2.0 支持自定义 |
| Workflow | 工作流 | 状态流转、条件、校验和后置动作 |
| Custom Field | 自定义字段 | 25 种字段类型，V2.0 完整实现 |
| Screen | 表单屏幕 | Create / Edit / View 的字段布局 |
| Field Configuration | 字段配置 | 隐藏、必填、只读、默认值 |
| Board | 看板 | Scrum/Kanban 看板、泳道、快捷筛选 |
| Sprint | 迭代 | Backlog、Sprint 规划、燃尽图 |
| Version | 版本/里程碑 | 发布版本规划、变更记录 |
| Automation | 自动化规则 | 触发器、条件、动作、规则模板 |
| JQL | 查询语言 | 筛选器、订阅、导出和迁移 |

## 目录结构

```
docs/
├── README.md                    # 文档中心入口
├── ROADMAP-CORE-FIRST.zh-CN.md  # Core-first 主路线图
├── MILESTONES.zh-CN.md          # 版本里程碑总览
├── MILESTONES.md                # 英文入口/中文路线跳转
├── TODO.md                      # 文档一致性追踪
├── MILESTONES/                  # V1.0~V5.0 小版本规划
├── 1-prd/                       # 产品需求文档
│   ├── README.md                # PRD 总索引
│   ├── SCHEME-REFERENCE.md      # Jira Scheme 参考手册
│   ├── MULTI-END-ARCHITECTURE.md
│   ├── core/                    # Core 主线 PRD
│   │   ├── EXTENSION-POINTS.md  # 扩展点契约
│   │   ├── V1.0/               # Jira Core 基础版
│   │   ├── V2.0/               # Scheme 可配置版
│   │   ├── V3.0/               # Jira Core 完整版
│   │   ├── V4.0/               # V4 无新增 Core，转入插件生态
│   │   └── V5.0/               # 企业商业版骨架
│   └── plugins/                 # V4/V5 插件 PRD
│       ├── V4.0/               # V4 插件生态与 AI
│       └── V5.0/               # V5 企业插件与商业能力
├── 2-tasks/                     # 开发任务单
│   ├── core/                    # Core 任务单
│   └── plugins/                 # V4/V5 插件任务单
├── architecture/                # 架构方案与迁移方案
├── guide/                       # 开发、部署、分支指南
└── reference/                   # API、数据库、技术参考
```

## 快速导航

| 类型 | 文档 |
|------|------|
| 开发执行计划 | [DEVELOPMENT-EXECUTION-PLAN.zh-CN.md](./DEVELOPMENT-EXECUTION-PLAN.zh-CN.md) |
| 主路线图 | [ROADMAP-CORE-FIRST.zh-CN.md](./ROADMAP-CORE-FIRST.zh-CN.md) |
| 版本总览 | [MILESTONES.zh-CN.md](./MILESTONES.zh-CN.md) |
| 小版本规划 | [MILESTONES/](./MILESTONES/) |
| PRD 总索引 | [1-prd/README.md](./1-prd/README.md) |
| Task 总索引 | [2-tasks/README.md](./2-tasks/README.md) |
| Scheme 参考 | [1-prd/SCHEME-REFERENCE.md](./1-prd/SCHEME-REFERENCE.md) |
| 扩展点契约 | [1-prd/core/EXTENSION-POINTS.md](./1-prd/core/EXTENSION-POINTS.md) |
| 插件 PRD | [1-prd/plugins/README.md](./1-prd/plugins/README.md) |
| 多端架构 | [1-prd/MULTI-END-ARCHITECTURE.md](./1-prd/MULTI-END-ARCHITECTURE.md) |
| 技术架构 | [architecture/BACKEND-FRONTEND-ARCHITECTURE.md](./architecture/BACKEND-FRONTEND-ARCHITECTURE.md) |
| 微服务迁移 | [architecture/MICROSERVICE-MIGRATION.md](./architecture/MICROSERVICE-MIGRATION.md) |
| 版本执行计划 | [architecture/VERSION-PLAN.md](./architecture/VERSION-PLAN.md) |
| 错误码规范 | [1-prd/plugins/ERROR-CODES.md](./1-prd/plugins/ERROR-CODES.md) |

## PRD 与 Task 对应关系

| 版本 | PRD | Task | 小版本规划 |
|------|-----|------|------------|
| V1.0 | [1-prd/core/V1.0/](./1-prd/core/V1.0/) | [2-tasks/core/V1.0/](./2-tasks/core/V1.0/) | [V1.0-小版本规划.md](./MILESTONES/V1.0-小版本规划.md) |
| V2.0 | [1-prd/core/V2.0/](./1-prd/core/V2.0/) | [2-tasks/core/V2.0/](./2-tasks/core/V2.0/) | [V2.0-小版本规划.md](./MILESTONES/V2.0-小版本规划.md) |
| V3.0 | [1-prd/core/V3.0/](./1-prd/core/V3.0/) | [2-tasks/core/V3.0/](./2-tasks/core/V3.0/) | [V3.0-小版本规划.md](./MILESTONES/V3.0-小版本规划.md) |
| V4.0 | [1-prd/core/V4.0/](./1-prd/core/V4.0/) + [1-prd/plugins/V4.0/](./1-prd/plugins/V4.0/) | [2-tasks/core/V4.0/](./2-tasks/core/V4.0/) + [2-tasks/plugins/V4.0/](./2-tasks/plugins/V4.0/) | [V4.0-小版本规划.md](./MILESTONES/V4.0-小版本规划.md) |
| V5.0 | [1-prd/core/V5.0/](./1-prd/core/V5.0/) | [2-tasks/core/V5.0/](./2-tasks/core/V5.0/) | [V5.0-小版本规划.md](./MILESTONES/V5.0-小版本规划.md) |

## 插件规划说明

V1.0/V2.0/V3.0 不保留插件 PRD/TASK 入口。插件从 V4.0 开始正式开发，企业商业能力进入 V5.0 商业 Core。

## 维护规则

1. 版本范围以 [ROADMAP-CORE-FIRST.zh-CN.md](./ROADMAP-CORE-FIRST.zh-CN.md) 和 [MILESTONES.zh-CN.md](./MILESTONES.zh-CN.md) 为准。
2. 每个正式版本必须同时维护 PRD、Task 和小版本规划三类文档。
3. 历史评审、差距分析和过程稿不放在 `1-prd/` 根目录，避免和正式 PRD 混淆。
4. 新增插件 PRD/TASK 必须放入 V4.0，并同步小版本规划；V5.0 企业身份、租户、合规和 SLA 能力放入 Core。
5. 改动文档后运行 Markdown 链接检查，确保相对路径可用。
