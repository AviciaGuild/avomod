package tk.avicia.avomod.events;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.ITextComponent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.Element;
import tk.avicia.avomod.renderer.MultipleElements;
import tk.avicia.avomod.renderer.Rectangle;
import tk.avicia.avomod.renderer.Text;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.WorldUpTime;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class WorldInfo {
    private static String currentWorld = "";
    private static WorldUpTime worldData;

    public static MultipleElements getElementsToDraw() {
        if (worldData == null) return null;

        updateCurrentWorld();
        String currentWorldString = "";
        String newestWorldString = "";
        try {
            Map.Entry<String, JsonElement> newestWorld = worldData.getWorldUpTimeData().get(0);
            newestWorldString = "Newest world " + newestWorld.getKey() + ": " +
                    Utils.getReadableTime(Integer.parseInt(newestWorld.getValue().getAsJsonObject().get("age").getAsString()));
            if (currentWorld.length() > 1) { // Not in streamer mode or lobby
                currentWorldString = "Your world " + currentWorld + ": " + Utils.getReadableTime(worldData.getAge(currentWorld).y);
            }
        } catch (NoSuchFieldException | NullPointerException e) {
            e.printStackTrace();
        }

        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        ArrayList<Element> elementsList = new ArrayList<>();

        int rectangleWidth = Math.max(fontRenderer.getStringWidth(currentWorldString) + 4, fontRenderer.getStringWidth(newestWorldString) + 4);
        int rectangleHeight = 12;
        float scale = 1.5F;
        String locationText = Avomod.getLocation("worldInfo");
        if (locationText == null) return null;

        float screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth() / scale - rectangleWidth;
        float screenHeight = new ScaledResolution(Avomod.getMC()).getScaledHeight() / scale - rectangleHeight;
        float x = (Float.parseFloat(locationText.split(",")[0]) * screenWidth);
        float y = (Float.parseFloat(locationText.split(",")[1]) * screenHeight);

        if (!currentWorldString.equals("")) {
            elementsList.add(new Rectangle(x + rectangleWidth - fontRenderer.getStringWidth(currentWorldString) - 4,
                    y + rectangleHeight, fontRenderer.getStringWidth(currentWorldString) + 4, rectangleHeight, scale,
                    new Color(0, 0, 255, 100)));
            elementsList.add(new Text(currentWorldString, x + rectangleWidth - fontRenderer.getStringWidth(currentWorldString) - 2,
                    y + 2 + rectangleHeight, scale, Color.WHITE));
        }

        elementsList.add(new Rectangle(x + rectangleWidth - fontRenderer.getStringWidth(newestWorldString) - 4,
                y, fontRenderer.getStringWidth(newestWorldString) + 4, rectangleHeight, scale,
                new Color(0, 0, 255, 100)));
        elementsList.add(new Text(newestWorldString, x + rectangleWidth - fontRenderer.getStringWidth(newestWorldString) - 2,
                y + 2, scale, Color.WHITE));

        return new MultipleElements("worldInfo", 1F, elementsList);
    }

    public static void updateCurrentWorld() {
        try {
            if (Avomod.getMC().getConnection() == null) return;

            ITextComponent nameFormatted = Avomod.getMC().getConnection().getPlayerInfo(UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af")).getDisplayName();
            if (nameFormatted == null) return;

            String name = nameFormatted.getUnformattedText();
            currentWorld = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        } catch (NullPointerException | StringIndexOutOfBoundsException ignored) {
        } // If the current world can't be found on tab it gets caught here ignore to prevent console spam in lobby
    }

    public static void updateWorldData() {
        worldData = new WorldUpTime();
    }

}
