package tk.avicia.avomod.features;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.enums.BombType;
import tk.avicia.avomod.core.structures.BombData;
import tk.avicia.avomod.core.structures.ScreenCoordinates;
import tk.avicia.avomod.core.structures.render.Element;
import tk.avicia.avomod.core.structures.render.MultipleElements;
import tk.avicia.avomod.core.structures.render.Rectangle;
import tk.avicia.avomod.core.structures.render.Text;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BombBellTracker {
    private static final HashMap<String, ScreenCoordinates> bombBellCoordinates = new HashMap<>();
    private final List<BombData> storedBombs = new ArrayList<>();

    public static MultipleElements getElementsToDraw(List<BombData> storedBombs) {
        List<BombData> bombsToRemove = storedBombs.stream().filter(storedBomb -> storedBomb.getTimeLeft() <= 0).collect(Collectors.toList());
        storedBombs.removeAll(bombsToRemove);
        storedBombs.sort(Comparator.comparing(BombData::getTimeLeft));

        final int rectangleHeight = 12;
        final float scale = 1F;
        ArrayList<Element> elementsList = new ArrayList<>();
        AtomicReference<Float> y = new AtomicReference<>(Utils.getStartY("bombBellTracker", storedBombs.size(), scale));

        storedBombs.forEach(storedBomb -> {
            double timeLeft = storedBomb.getTimeLeft();
            int minutesLeft = (int) timeLeft / 60;
            int secondsLeft = (int) timeLeft % 60;
            String message = String.format("%s Bomb on WC%s - %02dm %02ds", storedBomb.getBombType().getBombName(), storedBomb.getWorld(), minutesLeft, secondsLeft);

            int rectangleWidth = Avomod.getMC().fontRenderer.getStringWidth(message) + 4;
            float x = Utils.getStartX("bombBellTracker", rectangleWidth, scale);

            elementsList.add(new Rectangle(x, y.get(), rectangleWidth, rectangleHeight, new Color(100, 100, 100, 100)));
            elementsList.add(new Text(message, x + 2, y.get() + 2, new Color(255, 251, 0)));
            bombBellCoordinates.put(storedBomb.getWorld(), new ScreenCoordinates(x, y.get(), x + rectangleWidth, y.get() + rectangleHeight));
            y.updateAndGet(v -> v + rectangleHeight);
        });

        return new MultipleElements("bombBellTracker", scale, elementsList);
    }

    public static List<BombData> getSampleData() {
        return Arrays.asList(
                new BombData("12", BombType.COMBAT_XP),
                new BombData("38", BombType.LOOT)
        );
    }

    private boolean isDuplicateBomb(BombData bomb) {
        return storedBombs.stream().anyMatch(bombData -> bombData.getWorld().equals(bomb.getWorld()) &&
                bombData.getBombType().equals(bomb.getBombType()));
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("bombBellTracker")) return;

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null || !message.startsWith("[Bomb Bell]")) return;

        ArrayList<String> matches = Utils.getMatches(message, "(?<= thrown a )[a-zA-Z ]+(?= Bomb on)|(?<= on WC)\\d+");
        if (matches.size() != 2) return;

        String bombName = matches.get(0);
        String world = matches.get(1);
        BombType bombType = BombType.getBombType(bombName);
        BombData bombData = new BombData(world, bombType);

        if (!isDuplicateBomb(bombData)) {
            storedBombs.add(bombData);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderOverlay(RenderGameOverlayEvent.Chat event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("bombBellTracker")) return;

        getElementsToDraw(storedBombs).draw();
    }

    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("bombBellTracker")) return;

        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenHeight = scaledResolution.getScaledHeight();
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledMouseX = Mouse.getX() / scaleFactor;
        int scaledMouseY = Mouse.getY() / scaleFactor;

        if (event.getGui() instanceof GuiChat && Mouse.getEventButtonState()) {
            for (Map.Entry<String, ScreenCoordinates> bombBellCoordinate : bombBellCoordinates.entrySet()) {
                if (bombBellCoordinate.getValue().mouseIn(scaledMouseX, screenHeight - scaledMouseY)) {
                    Avomod.getMC().player.sendChatMessage("/switch " + bombBellCoordinate.getKey());
                }
            }
        }
    }
}
