package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.utils.Tuple;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.WorldUpTime;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AgeCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        WorldUpTime worldUpTime = new WorldUpTime();
        if (params.length >= 1) {
            String world = params[0];
            try {
                Tuple<String, Integer> worldAge = worldUpTime.getAge(world);
                outputMessage = TextFormatting.GOLD + worldAge.x + " : " + TextFormatting.DARK_AQUA +
                        Utils.getReadableTime(worldAge.y);
            } catch (NotFound notFound) {
                outputMessage = TextFormatting.DARK_RED + params[0] + TextFormatting.RED + " Could not be found!";
            }
        } else {
            String name = Avomod.getMC().getConnection().getPlayerInfo(UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af")).getDisplayName().getUnformattedText();
            String currentWorld = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
            try {
                Tuple<String, Integer> worldAge = worldUpTime.getAge(currentWorld);
                outputMessage = TextFormatting.GOLD + worldAge.x + " : " + TextFormatting.DARK_AQUA +
                        Utils.getReadableTime(worldAge.y);
            } catch (NotFound notFound) {
                outputMessage = TextFormatting.DARK_RED + currentWorld + TextFormatting.RED + " Could not be found!";
            }
        }
        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "age";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "age <world>";
    }

    @Override
    public String getDescription() {
        return "Shows the age of a specific world";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }
}
