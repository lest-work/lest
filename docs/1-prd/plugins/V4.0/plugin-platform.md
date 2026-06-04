# 插件平台 PRD

> **版本归属**：V4.0.0-alpha / 4.0.1 / 4.0.6
>
> **定位**：把 V1.0~V3.0 沉淀的扩展点契约变成可安装、可启停、可审计的插件平台。

## 范围

| 模块 | 说明 |
|------|------|
| 插件生命周期 | 安装、启用、禁用、配置、卸载、健康检查 |
| Manifest | 插件 ID、版本、兼容范围、权限声明、扩展点声明 |
| 扩展点注册中心 | 后端 Provider、事件订阅、前端插槽统一注册 |
| 插件 SDK | 后端 SDK、前端 SDK、API Client、事件订阅 Client |
| 权限与审计 | 最小权限声明、越权拦截、安装/启停/配置审计 |
| 插件管理 UI | 插件列表、详情、配置页、健康状态、错误日志 |
| 兼容矩阵 | Core 版本与插件版本兼容策略 |

## 不做

| 能力 | 归属 |
|------|------|
| 官方 IM / DevOps / 协作 / AI 插件业务逻辑 | V4.0.2 ~ 4.0.5 对应插件 |
| SSO、LDAP、SCIM、多租户 | V5.0 商业 Core |
| 插件市场、付费分发、远程仓库 | V4.0 之后再评估 |

## Manifest 最小结构

```json
{
  "id": "lest-example",
  "name": "Example Plugin",
  "version": "1.0.0",
  "coreVersion": ">=4.0.0 <5.0.0",
  "permissions": ["issue:read", "notification:send"],
  "extensions": [
    {
      "point": "task-detail.tab",
      "id": "example-tab"
    }
  ]
}
```

## 验收标准

1. 插件可以通过 Manifest 完成安装、启用、禁用、卸载。
2. 插件只能访问 Manifest 声明的权限和扩展点。
3. 插件异常不能影响 Core 项目、Issue、Sprint、Board 主链路。
4. 插件操作写入审计日志，管理员能定位安装、启停、配置变更。
