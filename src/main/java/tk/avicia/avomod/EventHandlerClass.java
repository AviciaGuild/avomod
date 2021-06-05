package tk.avicia.avomod;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.model.SimpleModelFontRenderer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                if (Mouse.isButtonDown(2) && Mouse.getY() < ((screenHeight - 439) / 2) + (4 * slotDimensions) + 7 && Mouse.getY() > (screenHeight - 439) / 2 &&
                        Mouse.getX() > (screenWidth - 323) / 2 && Mouse.getX() < ((screenWidth - 323) / 2) + (9 * slotDimensions)) {
                    event.setCanceled(true);

                    int slotNumber = 0;
                    int slotX = (int) Math.floor((Mouse.getX() - ((screenWidth - 323) / 2)) / 36);
                    int slotY = (int) Math.ceil(((((screenHeight - 439) / 2) + (4 * slotDimensions) + 7) - Mouse.getY()) / 36);

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

    private boolean isChestNew = true;
    private int levelledItemCount = 0;
    private int totalLevelItems = 0;

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (Avomod.getMC().player == null || event.getGui() == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (containerName.contains("Loot Chest")) {
                if (isChestNew) {
                    int itemCount = 0;
                    for (int i = 0; i < 27; i++) {
                        if (!lowerInventory.getStackInSlot(i).getDisplayName().equals("Air")) {
                            itemCount++;
                        }
                    }
                    if (itemCount > 0) {
                        isChestNew = false;
                    } else {
                        // If there are no items on the chest (or the items haven't loaded) just try again basically
                        return;
                    }
                    totalLevelItems = 0;
                    levelledItemCount = 0;
                    for (int i = 0; i < 27; i++) {
                        ItemStack itemStack = lowerInventory.getStackInSlot(i);
                        if (!itemStack.getDisplayName().equals("Air")) {
                            List<String> lore = itemStack.getTooltip(Avomod.getMC().player, ITooltipFlag.TooltipFlags.ADVANCED);
                            Optional<String> itemLevel = lore.stream()
                                    .filter(line -> line.contains("Lv. ")).findFirst();

                            if (itemLevel.isPresent()) {
                                try {
                                    Pattern numberPattern = Pattern.compile("(^[0-9]+)", Pattern.CASE_INSENSITIVE);
                                    if (numberPattern.matcher(itemLevel.get().split(": ")[1]).find()) {
                                        totalLevelItems += Integer.parseInt(itemLevel.get().split(": ")[1].replaceAll("§.", ""));
                                    } else if (itemLevel.get().split(" §f")[1].split("-").length == 2) {
                                        System.out.println("double");
                                        String[] levelRange = itemLevel.get().split(" §f")[1].split("-");
                                        totalLevelItems += (Integer.parseInt(levelRange[0].replaceAll("§.", ""))
                                                + Integer.parseInt(levelRange[1].replaceAll("§.", ""))) / 2;
                                    }
                                    levelledItemCount += 1;
                                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
//                    Avomod.getMC().player.sendMessage(new TextComponentString((totalLevelItems / itemCount) + ""));
                }
                int screenWidth = event.getGui().width;
                int screenHeight = event.getGui().height;
                String displayString = "Average level of items in chest: " + (totalLevelItems / levelledItemCount);
                FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
                fontRenderer.drawString(displayString, screenWidth / 2 - fontRenderer.getStringWidth(displayString) / 2
                        , screenHeight / 2 - 100, Color.WHITE.getRGB());

            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (Avomod.getMC().player == null) {
            return;
        }
        Container openContainer = Avomod.getMC().player.openContainer;

        if (!(openContainer instanceof ContainerChest)) {
            isChestNew = true;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
//        int screenWidth = Avomod.getMC().displayWidth;
//        int screenHeight = Avomod.getMC().displayHeight;
//        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
//        fontRenderer.drawString("Hello HEllo HELLO HELLO HELLO HELLO hello hello", 20, 20, 1);
    }

}
