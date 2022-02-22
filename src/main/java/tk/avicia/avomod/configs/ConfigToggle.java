package tk.avicia.avomod.configs;

public class ConfigToggle extends Config {
    public String[] choices;

    public ConfigToggle(String sectionText, String defaultValue, String configsKey) {
        super(sectionText, defaultValue, configsKey);
        this.choices = new String[]{"Enabled", "Disabled"};
    }
}
