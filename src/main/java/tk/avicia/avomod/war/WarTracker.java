package tk.avicia.avomod.war;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class WarTracker {
    public static void warStart(String territoryName, List<String> members) {
        List<String> filteredMembers = members.stream().filter(e -> !e.equals(Avomod.getMC().player.getName())).collect(Collectors.toList());
        WarObject currentWarObject = new WarObject(territoryName, filteredMembers);
        WarTrackerFile.addWar(currentWarObject);
    }

    public static void draw() {
        long weeklyWars = WarTrackerFile.getWars(System.currentTimeMillis() - 604800000L);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.5F, 1.5F, 1.5F);
        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());

        String plural = "";
        if (weeklyWars != 1) {
            plural = "s";
        }

        int stringWidth = Avomod.getMC().fontRenderer.getStringWidth(weeklyWars + " war" + plural);
        int x = (int) (scaledResolution.getScaledWidth() / 1.5) - (stringWidth + 10);
        int y = (int) (scaledResolution.getScaledHeight() / 1.5) - 15;

        Renderer.drawRect(new Color(100, 100, 100, 100), x - 2, y - 2, stringWidth + 4, 12);
        Renderer.drawString(weeklyWars + " war" + plural, x, y, Color.MAGENTA);

        GlStateManager.popMatrix();
    }
}
