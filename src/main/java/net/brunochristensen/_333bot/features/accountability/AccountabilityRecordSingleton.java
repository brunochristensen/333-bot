package net.brunochristensen._333bot.features.accountability;

import net.brunochristensen._333bot.utils.EmbedResponse;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountabilityRecordSingleton extends ListenerAdapter {

    private static AccountabilityRecordSingleton instance;
    private HashMap<String, String> accountabilityReport;

    public static AccountabilityRecordSingleton getInstance() {
        if (instance == null) {
            instance = new AccountabilityRecordSingleton();
            instance.resetAccountabilityReport();
        }
        return instance;
    }

    public void resetAccountabilityReport() {
        accountabilityReport = new HashMap<>();
    }

    public Map<String, String> getAccountabilityReport() {
        return accountabilityReport;
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (event.getComponentId()
                .equals("choose-acc")) {
            accountabilityReport.put(Objects.requireNonNull(event.getMember())
                    .getEffectiveName(), event.getValues()
                    .get(0));
            event.replyEmbeds(EmbedResponse.success("Submission has been processed.")
                            .build())
                    .setEphemeral(true)
                    .queue();
        }
    }
}
