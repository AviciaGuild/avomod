package tk.avicia.avomod.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.webapi.PlayerData;
import tk.avicia.avomod.webapi.WorldUpTime;

import java.util.List;
import java.util.Map;

public class RandomWorldSwitchKeybind {
    private static KeyBinding keyBinding;
    private static long lastPress = 0;
    private static long pressBeforeLastPress = 0;

    public static void init() {
        keyBinding = new KeyBinding("Switch to random world", Keyboard.getKeyIndex("J"), "Avomod");
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) throws NoSuchFieldException {
        if (Avomod.getMC().player == null) return;

        if (keyBinding.isPressed()) {
            if (lastPress > System.currentTimeMillis() - 3000 & pressBeforeLastPress > System.currentTimeMillis() - 3000) {
                PlayerData playerData = new PlayerData(Minecraft.getMinecraft().getSession().getUsername());
                String playerWorld = playerData.getWorld();
                WorldUpTime worldUpTime = new WorldUpTime();

                List<String> worlds = worldUpTime.getWorldUpTimeData().stream()
                        .filter(world -> !world.getKey().equals(playerWorld))
                        .filter(world -> world.getValue().getAsJsonObject().get("age").getAsInt() < 360)
                        .filter(world -> world.getValue().getAsJsonObject().get("players").getAsInt() < 35)
                        .map(Map.Entry::getKey)
                        .collect(java.util.stream.Collectors.toList());
                String randomWorld = worlds.get((int) (Math.random() * worlds.size()));
                Avomod.getMC().player.sendChatMessage("/switch " + randomWorld);
            } else {
                pressBeforeLastPress = lastPress;
                lastPress = System.currentTimeMillis();
            }
        }
    }
}
