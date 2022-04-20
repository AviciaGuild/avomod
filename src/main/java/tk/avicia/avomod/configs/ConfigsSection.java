package tk.avicia.avomod.configs;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.CustomFile;

public class ConfigsSection {
    private final CustomFile customFile;
    private final String configsKey;
    private final String configsCategory;
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
}
