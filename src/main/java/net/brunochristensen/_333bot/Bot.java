package net.brunochristensen._333bot;

import net.brunochristensen._333bot.commands.AccountabilityCommand;
import net.brunochristensen._333bot.components.accountability.AccountabilityRecord;
import net.brunochristensen._333bot.events.MemberJoinListener;
import net.brunochristensen._333bot.events.PingListener;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Objects;

public class Bot {

    public static void main(String[] args) {
        JDA api = JDABuilder.createDefault(Env.get("DISCORD_TOKEN"), GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.SCHEDULED_EVENTS)
                .addEventListeners(new AccountabilityCommand())
                .build();
        try {
            api.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        api.addEventListener(new MemberJoinListener(), new PingListener(), AccountabilityRecord.getInstance());
        Objects.requireNonNull(api.getGuildById(Env.get("GUILD_ID")))
                .updateCommands()
                .addCommands(Commands.slash("account", "Brings up the Accountability menu. ADMIN/MOD ONLY."))
                .queue();
    }
}