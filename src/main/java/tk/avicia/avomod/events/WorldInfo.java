package tk.avicia.avomod.events;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.WorldUpTime;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

public class WorldInfo {
    private static String currentWorld = "";
    private static WorldUpTime worldData;

    public static void draw() {
        final int screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth();
        final int screenHeight = new ScaledResolution(Avomod.getMC()).getScaledHeight();
        if (worldData != null) {
            updateCurrentWorld();
            String currentWorldString = "";
            String newestWorldString = "";
            try {
                Map.Entry<String, JsonElement> newestWorld = worldData.getWorldUpTimeData().get(0);
                newestWorldString = "Newest world " + newestWorld.getKey() + " : " +
                        Utils.getReadableTime(Integer.parseInt(newestWorld.getValue().getAsJsonObject().get("age").getAsString()));
                if (currentWorld.length() > 1) { // Not in streamer mode or lobby
                    currentWorldString = "Your world " + currentWorld + " : " + Utils.getReadableTime(worldData.getAge(currentWorld).y);
                }
            } catch (NoSuchFieldException | NullPointerException e) {
                e.printStackTrace();
            }


            FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
            if (!currentWorldString.equals("")) {
                Renderer.drawRect(new Color(0, 0, 255, 100),
                        screenWidth - fontRenderer.getStringWidth(currentWorldString) - 2, screenHeight / 2f + 100,
                        fontRenderer.getStringWidth(currentWorldString) + 4, 12);
                fontRenderer.drawString(currentWorldString, screenWidth - fontRenderer.getStringWidth(currentWorldString)
                        , screenHeight / 2 + 102, Color.WHITE.getRGB());
            }
            Renderer.drawRect(new Color(0, 0, 255, 100),
                    screenWidth - fontRenderer.getStringWidth(newestWorldString) - 2, screenHeight / 2f + 88,
                    fontRenderer.getStringWidth(newestWorldString) + 4, 12);
            fontRenderer.drawString(newestWorldString, screenWidth - fontRenderer.getStringWidth(newestWorldString)
                    , screenHeight / 2 + 90, Color.WHITE.getRGB());
        }
    }

    public static void updateCurrentWorld() {
        try {
            String name = Avomod.getMC().getConnection().getPlayerInfo(UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af")).getDisplayName().getUnformattedText();
            currentWorld = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        } catch (NullPointerException | StringIndexOutOfBoundsException ignored) {} // If the current world can't be found on tab it gets caught here ignore to prevent console spam in lobby
    }

    public static void updateWorldData() {
        worldData = new WorldUpTime();
    }

}
