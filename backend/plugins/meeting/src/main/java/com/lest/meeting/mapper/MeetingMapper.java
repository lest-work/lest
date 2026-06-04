package com.lest.meeting.mapper;

import com.lest.meeting.domain.Meeting;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MeetingMapper
{
    List<Meeting> selectMeetingList(Meeting meeting);
    Meeting selectMeetingById(Long meetingId);
    int insertMeeting(Meeting meeting);
    int updateMeeting(Meeting meeting);
    int deleteMeetingById(Long meetingId);
}
