package dan.langford.chult.model;

public enum Terrain {
    BEACH(10), JUNGLE_NO_UNDEAD(15), JUNGLE_LESSER_UNDEAD(15), JUNGLE_GREATER_UNDEAD(15), MOUNTAINS(15), RIVERS(15), RUINS(15), SWAMPS(15), WASTELAND(15);

    public final int dc;

    Terrain(int dc) {
        this.dc=dc;
    }
}