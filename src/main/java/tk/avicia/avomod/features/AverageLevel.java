package tk.avicia.avomod.features;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AverageLevel {
    public static boolean isChestNew = true;
    private static int levelledItemCount = 0;
    private static int totalLevelItems = 0;
    private boolean guiJustOpened;

    public static void execute(GuiScreenEvent.BackgroundDrawnEvent event, InventoryBasic lowerInventory) {
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
                        String itemLevelUnformatted = TextFormatting.getTextWithoutFormattingCodes(itemLevel.get());
                        if (itemLevelUnformatted == null) return;

                        try {
                            Pattern numberPattern = Pattern.compile("(^[0-9]+$)", Pattern.CASE_INSENSITIVE);
                            if (numberPattern.matcher(itemLevelUnformatted.split(": ")[1]).find()) {
                                totalLevelItems += Integer.parseInt(itemLevelUnformatted.split(": ")[1]);
                            } else if (itemLevelUnformatted.split(": ")[1].split("-").length == 2) {
                                String[] levelRange = itemLevelUnformatted.split(": ")[1].split("-");
                                totalLevelItems += (Integer.parseInt(levelRange[0])
                                        + Integer.parseInt(levelRange[1])) / 2;
                            }
                            levelledItemCount += 1;
                        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        int screenWidth = event.getGui().width;
        int screenHeight = event.getGui().height;
        int averageLevel = 0;
        if (levelledItemCount > 0) {
            averageLevel = (totalLevelItems / levelledItemCount);
        }
        String displayString = "Average level of items in chest: " + averageLevel;
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        fontRenderer.drawString(displayString, screenWidth / 2 - fontRenderer.getStringWidth(displayString) / 2
                , screenHeight / 2 - 100, Color.WHITE.getRGB());
    }

    @SubscribeEvent
    public void guiJustOpened(GuiOpenEvent event) {
        guiJustOpened = true;
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        guiJustOpened = false;
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null || event.getGui() == null) return;

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (containerName.contains("Loot Chest")) {
                AverageLevel.execute(event, lowerInventory);
            }
        }
    }

    @SubscribeEvent
    public void guiInitialize(GuiScreenEvent.InitGuiEvent.Post event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null || event.getGui() == null) return;

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (containerName.contains("Loot Chest")) {
                ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
                int screenHeight = scaledResolution.getScaledHeight();
                int scaleFactor = scaledResolution.getScaleFactor();
                int scaledMouseX = Mouse.getX() / scaleFactor;
                int scaledMouseY = Mouse.getY() / scaleFactor;

                if (guiJustOpened && scaledMouseY != Math.ceil(screenHeight / 2.0)) {
                    Mouse.setCursorPosition(scaledMouseX * scaleFactor, ((int) Math.ceil(screenHeight / 2.0)) * scaleFactor);
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null) return;

        Container openContainer = Avomod.getMC().player.openContainer;
        if (!(openContainer instanceof ContainerChest)) {
            isChestNew = true;
        }
    }
}
