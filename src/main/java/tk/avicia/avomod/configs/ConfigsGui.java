package tk.avicia.avomod.configs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
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
    public final int settingLineHeight = 27;
    public final int startingHeight = 85;
    public final int settingHeight = 23;
    public ArrayList<ConfigsCategory> categories = new ArrayList<>();
    // buttonList exists too, doesn't need to be created
    ArrayList<ConfigsTextField> textFieldsList = new ArrayList<>();
    String selectedCategory;
    Map<String, ArrayList<ConfigsSection>> totalSections = new HashMap<>();
    int scrollSections; // the index of the first section to be displayed

    GuiTextField searchTextField;

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
        this.drawCenteredString(this.fontRenderer, "Avomod Configs", this.width / 4 + 1, 11, 0x444444);
        this.drawCenteredString(this.fontRenderer, "Avomod Configs", this.width / 4, 10, 0x1B33CF);
        GlStateManager.popMatrix();

        this.buttonList = new ArrayList<>();
        this.textFieldsList = new ArrayList<>();

        if (searchTextField.getText().length() > 0) {
            drawWithSearch();
        } else {
            buttonList.addAll(categories);
            drawWithCategories();
        }

        // Draw all text field inputs
        for (ConfigsTextField textField : this.textFieldsList) {
            textField.drawTextBox();
        }

        searchTextField.drawTextBox();
        if (searchTextField.getText().length() == 0 && !searchTextField.isFocused())
            Renderer.drawString("Type here to search...", this.width / 16 + 4, startingHeight - settingHeight - 6, Color.DARK_GRAY);

        try {
            super.drawScreen(mouseX, mouseY, partialTicks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawWithCategories() {
        ArrayList<ConfigsSection> sectionsToShow = new ArrayList<>();
        Renderer.drawVerticalLine(this.width / 16 + 110, startingHeight - 10, this.height - 10, Color.WHITE);
        sectionsToShow.addAll(totalSections.get(selectedCategory).subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), totalSections.get(selectedCategory).size())));
        for (ConfigsSection configsSection : sectionsToShow) {
            int index = sectionsToShow.indexOf(configsSection);
            boolean showLine = totalSections.get(selectedCategory).indexOf(configsSection) != totalSections.get(selectedCategory).size() - 1;
            configsSection.drawSection(this, this.width / 16 + 118, startingHeight + (settingHeight + settingLineHeight + 3) * index, showLine);
        }

        if (sectionsToShow.size() < totalSections.get(selectedCategory).size()) { // if not all configs fit on screen
            double segmentHeight = (double)((height / 16 * 15) - startingHeight) / totalSections.get(selectedCategory).size();
            Renderer.drawVerticalLine(this.width / 16 * 15 + 5, startingHeight, height / 16 * 15, Color.DARK_GRAY);
            Renderer.drawVerticalLine(this.width / 16 * 15 + 5, startingHeight + (int)(segmentHeight * scrollSections), startingHeight + (int)(segmentHeight * (scrollSections + sectionsToShow.size())), new Color(32, 110, 225));
        }
    }

    private void drawWithSearch() {
        ArrayList<ConfigsSection> searchSections = getSectionsBySearch();
        ArrayList<ConfigsSection> sectionsToShow = new ArrayList<>();
        sectionsToShow.addAll(searchSections.subList(scrollSections, Math.min(scrollSections + (this.height - startingHeight) / (settingLineHeight + settingHeight), searchSections.size())));

        for (ConfigsSection configsSection : sectionsToShow) {
            int index = sectionsToShow.indexOf(configsSection);
            boolean showLine = index != sectionsToShow.size() - 1;
            configsSection.drawSection(this, this.width / 16, startingHeight + (settingHeight + settingLineHeight + 3) * index, showLine);
        }

        if (sectionsToShow.size() == 0) {
            Renderer.drawString("[No Settings Found]", this.width / 16, startingHeight, new Color(127, 127, 127));
        }

        if (sectionsToShow.size() < searchSections.size()) { // if not all configs fit on screen
            double segmentHeight = (double)((height / 16 * 15) - startingHeight) / searchSections.size();
            Renderer.drawVerticalLine(this.width / 16 * 15 + 5, startingHeight, height / 16 * 15, Color.DARK_GRAY);
            Renderer.drawVerticalLine(this.width / 16 * 15 + 5, startingHeight + (int)(segmentHeight * scrollSections), startingHeight + (int)(segmentHeight * (scrollSections + sectionsToShow.size())), new Color(32, 110, 225));
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

        searchTextField = new GuiTextField(textFieldsList.size(), this.fontRenderer, width / 16, startingHeight - settingHeight - 10, 200, 17);
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

    @Override
    public void onResize(@Nonnull Minecraft mineIn, int w, int h) {
        String oldCategory = selectedCategory;
        String searchText = searchTextField.getText();

        super.onResize(mineIn, w, h);
        this.initGui();
        setCategory(oldCategory);
        searchTextField.setText(searchText);
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

    private ArrayList<ConfigsSection> getSectionsBySearch() {
        String search = searchTextField.getText();
        ArrayList<ConfigsSection> returnSections = new ArrayList<>();

        ArrayList<ConfigsSection> allSections = new ArrayList<>();
        for (String key : totalSections.keySet()) allSections.addAll(totalSections.get(key));
        for (ConfigsSection section : allSections) {
            if (section.title.toLowerCase().contains(search.toLowerCase())) returnSections.add(section);
        }

        return returnSections;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        searchTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (ConfigsTextField textField : this.textFieldsList) {
            textField.textboxKeyTyped(typedChar, keyCode);
        }
        searchTextField.textboxKeyTyped(typedChar, keyCode);

        if (searchTextField.isFocused()) {
            scrollSections = 0;
        }

        if (keyCode == 15) {
            if (isShiftKeyDown()) {
                previousCategory();
            } else {
                nextCategory();
            }
        }
    }

    public void addButton(ConfigsButton button) {
        buttonList.add(button);
    }

    public void addTextField(ConfigsTextField textField) {
        textFieldsList.add(textField);
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

        // no need to scroll if every setting fits on screen
        if  (searchTextField.getText().length() > 0) {
            if (settingsOnScreen > getSectionsBySearch().size()) return;
        } else {
            if (settingsOnScreen > totalSections.get(selectedCategory).size()) return;
        }

        scrollSections += -amount;
        if (scrollSections < 0) scrollSections = 0;
        if (searchTextField.getText().length() > 0) {
            if (scrollSections > getSectionsBySearch().size() - settingsOnScreen)
                scrollSections = getSectionsBySearch().size() - settingsOnScreen;
        } else {
            if (scrollSections > totalSections.get(selectedCategory).size() - settingsOnScreen)
                scrollSections = totalSections.get(selectedCategory).size() - settingsOnScreen;
        }

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
