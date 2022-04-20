package tk.avicia.avomod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.Coordinates;

import java.awt.*;

public class BeaconManager {
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final ResourceLocation beamResource = new ResourceLocation("textures/entity/beacon_beam.png");

    public static Coordinates compassLocation = null;
    public static String compassTerritory = null;

    public static Coordinates soonestTerritoryLocation = null;
    public static String soonestTerritory = null;

    public static void drawBeam(Coordinates loc, Color color, float partialTicks, String territoryName) {
        RenderManager renderManager = Avomod.getMC().getRenderManager();
        if (renderManager.renderViewEntity == null) return;

        float alpha = 1f;

        Vec3d positionVec = new Vec3d(loc.getX(), loc.getY() + 0.118D, loc.getZ());
        Vec3d playerVec = renderManager.renderViewEntity.getPositionVector();

        double distance = playerVec.distanceTo(positionVec);
        if (distance <= 4f || distance > 4000f) return;
        if (distance <= 8f) alpha = (float) (distance - 4f) / 3f;

        if (alpha > 1) alpha = 1;  // avoid excessive values

        alpha *= color.getAlpha() / 255.0f;

        double maxDistance = Avomod.getMC().gameSettings.renderDistanceChunks * 16d;
        if (distance > maxDistance) {  // this will drag the beam to the visible area if outside of it
            // partial ticks aren't factored into player pos, so if we're going to use it for rendering, we need to recalculate to account for partial ticks
            Vec3d prevPosVec = new Vec3d(renderManager.renderViewEntity.prevPosX, renderManager.renderViewEntity.prevPosY, renderManager.renderViewEntity.prevPosZ);
            playerVec = playerVec.subtract(prevPosVec).scale(partialTicks).add(prevPosVec);

            Vec3d delta = positionVec.subtract(playerVec).normalize();
            positionVec = playerVec.add(new Vec3d(delta.x * maxDistance, delta.y * maxDistance, delta.z * maxDistance));
        }

        drawBeam(loc, positionVec.x - renderManager.viewerPosX, -renderManager.viewerPosY, positionVec.z - renderManager.viewerPosZ, alpha, color, partialTicks, territoryName);
    }

    private static void drawBeam(Coordinates originalLoc, double x, double y, double z, float alpha, Color color, float partialTicks, String territoryName) {
        GlStateManager.pushAttrib();
        {
            Avomod.getMC().renderEngine.bindTexture(beamResource);  // binds the texture
            GlStateManager.glTexParameteri(3553, 10242, 10497);

            float time = Minecraft.getSystemTime() / 50F;
            float offset = -(-time * 0.2F - MathHelper.fastFloor(-time * 0.1F)) * 0.6F;

            double d1 = 256.0F * alpha;
            double d2 = -1f + offset;
            double d3 = 256.0F * alpha + d2;

            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(1f, 1f, 1f, 1f);

            tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            BufferBuilder builder = tessellator.getBuffer();
            {
                builder.pos(x + .2d, y + d1, z + .2d).tex(1d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y, z + .2d).tex(1d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y, z + .2d).tex(0d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y + d1, z + .2d).tex(0d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y + d1, z + .8d).tex(1d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y, z + .8d).tex(1d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y, z + .8d).tex(0d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y + d1, z + .8d).tex(0d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y + d1, z + .2d).tex(1d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y, z + .2d).tex(1d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y, z + .8d).tex(0d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .8d, y + d1, z + .8d).tex(0d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y + d1, z + .8d).tex(1d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y, z + .8d).tex(1d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y, z + .2d).tex(0d, d2).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
                builder.pos(x + .2d, y + d1, z + .2d).tex(0d, d3).color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha).endVertex();
            }
            tessellator.draw();
        }
        GlStateManager.popAttrib();

        drawBeaconTitle(originalLoc, territoryName, partialTicks);
    }

    private static void drawBeaconTitle(Coordinates originalLoc, String territoryName, float partialTicks) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();

        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.pushMatrix();
        Entity viewer = Avomod.getMC().getRenderViewEntity();
        if (viewer == null) return;

        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        double newX = originalLoc.getX() - viewerX;
        double newY = (viewer.posY + 3) - viewerY - viewer.getEyeHeight();
        double newZ = originalLoc.getZ() - viewerZ;
        double distSq = newX * newX + newY * newY + newZ * newZ;
        double dist = Math.sqrt(distSq);
        if (distSq > 144) {
            newX *= 12 / dist;
            newY *= 12 / dist;
            newZ *= 12 / dist;
        }
        GlStateManager.translate(newX, newY, newZ);
        GlStateManager.translate(0f, viewer.getEyeHeight(), 0f);
        drawNametag(territoryName);
        GlStateManager.rotate(-Avomod.getMC().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Avomod.getMC().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0f, -0.25f, 0f);
        GlStateManager.rotate(-Avomod.getMC().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(Avomod.getMC().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        drawNametag(TextFormatting.YELLOW.toString() + Math.round(dist) + "m");
        GlStateManager.popMatrix();
        GlStateManager.disableLighting();
    }

    private static void drawNametag(String text) {
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;
        double f = 1.6f;
        double f1 = 0.016666668f * f;
        GlStateManager.pushMatrix();
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-Avomod.getMC().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Avomod.getMC().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-f1, -f1, f1);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        int i = 0;
        double j = fontRenderer.getStringWidth(text) / 2.0;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((-j - 1), (-1 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldrenderer.pos((-j - 1), (8 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldrenderer.pos((j + 1), (8 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        worldrenderer.pos((j + 1), (-1 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, i, 553648127);
        GlStateManager.depthMask(true);
        fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, i, -1);
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (compassLocation != null) {
            drawBeam(compassLocation, new Color(0, 50, 150, 255), e.getPartialTicks(), compassTerritory);
        }

        if (soonestTerritoryLocation != null && Avomod.getConfigBoolean("greenBeacon")) {
            drawBeam(soonestTerritoryLocation, new Color(50, 150, 0, 255), e.getPartialTicks(), soonestTerritory);
        }
    }
}
