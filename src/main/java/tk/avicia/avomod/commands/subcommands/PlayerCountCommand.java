package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonArray;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.OnlinePlayers;

import java.util.Arrays;
import java.util.List;

public class PlayerCountCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        if (params.length >= 1) {
            String world = params[0];
            if (world.matches("^\\d+")) {
                world = "WC" + world;
            }
            JsonArray onlineWorldPlayers = new OnlinePlayers().getWorldPlayers(world.toUpperCase());

            if (onlineWorldPlayers != null) {
                outputMessage = TextFormatting.GOLD + world.toUpperCase() + " : " + TextFormatting.DARK_AQUA + " has " + onlineWorldPlayers.size() + " players";
            } else {
                outputMessage = TextFormatting.DARK_RED + world.toUpperCase() + TextFormatting.RED + " is not up";
            }
        } else {
            outputMessage = TextFormatting.RED + "Correct usage: /am pc <world>";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "playercount";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "playercount <world>";
    }

    @Override
    public String getDescription() {
        return "Shows the playercount of a specific world";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pc");
    }
}