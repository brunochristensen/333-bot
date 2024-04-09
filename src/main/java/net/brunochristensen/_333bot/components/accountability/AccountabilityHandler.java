package net.brunochristensen._333bot.components.accountability;

import net.brunochristensen._333bot.components.SingleJobHandler;
import net.dv8tion.jda.api.JDA;
import org.quartz.SchedulerException;

public class AccountabilityHandler extends SingleJobHandler {

    public AccountabilityHandler(JDA api) throws SchedulerException {
        super(api, AccountabilityJob.class, "accountabilityGroup", "accountabilityJob");
    }
}
