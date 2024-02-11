package net.brunochristensen._333bot.tasks.accountability;

import org.quartz.JobDetail;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.quartz.JobBuilder.newJob;

public class AccountabilityHandler {

    private final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    JobDetail accountabilityJob;
    private final List<Trigger> triggers = new ArrayList<>();

    public AccountabilityHandler() {
        accountabilityJob = newJob(AccountabilityTask.class)
                .withIdentity("accountabilityJob", "accountabilityGroup")
                .build();
        accountabilityJob.getJobDataMap().put("api", api);
    }

    public void newTrigger(){

    }

//    try {
//        Scheduler scheduler = schedulerFactory.getScheduler();
//        JobDetail accountabilityJob = newJob(AccountabilityTask.class)
//                .withIdentity("accountabilityJob", "group1")
//                .build();
//        Trigger accountabilityTrigger = TriggerBuilder.newTrigger()
//                .withIdentity("accountabilityTrigger", "group1")
//                .startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                        .withIntervalInSeconds(5)
//                        .repeatForever())
//                .build();
//        accountabilityJob.getJobDataMap().put("api", api);
//        accountabilityJob.getJobDataMap().put("channel", envGetter.get("ACCOUNTABILITY_CHANNEL_ID"));
//        scheduler.scheduleJob(accountabilityJob, accountabilityTrigger);
//        scheduler.start();
//    } catch (SchedulerException e) {
//        throw new RuntimeException(e);
//    }


}
