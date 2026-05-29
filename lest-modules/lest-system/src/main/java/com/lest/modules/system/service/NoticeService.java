package com.lest.modules.system.service;

import com.lest.common.core.PageResult;
import com.lest.modules.system.entity.dto.NoticeDTO;
import com.lest.modules.system.entity.dto.NoticeQueryDTO;
import com.lest.modules.system.entity.vo.NoticeVO;

public interface NoticeService {
    PageResult<NoticeVO> page(NoticeQueryDTO dto);
    NoticeVO getById(Long id);
    void add(NoticeDTO dto);
    void update(NoticeDTO dto);
    void delete(Long id);
    void publish(Long id);
}
