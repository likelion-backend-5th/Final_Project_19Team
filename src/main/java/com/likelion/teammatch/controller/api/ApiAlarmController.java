package com.likelion.teammatch.controller.api;

import com.likelion.teammatch.dto.AlarmDto;
import com.likelion.teammatch.service.AlarmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class ApiAlarmController {
    private final AlarmService alarmService;

    public ApiAlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    @GetMapping("/getAlarm")
    public List<AlarmDto> getMyAlarm(){
        return alarmService.getAlarmsForUser();
    }

    @PostMapping("/deleteAlarm/{alarmId}")
    public void deleteAlarm(@PathVariable("alarmId") Long alarmId){
        alarmService.deleteAlarmForUser(alarmId);
    }
}
