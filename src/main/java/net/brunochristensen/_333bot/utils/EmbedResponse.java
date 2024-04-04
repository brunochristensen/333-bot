package net.brunochristensen._333bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EmbedResponse {
    private static final String BOTNAME = "333-bot";
    private static final String URL = "https://github.com/brunochristensen/333-bot";

    private EmbedResponse() {
        throw new IllegalStateException("Utility class");
    }

    @NotNull
    public static MessageEmbed success(String content) {
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(content)
                .setAuthor(BOTNAME, URL)
                .build();
    }

    @NotNull
    public static MessageEmbed failure(String content) {
        return new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(content)
                .setAuthor(BOTNAME, URL)
                .build();
    }

    @NotNull
    public static MessageEmbed error(String content) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(content)
                .setAuthor(BOTNAME, URL)
                .build();
    }

    public static MessageEmbed message(String title, String body) {
        return new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(title)
                .setDescription(body)
                .setAuthor(BOTNAME, URL)
                .build();
    }
}
