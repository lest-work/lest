package com.lest.modules.system.entity.domain;

/**
 * 岗位实体，对应 sys_post 表
 */
public class SysPost {

    private Long id;
    private String postCode;
    private String postName;
    private Integer sort;
    private Integer status;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
    public String getPostName() { return postName; }
    public void setPostName(String postName) { this.postName = postName; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
