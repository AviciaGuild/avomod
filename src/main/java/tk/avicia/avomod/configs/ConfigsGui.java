package tk.avicia.avomod.configs;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiScreen;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;
import java.util.ArrayList;

public class ConfigsGui extends GuiScreen {
    ArrayList<ConfigsSection> sectionList = new ArrayList<>();
    private final int settingLineHeight = 27;
    public ConfigsGui() {
        super();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Makes blur
        this.drawWorldBackground(0);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        this.drawCenteredString(this.fontRenderer, ChatFormatting.BOLD + "Avomod Configs", this.width / 2 + 1, 6, 0x444444);
        this.drawCenteredString(this.fontRenderer, ChatFormatting.BOLD + "Avomod Configs", this.width / 2, 5, 0x1B33CF);

        this.sectionList.forEach((ConfigsSection configsSection) -> {
            int color = 0x777777;
            // Makes the enabled options brighter
            if(configsSection.button.displayString.equals("Enabled")){
                color = 0xFFFFFF;
            }
            // Draws a shadowed string with the opposite color, to make it easier to read depending on the background
            this.drawString(this.fontRenderer, configsSection.title, this.width / 2 - this.width / 4 + 1 , sectionList.indexOf(configsSection) * settingLineHeight + 22, 0xFFFFFF - color);
            // Draws the actual string
            this.drawString(this.fontRenderer, configsSection.title, this.width / 2 - this.width / 4, sectionList.indexOf(configsSection) * settingLineHeight + 21, color);
            Renderer.drawHorizontalLine(this.width / 4 + 3, this.width / 4 * 3 - 5, sectionList.indexOf(configsSection) * settingLineHeight + 38, new Color(255, 255, 255));
        });

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void initGui() {
        this.sectionList = new ArrayList<>();
        for (Config config : Avomod.configsArray) {
            this.addSection(config);
        }
        this.sectionList.forEach((ConfigsSection configsSection) -> {
            this.buttonList.add(configsSection.button);
        });
    }

    public void addSection(Config config) {
        String configValue = Avomod.getConfig(config.configsKey);
        if (!configValue.equals("")) {
            config.defaultValue = configValue;
        }
        this.sectionList.add(new ConfigsSection(config.sectionText, new ConfigsButton(this.sectionList.size(), this.sectionList.size() * settingLineHeight + 15, config.choices, config.defaultValue, this.width), config.configsKey));
    }
}
