package tk.avicia.avomod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import tk.avicia.avomod.webapi.PlayerData;

public class ChestCountCommand extends CommandBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        PlayerData player;
        if (params != null && params.length > 0) {
            player = new PlayerData(params[0]);
        } else {
            player = new PlayerData("newracket");
        }

        String outputMessage = player.getPlayerName() + " has found " + player.getChestCount() + " chests!";
        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "chestcount";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/chestcount username";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
