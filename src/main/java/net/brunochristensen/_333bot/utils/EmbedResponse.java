package net.brunochristensen._333bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EmbedResponse {
    @NotNull
    public static MessageEmbed success(String content) {
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(content)
                .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
                .build();
    }

    @NotNull
    public static MessageEmbed failure(String content) {
        return new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(content)
                .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
                .build();
    }

    @NotNull
    public static MessageEmbed error(String content) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(content)
                .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
                .build();
    }
}
