package tk.avicia.avomod.webapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class GuildNameFromTag {
    private Set<Map.Entry<String, JsonElement>> guildMatches;
    private String guildName;

    public GuildNameFromTag(String guildTag) {
        try {
            URL urlObject = new URL("http://avicia.ga/api/tag/?tag=" + guildTag);
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
                if (response.toString().charAt(0) == '{') {
                    JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
                    guildMatches = jsonObject.entrySet();
                    for (Map.Entry<String, JsonElement> match : guildMatches) {
                        guildName = match.getValue().getAsString();
                        break;
                    }
                } else {
                    guildName = new JsonParser().parse(response.toString()).getAsString();
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasMatch() {
        return !guildName.equals("null");
    }

    public boolean hasMultipleMatches() {
        return guildMatches != null;
    }

    public Set<Map.Entry<String, JsonElement>> getGuildMatches() {
        return guildMatches;
    }

    public String getGuildName() {
        return guildName;
    }
}
