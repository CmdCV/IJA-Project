package ija.project.ijaproject.game;

import ija.project.ijaproject.common.GameNode;
import ija.project.ijaproject.common.Position;
import ija.project.ijaproject.common.Side;
import ija.project.ijaproject.common.Type;
import ija.project.ijaproject.common.tool.Observable;
import ija.project.ijaproject.common.tool.ToolEnvironment;
import ija.project.ijaproject.common.tool.ToolField;

public class Game implements ToolEnvironment, Observable.Observer {
    private final int rows;
    private final int cols;
    private final GameNode[][] board;
    private Position powerPlaced = null;
    private boolean updating = false; // Flag to prevent re-entrant calls

    public Game(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new GameNode[rows][cols];
        // Initialize the board
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                Position position = new Position(i, j);
                this.setBoardNode(position, new GameNode(position, Type.EMPTY));
            }
        }
    }

    public static Game create(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException();
        }
        return new Game(rows, cols);
    }

    public int rows() {
        return this.rows;
    }

    public int cols() {
        return this.cols;
    }

    private void resetPowerStates() {
        for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= cols; c++) {
                GameNode n = this.node(new Position(r, c));
                if (n != null && !n.isPower()) {
                    n.setPowered(false);
                }
            }
        }
    }

    public GameNode node(Position p) {
        if (!isValidPosition(p)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return this.board[p.getRow() - 1][p.getCol() - 1];
    }

    @Override
    public ToolField fieldAt(int i, int i1) {
        return this.node(new Position(i, i1));
    }

    private void setBoardNode(Position position, GameNode node) {
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("Invalid position");
        }
        this.board[position.getRow() - 1][position.getCol() - 1] = node;
        node.addObserver(this);
    }

    private GameNode createNode(Position position, Type type, Side... sides) {
        if (!isValidPosition(position) || (type == Type.LINK && sides.length < 2) || (type == Type.POWER && (sides.length < 1 || powerPlaced != null))) {
            return null;
        }
        GameNode node = new GameNode(position, type, sides);
        if (type == Type.POWER) {
            node.setPowered(true);
            powerPlaced = position;
        }
        this.setBoardNode(position, node);
        return node;
    }

    public GameNode createBulbNode(Position position, Side side) {
        return createNode(position, Type.BULB, side);
    }

    public GameNode createLinkNode(Position position, Side... sides) {
        return createNode(position, Type.LINK, sides);
    }

    public GameNode createPowerNode(Position position, Side... sides) {
        return createNode(position, Type.POWER, sides);
    }

    public void init() {
        if (powerPlaced != null) {
            checkNode(new Position(powerPlaced.getRow(), powerPlaced.getCol()), null);
//            System.out.print(this);
        } else {
            throw new IllegalStateException("No power node placed");
        }
    }

    private void checkNode(Position position, Side from) {
        // Check if the position is valid
        if (!isValidPosition(position)) {
            return;
        }
        // Get the node at the current position
        GameNode node = this.node(position);
        // Check if the node can be powered and is not already powered to prevent infinite loop
        if (from == null || (node.containsConnector(from) && !node.light())) {
            // Update powered state
            node.setPowered(true);
            // Continue checking adjacent nodes without going back to the previous one
            for (Side side : Side.values()) {
                if (side != from && node.containsConnector(side)) {
                    Position newPosition = switch (side) {
                        case NORTH -> new Position(position.getRow() - 1, position.getCol());
                        case EAST -> new Position(position.getRow(), position.getCol() + 1);
                        case SOUTH -> new Position(position.getRow() + 1, position.getCol());
                        case WEST -> new Position(position.getRow(), position.getCol() - 1);
                    };
                    checkNode(newPosition, side.opposite());
                }
            }
        }
    }

    @Override
    public void update(Observable node) {
        if (updating) return; // Prevent re-entrant calls
        updating = true;
        try {
            resetPowerStates();
            init();
        } finally {
            updating = false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Append the top border
        for (int c = 0; c <= cols + 1; c++) {
            sb.append("#");
        }
        sb.append("\n");
        for (int r = 1; r <= rows; r++) {
            // Append the left border
            sb.append("#");
            for (int c = 1; c <= cols; c++) {
                GameNode node = this.node(new Position(r, c));
                if (node == null) {
                    sb.append(" ");
                    continue;
                }
                sb.append(node.getNodeSymbol());
            }
            // Append the right border
            sb.append("#\n");
        }
        // Append the bottom border
        for (int c = 0; c <= cols + 1; c++) {
            sb.append("#");
        }
        sb.append("\n");
        return sb.toString();
    }

    private boolean isValidPosition(Position position) {
        return position.getRow() > 0 && position.getRow() <= rows && position.getCol() > 0 && position.getCol() <= cols;
    }
}
