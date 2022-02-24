package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.configs.ConfigsGui;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ConfigsCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        Avomod.guiToDraw = new ConfigsGui();
    }

    @Override
    public @Nonnull
    String getName() {
        return "configs";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "configs";
    }

    @Override
    public String getDescription() {
        return "Pulls up avomod configs";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Arrays.asList("cf", "config");
    }
}