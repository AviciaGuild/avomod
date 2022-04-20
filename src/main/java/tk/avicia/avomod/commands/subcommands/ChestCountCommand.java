package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.PlayerTabCompletionCommand;
import tk.avicia.avomod.webapi.PlayerData;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ChestCountCommand extends PlayerTabCompletionCommand {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        String outputMessage;
        PlayerData player;
        String username;
        if (params.length >= 1) {
            username = params[0];
        } else {
            username = sender.getName();
        }

        try {
            player = new PlayerData(username);
            outputMessage = TextFormatting.AQUA + player.getPlayerName() + TextFormatting.GRAY + " has found " +
                    TextFormatting.AQUA + player.getChestCount() + TextFormatting.GRAY + " chests!";
        } catch (NoSuchFieldException | NullPointerException e) {
            outputMessage = TextFormatting.DARK_RED + username + TextFormatting.RED + " is not a valid Wynncraft player";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public @Nonnull
    String getName() {
        return "chestcount";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "chestcount <username>";
    }

    @Override
    public String getDescription() {
        return "Shows the amount of chests the player has opened";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("cc");
    }
}
