package net.brunochristensen._333bot.components.accountability;

import net.brunochristensen._333bot.components.SingleJobHandler;
import net.brunochristensen._333bot.utils.EmbedResponse;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AccountabilityCommand extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AccountabilityCommand.class);
    private final MessageEmbed controlMenuEmbed = EmbedResponse.message("Accountability Job Master Menu", "Here you can view and configure " + "when the bot will send out requests for accountability, Triggers can be created, " + "deleted, and running Trigger information can be viewed. The Skip button can be used to " + "skip the next Trigger that will be fired.")
            .build();
    private final Modal additionModal = Modal.create("newAccModal", "New Accountability Schedule")
            .addComponents(ActionRow.of(TextInput.create("triggerNameAddTextInput", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name for Trigger")
                    .setRequiredRange(1, 50)
                    .build()), ActionRow.of(TextInput.create("cronSchTextInput", "CronSch", TextInputStyle.SHORT)
                    .setPlaceholder("CronJob formatted schedule")
                    .setRequiredRange(1, 50)
                    .build()), ActionRow.of(TextInput.create("uniformTextInput", "Uniform", TextInputStyle.SHORT)
                    .setPlaceholder("Uniform of the Day")
                    .setRequiredRange(1, 50)
                    .build()), ActionRow.of(TextInput.create("timeTextInput", "Time", TextInputStyle.SHORT)
                    .setPlaceholder("Time (e.g. 0550)")
                    .setRequiredRange(4, 4)
                    .build()), ActionRow.of(TextInput.create("locationTextInput", "Location", TextInputStyle.SHORT)
                    .setPlaceholder("Where accountability is taking place")
                    .setRequiredRange(1, 50)
                    .build()))
            .build();
    private final Modal deletionModal = Modal.create("delAccModal", "Delete Accountability Schedule")
            .addComponents(ActionRow.of(TextInput.create("triggerNameDelTextInput", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name of Trigger")
                    .setRequiredRange(1, 100)
                    .build()))
            .build();
    private SingleJobHandler handler;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Executing onReady()");
        event.getJDA()
                .addEventListener(AccountabilityRecordSingleton.getInstance());
        try {
            handler = new SingleJobHandler(event.getJDA(), AccountabilityJob.class, "accountabilityGroup", "accountabilityJob");
        } catch (SchedulerException e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName()
                .equals("account")) {
            logger.info("Executing onSlashCommandInteraction():\"account\"");
            Button addButton = Button.success("accAdd", "Add New Accountability Times");
            Button delButton = Button.danger("accDel", "Delete Accountability Times");
            Button viewButton = Button.secondary("accView", "View Accountability Times");
            Button skipButton = Button.primary("accSkip", "Skip Next Accountability Time");
            Button getButton = Button.primary("accGet", "Get Accountability Responses");
            List<Role> userRoles = Objects.requireNonNull(event.getMember())
                    .getRoles();
            if (userRoles.contains(event.getJDA()
                    .getRoleById(Env.get("ADMIN_ROLE_ID")))) {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(addButton)
                        .addActionRow(delButton)
                        .addActionRow(viewButton)
                        .addActionRow(skipButton)
                        .addActionRow(getButton)
                        .setEphemeral(true)
                        .queue();
            } else if (userRoles.contains(event.getJDA()
                    .getRoleById(Env.get("AL_ROLE_ID")))) {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(addButton.asDisabled())
                        .addActionRow(delButton.asDisabled())
                        .addActionRow(viewButton)
                        .addActionRow(skipButton.asDisabled())
                        .addActionRow(getButton)
                        .setEphemeral(true)
                        .queue();
            } else {
                event.replyEmbeds(controlMenuEmbed)
                        .addActionRow(addButton.asDisabled())
                        .addActionRow(delButton.asDisabled())
                        .addActionRow(viewButton.asDisabled())
                        .addActionRow(skipButton.asDisabled())
                        .addActionRow(getButton.asDisabled())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        switch (componentId) {
            case "accAdd":
                logger.info("Executing onButtonInteraction():\"accAdd\"");
                event.replyModal(additionModal)
                        .queue();
                break;
            case "accDel":
                logger.info("Executing onButtonInteraction():\"accDel\"");
                event.replyModal(deletionModal)
                        .queue();
                break;
            case "accView":
                logger.info("Executing onButtonInteraction():\"accView\"");
                try {
                    Map<String, Map<String, String>> data = handler.getTriggerData();
                    if (data.isEmpty()) {
                        event.replyEmbeds(EmbedResponse.success("There are no Jobs currently scheduled.")
                                        .build())
                                .setEphemeral(true)
                                .queue();
                    } else {
                        event.replyEmbeds(getTriggerDataEmbed(data))
                                .setEphemeral(true)
                                .queue();
                    }
                } catch (SchedulerException e) {
                    logger.error(e.toString());
                }
                break;
            case "accSkip":
                logger.info("Executing onButtonInteraction():\"accSkip\"");
                try {
                    if (handler.skipNextTrigger()) {
                        event.replyEmbeds(EmbedResponse.success("The next Trigger will skip it's fire time.")
                                        .build())
                                .setEphemeral(true)
                                .queue();
                    } else {
                        event.replyEmbeds(EmbedResponse.failure("There are no Triggers to skip.")
                                        .build())
                                .setEphemeral(true)
                                .queue();
                    }
                } catch (SchedulerException e) {
                    logger.error(e.toString());
                }
                break;
            case "accGet":
                logger.info("Executing onButtonInteraction():\"accGet\"");
                Map<String, String> data = handler.getAccountabilityResponses();
                if (data.isEmpty()) {
                    event.replyEmbeds(EmbedResponse.success("Nobody has responded to this accountability.")
                                    .build())
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(getAccountabilityResponseEmbed(data))
                            .setEphemeral(true)
                            .queue();
                }
                break;
            default:
                break;
        }
    }

    @NotNull
    private static MessageEmbed getAccountabilityResponseEmbed(@NotNull Map<String, String> data) {
        EmbedBuilder tmp = EmbedResponse.success("The following individuals has reported for accountability:");
        SortedSet<String> keys = new TreeSet<>(data.keySet());
        for (String key : keys) {
            tmp.addField(key, data.get(key), false);
        }
        return tmp.build();
    }

    @NotNull
    private static MessageEmbed getTriggerDataEmbed(@NotNull Map<String, Map<String, String>> data) {
        EmbedBuilder tmp = EmbedResponse.success("The currently scheduled Jobs are as follows:");
        for (Map.Entry<String, Map<String, String>> trigger : data.entrySet()) {
            tmp.addField("Name", trigger.getKey(), false);
            for (Map.Entry<String, String> detail : trigger.getValue()
                    .entrySet()) {
                tmp.addField(detail.getKey(), detail.getValue(), true);
            }
        }
        return tmp.build();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId()
                .equals("newAccModal")) {
            logger.info("Executing onModalInteraction():\"newAccModal\"");
            String triggerName = Objects.requireNonNull(event.getValue("triggerNameAddTextInput"))
                    .getAsString();
            String cronSch = Objects.requireNonNull(event.getValue("cronSchTextInput"))
                    .getAsString();
            String uniform = Objects.requireNonNull(event.getValue("uniformTextInput"))
                    .getAsString();
            String time = Objects.requireNonNull(event.getValue("timeTextInput"))
                    .getAsString();
            String location = Objects.requireNonNull(event.getValue("locationTextInput"))
                    .getAsString();
            try {
                if (handler.buildTrigger(triggerName, cronSch)
                        .addToDataMap("uniform", uniform)
                        .addToDataMap("time", time)
                        .addToDataMap("location", location)
                        .schedule()) {
                    event.replyEmbeds(EmbedResponse.success("New accountability Trigger scheduled.")
                                    .build())
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("Invalid Cron expression.")
                                    .build())
                            .setEphemeral(true)
                            .queue();
                }
            } catch (SchedulerException e) {
                logger.error(e.toString());
            }
        } else if (event.getModalId()
                .equals("delAccModal")) {
            logger.info("Executing onModalInteraction():\"delAccModal\"");
            String triggerName = Objects.requireNonNull(event.getValue("triggerNameDelTextInput"))
                    .getAsString();
            try {
                if (handler.delTrigger(triggerName)) {
                    event.replyEmbeds(EmbedResponse.success("Accountability Trigger deleted.")
                                    .build())
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("Invalid Trigger name.")
                                    .build())
                            .setEphemeral(true)
                            .queue();
                }
            } catch (SchedulerException e) {
                logger.error(e.toString());
            }
        }
    }
}