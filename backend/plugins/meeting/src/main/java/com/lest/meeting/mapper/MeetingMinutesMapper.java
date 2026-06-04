package com.lest.meeting.mapper;

import com.lest.meeting.domain.MeetingMinutes;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingMinutesMapper
{
    MeetingMinutes selectByMeetingId(Long meetingId);
    int insertMinutes(MeetingMinutes minutes);
    int updateMinutes(MeetingMinutes minutes);
}
