package com.lest.modules.system.entity.domain;

/**
 * 通知公告实体，对应 sys_notice 表
 */
public class SysNotice {

    private Long id;
    private String noticeTitle;
    private Integer noticeType;
    private String noticeContent;
    private Integer status;
    private String createBy;
    private String updateBy;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNoticeTitle() { return noticeTitle; }
    public void setNoticeTitle(String noticeTitle) { this.noticeTitle = noticeTitle; }
    public Integer getNoticeType() { return noticeType; }
    public void setNoticeType(Integer noticeType) { this.noticeType = noticeType; }
    public String getNoticeContent() { return noticeContent; }
    public void setNoticeContent(String noticeContent) { this.noticeContent = noticeContent; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
