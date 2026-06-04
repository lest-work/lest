# REALTIME_PROVIDER — 实时推送扩展点任务分解

> **文档目的**：为 `REALTIME_PROVIDER` 扩展点提供完整的技术任务分解。
>
> **对应扩展点**：`REALTIME_PROVIDER`（EXTENSION-POINTS.md §J）
>
> **对应版本**：V2.0
>
> **优先级**：P0（所有实时能力的基础）
>
> **最后更新**：2026-06-02

---

## 一、任务总览

| 任务数 | 估计工时 | 前置依赖 |
|--------|---------|---------|
| 25 | 60人天 | V1.0 Core 认证 + 扩展点契约 |

---

## 二、任务详情

### Phase 1：基础设施（15人天）

#### RT-001：接口设计与 TypeScript 定义
**任务**：在 Core 扩展契约中定义 `RealtimeProvider` 和相关类型；完整 Plugin SDK 导出等到 V4.0。

**验收标准**：
- `RealtimeProvider` 接口继承 `BaseProvider`，包含 `initialize()` / `destroy()` / `healthCheck()`
- 定义 `Subscription` / `ConnectionOptions` / `RealtimeEvent` / `ConnectionStatus` 类型
- 接口先由 Core 类型包导出；Plugin SDK TypeScript 包在 V4.0 接入
- 提供 JSDoc 注释，所有方法有 TypeScript 类型

**估计工时**：3人天

---

#### RT-002：WebSocket 服务端实现
**任务**：实现 Core 内置的 WebSocket Provider（`@lest/realtime-websocket`）。

**验收标准**：
- 基于 Spring WebSocket + STOMP 协议
- 支持 JWT Token 认证（握手时验证）
- 支持多实例部署（Redis Pub/Sub 广播）
- 支持断线重连（指数退避，最长 30 秒）
- 支持心跳保活（每 30 秒发送 PING）
- 支持订阅主题：`task:{taskId}:updated`、`project:{projectId}:*`、`user:{userId}:notifications`
- 并发连接数基准测试：单实例 ≥ 10,000 连接

**估计工时**：8人天

---

#### RT-003：Redis Pub/Sub 集群支持
**任务**：实现多实例部署时的消息广播。

**验收标准**：
- 所有实例通过 Redis Channel 广播消息（Channel 命名：`lest:realtime:{topic}`）
- 支持消息优先级（高优先级消息使用独立 Channel）
- 广播失败时降级到轮询（FallbackInterval: 5s）
- Redis 连接池配置：max=50, min=10
- 跨区域部署支持（通过 Redis Cluster）

**估计工时**：4人天

---

### Phase 2：客户端 SDK（15人天）

#### RT-004：Web 端 SDK 实现
**任务**：在 `packages/web-sdk/src/realtime.ts` 中实现浏览器端 SDK。

**验收标准**：
- 基于原生 WebSocket API，封装成 `WebSocketClient` 类
- 自动重连：断线后按指数退避重连（最大 30 秒）
- 心跳保活：30 秒无消息自动发送 PING
- 订阅管理：`subscribe()` 返回 `Subscription`，支持 `unsubscribe()`
- 事件处理：`onConnectionChange()` 回调，连接状态：`connecting / connected / disconnected / error`
- 内存管理：组件卸载时自动 `disconnect()`，防止内存泄漏
- 压缩：`permessage-deflate` WebSocket 压缩支持

**估计工时**：5人天

---

#### RT-005：Vue 3 集成组件
**任务**：实现 `packages/web-components/src/composables/useRealtime.ts` 组合式函数。

**验收标准**：
- `useRealtime()` 返回 `{ subscribe, isConnected, connectionStatus }`
- 与 Pinia Store 集成，连接状态自动同步到全局 Store
- Reactivity：任务数据变更时自动触发 Vue 响应式更新
- 错误处理：连接失败时显示全局 Toast 提示
- 性能：不会因为高频更新导致 Vue 组件重复渲染（使用 `shallowRef`）

**估计工时**：3人天

---

#### RT-006：服务端事件发布 API
**任务**：实现事件发布接口，供 Core 内部模块调用；V4.0 插件运行时接入时复用该接口契约。

**验收标准**：
- `EventPublisher.publish(topic, event)` — 发布事件到指定主题
- `EventPublisher.sendToUser(userId, message)` — 点对点发送
- V2.0 仅校验 Core 模块发布权限；插件订阅权限和 `dependsOn` 过滤在 V4.0 插件运行时实现
- 权限检查：未认证用户不能发布事件
- 限流：单用户每秒最多发布 10 个事件（防止滥用）

**估计工时**：4人天

---

#### RT-007：消息格式规范
**任务**：定义 WebSocket 消息的 JSON Schema。

**验收标准**：
```typescript
// 消息格式
interface RealtimeMessage {
  id: string;          // UUID
  topic: string;        // 主题
  event: string;        // 事件类型：'created' | 'updated' | 'deleted' | 'notification'
  data: unknown;        // 载荷
  timestamp: number;    // Unix ms
  priority?: 'high';    // 可选高优先级
}

// 订阅响应
interface SubscriptionAck {
  subscriptionId: string;
  topic: string;
  status: 'subscribed' | 'already-subscribed';
}
```

**估计工时**：1人天

---

#### RT-008：桌面端 Electron 集成
**任务**：在 Electron 主进程中集成 WebSocket 客户端。

**验收标准**：
- 主进程维护一个 WebSocket 连接（`MainRealtimeClient`）
- 通过 IPC 将事件转发到渲染进程
- 支持系统托盘通知（收到高优先级事件时闪动托盘）
- 离线检测：网络断开时在渲染进程显示离线Banner
- 恢复连接：网络恢复后自动重新订阅所有主题

**估计工时**：4人天

---

### Phase 3：运维与监控（10人天）

#### RT-009：连接统计与监控
**任务**：实现连接监控 Dashboard Gadget。

**验收标准**：
- 实时显示：当前连接数 / 总容量 / 各实例分布
- 历史趋势：最近 7 天的连接数折线图
- 异常告警：连接数超过容量 80% 时触发告警
- Admin 页面显示：活跃订阅数 / 消息吞吐量 / 平均延迟
- 与 `AUDIT_HANDLER` 集成：记录连接/断开事件

**估计工时**：3人天

---

#### RT-010：消息投递保证
**任务**：实现 At-Least-Once 投递保证。

**验收标准**：
- 客户端订阅时携带 `lastEventId`（最近收到的消息ID）
- 服务端重连后推送期间的消息（Redis Stream，保留 1 小时）
- 客户端收到消息后发送 ACK（`subscriber.ack(messageId)`）
- 超过 5 分钟未 ACK 的消息标记为失败
- 提供 `MessageDeliveryReport`：投递成功 / 失败 / 超时统计

**估计工时**：4人天

---

#### RT-011：性能基准测试
**任务**：对 WebSocket 服务进行全量性能测试。

**验收标准**：
- 单实例 10,000 并发连接，CPU < 60%，内存 < 2GB
- 消息延迟：P50 < 10ms，P99 < 100ms
- 广播性能：1对10,000推送 < 500ms
- Redis 多实例广播延迟：< 50ms
- 内存泄漏检测：24小时压测后内存增长 < 5%

**估计工时**：3人天

---

## 三、验收标准

1. Web 端任务详情页变更时，5 秒内在所有在线用户的页面实时更新
2. 看板拖拽状态变更时，< 2 秒内同步到所有协作用户
3. 断线重连后自动恢复订阅，不丢失任何事件（通过消息队列缓冲）
4. 多实例部署时，所有实例都能收到消息（Redis Pub/Sub）
5. 支持 10,000 并发连接，无内存泄漏
6. 与 `NOTIFICATION_CHANNEL` 集成：站内通知实时推送
7. 与 `PUSH_NOTIFICATION_PROVIDER` 集成：App 不在线时消息进入推送队列
