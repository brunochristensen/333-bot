package net.brunochristensen._333bot.features;

import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class TriggerBuilder {

    private final Trigger trigger;

    private SingleJobTriggerBuilder(String triggerName, String cronSch) throws SchedulerException {
        trigger = newTrigger().withIdentity(triggerName, groupName)
                .withSchedule(cronSchedule(cronSch))
                .forJob(scheduler.getJobDetail(new JobKey(jobName, groupName)))
                .startNow()
                .build();
    }

    public boolean schedule() throws SchedulerException {
        scheduler.scheduleJob(trigger);
        return true;
    }

    public <T> SingleJobHandler.SingleJobTriggerBuilder addToDataMap(String key, T value) {
        trigger.getJobDataMap()
                .put(key, value);
        return this;
    }

}
