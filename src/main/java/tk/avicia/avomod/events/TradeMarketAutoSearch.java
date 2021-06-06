package tk.avicia.avomod.events;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;

public class TradeMarketAutoSearch {
    private static boolean executing = false;

    public static void execute(GuiScreenEvent.MouseInputEvent.Pre event, Container openContainer, int screenWidth, int screenHeight, int slotDimensions) {
        if (TradeMarketAutoSearch.executing) return;
        if (Mouse.isButtonDown(2) && Mouse.getY() < ((screenHeight - 439) / 2) + (4 * slotDimensions) + 7 && Mouse.getY() > (screenHeight - 439) / 2 &&
                Mouse.getX() > (screenWidth - 323) / 2 && Mouse.getX() < ((screenWidth - 323) / 2) + (9 * slotDimensions)) {
            event.setCanceled(true);

            int slotNumber = 0;
            int slotX = (int) Math.floor((Mouse.getX() - ((screenWidth - 323) / 2)) / 36);
            int slotY = (int) Math.ceil(((((screenHeight - 439) / 2) + (4 * slotDimensions) + 7) - Mouse.getY()) / 36);
            InventoryPlayer inventory = Avomod.getMC().player.inventory;

            if (slotY == 3) {
                slotNumber = slotX;
            } else {
                slotNumber = 9 * (slotY + 1) + slotX;
            }

            ItemStack itemInSlot = inventory.getStackInSlot(slotNumber);
            String name = TextFormatting.getTextWithoutFormattingCodes(itemInSlot.getDisplayName());

            if (!name.equals("Air")) {
                ItemStack compass = openContainer.inventorySlots.get(35).getStack();
                if (compass.getDisplayName().contains("Search Item")) {
                    Thread thread = new Thread(() -> {
                        executing = true;

                        CPacketClickWindow compassPacket = new CPacketClickWindow(openContainer.windowId, 35, 0,
                                ClickType.PICKUP, compass, openContainer.getNextTransactionID(inventory));
                        Avomod.getMC().getConnection().sendPacket(compassPacket);

                        for (int i = 0; i < 5; i++) {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Container newContainer = Avomod.getMC().player.openContainer;
                            ItemStack sign = newContainer.inventorySlots.get(3).getStack();

                            if (sign.getDisplayName().contains("Add Name Contains Filter")) {
                                CPacketClickWindow signPacket = new CPacketClickWindow(newContainer.windowId, 3, 0,
                                        ClickType.PICKUP, sign, newContainer.getNextTransactionID(inventory));
                                Avomod.getMC().getConnection().sendPacket(signPacket);
                                break;
                            }
                        }

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        CPacketChatMessage chatPacket = new CPacketChatMessage(name);
                        Avomod.getMC().getConnection().sendPacket(chatPacket);

                        for (int i = 0; i < 5; i++) {
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Container newContainer = Avomod.getMC().player.openContainer;
                            ItemStack searchItem = newContainer.inventorySlots.get(53).getStack();

                            if (searchItem.getDisplayName().contains("Search")) {
                                CPacketClickWindow confirmPacket = new CPacketClickWindow(newContainer.windowId, 53, 0,
                                        ClickType.PICKUP, searchItem, newContainer.getNextTransactionID(inventory));
                                Avomod.getMC().getConnection().sendPacket(confirmPacket);
                                break;
                            }
                        }

                        executing = false;
                    });
                    thread.start();
                }
            }
        }
    }
}
