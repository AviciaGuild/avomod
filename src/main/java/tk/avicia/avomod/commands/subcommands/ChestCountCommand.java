package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.PlayerData;

import java.util.Arrays;
import java.util.List;

public class ChestCountCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        PlayerData player;
        String username = "";
        if (params != null && params.length >= 1) {
            username = params[0];
        } else {
            username = sender.getName();
        }
        try {
            player = new PlayerData(username);
            outputMessage = TextFormatting.AQUA + player.getPlayerName() + TextFormatting.GRAY + " has found " +
                    TextFormatting.AQUA + player.getChestCount() + TextFormatting.GRAY + " chests!";
        } catch (NoSuchFieldException e) {
            outputMessage = TextFormatting.DARK_RED + username + TextFormatting.RED + " is not a valid Wynncraft player";
        }


        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "chestcount";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "chestcount <username>";
    }

    @Override
    public String getDescription() {
        return "Shows the amount of chests the player has opened";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("cc");
    }
}
