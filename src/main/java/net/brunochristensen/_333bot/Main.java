package net.brunochristensen._333bot;

import net.brunochristensen._333bot.features.accountability.AccountabilityFeatureManager;

public class Main {

    public static void main(String[] args) {
        Bot bot = new Bot();
        bot.addFeatureManager(new AccountabilityFeatureManager());
    }

}
