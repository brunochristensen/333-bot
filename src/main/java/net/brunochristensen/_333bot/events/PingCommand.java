package net.brunochristensen._333bot.events;

import net.brunochristensen._333bot.utils.EmbedResponse;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class PingCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName()
                .equals("ping")) {
            event.replyEmbeds(EmbedResponse.success("pong!")
                            .build())
                    .setEphemeral(true)
                    .queue();
        }
    }
}
