package tk.avicia.avomod.features;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

public class TradeMarketAutoSearch {
    private static boolean executing = false;

    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null || Avomod.getMC().getConnection() == null)
            return;

        Container openContainer = Avomod.getMC().player.openContainer;
        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledMouseX = Mouse.getX() / scaleFactor;
        int scaledMouseY = Mouse.getY() / scaleFactor;
        int slotDimensions = 18;

        if (!(openContainer instanceof ContainerChest)) return;
        String containerName = ((ContainerChest) openContainer).getLowerChestInventory().getName();

        if (!Mouse.isButtonDown(2) || !containerName.equals("Trade Market")) return;

        int leftEdge = (screenWidth / 2) - (int) Math.floor(4.5 * slotDimensions);
        int topEdge = (screenHeight / 2) - (int) Math.floor(0.9 * slotDimensions) - 12;

        if (scaledMouseY < topEdge && scaledMouseY > topEdge - (4 * slotDimensions) - (2 * scaleFactor) &&
                scaledMouseX > leftEdge && scaledMouseX < leftEdge + (9 * slotDimensions)) {
            event.setCanceled(true);

            int slotNumber;
            int slotX = (int) Math.floor((double) (scaledMouseX - leftEdge) / slotDimensions);
            int slotY = (int) Math.ceil((double) (topEdge - scaledMouseY) / slotDimensions);
            InventoryPlayer inventory = Avomod.getMC().player.inventory;

            if (slotY == 4) {
                slotNumber = slotX;
            } else {
                slotNumber = 9 * slotY + slotX;
            }

            ItemStack itemInSlot = inventory.getStackInSlot(slotNumber);
            String name = TextFormatting.getTextWithoutFormattingCodes(itemInSlot.getDisplayName());
            if (name == null) return;

            if (name.equals("Air")) return;
            ItemStack compass = openContainer.inventorySlots.get(35).getStack();

            if (!compass.getDisplayName().contains("Search Item") || TradeMarketAutoSearch.executing) return;
            executing = true;

            new Thread(() -> {
                Utils.sendClickPacket(openContainer, 35, ClickType.PICKUP, 0, compass);

                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Container newContainer = Avomod.getMC().player.openContainer;
                    ItemStack sign = newContainer.inventorySlots.get(3).getStack();

                    if (!sign.getDisplayName().contains("Add Name Contains Filter")) continue;

                    Utils.sendClickPacket(newContainer, 3, ClickType.PICKUP, 0, sign);
                    break;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                CPacketChatMessage chatPacket = new CPacketChatMessage(name.replaceAll(" \\[.*]", ""));
                Avomod.getMC().getConnection().sendPacket(chatPacket);

                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Container newContainer = Avomod.getMC().player.openContainer;
                    ItemStack searchItem = newContainer.inventorySlots.get(53).getStack();

                    if (!searchItem.getDisplayName().contains("Search")) continue;

                    Utils.sendClickPacket(newContainer, 53, ClickType.PICKUP, 0, searchItem);
                    break;
                }

                executing = false;
            }).start();
        }
    }
}
