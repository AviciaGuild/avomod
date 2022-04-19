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
import java.util.Arrays;

public class UpdateChecker {
    private static JsonObject stableUpdateData;
    private static JsonObject betaUpdateData;

    public static void checkStableUpdate() {
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
                String currentVersion = String.join(".", Arrays.copyOfRange(Avomod.VERSION.split("\\."), 0, 2));

                if (Double.parseDouble(updateData.get("version").getAsString()) > Double.parseDouble(currentVersion)) {
                    UpdateChecker.stableUpdateData = updateData;
                    updateStableMessage();
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkBetaUpdate() {
        try {
            URL urlObject = new URL("https://script.google.com/macros/s/AKfycbz53vtXwsvWUUAZMtmjx9fdHs4VCJZaBvNpL9jDGh45b2nb8D2uvGRw89WfJAWOBUjo/exec");
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
                String currentBetaVersion = Avomod.VERSION.split("\\.")[2];
                String newestBetaVersion = updateData.get("version").getAsString().split("\\.")[2];
                String currentStableVersion = Avomod.VERSION.split("\\.")[1];
                String newestStableVersion = updateData.get("version").getAsString().split("\\.")[1];

                if (currentStableVersion.equals(newestStableVersion) && Double.parseDouble(newestBetaVersion) > Double.parseDouble(currentBetaVersion)) {
                    UpdateChecker.betaUpdateData = updateData;
                    updateBetaMessage();
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStableMessage() {
        String version = stableUpdateData.get("version").getAsString();
        JsonArray changelog = stableUpdateData.get("changelog").getAsJsonArray();

        if (Avomod.getMC().player == null) return;

        StringBuilder message = new StringBuilder("\n\n\n" + TextFormatting.AQUA + "Avomod has an update! Please update to version "
                + TextFormatting.GOLD + version
                + TextFormatting.AQUA + " as soon as you can. \n"
                + TextFormatting.RED + TextFormatting.BOLD + "Changelog: ");

        for (JsonElement element : changelog) {
            message.append("\n").append(TextFormatting.YELLOW).append(element.getAsString());
        }

        String linkMessage = TextFormatting.LIGHT_PURPLE + "You can find the newest version at " + TextFormatting.GOLD + TextFormatting.UNDERLINE + "https://avicia.ml/avomod";
        ITextComponent linkComponent = new TextComponentString(linkMessage).setStyle(
                new Style().setClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_URL, "https://avicia.ml/avomod")));

        Avomod.getMC().player.sendMessage(new TextComponentString(message.toString()));
        Avomod.getMC().player.sendMessage(linkComponent);
    }

    public static void updateBetaMessage() {
        String version = betaUpdateData.get("version").getAsString();
        JsonArray changelog = betaUpdateData.get("changelog").getAsJsonArray();

        if (Avomod.getMC().player == null) return;

        StringBuilder message = new StringBuilder("\n\n\n" + TextFormatting.AQUA + "Avomod has a beta update! The newest beta version is "
                + TextFormatting.GOLD + version + "\n"
                + TextFormatting.RED + TextFormatting.BOLD + "Changelog: ");

        for (JsonElement element : changelog) {
            message.append("\n").append(TextFormatting.YELLOW).append(element.getAsString());
        }

        String linkMessage = TextFormatting.LIGHT_PURPLE + "You can find the newest beta version at " + TextFormatting.GOLD + TextFormatting.UNDERLINE + "https://avicia.ml/avomod";
        ITextComponent linkComponent = new TextComponentString(linkMessage).setStyle(
                new Style().setClickEvent(
                        new ClickEvent(ClickEvent.Action.OPEN_URL, "https://avicia.ml/avomod")));

        Avomod.getMC().player.sendMessage(new TextComponentString(message.toString()));
        Avomod.getMC().player.sendMessage(linkComponent);
    }
}
