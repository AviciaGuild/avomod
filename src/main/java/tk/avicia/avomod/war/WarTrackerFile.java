package tk.avicia.avomod.war;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.CustomFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarTrackerFile {
    public static void addWar(WarObject warObject) {
        CustomFile warFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/wars/wars.json");
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            savedWars.addProperty("wars", "");
        }

        String newWarsString = savedWars.get("wars").getAsString() + warObject + "|";
        savedWars.addProperty("wars", newWarsString);
        warFile.writeJson(savedWars);

        System.out.println("Added to JSON: " + newWarsString);
    }

    public static long getWars(long timeSince) {
        CustomFile warFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/wars/wars.json");
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            return 0;
        }

        String wars = savedWars.get("wars").getAsString();
        List<String> warsAfter = Arrays.stream(wars.split("\\|")).filter(e -> Long.parseLong(e.split("/")[2]) > timeSince).collect(Collectors.toList());
        return warsAfter.size();
    }

    public static long timeOfFirstWar() {
        CustomFile warFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/wars/wars.json");
        JsonObject savedWars = warFile.readJson();

        if (!savedWars.has("wars")) {
            return 0;
        }

        String[] wars = savedWars.get("wars").getAsString().split("\\|");
        if (wars.length == 0) return 0;

        return Long.parseLong(wars[0].split("/")[2]);
    }
}
