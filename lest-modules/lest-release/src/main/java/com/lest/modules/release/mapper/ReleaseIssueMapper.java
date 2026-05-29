package com.lest.modules.release.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.modules.release.entity.domain.ReleaseIssue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReleaseIssueMapper extends BaseMapper<ReleaseIssue> {

    Page<ReleaseIssue> selectIssuePage(Page<ReleaseIssue> page, LambdaQueryWrapper<ReleaseIssue> wrapper);
}
