package com.lest.modules.release.entity.dto;

import lombok.Data;

@Data
public class ReleasePlanQueryDTO {
    private Long projectId;
    private String keyword;
    private Integer status;
    private Integer releaseType;
    private String startDate;
    private String endDate;
    private Integer isDraft;
    private Integer isStable;
    private Integer page = 1;
    private Integer pageSize = 20;
    private String sortField = "created_at";
    private String sortOrder = "desc";
}
