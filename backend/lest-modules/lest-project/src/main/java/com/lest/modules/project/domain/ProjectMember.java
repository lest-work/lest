package com.lest.modules.project.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 项目成员对象 project_member
 * 
 * @author yshan2028
 */
public class ProjectMember implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 项目ID */
    private Long projectId;

    /** 用户ID */
    private Long userId;

    /** 角色：admin / developer / observer */
    private String role;

    /** 加入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinedAt;

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public Date getJoinedAt()
    {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt)
    {
        this.joinedAt = joinedAt;
    }
}
