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

public class UpCommand extends Command {
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
        for (int i = 0; i < amount; i++) {
            int age = worldData.get(i).getValue()
                    .getAsJsonObject().get("age").getAsInt();
            String readableAge = Utils.getReadableTime(age);
            TextComponentString textComponent = new TextComponentString(TextFormatting.GOLD +
                    worldData.get(i).getKey() + ": " + TextFormatting.GREEN + readableAge);
            sender.sendMessage(textComponent);
        }
    }

    @Override
    public String getName() {
        return "up";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "up <amount>";
    }

    @Override
    public String getDescription() {
        return "Shows worlds and their age";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }
}
