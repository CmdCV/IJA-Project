package ija.project.ijaproject.common;

public enum Side {
    NORTH, EAST, SOUTH, WEST;

    public Side next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    public Side opposite() {
        return values()[(this.ordinal() + 2) % values().length];
    }
}
