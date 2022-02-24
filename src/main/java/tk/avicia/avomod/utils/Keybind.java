package tk.avicia.avomod.utils;

import net.minecraft.client.settings.KeyBinding;

public class Keybind extends KeyBinding {
    private final String commandToRun;

    public Keybind(String description, int keyCode, String category, String commandToRun) {
        super(description, keyCode, category);

        this.commandToRun = commandToRun;
    }

    public String getCommandToRun() {
        return this.commandToRun;
    }
}
