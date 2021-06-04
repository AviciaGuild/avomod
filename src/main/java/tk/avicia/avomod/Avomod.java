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
    //List of commands to add: "config", "warinfo", "onlinemembers", "lastseen", "up", "age", "playercount", "count"
    public static Map<String, Command> commands = new HashMap<String, Command>() {{
        put("help", new HelpCommand());
        put("chestcount", new ChestCountCommand());
        put("filterbank", new FilterBankCommand());
        put("aliases", new AliasesCommand());
        put("find", new FindCommand());
        put("playerguild", new PlayerGuildCommand());
        put("onlinemembers", new OnlineMembersCommand());
    }};
    public static Map<String, Command> aliases = new HashMap<String, Command>();
    private static boolean filterChat = true;

    public static Minecraft getMC() {
        return Minecraft.getMinecraft();
    }

    public static void toggleFilter(boolean newValue) {
        filterChat = newValue;
    }

    public static boolean isBankFiltered() {
        return filterChat;
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
