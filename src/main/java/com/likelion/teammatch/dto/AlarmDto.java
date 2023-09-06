package com.likelion.teammatch.dto;

import com.likelion.teammatch.entity.Alarm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AlarmDto {
    private Long id;
    private String text;

    public static AlarmDto fromEntity(Alarm alarm){
        AlarmDto alarmDto = new AlarmDto();
        alarmDto.setId(alarm.getId());
        alarmDto.setText(alarm.getText());
        return alarmDto;
    }
}
