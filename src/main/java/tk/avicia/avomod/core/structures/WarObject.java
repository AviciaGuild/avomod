package tk.avicia.avomod.core.structures;

import java.util.Arrays;
import java.util.List;

public class WarObject {
    private final String territory;
    private final List<String> otherMembers;
    private final long warStartTimestamp;

    public WarObject(String territory, List<String> otherMembers) {
        this(territory, otherMembers, System.currentTimeMillis());
    }

    public WarObject(String territory, List<String> otherMembers, long warStartTimestamp) {
        this.territory = territory;
        this.otherMembers = otherMembers;
        this.warStartTimestamp = warStartTimestamp;
    }

    public String getTerritory() {
        return territory;
    }

    public List<String> getOtherMembers() {
        return otherMembers;
    }

    public long getWarStart() {
        return warStartTimestamp;
    }

    public String toString() {
        return String.format("%s/%s/%s", this.territory, String.join(",", this.otherMembers), this.warStartTimestamp);
    }

    public static WarObject parseString(String war) {
        String[] warSections = war.split("/");

        return new WarObject(
                warSections[0],
                Arrays.asList(warSections[1].split(",")),
                Long.parseLong(warSections[2])
        );
    }
}
