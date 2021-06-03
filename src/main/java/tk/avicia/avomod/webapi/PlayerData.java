package tk.avicia.avomod.webapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerData {
    private String playerName;
    private String playerData;

    public PlayerData(String playerName) {
        this.playerName = playerName;

        try {
            URL urlObject = new URL("https://api.wynncraft.com/v2/player/" + this.playerName + "/stats");
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                this.playerData = response.toString();
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getChestCount() {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonTree = jsonParser.parse(this.playerData);
        JsonObject jsonObject = jsonTree.getAsJsonObject();
        JsonArray classes = ((JsonObject) jsonObject.getAsJsonArray("data").get(0)).getAsJsonArray("classes");

        int totalChestCount = 0;

        for (int i = 0; i < classes.size(); i++) {
            int classChestCount = ((JsonObject) classes.get(i)).get("chestsFound").getAsInt();
            totalChestCount += classChestCount;
        }

        return totalChestCount;
    }
}
