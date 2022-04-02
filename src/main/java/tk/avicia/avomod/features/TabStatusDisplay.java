package tk.avicia.avomod.features;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.Element;
import tk.avicia.avomod.renderer.MultipleElements;
import tk.avicia.avomod.renderer.Rectangle;
import tk.avicia.avomod.renderer.Text;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

public class TabStatusDisplay {
    public static MultipleElements getElementsToDraw(List<String> stats) {
        final float scale = 1F;
        final int rectangleHeight = 12;
        float y = Utils.getStartY("tabStatusDisplay", stats.size(), scale);
        List<Element> elementsList = new ArrayList<>();

        for (String stat : stats) {
            int rectangleWidth = Avomod.getMC().fontRenderer.getStringWidth(stat) + 4;
            float x = Utils.getStartX("tabStatusDisplay", rectangleWidth, scale);
            elementsList.add(new Rectangle(x, y, rectangleWidth, rectangleHeight, new Color(100, 100, 100, 100)));
            elementsList.add(new Text(stat, x + 2, y + 2, Color.WHITE));
            y += rectangleHeight;
        }

        return new MultipleElements("tabStatusDisplay", scale, elementsList);
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Chat event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("tabStatusDisplay")) return;
        Map<String, Integer> statsCount = new HashMap<>();
        Map<String, String> statsTimer = new HashMap<>();
        try {
            GuiPlayerTabOverlay tabList = Avomod.getMC().ingameGUI.getTabList();
            Field tabListField = null;
            // Checks the parent so that it works with wynntils overlay
            for (Field declaredField : tabList.getClass().getSuperclass().getDeclaredFields()) {
                // The stats are written in the tab footer, and it can only be accessed via reflection
                if (declaredField.getName().equals("footer") || declaredField.getName().equals("field_175255_h")) {
                    tabListField = declaredField;
                }
            }

            for (Field declaredField : tabList.getClass().getDeclaredFields()) {
                // The stats are written in the tab footer, and it can only be accessed via reflection
                if (declaredField.getName().equals("footer") || declaredField.getName().equals("field_175255_h")) {
                    tabListField = declaredField;
                }
            }
            if (tabListField != null) {
                tabListField.setAccessible(true);
                ITextComponent footer = ((ITextComponent) tabListField.get(tabList));
                // footer is null if there are no active effects
                if (footer != null) {
                    for (ITextComponent sibling : footer.getSiblings()) {
                        // Some stats have more than 2 spaces separating them, so make the space even between all of them
                        // and split it on 2 spaces
                        for (String stat : sibling.getUnformattedComponentText().trim().replaceAll("   +", "  ").split("  ")) {
                            if (Arrays.stream(Avomod.statsFromTabToShow).anyMatch(stat::contains)) {
                                String statKey = stat.substring(0, stat.length() - 7);
                                statsCount.put(statKey, statsCount.containsKey(statKey) ? statsCount.get(statKey) + 1 : 1);
                                statsTimer.put(statKey, stat.substring(stat.length() - 7));
                            }
                        }
                    }
                }
            }
            List<String> stats = new ArrayList<>();

            for (Map.Entry<String, Integer> stat : statsCount.entrySet()) {
                stats.add(stat.getKey() + statsTimer.get(stat.getKey()) + TextFormatting.AQUA + " x" + stat.getValue());
            }

            getElementsToDraw(stats).draw();
        } catch (NullPointerException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
