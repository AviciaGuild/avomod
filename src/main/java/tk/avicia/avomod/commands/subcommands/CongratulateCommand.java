package tk.avicia.avomod.commands.subcommands;

import joptsimple.internal.Strings;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CongratulateCommand extends Command {

    public static List<String> list = new ArrayList<>();

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        String username = Strings.join(params, " ");
        if (username.isEmpty() || username.contains(" ")) {
            sender.sendMessage(
                    new TextComponentString(TextFormatting.RED + "wrong username")
            );
            return;
        }
        if (!list.contains(username))
            return;

        String message = Avomod.getConfig("congratsMessage");
        Avomod.getMC().player.sendChatMessage("/msg " + username + " " + message);
        list.remove(username);
    }

    @Override
    public @Nonnull
    String getName() {
        return "count";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "congratulate";
    }

    @Override
    public String getDescription() {
        return "Congratulate a player";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("msg");
    }
}