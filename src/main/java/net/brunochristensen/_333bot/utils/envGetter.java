package net.brunochristensen._333bot.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class envGetter {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./src/main/resources/env")
            .filename("tokens.env")
            .load();

    public static String get(String s) {
        return dotenv.get(s);
    }
}
