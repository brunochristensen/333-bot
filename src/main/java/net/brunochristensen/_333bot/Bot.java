package net.brunochristensen._333bot;

import net.brunochristensen._333bot.listeners.PingListener;
import net.brunochristensen._333bot.tasks.AccountabilityTask;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;

public class Bot {

    public static void main(String[] args){
        //Load our Dotenv object to reference our environment tokens
        Dotenv dotenv = Dotenv.configure().directory("./src/main/resources").filename("tokens.env").load();
        //Create our api object. This will be the object that interacts with the discord api for us.
        JDA api = JDABuilder.createDefault(dotenv.get("DISCORD_TOKEN"),
                //Discord requires that some intents (privileges) be explicitly declared. Do that here:
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.SCHEDULED_EVENTS).build();
        /*Add listeners here. New bot functionality should be implemented as separate
        objects in their own .class file. Be sure that the package hierarchy is maintained when
        new classes are created.*/
        api.addEventListener(new PingListener());
        /*Jobs*/
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            api.awaitReady();
            Scheduler scheduler = schedulerFactory.getScheduler();
            JobDetail accountabilityJob = newJob(AccountabilityTask.class)
                    .withIdentity("accountabilityJob", "group1")
                    .build();
            Trigger accountabilityTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("accountabilityTrigger", "group1")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(5)
                            .repeatForever())
                    .build();
            accountabilityJob.getJobDataMap().put("api", api);
            accountabilityJob.getJobDataMap().put("channel", dotenv.get("ACCOUNTABILITY_CHANNEL_ID"));
            scheduler.scheduleJob(accountabilityJob, accountabilityTrigger);
            scheduler.start();
        } catch (SchedulerException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}