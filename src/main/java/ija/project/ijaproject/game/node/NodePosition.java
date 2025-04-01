package ija.project.ijaproject.game.node;

public record NodePosition(int row, int col) {

    public static NodePosition fromString(String str) {
        try {
            String[] coords = str.replace("[", "").replace("]", "").split("@");
            int row = Integer.parseInt(coords[0]);
            int col = Integer.parseInt(coords[1]);
            return new NodePosition(row, col);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%d@%d]", this.row(), this.col());
    }

}
