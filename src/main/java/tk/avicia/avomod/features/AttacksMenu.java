package tk.avicia.avomod.features;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.Coordinates;
import tk.avicia.avomod.core.structures.ScreenCoordinates;
import tk.avicia.avomod.core.structures.Tuple;
import tk.avicia.avomod.core.structures.render.Element;
import tk.avicia.avomod.core.structures.render.MultipleElements;
import tk.avicia.avomod.core.structures.render.Rectangle;
import tk.avicia.avomod.core.structures.render.Text;
import tk.avicia.avomod.utils.BeaconManager;
import tk.avicia.avomod.utils.TerritoryData;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AttacksMenu {
    private static final HashMap<String, ScreenCoordinates> attackCoordinates = new HashMap<>();
    public static HashMap<String, Tuple<String, Long>> savedDefenses = new HashMap<>();

    public static MultipleElements getElementsToDraw(List<String> upcomingAttacks, boolean sample) {
        if (upcomingAttacks.size() == 0) {
            BeaconManager.soonestTerritory = null;
            BeaconManager.soonestTerritoryLocation = null;
            BeaconManager.compassTerritory = null;
            BeaconManager.compassLocation = null;

            return null;
        }

        if (!TerritoryData.hasValues()) {
            TerritoryData.updateTerritoryData();
        }

        List<Tuple<String, String>> upcomingAttacksSplit = new ArrayList<>();
        List<String> upcomingAttackTerritories = new ArrayList<>();

        for (String upcomingAttack : upcomingAttacks) {
            String upcomingAttackUnformatted = TextFormatting.getTextWithoutFormattingCodes(upcomingAttack);
            if (upcomingAttackUnformatted == null) return null;

            String[] words = upcomingAttackUnformatted.split(" ");
            if (words.length < 3) return null;

            String time = words[1];
            String territory = String.join(" ", Arrays.copyOfRange(words, 2, words.length));

            upcomingAttacksSplit.add(new Tuple<>(time, territory));
            upcomingAttackTerritories.add(territory);
        }

        List<String> terrsToRemove = new ArrayList<>();
        for (Map.Entry<String, Tuple<String, Long>> savedDefense : savedDefenses.entrySet()) {
            if (!upcomingAttackTerritories.contains(savedDefense.getKey())) {
                terrsToRemove.add(savedDefense.getKey());
            }
        }

        if (!sample) {
            for (String terrToRemove : terrsToRemove) {
                savedDefenses.remove(terrToRemove);
                attackCoordinates.remove(terrToRemove);

                if (terrToRemove.equals(BeaconManager.compassTerritory)) {
                    BeaconManager.compassTerritory = null;
                    BeaconManager.compassLocation = null;
                }
            }
        }

        upcomingAttacksSplit.sort((o1, o2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date d1 = sdf.parse(o1.x);
                Date d2 = sdf.parse(o2.x);
                return (int) (d1.getTime() - d2.getTime());
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });

        if (!sample) {
            if (!upcomingAttacksSplit.get(0).y.equals(BeaconManager.soonestTerritory)) {
                BeaconManager.soonestTerritory = upcomingAttacksSplit.get(0).y;
                BeaconManager.soonestTerritoryLocation = Avomod.territoryData.getMiddleOfTerritory(upcomingAttacksSplit.get(0).y);
            }
        }

        int xPos = Avomod.getMC().player.getPosition().getX();
        int zPos = Avomod.getMC().player.getPosition().getZ();
        String currentTerritory = Avomod.territoryData.coordinatesInTerritory(new Tuple<>(xPos, zPos));

        final int rectangleHeight = 12;
        final float scale = 1F;
        ArrayList<Element> elementsList = new ArrayList<>();
        float y = Utils.getStartY("attacksMenu", upcomingAttacksSplit.size(), scale);

        for (Tuple<String, String> attack : upcomingAttacksSplit) {
            Tuple<String, Long> savedDefense = savedDefenses.get(attack.y);
            int minutes = Integer.parseInt(attack.x.split(":")[0]);
            int seconds = Integer.parseInt(attack.x.split(":")[1]);
            Long warTimestamp = (minutes * 60000L + seconds * 1000L) + System.currentTimeMillis();

            if (sample) {
                savedDefense = new Tuple<>("Retrieving...", warTimestamp);
            } else if (savedDefense == null || Math.abs(savedDefense.y - warTimestamp) > 10000) {
                if (System.currentTimeMillis() - AttackedTerritoryDifficulty.currentTime < 5000 && attack.y.equals((AttackedTerritoryDifficulty.currentTerritory))) {
                    savedDefense = new Tuple<>(AttackedTerritoryDifficulty.currentDefense, (AttackedTerritoryDifficulty.currentTimer * 60000L) + System.currentTimeMillis());
                } else {
                    savedDefense = new Tuple<>("Retrieving...", warTimestamp);
                    TerritoryData.getTerritoryDefense(attack.y, warTimestamp);
                }

                savedDefenses.put(attack.y, savedDefense);
            }

            String terrDefense = savedDefense.x;
            if (terrDefense.equals("Low") || terrDefense.equals("Very Low")) {
                terrDefense = TextFormatting.GREEN + terrDefense;
            } else if (terrDefense.equals("Medium")) {
                terrDefense = TextFormatting.YELLOW + terrDefense;
            } else {
                terrDefense = TextFormatting.RED + terrDefense;
            }

            String message = TextFormatting.GOLD + attack.y + " (" + terrDefense + TextFormatting.GOLD + ") " + TextFormatting.AQUA + attack.x;
            if (attack.y.equals(currentTerritory)) {
                message = TextFormatting.LIGHT_PURPLE + "" + TextFormatting.BOLD + attack.y + TextFormatting.RESET + TextFormatting.GOLD + " (" + terrDefense + TextFormatting.GOLD + ") " + TextFormatting.AQUA + attack.x;
            }

            int rectangleWidth = Avomod.getMC().fontRenderer.getStringWidth(message) + 4;
            float x = Utils.getStartX("attacksMenu", rectangleWidth, scale);

            elementsList.add(new Rectangle(x, y, rectangleWidth, rectangleHeight, new Color(100, 100, 100, 100)));
            elementsList.add(new Text(message, x + 2, y + 2, new Color(255, 170, 0)));
            attackCoordinates.put(attack.y, new ScreenCoordinates(x, y, x + rectangleWidth, y + rectangleHeight));
            y += rectangleHeight;
        }

        return new MultipleElements("attacksMenu", 1F, elementsList);
    }

    private static List<String> getUpcomingAttacks() {
        if (Avomod.getMC().player == null || Avomod.getMC().world == null) return new ArrayList<>();

        Scoreboard scoreboard = Avomod.getMC().world.getScoreboard();
        Collection<Score> scores = scoreboard.getScores();
        Optional<Score> titleScoreOptional = scores.stream().filter(e -> e.getPlayerName().contains("Upcoming Attacks")).findFirst();

        if (titleScoreOptional.isPresent()) {
            int titleScore = titleScoreOptional.get().getScorePoints();
            List<Score> upcomingAttackScores = scores.stream().filter(e -> e.getScorePoints() < titleScore).collect(Collectors.toList());
            List<String> upcomingAttacks = upcomingAttackScores.stream().map(Score::getPlayerName).collect(Collectors.toList());
            List<String> duplicateTerritories = new ArrayList<>();

            return upcomingAttacks.stream().filter(e -> {
                if (!duplicateTerritories.contains(e)) {
                    duplicateTerritories.add(e);
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderOverlay(RenderGameOverlayEvent.Chat event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("attacksMenu")) return;

        // The Chat RenderGameOverlayEvent renders stuff normally, it disappears in f1, you can see it when your
        // inventory is open and you can make stuff transparent
        List<String> upcomingAttacks = getUpcomingAttacks();
        MultipleElements elementsToDraw = AttacksMenu.getElementsToDraw(upcomingAttacks, false);
        if (elementsToDraw != null) {
            elementsToDraw.draw();
        }
    }

    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("attacksMenu")) return;

        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenHeight = scaledResolution.getScaledHeight();
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledMouseX = Mouse.getX() / scaleFactor;
        int scaledMouseY = Mouse.getY() / scaleFactor;

        if (event.getGui() instanceof GuiChat && Mouse.getEventButtonState()) {
            for (Map.Entry<String, ScreenCoordinates> attackCoordinate : attackCoordinates.entrySet()) {
                if (attackCoordinate.getValue().mouseIn(scaledMouseX, screenHeight - scaledMouseY)) {
                    Coordinates territoryLocation = Avomod.territoryData.getMiddleOfTerritory(attackCoordinate.getKey());
                    BeaconManager.compassLocation = territoryLocation;

                    if (BeaconManager.compassLocation != null) {
                        Avomod.getMC().player.sendMessage(new TextComponentString("A blue beacon beam has been created in " + attackCoordinate.getKey() + " at (" + territoryLocation.getX() + ", " + territoryLocation.getZ() + ")"));
                        BeaconManager.compassTerritory = attackCoordinate.getKey();
                    } else {
                        Avomod.getMC().player.sendMessage(new TextComponentString("Not a correct territory name (probably too long for the scoreboard)"));
                    }
                }
            }
        }
    }
}
