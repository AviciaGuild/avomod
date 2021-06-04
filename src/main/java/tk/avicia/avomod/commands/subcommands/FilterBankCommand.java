package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import java.util.Arrays;
import java.util.List;

public class FilterBankCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params != null && params.length > 0) {
            if (params[0].equals("enable")) {
                Avomod.toggleFilter(true);
            } else if (params[0].equals("disable")) {
                Avomod.toggleFilter(false);
            }
        } else {
            Avomod.toggleFilter(!Avomod.filterChat());
        }

        String outputMessage;
        if (Avomod.filterChat()) {
            outputMessage = "Bank messages filter is now enabled.";
        } else {
            outputMessage = "Bank messages filter is now disabled.";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "filterbank";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "filterbank <enable:disable>";
    }

    @Override
    public String getDescription() {
        return "Toggles the filter for bank messages";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("fb");
    }
}
