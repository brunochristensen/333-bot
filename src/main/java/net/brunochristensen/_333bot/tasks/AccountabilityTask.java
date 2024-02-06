package net.brunochristensen._333bot.tasks;

import net.dv8tion.jda.api.JDA;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AccountabilityTask implements Job {

    private JDA api;
    private String channel;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }

    public void setApi(JDA api) {
        this.api = api;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
