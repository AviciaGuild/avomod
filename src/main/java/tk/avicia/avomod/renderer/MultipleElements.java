package tk.avicia.avomod.renderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.locations.LocationsFile;
import tk.avicia.avomod.locations.LocationsGui;

import java.util.List;
import java.util.stream.Collectors;

public class MultipleElements {
    private final List<Element> elementsList;
    private final String key;
    private final float scale;
    private int startX, startY;
    private boolean clicked = false;
    private boolean leftAlign = true;

    public MultipleElements(String key, float scale, List<Element> elementsList) {
        this.key = key;
        this.elementsList = elementsList;
        this.scale = scale;

        updateAlignment();
    }

    public void draw() {
        if(!LocationsGui.isOpen()) {
            elementsList.forEach(Element::draw);
        }
    }

    public void drawGuiElement() {
        elementsList.forEach(Element::draw);
    }

    public void pickup(int mouseX, int mouseY) {
        elementsList.forEach(element -> {
            if (element instanceof Rectangle && ((Rectangle) element).inRectangle(mouseX, mouseY)) {
                startX = mouseX;
                startY = mouseY;
                clicked = true;
            }
        });
    }

    public void move(int mouseX, int mouseY) {
        if (!clicked) return;

        elementsList.forEach(element -> element.move(mouseX - startX, mouseY - startY));
        startX = mouseX;
        startY = mouseY;

        updateAlignment();
    }

    public void updateAlignment() {
        List<Rectangle> rectangleList = elementsList.stream().filter(Rectangle.class::isInstance).map(Rectangle.class::cast).collect(Collectors.toList());
        float minLeftEdge = rectangleList.stream().map(Element::getLeftEdge).min(Float::compare).orElse((float) 0) / scale;
        float maxRightEdge = rectangleList.stream().map(Rectangle::getRightEdge).max(Float::compare).orElse((float) 0) / scale;
        float screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth() / scale;
        FontRenderer fontRenderer = Avomod.getMC().fontRenderer;

        if (leftAlign && minLeftEdge + (maxRightEdge - minLeftEdge) / 2 > screenWidth / 2) {
            leftAlign = false;

            elementsList.forEach(element -> {
                if (element instanceof Rectangle) {
                    element.setX(maxRightEdge - ((Rectangle) element).getWidth());
                } else if (element instanceof Text) {
                    element.setX(maxRightEdge - fontRenderer.getStringWidth(((Text) element).getText()) - 2);
                }
            });
        } else if (!leftAlign && minLeftEdge + (maxRightEdge - minLeftEdge) / 2 < screenWidth / 2) {
            leftAlign = true;

            elementsList.forEach(element -> {
                if (element instanceof Rectangle) {
                    element.setX(minLeftEdge);
                } else if (element instanceof Text) {
                    element.setX(minLeftEdge + 2);
                }
            });
        }
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
        List<Rectangle> rectangleList = elementsList.stream().filter(Rectangle.class::isInstance).map(Rectangle.class::cast).collect(Collectors.toList());
        float minLeftEdge = rectangleList.stream().map(Element::getLeftEdge).min(Float::compare).orElse((float) 0) / scale;
        float maxRightEdge = rectangleList.stream().map(Rectangle::getRightEdge).max(Float::compare).orElse((float) 0) / scale;
        float minTopEdge = rectangleList.stream().map(Element::getTopEdge).min(Float::compare).orElse((float) 0) / scale;
        float maxBottomEdge = rectangleList.stream().map(Rectangle::getBottomEdge).max(Float::compare).orElse((float) 0) / scale;

        float screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth() / scale;
        float screenHeight = (new ScaledResolution(Avomod.getMC()).getScaledHeight() / scale) - (maxBottomEdge - minTopEdge);
        float xProp = ((int) minLeftEdge) / screenWidth;
        float yProp = ((int) minTopEdge) / screenHeight;

        if (!leftAlign) {
            xProp = ((int) maxRightEdge) / screenWidth;
        }

        return xProp + "," + yProp + "," + leftAlign;
    }
}
