package tk.avicia.avomod.configs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.Arrays;

public class ConfigsButton extends GuiButton {
    public String[] choices;
    private ConfigsSection configsSection;
    private int currentIndex;

    public ConfigsButton(int buttonId, int x, int y, int width, String[] choices, String defaultValue) {
        super(buttonId, x, y, width, 20, defaultValue);
        this.choices = choices;
        this.currentIndex = Arrays.asList(choices).indexOf(defaultValue);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.currentIndex++;
        if (this.currentIndex == choices.length) {
            this.currentIndex = 0;
        }
        this.displayString = this.choices[this.currentIndex];

        if (this.configsSection != null) {
            this.configsSection.updateConfigs(this.displayString);
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        drawRect(x, y, x + width, y + 1, 0xFFFFFFFF);
        drawRect(x + width - 1, y, x + width, y + 20, 0xFFFFFFFF);
        drawRect(x, y, x + 1, y + 20, 0xFFFFFFFF);
        drawRect(x, y + 19, x + width, y + 20, 0xFFFFFFFF);
        int color = 0xFF8888;
        if (choices[currentIndex].equals("Enabled")) color = 0x88FF88;
        this.drawCenteredString(mc.fontRenderer, choices[currentIndex], x + width / 2, y + 6, color);
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
