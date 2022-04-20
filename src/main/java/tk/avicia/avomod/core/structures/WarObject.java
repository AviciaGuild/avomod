package tk.avicia.avomod.core.structures;

import java.util.List;

public class WarObject {
    private final String territory;
    private final List<String> otherMembers;
    private final long warStartTimestamp;

    public WarObject(String territory, List<String> otherMembers) {
        this.territory = territory;
        this.otherMembers = otherMembers;
        this.warStartTimestamp = System.currentTimeMillis();
    }

    public String toString() {
        return String.format("%s/%s/%s", this.territory, String.join(",", this.otherMembers), this.warStartTimestamp);
    }
}
