package tk.avicia.avomod.locations;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.render.MultipleElements;
import tk.avicia.avomod.features.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LocationsGui extends GuiScreen {
    private static boolean isOpen = false;
    private List<MultipleElements> items;

    public static boolean isOpen() {
        return isOpen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Makes blur
        this.drawWorldBackground(0);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRenderer, "Avomod Locations", this.width / 4 + 1, 6, 0x444444);
        this.drawCenteredString(this.fontRenderer, "Avomod Locations", this.width / 4, 5, 0x1B33CF);
        GlStateManager.popMatrix();

        items.forEach(MultipleElements::drawGuiElement);
        buttonList.forEach(button -> button.drawButton(Avomod.getMC(), mouseX, mouseY, partialTicks));
    }

    @Override
    public void initGui() {
        items = Arrays.asList(
                WarTracker.getElementsToDraw(),
                WorldInfo.getElementsToDraw(),
                AttacksMenu.getElementsToDraw(Arrays.asList("- 13:47 Otherwordly Monolith", "- 5:23 Detlas", "- 9:52 Guild Hall"), true),
                TabStatusDisplay.getElementsToDraw(Arrays.asList("Stealth Attack (00:01) x1", "90% Damage Bonus (00:04) x1")),
                WarDPS.getElementsToDraw(224, 12523563, 24400, 36000),
                BombBellTracker.getElementsToDraw(BombBellTracker.getSampleData())
        );
        buttonList.add(new ResetToDefault(0, this.width / 2 - 50, this.height - 30, 100, 20, "Reset to Defaults", this));
        isOpen = true;
    }

    @Override
    public void onResize(@Nonnull Minecraft mineIn, int w, int h) {
        super.onResize(mineIn, w, h);

        this.initGui();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int guiScale = new ScaledResolution(Avomod.getMC()).getScaleFactor();
        items.forEach(e -> e.pickup((Mouse.getX() / guiScale), height - ((Mouse.getY() / guiScale))));

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        int guiScale = new ScaledResolution(Avomod.getMC()).getScaleFactor();
        items.forEach(e -> e.move(Mouse.getX() / guiScale, height - (Mouse.getY() / guiScale)));

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int guiScale = new ScaledResolution(Avomod.getMC()).getScaleFactor();
        items.forEach(e -> e.release(Mouse.getX() / guiScale, height - (Mouse.getY() / guiScale)));

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        items.forEach(MultipleElements::save);
        isOpen = false;
        super.onGuiClosed();
    }
}
