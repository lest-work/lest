package com.lest.modules.file.service;

import com.lest.modules.file.entity.domain.UserFile;
import com.lest.modules.file.entity.dto.UserFileDTO;
import com.lest.modules.file.entity.vo.UserFileVO;

import java.util.List;

public interface UserFileService {
    List<UserFileVO> list(UserFile query);
    List<UserFileVO> page(UserFile query);
    void add(UserFileDTO dto);
    void update(UserFileDTO dto);
    void remove(Long id, Long userId);
    void removeBatch(List<Long> ids, Long userId);
}
