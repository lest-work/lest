


---

## 十、项目结构总览

### 10.1 顶层目录结构

```
lest-platform/                         # 项目根目录
│
├── backend/                      # 后端 Java 微服务（Gradle Monorepo）
├── frontend/                          # 前端（Vue 3 + Vite，ele-admin-ts 管理后台）
├── docs/                              # 文档目录（PRD、架构、数据库、API 等）
├── sql/                               # SQL 脚本
├── docker-compose.yml                 # 基础设施编排（MySQL 8.4 + Redis 8.6）
├── start-dev.sh                      # 开发环境启动脚本
└── stop-dev.sh                       # 开发环境停止脚本
```

> **前端 H5 端**：V1.0 阶段暂不开发，H5 页面需求可复用 Web 前端响应式页面。  
> **前端 APP 端**：V1.0 阶段暂不开发。

### 10.2 后端目录结构

```
backend/                          # Gradle Monorepo 根目录
│
├── build.gradle                       # 根构建配置
├── settings.gradle                    # 子项目配置
├── gradlew                            # Gradle Wrapper
│
├── lest-common/                       # 公共模块（所有微服务共享）
│   ├── lest-common-log/              # 日志组件（统一日志格式、MDC）
│   ├── lest-common-redis/             # Redis 组件（序列化、分布式锁）
│   └── lest-common-security/          # 安全组件（JWT、RBAC、ABAC）
│
└── lest-modules/                     # 微服务模块
    ├── lest-gateway/                 # API 网关（Spring Cloud Gateway）
    ├── lest-auth/                    # 认证服务（OAuth2、JWT、登录注册）
    ├── lest-system/                  # 系统管理服务（用户、角色、菜单、字典、机构）
    ├── lest-project/                 # 项目管理服务（项目、成员、迭代）
    ├── lest-task/                   # 任务管理服务（任务、工时、评论、附件）
    ├── lest-code/                   # 代码管理服务（仓库、提交、MR）
    ├── lest-meeting/                # 会议管理服务（会议、行动项、站会）
    ├── lest-notification/             # 通知服务（推送、模板、订阅）
    ├── lest-release/                 # 发布管理服务（版本、审批、部署）
    ├── lest-performance/              # 团队绩效服务（指标、报告）
    ├── lest-wakapi/                 # WakaTime 集成服务（心跳、编码统计）
    ├── lest-ai/                     # AI 服务（网关、代码审查、会议总结）
    ├── lest-file/                   # 文件服务（上传、存储、元数据）
    ├── lest-plugin/                 # 插件系统服务（注册、扩展点、SDK）
    └── lest-open/                   # 开放平台服务（API Key、Webhook、OAuth）
```

### 10.3 前端目录结构

```
frontend/                            # Vue 3 + Vite（基于 ele-admin-ts 模版）
├── src/
│   ├── api/                      # API 调用封装（Axios 封装）
│   │   ├── auth/                  # 认证相关 API
│   │   ├── system/                # 系统管理 API
│   │   ├── project/               # 项目管理 API
│   │   ├── task/                  # 任务管理 API
│   │   └── ...
│   ├── components/                # 公共组件
│   ├── views/                    # 页面视图（管理后台页面）
│   │   ├── auth/                 # 登录、注册
│   │   ├── dashboard/             # 仪表盘
│   │   ├── project/              # 项目管理页面
│   │   ├── task/                 # 任务管理页面
│   │   ├── meeting/              # 会议管理页面
│   │   └── ...
│   ├── router/                   # 路由配置
│   ├── store/                    # Pinia 状态管理
│   ├── utils/                    # 工具函数
│   ├── i18n/                    # 国际化
│   └── styles/                   # 全局样式
├── public/                        # 静态资源
├── vite.config.ts                # Vite 配置
└── package.json
```

> **前端 H5 / APP 端**：V1.0 阶段暂不开发，Web 前端需做好响应式布局，移动端访问时展示简化页面。APP 端计划使用 UniApp 开发，后续版本规划。

---

## 十一、开发流程

### 11.1 整体开发流程

```
需求输入
    │
    ▼
编写 PRD (产品需求文档)
    │
    ▼
业务逻辑设计
    │
    ▼
后端接口开发
    │
    ├── 数据库设计 (Flyway 迁移脚本)
    ├── API 接口开发 (Controller + Service)
    ├── 单元测试
    └── 接口联调
    │
    ▼
前端 PC 端开发
    │
    ├── 页面开发
    ├── 接口对接
    ├── 单元测试
    └── 前后端联调
    │
    ▼
前端 Web 端开发
    │
    ├── 页面开发
    ├── 接口对接
    └── 前后端联调
    │
    ▼
集成测试
    │
    ▼
发布上线
```

### 11.2 单功能开发流程

#### 步骤 1：编写 PRD

每个功能开发前，必须编写 PRD，包含：
- 功能背景和目标
- 用户故事 (User Story)
- 功能详细描述
- 业务流程图
- 页面原型 (可选)
- 验收标准 (Acceptance Criteria)

**PRD 存放位置**：`docs/PRD/`

```
docs/PRD/
├── V1.0/
│   ├── project-management.md        # 项目管理 PRD
│   ├── task-management.md          # 任务管理 PRD
│   └── ...
└── V2.0/
    └── ...
```

#### 步骤 2：设计数据库

根据 PRD，设计数据库表结构：
- 创建 Flyway 迁移脚本 (`V*.sql`)
- 编写数据库设计文档 (`docs/DATABASE.md`)

#### 步骤 3：开发后端接口

**TDD 开发流程**：
1. 先写接口文档 (Swagger/OpenAPI)
2. 根据文档编写单元测试
3. 实现接口代码
4. 跑通测试

**代码规范**：见第十二节

#### 步骤 4：前端 PC 开发

- 根据 PRD 和接口文档开发页面
- 对接后端接口
- 响应式布局适配

#### 步骤 5：前端 H5 开发

- 复用 PC 端组件 (如有可能)
- 针对移动端优化交互
- 单独处理移动端适配

#### 步骤 6：集成测试

- 前后端联调
- 功能测试
- 性能测试

### 11.3 Git 分支管理

```
main (生产分支)
    │
    └── develop (开发分支)
            │
            ├── feature/功能名称     # 功能开发分支
            │   └── 从 develop 拉取
            │
            ├── fix/问题描述        # Bug 修复分支
            │   └── 从 develop 拉取
            │
            └── release/v1.0.0    # 发布分支
                └── 从 develop 拉取，测试通过后合并到 main
```

**分支命名规范**：
- 功能分支：`feature/task-management`
- 修复分支：`fix/login-bug`
- 发布分支：`release/v1.0.0`

---

## 十二、代码规范

### 12.1 Java 代码规范

#### 12.1.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | UpperCamelCase | `ProjectService` |
| 方法名 | lowerCamelCase | `getProjectById` |
| 变量名 | lowerCamelCase | `projectName` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 包名 | 全小写 | `com.lest.auth` |
| 枚举 | UpperCamelCase | `ProjectStatus` |
| 枚举值 | UPPER_SNAKE_CASE | `PROJECT_ACTIVE` |

#### 12.1.2 类设计规范

```java
// 领域实体: 使用 Record (JDK 25)
public record Project(
    Long id,
    String name,
    ProjectStatus status
) {
    // 工厂方法
    public static Project create(String name) {
        return new Project(null, name, ProjectStatus.ACTIVE);
    }

    // 领域行为
    public Project rename(String newName) {
        return new Project(id, newName, status);
    }
}

// 枚举: 状态枚举
public enum ProjectStatus {
    PLANNING("计划中"),
    ACTIVE("进行中"),
    COMPLETED("已完成");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }
}

// DTO: 使用 Record
public record CreateProjectRequest(
    @NotBlank(message = "项目名称不能为空")
    String name,

    String description,

    @NotNull(message = "负责人不能为空")
    Long ownerId
) {}

// VO: 使用 Record
public record ProjectVO(
    Long id,
    String name,
    String status
) {
    // 静态工厂方法
    public static ProjectVO from(Project project) {
        return new ProjectVO(
            project.id(),
            project.name(),
            project.status().name()
        );
    }
}
```

#### 12.1.3 注释规范

```java
/**
 * 项目管理服务
 * <p>
 * 负责项目的创建、更新、删除以及成员管理等功能。
 * 项目是团队协作的核心载体，每个项目包含多个迭代和里程碑。
 *
 * @author LEST Team
 * @since 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectService {

    /**
     * 创建项目
     * <p>
     * 业务流程:
     * 1. 校验项目名称唯一性
     * 2. 创建项目记录
     * 3. 将创建者添加为项目管理员
     * 4. 发布项目创建事件
     *
     * @param request 创建项目请求参数
     * @return 创建成功的项目信息
     * @throws BizException 当项目名称已存在时抛出
     */
    public ApiResult<ProjectVO> createProject(CreateProjectRequest request) {
        // 1. 校验名称唯一性
        if (projectRepository.existsByName(request.name())) {
            throw new BizException(ErrorCode.PROJECT_NAME_EXISTS);
        }

        // 2. 创建项目
        Project project = Project.create(request.name(), request.ownerId());
        Project saved = projectRepository.save(project);

        // 3. 添加项目管理员
        projectMemberService.addMember(saved.id(), request.ownerId(), ProjectRole.ADMIN);

        // 4. 发布领域事件
        eventPublisher.publish(new ProjectCreatedEvent(saved));

        return ApiResult.ok(ProjectVO.from(saved));
    }
}
```

#### 12.1.4 Controller 规范

```java
/**
 * 项目管理接口
 * <p>
 * 提供项目管理相关的 HTTP 接口，包括项目的 CRUD 操作。
 * 所有接口均需要登录认证后方可访问。
 */
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * 创建项目
     */
    @PostMapping
    @RequiresPermissions("project:add")
    public ApiResult<ProjectVO> create(@RequestBody @Valid CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    /**
     * 分页查询项目列表
     *
     * @param name 项目名称 (可选，用于模糊搜索)
     * @param status 项目状态 (可选)
     * @param page 页码 (默认 1)
     * @param size 每页数量 (默认 20)
     */
    @GetMapping("/page")
    public ApiResult<PageResult<ProjectVO>> page(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "20") Long size
    ) {
        return projectService.pageProject(new PageParam(page, size), name, status);
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public ApiResult<ProjectVO> getById(@PathVariable Long id) {
        return projectService.getProjectById(id);
    }
}
```

### 12.2 前端代码规范

#### 12.2.1 Vue 组件规范

```vue
<!-- ProjectList.vue -->
<template>
  <div class="project-list">
    <!-- 搜索栏 -->
    <SearchBar v-model="searchForm" @search="handleSearch" />

    <!-- 项目列表 -->
    <el-table :data="projectList" v-loading="loading">
      <el-table-column prop="name" label="项目名称" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      v-model:page="pagination.page"
      v-model:size="pagination.size"
      :total="pagination.total"
      @change="fetchProjectList"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 项目列表页面
 * @description 展示项目列表，支持搜索、分页、CRUD 操作
 * @author LEST Team
 * @date 2026-05-25
 */
import { ref, reactive, onMounted } from 'vue'
import { getProjectPage, deleteProject } from '@/api/project'
import type { Project } from '@/api/project/types'

// 状态
const loading = ref(false)
const projectList = ref<Project[]>([])
const searchForm = reactive({
  name: '',
  status: ''
})
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 生命周期
onMounted(() => {
  fetchProjectList()
})

// 方法
const fetchProjectList = async () => {
  loading.value = true
  try {
    const res = await getProjectPage({
      ...searchForm,
      page: pagination.page,
      size: pagination.size
    })
    projectList.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchProjectList()
}

// ... 其他方法
</script>

<style scoped lang="scss">
.project-list {
  padding: 16px;
}
</style>
```

#### 12.2.2 API 调用规范

```typescript
// api/project/index.ts
/**
 * 项目管理 API
 * @description 封装项目相关的 HTTP 请求
 * @author LEST Team
 * @date 2026-05-25
 */
import request from '@/utils/request'
import type { ApiResult, PageResult } from '@/types'

/** 项目类型定义 */
export interface Project {
  id: number
  name: string
  status: 'planning' | 'active' | 'completed'
  ownerId: number
  createdAt: string
}

/** 创建项目请求参数 */
export interface CreateProjectDTO {
  name: string
  description?: string
  ownerId: number
}

/**
 * 获取项目分页列表
 * @param params 查询参数
 */
export function getProjectPage(params: {
  name?: string
  status?: string
  page: number
  size: number
}) {
  return request<ApiResult<PageResult<Project>>>({
    url: '/project/page',
    method: 'GET',
    params
  })
}

/**
 * 创建项目
 * @param data 创建参数
 */
export function createProject(data: CreateProjectDTO) {
  return request<ApiResult<Project>>({
    url: '/project',
    method: 'POST',
    data
  })
}
```

### 12.3 Git Commit 规范

使用 Conventional Commits 规范：

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

**type 类型**：

| type | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档更新 |
| style | 代码格式 (不影响功能) |
| refactor | 重构 (非新功能) |
| perf | 性能优化 |
| test | 测试相关 |
| chore | 构建/工具相关 |

**示例**：

```
feat(project): 添加项目成员管理功能

- 支持添加/移除项目成员
- 支持设置成员角色 (管理员/开发者/观察者)

Closes #123
```

### 12.4 数据库命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 数据库名 | 小写下划线 | `lest_project` |
| 表名 | 小写下划线，单数 | `sys_user` |
| 主键 | `id` | `id BIGINT` |
| 外键 | `{表名}_id` | `user_id` |
| 索引 | `idx_{表名}_{列名}` | `idx_sys_user_username` |
| 唯一索引 | `uk_{表名}_{列名}` | `uk_sys_user_username` |
| 创建时间 | `created_at` | `DATETIME` |
| 更新时间 | `updated_at` | `DATETIME` |
| 软删除 | `deleted` | `TINYINT` |

### 12.5 API 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 查询列表 | `GET /{resource}` | `GET /project` |
| 分页查询 | `GET /{resource}/page` | `GET /project/page` |
| 获取详情 | `GET /{resource}/{id}` | `GET /project/1` |
| 创建 | `POST /{resource}` | `POST /project` |
| 更新 | `PUT /{resource}/{id}` | `PUT /project/1` |
| 删除 | `DELETE /{resource}/{id}` | `DELETE /project/1` |
| 批量删除 | `DELETE /{resource}/batch` | `DELETE /project/batch` |
| 复杂操作 | `POST /{resource}/{id}/action` | `POST /project/1/transfer` |

---

## 十三、文档目录结构

```
docs/
├── README.md                         # 文档首页
│
├── PRD/                             # 产品需求文档
│   ├── V1.0/
│   │   ├── project-management.md     # 项目管理 PRD
│   │   ├── task-management.md       # 任务管理 PRD
│   │   ├── meeting-management.md     # 会议管理 PRD
│   │   └── ...
│   └── V2.0/
│       └── ...
│
├── ARCHITECTURE.md                  # 架构文档
│
├── API.md                          # API 接口文档
│
├── DATABASE.md                     # 数据库设计文档
│
├── DEVELOPMENT.md                  # 开发指南
│
├── DEPLOYMENT.md                   # 部署文档
│
└── PLUGIN_DEVELOPMENT.md          # 插件开发指南
```

---

## 十四、PRD 模板

```markdown
# {功能名称} PRD

## 1. 概述

### 1.1 功能背景
描述功能产生的背景和原因。

### 1.2 功能目标
描述功能要达成的目标。

### 1.3 目标用户
- 用户群体 1
- 用户群体 2

## 2. 用户故事

### 2.1 作为 {角色}，我希望 {功能}，以便 {收益}
- 验收标准:
  - [ ] 标准 1
  - [ ] 标准 2

## 3. 功能详细设计

### 3.1 功能清单
| 功能点 | 优先级 | 说明 |
|--------|--------|------|
| 功能点 1 | P0 | 必须实现 |
| 功能点 2 | P1 | 重要 |
| 功能点 3 | P2 | 优化项 |

### 3.2 业务流程
描述核心业务流程。

```
开始 → 步骤 1 → 步骤 2 → 步骤 3 → 结束
```

### 3.3 页面原型
[页面原型描述或截图]

### 3.4 接口设计

#### 3.4.1 获取列表
- 接口: `GET /api/xxx`
- 参数: ...
- 返回: ...

#### 3.4.2 创建
- 接口: `POST /api/xxx`
- 参数: ...
- 返回: ...

## 4. 验收标准

- [ ] 验收标准 1
- [ ] 验收标准 2

## 5. 非功能性要求

- 性能: 页面加载 < 2s
- 安全: 需登录认证
- 兼容性: Chrome/Firefox/Safari 最新版
```

---

## 十五、架构决策记录 (ADR) — 完整版

| ID | 决策 | 状态 | 原因 |
|----|------|------|------|
| ADR-001 | Gradle 构建工具 | 已采纳 | 稳定、团队熟悉、企业友好 |
| ADR-002 | 18 个独立微服务模块 | 已采纳 | 边界清晰 |
| ADR-003 | Monorepo 代码管理 | 已采纳 | 便于开发、代码共享 |
| ADR-004 | Spring Profiles 合并部署 | 已采纳 | 开发体验好 |
| ADR-005 | MVP 使用 Spring Event | 已采纳 | 无网络开销 |
| ADR-006 | MySQL Database 隔离 | 已采纳 | 运维简单，每个服务独立 Database，故障隔离 |
| ADR-007 | JDK 25 + 虚拟线程（可选） | 已采纳 | LTS 版本，生产环境稳定 |
| ADR-008 | OpenTelemetry 链路追踪 | 已采纳 | 2026 事实标准 |
| ADR-009 | MyBatis-Plus ORM | 已采纳 | 团队熟悉 |
| ADR-010 | Flyway 数据库迁移 | 已采纳 | SQL 原生 |
| ADR-011 | DDD 风格模块设计 | 已采纳 | 边界清晰 |
| ADR-012 | 内部 API + 外部 API 分离 | 已采纳 | 契约清晰、安全可控 |
| ADR-013 | lest-system 拆分 | 已采纳 | 避免职责不清 |
| ADR-015 | 前端 Web 端为主，H5/APP 暂缓 | 已采纳 | Web 端为 V1.0 主攻方向，H5 和 APP 在后续版本规划 |
| ADR-016 | 开发顺序: 后端 → 前端 Web | 已采纳 | 稳定优先，移动端暂缓 |
| ADR-017 | PRD 先于开发 | 已采纳 | 业务逻辑先行，减少返工 |
| ADR-018 | 文档放 docs/ 目录 | 已采纳 | 统一管理 |
| ADR-019 | CI/CD 使用 GitHub Actions | 已采纳 | 开源项目友好 |
| ADR-020 | Nacos 配置命名规范 | 已采纳 | 配置管理标准化 |
| ADR-021 | API 版本管理策略 | 已采纳 | URL 路径方式 (/v1/, /v2/) |

---

## 十六、CI/CD 设计

### 16.1 GitHub Actions 流水线

每个微服务独立流水线，Monorepo 模式下通过路径触发：

```yaml
# .github/workflows/backend.yml
name: Backend CI/CD

on:
  push:
    paths:
      - 'backend/**'
  pull_request:
    paths:
      - 'backend/**'

jobs:
  # 公共模块变更检测
  common-changed:
    runs-on: ubuntu-latest
    outputs:
      changed: ${{ steps.filter.outputs.changed }}
    steps:
      - uses: actions/checkout@v4
      - uses: dorny/paths-filter@v2
        id: filter
        with:
          filters: |
            common:
              - 'backend/lest-common/**'
            auth:
              - 'backend/lest-modules/lest-auth/**'
            project:
              - 'backend/lest-modules/lest-project/**'

  # 构建和测试
  build:
    needs: common-changed
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service: [auth, system, task, project, code, meeting, notification, release, performance, wakapi, ai, file, plugin, scheduler, open, reg]
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 25
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '25'

      - name: Build with Gradle
        run: |
          cd backend
          ./gradlew build -x test --no-daemon

      - name: Run Tests
        run: |
          cd backend
          ./gradlew test --no-daemon

      - name: Build Docker Image
        if: github.ref == 'refs/heads/main'
        run: |
          cd backend
          docker build -f lest-modules/lest-${{ matrix.service }}/Dockerfile \
            -t ghcr.io/lest-platform/lest-${{ matrix.service }}:${{ github.sha }} .

      - name: Push to Registry
        if: github.ref == 'refs/heads/main'
        run: |
          echo ${{ secrets.GITHUB_TOKEN }} | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          docker push ghcr.io/lest-platform/lest-${{ matrix.service }}:${{ github.sha }}

  # 构建前端
  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'

      - name: Install dependencies
        run: cd frontend && npm ci

      - name: Lint
        run: cd frontend && npm run lint

      - name: Build
        run: cd frontend && npm run build

      - name: Upload artifacts
        uses: actions/upload-artifact@v4
        with:
          name: frontend-dist
          path: frontend/dist
```

### 16.2 部署流水线

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  release:
    types: [published]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/lest-auth \
            lest-auth=${{ github.event.release.tag_name }} \
            -n lest
          kubectl rollout status deployment/lest-auth -n lest
```

---

## 十七、配置管理规范

### 17.1 Nacos 配置命名规范

**格式**：`{环境}-{服务名}.{配置类型}`

| 配置类型 | 示例 | 说明 |
|---------|------|------|
| application | `dev-auth.application` | 应用配置 |
| datasource | `dev-auth.datasource` | 数据源配置 |
| redis | `dev-auth.redis` | Redis 配置 |
| jwt | `dev-jwt.secret` | JWT 密钥（共享）|
| mysql-common | `prod-mysql-common` | 公共数据库配置 |

### 17.2 共享配置

以下配置为所有服务共享，放在 Nacos 的 `public` 命名空间：

| 配置名 | 内容 | 说明 |
|--------|------|------|
| `jwt.yaml` | 密钥、过期时间 | JWT 相关配置 |
| `mysql-common.yaml` | 连接池大小、超时 | 数据库公共配置 |
| `redis-common.yaml` | 连接配置、TTL | Redis 公共配置 |
| `kafka-common.yaml` | 消费者配置 | Kafka 公共配置 |

### 17.3 服务配置示例

```yaml
# Nacos: dev-auth.application
spring:
  application:
    name: lest-auth
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://mysql:3306/lest_auth?useUnicode=true
    username: root
    password: ${MYSQL_PASSWORD:lest123}
  redis:
    host: redis
    port: 6379
    password: ${REDIS_PASSWORD:lest123}

server:
  port: 8096

# JWT 配置（引用共享配置）
jwt:
  secret: ${JWT_SECRET:defaultSecretKeyForDev}
  access-token-expiration: 900000
  refresh-token-expiration: 604800000
```

---

## 十八、产品功能-技术组件映射矩阵

为了防止后续开发中 AI 开发者因无法将业务功能对应到具体技术组件而产生偏差，特制定本映射矩阵：

| 产品功能板块 | 核心业务需求描述 | 对应微服务模块 | 数据库与表 | 核心技术点与调用链路 |
| :--- | :--- | :--- | :--- | :--- |
| **WakaTime 集成** | 接收 IDE 心跳，统计编码时长，自动关联到敏捷任务 | `lest-wakapi` | `wakapi_db` (`wakapi_heartbeat`, `wakapi_task_link`) | 1. 兼容 WakaTime 协议 API 端点<br>2. 时间窗口匹配算法（匹配任务活动）<br>3. 发送 `lest-wakapi.heartbeatReceived` 事件 |
| **AI 自动化服务** | 任务完成预估分析、MR 自动代码审查、会议纪要自动总结 | `lest-ai` | `ai_db` (`ai_code_review`, `ai_conversation`) | 1. 对接大语言模型（OpenAI/Claude API）<br>2. 提示词工程模板（Prompts）与异步生成重试<br>3. 审查结果通过 FeignClient 写回 `lest-code` / `lest-task` |
| **插件系统** | 管理后台上传插件（.lpkg），提供隔离的运行沙箱与 SDK | `lest-plugin` | `plugin_db` (`plugin_registry`, `plugin_permission`) | 1. 独立 ClassLoader 实现 Java 类加载隔离<br> 2. 数据表按插件 ID 动态路由/添加前缀隔离<br>3. 插件 SDK（双语，OpenFeign 桥接） |
| **敏捷会议行动项** | 敏捷站会/纪要结束后，自动在任务板生成相关的待办行动项 | `lest-meeting` | `meeting_db` (`meet_action_item`) | 1. 纪要编辑完成触发 `lest-meeting.actionItemCreated`<br>2. 通过 FeignClient 调用 `lest-task` 模块自动创建任务 |
| **通知与订阅机制** | 任务分配、状态流转、低活跃度告警等多渠道（邮件/站内信）订阅与推送 | `lest-notification` | `notification_db` (`notification`, `notification_setting`) | 1. 消费各模块 Kafka 领域事件<br>2. 异步邮件线程池与 WebSocket 站内信实时推送 |

---

## 十九、本地单进程（Monolith）与微服务（Distributed）双部署抽象策略

为了降低开发调试成本，LEST Platform 支持**本地单进程（合并部署）**和**云端微服务（独立 Pod）**双重部署模式。严禁硬编码 Feign 调用或本地 Event 调用，必须使用如下抽象接口实现无缝切换。

### 19.1 异步事件发布抽象（Event Publisher）

当需要发布跨服务事件（如 `taskCreated`）时，严禁直接硬编码 `kafkaTemplate.send()`，必须注入统一的 `LestEventPublisher` 接口：

```java
package com.lest.common.core.event;

public interface LestEventPublisher {
    void publish(String topic, Object eventPayload);
}
```

#### 本地合并部署实现（Spring ApplicationEvent）
当 `lest.deployment.mode=monolith` 时生效，事件直接通过 Spring 进程内总线分发，零网络开销：

```java
@Component
@ConditionalOnProperty(name = "lest.deployment.mode", havingValue = "monolith", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalSpringEventPublisher implements LestEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(String topic, Object eventPayload) {
        eventPublisher.publishEvent(new LocalEventEnvelope(topic, eventPayload));
    }
}
```

#### 分布式微服务部署实现（Kafka Event）
当 `lest.deployment.mode=distributed` 时生效，事件推送至 Kafka 集群：

```java
@Component
@ConditionalOnProperty(name = "lest.deployment.mode", havingValue = "distributed")
@RequiredArgsConstructor
public class KafkaEventPublisher implements LestEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(String topic, Object eventPayload) {
        kafkaTemplate.send(topic, eventPayload);
    }
}
```

### 19.2 服务间直接调用抽象（Feign vs Local Inject）

对于同步 RPC（如 `lest-task` 查询 `lest-auth` 验证用户信息），统一通过 Feign 接口声明，但需要为单进程部署模式配置本地直接依赖以避开 HTTP 网关开销。

```java
// 1. 声明 Feign 客户端（在微服务模式下生效）
@FeignClient(name = "lest-auth", contextId = "authClient", path = "/api/auth/v1")
public interface AuthClient {
    @GetMapping("/user/{id}")
    Result<UserVO> getUserById(@PathVariable("id") Long id);
}
```

```java
// 2. 本地模式直接注入本地 Service，避开 Feign HTTP 调用
@Configuration
public class AuthClientConfig {

    @Bean
    @ConditionalOnProperty(name = "lest.deployment.mode", havingValue = "monolith", matchIfMissing = true)
    public AuthClient localAuthClient(UserService userService) {
        // 直接使用 UserService 的本地实现对齐 Feign 接口逻辑
        return userService::getUserById; 
    }
}
```

此设计确保了 AI 在编写 RPC 时，仅需关心 `AuthClient` 的调用，系统会根据配置文件中的 `lest.deployment.mode` 自动加载最优化实现。

---

## 二十、API 版本管理策略

### 20.1 版本策略

- **URL 路径方式**：`/api/v1/{resource}`
- **版本号位置**：跟在 `/api` 之后
- **版本升级规则**：Breaking Change 时升级版本号，旧版本至少保留 6 个月

### 18.2 API 文档

- 使用 springdoc-openapi 自动生成 Swagger 文档
- 访问地址：`/swagger-ui.html`
- OpenAPI JSON：`/v3/api-docs`

### 18.3 版本升级流程

```
当前版本: /api/v1/project

    │
    ├── 修复 Bug ──────────────────────► 继续使用 /api/v1/project
    │         (不改变接口契约)
    │
    └── Breaking Change
              │
              ▼
        保留旧版本 ────────────────► 继续使用 /api/v1/project
              │                   (6 个月内响应)
              │
              ▼
        发布新版本 ────────────────► 使用 /api/v2/project
        (旧版本进入维护模式)
```

### 18.4 版本兼容性

| 变更类型 | 是否 Breaking | 是否需要升级版本 |
|---------|-------------|----------------|
| 新增字段 | 否 | 否 |
| 新增接口 | 否 | 否 |
| 修改字段名 | 是 | 是 |
| 删除字段 | 是 | 是 |
| 修改字段类型 | 是 | 是 |
| 修改必填规则 | 是 | 是 |
| 修改枚举值 | 是 | 是 |
