package tk.avicia.avomod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import tk.avicia.avomod.AvoMod;

public class FilterCommand extends CommandBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params != null && params.length > 0) {
            if (params[0].equals("enable")) {
                AvoMod.toggleFilter(true);
            } else if (params[0].equals("disable")) {
                AvoMod.toggleFilter(false);
            }
        } else {
            AvoMod.toggleFilter(!AvoMod.filterChat());
        }

        String outputMessage;
        if (AvoMod.filterChat()) {
            outputMessage = "Bank messages filter is now enabled.";
        } else {
            outputMessage = "Bank messages filter is now disabled.";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
        System.out.println(outputMessage);
    }

    @Override
    public String getName() {
        return "filterbank";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/filterbank <enable:disable>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
