package tk.avicia.avomod.features;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.render.Element;
import tk.avicia.avomod.core.structures.render.MultipleElements;
import tk.avicia.avomod.core.structures.render.Rectangle;
import tk.avicia.avomod.core.structures.render.TextWithShadow;
import tk.avicia.avomod.utils.Renderer;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class WarDPS {
    public static long warStartTime = -1;
    public static long firstDamageTime = -1;
    public static String previousTerritoryName = "";
    public static long lastTimeInWar = 0;
    private static long previousTime = 0;
    private static double previousEhp = 0;
    private static double dps = 0;
    private static List<Double> previousFiveEhp = new ArrayList<>();
    private static double dpsFiveSec = 0;
    private static double maxEhp = 0;
    private static double dpsSinceStart = 0;
    private static double timeRemaining = 0;
    private static String initialStats = "";
    private static String latestStats = "";

    public static void execute(String[] bossbarWords) {
        try {
            if (System.currentTimeMillis() - lastTimeInWar > 119 * 1000) {
                // If the last war happened more than 2 minutes ago, reset the previous territory name,
                // in case you war the same territory twice in a row
                previousTerritoryName = "";
            }

            lastTimeInWar = System.currentTimeMillis();
            int startIndex1 = Arrays.asList(bossbarWords).indexOf("-");
            int startIndex2 = Arrays.asList(bossbarWords).lastIndexOf("-");
            StringBuilder territoryName = new StringBuilder();
            for (int i = 1; i < startIndex1 - 1; i++) {
                territoryName.append(TextFormatting.getTextWithoutFormattingCodes(bossbarWords[i])).append(" ");
            }

            if (!territoryName.toString().equals(previousTerritoryName)) {
                newWar();
                previousTerritoryName = territoryName.toString();
                warStartTime = System.currentTimeMillis();
                initialStats = String.join(" ", bossbarWords);
            }
            latestStats = String.join(" ", bossbarWords);

            String health = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex1 + 2]));
            String defense = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex1 + 3]))
                    .replace("(", "").split("\\)")[0].replace("%", "");
            String damage = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex2 + 2]));
            String attacks = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(bossbarWords[startIndex2 + 3]))
                    .replace("(", "").split("\\)")[0].replace("x", "");

            double ehp = Math.round(Double.parseDouble(health) / (1.0 - (Double.parseDouble(defense) / 100.0)));
            double lowerDps = Double.parseDouble(damage.split("-")[0]) * Double.parseDouble(attacks);
            double higherDps = Double.parseDouble(damage.split("-")[1]) * Double.parseDouble(attacks);

            if (maxEhp == 0) {
                maxEhp = ehp;
                previousEhp = ehp;
                previousFiveEhp.add(ehp);
            }

            long time = (System.currentTimeMillis() - warStartTime) / 1000;
            if (time != previousTime) {
                dps = previousEhp - ehp;
                previousEhp = ehp;

                if (firstDamageTime == -1 && dps > 0) {
                    firstDamageTime = System.currentTimeMillis();
                }

                if (previousFiveEhp.size() == 5) {
                    previousFiveEhp.remove(0);
                }

                previousFiveEhp.add(ehp);
                dpsFiveSec = Math.floor((previousFiveEhp.get(0) - ehp) / 5);

                if (firstDamageTime != -1 && System.currentTimeMillis() - firstDamageTime > 0) {
                    dpsSinceStart = (maxEhp - previousEhp) / ((System.currentTimeMillis() - firstDamageTime) / 1000.0);
                    timeRemaining = Math.floor(previousEhp / dpsSinceStart);
                }
            }

            previousTime = time;
            getElementsToDraw(time, ehp, lowerDps, higherDps).draw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void warEnded(boolean warWon) {
        Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Time in War: " + TextFormatting.AQUA +
                ((System.currentTimeMillis() - warStartTime) / 1000) + "s"));
        Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Average DPS: " + TextFormatting.AQUA +
                String.format("%,.1f", (warWon ? maxEhp : maxEhp - previousEhp) / ((System.currentTimeMillis() - firstDamageTime) / 1000))));

        String[] statsSplit = initialStats.split(" - ");
        if (statsSplit.length >= 2) {
            Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Initial Tower Stats: " +
                    String.join(" - ", Arrays.copyOfRange(statsSplit, 1, statsSplit.length))));
        }

        if (!warWon) {
            String[] latestStatsSplit = latestStats.split(" - ");
            if (latestStatsSplit.length >= 2) {
                Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Final Tower Stats: " +
                        String.join(" - ", Arrays.copyOfRange(latestStatsSplit, 1, latestStatsSplit.length))));
            }
        }

        newWar();
    }

    private static void newWar() {
        warStartTime = -1;
        firstDamageTime = -1;
        previousTime = 0;
        previousEhp = 0;
        previousFiveEhp = new ArrayList<>();
        dps = 0;
        dpsFiveSec = 0;
        dpsSinceStart = 0;
        maxEhp = 0;
        timeRemaining = 0;
        previousTerritoryName = "";
        initialStats = "";
        latestStats = "";

        AuraHandler.firstAura = 0;
        AuraHandler.auraTimer = 0;
    }

    public static MultipleElements getElementsToDraw(long time, double towerEhp, double lowerTowerDps, double upperTowerDps) {
        String[] stats = new String[]{
                String.format("%s Seconds", time),
                String.format("Tower EHP: %s", Utils.parseReadableNumber(towerEhp, 2)),
                String.format("Tower DPS: %s-%s", Utils.parseReadableNumber(lowerTowerDps, 2), Utils.parseReadableNumber(upperTowerDps, 2)),
                String.format("Team DPS/1s: %s", Utils.parseReadableNumber(dps, 2)),
                String.format("Team DPS/5s: %s", Utils.parseReadableNumber(dpsFiveSec, 2)),
                String.format("Team DPS (total): %s", Utils.parseReadableNumber(dpsSinceStart, 2)),
                String.format("Estimated Time Remaining: %ss", (int) timeRemaining)
        };

        if (dpsSinceStart == 0) {
            stats[6] = "Estimated Time Remaining: Unknown";
        }

        int maxWidth = Collections.max(Arrays.stream(stats).map(Renderer::getStringWidth).collect(Collectors.toList()));
        List<Element> elementList = new ArrayList<>();
        float y = Utils.getStartY("warDPS", 1F, 18 + (10 * stats.length));
        float x = Utils.getStartX("warDPS", maxWidth + 10, 1F);

        elementList.add(new Rectangle(x, y, maxWidth + 10, 18 + (10 * stats.length), new Color(0, 0, 0, 100)));
        elementList.add(new TextWithShadow(TextFormatting.BOLD + "War Info", x + 4, y + 4, Color.CYAN));

        int additionalHeight = 15;
        for (String stat : stats) {
            x = Utils.getStartX("warDPS", Avomod.getMC().fontRenderer.getStringWidth(stat), 1F);
            elementList.add(new TextWithShadow(stat, x + 4, y + additionalHeight, Color.WHITE));
            additionalHeight += 10;
        }

        return new MultipleElements("warDPS", 1F, elementList);
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null) return;

        if (Avomod.getConfigBoolean("dpsInWars") && System.currentTimeMillis() - lastTimeInWar < 5000 && message.contains(previousTerritoryName.trim())) {
            // If you saw a tower health bar less than 5 seconds ago (if you're in a war)
            if (message.startsWith("[WAR] You have taken control of ")) {
                warEnded(true);
            }
            if (message.startsWith("[WAR] Your guild has lost the war for ") || message.startsWith("Your active attack was canceled and refunded to your headquarter")) {
                warEnded(false);
            }
        }
    }

    @SubscribeEvent
    public void bossInfo(RenderGameOverlayEvent.BossInfo event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        try {
            BossInfo bossInfo = event.getBossInfo();
            String bossbarName = bossInfo.getName().getFormattedText();
            String[] bossbarWords = bossbarName.split(" ");

            if (Avomod.getConfigBoolean("dpsInWars") && bossbarName.contains("Tower") && bossbarWords.length >= 6) {
                try {
                    execute(bossbarWords);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
