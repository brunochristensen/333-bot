package net.brunochristensen._333bot.features;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class FeatureManager extends ListenerAdapter {

    private final ListenerAdapter[] listeners;

    protected FeatureManager(ListenerAdapter... listeners) {
        this.listeners = listeners;
    }

    @Override
    public abstract void onReady(@NotNull ReadyEvent event);
}
