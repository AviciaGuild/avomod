package tk.avicia.avomod.commands.subcommands;

import joptsimple.internal.Strings;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class DebugMessage extends Command {


    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        String message = Strings.join(params, " ");
        Avomod.getMC().player.sendMessage(new TextComponentString(message));
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(ChatType.CHAT, new TextComponentString(message));
        MinecraftForge.EVENT_BUS.post(event);
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