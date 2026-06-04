package com.lest.notification.service;

import com.lest.notification.domain.Notification;
import java.util.List;

public interface INotificationService
{
    List<Notification> selectNotificationList(Notification notification);
    Notification selectNotificationById(Long notificationId);
    int insertNotification(Notification notification);
    int markAsRead(Long notificationId);
    int markAllAsRead(Long userId);
    int deleteNotification(Long notificationId);
    int countUnread(Long userId);
    void sendNotification(Long userId, String type, String title, String content, String relatedType, Long relatedId);
}
