package com.lest.meeting.mapper;

import com.lest.meeting.domain.MeetingParticipant;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MeetingParticipantMapper
{
    List<MeetingParticipant> selectByMeetingId(Long meetingId);
    int insertParticipant(MeetingParticipant participant);
    int updateParticipant(MeetingParticipant participant);
    int deleteByMeetingId(Long meetingId);
}
