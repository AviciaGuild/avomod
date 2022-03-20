package tk.avicia.avomod.war;

import net.minecraft.client.gui.ScaledResolution;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.Rectangle;
import tk.avicia.avomod.renderer.RectangleText;
import tk.avicia.avomod.renderer.Text;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class WarTracker {
    public static void warStart(String territoryName, List<String> members) {
        List<String> filteredMembers = members.stream().filter(e -> !e.equals(Avomod.getMC().player.getName())).collect(Collectors.toList());
        WarObject currentWarObject = new WarObject(territoryName, filteredMembers);
        WarTrackerFile.addWar(currentWarObject);
    }

    public static RectangleText getRectangleText() {
        long weeklyWars = WarTrackerFile.getWars(System.currentTimeMillis() - 604800000L);

        String plural = "";
        if (weeklyWars != 1) {
            plural = "s";
        }

        String text = String.format("%s war%s", weeklyWars, plural);
        int rectangleWidth = Avomod.getMC().fontRenderer.getStringWidth(text) + 4;
        int rectangleHeight = 12;

        String locationText = Avomod.getLocation("weeklyWars");
        if (locationText == null) return null;

        final float screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth() / 1.5F - rectangleWidth;
        final float screenHeight = new ScaledResolution(Avomod.getMC()).getScaledHeight() / 1.5F - rectangleHeight;
        float x = (Float.parseFloat(locationText.split(",")[0]) * screenWidth);
        float y = (Float.parseFloat(locationText.split(",")[1]) * screenHeight);

        Rectangle newRectangle = new Rectangle(x, y,
                rectangleWidth, rectangleHeight, 1.5F, new Color(100, 100, 100, 100));
        Text newText = new Text(text, x + 2, y + 2, 1.5F, Color.MAGENTA);

        return new RectangleText("weeklyWars", newRectangle, newText);
    }
}
