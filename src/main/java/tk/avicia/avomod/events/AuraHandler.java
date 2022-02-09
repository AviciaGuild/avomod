package tk.avicia.avomod.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;

public class AuraHandler {
    private static final int auraProcTime = 3200;
    private static long lastAura = 0;
    private static long firstAura = 0;

    public static void auraPinged() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAura > auraProcTime) {
            firstAura = currentTime;
        }

        lastAura = currentTime;
    }

    public static void draw() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - firstAura < auraProcTime) {
            long timeRemaining = (auraProcTime - (currentTime - firstAura));
            String remainingTimer = Double.toString(Math.floor(timeRemaining / 100.0) / 10.0);

            GlStateManager.pushMatrix();
            GlStateManager.scale(6.0F, 6.0F, 6.0F);
            ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
            Renderer.drawCenteredShadowedString(remainingTimer, scaledResolution.getScaledWidth() / 12, scaledResolution.getScaledHeight() / 12 - 3, Color.CYAN);

            if (currentTime - firstAura < 400) {
                Renderer.drawRect(new Color(255, 111, 0, 50), 0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            }

            GlStateManager.popMatrix();
        }
    }
}
