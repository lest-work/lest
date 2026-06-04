package com.lest.task.mapper;

import com.lest.task.domain.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AttachmentMapper
{
    Attachment selectById(@Param("attachmentId") Long attachmentId);
    List<Attachment> selectByTaskId(@Param("taskId") Long taskId);
    Long getMaxVersionByFileName(@Param("taskId") Long taskId, @Param("fileName") String fileName);
    int insert(Attachment attachment);
    int updateById(Attachment attachment);
    int deleteById(@Param("attachmentId") Long attachmentId);
    int softDeleteById(@Param("attachmentId") Long attachmentId, @Param("userId") Long userId);
    int permanentDeleteById(@Param("attachmentId") Long attachmentId);
    int restoreById(@Param("attachmentId") Long attachmentId);
    int deleteByTaskId(@Param("taskId") Long taskId);
}
