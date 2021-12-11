package tk.avicia.avomod.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import tk.avicia.avomod.Avomod;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;

public class GetTokenCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String token = Avomod.getMC().getSession().getToken();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(token), null);

        sender.sendMessage(new TextComponentString("Token was copied to clipboard. Do not give this token to anyone."));
    }

    @Override
    public String getName() {
        return "gettoken";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "gettoken";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Gets minecraft client token";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("gt");
    }
}
