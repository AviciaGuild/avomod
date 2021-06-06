package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.PlayerData;

import java.util.Arrays;
import java.util.List;

public class PlayerGuildCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        if (params.length >= 1) {
            String username = params[0];
            try {

                PlayerData playerData = new PlayerData(username);
                String playerGuild = playerData.getGuild();
                String playerGuildRank = playerData.getGuildRank();

                if (playerGuild != null && playerGuildRank != null) {
                    outputMessage = TextFormatting.AQUA + playerData.getPlayerName() + TextFormatting.GRAY + " is a " + TextFormatting.AQUA +
                            Utils.firstLetterCapital(playerGuildRank) + TextFormatting.GRAY + " in the guild " + TextFormatting.AQUA + playerGuild;
                } else {
                    outputMessage = TextFormatting.AQUA + playerData.getPlayerName() + TextFormatting.GRAY + " is not in a guild.";
                }
            } catch (NotFound e) {
                outputMessage = TextFormatting.DARK_RED + username + TextFormatting.RED + " is not a Wynncraft player.";

            }
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public String getName() {
        return "playerguild";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "playerguild <username>";
    }

    @Override
    public String getDescription() {
        return "Shows what guild the person is in";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pg");
    }
}