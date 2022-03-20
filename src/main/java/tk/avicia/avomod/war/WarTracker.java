package tk.avicia.avomod.war;

import net.minecraft.client.gui.ScaledResolution;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.MultipleElements;
import tk.avicia.avomod.renderer.Rectangle;
import tk.avicia.avomod.renderer.Text;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarTracker {
    public static void warStart(String territoryName, List<String> members) {
        List<String> filteredMembers = members.stream().filter(e -> !e.equals(Avomod.getMC().player.getName())).collect(Collectors.toList());
        WarObject currentWarObject = new WarObject(territoryName, filteredMembers);
        WarTrackerFile.addWar(currentWarObject);
    }

    public static MultipleElements getElementsToDraw() {
        long weeklyWars = WarTrackerFile.getWars(System.currentTimeMillis() - 604800000L);

        String plural = "";
        if (weeklyWars != 1) {
            plural = "s";
        }

        String text = String.format("%s war%s", weeklyWars, plural);
        int rectangleWidth = Avomod.getMC().fontRenderer.getStringWidth(text) + 4;
        int rectangleHeight = 12;
        float scale = 1.5F;

        String locationText = Avomod.getLocation("weeklyWars");
        if (locationText == null) return null;

        final float screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth() / scale - rectangleWidth;
        final float screenHeight = new ScaledResolution(Avomod.getMC()).getScaledHeight() / scale - rectangleHeight;
        float x = (Float.parseFloat(locationText.split(",")[0]) * screenWidth);
        float y = (Float.parseFloat(locationText.split(",")[1]) * screenHeight);

        Rectangle newRectangle = new Rectangle(x, y,
                rectangleWidth, rectangleHeight, scale, new Color(100, 100, 100, 100));
        Text newText = new Text(text, x + 2, y + 2, scale, Color.MAGENTA);

        return new MultipleElements("weeklyWars", scale, Arrays.asList(newRectangle, newText));
    }
}
