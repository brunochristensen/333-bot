package net.brunochristensen._333bot.features.reminders;

import net.brunochristensen._333bot.features.SingleJobHandler;
import net.brunochristensen._333bot.utils.EmbedResponse;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ReminderCommand extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ReminderCommand.class);
    private final MessageEmbed controlMenuEmbed = EmbedResponse.message("Todo", "todo")
            .build();
    private SingleJobHandler handler;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            handler = new SingleJobHandler(event.getJDA(), ReminderJob.class, "reminderGroup",
                    "reminderJob");
        } catch (SchedulerException e) {
            logger.error(e.toString());
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName()
                .equals("remind")) {
            Button addButton = Button.success("remAdd", "Add New Reminder");
            Button delButton = Button.danger("remDel", "Delete Reminder");
            Button viewButton = Button.secondary("remView", "View  Reminders");
            List<Role> userRoles = Objects.requireNonNull(event.getMember())
                    .getRoles();
            JDA jda = event.getJDA();
            ReplyCallbackAction embed = event.replyEmbeds(controlMenuEmbed);
            if (userRoles.contains(jda.getRoleById(Env.get("ADMIN_ROLE_ID"))) || userRoles.contains(
                    jda.getRoleById(Env.get("AL_ROLE_ID")))) {
                embed.addActionRow(addButton)
                        .addActionRow(delButton)
                        .addActionRow(viewButton);
            } else {
                embed.addActionRow(addButton.asDisabled())
                        .addActionRow(delButton.asDisabled())
                        .addActionRow(viewButton.asDisabled());
            }
            embed.setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId()
                .equals("remAdd")) {

        } else if (event.getComponentId()
                .equals("remDel")) {

        } else if (event.getComponentId()
                .equals("remView")) {

        }
    }
}
