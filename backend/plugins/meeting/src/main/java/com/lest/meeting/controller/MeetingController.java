package com.lest.meeting.controller;

import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.meeting.domain.Meeting;
import com.lest.meeting.domain.MeetingMinutes;
import com.lest.meeting.domain.MeetingParticipant;
import com.lest.meeting.service.IMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController extends BaseController
{
    @Autowired
    private IMeetingService meetingService;

    @GetMapping("/list")
    public TableDataInfo list(Meeting meeting)
    {
        startPage();
        List<Meeting> list = meetingService.selectMeetingList(meeting);
        return getDataTable(list);
    }

    @GetMapping("/{meetingId}")
    public AjaxResult get(@PathVariable Long meetingId)
    {
        return success(meetingService.selectMeetingById(meetingId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody Meeting meeting)
    {
        meeting.setCreatorId(SecurityUtils.getLoginUser().getUserid());
        return toAjax(meetingService.insertMeeting(meeting));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody Meeting meeting)
    {
        return toAjax(meetingService.updateMeeting(meeting));
    }

    @DeleteMapping("/{meetingId}")
    public AjaxResult remove(@PathVariable Long meetingId)
    {
        return toAjax(meetingService.deleteMeeting(meetingId));
    }

    @GetMapping("/{meetingId}/participant")
    public TableDataInfo participants(@PathVariable Long meetingId)
    {
        startPage();
        List<MeetingParticipant> list = meetingService.getParticipants(meetingId);
        return getDataTable(list);
    }

    @PostMapping("/{meetingId}/participant")
    public AjaxResult addParticipant(@PathVariable Long meetingId, @RequestBody MeetingParticipant participant)
    {
        participant.setMeetingId(meetingId);
        return toAjax(meetingService.addParticipant(participant));
    }

    @GetMapping("/{meetingId}/minutes")
    public AjaxResult minutes(@PathVariable Long meetingId)
    {
        return success(meetingService.getMinutes(meetingId));
    }

    @PostMapping("/{meetingId}/minutes")
    public AjaxResult saveMinutes(@PathVariable Long meetingId, @RequestBody MeetingMinutes minutes)
    {
        minutes.setMeetingId(meetingId);
        minutes.setCreatedBy(SecurityUtils.getLoginUser().getUserid());
        return toAjax(meetingService.saveMinutes(minutes));
    }
}
