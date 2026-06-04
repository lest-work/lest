package com.lest.meeting.service;

import com.lest.meeting.domain.Meeting;
import com.lest.meeting.domain.MeetingMinutes;
import com.lest.meeting.domain.MeetingParticipant;
import java.util.List;

public interface IMeetingService
{
    List<Meeting> selectMeetingList(Meeting meeting);
    Meeting selectMeetingById(Long meetingId);
    int insertMeeting(Meeting meeting);
    int updateMeeting(Meeting meeting);
    int deleteMeeting(Long meetingId);
    List<MeetingParticipant> getParticipants(Long meetingId);
    int addParticipant(MeetingParticipant participant);
    int updateParticipant(MeetingParticipant participant);
    MeetingMinutes getMinutes(Long meetingId);
    int saveMinutes(MeetingMinutes minutes);
}
