package ija.project.ijaproject.view;

import ija.project.ijaproject.game.Game;
import ija.project.ijaproject.game.node.GameNode;
import ija.project.ijaproject.game.node.NodePosition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

public class BoardView extends GridPane {
    private final Game game;
    private final boolean infoBoardView;

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

    private void initializeBoard() {
        // Calculate optimal size based on board dimensions
        int baseSize = 500;
        int nodeSize = baseSize / Math.max(game.rows(), game.cols());

        for (int row = 1; row <= game.rows(); row++) {
            for (int col = 1; col <= game.cols(); col++) {
                GameNode field = game.node(new NodePosition(row, col));
                NodeView nodeView = new NodeView(field, this.infoBoardView, nodeSize, game);
                this.add(nodeView, col - 1, row - 1);
            }
        }
    }
}
