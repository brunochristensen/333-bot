package net.brunochristensen._333bot.events;

import net.brunochristensen._333bot.utils.EmbedResponse;
import net.brunochristensen._333bot.utils.Env;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.Random;

public class MemberJoinListener extends ListenerAdapter {
    private final Random random = new Random();

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        String name = event.getMember()
                .getEffectiveName();
        try (RandomAccessFile fileReader = new RandomAccessFile(new File("src/main/resources/Welcome.txt"), "r")) {
            fileReader.seek(random.nextLong(fileReader.length()));
            fileReader.readLine();
            String message = fileReader.readLine();
            Objects.requireNonNull(event.getJDA()
                            .getTextChannelById(Env.get("WELCOME_CHANNEL_ID")))
                    .sendMessageEmbeds(EmbedResponse.success(String.format(message, name))
                            .build())
                    .addFiles(FileUpload.fromData(new File("src/main/resources/pictures/Duc.png")))
                    .queue();
        } catch (IOException e) {
            Objects.requireNonNull(event.getJDA()
                            .getTextChannelById(Env.get("WELCOME_CHANNEL_ID")))
                    .sendMessageEmbeds(EmbedResponse.success(String.format("Someone's garbage programming broke, " +
                                    "now you have to deal with this welcome message %s. Enjoy.", name))
                            .build())
                    .queue();
        }
    }
}