-- =====================================================
-- lest-task 模块数据库初始化脚本
-- 创建时间: 2026-05-26
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS lest_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lest_platform;

-- =====================================================
-- 任务表
-- =====================================================
CREATE TABLE IF NOT EXISTS task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL COMMENT '任务标题',
  description TEXT COMMENT '任务描述',
  project_id BIGINT NOT NULL COMMENT '所属项目ID',
  iteration_id BIGINT COMMENT '所属迭代ID',
  parent_id BIGINT COMMENT '父任务ID',
  task_type VARCHAR(16) NOT NULL COMMENT '类型：story/task/bug/improvement',
  priority VARCHAR(8) NOT NULL COMMENT '优先级：p0/p1/p2/p3',
  status VARCHAR(16) NOT NULL COMMENT '状态：todo/in_progress/completed',
  assignee_id BIGINT COMMENT '负责人ID',
  start_time DATETIME COMMENT '开始时间',
  completed_at DATETIME COMMENT '完成时间',
  estimated_hours DECIMAL(5,1) COMMENT '预估工时',
  actual_hours DECIMAL(5,1) DEFAULT 0 COMMENT '实际工时',
  due_date DATE COMMENT '截止日期',
  sort INT DEFAULT 0 COMMENT '排序权重',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_project_id (project_id),
  INDEX idx_iteration_id (iteration_id),
  INDEX idx_assignee_id (assignee_id),
  INDEX idx_status (status),
  INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- =====================================================
-- 工时记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS task_worklog (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT NOT NULL COMMENT '任务ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  hours DECIMAL(5,1) NOT NULL COMMENT '工时（小时）',
  description VARCHAR(512) COMMENT '工作描述',
  work_date DATE NOT NULL COMMENT '工作日期',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_task_id (task_id),
  INDEX idx_user_id (user_id),
  INDEX idx_work_date (work_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工时记录表';

-- =====================================================
-- 任务标签关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS task_label (
  task_id BIGINT NOT NULL,
  label_id BIGINT NOT NULL,
  PRIMARY KEY (task_id, label_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务标签关联表';

-- =====================================================
-- 任务依赖表
-- =====================================================
CREATE TABLE IF NOT EXISTS task_dependency (
  task_id BIGINT NOT NULL COMMENT '任务ID',
  dependency_task_id BIGINT NOT NULL COMMENT '依赖任务ID',
  type VARCHAR(16) NOT NULL COMMENT '类型：blocker/blocked_by',
  PRIMARY KEY (task_id, dependency_task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务依赖表';

-- =====================================================
-- 任务评论表
-- =====================================================
CREATE TABLE IF NOT EXISTS task_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT NOT NULL COMMENT '任务ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  content TEXT NOT NULL COMMENT '评论内容',
  parent_id BIGINT COMMENT '父评论ID（回复）',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务评论表';

-- =====================================================
-- 任务关注者表
-- =====================================================
CREATE TABLE IF NOT EXISTS task_watcher (
  task_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  PRIMARY KEY (task_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务关注者表';

-- =====================================================
-- 标签表
-- =====================================================
CREATE TABLE IF NOT EXISTS label (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL COMMENT '所属项目ID',
  name VARCHAR(32) NOT NULL COMMENT '标签名称',
  color VARCHAR(16) NOT NULL COMMENT '标签颜色',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_project_id (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- =====================================================
-- 任务代码关联表（Git提交和MR）
-- =====================================================
CREATE TABLE IF NOT EXISTS task_commit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT COMMENT '任务ID',
  commit_hash VARCHAR(64) COMMENT '提交Hash',
  commit_message VARCHAR(512) COMMENT '提交消息',
  author VARCHAR(128) COMMENT '提交者',
  commit_time DATETIME COMMENT '提交时间',
  repo_id VARCHAR(128) COMMENT '仓库ID',
  mr_id BIGINT COMMENT 'MR ID',
  mr_title VARCHAR(255) COMMENT 'MR标题',
  mr_status VARCHAR(32) COMMENT 'MR状态',
  type VARCHAR(16) NOT NULL COMMENT '关联类型：commit/mr',
  source VARCHAR(16) DEFAULT 'auto' COMMENT '来源：auto/manual',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_task_id (task_id),
  INDEX idx_commit_hash (commit_hash),
  INDEX idx_mr_id (mr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务代码关联表';
