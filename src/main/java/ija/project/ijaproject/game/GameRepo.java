/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
#                                                       #
#########################################################
*/

/**
 * @file GameRepo.java
 * @brief Provides functionality for generating game boards and puzzles.
 */

package ija.project.ijaproject.game;

import ija.project.ijaproject.game.node.NodePosition;
import ija.project.ijaproject.game.node.NodeSide;

import java.util.*;

import static ija.project.ijaproject.game.node.NodeSide.*;
import static ija.project.ijaproject.game.node.NodeType.EMPTY;

/**
 * @class GameRepo
 * @brief Handles the generation of game boards and puzzles based on difficulty levels.
 * The GameRepo class provides methods to generate game boards using Prim's algorithm,
 * create nodes, and randomize node rotations to increase difficulty.
 */
public class GameRepo {
    private static final Map<String, int[]> BOARD_SIZES = new HashMap<>();

    ///< Predefined board sizes for each difficulty level.

    static {
        BOARD_SIZES.put("Easy", new int[]{4, 4});
        BOARD_SIZES.put("Medium", new int[]{8, 8});
        BOARD_SIZES.put("Hard", new int[]{16, 16});
    }

    /**
     * @param difficulty The difficulty level ("Easy", "Medium", "Hard").
     * @return A new Game instance.
     * @brief Generates a new game based on the specified difficulty.
     */
    public static Game generate(String difficulty) {
        int[] size = BOARD_SIZES.getOrDefault(difficulty, new int[]{8, 8});
        int rows = size[0];
        int cols = size[1];

        Game game = new Game(rows, cols);

        // Generate a maze using Prim's algorithm
        generateMaze(game, difficulty);

        // Initialize the game
        game.init();
        game.logger().logAction("Gen finished");

        return game;
    }

    /**
     * @param game       The Game instance to populate.
     * @param difficulty The difficulty level.
     * @brief Generates a maze using Prim's algorithm.
     */
    private static void generateMaze(Game game, String difficulty) {
        Random rand = new Random();
        int rows = game.rows();
        int cols = game.cols();

        boolean[][] visited = new boolean[rows][cols];
        PriorityQueue<int[]> frontier = new PriorityQueue<>(Comparator.comparingInt(a -> a[4]));

        int startRow = rand.nextInt(rows) + 1;
        int startCol = rand.nextInt(cols) + 1;
        NodePosition startPos = new NodePosition(startRow, startCol);

        Map<NodePosition, Set<NodeSide>> connections = new HashMap<>();
        connections.put(startPos, new HashSet<>());

        visited[startRow - 1][startCol - 1] = true;

        addNeighborsToFrontier(frontier, startRow, startCol, rows, cols, visited, rand);

        int edgeLimit = switch (difficulty) {
            case "Easy" -> rows * cols / 2;
            case "Medium" -> rows * cols * 3 / 4;
            case "Hard" -> rows * cols;
            default -> rows * cols / 2;
        };

        int edgeCount = 0;

        while (!frontier.isEmpty() && edgeCount < edgeLimit) {
            int[] next = frontier.poll();
            int row = next[0], col = next[1];
            int fromRow = next[2], fromCol = next[3];

            if (visited[row - 1][col - 1]) continue;

            visited[row - 1][col - 1] = true;
            edgeCount++;

            NodePosition pos = new NodePosition(row, col);
            NodePosition fromPos = new NodePosition(fromRow, fromCol);

            NodeSide fromToCurrentSide = getSide(fromRow, fromCol, row, col);
            NodeSide currentToFromSide = fromToCurrentSide.opposite();

            connections.putIfAbsent(pos, new HashSet<>());
            connections.putIfAbsent(fromPos, new HashSet<>());
            connections.get(pos).add(currentToFromSide);
            connections.get(fromPos).add(fromToCurrentSide);

            addNeighborsToFrontier(frontier, row, col, rows, cols, visited, rand);
        }

        createNodes(game, connections, startPos, difficulty);
        randomizeRotations(game);
    }

    /**
     * @param frontier The priority queue of frontier cells.
     * @param row      The current row.
     * @param col      The current column.
     * @param rows     Total number of rows.
     * @param cols     Total number of columns.
     * @param visited  The visited cells grid.
     * @param rand     Random number generator.
     * @brief Adds neighboring cells to the frontier for Prim's algorithm.
     */
    private static void addNeighborsToFrontier(
            PriorityQueue<int[]> frontier, int row, int col,
            int rows, int cols, boolean[][] visited, Random rand) {

        int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

        for (int[] dir : dirs) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 1 && newRow <= rows &&
                    newCol >= 1 && newCol <= cols &&
                    !visited[newRow - 1][newCol - 1]) {

                int weight = rand.nextInt(100);
                frontier.add(new int[]{newRow, newCol, row, col, weight});
            }
        }
    }

    /**
     * @param fromRow The row of the source node.
     * @param fromCol The column of the source node.
     * @param toRow   The row of the target node.
     * @param toCol   The column of the target node.
     * @return The NodeSide representing the direction.
     * @brief Determines the side of a connection between two nodes.
     */
    private static NodeSide getSide(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow < toRow) return SOUTH;
        if (fromRow > toRow) return NORTH;
        if (fromCol < toCol) return EAST;
        return WEST;
    }

    /**
     * @param game        The Game instance.
     * @param connections The map of node connections.
     * @param powerPos    The position of the power node.
     * @param difficulty  The difficulty level.
     * @brief Creates nodes based on the connections map.
     */
    private static void createNodes(
            Game game,
            Map<NodePosition, Set<NodeSide>> connections,
            NodePosition powerPos,
            String difficulty) {

        Random rand = new Random();

        Set<NodeSide> powerSides = connections.get(powerPos);
        game.createPowerNode(powerPos, powerSides.toArray(new NodeSide[0]));

        int bulbCount = switch (difficulty) {
            case "Easy" -> 4;
            case "Medium" -> 8;
            case "Hard" -> 12;
            default -> 6;
        };

        List<NodePosition> leafNodes = new ArrayList<>();

        for (NodePosition pos : connections.keySet()) {
            if (!pos.equals(powerPos)) {
                Set<NodeSide> sides = connections.get(pos);
                if (sides.size() == 1) {
                    leafNodes.add(pos);
                } else if (sides.size() >= 2) {
                    game.createLinkNode(pos, sides.toArray(new NodeSide[0]));
                }
            }
        }

        Collections.shuffle(leafNodes);
        int bulbsCreated = 0;

        for (NodePosition pos : leafNodes) {
            if (bulbsCreated >= bulbCount) break;

            NodeSide side = connections.get(pos).iterator().next();
            game.createBulbNode(pos, side);
            bulbsCreated++;
        }

        if (bulbsCreated < bulbCount) {
            for (NodePosition pos : connections.keySet()) {
                if (bulbsCreated >= bulbCount) break;

                if (!pos.equals(powerPos) && game.node(pos).is(EMPTY)) {
                    Set<NodeSide> sides = connections.get(pos);
                    if (!sides.isEmpty()) {
                        NodeSide side = sides.iterator().next();
                        game.createBulbNode(pos, side);
                        bulbsCreated++;
                    }
                }
            }
        }
    }

    /**
     * @param game The Game instance.
     * @brief Randomizes the rotations of nodes to increase difficulty.
     */
    private static void randomizeRotations(Game game) {
        Random rand = new Random();
        for (int row = 1; row <= game.rows(); row++) {
            for (int col = 1; col <= game.cols(); col++) {
                NodePosition pos = new NodePosition(row, col);
                if (game.node(pos) != null && !game.node(pos).is(EMPTY)) {
                    int rotations = rand.nextInt(4);
                    for (int i = 0; i < rotations; i++) game.node(pos).turn(false);
                }
            }
        }
    }

    /**
     * @return A set of available difficulty levels.
     * @brief Retrieves the available puzzle difficulties.
     */
    public static Set<String> getAvailablePuzzles() {
        return BOARD_SIZES.keySet();
    }
}
