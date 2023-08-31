package com.likelion.teammatch.service;

import com.likelion.teammatch.entity.Alarm;
import com.likelion.teammatch.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlarmService {
    @Autowired
    private AlarmRepository alarmRepository;

    public void createAndCallAlarm(String alarmType, String projectName, String memberType, Long userId, String action) {
        Alarm alarm = Alarm.create(alarmType, projectName, memberType, userId);
        alarmRepository.save(alarm);
    }

    public List<Alarm> getAllAlarmsByUserId(Long userId) {
        return alarmRepository.findByUserId(userId);
    }

    public void markAlarmAsRead(Long alarmId) {
        Optional<Alarm> alarmOptional = alarmRepository.findById(alarmId);
        if (alarmOptional.isPresent()) {
            Alarm alarm = alarmOptional.get();
            alarm.markAsRead();
            alarmRepository.save(alarm);
        }
    }

    public void performAlarmAction(Long alarmId, String action) {
        Optional<Alarm> alarmOptional = alarmRepository.findById(alarmId);
        if (alarmOptional.isPresent()) {
            Alarm alarm = alarmOptional.get();
            alarm.performAction(action);
            alarmRepository.save(alarm);
        }
    }
}
