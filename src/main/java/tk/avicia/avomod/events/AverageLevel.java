package tk.avicia.avomod.events;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import tk.avicia.avomod.Avomod;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class AverageLevel {
    public static boolean isChestNew = true;
    private static int levelledItemCount = 0;
    private static int totalLevelItems = 0;

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
        }

        int screenWidth = event.getGui().width;
        int screenHeight = event.getGui().height;
        String displayString = "Average level of items in chest: " + (totalLevelItems / levelledItemCount);
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        fontRenderer.drawString(displayString, screenWidth / 2 - fontRenderer.getStringWidth(displayString) / 2
                , screenHeight / 2 - 100, Color.WHITE.getRGB());
    }
}
