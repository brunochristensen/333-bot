package net.brunochristensen._333bot.tasks.accountability;

import net.brunochristensen._333bot.utils.envGetter;
import net.dv8tion.jda.api.JDA;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Objects;

public class AccountabilityJob implements Job {

    private JDA api;
    private String UoD;
    private String time;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Objects.requireNonNull(api.getTextChannelById(envGetter.get("ACCOUNTABILITY_CHANNEL_ID"))).sendMessage("Testing").queue();
    }

    public void setApi(JDA api) {
        this.api = api;
    }

}
