package tk.avicia.avomod.renderer;

import net.minecraft.client.gui.ScaledResolution;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.locations.LocationsFile;

public class RectangleText {
    private final Rectangle rectangle;
    private final Text text;
    private final String key;
    private int startX, startY;
    private boolean clicked = false;

    public RectangleText(String key, Rectangle rectangle, Text text) {
        this.key = key;
        this.rectangle = rectangle;
        this.text = text;
    }

    public RectangleText(String key, String fileData) {
        this.key = key;
        this.rectangle = new Rectangle(fileData.split("\\|")[0]);
        this.text = new Text(fileData.split("\\|")[1]);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Text getText() {
        return text;
    }

    public void draw() {
        rectangle.draw();
        text.draw();
    }

    public void pickup(int mouseX, int mouseY) {
        if (mouseX < rectangle.getLeftEdge() || mouseX > rectangle.getRightEdge() ||
                mouseY < rectangle.getTopEdge() || mouseY > rectangle.getBottomEdge()) return;

        startX = mouseX;
        startY = mouseY;
        clicked = true;
    }

    public void move(int mouseX, int mouseY) {
        if (!clicked) return;

        rectangle.move(mouseX - startX, mouseY - startY);
        text.setX(rectangle.getX() + 2);
        text.setY(rectangle.getY() + 2);
        startX = mouseX;
        startY = mouseY;
    }

    public void release(int mouseX, int mouseY) {
        if (!clicked) return;

        startX = 0;
        startY = 0;
        clicked = false;
    }

    public void save() {
        LocationsFile.save(this);
    }

    public String getKey() {
        return key;
    }

    public String toString() {
        final float screenWidth = (new ScaledResolution(Avomod.getMC()).getScaledWidth() / 1.5F) - getRectangle().getWidth();
        final float screenHeight = (new ScaledResolution(Avomod.getMC()).getScaledHeight() / 1.5F) - getRectangle().getHeight();
        final float xProp = ((int) (getRectangle().getX())) / screenWidth;
        final float yProp = ((int) (getRectangle().getY())) / screenHeight;

        return xProp + "," + yProp;
    }
}
