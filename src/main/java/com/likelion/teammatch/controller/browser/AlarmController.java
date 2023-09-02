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
    public String createAlarm(@RequestParam("text") String text, @RequestParam("receiverIds") List<Long> receiverIds) {
        alarmService.createAlarm(text, receiverIds);

        return "redirect:/index"; // 알람 생성 후 알람 목록 페이지로 리다이렉트
    }

    // 알람 생성 처리
    @PostMapping("/createTeamAlarm")
    public String createTeamAlarm(@RequestParam("text") String text, @RequestParam("teamId") Long teamId) {
        // 현재 사용자 정보 얻기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 알람 생성 서비스 호출
        alarmService.createTeamAlarm(text, teamId, username);
        return "redirect:/index"; // 알람 생성 후 알람 목록 페이지로 리다이렉트
    }

    // 알람 삭제 처리
    @PostMapping("/delete/{alarmId}")
    public String deleteAlarm(@PathVariable Long alarmId) {
        // 현재 로그인한 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 사용자의 username 또는 ID를 가져옵니다. (Spring Security 설정에 따라 달라질 수 있음)

        // 이제 username 또는 ID를 사용하여 해당 사용자에게 전송된 알람을 삭제할 수 있습니다.
        alarmService.deleteAlarmForUser(alarmId, username); // username 또는 ID를 사용하여 알람 삭제 처리
        return "redirect:/index"; // 알람 삭제 후 알람 목록 페이지로 리다이렉트
    }

    // 알람 목록 페이지로 이동
    @GetMapping
    public String showAlarmList(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Alarm> alarms = alarmService.getAlarmsForUser(username); // username 또는 ID를 사용하여 알람 목록을 가져오도록 변경
        model.addAttribute("alarms", alarms);
        return "redirect:/index"; // 알람 목록 페이지의 파일명
    }
}
