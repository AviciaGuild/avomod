package tk.avicia.avomod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import tk.avicia.avomod.commands.AvomodCommand;
import tk.avicia.avomod.commands.Command;
import tk.avicia.avomod.commands.subcommands.*;

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
    }};
    public static Map<String, Command> aliases = new HashMap<>();
    public static int[] armorAccessorySlotNumbers = new int[]{5, 6, 7, 8, 9, 10, 11, 12};

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

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerClass());
        ClientCommandHandler.instance.registerCommand(new AvomodCommand());

        this.initializeAliases();
    }

    public void initializeAliases() {
        for (Map.Entry<String, Command> commandMap : this.commands.entrySet()) {
            Command command = commandMap.getValue();

            for (String alias : command.getAliases()) {
                this.aliases.put(alias, command);
            }
        }
    }
}
