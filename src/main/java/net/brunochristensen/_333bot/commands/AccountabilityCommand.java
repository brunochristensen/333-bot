package net.brunochristensen._333bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AccountabilityCommand extends ListenerAdapter {

    public AccountabilityCommand() {

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
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
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("accView")) {

        } else if (event.getComponentId().equals("accSkip")) {

        } else if (event.getComponentId().equals("accAdd")) {

        } else if (event.getComponentId().equals("accDel")) {

        } else if (event.getComponentId().equals("accMod")) {

        }
    }
}
