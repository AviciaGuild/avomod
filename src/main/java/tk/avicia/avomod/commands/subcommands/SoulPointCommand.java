package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonElement;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.WorldUpTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SoulPointCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        int amount = 5;
        if (params.length >= 1) {
            try {
                amount = parseInt(params[0]);
            } catch (NumberInvalidException e) {
                TextComponentString textComponent = new TextComponentString(TextFormatting.DARK_RED + params[0] + TextFormatting.RED + " is not a valid amount");
                sender.sendMessage(textComponent);
            }
        }
        WorldUpTime worldUpTime = new WorldUpTime();
        ArrayList<Map.Entry<String, JsonElement>> worldData = worldUpTime.getWorldUpTimeData();
        if (amount > worldData.size()) amount = worldData.size();
        int sentWorlds = 0;

        for (int spRange = 19; spRange > 16; spRange--) {
            for (Map.Entry<String, JsonElement> worldDatum : worldData) {
                int age = worldDatum.getValue()
                        .getAsJsonObject().get("age").getAsInt();
                int playerCount = worldDatum.getValue()
                        .getAsJsonObject().get("players").getAsInt();
                if (age % 20 == spRange) {
                    String readableAge = Utils.getReadableTime(age);
                    TextComponentString textComponent = new TextComponentString(TextFormatting.GOLD +
                            worldDatum.getKey() + ": " + TextFormatting.GREEN + readableAge + TextFormatting.GOLD +
                            " | " + TextFormatting.GRAY + +playerCount + " players");
                    sender.sendMessage(textComponent);
                    sentWorlds++;
                }
                if (sentWorlds == amount) break;
            }
            if (sentWorlds == amount) break;
        }

        if (sentWorlds == 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "No worlds found with that criteria"));
        }
    }

    @Override
    public String getName() {
        return "soulpoints";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "soulpoints <amount>";
    }

    @Override
    public String getDescription() {
        return "Shows worlds that will soon get soul points";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sp");
    }
}
