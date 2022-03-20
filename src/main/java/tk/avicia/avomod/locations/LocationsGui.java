package tk.avicia.avomod.locations;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.events.WorldInfo;
import tk.avicia.avomod.renderer.MultipleElements;
import tk.avicia.avomod.war.WarTracker;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LocationsGui extends GuiScreen {
    private List<MultipleElements> items;

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

        items.forEach(MultipleElements::draw);
    }

    @Override
    public void initGui() {
        items = Arrays.asList(WarTracker.getElementsToDraw(), WorldInfo.getElementsToDraw());
    }

    @Override
    public void onResize(@Nonnull Minecraft mineIn, int w, int h) {
        super.onResize(mineIn, w, h);

        this.initGui();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        items.forEach(e -> e.pickup(Mouse.getX() / 2, height - (Mouse.getY() / 2)));

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        items.forEach(e -> e.move(Mouse.getX() / 2, height - (Mouse.getY() / 2)));

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        items.forEach(e -> e.release(Mouse.getX() / 2, height - (Mouse.getY() / 2)));

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
        items.forEach(MultipleElements::save);

        super.onGuiClosed();
    }
}
