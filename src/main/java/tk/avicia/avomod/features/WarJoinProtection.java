package tk.avicia.avomod.features;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.Tuple;
import tk.avicia.avomod.utils.Utils;

import java.util.ArrayList;

public class WarJoinProtection {
    private static long lastAction = System.currentTimeMillis();

    private static boolean isAfk() {
        return System.currentTimeMillis() - lastAction > 10000;
    }

    @SubscribeEvent
    public void onMouseEvent(InputEvent.MouseInputEvent event) {
        lastAction = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onKeyboardEvent(InputEvent.KeyInputEvent event) {
        lastAction = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null) return;

        ArrayList<String> matches = Utils.getMatches(message, "(?<=^\\[WAR\\] The war for )([a-zA-Z ]*)(?= will start in \\d{1,2} seconds.)");
        if (matches.size() != 1) return;

        int xPos = Avomod.getMC().player.getPosition().getX();
        int zPos = Avomod.getMC().player.getPosition().getZ();
        String currentTerritory = Avomod.territoryData.coordinatesInTerritory(new Tuple<>(xPos, zPos));
        if (currentTerritory == null) return;

        int combatLevel = Avomod.getMC().player.experienceLevel;
        if (isAfk() && currentTerritory.equals(matches.get(0)) && Avomod.getConfigBoolean("afkWarProtection")) {
            Avomod.getMC().player.sendChatMessage("/class");
            Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Avomod: You've been /classed because you were afk right before " +
                    "a war was about to start."));
        } else if (combatLevel > 0 && combatLevel < 100) {
            Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Avomod: You're in a territory about to join a war, and your combat level " +
                    "is lower than 100. Are you sure you want to join this war? Please /class or leave the territory if you don't want to join this war."));
        }
    }
}
