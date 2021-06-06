package tk.avicia.avomod.settings;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.CustomFile;

public class KeybindSettings {
    private static String[] configsList = new String[]{"keybinds"};

    public static JsonObject getSettings() {
        CustomFile avomodKeybinds = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/keybinds.json");

        return avomodKeybinds.readJson();
    }

    public static void setSettings(String key, String command) {
        JsonObject currentSettingsJson = getSettings();
        currentSettingsJson.addProperty(key, command);

        CustomFile avomodKeybinds = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/keybinds.json");
        avomodKeybinds.writeJson(currentSettingsJson);

        Avomod.updateKeybinds();
    }

    public static void removeSetting(String key) {
        JsonObject currentSettingsJson = getSettings();
        currentSettingsJson.remove(key);

        CustomFile avomodKeybinds = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/keybinds.json");
        avomodKeybinds.writeJson(currentSettingsJson);

        Avomod.updateKeybinds();
    }
}
