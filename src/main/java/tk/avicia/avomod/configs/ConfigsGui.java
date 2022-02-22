package tk.avicia.avomod.configs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ConfigsGui extends GuiScreen {
    private final int settingLineHeight = 27;
    private final int startingHeight = 30;
    ArrayList<ConfigsSection> sectionList, totalSectionsList = new ArrayList<>();
    ArrayList<ConfigsTextField> textFieldsList = new ArrayList<>();

    public ConfigsGui() {
        super();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Makes blur
        this.drawWorldBackground(0);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRenderer, "Avomod Configs", this.width / 4 + 1, 6, 0x444444);
        this.drawCenteredString(this.fontRenderer, "Avomod Configs", this.width / 4, 5, 0x1B33CF);
        GlStateManager.popMatrix();

        for (ConfigsSection configsSection : this.sectionList) {
            int y = sectionList.indexOf(configsSection) * settingLineHeight;

            int color = 0x777777;
            // Makes the enabled options brighter
            if (configsSection.button == null || configsSection.button.displayString.equals("Enabled")) {
                color = 0xFFFFFF;
            }

            // Draws a shadowed string with the opposite color, to make it easier to read depending on the background
            this.drawString(this.fontRenderer, configsSection.title, this.width / 2 - this.width / 4 + 1, y + 7 + startingHeight, 0xFFFFFF - color);
            // Draws the actual string
            this.drawString(this.fontRenderer, configsSection.title, this.width / 2 - this.width / 4, y + 6 + startingHeight, color);
            Renderer.drawHorizontalLine(this.width / 4 + 3, this.width / 4 * 3 - 5, y + 23 + startingHeight, new Color(255, 255, 255));
        }

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.drawTextBox();
        }

        try {
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void initGui() {
        this.sectionList = new ArrayList<>();
        this.totalSectionsList = new ArrayList<>();
        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();

        for (Config config : Avomod.configsArray) {
            this.addSection(config);
        }
        this.sectionList.forEach((ConfigsSection configsSection) -> {
            if (configsSection.button != null) {
                this.buttonList.add(configsSection.button);
            }

            if (configsSection.textField != null) {
                this.textFieldsList.add(configsSection.textField);
            }
        });
    }

    @Override
    public void onResize(Minecraft mineIn, int w, int h) {
        super.onResize(mineIn, w, h);

        this.initGui();
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int scrollAmount = Mouse.getDWheel() / 120;

        if (scrollAmount != 0) {
            try {
                this.scroll(scrollAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onGuiClosed() {
        for (ConfigsTextField textField : this.textFieldsList) {
            if (Pattern.matches(textField.finalValidation, textField.getText())) {
                textField.configsSection.updateConfigs(textField.getText());
            }
        }

        super.onGuiClosed();
    }

    public void scroll(int amount) {
        if (this.totalSectionsList.size() == this.sectionList.size()) return;

        int totalAllowed = (int) Math.floor(this.height / settingLineHeight);
        int startingIndex = this.totalSectionsList.indexOf(this.sectionList.get(0));
        if (amount < 0 && startingIndex + totalAllowed < this.totalSectionsList.size()) {
            startingIndex++;
        } else if (amount > 0 && startingIndex > 0) {
            startingIndex--;
        }

        this.sectionList = new ArrayList<>();
        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();

        for (int i = startingIndex; i < startingIndex + totalAllowed; i++) {
            this.sectionList.add(this.totalSectionsList.get(i));
        }

        for (ConfigsSection configsSection : this.sectionList) {
            if (configsSection.button != null) {
                configsSection.button.y = this.sectionList.indexOf(configsSection) * settingLineHeight + 15;
                this.buttonList.add(configsSection.button);
            }

            if (configsSection.textField != null) {
                configsSection.textField.y = this.sectionList.indexOf(configsSection) * settingLineHeight + 21;
                this.textFieldsList.add(configsSection.textField);
            }
        }
    }

    public void addSection(Config config) {
        String configValue = Avomod.getConfig(config.configsKey);
        if (!configValue.equals("")) {
            config.defaultValue = configValue;
        }

        ConfigsSection sectionToAdd;

        if (config instanceof ConfigToggle) {
            ConfigsButton configButton = new ConfigsButton(this.sectionList.size(), this.sectionList.size() * settingLineHeight + startingHeight, new String[]{"Enabled", "Disabled"}, config.defaultValue, this.width);
            sectionToAdd = new ConfigsSection(config.sectionText, configButton, config.configsKey);
        } else {
            ConfigsTextField textField = new ConfigsTextField(this.sectionList.size(), ((ConfigInput) config).allowedInputs, ((ConfigInput) config).finalValidation, Avomod.getMC().fontRenderer, this.sectionList.size() * settingLineHeight + startingHeight + 2, 80, 16, this.width);
            textField.setText(config.defaultValue);
            if (((ConfigInput) config).maxLength != 0) {
                textField.setMaxStringLength(((ConfigInput) config).maxLength);
            }

            sectionToAdd = new ConfigsSection(config.sectionText, textField, config.configsKey);
        }

        if ((this.sectionList.size() + 1) * settingLineHeight < this.height) {
            this.sectionList.add(sectionToAdd);
        }

        this.totalSectionsList.add(sectionToAdd);
    }
}
