# LEST Platform 开发指南

## 文档信息

| 属性 | 内容 |
|------|------|
| 版本 | V1.0 |
| 状态 | 已完成 |
| 创建日期 | 2026-05-25 |
| 最后更新 | 2026-05-25 |

---

## 1. 概述

### 1.1 文档目的

本文档是 LEST Platform 的开发指南，旨在帮助开发者快速上手项目开发。涵盖开发环境搭建、工程结构规范、代码规范、Git 工作流、调试技巧等内容。

### 1.2 技术栈概览

| 层级 | 技术选型 | 说明 |
|------|---------|------|
| **后端框架** | Spring Boot 4.0.6 | Java 使用 Spring Boot 4.0.6 核心框架 |
| **ORM** | MyBatis-Plus | Java 使用 MyBatis-Plus 作为 ORM 工具 |
| **数据库** | MySQL 8.4.9 + Redis 8.6.3 | 主数据库 + 缓存 |
| **消息队列** | Kafka 4.3.0 | 服务间异步通信 |
| **前端框架** | Vue 3 + Element Plus | Composition API + Vite |
| **微服务网关** | Spring Cloud Gateway / Kong | API 网关 |
| **服务注册** | Nacos 3.2.1 | 服务发现和配置中心 |
| **容器化** | Docker + Kubernetes | 容器化和编排 |
| **监控** | Prometheus + Grafana | 指标监控 and 可视化 |

---

## 2. 开发环境搭建

### 2.1 环境要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 25 | 后端 Java 服务 |
| Node.js | 18+ LTS | 前端开发 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存和消息队列 |
| Docker | 24.0+ | 容器化支持 |
| Git | 2.30+ | 版本控制 |

### 2.2 开发工具推荐

| 工具 | 说明 |
|------|------|
| IDE | IntelliJ IDEA (后端)、VS Code (前端 + Python) |
| 数据库客户端 | DataGrip、DBeaver、MySQL Workbench |
| API 测试 | Postman、Apifox、Insomnia |
| Git 客户端 | GitHub Desktop、Sourcetree、Git Kraken |
| 容器管理 | Docker Desktop、Portainer |
| 终端 | iTerm2 + Oh My Zsh、Windows Terminal |

### 2.3 项目获取

```bash
# 克隆项目
git clone https://github.com/lest-team/lest-platform.git

# 进入项目目录
cd lest-platform

# 查看项目结构
ls -la
```

### 2.4 环境变量配置

#### 2.4.1 后端环境变量 (.env)

```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/auth_db?useUnicode=true&characterEncoding=utf8
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_password

# Nacos 配置
NACOS_SERVER_ADDR=localhost:8848
NACOS_NAMESPACE=dev

# Kafka 配置
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT 配置
JWT_SECRET=your_secret_key_here_minimum_32_chars
JWT_EXPIRATION=900000

# 日志配置
LOG_LEVEL=DEBUG
```

#### 2.4.2 前端环境变量

```bash
# .env.development
VITE_APP_API_BASE_URL=http://localhost:8080
VITE_APP_WS_URL=ws://localhost:8080

# .env.production
VITE_APP_API_BASE_URL=https://api.lest.com
VITE_APP_WS_URL=wss://api.lest.com
```

### 2.5 数据库初始化

```bash
# 进入数据库脚本目录
cd backend/sql

# 执行初始化脚本
mysql -u root -p < init.sql

# 各服务数据库按 schema 独立初始化
mysql -u root -p < auth_db.sql
mysql -u root -p < project_db.sql
mysql -u root -p < task_db.sql
# ... 其他数据库脚本
```

### 2.6 本地服务启动

#### 2.6.1 后端服务（Java）

```bash
# 进入后端目录
cd backend

# 使用 Gradle 启动（示例：启动认证服务）
./gradlew :lest-modules:lest-auth:bootRun

# 或者使用 IDE 直接运行主类
# 在 IntelliJ IDEA 中导入 Gradle 项目，点击 LestAuthApplication 运行
```

#### 2.6.2 前端开发服务器

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问 http://localhost:5173
```

#### 2.6.3 Docker Compose 启动依赖服务

```bash
# 启动 MySQL、Redis、Nacos 等依赖服务
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose -f docker-compose.dev.yml ps

# 查看日志
docker-compose -f docker-compose.dev.yml logs -f
```

---

## 3. 工程结构规范

### 3.1 整体目录结构

```
lest-platform/
├── backend/                         # 后端 Java 微服务（Gradle Monorepo）
│   ├── build.gradle                 # Gradle Monorepo 根构建配置
│   ├── settings.gradle              # 子模块注册列表
│   ├── lest-common/                 # 公共基础类库（所有微服务共享）
│   │   ├── lest-common-log/         # 日志公共组件
│   │   ├── lest-common-redis/       # Redis 公共组件
│   │   └── lest-common-security/    # 安全公共组件（JWT/权限）
│   └── lest-modules/                # 业务微服务模块
│       ├── lest-gateway/            # API 网关服务
│       ├── lest-auth/               # 认证服务
│       ├── lest-system/             # 系统管理服务
│       ├── lest-project/            # 项目管理服务
│       ├── lest-task/               # 任务管理服务
│       ├── lest-code/               # 代码管理服务
│       ├── lest-meeting/            # 会议管理服务
│       ├── lest-notification/       # 消息通知服务
│       ├── lest-release/            # 发布管理服务
│       ├── lest-performance/        # 团队绩效服务
│       ├── lest-wakapi/             # WakaTime 集成服务
│       ├── lest-ai/                 # AI 服务
│       ├── lest-file/               # 文件服务
│       ├── lest-plugin/             # 插件系统服务
│       ├── lest-open/               # 开放平台服务
│
├── frontend-pc/                     # PC管理端前端（Vue 3 + TS + Vite + Element Plus）
│   ├── src/
│   │   ├── api/                     # API 调用封装
│   │   ├── components/              # 公共组件
│   │   ├── views/                   # 页面视图（管理后台页面）
│   │   ├── router/                  # 路由配置
│   │   └── store/                   # Pinia 状态管理
│   ├── public/                      # 静态资源
│   ├── vite.config.ts               # Vite 配置
│   └── package.json
│
├── docs/                            # 文档中心
│   ├── PRD/                         # 产品需求文档
│   ├── ARCHITECTURE.md              # 架构设计文档
│   ├── DATABASE.md                  # 数据库设计文档
│   ├── API.md                       # API 接口文档
│   └── DEVELOPMENT.md               # 本开发指南
│
├── sql/                             # 数据库初始化脚本
└── docker-compose.yml               # 基础设施编排（MySQL + Redis + Kafka）
```

### 3.2 Java 微服务模块结构

```
lest-modules/lest-auth/
├── src/main/java/com/lest/auth/
│   ├── controller/                 # 控制器层
│   │   ├── AuthController.java
│   │   └── UserController.java
│   ├── service/                    # 服务层
│   │   ├── AuthService.java
│   │   └── UserService.java
│   ├── mapper/                     # 数据访问层
│   │   ├── UserMapper.java
│   │   └── RoleMapper.java
│   ├── entity/                     # 实体类
│   │   ├── domain/               # 领域实体
│   │   ├── dto/                  # 数据传输对象
│   │   └── vo/                   # 视图对象
│   ├── config/                     # 配置类
│   ├── security/                   # 安全相关
│   └── LestAuthApplication.java   # 启动类
├── src/main/resources/
│   ├── application.yml             # 应用配置
│   └── mapper/                    # MyBatis XML
└── src/test/java/                  # 测试
```

### 3.3 前端项目结构

```
src/
├── api/                            # API 接口
│   ├── user.js
│   ├── project.js
│   ├── task.js
│   └── index.js                   # API 统一导出
│
├── components/                     # 公共组件
│   ├── common/                    # 通用组件
│   │   ├── Pagination.vue
│   │   ├── SearchForm.vue
│   │   └── Table.vue
│   └── icons/                    # 图标组件
│
├── views/                          # 页面视图（按模块组织）
│   ├── system/                    # 系统管理
│   │   ├── user/
│   │   ├── role/
│   │   └── menu/
│   ├── project/                  # 项目管理
│   ├── task/                     # 任务管理
│   └── dashboard/               # 仪表盘
│
├── router/                         # 路由配置
│   └── index.js
│
├── store/                          # 状态管理
│   ├── modules/
│   │   ├── user.js
│   │   └── permission.js
│   └── index.js
│
├── utils/                          # 工具函数
│   ├── request.js                # Axios 封装
│   ├── auth.js                  # 认证工具
│   └── format.js                # 格式化工具
│
├── styles/                         # 全局样式
│   ├── variables.scss
│   └── global.scss
│
└── App.vue                        # 根组件
```

### 3.4 包命名规范

| 包名 | 说明 |
|------|------|
| `com.lest.{module}.controller` | 控制器层 |
| `com.lest.{module}.service` | 服务层接口 |
| `com.lest.{module}.service.impl` | 服务层实现 |
| `com.lest.{module}.mapper` | 数据访问层 |
| `com.lest.{module}.entity.domain` | 领域实体 |
| `com.lest.{module}.entity.dto` | 数据传输对象 |
| `com.lest.{module}.entity.vo` | 视图对象 |
| `com.lest.{module}.config` | 配置类 |
| `com.lest.{module}.security` | 安全相关 |
| `com.lest.common` | 公共组件 |

---

## 4. 代码规范

### 4.1 通用命名规范

#### 4.1.1 Java 代码命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | UpperCamelCase | `UserService`, `TaskController` |
| 方法名 | lowerCamelCase | `getUserById`, `createTask` |
| 变量名 | lowerCamelCase | `userId`, `taskList` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE` |
| 包名 | lowercase | `com.lest.auth.controller` |

#### 4.1.2 数据库命名

| 对象 | 规范 | 示例 |
|------|------|------|
| 数据库 | `{service}_db` | `auth_db`, `task_db` |
| 表名 | `snake_case` | `sys_user`, `task_worklog` |
| 字段名 | `snake_case` | `user_id`, `created_at` |
| 索引名 | `idx_{table}_{column}` | `idx_user_id` |

#### 4.1.3 前端代码命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件名 | PascalCase | `UserList.vue`, `TaskCard.vue` |
| 变量名 | camelCase | `userList`, `taskData` |
| 常量 | UPPER_SNAKE_CASE | `API_BASE_URL` |
| CSS 类名 | kebab-case | `.user-card`, `.task-list` |
| 路由路径 | kebab-case | `/user-management` |

### 4.2 Java 代码规范

#### 4.2.1 类设计原则

```java
// ✅ 正确：类职责单一，命名清晰
public class UserServiceImpl implements UserService {
    // ...
}

// ❌ 错误：类名模糊，职责不清
public class Manager {
    // ...
}
```

#### 4.2.2 方法设计原则

```java
// ✅ 正确：方法命名清晰，参数适量
public UserVO getUserById(Long userId) {
    return userMapper.selectById(userId);
}

// ✅ 正确：方法不超过 3 个参数，超过时使用参数对象
public PageResult<UserVO> queryUsers(UserQueryDTO query) {
    // 使用 DTO 封装查询条件
}

// ❌ 错误：参数过多，方法过长
public PageResult<UserVO> queryUsers(String name, Integer status,
    Long orgId, Date startDate, Date endDate, Integer page,
    Integer size, String sort, String order) {
    // 参数过多，应该使用 DTO
}
```

#### 4.2.3 异常处理

```java
// ✅ 正确：使用自定义异常，异常信息清晰
if (user == null) {
    throw new BusinessException("USER_NOT_FOUND", "用户不存在");
}

// ✅ 正确：捕获具体异常，处理合理
try {
    userService.createUser(userDTO);
} catch (DataIntegrityViolationException e) {
    throw new BusinessException("USER_NAME_EXISTS", "用户名已存在");
} catch (Exception e) {
    log.error("创建用户失败", e);
    throw new BusinessException("CREATE_USER_ERROR", "创建用户失败，请稍后重试");
}
```

#### 4.2.4 日志规范

```java
// ✅ 正确：使用 SLF4J，日志级别合理
@Slf4j
@Service
public class UserServiceImpl {

    public void createUser(UserDTO userDTO) {
        log.info("创建用户, username={}", userDTO.getUsername());

        try {
            userMapper.insert(user);
            log.info("创建用户成功, userId={}", user.getId());
        } catch (Exception e) {
            log.error("创建用户失败, username={}", userDTO.getUsername(), e);
            throw e;
        }
    }
}

// ❌ 错误：日志信息不足
log.info("操作成功");
log.error("操作失败", e);  // e 应该带上相关参数
```

### 4.3 前端代码规范

#### 4.3.1 Vue 组件规范

```vue
<!-- ✅ 正确：组件结构清晰，命名规范 -->
<template>
  <div class="user-list">
    <search-form @search="handleSearch" />
    <el-table :data="tableData" v-loading="loading">
      <el-table-column prop="username" label="用户名" />
    </el-table>
    <pagination
      :total="total"
      :page="queryParams.pageNum"
      :limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup>
// ✅ 正确：使用 Composition API，逻辑清晰
import { ref, reactive, onMounted } from 'vue'
import { getUserList } from '@/api/user'

// 状态
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  username: ''
})

// 方法
const getList = async () => {
  loading.value = true
  try {
    const { data } = await getUserList(queryParams)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  getList()
})
</script>
```

#### 4.3.2 API 调用规范

```javascript
// ✅ 正确：统一使用封装好的 request
import request from '@/utils/request'

// 用户 API
export const getUserList = (params) => {
  return request({
    url: '/auth/user/page',
    method: 'get',
    params
  })
}

export const createUser = (data) => {
  return request({
    url: '/auth/user',
    method: 'post',
    data
  })
}
```

### 4.4 Git 提交规范

#### 4.4.1 提交信息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### 4.4.2 Type 类型

| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档变更 |
| `style` | 代码格式（不影响功能） |
| `refactor` | 重构（既不是新功能也不是修复） |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建/工具变更 |

#### 4.4.3 示例

```bash
# 新功能
git commit -m "feat(auth): 增加图形验证码登录功能

- 新增图形验证码接口
- 登录时增加验证码校验
- 支持验证码过期自动刷新"

# Bug 修复
git commit -m "fix(task): 修复任务删除后子任务未删除的问题

- 删除任务时级联删除子任务
- 增加删除确认提示"

# 重构
git commit -m "refactor(code): 重构代码审查服务接口

- 统一接口返回格式
- 优化错误处理逻辑"
```

---

## 5. Git 工作流

### 5.1 分支策略

```
main (生产环境)
│
├── develop (开发环境)
│   │
│   ├── feature/user-login    # 用户登录功能
│   ├── feature/task-board     # 任务看板功能
│   └── fix/task-delete-bug   # 任务删除 Bug 修复
│   │
│   └── release/v1.0.0      # 发布分支
```

### 5.2 开发流程

```bash
# 1. 从 develop 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/user-login

# 2. 开发功能，频繁提交
git add .
git commit -m "feat(auth): 实现用户登录基础功能"

# 3. 保持 develop 最新（定期 rebase）
git fetch origin
git rebase origin/develop

# 4. 开发完成后，推送分支
git push origin feature/user-login

# 5. 创建 Pull Request
# 在 GitHub/GitLab 上创建 PR，请求合并到 develop

# 6. Code Review 通过后，合并 PR
# 使用 "Squash and merge" 合并，保持 commit 历史整洁
```

### 5.3 发布流程

```bash
# 1. 从 develop 创建发布分支
git checkout develop
git pull
git checkout -b release/v1.0.0

# 2. 测试和修复
git commit -m "fix: 修复发布前问题"

# 3. 合并到 main
git checkout main
git merge release/v1.0.0 --no-ff
git tag -a v1.0.0 -m "v1.0.0 发布版本"
git push origin main --tags

# 4. 合并回 develop
git checkout develop
git merge release/v1.0.0 --no-ff
git push origin develop

# 5. 删除发布分支
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

---

## 6. 调试技巧

### 6.1 后端调试

#### 6.1.1 Spring Boot 调试

```bash
# 启用调试模式
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# IDE 中配置 Remote Debug
# Host: localhost, Port: 5005
```

#### 6.1.2 日志调试

```java
// 使用日志级别调试
log.debug("调试信息: {}", variable);
log.info("普通信息: {}", variable);
log.warn("警告信息: {}", variable);
log.error("错误信息", exception);

// 临时增加日志级别（无需重启）
# 在 application.yml 中配置
logging:
  level:
    com.lest.auth: DEBUG
    org.springframework.web: DEBUG
```

### 6.2 前端调试

#### 6.2.1 Vue DevTools

```bash
# 安装 Vue DevTools 浏览器扩展
# Chrome: https://chrome.google.com/webstore
```

#### 6.2.2 console 调试

```javascript
// 使用 console 方法
console.log('普通信息');
console.warn('警告信息');
console.error('错误信息');

// 使用断点
debugger;

// 格式化输出
console.table(data);
console.group('用户信息');
console.log('用户名:', user.name);
console.groupEnd();
```

### 6.3 API 调试

#### 6.3.1 使用 curl 测试

```bash
# 登录获取 Token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"your_password","captcha":"1234","uuid":"xxx"}'

# 使用 Token 访问受保护接口
curl -X GET http://localhost:8080/auth/user \
  -H "Authorization: Bearer your_token_here"
```

#### 6.3.2 使用 Postman/Apifox

1. 导入 API 文档（OpenAPI/Swagger）
2. 配置环境变量
3. 设置全局认证 Token
4. 组织好 Collections 和文件夹结构

---

## 7. 测试规范

### 7.1 单元测试

#### 7.1.1 Java 单元测试

```java
// 使用 JUnit 5 + Mockito
@SpringBootTest
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUserById() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        when(userMapper.selectById(1L)).thenReturn(user);

        // When
        User result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }
}
```

#### 7.1.2 前端单元测试

```javascript
// 使用 Vitest + @vue/test-utils
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import UserCard from './UserCard.vue'

describe('UserCard', () => {
  it('renders user name', () => {
    const wrapper = mount(UserCard, {
      props: {
        user: { name: '张三', avatar: '...' }
      }
    })
    expect(wrapper.text()).toContain('张三')
  })
})
```

### 7.2 集成测试

```java
// Spring Boot 集成测试
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.accessToken").exists());
    }
}
```

### 7.3 测试覆盖率

| 级别 | 目标覆盖率 |
|------|----------|
| Service 层 | >= 80% |
| Mapper 层 | >= 70% |
| Controller 层 | >= 60% |
| 整体 | >= 70% |

---

## 8. 部署规范

### 8.1 Docker 镜像构建

```bash
# 后端镜像构建
docker build -f Dockerfile -t lest-platform/lest-auth:latest ./backend-java/lest-modules/lest-auth

# 前端镜像构建
docker build -f Dockerfile -t lest-platform/frontend:latest ./frontend

# 推送镜像
docker push lest-platform/lest-auth:latest
```

### 8.2 Kubernetes 部署

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lest-auth
spec:
  replicas: 2
  selector:
    matchLabels:
      app: lest-auth
  template:
    metadata:
      labels:
        app: lest-auth
    spec:
      containers:
      - name: lest-auth
        image: lest-platform/lest-auth:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

### 8.3 环境配置优先级

```
命令行参数 > 环境变量 > application-{profile}.yml > application.yml
```

---

## 9. 常见问题

### 9.1 数据库连接失败

```
检查清单：
1. MySQL 服务是否启动
2. 端口是否正确（默认 3306）
3. 用户名密码是否正确
4. 数据库是否已创建
5. 防火墙是否允许连接
```

### 9.2 前端 API 请求跨域

```
解决方案：
1. 开发环境：配置 Vite 代理
2. 生产环境：Nginx 反向代理
3. 后端：配置 CORS
```

### 9.3 微服务间调用超时

```
检查清单：
1. 被调用服务是否启动
2. Nacos 服务注册是否正常
3. 网络是否连通
4. 服务是否负载过高
```

### 9.4 Docker 容器无法启动

```bash
# 查看容器日志
docker-compose logs -f service-name

# 进入容器调试
docker exec -it container-name /bin/bash

# 重建容器
docker-compose up -d --force-recreate
```

---

## 10. 参考资源

| 资源 | 链接 |
|------|------|
| Spring Boot 文档 | https://spring.io/projects/spring-boot |
| Vue 3 文档 | https://vuejs.org/ |
| Element Plus | https://element-plus.org/ |
| MyBatis-Plus | https://baomidou.com/ |
| Docker 文档 | https://docs.docker.com/ |
| Kubernetes 文档 | https://kubernetes.io/zh/docs/ |

---

## 附录 A：常用命令速查

### Git 命令

```bash
# 分支操作
git branch                          # 查看本地分支
git branch -a                       # 查看所有分支
git checkout -b feature/xxx         # 创建并切换分支
git merge branch-name                # 合并分支
git branch -d branch-name            # 删除本地分支

# 提交操作
git add .                           # 暂存所有变更
git commit -m "message"              # 提交
git push origin branch-name          # 推送到远程
git pull origin branch-name          # 拉取远程更新

# 查看操作
git log --oneline -10               # 查看最近10条提交
git status                          # 查看工作区状态
git diff                            # 查看变更内容
```

### Docker 命令

```bash
# 容器管理
docker ps                           # 查看运行中的容器
docker ps -a                        # 查看所有容器
docker start container-name          # 启动容器
docker stop container-name           # 停止容器
docker restart container-name        # 重启容器
docker rm container-name             # 删除容器

# 镜像管理
docker images                       # 查看本地镜像
docker rmi image-name                # 删除镜像
docker build -t name:tag .           # 构建镜像

# 日志和调试
docker logs -f container-name       # 查看容器日志
docker exec -it container-name /bin/bash  # 进入容器
```

### NPM 命令

```bash
npm install                          # 安装依赖
npm run dev                          # 启动开发服务器
npm run build                        # 构建生产版本
npm run lint                         # 代码检查
npm run preview                      # 预览构建结果
```

### Gradle 命令

```bash
./gradlew clean build               # 清理并构建
./gradlew :lest-modules:{service}:bootRun  # 启动 Spring Boot 微服务
./gradlew test                      # 运行测试
./gradlew bootJar                   # 打包可执行 JAR
```

---

## 附录 B：全局通用基座与异步上下文透传规范

为了确保 AI 开发者和人工开发的代码具有一致性，防止接口返回结构混乱、丢失链路跟踪，必须强制执行以下规范。

### B.1 全局统一 API 响应包装（Result / PageResult）

所有控制层（`@RestController`）接口必须返回标准 `Result<T>`。分页查询必须返回 `Result<PageResult<T>>`。
使用 **Java 25 Record** 结构定义以达到最大代码简洁性：

```java
package com.lest.common.core.domain;

import java.io.Serializable;

/**
 * 统一 API 响应信封 (Java 25 Record)
 */
public record Result<T>(
    int code,
    String message,
    T data,
    long timestamp
) implements Serializable {
    
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data, System.currentTimeMillis());
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }
    
    public boolean isSuccess() {
        return this.code == 200;
    }
}
```

```java
package com.lest.common.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 统一分页响应包装
 */
public record PageResult<T>(
    List<T> records,
    long total,
    long size,
    long current
) implements Serializable {
    public static <T> PageResult<T> of(List<T> records, long total, long size, long current) {
        return new PageResult<>(records, total, size, current);
    }
}
```

### B.2 统一全局异常处理（GlobalExceptionHandler）

所有微服务统一引入 `RestControllerAdvice`。严禁在 Controller 层使用大量的 try-catch 块返回错误信息：

```java
package com.lest.common.security.handler;

import com.lest.common.core.domain.Result;
import com.lest.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理 JSR380 参数校验异常 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验异常: {}", msg);
        return Result.fail(400, msg);
    }

    /**
     * 兜底处理所有系统未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统未知错误: ", e);
        return Result.fail(500, "系统内部错误，请稍后再试");
    }
}
```

### B.3 异步线程上下文透传规范（JWT / Tenant / Trace ID）

微服务中通过异步线程池（如 `@Async`）执行后台任务时，子线程无法共享主线程的 ThreadLocal（如 MDC 的 Trace ID、Spring Security 上下文以及租户 ID）。
必须在配置类中通过 `TaskDecorator` 实现全量复制：

```java
package com.lest.common.core.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;

/**
 * 线程上下文复制器，防止异步处理时 Trace ID、Security Token、Tenant ID 丢失
 */
public class ContextCopyingDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 1. 获取主线程中的上下文快照
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // 假设的租户上下文，根据项目实际命名替换
        String tenantId = TenantContextHolder.getTenantId(); 

        return () -> {
            try {
                // 2. 复制并恢复上下文到当前执行的子线程
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                SecurityContextHolder.setContext(securityContext);
                TenantContextHolder.setTenantId(tenantId);
                
                // 3. 执行子线程任务
                runnable.run();
            } finally {
                // 4. 清理上下文，防止线程复用导致上下文污染
                MDC.clear();
                SecurityContextHolder.clearContext();
                TenantContextHolder.clear();
            }
        };
    }
}
```

在配置异步线程池时绑定该 Decorator：

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("lest-async-");
        // 绑定上下文装饰器
        executor.setTaskDecorator(new ContextCopyingDecorator());
        executor.initialize();
        return executor;
    }
}
```

---

## 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| V1.0 | 2026-05-25 | 初始版本 | - |
