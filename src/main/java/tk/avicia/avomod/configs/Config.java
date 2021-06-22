package tk.avicia.avomod.configs;

public class Config {
    public String sectionText, defaultValue, configsKey;
    public String[] choices;

    public Config(String sectionText, String[] choices, String defaultValue, String configsKey) {
        this.sectionText = sectionText;
        this.choices = choices;
        this.defaultValue = defaultValue;
        this.configsKey = configsKey;
    }
}
