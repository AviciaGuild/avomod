package tk.avicia.avomod;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClass {
    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        String message = event.getMessage().getFormattedText();
        boolean bankMessage = message.startsWith("[INFO]") && message.contains("Guild Bank");
        if (bankMessage && Avomod.isBankFiltered()) {
            event.setCanceled(true);
        }
    }
}
