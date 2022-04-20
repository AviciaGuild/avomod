package tk.avicia.avomod.features;


import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.CustomFile;
import tk.avicia.avomod.core.structures.WarObject;
import tk.avicia.avomod.core.structures.render.MultipleElements;
import tk.avicia.avomod.core.structures.render.Rectangle;
import tk.avicia.avomod.core.structures.render.Text;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.TerritoryDataApi;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarTracker {
    private static long lastWarBar;
    private List<String> members = new ArrayList<>();
    private List<String> uuids = new ArrayList<>();

    public static void warStart(String territoryName, List<String> members) {
        List<String> filteredMembers = members.stream().filter(e -> !e.equals(Avomod.getMC().player.getName())).collect(Collectors.toList());
        WarObject currentWarObject = new WarObject(territoryName, filteredMembers);
        addWar(currentWarObject);
    }

    public static MultipleElements getElementsToDraw() {
        long weeklyWars = getWars(System.currentTimeMillis() - 604800000L);

        String plural = "";
        if (weeklyWars != 1) {
            plural = "s";
        }

        String text = String.format("%s war%s", weeklyWars, plural);
        int rectangleWidth = Avomod.getMC().fontRenderer.getStringWidth(text) + 4;
        int rectangleHeight = 12;
        float scale = 1.5F;

        float x = Utils.getStartX("weeklyWars", rectangleWidth, scale);
        float y = Utils.getStartY("weeklyWars", 1, scale);

        Rectangle newRectangle = new Rectangle(x, y,
                rectangleWidth, rectangleHeight, scale, new Color(100, 100, 100, 100));
        Text newText = new Text(text, x + 2, y + 2, scale, Color.MAGENTA);

        return new MultipleElements("weeklyWars", scale, Arrays.asList(newRectangle, newText));
    }

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

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null) return;

        if (message.startsWith("[WAR] The war battle will start in 25 seconds.")) {
            new Thread(() -> {
                try {
                    this.members = new ArrayList<>();
                    this.uuids = new ArrayList<>();
                    EntityPlayerSP player = Minecraft.getMinecraft().player;
                    int searchRadius = 100;

                    for (int i = 0; i < 25; i++) {
                        List<EntityPlayer> newPlayers = Minecraft.getMinecraft().world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(
                                player.posX - searchRadius, player.posY - searchRadius, player.posZ - searchRadius,
                                player.posX + searchRadius, player.posY + searchRadius, player.posZ + searchRadius
                        ));
                        List<String> newMembers = newPlayers.stream().map(EntityPlayer::getName).collect(Collectors.toList());
                        List<String> newUuids = newPlayers.stream().map(e -> e.getUniqueID().toString()).collect(Collectors.toList());

                        newMembers.removeAll(this.members);
                        this.members.addAll(newMembers);

                        newUuids.removeAll(this.uuids);
                        this.uuids.addAll(newUuids);

                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @SubscribeEvent
    public void bossInfo(RenderGameOverlayEvent.BossInfo event) {
        try {
            BossInfo bossInfo = event.getBossInfo();
            String bossbarName = TextFormatting.getTextWithoutFormattingCodes(bossInfo.getName().getFormattedText());
            if (bossbarName == null) return;

            if (bossbarName.contains("Tower")) {
                if (System.currentTimeMillis() - lastWarBar < 25000) {
                    lastWarBar = System.currentTimeMillis();
                    return;
                }
                lastWarBar = System.currentTimeMillis();

                String[] territorySplit = bossbarName.split(" - ")[0].split("] ");
                if (territorySplit.length == 1) return;
                String[] territoryWords = territorySplit[1].split(" ");

                String territoryName = String.join(" ", Arrays.copyOfRange(territoryWords, 0, territoryWords.length - 1));

                if (TerritoryDataApi.territoryList != null && TerritoryDataApi.territoryList.contains(territoryName)) {
                    WarTracker.warStart(territoryName, this.members);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderOverlay(RenderGameOverlayEvent.Chat event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getConfigBoolean("displayWeeklyWarcount")) {
            WarTracker.getElementsToDraw().draw();
        }
    }
}
