/**
 * @file GameApp.java
 * @brief Main application class for the LightBulb game.
 */

 package ija.project.ijaproject;

 import ija.project.ijaproject.game.node.NodePosition;
 import ija.project.ijaproject.game.node.NodeSide;
 import ija.project.ijaproject.game.Game;
 import ija.project.ijaproject.game.GameRepo;
 import ija.project.ijaproject.view.BoardView;
 import javafx.application.Application;
 import javafx.application.Platform;
 import javafx.geometry.Insets;
 import javafx.geometry.Pos;
 import javafx.scene.Scene;
 import javafx.scene.control.Button;
 import javafx.scene.control.ComboBox;
 import javafx.scene.control.Label;
 import javafx.scene.layout.BorderPane;
 import javafx.scene.layout.HBox;
 import javafx.scene.layout.VBox;
 import javafx.stage.FileChooser;
 import javafx.stage.Stage;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.util.*;
 
 /**
  * @class GameApp
  * @brief Main application class for managing the game lifecycle and UI.
  * 
  * The GameApp class initializes the game, handles user interactions, and manages
  * the graphical user interface using JavaFX.
  */
 public class GameApp extends Application {
     private Game game;             ///< The current game instance.
     private BoardView boardView;   ///< The main board view.
     private BoardView infoView;    ///< The informational board view.
     private Stage infoStage;       ///< The stage for the informational view.
     private Label statusLabel;     ///< Label for displaying game status messages.
 
     /**
      * @brief Entry point for the application.
      * @param args Command-line arguments.
      */
     public static void main(String[] args) {
         launch();
     }
 
     /**
      * @brief Starts the JavaFX application.
      * @param stage The primary stage for the application.
      */
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
         difficultySelector.getItems().addAll(GameRepo.getAvailablePuzzles());
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
         saveLogButton.setOnAction(e -> this.game.logger().save());
 
         Button loadLogButton = new Button("Load Log");
         loadLogButton.setOnAction(e -> loadGameFromLog());
 
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
 
         calculateAndSetMinimumSize(stage, boardView);
     }
 
     /**
      * @brief Creates a new game with the specified difficulty.
      * @param difficulty The difficulty level ("Easy", "Medium", "Hard").
      */
     private void createGame(String difficulty) {
         // Close info view if open
         if (infoStage != null) {
             infoStage.close();
             infoStage = null;
         }
 
         // Use GameRepo to generate the game
         game = GameRepo.generate(difficulty);
 
         // Update the board view
         if (boardView != null) {
             BorderPane root = (BorderPane) boardView.getParent();
             boardView = new BoardView(game, false);
             root.setCenter(boardView);
 
             if (boardView.getScene() != null) {
                 calculateAndSetMinimumSize((Stage) boardView.getScene().getWindow(), boardView);
             }
         }
 
         statusLabel.setText("New game started - " + difficulty + " difficulty");
     }
 
     /**
      * @brief Displays the informational view of the game.
      */
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
 
             calculateAndSetMinimumSize(infoStage, infoView);
         } else {
             infoStage.toFront();
         }
     }
 
     /**
      * @brief Calculates and sets the minimum size of the stage based on the board view.
      * @param stage The stage to resize.
      * @param view The board view to base the size on.
      */
     private void calculateAndSetMinimumSize(Stage stage, BoardView view) {
         Platform.runLater(() -> {
             // Get the actual size of the board view
             double boardWidth = view.getBoundsInParent().getWidth();
             double boardHeight = view.getBoundsInParent().getHeight();
 
             // Default padding
             double paddingWidth = 40;
             double paddingHeight = 60; // Extra padding for height
 
             // For main view with controls
             double controlsHeight = 0;
             if (view.getParent() instanceof BorderPane parent) {
                 if (parent.getBottom() instanceof VBox controls) {
                     controlsHeight = controls.getBoundsInParent().getHeight();
                 }
             }
 
             // Calculate minimum dimensions
             double minWidth = boardWidth + paddingWidth;
             double minHeight = boardHeight + controlsHeight + paddingHeight;
 
             // Account for window decorations (title bar)
             minHeight += 30;
 
             // Set minimum window size
             stage.setMinWidth(minWidth);
             stage.setMinHeight(minHeight);
 
             // If current size is smaller than minimum, resize
             if (stage.getWidth() < minWidth) {
                 stage.setWidth(minWidth);
             }
             if (stage.getHeight() < minHeight) {
                 stage.setHeight(minHeight);
             }
         });
     }
 
     /**
      * @brief Replays the previous move in the game.
      */
     private void replayPreviousMove() {
         this.game.logger().disable();
         String[] parts = this.game.logger().getLine().split(" ", 2);
         switch (parts[0]) {
             case "G":
                 // Game initialization - Cannot be undone
                 break;
             case "N":
                 // Node creation - Cannot be undone
                 break;
             case "T":
                 // Turn action - format is "T [row@col]"
                 NodePosition pos = NodePosition.fromString(parts[1]);
                 game.node(pos).turnBack(true);
                 this.game.logger().previous();
                 break;
         }
         this.game.logger().enable();
     }
 
     /**
      * @brief Replays the next move in the game.
      */
     private void replayNextMove() {
         this.game.logger().disable();
         if (this.game.logger().next()) {
             String[] parts = this.game.logger().getLine().split(" ", 2);
             switch (parts[0]) {
                 case "G":
                     // Game initialization - Should not be possible to undo
                     break;
                 case "N":
                     // Node creation - Should not be possible to undo
                     break;
                 case "T":
                     // Turn action - format is "T [row@col]"
                     NodePosition pos = NodePosition.fromString(parts[1]);
                     game.node(pos).turn(true);
                     break;
             }
         }
         this.game.logger().enable();
     }
 
     /**
      * @brief Loads a game from a log file.
      */
     private void loadGameFromLog() {
         FileChooser fileChooser = new FileChooser();
         fileChooser.setTitle("Load Game Log");
         fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Game Log Files", "*.log"));
 
         File file = fileChooser.showOpenDialog(null);
         if (file != null) {
             try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                 // First, load the log into memory
                 List<String> logActions = new ArrayList<>();
                 String line;
                 while ((line = reader.readLine()) != null) {
                     logActions.addAll(Arrays.asList(line.split(" ")));
                 }
 
                 if (logActions.isEmpty()) return;
 
                 // Process first game initialization command
                 String firstAction = logActions.get(0);
                 if (firstAction.equals("G")) {
                     NodePosition pos = NodePosition.fromString(logActions.get(1));
                     if (pos != null) {
                         game = new Game(pos.row(), pos.col());
 
                         // Process remaining actions
                         boolean generated = false;
                         for (int i = 2; i < logActions.size(); i++) {
                             String action = logActions.get(i);
                             if (action.equals("N") || action.equals("T")) {
                                 generated = executeAction(action + " " + logActions.get(++i), generated);
                             } else if (action.equals("Gen")) {
                                 this.game.logger().logAction("Gen finished");
                                 generated = true;
                             }
                         }
 
                         game.init();
 
                         // Update UI
                         if (boardView != null) {
                             BorderPane root = (BorderPane) boardView.getParent();
                             boardView = new BoardView(game, false);
                             root.setCenter(boardView);
                         }
 
                         statusLabel.setText("Game loaded from log");
                     }
                 }
             } catch (IOException e) {
                 System.err.println("Error loading game log: " + e.getMessage());
             }
         }
     }
 
     /**
      * @brief Executes a game action from the log.
      * @param action The action to execute.
      * @param generated Whether the game has been generated.
      * @return True if the game was generated, false otherwise.
      */
     private boolean executeAction(String action, boolean generated) {
         if (action == null || action.isEmpty()) return generated;
 
         // Parse and execute the action based on its type
         String[] parts = action.split(" ", 2);
         String actionType = parts[0];
         NodePosition pos;
         switch (actionType) {
             case "G":
                 // Game initialization
                 if (parts.length > 1) {
                     pos = NodePosition.fromString(parts[1]);
                     if (pos != null) {
                         game = new Game(pos.row(), pos.col());
 
                         if (boardView != null) {
                             BorderPane root = (BorderPane) boardView.getParent();
                             boardView = new BoardView(game, false);
                             root.setCenter(boardView);
 
                             // Update info view if open
                             if (infoView != null && infoStage != null && infoStage.isShowing()) {
                                 BorderPane infoRoot = (BorderPane) infoView.getParent();
                                 infoView = new BoardView(game, true);
                                 infoRoot.setCenter(infoView);
                             }
                         }
 
                         statusLabel.setText("Game loaded from log");
                     }
                 }
                 break;
             case "N":
                 // Node creation - will need to parse the node details and create it
                 parseAndCreateNode(parts[1]);
                 break;
             case "T":
                 // Turn action - format is "T [row@col]"
                 if (parts.length > 1) {
                     pos = NodePosition.fromString(parts[1]);
                     if (pos != null) game.node(pos).turn(generated);
                 }
                 break;
 
         }
         return generated;
     }
 
     /**
      * @brief Parses and creates a node from a log entry.
      * @param nodeStr The string representation of the node.
      */
     private void parseAndCreateNode(String nodeStr) {
         try {
             // Remove outer braces
             nodeStr = nodeStr.substring(1, nodeStr.length() - 1);
 
             // Extract type (first character)
             String typeStr = nodeStr.substring(0, 1);
 
             // Extract position (between first [ and ])
             int posStartIdx = nodeStr.indexOf('[');
             int posEndIdx = nodeStr.indexOf(']');
             String posStr = nodeStr.substring(posStartIdx, posEndIdx + 1);
             NodePosition position = NodePosition.fromString(posStr);
 
             // Extract sides (between second [ and ])
             int sidesStartIdx = nodeStr.indexOf('[', posEndIdx + 1);
             int sidesEndIdx = nodeStr.indexOf(']', sidesStartIdx);
             String sidesStr = nodeStr.substring(sidesStartIdx + 1, sidesEndIdx);
 
             // Parse sides
             String[] sideNames = sidesStr.split(",");
             NodeSide[] sides = new NodeSide[sideNames.length];
             for (int i = 0; i < sideNames.length; i++) {
                 sides[i] = NodeSide.valueOf(sideNames[i]);
             }
 
             // Create node based on type
             switch (typeStr) {
                 case "L":
                     game.createLinkNode(position, sides);
                     break;
                 case "B":
                     game.createBulbNode(position, sides[0]);
                     break;
                 case "P":
                     game.createPowerNode(position, sides);
                     break;
             }
         } catch (Exception e) {
             System.err.println("Error parsing node: " + e.getMessage());
         }
     }
 }
