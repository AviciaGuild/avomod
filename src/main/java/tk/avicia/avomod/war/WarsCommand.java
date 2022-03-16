package tk.avicia.avomod.war;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.Command;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WarsCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        long wars;
        long since = WarTrackerFile.timeOfFirstWar();

        if (params.length >= 1) {
            try {
                int daysSince = Integer.parseInt(params[0]);
                since = System.currentTimeMillis() - (daysSince * 86400000L);
                wars = WarTrackerFile.getWars(since);
            } catch (NumberFormatException e) {
                wars = WarTrackerFile.getWars(0);
            }
        } else {
            wars = WarTrackerFile.getWars(0);
        }
        String plural = "";
        if (wars != 1) {
            plural = "s";
        }

        String outputMessage = TextFormatting.AQUA + "You have done " + TextFormatting.GOLD + wars + TextFormatting.AQUA + " war" + plural + " since " +
                TextFormatting.GOLD + new Date(since) + "\n" +
                TextFormatting.RESET + TextFormatting.LIGHT_PURPLE + "Only wars done with avomod active are counted.";
        sender.sendMessage(new TextComponentString(outputMessage));
    }

    @Override
    public @Nonnull
    String getName() {
        return "wars";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "age <world>";
    }

    @Override
    public String getDescription() {
        return "Shows the age of a specific world";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Arrays.asList("war", "w");
    }
}
