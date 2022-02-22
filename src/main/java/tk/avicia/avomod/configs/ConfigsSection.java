package tk.avicia.avomod.configs;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.CustomFile;

public class ConfigsSection {
    public ConfigsButton button;
    public ConfigsTextField textField;
    public String title;
    private CustomFile customFile;
    private String configsKey;

    public ConfigsSection(String title, ConfigsButton button, String configsKey) {
        this.title = title;
        this.button = button;
        this.configsKey = configsKey;

        this.button.setConfigsSection(this);
        this.customFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/configs.json");
    }

    public ConfigsSection(String title, ConfigsTextField textField, String configsKey) {
        this.title = title;
        this.textField = textField;
        this.configsKey = configsKey;

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
