package net.brunochristensen._333bot.features;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class MenuListener extends ListenerAdapter {
    @Override
    public abstract void onButtonInteraction(@NotNull ButtonInteractionEvent event);
}
