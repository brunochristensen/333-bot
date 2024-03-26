package net.brunochristensen._333bot.components.accountability;

import net.brunochristensen._333bot.components.JobHandler;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;

import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

public class AccountabilityHandler implements JobHandler {

    private final Scheduler scheduler;
    private final JDA api;

    public AccountabilityHandler(JDA api) throws SchedulerException {
        this.api = api;
        scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
    }

    public boolean addJob(String jobName) throws SchedulerException {
        for (JobKey jobKey : scheduler.getJobKeys(groupEquals("accountabilityGroup"))) {
            if (jobName.equals(jobKey.getName())) {
                return false;
            }
        }
        JobDetail accountabilityJob = JobBuilder.newJob(AccountabilityJob.class)
                .withIdentity(jobName, "accountabilityGroup")
                .storeDurably()
                .build();
        accountabilityJob.getJobDataMap().put("api", api);
        scheduler.addJob(accountabilityJob, false);
        return true;
    }

    public boolean delJob(String jobName) throws SchedulerException {
        return false;
    }

    public boolean addTrigger(String triggerName, String cronSch, String... args) throws SchedulerException {
        if (!isValidExpression(cronSch) ||
                scheduler.getTriggersOfJob(new JobKey("accountabilityJob", "accountabilityGroup"))
                        .stream().anyMatch(k -> k.getKey().getName().equals(triggerName))) {
            return false;
        }
        Trigger accountabilityTrigger = newTrigger()
                .withIdentity(triggerName, "accountabilityGroup")
                .startNow()
                .withSchedule(cronSchedule(cronSch))
                .withDescription(triggerName)
                .forJob(scheduler.getJobDetail(new JobKey("accountabilityJob", "accountabilityGroup")))
                .build();
        accountabilityTrigger.getJobDataMap().putAll(
                Map.of("uniform", args[0], "time", args[1], "location", args[2]));
        scheduler.scheduleJob(accountabilityTrigger);
        return true;
    }

    public boolean delTrigger(@NotNull String triggerName) throws SchedulerException {
        HashSet<String> rt = new HashSet<>();
        scheduler.getTriggerKeys(groupEquals("accountabilityGroup"))
                .forEach(key -> rt.add(key.getName()));
        if (!rt.contains(triggerName)) {
            return false;
        }
        scheduler.unscheduleJob(new TriggerKey(triggerName, "accountabilityGroup"));
        return true;
    }

    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
        Map<String, Map<String, String>> triggerData = new LinkedHashMap<>();
        for (TriggerKey tk : scheduler.getTriggerKeys(groupEquals("accountabilityGroup"))) {
            CronTrigger t = (CronTrigger) scheduler.getTrigger(tk);
            Map<String, String> m = new LinkedHashMap<>();
            m.put("Name", t.getDescription());
            m.put("Cron Expression", t.getCronExpression());
            m.put("Next Fire Time", t.getNextFireTime().toString());
            m.put("Previous Fire Time", (t.getPreviousFireTime() == null) ? "N/A" : t.getPreviousFireTime().toString());
            m.put("Expression Summary", t.getExpressionSummary());
            triggerData.put(t.getKey().getName(), m);
        }
        return triggerData;
    }

    public Map<String, String> getTriggerResponses() {
        return AccountabilityRecordSingleton.getInstance().getAccountabilityReport();
    }

    public boolean skipTrigger() throws SchedulerException {
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey("accountabilityJob", "accountabilityGroup"));
        if (triggers.isEmpty()) {
            return false;
        }
        triggers.sort(Comparator.comparing(Trigger::getNextFireTime));
        CronTrigger nextTrigger = (CronTrigger) triggers.get(0);
        CronTrigger newTrigger = nextTrigger.getTriggerBuilder()
                .startAt(nextTrigger.getFireTimeAfter(nextTrigger.getNextFireTime()))
                .build();
        scheduler.rescheduleJob(nextTrigger.getKey(), newTrigger);
        return true;
    }
}
