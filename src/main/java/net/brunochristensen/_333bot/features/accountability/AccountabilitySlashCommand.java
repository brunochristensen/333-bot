package net.brunochristensen._333bot.features.accountability;

import net.brunochristensen._333bot.utils.EmbedResponse;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class AccountabilitySlashCommand extends ListenerAdapter {

    private final Logger logger;
    private final MessageEmbed controlMenuEmbed = EmbedResponse.message(
            "Accountability Job Master Menu",
            "Here you can view and configure when the bot will send out requests for accountability, Triggers"
                    + " can be created, deleted, and running Trigger information can be viewed. The Skip button"
                    + " can be used to skip the next Trigger that will be fired.").build();

    public AccountabilitySlashCommand() {
        logger = LoggerFactory.getLogger(AccountabilitySlashCommand.class);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("account")) {
            Button addButton = Button.success(AccountabilityComponentID.ADDBUTTON.id, "Add New Accountability Times");
            Button delButton = Button.danger(AccountabilityComponentID.DELBUTTON.id, "Delete Accountability Times");
            Button viewButton = Button.secondary(AccountabilityComponentID.VIEWBUTTON.id, "View Accountability Times");
            Button skipButton = Button.primary(AccountabilityComponentID.SKIPBUTTON.id, "Skip Next Accountability Time");
            Button getButton = Button.primary(AccountabilityComponentID.GETBUTTON.id, "Get Accountability Responses");
            List<Role> userRoles = Objects.requireNonNull(event.getMember()).getRoles();
            JDA jda = event.getJDA();
            ReplyCallbackAction embed = event.replyEmbeds(controlMenuEmbed);
            if (userRoles.contains(jda.getRoleById(Env.get("ADMIN_ROLE_ID")))) {
                embed.addActionRow(addButton).addActionRow(delButton).addActionRow(viewButton)
                        .addActionRow(skipButton).addActionRow(getButton);
            } else if (userRoles.contains(jda.getRoleById(Env.get("AL_ROLE_ID")))) {
                embed.addActionRow(addButton.asDisabled()).addActionRow(delButton.asDisabled())
                        .addActionRow(viewButton).addActionRow(skipButton.asDisabled()).addActionRow(getButton);
            } else {
                embed.addActionRow(addButton.asDisabled()).addActionRow(delButton.asDisabled())
                        .addActionRow(viewButton.asDisabled()).addActionRow(skipButton.asDisabled())
                        .addActionRow(getButton.asDisabled());
            }
            embed.setEphemeral(true).queue();
        }
    }
}
