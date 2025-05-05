package com.fabcord;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class FabcordCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, Verifier verifier) {
        dispatcher.register(
            literal("discord")
                .then(literal("verify")
                    .then(argument("discordUsername", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            String tag = StringArgumentType.getString(ctx, "discordUsername");
                            verifier.startVerification(player, tag);
                            ctx.getSource().sendFeedback(() -> Text.literal("Verification code sent to " + tag), false);
                            return 1;
                        })
                    )
                )
        );
    }
}
