package tk.avicia.avomod.commands.subcommands;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.commands.Command;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CountCommand extends Command {
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
        String outputMessage;

        EntityPlayerSP userObject = Avomod.getMC().player;
        InventoryPlayer inventory = userObject.inventory;
        ItemStack currentItem = inventory.getCurrentItem();
        String currentItemName = currentItem.getDisplayName();

        if (currentItemName.equals("Air")) {
            outputMessage = TextFormatting.RED + "You must be holding an item to count";
        } else {
            String itemLevel = "";
            String dungeonScroll = "";
            int totalNumber = 0;

            List<String> lore = currentItem.getTooltip(userObject, ITooltipFlag.TooltipFlags.ADVANCED);
            Optional<String> combatMinItemObject = lore.stream().filter(line -> line.contains("Combat Lv. Min: ")).findFirst();
            Object[] dungeonScrollObject = lore.stream().filter(line -> line.contains("Dungeon Teleport Scroll") || line.contains("Teleports to: ")).toArray();

            if (combatMinItemObject.isPresent()) {
                String[] splitWords = combatMinItemObject.get().split(" ");
                itemLevel = " Lvl. " + splitWords[splitWords.length - 1];
            }
            if (dungeonScrollObject.length >= 2) {
                String dungeonScrollLocationObject = dungeonScrollObject[1].toString();
                dungeonScroll = TextFormatting.GRAY + " to" + dungeonScrollLocationObject.split(":")[1];
            }

            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack slotItem = inventory.getStackInSlot(i);

                if (slotItem.getDisplayName().equals(currentItemName)) {
                    totalNumber += slotItem.getCount();
                }
            }

            outputMessage = TextFormatting.GRAY + "You have: " + TextFormatting.AQUA + totalNumber + " " + currentItemName + itemLevel + dungeonScroll + TextFormatting.GRAY + " in your inventory";
        }

        TextComponentString textComponent = new TextComponentString(outputMessage);
        sender.sendMessage(textComponent);
    }

    @Override
    public @Nonnull
    String getName() {
        return "count";
    }

    @Override
    public @Nonnull
    String getUsage(@Nonnull ICommandSender sender) {
        return "count";
    }

    @Override
    public String getDescription() {
        return "Counts how many of your held item you have in your inventory";
    }

    @Override
    public @Nonnull
    List<String> getAliases() {
        return Collections.singletonList("c");
    }
}