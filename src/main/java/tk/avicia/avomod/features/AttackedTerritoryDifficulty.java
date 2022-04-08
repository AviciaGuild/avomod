package tk.avicia.avomod.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.webapi.ApiRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AttackedTerritoryDifficulty {
    public static long currentTime = System.currentTimeMillis();
    public static String currentTerritory = null;
    public static String currentDefense = null;
    public static int currentTimer = 0;

    public static void inMenu() {
        String territoryDefense = null;

        Container openContainer = Avomod.getMC().player.openContainer;
        InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
        ItemStack minecart = lowerInventory.getStackInSlot(13);
        List<String> territoryLore = minecart.getTooltip(Avomod.getMC().player, ITooltipFlag.TooltipFlags.ADVANCED);
        String territoryDefenseMessage = territoryLore.get(1);

        Optional<String> timerTextOptional = territoryLore.stream().filter(e ->
                Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(e)).startsWith("Time to Start")).findFirst();
        if (!timerTextOptional.isPresent()) return;

        String timerText = timerTextOptional.get();
        String[] timerSplit = timerText.split(": ");

        if (timerSplit.length < 2) return;
        String timer = TextFormatting.getTextWithoutFormattingCodes(timerSplit[1].split("m")[0]);
        if (timer == null) return;

        if (territoryDefenseMessage.contains("Territory Defences")) {
            String unformattedTerritoryDefenseMessage = TextFormatting.getTextWithoutFormattingCodes(territoryDefenseMessage);
            if (unformattedTerritoryDefenseMessage == null) return;

            territoryDefense = unformattedTerritoryDefenseMessage.split(": ")[1];
        }

        if (territoryDefense == null) return;

        currentDefense = territoryDefense;
        currentTerritory = lowerInventory.getName().split(": ")[1];
        currentTime = System.currentTimeMillis();
        currentTimer = Integer.parseInt(timer);
    }

    public static void receivedChatMessage(String message, String territory) {
        if (System.currentTimeMillis() - currentTime < 5000 && territory.equals(currentTerritory)) {
            if (Avomod.getConfigBoolean("terrDefenseInChat")) {
                Avomod.getMC().player.sendChatMessage("/g " + currentTerritory + " defense is " + currentDefense);
            }

            try {
                recordDefense(message, territory, currentDefense);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            recordDefense(message, territory, "Unknown");
        }
    }

    public static void recordDefense(String message, String territory, String defense) {
        String username = Minecraft.getMinecraft().player.getName();
        String uuid = Minecraft.getMinecraft().player.getUniqueID().toString();
        long timestamp = System.currentTimeMillis();
        String timer = message.split("will start in ")[1].split(" minutes.")[0];

        ApiRequest.post("https://script.google.com/macros/s/AKfycbw7lRN6tojW1RjsPeC7bhVNsGETBl_LZEc6bZKXAHG95HB_UC4NKQMm9LGmuvT8KU-R-A/exec",
                String.format("{`username`:`%s`,`territory`:`%s`,`timestamp`:%s,`playerUuid`:`%s`,`timer`:`%s`,`defense`:`%s`}",
                        username, territory, timestamp, uuid, timer, defense).replace('`', '"'));
    }

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null) return;

        if (message.contains("The war for") && message.endsWith("minutes.")) {
            String territory = message.split("for ")[1].split(" will")[0];
            receivedChatMessage(message, territory);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null || event.getGui() == null) return;

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();

            if (containerName.contains("Attacking: ")) {
                try {
                    inMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
