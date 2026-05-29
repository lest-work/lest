package com.lest.modules.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lest.modules.file.entity.domain.UserFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserFileMapper extends BaseMapper<UserFile> {
}
