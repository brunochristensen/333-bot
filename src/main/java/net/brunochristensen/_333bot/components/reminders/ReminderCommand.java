package net.brunochristensen._333bot.components.reminders;

import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class ReminderCommand extends ListenerAdapter {

    private final MessageEmbed controlMenuEmbed = new EmbedBuilder()
            .setTitle("Reminder Job Master Menu")
            .setColor(Color.yellow)
            .setDescription("Here you can view and configure when the bot will send out reminders.")
            .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
            .build();
    private ReminderBuilder handler;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        handler = new ReminderBuilder(event.getJDA());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName()
                .equals("remind")) {
            if (Objects.requireNonNull(event.getMember())
                    .getRoles()
                    .contains(event.getJDA()
                            .getRoleById(Env.get("ADMIN_ROLE_ID"))) ||
                    Objects.requireNonNull(event.getMember())
                            .getRoles()
                            .contains(event.getJDA()
                                    .getRoleById(Env.get("AL_ROLE_ID")))) {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(Button.success("remAdd", "Add New Reminder"))
                        .addActionRow(Button.danger("remDel", "Delete Reminder"))
                        .addActionRow(Button.secondary("remView", "View  Reminders"))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(Button.success("remAdd", "Add New Reminder")
                                .asDisabled())
                        .addActionRow(Button.danger("remDel", "Delete Reminder")
                                .asDisabled())
                        .addActionRow(Button.secondary("remView", "View  Reminders")
                                .asDisabled())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId()
                .equals("remAdd")) {
            handler.addJob();
            handler.addTrigger();
        } else if (event.getComponentId()
                .equals("remDel")) {
            handler.delTrigger();
            handler.delJob();
        } else if (event.getComponentId()
                .equals("remView")) {
            handler.getTriggerData();
        }
    }
}
