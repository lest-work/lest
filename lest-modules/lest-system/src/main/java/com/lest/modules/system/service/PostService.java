package com.lest.modules.system.service;

import com.lest.common.core.PageResult;
import com.lest.modules.system.entity.dto.PostDTO;
import com.lest.modules.system.entity.dto.PostQueryDTO;
import com.lest.modules.system.entity.vo.PostVO;

public interface PostService {
    PageResult<PostVO> page(PostQueryDTO dto);
    PostVO getById(Long id);
    void add(PostDTO dto);
    void update(PostDTO dto);
    void delete(Long id);
}
