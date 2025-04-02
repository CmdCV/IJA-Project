package ija.project.ijaproject.game;

import ija.project.ijaproject.game.node.NodePosition;
import ija.project.ijaproject.game.node.NodeSide;
import ija.project.ijaproject.game.node.NodeType;
import javafx.scene.Node;

import java.util.*;

import static ija.project.ijaproject.game.node.NodeSide.*;
import static ija.project.ijaproject.game.node.NodeType.*;

public class GameRepo {
    private static final Map<String, int[]> BOARD_SIZES = new HashMap<>();

    static {
        BOARD_SIZES.put("Easy", new int[]{4, 4});
        BOARD_SIZES.put("Medium", new int[]{8, 8});
        BOARD_SIZES.put("Hard", new int[]{16, 16});
    }

    public static Game generate(String difficulty) {
        int[] size = BOARD_SIZES.getOrDefault(difficulty, new int[]{8, 8});
        int rows = size[0];
        int cols = size[1];

        Game game = new Game(rows, cols);

        // Použití Primova algoritmu pro vytvoření propojené sítě
        generateMaze(game, difficulty);

        // Inicializace hry
        game.init();
        game.logger().logAction("Gen finished");

        return game;
    }

    private static void generateMaze(Game game, String difficulty) {
        Random rand = new Random();
        int rows = game.rows();
        int cols = game.cols();

        // Mřížka pro sledování navštívených uzlů
        boolean[][] visited = new boolean[rows][cols];

        // Prioritní fronta pro Primův algoritmus [řádek, sloupec, zdrojový řádek, zdrojový sloupec, váha]
        PriorityQueue<int[]> frontier = new PriorityQueue<>(Comparator.comparingInt(a -> a[4]));

        // Vybrat náhodnou startovní pozici pro zdroj energie
        int startRow = rand.nextInt(rows) + 1;
        int startCol = rand.nextInt(cols) + 1;
        NodePosition startPos = new NodePosition(startRow, startCol);

        // Uložit propojení mezi uzly
        Map<NodePosition, Set<NodeSide>> connections = new HashMap<>();
        connections.put(startPos, new HashSet<>());

        visited[startRow-1][startCol-1] = true;

        // Přidat sousední buňky do fronty
        addNeighborsToFrontier(frontier, startRow, startCol, rows, cols, visited, rand);

        // Určit složitost podle obtížnosti
        int edgeLimit = switch (difficulty) {
            case "Easy" -> rows * cols / 2;
            case "Medium" -> rows * cols * 3 / 4;
            case "Hard" -> rows * cols;
            default -> rows * cols / 2;
        };

        int edgeCount = 0;

        // Primův algoritmus
        while (!frontier.isEmpty() && edgeCount < edgeLimit) {
            int[] next = frontier.poll();
            int row = next[0], col = next[1];
            int fromRow = next[2], fromCol = next[3];

            if (visited[row-1][col-1]) continue;

            visited[row-1][col-1] = true;
            edgeCount++;

            // Vytvořit propojení mezi uzly
            NodePosition pos = new NodePosition(row, col);
            NodePosition fromPos = new NodePosition(fromRow, fromCol);

            // Určit strany pro propojení
            NodeSide fromToCurrentSide = getSide(fromRow, fromCol, row, col);
            NodeSide currentToFromSide = fromToCurrentSide.opposite();

            // Přidat propojení
            connections.putIfAbsent(pos, new HashSet<>());
            connections.putIfAbsent(fromPos, new HashSet<>());
            connections.get(pos).add(currentToFromSide);
            connections.get(fromPos).add(fromToCurrentSide);

            // Přidat další sousedy
            addNeighborsToFrontier(frontier, row, col, rows, cols, visited, rand);
        }

        // Vytvořit uzly podle propojení
        createNodes(game, connections, startPos, difficulty);

        // Náhodně otočit uzly pro zvýšení obtížnosti
//        randomizeRotations(game);
    }

    private static void addNeighborsToFrontier(
            PriorityQueue<int[]> frontier, int row, int col,
            int rows, int cols, boolean[][] visited, Random rand) {

        // Směry: Sever, Východ, Jih, Západ
        int[][] dirs = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

        for (int[] dir : dirs) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 1 && newRow <= rows &&
                    newCol >= 1 && newCol <= cols &&
                    !visited[newRow-1][newCol-1]) {

                // Náhodná váha pro různé cesty
                int weight = rand.nextInt(100);
                frontier.add(new int[]{newRow, newCol, row, col, weight});
            }
        }
    }

    private static NodeSide getSide(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow < toRow) return SOUTH;
        if (fromRow > toRow) return NORTH;
        if (fromCol < toCol) return EAST;
        return WEST;
    }

    private static void createNodes(
            Game game,
            Map<NodePosition, Set<NodeSide>> connections,
            NodePosition powerPos,
            String difficulty) {

        Random rand = new Random();

        // Vytvořit uzel zdroje
        Set<NodeSide> powerSides = connections.get(powerPos);
        game.createPowerNode(powerPos, powerSides.toArray(new NodeSide[0]));

        // Určit počet žárovek podle obtížnosti
        int bulbCount = switch (difficulty) {
            case "Easy" -> 4;
            case "Medium" -> 8;
            case "Hard" -> 12;
            default -> 6;
        };

        // Vytvořit propojovací uzly
        List<NodePosition> leafNodes = new ArrayList<>();

        for (NodePosition pos : connections.keySet()) {
            if (!pos.equals(powerPos)) {
                Set<NodeSide> sides = connections.get(pos);
                if (sides.size() == 1) {
                    leafNodes.add(pos); // Koncové uzly pro žárovky
                } else if (sides.size() >= 2) {
                    // Propojovací uzly
                    game.createLinkNode(pos, sides.toArray(new NodeSide[0]));
                }
            }
        }

        // Vytvořit žárovky na koncových uzlech
        Collections.shuffle(leafNodes);
        int bulbsCreated = 0;

        for (NodePosition pos : leafNodes) {
            if (bulbsCreated >= bulbCount) break;

            NodeSide side = connections.get(pos).iterator().next();
            game.createBulbNode(pos, side);
            bulbsCreated++;
        }

        // Pokud potřebujeme více žárovek, přidat je na další volné uzly
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
        return BOARD_SIZES.keySet();
    }
}
