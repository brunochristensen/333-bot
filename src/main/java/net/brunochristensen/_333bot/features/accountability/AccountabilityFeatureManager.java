package net.brunochristensen._333bot.features.accountability;

import net.brunochristensen._333bot.FeatureManager;
import net.brunochristensen._333bot.features.SingleJobHandler;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AccountabilityFeatureManager implements FeatureManager {

    private final Logger logger;

    public AccountabilityFeatureManager() {
        logger = LoggerFactory.getLogger(AccountabilityFeatureManager.class);
    }

    @Override
    public void registerEventListeners(JDA api) {
        Objects.requireNonNull(api.getGuildById(Env.get("GUILD_ID")))
                .updateCommands()
                .addCommands(Commands.slash("account", "Brings up the accountability config menu."))
                .queue();
        try {
            SingleJobHandler handler = new SingleJobHandler(api, AccountabilityJob.class,
                    "accountabilityGroup", "accountabilityJob");
            api.addEventListener(AccountabilityRecordSingleton.getInstance(),
                    new AccountabilityMenuListener(handler),
                    new AccountabilityModalListener(handler),
                    new AccountabilitySlashCommand());
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        }
    }

}
