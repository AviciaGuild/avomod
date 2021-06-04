package tk.avicia.avomod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.webapi.PlayerData;

import static tk.avicia.avomod.AvoMod.mc;

public class ChestCountCommand extends CommandBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        PlayerData player;
        if (params != null && params.length > 0) {
            player = new PlayerData(params[0]);
        } else {
            player = new PlayerData(mc().player.getName());
        }

        String outputMessage = TextFormatting.AQUA +player.getPlayerName() + TextFormatting.GRAY +" has found " +
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
        return "/chestcount username";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
