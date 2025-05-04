// src/main/java/com/example/discordverifier/DMListener.java
package com.example.discordverifier;

import net.dv8tion.jda.api.entities.channel.ChannelType;          // ← new package
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DMListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Only react to DMs from real users
        if (event.getChannelType() != ChannelType.PRIVATE || event.getAuthor().isBot()) {
            return;
        }

        String content = event.getMessage().getContentRaw().trim();
        if (content.matches("\\d{6}")) {
            boolean ok = DiscordVerifierMod.manager.verifyCode(content);
            event.getChannel().sendMessage(ok
                    ? "✅  Verification successful! Enjoy the server."
                    : "❌  Code invalid or expired. Run /discord verify again.").queue();
        }
    }
}
