package tk.avicia.avomod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import tk.avicia.avomod.Avomod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AvomodCommand extends CommandBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        if (params.length >= 1) {
            Command commandToExecute = Avomod.commands.get(params[0]);

            if (commandToExecute == null) {
                commandToExecute = Avomod.aliases.get(params[0]);
            }

            if (commandToExecute != null) {
                commandToExecute.execute(server, sender, Arrays.copyOfRange(params, 1, params.length));
            } else {
                TextComponentString textComponent = new TextComponentString("That command does not exist.");
                sender.sendMessage(textComponent);
            }
        }
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
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("am");
    }
}
