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

public class MemberJoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        String name = event.getMember().getEffectiveName();
        try {
            RandomAccessFile fileReader = new RandomAccessFile(new File("src/main/resources/Welcome.txt"), "r");
            fileReader.seek((long) (Math.random() * fileReader.length()));
            fileReader.readLine();
            Objects.requireNonNull(event.getJDA().getTextChannelById(Env.get("WELCOME_CHANNEL_ID")))
                    .sendMessageEmbeds(EmbedResponse.success(String.format(fileReader.readLine(), name)))
                    .addFiles(FileUpload.fromData(new File("src/main/resources/pictures/Duc.png")))
                    .queue();
            fileReader.close();
        } catch (IOException e) {
            Objects.requireNonNull(event.getJDA().getTextChannelById(Env.get("WELCOME_CHANNEL_ID")))
                    .sendMessageEmbeds(EmbedResponse.success(String.format("Someone's dog shit programming broke, " +
                            "now you have to deal with this welcome message %s. Enjoy.", name)))
                    .queue();
        }
    }
}