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

public class FindCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        if (params.length >= 1) {
            String username = params[0];
            PlayerData playerData = new PlayerData(username);
            String playerWorld = playerData.getWorld();

            if (playerWorld != "") {
                outputMessage = TextFormatting.DARK_RED + playerData.getPlayerName() + TextFormatting.RED + " is on " + playerWorld;
            } else {
                outputMessage = TextFormatting.DARK_RED + playerData.getPlayerName() + TextFormatting.RED + " is not online";
            }
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "find";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "find <username>";
    }

    @Override
    public String getDescription() {
        return "Finds the server a player is on, even works in the lobby";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("f");
    }
}
