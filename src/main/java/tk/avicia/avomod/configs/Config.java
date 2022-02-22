package tk.avicia.avomod.configs;

public class Config {
    public String sectionText, defaultValue, configsKey;

    public Config(String sectionText, String defaultValue, String configsKey) {
        this.sectionText = sectionText;
        this.defaultValue = defaultValue;
        this.configsKey = configsKey;
    }
}
