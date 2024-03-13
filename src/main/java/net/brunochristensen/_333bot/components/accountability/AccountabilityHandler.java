package net.brunochristensen._333bot.components.accountability;

import net.dv8tion.jda.api.JDA;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

public class AccountabilityHandler {

    private final Scheduler scheduler;
    JobDetail accountabilityJob;

    public AccountabilityHandler(JDA api) throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        accountabilityJob = newJob(AccountabilityJob.class).withIdentity("accountabilityJob", "accountabilityGroup")
                .build();
        accountabilityJob.getJobDataMap().put("api", api);
    }

    public String viewTriggers() throws SchedulerException {
        StringBuilder sb = new StringBuilder();
        Set<TriggerKey> keys = scheduler.getTriggerKeys(GroupMatcher.groupEquals("accountabilityGroup"));
        for (TriggerKey tk : keys) {
            CronTrigger t = (CronTrigger) scheduler.getTrigger(tk);
            sb.append(String.format("Name: %s\nCron Schedule: %s\nSummary:\n%s\n", t.getDescription(),
                    t.getCronExpression(), t.getExpressionSummary()));
        }
        return sb.isEmpty() ? "There are no currently scheduled Jobs" : sb.toString();
    }

    public void newTrigger(String triggerName, String cronSch, String uniform, String time, String location)
            throws SchedulerException {
        CronTrigger accountabilityTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "accountabilityGroup")
                .startNow()
                .withSchedule(cronSchedule(cronSch))
                .withDescription(triggerName)
                .forJob("accountabilityJob", "accountabilityGroup")
                .build();
        accountabilityTrigger.getJobDataMap()
                .putAll(Map.of("uniform", uniform, "time", time, "location", location));
        scheduler.scheduleJob(accountabilityJob, accountabilityTrigger);
    }

    public void delTrigger(String triggerName) throws SchedulerException {
        scheduler.unscheduleJob(new TriggerKey(triggerName, "accountabilityGroup"));
    }

    public void modTrigger() {
        //TODO
    }

    public void skipTrigger() {
        //TODO
    }

    public HashSet<String> getTriggerNames() throws SchedulerException {
        HashSet<String> rt = new HashSet<>();
        Set<TriggerKey> keys = scheduler.getTriggerKeys(GroupMatcher.groupEquals("accountabilityGroup"));
        keys.forEach(key -> rt.add(key.getName()));
        return rt;
    }

}
