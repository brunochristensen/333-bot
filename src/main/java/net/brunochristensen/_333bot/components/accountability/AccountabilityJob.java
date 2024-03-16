package net.brunochristensen._333bot.components.accountability;

import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class AccountabilityJob implements Job {

    private JDA api;
    private String uniform;
    private String time;
    private String location;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        MessageEmbed infoMenu = new EmbedBuilder()
                .setTitle("Accountability")
                .setColor(Color.yellow)
                .setDescription("Report where you will be during accountability. THIS IS NOT AN OFFICIAL FORM OF "
                        + "COMMUNICATION. You still need to ask for MTL permission. Not sure why this feature "
                        + "is still here to be honest. If you are in processing this doesn't apply to you yet.")
                .addField("Uniform", uniform, false)
                .addField("Time", time, false)
                .addField("Location", location, false)
                .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
                .build();
        StringSelectMenu selectMenu = StringSelectMenu.create("choose-acc")
                .setRequiredRange(1, 1)
                .addOption("Sick Call", "Sick Call", "See MTL at first accountability.")
                .addOption("CQ", "CQ")
                .addOption("ITF", "ITF")
                .addOption("SIA", "SIA")
                .addOption("Sec+", "Sec+")
                .addOption("In-Processing", "In-Processing")
                .addOption("Graduation", "Graduation")
                .addOption("Out-processing", "Out-processing")
                .build();
        AccountabilityRecord.getInstance().resetAccountabilityReport();
        TextChannel channel = Objects.requireNonNull(
                api.getTextChannelById(Env.get("ACCOUNTABILITY_CHANNEL_ID")));
        List<Message> messages = channel.getHistory().retrievePast(10).complete();
        messages.forEach((message -> message.delete().complete()));
        channel.sendMessageEmbeds(infoMenu).addActionRow(selectMenu).queue();
    }

    @SuppressWarnings("unused")
    public void setApi(JDA api) {
        this.api = api;
    }

    @SuppressWarnings("unused")
    public void setUniform(String uniform) {
        this.uniform = uniform;
    }

    @SuppressWarnings("unused")
    public void setTime(String time) {
        this.time = time;
    }

    @SuppressWarnings("unused")
    public void setLocation(String location) {
        this.location = location;
    }
}
