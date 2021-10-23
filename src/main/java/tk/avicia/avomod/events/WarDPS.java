package tk.avicia.avomod.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import java.awt.*;
import java.util.Arrays;

public class WarDPS {
    public static long warStartTime = -1;
    private static String previousTerritoryName = "";
    public static long lastTimeInWar = 0;
    private static long previousTime = 0;
    private static double previousEhp = 0;
    private static double dps = 0;
    private static double maxEhp = 0;

    public static void execute(String[] bossbarWords) {
        try {
            lastTimeInWar = System.currentTimeMillis();
            int startIndex1 = Arrays.asList(bossbarWords).indexOf("-");
            int startIndex2 = Arrays.asList(bossbarWords).lastIndexOf("-");
            StringBuilder territoryName = new StringBuilder();
            for (int i = 1; i < startIndex1; i++) {
                territoryName.append(TextFormatting.getTextWithoutFormattingCodes(bossbarWords[i]));
            }
            if (!territoryName.toString().equals(previousTerritoryName)) {
                resetValues();
                previousTerritoryName = territoryName.toString();
                warStartTime = System.currentTimeMillis();
            }
            String health = TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex1 + 2]);
            String defense = TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex1 + 3])
                    .replace("(", "").split("\\)")[0].replace("%", "");
            String damage = TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex2 + 2]);
            String attacks = TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex2 + 3])
                    .replace("(", "").split("\\)")[0].replace("x", "");

//            System.out.println(bossbarWords);
//                System.out.println(bossInfo.getName().getUnformattedText());
//            System.out.println("Health: " + health + ", Defense: " + defense + ", Damage: " + damage + ", Attacks: " + attacks);

            double ehp = Math.round(Double.parseDouble(health) / (1.0 - (Double.parseDouble(defense) / 100.0)));
            double lowerDps = Double.parseDouble(damage.split("-")[0]) * Double.parseDouble(attacks);
            double higherDps = Double.parseDouble(damage.split("-")[1]) * Double.parseDouble(attacks);
            if (maxEhp == 0) {
                maxEhp = ehp;
                previousEhp = ehp;
            }
//            System.out.println(ehp + "");
//            System.out.println(lowerDps + "");
//            System.out.println(higherDps + "");
            long time = (System.currentTimeMillis() - warStartTime) / 1000;
            if (time != previousTime) {
                dps = previousEhp - ehp;
                previousEhp = ehp;
            }
            previousTime = time;
            ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();

            Renderer.drawCenteredShadowedString(time + " Seconds", scaledResolution.getScaledWidth() / 2, 100, Color.CYAN);
            Renderer.drawCenteredShadowedString(String.format("%,.1f", dps) + " DPS", scaledResolution.getScaledWidth() / 2, 110, Color.WHITE);

//                List<String> newMessageWords = new ArrayList<>(Arrays.asList(bossbarWords.clone()));
//                newMessageWords.set(startIndex1 + 3, TextFormatting.GRAY + "(" + TextFormatting.GOLD + defense + "%" + TextFormatting.GRAY + ")"
//                        + TextFormatting.DARK_PURPLE + " <3 " + (int) ehp + TextFormatting.GRAY);
//                newMessageWords.set(startIndex2 + 3, TextFormatting.GRAY + "(" + TextFormatting.DARK_AQUA + attacks + "x" + TextFormatting.GRAY + ")"
//                        + TextFormatting.DARK_PURPLE + " ☠ " + (int) lowerDps + "-" + (int) higherDps);

//                for (int i = startIndex1 + 4; i < startIndex2; i++) {
//                    newMessageWords.remove(i);
//                }
//                for (int i = startIndex2 + 4; i < newMessageWords.size(); i++) {
//                    newMessageWords.remove(i);
//                }

//                bossInfo.setName(new TextComponentString(String.join(" ", newMessageWords)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warEnded(boolean warWon) {
        Avomod.getMC().player.sendMessage(new TextComponentString("The war lasted for: " + TextFormatting.AQUA + ((System.currentTimeMillis() - warStartTime) / 1000) + TextFormatting.WHITE + " Seconds"));
        Avomod.getMC().player.sendMessage(new TextComponentString("Average DPS: " + String.format("%,.1f", (warWon ? maxEhp : maxEhp - previousEhp) / ((System.currentTimeMillis() - warStartTime) / 1000))));
        resetValues();
    }

    private static void resetValues() {
        warStartTime = -1;
        previousTime = 0;
        previousEhp = 0;
        dps = 0;
        maxEhp = 0;
    }
}
