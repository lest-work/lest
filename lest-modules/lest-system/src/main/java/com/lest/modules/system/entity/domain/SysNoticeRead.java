package com.lest.modules.system.entity.domain;

/**
 * 通知已读记录实体，对应 sys_notice_read 表
 */
public class SysNoticeRead {

    private Long id;
    private Long noticeId;
    private Long userId;
    private Long readAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getReadAt() { return readAt; }
    public void setReadAt(Long readAt) { this.readAt = readAt; }
}
