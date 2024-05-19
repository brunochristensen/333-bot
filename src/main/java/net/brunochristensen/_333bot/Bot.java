package net.brunochristensen._333bot;

import net.brunochristensen._333bot.commands.HelpCommand;
import net.brunochristensen._333bot.commands.PingCommand;
import net.brunochristensen._333bot.events.MemberJoinListener;
import net.brunochristensen._333bot.features.accountability.AccountabilitySlashCommand;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Bot {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Bot.class);
        JDA api = JDABuilder.createDefault(Env.get("DISCORD_TOKEN"), GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS)
                .addEventListeners(new AccountabilitySlashCommand())
                .setActivity(Activity.listening("the world burn."))
                .build();
        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            logger.error(e.toString());
            Thread.currentThread()
                    .interrupt();
        }
        api.addEventListener(new MemberJoinListener(), new PingCommand(), new HelpCommand());
        Objects.requireNonNull(api.getGuildById(Env.get("GUILD_ID")))
                .updateCommands()
                .addCommands(Commands.slash("account", "Brings up the accountability config menu."),
                        Commands.slash("remind", "Brings up the reminder config menu."),
                        Commands.slash("help", "A lovely list of commands you may or may not be able to use."),
                        Commands.slash("ping", "pong!"))
                .queue();
    }
}