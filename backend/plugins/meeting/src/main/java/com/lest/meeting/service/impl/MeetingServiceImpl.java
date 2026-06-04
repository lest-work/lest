package com.lest.meeting.service.impl;

import com.lest.meeting.domain.Meeting;
import com.lest.meeting.domain.MeetingMinutes;
import com.lest.meeting.domain.MeetingParticipant;
import com.lest.meeting.mapper.MeetingMapper;
import com.lest.meeting.mapper.MeetingMinutesMapper;
import com.lest.meeting.mapper.MeetingParticipantMapper;
import com.lest.meeting.service.IMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class MeetingServiceImpl implements IMeetingService
{
    @Autowired private MeetingMapper meetingMapper;
    @Autowired private MeetingParticipantMapper participantMapper;
    @Autowired private MeetingMinutesMapper minutesMapper;

    @Override
    public List<Meeting> selectMeetingList(Meeting meeting)
    {
        return meetingMapper.selectMeetingList(meeting);
    }

    @Override
    public Meeting selectMeetingById(Long meetingId)
    {
        return meetingMapper.selectMeetingById(meetingId);
    }

    @Override
    public int insertMeeting(Meeting meeting)
    {
        meeting.setCreateAt(new Date());
        meeting.setStatus("scheduled");
        return meetingMapper.insertMeeting(meeting);
    }

    @Override
    public int updateMeeting(Meeting meeting)
    {
        meeting.setUpdateAt(new Date());
        return meetingMapper.updateMeeting(meeting);
    }

    @Override
    public int deleteMeeting(Long meetingId)
    {
        participantMapper.deleteByMeetingId(meetingId);
        return meetingMapper.deleteMeetingById(meetingId);
    }

    @Override
    public List<MeetingParticipant> getParticipants(Long meetingId)
    {
        return participantMapper.selectByMeetingId(meetingId);
    }

    @Override
    public int addParticipant(MeetingParticipant participant)
    {
        return participantMapper.insertParticipant(participant);
    }

    @Override
    public int updateParticipant(MeetingParticipant participant)
    {
        participant.setResponseTime(new Date());
        return participantMapper.updateParticipant(participant);
    }

    @Override
    public MeetingMinutes getMinutes(Long meetingId)
    {
        return minutesMapper.selectByMeetingId(meetingId);
    }

    @Override
    public int saveMinutes(MeetingMinutes minutes)
    {
        if (minutes.getMinutesId() != null)
        {
            minutes.setUpdateAt(new Date());
            return minutesMapper.updateMinutes(minutes);
        }
        else
        {
            minutes.setCreateAt(new Date());
            return minutesMapper.insertMinutes(minutes);
        }
    }
}
