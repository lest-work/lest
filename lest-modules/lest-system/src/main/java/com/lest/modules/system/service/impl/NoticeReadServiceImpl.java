package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lest.modules.system.entity.domain.SysNoticeRead;
import com.lest.modules.system.mapper.SysNoticeReadMapper;
import com.lest.modules.system.service.NoticeReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeReadServiceImpl implements NoticeReadService {

    private final SysNoticeReadMapper noticeReadMapper;

    @Override
    public void markRead(Long noticeId, Long userId) {
        LambdaQueryWrapper<SysNoticeRead> w = new LambdaQueryWrapper<>();
        w.eq(SysNoticeRead::getNoticeId, noticeId).eq(SysNoticeRead::getUserId, userId);
        if (noticeReadMapper.selectCount(w) == 0) {
            SysNoticeRead record = new SysNoticeRead();
            record.setNoticeId(noticeId);
            record.setUserId(userId);
            record.setReadAt(System.currentTimeMillis());
            noticeReadMapper.insert(record);
        }
    }

    @Override
    public boolean isRead(Long noticeId, Long userId) {
        LambdaQueryWrapper<SysNoticeRead> w = new LambdaQueryWrapper<>();
        w.eq(SysNoticeRead::getNoticeId, noticeId).eq(SysNoticeRead::getUserId, userId);
        return noticeReadMapper.selectCount(w) > 0;
    }
}
