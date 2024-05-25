package net.brunochristensen._333bot.features;

import net.dv8tion.jda.api.JDA;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;

public class SingleJobHandler {

//    private final String groupName;
//    private final String jobName;
    private final Scheduler scheduler;

    public SingleJobHandler(JDA api, Class<? extends org.quartz.Job> jobClass, String groupName,
                            String jobName) throws SchedulerException {
        this.groupName = groupName;
        this.jobName = jobName;
        this.scheduler = new StdSchedulerFactory().getScheduler();
        this.scheduler.start();
        JobDetail accountabilityJob = newJob(jobClass).withIdentity(jobName, groupName)
                .storeDurably()
                .build();
        accountabilityJob.getJobDataMap()
                .put("api", api);
        scheduler.addJob(accountabilityJob, false);
    }

//    public SingleJobTriggerBuilder buildTrigger(String triggerName, String cronSch)
//            throws SchedulerException {
//        if (isValidExpression(cronSch) && !scheduler.checkExists(
//                new TriggerKey(triggerName, groupName))) {
//            return new SingleJobTriggerBuilder(triggerName, cronSch);
//        }
//        throw new SchedulerException("Duplicate Trigger name or invalid Cron Schedule");
//    }
//
//    public boolean delTrigger(@NotNull String triggerName) throws SchedulerException {
//        TriggerKey triggerKey = new TriggerKey(triggerName, groupName);
//        if (scheduler.checkExists(triggerKey)) {
//            scheduler.unscheduleJob(triggerKey);
//            return true;
//        }
//        return false;
//    }
//
//    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
//        Map<String, Map<String, String>> triggerData = new LinkedHashMap<>();
//        for (TriggerKey tk : scheduler.getTriggerKeys(groupEquals(groupName))) {
//            CronTrigger t = (CronTrigger) scheduler.getTrigger(tk);
//            Map<String, String> m = new LinkedHashMap<>();
//            m.put("Next Fire Time", t.getNextFireTime()
//                    .toString());
//            m.put("Previous Fire Time", t.getPreviousFireTime() == null ? "N/A" : t.getPreviousFireTime()
//                    .toString());
//            m.put("Description", t.getDescription() == null ? "N/A" : t.getDescription());
//            m.put("Cron Expression", t.getCronExpression());
//            m.put("Expression Summary", t.getExpressionSummary());
//            triggerData.put(t.getKey()
//                    .getName(), m);
//        }
//        return triggerData;
//    }
//
//    public boolean skipNextTrigger() throws SchedulerException {
//        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(new JobKey(jobName, groupName));
//        if (triggers.isEmpty()) {
//            return false;
//        }
//        triggers.sort(Comparator.comparing(Trigger::getNextFireTime));
//        CronTrigger nextTrigger = (CronTrigger) triggers.get(0);
//        CronTrigger newTrigger = nextTrigger.getTriggerBuilder()
//                .startAt(nextTrigger.getFireTimeAfter(nextTrigger.getNextFireTime()))
//                .build();
//        scheduler.rescheduleJob(nextTrigger.getKey(), newTrigger);
//        return true;
//    }

}
