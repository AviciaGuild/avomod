package tk.avicia.avomod.utils;

public class ScreenCoordinates {
    private final float startX, startY, endX, endY;

    public ScreenCoordinates(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public boolean mouseIn(int mouseX, int mouseY) {
        return mouseX <= this.endX && mouseX >= this.startX && mouseY <= this.endY && mouseY >= this.startY;
    }
}
