package com.lest.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.core.PageResult;
import com.lest.common.core.exception.ServiceException;
import com.lest.modules.system.entity.domain.SysPost;
import com.lest.modules.system.entity.dto.PostDTO;
import com.lest.modules.system.entity.dto.PostQueryDTO;
import com.lest.modules.system.entity.vo.PostVO;
import com.lest.modules.system.mapper.SysPostMapper;
import com.lest.modules.system.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final SysPostMapper postMapper;

    @Override
    public PageResult<PostVO> page(PostQueryDTO dto) {
        int pageNum = dto.page() != null ? dto.page() : 1;
        int pageSize = dto.size() != null ? dto.size() : 10;
        Page<SysPost> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPost> w = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dto.postCode())) w.like(SysPost::getPostCode, dto.postCode());
        if (StringUtils.hasText(dto.postName())) w.like(SysPost::getPostName, dto.postName());
        if (dto.status() != null) w.eq(SysPost::getStatus, dto.status());
        w.orderByAsc(SysPost::getSort);
        IPage<SysPost> result = postMapper.selectPage(p, w);
        return PageResult.of(result.getRecords().stream().map(this::toVO).toList(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public PostVO getById(Long id) {
        SysPost post = postMapper.selectById(id);
        if (post == null) throw new ServiceException("岗位不存在");
        return toVO(post);
    }

    @Override
    public void add(PostDTO dto) {
        SysPost post = new SysPost();
        post.setPostCode(dto.postCode());
        post.setPostName(dto.postName());
        post.setSort(dto.sort() != null ? dto.sort() : 0);
        post.setStatus(dto.status() != null ? dto.status() : 1);
        post.setRemark(dto.remark());
        postMapper.insert(post);
    }

    @Override
    public void update(PostDTO dto) {
        if (dto.id() == null) throw new ServiceException("岗位ID不能为空");
        SysPost post = postMapper.selectById(dto.id());
        if (post == null) throw new ServiceException("岗位不存在");
        post.setPostCode(dto.postCode());
        post.setPostName(dto.postName());
        if (dto.sort() != null) post.setSort(dto.sort());
        if (dto.status() != null) post.setStatus(dto.status());
        if (dto.remark() != null) post.setRemark(dto.remark());
        postMapper.updateById(post);
    }

    @Override
    public void delete(Long id) {
        postMapper.deleteById(id);
    }

    private PostVO toVO(SysPost post) {
        return PostVO.builder()
            .id(post.getId())
            .postCode(post.getPostCode())
            .postName(post.getPostName())
            .sort(post.getSort())
            .status(post.getStatus())
            .remark(post.getRemark())
            .build();
    }
}
