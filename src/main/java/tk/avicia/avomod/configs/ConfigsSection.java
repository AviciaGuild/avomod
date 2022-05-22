package tk.avicia.avomod.configs;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.CustomFile;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class ConfigsSection {
    public final String configsCategory;
    private final CustomFile customFile;
    private final String configsKey;
    public ConfigsButton button;
    public ConfigsTextField textField;
    public String title;

    public ConfigsSection(String configsCategory, String title, ConfigsButton button, String configsKey) {
        this.title = title;
        this.button = button;
        this.configsKey = configsKey;
        this.configsCategory = configsCategory;

        this.button.setConfigsSection(this);
        this.customFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/configs.json");
    }

    public ConfigsSection(String configsCategory, String title, ConfigsTextField textField, String configsKey) {
        this.title = title;
        this.textField = textField;
        this.configsKey = configsKey;
        this.configsCategory = configsCategory;

        this.textField.setConfigsSection(this);
        this.customFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/configs.json");
    }

    public void updateConfigs(String newValue) {
        JsonObject configsJson = this.customFile.readJson();
        configsJson.addProperty(this.configsKey, newValue);

        if (this.configsKey.equals("autoStream") && newValue.equals("Disabled")) {
            Avomod.getMC().player.sendChatMessage("/stream");
        }

        Avomod.configs = configsJson;
        this.customFile.writeJson(configsJson);
    }

    public void drawSection(ConfigsGui configsGui, int x, int y, boolean drawLine) {
        configsGui.drawString(configsGui.mc.fontRenderer, title, x, y, 0xFFFFFF);

        if (button != null) {
            button.x = x;
            button.y = y + configsGui.settingHeight - 5;
            configsGui.addButton(button);
        }
        if (textField != null) {
            textField.x = x + 5;
            textField.y = y + configsGui.settingHeight;
            configsGui.addTextField(textField);
        }

        if (drawLine) {
            Renderer.drawHorizontalLine(x, configsGui.width / 16 * 15, y + configsGui.settingLineHeight + configsGui.settingHeight - 5, Color.GRAY);
        }
    }
}
