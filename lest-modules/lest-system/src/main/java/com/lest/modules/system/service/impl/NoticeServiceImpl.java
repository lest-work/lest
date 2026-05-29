package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.PageResult;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.system.entity.domain.SysNotice;
import com.lest.modules.system.entity.dto.NoticeDTO;
import com.lest.modules.system.entity.dto.NoticeQueryDTO;
import com.lest.modules.system.entity.vo.NoticeVO;
import com.lest.modules.system.mapper.SysNoticeMapper;
import com.lest.modules.system.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final SysNoticeMapper noticeMapper;

    @Override
    public PageResult<NoticeVO> page(NoticeQueryDTO dto) {
        int pageNum = dto.page() != null ? dto.page() : 1;
        int pageSize = dto.size() != null ? dto.size() : 10;
        Page<SysNotice> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysNotice> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.noticeTitle())) w.like(SysNotice::getNoticeTitle, dto.noticeTitle());
        if (dto.noticeType() != null) w.eq(SysNotice::getNoticeType, dto.noticeType());
        if (dto.status() != null) w.eq(SysNotice::getStatus, dto.status());
        w.orderByDesc(SysNotice::getId);
        IPage<SysNotice> result = noticeMapper.selectPage(p, w);
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public NoticeVO getById(Long id) {
        SysNotice notice = noticeMapper.selectById(id);
        if (notice == null) throw new ServiceException("公告不存在");
        return toVO(notice);
    }

    @Override
    public void add(NoticeDTO dto) {
        SysNotice notice = new SysNotice();
        notice.setNoticeTitle(dto.noticeTitle());
        notice.setNoticeType(dto.noticeType() != null ? dto.noticeType() : 1);
        notice.setNoticeContent(dto.noticeContent());
        notice.setStatus(dto.status() != null ? dto.status() : 0);
        notice.setRemark(dto.remark());
        noticeMapper.insert(notice);
    }

    @Override
    public void update(NoticeDTO dto) {
        if (dto.id() == null) throw new ServiceException("公告ID不能为空");
        SysNotice notice = noticeMapper.selectById(dto.id());
        if (notice == null) throw new ServiceException("公告不存在");
        notice.setNoticeTitle(dto.noticeTitle());
        notice.setNoticeType(dto.noticeType());
        notice.setNoticeContent(dto.noticeContent());
        if (dto.status() != null) notice.setStatus(dto.status());
        if (dto.remark() != null) notice.setRemark(dto.remark());
        noticeMapper.updateById(notice);
    }

    @Override
    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    public void publish(Long id) {
        SysNotice notice = noticeMapper.selectById(id);
        if (notice == null) throw new ServiceException("公告不存在");
        notice.setStatus(1);
        noticeMapper.updateById(notice);
    }

    private NoticeVO toVO(SysNotice notice) {
        return NoticeVO.builder()
            .id(notice.getId())
            .noticeTitle(notice.getNoticeTitle())
            .noticeType(notice.getNoticeType())
            .noticeContent(notice.getNoticeContent())
            .status(notice.getStatus())
            .createBy(notice.getCreateBy())
            .updateBy(notice.getUpdateBy())
            .remark(notice.getRemark())
            .build();
    }
}
