package tk.avicia.avomod.utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import tk.avicia.avomod.Avomod;

import java.awt.*;

public class Renderer {
    public static void drawRect(Color color, float x, float y, float width, float height) {
        float[] pos = new float[]{x, y, x + width, y + height};

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(pos[0], pos[3], 0.0).endVertex();
        worldRenderer.pos(pos[2], pos[3], 0.0).endVertex();
        worldRenderer.pos(pos[2], pos[1], 0.0).endVertex();
        worldRenderer.pos(pos[0], pos[1], 0.0).endVertex();
        tessellator.draw();
        // set it back to normal
        GlStateManager.color(1f, 1f, 1f, 1f);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    public static void drawString(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        fontRenderer.drawString(text, x, y, color.getRGB());
    }

    public static void drawStringWithShadow(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        fontRenderer.drawString(text, x + 1, y + 1, Utils.getContrastColor(color).getRGB());
        fontRenderer.drawString(text, x, y, color.getRGB());
    }

    public static void drawStringWithShadow(String text, int x, int y, Color color, int maxWidth) {
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        String newText = text;
        if (!text.equals(fontRenderer.trimStringToWidth(text, maxWidth))) {
            newText = fontRenderer.trimStringToWidth(text, maxWidth - 3) + "..";
        }
        fontRenderer.drawString(newText, x + 1, y + 1, Utils.getContrastColor(color).getRGB());
        fontRenderer.drawString(newText, x, y, color.getRGB());
    }

    public static void drawCenteredString(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color.getRGB());
    }
    public static void drawCenteredShadowedString(String text, int x, int y, Color color) {
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        fontRenderer.drawString(text, x - (fontRenderer.getStringWidth(text) / 2) + 1, y + 1, Utils.getContrastColor(color).getRGB());
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color.getRGB());
    }

    public static void drawHorizontalLine(int startX, int endX, int y, Color color) {
        drawRect(color, startX, y, endX - startX, 1);
    }
}
