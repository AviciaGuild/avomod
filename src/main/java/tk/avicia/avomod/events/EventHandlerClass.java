package tk.avicia.avomod.events;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Keybind;

public class EventHandlerClass {
    @SubscribeEvent
    public void onChatEvent(ClientChatReceivedEvent event) {
        if (Avomod.revealNicks) {
            ChatUtils.execute(event);
        }

        String message = event.getMessage().getFormattedText();
        boolean bankMessage = message.startsWith("[INFO]") && message.contains("Guild Bank");
        if (bankMessage && Avomod.isBankFiltered()) {
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

        if (openContainer instanceof ContainerPlayer && event.getGui() instanceof GuiInventory && Avomod.isMovingArmorOrAccessoriesDisabled()) {
            if (Mouse.getEventButtonState() && scaledMouseY > (screenHeight / 2) - slotDimensions && scaledMouseX < (screenWidth / 2) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
                event.setCanceled(true);
            }
        } else if (openContainer instanceof ContainerChest) {
            String containerName = ((ContainerChest) openContainer).getLowerChestInventory().getName();
            if (containerName.equals("Trade Market")) {
                TradeMarketAutoSearch.execute(event, openContainer, screenWidth, screenHeight, slotDimensions, scaleFactor);
            }
        }
    }

    @SubscribeEvent
    public void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (Avomod.getMC().player == null) {
            return;
        }
        if (Avomod.isMovingArmorOrAccessoriesDisabled()) {
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
    }

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                ServerData serverData = Avomod.getMC().getCurrentServerData();

                if (serverData.serverName.equals("Wynncraft")) {
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
    }

    @SubscribeEvent
    public void onScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (Avomod.getMC().player == null || event.getGui() == null) {
            return;
        }

        // Stuff you draw here appears above the chest slots and behind the items z-levels, so you can use this for
        // highlights

    }

    @SubscribeEvent
    public void onTooltipRender(RenderTooltipEvent.PostBackground event) {
        ToolTipState.isTooltipRendering = true;
        ToolTipState.toolTipX = event.getX();
        ToolTipState.toolTipY = event.getY();
        ToolTipState.toolTipWidth = event.getWidth();
        ToolTipState.toolTipHeight = event.getHeight();
    }
}
