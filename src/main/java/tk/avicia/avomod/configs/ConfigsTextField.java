package tk.avicia.avomod.configs;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;
import java.util.regex.Pattern;

public class ConfigsTextField extends GuiTextField {
    public ConfigsSection configsSection;
    public String allowedInputs, finalValidation;
    public Color borderColor;
    private ConfigsGui cfgui;

    public ConfigsTextField(int componentId, String allowedInputs, String finalValidation, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, ConfigsGui cfgui) {
        super(componentId, fontrendererObj, x + 4, y + 4, par5Width, par6Height);
        this.allowedInputs = allowedInputs;
        this.finalValidation = finalValidation;
        this.cfgui = cfgui;

        this.setEnableBackgroundDrawing(false);
    }

    @Override
    public void drawTextBox() {
        if (Pattern.matches(this.finalValidation, this.getText())) {
            borderColor = new Color(0, 255, 0, 200);
        } else {
            borderColor = new Color(255, 0, 0, 200);
        }

        int modifiedX = this.x - 4;
        int modifiedY = this.y - 4;
        drawRect(modifiedX - 1, modifiedY - 1, modifiedX + this.width + 1, modifiedY + this.height + 1, borderColor.getRGB());
        drawRect(modifiedX, modifiedY, modifiedX + this.width, modifiedY + this.height, -16777216);

        super.drawTextBox();
    }

    @Override
    public void setFocused(boolean isFocusedIn) {
        boolean oldFocused = this.isFocused();
        super.setFocused(isFocusedIn);

        if (oldFocused == isFocusedIn) return;

        cfgui.textFieldIsFocused = cfgui.textFieldsList.stream().anyMatch(GuiTextField::isFocused);
        if (isFocusedIn) {
            cfgui.searchTextField.setFocused(false);
            cfgui.textFieldIsFocused = true;
        } else if (!cfgui.textFieldIsFocused) {
            cfgui.searchTextField.setFocused(true);
        }
    }

    @Override
    public void writeText(String text) {
        if (text.length() == 0 || Pattern.matches(this.allowedInputs, text)) {
            super.writeText(text);
        }
    }

    public void setConfigsSection(ConfigsSection configsSection) {
        this.configsSection = configsSection;
    }
}
