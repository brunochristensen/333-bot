package net.brunochristensen._333bot.components;

import net.brunochristensen._333bot.components.accountability.AccountabilityRecordSingleton;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.*;

import static org.quartz.CronExpression.isValidExpression;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

public class SingleJobHandler {
    protected final String groupName;
    protected final String jobName;
    protected final Scheduler scheduler;

    protected SingleJobHandler(JDA api, Class<? extends org.quartz.Job> jobClass, String groupName, String jobName) throws SchedulerException {
        this.groupName = groupName;
        this.jobName = jobName;
        this.scheduler = new StdSchedulerFactory().getScheduler();
        this.scheduler.start();
        JobDetail accountabilityJob = newJob(jobClass)
                .withIdentity(jobName, groupName)
                .storeDurably()
                .build();
        accountabilityJob.getJobDataMap()
                .put("api", api);
        scheduler.addJob(accountabilityJob, false);
    }
    public SingleJobTriggerBuilder buildTrigger(String triggerName, String cronSch) throws SchedulerException {
        return new SingleJobTriggerBuilder(triggerName, cronSch);
    }

    public boolean delTrigger(@NotNull String triggerName) throws SchedulerException {
        HashSet<String> rt = new HashSet<>();
        scheduler.getTriggerKeys(groupEquals(groupName))
                .forEach(key -> rt.add(key.getName()));
        if (!rt.contains(triggerName)) {
            return false;
        }
        scheduler.unscheduleJob(new TriggerKey(triggerName, groupName));
        return true;
    }

    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
        Map<String, Map<String, String>> triggerData = new LinkedHashMap<>();
        for (TriggerKey tk : scheduler.getTriggerKeys(groupEquals(groupName))) {
            CronTrigger t = (CronTrigger) scheduler.getTrigger(tk);
            Map<String, String> m = new LinkedHashMap<>();
            m.put("Next Fire Time", t.getNextFireTime()
                    .toString());
            m.put("Previous Fire Time", t.getPreviousFireTime() == null ? "N/A" : t.getPreviousFireTime()
                    .toString());
            m.put("Description", t.getDescription() == null ? "N/A" : t.getDescription());
            m.put("Cron Expression", t.getCronExpression());
            m.put("Expression Summary", t.getExpressionSummary());
            triggerData.put(t.getKey()
                    .getName(), m);
        }
        return triggerData;
    }

    public Map<String, String> getAccountabilityResponses() {
        return AccountabilityRecordSingleton.getInstance()
                .getAccountabilityReport();
    }

    public boolean skipNextTrigger() throws SchedulerException {
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(jobName, groupName));
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

    public class SingleJobTriggerBuilder {
        private final Trigger trigger;

        private SingleJobTriggerBuilder(String triggerName, String cronSch) throws SchedulerException {
            if (isValidExpression(cronSch) &&
                    scheduler.getTriggersOfJob(new JobKey(jobName, groupName))
                            .stream()
                            .noneMatch(k -> k.getKey()
                                    .getName()
                                    .equals(triggerName))) {
                trigger = newTrigger()
                        .withIdentity(triggerName)
                        .withSchedule(cronSchedule(cronSch))
                        .forJob(scheduler.getJobDetail(new JobKey(jobName, groupName)))
                        .startNow()
                        .build();
            } else {
                throw new SchedulerException("Duplicate Trigger name or invalid Cron Schedule");
            }
        }

        public boolean schedule() throws SchedulerException {
            scheduler.scheduleJob(trigger);
            return true;
        }

        public <T> SingleJobTriggerBuilder addToDataMap(String key, T value) {
            trigger.getJobDataMap()
                    .put(key, value);
            return this;
        }
    }
}
