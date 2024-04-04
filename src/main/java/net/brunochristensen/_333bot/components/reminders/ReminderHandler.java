package net.brunochristensen._333bot.components.reminders;

import net.brunochristensen._333bot.components.JobHandler;
import net.dv8tion.jda.api.JDA;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.Map;

public class ReminderHandler implements JobHandler {
    private Scheduler scheduler;
    private Trigger storedTrigger;

    public ReminderHandler(JDA api) throws SchedulerException {

    }

    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
        return null;
    }

    @Override
    public boolean addJob(String jobName) throws SchedulerException {
        return false;
    }

    @Override
    public boolean delJob(String jobName) throws SchedulerException {
        return false;
    }

    @Override
    public boolean addTrigger(String triggerName, String jobName, String cronSch, String... args) throws SchedulerException {
        return false;
    }

    @Override
    public boolean delTrigger(String triggerName) throws SchedulerException {
        return false;
    }

    public boolean schedule(String jobName, String triggerName) throws SchedulerException {
        scheduler.scheduleJob(scheduler.getJobDetail(new JobKey(jobName)), storedTrigger);
        return true;
    }
}
