package tk.avicia.avomod.configs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import tk.avicia.avomod.Avomod;
import tk.avicia.avomod.utils.Renderer;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigsGui extends GuiScreen {
    private final int settingLineHeight = 27;
    private final int startingHeight = 65;
    private final int settingHeight = 23;
    public ArrayList<ConfigsCategory> categories = new ArrayList<>();
    // buttonList exists too, doesn't need to be created
    ArrayList<ConfigsTextField> textFieldsList = new ArrayList<>();
    String selectedCategory;
    Map<String, ArrayList<ConfigsSection>> totalSections = new HashMap<>();
    int scrollSections; // the index of the first section to be displayed

    public ConfigsGui() {
        super();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Makes blur
        this.drawWorldBackground(0);
        // Draws a shadowed string with a dark color, to make it easier to read depending on the background
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        this.drawCenteredString(this.fontRenderer, "Avomod Configs", this.width / 4 + 1, 14, 0x444444);
        this.drawCenteredString(this.fontRenderer, "Avomod Configs", this.width / 4, 13, 0x1B33CF);
        GlStateManager.popMatrix();
        Renderer.drawVerticalLine(this.width / 16 + 110, startingHeight - 10, this.height - 10, new Color(255, 255, 255));

        ArrayList<ConfigsSection> sectionsToShow = new ArrayList<>();
        sectionsToShow.addAll(totalSections.get(selectedCategory).subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), totalSections.get(selectedCategory).size())));
        for (ConfigsSection configsSection : sectionsToShow) {
            int y = sectionsToShow.indexOf(configsSection);

            int color = 0xFFFFFF;

            // Draws the actual string
            this.drawString(this.fontRenderer, configsSection.title, this.width / 16 + 121, (y * settingLineHeight) + (y * settingHeight) + 6 + startingHeight, color);
            if (totalSections.get(selectedCategory).indexOf(configsSection) != totalSections.get(selectedCategory).size() - 1)
                Renderer.drawHorizontalLine(this.width / 16 + 118, this.width - (this.width / 16) - 21, (y * settingLineHeight) + ((y + 1) * settingHeight) + 23 + startingHeight, new Color(255, 255, 255));
        }

        // Draw all text field inputs
        for (ConfigsTextField textField : this.textFieldsList) {
            textField.drawTextBox();
        }

        try {
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // sets the selected category
    public void setCategory(String title) {
        scrollSections = 0;
        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();
        buttonList.addAll(categories);

        selectedCategory = title;
        for (ConfigsCategory category : categories) {
            category.enabled = category.title.equals(title);
        }

        ArrayList<ConfigsSection> sectionList = new ArrayList<>(totalSections.get(title).subList(0, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), totalSections.get(selectedCategory).size())));
        sectionList.forEach((ConfigsSection configsSection) -> {
            int configPlacement = sectionList.indexOf(configsSection);
            if (configsSection.button != null) {
                configsSection.button.y = configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1));
                this.buttonList.add(configsSection.button);
            }

            if (configsSection.textField != null) {
                configsSection.textField.y = configPlacement * settingLineHeight + startingHeight + 2 + (settingHeight * (configPlacement + 1));
                this.textFieldsList.add(configsSection.textField);
            }
        });
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void initGui() {
        super.initGui();
        scrollSections = 0;
        this.totalSections = new HashMap<>();
        this.categories = new ArrayList<>();
        selectedCategory = "";

        for (Config config : Avomod.configsArray) {
            this.addSection(config);
        }
        setCategory(categories.get(0).title);
    }

    // Bug #1 - resizing changes category.
    // Make tab change category

    @Override
    public void onResize(@Nonnull Minecraft mineIn, int w, int h) {
        String oldCategory = selectedCategory;

        super.onResize(mineIn, w, h);
        this.initGui();
        setCategory(oldCategory);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int scrollAmount = Mouse.getDWheel() / 120;

        if (scrollAmount != 0) {
            try {
                this.scroll(scrollAmount, Mouse.getX());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.textboxKeyTyped(typedChar, keyCode);
        }

        if (keyCode == 15) {
            if (isShiftKeyDown()) {
                previousCategory();
            } else {
                nextCategory();
            }
        }
    }

    private void nextCategory() {
        int currentIndex = categories.stream().map(e -> e.title).collect(Collectors.toList()).indexOf(selectedCategory);
        if (currentIndex == categories.size() - 1) {
            setCategory(categories.get(0).title);
        } else {
            setCategory(categories.get(currentIndex + 1).title);
        }
    }

    private void previousCategory() {
        int currentIndex = categories.stream().map(e -> e.title).collect(Collectors.toList()).indexOf(selectedCategory);
        if (currentIndex == 0) {
            setCategory(categories.get(categories.size() - 1).title);
        } else {
            setCategory(categories.get(currentIndex - 1).title);
        }
    }

    @Override
    public void onGuiClosed() {
        for (ConfigsTextField textField : this.textFieldsList) {
            if (Pattern.matches(textField.finalValidation, textField.getText())) {
                textField.configsSection.updateConfigs(textField.getText());
            }
        }

        super.onGuiClosed();
    }

    public void scroll(int amount, int mouseX) {
        if (mouseX < this.width / 16 + 100) return;
        int configHeight = settingLineHeight + settingHeight;
        int settingsOnScreen = (this.height - startingHeight) / configHeight;
        if (settingsOnScreen > totalSections.get(selectedCategory).size()) return;

        scrollSections += -amount;
        if (scrollSections < 0) scrollSections = 0;
        if (scrollSections > totalSections.get(selectedCategory).size() - settingsOnScreen)
            scrollSections = totalSections.get(selectedCategory).size() - settingsOnScreen;

        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();

        buttonList.addAll(categories);
        ArrayList<ConfigsSection> sectionList = new ArrayList<>(totalSections.get(selectedCategory).subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), totalSections.get(selectedCategory).size())));
        sectionList.forEach((ConfigsSection configsSection) -> {
            int configPlacement = sectionList.indexOf(configsSection);
            if (configsSection.button != null) {
                configsSection.button.y = configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1));
                this.buttonList.add(configsSection.button);
            }

            if (configsSection.textField != null) {
                configsSection.textField.y = configPlacement * settingLineHeight + startingHeight + 2 + (settingHeight * (configPlacement + 1));
                this.textFieldsList.add(configsSection.textField);
            }
        });
    }

    public void addSection(Config config) {
        String configValue = Avomod.getConfig(config.configsKey);
        if (!configValue.equals("")) {
            config.defaultValue = configValue;
        }

        ConfigsSection sectionToAdd;

        int configPlacement = 0;
        if (totalSections.get(config.configsCategory) != null) {
            configPlacement = totalSections.get(config.configsCategory).size();
        }
        if (config instanceof ConfigToggle) {
            String[] choices = new String[]{"Enabled", "Disabled"};
            //int x = this.width - (this.width / 4) - (Stream.of(choices).mapToInt((String choice) -> Avomod.getMC().fontRenderer.getStringWidth(choice)).max().getAsInt() + 10);
            int width = Stream.of(choices).mapToInt((String choice) -> Avomod.getMC().fontRenderer.getStringWidth(choice)).max().getAsInt() + 10;

            ConfigsButton configButton = new ConfigsButton(configPlacement, this.width / 16 + 121, configPlacement * settingLineHeight + startingHeight - 4 + (settingHeight * (configPlacement + 1)), width, choices, config.defaultValue);
            sectionToAdd = new ConfigsSection(config.configsCategory, config.sectionText, configButton, config.configsKey);
        } else {
            ConfigsTextField textField = new ConfigsTextField(configPlacement, ((ConfigInput) config).allowedInputs, ((ConfigInput) config).finalValidation, Avomod.getMC().fontRenderer, this.width / 16 + 122, configPlacement * settingLineHeight + startingHeight - 2 + (settingHeight * (configPlacement + 1)), 80, 16, this.width);
            textField.setText(config.defaultValue);
            if (((ConfigInput) config).maxLength != 0) {
                textField.setMaxStringLength(((ConfigInput) config).maxLength);
            }

            sectionToAdd = new ConfigsSection(config.configsCategory, config.sectionText, textField, config.configsKey);
        }

        // add the ConfigsSection to the configs map
        if (totalSections.containsKey(config.configsCategory)) {
            ArrayList<ConfigsSection> s = totalSections.get(config.configsCategory);
            s.add(sectionToAdd);
            totalSections.put(config.configsCategory, s);
        } else {
            totalSections.put(config.configsCategory, new ArrayList<>(Collections.singletonList(sectionToAdd)));
        }

        // make sure all categories that a config can be in are present in categories
        boolean containsCategory = false;
        for (ConfigsCategory category : categories) {
            if (category.title.equals(config.configsCategory)) {
                containsCategory = true;
                break;
            }
        }
        if (!containsCategory) {
            ConfigsCategory c = new ConfigsCategory(categories.size(), this.width / 16, startingHeight + categories.size() * settingLineHeight, config.configsCategory);
            c.setCFGUI(this);
            categories.add(c);
        }
    }
}
