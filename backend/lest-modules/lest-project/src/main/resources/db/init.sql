-- 项目管理模块数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS lest_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lest_platform;

-- 项目表
CREATE TABLE IF NOT EXISTS project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL COMMENT '项目名称',
  description TEXT COMMENT '项目描述',
  status TINYINT DEFAULT 1 COMMENT '状态：1-活跃，2-已归档',
  template VARCHAR(32) COMMENT '模板：agile / waterfall / kanban',
  owner_id BIGINT NOT NULL COMMENT '负责人ID',
  start_date DATE COMMENT '开始日期',
  end_date DATE COMMENT '结束日期',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_name (name),
  INDEX idx_owner_id (owner_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 项目成员表
CREATE TABLE IF NOT EXISTS project_member (
  project_id BIGINT NOT NULL COMMENT '项目ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role VARCHAR(16) NOT NULL COMMENT '角色：admin / developer / observer',
  joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (project_id, user_id),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

-- 迭代表
CREATE TABLE IF NOT EXISTS iteration (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL COMMENT '所属项目ID',
  name VARCHAR(64) NOT NULL COMMENT '迭代名称',
  goal VARCHAR(255) COMMENT '迭代目标',
  status TINYINT DEFAULT 1 COMMENT '状态：1-计划中，2-进行中，3-已完成',
  start_date DATE COMMENT '开始日期',
  end_date DATE COMMENT '结束日期',
  completed_at DATETIME COMMENT '完成时间',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_project_id (project_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='迭代表';

-- 里程碑表
CREATE TABLE IF NOT EXISTS milestone (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  project_id BIGINT NOT NULL COMMENT '所属项目ID',
  name VARCHAR(128) NOT NULL COMMENT '里程碑名称',
  description TEXT COMMENT '里程碑描述',
  target_date DATE NOT NULL COMMENT '目标日期',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_project_id (project_id),
  INDEX idx_target_date (target_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='里程碑表';

-- 里程碑与迭代关联表
CREATE TABLE IF NOT EXISTS milestone_iteration (
  milestone_id BIGINT NOT NULL COMMENT '里程碑ID',
  iteration_id BIGINT NOT NULL COMMENT '迭代ID',
  PRIMARY KEY (milestone_id, iteration_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='里程碑与迭代关联表';
