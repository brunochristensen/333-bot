package net.brunochristensen._333bot.features;

import org.quartz.*;

import java.util.Map;

import static org.quartz.JobBuilder.newJob;

public class QuartzHandler implements ScheduleHandler {

    private Scheduler scheduler;

    @Override
    public void addJob(Class<? extends Job> jobClass, JobKey key, Map<String, Object> data) throws SchedulerException {
        JobDetail job = newJob(jobClass).withIdentity(key)
                .storeDurably()
                .build();
        job.getJobDataMap().putAll(data);
        scheduler.addJob(job, false);
    }

    @Override
    public void removeJob(JobKey key) throws SchedulerException {
        scheduler.deleteJob(key);
    }

    @Override
    public void queryJobs() {

    }

    @Override
    public void addTrigger() {

    }

    @Override
    public void removeTrigger() {

    }

    @Override
    public void queryTriggers() {

    }

    @Override
    public void skipNextTrigger() {

    }

}
