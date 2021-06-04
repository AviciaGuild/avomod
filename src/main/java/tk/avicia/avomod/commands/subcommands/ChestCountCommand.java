package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.PlayerData;

import java.util.Arrays;
import java.util.List;

public class ChestCountCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        PlayerData player;
        if (params != null && params.length >= 1) {
            player = new PlayerData(params[0]);
        } else {
            player = new PlayerData(Avomod.getMC().player.getName());
        }

        String outputMessage = TextFormatting.AQUA + player.getPlayerName() + TextFormatting.GRAY + " has found " +
                TextFormatting.AQUA + player.getChestCount() + TextFormatting.GRAY + " chests!";
        System.out.println(outputMessage);
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
