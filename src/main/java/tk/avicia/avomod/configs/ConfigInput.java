package tk.avicia.avomod.configs;

public class ConfigInput extends Config {
    public String allowedInputs, finalValidation;
    public int maxLength;

    public ConfigInput(String sectionText, String defaultValue, String allowedInputs, String finalValidation, int maxLength, String configsKey) {
        super(sectionText, defaultValue, configsKey);
        this.allowedInputs = allowedInputs;
        this.finalValidation = finalValidation;
        this.maxLength = maxLength;
    }
}
