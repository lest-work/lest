# 分支管理规范 | Branching Strategy

本项目采用 **Git Flow 简化版**，适配小型工程团队的快速迭代节奏。

---

## 分支结构

```
main          ← 生产就绪代码（只接受来自 develop 的 PR，以及紧急 hotfix）
  └─ develop  ← 集成分支（所有功能在此集成测试）
       ├─ feature/xxx   ← 新功能开发
       ├─ fix/xxx       ← Bug 修复
       ├─ refactor/xxx  ← 重构/性能优化
       └─ chore/xxx     ← 依赖升级/构建配置
```

---

## 分支说明

| 分支 | 用途 | 基于 | 合并到 | 保护 |
|------|------|------|--------|------|
| `main` | 生产发布，每次合并打 tag | — | — | ✅ 需要 PR + Review |
| `develop` | 集成测试主线 | `main` | `main` | ✅ 需要 PR |
| `feature/*` | 新功能（按模块命名） | `develop` | `develop` | ❌ |
| `fix/*` | Bug 修复 | `develop` | `develop` | ❌ |
| `hotfix/*` | 生产紧急修复 | `main` | `main` + `develop` | ❌ |
| `release/*` | 发布预备分支（可选） | `develop` | `main` | ❌ |

---

## 命名规范

```bash
feature/task-kanban-drag        # 任务看板拖拽功能
feature/project-burndown-chart  # 项目燃尽图
fix/login-captcha-refresh       # 登录验证码刷新 Bug
fix/task-status-update          # 任务状态更新异常
hotfix/gateway-auth-timeout     # 网关认证超时紧急修复
refactor/project-mapper-sql     # 项目 Mapper SQL 重构
chore/upgrade-spring-boot-4.0.4 # Spring Boot 升级
```

---

## 工作流程

### 日常功能开发

```bash
# 1. 从 develop 切出功能分支
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name

# 2. 开发、提交（遵循 Conventional Commits）
git add .
git commit -m "feat(project): add burndown chart with ECharts"

# 3. 推送并发起 PR → develop
git push origin feature/your-feature-name
# 在 GitHub 发起 PR：feature/your-feature-name → develop
```

### 生产发布

```bash
# develop 测试通过后
git checkout main
git merge --no-ff develop -m "chore: release v0.3.0"
git tag -a v0.3.0 -m "v0.3.0 - Burndown chart & Task enhancement"
git push origin main --tags
```

### 紧急热修复

```bash
git checkout main
git checkout -b hotfix/critical-issue-description
# 修复代码
git commit -m "fix: resolve critical issue"
git checkout main && git merge --no-ff hotfix/critical-issue-description
git checkout develop && git merge --no-ff hotfix/critical-issue-description
git push origin main develop --tags
```

---

## Commit 规范（Conventional Commits）

格式：`<type>(<scope>): <subject>`

| Type | 用途 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(task): add worklog panel` |
| `fix` | Bug 修复 | `fix(login): captcha not refreshing` |
| `docs` | 文档更新 | `docs: update README branching section` |
| `refactor` | 重构 | `refactor(project): migrate to native mybatis` |
| `chore` | 构建/配置 | `chore: upgrade spring boot to 4.0.4` |
| `test` | 测试 | `test(project): add ProjectService unit tests` |
| `style` | 代码格式 | `style: format java files` |
| `perf` | 性能优化 | `perf(task): optimize list query with index` |

**Scope 参考**：`auth`, `gateway`, `system`, `project`, `task`, `release`, `job`, `frontend`, `sql`, `docker`

---

## 当前活跃分支

| 分支 | 状态 | 说明 |
|------|------|------|
| `main` | 🟢 稳定 | v0.2.0 |
| `develop` | 🟡 开发中 | v0.3.0 集成测试 |

---

> 所有新工作请从 `develop` 切出，不要直接在 `main` 上开发。
