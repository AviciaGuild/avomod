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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        int amount = 5;
        int minAge = 0;
        int maxAge = Integer.MAX_VALUE;
        if (params.length >= 1) {
            try {
                amount = parseInt(params[0]);
                if (params.length >= 2) {
                    minAge = parseInt(params[1]);
                }
                if (params.length >= 3) {
                    maxAge = parseInt(params[2]);
                }
            } catch (NumberInvalidException e) {
                TextComponentString textComponent = new TextComponentString(TextFormatting.DARK_RED + params[0] + TextFormatting.RED + " is not a valid amount");
                sender.sendMessage(textComponent);
            }
        }
        WorldUpTime worldUpTime = new WorldUpTime();
        ArrayList<Map.Entry<String, JsonElement>> worldData = worldUpTime.getWorldUpTimeData();
        if (amount > worldData.size()) amount = worldData.size();
        int sentWorlds = 0;
        for (int i = 0; i < amount; i++) {
            int age = worldData.get(i).getValue()
                    .getAsJsonObject().get("age").getAsInt();
            int playerCount = worldData.get(i).getValue()
                    .getAsJsonObject().get("players").getAsInt();
            if (age >= minAge && age <= maxAge) {
                String readableAge = Utils.getReadableTime(age);
                TextComponentString textComponent = new TextComponentString(TextFormatting.GOLD +
                        worldData.get(i).getKey() + ": " + TextFormatting.GREEN + readableAge + TextFormatting.GOLD +
                        " | " + TextFormatting.GRAY + +playerCount + " players");
                sender.sendMessage(textComponent);
                sentWorlds++;
            } else {
                if (amount < worldData.size() - 2)
                    amount++;
            }
        }
        if (sentWorlds == 0) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "No worlds found with that criteria"));
        }
    }

    @Override
    public @Nonnull
    String getName() {
        return "up";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "up <amount> <minAge(minutes)> <maxAge(minutes)>";
    }

    @Override
    public String getDescription() {
        return "Shows worlds and their age";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.emptyList();
    }
}
