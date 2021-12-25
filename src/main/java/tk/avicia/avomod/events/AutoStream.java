package tk.avicia.avomod.events;

import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

public class AutoStream {
    public static long lastStream = 0;

    public static void checkIfStreaming(String bossbarName) {
        if (TextFormatting.getTextWithoutFormattingCodes(bossbarName).contains("Streamer mode enabled")) {
            lastStream = System.currentTimeMillis();
        }
    }

    public static void execute() {
        if (Avomod.getConfigBoolean("autoStream") && Utils.inWorld() && System.currentTimeMillis() - lastStream > 1000) {
            Avomod.getMC().player.sendChatMessage("/stream");
        }
    }
}
