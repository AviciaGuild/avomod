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
        int screenWidth = scaledResolution.getScaledWidth();
        int y = 20;

        int xPos = Avomod.getMC().player.getPosition().getX();
        int zPos = Avomod.getMC().player.getPosition().getZ();
        String currentTerritory = Avomod.territoryData.coordinatesInTerritory(new Tuple<>(xPos, zPos));

        for (Tuple<String, String> attack : upcomingAttacksSplit) {
            String savedDefense = savedDefenses.get(attack.y);
            if (savedDefense == null) {
                savedDefense = TerritoryData.getTerritoryDefense(attack.y);
                savedDefenses.put(attack.y, savedDefense);
            }

            if (savedDefense.equals("Low") || savedDefense.equals("Very Low")) {
                savedDefense = TextFormatting.GREEN + savedDefense;
            } else if (savedDefense.equals("Medium")) {
                savedDefense = TextFormatting.YELLOW + savedDefense;
            } else {
                savedDefense = TextFormatting.RED + savedDefense;
            }

            String message = TextFormatting.GOLD + attack.y + " (" + savedDefense + TextFormatting.GOLD + ") " + TextFormatting.AQUA + attack.x;
            if (attack.y.equals(currentTerritory)) {
                message = TextFormatting.LIGHT_PURPLE + "" + TextFormatting.BOLD + attack.y + TextFormatting.RESET + TextFormatting.GOLD + " (" + savedDefense + TextFormatting.GOLD + ") " + TextFormatting.AQUA + attack.x;
            }

            int width = Avomod.getMC().fontRenderer.getStringWidth(message);
            int x = screenWidth - width - 5;
            attackCoordinates.put(attack.y, new ScreenCoordinates(x - 2, y, x + width + 2, y + 12));

            Renderer.drawRect(new Color(100, 100, 100, 100), x - 2, y, width + 2, 12);
            Renderer.drawString(message, x, y + 2, new Color(255, 170, 0));
            y += 12;
        }
    }
}
