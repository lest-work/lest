package com.lest.meeting.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 会议参与人对象 meeting_participant
 *
 * @author yshan2028
 */
public class MeetingParticipant implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 参与人ID */
    private Long participantId;

    /** 会议ID */
    private Long meetingId;

    /** 用户ID */
    private Long userId;

    /** 参与状态 */
    private String status;

    /** 回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date responseTime;

    public Long getParticipantId()
    {
        return participantId;
    }

    public void setParticipantId(Long participantId)
    {
        this.participantId = participantId;
    }

    public Long getMeetingId()
    {
        return meetingId;
    }

    public void setMeetingId(Long meetingId)
    {
        this.meetingId = meetingId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getResponseTime()
    {
        return responseTime;
    }

    public void setResponseTime(Date responseTime)
    {
        this.responseTime = responseTime;
    }
}
