package ija.project.ijaproject.game;

import ija.project.ijaproject.game.node.NodePosition;
import ija.project.ijaproject.game.node.NodeSide;
import ija.project.ijaproject.game.node.NodeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static ija.project.ijaproject.game.node.NodeSide.*;
import static ija.project.ijaproject.game.node.NodeType.*;

public class GameRepo {
    private static final Map<String, Object[][]> PUZZLES = new HashMap<>();
    private static final Map<String, int[]> BOARD_SIZES = new HashMap<>();

    static {
        BOARD_SIZES.put("Easy", new int[]{4, 4});
        BOARD_SIZES.put("Medium", new int[]{8, 8});
        BOARD_SIZES.put("Hard", new int[]{16, 16});

        PUZZLES.put("Easy", new Object[][]{
                {POWER, 2, 2, NORTH, EAST, SOUTH},
                {BULB, 1, 2, SOUTH},
                {BULB, 3, 4, WEST},
                {BULB, 4, 1, NORTH},
                {BULB, 4, 3, NORTH},
                {LINK, 2, 3, WEST, SOUTH},
                {LINK, 3, 1, EAST, SOUTH},
                {LINK, 3, 2, WEST, NORTH},
                {LINK, 3, 3, NORTH, EAST, SOUTH}
        });

        // Medium puzzle
        PUZZLES.put("Medium", new Object[][]{
                {POWER, 3, 4, WEST, SOUTH},
                {BULB, 1, 7, SOUTH},
                {BULB, 2, 6, SOUTH},
                {BULB, 2, 8, WEST},
                {BULB, 3, 2, EAST},
                {BULB, 4, 7, WEST},
                {BULB, 5, 6, SOUTH},
                {BULB, 7, 3, NORTH},
                {BULB, 7, 7, WEST},
                {LINK, 2, 7, NORTH, EAST, SOUTH},
                {LINK, 3, 3, WEST, EAST},
                {LINK, 3, 6, NORTH, EAST, SOUTH},
                {LINK, 3, 7, NORTH, WEST},
                {LINK, 4, 4, NORTH, EAST, SOUTH},
                {LINK, 4, 5, WEST, EAST},
                {LINK, 4, 6, NORTH, EAST, WEST},
                {LINK, 5, 4, NORTH, SOUTH},
                {LINK, 6, 3, SOUTH, EAST},
                {LINK, 6, 4, NORTH, EAST, WEST},
                {LINK, 6, 5, WEST, EAST},
                {LINK, 6, 6, NORTH, SOUTH, WEST},
                {LINK, 7, 6, NORTH, EAST}
        });

        // Hard puzzle could be defined here IMPOSSIBLE
        PUZZLES.put("Hard", new Object[][]{
                {POWER, 15, 9, NORTH},
                {BULB, 3, 7, SOUTH},
                {BULB, 3, 9, SOUTH},
                {BULB, 4, 6, SOUTH},
                {BULB, 4, 8, SOUTH},
                {BULB, 4, 11, SOUTH},
                {BULB, 5, 10, SOUTH},
                {BULB, 5, 13, SOUTH},
                {BULB, 6, 4, SOUTH},
                {BULB, 6, 5, SOUTH},
                {BULB, 7, 3, EAST},
                {BULB, 7, 14, WEST},
                {BULB, 8, 13, WEST},
                {BULB, 9, 3, EAST},
                {BULB, 9, 14, WEST},
                {BULB, 10, 4, EAST},
                {BULB, 11, 14, WEST},
                {BULB, 12, 6, EAST},
                {BULB, 12, 12, WEST},
                {LINK, 4, 9, NORTH,SOUTH},
                {LINK, 4, 7, NORTH,SOUTH},
                {LINK, 5, 6, NORTH,SOUTH,EAST},
                {LINK, 5, 7, NORTH,WEST},
                {LINK, 5, 8, NORTH,SOUTH},
                {LINK, 5, 9, NORTH,SOUTH},
                {LINK, 5, 11, NORTH,SOUTH},
                {LINK, 6, 6, NORTH,SOUTH},
                {LINK, 6, 8, NORTH,SOUTH},
                {LINK, 6, 9, NORTH,SOUTH},
                {LINK, 6, 10, NORTH,SOUTH,EAST},
                {LINK, 6, 11, NORTH,WEST},
                {LINK, 6, 13, NORTH,SOUTH},
                {LINK, 7, 4, NORTH,SOUTH,WEST},
                {LINK, 7, 5, NORTH,SOUTH},
                {LINK, 7, 6, NORTH,EAST},
                {LINK, 7, 7, WEST,EAST},
                {LINK, 7, 8, NORTH,SOUTH,WEST},
                {LINK, 7, 9, NORTH,SOUTH},
                {LINK, 7, 10, NORTH,SOUTH},
                {LINK, 7, 11, EAST,SOUTH},
                {LINK, 7, 12, EAST,SOUTH,WEST},
                {LINK, 7, 13, NORTH,EAST,WEST},
                {LINK, 8, 4, NORTH,EAST},
                {LINK, 8, 5, NORTH,EAST,WEST},
                {LINK, 8, 6, SOUTH,WEST},
                {LINK, 8, 8, NORTH,SOUTH},
                {LINK, 8, 9, NORTH,EAST,SOUTH},
                {LINK, 8, 10, NORTH,WEST},
                {LINK, 8, 11, NORTH,SOUTH},
                {LINK, 8, 12, NORTH,EAST},
                {LINK, 9, 4, WEST,EAST},
                {LINK, 9, 5, WEST,SOUTH},
                {LINK, 9, 6, NORTH,SOUTH},
                {LINK, 9, 8, NORTH,EAST},
                {LINK, 9, 9, NORTH,SOUTH,WEST},
                {LINK, 9, 11, NORTH,SOUTH},
                {LINK, 9, 12, EAST,SOUTH},
                {LINK, 9, 13, WEST,EAST},
                {LINK, 10, 5, NORTH,EAST,WEST},
                {LINK, 10, 6, NORTH,EAST,WEST},
                {LINK, 10, 7, WEST,SOUTH},
                {LINK, 10, 9, NORTH,SOUTH,EAST},
                {LINK, 10, 10, WEST,EAST},
                {LINK, 10, 11, NORTH,WEST},
                {LINK, 10, 12, NORTH,SOUTH},
                {LINK, 11, 7, NORTH,SOUTH},
                {LINK, 11, 9, NORTH,SOUTH,EAST},
                {LINK, 11, 10, EAST,WEST},
                {LINK, 11, 11, WEST,EAST,SOUTH},
                {LINK, 11, 12, WEST,EAST,NORTH},
                {LINK, 11, 13, WEST,EAST},
                {LINK, 12, 7, NORTH,EAST,WEST},
                {LINK, 12, 8, WEST,EAST},
                {LINK, 12, 9, NORTH,SOUTH,WEST},
                {LINK, 12, 11, NORTH,EAST},
                {LINK, 13, 9, NORTH,SOUTH},
                {LINK, 14, 9, NORTH,SOUTH},
        });
    }

    public static Game generate(String difficulty) {
        Object[][] puzzle = PUZZLES.get(difficulty);
        if (puzzle == null) {
            throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
        }

        int[] size = BOARD_SIZES.getOrDefault(difficulty, new int[]{8, 8});
        int rows = size[0];
        int cols = size[1];

        Game game = new Game(rows, cols);

        for (Object[] n : puzzle) {
            NodeType type = (NodeType) n[0];
            int row = (Integer) n[1];
            int col = (Integer) n[2];

            NodeSide[] sides = new NodeSide[n.length - 3];
            for (int i = 3; i < n.length; i++) {
                sides[i - 3] = (NodeSide) n[i];
            }

            NodePosition position = new NodePosition(row, col);
            switch (type) {
                case LINK -> game.createLinkNode(position, sides);
                case BULB -> game.createBulbNode(position, sides[0]);
                case POWER -> game.createPowerNode(position, sides);
            }
        }

        randomizeRotations(game);

        game.init();
        game.logger().logAction("Gen finished");

        return game;
    }

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

    public static Set<String> getAvailablePuzzles() {
        return PUZZLES.keySet();
    }
}
