package tk.avicia.avomod.commands.subcommands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.PlayerData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LastSeenCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        String outputMessage = "";
        if (params.length >= 1) {
            String username = params[0];
            try {
                PlayerData playerData = new PlayerData(username);
                String playerLastJoin = playerData.getLastJoin();
                try {
                    SimpleDateFormat simpleDateObject = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    simpleDateObject.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date playerLastJoinDateObject = simpleDateObject.parse(playerLastJoin.replace("T", " ").split("[.]")[0]);
                    long lastSeenMilliseconds = new Date().getTime() - playerLastJoinDateObject.getTime();
                    int lastSeenDays = (int) Math.floor(lastSeenMilliseconds / 86400000);
                    int lastSeenHours = (int) Math.floor(lastSeenMilliseconds / 3600000) % 24;

                    outputMessage = TextFormatting.DARK_RED + playerData.getPlayerName() + TextFormatting.RED + " was last seen "
                            + lastSeenDays + " days, and " + lastSeenHours + " hours ago";
                } catch (ParseException e) {
                    TextComponentString textComponent = new TextComponentString(TextFormatting.RED + "Error parsing date from api.");
                    sender.sendMessage(textComponent);
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
        return "lastseen";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "lastseen <username>";
    }

    @Override
    public String getDescription() {
        return "Shows how long ago the person last logged on";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("ls");
    }
}