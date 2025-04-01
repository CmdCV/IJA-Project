package ija.project.ijaproject.common;

import java.util.Objects;

public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return this.row; }

    public int getCol() {
        return this.col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return String.format("[%d@%d]", this.getRow(), this.getCol());
    }

    public static Position fromString(String str) {
        try {
            str = str.replace("[", "").replace("]", "");
            String[] coords = str.split("@");
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);
            return new Position(row, col);
        } catch (Exception e) {
            return null;
        }
    }
}
