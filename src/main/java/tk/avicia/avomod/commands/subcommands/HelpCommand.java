package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params.length == 0) {
            String outputMessage = TextFormatting.BLUE + "Help for AVOMod:";

            for (Map.Entry<String, Command> command : Avomod.commands.entrySet()) {
                outputMessage += "\n" + TextFormatting.GOLD + "/avomod " + command.getKey() + ": " + TextFormatting.GREEN + command.getValue().getDescription();
            }

            TextComponentString textComponent = new TextComponentString(outputMessage);
            sender.sendMessage(textComponent);
        } else {
            if (Avomod.commands.containsKey(params[0])) {
                sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Usage: " + Avomod.commands.get(params[0]).getUsage(sender)));
            }else{
                sender.sendMessage(new TextComponentString(TextFormatting.DARK_RED + params[0] + TextFormatting.RED + " is not an AvoMod command"));
            }
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "help <command>";
    }

    @Override
    public String getDescription() {
        return "Shows help for commands with AVOMod";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("h");
    }
}
