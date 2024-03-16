package net.brunochristensen._333bot.components.accountability;

import net.dv8tion.jda.api.JDA;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;

public class AccountabilityHandler {

    private final Scheduler scheduler;
    private final JobDetail accountabilityJob;

    public AccountabilityHandler(JDA api) throws SchedulerException {
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.clear();
        scheduler.start();
        accountabilityJob = JobBuilder.newJob(AccountabilityJob.class)
                .withIdentity("accountabilityJob", "accountabilityGroup")
                .build();
        accountabilityJob.getJobDataMap().put("api", api);
    }

    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
        Map<String, Map<String, String>> triggerData = new LinkedHashMap<>();
        for (TriggerKey tk : scheduler.getTriggerKeys(GroupMatcher.groupEquals("accountabilityGroup"))) {
            CronTrigger t = (CronTrigger) scheduler.getTrigger(tk);
            Map<String, String> m = new LinkedHashMap<>();
            m.put("Name", t.getDescription());
            m.put("Cron Expression", t.getCronExpression());
            m.put("Expression Summary", t.getExpressionSummary());
            m.put("Next Fire Time", t.getNextFireTime().toString());
            m.put("Previous Fire Time", t.getPreviousFireTime().toString());
            triggerData.put(t.getKey().getName(), m);
        }
        return triggerData;
    }

    public boolean newTrigger(String triggerName, String cronSch, String uniform, String time, String location)
            throws SchedulerException {
        if (!CronExpression.isValidExpression(cronSch)) {
            return false;
        }
        Trigger accountabilityTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, "accountabilityGroup")
                .startNow()
                .withSchedule(cronSchedule(cronSch))
                .withDescription(triggerName)
                .forJob(accountabilityJob)
                .build();
        accountabilityTrigger.getJobDataMap().putAll(
                Map.of("uniform", uniform, "time", time, "location", location));
        scheduler.scheduleJob(accountabilityJob, accountabilityTrigger);
        return true;
    }

    public boolean delTrigger(String triggerName) throws SchedulerException {
        HashSet<String> rt = new HashSet<>();
        scheduler.getTriggerKeys(GroupMatcher.groupEquals("accountabilityGroup"))
                .forEach(key -> rt.add(key.getName()));
        if(!rt.contains(triggerName)) {
            return false;
        }
        scheduler.unscheduleJob(new TriggerKey(triggerName, "accountabilityGroup"));
        return true;
    }

    public boolean modTrigger() {
        //TODO
        return false;
    }

    public boolean skipTrigger() {
        //TODO
        return false;
    }
}
