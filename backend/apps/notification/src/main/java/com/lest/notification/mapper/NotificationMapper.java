package com.lest.notification.mapper;

import com.lest.notification.domain.Notification;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface NotificationMapper
{
    List<Notification> selectNotificationList(Notification notification);
    Notification selectNotificationById(Long notificationId);
    int insertNotification(Notification notification);
    int updateNotification(Notification notification);
    int deleteNotificationById(Long notificationId);
    int countUnreadByUserId(Long userId);
}
