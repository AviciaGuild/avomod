package tk.avicia.avomod.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;
import tk.avicia.avomod.utils.ScreenCoordinates;
import tk.avicia.avomod.utils.TerritoryData;
import tk.avicia.avomod.utils.Tuple;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class AttacksMenu {
    public static HashMap<String, ScreenCoordinates> attackCoordinates = new HashMap<>();
    private static HashMap<String, String> savedDefenses = new HashMap<>();

    public static void draw(List<String> upcomingAttacks) {
        if (upcomingAttacks.size() == 0) return;

        if (!TerritoryData.hasValues()) {
            TerritoryData.updateTerritoryData();
        }

        List<Tuple<String, String>> upcomingAttacksSplit = new ArrayList<>();
        List<String> upcomingAttackTerritories = new ArrayList<>();

        for (String upcomingAttack : upcomingAttacks) {
            String[] words = TextFormatting.getTextWithoutFormattingCodes(upcomingAttack).split(" ");
            String time = words[1];
            String territory = String.join(" ", Arrays.copyOfRange(words, 2, words.length));

            upcomingAttacksSplit.add(new Tuple<>(time, territory));
            upcomingAttackTerritories.add(territory);
        }

        List<String> terrsToRemove = new ArrayList<>();
        for (Map.Entry<String, String> savedDefense : savedDefenses.entrySet()) {
            if (!upcomingAttackTerritories.contains(savedDefense.getKey())) {
                terrsToRemove.add(savedDefense.getKey());
            }
        }

        for (String terrToRemove : terrsToRemove) {
            savedDefenses.remove(terrToRemove);
            attackCoordinates.remove(terrToRemove);

            if (terrToRemove.equals(Avomod.compassTerritory)) {
                Avomod.compassTerritory = null;
                Avomod.compassLocation = null;
            }
        }

        upcomingAttacksSplit.sort((o1, o2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date d1 = sdf.parse(o1.x);
                Date d2 = sdf.parse(o2.x);
                return (int) (d1.getTime() - d2.getTime());
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });

        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenHeight = scaledResolution.getScaledHeight();
        int y = screenHeight / 3;
        int longestLength = upcomingAttacksSplit.stream().map(e -> Avomod.getMC().fontRenderer.getStringWidth(e.x + "   " + e.y + " (" + TerritoryData.getTerritoryDefense(e.y) + ")")).max(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        }).get();
        Color background = new Color(0, 0, 255, 150);

        int xPos = Avomod.getMC().player.getPosition().getX();
        int zPos = Avomod.getMC().player.getPosition().getZ();
        String currentTerritory = Avomod.territoryData.coordinatesInTerritory(new Tuple<>(xPos, zPos));

        for (Tuple<String, String> attack : upcomingAttacksSplit) {
            if (attack.y.equals(currentTerritory)) {
                Renderer.drawRect(new Color(0, 150, 0, 200), 0, y, longestLength, 12);
            } else {
                Renderer.drawRect(background, 0, y, longestLength, 12);
            }

            attackCoordinates.put(attack.y, new ScreenCoordinates(0, y, longestLength, y + 12));

            String savedDefense = savedDefenses.get(attack.y);
            if (savedDefense == null) {
                savedDefense = TerritoryData.getTerritoryDefense(attack.y);
                savedDefenses.put(attack.y, savedDefense);
            }

            Renderer.drawString(attack.x + "   " + attack.y + " (" + savedDefense + ")", 0, y + 2, Color.WHITE);
            y += 12;

            if (background.equals(new Color(0, 0, 255, 150))) {
                background = new Color(0, 0, 150, 150);
            } else {
                background = new Color(0, 0, 255, 150);
            }
        }
    }
}
