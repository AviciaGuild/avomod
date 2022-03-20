package tk.avicia.avomod.locations;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.renderer.RectangleText;
import tk.avicia.avomod.war.WarTracker;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;

public class LocationsGui extends GuiScreen {
    private ArrayList<RectangleText> items = new ArrayList<>();

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

        items.forEach(RectangleText::draw);
    }

    @Override
    public void initGui() {
        //Wars
//        GlStateManager.pushMatrix();
//        GlStateManager.scale(1.5F, 1.5F, 1.5F);
//        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
//
//        int stringWidth = Avomod.getMC().fontRenderer.getStringWidth("200 wars");
//        int x = (int) (scaledResolution.getScaledWidth() / 1.5) - (stringWidth + 10);
//        int y = (int) (scaledResolution.getScaledHeight() / 1.5) - 15;
//        Rectangle rectangle = new Rectangle(x - 2, y - 2, stringWidth + 4, 12, 1.5F, new Color(100, 100, 100, 255));
//        Text text = new Text("200 wars", x, y, 1.5F, Color.MAGENTA);

//        items.add(new LocationsItem("weeklyWars", rectangle, text));
        try {
            items.add(WarTracker.getRectangleText());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        GlStateManager.popMatrix();
    }

    @Override
    public void onResize(@Nonnull Minecraft mineIn, int w, int h) {
        super.onResize(mineIn, w, h);

        this.initGui();
    }

    @Override
    public void handleMouseInput() throws IOException {
//        if (Mouse.getEventButtonState() && Mouse.getEventButton() == 1) {
//            items.forEach(e -> e.move(Mouse.getX() / 2, height - (Mouse.getY() / 2)));
//        }

        super.handleMouseInput();
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
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        items.forEach(RectangleText::save);

        super.onGuiClosed();
    }
}
