package tk.avicia.avomod.configs;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SearchTextField extends GuiTextField {
    private final ConfigsGui cfgui;

    public SearchTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, ConfigsGui cfgui) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);

        this.cfgui = cfgui;
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        String oldText = this.getText();

        boolean output = super.textboxKeyTyped(typedChar, keyCode);
        if (!this.isFocused()) return output;

        if (this.getText().length() > 0) {
            if (oldText.length() == 0) {
                cfgui.categories.add(new ConfigsCategory(cfgui.categories.size(), cfgui.width / 16,
                        cfgui.startingHeight + cfgui.categories.size() * cfgui.settingLineHeight,
                        "All", cfgui));
                cfgui.categories.get(cfgui.categories.size() - 1).hasSearchItem = true;
                cfgui.setCategory("All");
            }

            ArrayList<ConfigsSection> selectionSections = cfgui.getSectionsBySearch("All");
            if (selectionSections.size() == 0) {
                cfgui.categories.forEach(e -> e.hasSearchItem = false);
            } else {
                cfgui.categories.get(cfgui.categories.size() - 1).hasSearchItem = true;

                selectionSections.forEach(selectionSection -> {
                    cfgui.categories.stream().filter(e -> e.title.equals(selectionSection.configsCategory)).findFirst().get().hasSearchItem = true;
                });
            }

            cfgui.drawSections(selectionSections);
        } else if (oldText.length() != this.getText().length()) {
            cfgui.categories = cfgui.categories.stream().filter(e -> !e.title.equals("All")).collect(Collectors.toList());
            System.out.println(cfgui.savedCategory);
            cfgui.setCategory(cfgui.savedCategory);

            cfgui.categories.forEach(e -> e.hasSearchItem = false);
        }

        return output;
    }
}
