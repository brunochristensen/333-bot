package net.brunochristensen._333bot.commands;

import net.brunochristensen._333bot.components.accountability.AccountabilityHandler;
import net.brunochristensen._333bot.components.accountability.AccountabilityRecord;
import net.brunochristensen._333bot.utils.envGetter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;

import java.awt.*;
import java.util.Hashtable;
import java.util.Objects;

public class AccountabilityCommand extends ListenerAdapter {

    private final MessageEmbed controlMenu = new EmbedBuilder().setTitle("Accountability Job Master Menu")
            .setColor(Color.yellow)
            .setDescription("Here you can view and configure when the bot will send out " +
                            "requests for accountability, Triggers can be created, deleted, modified, " +
                            "or paused here.")
            .build();
    private AccountabilityHandler handler;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            this.handler = new AccountabilityHandler(event.getJDA());
        } catch (SchedulerException e) {
            Objects.requireNonNull(event.getJDA()
                            .getTextChannelById(envGetter.get("DEBUG_CHANNEL_ID")))
                    .sendMessage("AccountabilityHandler failed to fetch scheduler.")
                    .queue();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("account")) {
            event.replyEmbeds(controlMenu)
                    .addActionRow(Button.primary("accView", "View Accountability Times"))
                    .addActionRow(Button.primary("accSkip", "Skip Next Accountability Time"))
                    .addActionRow(Button.primary("accAdd", "Add New Accountability Times"))
                    .addActionRow(Button.primary("accDel", "Delete Accountability Times"))
                    .addActionRow(Button.primary("accMod", "Modify Accountability Times"))
                    .setEphemeral(true)
                    .queue();
        } else if (event.getName().equals("getaccount")) {
            Hashtable<String, String> report = AccountabilityRecord.getInstance()
                    .getAccountabilityReport();
            StringBuilder sb = new StringBuilder();
            report.forEach((k, v) -> sb.append(String.format("%s, %s\n", k, v)));
            event.reply(sb.toString()).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId()
                .equals("accView")) {
            try {
                event.reply(handler.viewTriggers())
                        .setEphemeral(true)
                        .queue();
            } catch (SchedulerException e) {
                event.reply("Failed to fetch Keys from scheduler. Contact a dev for assistance.")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        } else if (event.getComponentId()
                .equals("accSkip")) {
            event.reply("Not Implemented")
                    .setEphemeral(true)
                    .queue();
        } else if (event.getComponentId()
                .equals("accAdd")) {
            TextInput triggerName = TextInput.create("triggerName", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name for Trigger")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();
            TextInput cronSch = TextInput.create("cronSch", "CronSch", TextInputStyle.SHORT)
                    .setPlaceholder("CronJob formatted schedule")
                    .setMinLength(10)
                    .setMaxLength(100)
                    .build();
            TextInput uniform = TextInput.create("uniform", "Uniform", TextInputStyle.SHORT)
                    .setPlaceholder("Uniform of the Day")
                    .setMinLength(3)
                    .setMaxLength(10)
                    .build();
            TextInput time = TextInput.create("time", "Time", TextInputStyle.SHORT)
                    .setPlaceholder("Time (e.g. 0550)")
                    .setMinLength(4)
                    .setMaxLength(4)
                    .build();
            TextInput location = TextInput.create("location", "Location", TextInputStyle.SHORT)
                    .setPlaceholder("Where accountability is taking place")
                    .setMinLength(1)
                    .setMaxLength(50)
                    .build();
            Modal modal = Modal.create("newAccModal", "New Accountability Schedule")
                    .addComponents(ActionRow.of(triggerName), ActionRow.of(cronSch), ActionRow.of(uniform),
                            ActionRow.of(time), ActionRow.of(location))
                    .build();
            event.replyModal(modal)
                    .queue();
        } else if (event.getComponentId()
                .equals("accDel")) {
            TextInput triggerName = TextInput.create("triggerName", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name of Trigger")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();
            Modal modal = Modal.create("delAccModal", "Delete Accountability Schedule")
                    .addComponents(ActionRow.of(triggerName))
                    .build();
            event.replyModal(modal)
                    .queue();
        } else if (event.getComponentId()
                .equals("accMod")) {
            event.reply("Not Implemented")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId()
                .equals("newAccModal")) {
            try {
                String triggerName = Objects.requireNonNull(event.getValue("triggerName"))
                        .getAsString();
                String cronSch = Objects.requireNonNull(event.getValue("cronSch"))
                        .getAsString();
                String uniform = Objects.requireNonNull(event.getValue("uniform"))
                        .getAsString();
                String time = Objects.requireNonNull(event.getValue("time"))
                        .getAsString();
                String location = Objects.requireNonNull(event.getValue("location"))
                        .getAsString();
                if (CronExpression.isValidExpression(cronSch)) {
                    handler.newTrigger(triggerName, cronSch, uniform, time, location);
                    event.reply("New accountability Trigger scheduled.")
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.reply("Invalid Cron expression.")
                            .setEphemeral(true)
                            .queue();
                }
            } catch (NullPointerException e) {
                event.reply("Invalid field entry of scheduled Trigger")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            } catch (SchedulerException e) {
                event.reply("Scheduler failed to add Trigger. Contact a dev for assistance.")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        } else if (event.getModalId()
                .equals("delAccModal")) {
            try {
                String triggerName = Objects.requireNonNull(event.getValue("triggerName"))
                        .getAsString();
                if (handler.getTriggerNames()
                        .contains(triggerName)) {
                    handler.delTrigger(triggerName);
                    event.reply("Accountability Trigger deleted.")
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.reply("Invalid Trigger name.")
                            .setEphemeral(true)
                            .queue();
                }
            } catch (NullPointerException e) {
                event.reply("Invalid field entry of scheduled Trigger")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            } catch (SchedulerException e) {
                event.reply("Scheduler failed to remove Trigger. Contact a dev for assistance.")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        }
    }

}