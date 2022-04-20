package tk.avicia.avomod.features;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.enums.BombType;
import tk.avicia.avomod.core.structures.BombData;
import tk.avicia.avomod.core.structures.render.Element;
import tk.avicia.avomod.core.structures.render.MultipleElements;
import tk.avicia.avomod.core.structures.render.Rectangle;
import tk.avicia.avomod.core.structures.render.Text;
import tk.avicia.avomod.utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BombBellTracker {
    private final List<BombData> storedBombs = new ArrayList<>();

    public static MultipleElements getElementsToDraw(List<BombData> storedBombs) {
        storedBombs = storedBombs.stream().filter(storedBomb -> storedBomb.getTimeLeft() > 0).collect(Collectors.toList());
        storedBombs.sort(Comparator.comparing(BombData::getTimeLeft));
//        System.out.println(storedBombs.stream().map(e -> e.getBombType().toString() + " " + e.getTimeLeft()).collect(Collectors.toList()));

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

//        Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Avomod: " + bombType + " detected on WC" + world));
        storedBombs.add(new BombData(world, bombType));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderOverlay(RenderGameOverlayEvent.Chat event) {
        if (Avomod.getConfigBoolean("disableAll") || !Avomod.getConfigBoolean("bombBellTracker")) return;

        // The Chat RenderGameOverlayEvent renders stuff normally, it disappears in f1, you can see it when your
        // inventory is open and you can make stuff transparent
        getElementsToDraw(storedBombs).draw();
//        getElementsToDraw(storedBombs);
    }
}
