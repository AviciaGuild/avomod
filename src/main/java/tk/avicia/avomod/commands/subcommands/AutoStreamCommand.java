package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.core.structures.CustomFile;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class AutoStreamCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        boolean autoStreamValue = Avomod.getConfigBoolean("autoStream");

        if (params.length == 0) {
            autoStreamValue = !autoStreamValue;
        } else if (params.length == 1) {
            if (params[0].equalsIgnoreCase("off") || params[0].equalsIgnoreCase("false")) {
                autoStreamValue = false;
            } else if (params[0].equalsIgnoreCase("on") || params[0].equalsIgnoreCase("true")) {
                autoStreamValue = true;
            } else {
                return;
            }
        }

        CustomFile customFile = new CustomFile(Avomod.getConfigPath("configs"));
        JsonObject configsJson = customFile.readJson();

        configsJson.addProperty("autoStream", autoStreamValue ? "Enabled" : "Disabled");

        customFile.writeJson(configsJson);
        Avomod.configs = configsJson;
        Avomod.getMC().player.sendChatMessage("/stream");
    }

    @Override
    public @Nonnull
    String getName() {
        return "autostream";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "autostream <off|on [OPTIONAL]>";
    }

    @Override
    public String getDescription() {
        return "Enables autostream on world swap";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("as");
    }

    @Override
    public @Nonnull
    List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos
            targetPos) {
        return getListOfStringsMatchingLastWord(args, "off", "on");
    }
}
