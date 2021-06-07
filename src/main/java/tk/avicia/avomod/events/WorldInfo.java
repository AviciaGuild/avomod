package tk.avicia.avomod.events;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import tk.avicia.avomod.Avomod;
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
            try {
                currentWorldString = "Your world " + currentWorld + " : " + Utils.getReadableTime(worldData.getAge(currentWorld).y);
            } catch (NotFound notFound) {
                notFound.printStackTrace();
            }
            Map.Entry<String, JsonElement> newestWorld = worldData.getWorldUpTimeData().get(0);
            String newestWorldString = "Newest world " + newestWorld.getKey() + " : " +
                    Utils.getReadableTime(Integer.parseInt(newestWorld.getValue().getAsJsonObject().get("age").getAsString()));
            FontRenderer fontRenderer = Avomod.getMC().fontRenderer;

            int[] pos = new int[]{screenWidth - fontRenderer.getStringWidth(newestWorldString) - 2, screenHeight / 2 + 88, screenWidth + 2, (screenHeight / 2 + 100) + 10};

            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();

            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(0, 0, 255, 1);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION);
            worldRenderer.pos(new Double(pos[0]), new Double(pos[3]), 0.0).endVertex();
            worldRenderer.pos(new Double(pos[2]), new Double(pos[3]), 0.0).endVertex();
            worldRenderer.pos(new Double(pos[2]), new Double(pos[1]), 0.0).endVertex();
            worldRenderer.pos(new Double(pos[0]), new Double(pos[1]), 0.0).endVertex();
            tessellator.draw();
            GlStateManager.color(1f, 1f, 1f, 1f);

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();

            fontRenderer.drawString(currentWorldString, screenWidth - fontRenderer.getStringWidth(currentWorldString)
                    , screenHeight / 2 + 100, Color.WHITE.getRGB());
            fontRenderer.drawString(newestWorldString, screenWidth - fontRenderer.getStringWidth(newestWorldString)
                    , screenHeight / 2 + 90, Color.WHITE.getRGB());
        }
    }

    public static void updateCurrentWorld() {
        try {
            String name = Avomod.getMC().getConnection().getPlayerInfo(UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af")).getDisplayName().getUnformattedText();
            currentWorld = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        } catch (NullPointerException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public static void updateWorldData() {
        worldData = new WorldUpTime();
    }

}
