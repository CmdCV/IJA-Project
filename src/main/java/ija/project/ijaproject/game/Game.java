package ija.project.ijaproject.game;

import ija.project.ijaproject.common.AbstractObservable;
import ija.project.ijaproject.common.Observable;
import ija.project.ijaproject.game.node.GameNode;
import ija.project.ijaproject.game.node.NodePosition;
import ija.project.ijaproject.game.node.NodeSide;
import ija.project.ijaproject.game.node.NodeType;

import java.util.ArrayList;
import java.util.List;

import static ija.project.ijaproject.game.node.NodeType.*;

public class Game extends AbstractObservable implements Observable.Observer {
    private final int rows;
    private final int cols;
    private final GameNode[][] board;
    private final GameLogger logger;
    private NodePosition powerPlaced = null;
    private final List<NodePosition> bulbs = new ArrayList<>();
    private boolean updating = false; // Flag to prevent re-entrant calls

    public Game(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException();
        }
        this.rows = rows;
        this.cols = cols;
        this.board = new GameNode[rows][cols];
        // Initialize the board
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= cols; j++) {
                NodePosition position = new NodePosition(i, j);
                this.setBoardNode(position, new GameNode(position, NodeType.EMPTY));
            }
        }
        // Initialize the Logger
        this.logger = new GameLogger();
        this.logger.clear();
        this.logger.logAction("G [" + rows + "@" + cols + "]");
    }

    public int rows() {
        return this.rows;
    }

    public int cols() {
        return this.cols;
    }

    public GameNode node(NodePosition p) {
        if (!isValidPosition(p)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return this.board[p.row() - 1][p.col() - 1];
    }

    private void setBoardNode(NodePosition position, GameNode node) {
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("Invalid position");
        }
        if (node(position) != null && !node(position).is(EMPTY)) {
            throw new IllegalArgumentException("Node already exists at position " + position);
        }
        this.board[position.row() - 1][position.col() - 1] = node;
        node.addObserver(this);
        if (logger != null) logger.logAction("N " + node);
    }

    private GameNode createNode(NodePosition position, NodeType type, NodeSide... sides) {
        if (!isValidPosition(position) || (type == NodeType.LINK && sides.length < 2) || (type == POWER && (sides.length < 1 || powerPlaced != null))) {
            return null;
        }
        GameNode node = new GameNode(position, type, sides);
        if (type == POWER) powerPlaced = position;
        if (type == BULB) bulbs.add(position);
        this.setBoardNode(position, node);
        return node;
    }

    public GameNode createBulbNode(NodePosition position, NodeSide side) {
        return createNode(position, BULB, side);
    }

    public GameNode createLinkNode(NodePosition position, NodeSide... sides) {
        return createNode(position, NodeType.LINK, sides);
    }

    public GameNode createPowerNode(NodePosition position, NodeSide... sides) {
        return createNode(position, POWER, sides);
    }

    public boolean isComplete() {
        for (NodePosition position : bulbs) {
            GameNode node = this.node(position);
            if (node != null && node.is(BULB)) {
                if (!node.isPowered()) return false;
            }
        }
        return true;
    }

    @Override
    public void update(Observable o, String event) {
        if (event != null) logger.logAction(event);
        if (updating) return; // Prevent re-entrant calls
        updating = true;
        try {
            // Reset powerState of all nodes
            for (int r = 1; r <= rows; r++) {
                for (int c = 1; c <= cols; c++) {
                    GameNode node = this.node(new NodePosition(r, c));
                    if (node != null && !node.is(POWER)) {
                        node.setPower(false);
                    }
                }
            }
            // Update powerState of all nodes from Power
            init();
        } finally {
            updating = false;
        }
    }

    public void init() {
        if (powerPlaced != null && !bulbs.isEmpty()) {
            checkNode(new NodePosition(powerPlaced.row(), powerPlaced.col()), null);
            notifyObservers(null);
        } else {
            throw new IllegalStateException("No power node placed");
        }
    }

    private void checkNode(NodePosition position, NodeSide from) {
        // Check if the position is valid
        if (!isValidPosition(position)) {
            return;
        }
        // Get the node at the current position
        GameNode node = this.node(position);
        // Check if the node can be powered and is not already powered to prevent infinite loop
        if (from == null || (node.connects(from) && !node.isPowered())) {
            // Update powered state
            node.setPower(true);
            // Continue checking adjacent nodes without going back to the previous one
            for (NodeSide side : NodeSide.values()) {
                if (side != from && node.connects(side)) {
                    NodePosition newPosition = switch (side) {
                        case NORTH -> new NodePosition(position.row() - 1, position.col());
                        case EAST -> new NodePosition(position.row(), position.col() + 1);
                        case SOUTH -> new NodePosition(position.row() + 1, position.col());
                        case WEST -> new NodePosition(position.row(), position.col() - 1);
                    };
                    checkNode(newPosition, side.opposite());
                }
            }
        }
    }

    private boolean isValidPosition(NodePosition position) {
        return position.row() > 0 && position.row() <= rows && position.col() > 0 && position.col() <= cols;
    }

    public GameLogger logger() {
        return logger;
    }
}
