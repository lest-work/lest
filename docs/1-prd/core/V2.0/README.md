# Core 核心 -- V2.0

> **Core V2.0** 对 V1.0 进行架构升级，引入 Jira 的 Scheme 体系，实现高度可定制化。

## Core V2.0 模块

| 文档 | Jira 映射 | 说明 | Jira 对齐率 | 状态 |
|------|---------|------|-----------|------|
| [任务管理](./任务管理.md) | Jira Issue + Custom Field + Workflow | **EAV 自定义字段**（25 种类型） + **Screen/Scheme** + **工作流引擎** | **90%** | 规划中 |
| [项目管理](./项目管理.md) | Jira Project Configuration | **Project Scheme 体系**：Issue Type / Workflow / Screen / Field Configuration / **Permission** / **Notification** / **Priority** | **97%** | 规划中 |
| [插件架构与功能分层](./插件架构与功能分层.md) | Atlassian Marketplace Apps | **Core vs Plugin 边界设计**：官方插件矩阵（21 个插件） | -- | 规划中 |

## V1.0 vs V2.0 Core 功能对比

| 功能维度 | V1.0（硬编码） | V2.0（Jira 方案） | Jira 对齐率 |
|---------|--------------|-----------------|-----------|
| 任务类型 | 全局固定 4 种 | 项目可自定义 | **90%** |
| 状态 | 全局固定 3 种 | 项目可自定义 | **90%** |
| 工作流 | 仅状态变更 | 完整工作流引擎 | **100%** |
| 自定义字段 | 无 | 25 种字段类型 | **100%** |
| 表单配置 | 固定表单 | Screen/Scheme | **100%** |
| Permission Scheme | 无 | 完整权限方案 | **100%** |
| Priority Scheme | 无 | 优先级方案 | **100%** |
| Issue Security | 无 | 安全级别 | **100%** |

> **V2.0 整体 Jira 功能对齐率**：约 **90%**。
