package net.brunochristensen._333bot;

import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Bot {

    private final Logger logger;
    private final JDA api;

    public Bot() {
        this.logger = LoggerFactory.getLogger(Bot.class);
        this.api = JDABuilder.createDefault(Env.get("DISCORD_TOKEN"),
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.SCHEDULED_EVENTS)
                .setActivity(Activity.listening("the world burn."))
                .build();
        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            logger.error(e.toString());
            Thread.currentThread().interrupt();
        }
    }

    public void addFeatureManager(FeatureManager... managers) {
        Arrays.stream(managers).iterator().forEachRemaining(manager -> manager.registerEventListeners(api));
    }

}