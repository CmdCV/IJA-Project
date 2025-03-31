package ija.project.ijaproject;

import ija.project.ijaproject.common.Position;
import ija.project.ijaproject.common.Side;
import static ija.project.ijaproject.common.Side.*;
import ija.project.ijaproject.game.Game;
import ija.project.ijaproject.view.BoardView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GameApplication extends Application {
    private Game game;
    private BoardView boardView;
    private BoardView infoView;
    private Stage infoStage;
    private Label statusLabel;

    // Game definitions for different difficulty levels
    private static final Map<String, Object[][]> PUZZLES = new HashMap<>();

    static {
        // Easy puzzle
        PUZZLES.put("Easy", new Object[][] {
                {"P", 2, 2, EAST, SOUTH},
                {"L", 2, 3, WEST, EAST},
                {"L", 2, 4, WEST, SOUTH},
                {"L", 3, 4, NORTH, EAST},
                {"L", 3, 5, WEST, SOUTH},
                {"B", 4, 5, NORTH}
        });

        // Medium puzzle
        PUZZLES.put("Medium", new Object[][] {
                {"L", 4, 5, NORTH, EAST, SOUTH},
                {"L", 5, 5, NORTH, EAST, WEST},
                {"L", 5, 4, EAST, SOUTH},
                {"L", 4, 6, EAST, SOUTH},
                {"L", 5, 6, NORTH, SOUTH},
                {"L", 3, 6, EAST, WEST},
                {"L", 3, 4, EAST, WEST},
                {"L", 5, 7, EAST, SOUTH},
                {"B", 6, 4, NORTH},
                {"B", 3, 3, NORTH},
                {"B", 2, 6, SOUTH},
                {"B", 4, 7, WEST},
                {"P", 3, 5, EAST, SOUTH}
        });
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create controls
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10, 0, 0, 0));
        controls.setAlignment(Pos.CENTER);

        statusLabel = new Label("Rotate pieces to connect power to bulbs");

        HBox gameControls = new HBox(10);
        gameControls.setAlignment(Pos.CENTER);

        // Difficulty selection
        ComboBox<String> difficultySelector = new ComboBox<>();
        difficultySelector.getItems().addAll(PUZZLES.keySet());
        difficultySelector.setValue("Easy");
        difficultySelector.setOnAction(e -> createGame(difficultySelector.getValue()));

        Button newGameButton = new Button("New Game");
        newGameButton.setOnAction(e -> createGame(difficultySelector.getValue()));

        Button showInfoButton = new Button("Show Info View");
        showInfoButton.setOnAction(e -> showInfoView());

        gameControls.getChildren().addAll(difficultySelector, newGameButton, showInfoButton);

        // Replay controls
        HBox replayControls = new HBox(10);
        replayControls.setAlignment(Pos.CENTER);

        Button saveLogButton = new Button("Save Log");
        saveLogButton.setOnAction(e -> saveGameLog());

        Button loadLogButton = new Button("Load Log");
        loadLogButton.setOnAction(e -> loadGameLog());

        Button prevButton = new Button("←");
        prevButton.setOnAction(e -> replayPreviousMove());

        Button nextButton = new Button("→");
        nextButton.setOnAction(e -> replayNextMove());

        replayControls.getChildren().addAll(saveLogButton, loadLogButton, prevButton, nextButton);

        controls.getChildren().addAll(statusLabel, gameControls, replayControls);
        root.setBottom(controls);

        // Now that statusLabel is initialized, create the game
        createGame("Easy");

        // Create board view
        boardView = new BoardView(game, false);
        root.setCenter(boardView);

        Scene scene = new Scene(root, 600, 650);
        stage.setTitle("IJA 2024/25: LightBulb");
        stage.setScene(scene);
        stage.show();
    }

    private void createGame(String difficulty) {
        // Close info view if open
        if (infoStage != null) {
            infoStage.close();
            infoStage = null;
        }

        Object[][] puzzle = PUZZLES.get(difficulty);
        int rows = 8;
        int cols = 8;

        if (difficulty.equals("Medium")) {
            rows = 10;
            cols = 12;
        }

        game = Game.create(rows, cols);

        // Create puzzle elements
        for (Object[] n : puzzle) {
            String type = (String)n[0];
            int row = (Integer)n[1];
            int col = (Integer)n[2];

            Side[] sides = new Side[n.length-3];
            for (int i = 3; i < n.length; i++) {
                sides[i-3] = (Side)n[i];
            }

            Position p = new Position(row, col);
            switch (type) {
                case "L" -> game.createLinkNode(p, sides);
                case "B" -> game.createBulbNode(p, sides[0]);
                case "P" -> game.createPowerNode(p, sides);
            }
        }

        game.init();

        // Randomly rotate pieces
        randomizeRotations();

        // Update the board view
        if (boardView != null) {
            BorderPane root = (BorderPane) boardView.getParent();
            boardView = new BoardView(game, false);
            root.setCenter(boardView);
        }

        statusLabel.setText("New game started - " + difficulty + " difficulty");
    }

    private void randomizeRotations() {
        Random rand = new Random();

        for (int row = 1; row <= game.rows(); row++) {
            for (int col = 1; col <= game.cols(); col++) {
                try {
                    Position pos = new Position(row, col);
                    if (game.node(pos) != null && !game.node(pos).isPower()) {
                        int rotations = rand.nextInt(4);
                        for (int i = 0; i < rotations; i++) {
                            game.node(pos).turn();
                        }
                    }
                } catch (Exception e) {
                    // Skip invalid positions
                }
            }
        }
    }

    private void showInfoView() {
        if (infoStage == null || !infoStage.isShowing()) {
            infoStage = new Stage();
            infoStage.setTitle("Game Information");

            BorderPane infoRoot = new BorderPane();
            infoRoot.setPadding(new Insets(10));

            infoView = new BoardView(game, true);
            infoRoot.setCenter(infoView);

            Scene infoScene = new Scene(infoRoot, 500, 550);
            infoStage.setScene(infoScene);
            infoStage.show();
        } else {
            infoStage.toFront();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
