package tk.avicia.avomod.events;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class EventHandlerClass {
    private int tick = 0;
    private boolean guiOpen = false;

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("revealNicks")) {
            ChatUtils.execute(event);
        }

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        boolean bankMessage = message.startsWith("[INFO]") && message.contains("Guild Bank");
        if (bankMessage && Avomod.getConfigBoolean("filterBankMessages")) {
            event.setCanceled(true);
        }

        KeyBinding sneakKeyBind = Avomod.getMC().gameSettings.keyBindSneak;
        if (message.contains("Press SHIFT to continue") && Avomod.getConfigBoolean("skipDialogue")) {
            Thread thread = new Thread(() -> {
                try {
                    KeyBinding.setKeyBindState(sneakKeyBind.getKeyCode(), true);
                    Thread.sleep(100);
                    KeyBinding.setKeyBindState(sneakKeyBind.getKeyCode(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        if (message.startsWith("[INFO]") && message.contains("resources") && Avomod.getConfigBoolean("filterResourceMessages")) {
//            String territory = message.split("Territory ")[1].split(" is")[0];
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Avomod.getMC().player == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;
        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledMouseX = Mouse.getX() / scaleFactor;
        int scaledMouseY = Mouse.getY() / scaleFactor;
        int slotDimensions = 18;

        if (openContainer instanceof ContainerPlayer && event.getGui() instanceof GuiInventory && Avomod.getConfigBoolean("disableMovingArmor")) {
            if (Mouse.getEventButtonState() && scaledMouseY > (screenHeight / 2) - slotDimensions && scaledMouseX < (screenWidth / 2) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
                event.setCanceled(true);
            }
        } else if (openContainer instanceof ContainerChest) {
            String containerName = ((ContainerChest) openContainer).getLowerChestInventory().getName();
            if (containerName.equals("Trade Market")) {
                TradeMarketAutoSearch.execute(event, openContainer, screenWidth, screenHeight, slotDimensions, scaleFactor);
            } else if (containerName.contains("Loot Chest")) {
                PowderStackingFix.execute(event, openContainer);
            }
        }

        if (event.getGui() instanceof GuiChat && Mouse.getEventButtonState()) {
            for (Map.Entry<String, ScreenCoordinates> attackCoordinates : AttacksMenu.attackCoordinates.entrySet()) {
                if (attackCoordinates.getValue().mouseIn(scaledMouseX, screenHeight - scaledMouseY)) {
                    Coordinates territoryLocation = Avomod.territoryData.getMiddleOfTerritory(attackCoordinates.getKey());
                    Avomod.compassLocation = territoryLocation;

                    if (Avomod.compassLocation != null) {
                        Avomod.getMC().player.sendMessage(new TextComponentString("A blue beacon beam has been created in " + attackCoordinates.getKey() + " at (" + (int) territoryLocation.getX() + ", " + (int) territoryLocation.getZ() + ")"));
                        Avomod.compassTerritory = attackCoordinates.getKey();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (Avomod.getMC().player == null) {
            return;
        }
        if (Avomod.getConfigBoolean("disableMovingArmor")) {
            Container openContainer = Avomod.getMC().player.openContainer;

            if (openContainer instanceof ContainerPlayer) {
                int screenWidth = Avomod.getMC().displayWidth;
                int screenHeight = Avomod.getMC().displayHeight;
                int slotDimensions = 36;

                if (Keyboard.getEventKey() == 16 && Mouse.getY() > (screenHeight / 2) - slotDimensions && Mouse.getX() < (screenWidth / 2) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (Avomod.getMC().player == null || event.getGui() == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (containerName.contains("Loot Chest")) {
                AverageLevel.execute(event, lowerInventory);
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (Avomod.getMC().player == null) {
            return;
        }
        Container openContainer = Avomod.getMC().player.openContainer;

        if (!(openContainer instanceof ContainerChest)) {
            AverageLevel.isChestNew = true;
        }
        ToolTipState.isTooltipRendering = false;

        tick++;
        if (tick % 60000 == 0) {
            TerritoryData.updateTerritoryData();
        }

        guiOpen = false;
    }

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (Avomod.getConfigBoolean("autojoinWorld")) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    ServerData serverData = Avomod.getMC().getCurrentServerData();

                    if (serverData.serverIP.contains("wynncraft.com")) {
                        Autojoin.execute();
                        break;
                    }

                    try {
                        Thread.sleep(400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        for (Keybind keybind : Avomod.keybinds.values()) {
            if (keybind.isPressed()) {
                Avomod.getMC().player.sendChatMessage("/" + keybind.getCommandToRun());
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (Avomod.getMC().gameSettings.keyBindPlayerList.isKeyDown()) {
            WorldInfo.draw();
        }

        if (Avomod.guiToDraw != null) {
            Avomod.getMC().displayGuiScreen(Avomod.guiToDraw);
            Avomod.guiToDraw = null;
        }

        List<String> upcomingAttacks = Utils.getUpcomingAttacks();
        if (upcomingAttacks.size() != 0 && Avomod.getConfigBoolean("attacksMenu") && !guiOpen) {
            try {
                AttacksMenu.draw(upcomingAttacks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e) {
        if (Avomod.compassLocation == null) return;

        Renderer.drawBeam(Avomod.compassLocation, new Color(0, 50, 150, 255), e.getPartialTicks());
    }

    @SubscribeEvent
    public void onScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (Avomod.getMC().player == null || event.getGui() == null) {
            return;
        }

        // Stuff you draw here appears above the chest slots and behind the items z-levels, so you can use this for
        // highlights
        if (!(event.getGui() instanceof GuiChat)) {
            guiOpen = true;
        }
    }

    @SubscribeEvent
    public void onTooltipRender(RenderTooltipEvent.PostBackground event) {
        ToolTipState.isTooltipRendering = true;
        ToolTipState.toolTipX = event.getX();
        ToolTipState.toolTipY = event.getY();
        ToolTipState.toolTipWidth = event.getWidth();
        ToolTipState.toolTipHeight = event.getHeight();
    }

    @SubscribeEvent
    public void bossInfo(RenderGameOverlayEvent.BossInfo event) {
//        String bossbarName = TextFormatting.getTextWithoutFormattingCodes(event.getBossInfo().getName().getUnformattedText());
//
//        if (bossbarName.contains("Tower")) {
//            String[] bossbarWords = bossbarName.split(" ");
//
//            String health = bossbarWords[bossbarWords.length - 6];
//            String defense = bossbarWords[bossbarWords.length - 5];
//            String damage = bossbarWords[bossbarWords.length - 2];
//            String attacks = bossbarWords[bossbarWords.length - 1];
//
////            System.out.println("Health: " + health + ", Defense: " + defense + ", Damage: " + damage + ", Attacks: " + attacks);
//        }
    }
}