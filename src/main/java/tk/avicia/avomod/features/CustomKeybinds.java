package tk.avicia.avomod.features;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.CustomFile;
import tk.avicia.avomod.core.structures.Keybind;
import tk.avicia.avomod.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class CustomKeybinds {
    public static Map<String, Keybind> keybinds = new HashMap<>();

    public static JsonObject getSettings() {
        CustomFile avomodKeybinds = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/keybinds.json");

        return avomodKeybinds.readJson();
    }

    public static void setSettings(String key, String command) {
        JsonObject currentSettingsJson = getSettings();
        currentSettingsJson.addProperty(key, command);

        CustomFile avomodKeybinds = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/keybinds.json");
        avomodKeybinds.writeJson(currentSettingsJson);

        updateKeybinds();
    }

    public static void removeSetting(String key) {
        JsonObject currentSettingsJson = getSettings();
        currentSettingsJson.remove(key);

        CustomFile avomodKeybinds = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/keybinds.json");
        avomodKeybinds.writeJson(currentSettingsJson);

        updateKeybinds();
    }

    public static void updateKeybinds() {
        GuildBankKeybind.init();

        JsonObject settings = CustomKeybinds.getSettings();
        if (settings != null) {
            for (Map.Entry<String, JsonElement> e : settings.entrySet()) {
                if (!keybinds.containsKey(e.getKey()) || !keybinds.get(e.getKey()).getCommandToRun().equals(e.getValue().getAsString())) {
                    Keybind keybind = new Keybind(Utils.firstLetterCapital(e.getValue().getAsString()), Keyboard.getKeyIndex(e.getKey().toUpperCase()), "Avomod", e.getValue().getAsString());
                    keybinds.put(e.getKey(), keybind);

                    ClientRegistry.registerKeyBinding(keybind);
                }
            }
        }

        if (settings == null) return;
        keybinds.entrySet().removeIf(entry -> !settings.has(entry.getKey()));
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        for (Keybind keybind : keybinds.values()) {
            if (keybind.isPressed()) {
                Avomod.getMC().player.sendChatMessage("/" + keybind.getCommandToRun());
            }
        }
    }
}
