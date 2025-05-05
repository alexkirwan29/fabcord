package com.fabcord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class BotClient {
    public static JDA jda;
    public static Guild guild;
    public static Role verifiedRole;

    private static final String CONFIG_DIR = "config";
    private static final String FILE_NAME = "fabcord.properties";

    public static void init() {
        Path configPath = Paths.get(CONFIG_DIR, FILE_NAME);
        Properties props = new Properties();

        try {
            // If config file doesn't exist, create template
            if (Files.notExists(configPath)) {
                createTemplateConfig(configPath);
                throw new IllegalStateException("fabcord.properties was created. Please fill it out and restart the server.");
            }

            // Load filled config
            try (InputStream input = Files.newInputStream(configPath)) {
                props.load(input);
            }

            // Validate required fields
            String token = props.getProperty("token");
            String guildId = props.getProperty("guild");
            String roleId = props.getProperty("verifiedRole");

            if (token == null || guildId == null || roleId == null) {
                throw new IllegalArgumentException("Missing required properties in fabcord.properties");
            }

            jda = JDABuilder.createLight(
                    token,
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS
            )
            .addEventListeners(new DMListener())
            .build()
            .awaitReady();

            guild = jda.getGuildById(guildId);
            verifiedRole = guild.getRoleById(roleId);

            System.out.println("[Fabcord] Bot ready as " + jda.getSelfUser().getAsTag());

        } catch (Exception e) {
            throw new RuntimeException("[Fabcord] Failed to initialize Discord bot", e);
        }
    }

    private static void createTemplateConfig(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
            writer.write("# Fabcord Bot Configuration\n");
            writer.write("# Fill in your bot details and restart the server\n");
            writer.write("token=YOUR_DISCORD_BOT_TOKEN\n");
            writer.write("guild=YOUR_DISCORD_GUILD_ID\n");
            writer.write("verifiedRole=YOUR_VERIFIED_ROLE_ID\n");

            // Future placeholders
            writer.write("# Additional config options can be added below\n");
        }
    }
}
