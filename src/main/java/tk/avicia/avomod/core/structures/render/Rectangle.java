package tk.avicia.avomod.core.structures.render;

import net.minecraft.client.renderer.GlStateManager;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class Rectangle extends Element {
    private float width, height;

    public Rectangle(float x, float y, float width, float height, float scale, Color color) {
        super(x, y, scale, color);
        this.width = width;
        this.height = height;
    }

    public Rectangle(float x, float y, float width, float height, Color color) {
        this(x, y, width, height, 1F, color);
    }

    public void draw() {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        Renderer.drawRect(color, (int) x, (int) y, width, height);
        GlStateManager.popMatrix();
    }

    public float getRightEdge() {
        return (getX() + getWidth()) * getScale();
    }

    public float getBottomEdge() {
        return (getY() + getHeight()) * getScale();
    }

    public boolean inRectangle(int mouseX, int mouseY) {
        return mouseX >= getLeftEdge() && mouseX <= getRightEdge() &&
                mouseY >= getTopEdge() && mouseY <= getBottomEdge();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
