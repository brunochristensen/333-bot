package net.brunochristensen._333bot.commands;

import net.brunochristensen._333bot.components.accountability.AccountabilityHandler;
import net.brunochristensen._333bot.components.accountability.AccountabilityRecord;
import net.brunochristensen._333bot.utils.EmbedResponse;
import net.brunochristensen._333bot.utils.Env;
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

    private final MessageEmbed controlMenuEmbed = new EmbedBuilder()
            .setTitle("Accountability Job Master Menu")
            .setColor(Color.yellow)
            .setDescription("Here you can view and configure when the bot will send out requests for " +
                    "accountability, Triggers can be created, deleted, modified, or paused here.")
            .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot")
            .build();
    private final Modal additionModal = Modal.create("newAccModal", "New Accountability Schedule")
            .addComponents(ActionRow.of(TextInput.create("triggerName", "TriggerName", TextInputStyle.SHORT)
                            .setPlaceholder("Unique name for Trigger")
                            .setRequiredRange(1, 100)
                            .build()),
                    ActionRow.of(TextInput.create("cronSch", "CronSch", TextInputStyle.SHORT)
                            .setPlaceholder("CronJob formatted schedule")
                            .setRequiredRange(10, 100)
                            .build()),
                    ActionRow.of(TextInput.create("uniform", "Uniform", TextInputStyle.SHORT)
                            .setPlaceholder("Uniform of the Day")
                            .setRequiredRange(3, 10)
                            .build()),
                    ActionRow.of(TextInput.create("time", "Time", TextInputStyle.SHORT)
                            .setPlaceholder("Time (e.g. 0550)")
                            .setRequiredRange(4, 4)
                            .build()),
                    ActionRow.of(TextInput.create("location", "Location", TextInputStyle.SHORT)
                            .setPlaceholder("Where accountability is taking place")
                            .setRequiredRange(1, 50)
                            .build()))
            .build();
    private final Modal deletionModal = Modal.create("delAccModal", "Delete Accountability Schedule")
            .addComponents(ActionRow.of(TextInput.create("triggerName", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name of Trigger")
                    .setRequiredRange(1, 100)
                    .build()))
            .build();
    private AccountabilityHandler handler;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            this.handler = new AccountabilityHandler(event.getJDA());
        } catch (SchedulerException e) {
            Objects.requireNonNull(event.getJDA().getTextChannelById(Env.get("DEBUG_CHANNEL_ID")))
                    .sendMessageEmbeds(EmbedResponse.create(Color.RED, "AccountabilityHandler failed to fetch scheduler."))
                    .queue();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("account")) {
            event.replyEmbeds(controlMenuEmbed)
                    .addActionRow(Button.success("accAdd", "Add New Accountability Times"))
                    .addActionRow(Button.danger("accDel", "Delete Accountability Times"))
                    .addActionRow(Button.secondary("accView", "View Accountability Times"))
                    .addActionRow(Button.primary("accSkip", "Skip Next Accountability Time"))
                    .addActionRow(Button.primary("accGet", "Get Accountability Responses"))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("accAdd")) {
            event.replyModal(additionModal)
                    .queue();
        } else if (event.getComponentId().equals("accDel")) {
            event.replyModal(deletionModal)
                    .queue();
        } else if (event.getComponentId().equals("accView")) {
            try {
                event.replyEmbeds(EmbedResponse.create(Color.GREEN, handler.viewTriggers()))
                        .setEphemeral(true)
                        .queue();
            } catch (SchedulerException e) {
                event.replyEmbeds(EmbedResponse.create(Color.RED, "Failed to fetch Keys from scheduler. Contact a dev for assistance."))
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        } else if (event.getComponentId().equals("accSkip")) {
            event.reply("Not Implemented")
                    .setEphemeral(true)
                    .queue();
        } else if (event.getComponentId().equals("accGet")) {
            StringBuilder sb = new StringBuilder();
            AccountabilityRecord.getInstance().getAccountabilityReport()
                    .forEach((k, v) -> sb.append(String.format("%s, %s\n", k, v)));
            event.replyEmbeds(EmbedResponse.create(Color.GREEN, sb.toString()))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("newAccModal")) {
            try {
                String triggerName = Objects.requireNonNull(event.getValue("triggerName")).getAsString();
                String cronSch = Objects.requireNonNull(event.getValue("cronSch")).getAsString();
                String uniform = Objects.requireNonNull(event.getValue("uniform")).getAsString();
                String time = Objects.requireNonNull(event.getValue("time")).getAsString();
                String location = Objects.requireNonNull(event.getValue("location")).getAsString();
                if (CronExpression.isValidExpression(cronSch)) {
                    handler.newTrigger(triggerName, cronSch, uniform, time, location);
                    event.replyEmbeds(EmbedResponse.create(Color.GREEN,"New accountability Trigger scheduled."))
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(EmbedResponse.create(Color.YELLOW,"Invalid Cron expression."))
                            .setEphemeral(true)
                            .queue();
                }
            } catch (SchedulerException e) {
                event.replyEmbeds(EmbedResponse.create(Color.RED,"Scheduler failed to add Trigger. Contact a dev for assistance."))
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        } else if (event.getModalId().equals("delAccModal")) {
            try {
                String triggerName = Objects.requireNonNull(event.getValue("triggerName")).getAsString();
                if (handler.getTriggerNames().contains(triggerName)) {
                    handler.delTrigger(triggerName);
                    event.reply("Accountability Trigger deleted.")
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.reply("Invalid Trigger name.")
                            .setEphemeral(true)
                            .queue();
                }
            } catch (SchedulerException e) {
                event.reply("Scheduler failed to remove Trigger. Contact a dev for assistance.")
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        }
    }
}