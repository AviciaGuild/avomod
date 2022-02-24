package tk.avicia.avomod.configs;

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

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
