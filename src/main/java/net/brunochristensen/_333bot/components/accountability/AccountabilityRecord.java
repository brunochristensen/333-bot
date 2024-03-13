package net.brunochristensen._333bot.components.accountability;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Hashtable;

public class AccountabilityRecord extends ListenerAdapter {

    private static AccountabilityRecord instance;
    private Hashtable<String, String> accountabilityReport;

    public static AccountabilityRecord getInstance() {
        if (instance == null) {
            instance = new AccountabilityRecord();
            instance.resetAccountabilityReport();
        }
        return instance;
    }

    public Hashtable<String, String> getAccountabilityReport() {
        return accountabilityReport;
    }

    public void resetAccountabilityReport() {
        accountabilityReport = new Hashtable<>();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId()
                .equals("choose-acc")) {
            accountabilityReport.put(event.getUser()
                    .getName(), event.getValues()
                    .get(0));
        }
    }
}
