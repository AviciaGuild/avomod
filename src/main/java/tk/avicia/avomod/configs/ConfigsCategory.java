package tk.avicia.avomod.configs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class ConfigsCategory extends GuiButton {
    public String title;
    private int x;
    private int y;
    public boolean enabled;
    private ConfigsGui cfgui;

    public ConfigsCategory(int buttonId, int x, int y, String title) {
        super(buttonId, x, y, 100, 20, title);
        this.title = title;
        this.x = x;
        this.y = y;
        this.enabled = false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        cfgui.setCategory(title);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (enabled) {
            drawRect(x, y, x + 100, y + 1, 0xFFFFFFFF);
            drawRect(x + 99, y, x + 100, y + 20, 0xFFFFFFFF);
            drawRect(x, y, x + 1, y + 20, 0xFFFFFFFF);
            drawRect(x, y + 19, x + 100, y + 20, 0xFFFFFFFF);
        }
        int color = 0xBBBBBB;
        if (enabled) color = 0xFFFFFF;
        this.drawCenteredString(mc.fontRenderer, title, x + 50, y + 6, color);
    }

    public void setCFGUI(ConfigsGui cfgui) {this.cfgui = cfgui;}
}