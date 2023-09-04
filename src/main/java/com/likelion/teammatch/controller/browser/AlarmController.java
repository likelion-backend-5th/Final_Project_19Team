package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.entity.Alarm;
import com.likelion.teammatch.service.AlarmService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/alarms")
public class AlarmController {
    private final AlarmService alarmService;

    public AlarmController(AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    // 알람 생성 처리
    @PostMapping("/create")
    public String createAlarm(@RequestParam("text") String text, @RequestParam("receiverId") Long receiverId) {
        alarmService.createAlarm(text, receiverId);
        return "redirect:/index"; // 알람 생성 후 알람 목록 페이지로 리다이렉트
    }

    // 알람 생성 처리
    @PostMapping("/createTeamAlarm")
    public String createTeamAlarm(@RequestParam("text") String text) {
        // 알람 생성 서비스 호출
        alarmService.createTeamAlarm(text);
        return "redirect:/index"; // 알람 생성 후 알람 목록 페이지로 리다이렉트
    }

    // 알람 삭제 처리
    @PostMapping("/delete/{alarmId}")
    public String deleteAlarm(@PathVariable Long alarmId) {
        alarmService.deleteAlarmForUser(alarmId);
        return "redirect:/index"; // 알람 삭제 후 알람 목록 페이지로 리다이렉트
    }

    // 알람 목록 페이지로 이동
    @GetMapping
    public List<Alarm> showAlarmList(Model model) {
        List<Alarm> alarms = alarmService.getAlarmsForUser();
        model.addAttribute("alarms", alarms);
        return alarms;
    }
}
