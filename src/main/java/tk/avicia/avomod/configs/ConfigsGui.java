package tk.avicia.avomod.configs;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiScreen;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;
import java.util.ArrayList;

public class ConfigsGui extends GuiScreen {
    ArrayList<ConfigsSection> sectionList = new ArrayList<>();

    public ConfigsGui() {
        super();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRenderer, ChatFormatting.BOLD + "Avomod Configs", this.width / 2, 5, 0x1B33CF);

        this.sectionList.forEach((ConfigsSection configsSection) -> {
            this.drawString(this.fontRenderer, configsSection.title, 5, sectionList.indexOf(configsSection) * 26 + 25, 0xFFFFFF);
            Renderer.drawHorizontalLine(0, this.width, sectionList.indexOf(configsSection) * 26 + 38, new Color(255, 255, 255));
        });

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void initGui() {
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
        this.sectionList.add(new ConfigsSection(config.sectionText, new ConfigsButton(this.sectionList.size(), this.sectionList.size() * 26 + 15, config.choices, config.defaultValue, this.width), config.configsKey));
    }
}
