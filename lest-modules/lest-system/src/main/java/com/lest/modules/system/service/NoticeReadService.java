package com.lest.modules.system.service;

import com.lest.modules.system.entity.domain.SysNoticeRead;

public interface NoticeReadService {
    void markRead(Long noticeId, Long userId);
    boolean isRead(Long noticeId, Long userId);
}
