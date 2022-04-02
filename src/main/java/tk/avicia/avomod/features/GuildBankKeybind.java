package tk.avicia.avomod.features;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

public class GuildBankKeybind {
    private static boolean openingBank;
    private static KeyBinding keyBinding;

    public static void init() {
        keyBinding = new KeyBinding("Keybind to open guild bank", Keyboard.getKeyIndex("Y"), "Avomod");
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (Avomod.getMC().player == null) return;

        if (keyBinding.isPressed()) {
            Avomod.getMC().player.sendChatMessage("/gu manage");
            openingBank = true;
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (Avomod.getMC().player == null || !(event.getGui() instanceof GuiChest) || !openingBank) return;

        GuiChest manageMenu = (GuiChest) event.getGui();
        Container container = manageMenu.inventorySlots;
        if (!(container instanceof ContainerChest)) return;

        InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) container).getLowerChestInventory();
        if (!lowerInventory.getName().contains(": Manage")) return;

        ItemStack guildBankItem = lowerInventory.getStackInSlot(15);
        if (!guildBankItem.getDisplayName().contains("Bank")) return;

        Utils.sendClickPacket(container, 15, ClickType.PICKUP, 0, guildBankItem);
        openingBank = false;
    }
}
