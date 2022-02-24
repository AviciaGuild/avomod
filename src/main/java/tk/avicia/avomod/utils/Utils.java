package tk.avicia.avomod.utils;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import tk.avicia.avomod.Avomod;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Utils {
    public static String firstLetterCapital(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getReadableTime(int minutes) {
        return (int) (Math.floor(minutes / 60.0)) + " h " + minutes % 60 + " m";
    }

//    public static void sendClickPacket(Container container, int slotId, ClickType clickType, int clickButton, ItemStack itemStack) {
//        CPacketClickWindow compassPacket = new CPacketClickWindow(container.windowId, slotId, clickButton,
//                clickType, itemStack, container.getNextTransactionID(Avomod.getMC().player.inventory));
//        NetHandlerPlayClient connection = Avomod.getMC().getConnection();
//
//        if (connection != null) {
//            connection.sendPacket(compassPacket);
//        }
//    }

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

    public static List<String> getUpcomingAttacks() {
        if (Avomod.getMC().player == null || Avomod.getMC().world == null) return new ArrayList<>();

        Scoreboard scoreboard = Avomod.getMC().world.getScoreboard();
        Collection<Score> scores = scoreboard.getScores();
        Optional<Score> titleScoreOptional = scores.stream().filter(e -> e.getPlayerName().contains("Upcoming Attacks")).findFirst();

        if (titleScoreOptional.isPresent()) {
            int titleScore = titleScoreOptional.get().getScorePoints();
            List<Score> upcomingAttackScores = scores.stream().filter(e -> e.getScorePoints() < titleScore).collect(Collectors.toList());
            List<String> upcomingAttacks = upcomingAttackScores.stream().map(Score::getPlayerName).collect(Collectors.toList());
            List<String> duplicateTerritories = new ArrayList<>();

            return upcomingAttacks.stream().filter(e -> {
                if (!duplicateTerritories.contains(e)) {
                    duplicateTerritories.add(e);
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    public static String parseReadableNumber(double number) {
        if (number >= 1000000000) {
            return String.format("%sB", Math.floor(number / 10000000) / 100);
        } else if (number >= 1000000) {
            return String.format("%sM", Math.floor(number / 10000) / 100);
        } else if (number >= 1000) {
            return String.format("%sK", Math.floor(number / 10) / 100);
        }

        return String.valueOf(Math.floor(number));
    }

    public static boolean inWorld() {
        InventoryPlayer inventoryPlayer = Avomod.getMC().player.inventory;

        return inventoryPlayer.getStackInSlot(8).toString().contains("netherStar");
    }
}
