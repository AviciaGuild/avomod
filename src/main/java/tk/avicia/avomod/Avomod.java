package tk.avicia.avomod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;
import tk.avicia.avomod.commands.AvomodCommand;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.commands.subcommands.*;
import tk.avicia.avomod.configs.Config;
import tk.avicia.avomod.events.EventHandlerClass;
import tk.avicia.avomod.events.WorldInfo;
import tk.avicia.avomod.settings.KeybindSettings;
import tk.avicia.avomod.utils.CustomFile;
import tk.avicia.avomod.utils.Keybind;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.OnlinePlayers;
import tk.avicia.avomod.webapi.TerritoryDataApi;
import tk.avicia.avomod.webapi.UpdateChecker;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = Avomod.MODID, name = Avomod.NAME, version = Avomod.VERSION)
public class Avomod {
    public static final String MODID = "avomod";
    public static final String NAME = "avomod";
    public static final String VERSION = "1.2";
    public static Map<String, Command> commands = new HashMap<String, Command>() {{
        put("help", new HelpCommand());
        put("chestcount", new ChestCountCommand());
        put("aliases", new AliasesCommand());
        put("find", new FindCommand());
        put("playerguild", new PlayerGuildCommand());
        put("onlinemembers", new OnlineMembersCommand());
        put("up", new UpCommand());
        put("age", new AgeCommand());
        put("lastseen", new LastSeenCommand());
        put("playercount", new PlayerCountCommand());
        put("count", new CountCommand());
        put("keybinds", new KeybindsCommand());
        put("configs", new ConfigsCommand());
    }};
    public static Map<String, Command> aliases = new HashMap<>();
    public static Map<String, Keybind> keybinds = new HashMap<>();
    public static GuiScreen guiToDraw = null;
    public static JsonObject configs = null;
    public static Config[] configsArray = new Config[]{
            new Config("Autojoin wynncraft", new String[]{"Enabled", "Disabled"}, "Disabled", "autojoinWynncraft"),
            new Config("Prevent moving armor/accessories", new String[]{"Enabled", "Disabled"}, "Disabled", "disableMovingArmor"),
            new Config("Filter out bank messages", new String[]{"Enabled", "Disabled"}, "Disabled", "filterBankMessages"),
            new Config("Reveal nicknames", new String[]{"Enabled", "Disabled"}, "Enabled", "revealNicks"),
            new Config("Auto skip quest dialogue", new String[]{"Enabled", "Disabled"}, "Disabled", "skipDialogue"),
            new Config("Filter out all resource messages", new String[]{"Enabled", "Disabled"}, "Disabled", "filterResourceMessages"),
            new Config("Custom attack timers display", new String[]{"Enabled", "Disabled"}, "Enabled", "attacksMenu"),
            new Config("Green beacon at soonest war", new String[]{"Enabled", "Disabled"}, "Enabled", "greenBeacon"),
            new Config("Say territory defense in chat", new String[]{"Enabled", "Disabled"}, "Enabled", "terrDefenseInChat")
    };
    public static TerritoryDataApi territoryData;
    public static OnlinePlayers onlinePlayers;

    public static Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    public static void updateKeybinds() {
        JsonObject settings = KeybindSettings.getSettings();
        if (settings != null) {
            for (Map.Entry<String, JsonElement> e : settings.entrySet()) {
                if (!keybinds.containsKey(e.getKey()) || !keybinds.get(e.getKey()).getCommandToRun().equals(e.getValue().getAsString())) {
                    Keybind keybind = new Keybind(Utils.firstLetterCapital(e.getValue().getAsString()), Keyboard.getKeyIndex(e.getKey().toUpperCase()), "Avomod", e.getValue().getAsString());
                    keybinds.put(e.getKey(), keybind);

                    ClientRegistry.registerKeyBinding(keybind);
                }
            }
        }

        keybinds.entrySet().removeIf(entry -> !settings.has(entry.getKey()));
    }

    public static String getConfig(String configKey) {
        JsonElement configElement = Avomod.configs.get(configKey);

        if (configElement == null || configElement.isJsonNull()) {
            return "";
        } else {
            return configElement.getAsString();
        }
    }

    public static boolean getConfigBoolean(String configKey) {
        String configValue = Avomod.getConfig(configKey);

        return configValue.equals("Enabled");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.initializeConfigs();
        UpdateChecker.checkUpdate();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Avomod.getConfigBoolean("autojoinWynncraft")) {
            FMLClientHandler.instance().connectToServerAtStartup("play.wynncraft.com", 25565);
            Avomod.getMC().setServerData(new ServerData("Wynncraft", "play.wynncraft.com", false));
        }

        Thread thread = new Thread(() -> {
            do {
                onlinePlayers = new OnlinePlayers();
                WorldInfo.updateWorldData();
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        });
        thread.start();

        territoryData = new TerritoryDataApi();

        MinecraftForge.EVENT_BUS.register(new EventHandlerClass());
        ClientCommandHandler.instance.registerCommand(new AvomodCommand());

        this.initializeAliases();
        updateKeybinds();
    }

    private void initializeConfigs() {
        CustomFile configsFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/configs.json");
        JsonObject configsJson = configsFile.readJson();
        boolean changed = false;

        for (Config config : this.configsArray) {
            JsonElement configElement = configsJson.get(config.configsKey);

            if (configElement == null || configElement.isJsonNull()) {
                configsJson.addProperty(config.configsKey, config.defaultValue);
                changed = true;
            }
        }

        if (changed) {
            configsFile.writeJson(configsJson);
        }

        Avomod.configs = configsJson;
    }

    private void initializeAliases() {
        for (Map.Entry<String, Command> commandMap : commands.entrySet()) {
            Command command = commandMap.getValue();

            for (String alias : command.getAliases()) {
                aliases.put(alias, command);
            }
        }
    }
}
