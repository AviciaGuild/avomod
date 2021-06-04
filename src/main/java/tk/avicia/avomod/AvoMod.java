package tk.avicia.avomod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import tk.avicia.avomod.commands.ChestCountCommand;
import tk.avicia.avomod.commands.FilterCommand;

@Mod(modid = AvoMod.MODID, name = AvoMod.NAME, version = AvoMod.VERSION)
public class AvoMod {
    public static final String MODID = "avomod";
    public static final String NAME = "AvoMod";
    public static final String VERSION = "1.0";

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    private static boolean filterChat = true;

    public static void toggleFilter(boolean newValue) {
        filterChat = newValue;
    }

    public static boolean filterChat() {
        return filterChat;
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerClass());
        ClientCommandHandler.instance.registerCommand(new FilterCommand());
        ClientCommandHandler.instance.registerCommand(new ChestCountCommand());
    }
}
