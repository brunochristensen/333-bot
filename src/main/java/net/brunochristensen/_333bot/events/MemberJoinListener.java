package net.brunochristensen._333bot.events;

import net.brunochristensen._333bot.utils.envGetter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class MemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String name = event.getMember().getEffectiveName();
        JDA api = event.getJDA();
        TextChannel ch = api.getTextChannelById(envGetter.get("ACCOUNTABILITY_CHANNEL_ID"));
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.yellow);
        assert ch != null;
        ch.sendMessageEmbeds(eb.build()).queue();
    }
}