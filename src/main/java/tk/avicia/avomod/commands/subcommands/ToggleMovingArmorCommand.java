package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import java.util.Arrays;
import java.util.List;

public class ToggleMovingArmorCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params != null && params.length > 0) {
            if (params[0].equals("enable")) {
                Avomod.toggleMovingArmorOrAccessories(true);
            } else if (params[0].equals("disable")) {
                Avomod.toggleMovingArmorOrAccessories(false);
            }
        } else {
            Avomod.toggleMovingArmorOrAccessories(!Avomod.isMovingArmorOrAccessoriesDisabled());
        }

        String outputMessage;
        if (Avomod.isMovingArmorOrAccessoriesDisabled()) {
            outputMessage = "Moving armor or accessories is now disabled.";
        } else {
            outputMessage = "Moving armor or accessories is now enabled.";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "togglemoving";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "togglemoving <enable:disable>";
    }

    @Override
    public String getDescription() {
        return "Toggles moving armor or accessories";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("tm");
    }
}
