package tk.avicia.avomod.utils;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.features.AttacksMenu;
import tk.avicia.avomod.webapi.ApiRequest;

import java.util.HashMap;
import java.util.Map;

public class TerritoryData {
    private final static Map<String, String> defenses = new HashMap<>();
    private int tick = 0;

    public static void updateTerritoryData() {
        try {
            if (Avomod.getMC().getConnection() == null) return;

            for (Advancement advancement : Avomod.getMC().getConnection().getAdvancementManager().getAdvancementList().getAdvancements()) {
                if (advancement.getDisplay() != null) {
                    try {
                        String territoryName = advancement.getDisplay().getTitle().getUnformattedText().trim();
                        String territoryDataFormatted = TextFormatting.getTextWithoutFormattingCodes(advancement.getDisplay().getDescription().getUnformattedText());
                        if (territoryDataFormatted == null) return;

                        String territoryData = territoryDataFormatted.trim().replaceAll("\\s+", " ").replaceAll("\\n", " ");

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

    public static String getTerritoryDefense(String territoryName, Long warTimestamp) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
                String result = ApiRequest.get(String.format("https://script.google.com/macros/s/AKfycbw7lRN6tojW1RjsPeC7bhVNsGETBl_LZEc6bZKXAHG95HB_UC4NKQMm9LGmuvT8KU-R-A/exec?territory=%s&timestamp=%s", territoryName.replace(" ", "%20"), System.currentTimeMillis()));
                if (result == null) throw new NullPointerException();

                String defense = result.split("\\|")[0];
                Long savedWarTimestamp = Long.parseLong(result.split("\\|")[1]);

                if (defense != null && !defense.equals("null")) {
                    AttacksMenu.savedDefenses.put(territoryName, new Tuple<>(defense, savedWarTimestamp));
                } else {
                    AttacksMenu.savedDefenses.put(territoryName, new Tuple<>(defenses.getOrDefault(territoryName, "Unknown"), savedWarTimestamp));
                }
            } catch (Exception e) {
                AttacksMenu.savedDefenses.put(territoryName, new Tuple<>(defenses.getOrDefault(territoryName, "Unknown"), warTimestamp));
                e.printStackTrace();
            }
        });
        thread.start();

        return "Retrieving...";
    }

    public static boolean hasValues() {
        return !defenses.isEmpty();
    }


    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null) return;

        tick++;
        if (tick % 60000 == 0) {
            updateTerritoryData();
        }
    }
}