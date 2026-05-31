-- ============================================================
-- Domain Table Naming Convention Migration
-- Align all domain tables with sys_* table naming pattern:
-- PRIMARY KEY: {entity}_id (not just `id`)
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. project: id -> project_id
ALTER TABLE project CHANGE COLUMN `id` `project_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID' FIRST;

-- 2. iteration: id -> iteration_id
ALTER TABLE iteration CHANGE COLUMN `id` `iteration_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '迭代ID' FIRST;

-- 3. milestone: id -> milestone_id
ALTER TABLE milestone CHANGE COLUMN `id` `milestone_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '里程碑ID' FIRST;

-- 4. task: id -> task_id
ALTER TABLE task CHANGE COLUMN `id` `task_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID' FIRST;

-- 5. label: id -> label_id
ALTER TABLE label CHANGE COLUMN `id` `label_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID' FIRST;

-- 6. task_commit: id -> task_commit_id
ALTER TABLE task_commit CHANGE COLUMN `id` `task_commit_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '提交记录ID' FIRST;

-- Fix task_commit column names to match entity field names
ALTER TABLE task_commit CHANGE COLUMN `repo` `repo_id` VARCHAR(200) COMMENT '仓库ID/名称' AFTER `task_commit_id`;
ALTER TABLE task_commit CHANGE COLUMN `commit_hash` `commit_hash` VARCHAR(64) NOT NULL COMMENT '提交哈希' AFTER `repo_id`;
ALTER TABLE task_commit CHANGE COLUMN `message` `commit_message` VARCHAR(500) COMMENT '提交信息' AFTER `commit_hash`;
ALTER TABLE task_commit CHANGE COLUMN `author` `author` VARCHAR(100) COMMENT '提交者' AFTER `commit_message`;
ALTER TABLE task_commit CHANGE COLUMN `committed_at` `commit_time` DATETIME COMMENT '提交时间' AFTER `author`;

-- 7. task_dependency: id -> task_dependency_id
ALTER TABLE task_dependency CHANGE COLUMN `id` `task_dependency_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '依赖关系ID' FIRST;

-- Fix task_dependency column names
ALTER TABLE task_dependency CHANGE COLUMN `depends_on_id` `dependency_task_id` BIGINT NOT NULL COMMENT '依赖的任务ID' AFTER `task_id`;

-- 8. task_worklog: id -> task_worklog_id
ALTER TABLE task_worklog CHANGE COLUMN `id` `task_worklog_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工时记录ID' FIRST;

-- 9. task_comment: id -> task_comment_id
ALTER TABLE task_comment CHANGE COLUMN `id` `task_comment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID' FIRST;

SET FOREIGN_KEY_CHECKS = 1;

-- Verify the changes
SELECT 'project' as tbl, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='lest_platform' AND TABLE_NAME='project' AND COLUMN_NAME IN ('project_id','name','status','template','owner_id','start_date','end_date','deleted','create_by','create_time','update_by','update_time');
SELECT 'task' as tbl, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='lest_platform' AND TABLE_NAME='task' AND COLUMN_NAME IN ('task_id','title','description','project_id','iteration_id','parent_id','task_type','priority','status','assignee_id','start_time','completed_at','estimated_hours','actual_hours','due_date','sort','deleted','create_by','create_time');
SELECT 'task_commit' as tbl, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='lest_platform' AND TABLE_NAME='task_commit';
SELECT 'task_dependency' as tbl, COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='lest_platform' AND TABLE_NAME='task_dependency';
