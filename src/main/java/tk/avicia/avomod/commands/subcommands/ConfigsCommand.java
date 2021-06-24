package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.configs.ConfigsGui;

import java.util.Arrays;
import java.util.List;

public class ConfigsCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        Avomod.guiToDraw = new ConfigsGui();
    }

    @Override
    public String getName() {
        return "configs";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "configs";
    }

    @Override
    public String getDescription() {
        return "Pulls up avomod configs";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("cf", "config");
    }
}