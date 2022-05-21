package tk.avicia.avomod.core.structures;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import tk.avicia.avomod.Avomod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CustomFile extends File {
    public CustomFile(String child) {
        this(Avomod.getMC().mcDataDir, child);
    }

    public CustomFile(File parent, String child) {
        super(parent, child);

        if (!super.exists()) {
            try {
                String[] split = child.split("/");

                if (split.length > 1) {
                    String[] directorySplit = Arrays.copyOfRange(split, 0, split.length - 1);
                    if (directorySplit.length > 1) {
                        String directory = String.join("/", directorySplit);

                        File file = new File(directory);
                        boolean success = file.mkdirs();
                        if (!success) {
                            System.out.println("Didn't create file");
                        }
                    }
                }
                boolean success = super.createNewFile();
                if (!success) {
                    System.out.println("Didn't create file");
                }
                this.writeJson("{}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JsonObject readJson() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(this), StandardCharsets.UTF_8); JsonReader jsonReader = new JsonReader(reader)) {
            jsonReader.setLenient(true);
            return new JsonParser().parse(jsonReader).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            this.writeJson("{}");
            return new JsonParser().parse("{}").getAsJsonObject();
        }
    }

    public void writeJson(JsonObject jsonObject) {
        this.writeJson(jsonObject.toString());
    }

    public void writeJson(String text) {
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(this), StandardCharsets.UTF_8)) {
            fileWriter.write(text, 0, text.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
