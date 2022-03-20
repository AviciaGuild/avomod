package tk.avicia.avomod.locations;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.renderer.MultipleElements;
import tk.avicia.avomod.utils.CustomFile;

public class LocationsFile {
    public static void save(MultipleElements multipleElements) {
        CustomFile locationsFile = new CustomFile(Avomod.getMC().mcDataDir, "avomod/configs/locations.json");
        JsonObject locations = locationsFile.readJson();
        locations.addProperty(multipleElements.getKey(), multipleElements.toString());

        Avomod.locations = locations;
        locationsFile.writeJson(locations);
    }
}
