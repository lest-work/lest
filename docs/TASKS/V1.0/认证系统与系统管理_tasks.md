# 认证系统与系统管理开发任务列表

## 模块上下文

- **微服务模块**：`lest-auth`（端口: 8096）/ `lest-system`（端口: 8001）
- **主目录**：`backend/lest-modules/lest-auth` / `backend/lest-modules/lest-system`
- **包路径**：`com.lest.auth` / `com.lest.system`
- **数据库 Schema**：`lest_auth` / `lest_system`
- **前端 PC 目录**：`frontend-pc/src/views/system/`

---

## 1. 数据库迁移任务（DDL Tasks）

| 任务 ID | 类型 | 目标文件 | 详细实现细节 | 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TSK-AUTH-DDL-101** | DDL | `V1.0.0__init_auth_tables.sql` | 创建 `sys_user` 表：`id`(BIGINT PK)、`username`(VARCHAR 64 UNIQUE NOT NULL)、`password`(VARCHAR 255 BCrypt)、`nickname`(VARCHAR 64)、`email`(VARCHAR 255)、`phone`(VARCHAR 32)、`avatar`(VARCHAR 512)、`sex`(TINYINT DEFAULT 0)、`status`(TINYINT DEFAULT 1)、`org_id`(BIGINT)、`created_at`、`updated_at`、`deleted`(TINYINT DEFAULT 0)。索引：`uk_username`、`idx_status`、`idx_org_id`。 | P0 | 待启动 | 未测试 |
| **TSK-AUTH-DDL-102** | DDL | `V1.0.1__init_role_menu_tables.sql` | 创建 `sys_role`（id/role_code UNIQUE/role_name/description/status/sort/created_at/updated_at/deleted）、`sys_menu`（id/parent_id/menu_name/menu_type 1目录2菜单3按钮/path/component/permission/icon/sort/visible/status/keep_alive/always_show/redirect/created_at/updated_at/deleted）。 | P0 | 待启动 | 未测试 |
| **TSK-AUTH-DDL-103** | DDL | `V1.0.2__init_relation_tables.sql` | 创建 `sys_user_role`（user_id/role_id 联合主键）、`sys_role_menu`（role_id/menu_id 联合主键）。 | P0 | 待启动 | 未测试 |
| **TSK-AUTH-DDL-104** | DDL | `V1.0.3__init_org_dict_tables.sql` | 创建 `sys_organization`（id/parent_id/org_name/org_code UNIQUE/sort/status/created_at/updated_at/deleted）、`sys_dictionary`（id/dict_code UNIQUE/dict_name/description/status/created_at/updated_at/deleted）、`sys_dictionary_data`（id/dict_id FK/data_key/data_value/label/sort/status/created_at/updated_at/deleted，索引 idx_dict_id）。 | P1 | 待启动 | 未测试 |
| **TSK-AUTH-DDL-105** | DDL | `V1.0.4__init_log_tables.sql` | 创建 `sys_operation_log`（id/user_id/username/module/operation/method/url/params TEXT/result TEXT/error_msg TEXT/ip/user_agent/execution_time_ms/created_at，索引 idx_user_id/idx_created_at）、`auth_login_log`（id/user_id/username/ip/user_agent TEXT/status TINYINT/msg/login_time，索引 idx_username/idx_login_time）。 | P1 | 待启动 | 未测试 |

---

## 2. 后端 API 任务

### 2.1 认证接口（lest-auth）

---

#### TSK-AUTH-API-101 · 获取图形验证码

**接口**：`GET /auth/captcha`
**权限**：公开接口，无需 Token

**实现要点**：使用 Kaptcha 生成 4 位字母数字验证码，图片 120×40；UUID 作为 Redis Key，有效期 5 分钟；返回 Base64 图片。

**curl 示例**：
```bash
curl -X GET http://localhost:8096/auth/captcha
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "captcha": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-AUTH-API-102 · 用户登录

**接口**：`POST /auth/login`
**权限**：公开接口，无需 Token

**实现要点**：
1. 校验 Redis 中 UUID 对应验证码，错误抛 `AUTH_CAPTCHA_ERROR(1002)`
2. BCrypt 比对密码，失败抛 `AUTH_USERNAME_PASSWORD_ERROR(1000)`
3. 校验用户状态，禁用抛 `AUTH_ACCOUNT_DISABLED(1003)`
4. 签发 Access Token（15分钟）和 Refresh Token（7天），Refresh Token 存 Redis
5. 记录登录日志到 `auth_login_log`

**curl 示例**：
```bash
curl -X POST http://localhost:8096/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "lest123456",
    "captcha": "a3f9",
    "uuid": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**请求体**：
```json
{
  "username": "admin",
  "password": "lest123456",
  "captcha": "a3f9",
  "uuid": "550e8400-e29b-41d4-a716-446655440000"
}
```

**响应示例（成功）**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4ifQ.xxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.yyy",
    "expiresIn": 900
  }
}
```

**响应示例（验证码错误）**：
```json
{
  "code": 1002,
  "message": "验证码错误",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-AUTH-API-103 · 刷新 Token

**接口**：`POST /auth/refresh`
**权限**：公开接口，无需 Token

**实现要点**：验证 Refresh Token 签名和有效期；检查 Redis 中是否存在（防重放）；删除旧 Token，签发新 Token 对。

**curl 示例**：
```bash
curl -X POST http://localhost:8096/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.yyy"}'
```

**请求体**：
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.yyy"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.new_access_token.xxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.new_refresh_token.yyy",
    "expiresIn": 900
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-AUTH-API-104 · 获取当前用户信息

**接口**：`GET /auth/user`
**权限**：需要 Bearer Token

**实现要点**：从 JWT 提取 userId；查询用户基本信息、角色、菜单权限；menus 组装为树形结构（仅 visible=1）。

**curl 示例**：
```bash
curl -X GET http://localhost:8096/auth/user \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin",
    "nickname": "管理员",
    "email": "admin@lest.dev",
    "phone": "138****8888",
    "avatar": "https://cdn.lest.dev/avatar/1.png",
    "roles": ["admin"],
    "permissions": ["user:list", "user:add", "user:edit", "user:delete", "role:list"],
    "menus": [
      {
        "name": "系统管理",
        "path": "/system",
        "icon": "Setting",
        "children": [
          {
            "name": "用户管理",
            "path": "/system/user",
            "icon": "User",
            "component": "/system/user/index"
          }
        ]
      }
    ]
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-AUTH-API-105 · 修改密码

**接口**：`PUT /auth/password`
**权限**：需要 Bearer Token

**实现要点**：校验旧密码；新旧密码不能相同；格式校验（8-32位含字母和数字）；BCrypt 加密更新；批量删除该用户所有 Refresh Token。

**curl 示例**：
```bash
curl -X PUT http://localhost:8096/auth/password \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "lest123456",
    "newPassword": "NewPass@2026"
  }'
```

**请求体**：
```json
{
  "oldPassword": "lest123456",
  "newPassword": "NewPass@2026"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-AUTH-API-106 · 登出

**接口**：`POST /auth/logout`
**权限**：需要 Bearer Token

**curl 示例**：
```bash
curl -X POST http://localhost:8096/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |


### 2.2 用户管理接口（lest-system）

---

#### TSK-SYS-API-201 · 分页查询用户

**接口**：`GET /system/user/page`
**权限**：需要 Bearer Token + `user:list` 权限

**curl 示例**：
```bash
curl -X GET "http://localhost:8001/system/user/page?username=zhang&status=1&page=1&size=20" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| username | string | 否 | 用户名模糊搜索 |
| phone | string | 否 | 手机号模糊搜索 |
| status | integer | 否 | 0=禁用 1=正常 |
| orgId | long | 否 | 机构 ID |
| startTime | string | 否 | 创建时间起，格式 yyyy-MM-dd |
| endTime | string | 否 | 创建时间止，格式 yyyy-MM-dd |
| page | integer | 是 | 页码，默认 1 |
| size | integer | 是 | 每页条数，默认 20 |

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "zhangsan",
        "nickname": "张三",
        "email": "zhangsan@lest.dev",
        "phone": "138****1111",
        "status": 1,
        "roles": ["developer"],
        "orgName": "研发部",
        "createdAt": "2026-05-25 10:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-202 · 创建用户

**接口**：`POST /system/user`
**权限**：需要 Bearer Token + `user:add` 权限

**实现要点**：校验 username 唯一性和格式（4-32位字母数字下划线）；密码默认 `lest123456` BCrypt 加密；事务内插入 `sys_user` 和 `sys_user_role`；记录操作日志。

**curl 示例**：
```bash
curl -X POST http://localhost:8001/system/user \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "nickname": "张三",
    "email": "zhangsan@lest.dev",
    "phone": "13812345678",
    "roleIds": [2, 3],
    "orgId": 5
  }'
```

**请求体**：
```json
{
  "username": "zhangsan",
  "nickname": "张三",
  "email": "zhangsan@lest.dev",
  "phone": "13812345678",
  "roleIds": [2, 3],
  "orgId": 5
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10,
    "username": "zhangsan"
  }
}
```

**错误响应（用户名已存在）**：
```json
{
  "code": 2001,
  "message": "用户名已存在",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-203 · 更新用户

**接口**：`PUT /system/user/{id}`
**权限**：需要 Bearer Token + `user:edit` 权限

**curl 示例**：
```bash
curl -X PUT http://localhost:8001/system/user/10 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "张三（更新）",
    "email": "zhangsan_new@lest.dev",
    "phone": "13912345678",
    "roleIds": [2],
    "orgId": 6
  }'
```

**请求体**：
```json
{
  "nickname": "张三（更新）",
  "email": "zhangsan_new@lest.dev",
  "phone": "13912345678",
  "roleIds": [2],
  "orgId": 6
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-204 · 删除用户

**接口**：`DELETE /system/user/{id}`
**权限**：需要 Bearer Token + `user:delete` 权限

**curl 示例**：
```bash
curl -X DELETE http://localhost:8001/system/user/10 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误响应（禁止删除超管）**：
```json
{
  "code": 2005,
  "message": "禁止删除超级管理员",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-205 · 修改用户状态

**接口**：`PUT /system/user/{id}/status`
**权限**：需要 Bearer Token + `user:edit` 权限

**curl 示例（禁用用户）**：
```bash
curl -X PUT http://localhost:8001/system/user/10/status \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{"status": 0}'
```

**请求体**：
```json
{
  "status": 0
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-206 · 重置密码

**接口**：`PUT /system/user/{id}/password`
**权限**：需要 Bearer Token + `user:edit` 权限

**curl 示例**：
```bash
curl -X PUT http://localhost:8001/system/user/10/password \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-207 · 批量导入用户

**接口**：`POST /system/user/import`
**权限**：需要 Bearer Token + `user:add` 权限

**curl 示例**：
```bash
curl -X POST http://localhost:8001/system/user/import \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -F "file=@/path/to/users.xlsx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 18,
    "failCount": 2,
    "failDetails": [
      { "row": 3, "reason": "用户名 test_user 已存在" },
      { "row": 7, "reason": "邮箱格式不正确" }
    ]
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P1 | 待启动 | 未测试 |


### 2.3 角色管理接口（lest-system）

---

#### TSK-SYS-API-301 · 分页查询角色

**接口**：`GET /system/role/page`
**权限**：需要 Bearer Token + `role:list` 权限

**curl 示例**：
```bash
curl -X GET "http://localhost:8001/system/role/page?roleName=开发&page=1&size=20" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 2,
        "roleCode": "developer",
        "roleName": "开发者",
        "description": "研发团队角色",
        "status": 1,
        "sort": 2,
        "createdAt": "2026-05-25 10:00:00"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 20
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-302 · 创建角色

**接口**：`POST /system/role`
**权限**：需要 Bearer Token + `role:add` 权限

**curl 示例**：
```bash
curl -X POST http://localhost:8001/system/role \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "roleCode": "developer",
    "roleName": "开发者",
    "description": "研发团队角色",
    "menuIds": [1, 2, 3, 10, 11, 12]
  }'
```

**请求体**：
```json
{
  "roleCode": "developer",
  "roleName": "开发者",
  "description": "研发团队角色",
  "menuIds": [1, 2, 3, 10, 11, 12]
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 5,
    "roleCode": "developer"
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-303 · 更新角色

**接口**：`PUT /system/role/{id}`
**权限**：需要 Bearer Token + `role:edit` 权限

**curl 示例**：
```bash
curl -X PUT http://localhost:8001/system/role/5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "roleName": "开发者（高级）",
    "description": "高级研发团队角色",
    "menuIds": [1, 2, 3, 10, 11, 12, 20, 21]
  }'
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-304 · 删除角色

**接口**：`DELETE /system/role/{id}`
**权限**：需要 Bearer Token + `role:delete` 权限

**curl 示例**：
```bash
curl -X DELETE http://localhost:8001/system/role/5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误响应（角色下有用户）**：
```json
{
  "code": 3003,
  "message": "角色下存在关联用户，无法删除",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-305 · 查询角色已分配菜单

**接口**：`GET /system/role/{id}/menu`
**权限**：需要 Bearer Token + `role:edit` 权限

**curl 示例**：
```bash
curl -X GET http://localhost:8001/system/role/5/menu \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "menuIds": [1, 2, 3, 10, 11, 12]
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

### 2.4 菜单管理接口（lest-system）

---

#### TSK-SYS-API-401 · 获取菜单树

**接口**：`GET /system/menu/tree`
**权限**：需要 Bearer Token + `menu:list` 权限

**curl 示例**：
```bash
curl -X GET "http://localhost:8001/system/menu/tree?status=1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "menuType": 1,
      "path": "/system",
      "icon": "Setting",
      "sort": 1,
      "visible": 1,
      "status": 1,
      "children": [
        {
          "id": 2,
          "menuName": "用户管理",
          "parentId": 1,
          "menuType": 2,
          "path": "/system/user",
          "component": "/system/user/index",
          "icon": "User",
          "sort": 1,
          "permission": "user:list",
          "children": [
            {
              "id": 10,
              "menuName": "新增用户",
              "parentId": 2,
              "menuType": 3,
              "permission": "user:add"
            }
          ]
        }
      ]
    }
  ]
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-402 · 获取当前用户路由

**接口**：`GET /system/menu/routes`
**权限**：需要 Bearer Token

**curl 示例**：
```bash
curl -X GET http://localhost:8001/system/menu/routes \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "path": "/dashboard",
      "name": "Dashboard",
      "component": "/dashboard/index",
      "meta": { "title": "首页", "icon": "HomeFilled" }
    },
    {
      "path": "/system",
      "name": "System",
      "component": "Layout",
      "meta": { "title": "系统管理", "icon": "Setting" },
      "children": [
        {
          "path": "user",
          "name": "SystemUser",
          "component": "/system/user/index",
          "meta": { "title": "用户管理", "icon": "User" }
        }
      ]
    }
  ]
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-403 · 创建菜单

**接口**：`POST /system/menu`
**权限**：需要 Bearer Token + `menu:add` 权限

**curl 示例**：
```bash
curl -X POST http://localhost:8001/system/menu \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "parentId": 1,
    "menuName": "角色管理",
    "menuType": 2,
    "path": "/system/role",
    "component": "/system/role/index",
    "icon": "UserFilled",
    "sort": 2,
    "visible": 1,
    "permission": "role:list"
  }'
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": { "id": 3, "menuName": "角色管理" }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-404 · 更新菜单

**接口**：`PUT /system/menu/{id}`
**权限**：需要 Bearer Token + `menu:edit` 权限

**curl 示例**：
```bash
curl -X PUT http://localhost:8001/system/menu/3 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx" \
  -H "Content-Type: application/json" \
  -d '{
    "menuName": "角色管理",
    "sort": 3,
    "visible": 1
  }'
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

#### TSK-SYS-API-405 · 删除菜单

**接口**：`DELETE /system/menu/{id}`
**权限**：需要 Bearer Token + `menu:delete` 权限

**curl 示例**：
```bash
curl -X DELETE http://localhost:8001/system/menu/3 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

**错误响应（有子菜单）**：
```json
{
  "code": 4001,
  "message": "菜单下存在子菜单，无法删除",
  "data": null
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

### 2.5 操作日志接口

---

#### TSK-SYS-API-701 · 分页查询操作日志

**接口**：`GET /system/operation-log/page`
**权限**：需要 Bearer Token + `log:list` 权限

**curl 示例**：
```bash
curl -X GET "http://localhost:8001/system/operation-log/page?username=admin&module=用户管理&startTime=2026-05-01&endTime=2026-05-31&page=1&size=20" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.xxx"
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "module": "用户管理",
        "operation": "创建用户",
        "method": "com.lest.system.controller.UserController.createUser",
        "url": "/system/user",
        "params": "{\"username\":\"zhangsan\"}",
        "result": "success",
        "ip": "192.168.1.100",
        "executionTimeMs": 45,
        "createdAt": "2026-05-25 10:00:00"
      }
    ],
    "total": 500,
    "page": 1,
    "size": 20
  }
}
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P1 | 待启动 | 未测试 |


---

## 3. 前端 PC 端任务

### 3.1 登录页面

**TSK-AUTH-FE-101** · `@/views/login/index.vue` · 路由 `/login`

**页面原型**：
```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│              🚀  LEST Platform                          │
│                                                         │
│   ┌─────────────────────────────────────────────────┐   │
│   │ 👤  用户名                                       │   │
│   └─────────────────────────────────────────────────┘   │
│                                                         │
│   ┌─────────────────────────────────────────────────┐   │
│   │ 🔒  密码                              [👁 显示]  │   │
│   └─────────────────────────────────────────────────┘   │
│                                                         │
│   ┌──────────────────────┐  ┌──────────────────────┐   │
│   │  验证码输入框         │  │  [图片验证码] [刷新]  │   │
│   └──────────────────────┘  └──────────────────────┘   │
│                                                         │
│   ┌─────────────────────────────────────────────────┐   │
│   │              登  录  (主色按钮)                  │   │
│   └─────────────────────────────────────────────────┘   │
│                                                         │
│   登录失败时：⚠️ 用户名或密码错误（红色提示文字）        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**交互说明**：
- 页面加载时调用 `GET /auth/captcha` 获取验证码图片
- 点击验证码图片重新调用接口刷新
- 点击登录按钮调用 `POST /auth/login`，成功后将 accessToken/refreshToken 存入 Pinia + localStorage，跳转 `/dashboard`
- 失败显示 `ElMessage.error` 错误提示
- 表单校验：用户名必填、密码必填（6-32位）、验证码必填（4位）

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

### 3.2 用户管理页面

**TSK-SYS-FE-201** · `@/views/system/user/index.vue` · 路由 `/system/user`

**页面原型**：
```
┌──────────────────────────────────────────────────────────────────────────┐
│ 用户管理                                                                  │
├──────────────────────────────────────────────────────────────────────────┤
│ [用户名输入框] [手机号输入框] [状态下拉▼] [搜索按钮] [重置按钮]          │
│                                    [+ 新增用户] [📥 导入] [📤 导出]      │
├──────────────────────────────────────────────────────────────────────────┤
│ ☐ │ 序号 │ 用户名    │ 昵称   │ 手机号      │ 角色     │ 状态  │ 操作   │
│───┼──────┼───────────┼────────┼─────────────┼──────────┼───────┼────────│
│ ☐ │  1   │ admin     │ 管理员 │ 138****8888 │ 超级管理员│ ●正常 │[编辑]  │
│   │      │           │        │             │          │       │[重置密码]│
│   │      │           │        │             │          │       │[删除]  │
│───┼──────┼───────────┼────────┼─────────────┼──────────┼───────┼────────│
│ ☐ │  2   │ zhangsan  │ 张三   │ 139****1111 │ 开发者   │ ●正常 │[编辑]  │
│   │      │           │        │             │          │       │[重置密码]│
│   │      │           │        │             │          │       │[删除]  │
│───┼──────┼───────────┼────────┼─────────────┼──────────┼───────┼────────│
│ ☐ │  3   │ lisi      │ 李四   │ 137****2222 │ 测试     │ ○禁用 │[编辑]  │
│   │      │           │        │             │          │       │[重置密码]│
│   │      │           │        │             │          │       │[删除]  │
├──────────────────────────────────────────────────────────────────────────┤
│                                              共 100 条  [< 1 2 3 4 5 >]  │
└──────────────────────────────────────────────────────────────────────────┘
```

**新增/编辑用户弹窗**：
```
┌──────────────────────────────────────────────┐
│ 新增用户                                  [×] │
├──────────────────────────────────────────────┤
│ 用户名 *  [zhangsan________________]          │
│           (4-32位字母数字下划线)              │
│ 昵称      [张三____________________]          │
│ 邮箱      [zhangsan@lest.dev_______]          │
│ 手机号    [13812345678_____________]          │
│ 所属机构  [研发部 ▼ 树形选择器]              │
│ 角色      [开发者 ×] [测试 ×] [+ 添加]       │
├──────────────────────────────────────────────┤
│              [取消]  [确 定（主色按钮）]       │
└──────────────────────────────────────────────┘
```

**交互说明**：
- 页面加载调用 `GET /system/user/page`
- 状态列使用 `el-switch` 开关，切换时调用 `PUT /system/user/{id}/status`
- 删除按钮弹出 `ElMessageBox.confirm` 确认后调用 `DELETE /system/user/{id}`
- 重置密码按钮弹出确认框后调用 `PUT /system/user/{id}/password`
- 新增/编辑按钮打开 `UserFormDialog`
- 按钮权限控制：`v-permission="'user:add'"` 等指令

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

### 3.3 角色管理页面

**TSK-SYS-FE-301** · `@/views/system/role/index.vue` · 路由 `/system/role`

**页面原型**：
```
┌──────────────────────────────────────────────────────────────────────────┐
│ 角色管理                                                                  │
├──────────────────────────────────────────────────────────────────────────┤
│ [角色名称输入框] [角色编码输入框] [状态下拉▼] [搜索] [重置]              │
│                                                        [+ 新增角色]      │
├──────────────────────────────────────────────────────────────────────────┤
│ 序号 │ 角色编码    │ 角色名称   │ 描述         │ 状态  │ 排序 │ 操作     │
│──────┼─────────────┼────────────┼──────────────┼───────┼──────┼──────────│
│  1   │ admin       │ 超级管理员 │ 系统最高权限 │ ●正常 │  1   │[编辑]    │
│      │             │            │              │       │      │[权限分配]│
│      │             │            │              │       │      │[删除]    │
│──────┼─────────────┼────────────┼──────────────┼───────┼──────┼──────────│
│  2   │ developer   │ 开发者     │ 研发团队角色 │ ●正常 │  2   │[编辑]    │
│      │             │            │              │       │      │[权限分配]│
│      │             │            │              │       │      │[删除]    │
├──────────────────────────────────────────────────────────────────────────┤
│                                                共 5 条  [< 1 >]          │
└──────────────────────────────────────────────────────────────────────────┘
```

**权限分配弹窗**：
```
┌──────────────────────────────────────────────────────┐
│ 权限分配 - 开发者                                 [×] │
├──────────────────────────────────────────────────────┤
│ [全选] [全不选] [展开全部] [折叠全部]                │
│                                                      │
│ ☑ 系统管理                                          │
│   ☑ 用户管理                                        │
│     ☑ 查看用户列表 (user:list)                      │
│     ☑ 新增用户 (user:add)                           │
│     ☑ 编辑用户 (user:edit)                          │
│     ☐ 删除用户 (user:delete)                        │
│   ☑ 角色管理                                        │
│     ☑ 查看角色列表 (role:list)                      │
│     ☐ 新增角色 (role:add)                           │
│ ☑ 项目管理                                          │
│   ☑ 项目列表 (project:list)                         │
│   ☑ 新增项目 (project:add)                          │
│                                                      │
├──────────────────────────────────────────────────────┤
│                    [取消]  [保存（主色按钮）]         │
└──────────────────────────────────────────────────────┘
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

### 3.4 菜单管理页面

**TSK-SYS-FE-401** · `@/views/system/menu/index.vue` · 路由 `/system/menu`

**页面原型**：
```
┌──────────────────────────────────────────────────────────────────────────────┐
│ 菜单管理                                                                      │
├──────────────────────────────────────────────────────────────────────────────┤
│ [菜单名称输入框] [状态下拉▼] [搜索] [重置]                [+ 新增菜单]       │
├──────────────────────────────────────────────────────────────────────────────┤
│ 菜单名称          │ 类型   │ 路由地址       │ 权限标识   │ 排序 │ 状态 │ 操作 │
│───────────────────┼────────┼────────────────┼────────────┼──────┼──────┼──────│
│ ▼ ⚙ 系统管理      │ 目录   │ /system        │            │  1   │ ●    │[编辑]│
│                   │        │                │            │      │      │[新增]│
│                   │        │                │            │      │      │[删除]│
│   ▼ 👤 用户管理   │ 菜单   │ /system/user   │ user:list  │  1   │ ●    │[编辑]│
│                   │        │                │            │      │      │[新增]│
│                   │        │                │            │      │      │[删除]│
│      新增用户      │ 按钮   │                │ user:add   │  1   │ ●    │[编辑]│
│                   │        │                │            │      │      │[删除]│
│      编辑用户      │ 按钮   │                │ user:edit  │  2   │ ●    │[编辑]│
│                   │        │                │            │      │      │[删除]│
│      删除用户      │ 按钮   │                │ user:delete│  3   │ ●    │[编辑]│
│                   │        │                │            │      │      │[删除]│
│   ▶ 🔑 角色管理   │ 菜单   │ /system/role   │ role:list  │  2   │ ●    │[编辑]│
│                   │        │                │            │      │      │[新增]│
│                   │        │                │            │      │      │[删除]│
└──────────────────────────────────────────────────────────────────────────────┘
```

**新增/编辑菜单弹窗**：
```
┌──────────────────────────────────────────────────────┐
│ 新增菜单                                          [×] │
├──────────────────────────────────────────────────────┤
│ 菜单类型 *  ● 目录  ○ 菜单  ○ 按钮                  │
│ 上级菜单    [系统管理 ▼ 树形选择器]                  │
│ 菜单名称 *  [用户管理_______________]                 │
│ 路由地址    [/system/user___________]  (菜单时显示)  │
│ 组件路径    [/system/user/index_____]  (菜单时显示)  │
│ 权限标识    [user:list______________]  (按钮时显示)  │
│ 菜单图标    [User ▼ 图标选择器]        (目录/菜单)   │
│ 排序        [1_____]                                 │
│ 是否显示    ● 显示  ○ 隐藏                           │
│ 是否缓存    ● 缓存  ○ 不缓存           (菜单时显示)  │
├──────────────────────────────────────────────────────┤
│                    [取消]  [确 定（主色按钮）]         │
└──────────────────────────────────────────────────────┘
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P0 | 待启动 | 未测试 |

---

### 3.5 操作日志页面

**TSK-SYS-FE-701** · `@/views/system/log/operation.vue` · 路由 `/system/log/operation`

**页面原型**：
```
┌──────────────────────────────────────────────────────────────────────────┐
│ 操作日志                                                                  │
├──────────────────────────────────────────────────────────────────────────┤
│ [操作用户] [操作模块] [操作类型] [时间范围: 2026-05-01 ~ 2026-05-31]     │
│                                              [搜索] [重置] [📤 导出]     │
├──────────────────────────────────────────────────────────────────────────┤
│ 操作用户 │ 操作模块 │ 操作类型 │ 请求地址         │ IP          │ 耗时  │ 结果  │ 时间       │ 操作   │
│──────────┼──────────┼──────────┼──────────────────┼─────────────┼───────┼───────┼────────────┼────────│
│ admin    │ 用户管理 │ 创建用户 │ POST /system/user│ 192.168.1.1 │ 45ms  │ ✅成功│ 05-25 10:00│[详情]  │
│ zhangsan │ 任务管理 │ 更新任务 │ PUT /task/123    │ 192.168.1.2 │ 32ms  │ ✅成功│ 05-25 09:55│[详情]  │
│ lisi     │ 角色管理 │ 删除角色 │ DELETE /role/5   │ 192.168.1.3 │ 28ms  │ ❌失败│ 05-25 09:50│[详情]  │
├──────────────────────────────────────────────────────────────────────────┤
│                                              共 500 条  [< 1 2 3 ... >]  │
└──────────────────────────────────────────────────────────────────────────┘

详情弹窗：
┌──────────────────────────────────────────────────────┐
│ 操作详情                                          [×] │
├──────────────────────────────────────────────────────┤
│ 操作用户：admin                                      │
│ 操作模块：用户管理                                   │
│ 操作类型：创建用户                                   │
│ 请求地址：POST /system/user                          │
│ 请求方法：com.lest.system.UserController.createUser  │
│ IP 地址：192.168.1.100                               │
│ 执行时长：45ms                                       │
│ 操作时间：2026-05-25 10:00:00                        │
│                                                      │
│ 请求参数：                                           │
│ {                                                    │
│   "username": "zhangsan",                            │
│   "nickname": "张三",                                │
│   "email": "zhangsan@lest.dev"                       │
│ }                                                    │
│                                                      │
│ 返回结果：success                                    │
├──────────────────────────────────────────────────────┤
│                              [关 闭]                 │
└──────────────────────────────────────────────────────┘
```

| 优先级 | 状态 | 测试结果 |
| :--- | :--- | :--- |
| P1 | 待启动 | 未测试 |