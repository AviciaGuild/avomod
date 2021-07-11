package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.settings.KeybindSettings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeybindsCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        if (params.length >= 1) {
            if (params[0].equals("add")) {
                if (params.length < 3) {
                    outputMessage = TextFormatting.RED + "Incorrect formatting. /avomod keybinds add <key> <command to execute>";
                } else if (params[1].length() != 1) {
                    outputMessage = TextFormatting.RED + "The keybind you're adding must be one letter.";
                } else {
                    String messageToExecute = String.join(" ", Arrays.copyOfRange(params, 2, params.length));
                    KeybindSettings.setSettings(params[1], messageToExecute);

                    outputMessage = TextFormatting.BLUE + "Keybind added. " + TextFormatting.GREEN + params[1].toUpperCase() + " will execute " + TextFormatting.YELLOW + "/" + messageToExecute;
                }
            } else if (params[0].equals("list")) {
                JsonObject keybinds = KeybindSettings.getSettings();
                outputMessage = TextFormatting.BLUE + "Current Avomod Keybinds:";

                for (Map.Entry<String, JsonElement> e : keybinds.entrySet()) {
                    outputMessage += "\n" + TextFormatting.GREEN + e.getKey().toUpperCase() + ": " + TextFormatting.YELLOW + "/" + e.getValue().getAsString();
                }
            } else if (params[0].equals("remove")) {
                if (params.length < 2) {
                    outputMessage = TextFormatting.RED + "Incorrect formatting. /avomod keybinds remove <key>";
                } else if (params[1].length() != 1) {
                    outputMessage = TextFormatting.RED + "The keybind you're removing must be one letter.";
                } else if (!Avomod.keybinds.containsKey(params[1])) {
                    outputMessage = TextFormatting.RED + "The keybind you're removing does not exist.";
                } else {
                    Avomod.keybinds.get(params[1]).setKeyCode(0);
                    KeybindSettings.removeSetting(params[1]);

                    outputMessage = TextFormatting.BLUE + "Keybind removed for " + TextFormatting.GREEN + params[1].toUpperCase() + ". Note: This keybind will show up in your controls until you restart your game.";
                }
            }
        } else {
            outputMessage = TextFormatting.RED + "keybinds <add:list:remove>";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "keybinds";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "keybinds <add:list:remove>";
    }

    @Override
    public String getDescription() {
        return "Avomod custom keybinds command";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos
            targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "list");
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("kb");
    }
}