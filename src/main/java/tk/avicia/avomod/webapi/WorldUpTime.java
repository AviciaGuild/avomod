package tk.avicia.avomod.webapi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tk.avicia.avomod.utils.Tuple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldUpTime {
    JsonObject worldUpTimeData;
    private java.util.Comparator<Map.Entry<String, JsonElement>> mapComparator =
            Comparator.comparingInt(m -> m.getValue().getAsJsonObject().get("age").getAsInt());

    public WorldUpTime() {
        try {
            URL urlObject = new URL("http://avicia.ga/api/up/");
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

                this.worldUpTimeData = new JsonParser().parse(response.toString()).getAsJsonObject();
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map.Entry<String, JsonElement>> getWorldUpTimeData() {
        ArrayList<Map.Entry<String, JsonElement>> worldUpTimes = new ArrayList<>(worldUpTimeData.entrySet());
        worldUpTimes.sort(mapComparator);
        return worldUpTimes;
    }

    public Tuple<String, Integer> getAge(String world) throws NoSuchFieldException {
        Pattern pattern = Pattern.compile("(^wc[0-9]+)|(^[0-9]+$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(world);
        boolean matchFound = matcher.find();
        if (matchFound) {
            String wc = "";
            if (world.toUpperCase(Locale.ROOT).startsWith("WC")) {
                wc = world;
            } else {
                wc = "WC" + world;
            }
            if (worldUpTimeData.has(wc)) {
                int age = worldUpTimeData.get(wc).getAsJsonObject().get("age").getAsInt();
                return new Tuple<>(wc, age);
            } else {
                throw new NoSuchFieldException();
            }
        } else {
            throw new NoSuchFieldException();
        }
    }
}
