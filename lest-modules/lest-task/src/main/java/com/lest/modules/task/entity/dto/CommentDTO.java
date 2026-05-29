package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 评论DTO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class CommentDTO {

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;
}
