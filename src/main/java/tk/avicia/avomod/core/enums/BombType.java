package tk.avicia.avomod.core.enums;

import java.util.Arrays;
import java.util.Optional;

public enum BombType {
    COMBAT_XP("Combat XP"),
    PROFESSION_XP("Profession XP"),
    PROFESSION_SPEED("Profession Speed"),
    DUNGEON("Dungeon"),
    LOOT("Loot");

    private final String bombName;

    BombType(String bombName) {
        this.bombName = bombName;
    }

    public static BombType getBombType(String bombName) {
        Optional<BombType> optional = Arrays.stream(values()).filter(e -> e.bombName.equals(bombName)).findFirst();

        return optional.orElse(null);
    }

    public static int getTimeRemaining(BombType bombType) {
        switch (bombType) {
            case PROFESSION_SPEED:
            case DUNGEON:
                return 600;
            default:
                return 1200;
        }
    }

    public String getBombName() {
        return bombName;
    }
}
