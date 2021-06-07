package tk.avicia.avomod.utils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.awt.*;

public class Renderer {
    public static void drawRect(Color color, float x, float y, float width, float height){
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
}
