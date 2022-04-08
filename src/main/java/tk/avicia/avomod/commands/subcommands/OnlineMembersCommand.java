package tk.avicia.avomod.commands.subcommands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.webapi.GuildData;
import tk.avicia.avomod.webapi.GuildNameFromTag;
import tk.avicia.avomod.webapi.OnlinePlayers;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OnlineMembersCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] params) throws CommandException {
        String outputMessage;

        if (params.length >= 1) {
            String guildName = String.join("%20", params);

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
                            guildName = guildNameFromTag.getGuildName();
                        } else {
                            ITextComponent textComponent = new TextComponentString(TextFormatting.DARK_RED + guildName + TextFormatting.RED + " is not a guild");
                            sender.sendMessage(textComponent);
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
                    // Sorts the list alphabetically and since the ranks are displayed with ★ and since ★ is really
                    // late alphabetically, it also sorts by rank
                    membersWithRank.sort(String::compareToIgnoreCase);
                    Collections.reverse(membersWithRank);
                    outputMessage = TextFormatting.BLUE + guildData.getGuildName() + TextFormatting.DARK_AQUA + " [" +
                            TextFormatting.BLUE + guildData.getGuildTag() + TextFormatting.DARK_AQUA + "]"
                            + TextFormatting.AQUA + " has " + membersWithRank.size() + " members online: " +
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
    }

    @Override
    public @Nonnull
    String getName() {
        return "onlinemembers";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "onlinemembers <guildName>";
    }

    @Override
    public String getDescription() {
        return "Shows the players online in a guild. Somewhat case sensitive, but you may use guild tags";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("om");
    }
}
