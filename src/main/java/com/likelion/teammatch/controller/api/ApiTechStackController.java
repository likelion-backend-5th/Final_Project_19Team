package com.likelion.teammatch.controller.api;

import com.likelion.teammatch.repository.TechStackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@Slf4j
public class ApiTechStackController {

    private final TechStackRepository techStackRepository;

    public ApiTechStackController(TechStackRepository techStackRepository) {
        this.techStackRepository = techStackRepository;
    }

    @PostMapping("/techStackExists")
    public boolean checkTechStackExists(@RequestBody String techValue){
        log.info("techStack Searched : {}", techValue);

        techValue = techValue.toLowerCase();

        //DB에 쿼리를 날려도 되는 검사인지 반드시 확인할 것!
        return techStackRepository.existsByName(techValue);
    }
}
