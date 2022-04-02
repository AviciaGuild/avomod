package tk.avicia.avomod.utils;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import tk.avicia.avomod.Avomod;

import java.awt.*;

public class Utils {
    public static String firstLetterCapital(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getReadableTime(int minutes) {
        return (minutes >= 3600 ? (int) Math.floor((minutes / 1440.0)) + "d " : "") + (int) (Math.floor((minutes % 1440) / 60.0)) + "h " + minutes % 60 + "m";
    }

    public static void sendClickPacket(Container container, int slotId, ClickType clickType, int clickButton, ItemStack itemStack) {
        sendClickPacket(new CPacketClickWindow(container.windowId, slotId, clickButton,
                clickType, itemStack, container.getNextTransactionID(Avomod.getMC().player.inventory)));
    }

    public static void sendClickPacket(CPacketClickWindow cPacketClickWindow) {
        NetHandlerPlayClient connection = Avomod.getMC().getConnection();

        if (connection != null) {
            connection.sendPacket(cPacketClickWindow);
        }
    }

//    public static Tuple<Integer, Integer> getTopLeftCornerOfSlot(int slot) {
//        if (Avomod.getMC().player == null) {
//            return null;
//        }
//        int guiSize = Avomod.getMC().player.openContainer.inventorySlots.size();
//        final int rows = (guiSize - 36) / 9;
//        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
//        int screenWidth = scaledResolution.getScaledWidth();
//        int screenHeight = scaledResolution.getScaledHeight();
//        final int slotDimensions = 18;
//        // The center of the screen is always the same, no matter gui scale and resolution
//        int leftEdge = (screenWidth / 2) - (int) Math.floor(4.5 * slotDimensions) + (slot % 9) * slotDimensions;
//        // For all new rows added after 3 the center of the screen gets adjusted by .5 slotdimentions
//        // the thing between the slots that says "Inventory" has a height of 14
//        int topEdge = (screenHeight / 2) - (int) Math.floor(((rows - 3) * -0.5) * slotDimensions) - rows * slotDimensions - 14 +
//                (int) Math.floor(slot / 9.0) * slotDimensions;
//        if (slot > rows * 9) {
//            topEdge += 14;
//        }
//        return new Tuple<>(leftEdge, topEdge);
//    }

    public static Color getContrastColor(Color color) {
        double y = (299.0 * color.getRed() + 587.0 * color.getGreen() + 114.0 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    public static String parseReadableNumber(double number, int decimals) {
        if (number >= 1000000000) {
            return String.format("%sB", Math.round(number / (1000000000 / Math.pow(10, decimals))) / Math.pow(10, decimals));
        } else if (number >= 1000000) {
            return String.format("%sM", Math.round(number / (1000000 / Math.pow(10, decimals))) / Math.pow(10, decimals));
        } else if (number >= 1000) {
            return String.format("%sK", Math.round(number / (1000 / Math.pow(10, decimals))) / Math.pow(10, decimals));
        }

        return String.valueOf((int) number);
    }

    public static boolean inWorld() {
        InventoryPlayer inventoryPlayer = Avomod.getMC().player.inventory;

        return inventoryPlayer.getStackInSlot(8).toString().contains("netherStar");
    }

    public static float getStartX(String key, int rectangleWidth, float scale) {
        String locationText = Avomod.getLocation(key);
        if (locationText == null) return (float) 0;

        float screenWidth = new ScaledResolution(Avomod.getMC()).getScaledWidth() / scale;
        float x = (Float.parseFloat(locationText.split(",")[0]) * screenWidth);
        boolean alignment = Boolean.parseBoolean(locationText.split(",")[2]);

        return alignment ? x : x - rectangleWidth;
    }

    public static float getStartY(String key, int numRectangles, float scale) {
        int rectangleHeight = 12;

        String locationText = Avomod.getLocation(key);
        if (locationText == null) return (float) 0;

        float screenHeight = (new ScaledResolution(Avomod.getMC()).getScaledHeight() / scale) - (rectangleHeight * numRectangles);
        return Float.parseFloat(locationText.split(",")[1]) * screenHeight;
    }

    public static float getStartY(String key, float scale, int totalHeight) {
        String locationText = Avomod.getLocation(key);
        if (locationText == null) return (float) 0;

        float screenHeight = (new ScaledResolution(Avomod.getMC()).getScaledHeight() / scale) - totalHeight;
        return Float.parseFloat(locationText.split(",")[1]) * screenHeight;
    }
}
