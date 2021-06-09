package tk.avicia.avomod.events;

import net.minecraft.advancements.Advancement;
import net.minecraft.util.text.TextFormatting;
import tk.avicia.avomod.Avomod;

import java.util.*;

public class TerritoryData {
    private Map<String, List<String>> connections = new HashMap<>();

    public TerritoryData() {
        try {
            for (Advancement advancement : Avomod.getMC().getConnection().getAdvancementManager().getAdvancementList().getAdvancements()) {
                if (advancement.getDisplay() != null) {
                    try {
                        String territoryName = advancement.getDisplay().getTitle().getUnformattedText().trim();
                        String territoryData = TextFormatting
                                .getTextWithoutFormattingCodes(advancement.getDisplay().getDescription().getUnformattedText())
                                .trim().replaceAll("\\s+", " ").replaceAll("\\n", " ");
//                        System.out.println(territoryData);
                        if (territoryData.split("Trading Routes: - ").length == 2) {
                            if (!connections.containsKey(territoryName)) {
                                connections.put(territoryName, new ArrayList<>());
                            }
                            String[] conns = territoryData.split("Trading Routes: - ")[1].split(" - ");
                            for (String conn : conns) {
                                if (!connections.get(territoryName).contains(conn)) {
                                    connections.get(territoryName).add(conn);
                                }
                                if (!connections.containsKey(conn)) {
                                    connections.put(conn, new ArrayList<>());
                                    connections.get(conn).add(territoryName);
                                } else {
                                    connections.get(conn).add(territoryName);
                                }
                            }
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

    public List<String> getConnections(String territory) {
        return connections.getOrDefault(territory, null);
    }

    public boolean haveConnections(String goalTerr, String startTerr, List<String> ownedTerritories) {
        List<String> queue = new ArrayList<>();
        queue.add(startTerr);
        List<String> checkedTerrs = new ArrayList<>();
        while (!queue.isEmpty()) {
            String currTerr = queue.get(0);
            queue.remove(0);
            checkedTerrs.add(currTerr);
            if (currTerr.equals(goalTerr)) {
                return true;
            }
            if (getConnections(currTerr) != null) {
                for (String connection : getConnections(currTerr)) {
                    if (!checkedTerrs.contains(connection) && ownedTerritories.contains(connection)) {
                        queue.add(connection);
                    }
                }
            }
        }
        return false;
    }

}
