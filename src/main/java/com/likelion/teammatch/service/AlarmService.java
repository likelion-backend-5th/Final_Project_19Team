package com.likelion.teammatch.service;

import com.likelion.teammatch.entity.Alarm;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.entity.UserTeam;
import com.likelion.teammatch.repository.AlarmRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.team.UserTeamRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final UserTeamRepository userTeamRepository;

    public AlarmService(AlarmRepository alarmRepository, UserRepository userRepository, UserTeamRepository userTeamRepository) {
        this.alarmRepository = alarmRepository;
        this.userRepository = userRepository;
        this.userTeamRepository = userTeamRepository;
    }

    // 알람 생성 및 수신자 추가
    public void createAlarm(String text, Long receiverId) {
        Alarm alarm = new Alarm();
        alarm.setText(text);
        alarm.setReceiverId(receiverId);

        alarmRepository.save(alarm);
    }

    // 알람 생성 및 수신자 추가
    public void createTeamAlarm(String text) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUserOptional = userRepository.findByUsername(username);

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();

            // 팀 멤버 중 본인을 제외한 모든 멤버를 수신자로 추가
            List<UserTeam> userTeams = userTeamRepository.findAllByUserId(currentUser.getId());
            List<Long> receiverIds = userTeams.stream()
                    .filter(userTeam -> !userTeam.getUserId().equals(currentUser.getId()))
                    .map(UserTeam::getUserId)
                    .collect(Collectors.toList());

            // 알람 생성 및 수신자 설정
            for (Long receiverId : receiverIds) {
                Alarm alarm = new Alarm();
                alarm.setText(text);
                alarm.setReceiverId(receiverId);
                alarmRepository.save(alarm);
            }
        } else {
            // 사용자 정보를 찾을 수 없는 경우 예외 처리
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }

    // 특정 사용자가 자신에게 전송된 알람을 제거
    public void deleteAlarmForUser(Long alarmId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();

            // 해당 알람이 사용자에게 전송되었는지 확인
            if (alarm.getReceiverId().equals(Long.valueOf(username))) {
                alarmRepository.deleteById(alarmId);
            } else {
                // 해당 알람이 사용자에게 전송되지 않은 경우 예외 처리
                throw new IllegalArgumentException("The alarm is not for the specified user.");
            }
        } else {
            // 해당 알람이 존재하지 않는 경우 예외 처리
            throw new IllegalArgumentException("Alarm not found with ID: " + alarmId);
        }
    }

    // 특정 사용자에게 전송된 알람 목록을 가져오는 메서드
    public List<Alarm> getAlarmsForUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 사용자의 정보를 기반으로 알람 리스트를 가져옵니다.
        // username 또는 ID를 사용하여 알람 목록을 필터링하도록 변경
        Long userId = Long.valueOf(username);

        return alarmRepository.findAllByReceiverId(userId);
    }
}
