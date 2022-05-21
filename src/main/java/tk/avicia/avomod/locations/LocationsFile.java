package tk.avicia.avomod.locations;

import com.google.gson.JsonObject;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.core.structures.CustomFile;
import tk.avicia.avomod.core.structures.render.MultipleElements;

public class LocationsFile {
    public static void save(MultipleElements multipleElements) {
        CustomFile locationsFile = new CustomFile("avomod/configs/locations.json");
        JsonObject locations = locationsFile.readJson();
        locations.addProperty(multipleElements.getKey(), multipleElements.toString());

        Avomod.locations = locations;
        locationsFile.writeJson(locations);
    }
}
