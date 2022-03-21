package tk.avicia.avomod.renderer;

import net.minecraft.client.renderer.GlStateManager;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class Text extends Element {
    protected String text;

    public Text(String text, float x, float y, float scale, Color color) {
        super(x, y, scale, color);
        this.text = text;
    }

    public Text(String text, float x, float y, Color color) {
        this(text, x, y, 1F, color);
    }

    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        Renderer.drawString(text, (int) x, (int) y, color);
        GlStateManager.popMatrix();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
