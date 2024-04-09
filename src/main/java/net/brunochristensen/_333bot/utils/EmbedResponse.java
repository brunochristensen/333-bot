package net.brunochristensen._333bot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class EmbedResponse {
    private static final String BOT_NAME = "333-bot";
    private static final String URL = "https://github.com/brunochristensen/333-bot";

    private EmbedResponse() {
        throw new IllegalStateException("Utility class");
    }

    @NotNull
    public static EmbedBuilder success(String content) {
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(content)
                .setAuthor(BOT_NAME, URL);
    }

    @NotNull
    public static EmbedBuilder failure(String content) {
        return new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(content)
                .setAuthor(BOT_NAME, URL);
    }

    @NotNull
    public static EmbedBuilder error(String content) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(content)
                .setAuthor(BOT_NAME, URL);
    }

    public static EmbedBuilder message(String title, String body) {
        return new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(title)
                .setDescription(body)
                .setAuthor(BOT_NAME, URL);
    }
}
