package net.brunochristensen._333bot.features;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class SlashCommandListener extends ListenerAdapter {

    @Override
    public abstract void onReady(@NotNull ReadyEvent event);

    @Override
    public abstract void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event);
}

