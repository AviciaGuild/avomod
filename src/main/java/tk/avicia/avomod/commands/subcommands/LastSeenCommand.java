package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.PlayerTabCompletionCommand;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.PlayerData;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class LastSeenCommand extends PlayerTabCompletionCommand {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        String outputMessage;
        if (params.length >= 1) {
            String username = params[0];
            try {
                PlayerData playerData = new PlayerData(username);
                String playerLastJoin = playerData.getLastJoin();
                Instant instant = Instant.parse(playerLastJoin);
                if (playerData.isPlayerOnline()) {
                    outputMessage = TextFormatting.AQUA + playerData.getPlayerName() + TextFormatting.GRAY + " is online on " + TextFormatting.AQUA + playerData.getWorld();
                } else {
                    outputMessage = TextFormatting.AQUA + playerData.getPlayerName() + TextFormatting.GRAY + " was last seen " + TextFormatting.AQUA
                            + Utils.getReadableTime((int) ((System.currentTimeMillis() - instant.toEpochMilli()) / 60000)) + TextFormatting.GRAY + " ago";
                }
            } catch (NoSuchFieldException e) {
                outputMessage = TextFormatting.DARK_RED + username + TextFormatting.RED + " is not a Wynncraft player.";
            }
        } else {
            outputMessage = TextFormatting.RED + "Correct usage: /am lastseen <username>";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public @Nonnull
    String getName() {
        return "lastseen";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "lastseen <username>";
    }

    @Override
    public String getDescription() {
        return "Shows how long ago the person last logged on";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("ls");
    }
}