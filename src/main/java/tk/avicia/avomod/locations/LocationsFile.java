package tk.avicia.avomod.locations;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.RectangleText;
import tk.avicia.avomod.utils.CustomFile;

public class LocationsFile {
    public static void save(RectangleText rectangleText) {
        CustomFile locationsFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/locations.json");
        JsonObject locations = locationsFile.readJson();
        locations.addProperty(rectangleText.getKey(), rectangleText.toString());

        Avomod.locations = locations;
        locationsFile.writeJson(locations);
    }

    public static RectangleText fetch(String key) {
        CustomFile locationsFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/locations.json");
        JsonObject locations = locationsFile.readJson();

        return new RectangleText(key, locations.get(key).getAsString());
    }
}
