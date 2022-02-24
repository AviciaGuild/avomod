package tk.avicia.avomod.webapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class OnlinePlayers {
    private Thread thread;
    private JsonObject onlinePlayerData;

    public OnlinePlayers() {
        getAPIData();
    }

    private void getAPIData() {
        try {
            URL urlObject = new URL("https://api.wynncraft.com/public_api.php?action=onlinePlayers");
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

                this.onlinePlayerData = new JsonParser().parse(response.toString()).getAsJsonObject();
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateData() {
        // Updates the data, but it can be called at most once per minute to not spam requests to the api
        if (thread == null) {
            thread = new Thread(() -> {
                getAPIData();
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        if (!thread.isAlive()) {
            thread.start();
        }
    }

//    public JsonObject getOnlinePlayerData() {
//        return onlinePlayerData;
//    }

    public boolean isPlayerOnline(String username) {
        Set<Map.Entry<String, JsonElement>> worlds = onlinePlayerData.entrySet();
        for (Map.Entry<String, JsonElement> world : worlds) {
            if (world.getKey().equals("request")) continue;
            JsonArray players = world.getValue().getAsJsonArray();
            for (JsonElement player : players) {
                if (player.getAsString().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public JsonArray getWorldPlayers(String world) {
        if (world.matches("^\\d+")) {
            world = "WC" + world;
        }

        return this.onlinePlayerData.getAsJsonArray(world);
    }
}
