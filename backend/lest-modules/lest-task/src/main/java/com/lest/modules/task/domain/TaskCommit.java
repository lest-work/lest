package com.lest.modules.task.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lest.common.core.web.domain.BaseEntity;

/**
 * 任务代码关联对象 task_commit
 * 
 * @author yshan2028
 */
public class TaskCommit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long taskId;
    private String commitHash;
    private String commitMessage;
    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date commitTime;

    private String repoId;
    /** commit / mr */
    private String type;
    /** manual / webhook */
    private String source;
    private Long mrId;
    private String mrTitle;
    private String mrStatus;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getCommitHash() { return commitHash; }
    public void setCommitHash(String commitHash) { this.commitHash = commitHash; }
    public String getCommitMessage() { return commitMessage; }
    public void setCommitMessage(String commitMessage) { this.commitMessage = commitMessage; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public Date getCommitTime() { return commitTime; }
    public void setCommitTime(Date commitTime) { this.commitTime = commitTime; }
    public String getRepoId() { return repoId; }
    public void setRepoId(String repoId) { this.repoId = repoId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Long getMrId() { return mrId; }
    public void setMrId(Long mrId) { this.mrId = mrId; }
    public String getMrTitle() { return mrTitle; }
    public void setMrTitle(String mrTitle) { this.mrTitle = mrTitle; }
    public String getMrStatus() { return mrStatus; }
    public void setMrStatus(String mrStatus) { this.mrStatus = mrStatus; }
}
