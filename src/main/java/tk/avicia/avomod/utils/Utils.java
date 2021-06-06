package tk.avicia.avomod.utils;

import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import tk.avicia.avomod.Avomod;

public class Utils {
    public static String firstLetterCapital(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getReadableTime(int minutes) {
        return (int) (Math.floor(minutes / 60.0)) + " h " + minutes % 60 + " m";
    }

    public static void sendClickPacket(Container container, int slotId, ClickType clickType, int clickButton, ItemStack itemStack) {
        CPacketClickWindow compassPacket = new CPacketClickWindow(container.windowId, slotId, clickButton,
                clickType, itemStack, container.getNextTransactionID(Avomod.getMC().player.inventory));
        Avomod.getMC().getConnection().sendPacket(compassPacket);
    }
}
