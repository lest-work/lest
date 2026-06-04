package com.lest.notification.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.mq.constants.MqTopicConstants;
import com.lest.notification.service.INotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NotificationEventConsumer
{
    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final INotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationEventConsumer(INotificationService notificationService)
    {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = MqTopicConstants.TASK_ASSIGNEE_CHANGED, groupId = "notification-group")
    public void consumeTaskAssigned(String message)
    {
        try
        {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            Long userId = ((Number) event.get("assigneeId")).longValue();
            String taskTitle = (String) event.get("taskTitle");
            notificationService.sendNotification(userId, "task_assigned",
                "任务已分配给你", "任务「" + taskTitle + "」已被分配给你", "task",
                ((Number) event.get("taskId")).longValue());
        }
        catch (Exception e)
        {
            log.error("Failed to process task assigned event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = MqTopicConstants.TASK_COMMENTED, groupId = "notification-group")
    public void consumeTaskCommented(String message)
    {
        try
        {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            Long userId = ((Number) event.get("taskOwnerId")).longValue();
            String taskTitle = (String) event.get("taskTitle");
            String commentUser = (String) event.get("commentUser");
            notificationService.sendNotification(userId, "task_commented",
                "任务有新评论", commentUser + " 评论了任务「" + taskTitle + "」", "task",
                ((Number) event.get("taskId")).longValue());
        }
        catch (Exception e)
        {
            log.error("Failed to process task commented event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = MqTopicConstants.TASK_STATUS_CHANGED, groupId = "notification-group")
    public void consumeTaskStatusChanged(String message)
    {
        try
        {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            Long userId = ((Number) event.get("assigneeId")).longValue();
            String taskTitle = (String) event.get("taskTitle");
            String newStatus = (String) event.get("newStatus");
            notificationService.sendNotification(userId, "task_status_changed",
                "任务状态变更", "任务「" + taskTitle + "」已变更为「" + newStatus + "」", "task",
                ((Number) event.get("taskId")).longValue());
        }
        catch (Exception e)
        {
            log.error("Failed to process task status changed event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = MqTopicConstants.TASK_MENTIONED, groupId = "notification-group")
    public void consumeTaskMentioned(String message)
    {
        try
        {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            Long userId = ((Number) event.get("mentionedUserId")).longValue();
            String taskTitle = (String) event.get("taskTitle");
            String commentUser = (String) event.get("commentUser");
            notificationService.sendNotification(userId, "task_mentioned",
                "在任务评论中被提及", commentUser + " 在任务「" + taskTitle + "」的评论中提到了你", "task",
                ((Number) event.get("taskId")).longValue());
        }
        catch (Exception e)
        {
            log.error("Failed to process task mentioned event: {}", e.getMessage());
        }
    }
}
