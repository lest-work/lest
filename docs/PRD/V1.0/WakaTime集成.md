# WakaTime 集成 PRD

## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-25 |
| 最后更新 | 2026-05-25 |

---

## 1. 概述

### 1.1 功能背景

LEST Platform 通过 WakaTime 集成实现开发者的**编码活动自动追踪**。WakaTime 是业界标准的开发者时间追踪工具，其插件覆盖 VS Code、IntelliJ、PyCharm 等主流 IDE，能自动记录开发者在每个文件上的编码时间。

本模块实现：
- **心跳追踪**：接收 IDE 插件发送的心跳数据，持久化存储
- **每日汇总**：按天/周/月汇总编码时长、项目分布、语言使用
- **任务关联**：将心跳数据自动关联到具体任务（通过文件路径匹配）
- **绩效供数**：为团队绩效模块提供编码时长、活跃度等数据

### 1.2 功能目标

- 提供 WakaTime 兼容 API（`/compat/wakatime/v1/*`），兼容官方 WakaTime 插件
- 支持心跳数据的接收、存储、查询
- 支持编码时长按项目/语言/编辑器/机器统计
- 支持心跳数据自动关联到任务（基于文件路径）
- 支持手动登记工时与心跳自动追踪并存
- 为团队绩效提供编码活动数据

### 1.3 与现有模块的关系

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         WakaTime 集成与其他模块的关系                     │
└─────────────────────────────────────────────────────────────────────────┘

                            ┌──────────────────┐
                            │  IDE 插件         │
                            │  (VS Code 等)    │
                            └────────┬─────────┘
                                     │ 心跳 POST
                                     ▼
┌──────────────────┐          ┌──────────────────┐
│ 任务管理服务      │          │ WakaTime 服务     │
│ (手动工时登记)    │◄────────│ (lest-wakapi)   │
│ task_worklog     │  关联    │                  │
└────────┬─────────┘          └────────┬─────────┘
         │                             │
         │ 估算工时 vs 实际工时          │ 编码时长
         ▼                             ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                         团队绩效服务 (lest-performance)                     │
│  编码时长（来自 wakapi） + 手动登记工时（来自 task_worklog）= 完整工作时长  │
└──────────────────────────────────────────────────────────────────────────┘
```

### 1.4 目标用户

- **开发者**：通过 IDE 插件自动记录编码时间，查看个人编码统计
- **项目经理**：查看团队编码活跃度、项目时间分布
- **管理者**：查看团队效能报表

---

## 2. 用户故事

### 2.1 心跳接收

#### US-001: 接收心跳数据
**作为** 开发者，**我希望** 我的 IDE 插件自动发送心跳到平台，**以便** 记录编码活动。

验收标准：
- [ ] 提供 WakaTime 兼容的心跳 API（`POST /compat/wakatime/v1/users/current/heartbeats`）
- [ ] 支持批量心跳（单请求可包含多个心跳）
- [ ] 心跳包含：文件路径、项目名、分支、语言、编辑器、操作系统、是否写操作、时间戳
- [ ] 防重处理：相同 entity + time + hash 的心跳不重复存储
- [ ] 心跳写入延迟 < 100ms（异步消费）
- [ ] 返回心跳数量确认

#### US-002: 心跳防重
**作为** 系统，**我希望** 忽略重复的心跳数据，**以便** 避免数据膨胀。

验收标准：
- [ ] 心跳唯一性由 entity + time + hash 组合确定
- [ ] 重复心跳返回成功但不写入数据库
- [ ] 支持心跳去重窗口：5分钟内相同 entity 合并为一条记录

### 2.2 数据查询

#### US-003: 查询编码统计
**作为** 开发者，**我希望** 查看我的编码统计，**以便** 了解个人编码习惯。

验收标准：
- [ ] 支持查询每日/每周/每月编码时长
- [ ] 支持按项目/语言/编辑器/机器维度分组
- [ ] 显示编码时间分布（按小时）
- [ ] 显示活动热力图数据

#### US-004: 查询汇总数据
**作为** 管理者，**我希望** 查看团队编码汇总，**以便** 了解团队活跃度。

验收标准：
- [ ] 支持查询团队所有成员的编码统计
- [ ] 支持按项目/部门筛选
- [ ] 支持导出报表（CSV/Excel）
- [ ] 支持排行榜（编码时长、提交数）

### 2.3 任务关联

#### US-005: 心跳自动关联任务
**作为** 开发者，**我希望** 我的编码时间自动记录到相关任务，**以便** 减少手动登记。

验收标准：
- [ ] 系统根据心跳的文件路径自动匹配关联的任务
- [ ] 匹配规则：心跳的 `project` 字段对应仓库 → 仓库关联的项目 → 项目的任务（通过 commit 或文件路径前缀）
- [ ] 支持手动指定关联任务
- [ ] 关联的任务显示累计编码时长
- [ ] 编码时长计入任务实际工时（可配置是否自动）

#### US-006: 查看任务编码时间
**作为** 开发者，**我希望** 查看任务的编码时间统计，**以便** 了解任务花费的实际时间。

验收标准：
- [ ] 任务详情页显示编码时长（来自 WakaTime）
- [ ] 显示编码时间分布（按天）
- [ ] 显示关联的心跳记录列表
- [ ] 区分：手动登记工时 + 心跳自动追踪工时

### 2.4 绩效供数

#### US-007: 绩效数据接口
**作为** 绩效服务，**我希望** 通过 API 获取编码活动数据，**以便** 计算绩效指标。

验收标准：
- [ ] 提供成员编码时长 API（按时间范围）
- [ ] 提供成员项目时间分布 API
- [ ] 提供成员语言使用统计 API
- [ ] 提供团队编码活跃度 API
- [ ] 数据精确到小时

---

## 3. 功能详细设计

### 3.1 功能清单

| 序号 | 功能点 | 功能描述 | 优先级 |
|------|--------|---------|--------|
| 1 | WakaTime 兼容 API | 实现官方兼容的心跳和查询接口 | P0 |
| 2 | 心跳存储 | 高效存储和查询心跳数据 | P0 |
| 3 | 编码统计查询 | 按天/周/月统计编码时长 | P0 |
| 4 | 每日汇总计算 | 定时计算并存储每日汇总数据 | P0 |
| 5 | 任务自动关联 | 通过文件路径匹配关联任务 | P1 |
| 6 | 项目统计 | 按项目统计编码时间分布 | P1 |
| 7 | 语言统计 | 统计各语言使用时长 | P1 |
| 8 | 排行榜 | 团队编码活跃度排行 | P2 |
| 9 | 绩效数据接口 | 为绩效服务提供 API | P1 |

### 3.2 心跳数据模型（与 WakaTime 官方兼容）

#### 字段映射

| WakaTime 字段 | 类型 | 说明 | LEST Platform 存储 |
|--------------|------|------|------------------|
| entity | string | 文件路径/URL | `entity` |
| type | string | file/domain/url | `entity_type` |
| category | string | coding/browsing | `category` |
| project | string | 项目名 | `project_name` → 映射到 `project_id` |
| branch | string | Git 分支 | `branch` |
| language | string | 编程语言 | `language` |
| is_write | bool | 是否写操作 | `is_write` |
| editor | string | 编辑器名 | `editor` |
| operating_system | string | 操作系统 | `os` |
| machine | string | 机器名 | `machine_name` |
| time | float | Unix 时间戳（秒） | `heartbeat_time` |
| lines | int | 文件总行数 | `lines_total` |
| lineno | int | 当前行号 | `lineno` |
| cursorpos | int | 光标位置 | `cursorpos` |
| is_heartbeat | bool | 是否心跳 | `is_heartbeat` |
| user_agent | string | User-Agent | `user_agent` |

### 3.3 心跳与任务关联机制

#### 关联规则优先级

```
1. 精确匹配：心跳 entity（文件路径）= 任务关联的代码文件路径
   ↓ 匹配到 → 直接关联

2. 项目匹配：心跳 project（项目名）= 仓库名 → 仓库关联项目 → 
   项目下存在 commit 或最近活跃的任务
   ↓ 匹配到 → 关联最近活跃任务

3. 手动指定：用户手动将心跳关联到指定任务
   ↓ 指定 → 强制关联

4. 不关联：无法匹配时仅记录心跳，不关联任务
```

#### 关联存储

心跳关联任务后，写入 `wakapi_task_link` 表（心跳-任务关联表），避免在心跳主表直接存储任务 ID，保持灵活性。

### 3.4 数据流向

```
IDE 插件
    │
    │ 1. POST /compat/wakatime/v1/users/current/heartbeats
    ▼
WakaTime 服务
    │
    ├── 实时写入 ──► wakapi_heartbeat 表（原始心跳）
    │
    ├── 异步队列 ──► Kafka (wakapi.heartbeat.process.v1)
    │                  │
    │                  └──► 消费者：
    │                         ① 任务关联（匹配 entity → task_id）
    │                         ② 写入 wakapi_task_link 表
    │                         ③ 更新用户/项目/语言统计
    │
    └── 定时任务（每日 00:05）
              │
              └──► 计算昨日汇总
                    │
                    └──► 写入 wakapi_daily_summary 表
```

### 3.5 页面原型

#### 3.5.1 个人编码统计页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 我的编码统计                                        [今日 ▼] [导出]         │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  总编码时长 │ 今日活跃 │ 本周活跃 │ 本月活跃                            │
│   1,234h   │   5.2h   │   32.5h  │   156h                            │
│                                                                              │
├────────────────────────────────────────────────────────────────────────────┤
│ 编码时间分布                                                                    │
│ ┌──────────────────────────────────────────────────────────────────────────┐│
│ │ 00 █                                                                    ││
│ │ 02 █                                                                    ││
│ │ 04 █                                                                    ││
│ │ 06 ██                                                                  ││
│ │ 08 ████████                                                             ││
│ │ 10 ██████████████                                                       ││
│ │ 12 ██████████                                                           ││
│ │ 14 ██████████████████                                                   ││
│ │ 16 ██████████████████████████                                           ││
│ │ 18 ██████████████████                                                   ││
│ │ 20 ████████████████                                                     ││
│ │ 22 ██████████                                                           ││
│ │ (今日编码时间分布，横轴：小时，纵轴：活跃度)                                    ││
│ └──────────────────────────────────────────────────────────────────────────┘│
│                                                                              │
│ 项目分布                         │ 语言分布                                    │
│ ┌────────────────────────────┐  │ ┌────────────────────────────────────────┐│
│ │ lest-platform    ████████  │  │ │ TypeScript ████████████  45%          ││
│ │   45%          8.2h       │  │ │ Python     ████████      30%          ││
│ │ lest-frontend   █████      │  │ │ Go         █████         15%          ││
│ │   25%          4.5h       │  │ │ Other      ████          10%          ││
│ │ lest-task       ███        │  │ │                                            ││
│ │   15%          2.7h       │  │ │                                            ││
│ │ other          ███         │  │ │                                            ││
│ │   15%          2.7h       │  │ │                                            ││
│ └────────────────────────────┘  │ └────────────────────────────────────────┘│
│                                                                              │
└────────────────────────────────────────────────────────────────────────────┘
```

#### 3.5.2 心跳记录页

```
┌────────────────────────────────────────────────────────────────────────────┐
│ 心跳记录          [搜索...] [任务筛选 ▼] [日期范围]                          │
├────────────────────────────────────────────────────────────────────────────┤
│ ┌────────┬──────────────┬────────┬────────┬────────┬──────────────────┐  │
│ │  时间   │   文件       │  项目  │ 语言   │  时长  │ 关联任务          │  │
│ ├────────┼──────────────┼────────┼────────┼────────┼──────────────────┤  │
│ │ 16:45  │ api/user.ts │ 前端   │ TS     │ 5m     │ 用户模块开发      │  │
│ │ 16:32  │ utils/helper│ 前端   │ TS     │ 8m     │ 工具函数优化      │  │
│ │ 16:15  │ store/user  │ 前端   │ TS     │ 12m    │ 用户状态管理      │  │
│ │ 15:50  │ app.go      │ 后端   │ Go     │ 25m    │ API 开发         │  │
│ │ 15:20  │ handler.go  │ 后端   │ Go     │ 20m    │ API 开发         │  │
│ └────────┴──────────────┴────────┴────────┴────────┴──────────────────┘  │
│                                                                              │
│ [上一页]  [1] [2] [3] ... [20] [下一页]                      共 156 条    │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. API 设计

### 4.1 WakaTime 兼容 API

#### 4.1.1 发送心跳
```
POST /compat/wakatime/v1/users/current/heartbeats
Content-Type: application/json
X-API-Key: {api_key}

请求体（单个心跳）：
{
  "entity": "/Users/zhangsan/project/src/main.go",
  "type": "file",
  "category": "coding",
  "project": "lest-platform",
  "branch": "feature/user-module",
  "language": "Go",
  "is_write": true,
  "editor": "GoLand",
  "operating_system": "macos",
  "machine": "zhangsan-macbook",
  "time": 1748169600,
  "lines": 150,
  "lineno": 45,
  "cursorpos": 120
}

请求体（批量心跳）：
{
  "heartbeats": [
    { ... },
    { ... }
  ]
}

响应：
{
  "code": 200,
  "data": {
    "sent": 2,
    "accepted": 2,
    "rejected": 0
  }
}
```

#### 4.1.2 查询编码统计
```
GET /compat/wakatime/v1/users/current/stats?range=last_7_days
Authorization: Bearer {token}

响应：
{
  "code": 200,
  "data": {
    "range": {
      "start": "2026-05-18T00:00:00Z",
      "end": "2026-05-25T00:00:00Z"
    },
    "grand_total": {
      "hours": 32,
      "minutes": 45,
      "total_seconds": 117900,
      "text": "32 hrs 45 mins",
      "digital": "32:45"
    },
    "daily_average": {
      "hours": 4,
      "minutes": 41,
      "total_seconds": 16843,
      "text": "4 hrs 41 mins"
    },
    "editors": [
      { "name": "VSCode", "total_seconds": 72000, "percent": 61.1 }
    ],
    "languages": [
      { "name": "TypeScript", "total_seconds": 54000, "percent": 45.8 },
      { "name": "Python", "total_seconds": 36000, "percent": 30.5 }
    ],
    "operating_systems": [
      { "name": "macOS", "total_seconds": 117900, "percent": 100 }
    ]
  }
}
```

#### 4.1.3 查询汇总
```
GET /compat/wakatime/v1/users/current/summaries?start=2026-05-18&end=2026-05-25
Authorization: Bearer {token}

响应：
{
  "code": 200,
  "data": {
    "start": "2026-05-18",
    "end": "2026-05-25",
    "languages": [
      { "name": "TypeScript", "total_seconds": 54000, "percent": 45.8 }
    ],
    "editors": [...],
    "operating_systems": [...],
    "projects": [
      { "name": "lest-platform", "total_seconds": 72000, "percent": 61.1 }
    ],
    "branches": [...],
    "days": [
      {
        "date": "2026-05-18",
        "grand_total": { "total_seconds": 18000, "text": "5 hrs 0 mins" },
        "languages": [...],
        "projects": [...]
      }
    ]
  }
}
```

#### 4.1.4 查询全部累计
```
GET /compat/wakatime/v1/users/current/all_time_since_today
Authorization: Bearer {token}

响应：
{
  "code": 200,
  "data": {
    "range": {
      "start_date": "2021-03-15",
      "end_date": "2026-05-25",
      "name": "all_time"
    },
    "grand_total": {
      "hours": 4521,
      "minutes": 34,
      "total_seconds": 16281240,
      "text": "4521 hrs 34 mins",
      "digital": "4521:34"
    }
  }
}
```

### 4.2 内部业务 API

#### 4.2.1 获取用户编码统计
```
GET /wakapi/stats/user/{userId}?range=month&projectId=1
Authorization: Bearer {token}

响应：
{
  "code": 200,
  "data": {
    "userId": 1,
    "range": "month",
    "totalSeconds": 112320,
    "totalHours": 31.2,
    "activeDays": 18,
    "byProject": [
      { "projectId": 1, "projectName": "lest-platform", "seconds": 72000 }
    ],
    "byLanguage": [
      { "language": "TypeScript", "seconds": 54000 }
    ],
    "byEditor": [
      { "editor": "VSCode", "seconds": 72000 }
    ],
    "dailyAverage": 6240
  }
}
```

#### 4.2.2 获取团队编码排行
```
GET /wakapi/stats/team?projectId=1&range=week
Authorization: Bearer {token}

响应：
{
  "code": 200,
  "data": {
    "range": "week",
    "totalSeconds": 540000,
    "members": [
      { "userId": 1, "username": "zhangsan", "nickname": "张三", "seconds": 93600, "rank": 1 },
      { "userId": 2, "username": "lisi", "nickname": "李四", "seconds": 86400, "rank": 2 }
    ]
  }
}
```

#### 4.2.3 手动关联心跳到任务
```
POST /wakapi/heartbeat/link
Authorization: Bearer {token}
Content-Type: application/json

{
  "heartbeatIds": [1, 2, 3],
  "taskId": 123
}

响应：
{
  "code": 200,
  "message": "success",
  "data": { "linkedCount": 3 }
}
```

#### 4.2.4 获取任务编码时间
```
GET /wakapi/stats/task/{taskId}?range=month
Authorization: Bearer {token}

响应：
{
  "code": 200,
  "data": {
    "taskId": 123,
    "manualWorklogHours": 5.0,
    "heartbeatHours": 12.5,
    "totalHours": 17.5,
    "dailyBreakdown": [
      { "date": "2026-05-20", "seconds": 7200 }
    ]
  }
}
```

---

## 5. 数据库设计

### 5.1 数据库 Schema

WakaTime 服务使用独立的 `wakapi_db` Schema。

### 5.2 表结构

#### wakapi_heartbeat 心跳记录表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_id | BIGINT | NOT NULL | 用户ID |
| entity | VARCHAR(512) | NOT NULL | 文件路径/URL |
| entity_type | VARCHAR(16) | NOT NULL DEFAULT 'file' | 实体类型：file/url/domain |
| category | VARCHAR(32) | NOT NULL DEFAULT 'coding' | 分类：coding/browsing |
| project_id | BIGINT | DEFAULT NULL | 关联项目ID（映射 project_name） |
| repository_id | BIGINT | DEFAULT NULL | 关联仓库ID |
| project_name | VARCHAR(128) | DEFAULT NULL | 项目名（原始值） |
| branch | VARCHAR(256) | DEFAULT NULL | Git 分支 |
| language | VARCHAR(64) | DEFAULT NULL | 编程语言 |
| is_write | TINYINT | NOT NULL DEFAULT 0 | 是否写操作 |
| editor | VARCHAR(64) | DEFAULT NULL | 编辑器名 |
| os | VARCHAR(64) | DEFAULT NULL | 操作系统 |
| machine_name | VARCHAR(128) | DEFAULT NULL | 机器名 |
| machine_id | VARCHAR(64) | DEFAULT NULL | 机器ID |
| heartbeat_time | DATETIME | NOT NULL | 心跳时间 |
| lines_total | INT | DEFAULT NULL | 文件总行数 |
| lineno | INT | DEFAULT NULL | 当前行号 |
| cursorpos | INT | DEFAULT NULL | 光标位置 |
| user_agent | VARCHAR(256) | DEFAULT NULL | User-Agent |
| heartbeat_hash | VARCHAR(64) | DEFAULT NULL | 防重哈希（entity+time 的 MD5） |
| created_at | DATETIME | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| **索引** | | | |
| uk_heartbeat_hash | | UNIQUE | 心跳哈希唯一索引（防重） |
| idx_user_time | | | user_id + heartbeat_time 联合索引 |
| idx_project_name | | | project_name 索引 |
| idx_language | | | language 索引 |

#### wakapi_daily_summary 每日编码汇总表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_id | BIGINT | NOT NULL | 用户ID |
| summary_date | DATE | NOT NULL | 汇总日期 |
| total_seconds | INT | NOT NULL DEFAULT 0 | 总编码秒数 |
| total_hours | DECIMAL(6,2) | NOT NULL DEFAULT 0 | 总编码小时数 |
| active_minutes | INT | NOT NULL DEFAULT 0 | 活跃分钟数（>0的分钟） |
| projects_json | JSON | DEFAULT NULL | 各项目时间分布（JSON数组） |
| languages_json | JSON | DEFAULT NULL | 各语言时间分布 |
| editors_json | JSON | DEFAULT NULL | 各编辑器时间分布 |
| machines_json | JSON | DEFAULT NULL | 各机器时间分布 |
| daily_average | INT | NOT NULL DEFAULT 0 | 日均编码秒数 |
| calculated_at | DATETIME | DEFAULT NULL | 计算时间 |
| created_at | DATETIME | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| UNIQUE KEY uk_user_date | | | user_id + summary_date |
| KEY idx_user_id | | | user_id |
| KEY idx_summary_date | | | summary_date |

#### wakapi_task_link 心跳-任务关联表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| heartbeat_id | BIGINT | NOT NULL | 心跳记录ID |
| task_id | BIGINT | NOT NULL | 关联任务ID |
| link_type | VARCHAR(16) | NOT NULL DEFAULT 'auto' | 关联类型：auto/manual |
| match_rule | VARCHAR(32) | DEFAULT NULL | 匹配规则：file_path/project/commit |
| seconds | INT | NOT NULL DEFAULT 0 | 该心跳记录的编码秒数 |
| linked_at | DATETIME | NOT NULL DEFAULT CURRENT_TIMESTAMP | 关联时间 |
| created_at | DATETIME | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| UNIQUE KEY uk_heartbeat_task | | | heartbeat_id + task_id |
| KEY idx_task_id | | | task_id |
| KEY idx_link_type | | | link_type |

#### wakapi_machine 机器注册表

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| user_id | BIGINT | NOT NULL | 用户ID |
| machine_name | VARCHAR(128) | NOT NULL | 机器名 |
| machine_id | VARCHAR(64) | NOT NULL | 机器唯一ID（WakaTime生成） |
| os | VARCHAR(64) | DEFAULT NULL | 操作系统 |
| last_heartbeat_at | DATETIME | DEFAULT NULL | 最后心跳时间 |
| is_active | TINYINT | NOT NULL DEFAULT 1 | 是否激活 |
| created_at | DATETIME | NOT NULL DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| UNIQUE KEY uk_user_machine | | | user_id + machine_id |
| KEY idx_is_active | | | is_active |

---

## 6. 与任务管理的协同

### 6.1 工时来源对比

| 来源 | 表 | 记录方式 | 精度 | 关联任务 |
|------|---|---------|------|---------|
| 手动登记 | task_worklog | 用户手动输入 | 0.5小时 | 必填 |
| 心跳追踪 | wakapi_heartbeat + wakapi_task_link | IDE自动 | 分钟级 | 可选 |

### 6.2 任务工时展示

任务详情页同时展示两种工时：

```
┌─────────────────────────────────────────────────────────────────┐
│ 工时信息                                                                │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ 预估工时: 8h    手动登记: 5.0h    心跳追踪: 12.5h    总计: 17.5h │ │
│ │ 预估 ████████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ 100%  │ │
│ │ (实际 218%)                                          ⚠️ 超时  │ │
│ └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 6.3 心跳匹配任务算法

```java
// 心跳关联任务的匹配算法
public Long matchTask(WakapiHeartbeat heartbeat) {
    // 1. 尝试精确匹配文件路径
    // 查询 task_commit 中 commit 对应的文件路径是否包含 heartbeat.entity
    List<Long> tasksByFile = taskCommitDao.findTasksByFilePath(
        heartbeat.getProjectName(),
        heartbeat.getEntity()
    );
    if (tasksByFile.size() == 1) {
        return tasksByFile.get(0);
    }

    // 2. 尝试项目+最近活跃任务匹配
    // 获取仓库 → 项目 → 该项目下最近7天内有代码提交的未完成任务
    if (StringUtils.isNotBlank(heartbeat.getProjectName())) {
        Long taskId = taskDao.findRecentActiveTaskByProjectName(
            heartbeat.getProjectName(),
            7 // days
        );
        if (taskId != null) {
            return taskId;
        }
    }

    // 3. 无法匹配
    return null;
}
```

---

## 7. 错误码

### 7.1 WakaTime 模块错误码 (19000-19999)

| 错误码 | 枚举常量 | HTTP 状态码 | 说明 |
|--------|----------|-------------|------|
| 19000 | `HEARTBEAT_ENTITY_TOO_LONG` | 400 | entity 路径过长 |
| 19001 | `HEARTBEAT_TIME_INVALID` | 400 | 时间戳无效 |
| 19002 | `HEARTBEAT_DUPLICATE` | 200 | 心跳重复（已处理） |
| 19003 | `MACHINE_NOT_FOUND` | 404 | 机器未注册 |
| 19004 | `SUMMARY_NOT_READY` | 503 | 汇总数据计算中 |
| 19005 | `STATS_RANGE_INVALID` | 400 | 统计范围无效 |
| 19006 | `TASK_LINK_NOT_FOUND` | 404 | 心跳-任务关联不存在 |
| 19007 | `WAKATIME_AUTH_FAILED` | 401 | WakaTime API 认证失败 |

---

## 8. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
