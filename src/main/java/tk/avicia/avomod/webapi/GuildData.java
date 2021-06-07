package tk.avicia.avomod.webapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GuildData {
    private String guildName;
    private JsonObject guildData;

    public GuildData(String guildName) throws IllegalArgumentException {
        try {
            URL urlObject = new URL("https://api.wynncraft.com/public_api.php?action=guildStats&command=" + guildName);
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

                this.guildData = new JsonParser().parse(response.toString()).getAsJsonObject();
                this.guildName = this.guildData.get("name").getAsString();
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
//            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    public String getGuildName() {
        return guildName;
    }

    public JsonArray getMembers() {
        return this.guildData.getAsJsonArray("members");
    }

    public String getWithRankFormatting(String member) {
        JsonArray members = getMembers();
        Map<String, String> rankStars = new HashMap<>();
        // For some reason the stars show up as some weird combination of characters instead of stars,
        // the same seems to be happening to a lot of special characters. No idea why or how
        rankStars.put("OWNER", "\u2605\u2605\u2605\u2605\u2605");
        rankStars.put("CHIEF", "\u2605\u2605\u2605\u2605");
        rankStars.put("STRATEGIST", "\u2605\u2605\u2605");
        rankStars.put("CAPTAIN", "\u2605\u2605");
        rankStars.put("RECRUITER", "\u2605");
        rankStars.put("RECRUIT", "");
//        rankStars.put("OWNER", "*****");
//        rankStars.put("CHIEF", "****");
//        rankStars.put("STRATEGIST", "***");
//        rankStars.put("CAPTAIN", "**");
//        rankStars.put("RECRUITER", "*");
//        rankStars.put("RECRUIT", "");
        for (JsonElement jsonElement : members) {
            JsonObject memberData = jsonElement.getAsJsonObject();
            if(memberData.get("name").getAsString().equals(member)){
                return rankStars.get(memberData.get("rank").getAsString()) + memberData.get("name").getAsString();
            }
        }
        return member;
    }

    public String getOwner(){
        JsonArray members = this.guildData.getAsJsonArray("members");
        for (JsonElement member : members) {
            System.out.println(member.getAsJsonObject().get("rank").toString());
            System.out.println(member.getAsJsonObject().get("rank").toString().equals("OWNER"));
            if(member.getAsJsonObject().get("rank").getAsString().equals("OWNER")){
                return member.getAsJsonObject().get("name").getAsString();
            }
        }

        return "No Owner";
    }
}
