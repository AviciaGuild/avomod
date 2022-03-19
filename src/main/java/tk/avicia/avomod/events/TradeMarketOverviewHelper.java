package tk.avicia.avomod.events;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.client.event.GuiScreenEvent;
import tk.avicia.avomod.Avomod;

import java.util.List;
import java.util.Optional;


public class TradeMarketOverviewHelper {

    public static void execute(Container openContainer) {
        for (ItemStack itemStack : openContainer.getInventory()) {
            List<String> lore = itemStack.getTooltip(Avomod.getMC().player, ITooltipFlag.TooltipFlags.ADVANCED);
            Optional<String> soldOut = lore.stream()
                    .filter(line -> line.contains("Sold Out")).findFirst();

            if (itemStack.getUnlocalizedName().equals("item.shovelGold")) {
                if (itemStack.getDisplayName().contains("Selling")) {
                    if (soldOut.isPresent()) {
                        itemStack.setItemDamage(20);
                    } else {
                        itemStack.setItemDamage(19);
                    }
                }
                if (itemStack.getDisplayName().contains("Buying")) {
                    itemStack.setItemDamage(22);
                }
            }
        }
    }
}
