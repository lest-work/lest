package com.lest.common.mq.constants;

/**
 * Kafka Topic 常量
 */
public final class MqTopicConstants
{
    private MqTopicConstants() {}

    // Task topics
    public static final String TASK_CREATED = "task.created";
    public static final String TASK_UPDATED = "task.updated";
    public static final String TASK_DELETED = "task.deleted";
    public static final String TASK_ASSIGNED = "task.assigned";
    public static final String TASK_ASSIGNEE_CHANGED = "task.assignee.changed";
    public static final String TASK_STATUS_CHANGED = "task.status.changed";
    public static final String TASK_COMMENTED = "task.commented";
    public static final String TASK_MENTIONED = "task.mentioned";

    // Project topics
    public static final String PROJECT_CREATED = "project.created";
    public static final String PROJECT_UPDATED = "project.updated";
    public static final String PROJECT_MEMBER_ADDED = "project.member.added";
    public static final String PROJECT_MEMBER_REMOVED = "project.member.removed";

    // Iteration topics
    public static final String ITERATION_CREATED = "iteration.created";
    public static final String ITERATION_UPDATED = "iteration.updated";
    public static final String ITERATION_COMPLETED = "iteration.completed";

    // Release topics
    public static final String RELEASE_CREATED = "release.created";
    public static final String RELEASE_PUBLISHED = "release.published";
}
