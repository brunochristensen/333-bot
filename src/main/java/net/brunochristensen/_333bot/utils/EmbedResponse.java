package net.brunochristensen._333bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedResponse {
    public static MessageEmbed create(Color color, String content) {
        return new EmbedBuilder()
                .setColor(color)
                .setTitle(content)
                .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
                .build();
    }
}
