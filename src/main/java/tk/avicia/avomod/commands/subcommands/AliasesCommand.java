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

public class AliasesCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = TextFormatting.BLUE + "Aliases for AVOMod:";

        for (Map.Entry<String, Command> command : Avomod.commands.entrySet()) {
            String aliases = String.join(", ", command.getValue().getAliases());
            outputMessage += "\n" + TextFormatting.GOLD + command.getKey() + ": " + TextFormatting.GREEN + aliases;
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "aliases";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "aliases <command>";
    }

    @Override
    public String getDescription() {
        return "Shows all aliases for AVOMod";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("al");
    }
}
