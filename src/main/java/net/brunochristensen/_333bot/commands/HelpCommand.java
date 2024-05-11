package net.brunochristensen._333bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HelpCommand extends ListenerAdapter {
    private final MessageEmbed helpEmbed = new EmbedBuilder()
            .setTitle("You clearly need some help.")
            .setColor(Color.GREEN)
            .setDescription("Here are some useful commands you might be able to use:")
            .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
            .addField("/help", "That's me!", false)
            .addField("/account", "This command will bring up a menu to configure the " +
                            "accountability scheduler. You may need permissions to fully utilize this command.",
                    false)
            .addField("/remind", "This command opens a menu to configure server wide reminders. " +
                    "Permissions are also needed to utilize this command.", false)
            .addField("/ping", "pong!", false)
            .build();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName()
                .equals("help")) {
            event.replyEmbeds(helpEmbed).setEphemeral(true).queue();
        }
    }
}
