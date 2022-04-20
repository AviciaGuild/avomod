package tk.avicia.avomod.core.structures.render;

import net.minecraft.client.renderer.GlStateManager;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class TextWithShadow extends Text {
    public TextWithShadow(String text, float x, float y, float scale, Color color) {
        super(text, x, y, scale, color);
    }

    public TextWithShadow(String text, float x, float y, Color color) {
        super(text, x, y, color);
    }

    @Override
    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        Renderer.drawStringWithShadow(text, (int) x, (int) y, color);
        GlStateManager.popMatrix();
    }
}
