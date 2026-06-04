# DevOps 插件 PRD

> **版本归属**：V4.0.3
>
> **插件范围**：代码仓库与 CI/CD 最小集成。

## 产品目标

让 Issue 可以关联提交、MR/PR 和构建状态，帮助团队在任务上下文中看到研发交付进展。V4.0 只做轻量集成，不把代码托管或流水线系统做进 Core。

## 功能范围

| 插件 | 能力 | 说明 |
|------|------|------|
| 代码仓库插件 | Provider 接口 | GitHub / GitLab / Gitea 最小实现 |
| 代码仓库插件 | Issue Key 关联 | 从 Commit / MR / PR 标题或描述识别 Issue Key |
| 代码仓库插件 | Issue 详情展示 | 展示关联提交、MR/PR、作者、状态、链接 |
| CI/CD 插件 | Provider 接口 | Jenkins / GitHub Actions / GitLab CI 最小实现 |
| CI/CD 插件 | 构建状态 | 按 Issue Key 展示最近构建、状态、耗时、链接 |
| CI/CD 插件 | 手动触发 | 可选，按权限触发已配置流水线 |

## 不做

| 能力 | 归属 |
|------|------|
| 完整代码评审系统 | 外部代码平台 |
| 制品仓库、部署编排、环境管理 | 后续 DevOps 插件增强 |
| 代码数据写入 Core 主模型 | 插件自有表或缓存，Core 只保留关联索引 |

## 验收标准

1. Issue Key 能稳定关联 Commit、MR/PR 和构建记录。
2. Issue 详情页通过插件 Tab 或侧边信息展示 DevOps 状态。
3. 外部平台 Token 加密保存，权限校验和调用日志完整。
4. 外部平台不可用时 Core Issue、Board、Sprint 功能不受影响。
