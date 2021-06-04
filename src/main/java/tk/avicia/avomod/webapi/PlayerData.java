package tk.avicia.avomod.webapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerData {
    private String playerName;
    private JsonObject playerData;

    public PlayerData(String playerName) throws NotFound {
        try {
            URL urlObject = new URL("https://api.wynncraft.com/v2/player/" + playerName + "/stats");
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

                this.playerData = new JsonParser().parse(response.toString()).getAsJsonObject().getAsJsonArray("data").get(0).getAsJsonObject();
                this.playerName = this.playerData.get("username").getAsString();
            } else {
                System.out.println("GET request not worked");
                throw new NotFound();
            }
        } catch (NotFound ne) {
            throw new NotFound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getChestCount() {
        JsonArray classes = this.playerData.getAsJsonArray("classes");

        int totalChestCount = 0;

        for (int i = 0; i < classes.size(); i++) {
            int classChestCount = classes.get(i).getAsJsonObject().get("chestsFound").getAsInt();
            totalChestCount += classChestCount;
        }

        return totalChestCount;
    }

    public String getWorld() {
        JsonObject locationData = this.playerData.getAsJsonObject("meta").getAsJsonObject("location");

        if (locationData.get("online").getAsBoolean()) {
            return locationData.get("server").getAsString();
        }

        return "";
    }

    public String getGuild() {
        JsonObject guildData = this.playerData.getAsJsonObject("guild");
        JsonElement guildName = guildData.get("name");

        if (guildName.isJsonNull()) return null;

        return guildName.getAsString();
    }

    public String getGuildRank() {
        JsonObject guildData = this.playerData.getAsJsonObject("guild");
        JsonElement guildName = guildData.get("rank");

        if (guildName.isJsonNull()) return null;

        return guildName.getAsString();
    }

    public String getLastJoin() {
        return this.playerData.getAsJsonObject("meta").get("lastJoin").getAsString();
    }
}
