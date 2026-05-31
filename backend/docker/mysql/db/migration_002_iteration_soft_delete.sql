-- ============================================================
-- Migration 002: Add Iteration Soft-Delete Column
-- ============================================================

SET NAMES utf8mb4;

-- Iteration table: add deleted column for soft-delete consistency
ALTER TABLE iteration ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-删除' AFTER end_date;

SET NAMES utf8mb4;
