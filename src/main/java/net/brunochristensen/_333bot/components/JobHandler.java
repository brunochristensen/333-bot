package net.brunochristensen._333bot.components;

import org.quartz.SchedulerException;

import java.util.Map;

public interface JobHandler {

    boolean addJob(String jobName) throws SchedulerException;

    boolean delJob(String jobName) throws SchedulerException;

    boolean addTrigger(String triggerName, String cronSch, String... args) throws SchedulerException;

    boolean delTrigger(String triggerName) throws SchedulerException;
}
