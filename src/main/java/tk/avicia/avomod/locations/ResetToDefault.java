package tk.avicia.avomod.locations;

import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.CustomFile;

import java.util.Map;

public class ResetToDefault extends GuiButton {
    GuiScreen gui;

    public ResetToDefault(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, GuiScreen gui) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.gui = gui;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        CustomFile locationsFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/locations.json");
        JsonObject locationsJson = locationsFile.readJson();

        for (Map.Entry<String, String> locationData : Avomod.defaultLocations.entrySet()) {
            locationsJson.addProperty(locationData.getKey(), locationData.getValue());
        }

        locationsFile.writeJson(locationsJson);
        Avomod.locations = locationsJson;
        gui.initGui();
    }
}
