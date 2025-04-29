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

/**
 * @brief Represents the game board and main game logic.
 * The Game class manages the game board, node creation, and energy propagation in the electrical network.
 * Implements Observable.Observer to monitor changes in nodes.
 */
public class Game extends AbstractObservable implements Observable.Observer {
    /**
     * @brief Number of rows on the game board
     */
    private final int rows;
    /**
     * @brief Number of columns on the game board
     */
    private final int cols;
    /**
     * @brief Two-dimensional array representing the game board
     */
    private final GameNode[][] board;
    /**
     * @brief Logger for recording game actions
     */
    private final GameLogger logger;
    /**
     * @brief Position of the placed power source
     */
    private NodePosition powerPlaced = null;
    /**
     * @brief List of positions of all bulbs on the game board
     */
    private final List<NodePosition> bulbs = new ArrayList<>();
    /**
     * @brief Flag preventing recursive calls during updates
     */
    private boolean updating = false; // Flag to prevent re-entrant calls

    /**
     * @param rows Number of rows on the game board
     * @param cols Number of columns on the game board
     * @throws IllegalArgumentException If the number of rows or columns is less than 1
     * @brief Constructor creates a new game with the given number of rows and columns.
     */
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

    /**
     * @return Number of rows
     * @brief Returns the number of rows on the game board.
     */
    public int rows() {
        return this.rows;
    }

    /**
     * @return Number of columns
     * @brief Returns the number of columns on the game board.
     */
    public int cols() {
        return this.cols;
    }

    /**
     * @param p Position of the node
     * @return Node at the given position
     * @throws IllegalArgumentException If the position is invalid
     * @brief Returns the node at the given position.
     */
    public GameNode node(NodePosition p) {
        if (!isValidPosition(p)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return this.board[p.row() - 1][p.col() - 1];
    }

    /**
     * @param position Position where the node should be placed
     * @param node     Node to be placed
     * @throws IllegalArgumentException If the position is invalid or a non-empty node already exists at the position
     * @brief Sets a node at the given position.
     */
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

    /**
     * @param position Position where the node should be placed
     * @param type     Type of the node
     * @param sides    Sides to which the node connects
     * @return Created node or null if unsuccessful
     * @brief Creates a new node of the given type at the specified position.
     */
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

    /**
     * @param position Position where the node should be placed
     * @param side     Side to which the bulb connects
     * @return Created bulb node or null if unsuccessful
     * @brief Creates a new BULB node at the specified position.
     */
    public GameNode createBulbNode(NodePosition position, NodeSide side) {
        return createNode(position, BULB, side);
    }

    /**
     * @param position Position where the node should be placed
     * @param sides    Sides to which the link connects
     * @return Created link node or null if unsuccessful
     * @brief Creates a new LINK node at the specified position.
     */
    public GameNode createLinkNode(NodePosition position, NodeSide... sides) {
        return createNode(position, NodeType.LINK, sides);
    }

    /**
     * @param position Position where the node should be placed
     * @param sides    Sides to which the power source connects
     * @return Created power node or null if unsuccessful
     * @brief Creates a new POWER node at the specified position.
     */
    public GameNode createPowerNode(NodePosition position, NodeSide... sides) {
        return createNode(position, POWER, sides);
    }

    /**
     * @return true if all bulbs are powered, otherwise false
     * @brief Checks if all bulbs in the game are powered.
     */
    public boolean isComplete() {
        for (NodePosition position : bulbs) {
            GameNode node = this.node(position);
            if (node != null && node.is(BULB)) {
                if (!node.isPowered()) return false;
            }
        }
        return true;
    }

    /**
     * @param o     Observable object that triggered the update
     * @param event Event that triggered the update
     * @brief Updates the game state based on changes in the nodes.
     * Implements the Observer method of the Observable interface.
     */
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

    /**
     * @throws IllegalStateException If no power source or no bulbs are placed
     * @brief Initializes energy propagation from the power source.
     */
    public void init() {
        if (powerPlaced != null && !bulbs.isEmpty()) {
            checkNode(new NodePosition(powerPlaced.row(), powerPlaced.col()), null);
            notifyObservers(null);
        } else {
            throw new IllegalStateException("No power node placed");
        }
    }

    /**
     * @param position Current position of the node being checked
     * @param from     Side from which energy is coming (null for source)
     * @brief Recursively checks and updates power status of nodes.
     */
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

    /**
     * @param position Position to check
     * @return true if the position is valid, otherwise false
     * @brief Checks if a position is valid within the game board.
     */
    private boolean isValidPosition(NodePosition position) {
        return position.row() > 0 && position.row() <= rows && position.col() > 0 && position.col() <= cols;
    }

    /**
     * @return Game logger
     * @brief Returns the logger for recording game actions.
     */
    public GameLogger logger() {
        return logger;
    }
}
