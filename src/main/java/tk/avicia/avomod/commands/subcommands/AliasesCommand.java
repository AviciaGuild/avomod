package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AliasesCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        StringBuilder outputMessage = new StringBuilder(TextFormatting.BLUE + "Aliases for AVOMod:");

        for (Map.Entry<String, Command> command : Avomod.commands.entrySet()) {
            String aliases = String.join(", ", command.getValue().getAliases());
            outputMessage.append("\n").append(TextFormatting.GOLD).append(command.getKey()).append(": ").append(TextFormatting.GREEN).append(aliases);
        }

        TextComponentString textComponent = new TextComponentString(outputMessage.toString());
        sender.sendMessage(textComponent);
    }

    @Override
    public @Nonnull
    String getName() {
        return "aliases";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "aliases <command>";
    }

    @Override
    public String getDescription() {
        return "Shows all aliases for AVOMod";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("al");
    }
}
