package com.lest.project.controller.request;

import java.util.Date;

import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 更新项目请求体。
 */
public class ProjectUpdateRequest {

    private Long projectId;

    @Size(max = 128, message = "项目名称长度不能超过128个字符")
    private String name;

    @Size(max = 2000, message = "项目描述长度不能超过2000个字符")
    private String description;

    @Size(max = 32, message = "模板标识长度不能超过32个字符")
    private String template;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
