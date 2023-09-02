package com.likelion.teammatch.service;

import com.likelion.teammatch.entity.Alarm;
import com.likelion.teammatch.entity.Team;
import com.likelion.teammatch.entity.User;
import com.likelion.teammatch.repository.AlarmRepository;
import com.likelion.teammatch.repository.UserRepository;
import com.likelion.teammatch.repository.team.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public AlarmService(AlarmRepository alarmRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.alarmRepository = alarmRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    // 알람 생성 및 수신자 추가
    public void createAlarm(String text, List<Long> receiverIdList) {
        Alarm alarm = new Alarm();
        alarm.setText(text);

        // receiverIdList에 있는 사용자들을 찾아서 receivers에 추가
        for (Long receiverId : receiverIdList) {
            User receiver = userRepository.findById(receiverId).orElse(null);
            if (receiver != null) {
                alarm.getReceivers().add(receiver);
            }
        }

        // 알람 저장
        alarmRepository.save(alarm);
    }

    // 알람 생성 및 수신자 추가
    public void createTeamAlarm(String text, Long teamId, String currentUsername) {
        // username을 기반으로 해당 사용자의 ID를 데이터베이스에서 찾아온다고 가정
        Optional<User> currentUserOptional = userRepository.findByUsername(currentUsername);

        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();

            // 팀을 가져옴
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));

            // 팀 멤버 중 본인을 제외한 모든 멤버를 수신자로 추가
            List<User> receivers = team.getMembers().stream()
                    .filter(member -> !member.getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            // 알람 생성 및 수신자 설정
            Alarm alarm = new Alarm();
            alarm.setText(text);
            alarm.setReceivers(receivers);

            // 알람 저장
            alarmRepository.save(alarm);
        }
    }

    // 특정 사용자가 자신에게 전송된 알람을 제거
    public void deleteAlarmForUser(Long alarmId, String username) {
        Optional<Alarm> optionalAlarm = alarmRepository.findById(alarmId);
        if (optionalAlarm.isPresent()) {
            Alarm alarm = optionalAlarm.get();

            // 해당 알람이 사용자에게 전송되었는지 확인
            if (alarm.getReceivers().stream().anyMatch(user -> user.getUsername().equals(username))) {
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
    public List<Alarm> getAlarmsForUser(String username) {
        return alarmRepository.findByReceiversUsername(username);
    }
}
