package tk.avicia.avomod.war;

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
import tk.avicia.avomod.events.AttacksMenu;
import tk.avicia.avomod.events.AuraHandler;
import tk.avicia.avomod.utils.Utils;
import tk.avicia.avomod.webapi.TerritoryDataApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarEvents {
    private static long lastWarBar;
    private List<String> members = new ArrayList<>();
    private List<String> uuids = new ArrayList<>();

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
                if (System.currentTimeMillis() - WarEvents.lastWarBar < 25000) {
                    WarEvents.lastWarBar = System.currentTimeMillis();
                    return;
                }
                WarEvents.lastWarBar = System.currentTimeMillis();

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

        // The Chat RenderGameOverlayEvent renders stuff normally, it disappears in f1, you can see it when your
        // inventory is open and you can make stuff transparent
        List<String> upcomingAttacks = Utils.getUpcomingAttacks();
        if (Avomod.getConfigBoolean("attacksMenu")) {
            AttacksMenu.draw(upcomingAttacks);
        }
        if (Avomod.getConfigBoolean("auraPing")) {
            AuraHandler.draw();
        }
        if (Avomod.getConfigBoolean("displayWeeklyWarcount")) {
            WarTracker.draw();
        }
    }
}
