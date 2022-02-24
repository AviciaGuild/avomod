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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeybindsCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        StringBuilder outputMessage = new StringBuilder();
        if (params.length >= 1) {
            switch (params[0]) {
                case "add":
                    if (params.length < 3) {
                        outputMessage = new StringBuilder(TextFormatting.RED + "Incorrect formatting. /avomod keybinds add <key> <command to execute>");
                    } else if (params[1].length() != 1) {
                        outputMessage = new StringBuilder(TextFormatting.RED + "The keybind you're adding must be one letter.");
                    } else {
                        String messageToExecute = String.join(" ", Arrays.copyOfRange(params, 2, params.length));
                        if (messageToExecute.startsWith("/")) {
                            messageToExecute = messageToExecute.substring(1);
                        }
                        KeybindSettings.setSettings(params[1], messageToExecute);

                        outputMessage = new StringBuilder(TextFormatting.BLUE + "Keybind added. " + TextFormatting.GREEN + params[1].toUpperCase() + " will execute " + TextFormatting.YELLOW + "/" + messageToExecute);
                    }
                    break;
                case "list":
                    JsonObject keybinds = KeybindSettings.getSettings();
                    outputMessage = new StringBuilder(TextFormatting.BLUE + "Current Avomod Keybinds:");

                    for (Map.Entry<String, JsonElement> e : keybinds.entrySet()) {
                        outputMessage.append("\n").append(TextFormatting.GREEN).append(e.getKey().toUpperCase()).append(": ").append(TextFormatting.YELLOW).append("/").append(e.getValue().getAsString());
                    }
                    break;
                case "remove":
                    if (params.length < 2) {
                        outputMessage = new StringBuilder(TextFormatting.RED + "Incorrect formatting. /avomod keybinds remove <key>");
                    } else if (params[1].length() != 1) {
                        outputMessage = new StringBuilder(TextFormatting.RED + "The keybind you're removing must be one letter.");
                    } else if (!Avomod.keybinds.containsKey(params[1])) {
                        outputMessage = new StringBuilder(TextFormatting.RED + "The keybind you're removing does not exist.");
                    } else {
                        Avomod.keybinds.get(params[1]).setKeyCode(0);
                        KeybindSettings.removeSetting(params[1]);

                        outputMessage = new StringBuilder(TextFormatting.BLUE + "Keybind removed for " + TextFormatting.GREEN + params[1].toUpperCase() + ". Note: This keybind will show up in your controls until you restart your game.");
                    }
                    break;
            }
        } else {
            outputMessage = new StringBuilder(TextFormatting.RED + "keybinds <add:list:remove>");
        }

        TextComponentString textComponent = new TextComponentString(outputMessage.toString());
        sender.sendMessage(textComponent);
    }

    @Override
    public @Nonnull
    String getName() {
        return "keybinds";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "keybinds <add:list:remove>";
    }

    @Override
    public String getDescription() {
        return "Avomod custom keybinds command";
    }

    @Override
    public @Nonnull
    List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos
            targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "list");
        }

        return Collections.emptyList();
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("kb");
    }
}