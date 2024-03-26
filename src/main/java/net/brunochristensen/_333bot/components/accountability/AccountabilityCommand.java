package net.brunochristensen._333bot.components.accountability;

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
import org.quartz.SchedulerException;

import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

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
                            .setRequiredRange(1, 50)
                            .build()),
                    ActionRow.of(TextInput.create("cronSch", "CronSch", TextInputStyle.SHORT)
                            .setPlaceholder("CronJob formatted schedule")
                            .setRequiredRange(1, 50)
                            .build()),
                    ActionRow.of(TextInput.create("uniform", "Uniform", TextInputStyle.SHORT)
                            .setPlaceholder("Uniform of the Day")
                            .setRequiredRange(1, 50)
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
        event.getJDA().addEventListener(AccountabilityRecordSingleton.getInstance());
        try {
            handler = new AccountabilityHandler(event.getJDA());
            handler.addJob("accountabilityJob");
        } catch (SchedulerException e) {
            Objects.requireNonNull(event.getJDA().getTextChannelById(Env.get("DEBUG_CHANNEL_ID")))
                    .sendMessageEmbeds(EmbedResponse.error("AccountabilityHandler failed to fetch scheduler."))
                    .queue();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("account")) {
            if (Objects.requireNonNull(event.getMember())
                    .getRoles().contains(event.getJDA().getRoleById(Env.get("ADMIN_ROLE_ID")))) {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(Button.success("accAdd", "Add New Accountability Times"))
                        .addActionRow(Button.danger("accDel", "Delete Accountability Times"))
                        .addActionRow(Button.secondary("accView", "View Accountability Times"))
                        .addActionRow(Button.primary("accSkip", "Skip Next Accountability Time"))
                        .addActionRow(Button.primary("accGet", "Get Accountability Responses"))
                        .setEphemeral(true)
                        .queue();
            } else if (Objects.requireNonNull(event.getMember())
                    .getRoles().contains(event.getJDA().getRoleById(Env.get("AL_ROLE_ID")))) {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(Button.success("accAdd", "Add New Accountability Times").asDisabled())
                        .addActionRow(Button.danger("accDel", "Delete Accountability Times").asDisabled())
                        .addActionRow(Button.secondary("accView", "View Accountability Times"))
                        .addActionRow(Button.primary("accSkip", "Skip Next Accountability Time").asDisabled())
                        .addActionRow(Button.primary("accGet", "Get Accountability Responses"))
                        .setEphemeral(true)
                        .queue();
            } else {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(Button.success("accAdd", "Add New Accountability Times").asDisabled())
                        .addActionRow(Button.danger("accDel", "Delete Accountability Times").asDisabled())
                        .addActionRow(Button.secondary("accView", "View Accountability Times").asDisabled())
                        .addActionRow(Button.primary("accSkip", "Skip Next Accountability Time").asDisabled())
                        .addActionRow(Button.primary("accGet", "Get Accountability Responses").asDisabled())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            if (event.getComponentId().equals("accAdd")) {
                event.replyModal(additionModal)
                        .queue();
            } else if (event.getComponentId().equals("accDel")) {
                event.replyModal(deletionModal)
                        .queue();
            } else if (event.getComponentId().equals("accView")) {
                Map<String, Map<String, String>> data = handler.getTriggerData();
                if (data.isEmpty()) {
                    event.replyEmbeds(EmbedResponse.success("There are no Jobs currently scheduled."))
                            .setEphemeral(true)
                            .queue();
                } else {
                    EmbedBuilder tmp = new EmbedBuilder().setColor(Color.GREEN)
                            .setTitle("The currently scheduled Jobs are as follows:")
                            .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot");
                    for (Map.Entry<String, Map<String, String>> trigger : data.entrySet()) {
                        for (Map.Entry<String, String> detail : trigger.getValue().entrySet()) {
                            tmp.addField(detail.getKey(), detail.getValue(), true);
                        }
                        tmp.addField("", "", true);
                    }
                    event.replyEmbeds(tmp.build())
                            .setEphemeral(true)
                            .queue();
                }
            } else if (event.getComponentId().equals("accSkip")) {
                if (handler.skipTrigger()) {
                    event.replyEmbeds(EmbedResponse.success("The next Trigger will skip it's fire time."))
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("There are no Triggers to skip."))
                            .setEphemeral(true)
                            .queue();
                }
            } else if (event.getComponentId().equals("accGet")) {
                Map<String, String> data = handler.getTriggerResponses();
                if (data.isEmpty()) {
                    event.replyEmbeds(EmbedResponse.success("Nobody has responded to this accountability."))
                            .setEphemeral(true)
                            .queue();
                } else {
                    EmbedBuilder tmp = new EmbedBuilder().setColor(Color.GREEN)
                            .setTitle("The following individuals has reported for accountability:")
                            .setAuthor("333-bot", "https://github.com/brunochristensen/333-bot");
                    SortedSet<String> keys = new TreeSet<>(data.keySet());
                    for (String key : keys) {
                        tmp.addField(key, data.get(key), false);
                    }
                    event.replyEmbeds(tmp.build())
                            .setEphemeral(true)
                            .queue();
                }
            }
        } catch (SchedulerException e) {
            event.replyEmbeds(EmbedResponse.error("Failed to fetch Keys from scheduler."))
                    .setEphemeral(true)
                    .queue();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("newAccModal")) {
            String triggerName = Objects.requireNonNull(event.getValue("triggerName")).getAsString();
            String cronSch = Objects.requireNonNull(event.getValue("cronSch")).getAsString();
            String uniform = Objects.requireNonNull(event.getValue("uniform")).getAsString();
            String time = Objects.requireNonNull(event.getValue("time")).getAsString();
            String location = Objects.requireNonNull(event.getValue("location")).getAsString();
            try {
                if (handler.addTrigger(triggerName, cronSch, uniform, time, location)) {
                    event.replyEmbeds(EmbedResponse.success("New accountability Trigger scheduled."))
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("Invalid Cron expression."))
                            .setEphemeral(true)
                            .queue();
                }
            } catch (SchedulerException e) {
                event.replyEmbeds(EmbedResponse.error("Scheduler failed to add Trigger."))
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        } else if (event.getModalId().equals("delAccModal")) {
            String triggerName = Objects.requireNonNull(event.getValue("triggerName")).getAsString();
            try {
                if (handler.delTrigger(triggerName)) {
                    event.replyEmbeds(EmbedResponse.success("Accountability Trigger deleted."))
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("Invalid Trigger name."))
                            .setEphemeral(true)
                            .queue();
                }
            } catch (SchedulerException e) {
                event.replyEmbeds(EmbedResponse.error("Scheduler failed to remove Trigger."))
                        .setEphemeral(true)
                        .queue();
                throw new RuntimeException(e);
            }
        }
    }
}