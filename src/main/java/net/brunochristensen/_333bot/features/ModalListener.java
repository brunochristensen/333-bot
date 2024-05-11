package net.brunochristensen._333bot.features;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class ModalListener extends ListenerAdapter {
    @Override
    public abstract void onModalInteraction(@NotNull ModalInteractionEvent event);
}
