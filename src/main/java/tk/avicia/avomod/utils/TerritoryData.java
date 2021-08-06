package tk.avicia.avomod.utils;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;

import java.util.HashMap;
import java.util.Map;

public class TerritoryData {
    private static Map<String, String> defenses = new HashMap<>();

    public static void updateTerritoryData() {
        try {
            for (Advancement advancement : Avomod.getMC().getConnection().getAdvancementManager().getAdvancementList().getAdvancements()) {
                if (advancement.getDisplay() != null) {
                    try {
                        String territoryName = advancement.getDisplay().getTitle().getUnformattedText().trim();
                        String territoryData = TextFormatting
                                .getTextWithoutFormattingCodes(advancement.getDisplay().getDescription().getUnformattedText())
                                .trim().replaceAll("\\s+", " ").replaceAll("\\n", " ");

                        int index1 = territoryData.indexOf("Territory Defences: ");
                        int index2 = territoryData.indexOf(" Trading Routes");

                        if (index1 != -1 && index2 != -1) {
                            String territoryDefense = territoryData.substring(index1 + 20, index2);

                            defenses.put(territoryName, territoryDefense);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static String getTerritoryDefense(String territoryName) {
        return defenses.getOrDefault(territoryName, "Unknown");
    }

    public static boolean hasValues() {
        return !defenses.isEmpty();
    }
}