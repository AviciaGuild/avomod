package tk.avicia.avomod.utils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class Keybind extends KeyBinding {
    private String commandToRun;

    public Keybind(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
        super(description, keyConflictContext, keyCode, category);
    }

    public Keybind(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
        super(description, keyConflictContext, keyModifier, keyCode, category);
    }

    public Keybind(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }

    public Keybind(String description, int keyCode, String category, String commandToRun) {
        super(description, keyCode, category);

        this.commandToRun = commandToRun;
    }

    public String getCommandToRun() {
        return this.commandToRun;
    }
}
