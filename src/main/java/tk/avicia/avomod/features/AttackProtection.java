package tk.avicia.avomod.features;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;

import java.util.List;
import java.util.stream.Collectors;

public class AttackProtection {
    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!Mouse.getEventButtonState() || Avomod.getMC().player == null) return;

        try {
            Container openContainer = Avomod.getMC().player.openContainer;
            if (!(openContainer instanceof ContainerChest)) return;

            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            if (!lowerInventory.getName().contains("Attacking: ")) return;

            ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();
            int scaleFactor = scaledResolution.getScaleFactor();
            int scaledMouseX = Mouse.getX() / scaleFactor;
            int scaledMouseY = Mouse.getY() / scaleFactor;
            int slotDimensions = 18;

            int slotNumX = (int) Math.floor((scaledMouseX - (screenWidth / 2.0 - 4.5 * slotDimensions)) / slotDimensions);
            int slotNumY = (int) Math.floor(((screenHeight / 2.0 + 4.66667 * slotDimensions) - scaledMouseY) / slotDimensions);
            int slotNum = (slotNumY - 1) * 9 + slotNumX;

            if (slotNum != 13) return;
            List<String> territoryLore = lowerInventory.getStackInSlot(13).getTooltip(Avomod.getMC().player, ITooltipFlag.TooltipFlags.ADVANCED);
            List<String> costLines = territoryLore.stream().filter(e -> TextFormatting.getTextWithoutFormattingCodes(e).startsWith("Cost: ")).collect(Collectors.toList());

            if (costLines.size() == 0) return;
            String costLine = costLines.get(0);

            if (costLine.contains("Free")) return;
            String costString = costLine.split(" ")[costLine.split(" ").length - 1];
            int cost = Integer.parseInt(costString);

            ItemStack minecart = lowerInventory.getStackInSlot(13);
            if (!minecart.toString().contains("minecart") ||
                    cost < Integer.parseInt(Avomod.getConfig("attackConfirmation"))) return;

            event.setCanceled(true);
            NBTTagCompound checkmarNBT = new NBTTagCompound();
            checkmarNBT.setString("id", "minecraft:golden_shovel");
            checkmarNBT.setFloat("Count", 1);

            NBTTagCompound tagNBT = new NBTTagCompound();
            tagNBT.setFloat("Unbreakable", 1);
            tagNBT.setInteger("HideFlags", 6);

            NBTTagCompound displayNBT = new NBTTagCompound();
            displayNBT.setTag("Lore", ((NBTTagCompound) ((NBTTagCompound) minecart.serializeNBT().getTag("tag")).getTag("display")).getTag("Lore"));
            displayNBT.setString("Name", TextFormatting.GOLD + "" + TextFormatting.BOLD + "Click again to attack");
            tagNBT.setTag("display", displayNBT);
            checkmarNBT.setTag("tag", tagNBT);

            checkmarNBT.setTag("tag", tagNBT);
            checkmarNBT.setFloat("Damage", 20);

            lowerInventory.setInventorySlotContents(13, new ItemStack(checkmarNBT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
