package tk.avicia.avomod.features;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tk.avicia.avomod.Avomod;

public class EventHandlerClass {
    private int tick = 0;

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getConfigBoolean("revealNicks")) {
            ChatUtils.revealNicks(event);
        }

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null) return;

        boolean bankMessage = message.startsWith("[INFO]") && message.contains("Guild Bank");
        if (bankMessage && Avomod.getConfigBoolean("filterBankMessages")) {
            event.setCanceled(true);
        }

        KeyBinding sneakKeyBind = Avomod.getMC().gameSettings.keyBindSneak;
        if (message.contains("Press SHIFT to continue") && Avomod.getConfigBoolean("skipDialogue")) {
            Thread thread = new Thread(() -> {
                try {
                    KeyBinding.setKeyBindState(sneakKeyBind.getKeyCode(), true);
                    Thread.sleep(100);
                    KeyBinding.setKeyBindState(sneakKeyBind.getKeyCode(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        if (message.startsWith("[INFO]") && message.contains("resources") && Avomod.getConfigBoolean("filterResourceMessages")) {
            event.setCanceled(true);
        }

        if (message.trim().startsWith("Loading Resource Pack...")) {
            new Thread(() -> {
                UpdateChecker.checkStableUpdate();

                if (Avomod.getConfigBoolean("betaNotification")) {
                    UpdateChecker.checkBetaUpdate();
                }
            }).start();
        }

//        if (Avomod.getConfigBoolean("autogg") && message.startsWith("[!] Congratulations")) {
//            String[] firstSplit = message.split(" for")[0].split("to ");
//            if (firstSplit.length <= 1) return;
//
//            String username = firstSplit[1];
//            new Thread(() -> {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Avomod.getMC().player.sendChatMessage(String.format("/msg %s gg", username));
//            }).start();
//        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.guiToDraw != null) {
            Avomod.getMC().displayGuiScreen(Avomod.guiToDraw);
            Avomod.guiToDraw = null;
        }
    }

    @SubscribeEvent
    public void entityRender(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!Avomod.getConfigBoolean("hideEntitiesInWar") || (System.currentTimeMillis() - WarDPS.lastTimeInWar) > 2000)
            return;

        EntityLivingBase entity = event.getEntity();
        if (entity.getTeam() == null) {
            event.setCanceled(true);
        }
    }
}