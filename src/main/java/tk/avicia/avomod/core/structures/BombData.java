package tk.avicia.avomod.core.structures;

import tk.avicia.avomod.core.enums.BombType;

public class BombData {
    private final String world;
    private final long startTime;
    private final BombType bombType;

    public BombData(String world, BombType bombType) {
        this.world = world;
        this.bombType = bombType;

        startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public String getWorld() {
        return world;
    }

    public double getTimeLeft() {
        return BombType.getTimeRemaining(this.bombType) - ((System.currentTimeMillis() - startTime) / 1000.0);
    }

    public BombType getBombType() {
        return bombType;
    }
}
