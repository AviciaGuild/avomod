package tk.avicia.avomod.events;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Utils;

public class Autojoin {
    public static void execute() {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                if (Avomod.getMC().player != null) {
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ItemStack compass = null;
            for (int i = 0; i < 10; i++) {
                InventoryPlayer inventory = Avomod.getMC().player.inventory;
                compass = inventory.getStackInSlot(0);

                if (compass.getDisplayName().contains("Quick Connect")) {
                    break;
                }

                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (compass != null) {
                CPacketPlayerTryUseItem compassPacket = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
                Avomod.getMC().getConnection().sendPacket(compassPacket);

                for (int i = 0; i < 10; i++) {
                    Container openContainer = Avomod.getMC().player.openContainer;

                    if (openContainer instanceof ContainerChest) {
                        InventoryBasic chestContainer = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
                        ItemStack recommendedWorld = chestContainer.getStackInSlot(13);

                        if (recommendedWorld.getDisplayName().contains("Recommended")) {
                            Utils.sendClickPacket(openContainer, 13, ClickType.PICKUP, 0, recommendedWorld);
                            break;
                        }
                    }

                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
