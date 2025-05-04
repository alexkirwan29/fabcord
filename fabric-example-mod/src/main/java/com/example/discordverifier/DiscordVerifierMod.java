package com.example.discordverifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.InputStream;
import java.util.Properties;

public class DiscordVerifierMod implements ModInitializer {

    public static JDA    jda;
    public static Guild  guild;
    public static Role   verifiedRole;
    public static VerificationManager manager;

    @Override
    public void onInitialize() {
        try (InputStream in = DiscordVerifierMod.class.getResourceAsStream("/discord.properties")) {
            Properties p = new Properties();
            p.load(in);

            jda = JDABuilder.createLight(
                        p.getProperty("token"),
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS
                  )
                  .addEventListeners(new DMListener())
                  .build()
                  .awaitReady();

            guild        = jda.getGuildById(p.getProperty("guild"));
            verifiedRole = guild.getRoleById(p.getProperty("verifiedRole"));
            manager      = new VerificationManager();

            System.out.println("[DiscordVerifier] Bot ready as " + jda.getSelfUser().getAsTag());
        } catch (Exception e) {
            throw new RuntimeException("Discord bot failed to start", e);
        }

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        VerifyCommand.register(dispatcher, manager)
        );
    }
}