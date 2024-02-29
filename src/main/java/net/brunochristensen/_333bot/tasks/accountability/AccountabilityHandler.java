package net.brunochristensen._333bot.tasks.accountability;

import net.dv8tion.jda.api.JDA;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Map;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

public class AccountabilityHandler {

    private final Scheduler scheduler;
    JobDetail accountabilityJob;

    public AccountabilityHandler(JDA api) {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        accountabilityJob = newJob(AccountabilityJob.class)
                .withIdentity("accountabilityJob")
                .build();
        accountabilityJob.getJobDataMap().put("api", api);
    }

    public String viewTriggers() {
        StringBuilder sb = new StringBuilder();
        try {
            Set<TriggerKey> keys = scheduler.getTriggerKeys(GroupMatcher.anyGroup());
            for (TriggerKey tk : keys) {
                CronTrigger t = (CronTrigger) scheduler.getTrigger(tk);
                sb.append(String.format("%s, %s\n%s\n",
                        t.getDescription(),
                        t.getCronExpression(),
                        t.getExpressionSummary()));
            }
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public void newTrigger(String triggerName, String cronSch, String uniform, String time, String location) {
        CronTrigger accountabilityTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName)
                .startNow()
                .withSchedule(cronSchedule(cronSch))
                .withDescription(triggerName)
                .forJob(accountabilityJob)
                .build();
        accountabilityTrigger.getJobDataMap()
                .putAll(Map.of("uniform", uniform, "time", time, "location", location));
        try {
            scheduler.scheduleJob(accountabilityTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void delTrigger(String triggerName) {
        try {
            scheduler.unscheduleJob(new TriggerKey(triggerName));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void modTrigger() {
        //TODO
    }

    public void skipTrigger() {
        //TODO
    }

}
