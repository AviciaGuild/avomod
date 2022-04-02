package tk.avicia.avomod.features;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

public class AutoStream {
    private static long lastStream = 0;
    private int tick = 0;

    private static void checkIfStreaming(String bossbarName) {
        String bossbarNameUnformatted = TextFormatting.getTextWithoutFormattingCodes(bossbarName);
        if (bossbarNameUnformatted == null) return;

        if (bossbarNameUnformatted.contains("Streamer mode enabled")) {
            lastStream = System.currentTimeMillis();
        }
    }

    private static void execute() {
        if (Avomod.getConfigBoolean("autoStream") && Utils.inWorld() && System.currentTimeMillis() - lastStream > 1000) {
            Avomod.getMC().player.sendChatMessage("/stream");
        }
    }

    @SubscribeEvent
    public void bossInfo(RenderGameOverlayEvent.BossInfo event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("autoStream")) return;

        BossInfo bossInfo = event.getBossInfo();
        String bossbarName = bossInfo.getName().getFormattedText();
        checkIfStreaming(bossbarName);
    }


    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("autoStream") || Avomod.getMC().player == null)
            return;

        tick++;
        if (tick % 1000 == 0) {
            execute();
        }
    }
}
