package net.brunochristensen._333bot.events;

import java.awt.Color;
import java.util.Objects;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String name = event.getMember().getEffectiveName();
        JDA api = event.getJDA();
        TextChannel ch = Objects.requireNonNull(api.getTextChannelById(Env.get("ACCOUNTABILITY_CHANNEL_ID")));
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.yellow);
        ch.sendMessageEmbeds(eb.build()).queue();
    }
}