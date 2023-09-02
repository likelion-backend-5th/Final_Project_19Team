package com.likelion.teammatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Alarm extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 내용
    private String text;
    // 수신자
    @ManyToMany
    @JoinTable(
            name = "alarm_receiver",
            joinColumns = @JoinColumn(name = "alarm_id"),
            inverseJoinColumns = @JoinColumn(name = "receiver_id")
    )
    private List<User> receivers = new ArrayList<>();
}