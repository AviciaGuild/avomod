package tk.avicia.avomod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import tk.avicia.avomod.Avomod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AvomodCommand extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String s) {
        return false;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params[0].contains("configs")) {
            Command commandToExecute = Avomod.commands.get("configs");

            try {
                commandToExecute.execute(server, sender, Arrays.copyOfRange(params, 1, params.length));
            } catch (Exception e) {
                TextComponentString textComponent = new TextComponentString(TextFormatting.RED + "Command Failed");
                sender.sendMessage(textComponent);
                e.printStackTrace();
            }

            return;
        }
        Thread thread = new Thread(() -> {
            if (params.length >= 1) {
                Command commandToExecute = Avomod.commands.get(params[0]);

                if (commandToExecute == null) {
                    commandToExecute = Avomod.aliases.get(params[0]);
                }

                if (commandToExecute != null) {
                    try {
                        commandToExecute.execute(server, sender, Arrays.copyOfRange(params, 1, params.length));
                    } catch (Exception e) {
                        TextComponentString textComponent = new TextComponentString(TextFormatting.RED + "Command Failed");
                        sender.sendMessage(textComponent);
                        e.printStackTrace();
                    }
                } else {
                    TextComponentString textComponent = new TextComponentString("That command does not exist.");
                    sender.sendMessage(textComponent);
                }
            }
        });
        thread.start();
    }

    @Override
    public String getName() {
        return "avomod";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/avomod <command>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Avomod.commands.keySet());
        } else {
            String commandName = args[0];
            Command commandToExecute = Avomod.commands.get(commandName);
            if(commandToExecute == null){
                commandToExecute = Avomod.aliases.get(commandName);
            }

            if (commandToExecute != null) {
                try {
                    return commandToExecute.getTabCompletions(server, sender, args, targetPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("am");
    }
}
