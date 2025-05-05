package com.fabcord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.minecraft.server.network.ServerPlayerEntity;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Verifier {
    private static final SecureRandom RNG = new SecureRandom();

    private record Pending(UUID mcUuid, long expiresAt, String discordId) {}

    private final Map<String, Pending> pendingByCode = new ConcurrentHashMap<>();
    private final Map<UUID, String> verified = new ConcurrentHashMap<>();

    public void startVerification(ServerPlayerEntity player, String discordTagOrId) {
        String discordId = resolveDiscordUser(discordTagOrId);
        String code = String.format("%06d", RNG.nextInt(1_000_000));
        long expiry = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);

        pendingByCode.put(code, new Pending(player.getUuid(), expiry, discordId));

        User user = BotClient.jda.retrieveUserById(discordId).complete();
        user.openPrivateChannel().flatMap(ch ->
            ch.sendMessage("Reply **only** with this 6-digit code to link your Minecraft account:\n`" + code + "`")
        ).queue();
    }

    public boolean verifyCode(String code) {
        Pending p = pendingByCode.remove(code);
        if (p == null || p.expiresAt() < System.currentTimeMillis()) return false;

        BotClient.guild.addRoleToMember(
            UserSnowflake.fromId(p.discordId()),
            BotClient.verifiedRole
        ).queue();

        verified.put(p.mcUuid(), p.discordId());
        return true;
    }

    private String resolveDiscordUser(String input) {
        if (input.matches("\\d{17,20}")) return input;

        String[] parts = input.split("#", 2);
        if (parts.length != 2) throw new IllegalArgumentException("Use username#1234");

        return BotClient.guild.getMembersByName(parts[0], true).stream()
                .filter(m -> m.getUser().getDiscriminator().equals(parts[1]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();
    }
}
