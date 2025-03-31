package ija.project.ijaproject.common;

public enum Type {
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
