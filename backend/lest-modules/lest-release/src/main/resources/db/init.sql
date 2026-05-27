-- Release Management Database Schema
-- Release Plan
CREATE TABLE IF NOT EXISTS release_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL COMMENT 'Project ID',
    name VARCHAR(255) NOT NULL COMMENT 'Release Name',
    description TEXT COMMENT 'Description',
    `desc` TEXT COMMENT 'Additional Description',
    release_date DATETIME COMMENT 'Scheduled Release Date',
    status TINYINT DEFAULT 0 COMMENT 'Status: 0-Draft 1-Published 2-Building 3-Released 4-Archived',
    build_number INT DEFAULT 0 COMMENT 'Build Number',
    git_tag VARCHAR(255) COMMENT 'Git Tag',
    git_branch VARCHAR(255) COMMENT 'Git Branch',
    changelog TEXT COMMENT 'Changelog',
    release_type TINYINT DEFAULT 0 COMMENT 'Release Type: 0-Standard 1-Hotfix 2-Feature 3-Beta 4-Alpha',
    is_draft TINYINT DEFAULT 1 COMMENT 'Is Draft: 0-No 1-Yes',
    is_stable TINYINT DEFAULT 1 COMMENT 'Is Stable: 0-No 1-Yes',
    download_url VARCHAR(500) COMMENT 'Download URL',
    release_notes TEXT COMMENT 'Release Notes',
    created_by BIGINT NOT NULL COMMENT 'Creator ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
    updated_by BIGINT COMMENT 'Updater ID',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
    deleted TINYINT DEFAULT 0 COMMENT 'Soft Delete: 0-No 1-Yes',
    INDEX idx_project_id (project_id),
    INDEX idx_status (status),
    INDEX idx_release_date (release_date),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Release Plan';

-- Release Artifact
CREATE TABLE IF NOT EXISTS release_artifact (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    release_id BIGINT NOT NULL COMMENT 'Release Plan ID',
    artifact_name VARCHAR(255) NOT NULL COMMENT 'Artifact Name',
    artifact_type VARCHAR(100) NOT NULL COMMENT 'Artifact Type: jar, war, zip, docker',
    file_name VARCHAR(255) COMMENT 'File Name',
    file_path VARCHAR(500) COMMENT 'File Path',
    file_size BIGINT COMMENT 'File Size (bytes)',
    file_hash VARCHAR(64) COMMENT 'File Hash (SHA256)',
    download_url VARCHAR(500) COMMENT 'Download URL',
    download_count INT DEFAULT 0 COMMENT 'Download Count',
    metadata TEXT COMMENT 'Additional Metadata (JSON)',
    uploaded_by BIGINT NOT NULL COMMENT 'Uploader ID',
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Uploaded At',
    deleted TINYINT DEFAULT 0 COMMENT 'Soft Delete: 0-No 1-Yes',
    INDEX idx_release_id (release_id),
    INDEX idx_artifact_type (artifact_type),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Release Artifact';

-- Release Issue (tasks/issues associated with a release)
CREATE TABLE IF NOT EXISTS release_issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    release_id BIGINT NOT NULL COMMENT 'Release Plan ID',
    issue_id BIGINT COMMENT 'Issue ID (from lest-project)',
    task_id BIGINT COMMENT 'Task ID (from lest-task)',
    category TINYINT NOT NULL COMMENT 'Category: 0-New Feature 1-Bug Fix 2-Improvement 3-Documentation 4-Other',
    notes TEXT COMMENT 'Notes',
    added_by BIGINT NOT NULL COMMENT 'Added By User ID',
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Added At',
    deleted TINYINT DEFAULT 0 COMMENT 'Soft Delete: 0-No 1-Yes',
    INDEX idx_release_id (release_id),
    INDEX idx_issue_id (issue_id),
    INDEX idx_task_id (task_id),
    INDEX idx_category (category),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Release Issue';

-- Release Activity Log
CREATE TABLE IF NOT EXISTS release_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    release_id BIGINT NOT NULL COMMENT 'Release Plan ID',
    action_type VARCHAR(50) NOT NULL COMMENT 'Action Type: created, updated, published, built, released, archived',
    action_details TEXT COMMENT 'Action Details',
    actor_id BIGINT NOT NULL COMMENT 'Actor User ID',
    actor_name VARCHAR(100) COMMENT 'Actor Name',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
    INDEX idx_release_id (release_id),
    INDEX idx_action_type (action_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Release Activity Log';

-- Release Webhook Configuration
CREATE TABLE IF NOT EXISTS release_webhook (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL COMMENT 'Project ID',
    name VARCHAR(255) NOT NULL COMMENT 'Webhook Name',
    url VARCHAR(500) NOT NULL COMMENT 'Webhook URL',
    secret VARCHAR(255) COMMENT 'Webhook Secret',
    event_types VARCHAR(500) COMMENT 'Event Types: release.published, release.built, release.released, release.archived',
    is_active TINYINT DEFAULT 1 COMMENT 'Is Active: 0-No 1-Yes',
    created_by BIGINT NOT NULL COMMENT 'Creator ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created At',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated At',
    INDEX idx_project_id (project_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Release Webhook Configuration';
