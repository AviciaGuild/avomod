package tk.avicia.avomod.events;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;

import java.util.List;

public class AttackedTerritoryDifficulty {
    private static long currentTime = System.currentTimeMillis();
    private static String currentTerritory = null;
    private static String currentDefense = null;

    public static void inMenu() {
        String territoryDefense = null;

        Container openContainer = Avomod.getMC().player.openContainer;
        InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
        ItemStack minecart = lowerInventory.getStackInSlot(13);
        List<String> territoryLore = minecart.getTooltip(Avomod.getMC().player, ITooltipFlag.TooltipFlags.ADVANCED);
        String territoryDefenseMessage = territoryLore.get(1);

        if (territoryDefenseMessage.contains("Territory Defences")) {
            territoryDefense = TextFormatting.getTextWithoutFormattingCodes(territoryDefenseMessage).split(": ")[1];
        }

        if (territoryDefense == null) return;

        currentDefense = territoryDefense;
        currentTerritory = lowerInventory.getName().split(": ")[1];
        currentTime = System.currentTimeMillis();
    }

    public static void receivedChatMessage(String territory) {
        if (System.currentTimeMillis() - currentTime < 10000 && territory.equals((currentTerritory))) {
            Avomod.getMC().player.sendChatMessage("/g " + currentTerritory + " defense is " + currentDefense);
        }
    }
}
