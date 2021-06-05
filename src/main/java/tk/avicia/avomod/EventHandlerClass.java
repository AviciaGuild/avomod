package tk.avicia.avomod;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class EventHandlerClass {
    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        String message = event.getMessage().getFormattedText();
        boolean bankMessage = message.startsWith("[INFO]") && message.contains("Guild Bank");
        if (bankMessage && Avomod.isBankFiltered()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Avomod.getMC().player == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;
        int screenWidth = Avomod.getMC().displayWidth;
        int screenHeight = Avomod.getMC().displayHeight;
        int slotDimensions = 36;
        InventoryPlayer inventory = Avomod.getMC().player.inventory;

        if (openContainer instanceof ContainerPlayer && Avomod.isMovingArmorOrAccessoriesDisabled()) {
            if (Mouse.getEventButtonState() && Mouse.getY() > (screenHeight / 2) - slotDimensions && Mouse.getX() < (screenWidth / 2) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
                event.setCanceled(true);
            }
        } else if (openContainer instanceof ContainerChest) {
            String containerName = ((ContainerChest) openContainer).getLowerChestInventory().getName();
            if (containerName.equals("Trade Market") && !Avomod.searchButtonClicked) {
                if (Mouse.getEventButtonState() && Mouse.getY() < ((screenHeight - 439) / 2) + (4 * slotDimensions) + 7 && Mouse.getY() > (screenHeight - 439) / 2 &&
                        Mouse.getX() > (screenWidth - 323) / 2 && Mouse.getX() < ((screenWidth - 323) / 2) + (9 * slotDimensions)) {
                    event.setCanceled(true);

                    int slotNumber = 0;
                    int slotX = (int) Math.floor((Mouse.getX() - ((screenWidth - 323) / 2)) / 36);
                    int slotY = (int) Math.ceil(((((screenHeight - 439) / 2) + (4 * slotDimensions) + 7) - Mouse.getY()) / 36);

                    System.out.println(slotX + ", " + slotY);
                    if (slotY == 3) {
                        slotNumber = slotX;
                    } else {
                        slotNumber = 9 * (slotY + 1) + slotX;
                    }
                    System.out.println(slotNumber);

                    ItemStack itemInSlot = inventory.getStackInSlot(slotNumber);
                    String name = TextFormatting.getTextWithoutFormattingCodes(itemInSlot.getDisplayName());

                    System.out.println(name);
                    if (!name.equals("Air")) {
                        ItemStack compass = openContainer.inventorySlots.get(35).getStack();
                        if (compass.getDisplayName().contains("Search Item")) {
                            Thread thread = new Thread(() -> {
                                Avomod.searchButtonClicked = true;

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

                                Avomod.searchButtonClicked = false;
                            });
                            thread.start();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (Avomod.getMC().player == null) {
            return;
        }
        if (Avomod.isMovingArmorOrAccessoriesDisabled()) {
            Container openContainer = Avomod.getMC().player.openContainer;

            if (openContainer instanceof ContainerPlayer) {
                int screenWidth = Avomod.getMC().displayWidth;
                int screenHeight = Avomod.getMC().displayHeight;
                int slotDimensions = 36;

                if (Keyboard.getEventKey() == 16 && Mouse.getY() > (screenHeight / 2) - slotDimensions && Mouse.getX() < (screenWidth / 2) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
