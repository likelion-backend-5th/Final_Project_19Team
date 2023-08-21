package com.likelion.teammatch.config;

import com.likelion.teammatch.utils.TokenClearJob;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class JobConfig {
    private final Scheduler scheduler;

    public JobConfig(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void run(){
        JobDetail detail = runJobDetail(TokenClearJob.class, new HashMap());

        try {
            // 크론형식 지정 후 스케줄 시작
            scheduler.scheduleJob(detail, runJobTrigger("0 0/30 * * * ?"));
            //scheduler.scheduleJob(detail, runJobTrigger("0/10 * * * * ?"));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    public Trigger runJobTrigger(String scheduleExp){
        // 크론 스케줄 사용
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }

    public JobDetail runJobDetail(Class job, Map params){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);
        // 스케줄 생성
        return newJob(job).usingJobData(jobDataMap).build();
    }

}
