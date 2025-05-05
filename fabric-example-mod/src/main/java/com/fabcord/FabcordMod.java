package com.fabcord;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class FabcordMod implements ModInitializer {

    public static Verifier verifier;

    @Override
    public void onInitialize() {
        // Start Discord bot
        BotClient.init();

        // Init verifier
        verifier = new Verifier();

        // Register command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FabcordCommand.register(dispatcher, verifier);
        });
    }
}
