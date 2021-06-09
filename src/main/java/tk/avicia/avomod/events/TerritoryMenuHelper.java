package tk.avicia.avomod.events;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.utils.Renderer;
import tk.avicia.avomod.utils.Tuple;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TerritoryMenuHelper {
    private static final float scale = 0.8f;
    private static TerritoryData territoryData = new TerritoryData();
    private static List<String> oldTerritories = new ArrayList<>();
    private static List<Tuple<Integer, Integer>> disconnectedTerrSlotCoordinates = new ArrayList<>();

    public static void execute(InventoryBasic lowerInventory) {
        List<String> territories = new ArrayList<>();
        List<Integer> territorySlots = new ArrayList<>();
        String headQuarters = "";
        for (int i = 0; i < lowerInventory.getSizeInventory(); i++) {
            ItemStack itemStack = lowerInventory.getStackInSlot(i);
            String name = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName());
            if (!name.equals("Air")) {
                if (!name.equals("Back")) {
                    territories.add(name.replace(" (HQ)", ""));
                    territorySlots.add(i);
                    if (name.contains("(HQ)")) {
                        headQuarters = name.replace(" (HQ)", "");
                    }
                }
            }
        }
        // Only changes the way it's supposed to be displayed if the inventory changes, so as not to lag
        if (!oldTerritories.equals(territories)) {
            disconnectedTerrSlotCoordinates = new ArrayList<>();
            for (String ownedTerritory : territories) {
                if (!headQuarters.equals("")) {
                    Tuple<Integer, Integer> coordinates = Utils.getTopLeftCornerOfSlot(territorySlots.get(territories.indexOf(ownedTerritory)));
                    if (!territoryData.haveConnections(headQuarters, ownedTerritory, territories)) {
                        if (coordinates != null) {
                            disconnectedTerrSlotCoordinates.add(coordinates);
                        }
                    }
                }
            }
        }
        for (Tuple<Integer, Integer> territorySlot : disconnectedTerrSlotCoordinates) {
            if (!ToolTipState.areCoordinatesOverlappingTooltip(territorySlot)) {
                Renderer.drawRect(new Color(255, 0, 0, 150), territorySlot.x + 2, territorySlot.y + 3, 14, 14);
            }
        }
        // draws the text over the boxes
        // Above everything in the chest
        GlStateManager.translate(0f, 0f, 1000f);
        // Makes the text smaller, and imo easier to read, since there is too much text
        GlStateManager.scale(scale, scale, 1f);
        for (String ownedTerritory : territories) {
            Tuple<Integer, Integer> coordinates = Utils.getTopLeftCornerOfSlot(territorySlots.get(territories.indexOf(ownedTerritory)));
            if (coordinates != null) {
                int coordinateX = (int) (coordinates.x / scale);
                int coordinateY = (int) (coordinates.y / scale);

//                    Renderer.drawRect(new Color(0,0,255,100), coordinates.x + 2, coordinates.y + 3, 14, 14);
                StringBuilder initials = new StringBuilder();
                for (String s : ownedTerritory.split(" ")) {
                    initials.append(s.charAt(0));
                }
                if (!ToolTipState.isTooltipRendering) {
                    Renderer.drawStringWithShadow(initials.toString(), coordinateX + 2, coordinateY + 2, Color.ORANGE, 20);
                } else {
                    // Doesn't render text over tooltips, so the tooltips are still readable
                    if (!ToolTipState.areCoordinatesOverlappingTooltip(coordinates)) {
                        Renderer.drawStringWithShadow(initials.toString(), coordinateX + 2, coordinateY + 2, Color.ORANGE, 20);
                    }
                }
            }
        }
        oldTerritories = territories;
        GlStateManager.translate(0f, 0f, 0f);
        GlStateManager.scale(1f, 1f, 1f);
    }
}
