package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 代码提交VO
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Data
@Builder
public class CommitVO {

    private Long id;
    private Long taskId;
    private String commitHash;
    private String shortHash;
    private String commitMessage;
    private String author;
    private LocalDateTime commitTime;
    private String repoId;
    private String type;
    private String source;
}
