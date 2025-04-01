package ija.project.ijaproject.game;

import ija.project.ijaproject.common.NodePosition;
import ija.project.ijaproject.common.NodeSide;
import ija.project.ijaproject.common.NodeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static ija.project.ijaproject.common.NodeSide.*;
import static ija.project.ijaproject.common.NodeType.*;

public class GameRepo {
    private static final Map<String, Object[][]> PUZZLES = new HashMap<>();
    private static final Map<String, int[]> BOARD_SIZES = new HashMap<>();

    static {
        BOARD_SIZES.put("Easy", new int[]{8, 8});
        BOARD_SIZES.put("Medium", new int[]{12, 12});
        BOARD_SIZES.put("Hard", new int[]{16, 16});

        PUZZLES.put("Easy", new Object[][]{
                {POWER, 2, 2, EAST, SOUTH},
                {LINK, 2, 3, WEST, EAST},
                {LINK, 2, 4, WEST, SOUTH},
                {LINK, 3, 4, NORTH, EAST},
                {LINK, 3, 5, WEST, SOUTH},
                {BULB, 4, 5, NORTH}
        });

        // Medium puzzle
        PUZZLES.put("Medium", new Object[][]{
                {LINK, 4, 5, NORTH, EAST, SOUTH},
                {LINK, 5, 5, NORTH, EAST, WEST},
                {LINK, 5, 4, EAST, SOUTH},
                {LINK, 4, 6, NORTH, WEST},
                {LINK, 5, 6, EAST, WEST},
                {LINK, 3, 6, NORTH, SOUTH},
                {LINK, 3, 4, EAST, WEST},
                {LINK, 5, 7, NORTH, WEST},
                {BULB, 6, 4, NORTH},
                {BULB, 3, 3, EAST},
                {BULB, 2, 6, SOUTH},
                {BULB, 4, 7, SOUTH},
                {POWER, 3, 5, SOUTH, WEST}
        });

        // Hard puzzle could be defined here IMPOSSIBLE
        PUZZLES.put("Hard", new Object[][]{
                {LINK, 5, 6, EAST, WEST},
                {BULB, 5, 7, WEST},
                {POWER, 5, 5, EAST}
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
