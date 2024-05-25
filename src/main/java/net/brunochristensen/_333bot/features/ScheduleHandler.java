package net.brunochristensen._333bot.features;

import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import java.util.Map;

public interface ScheduleHandler {

    public void addJob(Class<? extends Job> jobClass, JobKey key, Map<String, Object> data) throws SchedulerException;

    public void removeJob(JobKey key) throws SchedulerException;

    public void queryJobs();

    public void addTrigger();

    public void removeTrigger();

    public void queryTriggers();

    public void skipNextTrigger();

}
