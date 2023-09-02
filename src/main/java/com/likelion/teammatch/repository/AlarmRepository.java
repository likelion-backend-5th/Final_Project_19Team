package com.likelion.teammatch.repository;

import com.likelion.teammatch.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByReceiversUsername(String username);
}
