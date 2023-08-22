package com.likelion.teammatch.utils;

import com.likelion.teammatch.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TokenClearJob extends QuartzJobBean {

    private final TokenRepository tokenRepository;

    public TokenClearJob(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("토큰 레포지토리 지우기");
        LocalDateTime timeThreshold = LocalDateTime.now().minusHours(12);
        tokenRepository.deleteTokensWithLastModifiedOlderThan(timeThreshold);
    }
}
