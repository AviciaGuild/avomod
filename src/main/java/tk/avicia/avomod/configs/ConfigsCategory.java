package tk.avicia.avomod.configs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class ConfigsCategory extends GuiButton {
    public String title;
    public boolean enabled;
    public boolean hasSearchItem;
    private int x;
    private int y;
    private ConfigsGui cfgui;

    public ConfigsCategory(int buttonId, int x, int y, String title, ConfigsGui cfgui) {
        super(buttonId, x, y, 100, 20, title);
        this.title = title;
        this.x = x;
        this.y = y;
        this.enabled = false;

        this.cfgui = cfgui;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        cfgui.setCategory(title);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        int color = 0xBBBBBB;

        if (hasSearchItem) {
            int width = mc.fontRenderer.getStringWidth(title);
            Renderer.drawHorizontalLine(x + 48 - width / 2, x + 52 + width / 2, y + 16, Color.WHITE);

            color = 0xFFFFFF;
        }

        if (enabled) {
            drawRect(x, y, x + 100, y + 1, 0xFFFFFFFF);
            drawRect(x + 99, y, x + 100, y + 20, 0xFFFFFFFF);
            drawRect(x, y, x + 1, y + 20, 0xFFFFFFFF);
            drawRect(x, y + 19, x + 100, y + 20, 0xFFFFFFFF);

            color = 0xFFFFFF;
        }

        this.drawCenteredString(mc.fontRenderer, title, x + 50, y + 6, color);
    }
}