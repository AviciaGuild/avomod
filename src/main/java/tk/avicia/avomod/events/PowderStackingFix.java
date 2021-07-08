package tk.avicia.avomod.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

import java.util.List;

public class PowderStackingFix {
    public static void execute(GuiScreenEvent.MouseInputEvent.Pre event, Container openContainer) {
        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledMouseX = Mouse.getX() / scaleFactor;
        int scaledMouseY = Mouse.getY() / scaleFactor;
        int slotDimensions = 18;

        int leftEdge = (screenWidth / 2) - (int) Math.floor(4.5 * slotDimensions);
        int topEdge = (screenHeight / 2) + (int) Math.floor(3 * slotDimensions) + 12;

        int slotX = (int) Math.floor((scaledMouseX - leftEdge) / slotDimensions);
        int slotY = (int) Math.ceil((topEdge - scaledMouseY) / slotDimensions);
        int slotNumber = 9 * slotY + slotX;

        List<Slot> inventorySlots = openContainer.inventorySlots;

        if (slotNumber >= inventorySlots.size() || slotNumber < 0) {
            return;
        }

        ItemStack hoveredItem = inventorySlots.get(slotNumber).getStack();

        if (!hoveredItem.getDisplayName().contains("Powder")) {
            return;
        }

        if (Mouse.getEventButtonState() && Keyboard.getEventKey() == 42 && Mouse.getEventButton() == 0 && scaledMouseY > screenHeight / 2) {
            int powderIndex = -1;
            List<ItemStack> inventory = Avomod.getMC().player.inventory.mainInventory;

            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.get(i).getDisplayName().equals(hoveredItem.getDisplayName()) && inventory.get(i).getCount() < 64) {
                    event.setCanceled(true);
                    System.out.println("Cancelled");
                    powderIndex = i;
                }
            }

            if (powderIndex != -1) {
                Utils.sendClickPacket(openContainer, slotNumber, ClickType.PICKUP, 0, hoveredItem);

                final int finalPowderIndex = powderIndex + 18;
                Thread thread = new Thread(() -> {
                    for (int i = 0; i < 100; i++) {
                        try {
                            Thread.sleep(5);

                            List<Slot> currentSlots = openContainer.inventorySlots;
                            if (!currentSlots.get(slotNumber).getStack().getDisplayName().contains("Powder")) {
                                Utils.sendClickPacket(openContainer, finalPowderIndex, ClickType.PICKUP, 0, inventorySlots.get(finalPowderIndex).getStack());
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        }
    }
}
