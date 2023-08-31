package com.likelion.teammatch.controller.api;

import com.likelion.teammatch.dto.AlarmRequestDto;
import com.likelion.teammatch.entity.Alarm;
import com.likelion.teammatch.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    @PostMapping
    public void createAlarm(@RequestBody AlarmRequestDto requestDto) {
        alarmService.createAndCallAlarm(
                requestDto.getAlarmType(),
                requestDto.getProjectName(),
                requestDto.getMemberType(),
                requestDto.getUserId(),
                requestDto.getAction()
        );
    }

    @GetMapping("/{userId}")
    public List<Alarm> getAllAlarmsByUserId(@PathVariable Long userId) {
        return alarmService.getAllAlarmsByUserId(userId);
    }

    @PutMapping("/{alarmId}/read")
    public void markAlarmAsRead(@PathVariable Long alarmId) {
        alarmService.markAlarmAsRead(alarmId);
    }

    @PutMapping("/{alarmId}/action/{action}")
    public void performAlarmAction(@PathVariable Long alarmId, @PathVariable String action) {
        alarmService.performAlarmAction(alarmId, action);
    }
}
