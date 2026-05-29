---
description: Weekly version release process | 每周版本发布流程
---

## 每周发版流程 / Weekly Release Process

> 发版节奏：每周四（周一开始功能开发，周三提测，周四发布）
> Release cadence: Every Thursday (Mon start dev, Wed QA, Thu release)

### Step 1: 确认本周版本号 / Determine Version Number

运行以下命令查看当前版本和周数：

```bash
git describe --tags --abbrev=0 2>/dev/null || echo "No tags yet"
date +"%Y-W%V"
```

版本规则 / Version rules:
- 每周 MINOR +1（如 v0.1.0 → v0.2.0）
- 当周 Bug 修复 PATCH +1（如 v0.2.0 → v0.2.1）
- 重大架构变更 MAJOR +1（需团队评审）

### Step 2: 更新 CHANGELOG.md

在 `CHANGELOG.md` 中：

1. 将 `[Unreleased]` 区块改为正式版本号和日期，格式：

```
## [v0.X.0] — YYYY-MM-DD (WXX)
```

2. 填写本周主题（中英文）
3. 按模块列出新功能、修复项、Breaking Changes
4. 在文档底部添加新的 `[Unreleased]` 区块，写入下周计划

### Step 3: 更新 docs/MILESTONES.md

- 将对应里程碑的状态从 `🔴 未开始` 或 `🟡 进行中` 改为 `✅ 已完成`
- 更新下一里程碑为 `🟡 进行中`
- 更新交付项表格中各条目状态

### Step 4: 更新 docs/TASKS/ 中对应任务文件

- 将已完成任务状态改为 `🟢 已完成`
- 填写备注/关联提交列

### Step 5: 提交版本 Commit

```bash
git add CHANGELOG.md docs/MILESTONES.md docs/TASKS/
git commit -m "docs: release v0.X.0 (WXX) | 发布 v0.X.0 版本 — [本周主题/Sprint Theme]"
```

### Step 6: 打 Git Tag

```bash
git tag -a v0.X.0 -m "v0.X.0 — [本周主题]

[中文功能摘要]

[English feature summary]"
```

### Step 7: 推送到 GitHub

```bash
git push origin main
git push origin v0.X.0
```

### Step 8: 在 GitHub 创建 Release（可选）

在 GitHub → Releases → Draft new release，选择对应 tag，
粘贴 CHANGELOG.md 对应版本的内容作为 Release Notes（中英双语）。

---

## Commit Message 规范 / Commit Message Convention

格式 / Format:
```
<type>(<scope>): <English description> | <中文描述>
```

类型 / Types:
- `feat` — 新功能 / New feature
- `fix` — Bug 修复 / Bug fix
- `refactor` — 重构（不影响功能）/ Refactoring
- `docs` — 文档变更 / Documentation
- `chore` — 构建/配置变更 / Build or config changes
- `perf` — 性能优化 / Performance improvement
- `test` — 测试相关 / Tests
- `ci` — CI/CD 相关 / CI/CD changes

示例 / Examples:
```
feat(task): add kanban board drag-and-drop | 任务看板支持拖拽更新状态
fix(auth): fix captcha validation on empty input | 修复空验证码时的校验异常
docs: release v0.2.0 (W23) | 发布 v0.2.0 版本 — 项目与任务前端页面
chore: add refe/ to .gitignore | 将参考项目 refe/ 加入 .gitignore
```

---

## 版本历史快速参考 / Version History Quick Reference

| 版本 | 周次 | 主题 | 状态 |
| :--- | :--- | :--- | :--- |
| v0.1.0 | W22 (2026-05-29) | 基础框架搭建 Foundation | ✅ |
| v0.2.0 | W23 (2026-06-05) | 项目与任务前端 Project & Task UI | 🔴 |
| v0.3.0 | W24 (2026-06-12) | 通知与消息 Notification | 🔴 |
| v0.4.0 | W25 (2026-06-19) | 敏捷会议 Agile Meetings | 🔴 |
| v0.5.0 | W26 (2026-06-26) | 发布管理 Release Management | 🔴 |
| v1.0.0 | W32 (2026-08-07) | 正式发布 General Release | 🔴 |
