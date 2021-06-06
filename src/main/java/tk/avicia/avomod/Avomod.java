package tk.avicia.avomod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.lwjgl.input.Keyboard;
import tk.avicia.avomod.commands.AvomodCommand;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.commands.subcommands.*;
import tk.avicia.avomod.events.EventHandlerClass;
import tk.avicia.avomod.settings.KeybindSettings;
import tk.avicia.avomod.utils.Keybind;
import tk.avicia.avomod.utils.Utils;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = Avomod.MODID, name = Avomod.NAME, version = Avomod.VERSION)
public class Avomod {
    public static final String MODID = "avomod";
    public static final String NAME = "AvoMod";
    public static final String VERSION = "1.0";

    public static Map<String, Command> commands = new HashMap<String, Command>() {{
        put("help", new HelpCommand());
        put("chestcount", new ChestCountCommand());
        put("filterbank", new FilterBankCommand());
        put("aliases", new AliasesCommand());
        put("find", new FindCommand());
        put("playerguild", new PlayerGuildCommand());
        put("onlinemembers", new OnlineMembersCommand());
        put("up", new UpCommand());
        put("age", new AgeCommand());
        put("lastseen", new LastSeenCommand());
        put("playercount", new PlayerCountCommand());
        put("count", new CountCommand());
        put("togglemoving", new ToggleMovingArmorCommand());
        put("keybinds", new KeybindsCommand());
    }};
    public static Map<String, Command> aliases = new HashMap<>();
    public static Map<String, Keybind> keybinds = new HashMap<>();
    public static boolean autoConnect = false;
    private static boolean filterChat = true;
    private static boolean disableMovingArmorOrAccessories = true;

    public static Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    public static void toggleFilter(boolean newValue) {
        filterChat = newValue;
    }

    public static boolean isBankFiltered() {
        return filterChat;
    }

    public static boolean isMovingArmorOrAccessoriesDisabled() {
        return disableMovingArmorOrAccessories;
    }

    public static void toggleMovingArmorOrAccessories(boolean newValue) {
        disableMovingArmorOrAccessories = newValue;
    }

    public static void updateKeybinds() {
        JsonObject settings = KeybindSettings.getSettings();

        for (Map.Entry<String, JsonElement> e : settings.entrySet()) {
            if (!keybinds.containsKey(e.getKey()) || !keybinds.get(e.getKey()).getCommandToRun().equals(e.getValue().getAsString())) {
                Keybind keybind = new Keybind(Utils.firstLetterCapital(e.getValue().getAsString()), Keyboard.getKeyIndex(e.getKey().toUpperCase()), "Avomod", e.getValue().getAsString());
                keybinds.put(e.getKey(), keybind);

                ClientRegistry.registerKeyBinding(keybind);
            }
        }

        keybinds.entrySet().removeIf(entry -> !settings.has(entry.getKey()));
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (autoConnect) {
            FMLClientHandler.instance().connectToServerAtStartup("play.wynncraft.com", 25565);
            Avomod.getMC().setServerData(new ServerData("Wynncraft", "play.wynncraft.com", false));
        }

        MinecraftForge.EVENT_BUS.register(new EventHandlerClass());
        ClientCommandHandler.instance.registerCommand(new AvomodCommand());

        this.initializeAliases();
        updateKeybinds();
    }

    public void initializeAliases() {
        for (Map.Entry<String, Command> commandMap : commands.entrySet()) {
            Command command = commandMap.getValue();

            for (String alias : command.getAliases()) {
                aliases.put(alias, command);
            }
        }
    }
}
