package tk.avicia.avomod.webapi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import tk.avicia.avomod.Avomod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private static JsonObject updateData;

    public static void checkUpdate() {
        try {
            URL urlObject = new URL("https://script.google.com/macros/s/AKfycbxMzhZElB1-ZX3TeQeWuGrdxgdWhIfEolbMDHz15LDC1EPSZrYnOIaMsRDjKdj4OBztyg/exec");
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

                JsonObject updateData = new JsonParser().parse(response.toString()).getAsJsonObject();
                if (!updateData.get("version").getAsString().equals(Avomod.VERSION)) {
                    UpdateChecker.updateData = updateData;
                    updateMessage();
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMessage() {
        String version = updateData.get("version").getAsString();
        JsonArray changelog = updateData.get("changelog").getAsJsonArray();

        if (Avomod.getMC().player == null) return;

        String message = "\n\n\n" + TextFormatting.AQUA + "Avomod has an update! Please update to version "
                + TextFormatting.GOLD + version
                + TextFormatting.AQUA + " as soon as you can. \n"
                + TextFormatting.RED + TextFormatting.BOLD + "Changelog: ";

        for (JsonElement element : changelog) {
            message += "\n" + TextFormatting.YELLOW + element.getAsString();
        }

        String linkMessage = TextFormatting.LIGHT_PURPLE + "You can find the newest version at " + TextFormatting.GOLD + TextFormatting.UNDERLINE + "https://avicia.tk/avomod";
        ITextComponent linkComponent = new TextComponentString(linkMessage).setStyle(
                new Style().setClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_URL, "https://avicia.tk/avomod")));

        Avomod.getMC().player.sendMessage(new TextComponentString(message));
        Avomod.getMC().player.sendMessage(linkComponent);
    }
}
