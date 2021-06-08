package tk.avicia.avomod.events;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.utils.Renderer;
import tk.avicia.avomod.utils.Tuple;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;

public class TerritoryMenuHelper {
    private static final float scale = 0.8f;
    public static void execute(InventoryBasic lowerInventory) {
        // Above everything in the chest
        GlStateManager.translate(0f, 0f, 1000f);
        // Makes the text smaller, and imo easier to read, since there is too much text
        GlStateManager.scale(scale, scale, 1f);
        for (int i = 0; i < lowerInventory.getSizeInventory(); i++) {
            ItemStack itemStack = lowerInventory.getStackInSlot(i);
            String name = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName());
            if (!name.equals("Air")) {
                Tuple<Integer, Integer> coordinates = Utils.getTopLeftCornerOfSlot(i);
                if (coordinates != null) {
                    int coordinateX = (int) (coordinates.x / scale);
                    int coordinateY = (int) (coordinates.y / scale);
                    StringBuilder initials = new StringBuilder();
                    for (String s : name.split(" ")) {
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
//                    Renderer.drawRect(new Color(0,0,255,100), coordinates.x + 2, coordinates.y + 3, 14, 14);
                }
            }
        }
        GlStateManager.translate(0f, 0f, 0f);
        GlStateManager.scale(1f, 1f, 1f);
    }
}
