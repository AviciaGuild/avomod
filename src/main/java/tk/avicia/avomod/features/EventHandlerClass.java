package tk.avicia.avomod.features;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.MultipleElements;
import tk.avicia.avomod.utils.BeaconManager;
import tk.avicia.avomod.utils.Keybind;
import tk.avicia.avomod.utils.TerritoryData;
import tk.avicia.avomod.webapi.UpdateChecker;

import java.awt.*;
import java.util.Calendar;
import java.util.regex.Pattern;

public class EventHandlerClass {
    private int tick = 0;
    private boolean guiJustOpened = false;

    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getConfigBoolean("revealNicks")) {
            ChatUtils.execute(event);
        }

        String message = TextFormatting.getTextWithoutFormattingCodes(event.getMessage().getUnformattedText());
        if (message == null) return;

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
            event.setCanceled(true);
        }

        if (message.contains("The war for") && message.endsWith("minutes.")) {
            String territory = message.split("for ")[1].split(" will")[0];
            AttackedTerritoryDifficulty.receivedChatMessage(message, territory);
        }

        if (message.trim().startsWith("Loading Resource Pack...")) {
            new Thread(() -> {
                UpdateChecker.checkStableUpdate();

                if (Avomod.getConfigBoolean("betaNotification")) {
                    UpdateChecker.checkBetaUpdate();
                }
            }).start();
        }

//        if (Avomod.getConfigBoolean("autogg") && message.startsWith("[!] Congratulations")) {
//            String[] firstSplit = message.split(" for")[0].split("to ");
//            if (firstSplit.length <= 1) return;
//
//            String username = firstSplit[1];
//            new Thread(() -> {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Avomod.getMC().player.sendChatMessage(String.format("/msg %s gg", username));
//            }).start();
//        }

        int month = Calendar.getInstance().get(Calendar.MONTH);
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (month == 3 && dayOfMonth == 1 && ChatUtils.checkIfGuildChat(event.getMessage())) {
            Pattern pattern = Pattern.compile("(i'm )|(im )|(i am )", Pattern.CASE_INSENSITIVE);
            String[] textToSend = pattern.split(message);

            if (textToSend.length > 1) {
                Avomod.getMC().player.sendChatMessage(String.format("/g Hi %s", textToSend[1].replaceAll("[^a-zA-Z0-9,:_ .!\\-&()\"'?]", "")));
                Avomod.getMC().player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "April fools! This avomod feature will automatically turn off on April 2nd"));
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getMC().player == null || event.getGui() == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (containerName.contains("Loot Chest")) {
                AverageLevel.execute(event, lowerInventory);
            } else if (containerName.contains("Attacking: ")) {
                try {
                    AttackedTerritoryDifficulty.inMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (containerName.equals("Trade Overview")) {
                TradeMarketOverviewHelper.execute(openContainer);
            }
        }

        guiJustOpened = false;
    }

    @SubscribeEvent
    public void guiInitialize(GuiScreenEvent.InitGuiEvent.Post event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getMC().player == null || event.getGui() == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;
        if (openContainer instanceof ContainerChest) {
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (containerName.contains("Loot Chest")) {
                ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
                int screenHeight = scaledResolution.getScaledHeight();
                int scaleFactor = scaledResolution.getScaleFactor();
                int scaledMouseX = Mouse.getX() / scaleFactor;
                int scaledMouseY = Mouse.getY() / scaleFactor;

                if (guiJustOpened && scaledMouseY != Math.ceil(screenHeight / 2.0)) {
                    Mouse.setCursorPosition(scaledMouseX * scaleFactor, ((int) Math.ceil(screenHeight / 2.0)) * scaleFactor);
                }
            }
        }
    }

    @SubscribeEvent
    public void guiJustOpened(GuiOpenEvent event) {
        guiJustOpened = true;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getMC().player == null) {
            return;
        }

        Container openContainer = Avomod.getMC().player.openContainer;

        if (!(openContainer instanceof ContainerChest)) {
            AverageLevel.isChestNew = true;
        }
//        ToolTipState.isTooltipRendering = false;

        tick++;
        if (tick % 60000 == 0) {
            TerritoryData.updateTerritoryData();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        for (Keybind keybind : Avomod.keybinds.values()) {
            if (keybind.isPressed()) {
                Avomod.getMC().player.sendChatMessage("/" + keybind.getCommandToRun());
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (Avomod.guiToDraw != null) {
            Avomod.getMC().displayGuiScreen(Avomod.guiToDraw);
            Avomod.guiToDraw = null;
        }

        if (Avomod.getConfigBoolean("disableAll")) return;

        if (Avomod.getMC().gameSettings.keyBindPlayerList.isKeyDown()) {
            MultipleElements elements = WorldInfo.getElementsToDraw();
            if (elements != null) {
                elements.draw();
            }
        }

    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e) {
        if (Avomod.getConfigBoolean("disableAll")) return;

        if (BeaconManager.compassLocation != null) {
            BeaconManager.drawBeam(BeaconManager.compassLocation, new Color(0, 50, 150, 255), e.getPartialTicks(), BeaconManager.compassTerritory);
        }

        if (BeaconManager.soonestTerritoryLocation != null && Avomod.getConfigBoolean("greenBeacon")) {
            BeaconManager.drawBeam(BeaconManager.soonestTerritoryLocation, new Color(50, 150, 0, 255), e.getPartialTicks(), BeaconManager.soonestTerritory);
        }

    }

//    @SubscribeEvent
//    public void onTooltipRender(RenderTooltipEvent.PostBackground event) {
//        if (Avomod.getConfigBoolean("disableAll")) return;

//        ToolTipState.isTooltipRendering = true;
//        ToolTipState.toolTipX = event.getX();
//        ToolTipState.toolTipY = event.getY();
//        ToolTipState.toolTipWidth = event.getWidth();
//        ToolTipState.toolTipHeight = event.getHeight();
//    }

    @SubscribeEvent
    public void entityRender(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (!Avomod.getConfigBoolean("hideEntitiesInWar") || (System.currentTimeMillis() - WarDPS.lastTimeInWar) > 2000)
            return;

        EntityLivingBase entity = event.getEntity();
        if (entity.getTeam() == null) {
            event.setCanceled(true);
        }
    }
}