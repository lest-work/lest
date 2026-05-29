# 后续任务清单

## 已完成 ✅

### 1. 规范文档补充
- ✅ `.windsurf/rules/05-api-conventions.md` - API 设计规范
- ✅ `.windsurf/rules/06-git-workflow.md` - Git 工作流规范
- ✅ `.windsurf/rules/07-testing-conventions.md` - 测试规范
- ✅ `.windsurf/rules/08-deployment-conventions.md` - 部署规范
- ✅ 后端 Controller 规范对齐（@RequiresPermissions + @Log）
- ✅ 前端 v-permission 字符串统一为 3 段格式

### 2. 编译验证
- ✅ `mvn clean compile` 全量通过（31 个模块）
- ✅ 零编译错误

## 待完成 ⏳

### 2. 更新 docs 目录

**任务**：
- [ ] 检查 `docs/TASKS/` 中所有任务的状态（是否已完成）
- [ ] 更新版本号为 V1.0（如有 V2.0 则保留）
- [ ] 验证任务描述与实际实现一致

**命令**：
```bash
# 查看任务文件
ls -la docs/TASKS/V1.0/
cat docs/TASKS/V1.0/项目管理_tasks.md
```

### 3. MySQL 数据初始化

**步骤**：
```bash
# 1. 连接 MySQL
mysql -h localhost -u root -p

# 2. 备份现有数据库（可选）
mysqldump -h localhost -u root -p lest_platform > backup_$(date +%Y%m%d_%H%M%S).sql

# 3. 删除所有表
USE lest_platform;
DROP DATABASE lest_platform;
CREATE DATABASE lest_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lest_platform;

# 4. 执行初始化脚本（按顺序）
SOURCE sql/init.sql;
SOURCE sql/system.sql;
SOURCE sql/project.sql;
SOURCE sql/task.sql;

# 5. 验证
SHOW TABLES;
SELECT COUNT(*) FROM sys_user;
```

**检查初始化 SQL 是否符合要求**：
- [ ] `sql/init.sql` - 基础表结构（user, role, menu, dept, post）
- [ ] `sql/system.sql` - 系统数据（默认用户、角色、菜单）
- [ ] `sql/project.sql` - 项目管理表（project, iteration, milestone）
- [ ] `sql/task.sql` - 任务管理表（task, label, worklog）

### 4. Docker 构建与发布

**步骤**：
```bash
# 1. 构建后端 JAR
cd backend
mvn clean package -DskipTests

# 2. 构建前端
cd frontend-pc
npm run build

# 3. 构建 Docker 镜像
docker-compose build

# 4. 启动容器
docker-compose up -d

# 5. 验证服务
curl http://localhost:8080/actuator/health
curl http://localhost:5173  # 前端
```

### 5. 更新文档

**任务**：
- [ ] 更新 `docs/README.md`
  - 改名：`LEST Platform` → `Lest`（更简洁）
  - 修复图片链接（使用相对路径或 CDN）
  - 更新项目描述
  
- [ ] 更新 `README.md`（根目录）
  - 同上

**建议的项目名称**：
- **Lest** - 简洁、易记（推荐）
- **Lest Platform** - 官方全名（备选）
- **Lest DevOps** - 强调 DevOps（备选）

### 6. 任务状态更新

**需要更新的文件**：
- `docs/TASKS/V1.0/项目管理_tasks.md` - 标记已完成的任务
- `docs/TASKS/V1.0/任务管理_tasks.md` - 标记已完成的任务
- `docs/MILESTONES.md` - 更新里程碑进度

**示例格式**：
```markdown
## 已完成任务

- [x] 项目列表页面
- [x] 项目详情页面
- [x] 项目成员管理
- [x] 迭代管理
- [x] 里程碑管理
- [x] 任务列表页面
- [x] 任务看板
- [x] 任务创建/编辑

## 进行中

- [ ] 任务评论功能
- [ ] 任务依赖管理

## 待开发

- [ ] 甘特图
- [ ] 报表统计
```

## 优先级

1. **高** - MySQL 初始化 + Docker 构建（影响部署）
2. **中** - 更新 docs 和 README（影响文档完整性）
3. **低** - 任务状态更新（参考作用）

## 预计时间

- MySQL 初始化：10-15 分钟
- Docker 构建：20-30 分钟
- 文档更新：30-45 分钟
- **总计**：1-2 小时

## 联系方式

如有问题，请参考：
- `.windsurf/rules/` - 所有规范文档
- `docs/DEPLOYMENT.md` - 部署指南
- `docs/DEVELOPMENT.md` - 开发指南
