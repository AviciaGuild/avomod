package tk.avicia.avomod.features;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;

public class DisableMovingArmor {


    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null) return;

        Container openContainer = Avomod.getMC().player.openContainer;
        ScaledResolution scaledResolution = new ScaledResolution(Avomod.getMC());
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();
        int scaleFactor = scaledResolution.getScaleFactor();
        int scaledMouseX = Mouse.getX() / scaleFactor;
        int scaledMouseY = Mouse.getY() / scaleFactor;
        int slotDimensions = 18;

        if (!(openContainer instanceof ContainerPlayer) || !(event.getGui() instanceof GuiInventory) || !(Avomod.getConfigBoolean("disableMovingArmor")))
            return;

        if (Mouse.getEventButtonState() && scaledMouseY > (screenHeight / 2) - slotDimensions && scaledMouseX < (screenWidth / 2.0) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (Avomod.getConfigBoolean("disableAll") || Avomod.getMC().player == null || !Avomod.getConfigBoolean("disableMovingArmor"))
            return;

        Container openContainer = Avomod.getMC().player.openContainer;
        if (!(openContainer instanceof ContainerPlayer)) return;

        int screenWidth = Avomod.getMC().displayWidth;
        int screenHeight = Avomod.getMC().displayHeight;
        int slotDimensions = 36;

        if (Keyboard.getEventKey() == 16 && Mouse.getY() > (screenHeight / 2) - slotDimensions && Mouse.getX() < (screenWidth / 2.0) - (4.5 * slotDimensions) + (4 * slotDimensions)) {
            event.setCanceled(true);
        }
    }
}
