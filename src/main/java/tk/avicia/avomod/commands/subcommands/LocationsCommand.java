package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.locations.LocationsGui;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class LocationsCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        Avomod.guiToDraw = new LocationsGui();
    }

    @Override
    public @Nonnull
    String getName() {
        return "locations";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "locations";
    }

    @Override
    public String getDescription() {
        return "Pulls up avomod locations";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Arrays.asList("l", "locations");
    }
}