package net.brunochristensen._333bot.components.accountability;

import net.brunochristensen._333bot.utils.envGetter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.awt.*;
import java.util.Objects;

public class AccountabilityJob implements Job {

    private JDA api;
    private String uniform;
    private String time;
    private String location;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
//        EmbedBuilder eb = new EmbedBuilder();
//        eb.setTitle("Accountability");
//        eb.setColor(Color.yellow);
//        eb.setDescription("Report where you will be during accountability. THIS IS NOT AN OFFICIAL FORM OF " +
//                "COMMUNICATION. You still need to ask for MTL permission. Not sure why this feature is still here " +
//                "to be honest.\nIf you are in processing this doesn't apply to you yet.");
//        MessageEmbed intro = eb.build();
//        MessageEmbed t = simpleEmbed("Time", time);
//        MessageEmbed u = simpleEmbed("Uniform", uniform);
//        MessageEmbed l = simpleEmbed("Location", location);
//        StringSelectMenu.Builder ssmb = StringSelectMenu.create("choose-acc")
//                .addOption("Sick Call", "Sick Call", "See MTL at first accountability.")
//                .addOption("CQ", "CQ")
//                .addOption("ITF", "ITF")
//                .addOption("SIA", "SIA")
//                .addOption("Sec+", "Sec+")
//                .addOption("In-Processing", "In-Processing")
//                .addOption("Graduation", "Graduation")
//                .addOption("Out-processing", "Out-processing");
//        Objects.requireNonNull(api.getTextChannelById(envGetter.get("ACCOUNTABILITY_CHANNEL_ID")))
//                .sendMessageEmbeds(Arrays.asList(intro, t, u , l)).addActionRow(ssmb.build()).queue();
        Objects.requireNonNull(api.getTextChannelById(envGetter.get("ACCOUNTABILITY_CHANNEL_ID")))
                .sendMessage(uniform + time + location).queue();
    }

    public void setApi(JDA api) {
        this.api = api;
    }

    public void setUniform(String uniform) {
        this.uniform = uniform;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NotNull
    private MessageEmbed simpleEmbed(String title, String desc) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title);
        eb.setColor(Color.YELLOW);
        eb.setDescription(desc);
        return eb.build();
    }
}
