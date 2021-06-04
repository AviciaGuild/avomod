package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.GuildData;
import tk.avicia.avomod.webapi.GuildNameFromTag;
import tk.avicia.avomod.webapi.OnlinePlayers;
import tk.avicia.avomod.webapi.PlayerData;

import javax.swing.text.TextAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static tk.avicia.avomod.Avomod.getMC;

public class OnlineMembersCommand extends Command {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
        Thread thread = new Thread(() -> {

            String outputMessage = "";

            if (params.length >= 1) {
                String guildName = params[0];

                if (!guildName.equals("")) {

                    try {
                        if (guildName.length() == 3 || guildName.length() == 4) {
                            GuildNameFromTag guildNameFromTag = new GuildNameFromTag(guildName);
                            if (guildNameFromTag.hasMatch()) {
                                if (guildNameFromTag.hasMultipleMatches()) {
                                    ITextComponent multipleTagsMessage = new TextComponentString(TextFormatting.RED +
                                            "Multiple guilds with tag: " + TextFormatting.DARK_RED + guildName);
                                    sender.sendMessage(multipleTagsMessage);
                                    for (Map.Entry<String, JsonElement> guildMatch : guildNameFromTag.getGuildMatches()) {
                                        ITextComponent tagMessage = new TextComponentString(TextFormatting.DARK_RED +
                                                guildMatch.getKey() + ": " + TextFormatting.RED + guildMatch.getValue()
                                                .getAsString());
                                        sender.sendMessage(tagMessage);
                                    }
                                }
                                guildName = guildNameFromTag.getGuildName().replaceAll(" ", "%20");
                            }
                        }
                        GuildData guildData = new GuildData(guildName);
                        OnlinePlayers onlinePlayerData = new OnlinePlayers();
                        JsonArray guildMembers = guildData.getMembers();
                        List<String> membersWithRank = new ArrayList<>();
                        for (JsonElement guildMember : guildMembers) {
                            JsonObject memberData = guildMember.getAsJsonObject();
                            if (onlinePlayerData.isPlayerOnline(memberData.get("name").getAsString())) {
                                membersWithRank.add(guildData.getWithRankFormatting(memberData.get("name").getAsString()));
                            }
                        }
                        // Sorts the list alphabetically and since the ranks are displayed with * and * is really early
                        // alphabetically, it also sorts by rank
                        membersWithRank.sort(String::compareToIgnoreCase);
                        outputMessage = TextFormatting.BLUE + guildData.getGuildName() + TextFormatting.AQUA + " has " +
                                membersWithRank.size() + " members online: " +
                                TextFormatting.GOLD + String.join(", ", membersWithRank);
                    } catch (IllegalArgumentException e) {
                        outputMessage = TextFormatting.DARK_RED + guildName + TextFormatting.RED + " is not a guild";
                    }
                } else {
                    outputMessage = TextFormatting.RED + "onlinemembers <guildName>";
                }
            } else {
                outputMessage = TextFormatting.RED + "onlinemembers <guildName>";
            }
            ITextComponent textComponent = new TextComponentString(outputMessage);
            sender.sendMessage(textComponent);

        });
        thread.start();
    }

    @Override
    public String getName() {
        return "onlinemembers";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "onlinemembers <guildName>";
    }

    @Override
    public String getDescription() {
        return "Shows the players online in a guild. Somewhat case sensitive, but you may use guild tags";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("om");
    }
}
