package tk.avicia.avomod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tk.avicia.avomod.commands.AvomodCommand;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.commands.subcommands.*;
import tk.avicia.avomod.configs.Config;
import tk.avicia.avomod.configs.ConfigInput;
import tk.avicia.avomod.configs.ConfigToggle;
import tk.avicia.avomod.core.structures.CustomFile;
import tk.avicia.avomod.features.*;
import tk.avicia.avomod.utils.BeaconManager;
import tk.avicia.avomod.utils.TerritoryData;
import tk.avicia.avomod.webapi.OnlinePlayers;
import tk.avicia.avomod.webapi.TerritoryDataApi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod(modid = Avomod.MODID, name = Avomod.NAME, version = Avomod.VERSION)
public class Avomod {
    public static final String MODID = "avomod";
    public static final String NAME = "avomod";
    public static final String VERSION = "1.6.0";
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
        put("soulpoints", new SoulPointCommand());
        put("wars", new WarsCommand());
        put("locations", new LocationsCommand());
        put("autostream", new AutoStreamCommand());
    }};
    public static Map<String, String> defaultLocations = new HashMap<String, String>() {{
        put("weeklyWars", "1,0.98,false");
        put("worldInfo", "1,0.7,false");
        put("attacksMenu", "1,0.1,false");
        put("tabStatusDisplay", "0.4,.1,true");
        put("warDPS", "0,0.2,true");
        put("bombBellTracker", "1,0.25,false");
    }};
    public static Map<String, Command> aliases = new HashMap<>();
    public static GuiScreen guiToDraw = null;
    public static JsonObject configs = null;
    public static JsonObject locations = null;
    public static Config[] configsArray = new Config[]{
            new ConfigToggle("General", "Disable Everything", "Disabled", "disableAll"),
            new ConfigToggle("General", "Notify for avomod BETA Version (may have bugs)", "Disabled", "betaNotification"),
            new ConfigToggle("Guild", "Filter Out Bank Messages", "Disabled", "filterBankMessages"),
            new ConfigToggle("Guild", "Filter Out All Resource Messages", "Disabled", "filterResourceMessages"),
            new ConfigToggle("Chat", "Reveal Nicknames", "Enabled", "revealNicks"),
            new ConfigToggle("Chat", "Auto Skip Quest Dialogue", "Disabled", "skipDialogue"),
            new ConfigInput("Chat", "Click to say congrats message", "Congrats!", ".+", "^.+$", 30, "congratsMessage"),
            new ConfigToggle("War", "Custom Attack Timers Display", "Enabled", "attacksMenu"),
            new ConfigToggle("War", "Green Beacon at Soonest War", "Enabled", "greenBeacon"),
            new ConfigToggle("War", "Announce Territory Defense in Chat", "Enabled", "terrDefenseInChat"),
            new ConfigToggle("War", "Display War Info (dps, tower ehp, etc.)", "Enabled", "dpsInWars"),
            new ConfigToggle("War", "Hide Entities in Wars", "Disabled", "hideEntitiesInWar"),
            new ConfigToggle("War", "Aura Ping", "Enabled", "auraPing"),
            new ConfigInput("War", "Aura Ping Color", "FF6F00", "[\\da-fA-F]+", "^[\\da-fA-F]{6}$", 6, "auraPingColor"),
            new ConfigToggle("War", "Display Weekly Warcount on Screen", "Disabled", "displayWeeklyWarcount"),
            new ConfigToggle("War", "Prevent joining wars when afk", "Enabled", "afkWarProtection"),
            new ConfigInput("War", "Minutes until considered afk", "10", "[0-9]+", "^[0-9]+$", 3, "afkTime"),
            new ConfigToggle("Misc", "Auto /stream on World Swap", "Disabled", "autoStream"),
            new ConfigToggle("Misc", "Prevent Moving Armor/Accessories", "Disabled", "disableMovingArmor"),
            new ConfigToggle("Misc", "Make Mob Health Bars More Readable", "Enabled", "readableHealth"),
            new ConfigToggle("Misc", "Display Some Tab Stats on Screen", "Disabled", "tabStatusDisplay"),
            new ConfigToggle("Misc", "Bomb Bell Tracker (REQUIRES CHAMPION)", "Enabled", "bombBellTracker")
    };
    public static TerritoryDataApi territoryData;
    public static OnlinePlayers onlinePlayers;
    public static String[] statsFromTabToShow = new String[]{"Damage Bonus", "Stealth Attack"};

    public static Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    public static String getLocation(String locationKey) {
        JsonElement locationElement = Avomod.locations.get(locationKey);

        if (locationElement == null || locationElement.isJsonNull()) {
            return defaultLocations.get(locationKey);
        } else {
            return locationElement.getAsString();
        }
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
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Runnable worldDataUpdater = () -> {
            onlinePlayers = new OnlinePlayers();
            WorldInfo.updateWorldData();
        };
        ScheduledExecutorService worldDataUpdaterService = Executors.newScheduledThreadPool(1);
        worldDataUpdaterService.scheduleAtFixedRate(worldDataUpdater, 0, 60, TimeUnit.SECONDS);

        territoryData = new TerritoryDataApi();

        Arrays.asList(
                new WarJoinProtection(),
                new AttackedTerritoryDifficulty(),
                new AttacksMenu(),
                new AutoStream(),
                new AuraHandler(),
                new AverageLevel(),
                new BeaconManager(),
                new BombBellTracker(),
                new CustomKeybinds(),
                new DisableMovingArmor(),
                new EventHandlerClass(),
                new GuildBankKeybind(),
                new MobHealthSimplifier(),
                new TabStatusDisplay(),
                new TerritoryData(),
                new TradeMarketFeatures(),
                new WarDPS(),
                new WarTracker(),
                new WorldInfo()
        ).forEach(MinecraftForge.EVENT_BUS::register);

        ClientCommandHandler.instance.registerCommand(new AvomodCommand());

        this.initializeAliases();
        CustomKeybinds.updateKeybinds();
    }

    private void initializeConfigs() {
        CustomFile configsFile = new CustomFile("avomod/configs/configs.json");
        JsonObject configsJson = configsFile.readJson();
        boolean configsChanged = false;

        for (Config config : configsArray) {
            JsonElement configElement = configsJson.get(config.configsKey);

            if (configElement == null || configElement.isJsonNull()) {
                configsJson.addProperty(config.configsKey, config.defaultValue);
                configsChanged = true;
            }
        }

        if (configsChanged) {
            configsFile.writeJson(configsJson);
        }
        Avomod.configs = configsJson;

        CustomFile locationsFile = new CustomFile("avomod/configs/locations.json");
        JsonObject locationsJson = locationsFile.readJson();
        boolean locationsChanged = false;

        for (Map.Entry<String, String> locationData : defaultLocations.entrySet()) {
            JsonElement locationsElement = locationsJson.get(locationData.getKey());

            if (locationsElement == null || locationsElement.isJsonNull()) {
                locationsJson.addProperty(locationData.getKey(), locationData.getValue());
                locationsChanged = true;
            }
        }

        if (locationsChanged) {
            locationsFile.writeJson(locationsJson);
        }
        Avomod.locations = locationsFile.readJson();
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
