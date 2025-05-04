package com.example.discordverifier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class VerifyCommand {

    public static void register(CommandDispatcher<ServerCommandSource> d,
                                VerificationManager manager) {

        d.register(CommandManager.literal("discord")
            .then(CommandManager.literal("verify")
            .then(CommandManager.argument("discord", StringArgumentType.string())
            .executes(ctx -> {
                ServerPlayerEntity player = ctx.getSource().getPlayer();
                String handle = StringArgumentType.getString(ctx, "discord");

                manager.startVerification(player, handle);
ctx.getSource().sendFeedback(
        () -> Text.literal("Check your Discord DMs for the verification code."),
        false
);
                return 1;
            }))));
    }
}

