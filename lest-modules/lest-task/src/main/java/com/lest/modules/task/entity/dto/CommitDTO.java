package com.lest.modules.task.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Git提交DTO（用于webhook回调）
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
public class CommitDTO {

    @NotBlank(message = "提交Hash不能为空")
    private String commitHash;

    private String commitMessage;

    private String author;

    private LocalDateTime commitTime;

    private String repoId;

    private String type;
}
