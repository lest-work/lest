package com.lest.common.mq.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LEST 平台事件基类<br>
 * 统一平台内所有领域事件的数据结构。
 *
 * @author yshan2028
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LestPlatformEvent implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 事件唯一标识 */
    private String eventId;

    /** 事件类型（枚举值见 EventTypes） */
    private String eventType;

    /** 事件发生时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    /** 事件来源服务 */
    private String source;

    /** 关联项目 ID */
    private Long projectId;

    /** 操作用户 ID */
    private Long userId;

    /** 事件负载（具体业务数据） */
    private Map<String, Object> payload;

    public LestPlatformEvent()
    {
        this.payload = new HashMap<>();
        this.timestamp = new Date();
    }

    public LestPlatformEvent(String eventType)
    {
        this();
        this.eventType = eventType;
    }

    public LestPlatformEvent(String eventType, Long projectId, Long userId)
    {
        this();
        this.eventType = eventType;
        this.projectId = projectId;
        this.userId = userId;
    }

    public void put(String key, Object value)
    {
        payload.put(key, value);
    }

    public Object get(String key)
    {
        return payload.get(key);
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public static final class EventTypes
    {
        private EventTypes() {}

        // Task events
        public static final String TASK_CREATED = "task.created";
        public static final String TASK_UPDATED = "task.updated";
        public static final String TASK_DELETED = "task.deleted";
        public static final String TASK_ASSIGNED = "task.assigned";
        public static final String TASK_STATUS_CHANGED = "task.status.changed";
        public static final String TASK_COMMENTED = "task.commented";
        public static final String TASK_WATCHED = "task.watched";

        // Project events
        public static final String PROJECT_CREATED = "project.created";
        public static final String PROJECT_UPDATED = "project.updated";
        public static final String PROJECT_DELETED = "project.deleted";
        public static final String PROJECT_MEMBER_ADDED = "project.member.added";
        public static final String PROJECT_MEMBER_REMOVED = "project.member.removed";

        // Iteration events
        public static final String ITERATION_CREATED = "iteration.created";
        public static final String ITERATION_UPDATED = "iteration.updated";
        public static final String ITERATION_COMPLETED = "iteration.completed";

        // Release events
        public static final String RELEASE_CREATED = "release.created";
        public static final String RELEASE_PUBLISHED = "release.published";
    }
}
