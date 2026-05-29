# LEST Platform - API 规范

> ⚠️ **注意**：本文件中第 7 节的类型定义（`ApiResult`/`PageResult`/`message`/`records`）已过时，
> 实际代码使用 RuoYi 标准格式（`AjaxResult`/`TableDataInfo`/`msg`/`rows`）。
> 请以 `.windsurf/rules/03-frontend-conventions.md` 为准。

> 本规范定义前后端通信的 HTTP 状态码、响应结构、认证错误码标准。所有前后端开发者必须严格遵循。

---

## 1. HTTP 状态码规范

HTTP 状态码决定前端行为，业务码仅用于调试提示。

| HTTP 状态码 | 含义 | 前端行为 |
|---|---|---|
| **200** | 成功 | 解析 `data`，即使 `body.code != 200` 也显示后端返回的 `message` |
| **400** | 参数校验失败 / 请求格式错误 | 显示 `message` |
| **401** | 未认证 / Token 无效 / Token 过期 | **强制跳转登录页**（见第 3 节） |
| **403** | 无权限访问 | 显示无权限提示 |
| **404** | 资源不存在 | 显示 404 页面 |
| **500** | 系统内部错误 | 显示系统异常提示 |

> **重要原则**：HTTP 状态码用于控制前端路由和行为，**业务码仅作描述性信息**。

---

## 2. 响应结构

所有 API 必须返回以下 JSON 结构：

```json
{
  "code": 200,           // HTTP 状态码（见第 1 节）
  "message": "success",  // 人类可读信息（成功时为 "success"）
  "data": { ... },       // 业务数据（可 null）
  "traceId": "abc123"    // 请求追踪 ID（可选）
}
```

分页响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

## 3. 认证错误码（Auth Module）

### 3.1 后端必须同时设置

- **HTTP 状态码** = `401`
- **响应 Body** 中 `code` = 具体业务错误码（见下表）
- **响应 Body** 中 `message` = 人类可读描述

### 3.2 业务错误码（仅用于 body.message 对照，不可用于 HTTP 状态码）

| 业务码 | 含义 | 英文 message |
|---|---|---|
| 1004 | Access Token 已过期 | Access Token expired |
| 1005 | Token 无效或被篡改 | Token invalid or tampered |
| 1006 | 刷新令牌已过期，需重新登录 | Refresh token expired |
| 1007 | 刷新令牌已被使用（防止重放攻击） | Refresh token already used |

### 3.3 前端行为

前端 `request.ts` 中定义：

```typescript
// 认证类错误码，收到后强制跳转登录页
const AUTH_ERROR_CODES = [401, 1004, 1005, 1006, 1007];
```

这意味着：
- **HTTP 401** → 跳转登录页
- **HTTP 200 + body.code = 1005** → 跳转登录页（`body.code` 在 `AUTH_ERROR_CODES` 中）
- **其他业务码（即使 code != 200）** → 不跳转，只显示 `message` 提示

---

## 4. 前端 request.ts 关键逻辑

```typescript
// 认证错误码列表（必须与后端 ErrorCode 1004-1007 对齐）
const AUTH_ERROR_CODES = [401, 1004, 1005, 1006, 1007];

// 登录过期处理
if (isAuthError(code)) {
  // 跳转登录页（带 from 参数）
  logout(true, fullPath, router.push);
}

// 普通业务错误（code != 200）→ 显示 message 提示，不跳转
if (res.data.code !== 200) {
  return res.data.message;
}
```

---

## 5. 后端 EntryPoint 编写规范

**错误示例**（混用 HTTP 状态码和业务码）：

```java
// ❌ 错误：HTTP 200 但返回认证错误，前端无法正确跳转
response.setStatus(200);
response.getWriter().write("{\"code\":1005,\"message\":\"Token无效\"}");
```

**正确示例**：

```java
// ✅ 正确：HTTP 401 + body.code = 1005
response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
response.setContentType(MediaType.APPLICATION_JSON_VALUE);
response.getWriter().write("{\"code\":1005,\"message\":\"Token无效或被篡改\"}");
```

所有 Security Filter 中的认证失败都必须：
1. 设置 `response.setStatus(401)`
2. Body 中 `code` 使用 `AUTH_TOKEN_INVALID(1005)` 等业务码
3. Body 中 `message` 提供具体描述

---

## 6. 后端 ErrorCode 模块划分

| 模块 | 错误码范围 |
|---|---|
| 认证 (Auth) | 1000 - 1999 |
| 用户 (User) | 2000 - 2999 |
| 角色 (Role) | 3000 - 3999 |
| 菜单 (Menu) | 4000 - 4999 |
| 机构 (Organization) | 5000 - 5999 |
| 字典 (Dictionary) | 6000 - 6999 |
| 通用 (Common) | 9500 - 9999 |

---

## 7. 前端类型定义

`src/api/index.ts` 中定义 `ApiResult` 和 `PageResult`：

```typescript
export interface ApiResult<T> {
  code: number;       // HTTP 状态码
  message?: string;   // 信息
  data?: T;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  page?: number;
  size?: number;
  pages?: number;
  hasPrevious?: boolean;
  hasNext?: boolean;
}
```

---

## 8. 前端新增 API 规范

新增 API 时必须：

1. **字段命名与数据库对齐**（由后端控制）
2. **在 `src/api/模块名/model/index.ts` 中定义 TypeScript 类型**
3. **在 `src/api/模块名/index.ts` 中定义请求方法**
4. **返回码判断统一使用 `code === 200`**（见 `request.ts`）
5. **认证错误由 `request.ts` 统一处理**，各 API 不需要单独处理 401 跳转
