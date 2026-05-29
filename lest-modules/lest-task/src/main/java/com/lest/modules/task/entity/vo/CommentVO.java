package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务评论VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class CommentVO {

    private Long id;
    private Long taskId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer replyCount;
}
