package com.lest.modules.task.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 看板视图VO
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
@Builder
public class BoardVO {

    private String column;
    private List<TaskVO> tasks;
    private Integer totalCount;
}
