/*
#########################################################
#                     IJA - project                     #
#         Authors: Urbánek Aleš, Kováčik Martin         #
#              Logins: xurbana00, xkovacm01             #
#                     Description:                      #
# JavaFX visualization of the game board. BoardView is  #
# a GridPane component that displays the game grid      #
# based on data from the Game class. It sets up         #
# background, shadow, border, and places NodeView       #
# components according to their position. Supports both #
# interactive (game )and read-only (informational)      #
# display.                                              #
#########################################################
*/

package ija.project.ijaproject.view;

import ija.project.ijaproject.game.Game;
import ija.project.ijaproject.game.node.GameNode;
import ija.project.ijaproject.game.node.NodePosition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * @class BoardView
 * @brief A JavaFX GridPane that visually represents the game board.
 * The BoardView class is responsible for rendering the game board using JavaFX.
 * It creates a grid layout with nodes styled for better visual appearance.
 */
public class BoardView extends GridPane {
    private final Game game;
    /// < The game instance associated with this view.
    private final boolean infoBoardView; ///< Flag indicating if this is an informational board view.

    /**
     * @param game          The game instance to be displayed.
     * @param infoBoardView Whether this is an informational board view.
     * @brief Constructs a new BoardView instance.
     */
    public BoardView(final Game game, boolean infoBoardView) {
        this.game = game;
        this.infoBoardView = infoBoardView;

        // Increased spacing for better visual separation
        this.setHgap(3);
        this.setVgap(3);

        // Create a gradient background
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(60, 60, 60)),
                new Stop(1, Color.rgb(40, 40, 40))
        );

        // Create stylish background with rounded corners
        this.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(12), Insets.EMPTY)));

        // Add padding and drop shadow for depth
        this.setPadding(new Insets(10));
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(10);
        this.setEffect(shadow);

        // Center the grid within its parent
        this.setAlignment(Pos.CENTER);

        // Responsive sizing
        this.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Add a subtle border
        this.setBorder(new Border(new BorderStroke(
                Color.rgb(100, 100, 100),
                BorderStrokeStyle.SOLID,
                new CornerRadii(12),
                new BorderWidths(2)
        )));

        initializeBoard();
    }

    /**
     * @brief Initializes the board by creating and adding NodeView instances.
     * This method calculates the size of each node based on the available space
     * and adds them to the grid layout.
     */
    private void initializeBoard() {
        // Base size of the board
        int baseSize = 500;
        // Calculate the space occupied by gaps and padding
        int totalGapSpace = (int) ((game.cols() - 1) * this.getHgap()); // Horizontal gaps
        int totalPaddingSpace = (int) this.getPadding().getLeft() + (int) this.getPadding().getRight();
        // Available space after subtracting gaps and padding
        int availableWidth = baseSize - totalGapSpace - totalPaddingSpace;
        // Calculate the size of each node
        int nodeSize = availableWidth / game.cols();

        for (int row = 1; row <= game.rows(); row++) {
            for (int col = 1; col <= game.cols(); col++) {
                GameNode field = game.node(new NodePosition(row, col));
                NodeView nodeView = new NodeView(field, this.infoBoardView, nodeSize, game);
                this.add(nodeView, col - 1, row - 1);
            }
        }
    }
}
