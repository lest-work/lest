package com.lest.notification.service.impl;

import com.lest.notification.domain.Notification;
import com.lest.notification.mapper.NotificationMapper;
import com.lest.notification.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements INotificationService
{
    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public List<Notification> selectNotificationList(Notification notification)
    {
        return notificationMapper.selectNotificationList(notification);
    }

    @Override
    public Notification selectNotificationById(Long notificationId)
    {
        return notificationMapper.selectNotificationById(notificationId);
    }

    @Override
    public int insertNotification(Notification notification)
    {
        return notificationMapper.insertNotification(notification);
    }

    @Override
    public int markAsRead(Long notificationId)
    {
        Notification n = new Notification();
        n.setNotificationId(notificationId);
        n.setIsRead(1);
        return notificationMapper.updateNotification(n);
    }

    @Override
    public int markAllAsRead(Long userId)
    {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setIsRead(1);
        return notificationMapper.updateNotification(n);
    }

    @Override
    public int deleteNotification(Long notificationId)
    {
        return notificationMapper.deleteNotificationById(notificationId);
    }

    @Override
    public int countUnread(Long userId)
    {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public void sendNotification(Long userId, String type, String title, String content, String relatedType, Long relatedId)
    {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notification.setCreateAt(new Date());
        notificationMapper.insertNotification(notification);

        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/notifications", notification);
    }
}
