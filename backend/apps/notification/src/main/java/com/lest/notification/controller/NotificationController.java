package com.lest.notification.controller;

import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.notification.domain.Notification;
import com.lest.notification.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController extends BaseController
{
    @Autowired
    private INotificationService notificationService;

    @GetMapping("/list")
    public TableDataInfo list(Notification notification)
    {
        notification.setUserId(SecurityUtils.getLoginUser().getUserid());
        startPage();
        List<Notification> list = notificationService.selectNotificationList(notification);
        return getDataTable(list);
    }

    @GetMapping("/unread/count")
    public AjaxResult unreadCount()
    {
        Long userId = SecurityUtils.getLoginUser().getUserid();
        int count = notificationService.countUnread(userId);
        return success(count);
    }

    @PutMapping("/{notificationId}/read")
    public AjaxResult markAsRead(@PathVariable Long notificationId)
    {
        return toAjax(notificationService.markAsRead(notificationId));
    }

    @PutMapping("/read/all")
    public AjaxResult markAllAsRead()
    {
        Long userId = SecurityUtils.getLoginUser().getUserid();
        return toAjax(notificationService.markAllAsRead(userId));
    }

    @DeleteMapping("/{notificationId}")
    public AjaxResult remove(@PathVariable Long notificationId)
    {
        return toAjax(notificationService.deleteNotification(notificationId));
    }
}
