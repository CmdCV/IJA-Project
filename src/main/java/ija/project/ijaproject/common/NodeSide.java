package ija.project.ijaproject.common;

public enum NodeSide {
    NORTH, EAST, SOUTH, WEST;

    public NodeSide next() {
        return values()[(this.ordinal() + 1) % values().length];
    }
    public NodeSide previous() {
        return values()[(this.ordinal() - 1 + values().length) % values().length];
    }

    public NodeSide opposite() {
        return values()[(this.ordinal() + 2) % values().length];
    }
}
