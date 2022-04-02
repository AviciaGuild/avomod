package tk.avicia.avomod.features;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;
import java.util.Arrays;

public class AuraHandler {
    private static final int auraProcTime = 3200;
    private static final int[] potentialAuraTimes = new int[]{24, 18, 12};

    public static int auraTimer = 0;
    public static long firstAura = 0;
    private static long lastAura = 0;

    private static void auraPinged() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastAura > auraProcTime) {
            if (firstAura != 0 && auraTimer == 0) {
                auraTimer = (int) (System.currentTimeMillis() - firstAura) / 1000;

                int[] differences = Arrays.stream(potentialAuraTimes).map(e -> e - auraTimer).toArray();
                int lowestValue = 99999;
                int lowestIndex = 0;
                for (int i = 0; i < differences.length; i++) {
                    if (differences[i] < lowestValue) {
                        lowestIndex = i;
                        lowestValue = differences[i];
                    }
                }

                auraTimer = potentialAuraTimes[lowestIndex];
                System.out.println(auraTimer);
            }

            firstAura = currentTime;
        }

        lastAura = currentTime;
    }

    private static void draw() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - firstAura < auraProcTime) {
            long timeRemaining = (auraProcTime - (currentTime - firstAura));
            String remainingTimer = Double.toString(Math.floor(timeRemaining / 100.0) / 10.0);

            GlStateManager.pushMatrix();
            GlStateManager.scale(6.0F, 6.0F, 6.0F);
            ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
            Renderer.drawCenteredShadowedString(remainingTimer, scaledResolution.getScaledWidth() / 12, scaledResolution.getScaledHeight() / 12 - 3, Color.CYAN);

            if (currentTime - firstAura < 400) {
                Color color;
                try {
                    color = Color.decode("#" + Avomod.getConfig("auraPingColor"));
                } catch (Exception e) {
                    color = new Color(255, 111, 0);
                }

                Renderer.drawRect(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50), 0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            }

            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("auraPing")) return;

        try {
            String subtitle = (String) ReflectionHelper.findField(GuiIngame.class, "displayedSubTitle", "field_175200_y").get(Avomod.getMC().ingameGUI);
            if (subtitle.length() > 0 && subtitle.contains("Aura")) {
                auraPinged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderOverlay(RenderGameOverlayEvent.Chat event) {
        if (Avomod.getConfigBoolean("disableAll") | !Avomod.getConfigBoolean("auraPing")) return;
        draw();
    }
}
