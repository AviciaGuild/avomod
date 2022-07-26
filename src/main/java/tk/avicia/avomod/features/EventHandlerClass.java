package tk.avicia.avomod.features;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tk.avicia.avomod.Avomod;

public class EventHandlerClass {
    private final int tick = 0;

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

        if (message.startsWith("[!] Congratulations") && Avomod.getConfigBoolean("clickToSayCongrats")) {
            String[] firstSplit = message.split(" for")[0].split("to ");
            if (firstSplit.length <= 1) return;

            String username = firstSplit[1];
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String congratsCommand = String.format("/msg %s %s", username, Avomod.getConfig("congratsMessage"));
                TextComponentString congratsMessage = new TextComponentString("Click to say Congratulations!");
                congratsMessage.setStyle(new Style()
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, congratsCommand))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(congratsCommand)))
                        .setUnderlined(true)
                        .setColor(TextFormatting.AQUA));

                Avomod.getMC().player.sendMessage(congratsMessage);
            }).start();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
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