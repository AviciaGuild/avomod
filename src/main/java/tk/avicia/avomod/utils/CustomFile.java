package tk.avicia.avomod.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CustomFile extends File {
    public CustomFile(File parent, String child) {
        super(parent, child);

        if (!super.exists()) {
            try {
                super.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JsonObject readJson() {
        String output = "";
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(this), StandardCharsets.UTF_8);

            int currentChar;
            while ((currentChar = reader.read()) != -1) {
                output += (char) currentChar;
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new JsonParser().parse(output).getAsJsonObject();
    }

    public void writeJson(JsonObject jsonObject) {
        try {
            OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(this), StandardCharsets.UTF_8);
            String text = jsonObject.toString();
            fileWriter.write(text, 0, text.length());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
