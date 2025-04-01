package ija.project.ijaproject.game.node;

public enum NodeType {
    BULB, LINK, POWER, EMPTY;

    @Override
    public String toString() {
        return switch (this) {
            case BULB -> "B";
            case LINK -> "L";
            case POWER -> "P";
            case EMPTY -> "E";
        };
    }
}
