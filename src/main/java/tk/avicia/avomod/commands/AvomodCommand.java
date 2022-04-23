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

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AvomodCommand extends CommandBase implements IClientCommand {
    public static void executeSubCommand(MinecraftServer server, ICommandSender sender, String[] params) {
        if (params.length < 1) return;

        if (params[0].contains("configs")) {
            Command commandToExecute = Avomod.commands.get("configs");

            try {
                commandToExecute.execute(server, sender, Arrays.copyOfRange(params, 1, params.length));
            } catch (Exception e) {
                TextComponentString textComponent = new TextComponentString(TextFormatting.RED + "Command Failed");
                sender.sendMessage(textComponent);
                e.printStackTrace();
            }
        } else {
            new Thread(() -> {
                Command commandToExecute = Avomod.commands.get(params[0]);

                if (commandToExecute == null) {
                    commandToExecute = Avomod.aliases.get(params[0]);
                }

                if (commandToExecute != null) {
                    try {
                        commandToExecute.execute(server, sender, Arrays.copyOfRange(params, 1, params.length));
                    } catch (Exception e) {
                        TextComponentString textComponent;

                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsString = sw.toString();
                        textComponent = new TextComponentString(TextFormatting.RED + "Command Failed: " + exceptionAsString);

                        sender.sendMessage(textComponent);
                    }
                } else {
                    TextComponentString textComponent = new TextComponentString("That command does not exist.");
                    sender.sendMessage(textComponent);
                }
            }).start();
        }
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String s) {
        return false;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        executeSubCommand(server, sender, params);
    }

    @Override
    public @Nonnull
    String getName() {
        return "avomod";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "/avomod <command>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public @Nonnull
    List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Avomod.commands.keySet());
        } else {
            String commandName = args[0];
            Command commandToExecute = Avomod.commands.get(commandName);
            if (commandToExecute == null) {
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
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("am");
    }
}
