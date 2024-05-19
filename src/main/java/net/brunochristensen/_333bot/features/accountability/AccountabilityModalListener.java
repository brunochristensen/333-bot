package net.brunochristensen._333bot.features.accountability;

import net.brunochristensen._333bot.features.ModalListener;
import net.brunochristensen._333bot.features.SingleJobHandler;
import net.brunochristensen._333bot.utils.EmbedResponse;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AccountabilityModalListener extends ModalListener {

    private final SingleJobHandler handler;
    private final Logger logger;

    public AccountabilityModalListener(SingleJobHandler handler) {
        super();
        this.handler = handler;
        logger = LoggerFactory.getLogger(AccountabilityModalListener.class);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().equals("newAccModal")) {
            String triggerName = Objects.requireNonNull(event.getValue("triggerNameAddTextInput"))
                    .getAsString();
            String cronSch = Objects.requireNonNull(event.getValue("cronSchTextInput")).getAsString();
            String uniform = Objects.requireNonNull(event.getValue("uniformTextInput")).getAsString();
            String time = Objects.requireNonNull(event.getValue("timeTextInput")).getAsString();
            String location = Objects.requireNonNull(event.getValue("locationTextInput")).getAsString();
            try {
                if (handler.buildTrigger(triggerName, cronSch).addToDataMap("uniform", uniform)
                        .addToDataMap("time", time).addToDataMap("location", location).schedule()) {
                    event.replyEmbeds(EmbedResponse.success("New accountability Trigger scheduled.").build())
                            .setEphemeral(true).queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("Invalid Cron expression.").build())
                            .setEphemeral(true).queue();
                }
            } catch (SchedulerException e) {
                logger.error(e.toString());
            }
        } else if (event.getModalId().equals("delAccModal")) {
            String triggerName = Objects.requireNonNull(event.getValue("triggerNameDelTextInput"))
                    .getAsString();
            try {
                if (handler.delTrigger(triggerName)) {
                    event.replyEmbeds(EmbedResponse.success("Accountability Trigger deleted.").build())
                            .setEphemeral(true).queue();
                } else {
                    event.replyEmbeds(EmbedResponse.failure("Invalid Trigger name.").build())
                            .setEphemeral(true).queue();
                }
            } catch (SchedulerException e) {
                logger.error(e.toString());
            }
        }
    }
}
