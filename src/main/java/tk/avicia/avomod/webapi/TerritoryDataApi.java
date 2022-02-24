package tk.avicia.avomod.webapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tk.avicia.avomod.utils.Coordinates;
import tk.avicia.avomod.utils.Tuple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TerritoryDataApi {
    public static List<String> territoryList;
    private JsonObject territoryData;

    public TerritoryDataApi() {
        this.makeApiRequest();
    }

    private void makeApiRequest() {
        try {
            URL urlObject = new URL("https://api.wynncraft.com/public_api.php?action=territoryList");
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                this.territoryData = new JsonParser().parse(response.toString()).getAsJsonObject();
                territoryList = this.territoryData.getAsJsonObject("territories").entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(10000);
                    this.makeApiRequest();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            });
            thread.start();
        }
    }

    public String coordinatesInTerritory(Tuple<Integer, Integer> coordinates) {
        if (this.territoryData == null) return null;
        for (Map.Entry<String, JsonElement> territory : this.territoryData.getAsJsonObject("territories").entrySet()) {
            JsonObject locationObject = territory.getValue().getAsJsonObject().getAsJsonObject("location");
            int startX = Math.min(locationObject.get("startX").getAsInt(), locationObject.get("endX").getAsInt());
            int startY = Math.min(locationObject.get("startY").getAsInt(), locationObject.get("endY").getAsInt());
            int endX = Math.max(locationObject.get("startX").getAsInt(), locationObject.get("endX").getAsInt());
            int endY = Math.max(locationObject.get("startY").getAsInt(), locationObject.get("endY").getAsInt());

            if (coordinates.x > startX && coordinates.x < endX && coordinates.y > startY && coordinates.y < endY) {
                return territory.getKey();
            }
        }

        return null;
    }

    public Coordinates getMiddleOfTerritory(String territory) {
        if (this.territoryData == null) return null;
        JsonObject territoryObject = this.territoryData.getAsJsonObject("territories").getAsJsonObject(territory);
        if (territoryObject == null) return null;

        JsonObject locationObject = territoryObject.getAsJsonObject("location");
        if (locationObject.isJsonNull()) return null;

        int middleX = (locationObject.get("startX").getAsInt() + locationObject.get("endX").getAsInt()) / 2;
        int middleZ = (locationObject.get("startY").getAsInt() + locationObject.get("endY").getAsInt()) / 2;
        return new Coordinates(middleX, 0, middleZ);
    }
}
