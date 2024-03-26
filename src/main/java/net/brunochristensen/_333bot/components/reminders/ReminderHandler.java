package net.brunochristensen._333bot.components.reminders;

import net.brunochristensen._333bot.components.JobHandler;
import net.dv8tion.jda.api.JDA;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReminderHandler implements JobHandler {
    private Scheduler scheduler;

    public ReminderHandler(JDA api) throws SchedulerException {

    }

    public Map<String, Map<String, String>> getTriggerData() throws SchedulerException {
        Map<String, Map<String, String>> triggerData = new LinkedHashMap<>();
        for (TriggerKey tk : scheduler.getTriggerKeys(GroupMatcher.groupEquals("accountabilityGroup"))) {
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

    @Override
    public boolean addJob(String jobName) throws SchedulerException {
        return false;
    }

    @Override
    public boolean delJob(String jobName) throws SchedulerException {
        return false;
    }

    @Override
    public boolean addTrigger(String triggerName, String cronSch, String... args) throws SchedulerException {
        return false;
    }

    @Override
    public boolean delTrigger(String triggerName) throws SchedulerException {
        return false;
    }
}
