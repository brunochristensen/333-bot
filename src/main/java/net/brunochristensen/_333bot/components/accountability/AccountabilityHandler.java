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

    public static final String ACCOUNTABILITY_GROUP = "accountabilityGroup";
    private final Scheduler scheduler;
    private final JDA api;

    public AccountabilityHandler(JDA api) throws SchedulerException {
        this.scheduler = new StdSchedulerFactory().getScheduler();
        this.scheduler.start();
        this.api = api;
    }

    public boolean addJob(String jobName) throws SchedulerException {
        for (JobKey jobKey : scheduler.getJobKeys(groupEquals(ACCOUNTABILITY_GROUP))) {
            if (jobName.equals(jobKey.getName())) {
                return false;
            }
        }
        JobDetail accountabilityJob = JobBuilder.newJob(AccountabilityJob.class)
                .withIdentity(jobName, ACCOUNTABILITY_GROUP)
                .storeDurably()
                .build();
        accountabilityJob.getJobDataMap().put("api", api);
        scheduler.addJob(accountabilityJob, false);
        return true;
    }

    public boolean delJob(String jobName) throws SchedulerException {
        return false;
    }

    public boolean addTrigger(String triggerName, String jobName, String cronSch, String... args) throws SchedulerException {
        if (!isValidExpression(cronSch) ||
                scheduler.getTriggersOfJob(new JobKey("accountabilityJob", ACCOUNTABILITY_GROUP))
                        .stream().anyMatch(k -> k.getKey().getName().equals(triggerName))) {
            return false;
        }
        Trigger accountabilityTrigger = newTrigger()
                .withIdentity(triggerName, ACCOUNTABILITY_GROUP)
                .startNow()
                .withSchedule(cronSchedule(cronSch))
                .withDescription(triggerName)
                .forJob(scheduler.getJobDetail(new JobKey(jobName, ACCOUNTABILITY_GROUP)))
                .build();
        accountabilityTrigger.getJobDataMap().putAll(
                Map.of("uniform", args[0], "time", args[1], "location", args[2]));
        scheduler.scheduleJob(accountabilityTrigger);
        return true;
    }

    public boolean delTrigger(@NotNull String triggerName) throws SchedulerException {
        HashSet<String> rt = new HashSet<>();
        scheduler.getTriggerKeys(groupEquals(ACCOUNTABILITY_GROUP))
                .forEach(key -> rt.add(key.getName()));
        if (!rt.contains(triggerName)) {
            return false;
        }
        scheduler.unscheduleJob(new TriggerKey(triggerName, ACCOUNTABILITY_GROUP));
        return true;
    }

    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
        Map<String, Map<String, String>> triggerData = new LinkedHashMap<>();
        for (TriggerKey tk : scheduler.getTriggerKeys(groupEquals(ACCOUNTABILITY_GROUP))) {
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
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey("accountabilityJob", ACCOUNTABILITY_GROUP));
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
