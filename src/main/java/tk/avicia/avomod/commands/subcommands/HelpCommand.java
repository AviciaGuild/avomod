package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        if (params.length == 0) {
            StringBuilder outputMessage = new StringBuilder(TextFormatting.BLUE + "Help for AVOMod:");

            for (Map.Entry<String, Command> command : Avomod.commands.entrySet()) {
                outputMessage.append("\n").append(TextFormatting.GOLD).append("/avomod ").append(command.getKey()).append(": ").append(TextFormatting.GREEN).append(command.getValue().getDescription());
            }

            TextComponentString textComponent = new TextComponentString(outputMessage.toString());
            sender.sendMessage(textComponent);
        } else {
            if (Avomod.commands.containsKey(params[0])) {
                sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Usage: " + Avomod.commands.get(params[0]).getUsage(sender)));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.DARK_RED + params[0] + TextFormatting.RED + " is not an AvoMod command"));
            }
        }
    }

    @Override
    public @Nonnull
    String getName() {
        return "help";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "help <command>";
    }

    @Override
    public String getDescription() {
        return "Shows help for commands with AVOMod";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("h");
    }

    @Override
    public @Nonnull
    List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, Avomod.commands.keySet());
    }
}
