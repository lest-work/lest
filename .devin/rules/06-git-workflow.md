---
description: Git 分支管理与提交规范
---

# LEST Git 工作流规范

## 1. 分支管理

### 1.1 分支类型

| 分支 | 用途 | 创建自 | 合并到 |
|------|------|--------|--------|
| `main` | 生产发布分支 | - | - |
| `develop` | 开发主分支 | - | `main` |
| `feature/*` | 功能开发 | `develop` | `develop` |
| `bugfix/*` | 缺陷修复 | `develop` | `develop` |
| `hotfix/*` | 紧急修复 | `main` | `main` + `develop` |
| `release/*` | 发布准备 | `develop` | `main` + `develop` |

### 1.2 分支命名规范

```
feature/{module}/{feature-name}
  示例：feature/project/add-kanban-board

bugfix/{module}/{bug-name}
  示例：bugfix/task/fix-status-update

hotfix/{version}/{issue}
  示例：hotfix/v1.0.1/fix-login-error

release/{version}
  示例：release/v1.0.0
```

**模块列表**：
- `auth` - 认证系统
- `system` - 系统管理
- `project` - 项目管理
- `task` - 任务管理
- `release` - 发布管理
- `meeting` - 会议管理
- `notification` - 通知服务
- `file` - 文件服务
- `frontend` - 前端通用

## 2. 提交规范

### 2.1 提交消息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### 2.2 Type 类型

| Type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(project): add kanban board` |
| `fix` | 缺陷修复 | `fix(task): fix status update bug` |
| `refactor` | 代码重构 | `refactor(project): align conventions` |
| `style` | 代码格式 | `style(frontend): format code` |
| `docs` | 文档更新 | `docs: update API documentation` |
| `test` | 测试相关 | `test(task): add unit tests` |
| `chore` | 构建/依赖 | `chore: update dependencies` |
| `ci` | CI/CD 配置 | `ci: add docker build pipeline` |

### 2.3 Scope 范围

- 后端：`{service-name}` (project, task, system, etc.)
- 前端：`{module-name}` (project, task, login, etc.)
- 通用：`core`, `common`, `gateway`, `docker`, `docs`

### 2.4 Subject 主题

- 使用英文，首字母小写
- 不超过 50 字符
- 使用祈使句（如 "add"，而不是 "added" 或 "adds"）
- 不以句号结尾

### 2.5 Body 正文

- 说明代码变更的原因、内容、影响
- 每行不超过 72 字符
- 可选，但复杂改动必须包含

**示例**：
```
refactor(project): align project/task with system module conventions

后端 Controller 规范对齐（与 SysUserController 等 system 模块一致）：

ProjectController:
- 补充 @RequiresPermissions + @Log 注解至所有写操作及查询方法
- 引入 com.lest.common.log.annotation.Log / BusinessType / RequiresPermissions

IterationController:
- 补充 @RequestMapping("") 类注解（此前缺失）
- 补充 @RequiresPermissions + @Log 至所有方法
```

### 2.6 Footer 页脚

用于关闭 Issue 或说明破坏性变更：

```
Closes #123
Closes #124, #125

BREAKING CHANGE: 说明破坏性变更的内容
```

## 3. 工作流程

### 3.1 功能开发流程

```bash
# 1. 从 develop 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/project/add-kanban-board

# 2. 开发并提交
git add .
git commit -m "feat(project): add kanban board view"

# 3. 推送分支
git push origin feature/project/add-kanban-board

# 4. 创建 Pull Request（在 GitHub/GitLab）
# - 标题：feat(project): add kanban board
# - 描述：说明功能、测试方法、相关 Issue

# 5. Code Review 通过后，合并到 develop
# - 使用 Squash and merge（保持历史清晰）
# - 或 Create a merge commit（保留分支历史）

# 6. 删除本地和远程分支
git branch -d feature/project/add-kanban-board
git push origin --delete feature/project/add-kanban-board
```

### 3.2 缺陷修复流程

```bash
# 1. 从 develop 创建 bugfix 分支
git checkout develop
git pull origin develop
git checkout -b bugfix/task/fix-status-update

# 2. 修复并提交
git add .
git commit -m "fix(task): fix task status update bug

修复任务状态更新时的并发问题，添加乐观锁机制"

# 3. 推送并创建 Pull Request
git push origin bugfix/task/fix-status-update

# 4. Code Review 通过后合并到 develop
```

### 3.3 紧急修复流程（Hotfix）

```bash
# 1. 从 main 创建 hotfix 分支
git checkout main
git pull origin main
git checkout -b hotfix/v1.0.1/fix-login-error

# 2. 修复并提交
git add .
git commit -m "fix(auth): fix login error in production"

# 3. 合并到 main 并标记版本
git push origin hotfix/v1.0.1/fix-login-error
# 创建 Pull Request 到 main

# 4. 合并到 develop（防止 main 领先 develop）
git checkout develop
git pull origin develop
git merge hotfix/v1.0.1/fix-login-error
git push origin develop

# 5. 删除 hotfix 分支
git branch -d hotfix/v1.0.1/fix-login-error
git push origin --delete hotfix/v1.0.1/fix-login-error
```

### 3.4 发布流程（Release）

```bash
# 1. 从 develop 创建 release 分支
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# 2. 更新版本号、CHANGELOG 等
# - 修改 pom.xml 版本号
# - 更新 CHANGELOG.md
# - 更新 docs/MILESTONES.md

git add .
git commit -m "chore(release): prepare v1.0.0 release"

# 3. 合并到 main 并标记版本
git push origin release/v1.0.0
# 创建 Pull Request 到 main

# 4. 在 main 上创建版本标签
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 5. 合并回 develop
git checkout develop
git merge main
git push origin develop

# 6. 删除 release 分支
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

## 4. Code Review 规范

### 4.1 审查清单

- [ ] 代码遵循项目规范（后端/前端约定）
- [ ] 功能完整，逻辑正确
- [ ] 没有明显的性能问题
- [ ] 添加了必要的错误处理
- [ ] 添加了单元测试（如适用）
- [ ] 更新了相关文档
- [ ] 没有硬编码的密钥、密码或敏感信息

### 4.2 审查意见格式

```
// 必须修改
🔴 MUST: 这里的异常处理不完整，需要添加日志

// 应该修改
🟡 SHOULD: 这个变量名不够清晰，建议改为 `projectIterationMap`

// 可以改进
🟢 NICE: 可以考虑提取为常量，提高代码复用性

// 赞同
✅ LGTM: 这个实现很优雅，学到了！
```

## 5. 版本号规范

遵循 **Semantic Versioning 2.0.0**：

```
{MAJOR}.{MINOR}.{PATCH}

例如：1.2.3
  1 - MAJOR：不兼容的 API 变更
  2 - MINOR：向后兼容的功能新增
  3 - PATCH：向后兼容的缺陷修复
```

**示例**：
- `1.0.0` - 首个正式版本
- `1.1.0` - 新增功能
- `1.1.1` - 缺陷修复
- `2.0.0` - 重大版本升级（破坏性变更）
