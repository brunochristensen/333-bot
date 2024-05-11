package net.brunochristensen._333bot.features.accountability;

import net.brunochristensen._333bot.features.MenuListener;
import net.brunochristensen._333bot.features.SingleJobHandler;
import net.brunochristensen._333bot.utils.EmbedResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class AccountabilityMenuListener extends MenuListener {
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
    private final Logger logger;
    private final SingleJobHandler handler;

    public AccountabilityMenuListener(SingleJobHandler handler) {
        super();
        this.handler = handler;
        logger = LoggerFactory.getLogger(AccountabilityMenuListener.class);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();
        switch (componentId) {
            case "accAdd":
                event.replyModal(additionModal)
                        .queue();
                break;
            case "accDel":
                event.replyModal(deletionModal)
                        .queue();
                break;
            case "accView":
                try {
                    Map<String, Map<String, String>> data = handler.getTriggerData();
                    event.replyEmbeds(getTriggerDataEmbed(data))
                            .setEphemeral(true)
                            .queue();
                } catch (SchedulerException e) {
                    logger.error(e.toString());
                }
                break;
            case "accSkip":
                try {
                    ReplyCallbackAction reply;
                    if (handler.skipNextTrigger()) {
                        reply = event.replyEmbeds(EmbedResponse.success("The next Trigger will skip it's fire time.")
                                .build());
                    } else {
                        reply = event.replyEmbeds(EmbedResponse.failure("There are no Triggers to skip.")
                                .build());
                    }
                    reply.setEphemeral(true)
                            .queue();
                } catch (SchedulerException e) {
                    logger.error(e.toString());
                }
                break;
            case "accGet":
                Map<String, String> data = handler.getAccountabilityResponses();
                event.replyEmbeds(getAccountabilityResponseEmbed(data))
                        .setEphemeral(true)
                        .queue();
                break;
            default:
                break;
        }
    }

    @NotNull
    private MessageEmbed getAccountabilityResponseEmbed(@NotNull Map<String, String> data) {
        EmbedBuilder tmp = EmbedResponse.success("The following individuals have reported for accountability:");
        SortedSet<String> keys = new TreeSet<>(data.keySet());
        if (keys.isEmpty()) {
            tmp.addField("Nobody responded :(", "So sad...", false);
            return tmp.build();
        }
        for (String key : keys) {
            tmp.addField(key, data.get(key), false);
        }
        return tmp.build();
    }

    @NotNull
    private MessageEmbed getTriggerDataEmbed(@NotNull Map<String, Map<String, String>> data) {
        EmbedBuilder tmp = EmbedResponse.success("Here are the currently scheduled accountability Jobs:");
        if (data.isEmpty()) {
            tmp.addField("No Jobs are scheduled :(", "So sad...", false);
            return tmp.build();
        }
        for (Map.Entry<String, Map<String, String>> trigger : data.entrySet()) {
            tmp.addField("Name", trigger.getKey(), false);
            for (Map.Entry<String, String> detail : trigger.getValue()
                    .entrySet()) {
                tmp.addField(detail.getKey(), detail.getValue(), true);
            }
        }
        return tmp.build();
    }
}
