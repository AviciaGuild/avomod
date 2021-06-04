package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonElement;
import javafx.util.Pair;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import tk.avicia.avomod.Tuple;
import tk.avicia.avomod.Utils;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.WorldUpTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AgeCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        Thread thread = new Thread(() -> {
            String outputMessage = "";
            if (params.length >= 1) {
                String world = params[0];
                WorldUpTime worldUpTime = new WorldUpTime();
                try {
                    Tuple<String, Integer> worldAge = worldUpTime.getAge(world);
                    outputMessage = TextFormatting.GOLD + worldAge.x + " : " + TextFormatting.DARK_AQUA +
                            Utils.getReadableTime(worldAge.y);
                } catch (NotFound notFound) {
                    outputMessage = TextFormatting.DARK_RED + params[0] + TextFormatting.RED + " Could not be found!";
                }
            } else {
                outputMessage = TextFormatting.RED + "age <world>";
            }
            TextComponentString textComponent = new TextComponentString(outputMessage);
            sender.sendMessage(textComponent);

        });
        thread.start();
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
