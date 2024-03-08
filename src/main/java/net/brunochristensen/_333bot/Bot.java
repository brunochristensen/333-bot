package net.brunochristensen._333bot;

import net.brunochristensen._333bot.commands.AccountabilityCommand;
import net.brunochristensen._333bot.events.MemberJoinListener;
import net.brunochristensen._333bot.events.PingListener;
import net.brunochristensen._333bot.utils.envGetter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Objects;

public class Bot {

    public static void main(String[] args) {

        //Create our api object. This will be the object that interacts with the discord api for us.
        JDA api = JDABuilder.createDefault(envGetter.get("DISCORD_TOKEN"),
                        //Discord requires that some intents (privileges) be explicitly declared. Do that here:
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.SCHEDULED_EVENTS)
                .addEventListeners().build();

        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*Add listeners here. New bot functionality should be implemented as separate
        objects in their own .class file. Be sure that the package hierarchy is maintained when
        new classes are created.*/
        api.addEventListener(new MemberJoinListener(),
                new PingListener(),
                new AccountabilityCommand());

        /*Add commands here. This just establishes a webhook to allow users to call commands.
        The logic of a command is implemented as a listener and must be registered above.*/
        Objects.requireNonNull(api.getGuildById(envGetter.get("GUILD_ID"))).updateCommands().addCommands(
                Commands.slash("account", "Brings up the Accountability Configuration menu.")
        ).queue();
    }
}