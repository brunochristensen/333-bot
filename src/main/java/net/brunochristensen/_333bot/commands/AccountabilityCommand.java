package net.brunochristensen._333bot.commands;

import net.brunochristensen._333bot.components.accountability.AccountabilityHandler;
import net.dv8tion.jda.api.EmbedBuilder;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountabilityCommand extends ListenerAdapter {

    private AccountabilityHandler handler;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.handler = new AccountabilityHandler(event.getJDA());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("account")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Accountability Job Master Menu");
            eb.setColor(Color.yellow);
            eb.setDescription("Here you can view and configure when the bot will send out " +
                    "requests for accountability, Jobs can be created, deleted, modified, " +
                    "or paused here.");

            List<Button> miscButtons = new ArrayList<>();
            miscButtons.add(Button.primary("accView", "View Accountability Times"));
            miscButtons.add(Button.primary("accSkip", "Skip Next Accountability Time"));

            List<Button> crudButtons = new ArrayList<>();
            crudButtons.add(Button.primary("accAdd", "Add New Accountability Times"));
            crudButtons.add(Button.primary("accDel", "Delete Accountability Times"));
            crudButtons.add(Button.primary("accMod", "Modify Accountability Times"));

            event.replyEmbeds(eb.build()).addActionRow(miscButtons)
                    .addActionRow(crudButtons).setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().equals("accView")) {
            event.reply(handler.viewTriggers()).setEphemeral(true).queue();
        } else if (event.getComponentId().equals("accSkip")) {
            event.reply("Not Implemented").setEphemeral(true).queue();
        } else if (event.getComponentId().equals("accAdd")) {
            TextInput triggerName = TextInput.create("triggerName", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name for schedule")
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
            event.replyModal(modal).queue();
        } else if (event.getComponentId().equals("accDel")) {
            TextInput triggerName = TextInput.create("triggerName", "TriggerName", TextInputStyle.SHORT)
                    .setPlaceholder("Unique name of schedule")
                    .setMinLength(1)
                    .setMaxLength(100)
                    .build();
            Modal modal = Modal.create("delAccModal", "Delete Accountability Schedule")
                    .addComponents(ActionRow.of(triggerName))
                    .build();
            event.replyModal(modal).queue();
        } else if (event.getComponentId().equals("accMod")) {
            event.reply("Not Implemented").setEphemeral(true).queue();
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
                handler.newTrigger(triggerName, cronSch, uniform, time, location);
                event.reply("New accountability job scheduled.").setEphemeral(true).queue();
            } catch (NullPointerException e) {
                event.reply("Check for empty or invalid fields.").setEphemeral(true).queue();
            }
        } else if (event.getModalId().equals("delAccModal")) {
            try {
                String triggerName = Objects.requireNonNull(event.getValue("triggerName")).getAsString();
                handler.delTrigger(triggerName);
                event.reply("Accountability job deleted.").setEphemeral(true).queue();
            } catch (NullPointerException e) {
                event.reply("Invalid name of scheduled job").setEphemeral(true).queue();
            }
        }
    }
}